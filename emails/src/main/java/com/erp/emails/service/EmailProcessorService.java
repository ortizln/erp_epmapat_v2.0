package com.erp.emails.service;

import com.erp.emails.model.EmailMessage;
import com.erp.emails.model.EmailStatus;
import com.erp.emails.repository.EmailMessageR;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class EmailProcessorService {

    private static final Logger log = LoggerFactory.getLogger(EmailProcessorService.class);

    @org.springframework.beans.factory.annotation.Value("${mailms.outbox.retry-delays-seconds:60,300,900,3600}")
    private long[] retryDelays={60,300,900,3600};
    @jakarta.annotation.PostConstruct
    void validarEsperas(){
        if(retryDelays.length==0 || java.util.Arrays.stream(retryDelays).anyMatch(n->n<1 || n>86400))
            throw new IllegalArgumentException("Esperas de correo deben estar entre 1 y 86400 segundos");
    }

    @org.springframework.beans.factory.annotation.Autowired
    private EmailAttemptService attempts;
    private final MailSenderService sender;
    private final EmailMessageR emailRepo;
    private final TransactionTemplate requiresNewTx;

    public EmailProcessorService(MailSenderService sender, EmailMessageR emailRepo, PlatformTransactionManager txManager) {
        this.sender = sender;
        this.emailRepo = emailRepo;
        this.requiresNewTx = new TransactionTemplate(txManager);
        this.requiresNewTx.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
    }

    public void procesarEmail(UUID emailId, int maxAttempts) {
        EmailMessage msg = claimForProcessing(emailId, maxAttempts);
        if (msg == null) {
            return;
        }

        try {
            sender.sendNow(msg);
        } catch (EmailBlacklistViolationException e) {
            markFailure(msg, e.getMessage(), EmailDeliveryException.Kind.PERMANENT, true);
            log.error("Correo bloqueado emailId={}", msg.getId());
            return;
        } catch (Exception e) {
            var kind = e instanceof EmailDeliveryException d ? d.kind()
                    : isMissingAttachmentFailure(e) ? EmailDeliveryException.Kind.PERMANENT : EmailDeliveryException.Kind.TRANSIENT;
            boolean terminal = kind != EmailDeliveryException.Kind.TRANSIENT
                    || (msg.getType() != com.erp.emails.model.EmailType.DOC_ELECTRONICO && msg.getAttempts() >= maxAttempts);
            markFailure(msg, e.getMessage(), kind, terminal);
            log.error("Fallo procesando email {}: {}", msg.getId(), e.getMessage(), e);
            return;
        }
        // Un fallo al persistir SENT no es un fallo SMTP: la reserva quedara UNCERTAIN.
        try {
            if(markSent(msg)) cleanupIfTerminal(msg);
        } catch(Exception persistence) {
            log.error("SMTP acepto email {}, pero no se pudo confirmar en BD; requiere conciliacion", msg.getId(), persistence);
        }
    }

    private boolean isMissingAttachmentFailure(Throwable error) {
        Throwable current = error;
        while (current != null) {
            String message = current.getMessage();
            if (current instanceof java.io.FileNotFoundException) {
                return true;
            }
            if (message != null) {
                String normalized = message.toLowerCase();
                if (normalized.contains("no se encontro el adjunto")
                        || normalized.contains("no se encontró el adjunto")
                        || normalized.contains("file not found")) {
                    return true;
                }
            }
            current = current.getCause();
        }
        return false;
    }

    private EmailMessage claimForProcessing(UUID emailId, int maxAttempts) {
        return requiresNewTx.execute(status -> {
            EmailMessage msg = emailRepo.lockById(emailId).orElse(null);
            if (msg == null) {
                return null;
            }
            if (msg.getProcessingToken() != null || msg.getStatus() != EmailStatus.PENDING
                    || (msg.getType() != com.erp.emails.model.EmailType.DOC_ELECTRONICO && msg.getAttempts() >= maxAttempts)
                    || (msg.getNextAttemptAt() != null && msg.getNextAttemptAt().isAfter(OffsetDateTime.now()))) {
                return null;
            }
            msg.setAttempts(msg.getAttempts() == Integer.MAX_VALUE ? Integer.MAX_VALUE : msg.getAttempts() + 1);
            // Reserva temporal: otra instancia no debe tomar el mismo mensaje durante el envio.
            msg.setProcessingToken(UUID.randomUUID());
            msg.setProcessingUntil(OffsetDateTime.now().plusMinutes(15));
            msg.setNextAttemptAt(null);
            attempts.iniciar(msg);
            return emailRepo.saveAndFlush(msg);
        });
    }

    private boolean markSent(EmailMessage claimed) {
        return Boolean.TRUE.equals(requiresNewTx.execute(status -> {
            EmailMessage msg = emailRepo.lockById(claimed.getId()).orElse(null);
            if (msg == null || !claimed.getProcessingToken().equals(msg.getProcessingToken())) {
                return false;
            }

            msg.setStatus(EmailStatus.SENT);
            msg.setSentAt(OffsetDateTime.now());
            attempts.finalizar(claimed.getProcessingToken(), "SENT", null);
            msg.setProcessingToken(null); msg.setProcessingUntil(null); msg.setFailureKind(null);
            msg.setLastError(null);
            msg.setNextAttemptAt(null);
            emailRepo.saveAndFlush(msg);
            return true;
        }));
    }

    private void markFailure(EmailMessage claimed, String errorMessage, EmailDeliveryException.Kind kind, boolean terminalFailure) {
        requiresNewTx.executeWithoutResult(status -> {
            EmailMessage msg = emailRepo.lockById(claimed.getId()).orElse(null);
            if (msg == null || !claimed.getProcessingToken().equals(msg.getProcessingToken())) return;
            String detail=truncate(errorMessage,1900);
            msg.setLastError(detail); msg.setFailureKind(kind.name());
            long delay=retryDelays[Math.min(retryDelays.length-1,Math.max(0,msg.getAttempts()-1))];
            msg.setNextAttemptAt(terminalFailure?null:OffsetDateTime.now().plusSeconds(delay));
            if (kind==EmailDeliveryException.Kind.UNCERTAIN) msg.setStatus(EmailStatus.UNCERTAIN);
            else if(terminalFailure) msg.setStatus(EmailStatus.FAILED);
            attempts.finalizar(claimed.getProcessingToken(),kind.name(),detail);
            msg.setProcessingToken(null);msg.setProcessingUntil(null);
            emailRepo.saveAndFlush(msg);
        });
    }

    private void cleanupIfTerminal(EmailMessage msg) {
        try {
            sender.cleanupAttachments(msg);
        } catch (Exception e) {
            log.warn("No se pudieron limpiar adjuntos del email {}: {}", msg.getId(), e.getMessage());
        }
    }

    private String truncate(String value, int maxLength) {
        if (value == null || value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength);
    }
}

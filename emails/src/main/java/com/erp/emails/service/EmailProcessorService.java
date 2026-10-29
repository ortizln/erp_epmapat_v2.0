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

    @org.springframework.beans.factory.annotation.Value("${mailms.outbox.initial-backoff-seconds:30}")
    private long initialBackoffSeconds = 30;

    @org.springframework.beans.factory.annotation.Value("${mailms.outbox.backoff-multiplier:2}")
    private double backoffMultiplier = 2;

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
            markSent(msg.getId());
            cleanupIfTerminal(msg);
        } catch (EmailBlacklistViolationException e) {
            markFailure(msg.getId(), e.getMessage(), true);
            log.error("Correo bloqueado por lista negra emailId={}", msg.getId());
        } catch (Exception e) {
            boolean terminalFailure = (msg.getType() != com.erp.emails.model.EmailType.DOC_ELECTRONICO
                    && msg.getAttempts() >= maxAttempts) || isMissingAttachmentFailure(e);
            markFailure(msg.getId(), e.getMessage(), terminalFailure);

            log.error("Fallo procesando email {}: {}", msg.getId(), e.getMessage(), e);
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
            if (msg.getStatus() != EmailStatus.PENDING
                    || (msg.getType() != com.erp.emails.model.EmailType.DOC_ELECTRONICO && msg.getAttempts() >= maxAttempts)
                    || (msg.getNextAttemptAt() != null && msg.getNextAttemptAt().isAfter(OffsetDateTime.now()))) {
                return null;
            }
            msg.setAttempts(msg.getAttempts() == Integer.MAX_VALUE ? Integer.MAX_VALUE : msg.getAttempts() + 1);
            // Reserva temporal: otra instancia no debe tomar el mismo mensaje durante el envio.
            msg.setNextAttemptAt(OffsetDateTime.now().plusMinutes(15));
            return emailRepo.saveAndFlush(msg);
        });
    }

    private void markSent(UUID emailId) {
        requiresNewTx.executeWithoutResult(status -> {
            EmailMessage msg = emailRepo.lockById(emailId).orElse(null);
            if (msg == null) {
                return;
            }

            msg.setStatus(EmailStatus.SENT);
            msg.setSentAt(OffsetDateTime.now());
            msg.setLastError(null);
            msg.setNextAttemptAt(null);
            emailRepo.saveAndFlush(msg);
        });
    }

    private void markFailure(UUID emailId, String errorMessage, boolean terminalFailure) {
        requiresNewTx.executeWithoutResult(status -> {
            EmailMessage msg = emailRepo.lockById(emailId).orElse(null);
            if (msg == null) {
                return;
            }

            msg.setLastError(truncate(errorMessage, 1900));
            long delay = (long) Math.min(3600, Math.max(1, initialBackoffSeconds)
                    * Math.pow(Math.max(1, backoffMultiplier), Math.max(0, msg.getAttempts() - 1)));
            msg.setNextAttemptAt(terminalFailure ? null : OffsetDateTime.now().plusSeconds(delay));
            if (terminalFailure) {
                msg.setStatus(EmailStatus.FAILED);
            }
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

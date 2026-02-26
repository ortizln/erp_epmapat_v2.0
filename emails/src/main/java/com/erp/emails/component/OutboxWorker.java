package com.erp.emails.component;

import com.erp.emails.model.EmailStatus;
import com.erp.emails.repository.EmailMessageR;
import com.erp.emails.service.MailSenderService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
public class OutboxWorker {

    private final EmailMessageR emailRepo;
    private final MailSenderService sender;

    @Value("${mailms.outbox.batch-size:20}")
    private int batchSize;

    @Value("${mailms.outbox.max-attempts:5}")
    private int maxAttempts;

    public OutboxWorker(EmailMessageR emailRepo, MailSenderService sender) {
        this.emailRepo = emailRepo;
        this.sender = sender;
    }

    @Scheduled(fixedDelayString = "${mailms.outbox.scheduler-fixed-delay-ms:15000}")
    @Transactional
    public void process() {
        var list = emailRepo.lockNextPending(EmailStatus.PENDING, maxAttempts, PageRequest.of(0, batchSize));
        for (var msg : list) {
            try {
                msg.setAttempts(msg.getAttempts() + 1);
                sender.sendNow(msg);
                msg.setStatus(EmailStatus.SENT);
                msg.setSentAt(OffsetDateTime.now());
                msg.setLastError(null);
            } catch (Exception e) {
                msg.setLastError(e.getMessage());
                if (msg.getAttempts() >= maxAttempts) {
                    msg.setStatus(EmailStatus.FAILED);
                }
            }
        }
    }
}
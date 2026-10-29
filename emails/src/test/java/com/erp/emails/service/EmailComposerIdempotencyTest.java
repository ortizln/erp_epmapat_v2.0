package com.erp.emails.service;
import com.erp.emails.dtos.SendEmailRequest;
import com.erp.emails.model.*;
import com.erp.emails.repository.*;
import com.erp.emails.interfaces.AttachmentStorage;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
class EmailComposerIdempotencyTest {
    @Test void lostHttpResponseReusesCommittedInvoiceEmailIncludingSentMessages() {
        var repo=mock(EmailMessageR.class);var attachments=mock(EmailAttachmentR.class);
        var accounts=mock(EmailAccountService.class);var blacklist=mock(EmailBlacklistService.class);
        var jdbc=mock(JdbcTemplate.class);
        var service=new EmailComposerService(repo,attachments,mock(TemplateService.class),mock(AttachmentStorage.class),accounts,blacklist,jdbc);
        var saved=new EmailMessage();saved.setId(UUID.randomUUID());saved.setStatus(EmailStatus.SENT);
        var req=new SendEmailRequest();req.correlationId="factura:123";
        when(repo.findFirstByTypeAndCorrelationIdOrderByCreatedAtAsc(EmailType.DOC_ELECTRONICO,"factura:123")).thenReturn(Optional.of(saved));
        assertEquals(saved.getId(),service.enqueue(EmailType.DOC_ELECTRONICO,req));
        assertEquals(saved.getId(),service.enqueue(EmailType.DOC_ELECTRONICO,req));
        verify(jdbc,times(2)).queryForList("SELECT pg_advisory_xact_lock(hashtextextended(?, 0))","factura:123");
        verify(repo,never()).save(any());verifyNoInteractions(accounts,attachments,blacklist);
    }
}

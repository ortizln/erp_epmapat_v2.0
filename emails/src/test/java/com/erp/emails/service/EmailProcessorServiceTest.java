package com.erp.emails.service;

import com.erp.emails.model.*;
import com.erp.emails.repository.EmailMessageR;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.SimpleTransactionStatus;
import java.time.OffsetDateTime;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmailProcessorServiceTest {
    EmailMessageR repo = mock(EmailMessageR.class);
    MailSenderService sender = mock(MailSenderService.class);
    PlatformTransactionManager transactions = mock(PlatformTransactionManager.class);
    EmailProcessorService service;
    EmailMessage message;

    @BeforeEach void setup() {
        when(transactions.getTransaction(any(TransactionDefinition.class))).thenReturn(new SimpleTransactionStatus());
        message = new EmailMessage(); message.setId(UUID.randomUUID()); message.setStatus(EmailStatus.PENDING);
        when(repo.lockById(message.getId())).thenReturn(Optional.of(message));
        when(repo.saveAndFlush(any())).thenAnswer(call -> call.getArgument(0));
        service = new EmailProcessorService(sender, repo, transactions);
    }

    @Test void failureSchedulesRetryAndPreservesAttachments() {
        doThrow(new RuntimeException("Authentication failed")).when(sender).sendNow(message);
        service.procesarEmail(message.getId(), 5);
        assertEquals(EmailStatus.PENDING, message.getStatus());
        assertEquals(1, message.getAttempts());
        assertTrue(message.getNextAttemptAt().isAfter(OffsetDateTime.now().plusSeconds(25)));
        verify(sender, never()).cleanupAttachments(any());
    }

    @Test void terminalFailurePreservesAttachmentsForRecovery() {
        message.setAttempts(4);
        doThrow(new RuntimeException("SMTP unavailable")).when(sender).sendNow(message);
        service.procesarEmail(message.getId(), 5);
        assertEquals(EmailStatus.FAILED, message.getStatus());
        assertNull(message.getNextAttemptAt());
        verify(sender, never()).cleanupAttachments(any());
    }

    @Test void activeReservationPreventsAnotherSend() {
        message.setNextAttemptAt(OffsetDateTime.now().plusMinutes(10));
        service.procesarEmail(message.getId(), 5);
        verifyNoInteractions(sender);
    }

    @Test void successRecordsAcceptanceAndCleansFiles() {
        service.procesarEmail(message.getId(), 5);
        assertEquals(EmailStatus.SENT, message.getStatus());
        assertNotNull(message.getSentAt());
        assertNull(message.getNextAttemptAt());
        verify(sender).cleanupAttachments(message);
    }

    @Test void invoiceContinuesRetryingAfterFiveFailures() {
        message.setType(EmailType.DOC_ELECTRONICO); message.setAttempts(50);
        doThrow(new RuntimeException("Authentication failed")).when(sender).sendNow(message);
        service.procesarEmail(message.getId(), 5);
        assertEquals(EmailStatus.PENDING, message.getStatus());
        assertEquals(51, message.getAttempts());
        assertNotNull(message.getNextAttemptAt());
        verify(sender, never()).cleanupAttachments(any());
    }

    @Test void missingInvoiceAttachmentRemainsVisibleForRepair() {
        message.setType(EmailType.DOC_ELECTRONICO);
        doThrow(new RuntimeException("No se encontro el adjunto factura.xml")).when(sender).sendNow(message);
        service.procesarEmail(message.getId(), 5);
        assertEquals(EmailStatus.FAILED, message.getStatus());
        verify(sender, never()).cleanupAttachments(any());
    }

    @Test void expiredReservationCanBeRecoveredAfterRestart() {
        message.setType(EmailType.DOC_ELECTRONICO); message.setAttempts(10);
        message.setNextAttemptAt(OffsetDateTime.now().minusMinutes(1));
        service.procesarEmail(message.getId(), 5);
        assertEquals(EmailStatus.SENT, message.getStatus());
    }
}

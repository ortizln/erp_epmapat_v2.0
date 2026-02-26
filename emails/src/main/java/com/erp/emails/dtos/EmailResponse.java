package com.erp.emails.dtos;
import com.erp.emails.model.EmailStatus;
import com.erp.emails.model.EmailType;

import java.time.OffsetDateTime;
import java.util.UUID;

public class EmailResponse {
    public UUID id;
    public EmailType type;
    public EmailStatus status;
    public String subject;
    public String correlationId;
    public int attempts;
    public String lastError;
    public OffsetDateTime createdAt;
    public OffsetDateTime sentAt;
}
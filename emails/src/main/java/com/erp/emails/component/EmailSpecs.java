package com.erp.emails.component;

import com.erp.emails.model.EmailMessage;
import com.erp.emails.model.EmailStatus;
import com.erp.emails.model.EmailType;
import org.springframework.data.jpa.domain.Specification;

public class EmailSpecs {
    public static Specification<EmailMessage> filter(EmailStatus status, EmailType type, String correlationId) {
        return (root, query, cb) -> {
            var p = cb.conjunction();
            if (status != null) p.getExpressions().add(cb.equal(root.get("status"), status));
            if (type != null) p.getExpressions().add(cb.equal(root.get("type"), type));
            if (correlationId != null && !correlationId.isBlank()) {
                p.getExpressions().add(cb.equal(root.get("correlationId"), correlationId));
            }
            return p;
        };
    }
}
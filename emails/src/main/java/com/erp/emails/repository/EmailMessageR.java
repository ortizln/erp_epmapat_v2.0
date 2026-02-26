package com.erp.emails.repository;

import com.erp.emails.model.EmailMessage;
import com.erp.emails.model.EmailStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.domain.Specification;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
public interface EmailMessageR extends JpaRepository<EmailMessage, UUID>,
        JpaSpecificationExecutor<EmailMessage> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        select e from EmailMessage e
        where e.status = :status
          and e.attempts < :maxAttempts
        order by e.createdAt asc
    """)
    List<EmailMessage> lockNextPending(@Param("status") EmailStatus status,
                                       @Param("maxAttempts") int maxAttempts,
                                       Pageable pageable);

    @Query("""
        select count(e) from EmailMessage e
        where e.createdAt >= :from and e.createdAt < :to
    """)
    long countInRange(@Param("from") OffsetDateTime from, @Param("to") OffsetDateTime to);
}
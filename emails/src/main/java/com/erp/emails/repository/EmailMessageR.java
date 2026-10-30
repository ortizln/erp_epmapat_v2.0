package com.erp.emails.repository;

import com.erp.emails.model.EmailMessage;
import com.erp.emails.model.EmailStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EmailMessageR extends JpaRepository<EmailMessage, UUID>,
        JpaSpecificationExecutor<EmailMessage> {
    Optional<EmailMessage> findFirstByTypeAndCorrelationIdOrderByCreatedAtAsc(
            com.erp.emails.model.EmailType type, String correlationId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        select e from EmailMessage e
        where e.status = :status
          and e.processingToken is null
          and (e.attempts < :maxAttempts or e.type = com.erp.emails.model.EmailType.DOC_ELECTRONICO)
          and (e.nextAttemptAt is null or e.nextAttemptAt <= CURRENT_TIMESTAMP)
        order by e.createdAt asc
    """)
    List<EmailMessage> lockNextPending(@Param("status") EmailStatus status,
                                       @Param("maxAttempts") int maxAttempts,
                                       Pageable pageable);

    @Query("""
        select e.id from EmailMessage e
        where e.status = :status
          and e.processingToken is null
          and (e.attempts < :maxAttempts or e.type = com.erp.emails.model.EmailType.DOC_ELECTRONICO)
          and (e.nextAttemptAt is null or e.nextAttemptAt <= CURRENT_TIMESTAMP)
        order by e.createdAt asc
    """)
    List<UUID> findNextPendingIds(@Param("status") EmailStatus status,
                                  @Param("maxAttempts") int maxAttempts,
                                  Pageable pageable);

    @org.springframework.transaction.annotation.Transactional
    @org.springframework.data.jpa.repository.Modifying
    @Query("update EmailMessage e set e.status = com.erp.emails.model.EmailStatus.FAILED, e.nextAttemptAt = null, e.lastError = 'Reserva vencida con intentos agotados; verificar proveedor antes de reintentar' where e.status = com.erp.emails.model.EmailStatus.PENDING and e.type <> com.erp.emails.model.EmailType.DOC_ELECTRONICO and e.attempts >= :maxAttempts and (e.nextAttemptAt is null or e.nextAttemptAt <= CURRENT_TIMESTAMP)")
    int failExhausted(@Param("maxAttempts") int maxAttempts);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from EmailMessage e where e.id = :id")
    Optional<EmailMessage> lockById(@Param("id") UUID id);

    @Query("""
        select count(e) from EmailMessage e
        where e.createdAt >= :from and e.createdAt < :to
    """)
    long countInRange(@Param("from") OffsetDateTime from, @Param("to") OffsetDateTime to);
}

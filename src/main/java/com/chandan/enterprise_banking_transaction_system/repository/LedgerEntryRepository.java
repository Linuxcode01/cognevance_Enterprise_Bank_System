package com.chandan.enterprise_banking_transaction_system.repository;

import com.chandan.enterprise_banking_transaction_system.entity.LedgerEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface LedgerEntryRepository extends JpaRepository<LedgerEntry, Long> {

    List<LedgerEntry> findByAccount_AccountNumberAndDateBetween(
            String accountNumber,
            LocalDateTime startDate,
            LocalDateTime endDate
    );
}

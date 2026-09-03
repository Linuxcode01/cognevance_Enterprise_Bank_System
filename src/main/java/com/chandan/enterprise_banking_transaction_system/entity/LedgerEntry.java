package com.chandan.enterprise_banking_transaction_system.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@Entity
public class LedgerEntry {
    @Id
    private String transaction;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;
    private BigDecimal amount;
    @Enumerated(EnumType.STRING)
    private AmountType entryType; // DEBIT or CREDIT
    @Column(nullable = false)
    private BigDecimal total;
    private LocalDateTime date;
}

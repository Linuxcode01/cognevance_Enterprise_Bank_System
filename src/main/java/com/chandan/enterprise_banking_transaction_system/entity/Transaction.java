package com.chandan.enterprise_banking_transaction_system.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class Transaction {
    @Id
    @Column(unique = true, nullable = false)
    private String transactionReference;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_account_id", nullable = false)
    private Account sourceAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_account_id", nullable = false)
    private Account destinationAccount;

    private BigDecimal amount;
    private BigDecimal transactionFee;
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType; // TRANSFER, DEPOSIT, WITHDRAWAL
    @Enumerated(EnumType.STRING)
    private TransactionStatus status; // PENDING, SUCCESS, FAILED, REVERSED
    private String description;
    private String channel; // WEB, MOBILE, ATM
    private LocalDateTime initiatedAt;
    private LocalDateTime completedAt;
    private String failureReason;
}

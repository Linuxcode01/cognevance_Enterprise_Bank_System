package com.chandan.enterprise_banking_transaction_system.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
public abstract class Account {

    @Id
    @Column(nullable = false, unique = true, length = 10)
    private String accountNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    private BigDecimal availableBalance = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    private KycStatus kycStatus;

    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    private BigDecimal dailyTransferLimit;
    private BigDecimal minimumBalance;
    private LocalDate openedDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void deposit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Invalid deposit amount");
        }
        setAvailableBalance(getAvailableBalance().add(amount));
    }
}
package com.chandan.enterprise_banking_transaction_system.dto.ResponseDTO;

import com.chandan.enterprise_banking_transaction_system.entity.Account;
import com.chandan.enterprise_banking_transaction_system.entity.AmountType;
import com.chandan.enterprise_banking_transaction_system.entity.Transaction;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
public class TransactionReportResponse {
    private String transaction;
    private String account;
    private BigDecimal amount;

    private AmountType entryType; // DEBIT or CREDIT

    private BigDecimal total;
    private LocalDateTime date;
}

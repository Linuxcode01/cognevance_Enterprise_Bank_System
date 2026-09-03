package com.chandan.enterprise_banking_transaction_system.dto.RequestDTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Setter
@Getter
public class TransactionReportDTO {
    private String accountNumber;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}

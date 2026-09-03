package com.chandan.enterprise_banking_transaction_system.dto.ResponseDTO;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
@Component
@Getter
@Setter
public class AccountResponseDTO {
    private Long id;
    private String accountNumber;
    private String customerName;
    private String branchName;
    private String accountType;
    private BigDecimal balance;
    private String currency;
    private String status;
    private LocalDate openedDate;
}

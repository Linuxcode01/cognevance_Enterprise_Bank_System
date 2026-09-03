package com.chandan.enterprise_banking_transaction_system.dto.RequestDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class DepositResponseDTO {
    private String accountNumber;
    private BigDecimal amount;
    private String description;
    private LocalDateTime createAt;
}

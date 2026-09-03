package com.chandan.enterprise_banking_transaction_system.dto.ResponseDTO;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
public class DepositRequestDTO {
    private String accountNumber;
    private BigDecimal amount;
    private String description;
}

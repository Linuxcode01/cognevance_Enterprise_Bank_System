package com.chandan.enterprise_banking_transaction_system.dto.RequestDTO;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
@Component
@Getter
@Setter
public class DepositRequestDTO {
    private String accountNumber;
    private BigDecimal amount;
    private String description;
}

package com.chandan.enterprise_banking_transaction_system.dto.RequestDTO;

import com.chandan.enterprise_banking_transaction_system.entity.Branch;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
@Component
@Getter
@Setter
public class AccountRequestDTO {
    private String customerCode;
    private Branch branch;
    private String accountType;
    private BigDecimal initialDeposit;
}

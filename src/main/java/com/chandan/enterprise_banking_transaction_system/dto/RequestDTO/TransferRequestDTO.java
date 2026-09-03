package com.chandan.enterprise_banking_transaction_system.dto.RequestDTO;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.antlr.v4.runtime.misc.NotNull;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Getter
@Setter
public class TransferRequestDTO {
    @NotBlank
    private String sourceAccountNumber;
    @NotBlank
    private String destinationAccountNumber;

    @NotNull
    @DecimalMin(value = "1.00")
    private BigDecimal amount;

    private String description;
}


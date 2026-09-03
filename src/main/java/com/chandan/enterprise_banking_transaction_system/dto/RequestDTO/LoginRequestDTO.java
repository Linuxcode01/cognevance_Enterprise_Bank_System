package com.chandan.enterprise_banking_transaction_system.dto.RequestDTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class LoginRequestDTO {
    @NotBlank
    private String customerCode;
    @NotBlank
    private String password;
}

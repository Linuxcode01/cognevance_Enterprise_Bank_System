package com.chandan.enterprise_banking_transaction_system.dto.ResponseDTO;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Getter
@Setter
@Component
public class CustomerLoginResponseDTO {
    private String Customer_Name;
    private String tokenType = "Bearer";
    private String accessToken;
    private String refreshToken;
    private LocalDate createdAt;
}

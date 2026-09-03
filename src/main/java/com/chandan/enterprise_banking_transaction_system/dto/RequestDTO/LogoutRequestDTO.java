package com.chandan.enterprise_banking_transaction_system.dto.RequestDTO;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class LogoutRequestDTO {
    String refreshToken;

}

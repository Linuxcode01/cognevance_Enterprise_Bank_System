package com.chandan.enterprise_banking_transaction_system.dto.RequestDTO;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class CreateUserRequestDTO {

    private String username;
    private String email;
    private String password;
    private String mobileNumber;
    private String role;
}

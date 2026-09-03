package com.chandan.enterprise_banking_transaction_system.dto.ResponseDTO;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Getter
@Setter
public class UserResponseDTO {

    private Long id;
    private String username;
    private String email;
    private String mobileNumber;
    private boolean enabled;
    private String role;
    private boolean accountNonLocked;
    private boolean accountNonExpired;
    private boolean credentialsNonExpired;
    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
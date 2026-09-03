package com.chandan.enterprise_banking_transaction_system.mapper;

import com.chandan.enterprise_banking_transaction_system.dto.RequestDTO.CreateUserRequestDTO;
import com.chandan.enterprise_banking_transaction_system.dto.ResponseDTO.LoginResponseDTO;
import com.chandan.enterprise_banking_transaction_system.dto.ResponseDTO.UserResponseDTO;
import com.chandan.enterprise_banking_transaction_system.entity.User;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class UserMapper {

    static PasswordEncoder passwordEncoder;
    /**
     * CreateUserRequest -> User
     */
    public User toEntity(CreateUserRequestDTO request) {

        User user = new User();

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setMobileNumber(request.getMobileNumber());
        user.setRole(request.getRole());

        // default values
        user.setEnabled(true);
        user.setAccountNonLocked(true);
        user.setAccountNonExpired(true);
        user.setCredentialsNonExpired(true);

        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        return user;
    }

    /**
     * User -> UserResponse
     */
    public UserResponseDTO toResponse(User user) {

        if (user == null) {
            return null;
        }

        UserResponseDTO response = new UserResponseDTO();

        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setMobileNumber(user.getMobileNumber());
        response.setEnabled(user.isEnabled());
        response.setRole(user.getRole());
        response.setAccountNonLocked(user.isAccountNonLocked());
        response.setAccountNonExpired(user.isAccountNonExpired());
        response.setCredentialsNonExpired(user.isCredentialsNonExpired());
        response.setLastLoginAt(user.getLastLoginAt());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());

        return response;
    }

    /**
     * User -> LoginResponseDTO
     */
    public LoginResponseDTO toLoginResponse(User user,
                                                   String accessToken,
                                                   String refreshToken) {

        if (user == null) {
            return null;
        }

        LoginResponseDTO response = new LoginResponseDTO();

        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setRole(user.getRole());

        // tokenType already has default value "Bearer"
        // response.setTokenType("Bearer");

        return response;
    }

}

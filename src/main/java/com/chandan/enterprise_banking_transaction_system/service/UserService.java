package com.chandan.enterprise_banking_transaction_system.service;

import com.chandan.enterprise_banking_transaction_system.config.JWTAuthConfig;
import com.chandan.enterprise_banking_transaction_system.dto.RequestDTO.CreateUserRequestDTO;
import com.chandan.enterprise_banking_transaction_system.dto.RequestDTO.LoginRequestDTO;
import com.chandan.enterprise_banking_transaction_system.dto.RequestDTO.LogoutRequestDTO;
import com.chandan.enterprise_banking_transaction_system.dto.ResponseDTO.LoginResponseDTO;
import com.chandan.enterprise_banking_transaction_system.dto.ResponseDTO.UserResponseDTO;
import com.chandan.enterprise_banking_transaction_system.entity.RefreshToken;
import com.chandan.enterprise_banking_transaction_system.entity.User;
import com.chandan.enterprise_banking_transaction_system.exception.InvalidCredentialsException;
import com.chandan.enterprise_banking_transaction_system.exception.SessionNotFoundException;
import com.chandan.enterprise_banking_transaction_system.exception.UserAlreadyExistsException;
import com.chandan.enterprise_banking_transaction_system.mapper.UserMapper;
import com.chandan.enterprise_banking_transaction_system.repository.RefreshTokenRepository;
import com.chandan.enterprise_banking_transaction_system.repository.UserRepository;

import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@AllArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository tokenRepository;

    public UserResponseDTO creatUser(CreateUserRequestDTO createUserRequestDTO) {

        if(userRepository.existsByUsername(createUserRequestDTO.getUsername())){
            throw new UserAlreadyExistsException("Username already exist ");
        }

        if(userRepository.existsByEmail(createUserRequestDTO.getEmail())){
            throw new UserAlreadyExistsException("Email already exist ");
        }

       User user = userMapper.toEntity(createUserRequestDTO);
       user.setPassword(passwordEncoder.encode(createUserRequestDTO.getPassword()));
       User saved = userRepository.save(user);
       return userMapper.toResponse(saved);
    }

    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {

        User user = userRepository.findByUsername(loginRequestDTO.getCustomerCode());

        if(user == null || !passwordEncoder.matches(loginRequestDTO.getPassword(),user.getPassword())){
            throw new InvalidCredentialsException("Invalid username or password");
        }

        String accessToken = JWTAuthConfig.accessToken(user.getUsername());
        String refreshToken = JWTAuthConfig.refreshToken(user.getUsername());

        RefreshToken refreshToken1 = new RefreshToken();
        refreshToken1.setUser(user);
        refreshToken1.setToken(refreshToken);
        refreshToken1.setRevoked(false);
        refreshToken1.setExpiryDate(LocalDateTime.now());
        refreshToken1.setCreatedAt(LocalDateTime.now());

        tokenRepository.save(refreshToken1);

        return userMapper.toLoginResponse(user,accessToken, refreshToken);
    }

    public String refreshToken(String token) {

        RefreshToken storedToken = tokenRepository
                .findByToken(token)
                .orElseThrow( ()->
                 new SessionNotFoundException("Invalid token"));

        if(storedToken.isRevoked()){
            throw new RuntimeException("Refresh token has been revoked");
        }

        if (storedToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Refresh token expired");
        }

        if(!JWTAuthConfig.isTokenValid(token)){
            throw new SessionNotFoundException("Invalid token");
        }

        String userName = JWTAuthConfig.extractUserName(token);
        String newToken = JWTAuthConfig.refreshToken(userName);

        User user = userRepository.findByUsername(userName);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setCreatedAt(LocalDateTime.now());
        refreshToken.setExpiryDate(LocalDateTime.now().plusDays(15));
        refreshToken.setRevoked(false);
        refreshToken.setToken(newToken);

        tokenRepository.save(refreshToken);
        return "Token Created";
    }

    public void logout(String refreshToken) {

        RefreshToken token = tokenRepository
                .findByToken(refreshToken)
                .orElseThrow(() ->
                        new SessionNotFoundException("Invalid refresh token"));
        token.setRevoked(true);
        tokenRepository.save(token);

    }
}

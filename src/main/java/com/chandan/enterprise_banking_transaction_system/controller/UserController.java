package com.chandan.enterprise_banking_transaction_system.controller;

import com.chandan.enterprise_banking_transaction_system.dto.RequestDTO.CreateUserRequestDTO;
import com.chandan.enterprise_banking_transaction_system.dto.RequestDTO.LoginRequestDTO;
import com.chandan.enterprise_banking_transaction_system.dto.RequestDTO.LogoutRequestDTO;
import com.chandan.enterprise_banking_transaction_system.dto.ResponseDTO.LoginResponseDTO;
import com.chandan.enterprise_banking_transaction_system.dto.ResponseDTO.UserResponseDTO;
import com.chandan.enterprise_banking_transaction_system.entity.User;
import com.chandan.enterprise_banking_transaction_system.repository.RefreshTokenRepository;
import com.chandan.enterprise_banking_transaction_system.service.UserService;
import com.chandan.enterprise_banking_transaction_system.utils.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class UserController {

    private final UserService userService;


    // POST /register
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponseDTO>> register(@RequestBody CreateUserRequestDTO createUserRequestDTO){
        UserResponseDTO userResponseDTO = userService.creatUser(createUserRequestDTO);

        return ResponseEntity.status(HttpStatusCode.valueOf(201))
                .body(ApiResponse.success("User created Successfully", userResponseDTO));
    }

    // POST /login
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDTO>> login(
            @RequestBody LoginRequestDTO loginRequestDTO){

        LoginResponseDTO login = userService.login(loginRequestDTO);
        return ResponseEntity.ok(ApiResponse.success("login successfully", login));
    }

    // POST /refresh
    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<String>> refreshToken(
            @RequestParam String token){
       String token1 = userService.refreshToken(token);

       return ResponseEntity.ok(ApiResponse.success("refresh token", token1));
    }

//    POST //logout
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(@RequestBody LogoutRequestDTO logoutRequestDTO){
        userService.logout(logoutRequestDTO.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.success("Logout successful",null));
    }

}

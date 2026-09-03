package com.chandan.enterprise_banking_transaction_system.controller;

import com.chandan.enterprise_banking_transaction_system.config.JWTAuthConfig;
import com.chandan.enterprise_banking_transaction_system.dto.RequestDTO.*;
import com.chandan.enterprise_banking_transaction_system.dto.RequestDTO.DepositRequestDTO;
import com.chandan.enterprise_banking_transaction_system.dto.ResponseDTO.*;
import com.chandan.enterprise_banking_transaction_system.entity.LedgerEntry;
import com.chandan.enterprise_banking_transaction_system.service.CustomerService;
import com.chandan.enterprise_banking_transaction_system.service.TransferService;
import com.chandan.enterprise_banking_transaction_system.utils.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/auth/customer")
public class CustomerController {

    private final CustomerService customerService;
    private final TransferService transferService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<CustomerResponseDTO>> register(@RequestBody CustomerRequestDTO customerRequestDTO){
        CustomerResponseDTO responseDTO =  customerService.register(customerRequestDTO);
        return ResponseEntity.ok(ApiResponse.success("Register successful ", responseDTO));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<CustomerLoginResponseDTO>> login(@RequestBody LoginRequestDTO loginRequestDTO){
        CustomerLoginResponseDTO responseDTO = customerService.login(loginRequestDTO);
        return ResponseEntity.ok(ApiResponse.success("Login successful", responseDTO));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<ApiResponse<WithdrawResponseDTO>> withdraw(@RequestHeader("Authorization") String token,
                                                                     @RequestHeader(value = "X-Channel", defaultValue = "WEB") String channel,
                                                                     @RequestBody WithdrawRequestDTO requestDTO){
        WithdrawResponseDTO withdraw = customerService.withdraw(token,channel, requestDTO);
        return ResponseEntity.ok(ApiResponse.success("Withdraw successful", withdraw));
    }

    @PostMapping("/deposit")
    public ResponseEntity<ApiResponse<DepositResponseDTO>> deposit(@RequestHeader("Authorization") String token,
                                                                   @RequestBody DepositRequestDTO requestDTO,
                                                                   String channel){
        DepositResponseDTO responseDTO = customerService.deposit(token, requestDTO, channel);
        return ResponseEntity.ok(ApiResponse.success("Deposit successful", responseDTO));
    }

    @PostMapping("/transfer")
    public ResponseEntity<ApiResponse<TransactionResponseDTO>> transfer(
            @RequestHeader("Authorization") String token,
            @RequestHeader(value = "X-Channel", defaultValue = "WEB") String channel,
            @RequestBody TransferRequestDTO requestDTO) {

        TransactionResponseDTO response = transferService.transfer(token, requestDTO, channel);
        return ResponseEntity.ok(ApiResponse.success("Transfer successful", response));
    }

    @PostMapping("/generate-report")
    public ResponseEntity<ApiResponse<List<TransactionReportResponse>>> generateReport( @RequestHeader("Authorization") String token,
                           @RequestBody TransactionReportDTO responseDTO
                           ){
        List<TransactionReportResponse> entryList = customerService.generateReport(token, responseDTO);

        return ResponseEntity.ok(ApiResponse.success("List of Transaction", entryList));
    }

}

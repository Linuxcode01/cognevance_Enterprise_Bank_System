package com.chandan.enterprise_banking_transaction_system.exception;

import com.chandan.enterprise_banking_transaction_system.utils.ApiResponse;
import org.springframework.boot.system.ApplicationPid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalException extends RuntimeException {

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleAccountNotFound(
            AccountNotFoundException ex) {

        ApiResponse<String> response = new ApiResponse<>();
        response.setSuccess(false);
        response.setMessage(ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiResponse<String>> handleInvalidCredentials(
            InvalidCredentialsException ex) {

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(ex.getMessage(), null));
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<String>> handleUserAlreadyExists(
            UserAlreadyExistsException ex) {

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(ex.getMessage(), null));
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<ApiResponse<String>> handleInsufficientFund(
            InsufficientBalanceException ex) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.error("Insufficient Funds", null));
    }

    @ExceptionHandler(TransactionFailedException.class)
    public ResponseEntity<ApiResponse<String>> handleTransactionFailed(
            TransactionFailedException ex) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.error("Transaction Failed", null));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleResourceNotFound(
            ResourceNotFoundException ex) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage(), null));
    }

    @ExceptionHandler(AccountNotCreated.class)
    public ResponseEntity<ApiResponse<String>> handleAccountNotCreated(
            AccountNotCreated ex
    ){
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage(), null));
    }

    @ExceptionHandler(InvalidAmountRequest.class)
    public  ResponseEntity<ApiResponse<String>> handleInvalidAmount(InvalidAmountRequest ex){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.error(ex.getMessage(), null));
    }
}

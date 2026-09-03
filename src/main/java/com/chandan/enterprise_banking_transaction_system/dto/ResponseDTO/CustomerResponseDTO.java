package com.chandan.enterprise_banking_transaction_system.dto.ResponseDTO;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@Getter
@Setter
public class CustomerResponseDTO {
    private String customerCode;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String gender;
    private String email;
    private String mobileNumber;
    private String city;
    private String state;
    private String country;

    // Account information
    private String accountNumber;
    private String accountType;
    private String kycStatus;


    private LocalDateTime createdAt;
}

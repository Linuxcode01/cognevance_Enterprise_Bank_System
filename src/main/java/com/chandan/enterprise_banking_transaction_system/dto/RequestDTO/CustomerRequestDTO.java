package com.chandan.enterprise_banking_transaction_system.dto.RequestDTO;

import com.chandan.enterprise_banking_transaction_system.entity.AccountType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Getter
@Setter
public class CustomerRequestDTO {
    private String firstName;
    private String lastName;
    private String password;
    private LocalDate dateOfBirth;
    private String gender;
    private String aadhaarNumber;
    private String panNumber;
    private String email;
    private String mobileNumber;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String country;
    private String postalCode;
    private AccountType accountType;
}

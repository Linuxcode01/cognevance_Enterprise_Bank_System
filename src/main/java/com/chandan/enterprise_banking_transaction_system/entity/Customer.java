package com.chandan.enterprise_banking_transaction_system.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity

public class Customer {
    @Id
    @Column(nullable = false, unique = true, length = 8)
    private String customerCode;
    @Column(nullable = false)
    private String firstName;
    @Column(nullable = false)
    private String lastName;
    @Column(unique = true,nullable = false)
    private String password;
    @Column(nullable = false)
    private LocalDate dateOfBirth;
    @Column(nullable = false)
    private String gender;
    @Column(unique = true,nullable = false)
    private String aadhaarNumber;

    private String panNumber;
    private String email;
    @Column(nullable = false)
    private String mobileNumber;
    @Column(nullable = false)
    private String addressLine1;
    private String addressLine2;
    @Column(nullable = false)
    private String city;
    @Column(nullable = false)
    private String state;
    @Column(nullable = false)
    private String country;
    private String postalCode;

    @OneToMany(mappedBy = "customer")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private List<Account> accounts;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

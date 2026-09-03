package com.chandan.enterprise_banking_transaction_system.repository;

import com.chandan.enterprise_banking_transaction_system.entity.Customer;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository  extends JpaRepository<Customer, Long> {

    Customer findByCustomerCode(String customerCode);

    boolean existsByCustomerCode(String customerCode);

    boolean existsByEmail(String email);
    boolean existsByAadhaarNumber(String aadhaarNumber);

    Customer findByAadhaarNumber(String aadhaarNumber);
}

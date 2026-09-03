package com.chandan.enterprise_banking_transaction_system.mapper;
import com.chandan.enterprise_banking_transaction_system.dto.RequestDTO.CustomerRequestDTO;
import com.chandan.enterprise_banking_transaction_system.dto.ResponseDTO.CustomerResponseDTO;
import com.chandan.enterprise_banking_transaction_system.entity.Account;
import com.chandan.enterprise_banking_transaction_system.entity.Customer;
import com.chandan.enterprise_banking_transaction_system.entity.KycStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CustomerMapper {

    public static Customer toEntity(CustomerRequestDTO dto) {

        Customer customer = new Customer();
        customer.setFirstName(dto.getFirstName());
        customer.setLastName(dto.getLastName());
        customer.setDateOfBirth(dto.getDateOfBirth());
        customer.setGender(dto.getGender());
        customer.setAadhaarNumber(dto.getAadhaarNumber());
        customer.setPanNumber(dto.getPanNumber());
        customer.setEmail(dto.getEmail());
        customer.setMobileNumber(dto.getMobileNumber());
        customer.setAddressLine1(dto.getAddressLine1());
        customer.setAddressLine2(dto.getAddressLine2());
        customer.setCity(dto.getCity());
        customer.setState(dto.getState());
        customer.setCountry(dto.getCountry());
        customer.setPostalCode(dto.getPostalCode());
        customer.setCreatedAt(LocalDateTime.now());
        customer.setUpdatedAt(LocalDateTime.now());

        return customer;
    }

    public static CustomerResponseDTO toResponse(Customer customer, Account account) {

        CustomerResponseDTO dto = new CustomerResponseDTO();

        dto.setCustomerCode(customer.getCustomerCode());
        dto.setFirstName(customer.getFirstName());
        dto.setLastName(customer.getLastName());
        dto.setDateOfBirth(customer.getDateOfBirth());
        dto.setGender(customer.getGender());
        dto.setEmail(customer.getEmail());
        dto.setMobileNumber(customer.getMobileNumber());
        dto.setCity(customer.getCity());
        dto.setState(customer.getState());
        dto.setCountry(customer.getCountry());
        dto.setCreatedAt(customer.getCreatedAt());

        // Account Details
        dto.setAccountNumber(account.getAccountNumber());
        dto.setAccountType(String.valueOf(account.getAccountType()));
        dto.setKycStatus(String.valueOf(account.getKycStatus()));

        return dto;
    }
}

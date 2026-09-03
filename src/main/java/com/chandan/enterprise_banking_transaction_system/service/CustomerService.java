package com.chandan.enterprise_banking_transaction_system.service;

import com.chandan.enterprise_banking_transaction_system.config.JWTAuthConfig;
import com.chandan.enterprise_banking_transaction_system.dto.RequestDTO.*;
import com.chandan.enterprise_banking_transaction_system.dto.RequestDTO.DepositRequestDTO;
import com.chandan.enterprise_banking_transaction_system.dto.ResponseDTO.*;
import com.chandan.enterprise_banking_transaction_system.entity.*;
import com.chandan.enterprise_banking_transaction_system.exception.AccountNotCreated;
import com.chandan.enterprise_banking_transaction_system.exception.InvalidCredentialsException;
import com.chandan.enterprise_banking_transaction_system.exception.SessionNotFoundException;
import com.chandan.enterprise_banking_transaction_system.exception.UserAlreadyExistsException;
import com.chandan.enterprise_banking_transaction_system.mapper.CustomerMapper;
import com.chandan.enterprise_banking_transaction_system.repository.CustomerRefreshTokenRepository;
import com.chandan.enterprise_banking_transaction_system.repository.CustomerRepository;
import com.chandan.enterprise_banking_transaction_system.utils.CustomerCodeGenerator;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@Service
public class CustomerService {
    private CustomerRepository customerRepository;
    private PasswordEncoder passwordEncoder;
    private CustomerRefreshTokenRepository customerRefreshTokenRepository;
    private AccountService accountService;


    private Customer createCustomer(CustomerRequestDTO customerRequestDTO){
        Customer customer = CustomerMapper.toEntity(customerRequestDTO);

        String code = CustomerCodeGenerator.generate();

        int count = 0;
        while(customerRepository.existsByCustomerCode(code)){
            code = CustomerCodeGenerator.generate();

            if(count == 5){
                throw new AccountNotCreated("Customer code not generated");
            }
            count++;
        }

        customer.setCustomerCode(code);
        customer.setPassword(passwordEncoder.encode(customerRequestDTO.getPassword()));
        return customerRepository.save(customer);

    }

    @Transactional
    public CustomerResponseDTO register(CustomerRequestDTO customerRequestDTO) {

//        if(customerRepository.existsByEmail(customerRequestDTO.getEmail())){
//            throw new UserAlreadyExistsException("Customer already exist");
//        }

        Customer customer = customerRepository.findByAadhaarNumber(customerRequestDTO.getAadhaarNumber());

        if(customer != null){
            Account account = accountService.createAccount(
                    customer,
                    customerRequestDTO.getAccountType()

            );
            return CustomerMapper.toResponse(customer, account);
        }

        Customer savedCustomer = createCustomer(customerRequestDTO);

            // CREATE ACCOUNT
        Account account = accountService.createAccount(
                savedCustomer,
                customerRequestDTO.getAccountType()

        );

        return CustomerMapper.toResponse(savedCustomer, account);
    }

    public CustomerLoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
        Customer customer = customerRepository.findByCustomerCode(loginRequestDTO.getCustomerCode());
        if( customer == null){
            throw new InvalidCredentialsException("Customer Not Exist");
        }

        if(!passwordEncoder.matches(loginRequestDTO.getPassword(), customer.getPassword())){
            throw new InvalidCredentialsException("Customer Not Exist");
        }

        String refToken = JWTAuthConfig.refreshToken(loginRequestDTO.getCustomerCode());
        String accessToken = JWTAuthConfig.accessToken(loginRequestDTO.getCustomerCode());

        CustomerRefreshToken cusRefreshToken = new CustomerRefreshToken();
        cusRefreshToken.setCustomer(customer);
        cusRefreshToken.setToken(refToken);
        cusRefreshToken.setRevoked(false);
        cusRefreshToken.setExpiryDate(LocalDateTime.now().plusDays(7));
        cusRefreshToken.setCreatedAt(LocalDateTime.now());

        customerRefreshTokenRepository.save(cusRefreshToken);

        CustomerLoginResponseDTO customerLoginResponseDTO = new CustomerLoginResponseDTO();
        customerLoginResponseDTO.setAccessToken(accessToken);
        customerLoginResponseDTO.setRefreshToken(refToken);
        customerLoginResponseDTO.setCustomer_Name(customer.getFirstName() + " " + customer.getLastName());
        customerLoginResponseDTO.setCreatedAt(LocalDate.now());

        return  customerLoginResponseDTO;
    }

    public WithdrawResponseDTO withdraw(String token,String channel, WithdrawRequestDTO requestDTO) {
        String jwtToken = JWTAuthConfig.stripBearer(token);
        if(!JWTAuthConfig.isTokenValid(jwtToken)){
            throw new SessionNotFoundException("Session Expire");
        }
        accountService.withdraw(requestDTO, channel);
        WithdrawResponseDTO withdrawResponseDTO = new WithdrawResponseDTO();
        withdrawResponseDTO.setAccountNumber(requestDTO.getAccountNumber());
        withdrawResponseDTO.setAmount(requestDTO.getAmount());
        withdrawResponseDTO.setDescription(requestDTO.getDescription());
        withdrawResponseDTO.setCreateAt(LocalDateTime.now());
        return withdrawResponseDTO;

    }

    public DepositResponseDTO deposit(String token, DepositRequestDTO requestDTO, String channel) {

        String jwtToken = JWTAuthConfig.stripBearer(token);
        if(!JWTAuthConfig.isTokenValid(jwtToken)){
            throw new SessionNotFoundException("Session Expire");
        }
        accountService.deposit(requestDTO,channel);

        DepositResponseDTO depositResponseDTO = new DepositResponseDTO();
        depositResponseDTO.setAccountNumber(requestDTO.getAccountNumber());
        depositResponseDTO.setAmount(requestDTO.getAmount());
        depositResponseDTO.setDescription(requestDTO.getDescription());
        depositResponseDTO.setCreateAt(LocalDateTime.now());

        return depositResponseDTO;
    }

    public List<TransactionReportResponse> generateReport(String token, TransactionReportDTO reportDTO){
        String jwtToken = JWTAuthConfig.stripBearer(token);
        if(JWTAuthConfig.isTokenValid(jwtToken)){
            throw new SessionAuthenticationException("Session Expire");
        }
         return accountService.generateTransactionReport(reportDTO);
    }
}

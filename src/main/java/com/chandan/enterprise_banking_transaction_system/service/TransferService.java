package com.chandan.enterprise_banking_transaction_system.service;

import com.chandan.enterprise_banking_transaction_system.config.JWTAuthConfig;
import com.chandan.enterprise_banking_transaction_system.dto.RequestDTO.TransferRequestDTO;
import com.chandan.enterprise_banking_transaction_system.dto.ResponseDTO.TransactionResponseDTO;
import com.chandan.enterprise_banking_transaction_system.entity.*;
import com.chandan.enterprise_banking_transaction_system.mapper.TransactionMapper;
import com.chandan.enterprise_banking_transaction_system.repository.AccountRepository;
import com.chandan.enterprise_banking_transaction_system.repository.LedgerEntryRepository;
import com.chandan.enterprise_banking_transaction_system.repository.TransactionRepository;
import com.chandan.enterprise_banking_transaction_system.utils.TransactionNumberGenerator;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class TransferService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final LedgerEntryRepository ledgerEntryRepository;

    @Transactional
    public TransactionResponseDTO transfer(String authenticatedCustomerCode, TransferRequestDTO requestDTO, String channel) {

        String sourceNo = requestDTO.getSourceAccountNumber();
        String destNo = requestDTO.getDestinationAccountNumber();
        BigDecimal amount = requestDTO.getAmount();

        String jwtToken = JWTAuthConfig.stripBearer(authenticatedCustomerCode);
        if(!JWTAuthConfig.isTokenValid(jwtToken)){
            throw new SessionAuthenticationException("Session Expire");
        }

        if (sourceNo.equals(destNo)) {
            throw new IllegalArgumentException("Cannot transfer to the same account");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Invalid transfer amount");
        }

        // consistent lock order — smaller account number first, deadlock avoid karne ke liye
        String firstLock = sourceNo.compareTo(destNo) < 0 ? sourceNo : destNo;
        String secondLock = sourceNo.compareTo(destNo) < 0 ? destNo : sourceNo;

        Account first = accountRepository.findByAccountNumberForUpdate(firstLock)
                .orElseThrow(() -> new RuntimeException("Account not found: " + firstLock));
        Account second = accountRepository.findByAccountNumberForUpdate(secondLock)
                .orElseThrow(() -> new RuntimeException("Account not found: " + secondLock));

        Account sourceAccount = sourceNo.equals(first.getAccountNumber()) ? first : second;
        Account destinationAccount = destNo.equals(first.getAccountNumber()) ? first : second;

        // OWNERSHIP CHECK — yeh line hi missing thi withdraw() mein bhi
//        if (!sourceAccount.getCustomer().getCustomerCode().equals(authenticatedCustomerCode)) {
//            throw new RuntimeException("You do not own the source account");
//        }

        if (sourceAccount.getStatus() != AccountStatus.ACTIVE
                || destinationAccount.getStatus() != AccountStatus.ACTIVE) {
            throw new RuntimeException("One or both accounts are not active");
        }

        if (sourceAccount.getAvailableBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient funds");
        }

        sourceAccount.setAvailableBalance(sourceAccount.getAvailableBalance().subtract(amount));
        destinationAccount.setAvailableBalance(destinationAccount.getAvailableBalance().add(amount));

        accountRepository.save(sourceAccount);
        accountRepository.save(destinationAccount);

        Transaction transaction = new Transaction();
        transaction.setTransactionReference(TransactionNumberGenerator.generate());
        transaction.setSourceAccount(sourceAccount);
        transaction.setDestinationAccount(destinationAccount);
        transaction.setAmount(amount);
        transaction.setTransactionFee(BigDecimal.ZERO);
        transaction.setTransactionType(TransactionType.TRANSFER);
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setDescription(requestDTO.getDescription());
        transaction.setChannel(channel);
        transaction.setInitiatedAt(LocalDateTime.now());
        transaction.setCompletedAt(LocalDateTime.now());

        Transaction savedTransaction = transactionRepository.save(transaction);

        LedgerEntry debit = new LedgerEntry();
        debit.setTransaction(savedTransaction.getTransactionReference());
        debit.setAccount(sourceAccount);
        debit.setEntryType(AmountType.DEBIT);
        debit.setTotal(amount);
        debit.setDate(LocalDateTime.now());
        ledgerEntryRepository.save(debit);

        LedgerEntry credit = new LedgerEntry();
        credit.setTransaction(savedTransaction.getTransactionReference());
        credit.setAccount(destinationAccount);
        credit.setEntryType(AmountType.CREDIT);
        credit.setTotal(amount);
        credit.setDate(LocalDateTime.now());

        ledgerEntryRepository.save(credit);

        return TransactionMapper.toResponse(savedTransaction);
    }
}

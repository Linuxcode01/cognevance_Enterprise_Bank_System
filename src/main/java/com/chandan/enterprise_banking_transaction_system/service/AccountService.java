package com.chandan.enterprise_banking_transaction_system.service;

import com.chandan.enterprise_banking_transaction_system.Account.CurrentAccount;
import com.chandan.enterprise_banking_transaction_system.Account.FixedAccount;
import com.chandan.enterprise_banking_transaction_system.Account.SavingAccount;
import com.chandan.enterprise_banking_transaction_system.Account.Withdrawable;
import com.chandan.enterprise_banking_transaction_system.dto.RequestDTO.DepositRequestDTO;
import com.chandan.enterprise_banking_transaction_system.dto.RequestDTO.TransactionReportDTO;
import com.chandan.enterprise_banking_transaction_system.dto.RequestDTO.TransferRequestDTO;
import com.chandan.enterprise_banking_transaction_system.dto.RequestDTO.WithdrawRequestDTO;
import com.chandan.enterprise_banking_transaction_system.dto.ResponseDTO.TransactionReportResponse;
import com.chandan.enterprise_banking_transaction_system.dto.ResponseDTO.TransactionResponseDTO;
import com.chandan.enterprise_banking_transaction_system.entity.*;
import com.chandan.enterprise_banking_transaction_system.exception.AccountNotFoundException;
import com.chandan.enterprise_banking_transaction_system.exception.InsufficientBalanceException;
import com.chandan.enterprise_banking_transaction_system.exception.UserAlreadyExistsException;
import com.chandan.enterprise_banking_transaction_system.repository.AccountRepository;
import com.chandan.enterprise_banking_transaction_system.repository.LedgerEntryRepository;
import com.chandan.enterprise_banking_transaction_system.repository.TransactionRepository;
import com.chandan.enterprise_banking_transaction_system.utils.TransactionNumberGenerator;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Random;
@Service
@AllArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final LedgerEntryRepository ledgerEntryRepository;


    public String getAccountTypeByCustomerCode(String customerCode) {
        Account account = accountRepository.findByCustomer_CustomerCode(customerCode)
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Account not found for customer code: " + customerCode));

        return account.getAccountType().name();
    }

    public Account createAccount(
            Customer customer,
            AccountType accountType) {

        // agar same customer same type ke account nhi open kar sakta hai
        // account db se customer find karo, aur uska kis type ka  account hai

        boolean alreadyHasType = accountRepository
                .findByCustomer_CustomerCode(customer.getCustomerCode())
                .stream()
                .anyMatch(acc -> acc.getAccountType() == accountType);

        if (alreadyHasType) {
            throw new UserAlreadyExistsException("Customer already has a " + accountType + " account");
        }

        Account account = switch (accountType) {
            case SAVINGS -> new SavingAccount();
            case CURRENT -> new CurrentAccount();
            case FIXED -> new FixedAccount();
        };

        account.setAccountNumber(generateAccountNumber());
        account.setCustomer(customer);
        account.setAccountType(accountType);

        account.setAvailableBalance(BigDecimal.ZERO);
        account.setStatus(AccountStatus.ACTIVE);
        account.setKycStatus(KycStatus.PENDING);

        account.setDailyTransferLimit(
                BigDecimal.valueOf(100000)
        );

        account.setOpenedDate(LocalDate.now());
        account.setCreatedAt(LocalDateTime.now());
        account.setUpdatedAt(LocalDateTime.now());

        return accountRepository.save(account);
    }

    private String generateAccountNumber() {

        return String.valueOf(
                1000000000L +
                        new Random().nextInt(900000000)
        );
    }

    private TransactionReportResponse toReportDTO(LedgerEntry entry) {
        TransactionReportResponse dto = new TransactionReportResponse();
        dto.setAmount(entry.getAmount());
        dto.setAccount(entry.getAccount().getAccountNumber());
        dto.setEntryType(entry.getEntryType());
        dto.setTotal(entry.getTotal());
        dto.setTransaction(entry.getTransaction());
        dto.setDate(entry.getDate());
        return dto;
    }

    @Transactional
    public Boolean withdraw(WithdrawRequestDTO requestDTO,String channel) {
        Account account = accountRepository.findByAccountNumber(requestDTO.getAccountNumber());

        if (account == null) {
            throw new RuntimeException("Account not found");
        }

        if (!(account instanceof Withdrawable withdrawableAccount)) {
            throw new UnsupportedOperationException(
                    account.getAccountType() + " account does not support withdrawals"
            );
        }

        if (account.getAvailableBalance().compareTo(requestDTO.getAmount()) < 0){
            throw  new InsufficientBalanceException("Insufficient Fund");
        }

        withdrawableAccount.withdraw(requestDTO.getAmount(),channel);
        Account accountSaveData = accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setTransactionReference(TransactionNumberGenerator.generate());
        transaction.setSourceAccount(account);
        transaction.setDestinationAccount(account);
        transaction.setAmount(requestDTO.getAmount());
        transaction.setTransactionFee(BigDecimal.ZERO);
        transaction.setTransactionType(TransactionType.WITHDRAWAL);
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setDescription(requestDTO.getDescription());
        transaction.setChannel(channel);
        transaction.setInitiatedAt(LocalDateTime.now());
        transaction.setCompletedAt(LocalDateTime.now());

        Transaction savedTransaction = transactionRepository.save(transaction);

        LedgerEntry debit = new LedgerEntry();
        debit.setTransaction(savedTransaction.getTransactionReference());
        debit.setAccount(account);
        debit.setAmount(requestDTO.getAmount());
        debit.setEntryType(AmountType.DEBIT);
        debit.setTotal(account.getAvailableBalance());
        debit.setDate(LocalDateTime.now());

        LedgerEntry ledgerSavedData = ledgerEntryRepository.save(debit);

        return (accountSaveData != null && ledgerSavedData != null) ? true : false;
    }
    @Transactional
    public Boolean deposit(DepositRequestDTO requestDTO, String channel) {
        Account account = accountRepository.findByAccountNumber(requestDTO.getAccountNumber());
        if (account == null) throw new AccountNotFoundException("Account not found");

        account.deposit(requestDTO.getAmount());   // agar Account abstract class mein deposit() method hai
        Account accountSavedData = accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setTransactionReference(TransactionNumberGenerator.generate());
        transaction.setSourceAccount(account);
        transaction.setDestinationAccount(account);
        transaction.setAmount(requestDTO.getAmount());
        transaction.setTransactionFee(BigDecimal.ZERO);
        transaction.setTransactionType(TransactionType.TRANSFER);
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setDescription(requestDTO.getDescription());
        transaction.setChannel(channel);
        transaction.setInitiatedAt(LocalDateTime.now());
        transaction.setCompletedAt(LocalDateTime.now());

        Transaction savedTransaction = transactionRepository.save(transaction);

        LedgerEntry credit = new LedgerEntry();
        credit.setTransaction(savedTransaction.getTransactionReference());
        credit.setAccount(account);
        credit.setAmount(requestDTO.getAmount());
        credit.setEntryType(AmountType.CREDIT);
        credit.setTotal(account.getAvailableBalance());
        credit.setDate(LocalDateTime.now());

        LedgerEntry ledgerSavedData = ledgerEntryRepository.save(credit);

        return  (accountSavedData != null && ledgerSavedData != null) ? true : false;
    }

    public List<TransactionReportResponse> generateTransactionReport(TransactionReportDTO reportDTO){
       List<LedgerEntry> entryList = ledgerEntryRepository
               .findByAccount_AccountNumberAndDateBetween(reportDTO.getAccountNumber(), reportDTO.getStartDate(),
                       reportDTO.getEndDate());

       return entryList.stream().map(this::toReportDTO).toList();
    }
}


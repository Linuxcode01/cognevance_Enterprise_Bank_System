package com.chandan.enterprise_banking_transaction_system.mapper;

import com.chandan.enterprise_banking_transaction_system.dto.ResponseDTO.TransactionResponseDTO;
import com.chandan.enterprise_banking_transaction_system.entity.Account;
import com.chandan.enterprise_banking_transaction_system.entity.Transaction;

public class TransactionMapper {

    public static TransactionResponseDTO toResponse(Transaction transaction) {

        TransactionResponseDTO dto = new TransactionResponseDTO();

        dto.setTransactionReference(transaction.getTransactionReference());

        Account source = transaction.getSourceAccount();
        dto.setSourceAccountNumber(source != null ? source.getAccountNumber() : null);

        Account destination = transaction.getDestinationAccount();
        dto.setDestinationAccountNumber(destination != null ? destination.getAccountNumber() : null);

        dto.setAmount(transaction.getAmount());
        dto.setTransactionFee(transaction.getTransactionFee());

        dto.setStatus(transaction.getStatus().name());
        dto.setDescription(transaction.getDescription());
        dto.setInitiatedAt(transaction.getInitiatedAt());
        dto.setCompletedAt(transaction.getCompletedAt());

        return dto;
    }
}

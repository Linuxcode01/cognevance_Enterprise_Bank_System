package com.chandan.enterprise_banking_transaction_system.repository;

import com.chandan.enterprise_banking_transaction_system.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}

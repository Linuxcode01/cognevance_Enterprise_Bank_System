package com.chandan.enterprise_banking_transaction_system.Account;

import com.chandan.enterprise_banking_transaction_system.entity.AccountStatus;
import com.chandan.enterprise_banking_transaction_system.entity.AccountType;
import com.chandan.enterprise_banking_transaction_system.entity.Branch;
import com.chandan.enterprise_banking_transaction_system.entity.Customer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface NonWithdrawable {

    public void deposit(BigDecimal amount);
}

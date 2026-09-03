package com.chandan.enterprise_banking_transaction_system.Account;

import com.chandan.enterprise_banking_transaction_system.entity.Account;
import com.chandan.enterprise_banking_transaction_system.entity.AccountType;
import com.chandan.enterprise_banking_transaction_system.exception.InvalidAmountRequest;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
public class FixedAccount extends Account implements NonWithdrawable {

    @Override
    public void deposit(BigDecimal amount) {
        if(amount.compareTo(BigDecimal.ZERO) <= 0){
            throw new InvalidAmountRequest("Invalid deposit amount");
        }

        BigDecimal balance = getAvailableBalance();
        balance = balance.add(amount);
        setAvailableBalance(balance);
    }
}

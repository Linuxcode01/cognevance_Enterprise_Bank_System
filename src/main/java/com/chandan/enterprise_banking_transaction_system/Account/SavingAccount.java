package com.chandan.enterprise_banking_transaction_system.Account;

import com.chandan.enterprise_banking_transaction_system.entity.Account;
import com.chandan.enterprise_banking_transaction_system.entity.AccountType;
import com.chandan.enterprise_banking_transaction_system.exception.InsufficientBalanceException;
import com.chandan.enterprise_banking_transaction_system.exception.InvalidAmountRequest;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Collections;

@Entity
@Getter
@Setter
public class SavingAccount extends Account implements  Withdrawable{

    @Override
    public void withdraw(BigDecimal amount, String channel) {
        if(amount.compareTo(BigDecimal.ZERO) <= 0){
            throw new InvalidAmountRequest("Invalid withdrawal amount");
        }

        BigDecimal balance = getAvailableBalance();
        if(balance.compareTo(amount) > 0){
            balance = balance.subtract(amount);
            setAvailableBalance(balance);
        }else{
            throw new InsufficientBalanceException("Not sufficient Funds");
        }

    }

    @Override
    public void deposit(BigDecimal amount, String channel) {
        if(amount.compareTo(BigDecimal.ZERO) <= 0){
            throw new InvalidAmountRequest("Invalid deposit amount");
        }

        BigDecimal balance = getAvailableBalance();
        balance = balance.add(amount);
        setAvailableBalance(balance);
    }

}

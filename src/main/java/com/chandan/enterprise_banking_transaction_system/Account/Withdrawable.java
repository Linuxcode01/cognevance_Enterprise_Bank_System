package com.chandan.enterprise_banking_transaction_system.Account;
import java.math.BigDecimal;


public interface Withdrawable  {

     void withdraw(BigDecimal amount, String channel);
     void deposit(BigDecimal amount, String channel);
}

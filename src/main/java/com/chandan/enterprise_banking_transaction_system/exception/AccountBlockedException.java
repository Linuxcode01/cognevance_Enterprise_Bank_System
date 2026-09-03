package com.chandan.enterprise_banking_transaction_system.exception;

public class AccountBlockedException extends RuntimeException{
    public AccountBlockedException(String message){
        super(message);
    }
}

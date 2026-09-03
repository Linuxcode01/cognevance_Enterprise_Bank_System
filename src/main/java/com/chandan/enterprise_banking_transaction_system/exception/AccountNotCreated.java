package com.chandan.enterprise_banking_transaction_system.exception;

public class AccountNotCreated extends RuntimeException{
    public AccountNotCreated(String message){
        super(message);
    }
}

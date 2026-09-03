package com.chandan.enterprise_banking_transaction_system.exception;

public class SessionNotFoundException extends  RuntimeException{
    public SessionNotFoundException(String message){
        super(message);
    }
}

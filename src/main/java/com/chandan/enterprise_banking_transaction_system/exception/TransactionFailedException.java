package com.chandan.enterprise_banking_transaction_system.exception;

public class TransactionFailedException extends RuntimeException{
    public TransactionFailedException(String message){
        super(message);
    }
}

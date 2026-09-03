package com.chandan.enterprise_banking_transaction_system.exception;

public class InvalidAmountRequest extends RuntimeException{
    public InvalidAmountRequest(String message){
        super(message);
    }

}

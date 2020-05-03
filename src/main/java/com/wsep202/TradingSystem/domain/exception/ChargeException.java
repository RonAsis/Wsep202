package com.wsep202.TradingSystem.domain.exception;

public class ChargeException extends TradingSystemException{
    public ChargeException(String errorDescription){
        super("Charge error occurred: "+errorDescription);
    }
}

package com.wsep202.TradingSystem.domain.exception;

public class IllegalProductPriceException extends TradingSystemException{
    public IllegalProductPriceException(int id){
        super("The discount with id: "+id+ " caused price to be equal or less than zero!");
    }
}

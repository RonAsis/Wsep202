package com.wsep202.TradingSystem.domain.exception;

public class CompositeOperatorNullException extends TradingSystemException{
    public CompositeOperatorNullException(int id){
        super("The discount with id: "+id+" activated but it doesn't include operator!");
    }
}

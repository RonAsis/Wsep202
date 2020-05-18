package com.wsep202.TradingSystem.domain.exception;

public class CompositeOperatorDoesntExistException extends TradingSystemException {

    public CompositeOperatorDoesntExistException(String operator){
        super("The Composite Operator '" + operator + "' doesn't exists");
    }
}

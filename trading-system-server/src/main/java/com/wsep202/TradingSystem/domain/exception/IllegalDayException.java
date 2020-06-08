package com.wsep202.TradingSystem.domain.exception;

public class IllegalDayException extends TradingSystemException {
    public IllegalDayException(int day){
        super("The day received: '" + day + "' is not valid!");
    }

}

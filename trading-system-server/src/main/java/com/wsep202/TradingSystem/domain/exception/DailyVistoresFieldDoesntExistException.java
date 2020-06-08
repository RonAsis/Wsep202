package com.wsep202.TradingSystem.domain.exception;

public class DailyVistoresFieldDoesntExistException extends TradingSystemException {

    public DailyVistoresFieldDoesntExistException(String dailyVistorsField){
        super("The DailyVistoresField '" + dailyVistorsField + "' doesn't exists");
    }
}

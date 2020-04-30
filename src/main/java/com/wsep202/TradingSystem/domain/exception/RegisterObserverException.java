package com.wsep202.TradingSystem.domain.exception;

public class RegisterObserverException extends TradingSystemException{

    public RegisterObserverException(String reasonToFail){
        super("The registration to publisher failed because:" + reasonToFail);
    }
}

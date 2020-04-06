package com.wsep202.TradingSystem.domain.exception;

public class NotAdministratorException extends RuntimeException{

    public NotAdministratorException(String username){
        super("The username '" + username + "' is not Administrator");
    }
}

package com.wsep202.TradingSystem.domain.exception;

public class UserDontExistInTheSystemException extends RuntimeException {

    public UserDontExistInTheSystemException(String username){
        super("The username '" + username + "' is not username in the system");
    }
}

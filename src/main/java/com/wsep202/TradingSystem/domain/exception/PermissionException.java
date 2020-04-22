package com.wsep202.TradingSystem.domain.exception;

public class PermissionException extends TradingSystemException {

    public PermissionException(String permission){
        super("The permission '" + permission + "' doesn't exists");
    }
}

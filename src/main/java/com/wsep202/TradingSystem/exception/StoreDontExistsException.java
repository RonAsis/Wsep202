package com.wsep202.TradingSystem.exception;

public class StoreDontExistsException extends RuntimeException {

    public  StoreDontExistsException(int storeId){
        super("Store number '" + storeId + "' don't exist in the system");
    }
}

package com.wsep202.TradingSystem.domain.exception;

public class StoreDontExistsException extends TradingSystemException {

    public  StoreDontExistsException(int storeId){
        super("Store number '" + storeId + "' don't exist in the system");
    }
}

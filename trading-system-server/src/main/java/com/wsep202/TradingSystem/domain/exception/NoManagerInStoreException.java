package com.wsep202.TradingSystem.domain.exception;

public class NoManagerInStoreException extends TradingSystemException {

    public NoManagerInStoreException(String userName, int storeId){
        super("The user name '" + userName + "' is not manager in the store number: '"+ storeId + "'");
    }
}

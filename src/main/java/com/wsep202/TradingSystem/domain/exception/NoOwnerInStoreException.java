package com.wsep202.TradingSystem.domain.exception;

public class NoOwnerInStoreException extends RuntimeException {

    public NoOwnerInStoreException(String userName, int storeId){
        super("The user name '" + userName + "' is not owner in the store number: '"+ storeId + "'");
    }
}

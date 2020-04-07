package com.wsep202.TradingSystem.domain.exception;

public class PurchaseTypeDontExistException extends RuntimeException {

    public PurchaseTypeDontExistException(String type){
        super("The purchaseT type '" + type + "' don't exists");
    }
}

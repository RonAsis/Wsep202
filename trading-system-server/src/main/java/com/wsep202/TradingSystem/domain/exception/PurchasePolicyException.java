package com.wsep202.TradingSystem.domain.exception;

public class PurchasePolicyException extends TradingSystemException{
    public PurchasePolicyException(String errorDescription){
        super("Purchase policy error occured: "+errorDescription);
    }
}

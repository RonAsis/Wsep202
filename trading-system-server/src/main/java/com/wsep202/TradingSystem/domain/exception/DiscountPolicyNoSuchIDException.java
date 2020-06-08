package com.wsep202.TradingSystem.domain.exception;

public class DiscountPolicyNoSuchIDException extends TradingSystemException {
    public DiscountPolicyNoSuchIDException(int id,String storeName){
        super("Couldn't find any discount with ID: '" +id + "' in store: "+storeName);
    }

}

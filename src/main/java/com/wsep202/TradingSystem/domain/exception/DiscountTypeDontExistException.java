package com.wsep202.TradingSystem.domain.exception;

public class DiscountTypeDontExistException extends RuntimeException {

    public DiscountTypeDontExistException(String type){
        super("The discount type '" + type + "' don't exists");
    }
}

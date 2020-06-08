package com.wsep202.TradingSystem.domain.exception;

public class IllegalMinPriceException extends TradingSystemException{
    public IllegalMinPriceException(int id, double minPrice){
        super("got invalid minPrice value: "+minPrice+" for discountId: "+id);
    }
}

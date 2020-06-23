package com.wsep202.TradingSystem.domain.exception;

public class IllegalPercentageException extends TradingSystemException{
    public IllegalPercentageException(long p, double discountPercentage){
        super("got invalid percentage value: "+discountPercentage+" for discountId: "+p);
    }
}

package com.wsep202.TradingSystem.domain.exception;

public class DeliveryRequestException extends TradingSystemException {
    public DeliveryRequestException(String errorDescription){
        super("Delivery error occurred: "+errorDescription);
    }
}

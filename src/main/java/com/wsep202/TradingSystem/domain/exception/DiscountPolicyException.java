package com.wsep202.TradingSystem.domain.exception;

public class DiscountPolicyException extends TradingSystemException{
        public DiscountPolicyException(String errorDescription){
            super("Discount policy error occurred: "+errorDescription);
        }
}


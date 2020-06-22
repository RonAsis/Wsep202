package com.wsep202.TradingSystem.domain.exception;

public class IllegalProductCostOrAmountException extends TradingSystemException{
    public IllegalProductCostOrAmountException(int productSn){
        super("Cost or Amount of product with productSn : " + productSn + " can't be negative");
    }
}

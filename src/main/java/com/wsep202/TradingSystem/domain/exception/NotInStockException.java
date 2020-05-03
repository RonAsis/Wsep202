package com.wsep202.TradingSystem.domain.exception;

public class NotInStockException extends TradingSystemException {

    public NotInStockException(String productName, String storeName) {
        super("The product: " + productName + " is out of stock in store: " + storeName);
    }
}

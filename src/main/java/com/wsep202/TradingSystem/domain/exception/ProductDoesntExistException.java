package com.wsep202.TradingSystem.domain.exception;

public class ProductDoesntExistException extends RuntimeException{
    public ProductDoesntExistException(int productId,int storeId){
        super("A product with id '" + productId + "' is not exist in store with id: '"+ storeId + "'");
    }
}
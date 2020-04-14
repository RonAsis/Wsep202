package com.wsep202.TradingSystem.domain.exception;

public class CategoryDoesntExistException extends RuntimeException {

    public CategoryDoesntExistException(String category){
        super("The category '" + category + "' doesn't exists");
    }
}

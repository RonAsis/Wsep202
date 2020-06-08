package com.wsep202.TradingSystem.domain.exception;

public class CategoryDoesntExistException extends TradingSystemException {

    public CategoryDoesntExistException(String category){
        super("The category '" + category + "' doesn't exists");
    }
}

package com.wsep202.TradingSystem.domain.exception;

public class CategoryDontExistException extends RuntimeException {

    public CategoryDontExistException(String category){
        super("The category '" + category + "' don't exists");
    }
}

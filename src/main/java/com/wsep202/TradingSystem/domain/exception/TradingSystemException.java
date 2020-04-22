package com.wsep202.TradingSystem.domain.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class TradingSystemException extends RuntimeException {

    public TradingSystemException(String exceptionContent){
        super(exceptionContent);
    }
}

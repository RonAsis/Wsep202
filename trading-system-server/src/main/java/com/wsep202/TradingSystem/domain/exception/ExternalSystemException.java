package com.wsep202.TradingSystem.domain.exception;

    public class ExternalSystemException extends TradingSystemException{
        public ExternalSystemException(String errorDescription){
            super("ExternalSystem error occurred: "+errorDescription);
        }
    }


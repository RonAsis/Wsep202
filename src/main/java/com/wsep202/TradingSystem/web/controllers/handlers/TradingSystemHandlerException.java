package com.wsep202.TradingSystem.web.controllers.handlers;

import com.wsep202.TradingSystem.domain.exception.TradingSystemException;
import com.wsep202.TradingSystem.dto.respones.ResponseMsgDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class TradingSystemHandlerException {

    @ExceptionHandler(TradingSystemException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ResponseMsgDto handleTradingSystemException(TradingSystemException ex) {
        return ResponseMsgDto.builder()
                .message(ex.getMessage())
                .build();
    }
}

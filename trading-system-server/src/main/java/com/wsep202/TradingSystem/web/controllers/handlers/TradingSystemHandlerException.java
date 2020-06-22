package com.wsep202.TradingSystem.web.controllers.handlers;

import com.wsep202.TradingSystem.domain.exception.TradingSystemException;
import com.wsep202.TradingSystem.dto.respones.ResponseMsgDto;
import org.hibernate.exception.JDBCConnectionException;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.transaction.TransactionException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.ConnectException;
import java.sql.SQLNonTransientConnectionException;
import java.sql.SQLTransientConnectionException;

@RestControllerAdvice
public class TradingSystemHandlerException {

    @ExceptionHandler(TradingSystemException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ResponseMsgDto handleTradingSystemException(TradingSystemException ex) {
        return ResponseMsgDto.builder()
                .message(ex.getMessage())
                .build();
    }

    @ExceptionHandler(TransactionException.class)
    @ResponseStatus(code = HttpStatus.REQUEST_TIMEOUT)
    public ResponseMsgDto handleTransactionException(TransactionException ex){
        return ResponseMsgDto.builder()
                .message("The connection with The DataBase is lost in the server, Try Later")
                .build();
    }

    @ExceptionHandler(JDBCConnectionException.class)
    @ResponseStatus(code = HttpStatus.REQUEST_TIMEOUT)
    public ResponseMsgDto handleTransactionException(JDBCConnectionException ex){
        return ResponseMsgDto.builder()
                .message("The connection with The DataBase is lost in the server, Try Later")
                .build();
    }

    @ExceptionHandler(SQLNonTransientConnectionException.class)
    @ResponseStatus(code = HttpStatus.REQUEST_TIMEOUT)
    public ResponseMsgDto handleTransactionException(SQLNonTransientConnectionException ex){
        return ResponseMsgDto.builder()
                .message("The connection with The DataBase is lost in the server, Try Later")
                .build();
    }

    @ExceptionHandler(ConnectException.class)
    @ResponseStatus(code = HttpStatus.REQUEST_TIMEOUT)
    public ResponseMsgDto handleTransactionException(ConnectException ex){
        return ResponseMsgDto.builder()
                .message("The connection with The DataBase is lost in the server, Try Later")
                .build();
    }
    @ExceptionHandler(SQLTransientConnectionException.class)
    @ResponseStatus(code = HttpStatus.REQUEST_TIMEOUT)
    public ResponseMsgDto handleTransactionException(SQLTransientConnectionException ex){
        return ResponseMsgDto.builder()
                .message("The connection with The DataBase is lost in the server, Try Later")
                .build();
    }

    @ExceptionHandler(CannotCreateTransactionException.class)
    @ResponseStatus(code = HttpStatus.REQUEST_TIMEOUT)
    public ResponseMsgDto handleTransactionException(CannotCreateTransactionException ex){
        return ResponseMsgDto.builder()
                .message("The connection with The DataBase is lost in the server, Try Later")
                .build();
    }

}

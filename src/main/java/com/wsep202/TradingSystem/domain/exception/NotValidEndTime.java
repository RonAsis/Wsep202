package com.wsep202.TradingSystem.domain.exception;

import java.util.Calendar;

public class NotValidEndTime extends TradingSystemException {
    public NotValidEndTime(Calendar time){
        super("The received time: '" + time.toString() + "' already passed");
    }
}

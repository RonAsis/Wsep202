package com.wsep202.TradingSystem.domain.trading_system_management.statistics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RequestGetDailyVisitors {

    private Date start;

    private Date end;

    private int firstIndex;

    private int lastIndex;
}

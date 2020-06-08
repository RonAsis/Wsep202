package com.wsep202.TradingSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RequestGetDailyVisitorsDto {

    private Date start;

    private Date end;
}

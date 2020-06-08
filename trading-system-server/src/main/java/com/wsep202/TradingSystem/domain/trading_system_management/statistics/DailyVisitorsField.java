package com.wsep202.TradingSystem.domain.trading_system_management.statistics;

import com.wsep202.TradingSystem.domain.exception.DailyVistoresFieldDoesntExistException;

import java.util.Arrays;

public enum DailyVisitorsField {

    GUESTS,
    OWNERS_STORES,
    MANAGER_STORES,
    SIMPLE_USER,
    ADMIN;

    public static DailyVisitorsField getDailyVisitorsField(String dailyVisitorsField) {
        return Arrays.stream(DailyVisitorsField.values())
                .filter(visitorsField -> visitorsField.name().equals(dailyVisitorsField)).findFirst()
                .orElseThrow(() -> new DailyVistoresFieldDoesntExistException(dailyVisitorsField));
    }
}

package com.wsep202.TradingSystem.domain.trading_system_management.policy_purchase;

import com.wsep202.TradingSystem.domain.exception.IllegalDayException;

import java.util.Arrays;

public enum  Day {
    Sunday(1),
    Monday(2),
    Tuesday(3),
    Wednesday(4),
    Thursday(5),
    Friday(6),
    Saturday(7);

    public final int day;
    Day(int day) {
        this.day = day;
    }

    /**
     * converts between day to Day enum if day is valid.
     * @param day2 - the day that needs to be converted.
     * @return - the converted Day enum or IllegalDayException if day doesn't exist.
     */
    public static Day getDay(int day2) {
        return Arrays.stream(Day.values())
                .filter(day1 -> day1.day == day2).findFirst()
                .orElseThrow(() -> new IllegalDayException(day2));
    }
    public String toString(){
        switch (day){
            case 1:
                return "Sunday";
            case 2:
                return "Monday";

            case 3:
                return "Tuesday";

            case 4:
                return "Wednesday";

            case 5:
                return "Thursday";

            case 6:
                return "Friday";

            case 7:
                return "Saturday";
            default:
                throw new IllegalDayException(day);
        }
    }
}


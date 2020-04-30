package com.wsep202.TradingSystem.domain.trading_system_management.notification;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class Notification {
    /**
     * the context of the notification
     */
    private String content;

    /**
     * the username
     */
    private String principal;
}

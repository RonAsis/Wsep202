package com.wsep202.TradingSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class NotificationDto {

    /**
     * the content of the notification
     */
    private String content;
    /**
     *
     */
    private String username;

}

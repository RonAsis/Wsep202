package com.wsep202.TradingSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class NotificationDto {

    /**
     * the content of the notification
     */
    private String content;
    /**
     *
     */
    private String principal;

}

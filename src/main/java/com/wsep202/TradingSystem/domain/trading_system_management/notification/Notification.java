package com.wsep202.TradingSystem.domain.trading_system_management.notification;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Notification implements Serializable {
    /**
     * the context of the notification
     */
    @Id
    private String content;

    /**
     * the username
     */
    @Id
    private String principal;
}

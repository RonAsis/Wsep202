package com.wsep202.TradingSystem.web.controllers.shakeHandler;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class ConnectionStart {
    private String username;
    private UUID uuid;
}

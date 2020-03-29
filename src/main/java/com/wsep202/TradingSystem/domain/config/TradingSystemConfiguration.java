package com.wsep202.TradingSystem.domain.config;

import com.wsep202.TradingSystem.domain.trading_system_management.ExternalServiceManagement;
import com.wsep202.TradingSystem.domain.trading_system_management.TradingSystem;
import com.wsep202.TradingSystem.domain.trading_system_management.TradingSystemFacade;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TradingSystemConfiguration {

    private TradingSystem tradingSystem() {
        return new TradingSystem(new ExternalServiceManagement());
    }

    @Bean
    public TradingSystemFacade tradingSystemFacade(){
        return new TradingSystemFacade(tradingSystem());
    }
}

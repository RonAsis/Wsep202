package com.wsep202.TradingSystem.domain.config;

import com.wsep202.TradingSystem.domain.factory.FactoryObjects;
import com.wsep202.TradingSystem.domain.trading_system_management.ExternalServiceManagement;
import com.wsep202.TradingSystem.domain.trading_system_management.TradingSystem;
import com.wsep202.TradingSystem.domain.trading_system_management.TradingSystemFacade;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.wsep202.TradingSystem.domain.factory.FactoryObjects;

@Configuration
public class TradingSystemConfiguration {

    private TradingSystem tradingSystem() {
        return new TradingSystem(new ExternalServiceManagement());
    }

    private FactoryObjects factoryObjects(){
        return new FactoryObjects();
    }
    @Bean
    public TradingSystemFacade tradingSystemFacade(ModelMapper modelMapper){
        return new TradingSystemFacade(tradingSystem(),modelMapper, factoryObjects());
    }
}

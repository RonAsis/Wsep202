package com.wsep202.TradingSystem.config;

import com.wsep202.TradingSystem.domain.factory.FactoryObjects;
import com.wsep202.TradingSystem.domain.trading_system_management.ExternalServiceManagement;
import com.wsep202.TradingSystem.domain.trading_system_management.TradingSystem;
import com.wsep202.TradingSystem.domain.trading_system_management.TradingSystemFacade;
import com.wsep202.TradingSystem.domain.trading_system_management.UserSystem;
import org.modelmapper.ModelMapper;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TradingSystemConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ExternalServiceManagement externalServiceManagement(){
        return new ExternalServiceManagement();
    }

    @Bean
    public TradingSystem tradingSystem(FactoryObjects factoryObjects,
                                       ApplicationArguments applicationArguments,
                                       ExternalServiceManagement externalServiceManagement) {
        String usernameAdmin = applicationArguments.getSourceArgs()[0];
        String password = applicationArguments.getSourceArgs()[1];
        UserSystem admin = factoryObjects.createSystemUser(usernameAdmin, password, "admin", "admin");
        return new TradingSystem(externalServiceManagement,admin);
    }

    @Bean
    public FactoryObjects factoryObjects(){
        return new FactoryObjects();
    }

    @Bean
    public TradingSystemFacade tradingSystemFacade(ModelMapper modelMapper, TradingSystem tradingSystem, FactoryObjects factoryObjects){
        return new TradingSystemFacade(tradingSystem,modelMapper, factoryObjects);
    }
}

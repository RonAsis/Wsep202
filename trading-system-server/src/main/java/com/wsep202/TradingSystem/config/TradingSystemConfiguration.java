package com.wsep202.TradingSystem.config;

import com.wsep202.TradingSystem.domain.trading_system_management.TradingSystemDao;
import com.wsep202.TradingSystem.domain.trading_system_management.TradingSystemDaoImpl;
import com.wsep202.TradingSystem.domain.factory.FactoryObjects;
import com.wsep202.TradingSystem.domain.trading_system_management.*;
import com.wsep202.TradingSystem.domain.trading_system_management.notification.Publisher;
import com.wsep202.TradingSystem.domain.trading_system_management.notification.Subject;
import com.wsep202.TradingSystem.service.ServiceFacade;
import com.wsep202.TradingSystem.service.user_service.NotificationServiceImpl;
import org.modelmapper.ModelMapper;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class TradingSystemConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public TradingSystemDao tradingSystemDao(){
        return new TradingSystemDaoImpl();
    }

    @Bean
    @ConditionalOnMissingBean
    public ExternalServiceManagement externalServiceManagement(){
        return new ExternalServiceManagement();
    }

    @Bean
    public TradingSystem tradingSystem(FactoryObjects factoryObjects,
                                       ApplicationArguments applicationArguments,
                                       ExternalServiceManagement externalServiceManagement,
                                       TradingSystemDao tradingSystemDao,
                                       PasswordEncoder passwordEncoder) {
        String usernameAdmin = applicationArguments.getSourceArgs()[0];
        String password = applicationArguments.getSourceArgs()[1];
        UserSystem admin = factoryObjects.createSystemUser(usernameAdmin, password, "admin", "admin");
        return new TradingSystem(externalServiceManagement,admin, tradingSystemDao, passwordEncoder);
    }

    @Bean
    public FactoryObjects factoryObjects(){
        return new FactoryObjects();
    }

    @Bean
    public ServiceFacade serviceFacade(@Lazy NotificationServiceImpl notificationService){
        return new ServiceFacade(notificationService);
    }

    @Bean
    public Subject subject(TradingSystemFacade tradingSystemFacade, TradingSystemDao tradingSystemDao){
        Subject subject = new Publisher(tradingSystemDao, tradingSystemFacade);
        TradingSystem.setSubject(subject);
        return subject;
    }

    @Bean
    public TradingSystemFacade tradingSystemFacade(ModelMapper modelMapper,
                                                   TradingSystem tradingSystem,
                                                   FactoryObjects factoryObjects,
                                                   ServiceFacade serviceFacade,
                                                   TradingSystemDao tradingSystemDao){
        return new TradingSystemFacade(tradingSystem,modelMapper, factoryObjects, serviceFacade, tradingSystemDao);
    }

}

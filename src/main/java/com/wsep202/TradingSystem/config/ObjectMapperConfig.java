package com.wsep202.TradingSystem.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.wsep202.TradingSystem.config.deserializers.ConnectionStartDeserializer;
import com.wsep202.TradingSystem.config.deserializers.ShoppingCartDtoDeserializer;
import com.wsep202.TradingSystem.dto.ShoppingCartDto;
import com.wsep202.TradingSystem.web.controllers.shakeHandler.ConnectionStart;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObjectMapperConfig {

    @Bean
    public ObjectMapper objectMapper(){
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule module = createSimpleModule();
        objectMapper.registerModule(module);
        return objectMapper;
    }

    private SimpleModule createSimpleModule() {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(ShoppingCartDto.class, new ShoppingCartDtoDeserializer());
        module.addDeserializer(ConnectionStart.class, new ConnectionStartDeserializer());
        return module;
    }
}

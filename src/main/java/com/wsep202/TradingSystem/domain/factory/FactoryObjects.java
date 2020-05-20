package com.wsep202.TradingSystem.domain.factory;

import com.wsep202.TradingSystem.domain.trading_system_management.UserSystem;

/**
 * create all the new objects in the system
 */
public class FactoryObjects {
    /*
    create user in the system
     */
    public UserSystem createSystemUser(String userName, String firstName, String lastName, String password){
        return UserSystem.builder()
                .userName(userName)
                .password(password)
                .firstName(firstName)
                .lastName(lastName).build();
    }

}

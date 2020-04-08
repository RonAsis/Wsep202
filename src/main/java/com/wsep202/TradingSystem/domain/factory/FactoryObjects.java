package com.wsep202.TradingSystem.domain.factory;

import com.wsep202.TradingSystem.domain.trading_system_management.UserSystem;

/**
 * create all the new objects in the system
 */
public class FactoryObjects {

    public UserSystem createSystemUser(String userName, String firstName, String lastName, String password){
        return new UserSystem(userName, password, firstName, lastName);
    }
}

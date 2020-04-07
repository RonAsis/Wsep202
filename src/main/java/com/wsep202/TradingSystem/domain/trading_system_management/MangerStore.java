package com.wsep202.TradingSystem.domain.trading_system_management;

import lombok.Data;

import java.util.Set;

@Data
public class MangerStore {

    /**
     *
     */
    private Set<StorePermission> storePermissions;

    /**
     *
     */
    private UserSystem appointedManager;

    public boolean isTheUser(UserSystem user) {
        return appointedManager.equals(user);
    }

    public boolean isTheUser(String username) {
        return appointedManager.getUserName().equals(username);
    }

}

package com.wsep202.TradingSystem.domain.trading_system_management;

import java.util.Set;

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
}

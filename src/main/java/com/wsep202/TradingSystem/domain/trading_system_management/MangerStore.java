package com.wsep202.TradingSystem.domain.trading_system_management;

public class MangerStore {

    /**
     *
     */
    private StorePermission storePermission;

    /**
     *
     */
    private UserSystem appointedManager;

    public boolean isTheUser(UserSystem user) {
        return appointedManager.equals(user);
    }
}

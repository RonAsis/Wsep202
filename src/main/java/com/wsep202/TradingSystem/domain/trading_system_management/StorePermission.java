package com.wsep202.TradingSystem.domain.trading_system_management;

import com.wsep202.TradingSystem.domain.exception.*;

import java.util.Arrays;

public enum StorePermission {

    VIEW("view");

    public final String function;

    StorePermission(String function) {
        this.function = function;
    }

    public static StorePermission getStorePermission(String function) {
        return Arrays.stream(StorePermission.values())
                .filter(productCategory -> productCategory.function.equals(function))
                .findFirst().orElseThrow(() -> new CategoryDoesntExistException(function));
    }
}

/**
 * class that represents the management options given to a certain manager
 */
package com.wsep202.TradingSystem.domain.trading_system_management;

import com.wsep202.TradingSystem.domain.exception.*;

import java.util.Arrays;

public enum StorePermission {

    VIEW("view"),
    EDIT("edit");


    public final String function;

    StorePermission(String function) {
        this.function = function;
    }
    /**
     * get the enum class of the permission string representation received as function
     * @param function
     * @return store permission enum
     */
    public static StorePermission getStorePermission(String function) {
        return Arrays.stream(StorePermission.values())
                .filter(permission -> permission.function.equals(function))
                .findFirst().orElseThrow(() -> new CategoryDoesntExistException(function));
    }
}
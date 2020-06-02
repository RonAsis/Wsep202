/**
 * class that represents the management options given to a certain manager
 */
package com.wsep202.TradingSystem.domain.trading_system_management;

import com.wsep202.TradingSystem.domain.exception.*;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public enum StorePermission {

    VIEW("view"),
    EDIT_PRODUCT("edit product"),
    EDIT_DISCOUNT("edit discount"),
    EDIT_PURCHASE_POLICY("edit purchase policy"),
    EDIT_Managers("edit managers");

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
                .findFirst().orElseThrow(() -> new PermissionException(function));
    }

    public static List<String> getStringPermissions(Set<StorePermission> storePermissions) {
        return storePermissions.stream()
                .map(storePermission -> storePermission.function)
                .collect(Collectors.toList());
    }

    public static List<String> getStringPermissions() {
        return Arrays.stream(StorePermission.values())
                .map(storePermission -> storePermission.function)
                .collect(Collectors.toList());
    }
}

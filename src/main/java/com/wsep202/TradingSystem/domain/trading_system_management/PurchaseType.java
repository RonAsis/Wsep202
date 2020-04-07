package com.wsep202.TradingSystem.domain.trading_system_management;
import com.wsep202.TradingSystem.domain.exception.*;

import java.util.Arrays;

public enum PurchaseType {

    BUY_IMMEDIATELY("Buy immediately");

    public final String type;

    PurchaseType(String type) {
        this.type = type;
    }

    public static PurchaseType getStorePermission(String type) {
        return Arrays.stream(PurchaseType.values())
                .filter(purchaseType -> purchaseType.type.equals(type))
                .findFirst().orElseThrow(() -> new PurchaseTypeDontExistException(type));
    }
}

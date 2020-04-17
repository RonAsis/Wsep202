package com.wsep202.TradingSystem.domain.trading_system_management;
import com.wsep202.TradingSystem.domain.exception.*;

import java.util.Arrays;

public enum PurchaseType {

    BUY_IMMEDIATELY("Buy immediately");

    public final String type;

    PurchaseType(String type) {
        this.type = type;
    }

    /**
     * This method is used to find the needed type.
     * @param type - the name of the type
     * @return the value if the type if exists
     */
    public static PurchaseType getPurchaseType(String type) {
        return Arrays.stream(PurchaseType.values())
                .filter(purchaseType -> purchaseType.type.equals(type))
                .findFirst().orElseThrow(() -> new PurchaseTypeDontExistException(type));
    }
}

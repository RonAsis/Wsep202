package com.wsep202.TradingSystem.domain.trading_system_management.policy_purchase;
import com.wsep202.TradingSystem.domain.exception.*;

import java.util.Arrays;

public enum PurchaseType {

    PRODUCT_DETAILS("specific product"),
    SHOPPING_BAG_DETAILS("all store"),
    SYSTEM_DETAILS("on system"),
    USER_DETAILS("on user"),
    COMPOSED_POLICY("compose");


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

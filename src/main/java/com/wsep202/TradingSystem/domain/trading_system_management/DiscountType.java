package com.wsep202.TradingSystem.domain.trading_system_management;

import java.util.Arrays;
import com.wsep202.TradingSystem.domain.exception.*;

public enum DiscountType {

    VISIBLE_DISCOUNT("visible discount"),
    NONE("no  discount");

    public final String type;

    DiscountType(String type) {
        this.type = type;
    }

    /**
     * This method is used to find the needed type.
     * @param type - the name of the type
     * @return the value if the type if exists
     */
    public static DiscountType getDiscountType(String type) {
        return Arrays.stream(DiscountType.values())
                .filter(discountType -> discountType.type.equals(type))
                .findFirst().orElseThrow(() -> new DiscountTypeDontExistException(type));
    }
}

package com.wsep202.TradingSystem.domain.trading_system_management.discount;

import com.wsep202.TradingSystem.domain.exception.CategoryDoesntExistException;
import com.wsep202.TradingSystem.domain.exception.DiscountTypeDontExistException;
import com.wsep202.TradingSystem.domain.trading_system_management.ProductCategory;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum DiscountType {

    VISIBLE("visible"),
    CONDITIONAL_STORE("conditional store"),
    CONDITIONAL_PRODUCT("conditional product"),
    COMPOSE("compose");

    public final String type;

     DiscountType(String type){
        this.type = type;
    }

    /**
     * converts between type to DiscountType enum if type exist.
     */
    public static DiscountType getDiscountType(String type) {
        return Arrays.stream(DiscountType.values())
                .filter(discountType -> discountType.type.equals(type)).findFirst()
                .orElseThrow(() -> new DiscountTypeDontExistException(type));
    }

    /**
     * get list of the akk optional discounts types
     */
    public static List<String> getDiscountTypes(){
        return Arrays.stream(DiscountType.values())
                .map(discountType -> discountType.type)
                .collect(Collectors.toList());
    }
}

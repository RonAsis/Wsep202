package com.wsep202.TradingSystem.domain.factory;

import com.wsep202.TradingSystem.domain.trading_system_management.Product;
import com.wsep202.TradingSystem.domain.trading_system_management.UserSystem;
import com.wsep202.TradingSystem.domain.trading_system_management.discount.*;
import com.wsep202.TradingSystem.dto.VisibleDiscountDto;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * create all the new objects in the system
 */
public class FactoryObjects {
    /*
    create user in the system
     */
    public UserSystem createSystemUser(String userName, String firstName, String lastName, String password){
        return UserSystem.builder()
                .userName(userName)
                .password(password)
                .firstName(firstName)
                .lastName(lastName).build();
    }
    /*
    create visible discount
     */
    public VisibleDiscount createVisibleDiscount(Calendar endTime, double discount,
                                                 Map<Product, Integer> products) {
        return new VisibleDiscount(products,endTime,discount);
    }
    /*
    create conditional product discount
     */
    public ConditionalProductDiscount createCondProductDiscountDiscount(Map<Product, Integer> productUnderDisc,
                                                                        Calendar endTime,
                                                                        double discount,
                                                                        String description,
                                                                        Map<Product, Integer> productsAmountsToApply) {
        return new ConditionalProductDiscount(productUnderDisc,endTime,discount,description,productsAmountsToApply);
    }
    /*
    create conditional store discount
     */
    public ConditionalStoreDiscount createCondStoreDiscount(Calendar endTime, double discountPercentage, String description, double minPrice) {
        return new ConditionalStoreDiscount(minPrice,endTime,discountPercentage,description);
    }
    /*
    create the composed discount
     */
    public ConditionalComposedDiscount createComposedDiscount(CompositeOperator operator,
                                                              Map<Integer, DiscountPolicy> composedDiscounts,
                                                              Map<Integer, DiscountPolicy> discountsToApply,
                                                              Calendar endTime,
                                                              double discountPercentage,
                                                              String description) {
        return new ConditionalComposedDiscount(operator,endTime,discountPercentage,description);
    }
}

package com.wsep202.TradingSystem.domain.trading_system_management;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedList;
import java.util.List;


@Data
public class DiscountPolicy {


    /**
     * allow everyone to purchase from store
     */
    private boolean isAllAllowed = true;

    /**
     * status as string
     */
    private String whoCanBuyStatus = "allow all discounts";

    /**
     * list that contains the allowed discounts in this policy
     */
    private List<DiscountType> listOfDiscountTypes;

    public DiscountPolicy(){
        listOfDiscountTypes = new LinkedList<>();
        listOfDiscountTypes.add(DiscountType.NONE);
        listOfDiscountTypes.add(DiscountType.VISIBLE_DISCOUNT);
    }
}

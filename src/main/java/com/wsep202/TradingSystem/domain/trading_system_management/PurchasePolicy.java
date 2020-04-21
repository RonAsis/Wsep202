package com.wsep202.TradingSystem.domain.trading_system_management;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedList;
import java.util.List;


@Data
public class PurchasePolicy {

    /**
     * allow everyone to purchase from store
     */
    private boolean isAllAllowed = true;

    /**
     * status as string
     */
    private String whoCanBuyStatus = "allow all purchases";

    /**
     * a list that includes all the purchase types which are allowed in the store.
     */
    private List<PurchaseType> listOfPurchaseTypes;

    public PurchasePolicy(){
        listOfPurchaseTypes = new LinkedList<>();
        listOfPurchaseTypes.add(PurchaseType.BUY_IMMEDIATELY);
    }
}

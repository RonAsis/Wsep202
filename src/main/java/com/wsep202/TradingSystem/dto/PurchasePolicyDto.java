package com.wsep202.TradingSystem.dto;

import com.wsep202.TradingSystem.domain.trading_system_management.PurchaseType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PurchasePolicyDto {

    /**
     * allow everyone to purchase from store
     */
    private boolean isAllAllowed;

    /**
     * status as string
     */
    private String whoCanBuyStatus;

    /**
     * a list that includes all the purchase types which are allowed in the store.
     */
    private List<String> listOfPurchaseTypes;
}

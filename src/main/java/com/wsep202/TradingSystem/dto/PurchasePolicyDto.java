package com.wsep202.TradingSystem.dto;

import com.wsep202.TradingSystem.domain.trading_system_management.Day;
import com.wsep202.TradingSystem.domain.trading_system_management.discount.CompositeOperator;
import com.wsep202.TradingSystem.domain.trading_system_management.purchase.PurchasePolicy;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PurchasePolicyDto {

    /**
     * The id of the discount
     */
    private int id;
    /**
     * logical operator between policies
     */
    private CompositeOperator compositeOperator;
    /**
     * children components of the composite Purchase policy
     * the operands of the composed Purchase policy
     */
    private List<PurchasePolicy> composedPurchasePolicies;
    /**
     * the SN of the product which have the purchase policy on.
     */
    private int productId;
    /**
     * the range of amounts defined as valid to buy product from the store
     */
    private int min;
    private int max;
    /**
     * the days in the week any user is permitted to perform a purchase
     */
    private Set<Day> storeWorkDays;
    /**
     * list of countries which their residents ca purchase in the store
     */
    private Set<String> countriesPermitted;

}

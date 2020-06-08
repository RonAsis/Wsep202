package com.wsep202.TradingSystem.dto;

import com.wsep202.TradingSystem.domain.trading_system_management.policy_purchase.Day;
import com.wsep202.TradingSystem.domain.trading_system_management.discount.CompositeOperator;
import com.wsep202.TradingSystem.domain.trading_system_management.policy_purchase.Purchase;
import com.wsep202.TradingSystem.domain.trading_system_management.policy_purchase.PurchasePolicy;
import com.wsep202.TradingSystem.domain.trading_system_management.policy_purchase.PurchaseType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.OneToOne;
import java.util.List;
import java.util.Map;
import java.util.Set;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PurchasePolicyDto {

    protected int purchaseId;
    /**
     * types of discounts in the store
     */
    @OneToOne(cascade = CascadeType.ALL)
    private PurchasePolicy purchasePolicy;

    private PurchaseType purchaseType;

    /**
     * list of countries that the store have deliveries to
     */
    private Set<String> countriesPermitted;
    /**
     * the days in the week any user is permitted to perform a purchase
     */
    private Set<Day> storeWorkDays;
    /**
     * the valid range of amount of products to buy in a purchase
     * in ShoppingBagDetails it is the amount of different items
     * in ProductDetails it is the amount of items from a specific one product
     */
    private int min,max;
    /**
     * the SN of product which has limitations amounts
     */
    private int productId;
    /**
     * logical operator between policies
     */
    private CompositeOperator compositeOperator;
    /**
     * children components of the composite Purchase policy
     * the operands of the composed Purchase policy
     */
    private List<PurchasePolicyDto> composedPurchasePolicies;

}

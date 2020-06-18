package com.wsep202.TradingSystem.domain.trading_system_management.policy_purchase;
/**
 * the class defines a purchase policy in store
 */

import com.wsep202.TradingSystem.domain.trading_system_management.Store;
import com.wsep202.TradingSystem.domain.trading_system_management.discount.DiscountPolicy;
import com.wsep202.TradingSystem.domain.trading_system_management.discount.DiscountType;
import com.wsep202.TradingSystem.domain.trading_system_management.purchase.BillingAddress;
import com.wsep202.TradingSystem.domain.trading_system_management.Product;
import com.wsep202.TradingSystem.domain.trading_system_management.discount.CompositeOperator;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import javax.validation.constraints.Min;
import java.util.*;

@Data
@Builder
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Purchase {

    /**
     * unique id of the purchase policy in the store
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected long purchaseId;

    /**
     * the actual type of purchase policy
     */
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private PurchasePolicy purchasePolicy;

    /**
     * the type of policy
     */
    @Enumerated(EnumType.STRING)
    private PurchaseType purchaseType;

    /**
     * describes the verbal description of the policy
     */
    private String description;

    /**
     * the following method check if the user and the received products stands in the current
     * purchase policy of the store.
     */
    public boolean isApproved(Map<Product, Integer> products, BillingAddress userAddress){
        return purchasePolicy.isApproved(this, products,userAddress);
    }

    ///////////////////////////////////////////////// edit /////////////////////////////////////

    public boolean editPurchase(String description,
                                PurchasePolicy purchasePolicy,
                                PurchaseType purchaseType) {
        this.description = description;
        this.purchasePolicy = purchasePolicy;
        this.purchaseType = purchaseType;
        return true;
    }




}

package com.wsep202.TradingSystem.domain.trading_system_management.policy_purchase;
/**
 * the class defines a purchase policy in store
 */

import com.wsep202.TradingSystem.domain.trading_system_management.purchase.BillingAddress;
import com.wsep202.TradingSystem.domain.trading_system_management.Product;
import com.wsep202.TradingSystem.domain.trading_system_management.discount.CompositeOperator;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import javax.validation.constraints.Min;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@Builder
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Purchase {

    protected static int purchaseIdAcc = 1;
    /**
     * saves the last purchaseSnAcc when a new product is created
     */

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Min(value = 1, message = "Must be greater than or equal zero")
    protected int purchaseId;

    /**
     * types of discounts in the store
     */
    @OneToOne(cascade = CascadeType.ALL)
    private PurchasePolicy purchasePolicy;


    private PurchaseType purchaseType;

    public Purchase(PurchasePolicy purchasePolicy,
                    PurchaseType purchaseType) {
        this.purchasePolicy = purchasePolicy;
        this.purchaseType = purchaseType;
        purchaseId = generatePurchaseSn();
    }

    private int generatePurchaseSn(){
        return purchaseIdAcc++;
    }

    /**
     * edit the right policy
     * @param countriesPermitted - for user policy
     * @param storeWorkDays - for system policy
     * @param min - for product OR bag policy
     * @param max - for product OR bag policy
     * @param productId - for product policy
     * @param compositeOperator - for composed policy
     * @param composedPurchasePolicies - for composed policy
     * @return true if policy was changed
     */
    public boolean edit(Set<String> countriesPermitted, Set<Day> storeWorkDays, int min, int max, int productId,
                        CompositeOperator compositeOperator, List<Purchase> composedPurchasePolicies){
        boolean isChanged = false;
        if (purchasePolicy instanceof ProductDetailsPolicy){
            isChanged = ((ProductDetailsPolicy) purchasePolicy).
                    edit(this, min, max, productId);
        }
        else if (purchasePolicy instanceof ShoppingBagDetailsPolicy) {
            isChanged = ((ShoppingBagDetailsPolicy) purchasePolicy).
                    edit(this, min, max);
        }
        else if (purchasePolicy instanceof UserDetailsPolicy){
            isChanged = ((UserDetailsPolicy) purchasePolicy).
                    edit(this,countriesPermitted);
        }
        else if (purchasePolicy instanceof SystemDetailsPolicy){
            isChanged = ((SystemDetailsPolicy) purchasePolicy).
                    edit(this, storeWorkDays);
        }
        else{
            isChanged = ((ComposedPurchase) purchasePolicy).
                    edit(compositeOperator, composedPurchasePolicies);

        }
        return isChanged;
    }


    public boolean isApproved(Map<Product, Integer> products, BillingAddress userAddress){
       return purchasePolicy.isApproved(this, products,userAddress);

    }


    /////////////////////////////////////is-methods/////////////////////////////////////////



//    /**
//     * the types of purchase exist in the store
//     */
//    @ManyToMany
//    @JoinTable()
//    private Map<String, PurchasePolicy> purchasePolicies;
    //    /**
//     * tells if its purchase policy at the shopping bag level
//     */
//    private boolean isShoppingBagPurchaseLimit;
}

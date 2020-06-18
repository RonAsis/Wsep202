package com.wsep202.TradingSystem.domain.trading_system_management.policy_purchase;
import com.wsep202.TradingSystem.domain.exception.PurchasePolicyException;
import com.wsep202.TradingSystem.domain.trading_system_management.purchase.BillingAddress;
import com.wsep202.TradingSystem.domain.trading_system_management.Product;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Setter
@Getter
@Slf4j
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingBagDetailsPolicy extends PurchasePolicy {

    private int min,max;

    public ShoppingBagDetailsPolicy(int min, int max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public boolean isApproved(Purchase purchase, Map<Product, Integer> products, BillingAddress userAddress) {
        int amountOfProductsInBag = products.size();
        if (!isStandsInTerms(amountOfProductsInBag,min,max)) {
            //is not approved policy terms
            log.info("bad amount of products in shopping bag: " + amountOfProductsInBag + " \npurchase " +
                    "policy with id: " + purchase.getPurchaseId() + " failed");
            throw new PurchasePolicyException("Sorry, your shopping bag details are incompatible with" +
                    "purchase policy: your shopping bag has "
                    + amountOfProductsInBag + " products but the policy minimum required is " + min + "and maximum is " +
                    max);
        }
        //approved policy terms on amount of products in bag
        log.info("shopping bag passed the shopping bag purchase policy with" +
                "ID: " + purchase.getPurchaseId());
        return true;
    }
    /**
     * checks that amount of product is in the required range
     * @param amount of product in bag
     * @return
     */
    private boolean isStandsInTerms(int amount,int min,int max) {
        return amount >= min && amount <= max && min>=0 && max>0;
    }

    /**
     * edit min and max
     * @param min - products to by in store
     * @param max - products to by in store
     * @return true if success, else false
     */
    public boolean edit(Purchase purchase, int min,int max){
        if(min < 0 || max < 0 || min > max){
            log.info("problem with updating policy in bag purchase policy number " + purchase.getPurchaseId());
            return false;
        }
        this.min = min;
        this.max = max;
        log.info("updated min & max in bag purchase policy number " + purchase.getPurchaseId());
        return true;
    }
}

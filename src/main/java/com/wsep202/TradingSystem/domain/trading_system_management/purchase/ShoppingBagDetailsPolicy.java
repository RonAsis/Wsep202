package com.wsep202.TradingSystem.domain.trading_system_management.purchase;
import com.wsep202.TradingSystem.domain.trading_system_management.BillingAddress;
import com.wsep202.TradingSystem.domain.trading_system_management.Product;
import com.wsep202.TradingSystem.domain.trading_system_management.UserSystem;
import com.wsep202.TradingSystem.domain.trading_system_management.notification.Notification;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Setter
@Getter
@Slf4j
@Builder
public class ShoppingBagDetailsPolicy extends PurchasePolicy {
//    private int min,max;
//
//    public ShoppingBagDetailsPolicy(int min, int max) {
//        this.min = min;
//        this.max = max;
////        this.id = getPurchaseIdAcc();
//    }

    @Override
    public boolean isApproved(Purchase purchase, Map<Product, Integer> products, UserSystem user, BillingAddress userAddress) {
        int amountOfProductsInBag = products.size();
        if (!isStandsInTerms(amountOfProductsInBag,purchase.getMin(),purchase.getMax())) {
            //is not approved policy terms
            user.newNotification(Notification.builder().content("" +
                    "Sorry, your shopping bag details are incompatible with" +
                    "purchase policy: your shopping bag has "
                    + amountOfProductsInBag + " products but the policy minimum required is " + purchase.getMin() + "and maximum is " +
                    purchase.getMax()).build());
            log.info("bad amount of products in shopping bag: " + amountOfProductsInBag + " \npurchase " +
                    "policy with id: " + purchase.purchaseId + " failed");
            return false;
        }
        //approved policy terms on amount of products in bag
        log.info("shopping bag passed the shopping bag purchase policy with" +
                "ID: " + purchase.purchaseId);
        return true;
    }
    /**
     * checks that amount of product is in the required range
     * @param amount of product in bag
     * @return
     */
    private boolean isStandsInTerms(int amount,int min,int max) {
        return amount >= min && amount <= max;
    }


}

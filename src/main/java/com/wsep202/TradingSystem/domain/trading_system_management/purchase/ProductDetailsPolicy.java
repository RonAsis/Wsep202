package com.wsep202.TradingSystem.domain.trading_system_management.purchase;
import com.wsep202.TradingSystem.domain.exception.PurchasePolicyException;
import com.wsep202.TradingSystem.domain.trading_system_management.BillingAddress;
import com.wsep202.TradingSystem.domain.trading_system_management.Product;
import com.wsep202.TradingSystem.domain.trading_system_management.UserSystem;
import com.wsep202.TradingSystem.domain.trading_system_management.notification.Notification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Optional;

@Setter
@Getter
@Slf4j
@Builder
public class ProductDetailsPolicy extends PurchasePolicy {


    @Override
    public boolean isApproved(Purchase purchase, Map<Product, Integer> products, BillingAddress userAddress) {
        Optional<Product> optionalProduct = products.keySet().stream().
                filter(product -> product.getProductSn()==purchase.getProductId()).findFirst();

        if(optionalProduct.isPresent()){
            Product productInStore = optionalProduct.get();
            int amount = products.get(productInStore);
            boolean isApproved = isStandsInTerms(amount,purchase.getMin(),purchase.getMax());
            if(!isApproved){
                log.info("bad amount of items for product: "+productInStore.getName()+" \npurchase " +
                        "policy failed");
                throw new PurchasePolicyException("Sorry, your product details are incompatible with" +
                        "purchase policy: product '"+productInStore.getName()+"' has "
                        +amount+" items but the policy minimum required is "+purchase.getMin()+ "and maximum is "+
                        purchase.getMax());
            }
        log.info("product: "+productInStore.getName()+" passed the product purchase policy with" +
                "ID: "+ purchase.purchaseId);
        }
        //succeeded due to empty manner or product stands in terms
        log.info("purchase policy of product details passed for: "+purchase.getProductId());
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

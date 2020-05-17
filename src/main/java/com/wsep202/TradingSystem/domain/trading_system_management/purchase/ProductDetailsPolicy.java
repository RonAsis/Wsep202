package com.wsep202.TradingSystem.domain.trading_system_management.purchase;
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
    private int productId;  //the SN of product which has limitations amounts
    private int min,max;

    public ProductDetailsPolicy(int productId, int min, int max) {
        this.productId = productId;
        this.min = min;
        this.max = max;
        this.id = getPurchaseIdAcc();
    }

    @Override
    public boolean isApproved(Map<Product, Integer> products, UserSystem user, BillingAddress userAddress) {
        Optional<Product> optionalProduct = products.keySet().stream().
                filter(product -> product.getProductSn()==productId).findFirst();

        if(optionalProduct.isPresent()){
            Product productInStore = optionalProduct.get();
            int amount = products.get(productInStore);
            boolean isApproved = isStandsInTerms(amount);
            if(!isApproved){
                user.newNotification(Notification.builder().content("" +
                        "Sorry, your product details are incompatible with" +
                        "purchase policy: product '"+productInStore.getName()+"' has "
                +amount+" items but the policy minimum required is "+this.min+ "and maximum is "+
                        this.max).build());
                log.info("bad amount of items for product: "+productInStore.getName()+" \npurchase " +
                        "policy failed");
                return false;
            }
        log.info("product: "+productInStore.getName()+" passed the product purchase policy with" +
                "ID: "+ id);
        }
        //succeeded due to empty manner or product stands in terms
        log.info("purchase policy of product details passed for: "+productId);
        return true;
    }

    /**
     * checks that amount of product is in the required range
     * @param amount of product in bag
     * @return
     */
    private boolean isStandsInTerms(int amount) {
        return amount >= min && amount <= max;
    }

}

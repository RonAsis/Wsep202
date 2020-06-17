package com.wsep202.TradingSystem.domain.trading_system_management.policy_purchase;
import com.wsep202.TradingSystem.domain.exception.PurchasePolicyException;
import com.wsep202.TradingSystem.domain.trading_system_management.purchase.BillingAddress;
import com.wsep202.TradingSystem.domain.trading_system_management.Product;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Entity;
import java.util.Map;
import java.util.Optional;

@Setter
@Getter
@Slf4j
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailsPolicy extends PurchasePolicy {

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

    @Override
    public boolean isApproved(Purchase purchase, Map<Product, Integer> products, BillingAddress userAddress) {
        Optional<Product> optionalProduct = products.keySet().stream().
                filter(product -> product.getProductSn()== productId).findFirst();

        if(optionalProduct.isPresent()){
            Product productInStore = optionalProduct.get();
            int amount = products.get(productInStore);
            boolean isApproved = isStandsInTerms(amount,min,max);
            if(!isApproved){
                log.info("bad amount of items for product: "+productInStore.getName()+" \npurchase " +
                        "policy failed");
                throw new PurchasePolicyException("Sorry, your product details are incompatible with" +
                        "purchase policy: product '"+productInStore.getName()+"' has "
                        +amount+" items but the policy minimum required is "+ min+ "and maximum is "+
                        max);
            }
        log.info("product: "+productInStore.getName()+" passed the product purchase policy with" +
                "ID: "+ purchase.getPurchaseId());
        }
        //succeeded due to empty manner or product stands in terms
        log.info("purchase policy of product details passed for: "+ productId);
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
     * @param min - amount of product to buy
     * @param max - amount of product to buy
     * @return true if success, else false
     */
    public boolean edit(Purchase purchase, int min,int max, int productId){
        if(min < 0 || max < 0 || min > max || this.productId != productId){
            log.info("problem with updating product details in product purchase policy number " + purchase.getPurchaseId());
            return false;
        }
        this.min = min;
        this.max = max;
        log.info("updated product details in product purchase policy number " + purchase.getPurchaseId());
        return true;
    }
}

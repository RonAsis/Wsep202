/**
 * this class represents a conditional discount to apply at the store level.
 * apply the discount on the sum of the purchase cost.
 */
package com.wsep202.TradingSystem.domain.trading_system_management.discount;
import com.wsep202.TradingSystem.domain.exception.IllegalMinPriceException;
import com.wsep202.TradingSystem.domain.exception.IllegalPercentageException;
import com.wsep202.TradingSystem.domain.exception.IllegalProductPriceException;
import com.wsep202.TradingSystem.domain.exception.NotValidEndTime;
import com.wsep202.TradingSystem.domain.trading_system_management.Product;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Entity;
import java.util.Calendar;
import java.util.Map;

@Setter
@Getter
@Builder
@Slf4j
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class ConditionalStoreDiscount extends DiscountPolicy {

    /**
     * the minimal price of purchase to apply the discount from
     */
    private double minPrice;
    @Override
    public void applyDiscount(Discount discount, Map<Product, Integer> products) {
        verifyValidity(discount);

        //The discount time is not expired yet
        if (!isExpired(discount) && !discount.isApplied()) {
            double totalPurchasedCost = getTotalPurchasedCost(products);
            if (minPrice <= totalPurchasedCost) {
                products.keySet().forEach(product -> {
                    setCostAfterDiscount(discount, product, calculateDiscount(discount, product.getOriginalCost()));
                });
            }
            discount.setApplied(true);
        } else {  //check if needs to update back the price
            undoDiscount(discount, products);
        }
    }

    @Override
    public boolean isApprovedProducts(Discount discount, Map<Product, Integer> products) {
        verifyValidity(discount);
        return !isExpired(discount) && minPrice <= getTotalPurchasedCost(products);
    }

    @Override
    public void undoDiscount(Discount discount, Map<Product, Integer> products) {
        if (discount.isApplied()) {
            products.keySet().forEach(product -> {
                double discountCost = calculateDiscount(discount, product.getOriginalCost());
                product.setCost(product.getCost() + discountCost);    //update price
            });
        }
    }

    @Override
    public void removeProductFromDiscount(int productSn) {
        // not need to do anything there is not product
    }
    private void verifyValidity(Discount discount) {
        if(discount.getDiscountPercentage()<0){
            throw new IllegalPercentageException(discount.getDiscountId(),discount.getDiscountPercentage());
        }
        if(minPrice<0){
            throw new IllegalMinPriceException(discount.getDiscountId(),minPrice);
        }
    }
}

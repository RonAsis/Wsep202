package com.wsep202.TradingSystem.domain.trading_system_management.discount;
/**
 * products under discount with no condition
 */

import com.wsep202.TradingSystem.domain.trading_system_management.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Data
@AllArgsConstructor
@Slf4j
@Builder
public class VisibleDiscount extends DiscountPolicy {

    /**
     * amount of product from to apply discount
     */
    private Map<Product, Integer> amountOfProductsForApplyDiscounts;

    @Override
    public void applyDiscount(Discount discount, Map<Product, Integer> products) {
        if (!isExpired(discount)) {
            products.keySet().forEach(product -> {
                if (isProductHaveDiscount(amountOfProductsForApplyDiscounts, product)) {
                    double discountCost = (discount.getDiscountPercentage() / 100) * product.getOriginalCost();
                    setCostAfterDiscount(discount, product, discountCost);
                }
            });
        } else {  //check if needs to update back the price
            undoDiscount(discount, products);
        }
    }

    @Override
    public boolean isApprovedProducts(Discount discount, Map<Product, Integer> products) {
        return !isExpired(discount) && products.keySet().stream()
                .anyMatch(product -> isProductHaveDiscount(amountOfProductsForApplyDiscounts, product));
    }

    @Override
    public void undoDiscount(Discount discount, Map<Product, Integer> products) {
        products.keySet().forEach(product -> {
            if (isProductHaveDiscount(amountOfProductsForApplyDiscounts, product) && discount.isApplied()) {
                double discountCost = calculateDiscount(discount, product.getOriginalCost());
                product.setCost(product.getCost() + discountCost);    //update the price by discount
            }
        });
    }

}

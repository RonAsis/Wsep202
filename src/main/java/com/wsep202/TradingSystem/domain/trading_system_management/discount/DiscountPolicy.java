package com.wsep202.TradingSystem.domain.trading_system_management.discount;

import com.wsep202.TradingSystem.domain.exception.IllegalProductPriceException;
import com.wsep202.TradingSystem.domain.trading_system_management.Product;

import java.util.Calendar;
import java.util.Map;

/**
 * The discount policy defines the discount interface
 */
public abstract class DiscountPolicy {

    public abstract void applyDiscount(Discount discount, Map<Product, Integer> products);

    public abstract boolean isApprovedProducts(Discount discount, Map<Product, Integer> products);

    public abstract void undoDiscount(Discount discount, Map<Product, Integer> products);

    public boolean isExpired(Discount discount) {
        return discount.getEndTime().compareTo(Calendar.getInstance()) < 0;
    }

    ////////////////////////////////////// general /////////////////////////////////////////

    /**
     * set updated cost of product by the pre received and calculated discount
     * @param discount object related to the actual discount
     * @param product to update its price
     * @param discountCost to sub from the price
     */
    public void setCostAfterDiscount(Discount discount, Product product, double discountCost) {
        product.setCost(product.getCost() - discountCost);
        discount.setApplied(true);
        if (product.getCost() < 0) {
            throw new IllegalProductPriceException(discount.getDiscountId());
        }
    }

    /**
     * calculate the cost of the sum of products received
     * @param products to calculate their sum price
     * @return
     */
    public double getTotalPurchasedCost(Map<Product, Integer> products) {
        return products.entrySet().stream()
                .reduce((double) 0, (sum, entry) -> sum + entry.getKey().getOriginalCost() * entry.getValue(), Double::sum);
    }

    /**
     * calculate discount on product price
     * @param price of product
     * @return
     */
    public double calculateDiscount(Discount discount, double price) {
        return (discount.getDiscountPercentage() * price) / 100;
    }

    /**
     * checks if the received product is currently under discount
     */
    public boolean isProductHaveDiscount(Map<Product, Integer> amountOfProductsForApplyDiscounts, Product product) {
        return amountOfProductsForApplyDiscounts.keySet().stream()
                .anyMatch(integer -> product.getProductSn() == integer.getProductSn());
    }
}

package com.wsep202.TradingSystem.domain.trading_system_management.discount;

import com.wsep202.TradingSystem.domain.exception.IllegalProductPriceException;
import com.wsep202.TradingSystem.domain.trading_system_management.Product;

import java.util.Calendar;
import java.util.Map;

public abstract class DiscountPolicy {

    public abstract void applyDiscount(Discount discount, Map<Product, Integer> products);

    public abstract boolean isApprovedProducts(Discount discount, Map<Product, Integer> products);

    public abstract void undoDiscount(Discount discount, Map<Product, Integer> products);

    public boolean isExpired(Discount discount) {
        return discount.getEndTime().compareTo(Calendar.getInstance()) < 0;
    }

    ////////////////////////////////////// general /////////////////////////////////////////

    public void setCostAfterDiscount(Discount discount, Product product, double discountCost) {
        product.setCost(product.getCost() - discountCost);
        discount.setApplied(true);
        if (product.getCost() <= 0) {
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
     * @param price
     * @return
     */
    public double calculateDiscount(Discount discount, double price) {
        return (discount.getDiscountPercentage() * price) / 100;
    }

    public boolean isProductHaveDiscount(Discount discount, Product product) {
        return discount.getAmountOfProductsForApplyDiscounts().keySet().stream()
                .anyMatch(integer -> product.getProductSn() == integer.getProductSn());
    }
}

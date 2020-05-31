package com.wsep202.TradingSystem.domain.trading_system_management.discount;

import com.wsep202.TradingSystem.domain.exception.IllegalProductPriceException;
import com.wsep202.TradingSystem.domain.trading_system_management.Product;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Min;
import java.util.Calendar;
import java.util.Map;

/**
 * The discount policy defines the discount interface
 */
@Data
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class DiscountPolicy {

    /**
     * saves the last discountPolicySnAcc when a new product is created
     */
    private static int discountPolicySnAcc = 1;

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Min(value = 1, message = "Must be greater than or equal zero")
    private int id = generateDiscountPolicySn();
  
    /**
     * apply discounts according to the type of the executing discount
     */
    public abstract void applyDiscount(Discount discount, Map<Product, Integer> products);
    /**
     * checks weather the terms for applying the discount are existing
     */
    public abstract boolean isApprovedProducts(Discount discount, Map<Product, Integer> products);
    /**
     * responsible for undoing the discount decrementing from product price in case it has been expired
     */
    public abstract void undoDiscount(Discount discount, Map<Product, Integer> products);
    /**
     * check if the discount date has expired
     */
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

    private int generateDiscountPolicySn(){
        return discountPolicySnAcc++;
    }
}

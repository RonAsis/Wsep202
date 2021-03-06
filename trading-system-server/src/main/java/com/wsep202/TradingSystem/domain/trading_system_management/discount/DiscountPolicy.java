package com.wsep202.TradingSystem.domain.trading_system_management.discount;

import com.wsep202.TradingSystem.domain.exception.IllegalProductPriceException;
import com.wsep202.TradingSystem.domain.trading_system_management.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.time.DateUtils;

import javax.persistence.*;
import javax.validation.constraints.Min;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * The discount policy defines the discount interface
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class DiscountPolicy {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long id;

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
        return discount.isExpired();
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

    public abstract void removeProductFromDiscount(int productSn);

    public void removeProductFromCollection(Map<Product, Integer> collection ,int productSn){
        collection.entrySet().stream()
                .filter(entry -> entry.getKey().getProductSn() == productSn)
                .findFirst()
                .map(entry -> collection.remove(entry.getKey()));
    }
}

package com.wsep202.TradingSystem.domain.trading_system_management.discount;
/**
 * this class defines the discount policy in store
 */

import com.wsep202.TradingSystem.domain.exception.IllegalProductPriceException;
import com.wsep202.TradingSystem.domain.trading_system_management.Product;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

//the component in  the composite pattern

@Data
@Builder
@Slf4j
@NoArgsConstructor
public class Discount {

    protected static int discountIdAcc = 0;
    protected int discountId;

    /**
     * how much discount should to apply on product
     */
    private double discountPercentage;

    /**
     * the product validation date
     */
    private Calendar endTime;

    /**
     * products that has the specified discount
     */
    private Map<Product, Integer> productsUnderThisDiscount;

    /**
     * describes the condition and the post of the specified discount
     */
    private String description;

    /**
     * amount of product from to apply discount
     */
    private Map<Product, Integer> amountOfProductsForApplyDiscounts;

    /**
     * the minimal price of purchase to apply the discount from
     */
    private double minPrice;

    /**
     * children components of the composite conditional discount
     */
    private List<Discount> composedDiscounts;

    /**
     * the operation between the conditionals discounts
     */
    private CompositeOperator compositeOperator;

    private boolean isApplied;

    private boolean isStoreDiscount;

    private Map<String, DiscountPolicy> discountPolicies;

    public Discount(double discountPercentage,
                    Calendar endTime,
                    Map<Product, Integer> productsUnderThisDiscount,
                    String description,
                    Map<Product, Integer> amountOfProductsForApplyDiscounts,
                    double minPrice,
                    List<Discount> composedDiscounts,
                    CompositeOperator compositeOperator,
                    boolean isStoreDiscount) {
        this.discountPercentage = discountPercentage;
        this.endTime = endTime;
        this.productsUnderThisDiscount = productsUnderThisDiscount;
        this.description = description;
        this.amountOfProductsForApplyDiscounts = amountOfProductsForApplyDiscounts;
        this.minPrice = minPrice;
        this.composedDiscounts = composedDiscounts;
        this.compositeOperator = compositeOperator;
        this.discountId = getDiscountIdAcc();
        this.isStoreDiscount = isStoreDiscount;
        this.isApplied = false;
        initDiscountPolicies();
    }

    private void initDiscountPolicies() {
        discountPolicies = new HashMap<>();
        discountPolicies.put(ConditionalProductDiscount.class.getName(), new ConditionalProductDiscount());
        discountPolicies.put(ConditionalStoreDiscount.class.getName(), new ConditionalStoreDiscount());
        discountPolicies.put(VisibleDiscount.class.getName(), new VisibleDiscount());
        discountPolicies.put(ComposedDiscount.class.getName(), new ComposedDiscount());
    }

    /**
     * apply the relevant discount type on the products in store
     *
     * @param products in store
     */
    public void applyDiscount(Map<Product, Integer> products) {
        if (isComposed()) {
            discountPolicies.get(ComposedDiscount.class.getName()).applyDiscount(this, products);
        } else if (isConditional()) {
            applyConditionalDiscount(products);
        } else {// visible
            discountPolicies.get(VisibleDiscount.class.getName()).applyDiscount(this, products);
        }
    }

    public boolean isApprovedProducts(Map<Product, Integer> products) {
        if (isComposed()) {
            return discountPolicies.get(ComposedDiscount.class.getName()).isApprovedProducts(this, products);
        } else if (isConditional()) {
            return isApprovedConditionalDiscount(products);
        } else {// visible
            return discountPolicies.get(VisibleDiscount.class.getName()).isApprovedProducts(this, products);
        }
    }

    //////////////////////////////////////Conditional Discount ////////////////////////////////

    public void applyConditionalDiscount(Map<Product, Integer> products) {
        if (isStoreDiscount) {
            discountPolicies.get(ConditionalStoreDiscount.class.getName()).applyDiscount(this, products);
        } else {
            discountPolicies.get(ConditionalProductDiscount.class.getName()).applyDiscount(this, products);
        }
    }

    private boolean isApprovedConditionalDiscount(Map<Product, Integer> products) {
        if (isStoreDiscount) {
            return discountPolicies.get(ConditionalStoreDiscount.class.getName()).isApprovedProducts(this, products);
        } else {
            return discountPolicies.get(ConditionalProductDiscount.class.getName()).isApprovedProducts(this, products);
        }
    }

    ///////////////////////////////////////////////// edit /////////////////////////////////////

    public boolean editDiscount(double discountPercentage,
                                Calendar endTime,
                                Map<Product, Integer> productsUnderThisDiscount,
                                String description,
                                Map<Product, Integer> amountOfProductsForApplyDiscounts,
                                double minPrice,
                                List<Discount> composedDiscounts,
                                CompositeOperator compositeOperator,
                                boolean isStoreDiscount) {
        this.discountPercentage = discountPercentage;
        this.endTime = endTime;
        this.productsUnderThisDiscount = productsUnderThisDiscount;
        this.description = description;
        this.amountOfProductsForApplyDiscounts = amountOfProductsForApplyDiscounts;
        this.minPrice = minPrice;
        this.composedDiscounts = composedDiscounts;
        this.compositeOperator = compositeOperator;
        this.isStoreDiscount = isStoreDiscount;
        return true;
    }

    /////////////////////////////////////is-methods/////////////////////////////////////////
    private boolean isComposed() {
        return !composedDiscounts.isEmpty();
    }

    private boolean isConditional() {
        return minPrice > 0 || !productsUnderThisDiscount.isEmpty();
    }

    ////////////////////////////////////// general /////////////////////////////////////////

    @Synchronized
    protected int getDiscountIdAcc() {
        return discountIdAcc++;
    }

    public boolean isExpired() {
        return getEndTime().compareTo(Calendar.getInstance()) < 0;
    }
}

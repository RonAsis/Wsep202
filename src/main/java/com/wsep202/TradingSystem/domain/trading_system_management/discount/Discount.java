package com.wsep202.TradingSystem.domain.trading_system_management.discount;
/**
 * this class defines the discount policy in store
 */

import com.wsep202.TradingSystem.domain.exception.IllegalProductPriceException;
import com.wsep202.TradingSystem.domain.trading_system_management.Product;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import javax.validation.constraints.Min;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

//the component in  the composite pattern

@Data
@Builder
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Discount {

    /**
     * saves the last discountSnAcc when a new product is created
     */
    private static int discountSnAcc = 1;

    @Id
    @GeneratedValue
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
     * describes the condition and the post of the specified discount
     */
    private String description;

    private boolean isApplied;

    /**
     * types of discounts in the store
     */
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private DiscountPolicy discountPolicy;

    @Enumerated(EnumType.STRING)
    private DiscountType discountType;

    public Discount(double discountPercentage,
                    Calendar endTime,
                    String description,
                    DiscountPolicy discountPolicy,
                    DiscountType discountType) {
        this.discountPercentage = discountPercentage;
        this.endTime = endTime;
        this.description = description;
        this.isApplied = false;
        this.discountPolicy = discountPolicy;
        this.discountType = discountType;
    }

    /**
     * apply the relevant discount type on the products in store
     * @param products in store
     */
    public void applyDiscount(Map<Product, Integer> products) {
       discountPolicy.applyDiscount(this, products);
    }

    public boolean isApprovedProducts(Map<Product, Integer> products) {
       return discountPolicy.isApprovedProducts(this, products);
    }

    ///////////////////////////////////////////////// edit /////////////////////////////////////

    public boolean editDiscount(double discountPercentage,
                                Calendar endTime,
                                String description,
                                DiscountPolicy discountPolicy,
                                DiscountType discountType) {
        this.discountPercentage = discountPercentage;
        this.endTime = endTime;
        this.description = description;
        this.discountPolicy = discountPolicy;
        this.discountType = discountType;
        return true;
    }

    ////////////////////////////////////// general /////////////////////////////////////////

    public boolean isExpired() {
        return getEndTime().compareTo(Calendar.getInstance()) < 0;
    }

}

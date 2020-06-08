/**
 * Dto of general discount class that defines the interface
 * part of UC 4.2
 */
package com.wsep202.TradingSystem.dto;

import com.wsep202.TradingSystem.domain.trading_system_management.discount.CompositeOperator;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Slf4j
public class DiscountDto {

    private long discountId;
    /**
     * how much discount should to apply on product
     */
    private double discountPercentage;

    /**
     * the product validation date
     */
    private Date endTime;

    /**
     * products that has the specified discount
     * if none one, so its apply on all store
     */
    private List<ProductDto> productsUnderThisDiscount;

    /**
     * describes the condition and the post of the specified discount
     */
    protected String description;

    /**
     * amount of product from to apply discount
     */
    private List<ProductDto> amountOfProductsForApplyDiscounts;

    /**
     * the minimal price of purchase to apply the discount from
     */
    private double minPrice;

    /**
     * children components of the composite conditional discount
     */
    private List<DiscountDto> composedDiscounts;

    /**
     * the operation between the conditionals discounts
     */
    private String compositeOperator;

    private String discountType;
}

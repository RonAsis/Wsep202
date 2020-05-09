package com.wsep202.TradingSystem.dto;

import com.wsep202.TradingSystem.domain.trading_system_management.discount.CompositeOperator;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Calendar;
import java.util.HashMap;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Slf4j
public class ConditionalComposedDiscountEditDto {
    /**
     * logical operator to apply between discounts that constitutes as condition
     */
    private CompositeOperator operator;

    /**
     * The id of the discount to edit
     */
    private int id;
    /**
     * products to add to products under discount
     */
    protected HashMap<Integer, DiscountPolicyDto> composedToAdd;

    /**
     * products to delete from products under discount
     */
    protected HashMap<Integer, DiscountPolicyDto> composedToDelete;


    /**
     * products to add to amount to apply on discount
     */
    protected HashMap<Integer, DiscountPolicyDto> applyToAdd;

    /**
     * products to remove from amount to apply on discount
     */
    protected HashMap<Integer, DiscountPolicyDto> applyToDelete;

    /**
     * the product validation date
     */
    protected Calendar endTime;

    /**
     * how much discount should to apply on product
     */
    protected double discountPercentage;
}

package com.wsep202.TradingSystem.dto;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Calendar;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Slf4j
public class ConditionalStoreDiscountEditDto {
    /**
     * The id of the discount to edit
     */
    private int id;

    /**
     * the product validation date
     */
    protected Calendar endTime;

    /**
     * how much discount should to apply on product
     */
    protected double discountPercentage;

    private double minPrice;

    protected String description;
}

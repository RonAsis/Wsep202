package com.wsep202.TradingSystem.dto;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Calendar;
import java.util.Map;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Slf4j
public class ConditionalStoreDiscountDto {


    /**
     * the product validation date
     */
    protected Calendar endTime;

    /**
     * how much discount should to apply on product
     */
    protected double discountPercentage;

    /**
     * the verbal description of the discount
     */
    protected String description;

    /**
     * the threshold of purchase price to get a discount in the store level on the bag's cost
     */
    private double minPrice;
}

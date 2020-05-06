package com.wsep202.TradingSystem.dto;

import com.wsep202.TradingSystem.domain.trading_system_management.discount.CompositeOperator;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Slf4j
public class ConditionalComposedDto {
    /**
     * children components of the composite conditional discount
     */
    private Map<Integer, DiscountPolicyDto> composedDiscounts;

    /**
     * the discounts to apply on the received products ion case they are stands in conditions
     */
    private Map<Integer, DiscountPolicyDto> discountsToApply;

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
     * the operation between the conditionals discounts
     */
    private CompositeOperator compositeOperator;


}

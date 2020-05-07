package com.wsep202.TradingSystem.dto;

import com.wsep202.TradingSystem.domain.trading_system_management.Product;
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
public class ConditionalProductDiscountDto {
    /**
     * products that has the specified discount
     */
    protected Map<ProductDto, Integer> productsUnderThisDiscount;

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
     * map that tells on how many items of each product to apply the discount
     */
    private Map<ProductDto,Integer> amountOfProductsForApplyDiscounts;

}

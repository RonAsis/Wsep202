/**
 * Dto of visible discount class
 * * part of UC 4.2
 */
package com.wsep202.TradingSystem.dto;
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
public class VisibleDiscountDto {
    /**
     * products that has the specified discount
     */
    protected HashMap<ProductDto, Integer> productsUnderThisDiscount;

    /**
     * the product validation date
     */
    protected Calendar endTime;

    /**
     * how much discount should to apply on product
     */
    protected double discountPercentage;
}

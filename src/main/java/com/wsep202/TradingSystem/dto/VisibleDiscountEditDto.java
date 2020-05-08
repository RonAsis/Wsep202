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
public class VisibleDiscountEditDto {
    /**
     * The id of the discount to edit
     */
    private int id;
    /**
     * products to add to products under discount
     */
    protected HashMap<ProductDto, Integer> productsToAdd;

    /**
     * products to delete from products under discount
     */
    protected HashMap<ProductDto, Integer> productsToDelete;

    /**
     * the product validation date
     */
    protected Calendar endTime;

    /**
     * how much discount should to apply on product
     */
    protected double discountPercentage;
}

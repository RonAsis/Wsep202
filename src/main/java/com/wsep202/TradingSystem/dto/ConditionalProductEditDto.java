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
public class ConditionalProductEditDto {
    /**
     * the verbal description of the discount as inserted by the user
     */
    protected String description;
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
     * products to add to amount to apply on discount
     */
    protected HashMap<ProductDto, Integer> applyToAdd;

    /**
     * products to remove from amount to apply on discount
     */
    protected HashMap<ProductDto, Integer> applyToDelete;

    /**
     * the product validation date
     */
    protected Calendar endTime;

    /**
     * how much discount should to apply on product
     */
    protected double discountPercentage;
}

/**
 * Dto of general discount class that defines the interface
 * part of UC 4.2
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
public class DiscountPolicyDto {
///////////////////////generic fields///////////////////////////////////////////////
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

///////////////////////////conditionals////////////////////////////////////////////////
    /**
     * describes the condition and the post of the specified discount
     */
    protected String conditionDescription;
//////////////////////////////////////////////////////////////////////////////////////
///////////////////////conditional product fields/////////////////////////////////////
    /**
     * The table that describes what is the amount of items from each product to apply the discount on.
     */
    private HashMap<ProductDto,Integer> amountOfProductsForApplyDiscounts;


////////////////////////////////////////////////////////////////////////////////////////
///////////////////////conditional store fields/////////////////////////////////////////
    /**
     * the minimal price of purchase to apply the discount from
     */
    private double minPrice;




}

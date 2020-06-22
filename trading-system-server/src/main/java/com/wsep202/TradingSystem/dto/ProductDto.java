package com.wsep202.TradingSystem.dto;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Slf4j
public class ProductDto {

    /**
     * the product serial number
     */
    private int productSn;

    /**
     * the name of the product
     */
    private String name;

    /**
     * the category of the product
     */
    private String category;

    /**
     * the amount of this product in the store (=>storeId)
     */
    private int amount;

    /**
     * the cost of this product
     */
    private double cost;

    /**
     * the original cost before any discount
     */
    private double originalCost;


    /**
     * the rank of this product
     */
    private int rank;

    /**
     * the storeId that connected to the store that the product exists in it.
     */
    private int storeId;
}

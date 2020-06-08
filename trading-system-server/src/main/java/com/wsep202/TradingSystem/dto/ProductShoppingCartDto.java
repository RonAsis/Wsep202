package com.wsep202.TradingSystem.dto;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Slf4j
public class ProductShoppingCartDto {

    /**
     * the product serial number
     */
    private int productSn;

    /**
     * the name of the product
     */
    private String name;

    /**
     * the amount of this product in the shoppingCart
     */
    private int amountInShoppingCart;

    /**
     * the cost of this product
     */
    private double cost;

    /**
     * the original cost before any discount
     */
    private double originalCost;

    /**
     * the storeId that connected to the store that the product exists in it.
     */
    private int storeId;

}

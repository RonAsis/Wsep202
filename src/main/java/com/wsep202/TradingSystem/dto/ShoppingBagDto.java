package com.wsep202.TradingSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShoppingBagDto {

    /**
     * list of all of the products and the amount of each product
     */
    private Map<ProductDto, Integer> productListFromStore;
}

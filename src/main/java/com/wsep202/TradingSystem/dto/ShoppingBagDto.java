package com.wsep202.TradingSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShoppingBagDto {

    /**
     * list of all of the products sn and the amount of each product
     */
    private Map<Integer, Integer> productListFromStore;
}

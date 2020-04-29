package com.wsep202.TradingSystem.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShoppingCartDto {


    /**
     * list of stores id and there shopping bags
     */
    private Map<Integer, ShoppingBagDto> shoppingBags;


}

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
     * list of stores and there shopping bags
     */
    private Map<StoreDto, ShoppingBagDto> shoppingBagsList;
    /**
     * the total cost of the products in cart
     */
    private double totalCartCost;
    /**
     * number of different shopping bags in cart
     */
    private int numOfBagsInCart;
    /**
     * number of different products in the cart
     */
    private int numOfProductsInCart;

}

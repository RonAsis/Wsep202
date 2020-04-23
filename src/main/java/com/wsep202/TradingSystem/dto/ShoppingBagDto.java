package com.wsep202.TradingSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShoppingBagDto {

    //private Map<Integer, Integer> mapProductSnToAmount;

    /**
     * the store of the products
     */
    private StoreDto storeOfProduct;
    /**
     * list of all of the products and the amount of each product
     */
    private Map<ProductDto, Integer> productListFromStore;
    /**
     * shows the cost of all of the products in bag
     */
    private double totalCostOfBag;
    /**
     * shows how many types of products in the bag
     */
    private int numOfProductsInBag;

}

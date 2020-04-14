package com.wsep202.TradingSystem.service.user_service.dto;

import com.wsep202.TradingSystem.domain.trading_system_management.Product;
import com.wsep202.TradingSystem.domain.trading_system_management.Store;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShoppingBagDto {

    //private Map<Integer, Integer> mapProductSnToAmount;

    private Store storeOfProduct;

    private Map<Product, Integer> productListFromStore;

    private double totalCostOfBag;

    private int numOfProductsInBag;

}

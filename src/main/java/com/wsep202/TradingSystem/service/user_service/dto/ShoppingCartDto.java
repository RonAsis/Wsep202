package com.wsep202.TradingSystem.service.user_service.dto;

import com.wsep202.TradingSystem.domain.trading_system_management.ShoppingBag;
import com.wsep202.TradingSystem.domain.trading_system_management.Store;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShoppingCartDto {

    private Map<Store, ShoppingBag> shoppingBagsList;

    private double totalCartCost;

    private int numOfBagsInCart;

    private int numOfProductsInCart;

}

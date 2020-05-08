package com.wsep202.TradingSystem.dto;

import com.wsep202.TradingSystem.domain.exception.NotInStockException;
import com.wsep202.TradingSystem.domain.trading_system_management.Product;
import com.wsep202.TradingSystem.domain.trading_system_management.Receipt;
import com.wsep202.TradingSystem.domain.trading_system_management.ShoppingBag;
import com.wsep202.TradingSystem.domain.trading_system_management.Store;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Builder
@AllArgsConstructor
@Data
@NoArgsConstructor
public class ShoppingCartViewDto {

    /**
     * list of stores and there shopping bags
     */
    private Map<Integer, ShoppingBagViewDto> shoppingBags;
}

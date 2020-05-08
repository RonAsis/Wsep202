package com.wsep202.TradingSystem.dto;

import com.wsep202.TradingSystem.domain.trading_system_management.Product;
import com.wsep202.TradingSystem.domain.trading_system_management.Store;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

import static com.wsep202.TradingSystem.domain.trading_system_management.purchase.Formatter.formatter;


@Data
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShoppingBagViewDto {

    private Map<Product, Integer> productListFromStore;
}

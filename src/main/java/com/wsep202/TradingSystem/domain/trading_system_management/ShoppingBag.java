package com.wsep202.TradingSystem.domain.trading_system_management;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShoppingBag {

    private Map<Integer, Integer> mapProductSnToAmount = new HashMap<>();
}

package com.wsep202.TradingSystem.domain.trading_system_management.purchase;

import com.wsep202.TradingSystem.domain.trading_system_management.BillingAddress;
import com.wsep202.TradingSystem.domain.trading_system_management.Product;
import com.wsep202.TradingSystem.domain.trading_system_management.UserSystem;

import java.util.Map;

public class ShoppingbagDetailsPolicy extends PurchasePolicy {
    @Override
    public boolean isApproved(Map<Product, Integer> products, UserSystem user, BillingAddress userAddress) {

    }
}

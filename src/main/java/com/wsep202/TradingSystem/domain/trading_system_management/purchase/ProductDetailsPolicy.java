package com.wsep202.TradingSystem.domain.trading_system_management.purchase;
import com.wsep202.TradingSystem.domain.trading_system_management.BillingAddress;
import com.wsep202.TradingSystem.domain.trading_system_management.Product;
import com.wsep202.TradingSystem.domain.trading_system_management.UserSystem;

import java.util.Map;

public class ProductDetailsPolicy extends PurchasePolicy {
    private int productId;  //the SN of product which has limitations amounts
    private int min=0;
    
    @Override
    public boolean isApproved(Map<Product, Integer> products, UserSystem user, BillingAddress userAddress) {
return true;
    }
}

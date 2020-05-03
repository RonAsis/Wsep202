package com.wsep202.TradingSystem.domain.trading_system_management.discount;

import com.wsep202.TradingSystem.domain.trading_system_management.Product;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class HiddenDiscount extends DiscountPolicy{


    @Override
    public void applyDiscount(HashMap<Product, Integer> products) {

    }

    @Override
    public boolean isApprovedProducts(HashMap<Product, Integer> products) {
        return false;
    }

    @Override
    public void editProductByDiscount(Product product) {

    }
}

package com.wsep202.TradingSystem.domain.trading_system_management.discount;

import com.wsep202.TradingSystem.domain.trading_system_management.Product;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
@Setter
@Getter
@AllArgsConstructor
@Slf4j
@Builder
public class HiddenDiscount extends DiscountPolicy{


    @Override
    public void applyDiscount(Map<Product, Integer> products) {

    }

    @Override
    public boolean isApprovedProducts(Map<Product, Integer> products) {
        return false;
    }

    @Override
    public void undoDiscount(Map<Product, Integer> products) {

    }

    @Override
    public void editProductByDiscount(Product product) {

    }
}

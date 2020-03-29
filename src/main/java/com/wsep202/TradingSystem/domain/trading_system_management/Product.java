package com.wsep202.TradingSystem.domain.trading_system_management;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@AllArgsConstructor
@Builder
@Slf4j
public class Product {

    @NotBlank(message = "product name must not be empty or null")
    private String name;

    @NotNull(message = "Must be category")
    private ProductCategory category;

    @Min(value = 0, message = "Must be greater than or equal zero")
    @Builder.Default
    private int amount = 0;

    @Min(value = 0, message = "Must be greater than or equal zero")
    @Builder.Default
    private double cost = 0;

    /**
     * Increases the amount of products
     */
    public void increasesProduct(){
        amount++;
    }

    /**
     * Reduces the amount of products
     */
    public boolean reducesProduct(){
        boolean canReduce = false;
        if(amount > 0){
            amount --;
            canReduce = true;
        }
        return canReduce;
    }
}

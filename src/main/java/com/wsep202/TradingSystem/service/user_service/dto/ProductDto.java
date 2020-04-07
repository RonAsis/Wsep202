package com.wsep202.TradingSystem.service.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {

    private int productSn;

    private String name;

    private String category;

    private int amount;

    private double cost;

    private int rank;

    private int storeId;
}

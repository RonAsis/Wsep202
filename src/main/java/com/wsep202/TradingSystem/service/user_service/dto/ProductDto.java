package com.wsep202.TradingSystem.service.user_service.dto;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Slf4j
public class ProductDto {

    private int productSn;

    private String name;

    private String category;

    private int amount;

    private double cost;

    private int rank;

    private int storeId;
}

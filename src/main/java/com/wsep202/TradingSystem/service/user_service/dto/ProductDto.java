package com.wsep202.TradingSystem.service.user_service.dto;

import com.wsep202.TradingSystem.domain.trading_system_management.DiscountType;
import com.wsep202.TradingSystem.domain.trading_system_management.PurchaseType;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Slf4j
public class ProductDto {

    /**
     * the product serial number
     */
    private int productSn;

    /**
     * the name of the product
     */
    private String name;

    /**
     * the category of the product
     */
    private String category;

    /**
     * the amount of this product in the store (=>storeId)
     */
    private int amount;

    /**
     * the cost of this product
     */
    private double cost;

    /**
     * the rank of this product
     */
    private int rank;

    /**
     * the storeId that connected to the store that the product exists in it.
     */
    private int storeId;

    /**
     * the type of discount with needs to be apply on the product when discount
     * and watch it's info.
     */
    private DiscountType discountType = DiscountType.NONE;

    /**
     * the type of purchase with needs to be apply on the product when purchasing
     * and watch it's info.
     */
    private PurchaseType purchaseType = PurchaseType.BUY_IMMEDIATELY;
}

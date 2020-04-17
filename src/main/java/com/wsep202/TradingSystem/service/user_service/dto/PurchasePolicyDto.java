package com.wsep202.TradingSystem.service.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchasePolicyDto {
    /**
     * allow everyone to purchase from store
     */
    private boolean isAllAllowed = true;

    /**
     * status as string
     */
    private String whoCanBuyStatus = "allow all purchases";


}

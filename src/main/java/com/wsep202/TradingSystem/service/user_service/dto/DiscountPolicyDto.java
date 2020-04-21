package com.wsep202.TradingSystem.service.user_service.dto;

import com.wsep202.TradingSystem.domain.trading_system_management.DiscountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiscountPolicyDto {

    /**
     * allow everyone to purchase from store
     */
    private boolean isAllAllowed = true;

    /**
     * status as string
     */
    private String whoCanBuyStatus = "allow all discounts";

    /**
     * list that contains the allowed discounts in this policy
     */
    private List<DiscountType> listOfDiscountTypes;
}

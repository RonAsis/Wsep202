package com.wsep202.TradingSystem.service.user_service.dto;

import com.wsep202.TradingSystem.domain.trading_system_management.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StoreDto {

    private int storeId;

    private String storeName;

    private  Set<ProductDto> products;

    private PurchasePolicyDto purchasePolicy;

    private DiscountPolicyDto discountPolicy;

    private String discountType;

    private String purchaseType;

    private Set<UserSystemDto> owners ;

    private List<ReceiptDto> receipts;

    private int rank;
}

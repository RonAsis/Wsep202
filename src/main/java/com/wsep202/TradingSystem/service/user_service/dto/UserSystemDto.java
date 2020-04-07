package com.wsep202.TradingSystem.service.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSystemDto {

    private String userName;

    private String firstName;

    private String lastName;

    private Set<StoreDto> managedStores;

    private Set<StoreDto> ownedStores;

    private ShoppingCartDto shoppingCart;

    private List<ReceiptDto> receipts;
}

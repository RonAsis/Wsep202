package com.wsep202.TradingSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PurchaseDto {

    private ShoppingCartDto shoppingCartDto;

    private PaymentDetailsDto paymentDetailsDto;

    private BillingAddressDto billingAddressDto;
}

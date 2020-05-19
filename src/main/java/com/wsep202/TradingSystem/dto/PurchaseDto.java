package com.wsep202.TradingSystem.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.wsep202.TradingSystem.config.deserializers.ShoppingCartDtoDeserializer;
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

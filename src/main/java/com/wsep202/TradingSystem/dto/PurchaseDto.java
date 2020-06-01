package com.wsep202.TradingSystem.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.wsep202.TradingSystem.config.deserializers.ShoppingCartDtoDeserializer;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PurchaseDto {

    private ShoppingCartDto shoppingCartDto;

    private PaymentDetailsDto paymentDetailsDto;

    private BillingAddressDto billingAddressDto;
}

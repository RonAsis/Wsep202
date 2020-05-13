package com.wsep202.TradingSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PurchaseRegisterBuyerDto {

    private PaymentDetailsDto paymentDetailsDto;

    private BillingAddressDto billingAddressDto;
}

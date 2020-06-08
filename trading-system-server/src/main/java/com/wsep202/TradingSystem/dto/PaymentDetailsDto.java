package com.wsep202.TradingSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor

public class PaymentDetailsDto {
    /**
     * the number of the card
     */
    private String creditCardNumber;
    /**
     * ccv of the, the digits on the back of the card
     */
    private int ccv;
    /**
     * the id of the credit card holder
     */
    private String holderIDNumber;
}

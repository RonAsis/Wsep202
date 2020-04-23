package com.wsep202.TradingSystem.dto;

import com.wsep202.TradingSystem.domain.trading_system_management.CardAction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor

public class PaymentDetailsDto {

    /**
     * the action the credit card was used for
     */
    private CardAction creditAction;
    /**
     * the number of the card
     */
    private String creditCardNumber;
    /**
     * the month of the credit card
     */
    private String month;
    /**
     * the year of the year
     */
    private String year;

    /**
     * the name of the credit card holder
     */
    private String Cardholder;

    /**
     * ccv of the, the digits on the back of the card
     */
    private int ccv;
    /**
     * the id of the credit card holder
     */
    private String holderIDNumber;
}

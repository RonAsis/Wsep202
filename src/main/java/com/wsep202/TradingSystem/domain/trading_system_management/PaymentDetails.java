/**
 * this class represents all necessary payment datails of the customer for
 * purchasing the shopping items
 */
package com.wsep202.TradingSystem.domain.trading_system_management;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Slf4j
public class PaymentDetails {
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

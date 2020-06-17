/**
 * this class represents all necessary payment datails of the customer for
 * purchasing the shopping items
 */
package com.wsep202.TradingSystem.domain.trading_system_management.purchase;

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
     * the number of the card
     */
    private String creditCardNumber;
    /**
     * ccv of the, the digits on the back of the card
     */
    private String ccv;
    /**
     * the id of the credit card holder
     */
    private String holderIDNumber;
    /**
     * valid thru details
     */
    private String month;
    private String year;
    /**
     * name of the credit card holder
     */
    private String holderName;


}

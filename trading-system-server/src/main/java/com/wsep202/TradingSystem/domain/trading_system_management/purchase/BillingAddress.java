/**
 * this class represents the address information of a user in the system
 */

package com.wsep202.TradingSystem.domain.trading_system_management.purchase;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Slf4j

public class BillingAddress {
    /**
     * the name of customer
     */
    private String customerFullName;
    /**
     * the street name
     */
    private String address;
    /**
     * the users city for the delivery
     */
    private String city;
    /**
     * the users country for the delivery
     */
    private String country;
    /**
     * the users zip code for the delivery
     */
    private String zipCode;
}

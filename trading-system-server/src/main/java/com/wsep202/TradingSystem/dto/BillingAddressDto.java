package com.wsep202.TradingSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BillingAddressDto {

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

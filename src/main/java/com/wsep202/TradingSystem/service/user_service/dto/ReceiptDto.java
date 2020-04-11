package com.wsep202.TradingSystem.service.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
public class ReceiptDto {

    /**
     * the storeId that connected to the store in which the purchase happened.
     */
    private int storeId;

    /**
     * the user name who perform this purchase or a Guest
     */
    @NotBlank
    private String userName;

    /**
     * the purchase date
     */
    private Date purchaseDate;

    /**
     * the price that the user had to pay in this purchase.
     */
    @Min(value = 0, message = "Must be greater than or equal zero")
    private int amountToPay;

    /**
     * a list of all the products that the user bought in this purchase.
     */
    @Builder.Default
    private Set<ProductDto> productsBought = new HashSet<>();;
}

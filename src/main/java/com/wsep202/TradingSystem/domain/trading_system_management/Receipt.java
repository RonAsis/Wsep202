package com.wsep202.TradingSystem.domain.trading_system_management;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.*;

@Builder
@AllArgsConstructor
@Data
@NoArgsConstructor
public class Receipt {

    private static int receiptSnAcc = 0;

    private int receiptSn;

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
    private double amountToPay;

    /**
     * a list of all the products that the user bought in this purchase.
     */
    @Builder.Default
    private Map<Product, Integer> productsBought = new HashMap<>();

    /**
     * Receipt Constructor
     * @param storeId - the storeId that the purchase happened in it.
     * @param userName - the user who performed this purchase.
     * @param amountToPay - the price that the user paid.
     * @param products - the products that the buyer bought.
     */
    public Receipt(int storeId, String userName, double amountToPay, Map<Product, Integer> products){
        purchaseDate = new Date(); // sets the current date
        this.receiptSn = getReceiptIdAcc();
        this.storeId = storeId;
        this.userName = userName;
        this.amountToPay = amountToPay;
        this.productsBought = products;
    }

    /**
     * get and accumilate the receipt id accumulator
     * @return the receipt serial number
     */
    private int getReceiptIdAcc(){
        return receiptSnAcc++;
    }
}

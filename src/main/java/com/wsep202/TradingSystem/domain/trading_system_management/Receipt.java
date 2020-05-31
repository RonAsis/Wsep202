package com.wsep202.TradingSystem.domain.trading_system_management;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Slf4j
@Entity
public class Receipt {

    /**
     * saves the last receiptSnAcc when a new receipt is created
     */
    private static int receiptSnAcc = 1;

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Min(value = 1, message = "Must be greater than or equal zero")
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
    @DateTimeFormat
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
    @ElementCollection
    @JoinTable()
    private Map<Product, Integer> productsBought = new HashMap<>();

    /**
     * Receipt Constructor
     * @param storeId - the storeId that the purchase happened in it.
     * @param userName - the user who performed this purchase.
     * @param amountToPay - the price that the user paid.
     * @param products - the products that the buyer bought.
     */
    public Receipt(int storeId, String userName, double amountToPay, Map<Product, Integer> products){
        receiptSn = generateReceiptSn();
        purchaseDate = new Date(); // sets the current date
        this.storeId = storeId;
        this.userName = userName;
        this.amountToPay = amountToPay;
        this.productsBought = products;
    }

    private int generateReceiptSn(){
        return receiptSnAcc++;
    }
}

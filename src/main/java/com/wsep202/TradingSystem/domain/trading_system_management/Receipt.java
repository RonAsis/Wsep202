package com.wsep202.TradingSystem.domain.trading_system_management;

import com.sun.jmx.mbeanserver.NamedObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Builder
@AllArgsConstructor
@Data
public class Receipt {

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
    private Set<Product> productsBought = new HashSet<>();

    /**
     * Receipt Constructor
     * @param storeId - the storeId that the purchase happened in it.
     * @param userName - the user who performed this purchase.
     * @param amountToPay - the price that the user paid.
     * @param products - the products that the buyer bought.
     */
    public Receipt(int storeId, String userName, int amountToPay, Set<Product> products){
        purchaseDate = new Date(); // sets the current date
        this.storeId = storeId;
        this.userName = userName;
        this.amountToPay = amountToPay;
        this.productsBought = products;
    }
}

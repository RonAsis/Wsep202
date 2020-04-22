package com.wsep202.TradingSystem.service.user_service.dto;

import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReceiptDto {

    /**
     * the receipt serial number
     */
    private int receiptSn;

    /**
     * the storeId that connected to the store in which the purchase happened.
     */
    private int storeId;

    /**
     * the user name who perform this purchase or a Guest
     */
    private String userName;

    /**
     * the purchase date
     */
    private Date purchaseDate;

    /**
     * the price that the user had to pay in this purchase.
     */
    private double amountToPay;

    /**
     * a list of all the products that the user bought in this purchase.
     */
    private Map<ProductDto,Integer> productsBought = new HashMap<>();

    public Integer getProductBoughtAmountByProductSn(int productSn){
        for (ProductDto productDto: productsBought.keySet()){
            if (productDto.getProductSn() == productSn){
                return productsBought.get(productDto);
            }
        }
        return null;
    }
}

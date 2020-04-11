package com.wsep202.TradingSystem.domain.trading_system_management;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Slf4j
public class Product {

    /**
     * the product serial number
     */
    @Min(value = 0, message = "Must be greater than or equal zero")
    private int productSn=-1;

    //private static int productSnAcc = 0;

    /**
     * the name of the product
     */
    @NotBlank(message = "product name must not be empty or null")
    private String name;

    /**
     * the category of the product
     */
    @NotNull(message = "Must be category")
    private ProductCategory category;

    /**
     * the amount of this product in the store (=>storeId)
     */
    @Min(value = 0, message = "Must be greater than or equal zero")
    @Builder.Default
    private int amount = 0;

    /**
     * the cost of this product
     */
    @Min(value = 0, message = "Must be greater than or equal zero")
    @Builder.Default
    private double cost = 0;

    /**
     * the rank of this product
     */
    @Min(value = 0, message = "Must be greater than or equal zero")
    @Max(value = 5, message = "Must be smaller than or equal 5")
    @Builder.Default
    private int rank = 0;

    /**
     * the storeId that connected to the store that the product exists in it.
     */
    private int storeId;

    /**
     * Product Constructor
     * @param name - product name.
     * @param category - product category.
     * @param amount - product amount.
     * @param cost - product cost.
     * @param storeId - the storeId to which the product is linked
     */
    public Product(String name, ProductCategory category, int amount, double cost, int storeId){
        this.productSn = generateProductSn();
        this.name = name;
        this.category = category;
        this.amount = amount;
        this.cost = cost;
        this.rank = 0;
        this.storeId = storeId;
    }

    /**
     * Generates a new productSn (to ensure productSn is unique).
     * @return - the new produceSn.
     */
    private int generateProductSn(){
        productSn += 1;
        return productSn;
    }

    /**
     * Increases the amount of products with newAmount.
     * @param addedAmount - the amount to add to the original amount
     *                  (must be greater then zero).
     * @return - true if succeeded, else returns false.
     */
    public boolean increasesProductAmount(int addedAmount){
        //amount++;
        boolean canIncrease = false;
        if (addedAmount > 0) { // can't increase with negative or zero newAmount
            amount += addedAmount;
            canIncrease = true;
        }
        return canIncrease;
    }

    /**
     * Reduces the amount of products with removalAmount.
     * @param removalAmount - the amount to reduce from the original amount
     *                  (must be greater then zero).
     * @return - true if succeeded, else returns false.
     */
    public boolean reducesProductAmount(int removalAmount){
        boolean canReduce = false;
        if(removalAmount > 0){ // can't reduce with negative or zero newAmount
            amount -= removalAmount;
            canReduce = true;
        }
        return canReduce;
    }
}

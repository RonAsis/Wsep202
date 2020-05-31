package com.wsep202.TradingSystem.domain.trading_system_management;

import com.google.common.base.Strings;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Slf4j
@Entity
public class Product {

    /**
     * saves the last productSnAcc when a new product is created
     */
    private static int productSnAcc = 1;

    /**
     * the product serial number
     */
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Min(value = 1, message = "Must be greater than or equal zero")
    private int productSn;

    /**
     * the name of the product
     */
    @NotBlank(message = "product name must not be empty or null")
    private String name;

    /**
     * the category of the product
     */
    @NotNull(message = "Must be category")
    @Enumerated(EnumType.STRING)
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
     * the original price of this product before discount if applied one
     */
    @Min(value = 0, message = "Must be greater than or equal zero")
    private double originalCost = 0;

    /**
     * the rank of this product
     */
    @Min(value = 1, message = "Must be greater than or equal zero")
    @Max(value = 9, message = "Must be smaller than or equal 5")
    @Builder.Default
    private int rank = 5;

    /**
     * the storeId that connected to the store that the product exists in it.
     */
    private int storeId;

    public Product(String name, ProductCategory category, int amount, double cost, int storeId){
        this.productSn = generateProductSn();
        this.name = name;
        this.category = category;
        this.amount = amount;
        this.cost = cost;
        this.originalCost = cost;
        this.rank = 5;
        this.storeId = storeId;
    }

    /**
     * Generates a new productSn (to ensure productSn is unique).
     * @return - the new produceSn.
     */
    private int generateProductSn(){
        return productSnAcc++;
    }

    /**
     * Increases the amount of products with newAmount.
     * @param addedAmount - the amount to add to the original amount
     *                  (must be greater then zero).
     * @return - true if succeeded, else returns false.
     */
    public boolean increasesProductAmount(int addedAmount){
        boolean canIncrease = false;
        if (addedAmount > 0) { // can't increase with negative or zero newAmount
            log.info("The amount in product: " + productSn + " raised from: " + amount + " to: "+ amount+addedAmount + ".");
            amount += addedAmount;
            canIncrease = true;
        }
        log.error("The function 'increasesProductAmount(int addedAmount)' can't accept negative/zero amount!");
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
            log.info("The amount in product: " + productSn + " raised from: " + amount + " to: "+ amount+removalAmount + ".");
            amount -= removalAmount;
            canReduce = true;
        }
        log.error("The function 'increasesProductAmount(int addedAmount)' can't accept negative/zero amount!");
        return canReduce;
    }

    /**
     * Returns a presentation of a product.
     * @return - a representation of a product.
     */
    public String showProductInfo() {
        return "Product serial number: " + productSn +
                "\nProduct name: " + name +
                "\nProduct amount: " + amount +
                "\nProduct cost: " + cost +
                "\nProduct rank: " + rank +
                "\nProduct store id: " + storeId;
    }

    /**
     * check if the current product name contains the given key words
     * @param keyWords - the key words to check for
     * @return - the current product.
     */
    public Product productNameThatContainsKeyWords(List<String> keyWords){
        boolean contains = true;
        for(String keyWord : keyWords)
            if (!name.contains(keyWord))
                contains = false;
        if(!contains)
            return null;
        return this;
    }

    /**
     * check if all product fields are valid
     * @return true if valid fields
     */
    public boolean isValidProduct() {
        return !Strings.isNullOrEmpty(this.name) &&
                !Strings.isNullOrEmpty(this.category.category);
    }

    public Product cloneProduct(){
        return new Product(name,category,amount,cost,storeId);
    }
}

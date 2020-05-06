package com.wsep202.TradingSystem.domain.trading_system_management.discount;
/**
 * this class defines the discount policy in store
 */

import com.wsep202.TradingSystem.domain.trading_system_management.Product;
import javafx.util.Pair;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.Synchronized;

import java.util.*;

//the component in  the composite pattern

@Getter
@Setter
public abstract class DiscountPolicy {

    //holds the conditioned products with pair of their required amount and quantity to apply discount
    protected HashMap<Product, Integer> productsUnderThisDiscount;
    protected static int discountIdAcc = 0;
    protected int id;
    protected boolean isExpired = false;
    protected boolean isApplied =false;
    protected boolean isUndone = false; //TODO if undone already then remove discount completely
    protected Calendar endTime; //expiration date of the discount
    protected double discountPercentage;    //discount percentage for product
    /**
     * apply the relevant discount type on the products in store
     * @param products in store
     */
    public abstract void applyDiscount(HashMap<Product,Integer> products);

    /**
     * add this discount type for the products
     * @param products
     * @return true for success
     */
    public boolean addProductToThisDiscount(HashMap<Product,Integer> products){
        if(products==null){
            //invalid null value product inserted or missing field
            return false;
        }
        //verify all products fields are valid
        for(Product product :products.keySet()){
            if(!product.isValidProduct())
                return false;
        }
        this.productsUnderThisDiscount.putAll(products); //add list of products to this discount
        return true;
    }

    /**
     * checks if the products stands in the discount condition
     * @param products
     * @return true if the discount applies on the products
     */
    public abstract boolean isApprovedProducts(HashMap<Product,Integer> products);

    /**
     * this method will be called in case the discount expired and has to be undone
     * @param products to update
     */
    public abstract void undoDiscount(HashMap<Product,Integer> products);


    @Synchronized
    protected int getdiscountIdAcc(){
        return discountIdAcc++;
    }


    /**
     * edit specific product by discount
     * @param product
     */
    public abstract void editProductByDiscount(Product product);


}

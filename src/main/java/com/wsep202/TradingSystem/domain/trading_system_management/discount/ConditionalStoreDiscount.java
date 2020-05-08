/**
 * this class represents a conditional discount to apply at the store level.
 * apply the discount on the sum of the purchase cost.
 */
package com.wsep202.TradingSystem.domain.trading_system_management.discount;
import com.wsep202.TradingSystem.domain.exception.IllegalProductPriceException;
import com.wsep202.TradingSystem.domain.exception.NotValidEndTime;
import com.wsep202.TradingSystem.domain.trading_system_management.Product;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

@Setter
@Getter

@Slf4j
public class ConditionalStoreDiscount extends ConditionalDiscount {
    public ConditionalStoreDiscount(double minPrice, Calendar endTime, double discountPercentage, String description) {
        super(endTime, discountPercentage, description);
        this.minPrice = minPrice;
    }
    //the minimal price of purchase to apply the discount from
    private double minPrice;
    /**
     * in case the condition is approved update products prices
     * @param products in store
     */
    @Override
    public void applyDiscount(Map<Product, Integer> products) {

        //The discount time is not expired yet
        if(this.endTime.compareTo(Calendar.getInstance()) >= 0 && !isDeleted){
            if(!isApplied) {
                applyConditionalStoreDiscount(products);
                isApplied = true;   //discount already performed
            }
        }else{  //check if needs to update back the price
            undoStoreDiscount(products);
        }
    }

    /**
     * delegates the internal undo store discount on products
     * @param products to update
     */
    @Override
    public void undoDiscount(Map<Product, Integer> products) {
        undoStoreDiscount(products);
    }

    /**
     * edit the discount with received parameters
     * @param endTime
     * @param percentage
     * @param minPrice
     */
    public void editDiscount(Calendar endTime, double percentage, double minPrice, String description) {
        if(endTime!=null){
            if(endTime.compareTo(Calendar.getInstance())<0){
                //the end time passed so not valid
                throw new NotValidEndTime(endTime);
            }
            //end time is valid
            this.endTime = endTime;
        }
        if(percentage>=0){
            this.discountPercentage = percentage;
        }

        if(minPrice >= 0){
            this.minPrice= minPrice;
        }
        if(description!=null){
            this.conditionDescription = description;
        }
    }


    /**
     * undo visible discount
     * @param products to update the related undo from the discount between them
     */
    private void undoStoreDiscount(Map<Product,Integer> products) {
        if(isApplied) {
            for (Product product : products.keySet()) {
                double discount = calculateDiscount(product.getOriginalCost());
                product.setCost(product.getCost() + discount);    //update price
            }
        }
    }
    /**
     * update price by visible discount
     * @param products to update the related to the discount between them
     */
    private void applyConditionalStoreDiscount(Map<Product, Integer> products) {
        double totalPurchasedCost = getTotalPurchasedCost(products);
        if(this.minPrice <= totalPurchasedCost){
            for(Product product: products.keySet()){
                double discount = calculateDiscount(product.getOriginalCost());
                if(product.getCost()-discount<0){
                    throw new IllegalProductPriceException(id);
                }
                product.setCost(product.getCost()-discount);    //update price
            }
        }
    }
    /**
     * calculate the cost of the sum of products received
     * @param products to calculate their sum price
     * @return
     */
    private double getTotalPurchasedCost(Map<Product, Integer> products) {
        double totalPurchasedCost = 0;
        for(Product product: products.keySet()){
            totalPurchasedCost+=product.getCost()*products.get(product);
        }
        return totalPurchasedCost;
    }
    /**
     * should get the discount?
     * @param products
     * @return
     */
    public boolean isApprovedProducts(Map<Product,Integer> products){
        if(this.endTime.compareTo(Calendar.getInstance())>=0) {
            return this.minPrice <= getTotalPurchasedCost(products);
        }
        return false;
    }
    /**
     * calculate discount on product price
     * @param price
     * @return
     */
    private double calculateDiscount(double price) {
        return (discountPercentage*price)/100;
    }


}

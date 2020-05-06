/**
 * this class represents a simple discount of conditional type discount
 */
package com.wsep202.TradingSystem.domain.trading_system_management.discount;

import com.wsep202.TradingSystem.domain.exception.IllegalProductPriceException;
import com.wsep202.TradingSystem.domain.trading_system_management.Product;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
@Slf4j
@Builder
public class ConditionalProductDiscount extends ConditionalDiscount {
    //this map tells us on how many products to apply the discount
    private Map<Product,Integer> amountOfProductsForApplyDiscounts;

    public ConditionalProductDiscount(Map<Product,Integer> productsUnderDiscount,
                                      Calendar endTime, double discountPercentage, String description,
                                      Map<Product,Integer> amountOfProductsForApplyDiscounts) {
        super(endTime, discountPercentage, description);
        this.productsUnderThisDiscount = productsUnderDiscount;
        this.amountOfProductsForApplyDiscounts = amountOfProductsForApplyDiscounts;
    }

    /**
     * checks weather the products stands in the condition or not
     * @param product to check
     * @param requiredAmount to check
     * @return  true if the products stands in terms for discount
     */

    public boolean isApprovedCondition(Product product, int requiredAmount) {
        Product productInStore = isProductInDiscountStruct(product);
        if(productInStore==null)
            return false;
        return this.productsUnderThisDiscount.get(productInStore) < requiredAmount;
    }

    /**
     * in case the condition is approved update products prices
     * @param products in store
     */
    @Override
    public void applyDiscount(Map<Product, Integer> products) {
        //The discount time is not expired yet
        if(this.endTime.compareTo(Calendar.getInstance()) >= 0){
            if(!isApplied) {
                applyConditionalProductDiscount(products);
                isApplied = true;   //discount already performed
            }
        }else{  //check if needs to update back the price
            undoConditionalDiscount(products);
        }
    }

    /**
     * delegates the product to be undo by the internal undo method
     * @param products to update
     */
    @Override
    public void undoDiscount(Map<Product, Integer> products) {
        undoConditionalDiscount(products);
    }

    /**
     * undo visible discount
     * @param products to update the related undo from the discount between them
     */
    private void undoConditionalDiscount(Map<Product,Integer> products) {
        for(Product product: products.keySet()){
            //verify that if the product is with this discount, and it updated
            //with this discount then we undo the performed discount
            if(this.productsUnderThisDiscount.containsKey(product)){
                if(isApplied) {
                    double discount = getDiscount(products, product);
                    product.setCost(product.getCost() + discount);    //update the price by discount
                    isExpired = true;
                }
            }
        }
    }

    private double getDiscount(Map<Product, Integer> products, Product product) {
        Product productInAmountStruct = isProductInAmountStruct(product);
        int amountToApply = this.amountOfProductsForApplyDiscounts.get(productInAmountStruct);
        return calculateDiscount(amountToApply, products.get(product), product.getOriginalCost());
    }

    /**
     * update price by visible discount
     * @param products to update the related to the discount between them
     */
    private void applyConditionalProductDiscount(Map<Product, Integer> products) {
        int amountOfTerms = productsUnderThisDiscount.size();    //how many subConditions in the map
        int amountOfTrueConditions = 0;
        for (Product product : products.keySet()) {
            if (isApprovedCondition(product, products.get(product))) {
                amountOfTrueConditions++;
            }
        }
        if (amountOfTerms == amountOfTrueConditions) {
            for (Product product : products.keySet()) {
                if (isApprovedCondition(product, products.get(product))) {
                    double discount = getDiscount(products, product);
                    product.setCost(product.getCost() - discount);    //update the price by discount
                    if (product.getCost() <= 0) {
                        throw new IllegalProductPriceException(id); //if the inserted percentage is not reasonable
                    }
                    //TODO: optional throwing exception for duplicated discount
                }
            }
        }
    }



    public boolean isApprovedProducts(Map<Product,Integer> products){
        if(this.endTime.compareTo(Calendar.getInstance())>=0) {
            int amountOfTerms = this.productsUnderThisDiscount.size();    //how many subConditions in the map
            int amountOfTrueConditions = 0;
            for (Product product : products.keySet()) {
                if (isApprovedCondition(product, products.get(product))) {
                    amountOfTrueConditions++;
                }
            }
            if (amountOfTerms == amountOfTrueConditions) {
                return true;
            }
        }
        return false;
    }


    private double calculateDiscount(int amountToApply, Integer amountInBag,double price) {
        if(amountInBag==0){
            return 0;
        }
        return (((discountPercentage*amountToApply)/amountInBag)/100)*price;
    }

    /**
     * match a product in the structure of discountsed products
     * @param product to search
     * @return
     */
    private Product isProductInDiscountStruct(Product product){
        for(Product product1: this.productsUnderThisDiscount.keySet()){
            if(product1.getProductSn()==product.getProductSn()){
                return product1;
            }
        }
        return null;
    }

    private Product isProductInAmountStruct(Product product){
        for(Product product1: this.amountOfProductsForApplyDiscounts.keySet()){
            if(product1.getProductSn()==product.getProductSn()){
                return product1;
            }
        }
        return null;
    }

    @Override
    public void editProductByDiscount(Product product) {

    }

    /**
     * add products with amount of units to apply the discount on.
     * @param products
     * @return
     */
    public boolean addProductToThisDiscount(HashMap<Product,Integer> products) {
        try{
            this.productsUnderThisDiscount.putAll(products);
            return true;
        }catch (NullPointerException e){
            return false;
        }
    }

    /**
     * add products with amount of units to apply the discount on.
     * @param products
     * @return
     */
    public boolean addProductToAmountToApply(HashMap<Product,Integer> products) {
        try{
            this.amountOfProductsForApplyDiscounts.putAll(products);
            return true;
        }catch (NullPointerException e){
            return false;
        }
    }

}

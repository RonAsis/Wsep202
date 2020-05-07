package com.wsep202.TradingSystem.domain.trading_system_management.discount;
/**
 * products under discount with no condition
 */

import com.wsep202.TradingSystem.domain.exception.IllegalProductPriceException;
import com.wsep202.TradingSystem.domain.trading_system_management.Product;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Setter
@Getter
@AllArgsConstructor
@Slf4j
@Builder
public class VisibleDiscount extends DiscountPolicy{

    public VisibleDiscount(Map<Product,Integer> productsUnderDiscount,
                           Calendar endTime, double discountPercentage) {
        this.endTime = endTime;
        this.discountPercentage = discountPercentage;
        //contains all products in store that are under this discount
        this.productsUnderThisDiscount = productsUnderDiscount;
        discountIdAcc = getdiscountIdAcc();
        this.id = discountIdAcc;
    }

    /**
     * apply visible discount without any condition
     * in case the time of the discount is not expired yet
     * @param products the items to apply the discount on
     */
    @Override
    public void applyDiscount(Map<Product,Integer> products) {
        //The discount time is not expired yet
        if(this.endTime.compareTo(Calendar.getInstance()) >= 0){
            if(!isApplied) {
                applyVisibleDiscount(products);
                isApplied = true;   //discount already performed
            }
        }else{  //check if needs to update back the price
            undoVisibleDiscount(products);

        }
    }

    @Override
    public boolean isApprovedProducts(Map<Product, Integer> products) {
        if (this.endTime.compareTo(Calendar.getInstance()) >= 0) {
            for (Product product : products.keySet()) {
                if (isProductInDiscountStruct(product)!=null) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * delegates the products to internal visible discount undo operation
     * @param products to update
     */
    @Override
    public void undoDiscount(Map<Product, Integer> products) {
        undoVisibleDiscount(products);
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


    /**
     * remove product from the products under discount list
     * @param product   to remove
     * @return  true for successful remove
     */
    public boolean removeProductFromDiscount(Product product){
        return this.productsUnderThisDiscount.remove(product) != null;
    }

    /**
     * undo visible discount
     * @param products to update the related undo from the discount between them
     */
    private void undoVisibleDiscount(Map<Product,Integer> products) {
        for(Product product: products.keySet()){
            //verify that if the product is with this discount, and it updated
            //with this discount then we undo the performed discount
            Product p = isProductInDiscountStruct(product);
            if(this.productsUnderThisDiscount.containsKey(p)){
                if(isApplied) {
                    double discount = (discountPercentage / 100) * product.getOriginalCost();
                    product.setCost(product.getCost() + discount);    //update the price by discount
                    isExpired = true;
                }
            }
        }
    }

    /**
     * update price by visible discount
     * @param products to update the related to the discount between them
     */
    private void applyVisibleDiscount(Map<Product,Integer> products) {
        for(Product product: products.keySet()){
            Product p = isProductInDiscountStruct(product);
            if(this.productsUnderThisDiscount.containsKey(p)){
                double discount = (discountPercentage/100)*product.getOriginalCost();
                product.setCost(product.getCost()-discount);    //update the price by discount
                if(product.getCost()<=0){
                    throw new IllegalProductPriceException(id);
                }
                //TODO: optional throwing exception for duplicated discount
            }
        }
    }

    /**
     * update specific product with discount
     * @param product to update
     */
    public void editProductByDiscount(Product product){
        HashMap<Product,Integer> productToUpdate = new HashMap<>();
        productToUpdate.put(product,0);
        applyVisibleDiscount(productToUpdate);
    }


    public String toString(){
        return "Discount details: discount percentage: "+discountPercentage+" ,valid util: "+endTime;
    }
}

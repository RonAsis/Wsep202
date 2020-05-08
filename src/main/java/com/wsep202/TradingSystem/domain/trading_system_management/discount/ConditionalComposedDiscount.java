package com.wsep202.TradingSystem.domain.trading_system_management.discount;

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
public class ConditionalComposedDiscount extends ConditionalDiscount {

    private CompositeOperator compositeOperator;
    /**
     * children components of the composite conditional discount
     * the operands of the composed conditional discount
     * Integer = the discount id
     */
    private Map<Integer, DiscountPolicy> composedDiscounts;

    /**
     * the discounts to apply on the received products ion case they are stands in conditions
     * Integer = the discount id
     */
    private HashMap<Integer, DiscountPolicy> discountsToApply;


    public ConditionalComposedDiscount(CompositeOperator compositeOperator, Calendar endTime, double discountPercentage, String description) {
        super(endTime, discountPercentage, description);
        discountIdAcc = getdiscountIdAcc();
        this.id = discountIdAcc;
        this.compositeOperator = compositeOperator;
        this.composedDiscounts = new HashMap<>();
        this.discountsToApply = new HashMap<>();
    }

    public void editDiscount(Calendar endTime, double percentage, CompositeOperator operator,
                             Map<Integer, DiscountPolicy> composedToAdd,
                             Map<Integer, DiscountPolicy> composedToDelete,
                             Map<Integer, DiscountPolicy> applyDiscToAdd,
                             Map<Integer, DiscountPolicy> applyDiscToDelete) {
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
        //update the logic operator
        if(operator!=null){
            this.compositeOperator = operator;
        }
        ///////////////update the discounts list to check and list of discounts to apply/////////
        if(composedToAdd!=null){
            this.composedDiscounts.putAll(composedToAdd);
        }
        if(composedToDelete!=null){
            for(DiscountPolicy policy: composedToDelete.values()){
                this.composedDiscounts.remove(policy);
            }
        }
        if(applyDiscToAdd!=null){
            this.discountsToApply.putAll(applyDiscToAdd);
        }
        if(applyDiscToDelete!=null){
            for(DiscountPolicy policy: applyDiscToDelete.values()){
                this.discountsToApply.remove(policy);
            }
        }
    }

    /**
     * add new discounts to the container
     *
     * @param composedDiscounts
     * @return
     */
    public boolean addComposedDiscounts(HashMap<Integer, DiscountPolicy> composedDiscounts) {
        if (composedDiscounts == null)
            return false;
        this.composedDiscounts.putAll(composedDiscounts);
        return true;
    }

    /**
     * checks conditions of all the sub discounts by the received composition operator
     *
     * @param products the products to check they are stands in the composite discount conditions
     * @return true if stands in the composite condition
     */
    @Override
    public boolean isApprovedProducts(Map<Product, Integer> products) {
        boolean ans;
        if (this.endTime.compareTo(Calendar.getInstance()) >= 0) { //not expired yet
            if (this.compositeOperator == CompositeOperator.AND) {                        //And case
                ans = true;
                for (DiscountPolicy discountPolicy : this.composedDiscounts.values()) {
                    ans = ans && discountPolicy.isApprovedProducts(products);
                }
                return ans;
            } else if (this.compositeOperator == CompositeOperator.OR) {                   //OR case
                ans = false;
                for (DiscountPolicy discountPolicy : this.composedDiscounts.values()) {
                    ans = ans || discountPolicy.isApprovedProducts(products);
                }
                return ans;

            } else if (this.compositeOperator == CompositeOperator.XOR) {                  //XOR case
                int sumOfTrue = 0;
                for (DiscountPolicy discountPolicy : this.composedDiscounts.values()) {
                    if (discountPolicy.isApprovedProducts(products)) {
                        sumOfTrue++;
                    }
                }
                if (sumOfTrue % 2 != 0) { //xor between odd amount of true = true
                    return true;
                }

            }
        }
        return false;
    }

    /**
     * apply all the implies discounts on the received products
     *
     * @param products in store
     */
    @Override
    public void applyDiscount(Map<Product, Integer> products) {
        //composed condition is approved and is not expired yet
        if (this.endTime.compareTo(Calendar.getInstance()) >= 0 && isApprovedProducts(products)&&
        !isDeleted) {
            for (DiscountPolicy discountPolicy : this.discountsToApply.values()) {
                //apply discounts on products
                isApplied = true;
                discountPolicy.applyDiscount(products);
            }
        } else { //undo the discounts in case they were ever applied
            if (isApplied) {
                undoDiscount(products);
            }
        }
    }

    /**
     * undo all discounts applied on products
     *
     * @param products to update
     */
    @Override
    public void undoDiscount(Map<Product, Integer> products) {
        for(DiscountPolicy discountPolicy: this.discountsToApply.values()){
            discountPolicy.undoDiscount(products);
        }
    }
}

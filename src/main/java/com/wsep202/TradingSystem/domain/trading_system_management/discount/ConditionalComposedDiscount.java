package com.wsep202.TradingSystem.domain.trading_system_management.discount;

import com.wsep202.TradingSystem.domain.trading_system_management.Product;

import java.util.Calendar;
import java.util.HashMap;

public class ConditionalComposedDiscount extends ConditionalDiscount{
    public ConditionalComposedDiscount(CompositeOperator compositeOperator,Calendar endTime, double discountPercentage, String description) {
        super(endTime, discountPercentage, description);
        discountIdAcc = getdiscountIdAcc();
        this.id = discountIdAcc;
        this.compositeOperator = compositeOperator;
    }

    private CompositeOperator compositeOperator;
    /**
     * children components of the composite conditional discount
     */
    private HashMap<Integer,DiscountPolicy> composedDiscounts;

    /**
     * add new discounts to the container
     * @param composedDiscounts
     * @return
     */
    public boolean addComposedDiscounts(HashMap<Integer,DiscountPolicy> composedDiscounts){
        if(composedDiscounts==null)
            return false;
        this.composedDiscounts.putAll(composedDiscounts);
        return true;
    }

    /**
     * checks conditions of all the sub discounts by the received composition operator
     * @param products the products to check they are stands in the composite discount conditions
     * @return true if stands in the composite condition
     */
    @Override
    public boolean isApprovedProducts(HashMap<Product,Integer> products) {
        boolean ans = false;
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

    @Override
    public void applyDiscount(HashMap<Product, Integer> products) {

    }

    @Override
    public void editProductByDiscount(Product product) {

    }
}

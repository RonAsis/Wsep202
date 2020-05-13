package com.wsep202.TradingSystem.domain.trading_system_management.purchase;

import com.wsep202.TradingSystem.domain.trading_system_management.BillingAddress;
import com.wsep202.TradingSystem.domain.trading_system_management.Product;
import com.wsep202.TradingSystem.domain.trading_system_management.UserSystem;
import com.wsep202.TradingSystem.domain.trading_system_management.discount.CompositeOperator;

import java.util.*;

public class ComposedPurchase extends PurchasePolicy{

    /**
     * logical operator between policies
     */
    private CompositeOperator compositeOperator;
    /**
     * children components of the composite Purchase policy
     * the operands of the composed Purchase policy
     */
    private List<PurchasePolicy> composedPurchasePolicies;

    public ComposedPurchase(CompositeOperator compositeOperator, List<PurchasePolicy> composedPurchasePolicies) {
        this.compositeOperator = compositeOperator;
        this.composedPurchasePolicies = composedPurchasePolicies;
        this.id = getPurchaseIdAcc();
    }

//    public void editDiscount(Calendar endTime, double percentage, CompositeOperator operator,
//                             Map<Integer, DiscountPolicy> composedToAdd,
//                             Map<Integer, DiscountPolicy> composedToDelete,
//                             Map<Integer, DiscountPolicy> applyDiscToAdd,
//                             Map<Integer, DiscountPolicy> applyDiscToDelete) {
//        if(endTime!=null){
//            if(endTime.compareTo(Calendar.getInstance())<0){
//                //the end time passed so not valid
//                throw new NotValidEndTime(endTime);
//            }
//            //end time is valid
//            this.endTime = endTime;
//        }
//        if(percentage>=0){
//            this.discountPercentage = percentage;
//        }
//        //update the logic operator
//        if(operator!=null){
//            this.compositeOperator = operator;
//        }
//        ///////////////update the discounts list to check and list of discounts to apply/////////
//        if(composedToAdd!=null){
//            this.composedDiscounts.putAll(composedToAdd);
//        }
//        if(composedToDelete!=null){
//            for(DiscountPolicy policy: composedToDelete.values()){
//                this.composedDiscounts.remove(policy);
//            }
//        }
//        if(applyDiscToAdd!=null){
//            this.discountsToApply.putAll(applyDiscToAdd);
//        }
//        if(applyDiscToDelete!=null){
//            for(DiscountPolicy policy: applyDiscToDelete.values()){
//                this.discountsToApply.remove(policy);
//            }
//        }
//    }

    /**
     * add new discounts to the container
     *
     * @param policies to add
     * @return
     */
    public boolean addComposedPurchasePolicies(Set<PurchasePolicy> policies) {
        if (policies == null)
            return false;
        this.composedPurchasePolicies.addAll(policies);
        return true;
    }

    @Override
    public boolean isApproved(Map<Product, Integer> products, UserSystem user, BillingAddress userAddress) {
        boolean ans;
            if (this.compositeOperator == CompositeOperator.AND) {                        //And case
                ans = true;
                for (PurchasePolicy policy : this.composedPurchasePolicies) {
                    ans = ans && policy.isApproved(products,user,userAddress);
                }
                return ans;
            } else if (this.compositeOperator == CompositeOperator.OR) {                   //OR case
                ans = false;
                for (PurchasePolicy policy : this.composedPurchasePolicies) {
                    ans = ans || policy.isApproved(products,user,userAddress);
                }
                return ans;

            } else if (this.compositeOperator == CompositeOperator.XOR) {                  //XOR case
                int sumOfTrue = 0;
                for (PurchasePolicy policy : this.composedPurchasePolicies) {
                    if (policy.isApproved(products,user,userAddress)) {
                        sumOfTrue++;
                    }
                }
                if (sumOfTrue % 2 != 0) { //xor between odd amount of true = true
                    return true;
                }

            }
        //operator is not defined
        return false;
    }
}

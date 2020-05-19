package com.wsep202.TradingSystem.domain.trading_system_management.purchase;

import com.wsep202.TradingSystem.domain.exception.PurchasePolicyException;
import com.wsep202.TradingSystem.domain.exception.TradingSystemException;
import com.wsep202.TradingSystem.domain.trading_system_management.BillingAddress;
import com.wsep202.TradingSystem.domain.trading_system_management.Product;
import com.wsep202.TradingSystem.domain.trading_system_management.TradingSystem;
import com.wsep202.TradingSystem.domain.trading_system_management.UserSystem;
import com.wsep202.TradingSystem.domain.trading_system_management.discount.CompositeOperator;

import java.util.*;

public class ComposedPurchase extends PurchasePolicy{

    @Override
    public boolean isApproved(Purchase purchase, Map<Product, Integer> products,
                              BillingAddress userAddress) throws TradingSystemException{
        boolean ans;
            if (purchase.getCompositeOperator() == CompositeOperator.AND) {                        //And case
                ans = true;
                for (Purchase policy : purchase.getComposedPurchasePolicies()) {
                    ans = ans && policy.isApproved(products,userAddress);
                }
                return ans;
            } else if (purchase.getCompositeOperator() == CompositeOperator.OR) {                   //OR case
                ans = isOrPolicyApproved(purchase, products, userAddress, false);
                if (!ans){  //all of the purchase policies failed
                    throw new PurchasePolicyException(getAllExceptionsMessages(purchase, products, userAddress));
                }
                //policies OR succeeded. someone succeeded.
                return true;

            } else if (purchase.getCompositeOperator() == CompositeOperator.XOR) {                  //XOR case
                int sumOfTrue = getSumOfTrue(purchase, products, userAddress);
                if (sumOfTrue % 2 != 0) { //xor between odd amount of true = true
                    return true;
                }
                //if XOR policies failed
                throw new PurchasePolicyException(getAllExceptionsMessages(purchase, products, userAddress));
            }
        //operator is not defined
        return false;
    }

    private int getSumOfTrue(Purchase purchase, Map<Product, Integer> products, BillingAddress userAddress) {
        int sumOfTrue = 0;
        for (Purchase policy : purchase.getComposedPurchasePolicies()) {
            try {
                if (policy.isApproved(products, userAddress)) {
                    sumOfTrue++;
                }
            }catch (TradingSystemException ex){
                //do nothing
            }
        }
        return sumOfTrue;
    }

    private String getAllExceptionsMessages(Purchase purchase, Map<Product, Integer> products, BillingAddress userAddress) {
        String exception = "";
        for (Purchase policy : purchase.getComposedPurchasePolicies()) {
            try {
                policy.isApproved(products, userAddress);
            }catch (TradingSystemException ex){
                exception+=" "+ex.getMessage()+".\n";
            }
        }
        return exception;
    }

    private boolean isOrPolicyApproved(Purchase purchase, Map<Product, Integer> products,
                                       BillingAddress userAddress, boolean ans) {
        for (Purchase policy : purchase.getComposedPurchasePolicies()) {
                    try {
                        ans = ans || policy.isApproved(products, userAddress);
                    }catch (TradingSystemException ex){
                        ans = ans || false;
                    }
        }
        return ans;
    }


}

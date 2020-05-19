package com.wsep202.TradingSystem.domain.trading_system_management.purchase;

import com.wsep202.TradingSystem.domain.trading_system_management.BillingAddress;
import com.wsep202.TradingSystem.domain.trading_system_management.Product;
import com.wsep202.TradingSystem.domain.trading_system_management.UserSystem;
import com.wsep202.TradingSystem.domain.trading_system_management.discount.CompositeOperator;

import java.util.*;

public class ComposedPurchase extends PurchasePolicy{

    @Override
    public boolean isApproved(Purchase purchase, Map<Product, Integer> products, UserSystem user, BillingAddress userAddress) {
        boolean ans;
            if (purchase.getCompositeOperator() == CompositeOperator.AND) {                        //And case
                ans = true;
                for (Purchase policy : purchase.getComposedPurchasePolicies()) {
                    ans = ans && policy.isApproved(products,user,userAddress);
                }
                return ans;
            } else if (purchase.getCompositeOperator() == CompositeOperator.OR) {                   //OR case
                ans = false;
                for (Purchase policy : purchase.getComposedPurchasePolicies()) {
                    ans = ans || policy.isApproved(products,user,userAddress);
                }
                return ans;

            } else if (purchase.getCompositeOperator() == CompositeOperator.XOR) {                  //XOR case
                int sumOfTrue = 0;
                for (Purchase policy : purchase.getComposedPurchasePolicies()) {
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

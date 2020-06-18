package com.wsep202.TradingSystem.domain.trading_system_management.policy_purchase;

import com.wsep202.TradingSystem.domain.exception.PurchasePolicyException;
import com.wsep202.TradingSystem.domain.exception.TradingSystemException;
import com.wsep202.TradingSystem.domain.trading_system_management.discount.Discount;
import com.wsep202.TradingSystem.domain.trading_system_management.purchase.BillingAddress;
import com.wsep202.TradingSystem.domain.trading_system_management.Product;
import com.wsep202.TradingSystem.domain.trading_system_management.discount.CompositeOperator;
import lombok.*;

import javax.persistence.*;
import java.util.*;

@Setter
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ComposedPurchase extends PurchasePolicy{

    /**
     * logical operator between policies
     */
    @Enumerated(EnumType.STRING)
    private CompositeOperator compositeOperator;

    /**
     * children components of the composite Purchase policy
     * the operands of the composed Purchase policy
     */
    @OneToMany(fetch = FetchType.EAGER, cascade={CascadeType.PERSIST,CascadeType.REMOVE}, orphanRemoval = true)
    private List<Purchase> composedPurchasePolicies;

    @Override
    public boolean isApproved(Purchase purchase, Map<Product, Integer> products,
                              BillingAddress userAddress) throws TradingSystemException{
        boolean ans;
            if (compositeOperator == CompositeOperator.AND) {                        //And case
                ans = true;
                for (Purchase policy : composedPurchasePolicies) {
                    ans = ans && policy.getPurchasePolicy().isApproved(purchase, products,userAddress);
                }
                return ans;
            } else if (compositeOperator == CompositeOperator.OR) {                   //OR case
                ans = isOrPolicyApproved(purchase, products, userAddress, false);
                if (!ans){  //all of the purchase policies failed
                    throw new PurchasePolicyException(getAllExceptionsMessages(purchase, products, userAddress));
                }
                //policies OR succeeded. someone succeeded.
                return true;

            } else if (compositeOperator == CompositeOperator.XOR) {                  //XOR case
                int sumOfTrue = getSumOfTrue(products, userAddress);
                if (sumOfTrue % 2 != 0) { //xor between odd amount of true = true
                    return true;
                }
                //if XOR policies failed
                throw new PurchasePolicyException(getAllExceptionsMessages(purchase, products, userAddress));
            }
        //operator is not defined
        return false;
    }




    private int getSumOfTrue( Map<Product, Integer> products, BillingAddress userAddress) {
        return composedPurchasePolicies.stream()
                .filter(purchase1 -> purchase1.isApproved(products,userAddress))
                .toArray().length;
    }

    private String getAllExceptionsMessages(Purchase purchase, Map<Product, Integer> products, BillingAddress userAddress) {
        String exception = "";
        for (Purchase policy : composedPurchasePolicies) {
            try {
                policy.getPurchasePolicy().isApproved(purchase,products, userAddress);
            }catch (TradingSystemException ex){
                exception+=" "+ex.getMessage()+".\n";
            }
        }
        return exception;
    }

    private boolean isOrPolicyApproved(Purchase purchase, Map<Product, Integer> products,
                                       BillingAddress userAddress, boolean ans) {
        for (Purchase policy : composedPurchasePolicies) {
                    try {
                        ans = ans || policy.getPurchasePolicy().isApproved(purchase,products, userAddress);
                    }catch (TradingSystemException ex){
                        ans = ans || false;
                    }
        }
        return ans;
    }

    public boolean edit(CompositeOperator compositeOperator, List<Purchase> composedPurchasePolicies){
        if (composedPurchasePolicies != null && composedPurchasePolicies != null &&
        !composedPurchasePolicies.isEmpty()){
            this.compositeOperator = compositeOperator;
            this.composedPurchasePolicies = composedPurchasePolicies;
            return true;
        }
        return false;
    }
}

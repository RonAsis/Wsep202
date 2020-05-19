package com.wsep202.TradingSystem.domain.trading_system_management.purchase;

import com.wsep202.TradingSystem.domain.exception.PurchasePolicyException;
import com.wsep202.TradingSystem.domain.exception.TradingSystemException;
import com.wsep202.TradingSystem.domain.trading_system_management.BillingAddress;
import com.wsep202.TradingSystem.domain.trading_system_management.Day;
import com.wsep202.TradingSystem.domain.trading_system_management.Product;
import com.wsep202.TradingSystem.domain.trading_system_management.UserSystem;
import com.wsep202.TradingSystem.domain.trading_system_management.notification.Notification;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Calendar;
import java.util.Map;
import java.util.Set;
@Setter
@Getter
@Slf4j
@Builder
public class SystemDetailsPolicy extends PurchasePolicy {

    @Override
    public boolean isApproved(Purchase purchase, Map<Product, Integer> products,
                              BillingAddress userAddress) throws TradingSystemException {
        Day purchaseDate = Day.getDay(Calendar.getInstance().get(Calendar.DAY_OF_WEEK));
        if(!purchase.getStoreWorkDays().contains(purchaseDate)){
            //user tried to purchase NOT in one of the work days of the store so policy failed
            //notice the user
            log.info("you tried to purchase in store out its working days.\n" +
                    "purchase policy ID: "+purchase.purchaseId);
            throw new PurchasePolicyException("Sorry," +
                    " but purchase doesn't stands at the store purchase policy:" +
                    " store is closed at: "+purchaseDate.toString());

        }
        //stands in the policy terms
        log.info("the user passed the purchase policy with ID:" +
                " " +purchase.purchaseId+".");
        return true;
    }

}

package com.wsep202.TradingSystem.domain.trading_system_management.purchase;

import com.wsep202.TradingSystem.domain.trading_system_management.BillingAddress;
import com.wsep202.TradingSystem.domain.trading_system_management.Day;
import com.wsep202.TradingSystem.domain.trading_system_management.Product;
import com.wsep202.TradingSystem.domain.trading_system_management.UserSystem;
import com.wsep202.TradingSystem.domain.trading_system_management.notification.Notification;

import java.util.Calendar;
import java.util.Map;
import java.util.Set;

public class SystemDetailsPolicy extends PurchasePolicy {
    //the system details which are the conditions to buy in the store
    /**
     * the days in the week any user is permitted to perform a purchase
     */
    private Set<Day> storeWorkDays;

    public SystemDetailsPolicy(Set<Day> storeWorkDays) {
        this.storeWorkDays = storeWorkDays;
        this.id = this.getPurchaseIdAcc();
    }

    @Override
    public boolean isApproved(Map<Product, Integer> products, UserSystem user, BillingAddress userAddress) {
        Day purchaseDate = Day.getDay(Calendar.getInstance().get(Calendar.DAY_OF_WEEK));
        if(!storeWorkDays.contains(purchaseDate)){
            //user tried to purchase NOT in one of the work days of the store so policy failed
            //notice the user
            user.newNotification(Notification.builder().content("Sorry," +
                    " but purchase doesn't stands at the store purchase policy:" +
                    " store is closed at: "+purchaseDate.toString()).build());
            return false;
        }
        //stands in the policy terms
        return true;
    }

    /**
     * extends work days with additional days for the store
     * @param days
     */
    public void addWorkDays(Set<Day> days){
        this.storeWorkDays.addAll(days);
    }

    /**
     * remove days from working days of store
     * @param days
     */
    public void removeWorkDays(Set<Day> days){
        this.storeWorkDays.removeAll(days);
    }
}

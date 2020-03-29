package com.wsep202.TradingSystem.domain.trading_system_management;

import java.util.HashSet;
import java.util.Set;

public class TradingSystem {

    private Set<Store> stores = new HashSet<>();

    private Set<UserSystem> users = new HashSet<>();

    private ExternalServiceManagement externalServiceManagement = new ExternalServiceManagement();

    /**
     * buy the shopping cart
     */
    public Receipt buyShoppingCart(UserSystem userSystem){
       // ShoppingCart shoppingCart = userSystem.ge
        return null;
    }

    /**
     * open empty store
     */
    public void openStore(UserSystem ownerStore){
        Store store = new Store(ownerStore);
        ownerStore.addNewStore(store);
    }

    /**
     * register new user
     */
    public boolean registerNewUser(UserSystem userToRegister){
        boolean isRegistered = isRegistered(userToRegister);
        if(!isRegistered){
            users.add(userToRegister);
        }
        return !isRegistered;
    }

    /**
     *
     */
    public boolean login(UserSystem userToLogin){
        boolean isRegistered = isRegistered(userToLogin);
        boolean suc = false;
        if(isRegistered && !userToLogin.isLogin()){
            userToLogin.login();
            suc = true;
        }
        return suc;
    }

    private boolean isRegistered(UserSystem userToRegister) {
        return users.stream()
                    .anyMatch(user -> user.getUserName().equals(userToRegister.getUserName()));
    }
}

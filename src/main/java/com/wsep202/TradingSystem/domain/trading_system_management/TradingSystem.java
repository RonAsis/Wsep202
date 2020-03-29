package com.wsep202.TradingSystem.domain.trading_system_management;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class TradingSystem {

    private Set<Store> stores;

    private Set<UserSystem> users;

    private ExternalServiceManagement externalServiceManagement ;

    private Set<UserSystem> administrators;

    public TradingSystem(ExternalServiceManagement externalServiceManagement){
        stores = new HashSet<>();
        users = new HashSet<>();
        administrators = new HashSet<>();
        this.externalServiceManagement = externalServiceManagement;
        externalServiceManagement.connect();
    }


    /**
     * buy the shopping cart
     */
    public Receipt buyShoppingCart(UserSystem userSystem){
        //TODO
        return null;
    }

    /**
     * open empty store
     */
    public void openStore(UserSystem ownerStore, PurchasePolicy purchasePolicy, DiscountPolicy discountPolicy, DiscountType discountType, PurchaseType purchaseType){
        //Store store = new Store(ownerStore, purchasePolicy, discountPolicy, discountType, purchaseType);
       // ownerStore.addNewStore(store);
    }

    /**
     * register new user
     */
    public boolean registerNewUser(UserSystem userToRegister){
        boolean isRegistered = isRegisteredUser(userToRegister);
        if(!isRegistered){
            users.add(userToRegister);
        }
        return !isRegistered;
    }

    /**
     *
     */
    public boolean login(UserSystem userToLogin, boolean isManager){
        return isManager? loginRegularUser(userToLogin) : loginAdministrator(userToLogin);
    }

    private boolean loginAdministrator(UserSystem userToLogin) {
        boolean isRegistered = isRegisteredAdministrator(userToLogin);
        boolean suc = false;
        if(isRegistered && !userToLogin.isLogin()){
            userToLogin.login();
            suc = true;
        }
        return suc;
    }

    private boolean isRegisteredAdministrator(UserSystem userToRegister) {
        return administrators.stream()
                .anyMatch(user -> user.getUserName().equals(userToRegister.getUserName()));
    }

    private boolean loginRegularUser(UserSystem userToLogin) {
        boolean isRegistered = isRegisteredUser(userToLogin);
        boolean suc = false;
        if(isRegistered && !userToLogin.isLogin()){
            userToLogin.login();
            suc = true;
        }
        return suc;
    }

    private boolean isRegisteredUser(UserSystem userToRegister) {
        return users.stream()
                    .anyMatch(user -> user.getUserName().equals(userToRegister.getUserName()));
    }

    public Optional<Store> getStore(int storeId) {
        return Optional.empty() ;//TODO
    }

    public boolean closeStore(Store store) {

        //need remove from owner, managers//TODO

        return stores.remove(store);
    }

    public Optional<UserSystem> getUser(String username) {
        return users.stream()
                .filter(user -> user.getUserName().equals(username))
                .findFirst();
    }

    public boolean logout(UserSystem user) {
        user.logout();
        return true;
    }
}

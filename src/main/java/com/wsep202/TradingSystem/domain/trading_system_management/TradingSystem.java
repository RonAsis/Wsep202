package com.wsep202.TradingSystem.domain.trading_system_management;

import com.wsep202.TradingSystem.exception.NotAdministratorException;
import com.wsep202.TradingSystem.exception.StoreDontExistsException;
import com.wsep202.TradingSystem.exception.UserDontExistInTheSystemException;

import java.util.*;

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

    public boolean closeStore(Store store) {

        //need remove from owner, managers//TODO

        return stores.remove(store);
    }

    private Optional<UserSystem> getUserOpt(String username) {
        return users.stream()
                .filter(user -> user.getUserName().equals(username))
                .findFirst();
    }

    public boolean logout(UserSystem user) {
        user.logout();
        return true;
    }

    public UserSystem getAdministratorUser(String administratorUsername) {
        return getAdministratorUserOpt(administratorUsername)
                .orElseThrow(()-> new NotAdministratorException(administratorUsername));
    }

    private Optional<UserSystem> getAdministratorUserOpt(String administratorUsername){
        return administrators.stream()
                .filter(user -> user.getUserName().equals(administratorUsername))
                .findFirst();
    }

    private boolean isAdmin(String administratorUsername){
        return getAdministratorUserOpt(administratorUsername).isPresent();
    }

    public Store getStore(String administratorUsername, int storeId) {
        if(isAdmin(administratorUsername)) {
            return getStore(storeId);
        }
        throw new NotAdministratorException(administratorUsername);
    }

    private Store getStore(int storeId) throws StoreDontExistsException {
        Optional<Store> storeOptional = stores.stream()
                .filter(store -> store.getStoreId() == storeId).findFirst();
        return storeOptional.orElseThrow(() -> new StoreDontExistsException(storeId));
    }

    public UserSystem getUser(String username) throws UserDontExistInTheSystemException {
        Optional<UserSystem> userOpt = getUserOpt(username);
        return userOpt.orElseThrow(() -> new UserDontExistInTheSystemException(username));
    }

    public UserSystem getUserByAdmin(String administratorUsername, String userName) {
        if(isAdmin(administratorUsername)) {
            return getUser(userName);
        }
        throw new NotAdministratorException(administratorUsername);
    }
}

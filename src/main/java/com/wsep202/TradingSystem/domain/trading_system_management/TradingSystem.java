package com.wsep202.TradingSystem.domain.trading_system_management;

import com.wsep202.TradingSystem.domain.exception.*;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class TradingSystem {

    private Set<Store> stores;

    private Set<UserSystem> users;

    private ExternalServiceManagement externalServiceManagement;

    private Set<UserSystem> administrators;

    public TradingSystem(ExternalServiceManagement externalServiceManagement) {
        stores = new HashSet<>();
        users = new HashSet<>();
        administrators = new HashSet<>();
        this.externalServiceManagement = externalServiceManagement;
        externalServiceManagement.connect();
    }

    /**
     * buy the shopping cart
     */
    public Receipt buyShoppingCart(UserSystem userSystem) {
        //TODO
        return null;
    }



    /**
     * register new user
     */
    public boolean registerNewUser(UserSystem userToRegister) {
        userToRegister.setPassword(externalServiceManagement.encryptPassword(userToRegister.getPassword()));
        boolean isRegistered = isRegisteredUser(userToRegister);
        if (!isRegistered) {
            users.add(userToRegister);
        }
        return !isRegistered;
    }

    /**
     *
     */
    public boolean login(UserSystem userToLogin, boolean isAdmin, String password) {
        //TODO
        return isAdmin ? loginRegularUser(userToLogin) : loginAdministrator(userToLogin);
    }

    private boolean loginAdministrator(UserSystem userToLogin) {
        boolean isRegistered = isRegisteredAdministrator(userToLogin);
        boolean suc = false;
        if (isRegistered && !userToLogin.isLogin()) {
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
        if (isRegistered && !userToLogin.isLogin()) {
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
                .orElseThrow(() -> new NotAdministratorException(administratorUsername));
    }

    private Optional<UserSystem> getAdministratorUserOpt(String administratorUsername) {
        return administrators.stream()
                .filter(user -> user.getUserName().equals(administratorUsername))
                .findFirst();
    }

    private boolean isAdmin(String administratorUsername) {
        return getAdministratorUserOpt(administratorUsername).isPresent();
    }

    public Store getStore(String administratorUsername, int storeId) {
        if (isAdmin(administratorUsername)) {
            return getStore(storeId);
        }
        throw new NotAdministratorException(administratorUsername);
    }

    public Store getStore(int storeId) throws StoreDontExistsException {
        Optional<Store> storeOptional = stores.stream()
                .filter(store -> store.getStoreId() == storeId).findFirst();
        return storeOptional.orElseThrow(() -> new StoreDontExistsException(storeId));
    }


    public UserSystem getUser(String username) throws UserDontExistInTheSystemException {
        Optional<UserSystem> userOpt = getUserOpt(username);
        return userOpt.orElseThrow(() -> new UserDontExistInTheSystemException(username));
    }

    public UserSystem getUserByAdmin(String administratorUsername, String userName) {
        if (isAdmin(administratorUsername)) {
            return getUser(userName);
        }
        throw new NotAdministratorException(administratorUsername);
    }

    /**
     * need pass on all the stores and give the product with this name
     * @param productName
     * @return
     */
    public List<Product> searchProductByName(String productName) {
        return null;
    }

    public List<Product> searchProductByCategory(ProductCategory productCategory) {
        return null;
    }

    public List<Product> searchProductByKeyWords(List<String> keyWords) {
        return null;
    }

    public List<Product> filterByRangePrice(List<Product> products, double min, double max) {
        return null;
    }

    public List<Product> filterByProductRank(List<Product> products, int rank) {
        return null;
    }

    public List<Product> filterByStoreRank(List<Product> products, int rank) {
        return null;
    }

    public List<Product> filterByStoreCategory(List<Product> products, String category) {
        return  null;
    }

    public Receipt purchaseShoppingCart(ShoppingCart shoppingCart) {
        return null;
    }

    public Receipt purchaseShoppingCart(UserSystem user) {
        return null;
    }

    public boolean openStore(UserSystem user, DiscountType discountTypeObj, PurchaseType purchaseTypeObj, PurchasePolicy purchasePolicy, DiscountPolicy discountPolicy, String storeName) {
        return false;
    }
}

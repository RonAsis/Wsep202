package com.wsep202.TradingSystem.domain.trading_system_management;

import com.wsep202.TradingSystem.domain.exception.*;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;

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

    public TradingSystem(ExternalServiceManagement externalServiceManagement, Set<Store> stores) {
        this.stores = stores;
        users = new HashSet<>();
        administrators = new HashSet<>();
        this.externalServiceManagement = externalServiceManagement;
        externalServiceManagement.connect();
    }

    /**
     * @ksenia TODO this method OR DELETE IT
     */
    public Receipt buyShoppingCart(UserSystem userSystem) {
        //TODO
        return null;
    }

    /**
     * This method is used to register a new user in the system,
     * this method uses the method isRegisteredUser to check if the user is already registered.
     * In the process of registration the system will encrypt the users password.
     * @param userToRegister - the user that needs to be registered
     * @return true if the registration was successful, returns false if the user is already registered
     * @version 1.0
     */
    public boolean registerNewUser(UserSystem userToRegister) {
        if (!isRegisteredUser(userToRegister)) {
            users.add(userToRegister);
            userToRegister.setPassword(externalServiceManagement.encryptPassword(userToRegister.getPassword()));
            //log.info("TradingSystem.registerNewUser: a new user was registered in the system");
            return true;
        }
        //log.error("TradingSystem.registerNewUser: the user is already registered");
        return false;
    }

    public boolean login(UserSystem userToLogin, boolean isAdmin, String password) {
        boolean isRegistered = administrators.stream()
                .anyMatch(user -> user.getUserName().equals(userToLogin.getUserName()));
        if (!isRegistered){
            //log.error("TradingSystem.login: a non registered user tries to login");
            return false;
        }
        //TODO to check encrypted user password against password received
        return true;
    }


    /*
    public boolean login(UserSystem userToLogin, boolean isAdmin, String password) {
        //TODO to check encrypted user password against password received
        return !isAdmin ? loginRegularUser(userToLogin) : loginAdministrator(userToLogin);
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

    ate boolean isRegisteredAdministrator(UserSystem userToRegister) {
        return administrators.stream()
                .anyMatch(user -> user.getUserName().equals(userToRegister.getUserName()));

    }


    private boolean loginRegularUser(UserSystem userToLogin) {
        boolean isRegistered = isRegisteredUser(userToLogin);
        boolean isSuccess = false;
        if (isRegistered && !userToLogin.isLogin()) {
            userToLogin.login();
            isSuccess = true;
        }
        return isSuccess;
    }
    */
    /**
     * Checks if serToRegister is registered in the system
     * @param userToRegister
     * @return true if there is match of such userv in the system, else false.
     */
    private boolean isRegisteredUser(UserSystem userToRegister) {
        return users.stream()
                .anyMatch(user -> user.getUserName().equals(userToRegister.getUserName()));
    }


    private Optional<UserSystem> getUserOpt(String username) {
        return users.stream()
                .filter(user -> user.getUserName().equals(username))
                .findFirst();
    }

    /**
     * logout the user from the system
     * @param user
     * @return true if logged out, otherwise false
     */
    public boolean logout(UserSystem user) {
        if(user.isLogin()){
            user.logout();
            return true;
        }
        return false;
    }

    /**
     * returns the User system object match the received string in case its an admin user
     * otherwise throw exception
     * @param administratorUsername
     * @return administrator type user
     */
    public UserSystem getAdministratorUser(String administratorUsername) {
        return getAdministratorUserOpt(administratorUsername)
                .orElseThrow(() -> new NotAdministratorException(administratorUsername));
    }

    /**
     * search for administratorUsername as administrator in the
     * @param administratorUsername
     * @return administrator as user system in case found natch
     * or else return null.
     */
    private Optional<UserSystem> getAdministratorUserOpt(String administratorUsername) {
        return administrators.stream()
                .filter(user -> user.getUserName().equals(administratorUsername))
                .findFirst();
    }

    /**
     * checks if the 'administratorUsername' is registered as administrator in the system.
     * @param administratorUsername
     * @return true if found admin with the received username
     * otherwise returns false
     */
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

    /**
     * filter products by range price
     * @param products - the list of products
     * @param min - min price
     * @param max - max price
     * @return - products filtered by range price
     */
    public List<Product> filterByRangePrice(@NotNull List<@NonNull Product> products, double min, double max) {
        return products.stream()
                .filter(product -> min <= product.getCost() && product.getCost() <= max)
                .collect(Collectors.toList());
    }

    /**
     * filter products by product rank
     * @param products - the list of products
     * @param rank - the minimum rank that want for product
     * @return - list of products filtered product rank
     */
    public List<Product> filterByProductRank(@NotNull List<@NonNull Product> products, int rank) {
        return products.stream()
                .filter(product -> rank <= product.getRank())
                .collect(Collectors.toList());
    }

    /**
     * filter products by store rank
     * @param products - the list of products
     * @param rank - the store rank
     * @return list of products filtered by store rank
     */
    public List<Product> filterByStoreRank(@NotNull List<@NonNull Product> products, int rank) {
        return products.stream()
                .filter(product -> {
                    Store store = getStore(product.getStoreId());
                    return rank <= store.getRank();
                })
                .collect(Collectors.toList());
    }

    /**
     * filter products by category
     * @param products - the list of products
     * @param category - the category that want get the products for
     * @return list of products filtered by category
     */
    public List<Product> filterByStoreCategory(@NotNull List<@NonNull Product> products, @NotNull ProductCategory category) {
        return products.stream()
                .filter(product -> category == product.getCategory())
                .collect(Collectors.toList());
    }
    /**
     * @ksenia TODO this method
     */
    public Receipt purchaseShoppingCart(ShoppingCart shoppingCart) {
        return null;
    }
    /**
     * @ksenia TODO this method
     */
    public Receipt purchaseShoppingCart(UserSystem user) {
        return null;
    }

    /**
     * This method is used to open a new store in the system
     * @param user - the user that wants to open the store
     * @param discountTypeObj - the type of discount that can be in the store
     * @param purchaseTypeObj - the type of purchase that can be in the store
     * @param purchasePolicy -  a collection of purchase rules that the user has decided on for his store
     * @param discountPolicy -  a collection of discount rules that the user has decided on for his store
     * @param storeName - the name of the store that the user decided
     * @return - false if the user is not registered, and true after the new store is added to store list
     * @version 1.0
     */
    public boolean openStore(UserSystem user, DiscountType discountTypeObj, PurchaseType purchaseTypeObj, PurchasePolicy purchasePolicy,
                             DiscountPolicy discountPolicy, String storeName) {
        //log.info("TradingSystem.openStore: the method was called with the user who wishes to open the store, discount and purchase policies & types
        // and a string of the name of the store");
        if (!this.users.contains(user)) {//if the user is not registered to the system, he can't open a store
            //log.error("TradingSystem.openStore: a non registered user tried to open a store");
            return false;
        }
        Store newStore = new Store(user,purchasePolicy,discountPolicy,discountTypeObj,purchaseTypeObj,storeName);
        this.stores.add(newStore);
        user.addNewStore(newStore);
        //log.info("TradingSystem.openStore: a new store was opened in the system");
        return true;
    }

    public Set<Store> getStoresList(){
        return this.stores;
    }

    public void setUsersList(Set<UserSystem> users){
        this.users = users;
    }
}

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

    public TradingSystem(@NotNull ExternalServiceManagement externalServiceManagement,
                         @NotNull UserSystem admin){
        this.externalServiceManagement = externalServiceManagement;// must be first
        encryptPassword(admin);
        stores = new HashSet<>();
        users = new HashSet<>();
        administrators = new HashSet<>(Collections.singletonList(admin));
        externalServiceManagement.connect();
    }

    public TradingSystem(ExternalServiceManagement externalServiceManagement, Set<Store> stores,
                         @NotNull UserSystem admin) {
        this.externalServiceManagement = externalServiceManagement;
        encryptPassword(admin);
        administrators = new HashSet<>(Collections.singletonList(admin));
        this.stores = stores;
        users = new HashSet<>();
        externalServiceManagement.connect();
    }

    /**
     * a function that encrypts a password
     * @param userToRegister - the user we want to encrypt his password
     */
    private void encryptPassword(UserSystem userToRegister) {
        PasswordSaltPair passwordSaltPair = externalServiceManagement
                .getEncryptedPasswordAndSalt(userToRegister.getPassword());
        //set the user password and its salt
        userToRegister.setPassword(passwordSaltPair.getHashedPassword());
        userToRegister.setSalt(passwordSaltPair.getSalt());
    }

    /**
     *  TODO this method OR DELETE IT - KSENIA
     */
    public Receipt buyShoppingCart(UserSystem userSystem) {
        //TODO
        return null;
    }

    /**
     * register new user in the system
     * with its password
     * @param userToRegister the user we want to register
     * @return true if the registration succeeded
     */
    public boolean registerNewUser(UserSystem userToRegister) {
        //encrypt his password to store it and its salt in the system
        PasswordSaltPair passwordSaltPair = externalServiceManagement
                .getEncryptedPasswordAndSalt(userToRegister.getPassword());
        //set the user password and its salt
        userToRegister.setPassword(passwordSaltPair.getHashedPassword());
        userToRegister.setSalt(passwordSaltPair.getSalt());
        boolean isRegistered = isRegisteredUser(userToRegister);
        if (!isRegistered) {
            users.add(userToRegister);
            return true;
        }
        return false;
    }

    // TODO - ksenia = add comment + check the correctness of the nex login functions
    /*public boolean login(UserSystem userToLogin, boolean isAdmin, String password) {
        boolean isRegistered = administrators.stream()
                .anyMatch(user -> user.getUserName().equals(userToLogin.getUserName()));
        if (!isRegistered){

            .error("TradingSystem.login: a non registered user tries to login");
            return false;
        }
        //TODO to check encrypted user password against password received
        return true;
    }*/

    public boolean login(UserSystem userToLogin, boolean isAdmin, String password) {
        //TODO example for using the security system password verification
        //verify that the user's password is correct as saved in our system
        if(!externalServiceManagement.isAuthenticatedUserPassword(password,userToLogin)){
            return false;
        }
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

    private boolean isRegisteredAdministrator(UserSystem userToRegister) {
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

    /**
     * Checks if serToRegister is registered in the system
     * @param userToRegister
     * @return true if there is match of such userv in the system, else false.
     */
    private boolean isRegisteredUser(UserSystem userToRegister) {
        return users.stream()
                .anyMatch(user -> user.getUserName().equals(userToRegister.getUserName()));
    }

    // TODO - BAR = add comment
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
     * @param administratorUsername - the name of the administrator
     * @return true if found admin with the received username
     * otherwise returns false
     */
    public boolean isAdmin(String administratorUsername) {
        return getAdministratorUserOpt(administratorUsername).isPresent();
    }

    /**
     * returns a store if the person who ask it is an administrator.
     * @param administratorUsername - the name of the administrator
     * @param storeId - the given store id to return
     * @return - a store if administratorUsername is an admin, else returns NotAdministratorException
     */
    public Store getStoreByAdmin(String administratorUsername, int storeId) {
        if (isAdmin(administratorUsername)) {
            log.info("Admin exists --> calls to 'getStore(storeId)' function.");
            return getStore(storeId);
        }
        log.error("Admin isn't exist --> throws 'NotAdministratorException(administratorUsername)' exception!");
        throw new NotAdministratorException(administratorUsername);
    }

    /**
     * returns a store by a given store id.
     * @param storeId - the store id to search for return
     * @return - the store that we searched for according to its id, else
     * @throws StoreDontExistsException
     */
    public Store getStore(int storeId) throws StoreDontExistsException {
        Optional<Store> storeOptional = stores.stream()
                .filter(store -> store.getStoreId() == storeId).findFirst();
        if (storeOptional.isPresent())
            log.info("Store: " + storeId + " exists in the Trading System.");
        else
            log.error("Store: " + storeId + " isn't exist in the Trading System.");
        return storeOptional.orElseThrow(() -> new StoreDontExistsException(storeId));
    }

    /**
     * returns a user by a given user name.
     * @param username - the user name to search for return
     * @return - the user that we searched for according to its name, else
     * @throws UserDontExistInTheSystemException
     */
    public UserSystem getUser(String username) throws UserDontExistInTheSystemException {
        Optional<UserSystem> userOpt = getUserOpt(username);
        if (userOpt.isPresent())
            log.info("User: " + username + " exists in the Trading System.");
        else
            log.error("User: " + username + " isn't exist in the Trading System.");
        return userOpt.orElseThrow(() -> new UserDontExistInTheSystemException(username));
    }

    /**
     * returns a user if the person who ask it is an administrator.
     * @param administratorUsername - the name of the administrator
     * @param userName - the given user name to return
     * @return - a user if administratorUsername is an admin, else returns NotAdministratorException
     */
    public UserSystem getUserByAdmin(String administratorUsername, String userName) {
        if (isAdmin(administratorUsername)) {
            log.info("Admin exists --> calls to 'getUser(userName)' function.");
            return getUser(userName);
        }
        log.error("Admin isn't exist --> throws 'NotAdministratorException(administratorUsername)' exception!");
        throw new NotAdministratorException(administratorUsername);
    }

    /**
     * searches all the products that there name is productName in all stores.
     * @param productName - the name of product we want to search.
     * @return - a list that contains all suitable products.
     */
    public List<Product> searchProductByName(String productName) {
        return new ArrayList<>(stores.stream()
                .map(store -> store.searchProductByName(productName))
                .reduce((products, products2) -> {
                    products.addAll(products2);
                    return products;})
                .orElse(new HashSet<>()));
    }

    /**
     * searches all the products that there category is productCategory in all stores.
     * @param productCategory - the category of product we want to search.
     * @return - a list that contains all suitable products.
     */
    public List<Product> searchProductByCategory(ProductCategory productCategory) {
        return new ArrayList<>(stores.stream()
                .map(store -> store.searchProductByCategory(productCategory))
                .reduce((products, products2) -> {
                    products.addAll(products2);
                    return products;})
                .orElse(new HashSet<>()));
    }

    /**
     * searches all the products that there name contains keyWords in all stores.
     * @param keyWords - the key words that contained in product name.
     * @return - a list that contains all suitable products.
     */
    public List<Product> searchProductByKeyWords(List<String> keyWords) {
        return new ArrayList<>(stores.stream()
                .map(store -> store.searchProductByKeyWords(keyWords))
                .reduce((products, products2) -> {
                    products.addAll(products2);
                    return products;})
                .orElse(new HashSet<>()));
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
     *  TODO this method - KSENIA
     */
    public Receipt purchaseShoppingCart(ShoppingCart shoppingCart) {
        return null;
    }
    /**
     *  TODO this method - KSENIA
     */
    public Receipt purchaseShoppingCart(UserSystem user) {
        return null;
    }

    // TODO - KSENIA = move over all comments (things putted in log)
    /**
     * This method is used to open a new store in the system
     * @param user - the user that wants to open the store
     * @param discountTypeObj - the type of discount that can be in the store
     * @param purchaseTypeObj - the type of purchase that can be in the store
     * @param purchasePolicy -  a collection of purchase rules that the user has decided on for his store
     * @param discountPolicy -  a collection of discount rules that the user has decided on for his store
     * @param storeName - the name of the store that the user decided
     * @return - false if the user is not registered, and true after the new store is added to store list
     */
    public boolean openStore(UserSystem user, DiscountType discountTypeObj, PurchaseType purchaseTypeObj, PurchasePolicy purchasePolicy,
                             DiscountPolicy discountPolicy, String storeName) {
        log.info("The method was called with the user who wishes to open the store, discount and purchase policies & types " +
                "and a string of the name of the store");

        if (!this.users.contains(user)) {//if the user is not registered to the system, he can't open a store
            log.error("A non registered user tried to open a store");
            return false;
        }
        Store newStore = new Store(user,purchasePolicy,discountPolicy,storeName);
        this.stores.add(newStore);
        user.addNewStore(newStore);
        log.info("A new store was opened in the system");
        return true;
    }

    public Set<Store> getStoresList(){
        return this.stores;
    }

    public void setUsersList(Set<UserSystem> users){
        this.users = users;
    }
  
    public void insertStoreToStores(Store store){
        stores.add(store);
    }
}

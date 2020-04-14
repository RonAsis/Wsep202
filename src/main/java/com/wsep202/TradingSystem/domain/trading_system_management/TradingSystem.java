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
     * buy the shopping cart
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
        encryptPassword(userToRegister);
        boolean isRegistered = isRegisteredUser(userToRegister);
        if (!isRegistered) {
            users.add(userToRegister);
            return true;
        }
        return false;
    }

    private void encryptPassword(UserSystem userToRegister) {
        PasswordSaltPair passwordSaltPair = externalServiceManagement
                .getEncryptedPasswordAndSalt(userToRegister.getPassword());
        //set the user password and its salt
        userToRegister.setPassword(passwordSaltPair.getHashedPassword());
        userToRegister.setSalt(passwordSaltPair.getSalt());
    }

    /**
     *
     */
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

    /**
     * login the userToLogin in the system in case he is registered
     * @param userToLogin
     * @return true upon success, else false
     */
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
            return getStore(storeId);
        }
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
            return getUser(userName);
        }
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

    public Receipt purchaseShoppingCart(ShoppingCart shoppingCart) {
        return null;
    }

    public Receipt purchaseShoppingCart(UserSystem user) {
        return null;
    }

    public void insertStoreToStores(Store store){
        stores.add(store);
    }

    /**
     * todo call to insertStoreToStores function
     * @param user
     * @param discountTypeObj
     * @param purchaseTypeObj
     * @param purchasePolicy
     * @param discountPolicy
     * @param storeId
     * @return
     */
    public boolean openStore(UserSystem user, DiscountType discountTypeObj, PurchaseType purchaseTypeObj, PurchasePolicy purchasePolicy, DiscountPolicy discountPolicy, int storeId) {
        return false;
    }

}

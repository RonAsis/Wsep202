package com.wsep202.TradingSystem.domain.trading_system_management;

import com.wsep202.TradingSystem.domain.exception.*;
import com.wsep202.TradingSystem.domain.image.ImagePath;
import com.wsep202.TradingSystem.domain.image.ImageUtil;
import com.wsep202.TradingSystem.domain.trading_system_management.notification.Observer;
import com.wsep202.TradingSystem.domain.trading_system_management.notification.Subject;
import com.wsep202.TradingSystem.domain.trading_system_management.purchase.PurchasePolicy;
import javafx.util.Pair;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;
@Getter
@Setter
@Slf4j
public class TradingSystem {

    private ExternalServiceManagement externalServiceManagement;

    private Map<String, UUID> usersLogin;

    private TradingSystemDao tradingSystemDao;

    @Setter
    private Subject subject;

    public TradingSystem(@NotNull ExternalServiceManagement externalServiceManagement,
                         @NotNull UserSystem admin,
                         @NotNull TradingSystemDao tradingSystemDao) {
        this.externalServiceManagement = externalServiceManagement;// must be first
        this.externalServiceManagement.connect();    //connect to the externals
        this.tradingSystemDao = tradingSystemDao;
        this.usersLogin = new HashMap<>();
        initAdmin(admin);
        externalServiceManagement.connect();
    }

    private void initAdmin(UserSystem admin) {
        encryptPassword(admin);
        tradingSystemDao.registerAdmin(admin);
    }

    /**
     * a function that encrypts a password
     *
     * @param userToRegister - the user we want to encrypt his password
     */
    private void encryptPassword(UserSystem userToRegister) {
        PasswordSaltPair passwordSaltPair = externalServiceManagement
                .getEncryptedPasswordAndSalt(userToRegister.getPassword());
        //set the user password and its salt
        userToRegister.setPassword(passwordSaltPair.getHashedPassword());
        userToRegister.setSalt(passwordSaltPair.getSalt());
        log.info("The user " + userToRegister.getUserName() + " got encrypted password and salt");
    }


    /**
     * register new user in the system
     * with its password
     *
     * @param userToRegister the user we want to register
     * @param image
     * @return true if the registration succeeded
     */
    @Synchronized
    public boolean registerNewUser(UserSystem userToRegister, MultipartFile image) {
        if (Objects.nonNull(userToRegister)
                && !tradingSystemDao.isRegistered(userToRegister)
                && userToRegister.isValidUser()) {
            encryptPassword(userToRegister); //encrypt user password to store it and its salt in the system
            log.info(String.format("The user %s registering", userToRegister.getUserName()));
            tradingSystemDao.addUserSystem(userToRegister, image);
            return true;
        }
        return false;
    }

    /**
     * This method is used to make a login for a the user
     *
     * @param password - the users password
     * @return true if the user logged-in, false if nots
     */
    @Synchronized
    public Pair<UUID, Boolean> login(String userName, String password) {
        Optional<UserSystem> userSystem = tradingSystemDao.getUserSystem(userName);
        if (userSystem.isPresent() &&
                tradingSystemDao.isRegistered(userSystem.get()) &&
                externalServiceManagement.isAuthenticatedUserPassword(password, userSystem.get()) &&
                Objects.isNull(usersLogin.get(userName))) {
            userSystem.get().login();
            UUID uuid = UUID.randomUUID();
            usersLogin.put(userName, uuid);
            boolean isAdmin = tradingSystemDao.isAdmin(userName);
            log.info(String.format("user: %s logged in successfully, is admin: %b", userName, isAdmin));
            return new Pair<>(uuid, isAdmin);
        }
        log.info(String.format("user: %s failed to login.", userName));
        return null;
    }

    /**
     * logout the user from the system
     *
     * @param user - the user that asks to log out
     * @return true if logged out, otherwise false
     */
    public boolean logout(@NotNull UserSystem user) {

        if (Objects.nonNull(user) &&
                Objects.nonNull(usersLogin.get(user.getUserName()))) {
            user.logout();
            usersLogin.remove(user.getUserName());
            log.info(String.format("user: %s logout successfully", (user.getUserName())));
            return true;
        }
        log.info("user logout fail");
        return false;
    }

    /**
     * returns the User system object match the received string in case its an admin user
     * otherwise throw exception
     *
     * @param administratorUsername - admins user name
     * @return administrator type user
     */
    public UserSystem getAdministratorUser(String administratorUsername) {
        return tradingSystemDao.getAdministratorUser(administratorUsername)
                .orElseThrow(() -> new NotAdministratorException(administratorUsername));
    }


    private boolean uuidIsValid(String administratorUsername, UUID uuid) {
        return Objects.nonNull(administratorUsername) &&
                Objects.nonNull(usersLogin.get(administratorUsername)) &&
                usersLogin.get(administratorUsername).equals(uuid);
    }

    /**
     * returns a store if the person who ask it is an administrator.
     *
     * @param administratorUsername - the name of the administrator
     * @param storeId               - the given store id to return
     * @param uuid
     * @return - a store if administratorUsername is an admin, else returns NotAdministratorException
     */
    public Store getStoreByAdmin(String administratorUsername, int storeId, UUID uuid) throws NotAdministratorException {
        if (uuidIsValid(administratorUsername, uuid) && tradingSystemDao.isAdmin(administratorUsername)) {
            return getStore(storeId);
        }
        throw new NotAdministratorException(administratorUsername);
    }

    /**
     * returns a store by a given store id.
     *
     * @param storeId - the store id to search for return
     * @return - the store that we searched for according to its id, else
     * @throws StoreDontExistsException
     */
    public Store getStore(int storeId) throws StoreDontExistsException {
        return tradingSystemDao.getStore(storeId).orElseThrow(() -> new StoreDontExistsException(storeId));
    }

    /**
     * returns a user by a given user name.
     *
     * @param username - the user name to search for return
     * @param uuid
     * @return - the user that we searched for according to its name, else
     * @throws UserDontExistInTheSystemException
     */
    public UserSystem getUser(String username, UUID uuid) throws UserDontExistInTheSystemException {
        return uuidIsValid(username, uuid) ? tradingSystemDao.getUserSystem(username).orElseThrow(() -> new UserDontExistInTheSystemException(username)) :
                null;
    }

    /**
     * returns a user if the person who ask it is an administrator.
     *
     * @param administratorUsername - the name of the administrator
     * @param uuid
     * @return - a user if administratorUsername is an admin, else returns NotAdministratorException
     */
    public UserSystem getUserByAdmin(String administratorUsername, String username, UUID uuid) {
        return uuidIsValid(administratorUsername, uuid) && tradingSystemDao.isAdmin(administratorUsername) ?
                tradingSystemDao.getUserSystem(username).orElseThrow(() -> new UserDontExistInTheSystemException(username)) :
                null;
    }

    /**
     * searches all the products that there name is productName in all stores.
     *
     * @param productName - the name of product we want to search.
     * @return - a list that contains all suitable products.
     */
    public List<Product> searchProductByName(String productName) {
        return tradingSystemDao.searchProductByName(productName);

    }

    /**
     * searches all the products that there category is productCategory in all stores.
     *
     * @param productCategory - the category of product we want to search.
     * @return - a list that contains all suitable products.
     */
    public List<Product> searchProductByCategory(ProductCategory productCategory) {
        return tradingSystemDao.searchProductByCategory(productCategory);
    }

    /**
     * searches all the products that there name contains keyWords in all stores.
     *
     * @param keyWords - the key words that contained in product name.
     * @return - a list that contains all suitable products.
     */
    public List<Product> searchProductByKeyWords(List<String> keyWords) {
        return tradingSystemDao.searchProductByKeyWords(keyWords);
    }

    /**
     * filter products by range price
     *
     * @param products - the list of products
     * @param min      - min price
     * @param max      - max price
     * @return - products filtered by range price
     */
    public List<Product> filterByRangePrice(@NotNull List<@NonNull Product> products, double min, double max) {
        return products.stream()
                .filter(product -> min <= product.getCost() && product.getCost() <= max)
                .collect(Collectors.toList());
    }

    /**
     * filter products by product rank
     *
     * @param products - the list of products
     * @param rank     - the minimum rank that want for product
     * @return - list of products filtered product rank
     */
    public List<Product> filterByProductRank(@NotNull List<@NonNull Product> products, int rank) {
        return products.stream()
                .filter(product -> rank <= product.getRank())
                .collect(Collectors.toList());
    }

    /**
     * filter products by store rank
     *
     * @param products - the list of products
     * @param rank     - the store rank
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
     *
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
     * This method is used to purchase all the products that the unregistered user added to his cart.
     *
     * @param shoppingCart   - the users personal shopping cart
     * @param paymentDetails - the user credit card number & expiration date
     * @return list of receipts for stores where payment has been made
     */
    public List<Receipt> purchaseShoppingCartGuest(ShoppingCart shoppingCart, PaymentDetails paymentDetails, BillingAddress billingAddress) {
        return purchaseAndDeliver(paymentDetails, shoppingCart, billingAddress, "Guest");
    }


    /**
     * This method is used to purchase all the products that the registered user added to his cart.
     *
     * @param paymentDetails - the user credit card number & expiration date
     * @param user           - the user that made
     * @return list of receipts for stores where payment has been made
     */
    public List<Receipt> purchaseShoppingCart(PaymentDetails paymentDetails, BillingAddress billingAddress, UserSystem user) {
        if (user == null) {
            log.error("user can't be null");
            return null;
        }
        return purchaseAndDeliver(paymentDetails, user.getShoppingCart(), billingAddress, user.getUserName());
    }


    /**
     * This method makes the payment in the externalServiceManagement using the charge method.
     *
     * @param paymentDetails - the users credit card details
     * @param shoppingCart   - the user personal shopping cart
     * @param billingAddress - the delivery address of the user
     * @return a list of receipts for all of the purchases the user made
     */
    private List<Receipt> purchaseAndDeliver(PaymentDetails paymentDetails, ShoppingCart shoppingCart, BillingAddress billingAddress, String customerName)
            throws TradingSystemException{
        //check validity of the arguments fields
        if (validationOfPurchaseArgs(paymentDetails, shoppingCart, billingAddress)) return null;
        shoppingCart.isAllBagsInStock();    //ask the cart to check all products in stock
        log.info("all products in stock");
        shoppingCart.applyDiscountPolicies();
        log.info("applied stores discount policies on shopping cart");
        externalServiceManagement.charge(paymentDetails, shoppingCart);
        log.info("The user has been charged by his purchase.");
        try{//request shipping for the purchase
            externalServiceManagement.deliver(billingAddress, shoppingCart);
        }catch (DeliveryRequestException exception){
            //in case delivery request rejected, cancel charged cart
            externalServiceManagement.cancelCharge(paymentDetails,shoppingCart);
            throw new DeliveryRequestException("The delivery request for: "+customerName+" " +
                    "has been rejected.");
        }
        log.info("delivery request accepted");
        shoppingCart.updateAllAmountsInStock();
        log.info("all amounts of products in stock of stores were updated");
        return shoppingCart.createReceipts(customerName);
    }

    private boolean validationOfPurchaseArgs(PaymentDetails paymentDetails, ShoppingCart shoppingCart, BillingAddress billingAddress) {
        if (shoppingCart == null || paymentDetails == null || billingAddress == null || shoppingCart.getNumOfBagsInCart() == 0 || shoppingCart.getShoppingBagsList() == null) {
            log.error("a store or payment details or billing address can't be null, cart can't be null or empty");
            return true;
        }
        return false;
    }


    /**
     * This method is used to open a new store in the system
     *
     * @param user           - the user that wants to open the store
     * @param storeName      - the name of the store that the user decided
     * @return - false if the user is not registered, and true after the new store is added to store list
     */
    public Store openStore(UserSystem user,
                           String storeName,
                           String description) {
        if (Objects.nonNull(user) &&
                Strings.isNotBlank(storeName)) {
            Store newStore = new Store(user, storeName, description);
            tradingSystemDao.addStore(newStore);
            user.addNewOwnedStore(newStore);
            log.info(String.format("A new store '%s' was opened in the system, %s is the owner", storeName, user.getUserName()));
            return newStore;
        }
        log.info(String.format("Was problem to open store '%s'", storeName));
        return null;
    }


    /**
     * This method is used to add a new manager to an existing store
     *
     * @param ownedStore     - The store to which you want to add a manager
     * @param ownerUser      - owner of the store
     * @param newManagerUser - the user that needs to be added as a manager
     * @return true if the addition was successful, false if there were a problem
     */
    public boolean addMangerToStore(Store ownedStore, UserSystem ownerUser, UserSystem newManagerUser) {
        if(Objects.nonNull(ownedStore) && Objects.nonNull(ownerUser) && Objects.nonNull(newManagerUser)
                && ownedStore.addManager(ownerUser, newManagerUser) ){
            log.info(String.format("user %s was added as manager in store '%d'", newManagerUser.getUserName(),ownedStore.getStoreId()));
            return newManagerUser.addNewManageStore(ownedStore);
        }
        log.info(String.format("failed add user as manager in store"));
        return false;
    }

    /**
     * This method is used to add a new owner to an existing store
     *
     * @param ownedStore   - The store to which you want to add a manager
     * @param ownerUser    - owner of the store
     * @param newOwnerUser - the user that needs to be added as an owner
     * @return true if the addition was successful, false if there were a problem
     */
    public boolean addOwnerToStore(Store ownedStore, UserSystem ownerUser, UserSystem newOwnerUser) {
        if (Objects.nonNull(ownedStore) && Objects.nonNull(ownerUser) && Objects.nonNull(newOwnerUser)
                && ownedStore.addOwner(ownerUser, newOwnerUser)) {
            log.info(String.format("user %s was added as manager in store '%d'", newOwnerUser.getUserName(), ownedStore.getStoreId()));
            return newOwnerUser.addNewOwnedStore(ownedStore);
        }
        log.info(String.format("failed add user as manager in store"));
        return false;
    }

    /**
     * remove manager from the store
     * @param ownedStSore   - the store
     * @param ownerUser    - the owner of the store that want remove the manager
     * @param managerStore - the manager that want to remove
     * @return true if manager was removed, else false
     */
    public boolean removeManager(Store ownedStSore, UserSystem ownerUser, UserSystem managerStore) {
        if(Objects.nonNull(ownedStSore) && Objects.nonNull(ownerUser) && Objects.nonNull(managerStore)
                && ownedStSore.removeManager(ownerUser, managerStore) ){
            log.info(String.format("user %s was removed as manager from store '%d'", managerStore.getUserName(),ownedStSore.getStoreId()));
        }
        log.info(String.format("failed remove user as manager from store"));
        return  false;
    }

    /**
     * connect to Notification System
     */
    public void connectNotificationSystem(Observer observer, String principal) {
        observer.connectNotificationSystem(subject, principal);
    }

    public Set<Store> getStores() {
        return tradingSystemDao.getStores();
    }

    public Set<Product> getProducts() {
        return tradingSystemDao.getProducts();
    }

    /**
     * get the total price of cart before the discounts
     * and get the total price of cart after the discounts
     * @param cartToCalculate
     * @return
     */
    public Pair<Double, Double> getTotalPrices(ShoppingCart cartToCalculate){
        double sumBeforeDiscounts;  //sum of original price
        double sumAfterDiscounts; //sum of the prices after discount
        //update the products in the bags with their discounts
        Map<Store,ShoppingBag> bagsToCalculate = cartToCalculate.getShoppingBagsList();
        for(Store store:bagsToCalculate.keySet()){  //apply the discounts on the bags in the cart (update the products prices)
            store.applyDiscountPolicies((HashMap<Product, Integer>) bagsToCalculate.get(store).getProductListFromStore());
        }
        sumBeforeDiscounts = getOriginalTotalPrice(bagsToCalculate);
        sumAfterDiscounts =  getCurrentTotalPrice(bagsToCalculate);
        return new Pair<>(sumBeforeDiscounts,sumAfterDiscounts);
    }

    /**
     * get the updated by discounts prices of the shopping bag.
     * @param bagsToCalculate
     * @return
     */
    private double getCurrentTotalPrice(Map<Store, ShoppingBag> bagsToCalculate) {
        double totalPrice = 0;
        for(ShoppingBag bag: bagsToCalculate.values()){
            totalPrice+= bag.getOriginalTotalCostOfBag();
        }
        return totalPrice;
    }

    /**
     * get the shopping bag cost with original products prices
     * @param bagsToCalculate
     * @return
     */
    private double getOriginalTotalPrice(Map<Store, ShoppingBag> bagsToCalculate) {
        double totalPrice = 0;
        for(ShoppingBag bag: bagsToCalculate.values()){
            totalPrice+= bag.getTotalCostOfBag();
        }
        return totalPrice;
    }


}

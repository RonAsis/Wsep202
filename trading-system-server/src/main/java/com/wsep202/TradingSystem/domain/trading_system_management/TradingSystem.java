package com.wsep202.TradingSystem.domain.trading_system_management;

import com.wsep202.TradingSystem.domain.exception.*;
import com.wsep202.TradingSystem.domain.trading_system_management.notification.Notification;
import com.wsep202.TradingSystem.domain.trading_system_management.notification.Observer;
import com.wsep202.TradingSystem.domain.trading_system_management.notification.Subject;
import com.wsep202.TradingSystem.domain.trading_system_management.ownerStore.StatusOwner;
import com.wsep202.TradingSystem.domain.trading_system_management.purchase.BillingAddress;
import com.wsep202.TradingSystem.domain.trading_system_management.purchase.PaymentDetails;
import com.wsep202.TradingSystem.domain.trading_system_management.statistics.DailyVisitor;
import com.wsep202.TradingSystem.domain.trading_system_management.statistics.RequestGetDailyVisitors;
import com.wsep202.TradingSystem.domain.trading_system_management.statistics.UpdateDailyVisitor;
import javafx.util.Pair;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@Slf4j
public class TradingSystem {

    private final Object purchaseLock = new Object();

    private ExternalServiceManagement externalServiceManagement;

    private TradingSystemDao tradingSystemDao;

    private static Subject subject;

    private PasswordEncoder passwordEncoder;

    public TradingSystem(@NotNull ExternalServiceManagement externalServiceManagement,
                         @NotNull UserSystem admin,
                         @NotNull TradingSystemDao tradingSystemDao,
                         PasswordEncoder passwordEncoder) {
        this.externalServiceManagement = externalServiceManagement;// must be first
        this.externalServiceManagement.connect();    //connect to the externals
        this.tradingSystemDao = tradingSystemDao;
        this.passwordEncoder = passwordEncoder;
        initAdmin(admin);
        externalServiceManagement.connect();
    }

    private void initAdmin(UserSystem admin) {
        admin.setAdmin(true);
        admin.getGrantedAuthorities().add(GrantedAuthorityImpl.USER);
        admin.getGrantedAuthorities().add(GrantedAuthorityImpl.ADMIN);
        encryptPassword(admin);
        tradingSystemDao.registerAdmin(admin);
    }

    public static Subject getSubject() {
        return subject;
    }

    public static void setSubject(Subject subject) {
        if (Objects.isNull(TradingSystem.subject)) {
            TradingSystem.subject = subject;
        }
    }

    /**
     * a function that encrypts a password
     *
     * @param userToRegister - the user we want to encrypt his password
     */
    private void encryptPassword(UserSystem userToRegister) {
        String encode = passwordEncoder.encode(userToRegister.getPassword());
        userToRegister.setPassword(encode);
        log.info("The user " + userToRegister.getUserName() + " got encrypted password and salt");
    }


    /**
     * UC 2.2
     * register new user in the system
     * with its password
     *
     * @param userToRegister the user we want to register
     * @param image
     * @return true if the registration succeeded
     */
    @Synchronized()
    public boolean registerNewUser(UserSystem userToRegister, MultipartFile image) {
        if (Objects.nonNull(userToRegister)
                && !tradingSystemDao.isRegistered(userToRegister)
                && userToRegister.isValidUser()) {
            encryptPassword(userToRegister); //encrypt user password to store it and its salt in the system
            userToRegister.getGrantedAuthorities().add(GrantedAuthorityImpl.USER);
            log.info(String.format("The user %s registering", userToRegister.getUserName()));
            tradingSystemDao.addUserSystem(userToRegister, image);
            return true;
        }
        return false;
    }

    /**
     * UC 2.3
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
                passwordEncoder.matches(password, userSystem.get().getPassword()) &&
                !tradingSystemDao.isLogin(userName)) {
            userSystem.get().login();
            UUID uuid = UUID.randomUUID();
            Optional<UpdateDailyVisitor> updateDailyVisitorOpt = tradingSystemDao.login(userName, uuid);
            updateDailyVisitorOpt.ifPresent(updateDailyVisitor -> subject.sendDailyVisitor(updateDailyVisitor));
            boolean isAdmin = tradingSystemDao.isAdmin(userName);
            log.info(String.format("user: %s logged in successfully, is admin: %b", userName, isAdmin));
            return new Pair<>(uuid, isAdmin);
        }
        log.info(String.format("user: %s failed to login.", userName));
        return null;
    }

    /**
     * This method is used to check how many users are logged-in the system
     * @return number of users logged-in
     */
    public int usersLoggedInSystem(){
        return tradingSystemDao.usersLoggedInSystem();
    }

    /**
     * This method is used to check if a certain user is logged-in
     * @param userToCheck - the user's username
     * @return true if the user is logged-in, else false
     */
    public boolean isLoggein(String userToCheck){
        if (userToCheck != null){
            return tradingSystemDao.isLogin(userToCheck);
        }
        return false;
    }

    /**
     * UC 3.1
     * logout the user from the system
     *
     * @param user - the user that asks to log out
     * @return true if logged out, otherwise false
     */
    public boolean logout(@NotNull UserSystem user) {

        if (Objects.nonNull(user) &&
                tradingSystemDao.isLogin(user.getUserName())) {
            user.logout();
            tradingSystemDao.logout(user.getUserName());
            subject.unRegDailyVisitor(user.getUserName());
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
                tradingSystemDao.isLogin(administratorUsername) &&
                tradingSystemDao.isValidUuid(administratorUsername, uuid);
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
     * UC 2.8
     * This method is used to purchase all the products that the unregistered user added to his cart.
     *
     * @param shoppingCart   - the users personal shopping cart
     * @param paymentDetails - the user credit card number & expiration date
     * @return list of receipts for stores where payment has been made
     */
    @Synchronized("purchaseLock")
    public List<Receipt> purchaseShoppingCartGuest(ShoppingCart shoppingCart, PaymentDetails paymentDetails, BillingAddress billingAddress) {
        List<Receipt> receipts = purchaseAndDeliver(paymentDetails, shoppingCart, billingAddress, "Guest");
        shoppingCart.getShoppingBagsList().keySet()
                .forEach(store -> tradingSystemDao.updateStore(store));
        return receipts;
    }

    /**
     * UC 2.8
     * This method is used to purchase all the products that the registered user added to his cart.
     *
     * @param paymentDetails - the user credit card number & expiration date
     * @param user           - the user that made
     * @return list of receipts for stores where payment has been made
     */
    @Synchronized("purchaseLock")
    public List<Receipt> purchaseShoppingCart(PaymentDetails paymentDetails, BillingAddress billingAddress, UserSystem user) {
        if (user == null) {
            log.error("user can't be null");
            return null;
        }
        tradingSystemDao.loadShoppingCart(user);
        List<Receipt> receipts = purchaseAndDeliver(paymentDetails, user.getShoppingCart(), billingAddress, user.getUserName());
        user.addReceipts(receipts);
        tradingSystemDao.updateUser(user);
        return receipts;
    }


    /**
     * This method makes the payment in the externalServiceManagement using the charge method.
     *
     * @param paymentDetails - the users credit card details
     * @param shoppingCart   - the user personal shopping cart
     * @param billingAddress - the delivery address of the user
     * @return a list of receipts for all of the purchases the user made
     */
    List<Receipt> purchaseAndDeliver(PaymentDetails paymentDetails,
                                     ShoppingCart shoppingCart, BillingAddress billingAddress,
                                     String customerName)
            throws TradingSystemException {
        //check validity of the arguments fields
        if (validationOfPurchaseArgs(paymentDetails, shoppingCart, billingAddress)) return null;
        shoppingCart.isAllBagsInStock();    //ask the cart to check all products in stock
        log.info("all products in stock");
        shoppingCart.approvePurchasePolicy(billingAddress);
        log.info("applied stores purchase policies on shopping cart");
        shoppingCart.applyDiscountPolicies();
        log.info("applied stores discount policies on shopping cart");
        int chargeTransactionId = externalServiceManagement.charge(paymentDetails, shoppingCart);
        if (chargeTransactionId < 10000 || chargeTransactionId > 100000) {
            //charge failed
            throw new ChargeException("failed to charge: " + paymentDetails.getHolderName() + " for purchase");
        }
        log.info("The card holder:" + paymentDetails.getHolderName() + " has been charged by his purchase.");
        int supplyTransId;
        try {//request shipping for the purchase
            supplyTransId = externalServiceManagement.deliver(billingAddress, shoppingCart);
            if (supplyTransId < 10000 || supplyTransId > 100000) {
                throw new DeliveryRequestException("supply rejected for: " + billingAddress.getCustomerFullName());
            }
        } catch (DeliveryRequestException exception) {
            //in case delivery request rejected, cancel charged cart
            externalServiceManagement.cancelCharge(paymentDetails, shoppingCart, String.valueOf(chargeTransactionId));
            throw new DeliveryRequestException("The delivery request for: " + customerName + " " +
                    "has been rejected.");
        }
        log.info("delivery request accepted");
        shoppingCart.updateAllAmountsInStock();
        log.info("all amounts of products in stock of stores were updated");
        return shoppingCart.createReceipts(customerName, chargeTransactionId, supplyTransId);
    }

    private boolean validationOfPurchaseArgs(PaymentDetails paymentDetails, ShoppingCart shoppingCart, BillingAddress billingAddress) {
        if (shoppingCart == null || paymentDetails == null || billingAddress == null || shoppingCart.getNumOfBagsInCart() == 0 || shoppingCart.getShoppingBagsList() == null) {
            log.error("a store or payment details or billing address can't be null, cart can't be null or empty");
            return true;
        }
        return false;
    }


    /**
     * UC 3.2
     * This method is used to open a new store in the system
     *
     * @param user      - the user that wants to open the store
     * @param storeName - the name of the store that the user decided
     * @return - false if the user is not registered, and true after the new store is added to store list
     */
    public Store openStore(UserSystem user,
                           String storeName,
                           String description) {
        if (Objects.nonNull(user) && Strings.isNotBlank(storeName) && Strings.isNotBlank(description)
                && tradingSystemDao.isRegistered(user)) {
            Store newStore = new Store(user, storeName, description);
            user.addNewOwnedStore(newStore);
            tradingSystemDao.updateStoreAndUserSystem(newStore, user);
            log.info(String.format("A new store '%s' was opened in the system, %s is the owner", storeName, user.getUserName()));
            return newStore;
        }
        log.info(String.format("Was problem to open store '%s'", storeName));
        return null;
    }


    /**
     * UC 4.5
     * This method is used to add a new manager to an existing store
     *
     * @param ownedStore - The store to which you want to add a manager
     * @param ownerUser  - owner of the store
     * @return true if the addition was successful, false if there were a problem
     */
    public MangerStore addMangerToStore(Store ownedStore, UserSystem ownerUser, String newManagerUsername) {
        Optional<UserSystem> newManagerUser = tradingSystemDao.getUserSystem(newManagerUsername);
        if (Objects.nonNull(ownedStore) && Objects.nonNull(ownerUser) && Objects.nonNull(newManagerUser) &&
                newManagerUser.isPresent()) {
            MangerStore mangerStore = ownedStore.appointAdditionManager(ownerUser, newManagerUser.get());
            if (Objects.nonNull(mangerStore)) {
                log.info(String.format("user %s was added as manager in store '%d'", newManagerUser.get().getUserName(), ownedStore.getStoreId()));
                if (newManagerUser.get().addNewManageStore(ownedStore)) {
                    newManagerUser.get().newNotification(Notification.builder()
                            .content(String.format("You are manger of store id %s", ownedStore.getStoreId()))
                            .build());
                    tradingSystemDao.updateStore(ownedStore);
                    log.info(String.format("user %s was saved as manager in store '%d'", newManagerUser.get().getUserName(), ownedStore.getStoreId()));
                    return mangerStore;
                }
            }
        }
        log.info(String.format("failed add user as manager in store id"));
        return null;
    }

    /**
     * UC 4.3
     * This method is used to add a new owner to an existing store
     *
     * @param ownedStore   - The store to which you want to add a manager
     * @param ownerUser    - owner of the store
     * @param newOwnerUser - the user that needs to be added as an owner
     * @return true if the addition was successful, false if there were a problem
     */
    public boolean addOwnerToStore(Store ownedStore, UserSystem ownerUser, String newOwnerUser) {
        UserSystem userSystem = tradingSystemDao.getUserSystem(newOwnerUser).orElse(null);
        if (Objects.nonNull(ownedStore) && Objects.nonNull(ownerUser) && Objects.nonNull(userSystem)) {
            Set<UserSystem> ownerNeedApprove = ownedStore.addOwner(ownerUser, userSystem);
            if (Objects.nonNull(ownerNeedApprove)) {
                log.info(String.format("user %s waiting for be approve in store '%d'", newOwnerUser, ownedStore.getStoreId()));
                approveOwner(ownerUser, userSystem, ownedStore);
                tradingSystemDao.updateUser(userSystem);
                ownerNeedApprove.forEach(userSystemNeedApprove -> tradingSystemDao.updateUser(userSystemNeedApprove));
                tradingSystemDao.updateStoreAndUserSystem(ownedStore, userSystem);
                return true;
            }
        }
        log.info(String.format("user failed for add agreement owner in store"));
        return false;
    }

    private void approveOwner(UserSystem ownerUser, UserSystem newOwnerUser, Store store) {
        if (store.isApproveOwner(newOwnerUser) == StatusOwner.APPROVE) {
            newOwnerUser.addNewOwnedStore(store);
            newOwnerUser.newNotification(Notification.builder()
                    .content(String.format("You are now owner of store %s on name %s", store.getStoreId(), store.getStoreName()))
                    .build());
        }
    }

    /**
     * UC 4.7
     * remove manager from the store
     *
     * @param ownedStSore  - the store
     * @param ownerUser    - the owner of the store that want remove the manager
     * @param managerStore - the manager that want to remove
     * @return true if manager was removed, else false
     */
    public boolean removeManager(Store ownedStSore, UserSystem ownerUser, UserSystem managerStore) {
        if (Objects.nonNull(ownedStSore) && Objects.nonNull(ownerUser) && Objects.nonNull(managerStore)
                && ownedStSore.removeManager(ownerUser, managerStore)) {
            managerStore.newNotification(Notification.builder()
                    .content(String.format("You are Not more manger of store id %s", ownedStSore.getStoreId()))
                    .build());
            tradingSystemDao.updateStoreAndUserSystem(ownedStSore, ownerUser);
            log.info(String.format("user %s was removed as manager from store '%d'", managerStore.getUserName(), ownedStSore.getStoreId()));
            return true;
        }
        log.info(String.format("failed remove user as manager from store"));
        return false;
    }

    /**
     * UC 4.4
     * remove owner from store
     *
     * @param store       - the store
     * @param ownerUser   - the owner of the store that want remove the other owner
     * @param removeOwner - the owner that needs to removed
     * @return true if manager was removed, else false
     */
    public boolean removeOwner(Store store, UserSystem ownerUser, UserSystem removeOwner) {
        if (Objects.nonNull(store) && Objects.nonNull(ownerUser) && Objects.nonNull(removeOwner)
                && store.removeOwner(ownerUser, removeOwner)) {
            removeOwner.newNotification(Notification.builder()
                    .content(String.format("You are Not more owner of store id %s", store.getStoreId()))
                    .build());
            tradingSystemDao.updateStoreAndUserSystem(store, ownerUser);
            log.info(String.format("user %s was removed as owner from store '%d'", removeOwner.getUserName(), store.getStoreId()));
            return true;
        }
        log.info(String.format("failed remove user as owner from store"));
        return false;
    }

    /**
     * connect to Notification System
     */
    public void connectNotificationSystem(Observer observer, String principal) {
        observer.connectNotificationSystem(principal);
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
     *
     * @param cartToCalculate
     * @return
     */
    public Pair<Double, Double> getTotalPrices(ShoppingCart cartToCalculate) {
        double sumBeforeDiscounts;  //sum of original price
        double sumAfterDiscounts; //sum of the prices after discount
        //update the products in the bags with their discounts
        Map<Store, ShoppingBag> bagsToCalculate = cartToCalculate.getCopyOfShoppingBagsList();

        for (Store store : bagsToCalculate.keySet()) {  //apply the discounts on the bags in the cart (update the products prices)
            store.applyDiscountPolicies(bagsToCalculate.get(store).getProductListFromStore());
        }
        sumBeforeDiscounts = getCurrentTotalPrice(bagsToCalculate);
        sumAfterDiscounts = getOriginalTotalPrice(bagsToCalculate);
        return new Pair<>(sumBeforeDiscounts, sumAfterDiscounts);
    }

    /**
     * get the updated by discounts prices of the shopping bag.
     *
     * @param bagsToCalculate
     * @return
     */
    private double getCurrentTotalPrice(Map<Store, ShoppingBag> bagsToCalculate) {
        double totalPrice = 0;
        for (ShoppingBag bag : bagsToCalculate.values()) {
            totalPrice += bag.getOriginalTotalCostOfBag();
        }
        return totalPrice;
    }

    /**
     * get the shopping bag cost with original products prices
     *
     * @param bagsToCalculate
     * @return
     */
    private double getOriginalTotalPrice(Map<Store, ShoppingBag> bagsToCalculate) {
        double totalPrice = 0;
        for (ShoppingBag bag : bagsToCalculate.values()) {
            totalPrice += bag.getTotalCostOfBag();
        }
        return totalPrice;
    }


    public Set<UserSystem> getUsers(UserSystem user) {
        if (tradingSystemDao.isAdmin(user.getUserName())) {
            return tradingSystemDao.getUsers();
        }
        throw new NotAdministratorException(user.getUserName());
    }

    public List<String> getAllUsernameNotOwnerNotMangerNotWaiting(Store store) {
        Set<String> usernameOwners = store.getOwnersUsername();
        List<String> usernameMangers = store.getManagersStore().stream()
                .map(userSystem -> userSystem.getAppointedManager().getUserName())
                .collect(Collectors.toList());
        List<String> withOutOwnerManager = tradingSystemDao.getUsers().stream()
                .filter(userSystem -> usernameOwners.stream().noneMatch(user -> user.equals(userSystem.getUserName())) &&
                        usernameMangers.stream().noneMatch(user -> user.equals(userSystem.getUserName())))
                .map(UserSystem::getUserName)
                .collect(Collectors.toList());
        List<String> newUserOwners = store.getAppointingAgreements().stream()
                .map(appointingAgreement -> appointingAgreement.getNewOwner().getUserName())
                .collect(Collectors.toList());
        return withOutOwnerManager.stream()
                .filter(userName -> !newUserOwners.contains(userName))
                .collect(Collectors.toList());
    }

    public ShoppingCart getShoppingCart(String username, UUID uuid) {
        return tradingSystemDao.getShoppingCart(username, uuid);
    }

    public Set<DailyVisitor> getDailyVisitors(String username, RequestGetDailyVisitors requestGetDailyVisitors, UUID uuid) {
        Set<DailyVisitor> dailyVisitors = tradingSystemDao.getDailyVisitors(username, requestGetDailyVisitors, uuid);
        Date toDay = new Date();
        Optional<DailyVisitor> dailyVisitorToday = dailyVisitors.stream().filter(dailyVisitor ->
                DateUtils.isSameDay(toDay, dailyVisitor.getDate()))
                .findFirst();
        dailyVisitorToday.map(dailyVisitor -> subject.regDailyVisitor(username))
                .orElseGet(() -> subject.unRegDailyVisitor(username));
        return dailyVisitors;
    }

    public void stopDailyVisitors(String username, UUID uuid) {
        if (tradingSystemDao.isValidUuid(username, uuid) && tradingSystemDao.isAdmin(username)) {
            subject.unRegDailyVisitor(username);
        }
    }
}

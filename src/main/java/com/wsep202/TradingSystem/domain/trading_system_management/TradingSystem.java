package com.wsep202.TradingSystem.domain.trading_system_management;

import com.wsep202.TradingSystem.domain.exception.*;
import javafx.util.Pair;
import lombok.NonNull;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class TradingSystem {

    private ExternalServiceManagement externalServiceManagement;

    private Map<String, UUID> usersLogin;

    private TradingSystemDao tradingSystemDao;


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
     * @return true if the registration succeeded
     */
    @Synchronized
    public boolean registerNewUser(UserSystem userToRegister) {
        if (Objects.nonNull(userToRegister)
                && !tradingSystemDao.isRegistered(userToRegister)
                && userToRegister.isValidUser()) {
            encryptPassword(userToRegister); //encrypt user password to store it and its salt in the system
            log.info(String.format("The user %s registering", userToRegister.getUserName()));
            tradingSystemDao.addUserSystem(userToRegister);
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

///////////////////////////TODO BAR AND KSENIA ///////////////////////////////

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
    private List<Receipt> purchaseAndDeliver(PaymentDetails paymentDetails, ShoppingCart shoppingCart, BillingAddress billingAddress, String customerName) {
        if (shoppingCart == null || paymentDetails == null || billingAddress == null || shoppingCart.getNumOfBagsInCart() == 0 || shoppingCart.getShoppingBagsList() == null) {
            log.error("a store or payment details or billing address can't be null, cart can't be null or empty");
            return null;
        }
        List<Integer> listOfStoresWherePaymentPassed = makePurchase(shoppingCart, paymentDetails);
        if (listOfStoresWherePaymentPassed.isEmpty()) {
            log.error("no item was purchased from cart for user " + customerName);
            return null;
        }
        boolean isDelivered = deliver(shoppingCart, billingAddress, listOfStoresWherePaymentPassed);
        if (isDelivered) {//if the delivery was approved than make the receipts and send them to the customer
            updateProductAmount(listOfStoresWherePaymentPassed, shoppingCart);
            return makeReceipts(listOfStoresWherePaymentPassed, shoppingCart, customerName);
        }
        log.error("problem with delivery, cancel payment for " + customerName + " canceling purchase");
        cancelPayment(listOfStoresWherePaymentPassed, shoppingCart, paymentDetails);
        return null;
    }

    /**
     * This method is used to check which shopping can the user purchase,
     * after checking all the cart, the bags and the payment details sent to externalServiceManagement to make the purchase.
     *
     * @param shoppingCart   - the users personal shopping cart
     * @param paymentDetails - the users payment details
     * @return true if the purchase was successful, false if there where problem in the process of charging.
     */
    private List<Integer> makePurchase(ShoppingCart shoppingCart, PaymentDetails paymentDetails) {
        boolean canPurchaseShoppingBag;
        ShoppingCart bagsToPurchase = new ShoppingCart();
        for (Store store : shoppingCart.getShoppingBagsList().keySet()) {
            canPurchaseShoppingBag = true;
            ShoppingBag shoppingBag = shoppingCart.getShoppingBag(store);
            Map<Product, Integer> productList = shoppingBag.getProductListFromStore();
            for (Product product : productList.keySet()) { //check if there is enough products in store to make the purchase
                if (product.getAmount() < productList.get(product) || !product.getPurchaseType().type.equals("Buy immediately")) {
                    canPurchaseShoppingBag = false;
                    log.error("not enough '" + product.getName() + "' in stock, shopping bag from store '" + store.getStoreName() + "'" +
                            " will not be purchased");
                    break;
                }
                bagsToPurchase.addBagToCart(store, shoppingBag);
                log.info("transfer shopping bag from store '" + store.getStoreName() + "' to charging system");
            }

        }
        return externalServiceManagement.charge(paymentDetails, bagsToPurchase);
    }

    /**
     * This method is used to make all the receipts for the purchases that was made by the user
     * and make a delivery throw externalServiceManagement using the deliver method
     *
     * @param listOfStoreIds - the store ids that the payment in them was successful
     * @param shoppingCart   - the user shopping cart
     * @param customerName   - the name of the user, if it's a guest the name will be "Guest" if the user is registered
     *                       the name will be his userName.
     * @return a list of all of the receipts for all the purchases the user made
     */
    private List<Receipt> makeReceipts(List<Integer> listOfStoreIds, ShoppingCart shoppingCart, String customerName) {
        List<Receipt> cartReceiptList = new ArrayList<>();
//        for (int storeId : listOfStoreIds) { // make a receipt for each store purchase
//            Store receivedPaymentStore = stores.stream().filter(store -> store.getStoreId() == storeId).findFirst().get();
//            double totalPaymentToStore = shoppingCart.getShoppingBag(receivedPaymentStore).getTotalCostOfBag();
//            Receipt storeReceipt = new Receipt(storeId, customerName, totalPaymentToStore,
//                    shoppingCart.getShoppingBag(receivedPaymentStore).getProductListFromStore());
//            cartReceiptList.add(storeReceipt);
//            receivedPaymentStore.addReceipt(storeReceipt);
//            shoppingCart.removeBagFromCart(receivedPaymentStore, shoppingCart.getShoppingBag(receivedPaymentStore));
//        }
//        log.info("made a list of receipts for " + customerName + " on his purchase");
        return cartReceiptList;
    }

    /**
     * This method is used to make the delivery of the shopping bags that was purchased.
     * The delivery happens in externalServiceManagement class.
     *
     * @param shoppingCart   - the users personal shopping cart
     * @param billingAddress - the users address
     * @param listOfStoreIds - list of all store ids that the payment was successful
     * @return true if the delivery was successful, false if there were a problem
     */
    private boolean deliver(ShoppingCart shoppingCart, BillingAddress billingAddress, List<Integer> listOfStoreIds) {
        ShoppingCart deliveryShoppingCart = getPurchasedShoppingCart(listOfStoreIds, shoppingCart);
        return externalServiceManagement.deliver(billingAddress, deliveryShoppingCart);
    }

    /**
     * This method is used If the delivery fails, then the system needs to cancel the payment on the bags that
     * the user purchased and return the money.
     *
     * @param listOfStoreIds - list of store ids that the users made purchase in
     * @param shoppingCart   - the users shopping cart
     * @param paymentDetails - the users payment details
     * @return true when the payment was canceled
     */
    private boolean cancelPayment(List<Integer> listOfStoreIds, ShoppingCart shoppingCart, PaymentDetails paymentDetails) {
        ShoppingCart getCreditCart = getPurchasedShoppingCart(listOfStoreIds, shoppingCart);
        return externalServiceManagement.cancelCharge(paymentDetails, getCreditCart);
    }

    /**
     * This method is used to get the shopping bags that the user purchased.
     *
     * @param listOfStoreIds - list of store ids that the users made purchase in
     * @param shoppingCart   - the users shopping cart
     * @return shopping cart with the bags that was purchased
     */
    private ShoppingCart getPurchasedShoppingCart(List<Integer> listOfStoreIds, ShoppingCart shoppingCart) {
        ShoppingCart shoppingCartToReturn = new ShoppingCart(); //save only the shopping bags that needs to be delivered
//        for (int storeId : listOfStoreIds) {
//            Store storeToSend = stores.stream().filter(store -> store.getStoreId() == storeId).findFirst().get();
//            shoppingCartToReturn.addBagToCart(storeToSend, shoppingCart.getShoppingBag(storeToSend));
//        }
        return shoppingCartToReturn;
    }

    /**
     * This method is used after the purchase of the products.
     * The amount of the product in the store needs to be updated.
     *
     * @param listOfStoreIds - list of store ids that the users made purchase in
     * @param shoppingCart   - the users shopping cart
     */
    private void updateProductAmount(List<Integer> listOfStoreIds, ShoppingCart shoppingCart) {
        ShoppingCart shoppingCartToEdit = getPurchasedShoppingCart(listOfStoreIds, shoppingCart);
        for (Store store : shoppingCartToEdit.getShoppingBagsList().keySet()) {
            for (Product product : shoppingCartToEdit.getShoppingBag(store).getProductListFromStore().keySet()) {
                product.setAmount(product.getAmount() - shoppingCartToEdit.getShoppingBag(store).getProductAmount(product));
                log.info("amount of product '" + product.getName() + "' was updated");
            }
        }
    }

//////////////////////////////////////////////////////////////////////////////todo/////////////////////////////

    /**
     * This method is used to open a new store in the system
     *
     * @param user           - the user that wants to open the store
     * @param purchasePolicy -  a collection of purchase rules that the user has decided on for his store
     * @param discountPolicy -  a collection of discount rules that the user has decided on for his store
     * @param storeName      - the name of the store that the user decided
     * @return - false if the user is not registered, and true after the new store is added to store list
     */
    public Store openStore(UserSystem user,
                           PurchasePolicy purchasePolicy,
                           DiscountPolicy discountPolicy,
                           String storeName) {
        if (Objects.nonNull(user) &&
                Objects.nonNull(purchasePolicy) &&
                Objects.nonNull(discountPolicy) &&
                Strings.isNotBlank(storeName)) {
            Store newStore = new Store(user, purchasePolicy, discountPolicy, storeName);
            tradingSystemDao.addStore(newStore);
            user.addNewOwnedStore(newStore);
            log.info(String.format("A new store '%s' was opened in the system, %s is the owner", storeName, user.getUserName()));
            return newStore;
        }
        log.info(String.format("Was problem to open store '%s' in the system by %s", storeName, user.getUserName()));
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
        log.info(String.format("failed add user %s as manager in store '%d'", newManagerUser.getUserName(),ownedStore.getStoreId()));
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
        if(Objects.nonNull(ownedStore) && Objects.nonNull(ownerUser) && Objects.nonNull(newOwnerUser)
                && ownedStore.addOwner(ownerUser, newOwnerUser) ){
            log.info(String.format("user %s was added as manager in store '%d'", newOwnerUser.getUserName(),ownedStore.getStoreId()));
            return newOwnerUser.addNewOwnedStore(ownedStore);
        }
        log.info(String.format("failed add user %s as manager in store '%d'", newOwnerUser.getUserName(),ownedStore.getStoreId()));
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
        log.info(String.format("failed remove user %s as manager from store '%s'", managerStore.getUserName(),ownedStSore.getStoreId()));
        return  false;
    }
}
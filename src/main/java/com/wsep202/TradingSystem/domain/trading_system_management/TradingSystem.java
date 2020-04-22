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
        this.externalServiceManagement.connect();    //connect to the externals
        encryptPassword(admin);
        stores = new HashSet<>();
        users = new HashSet<>();
        administrators = new HashSet<>(Collections.singletonList(admin));
        externalServiceManagement.connect();
    }

    public TradingSystem(ExternalServiceManagement externalServiceManagement, Set<Store> stores,
                         @NotNull UserSystem admin) {
        this.externalServiceManagement = externalServiceManagement;
        this.externalServiceManagement.connect();    //connect to the externals
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
        log.info("The user "+ userToRegister.getUserName()+" got encrypted password and salt");
    }


    /**
     * register new user in the system
     * with its password
     * @param userToRegister the user we want to register
     * @return true if the registration succeeded
     */
    public boolean registerNewUser(UserSystem userToRegister) {
        boolean isRegistered = isRegistered(userToRegister, users);
        if(!allFieldsAreValidInUser(userToRegister)){ //check validity of fields of user
            return false;
        }
        log.info("user "+ userToRegister.getUserName() +" has valid fields");
        encryptPassword(userToRegister); //encrypt user password to store it and its salt in the system
        if (!isRegistered) {
            users.add(userToRegister);
            log.info("The user "+userToRegister.getUserName()+ " has been registered successfully.");
            return true;
        }
        log.warn("The user "+userToRegister.getUserName()+ " failed to register (user already exist).");
        return false;
    }

    /**
     * checks if all fields in the user are valid
     * @param user the user to check for its inputs
     * @return true if all fields are valid
     */
    private boolean allFieldsAreValidInUser(UserSystem user) {
        //verify the fields are not empty
        boolean isValid = true;
        if(user.getUserName()==null || user.getUserName().equals("")) {
            log.error("user name is empty or null");
            isValid = false;
        }
        else if(user.getPassword()==null || user.getPassword().equals("")) {
            log.error("user "+ user.getUserName() +"'s password is empty or null");
            isValid = false;
        }
        else if(user.getFirstName()==null || user.getFirstName().equals("")) {
            log.error("user "+ user.getUserName() +"'s first name is empty or null");
            isValid = false;
        }
        else if(user.getLastName()==null || user.getLastName().equals("")) {
            log.error("user "+ user.getUserName() +"'s last name is empty or null");
            isValid = false;
        }
        return isValid;
    }


    /**
     * for tests use only
     * clear users and stores list
     */
    public void clearDS(){
        this.users.clear();
        this.stores.clear();
    }

    /**
     * This method is used to make a login for a the user
     * @param userToLogin - the user that asks to login
     * @param isAdmin - if the user is an admin or not
     * @param password - the users password
     * @return true if the user logged-in, false if nots
     */
    public boolean login(UserSystem userToLogin, boolean isAdmin, String password) {
        if (userToLogin == null || password == null){
            log.error("user or password can't be null");
            return false;
        }
        if (isAdmin){
            return loginUser(userToLogin, password, administrators, true);
        }
        return loginUser(userToLogin, password, users, false);
    }


    /**
     * This method logs a user the system using method in externalServiceManagement that checks
     * if the password and the user details are correct.
     * @param userToLogin - the user to log-in
     * @param password - the users password
     * @param listOfUser - the list of registered users or admin users
     * @param isAdmin - if the user is an admin or not
     * @return true if the log-in was successful, false if there were a problem
     */
    private boolean loginUser(UserSystem userToLogin, String password, Set<UserSystem> listOfUser, boolean isAdmin){
        boolean isRegistered = isRegistered(userToLogin, listOfUser);
        if (!isRegistered){
            log.error("user "+ userToLogin.getUserName() +" is not a registered user");
            return false;
        }
        //verify that the user's password is correct as saved in our system
        if(!externalServiceManagement.isAuthenticatedUserPassword(password,userToLogin)){
            log.warn("The user "+userToLogin.getUserName()+" failed to login.");
            return false;
        }
        boolean suc = false;
        String statusLogin = "failed";
        if (isRegistered && !userToLogin.isLogin()) {
            userToLogin.login();
            suc = true;
            statusLogin = "succeeded";
        }
        if (isAdmin)
            log.info("The user "+userToLogin.getUserName()+" "+statusLogin+" to login as administrator.");
        else
            log.info("The user "+userToLogin.getUserName()+" "+statusLogin+" to login as regular user.");
        return suc;
    }

    /**
     * This method is used to check if the user is registered
     * @param userToLogin - the user that needs to be checked if he is registered
     * @param listOfUser - the list that needs to contain the user
     * @return true if the user exists, false if the user does not exists
     */
    private boolean isRegistered(UserSystem userToLogin, Set<UserSystem> listOfUser){
        if (listOfUser.size() == 0 || userToLogin == null){
            log.error("user can't be null or size of list greater than 0");
            return false;
        }
        return listOfUser.stream()
                .anyMatch(user -> user.getUserName().equals(userToLogin.getUserName()));
    }

    /**
     * get the user by its username from users list
     * @param username - user's user name to check
     * @return
     */
    private Optional<UserSystem> getUserOpt(String username) {
        return users.stream()
                .filter(user -> user.getUserName().equals(username))
                .findFirst();
    }

    /**
     * logout the user from the system
     * @param user - the user that asks to log out
     * @return true if logged out, otherwise false
     */
    public boolean logout(UserSystem user) {
        if (user == null){
            log.error("user cant be null");
            return false;
        }
        if(user.isLogin()){
            user.logout();
            log.info("The user "+user.getUserName()+" logged out successfully.");
            return true;
        }
        log.warn("The user "+user.getUserName()+ " failed to logout");
        return false;
    }

    /**
     * returns the User system object match the received string in case its an admin user
     * otherwise throw exception
     * @param administratorUsername - admins user name
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
            log.info("Admin "+ administratorUsername +" exists --> calls to 'getStore(storeId)' function.");
            return getStore(storeId);
        }
        log.error("Admin "+ administratorUsername +" isn't exist --> throws 'NotAdministratorException(administratorUsername)' exception!");
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
        if (userOpt == null){
            log.error("User: " + username + " isn't exist in the Trading System.");
            return userOpt.orElseThrow(() -> new UserDontExistInTheSystemException(username));
        }
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
            log.info("Admin "+ administratorUsername +" exists --> calls to 'getUser(userName)' function.");
            return getUser(userName);
        }
        log.error("Admin "+ administratorUsername +" isn't exist --> throws 'NotAdministratorException(administratorUsername)' exception!");
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
     * This method is used to purchase all the products that the unregistered user added to his cart.
     * @param shoppingCart - the users personal shopping cart
     * @param paymentDetails - the user credit card number & expiration date
     * @return list of receipts for stores where payment has been made
     */
    public List<Receipt> purchaseShoppingCart(ShoppingCart shoppingCart, PaymentDetails paymentDetails, BillingAddress billingAddress) {
        return purchaseAndDeliver(paymentDetails, shoppingCart, billingAddress, "Guest");
    }


    /**
     * This method is used to purchase all the products that the registered user added to his cart.
     * @param paymentDetails - the user credit card number & expiration date
     * @param user - the user that made
     * @return list of receipts for stores where payment has been made
     */
    public List<Receipt> purchaseShoppingCart(PaymentDetails paymentDetails,BillingAddress billingAddress, UserSystem user) {
        if (user == null){
            log.error("user can't be null");
            return null;
        }
        return purchaseAndDeliver(paymentDetails, user.getShoppingCart(), billingAddress, user.getUserName());
    }

    /**
     * This method makes the payment in the externalServiceManagement using the charge method.
     * @param paymentDetails - the users credit card details
     * @param shoppingCart - the user personal shopping cart
     * @param billingAddress - the delivery address of the user
     * @return a list of receipts for all of the purchases the user made
     */
    private List<Receipt> purchaseAndDeliver(PaymentDetails paymentDetails, ShoppingCart shoppingCart, BillingAddress billingAddress, String customerName){
        if(shoppingCart == null || paymentDetails == null || billingAddress == null || shoppingCart.getNumOfBagsInCart() == 0 || shoppingCart.getShoppingBagsList() == null){
            log.error("a store or payment details or billing address can't be null, cart can't be null or empty");
            return null;
        }
        List<Integer> listOfStoresWherePaymentPassed = makePurchase(shoppingCart, paymentDetails);
        if (listOfStoresWherePaymentPassed.isEmpty()){
            log.error("no item was purchased from cart for user "+ customerName);
            return null;
        }
        boolean isDelivered = deliver(shoppingCart, billingAddress, listOfStoresWherePaymentPassed);
        if (isDelivered) {//if the delivery was approved than make the receipts and send them to the customer
            updateProductAmount(listOfStoresWherePaymentPassed, shoppingCart);
            return makeReceipts(listOfStoresWherePaymentPassed, shoppingCart, customerName);
        }
        log.error("problem with delivery, cancel payment for "+ customerName + " canceling purchase");
        cancelPayment(listOfStoresWherePaymentPassed, shoppingCart, paymentDetails);
        return null;
    }

    /**
     * This method is used to check which shopping can the user purchase,
     * after checking all the cart, the bags and the payment details sent to externalServiceManagement to make the purchase.
     * @param shoppingCart - the users personal shopping cart
     * @param paymentDetails - the users payment details
     * @return true if the purchase was successful, false if there where problem in the process of charging.
     */
    private List<Integer> makePurchase(ShoppingCart shoppingCart, PaymentDetails paymentDetails){
        boolean canPurchaseShoppingBag;
        ShoppingCart bagsToPurchase = new ShoppingCart();
        for (Store store: shoppingCart.getShoppingBagsList().keySet()) {
            canPurchaseShoppingBag = true;
            ShoppingBag shoppingBag = shoppingCart.getShoppingBag(store);
            Map<Product,Integer> productList = shoppingBag.getProductListFromStore();
            for (Product product: productList.keySet()){ //check if there is enough products in store to make the purchase
                if(product.getAmount() < productList.get(product) || !product.getPurchaseType().type.equals("Buy immediately")) {
                    canPurchaseShoppingBag = false;
                    log.error("not enough '"+ product.getName() +"' in stock, shopping bag from store '"+ store.getStoreName() +"'" +
                            " will not be purchased");
                    break;
                }
                bagsToPurchase.addBagToCart(store, shoppingBag);
                log.info("transfer shopping bag from store '"+ store.getStoreName() +"' to charging system");
            }

        }
        return externalServiceManagement.charge(paymentDetails, bagsToPurchase);
    }

    /**
     * This method is used to make all the receipts for the purchases that was made by the user
     * and make a delivery throw externalServiceManagement using the deliver method
     * @param listOfStoreIds - the store ids that the payment in them was successful
     * @param shoppingCart - the user shopping cart
     * @param customerName - the name of the user, if it's a guest the name will be "Guest" if the user is registered
     *                 the name will be his userName.
     * @return a list of all of the receipts for all the purchases the user made
     */
    private List<Receipt> makeReceipts(List<Integer> listOfStoreIds, ShoppingCart shoppingCart, String customerName){
        List<Receipt> cartReceiptList = new ArrayList<>();
        for (int storeId: listOfStoreIds) { // make a receipt for each store purchase
            Store receivedPaymentStore = stores.stream().filter(store -> store.getStoreId() == storeId).findFirst().get();
            double totalPaymentToStore = shoppingCart.getShoppingBag(receivedPaymentStore).getTotalCostOfBag();
            Receipt storeReceipt = new Receipt(storeId, customerName, totalPaymentToStore ,
                    shoppingCart.getShoppingBag(receivedPaymentStore).getProductListFromStore());
            cartReceiptList.add(storeReceipt);
            shoppingCart.removeBagFromCart(receivedPaymentStore,shoppingCart.getShoppingBag(receivedPaymentStore));
        }
        log.info("made a list of receipts for " + customerName + " on his purchase");
        return cartReceiptList;
    }

    /**
     * This method is used to make the delivery of the shopping bags that was purchased.
     * The delivery happens in externalServiceManagement class.
     * @param shoppingCart - the users personal shopping cart
     * @param billingAddress - the users address
     * @param listOfStoreIds - list of all store ids that the payment was successful
     * @return true if the delivery was successful, false if there were a problem
     */
    private boolean deliver(ShoppingCart shoppingCart, BillingAddress billingAddress, List<Integer> listOfStoreIds){
        ShoppingCart deliveryShoppingCart = getPurchasedShoppingCart(listOfStoreIds, shoppingCart);
        return externalServiceManagement.deliver(billingAddress, deliveryShoppingCart);
    }

    /**
     * This method is used If the delivery fails, then the system needs to cancel the payment on the bags that
     * the user purchased and return the money.
     * @param listOfStoreIds - list of store ids that the users made purchase in
     * @param shoppingCart - the users shopping cart
     * @param paymentDetails - the users payment details
     * @return true when the payment was canceled
     */
    private boolean cancelPayment(List<Integer> listOfStoreIds, ShoppingCart shoppingCart, PaymentDetails paymentDetails){
        ShoppingCart getCreditCart = getPurchasedShoppingCart(listOfStoreIds, shoppingCart);
        return externalServiceManagement.cancelCharge(paymentDetails, getCreditCart);
    }

    /**
     * This method is used to get the shopping bags that the user purchased.
     * @param listOfStoreIds - list of store ids that the users made purchase in
     * @param shoppingCart - the users shopping cart
     * @return shopping cart with the bags that was purchased
     */
    private ShoppingCart getPurchasedShoppingCart(List<Integer> listOfStoreIds, ShoppingCart shoppingCart){
        ShoppingCart shoppingCartToReturn = new ShoppingCart(); //save only the shopping bags that needs to be delivered
        for (int storeId: listOfStoreIds) {
            Store storeToSend = stores.stream().filter(store -> store.getStoreId() == storeId).findFirst().get();
            shoppingCartToReturn.addBagToCart(storeToSend, shoppingCart.getShoppingBag(storeToSend));
        }
        return shoppingCartToReturn;
    }

    /**
     * This method is used after the purchase of the products.
     * The amount of the product in the store needs to be updated.
     * @param listOfStoreIds - list of store ids that the users made purchase in
     * @param shoppingCart - the users shopping cart
     */
    private void updateProductAmount(List<Integer> listOfStoreIds, ShoppingCart shoppingCart){
        ShoppingCart shoppingCartToEdit = getPurchasedShoppingCart(listOfStoreIds, shoppingCart);
        for (Store store: shoppingCartToEdit.getShoppingBagsList().keySet()) {
            for (Product product: shoppingCartToEdit.getShoppingBag(store).getProductListFromStore().keySet()) {
                product.setAmount(product.getAmount()-shoppingCartToEdit.getShoppingBag(store).getProductAmount(product));
                log.info("amount of product '"+ product.getName() +"' was updated");
            }
        }
    }


    /**
     * This method is used to open a new store in the system
     * @param user - the user that wants to open the store
     * @param purchasePolicy -  a collection of purchase rules that the user has decided on for his store
     * @param discountPolicy -  a collection of discount rules that the user has decided on for his store
     * @param storeName - the name of the store that the user decided
     * @return - false if the user is not registered, and true after the new store is added to store list
     */
    public boolean openStore(UserSystem user, PurchasePolicy purchasePolicy,DiscountPolicy discountPolicy, String storeName) {
        if (user == null || purchasePolicy == null || discountPolicy == null || storeName == null || storeName.equals("")){
            log.error("One of the parameters received is equal to null ore store name empty");
            return false;
        }

        if (!isRegistered(user,users)) {//if the user is not registered to the system, he can't open a store
            log.error("A non registered user "+ user.getUserName() +" tried to open a store '"+ storeName +"'");
            return false;
        }
        boolean isStoreExists = isStoreExists(purchasePolicy, discountPolicy, storeName);
        if (!isStoreExists) {
            Store newStore = new Store(user, purchasePolicy, discountPolicy, storeName);
            this.stores.add(newStore);
            newStore.setDiscountType(DiscountType.NONE);
            newStore.setPurchaseType(PurchaseType.BUY_IMMEDIATELY);
            user.addNewOwnedStore(newStore);
            log.info("A new store '" + storeName + "' was opened in the system, "+ user.getUserName() +" is the owner");
            return true;
        }
        return false;
    }

    /**
     * This method is used to check if the store is already opened in the system,
     * used only in when open store.
     * @param purchasePolicy -  a collection of purchase rules that the user has decided on for his store
     * @param discountPolicy -  a collection of discount rules that the user has decided on for his store
     * @param storeName - the name of the store that the user decided
     * @return false if the store does not exists in the system, else true.
     */
    private boolean isStoreExists(PurchasePolicy purchasePolicy,DiscountPolicy discountPolicy, String storeName){
        if (stores.size() > 0) {// if the size of stores is 0, than the store does exists in system
            Optional<Store> storeOptional = stores.stream().filter(store1 -> store1.getStoreName().equals(storeName)).findFirst();
            if(storeOptional.isPresent()) {
                Store store = storeOptional.get();
                if (store != null && store.getStoreName().equals(storeName) && store.getPurchasePolicy().equals(purchasePolicy)
                        && store.getDiscountPolicy().equals(discountPolicy)) {
                    log.error("this store already exists, can't open it again");
                    return true;
                }
            }
            else
                return false;
        }
        return false;
    }

    /**
     * This method is used to check if a store exists
     * @param storeToCheck - the store that needs to be check
     * @return true if the store exists
     */
    private boolean isStoreExists(Store storeToCheck){
        if (storeToCheck == null || stores.size() == 0){
            log.error("store can't be null or store list needs to have at least 1 store");
            return false;
        }
        return stores.stream()
                .anyMatch(store -> store.getStoreId() == storeToCheck.getStoreId());
    }

    public Set<Store> getStoresList(){
        return this.stores;
    }

    public Set<UserSystem> getUsers(){
        return this.users;
    }

    public Set<UserSystem> getAdministrators(){
        return this.administrators;
    }

    public void setUsersList(Set<UserSystem> users){
        this.users = users;
    }

    public void insertStoreToStores(Store store){
        stores.add(store);
    }

    /**
     * This method is used to add a new manager to an existing store
     * @param ownedStore - The store to which you want to add a manager
     * @param ownerUser - owner of the store
     * @param newManagerUser - the user that needs to be added as a manager
     * @return true if the addition was successful, false if there were a problem
     */
    public boolean addMangerToStore(Store ownedStore, UserSystem ownerUser, UserSystem newManagerUser) {
        boolean addToUser = false;
        if(ownedStore == null || ownerUser == null || newManagerUser == null) { // error
            log.error("store or users can't be null");
            return false;
        }
        else{
            boolean addToStore = ownedStore.addManager(ownerUser, newManagerUser);
            if (addToStore) { // add to store succeed
                addToUser = newManagerUser.addNewManageStore(ownedStore);
                if(addToUser) // add to user succeed
                    log.info("user "+ newManagerUser.getUserName() +" was added as manager in store '"+ ownedStore.getStoreName() +"'");
                    return true;
            }
        }
        return false;
    }

    /**
     * This method is used to add a new owner to an existing store
     * @param ownerStore - The store to which you want to add a manager
     * @param ownerUser - owner of the store
     * @param newOwnerUser - the user that needs to be added as an owner
     * @return true if the addition was successful, false if there were a problem
     */
    public boolean addOwnerToStore(Store ownerStore, UserSystem ownerUser, UserSystem newOwnerUser) {
        boolean addToUser = false;
        if(ownerStore == null || ownerUser == null || newOwnerUser == null) { // error
            log.error("store or users can't be null");
            return false;
        }
        else{
            boolean addToStore = ownerStore.addOwner(ownerUser, newOwnerUser);
            if (addToStore) { // add to store succeed
                addToUser = newOwnerUser.addNewOwnedStore(ownerStore);
                if(addToUser) { // add to user succeed
                    log.info("user "+ newOwnerUser.getUserName() +" was added as manager in store '"+ ownerStore.getStoreName() +"'");
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * remove manager from the store
     * @param ownedStore - the store
     * @param ownerUser - the owner of the store that want remove the manager
     * @param managerStore - the manager that want to remove
     * @return true if manager was removed, else false
     */
    public boolean removeManager(Store ownedStore, UserSystem ownerUser, UserSystem managerStore) {
       if (ownedStore == null || ownerUser == null || managerStore == null){
           log.error("user or store can't be null");
           return false;
       }
       if (isRegistered(ownerUser,users) && isRegistered(managerStore, users)){
            if (isStoreExists(ownedStore)){
                boolean isRemoved = ownedStore.removeManager(ownerUser, managerStore);
                if (isRemoved){
                    log.info("user "+ managerStore.getUserName() +" from store '"+ ownedStore.getStoreName() +"");
                    return true;
                }
            }
            log.error("store '"+ ownedStore.getStoreName() +"' does not exists in system");
            return false;
       }
        log.error("user "+ ownerUser.getUserName() +" or user "+ managerStore.getUserName() +" is not registered");
       return false;
    }
}
package com.wsep202.TradingSystem.domain.trading_system_management;

import com.google.common.base.Strings;
import com.wsep202.TradingSystem.domain.exception.*;
import com.wsep202.TradingSystem.domain.trading_system_management.notification.Notification;
import com.wsep202.TradingSystem.domain.trading_system_management.notification.Subject;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import com.wsep202.TradingSystem.domain.trading_system_management.notification.Observer;

import java.util.*;

/**
 * define user in the system
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
@Builder
public class UserSystem implements Observer {

    /**
     * the user name
     */
    private String userName;
    /**
     * the encryption password of the the user
     */
    private String password;
    /**
     * the salt we use to hash the password for the user
     */
    private String salt;
    /**
     * the first name of the user
     */
    private String firstName;
    /**
     * the last name of the user
     */
    private String lastName;
    /**
     * The stores that the user manages
     */
    @Builder.Default
    private Set<Store> managedStores = new HashSet<>();
    /**
     * The stores that the user own
     */
    @Builder.Default
    private Set<Store> ownedStores = new HashSet<>();
    /**
     * The user personal shopping cart
     */
    @Builder.Default
    private ShoppingCart shoppingCart = new ShoppingCart();
    /**
     * Show the stage of the user, logged-in or logged-out
     */
    @Builder.Default
    private boolean isLogin = false;
    /**
     * The users personal receipts list
     */
    @Builder.Default
    private List<Receipt> receipts = new LinkedList<>();

    /**
     * for notification
     */
    @Builder.Default
    private List<Notification> notifications = new LinkedList<>();

    //need ignore in Db;
    private Subject subject;

    //need ignore in Db;
    private String principal;

    private String imageUrl;

    public UserSystem(String userName, String firstName, String lastName, String password) {
        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.notifications = new LinkedList<>();
        this.shoppingCart = new ShoppingCart();
        this.ownedStores = new HashSet<>();
        managedStores = new HashSet<>();
    }

    /**
     * This method is used to add a new owned store to the user
     *
     * @param store - the store that needs to be added
     * @return the answer that addNewOwnedOrManageStore method returns
     */
    public boolean addNewOwnedStore(Store store) {
        return addNewOwnedOrManageStore(store, ownedStores);
    }

    /**
     * This method is used to add a new managed store to the user
     *
     * @param store - the store that needs to be added
     * @return the answer that addNewOwnedOrManageStore method returns
     */
    public boolean addNewManageStore(Store store) {
        return addNewOwnedOrManageStore(store, managedStores);
    }

    /**
     * This method is used to add a store to the relevant list (owned OR managed)
     *
     * @param storeToAdd   - the store that needs to be added
     * @param listOfStores - the list that store is going to be added
     * @return true if the addition was successful, false if the store is null or the store is already in the list
     */
    private boolean addNewOwnedOrManageStore(Store storeToAdd, Set<Store> listOfStores) {
        if (storeToAdd == null) {
            log.error("can't add a null store to user");
            return false;
        }
        if (!listOfStores.contains(storeToAdd)) {
            log.info("added a new owned OR managed store '" + storeToAdd.getStoreName() + "' to the user '" + userName + "'");
            listOfStores.add(storeToAdd);
            return true;
        }
        log.error("can't add an existing store '" + storeToAdd.getStoreName() + "'");
        return false;
    }

    /**
     * This method is used to remove a store that is under a users management
     *
     * @param storeToRemove - the store that needs to be removed
     * @return true if the store exists in managedStores, false if not or null
     */
    public boolean removeManagedStore(Store storeToRemove) {
        if (storeToRemove == null) {
            log.error("can't remove a null store");
            return false;
        }
        if (!managedStores.contains(storeToRemove)) {
            log.error("store '" + storeToRemove.getStoreName() + "' is not managed by this user '" + userName + "'");
            return false;
        }
        managedStores.remove(storeToRemove);
        log.info("store '" + storeToRemove.getStoreName() + "' was removed from managed store list");
        return true;
    }

    /**
     * This method is used to change the stage of the user to logged-in
     */
    public void login() {
        if(!notifications.isEmpty()){
            subject.update(this);
        }
        isLogin = true;
    }

    /**
     * This method is used to change the stage of the user to logged-out
     *
     * @return always true, because the user is now logged-out
     */
    public boolean logout() {
        if(Objects.nonNull(subject)) {
            subject.unregister(this);
        }
        isLogin = false;
        return true;
    }

    /**
     * This method is used to find if this user is a owner of a certain store.
     *
     * @param storeId - the id of the store
     * @return the store if the user is an owner, exception if he's not
     */
    public Store getOwnerStore(int storeId) {
        return ownedStores.stream()
                .filter(store -> store.getStoreId() == storeId)
                .findFirst().orElseThrow(() -> new NoOwnerInStoreException(userName, storeId));
    }

    public Store getOwnerOrManagerStore(int storeId) {
        Optional<Store> ownerStore = ownedStores.stream()
                .filter(store -> store.getStoreId() == storeId)
                .findFirst();
        return ownerStore.isPresent() ? ownerStore.get() :
                managedStores.stream()
                        .filter(store -> store.getStoreId() == storeId && store.managerCanEdit(userName))
                        .findFirst().orElseThrow(() -> new TradingSystemException(
                        String.format("The user %s is not manager with edit permission or owner of store %d",userName, storeId)));
    }

    /**
     * This method is used to find if this user is a manager of a certain store.
     *
     * @param storeId - the id of the store
     * @return the store if the user is a manager, exception if he's not
     */
    public Store getManagerStore(int storeId) {
        return managedStores.stream()
                .filter(store -> store.getStoreId() == storeId)
                .findFirst().orElseThrow(() -> new NoManagerInStoreException(userName, storeId));
    }

    /**
     * This method is used to add a product to the users cart.
     * The method use the methods getShoppingBag & addBagToCart in ShoppingCart and addProductToBag in ShoppingBag.
     *
     * @param storeOfProduct  - the store of the product that needs to be added
     * @param productToAdd    - the product that needs to be added
     * @param amountOfProduct - the amount of the product
     * @return true if the addition was successful, false if there were a problem to add it
     */
    public boolean saveProductInShoppingBag(Store storeOfProduct, Product productToAdd, int amountOfProduct) {
        if (shoppingCart.getShoppingBag(storeOfProduct) == null) {
            ShoppingBag storeShoppingBag = new ShoppingBag(storeOfProduct);
            boolean isAdded = storeShoppingBag.addProductToBag(productToAdd, amountOfProduct);
            log.info("add the product '" + productToAdd.getName() + "' to a new shopping bug");
            if (isAdded)
                return shoppingCart.addBagToCart(storeOfProduct, storeShoppingBag);
            return false;
        }
        log.info("add the product '" + productToAdd.getName() + "' to an existing shopping bug");
        return shoppingCart.getShoppingBag(storeOfProduct).addProductToBag(productToAdd, amountOfProduct);
    }

    /**
     * This method is used to remove a product from the cart.
     * The method use the methods getShoppingBag in ShoppingCart and removeProductFromBag in ShoppingBag
     *
     * @param storeOfProduct  - the store of the product that needs to be removed
     * @param productToRemove - the product that needs to be removed
     * @return true if the removal was successful, false if there were a problem to remove it.
     */
    public boolean removeProductInShoppingBag(Store storeOfProduct, Product productToRemove) {
        if (shoppingCart.getShoppingBag(storeOfProduct) == null) {
            log.error("the product '" + productToRemove.getName() + "' is not in cart");
            return false;
        }
        boolean isProductRemoved = shoppingCart.removeProductInCart(storeOfProduct, productToRemove);
        if (isProductRemoved) {
            log.info("product '" + productToRemove.getName() + "' was removed");
            return true;
        }
        log.error("product '" + productToRemove.getName() + "' was not removed");
        return false;
    }

    public boolean isValidUser() {
        return !Strings.isNullOrEmpty(userName) &&
                !Strings.isNullOrEmpty(password) &&
                !Strings.isNullOrEmpty(firstName) &&
                !Strings.isNullOrEmpty(lastName);
    }

    @Override
    public void newNotification(Notification notification) {
        this.notifications.add(notification);
        subject.update(this);
    }

    @Override
    public void connectNotificationSystem(Subject subject, String principal) {
        setSubject(subject);
        subject.register(this);
        if(!notifications.isEmpty()){
            subject.update(this);
        }
    }

    @Override
    public List<Notification> getNotifications() {
        List<Notification> notifications = this.notifications;
        this.notifications = new LinkedList<>();
        notifications.forEach(notification -> notification.setPrincipal(principal));
        return notifications;
    }

    public boolean isOwner(int storeId) {
        return ownedStores.stream()
                .anyMatch(store -> store.getStoreId() == storeId);

    }

    public boolean changeProductAmountInShoppingBag(int storeId,int amount, int productSn) {
        return shoppingCart.changeProductAmountInShoppingBag(storeId, amount, productSn);
    }

    /**
     * This Method is used to add the receipts to the users receipt list
     * @param receipts - new receipt for new purchase
     */
    public void addReceipts(List<Receipt> receipts){
        if (receipts != null) {
            for (Receipt rep : receipts) {
                if (!this.receipts.contains(rep) && rep != null)
                    this.receipts.add(rep);
            }
        }
    }
}

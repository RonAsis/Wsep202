package com.wsep202.TradingSystem.domain.trading_system_management;

import com.google.common.base.Strings;
import com.wsep202.TradingSystem.domain.exception.*;
import com.wsep202.TradingSystem.domain.trading_system_management.notification.Notification;
import com.wsep202.TradingSystem.domain.trading_system_management.notification.Subject;
import com.wsep202.TradingSystem.domain.trading_system_management.ownerStore.OwnerToApprove;
import com.wsep202.TradingSystem.domain.trading_system_management.statistics.DailyVisitorsField;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import com.wsep202.TradingSystem.domain.trading_system_management.notification.Observer;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

import static javax.persistence.CascadeType.DETACH;

/**
 * define user in the system
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
@Builder
@Entity
public class UserSystem implements Observer, Serializable {

    /**
     * the user name
     */
    @Id
    private String userName;
    /**
     * the encryption password of the the user
     */
    @Lob
    private String password;
    /**
     * the salt we use to hash the password for the user
     */
    @Lob
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
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Store> managedStores = new HashSet<>();
    /**
     * The stores that the user own
     */
    @Builder.Default
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Store> ownedStores = new HashSet<>();
    /**
     * The user personal shopping cart
     */
    @Builder.Default
    @OneToOne(fetch = FetchType.EAGER,
            cascade = {CascadeType.MERGE,
                    CascadeType.REMOVE,
                    CascadeType.REFRESH,
                    DETACH}, orphanRemoval = true)
    private ShoppingCart shoppingCart = new ShoppingCart();

    /**
     * The users personal receipts list
     */
    @Builder.Default
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Receipt> receipts = new HashSet<>();

    /**
     * for notification
     */
    @Builder.Default
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Notification> notifications = new HashSet<>();

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<OwnerToApprove> ownerToApproves;

    private String principal;

    private boolean isAdmin;

    private String imageUrl;

    /**
     * use for DB
     */
    public UserSystem(String userName, String firstName, String lastName, String password, boolean isAdmin) {
        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.isAdmin = isAdmin;
        this.notifications = new HashSet<>();
        this.shoppingCart = new ShoppingCart();
        this.ownedStores = new HashSet<>();
        managedStores = new HashSet<>();
    }

    /**
     * use for new user
     */
    public UserSystem(String userName, String firstName, String lastName, String password) {
        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.isAdmin = false;
        this.notifications = new HashSet<>();
        this.shoppingCart = new ShoppingCart();
        this.ownedStores = new HashSet<>();
        managedStores = new HashSet<>();
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
        boolean response = false;
        if (Objects.nonNull(storeToAdd) && !listOfStores.contains(storeToAdd)) {
            log.info("added a new owned OR managed store '" + storeToAdd.getStoreName() + "' to the user '" + userName + "'");
            listOfStores.add(storeToAdd);
            response = true;
        }
        return response;
    }

    /**
     * This method is used to remove a store that is under a users management
     *
     * @param storeToRemove - the store that needs to be removed
     * @return true if the store exists in managedStores, false if not or null
     */
    public boolean removeManagedStore(Store storeToRemove) {
        boolean response = false;
        if (Objects.nonNull(storeToRemove) && managedStores.contains(storeToRemove)) {
            managedStores.remove(storeToRemove);
            log.info("store '" + storeToRemove.getStoreName() + "' was removed from managed store list");
            response = true;
        }
        return response;
    }

    /**
     * This method is used to remove a store that is under a users management
     *
     * @return true if the store exists in managedStores, false if not or null
     */
    public boolean removeManagedStore(int storeId) {
        managedStores.stream()
                .filter(managedStore -> managedStore.getStoreId() == storeId)
                .map(managedStore -> managedStores.remove(managedStore));
        return true;
    }

    /**
     * This method is used to remove a store that is under a users ownership
     *
     * @param storeToRemove - the store that needs to be removed
     * @return true if the store exists in ownedStores, false if not or null
     */
    public boolean removeOwnedStore(Store storeToRemove) {
        boolean response = false;
        if (Objects.nonNull(storeToRemove) && !ownedStores.contains(storeToRemove)) {
            ownedStores.remove(storeToRemove);
            log.info("store '" + storeToRemove.getStoreName() + "' was removed from owned store list");
            response = true;
        }
        return response;
    }

    /**
     * This method is used to change the stage of the user to logged-in
     */
    public void login() {
        if (!notifications.isEmpty()) {
            TradingSystem.getSubject().update(this);
        }
    }

    /**
     * This method is used to change the stage of the user to logged-out
     *
     * @return always true, because the user is now logged-out
     */
    public boolean logout() {
        TradingSystem.getSubject().unregister(this);
        return true;
    }

    public Store getOwnerOrManagerWithPermission(int storeId, StorePermission permission) {
        Optional<Store> ownerStore = getStoreOfOwner(storeId);
        return ownerStore.orElseGet(() -> getStoreOfManagerWithPermission(storeId, permission));
    }

    private Store getStoreOfManagerWithPermission(int storeId, StorePermission permission) {
        return managedStores.stream()
                .filter(store -> store.getStoreId() == storeId && store.managerHavePermission(userName, permission))
                .findFirst().orElseThrow(() -> new TradingSystemException(
                        String.format("The user %s is not manager with %s permission or owner of store %d", userName, permission.function, storeId)));
    }

    private Optional<Store> getStoreOfOwner(int storeId) {
        return ownedStores.stream()
                .filter(store -> store.getStoreId() == storeId)
                .findFirst();
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
        return shoppingCart.addProductToCart(storeOfProduct, productToAdd, amountOfProduct);
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
        return shoppingCart.removeProductInCart(storeOfProduct, productToRemove);
    }

    public boolean isValidUser() {
        return !Strings.isNullOrEmpty(userName) &&
                !Strings.isNullOrEmpty(password) &&
                !Strings.isNullOrEmpty(firstName) &&
                !Strings.isNullOrEmpty(lastName);
    }

    @Override
    public void newNotification(Notification notification) {
        notification.setPrincipal(principal);
        this.notifications.add(notification);
        TradingSystem.getSubject().update(this);
    }

    @Override
    public void connectNotificationSystem( String principal) {
        TradingSystem.getSubject().register(this);
        this.principal = principal;
        if (!notifications.isEmpty()) {
            TradingSystem.getSubject().update(this);
        }
    }

    @Override
    public List<Notification> getNotifications() {
        notifications.forEach(notification -> notification.setPrincipal(principal));
        return new LinkedList<>(notifications);
    }

    public boolean isOwner(int storeId) {
        return ownedStores.stream()
                .anyMatch(store -> store.getStoreId() == storeId);
    }

    /**
     * change the amount of product in the bag
     */
    public boolean changeProductAmountInShoppingBag(int storeId, int amount, int productSn) {
        return shoppingCart.changeProductAmountInShoppingBag(storeId, amount, productSn);
    }

    /**
     * This Method is used to add the receipts to the users receipt list
     *
     * @param receipts - new receipt for new purchase
     */
    public void addReceipts(List<Receipt> receipts) {
        if (Objects.nonNull(receipts)) {
            this.receipts.addAll(receipts);
        }
    }

    public ShoppingCart getShoppingCart() {
        shoppingCart.applyDiscountPolicies();
        return shoppingCart;
    }

    public void addOwnerToApprove(int storeId, String storeName, String userName) {
        OwnerToApprove ownerToApprove = new OwnerToApprove(storeId, storeName, userName);
        ownerToApproves.add(ownerToApprove);
        newNotification(Notification
                .builder()
                .content(String.format("You have a new Owner to approve in storeId %s", storeId))
                .build());
    }

    public void removeAgreement(int storeId, String userName) {
        ownerToApproves.stream()
                .filter(ownerToApprove -> ownerToApprove.getStoreId() == storeId && ownerToApprove.getUsernameToApprove().equals(userName))
                .findFirst()
                .map(ownerToApprove -> ownerToApproves.remove(ownerToApprove));
    }

    public void updateDaily(TradingSystemDao tradingSystemDao) {
        if(isAdmin()){ // admin
            tradingSystemDao.updateDailyVisitors(DailyVisitorsField.ADMIN);
        }
        if(ownedStores.isEmpty() && managedStores.isEmpty()){ // not owner not manger
            tradingSystemDao.updateDailyVisitors(DailyVisitorsField.SIMPLE_USER);
        }
        if(!managedStores.isEmpty() && ownedStores.isEmpty()){ // manager and not owner
            tradingSystemDao.updateDailyVisitors(DailyVisitorsField.MANAGER_STORES);
        }
        if(ownedStores.isEmpty()){ // owner
            tradingSystemDao.updateDailyVisitors(DailyVisitorsField.OWNERS_STORES);
        }
    }
}


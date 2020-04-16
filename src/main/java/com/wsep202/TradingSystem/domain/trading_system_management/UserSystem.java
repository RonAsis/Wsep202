package com.wsep202.TradingSystem.domain.trading_system_management;

import com.wsep202.TradingSystem.domain.exception.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * define user in the system
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class UserSystem {

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
    private Set<Store> managedStores;
    /**
     * The stores that the user own
     */
    private Set<Store> ownedStores;
    /**
     * The user personal shopping cart
     */
    private ShoppingCart shoppingCart;
    /**
     * Show the stage of the user, logged-in or logged-out
     */
    private boolean isLogin = false;
    /**
     * The users personal receipts list
     */
    private List<Receipt> receipts;

    public UserSystem(String userName, String firstName, String lastName, String password){
        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        managedStores = new HashSet<>();
        ownedStores = new HashSet<>();
        shoppingCart = new ShoppingCart();
        receipts = new LinkedList<>();
    }

    /**
     * This method is used to add a new owned store to the user
     * @param store - the store that needs to be added
     * @return the answer that addNewOwnedOrManageStore method returns
     */
    public boolean addNewOwnedStore(Store store) {
        return addNewOwnedOrManageStore(store, ownedStores);
    }

    /**
     * This method is used to add a new managed store to the user
     * @param store - the store that needs to be added
     * @return the answer that addNewOwnedOrManageStore method returns
     */
    public boolean addNewManageStore(Store store){
        return addNewOwnedOrManageStore(store, managedStores);
    }

    /**
     * This method is used to add a store to the relevant list (owned OR managed)
     * @param storeToAdd - the store that needs to be added
     * @param listOfStores - the list that store is going to be added
     * @return true if the addition was successful, false if the store is null or the store is already in the list
     */
    private boolean addNewOwnedOrManageStore(Store storeToAdd, Set<Store> listOfStores) {
        if (storeToAdd == null){
            log.error("can't add a null store to user");
            return false;
        }
        if (!listOfStores.contains(storeToAdd)){
            log.info("added a new owned OR managed store to the user");
            listOfStores.add(storeToAdd);
            return true;
        }
        log.error("can't add an existing store");
        return false;
    }

    /**
     * This method is used to change the stage of the user to loggin-out
     */
    public void login(){
        isLogin = true;
    }

    /**
     * This method is used to change the stage of the user to logged-out
     * @return always true, because the user is now logged-out
     */
    public boolean logout(){
        isLogin = false;
        return true;
    }

    /**
     * This method is used to find if this user is a owner of a certain store.
     * @param storeId - the id of the store
     * @return the store if the user is an owner, exception if he's not
     */
    public Store getOwnerStore(int storeId) {
        return ownedStores.stream()
                .filter(store -> store.getStoreId() == storeId)
                .findFirst().orElseThrow(() -> new NoOwnerInStoreException(userName, storeId));
    }

    /**
     * This method is used to find if this user is a manager of a certain store.
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
     * @param storeOfProduct - the store of the product that needs to be added
     * @param productToAdd - the product that needs to be added
     * @param amountOfProduct - the amount of the product
     * @return true if the addition was successful, false if there were a problem to add it
     */
    public boolean saveProductInShoppingBag(Store storeOfProduct, Product productToAdd, int amountOfProduct) {
        if (shoppingCart.getShoppingBag(storeOfProduct) == null){
            ShoppingBag storeShoppingBag = new ShoppingBag(storeOfProduct);
            boolean isAdded = storeShoppingBag.addProductToBag(productToAdd, amountOfProduct);
            log.info("try to add the product to a new shopping bug");
            if(isAdded)
                return shoppingCart.addBagToCart(storeOfProduct,storeShoppingBag);
            return false;
        }
        log.info("try to add the product to an existing shopping bug");
        return shoppingCart.getShoppingBag(storeOfProduct).addProductToBag(productToAdd, amountOfProduct);
    }

    /**
     * This method is used to remove a product from the cart.
     * The method use the methods getShoppingBag in ShoppingCart and removeProductFromBag in ShoppingBag
     * @param storeOfProduct - the store of the product that needs to be removed
     * @param productToRemove - the product that needs to be removed
     * @return true if the removal was successful, false if there were a problem to remove it.
     */
    public boolean removeProductInShoppingBag(Store storeOfProduct, Product productToRemove) {
        if(shoppingCart.getShoppingBag(storeOfProduct) == null){
            log.error("the product is not in cart");
        }
        log.info("try to remove a product from cart");
        return shoppingCart.getShoppingBag(storeOfProduct).removeProductFromBag(productToRemove);
    }


}

package com.wsep202.TradingSystem.domain.trading_system_management;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Builder
@AllArgsConstructor
@Data
public class ShoppingCart {

    NumberFormat formatter = new DecimalFormat("#.##");
    @Builder.Default
    private Map<Store, ShoppingBag> shoppingBagsList;
    private double totalCartCost;
    private int numOfBagsInCart;
    private int numOfProductsInCart;

    public ShoppingCart(){
        this.shoppingBagsList = new HashMap<>();
        totalCartCost = 0;
        numOfBagsInCart = 0;
        numOfProductsInCart = 0;
    }

    /**
     * This method is used to add a bag from the cart.
     * @param storeOfBag - the store of the bag
     * @param bagToAdd - the bag that needs to be added to the cart
     * @return true if the bag was added successfully to cart, false if the bag is already
     * in the cart or the store or the bag are null.
     */
    public boolean addBagToCart(Store storeOfBag, ShoppingBag bagToAdd){
        if(storeOfBag == null || bagToAdd == null){
            log.error("Can't add bag to cart if bag or store is null");
            return false;
        }
        if(shoppingBagsList.containsKey(storeOfBag)){
            log.error("Can't add the same bag again");
            return false;
        }
        shoppingBagsList.put(storeOfBag, bagToAdd);
        numOfBagsInCart += 1;
        numOfProductsInCart += bagToAdd.getNumOfProductsInBag();
        totalCartCost += bagToAdd.getTotalCostOfBag();
        totalCartCost = Double.parseDouble(formatter.format(totalCartCost));
        log.info("Bag was successfully added to cart");
        return true;
    }

    /**
     * This method is used to remove a bag from the cart.
     * @param storeOfBag - the store of the bag
     * @param bagToRemove - the bag that needs to be removed
     * @return true if the cart contains the bag, false if the bag or the store are null
     * or the cart does not contains the bag.
     */
    public boolean removeBagFromCart(Store storeOfBag, ShoppingBag bagToRemove){
        if(storeOfBag == null || bagToRemove == null){
            log.error("Can't remove bag from cart if bag or store is null");
            return false;
        }
        if(!shoppingBagsList.containsKey(storeOfBag)){
            log.error("The bag is not in the cart");
            return false;
        }
        numOfBagsInCart -= 1;
        numOfProductsInCart -= bagToRemove.getNumOfProductsInBag();
        totalCartCost -= bagToRemove.getTotalCostOfBag();
        totalCartCost = Double.parseDouble(formatter.format(totalCartCost));
        shoppingBagsList.remove(storeOfBag);
        log.info("Bag was successfully removed from cart");
        return true;
    }

    /**
     * This method used to get a shopping bag from the cart.
     * @param storeOfBag - the store of the shopping bag
     * @return null if the store is null or if the cart does not hold the store,
     * and the Shopping bag if it's exists.
     */
    public ShoppingBag getShoppingBag(Store storeOfBag){
       if(storeOfBag == null){
           log.error("Can't return shopping of a null store");
           return null;
       }
       if (shoppingBagsList.containsKey(storeOfBag)) {
           log.info("Returns the wanted shopping bag");
           return shoppingBagsList.get(storeOfBag);
       }
       log.error("The bag is not in the cart");
       return null;
    }
}

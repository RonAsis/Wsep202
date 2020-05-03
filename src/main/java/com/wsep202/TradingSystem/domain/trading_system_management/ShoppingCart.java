package com.wsep202.TradingSystem.domain.trading_system_management;

import com.wsep202.TradingSystem.domain.exception.NotInStockException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Builder
@AllArgsConstructor
@Data
public class ShoppingCart {

    NumberFormat formatter = new DecimalFormat("#.##");
    /**
     * list of stores and there shopping bags
     */
    @Builder.Default
    private Map<Store, ShoppingBag> shoppingBagsList = new HashMap<>();
    /**
     * the total cost of the products in cart
     */
    private double totalCartCost;
    /**
     * number of different shopping bags in cart
     */
    private int numOfBagsInCart;
    /**
     * number of different products in the cart
     */
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
            log.error("Can't add the same bag again of store '"+ storeOfBag.getStoreName() +"'");
            return false;
        }
        shoppingBagsList.put(storeOfBag, bagToAdd);
        numOfBagsInCart += 1;
        numOfProductsInCart += bagToAdd.getNumOfProductsInBag();
        totalCartCost += bagToAdd.getTotalCostOfBag();
        fixTotalCartCost();
        log.info("Bag was successfully added to cart from store '"+ storeOfBag.getStoreName() +"'");
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
            log.error("The bag is not in the cart from store '"+ storeOfBag.getStoreName() +"'");
            return false;
        }
        numOfBagsInCart -= 1;
        numOfProductsInCart -= bagToRemove.getNumOfProductsInBag();
        totalCartCost -= bagToRemove.getTotalCostOfBag();
        fixTotalCartCost();
        shoppingBagsList.remove(storeOfBag);
        log.info("Bag was successfully removed from cart from store '"+ storeOfBag.getStoreName() +"'");
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
       if (shoppingBagsList.size() > 0 && shoppingBagsList.containsKey(storeOfBag)) {
           log.info("Returns the wanted shopping bag from store '"+ storeOfBag.getStoreName() +"'");
           return shoppingBagsList.get(storeOfBag);
       }
       log.error("The bag is not in the cart fro store '"+ storeOfBag.getStoreName() +"'");
       return null;
    }

    public boolean removeProductInCart(Store storeOfProduct, ShoppingBag shoppingBag, Product productToRemove) {
        if (storeOfProduct == null || shoppingBag == null || productToRemove == null){
            log.error("store or product or shopping bag can't be null");
            return false;
        }
        if (!shoppingBagsList.containsKey(storeOfProduct) || shoppingBag.getProductAmount(productToRemove) == -1){
            log.error("product '"+ productToRemove.getName() +"' from store '"+ storeOfProduct.getStoreName() +"'");
            return false;
        }
        numOfProductsInCart--;
        totalCartCost -= (shoppingBag.getProductAmount(productToRemove)*productToRemove.getCost());
        fixTotalCartCost();
        if (shoppingBag.getNumOfProductsInBag() == 1){
            numOfBagsInCart --;
            shoppingBag.removeProductFromBag(productToRemove);
            shoppingBagsList.remove(storeOfProduct);
            log.info("delete an empty shopping bag from store '"+ storeOfProduct.getStoreName() +"'");
        }
        log.info("product '"+ productToRemove.getName() +"' was removed from cart");
        return true;
    }

    /**
     * This method is used to get all the products in shopping cart
     * @return map of products and there amounts
     */
    public Map<Product,Integer> watchShoppingCart(){
        Map<Product,Integer> allProducts = new HashMap<>();
        for(ShoppingBag shoppingBag: shoppingBagsList.values()){
            for (Product product: shoppingBag.getProductListFromStore().keySet()){
                allProducts.put(product,shoppingBag.getProductAmount(product));
            }
        }
        return allProducts;
    }

    /**
     * checks if all products in all bags are in ther related store
     * @return true if all products in all bags are in ther related store
     * @throws NotInStockException
     */
    public boolean isAllBagsInStock() throws NotInStockException {
        boolean ans = true; //true if all products in all bags are in ther related store
        for(Store store: this.getShoppingBagsList().keySet()){
            ans = ans && store.isAllInStock(this.getShoppingBagsList().get(store));
        }
        return ans;
    }

    /**
     * update the amount of products in stock after successful purchase
     */
    public void updateAllAmountsInStock() {
        for(Store store: this.getShoppingBagsList().keySet()){
            store.updateStock(this.getShoppingBagsList().get(store));
        }
    }

    /**
     * create receipts for all bags were purchased
     * @param buyerName
     * @return
     */
    public ArrayList<Receipt> createReceipts(String buyerName){
        ArrayList<Receipt> purchaseReceipts = new ArrayList<>();
        for (Store store: this.getShoppingBagsList().keySet()){
            purchaseReceipts.add(store.createReceipt(this.getShoppingBagsList().get(store),buyerName));
        }
        return purchaseReceipts;
    }

    public void applyDiscountPolicies() {
        for (Store store: this.getShoppingBagsList().keySet()){
            store.applyDiscountPolicies((HashMap<Product, Integer>) shoppingBagsList
                    .get(store).getProductListFromStore());
        }
    }

    private void fixTotalCartCost(){
        totalCartCost = Double.parseDouble(formatter.format(totalCartCost));
    }
}

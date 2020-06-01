package com.wsep202.TradingSystem.domain.trading_system_management;

import com.wsep202.TradingSystem.domain.exception.NotInStockException;
import com.wsep202.TradingSystem.domain.exception.PurchasePolicyException;
import com.wsep202.TradingSystem.domain.trading_system_management.purchase.BillingAddress;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Builder
@AllArgsConstructor
@Data
@Entity
public class ShoppingCart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    /**
     * list of stores and there shopping bags
     */
    @Builder.Default
    @OneToMany(cascade = CascadeType.ALL)
    @MapKeyJoinColumn(name = "store_store_id")
    private Map<Store, ShoppingBag> shoppingBagsList;

    public ShoppingCart(Map<Store,ShoppingBag> shoppingBagsList){
        this.shoppingBagsList=shoppingBagsList;
    }

    public ShoppingCart(){
        this.shoppingBagsList = new HashMap<>();
    }

    /**
     * This method is used to add a bag from the cart.
     * @param storeOfBag - the store of the bag
     * @param bagToAdd - the bag that needs to be added to the cart
     * @return true if the bag was added successfully to cart, false if the bag is already
     * in the cart or the store or the bag are null.
     */
    public boolean addBagToCart(Store storeOfBag, ShoppingBag bagToAdd){
        if (storeOfBag == null || bagToAdd == null){
            log.info("store or product or shopping bag can't be null");
            return false;
        }
        if (!shoppingBagsList.containsKey(storeOfBag)){
            shoppingBagsList.put(storeOfBag, bagToAdd);
            log.info("Bag was successfully added to cart from store '"+ storeOfBag.getStoreName() +"'");
            return true;
        }
        log.info("bag is already in cart");
        return false;
    }

    /**
     * This method is used to remove a bag from the cart.
     * @param storeOfBag - the store of the bag
     * @param bagToRemove - the bag that needs to be removed
     * @return true if the cart contains the bag, false if the bag or the store are null
     * or the cart does not contains the bag.
     */
    public boolean removeBagFromCart(Store storeOfBag, ShoppingBag bagToRemove){
        if (checkParameters(storeOfBag, bagToRemove)){
            shoppingBagsList.remove(storeOfBag);
            log.info("Bag was successfully removed from cart from store '"+ storeOfBag.getStoreName() +"'");
            return true;
        }
        return false;
    }

    /**
     * This method used to get a shopping bag from the cart.
     * @param storeOfBag - the store of the shopping bag
     * @return null if the store is null or if the cart does not hold the store,
     * and the Shopping bag if it's exists.
     */
    public ShoppingBag getShoppingBag(Store storeOfBag){
        if(storeOfBag == null){
            log.info("Can't return shopping of a null store");
            return null;
        }
        if (shoppingBagsList.size() > 0 && shoppingBagsList.containsKey(storeOfBag)) {
            log.info("Returns the wanted shopping bag from store '"+ storeOfBag.getStoreName() +"'");
            return shoppingBagsList.get(storeOfBag);
        }
        log.error("The bag is not in the cart fro store '"+ storeOfBag.getStoreName() +"'");
        return null;
    }

    /**
     * This method is used to remove products from cart
     * @param storeOfProduct - the products store
     * @param productToRemove - the product that needs to be removed
     * @return true if the removal was successful, else false
     */
    public boolean removeProductInCart(Store storeOfProduct, Product productToRemove) {
       if(checkParameters(storeOfProduct,productToRemove)) {
           shoppingBagsList.get(storeOfProduct).removeProductFromBag(productToRemove);
           if (shoppingBagsList.get(storeOfProduct).getNumOfProducts() == 0) {
               shoppingBagsList.remove(storeOfProduct);
               log.info("delete an empty shopping bag from store '" + storeOfProduct.getStoreName() + "'");
           }
           log.info("product '" + productToRemove.getName() + "' was removed from cart");
           return true;
       }
       return false;
    }

    /**
     * This method is used to add a product to an existing bag in cart
     * @param storeOfProduct - the products store
     * @param productToAdd - the product that needs to be added
     * @param amountOfProduct - the amount of the product
     * @return true if the addition was successful
     */
    public boolean addProductToCart(Store storeOfProduct, Product productToAdd, int amountOfProduct){
        if(checkParameters(storeOfProduct,productToAdd)) {
            shoppingBagsList.get(storeOfProduct).addProductToBag(productToAdd,amountOfProduct);
            log.info("the product '"+productToAdd.getName()+"' to cart");
            return true;
        }
        return false;
    }

    private boolean checkParameters(Store store, Object object2){
        if (store == null || object2 == null){
            log.info("store or product or shopping bag can't be null");
            return false;
        }
        if (!shoppingBagsList.containsKey(store)){
            log.info("No items from store '" +store.getStoreName()+ "' in cart");
            return false;
        }
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
                allProducts.put(product,shoppingBag.getProductAmount(product.getProductSn()));
            }
        }
        return allProducts;
    }

    /**
     * checks if all products in all bags are in their related store
     * @return true if all products in all bags are in their related store
     * @throws NotInStockException
     */
    public boolean isAllBagsInStock() throws NotInStockException {
        boolean ans = true; //true if all products in all bags are in ther related store
        for(Store store: shoppingBagsList.keySet()){
            ans = ans && store.isAllInStock(shoppingBagsList.get(store));
        }
        return ans;
    }

    /**
     * update the amount of products in stock after successful purchase
     */
    public void updateAllAmountsInStock() {
        for(Store store: shoppingBagsList.keySet()){
            store.updateStock(shoppingBagsList.get(store));
        }
    }

    /**
     * create receipts for all bags were purchased
     * @param buyerName
     * @return list of receipts
     */
    public ArrayList<Receipt> createReceipts(String buyerName){
        ArrayList<Receipt> purchaseReceipts = new ArrayList<>();
        for (Store store: shoppingBagsList.keySet()){
            purchaseReceipts.add(store.createReceipt(shoppingBagsList.get(store),buyerName));
        }
        shoppingBagsList = new HashMap<>();
        return purchaseReceipts;
    }

    public void applyDiscountPolicies() {
        for (Store store: shoppingBagsList.keySet()){
            store.applyDiscountPolicies((HashMap<Product, Integer>) shoppingBagsList
                    .get(store).getProductListFromStore());
        }
    }

    public boolean changeProductAmountInShoppingBag(int storeId, int amount, int productSn) {
        return shoppingBagsList.entrySet().stream()
                .filter(storeShoppingBagEntry -> storeShoppingBagEntry.getKey().getStoreId() == storeId)
                .map(storeShoppingBagEntry -> storeShoppingBagEntry.getValue())
                .findFirst().map(shoppingBag -> shoppingBag.changeAmountOfProductInBag(productSn , amount)).orElse(false);
    }

    public int getNumOfBagsInCart(){
        return shoppingBagsList.values().size();
    }

    public Double getTotalCartCost(){
        return shoppingBagsList.values().stream()
                .map(ShoppingBag::getTotalCostOfBag)
                .reduce((double) 0, Double::sum);
    }

    public int getNumOfProductsInCart(){
        return shoppingBagsList.values().stream()
                .map(ShoppingBag::getNumOfProducts)
                .reduce( 0, Integer::sum);

    }

    public boolean ApprovePurchasePolicy(BillingAddress billingAddress) throws PurchasePolicyException {
        for (Store store: this.getShoppingBagsList().keySet()){
            store.isApprovedPurchasePolicies((HashMap<Product, Integer>) shoppingBagsList
                    .get(store).getProductListFromStore(),billingAddress);
        }
        return true;
    }
}

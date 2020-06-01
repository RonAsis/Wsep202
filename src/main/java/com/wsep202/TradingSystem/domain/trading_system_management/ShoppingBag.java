package com.wsep202.TradingSystem.domain.trading_system_management;

import static com.wsep202.TradingSystem.domain.trading_system_management.policy_purchase.Formatter.formatter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import javax.validation.constraints.Min;
import java.util.HashMap;
import java.util.Map;


@Data
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class ShoppingBag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /**
     * the store of the products
     */
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private Store storeOfProduct;
    /**
     * list of all of the products and the amount of each product
     */
    @ElementCollection
    @MapKeyColumn(name = "products")
    private Map<Product, Integer> productListFromStore;

    public ShoppingBag(Store storeOfProduct){
        this.storeOfProduct = storeOfProduct;
        productListFromStore = new HashMap<>();
    }

    /**
     * This method is used for adding products to the bag.
     * In case if the product is null, the method won't do anything.
     * @param productToAdd - the product that needs to be added.
     * @return true if the product was added successfully to the bag, false if the product was null.
     */
    public boolean addProductToBag(Product productToAdd, int amountOfProduct){
        if (productToAdd == null || amountOfProduct <= 0) {
            log.info("A null product was trying to be added to the bag or a in correct amount");
            return false;
        }
        if(productToAdd.getStoreId() != storeOfProduct.getStoreId()){
            log.info("Store '"+ storeOfProduct.getStoreId() +"' id and product '"+ productToAdd.getName() +"' store id "+ productToAdd.getStoreId() +" does not mach");
            return false;
        }
        if (productToAdd.getAmount() < amountOfProduct){
            log.info("there is not enough '" + productToAdd.getName() + "' in store '" + storeOfProduct.getStoreName() + "'");
            return false;
        }
        Product isInBag = containProduct(productToAdd.getProductSn());
        if (isInBag != null) {
            return changeAmountOfProductInBag(isInBag.getProductSn(), amountOfProduct);
        }
        log.info("Product '"+ productToAdd.getName() + "' was added to bag");
        productListFromStore.put(productToAdd, amountOfProduct);
        return true;
    }

    /**
     * This method is used for deleting a product from the bag.
     * In case if the product does not exists or if it's null, the method won't do anything.
     * @param productToRemove - the product that needs to be removed.
     * @return true if the product was in the bag and removed from list, else false.
     */
    public boolean removeProductFromBag(Product productToRemove){
        if (productToRemove == null){
            log.info("Can't remove a null product");
            return false;
        }
        if(productToRemove.getStoreId() != storeOfProduct.getStoreId()){
            log.info("Store id "+ storeOfProduct.getStoreId() +" and product store id "+ productToRemove.getStoreId() +" does not mach");
            return false;
        }
        Product isInBag = containProduct(productToRemove.getProductSn());
        if (isInBag != null){
            log.info("The product "+ productToRemove.getName() +" was removed from the bag");
            productListFromStore.remove(isInBag);
            return true;
        }
        log.error("The product '"+ productToRemove.getName() +"' was not found");
        return false;
    }

    /**
     * This method used to change the amount of a product.
     * @param serialNumber - the products serial number
     * @param amountOfProduct - the amount that needs to be added
     * @return true if the amount can be changed, false if it can't.
     */
    public boolean changeAmountOfProductInBag(int serialNumber, int amountOfProduct){
        Product isInBag = containProduct(serialNumber);
        if (isInBag == null || amountOfProduct <= 0){
            log.info("product is not in bag or amount is zero or less");
            return false;
        }
        log.info("Update the amount of an exciting product '"+ isInBag.getName() +"'");
        productListFromStore.replace(isInBag ,amountOfProduct);
        return true;
    }

    /**
     * This method used to get the amount of a product in the bag
     * @param serialNumber - the products serial number in store
     * @return the amount of the product in the bag if it's exists, -1 if the product is null or not in the bag
     */
    public int getProductAmount(int serialNumber){
        Product isInBag = containProduct(serialNumber);
        if (isInBag == null){
            log.info("product not in bag");
            return -1;
        }
        log.info("return the amount of product '"+ productListFromStore.get(isInBag) +"'");
        return productListFromStore.get(isInBag);
    }

/*
    public boolean changeProductAmountInShoppingBag(int amount, int productSn) {
        return productListFromStore.entrySet().stream()
                .filter(productIntegerEntry -> productIntegerEntry.getKey().getProductSn() == productSn)
                .findFirst().map(productIntegerEntry -> {
                    productIntegerEntry.setValue(amount);
                    return true;
                }).orElse(false);
    }
*/

    /**
     * check if the product exists in bag
     * @param serialNum - the products serial number
     * @return null if the product does not exists, else the product in the bag
     */
    public Product containProduct(int serialNum){
        for (Product product: productListFromStore.keySet()) {
                if (product.getProductSn() == serialNum){
                    log.info("product with serial number '" + serialNum + "' is in bag");
                    return product;
                }
        }
        log.info("product with serial number '" + serialNum + "' is not in bag");
        return null;
    }

    public double getTotalCostOfBag(){
        Double price = this.getProductListFromStore().entrySet().stream()
                .reduce(0.0, (acc, entry) -> acc + entry.getKey().getCost() * entry.getValue(), Double::sum);
        return Double.parseDouble(formatter.format(price));
    }

    public double getOriginalTotalCostOfBag(){
        double price=0;
        for(Product product: this.getProductListFromStore().keySet()){
            price+=(product.getOriginalCost()*this.getProductListFromStore().get(product));
        }
        price = Double.parseDouble(formatter.format(price));
        return price;
    }

    public int getNumOfProducts() {
        return productListFromStore.values().size();
    }

    @ManyToOne(optional = false)
    private ShoppingCart shoppingCarts;

    public ShoppingCart getShoppingCarts() {
        return shoppingCarts;
    }

    public void setShoppingCarts(ShoppingCart shoppingCarts) {
        this.shoppingCarts = shoppingCarts;
    }
}

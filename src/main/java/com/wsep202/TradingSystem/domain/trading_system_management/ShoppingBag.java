package com.wsep202.TradingSystem.domain.trading_system_management;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;


@Data
@Slf4j
@NoArgsConstructor
public class ShoppingBag {

    //private Map<Integer, Integer> mapProductSnToAmount;
    NumberFormat formatter = new DecimalFormat("#.##");

    /**
     * the store of the products
     */
    private Store storeOfProduct;
    /**
     * list of all of the products and the amount of each product
     */
    private Map<Product, Integer> productListFromStore;
    /**
     * shows the cost of all of the products in bag
     */
    private double totalCostOfBag;
    /**
     * shows how many types of products in the bag
     */
    private int numOfProductsInBag;

    public ShoppingBag(Store storeOfProduct){
        this.storeOfProduct = storeOfProduct;
        productListFromStore = new HashMap<>();
        totalCostOfBag = 0;
        numOfProductsInBag = 0;
    }

    /**
     * This method is used for adding products to the bag.
     * In case if the product is null, the method won't do anything.
     * @param productToAdd - the product that needs to be added.
     * @return true if the product was added successfully to the bag, false if the product was null.
     */
    public boolean addProductToBag(Product productToAdd, int amountOfProduct){
        if (productToAdd == null) {
            log.error("A null product was trying to be added to the bag");
            return false;
        }
        if(productToAdd.getStoreId() != storeOfProduct.getStoreId()){
            log.error("Store '"+ storeOfProduct.getStoreId() +"' id and product '"+ productToAdd.getName() +"' store id "+ productToAdd.getStoreId() +" does not mach");
            return false;
        }
        if (productToAdd.getAmount() < amountOfProduct){
            log.error("there is not enough '" + productToAdd.getName() + "' in store '" + storeOfProduct.getStoreName() + "'");
            return false;
        }
        else {
            if (productListFromStore.containsKey(productToAdd)) {
                return changeAmountOfProductInBag(productToAdd, amountOfProduct);
            }
            if (amountOfProduct <= 0){
                log.error("The amount of the product '"+ productToAdd.getName() +"' needs to be greater than zero");
                return false;
            }
            log.info("Product '"+ productToAdd.getName() + "' was added to bag");
            productListFromStore.put(productToAdd, amountOfProduct);
            numOfProductsInBag += 1;
            totalCostOfBag += (productToAdd.getCost() * amountOfProduct);
            totalCostOfBag = Double.parseDouble(formatter.format(totalCostOfBag));
            return true;
        }
    }

    /**
     * This method is used for deleting a product from the bag.
     * In case if the product does not exists or if it's null, the method won't do anything.
     * @param productToRemove - the product that needs to be removed.
     * @return true if the product was in the bag and removed from list, else false.
     */
    public boolean removeProductFromBag(Product productToRemove){
        if (productToRemove == null){
            log.error("Can't remove a null product");
            return false;
        }
        if(productToRemove.getStoreId() != storeOfProduct.getStoreId()){
            log.error("Store id "+ storeOfProduct.getStoreId() +" and product store id "+ productToRemove.getStoreId() +" does not mach");
            return false;
        }
        if (productListFromStore.containsKey(productToRemove)){
            log.info("The product "+ productToRemove.getName() +" was removed from the bag");
            totalCostOfBag -= (productListFromStore.get(productToRemove)*productToRemove.getCost());
            totalCostOfBag = Double.parseDouble(formatter.format(totalCostOfBag));
            numOfProductsInBag -= 1;
            productListFromStore.remove(productToRemove);
            return true;
        }
        log.error("The product '"+ productToRemove.getName() +"' was not found");
        return false;
    }

    /**
     * This method used to change the amount of an exciting product.
     * In case if the calculation of amount of the product is less than 0, the method will return false.
     * @param product - the product that is already in the bag
     * @param amountOfProduct - the amount that needs to be added
     * @return true if the amount can be changed, false if it can't.
     */
    private boolean changeAmountOfProductInBag(Product product, int amountOfProduct){
        if(amountOfProduct < 0 && amountOfProduct + productListFromStore.get(product) < 0){
            log.error("The amount of product '"+ product.getName() +"' cannot be less than 0");
            return false;
        }
        log.info("Update the amount of an exciting product '"+ product.getName() +"'");
        totalCostOfBag += (amountOfProduct*product.getCost());
        totalCostOfBag = Double.parseDouble(formatter.format(totalCostOfBag));
        productListFromStore.replace(product,productListFromStore.get(product)+amountOfProduct);
        if (productListFromStore.get(product)==0) {
            productListFromStore.remove(product);
            numOfProductsInBag --;
        }
        return true;
    }

    /**
     * This method used to get the amount of a product in the bag
     * @param product - the product to get the amount
     * @return the amount of the product in the bag if it's exists, -1 if the product is null or not in the bag
     */
    public int getProductAmount(Product product){
        if (product == null){
            log.error("can't return amount for null product");
            return -1;
        }
        if(productListFromStore.containsKey(product)){
            log.info("return the amount of product '"+ productListFromStore.get(product) +"'");
            return productListFromStore.get(product);
        }
        return -1;
    }

    public double getTotalCostOfBag(){
        double price=0;
        for(Product product: this.getProductListFromStore().keySet()){
            price+=(product.getCost()*this.getProductListFromStore().get(product));
        }
        price = Double.parseDouble(formatter.format(price));
        return price;
    }

}
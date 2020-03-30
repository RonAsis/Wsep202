package com.wsep202.TradingSystem.domain.trading_system_management;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class TradingSystemFacade {

    private final TradingSystem tradingSystem;

    public List<Receipt> viewPurchaseHistory(String userName) {
        UserSystem user = tradingSystem.getUser(userName);
        return user.getReceipts();
    }

    public List<Receipt> viewPurchaseHistory(String administratorUsername, int storeId) {
        Store store = tradingSystem.getStore(administratorUsername, storeId);
        return store.getReceipts();
    }

    public List<Receipt> viewPurchaseHistory(String administratorUsername, String userName) {
        UserSystem userByAdmin = tradingSystem.getUserByAdmin(administratorUsername, userName);
        return userByAdmin.getReceipts();
    }

    public List<Receipt> viewPurchaseHistoryOfSeller(String username, int storeId) {
        UserSystem user = tradingSystem.getUser(username);
        return user.getManagerStore(storeId).getReceipts();
    }

    public List<Receipt> viewPurchaseHistoryOfOwner(String username, int storeId) {
        UserSystem user = tradingSystem.getUser(username);
        return user.getOwnerStore(storeId).getReceipts();
    }

    public boolean addProduct(String ownerUsername, int storeId, String productName, String category, int amount, double cost) {
        UserSystem user = tradingSystem.getUser(ownerUsername);
        Store ownerStore = user.getOwnerStore(storeId);
        ProductCategory productCategory = ProductCategory.getProductCategory(category);
        Product product = new Product(productName, productCategory,amount, cost);
        return ownerStore.addNewProduct(user, product);
    }

    public boolean removeProductFromStore(String ownerUsername,int storeId, String productName) {
        UserSystem user = tradingSystem.getUser(ownerUsername);
        Store ownerStore = user.getOwnerStore(storeId);
        return ownerStore.removeProductFromStore(user,productName);
    }

    public boolean editProduct(String ownerUsername, int storeId, int productSn, String productName, String category, int amount, double cost) {
        UserSystem user = tradingSystem.getUser(ownerUsername);
        Store ownerStore = user.getOwnerStore(storeId);
        return ownerStore.editProduct(user, productSn, productName, category, amount, cost);//TODO
    }

    public boolean addOwner(String ownerUsername, int storeId, String newOwnerUsername) {
        UserSystem ownerUser = tradingSystem.getUser(ownerUsername);
        UserSystem newOwnerUser = tradingSystem.getUser(newOwnerUsername);
        Store ownerStore = ownerUser.getOwnerStore(storeId);
        return ownerStore.addOwner(ownerStore, newOwnerUser);
    }

    public boolean addManager(String ownerUsername, int storeId, String newManagerUsername) {
        UserSystem ownerUser = tradingSystem.getUser(ownerUsername);
        UserSystem newManagerUser = tradingSystem.getUser(newManagerUsername);
        Store ownerStore = ownerUser.getOwnerStore(storeId);
        return ownerStore.addManager(ownerStore, newManagerUser);
    }

    public boolean addPermission(String ownerUsername, int storeId, String newManagerUsername, String permission){
        return false;//TODO
    }
    public boolean removeManager(String ownerUsername, int storeId, String newOwnerUsername) {
        return false;//TODO
    }

    public boolean logout(String username) {
        UserSystem user = tradingSystem.getUser(username);
        return user.logout();
    }

    public boolean openStore(String usernameOwner, String purchasePolicy, DiscountPolicy discountPolicy, DiscountPolicy discountPolicy1, String purchaseType, String storeName) {
        return false;//TODO
    }

    public boolean registerUser(String userName, String password, String firstName, String lastName) {
        return false;//TODO
    }

    public boolean login(String userName, String password) {
        return false;//TODO
    }

    public Store viewStoreInfo(String storeId) {
        return null; //TODO
    }

    public Product viewProduct(String storeId, String productId) {
        return null; //TODO
    }

    public List<Product> searchProductByName(String productName) {
        return null; //TODO
    }

    public List<Product> searchProductByCategory(String category) {
        return null; //TODO
    }

    public List<Product> searchProductByKeyWords(List<String> keyWords) {
        return null; //TODO
    }

    public List<Product> filterByRangePrice(double min, double max) {
        return null; //TODO
    }

    public List<Product> filterByProductRank(int rank) {
        return null; //TODO
    }

    public List<Product> filterByStoreRank(int rank) {
        return null; //TODO
    }

    public List<Product> filterByStoreCategory(String category) {
        return null; //TODO
    }

    public boolean saveProductInShoppingBag(String userName, int id, int storeId, int productSn) {
        return false; //TODO
    }

    public ShoppingCart viewProductsInShoppingCart(String userName) {
        return null; //TODO
    }

    public boolean removeProductInShoppingBag(String userName, int storeId, int productSn) {
        return false; //TODO
    }

    public Receipt purchaseShoppingCart(ShoppingCart shoppingCart) {
        return null; //TODO
    }

    public Receipt purchaseShoppingCart(String userName) {
        return null; //TODO
    }

}

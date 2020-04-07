package com.wsep202.TradingSystem.domain.trading_system_management;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class TradingSystemFacade {

    private final TradingSystem tradingSystem;

    /**
     * @param userName - must be logged in
     * @return
     */
    public List<Receipt> viewPurchaseHistory(String userName) {
        UserSystem user = tradingSystem.getUser(userName);
        return user.getReceipts();
    }

    /**
     * administrator view purchase history of store
     *
     * @param administratorUsername
     * @param storeId
     * @return
     */
    public List<Receipt> viewPurchaseHistory(String administratorUsername, int storeId) {
        Store store = tradingSystem.getStore(administratorUsername, storeId);
        return store.getReceipts();
    }

    /**
     * administrator view purchase history of user
     *
     * @param administratorUsername
     * @param userName
     * @return
     */
    public List<Receipt> viewPurchaseHistory(String administratorUsername, String userName) {
        UserSystem userByAdmin = tradingSystem.getUserByAdmin(administratorUsername, userName);
        return userByAdmin.getReceipts();
    }

    /**
     * Manager view purchase history of store
     * @param username
     * @param storeId
     * @return
     */
    public List<Receipt> viewPurchaseHistoryOfManager(String username, int storeId) {
        UserSystem user = tradingSystem.getUser(username);
        return user.getManagerStore(storeId).getReceipts();
    }

    /**
     * Owner view purchase history of store
     * @param username
     * @param storeId
     * @return
     */
    public List<Receipt> viewPurchaseHistoryOfOwner(String username, int storeId) {
        UserSystem user = tradingSystem.getUser(username);
        return user.getOwnerStore(storeId).getReceipts();
    }

    /**
     * add product to store
     * @param ownerUsername
     * @param storeId
     * @param productName
     * @param category
     * @param amount
     * @param cost
     * @return
     */
    public boolean addProduct(String ownerUsername, int storeId, String productName, String category, int amount, double cost) {
        UserSystem user = tradingSystem.getUser(ownerUsername);
        Store ownerStore = user.getOwnerStore(storeId);
        ProductCategory productCategory = ProductCategory.getProductCategory(category);
        Product product = new Product(productName, productCategory, amount, cost, storeId);
        return ownerStore.addNewProduct(user, product);
    }

    /**
     * delete product form store
     * @param ownerUsername
     * @param storeId
     * @param productName
     * @return
     */
    public boolean deleteProductFromStore(String ownerUsername, int storeId, String productName) {
        UserSystem user = tradingSystem.getUser(ownerUsername);
        Store ownerStore = user.getOwnerStore(storeId);
        return ownerStore.removeProductFromStore(user, productName);
    }

    /**
     * edit product
     * @param ownerUsername
     * @param storeId
     * @param productSn
     * @param productName
     * @param category
     * @param amount
     * @param cost
     * @return
     */
    public boolean editProduct(String ownerUsername, int storeId, int productSn, String productName, String category, int amount, double cost) {
        UserSystem user = tradingSystem.getUser(ownerUsername);
        Store ownerStore = user.getOwnerStore(storeId);
        return ownerStore.editProduct(user, productSn, productName, category, amount, cost);
    }

    /**
     * add new owner to store
     * @param ownerUsername
     * @param storeId
     * @param newOwnerUsername
     * @return
     */
    public boolean addOwner(String ownerUsername, int storeId, String newOwnerUsername) {
        UserSystem ownerUser = tradingSystem.getUser(ownerUsername);
        UserSystem newOwnerUser = tradingSystem.getUser(newOwnerUsername);
        Store ownerStore = ownerUser.getOwnerStore(storeId);
        return ownerStore.addOwner(ownerStore, newOwnerUser);
    }

    /**
     * add manger
     * @param ownerUsername
     * @param storeId
     * @param newManagerUsername
     * @return
     */
    public boolean addManager(String ownerUsername, int storeId, String newManagerUsername) {
        UserSystem ownerUser = tradingSystem.getUser(ownerUsername);
        UserSystem newManagerUser = tradingSystem.getUser(newManagerUsername);
        Store ownedStore = ownerUser.getOwnerStore(storeId);
        return ownedStore.addManager(ownedStore, newManagerUser);
    }

    /**
     *
     * @param ownerUsername
     * @param storeId
     * @param newManagerUsername
     * @param permission
     * @return
     */
    public boolean addPermission(String ownerUsername, int storeId, String newManagerUsername, String permission) {
        UserSystem ownerUser = tradingSystem.getUser(ownerUsername);
        Store ownerStore = ownerUser.getOwnerStore(storeId);
        UserSystem user = tradingSystem.getUser(newManagerUsername);
        StorePermission storePermission = StorePermission.getStorePermission(permission);
        return ownerStore.addPermissionToManager(ownerStore, user, storePermission);
    }

    public boolean removeManager(String ownerUsername, int storeId, String managerUsername) {
        UserSystem ownerUser = tradingSystem.getUser(ownerUsername);
        Store ownerStore = ownerUser.getOwnerStore(storeId);
        UserSystem user = tradingSystem.getUser(managerUsername);
        return ownerStore.removeManager(ownerStore, user);
    }

    public boolean logout(String username) {
        UserSystem user = tradingSystem.getUser(username);
        return user.logout();
    }

    public boolean openStore(String usernameOwner, PurchasePolicy purchasePolicy, DiscountPolicy discountPolicy,
                             String discountType, String purchaseType, String storeName) {
        UserSystem user = tradingSystem.getUser(usernameOwner);
        DiscountType discountTypeObj = DiscountType.getDiscountType(discountType);
        PurchaseType purchaseTypeObj = PurchaseType.getPurchaseType(purchaseType);
        return tradingSystem.openStore(user, discountTypeObj, purchaseTypeObj, purchasePolicy, discountPolicy,storeName);
    }

    public boolean registerUser(String userName, String password, String firstName, String lastName) {
        UserSystem userSystem = new UserSystem(userName, password, firstName, lastName);
        return tradingSystem.registerNewUser(userSystem);
    }

    public boolean login(String userName, String password) {
        UserSystem user = tradingSystem.getUser(userName);
        return tradingSystem.login(user, false, password);
    }

    public Store viewStoreInfo(int storeId) {
        return tradingSystem.getStore(storeId);
    }

    public Product viewProduct(int storeId, int productId) {
        Store store = tradingSystem.getStore(storeId);
        return store.getProduct(productId);
    }

    public List<Product> searchProductByName(String productName) {
        return tradingSystem.searchProductByName(productName);
    }

    public List<Product> searchProductByCategory(String category) {
        ProductCategory productCategory = ProductCategory.getProductCategory(category);
        return tradingSystem.searchProductByCategory(productCategory);
    }

    public List<Product> searchProductByKeyWords(List<String> keyWords) {
        return tradingSystem.searchProductByKeyWords(keyWords);
    }

    public List<Product> filterByRangePrice(List<Product> products, double min, double max) {
        return tradingSystem.filterByRangePrice(products, min, max);
    }

    public List<Product> filterByProductRank(List<Product> products, int rank) {
        return tradingSystem.filterByProductRank(products, rank);
    }

    public List<Product> filterByStoreRank(List<Product> products, int rank) {
        return tradingSystem.filterByStoreRank(products, rank);
    }

    public List<Product> filterByStoreCategory(List<Product> products, String category) {
        ProductCategory productCategory = ProductCategory.getProductCategory(category);
        return tradingSystem.filterByStoreCategory(products, productCategory);
    }

    public boolean saveProductInShoppingBag(String username, int id, int storeId, int productSn) {
        UserSystem user = tradingSystem.getUser(username);
        Store store = tradingSystem.getStore(storeId);
        Product product = store.getProduct(productSn);
        return user.saveProductInShoppingBag(user, store, product);
    }

    public ShoppingCart viewProductsInShoppingCart(String username) {
        UserSystem user = tradingSystem.getUser(username);
        return user.getShoppingCart();
    }

    public boolean removeProductInShoppingBag(String username, int storeId, int productSn) {
        UserSystem user = tradingSystem.getUser(username);
        Store store = tradingSystem.getStore(storeId);
        Product product = store.getProduct(productSn);
        return user.removeProductInShoppingBag(store, product);
    }

    public Receipt purchaseShoppingCart(ShoppingCart shoppingCart) {
        return tradingSystem.purchaseShoppingCart(shoppingCart);
    }

    public Receipt purchaseShoppingCart(String username) {
        UserSystem user = tradingSystem.getUser(username);
        return tradingSystem.purchaseShoppingCart(user);
    }

}

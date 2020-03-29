package com.wsep202.TradingSystem.domain.trading_system_management;


import com.wsep202.TradingSystem.service.user_service.dto.PurchaseHistoryDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class TradingSystemFacade {

    private final TradingSystem tradingSystem;

    public List<PurchaseHistoryDto> viewPurchaseHistory(String userName) {
        //TODO
        return null;
    }

    public List<PurchaseHistoryDto> viewPurchaseHistory(String administratorUsername, int storeId) {
        //TODO
        return null;
    }

    public List<PurchaseHistoryDto> viewPurchaseHistory(String administratorUsername, String userName) {
        //TODO
        return null;
    }

    public List<PurchaseHistoryDto> viewPurchaseHistoryOfSeller(String userName, int storeId) {
        return  null; //TODO
    }

    public List<PurchaseHistoryDto> viewPurchaseHistoryOfOwner(String username, int storeId) {
        return  null; //TODO
    }

    public boolean addProduct(int storeId, String productName, String category, int amount, double cost) {
        return false; // TODO
    }

    public boolean removeProductFromStore(int storeId, String productName) {
        return false; //TODO
    }

    public boolean editProduct(String ownerUsername, int storeId, int productSn, String productName, String category, int amount, double cost) {
        return false;//TODO
    }

    public boolean addOwner(String ownerUsername, int storeId, String newOwnerUsername) {
        return false;//TODO
    }

    public boolean addManager(String ownerUsername, int storeId, String newManagerUsername) {
        return false;//TODO
    }

    public boolean addPermission(String ownerUsername, int storeId, String newManagerUsername, String permission){
        return false;//TODO
    }
    public boolean removeManager(String ownerUsername, int storeId, String newOwnerUsername) {
        return false;//TODO
    }

    public boolean logout(String username) {
        Optional<UserSystem> userOpt = tradingSystem.getUser(username);
        return userOpt.map(tradingSystem::logout).orElse(false);
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

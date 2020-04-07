package com.wsep202.TradingSystem.domain.trading_system_management;


import com.wsep202.TradingSystem.service.user_service.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class TradingSystemFacade {

    private final TradingSystem tradingSystem;

    private final ModelMapper modelMapper;
    /**
     * @param userName - must be logged in
     * @return
     */
    public List<ReceiptDto> viewPurchaseHistory(String userName) {
        UserSystem user = tradingSystem.getUser(userName);
        List<Receipt> receipts = user.getReceipts();
        return convertReceiptDtoList(receipts);
    }

    /**
     * administrator view purchase history of store
     * @param administratorUsername
     * @param storeId
     * @return
     */
    public List<ReceiptDto> viewPurchaseHistory(String administratorUsername, int storeId) {
        Store store = tradingSystem.getStore(administratorUsername, storeId);
        List<Receipt> receipts = store.getReceipts();
        return convertReceiptDtoList(receipts);
    }

    /**
     * administrator view purchase history of user
     *
     * @param administratorUsername
     * @param userName
     * @return
     */
    public List<ReceiptDto> viewPurchaseHistory(String administratorUsername, String userName) {
        UserSystem userByAdmin = tradingSystem.getUserByAdmin(administratorUsername, userName);
        return convertReceiptDtoList(userByAdmin.getReceipts());
    }

    /**
     * Manager view purchase history of store
     * @param username
     * @param storeId
     * @return
     */
    public List<ReceiptDto> viewPurchaseHistoryOfManager(String username, int storeId) {
        UserSystem user = tradingSystem.getUser(username);
        return convertReceiptDtoList(user.getManagerStore(storeId).getReceipts());
    }

    /**
     * Owner view purchase history of store
     * @param username
     * @param storeId
     * @return
     */
    public List<ReceiptDto> viewPurchaseHistoryOfOwner(String username, int storeId) {
        UserSystem user = tradingSystem.getUser(username);
        return convertReceiptDtoList(user.getOwnerStore(storeId).getReceipts());
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

    public boolean openStore(String usernameOwner, PurchasePolicyDto purchasePolicyDto, DiscountPolicyDto discountPolicyDto,
                             String discountType, String purchaseType, String storeName) {
        UserSystem user = tradingSystem.getUser(usernameOwner);
        DiscountType discountTypeObj = DiscountType.getDiscountType(discountType);
        PurchaseType purchaseTypeObj = PurchaseType.getPurchaseType(purchaseType);
        PurchasePolicy purchasePolicy = modelMapper.map(purchasePolicyDto, PurchasePolicy.class);
        DiscountPolicy discountPolicy = modelMapper.map(discountPolicyDto, DiscountPolicy.class);
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

    public StoreDto viewStoreInfo(int storeId) {
        Store store = tradingSystem.getStore(storeId);
        return modelMapper.map(store, StoreDto.class);
    }

    public ProductDto viewProduct(int storeId, int productId) {
        Store store = tradingSystem.getStore(storeId);
        Product product = store.getProduct(productId);
        return modelMapper.map(product, ProductDto.class);
    }

    public List<ProductDto> searchProductByName(String productName) {
        List<Product> products = tradingSystem.searchProductByName(productName);
        return convertProductDtoList(products);
    }

    public List<ProductDto> searchProductByCategory(String category) {
        ProductCategory productCategory = ProductCategory.getProductCategory(category);
        List<Product> products = tradingSystem.searchProductByCategory(productCategory);
        return convertProductDtoList(products);
    }

    public List<ProductDto> searchProductByKeyWords(List<String> keyWords) {
        List<Product> products = tradingSystem.searchProductByKeyWords(keyWords);
        return convertProductDtoList(products);
    }

    public List<ProductDto> filterByRangePrice(List<ProductDto> productDtos, double min, double max) {
        List<Product> products = converterProductsList(productDtos);
        List<Product> productsFiltered = tradingSystem.filterByRangePrice(products, min, max);
        return convertProductDtoList(productsFiltered);
    }

    public List<ProductDto> filterByProductRank(List<ProductDto> productDtos, int rank) {
        List<Product> products = converterProductsList(productDtos);
        List<Product> productsFiltered = tradingSystem.filterByProductRank(products, rank);
        return convertProductDtoList(productsFiltered);
    }

    public List<ProductDto> filterByStoreRank(List<ProductDto> productDtos, int rank) {
        List<Product> products = converterProductsList(productDtos);
        List<Product> productsFiltered = tradingSystem.filterByStoreRank(products, rank);
        return convertProductDtoList(productsFiltered);
    }

    public List<ProductDto> filterByStoreCategory(List<ProductDto> productDtos, String category) {
        List<Product> products = converterProductsList(productDtos);
        List<Product> productsFiltered = tradingSystem.filterByStoreCategory(products, category);
        return convertProductDtoList(productsFiltered);
    }


    public boolean saveProductInShoppingBag(String username, int id, int storeId, int productSn) {
        UserSystem user = tradingSystem.getUser(username);
        Store store = tradingSystem.getStore(storeId);
        Product product = store.getProduct(productSn);
        return user.saveProductInShoppingBag(user, store, product);
    }

    public ShoppingCartDto viewProductsInShoppingCart(String username) {
        UserSystem user = tradingSystem.getUser(username);
        ShoppingCart shoppingCart = user.getShoppingCart();
        return modelMapper.map(shoppingCart, ShoppingCartDto.class);
    }

    public boolean removeProductInShoppingBag(String username, int storeId, int productSn) {
        UserSystem user = tradingSystem.getUser(username);
        Store store = tradingSystem.getStore(storeId);
        Product product = store.getProduct(productSn);
        return user.removeProductInShoppingBag(store, product);
    }

    public ReceiptDto purchaseShoppingCart(ShoppingCartDto shoppingCartDto) {
        ShoppingCart shoppingCart = modelMapper.map(shoppingCartDto, ShoppingCart.class);
        Receipt receipt = tradingSystem.purchaseShoppingCart(shoppingCart);
        return modelMapper.map(receipt, ReceiptDto.class);
    }

    public ReceiptDto purchaseShoppingCart(String username) {
        UserSystem user = tradingSystem.getUser(username);
        Receipt receipt = tradingSystem.purchaseShoppingCart(user);
        return modelMapper.map(receipt, ReceiptDto.class);
    }

    /////////////////////////////////// convectors ////////////////////
    private List<ReceiptDto> convertReceiptDtoList(List<Receipt> receipts) {
        List<ReceiptDto> receiptDtos = new ArrayList<>();
        modelMapper.map(receipts, receiptDtos);
        return receiptDtos;
    }

    private List<ProductDto> convertProductDtoList(List<Product> products){
        List<ProductDto> productDtos = new ArrayList<>();
        modelMapper.map(products, productDtos);
        return productDtos;
    }

    private List<Product> converterProductsList(List<ProductDto> productDtos) {
        List<Product> products = new ArrayList<>();
        modelMapper.map(productDtos, products);
        return products;
    }
}

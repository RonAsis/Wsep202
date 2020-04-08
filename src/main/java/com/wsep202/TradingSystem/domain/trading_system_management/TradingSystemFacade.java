package com.wsep202.TradingSystem.domain.trading_system_management;


import com.wsep202.TradingSystem.domain.factory.FactoryObjects;
import com.wsep202.TradingSystem.service.user_service.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.lang.reflect.Type;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class TradingSystemFacade {

    /**
     * inorder talk with the system
     */
    private final TradingSystem tradingSystem;

    /**
     * inorder to convert dto to object
     */
    private final ModelMapper modelMapper;

    /**
     * inorder to create new objects
     */
    private final FactoryObjects factoryObjects;

    /**
     * view purchase history of user logged in
     * @param userName - must be logged in
     * @return all the receipt of the user
     */
    public List<ReceiptDto> viewPurchaseHistory(@NotBlank String userName) {
        UserSystem user = tradingSystem.getUser(userName);
        List<Receipt> receipts = user.getReceipts();
        return convertReceiptDtoList(receipts);
    }

    /**
     * administrator view purchase history of store
     * @param administratorUsername the user name of the admin
     * @param storeId - the store id of the store that want view purchase history
     * @return all the receipt of the store
     */
    public List<ReceiptDto> viewPurchaseHistory( @NotBlank String administratorUsername, int storeId) {
        Store store = tradingSystem.getStore(administratorUsername, storeId);
        List<Receipt> receipts = store.getReceipts();
        return convertReceiptDtoList(receipts);
    }

    /**
     * administrator view purchase history of user
     * @param administratorUsername the user name of the admin
     * @param userName - the userName that want view purchase history
     * @return all the receipt of the user
     */
    public List<ReceiptDto> viewPurchaseHistory( @NotBlank String administratorUsername,@NotBlank  String userName) {
        UserSystem userByAdmin = tradingSystem.getUserByAdmin(administratorUsername, userName);
        return convertReceiptDtoList(userByAdmin.getReceipts());
    }

    /**
     * Manager view purchase history of store
     * @param managerUsername - the manager Username of the store manger that want view purchase history
     * @param storeId - the store that want view the purchase history
     * @return all the receipt of the store
     */
    public List<ReceiptDto> viewPurchaseHistoryOfManager(@NotBlank String managerUsername, int storeId) {
        UserSystem mangerStore = tradingSystem.getUser(managerUsername);
        return convertReceiptDtoList(mangerStore.getManagerStore(storeId).getReceipts());
    }

    /**
     * Owner view purchase history of store
     * @param ownerUserName the owner Username of the store manger that want view purchase history
     * @param storeId - the store that want view the purchase history
     * @return all the receipt of the store
     */
    public List<ReceiptDto> viewPurchaseHistoryOfOwner(@NotBlank String ownerUserName, int storeId) {
        UserSystem user = tradingSystem.getUser(ownerUserName);
        return convertReceiptDtoList(user.getOwnerStore(storeId).getReceipts());
    }

    /**
     * add product to store
     * @param ownerUsername the username of the owner store
     * @param storeId - of the store that want add product
     * @param productName - the name of the new product
     * @param category - the category of the product
     * @param amount - the amount of the product
     * @param cost - the cost of the product
     * @return true if succeed
     */
    public boolean addProduct( @NotBlank String ownerUsername, int storeId, @NotBlank String productName, @NotBlank String category,
                               int amount, double cost) {
        UserSystem user = tradingSystem.getUser(ownerUsername);
        Store ownerStore = user.getOwnerStore(storeId);
        ProductCategory productCategory = ProductCategory.getProductCategory(category);
        Product product = new Product(productName, productCategory, amount, cost, storeId);
        return ownerStore.addNewProduct(user, product);
    }

    /**
     * delete product form store
     * @param ownerUsername the username of the owner store
     * @param storeId - of the store that want delete product
     * @param productSn - the sn of the product
     * @return true if succeed
     */
    public boolean deleteProductFromStore(@NotBlank String ownerUsername, int storeId, int productSn) {
        UserSystem user = tradingSystem.getUser(ownerUsername);
        Store ownerStore = user.getOwnerStore(storeId);
        return ownerStore.removeProductFromStore(user, productSn);
    }

    /**
     * edit product
     * @param ownerUsername the username of the owner store
     * @param storeId - of the store that want edit product
     * @param productSn - the sn of the product
     * @param productName - the name of the product
     * @param category - the category of the product
     * @param amount - the amount of the product
     * @param cost - the cost of the product
     * @return true if succeed
     */
    public boolean editProduct(@NotBlank String ownerUsername, int storeId, int productSn, @NotBlank  String productName,
                               @NotBlank String category, int amount, double cost) {
        UserSystem user = tradingSystem.getUser(ownerUsername);
        Store ownerStore = user.getOwnerStore(storeId);
        return ownerStore.editProduct(user, productSn, productName, category, amount, cost);
    }

    /**
     * add new owner to store
     * @param ownerUsername the username of the owner store
     * @param storeId - of the store that want add new owner
     * @param newOwnerUsername - the new owner
     * @return true if succeed
     */
    public boolean addOwner(@NotBlank String ownerUsername, int storeId, @NotBlank String newOwnerUsername) {
        UserSystem ownerUser = tradingSystem.getUser(ownerUsername);
        UserSystem newOwnerUser = tradingSystem.getUser(newOwnerUsername);
        Store ownerStore = ownerUser.getOwnerStore(storeId);
        return ownerStore.addOwner(ownerUser, newOwnerUser);
    }

    /**
     * add manger to the store with the default permission
     * @param ownerUsername the username of the owner store
     * @param storeId - of the store that want add new owner
     * @param newManagerUsername - the new manger
     * @return true if succeed
     */
    public boolean addManager(@NotBlank String ownerUsername, int storeId, @NotBlank String newManagerUsername) {
        UserSystem ownerUser = tradingSystem.getUser(ownerUsername);
        UserSystem newManagerUser = tradingSystem.getUser(newManagerUsername);
        Store ownedStore = ownerUser.getOwnerStore(storeId);
        return ownedStore.addManager(ownerUser, newManagerUser);
    }

    /**
     * add permission the manger in the store
     * @param ownerUsername the username of the owner store
     * @param storeId - of the store that want add permission the manger
     * @param managerUserName - the user name of the manger
     * @param permission - the new permission
     * @return true if succeed
     */
    public boolean addPermission(@NotBlank String ownerUsername, int storeId, @NotBlank  String managerUserName,@NotBlank  String permission) {
        UserSystem ownerUser = tradingSystem.getUser(ownerUsername);
        Store ownedStore = ownerUser.getOwnerStore(storeId);
        UserSystem managerStore = ownedStore.getManager(ownerUser, managerUserName);
        StorePermission storePermission = StorePermission.getStorePermission(permission);
        return ownedStore.addPermissionToManager(ownerUser, managerStore, storePermission);
    }

    /**
     * remove manger from the store by the owner that appointed him
     * @param ownerUsername owner that appointed the manger
     * @param storeId - the id that of the store that want remove the manger
     * @param managerUsername - the manger that want remove
     * @return true if succeed
     */
    public boolean removeManager(@NotBlank String ownerUsername, int storeId, @NotBlank String managerUsername) {
        UserSystem ownerUser = tradingSystem.getUser(ownerUsername);
        Store ownedStore = ownerUser.getOwnerStore(storeId);
        UserSystem managerStore = ownedStore.getManager(ownerUser, managerUsername);
        return ownedStore.removeManager(ownerUser, managerStore);
    }

    /**
     * the user name logout from the system
     * @param username - the username that want logout
     * @return true if succeed
     */
    public boolean logout(@NotBlank String username) {
        UserSystem user = tradingSystem.getUser(username);
        return user.logout();
    }

    /**
     * open new store
     * @param usernameOwner - the user that open the store
     * @param purchasePolicyDto - the purchase policy
     * @param discountPolicyDto - the discount Policy
     * @param discountType - the discount type
     * @param purchaseType - the purchase type
     * @param storeName - the name of the new store
     * @return true if succeed
     */
    public boolean openStore(@NotBlank String usernameOwner, @NotNull PurchasePolicyDto purchasePolicyDto, @NotNull DiscountPolicyDto discountPolicyDto,
                             @NotBlank String discountType, @NotBlank String purchaseType, @NotBlank String storeName) {
        UserSystem user = tradingSystem.getUser(usernameOwner);
        DiscountType discountTypeObj = DiscountType.getDiscountType(discountType);
        PurchaseType purchaseTypeObj = PurchaseType.getPurchaseType(purchaseType);
        PurchasePolicy purchasePolicy = modelMapper.map(purchasePolicyDto, PurchasePolicy.class);
        DiscountPolicy discountPolicy = modelMapper.map(discountPolicyDto, DiscountPolicy.class);
        return tradingSystem.openStore(user, discountTypeObj, purchaseTypeObj, purchasePolicy, discountPolicy, storeName);
    }

    /**
     * register new user to the system
     * @param userName - the new username
     * @param password - the password of the user
     * @param firstName - the first name of the new user
     * @param lastName - the last name of the new user
     * @return true if succeed
     */
    public boolean registerUser(@NotBlank String userName,@NotBlank String password, @NotBlank String firstName,@NotBlank String lastName) {
        UserSystem userSystem = factoryObjects.createSystemUser(userName, password, firstName, lastName);
        return tradingSystem.registerNewUser(userSystem);
    }

    /**
     * user login to the system
     * @param userName - the username need to be register to the system for suc
     * @param password - the password must be the correct password of the user
     * @return true if succeed
     */
    public boolean login(@NotBlank String userName,@NotBlank String password) {
        UserSystem user = tradingSystem.getUser(userName);
        return tradingSystem.login(user, false, password);
    }

    /**
     * view the store info
     * @param storeId - the store id that want to see the details
     * @return the store details
     */
    public StoreDto viewStoreInfo(int storeId) {
        Store store = tradingSystem.getStore(storeId);
        return modelMapper.map(store, StoreDto.class);
    }

    /**
     * view product of specific store
     * @param storeId - the store id that include the product
     * @param productId - the product id that want view the product details
     * @return the product details
     */
    public ProductDto viewProduct(int storeId, int productId) {
        Store store = tradingSystem.getStore(storeId);
        Product product = store.getProduct(productId);
        return modelMapper.map(product, ProductDto.class);
    }

    /**
     * search product by name
     * @param productName - the product name that want to search
     * @return list of all the product with this name
     */
    public List<ProductDto> searchProductByName(@NotBlank String productName) {
        List<Product> products = tradingSystem.searchProductByName(productName);
        return convertProductDtoList(products);
    }

    /**
     * search product by category
     * @param category - the category of product that want to search
     * @return list of all the products that belong to this category
     */
    public List<ProductDto> searchProductByCategory(@NotBlank String category) {
        ProductCategory productCategory = ProductCategory.getProductCategory(category);
        List<Product> products = tradingSystem.searchProductByCategory(productCategory);
        return convertProductDtoList(products);
    }

    /**
     * search product by keyWords
     * @param keyWords - the keyWords that want search with
     * @return list of all the products that include the keyWords
     */
    public List<ProductDto> searchProductByKeyWords(@NotNull List<@NotBlank String> keyWords) {
        List<Product> products = tradingSystem.searchProductByKeyWords(keyWords);
        return convertProductDtoList(products);
    }

    /**
     * filter by range price
     * @param productDtos the list of products
     * @param minPrice - the minPrice price
     * @param maxPrice - the maxPrice price
     * @return list of all the products filtered by range price
     */
    public List<ProductDto> filterByRangePrice(@NotNull List< @NotNull ProductDto> productDtos, double minPrice, double maxPrice) {
        List<Product> products = converterProductsList(productDtos);
        List<Product> productsFiltered = tradingSystem.filterByRangePrice(products, minPrice, maxPrice);
        return convertProductDtoList(productsFiltered);
    }

    /**
     * filter by product rank
     * @param productDtos the list of products
     * @param rank - the product rank
     * @return list of all the products filtered by the product rank
     */
    public List<ProductDto> filterByProductRank(@NotNull List<@NotNull ProductDto> productDtos, int rank) {
        List<Product> products = converterProductsList(productDtos);
        List<Product> productsFiltered = tradingSystem.filterByProductRank(products, rank);
        return convertProductDtoList(productsFiltered);
    }

    /**
     * filter by store rank
     * @param productDtos the list of products
     * @param rank the rank of the store
     * @return list of all the products filtered by the store rank
     */
    public List<ProductDto> filterByStoreRank(@NotNull List<@NotNull ProductDto> productDtos, int rank) {
        List<Product> products = converterProductsList(productDtos);
        List<Product> productsFiltered = tradingSystem.filterByStoreRank(products, rank);
        return convertProductDtoList(productsFiltered);
    }

    /**
     * filter by category
     * @param productDtos the list of products
     * @param category the category of the product
     * @return list of all the products filtered by the category
     */
    public List<ProductDto> filterByStoreCategory(@NotNull List<@NotNull ProductDto> productDtos,@NotBlank String category) {
        List<Product> products = converterProductsList(productDtos);
        ProductCategory productCategory = ProductCategory.getProductCategory(category);
        List<Product> productsFiltered = tradingSystem.filterByStoreCategory(products, productCategory);
        return convertProductDtoList(productsFiltered);
    }

    /**
     *
     * @param username the username that want save ShoppingBag
     * @param storeId the storeId that the product belong to
     * @param productSn - the sn of the prodcut
     * @param amount - the amount that want save
     * @return true if succeed
     */
    public boolean saveProductInShoppingBag(@NotBlank String username, int storeId, int productSn, int amount) {
        UserSystem user = tradingSystem.getUser(username);
        Store store = tradingSystem.getStore(storeId);
        Product product = store.getProduct(productSn);
        return user.saveProductInShoppingBag(store, product, amount);
    }

    /**
     * view products in shopping cart
     * @param username the username that want view the ShoppingBag
     * @return shopping bag
     */
    public ShoppingCartDto viewProductsInShoppingCart(@NotBlank String username) {
        UserSystem user = tradingSystem.getUser(username);
        ShoppingCart shoppingCart = user.getShoppingCart();
        return modelMapper.map(shoppingCart, ShoppingCartDto.class);
    }

    /**
     * remove product in shopping bag
     * @param username the username that want remove product from the ShoppingBag
     * @param storeId - the id of the store that the product belong to
     * @param productSn - the sn of the product
     * @return true if succeed
     */
    public boolean removeProductInShoppingBag(@NotBlank String username, int storeId, int productSn) {
        UserSystem user = tradingSystem.getUser(username);
        Store store = tradingSystem.getStore(storeId);
        Product product = store.getProduct(productSn);
        return user.removeProductInShoppingBag(store, product);
    }

    /**
     * purchase shopping cart use for guest
     * @param shoppingCartDto the shopping cart
     * @return the receipt
     */
    public ReceiptDto purchaseShoppingCart(@NotNull ShoppingCartDto shoppingCartDto) {
        ShoppingCart shoppingCart = modelMapper.map(shoppingCartDto, ShoppingCart.class);
        Receipt receipt = tradingSystem.purchaseShoppingCart(shoppingCart);
        return modelMapper.map(receipt, ReceiptDto.class);
    }

    /**
     * purchase shopping cart of resisted user
     * @param username - the username that want purchase shopping cart
     * @return the receipt
     */
    public ReceiptDto purchaseShoppingCart(@NotBlank String username) {
        UserSystem user = tradingSystem.getUser(username);
        Receipt receipt = tradingSystem.purchaseShoppingCart(user);
        return modelMapper.map(receipt, ReceiptDto.class);
    }

    /////////////////////////////////// convectors ///////////////////////

    /**
     * converter of Receipt list to ReceiptDto list
     * @param receipts - list of receipts
     * @return list of ReceiptDto
     */
    private List<ReceiptDto> convertReceiptDtoList(@NotNull List<@NotNull Receipt> receipts) {
        Type listType = new TypeToken<List<ReceiptDto>>(){}.getType();
        return modelMapper.map(receipts, listType);
    }

    /**
     * converter of Product list to ProductDto list
     * @param products - list of products
     * @return  list of ProductDto
     */
    private List<ProductDto> convertProductDtoList(@NotNull List<@NotNull Product> products) {
        Type listType = new TypeToken<List<ProductDto>>(){}.getType();
        return modelMapper.map(products, listType);
    }

    /**
     * converter of ProductDto list to Product list
     * @param productDtos - list of productDtos
     * @return list of products
     */
    private List<Product> converterProductsList(@NotNull List<@NotNull ProductDto> productDtos) {
        Type listType = new TypeToken<List<Product>>(){}.getType();
        return modelMapper.map(productDtos, listType);
    }
}

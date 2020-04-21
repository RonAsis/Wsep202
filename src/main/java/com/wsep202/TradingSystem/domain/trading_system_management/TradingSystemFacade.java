package com.wsep202.TradingSystem.domain.trading_system_management;


import com.wsep202.TradingSystem.domain.exception.*;
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
import java.util.Set;

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
     * a function that creates a list of storeDto's from list of stores.
     * @return - list of storeDto's.
     */
    public List<StoreDto> getStoresDtos() {
        Type listType = new TypeToken<List<StoreDto>>(){}.getType();
        return modelMapper.map(tradingSystem.getStoresList(),listType);
    }

    /**
     * a function that creates a list of userDto's from list of users.
     * @return - list of userDto's.
     */
    public List<UserSystemDto> getUsersDtos() {
        Type listType = new TypeToken<List<UserSystemDto>>(){}.getType();
        return modelMapper.map(tradingSystem.getUsersList(),listType);
    }

    /**
     * a function that creates a list of userDto's from list of users.
     * @return - list of userDto's.
     */
    public List<UserSystemDto> getAdministratoesDtos() {
        Type listType = new TypeToken<List<UserSystemDto>>(){}.getType();
        return modelMapper.map(tradingSystem.getAdministratorsList(),listType);
    }

    /**
     * view purchase history of user logged in
     * @param userName - must be logged in
     * @return all the receipt of the user
     */
    public List<ReceiptDto> viewPurchaseHistory(@NotBlank String userName) {
       try{
           UserSystem user = tradingSystem.getUser(userName);   //get registered user by his username
           List<Receipt> receipts = user.getReceipts(); //get user receipts
           return convertReceiptDtoList(receipts);
       }catch (UserDontExistInTheSystemException exception){
           return null;
       }
    }

    /**
     * administrator view purchase history of store
     * @param administratorUsername the user name of the admin
     * @param storeId - the store id of the store that want view purchase history
     * @return all the receipt of the store
     */
    public List<ReceiptDto> viewPurchaseHistory( @NotBlank String administratorUsername, int storeId) {
        try {
            //get the store if the user has admin permissions (it is admin)
            Store store = tradingSystem.getStoreByAdmin(administratorUsername, storeId);
            List<Receipt> receipts = store.getReceipts();
            return convertReceiptDtoList(receipts);
        }catch (NotAdministratorException exception){   //admin with received name doesn't exist in system.
            return null;
        }
    }

    /**
     * administrator view purchase history of user
     * @param administratorUsername the user name of the admin
     * @param userName - the userName that want view purchase history
     * @return all the receipt of the user
     */
    public List<ReceiptDto> viewPurchaseHistory( @NotBlank String administratorUsername,@NotBlank  String userName) {
       try{
           UserSystem userByAdmin = tradingSystem.getUserByAdmin(administratorUsername, userName);
           return convertReceiptDtoList(userByAdmin.getReceipts());
       }catch (NotAdministratorException exception){
           return null;
       }
    }

    /**
     * Manager view purchase history of store
     * @param managerUsername - the manager Username of the store manger that want view purchase history
     * @param storeId - the store that want view the purchase history
     * @return all the receipt of the store
     */
    public List<ReceiptDto> viewPurchaseHistoryOfManager(@NotBlank String managerUsername, int storeId) {
        try{
            //get the registered user with username
            UserSystem mangerStore = tradingSystem.getUser(managerUsername);
            try{
                //return the receipts of the store belongs to the received manager
                return convertReceiptDtoList(mangerStore.getManagerStore(storeId).getReceipts());
            }catch (NoManagerInStoreException exception2){  //the user is not manager of the store received
                return null;
            }
        }catch (UserDontExistInTheSystemException exception1){ //the user is not registered in the system
            return null;
        }
    }

    /**
     * Owner view purchase history of store
     * @param ownerUserName the owner Username of the store manger that want view purchase history
     * @param storeId - the store that want view the purchase history
     * @return all the receipt of the store
     */
    public List<ReceiptDto> viewPurchaseHistoryOfOwner(@NotBlank String ownerUserName, int storeId) {
       try{
           UserSystem user = tradingSystem.getUser(ownerUserName);
           try{
               return convertReceiptDtoList(user.getOwnerStore(storeId).getReceipts());
           }catch (NoOwnerInStoreException exception2){ //user is not an owner of store with id storeId
               return null;
           }
       }catch (UserDontExistInTheSystemException exception1){   //user is not registered in the system
           return null;
       }

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
    public boolean addProduct( @NotBlank String ownerUsername, int storeId,
                               @NotBlank String productName, @NotBlank String category,
                               int amount, double cost) {
        try{
            UserSystem user = tradingSystem.getUser(ownerUsername); //get registered user with ownerUsername
            try{
                Store ownerStore = user.getOwnerStore(storeId); //verify he owns store with storeId
                try{
                    //convert to a category we can add to the product
                    ProductCategory productCategory = ProductCategory.getProductCategory(category);
                    Product product = new Product(productName, productCategory, amount, cost, storeId);
                    return ownerStore.addNewProduct(user, product);
                }catch (CategoryDoesntExistException exception2){   //invalid category received
                    return false;
                }

            }catch (NoOwnerInStoreException exception1){    //the user is not an owner in the store
                return false;
            }
        }catch (UserDontExistInTheSystemException exception){   //the user is not registered in the system
            return false;
        }
    }

    /**
     * delete product form store
     * @param ownerUsername the username of the owner store
     * @param storeId - of the store that want delete product
     * @param productSn - the sn of the product
     * @return true if succeed
     */
    public boolean deleteProductFromStore(@NotBlank String ownerUsername, int storeId, int productSn) {
        try {
            UserSystem user = tradingSystem.getUser(ownerUsername); //get the registered user
            try{
                Store ownerStore = user.getOwnerStore(storeId); //verify he is owner of the store
                return ownerStore.removeProductFromStore(user, productSn);
            }catch (NoOwnerInStoreException exception2){    //the user is not an owner in the storeId
                return false;
            }
        }catch (UserDontExistInTheSystemException exception){   //user not registered in the system
            return false;
        }
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
        try {
            UserSystem user = tradingSystem.getUser(ownerUsername);
            try{
                Store ownerStore = user.getOwnerStore(storeId);
                return ownerStore.editProduct(user, productSn, productName, category, amount, cost);
            }catch (NoOwnerInStoreException exception1){
                return false;
            }
        }catch (UserDontExistInTheSystemException exception){
            return false;
        }
    }

    /**
     * add new owner to store
     * @param ownerUsername the username of the owner store
     * @param storeId - of the store that want add new owner
     * @param newOwnerUsername - the new owner
     * @return true if succeed
     */
    public boolean addOwner(@NotBlank String ownerUsername, int storeId, @NotBlank String newOwnerUsername) {
        try{
            UserSystem ownerUser = tradingSystem.getUser(ownerUsername);
            UserSystem newOwnerUser = tradingSystem.getUser(newOwnerUsername);
            try{
                Store ownerStore = ownerUser.getOwnerStore(storeId);
                return tradingSystem.addOwnerToStore(ownerStore, ownerUser, newOwnerUser);
            }catch (NoOwnerInStoreException exception1){
                return false;
            }
        }catch (UserDontExistInTheSystemException exception){
            return false;
        }
    }

    /**
     * add manger to the store with the default permission
     * @param ownerUsername the username of the owner store
     * @param storeId - of the store that want add new owner
     * @param newManagerUsername - the new manger
     * @return true if succeed
     */
    public boolean addManager(@NotBlank String ownerUsername, int storeId, @NotBlank String newManagerUsername) {
        try{
            UserSystem ownerUser = tradingSystem.getUser(ownerUsername);
            UserSystem newManagerUser = tradingSystem.getUser(newManagerUsername);
            try{
                Store ownedStore = ownerUser.getOwnerStore(storeId);
                return tradingSystem.addMangerToStore(ownedStore, ownerUser, newManagerUser);
            }catch (NoOwnerInStoreException e1){
                return false;
            }
        }catch (UserDontExistInTheSystemException e){
            return false;
        }
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
        try{
            UserSystem ownerUser = tradingSystem.getUser(ownerUsername);
            try{
                Store ownedStore = ownerUser.getOwnerStore(storeId);
                try{
                    UserSystem managerStore = ownedStore.getManager(ownerUser, managerUserName);
                    try{
                        StorePermission storePermission = StorePermission.getStorePermission(permission);
                        return ownedStore.addPermissionToManager(ownerUser, managerStore, storePermission);
                    }catch (PermissionException e4){
                        return false;
                    }

                }catch (NoManagerInStoreException e3){
                    return false;
                }

            }catch (NoOwnerInStoreException e2){
                return false;
            }

        }catch (UserDontExistInTheSystemException e1){
            return false;
        }

    }

    /**
     * remove manger from the store by the owner that appointed him
     * @param ownerUsername owner that appointed the manger
     * @param storeId - the id that of the store that want remove the manger
     * @param managerUsername - the manger that want remove
     * @return true if succeed
     */
    public boolean removeManager(@NotBlank String ownerUsername, int storeId, @NotBlank String managerUsername) {
        try{
            UserSystem ownerUser = tradingSystem.getUser(ownerUsername); //get registered user
            try{
                Store ownedStore = ownerUser.getOwnerStore(storeId);    //verify the remover is owner
                try {
                    //get the manager to renove and remove him
                    UserSystem managerStore = ownedStore.getManager(ownerUser, managerUsername);
                    return tradingSystem.removeManager(ownedStore, ownerUser, managerStore);
                }catch (NoManagerInStoreException e2){
                    return false;
                }
            }catch (NoOwnerInStoreException e1){
                return false;
            }
        }catch (UserDontExistInTheSystemException e){
            return false;
        }

    }

    /**
     * the user name logout from the system
     * @param username - the username that want logout
     * @return true if succeed
     */
    public boolean logout(@NotBlank String username) {
        try{
            UserSystem user = tradingSystem.getUser(username);
            return user.logout();
        }catch (UserDontExistInTheSystemException e){ //cant logout not registered user
            return false;
        }
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
        try{
            UserSystem user = tradingSystem.getUser(usernameOwner);
            try{
                DiscountType discountTypeObj = DiscountType.getDiscountType(discountType);
                try{
                    PurchaseType purchaseTypeObj = PurchaseType.getPurchaseType(purchaseType);
                    PurchasePolicy purchasePolicy = modelMapper.map(purchasePolicyDto, PurchasePolicy.class);
                    DiscountPolicy discountPolicy = modelMapper.map(discountPolicyDto, DiscountPolicy.class);
                    return tradingSystem.openStore(user, purchasePolicy, discountPolicy, storeName);
                }catch (PurchaseTypeDontExistException e1){
                    return false;
                }
            }catch (DiscountTypeDontExistException e){
                return false;
            }
        }catch (UserDontExistInTheSystemException e){
            return false;
        }
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
        UserSystem userSystem = factoryObjects.createSystemUser(userName,password, firstName, lastName);
        return tradingSystem.registerNewUser(userSystem);
    }


    /**
     * user login to the system
     * @param userName - the username need to be register to the system for suc
     * @param password - the password must be the correct password of the user
     * @return true if succeed
     */
    public boolean login(@NotBlank String userName,@NotBlank String password) {
        try{
            UserSystem user = tradingSystem.getUser(userName);  //get the  registered user with that username
            return tradingSystem.login(user, false, password);
        }catch (UserDontExistInTheSystemException e){
            return false;
        }
    }

    /**
     * view the store info
     * @param storeId - the store id that want to see the details
     * @return the store details
     */
    public StoreDto viewStoreInfo(int storeId) {
        try{
            Store store = tradingSystem.getStore(storeId);
            return modelMapper.map(store, StoreDto.class);
        }catch (StoreDontExistsException e){
            return null;
        }
    }

    /**
     * view product of specific store
     * @param storeId - the store id that include the product
     * @param productId - the product id that want view the product details
     * @return the product details
     */
    public ProductDto viewProduct(int storeId, int productId) {
        try{
            Store store = tradingSystem.getStore(storeId);
            try{
                Product product = store.getProduct(productId);
                return modelMapper.map(product, ProductDto.class);
            }catch (ProductDoesntExistException e1){
                return null;
            }
        }catch (StoreDontExistsException e){
            return null;
        }

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
        try{
            ProductCategory productCategory = ProductCategory.getProductCategory(category);
            List<Product> products = tradingSystem.searchProductByCategory(productCategory);
            return convertProductDtoList(products);
        }catch (CategoryDoesntExistException e){
            return null;
        }
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
        try{
            ProductCategory productCategory = ProductCategory.getProductCategory(category);
            List<Product> productsFiltered = tradingSystem.filterByStoreCategory(products, productCategory);
            return convertProductDtoList(productsFiltered);
        }catch (CategoryDoesntExistException e){
            return null;
        }

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
        try{
            UserSystem user = tradingSystem.getUser(username);
            try{
                Store store = tradingSystem.getStore(storeId);
                try{
                    Product product = store.getProduct(productSn);
                    return user.saveProductInShoppingBag(store, product, amount);
                }catch (ProductDoesntExistException e2){
                    return false;
                }
            }catch (StoreDontExistsException e1){
                return false;
            }

        }catch (UserDontExistInTheSystemException e){
            return false;
        }
    }

    /**
     * view products in shopping cart
     * @param username the username that want view the ShoppingBag
     * @return shopping bag
     */
    public ShoppingCartDto viewProductsInShoppingCart(@NotBlank String username) {
        try{
            UserSystem user = tradingSystem.getUser(username);
            ShoppingCart shoppingCart = user.getShoppingCart();
            return modelMapper.map(shoppingCart, ShoppingCartDto.class);
        }catch (UserDontExistInTheSystemException e){
           return null;
        }
    }

    /**
     * remove product in shopping bag
     * @param username the username that want remove product from the ShoppingBag
     * @param storeId - the id of the store that the product belong to
     * @param productSn - the sn of the product
     * @return true if succeed
     */
    public boolean removeProductInShoppingBag(@NotBlank String username, int storeId, int productSn) {
        try{
            UserSystem user = tradingSystem.getUser(username);
            try{
                Store store = tradingSystem.getStore(storeId);
                try{
                    Product product = store.getProduct(productSn);
                    return user.removeProductInShoppingBag(store, product);
                }catch (ProductDoesntExistException e2){
                    return false;
                }
            }catch (StoreDontExistsException e1){
                return false;
            }
        }catch (UserDontExistInTheSystemException e){
            return false;
        }
    }

    /**
     * purchase shopping cart use for guest
     * @param shoppingCartDto the shopping cart
     * @return the receipt
     */
    public ReceiptDto purchaseShoppingCart(@NotNull ShoppingCartDto shoppingCartDto, PaymentDetailsDto paymentDetailsDto, BillingAddressDto billingAddressDto) {
        ShoppingCart shoppingCart = modelMapper.map(shoppingCartDto, ShoppingCart.class);
        PaymentDetails paymentDetails = modelMapper.map(paymentDetailsDto, PaymentDetails.class);
        BillingAddress billingAddress = modelMapper.map(billingAddressDto, BillingAddress.class);
        List<Receipt> receipts = tradingSystem.purchaseShoppingCart(shoppingCart, paymentDetails, billingAddress);
        return modelMapper.map(receipts, ReceiptDto.class);
    }

    /**
     * purchase shopping cart of resisted user
     * @param username - the username that want purchase shopping cart
     * @return the receipt
     */
    public ReceiptDto purchaseShoppingCart(@NotBlank String username, PaymentDetailsDto paymentDetailsDto, BillingAddressDto billingAddressDto) {
        try{
            UserSystem user = tradingSystem.getUser(username);
            PaymentDetails paymentDetails = modelMapper.map(paymentDetailsDto, PaymentDetails.class);
            BillingAddress billingAddress = modelMapper.map(billingAddressDto, BillingAddress.class);
            List<Receipt> receipts = tradingSystem.purchaseShoppingCart(paymentDetails,billingAddress, user);
            return modelMapper.map(receipts, ReceiptDto.class);
        }catch (UserDontExistInTheSystemException e){
            return null;
        }
    }
    /////////////////////////////////test utilities////////////////////////
    /**
     * This method is used for the tear down section in the acceptance tests
     */
    public void clearDS(){
        this.tradingSystem.clearDS();
    }

    //////////////////////////////// converters ///////////////////////////

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

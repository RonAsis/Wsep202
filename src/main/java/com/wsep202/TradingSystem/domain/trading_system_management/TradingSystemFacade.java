package com.wsep202.TradingSystem.domain.trading_system_management;


import com.wsep202.TradingSystem.domain.exception.*;
import com.wsep202.TradingSystem.domain.factory.FactoryObjects;
import com.wsep202.TradingSystem.domain.trading_system_management.discount.*;
import com.wsep202.TradingSystem.domain.trading_system_management.notification.Notification;
import com.wsep202.TradingSystem.domain.trading_system_management.notification.Observer;
import com.wsep202.TradingSystem.domain.trading_system_management.purchase.PurchasePolicy;
import com.wsep202.TradingSystem.dto.*;
import com.wsep202.TradingSystem.service.ServiceFacade;
import javafx.util.Pair;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Stream;

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

    private final ServiceFacade serviceFacade;

    /**
     * view purchase history of user logged in
     *
     * @param userName - must be logged in
     * @return all the receipt of the user
     */
    public List<ReceiptDto> viewPurchaseHistory(@NotBlank String userName, UUID uuid) {
        try {
            UserSystem user = tradingSystem.getUser(userName, uuid);   //get registered user by his username
            List<Receipt> receipts = user.getReceipts(); //get user receipts
            return convertReceiptList(receipts);
        } catch (TradingSystemException e) {
            log.error("Tried to view his purchase history and failed", e);
            return null;
        }
    }

    /**
     * administrator view purchase history of store
     *
     * @param administratorUsername the user name of the admin
     * @param storeId               - the store id of the store that want view purchase history
     * @return all the receipt of the store
     */
    public List<ReceiptDto> viewPurchaseHistory(@NotBlank String administratorUsername, int storeId, UUID uuid) {
        try {
            //get the store if the user has admin permissions (it is admin)
            Store store = tradingSystem.getStoreByAdmin(administratorUsername, storeId, uuid);
            List<Receipt> receipts = store.getReceipts();
            return convertReceiptList(receipts);
        } catch (TradingSystemException e) {
            log.error("Tried to view purchase history of store failed", e);
            return null;
        }
    }

    /**
     * administrator view purchase history of user
     *
     * @param administratorUsername the user name of the admin
     * @param userName              - the userName that want view purchase history
     * @return all the receipt of the user
     */
    public List<ReceiptDto> viewPurchaseHistory(@NotBlank String administratorUsername, @NotBlank String userName, UUID uuid) {
        try {
            UserSystem userByAdmin = tradingSystem.getUserByAdmin(administratorUsername, userName, uuid);
            return convertReceiptList(userByAdmin.getReceipts());
        } catch (TradingSystemException e) {
            log.error("Tried to view purchase history of user and failed", e);
            return null;
        }
    }

    /**
     * Manager view purchase history of store
     *
     * @param managerUsername - the manager Username of the store manger that want view purchase history
     * @param storeId         - the store that want view the purchase history
     * @param uuid
     * @return all the receipt of the store
     */
    public List<ReceiptDto> viewPurchaseHistoryOfManager(@NotBlank String managerUsername, int storeId, UUID uuid) {
        try {
            //get the registered user with username
            UserSystem mangerStore = tradingSystem.getUser(managerUsername, uuid);
            return convertReceiptList(mangerStore.getManagerStore(storeId).getReceipts());
        } catch (TradingSystemException e) {
            log.error("The user is not a manager of the store ,so cant see purchase history of store", e);
            return null;
        }
    }

    /**
     * Owner view purchase history of store
     *
     * @param ownerUserName the owner Username of the store manger that want view purchase history
     * @param storeId       - the store that want view the purchase history
     * @return all the receipt of the store
     */
    public List<ReceiptDto> viewPurchaseHistoryOfOwner(@NotBlank String ownerUserName, int storeId, UUID uuid) {
        try {
            UserSystem user = tradingSystem.getUser(ownerUserName, uuid);
            return convertReceiptList(user.getOwnerStore(storeId).getReceipts());
        } catch (TradingSystemException e) {
            log.error("Can't see history of the store", e);
            return null;
        }
    }

    /**
     * add product to store
     *
     * @param ownerUsername the username of the owner store
     * @param storeId       - of the store that want add product
     * @param productName   - the name of the new product
     * @param category      - the category of the product
     * @param amount        - the amount of the product
     * @param cost          - the cost of the product
     * @return true if succeed
     */
    public ProductDto addProduct(@NotBlank String ownerUsername, int storeId,
                              @NotBlank String productName, @NotBlank String category,
                              int amount, double cost, UUID uuid) {
        try {
            UserSystem user = tradingSystem.getUser(ownerUsername, uuid); //get registered user with ownerUsername
            Store ownerStore = user.getOwnerOrManagerStore(storeId); //verify he owns store with storeId
            //convert to a category we can add to the product
            ProductCategory productCategory = ProductCategory.getProductCategory(category);
            Product product = new Product(productName, productCategory, amount, cost, storeId);
            return ownerStore.addNewProduct(user, product)? modelMapper.map(product, ProductDto.class) : null;
        } catch (TradingSystemException e) {
            log.error("add product failed", e);
            return null;
        }
    }

    /**
     * delete product form store
     *
     * @param ownerUsername the username of the owner store
     * @param storeId       - of the store that want delete product
     * @param productSn     - the sn of the product
     * @return true if succeed
     */
    public boolean deleteProductFromStore(@NotBlank String ownerUsername, int storeId, int productSn, UUID uuid) {
        try {
            UserSystem user = tradingSystem.getUser(ownerUsername, uuid); //get the registered user
            Store ownerStore = user.getOwnerOrManagerStore(storeId); //verify he is owner of the store
            return ownerStore.removeProductFromStore(user, productSn);
        } catch (TradingSystemException e) {
            log.error("deleteProduct from store failed", e);
            return false;
        }
    }

    /**
     * edit product
     *
     * @param ownerUsername the username of the owner store
     * @param storeId       - of the store that want edit product
     * @param productSn     - the sn of the product
     * @param productName   - the name of the product
     * @param category      - the category of the product
     * @param amount        - the amount of the product
     * @param cost          - the cost of the product
     * @param uuid
     * @return true if succeed
     */
    public boolean editProduct(@NotBlank String ownerUsername, int storeId, int productSn, @NotBlank String productName,
                               @NotBlank String category, int amount, double cost, UUID uuid) {
        try {
            UserSystem user = tradingSystem.getUser(ownerUsername, uuid);
            Store ownerStore = user.getOwnerOrManagerStore(storeId);
            return ownerStore.editProduct(user, productSn, productName, category, amount, cost);
        } catch (TradingSystemException e) {
            log.error("editProduct failed", e);
            return false;
        }
    }

    /**
     * add new owner to store
     *
     * @param ownerUsername    the username of the owner store
     * @param storeId          - of the store that want add new owner
     * @param newOwnerUsername - the new owner
     * @param uuid
     * @return true if succeed
     */
    public boolean addOwner(@NotBlank String ownerUsername, int storeId, @NotBlank String newOwnerUsername, UUID uuid) {
        try {
            UserSystem ownerUser = tradingSystem.getUser(ownerUsername, uuid);
            Store ownerStore = ownerUser.getOwnerStore(storeId);
            return tradingSystem.addOwnerToStore(ownerStore, ownerUser, newOwnerUsername);
        } catch (TradingSystemException e) {
            log.error("Add owner failed", e);
            return false;
        }
    }

    /**
     * add manger to the store with the default permission
     *
     * @param ownerUsername      the username of the owner store
     * @param storeId            - of the store that want add new owner
     * @param newManagerUsername - the new manger
     * @param uuid
     * @return true if succeed
     */
    public ManagerDto addManager(@NotBlank String ownerUsername, int storeId, @NotBlank String newManagerUsername, UUID uuid) {
        try {
            UserSystem ownerUser = tradingSystem.getUser(ownerUsername, uuid);
            Store ownedStore = ownerUser.getOwnerStore(storeId);
            MangerStore mangerStore = tradingSystem.addMangerToStore(ownedStore, ownerUser, newManagerUsername);
            return Objects.nonNull(mangerStore) ? modelMapper.map(mangerStore, ManagerDto.class) : null;
        } catch (TradingSystemException e) {
            log.error("Add manager failed", e);
            return null;
        }
    }

    /**
     * add permission the manger in the store
     *
     * @param ownerUsername   the username of the owner store
     * @param storeId         - of the store that want add permission the manger
     * @param managerUsername - the user name of the manger
     * @param permission      - the new permission
     * @param uuid
     * @return true if succeed
     */
    public boolean addPermission(@NotBlank String ownerUsername, int storeId, @NotBlank String managerUsername, @NotBlank String permission, UUID uuid) {
        try {
            UserSystem ownerUser = tradingSystem.getUser(ownerUsername, uuid);
            Store ownedStore = ownerUser.getOwnerStore(storeId);
            UserSystem managerStore = ownedStore.getManager(ownerUser, managerUsername);
            StorePermission storePermission = StorePermission.getStorePermission(permission);
            return ownedStore.addPermissionToManager(ownerUser, managerStore, storePermission);
        } catch (TradingSystemException e) {
            log.error("Add permission failed", e);
            return false;
        }
    }


    public boolean removePermission(String ownerUsername, int storeId, String managerUsername, String permission, UUID uuid) {
        try {
            UserSystem ownerUser = tradingSystem.getUser(ownerUsername, uuid);
            Store ownedStore = ownerUser.getOwnerStore(storeId);
            UserSystem managerStore = ownedStore.getManager(ownerUser, managerUsername);
            StorePermission storePermission = StorePermission.getStorePermission(permission);
            return ownedStore.removePermission(ownerUser, managerStore, storePermission);
        } catch (TradingSystemException e) {
            log.error("Add permission failed", e);
            return false;
        }
    }


    public List<String> getPermissionOfManager(String ownerUsername, int storeId, String managerUsername, UUID uuid) {
        try {
            UserSystem ownerUser = tradingSystem.getUser(ownerUsername, uuid);
            Store ownedStore = ownerUser.getOwnerStore(storeId);
            UserSystem managerStore = ownedStore.getManager(ownerUser, managerUsername);
            Set<StorePermission> permissionOfManager = ownedStore.getPermissionOfManager(ownerUser, managerStore);
            return StorePermission.getStringPermissions(permissionOfManager);
        } catch (TradingSystemException e) {
            log.error("get permission failed", e);
            return null;
        }
    }

    /**
     * remove manger from the store by the owner that appointed him
     *
     * @param ownerUsername   owner that appointed the manger
     * @param storeId         - the id that of the store that want remove the manger
     * @param managerUsername - the manger that want remove
     * @param uuid
     * @return true if succeed
     */
    public boolean removeManager(@NotBlank String ownerUsername, int storeId, @NotBlank String managerUsername, UUID uuid) {
        try {
            UserSystem ownerUser = tradingSystem.getUser(ownerUsername, uuid); //get registered user
            Store ownedStore = ownerUser.getOwnerStore(storeId);    //verify the remover is owner
            UserSystem managerStore = ownedStore.getManager(ownerUser, managerUsername);
            return tradingSystem.removeManager(ownedStore, ownerUser, managerStore);
        } catch (TradingSystemException e) {
            log.error("Add permission failed", e);
            return false;
        }
    }

    /**
     * the user name logout from the system
     *
     * @param username - the username that want logout
     * @return true if succeed
     */
    public boolean logout(@NotBlank String username, UUID uuid) {
        try {
            UserSystem user = tradingSystem.getUser(username, uuid);
            return this.tradingSystem.logout(user);
        } catch (TradingSystemException e) {
            log.error("Failed to logout", e);
            return false;
        }
    }

    /**
     * open new store
     *
     * @param usernameOwner - the user that open the store
     * @param storeName     - the name of the new store
     * @return true if succeed
     */
    public boolean openStore(@NotBlank String usernameOwner, @NotBlank String storeName, String description, UUID uuid) {
        try {
            UserSystem user = tradingSystem.getUser(usernameOwner, uuid);
            Store store = tradingSystem.openStore(user, storeName, description);
            return Objects.nonNull(store);
        } catch (TradingSystemException e) {
            log.error("failed to open store", e);
            return false;
        }
    }

    /**
     * register new user to the system
     *
     * @param userName  - the new username
     * @param password  - the password of the user
     * @param firstName - the first name of the new user
     * @param lastName  - the last name of the new user
     * @param image
     * @return true if succeed
     */
    public boolean registerUser(@NotBlank String userName, @NotBlank String password, @NotBlank String firstName,
                                @NotBlank String lastName, MultipartFile image) {
        log.info("started session of registration for user: " + userName);
        UserSystem userSystem = factoryObjects.createSystemUser(userName, firstName, lastName, password);
        return tradingSystem.registerNewUser(userSystem, image);
    }


    /**
     * user login to the system
     *
     * @param userName - the username need to be register to the system for suc
     * @param password - the password must be the correct password of the user
     * @return true if succeed
     */
    public Pair<UUID, Boolean> login(@NotBlank String userName, @NotBlank String password) {
        try {
            return tradingSystem.login(userName, password);
        } catch (TradingSystemException e) {
            log.error("failed to login", e);
            return null;
        }
    }

    /**
     * view the store info
     *
     * @param storeId - the store id that want to see the details
     * @return the store details
     */
    public StoreDto viewStoreInfo(int storeId) {
        try {
            Store store = tradingSystem.getStore(storeId);
            store.applyVisibleDiscountPoliciesOnlyOnStoreProducts();
            return Objects.nonNull(store) ? modelMapper.map(store, StoreDto.class) : null;
        } catch (TradingSystemException e) {
            log.error("failed to see store info ", e);
            return null;
        }
    }

    /**
     * view product of specific store
     *
     * @param storeId   - the store id that include the product
     * @param productId - the product id that want view the product details
     * @return the product details
     */
    public ProductDto viewProduct(int storeId, int productId) {
        try {
            Store store = tradingSystem.getStore(storeId);
            Product product = store.getProduct(productId);
            return Objects.nonNull(product) ? modelMapper.map(product, ProductDto.class) : null;
        } catch (TradingSystemException e) {
            log.error("view product failed", e);
            return null;
        }
    }

    /**
     * search product by name
     *
     * @param productName - the product name that want to search
     * @return list of all the product with this name
     */
    public List<ProductDto> searchProductByName(@NotBlank String productName) {
        log.info("search product by name: " + productName);
        List<Product> products = tradingSystem.searchProductByName(productName);
        return convertProductDtoList(products);
    }

    /**
     * search product by category
     *
     * @param category - the category of product that want to search
     * @return list of all the products that belong to this category
     */
    public List<ProductDto> searchProductByCategory(@NotBlank String category) {
        try {
            log.info("search product by category: " + category);
            ProductCategory productCategory = ProductCategory.getProductCategory(category);
            List<Product> products = tradingSystem.searchProductByCategory(productCategory);
            return convertProductDtoList(products);
        } catch (TradingSystemException e) {
            log.error("failed search product by category", e);
            return null;
        }
    }

    /**
     * search product by keyWords
     *
     * @param keyWords - the keyWords that want search with
     * @return list of all the products that include the keyWords
     */
    public List<ProductDto> searchProductByKeyWords(@NotNull List<@NotBlank String> keyWords) {
        log.info("search product by keywords: " + keyWords);
        List<Product> products = tradingSystem.searchProductByKeyWords(keyWords);
        return convertProductDtoList(products);
    }

    /**
     * filter by range price
     *
     * @param productDtos the list of products
     * @param minPrice    - the minPrice price
     * @param maxPrice    - the maxPrice price
     * @return list of all the products filtered by range price
     */
    public List<ProductDto> filterByRangePrice(@NotNull List<@NotNull ProductDto> productDtos, double minPrice, double maxPrice) {
        log.info("filter products by price range [" + minPrice + "," + maxPrice + "]");
        List<Product> products = converterProductsList(productDtos);
        List<Product> productsFiltered = tradingSystem.filterByRangePrice(products, minPrice, maxPrice);
        return convertProductDtoList(productsFiltered);
    }

    /**
     * filter by product rank
     *
     * @param productDtos the list of products
     * @param rank        - the product rank
     * @return list of all the products filtered by the product rank
     */
    public List<ProductDto> filterByProductRank(@NotNull List<@NotNull ProductDto> productDtos, int rank) {
        log.info("filter products by product rank " + rank);
        List<Product> products = converterProductsList(productDtos);
        List<Product> productsFiltered = tradingSystem.filterByProductRank(products, rank);
        return convertProductDtoList(productsFiltered);
    }

    /**
     * filter by store rank
     *
     * @param productDtos the list of products
     * @param rank        the rank of the store
     * @return list of all the products filtered by the store rank
     */
    public List<ProductDto> filterByStoreRank(@NotNull List<@NotNull ProductDto> productDtos, int rank) {
        log.info("filter products by store rank " + rank);
        List<Product> products = converterProductsList(productDtos);
        List<Product> productsFiltered = tradingSystem.filterByStoreRank(products, rank);
        return convertProductDtoList(productsFiltered);
    }

    /**
     * filter by category
     *
     * @param productDtos the list of products
     * @param category    the category of the product
     * @return list of all the products filtered by the category
     */
    public List<ProductDto> filterByStoreCategory(@NotNull List<@NotNull ProductDto> productDtos, @NotBlank String category) {
        try {
            log.info("filter products by category " + category);
            List<Product> products = converterProductsList(productDtos);
            ProductCategory productCategory = ProductCategory.getProductCategory(category);
            List<Product> productsFiltered = tradingSystem.filterByStoreCategory(products, productCategory);
            return convertProductDtoList(productsFiltered);
        } catch (TradingSystemException e) {
            log.error("filter by store category", e);
            return null;
        }
    }

    /**
     * @param username  the username that want save ShoppingBag
     * @param storeId   the storeId that the product belong to
     * @param productSn - the sn of the prodcut
     * @param amount    - the amount that want save
     * @return true if succeed
     */
    public boolean saveProductInShoppingBag(@NotBlank String username, int storeId, int productSn, int amount, UUID uuid) {
        try {
            log.info("save product " + productSn + "in bag of store " + storeId);
            UserSystem user = tradingSystem.getUser(username, uuid);
            Store store = tradingSystem.getStore(storeId);
            Product product = store.getProduct(productSn);
            Product clonedProduct = product.cloneProduct(); //save copy of product in bag
            return user.saveProductInShoppingBag(store, clonedProduct, amount);
        } catch (TradingSystemException e) {
            log.error("saveInBag", e);
            return false;
        }
    }

    /**
     * view products in shopping cart
     *
     * @param username the username that want view the ShoppingBag
     * @return shopping bag
     */
    public ShoppingCartDto watchShoppingCart(@NotBlank String username, UUID uuid) {
        try {
            UserSystem user = tradingSystem.getUser(username, uuid);
            ShoppingCart shoppingCart = user.getShoppingCart();
            shoppingCart.applyDiscountPolicies();   //apply discount policies and verify the prices are updated
            return Objects.nonNull(shoppingCart) ?modelMapper.map(shoppingCart, ShoppingCartDto.class) : null;
        } catch (TradingSystemException e) {
            log.error("view products in shopping bag", e);
            return null;
        }
    }

    /**
     * remove product in shopping bag
     *
     * @param username  the username that want remove product from the ShoppingBag
     * @param storeId   - the id of the store that the product belong to
     * @param productSn - the sn of the product
     * @return true if succeed
     */
    public boolean removeProductInShoppingBag(@NotBlank String username, int storeId, int productSn, UUID uuid) {
        try {
            UserSystem user = tradingSystem.getUser(username, uuid);
            Store store = tradingSystem.getStore(storeId);
            Product product = store.getProduct(productSn);
            return user.removeProductInShoppingBag(store, product);
        } catch (TradingSystemException e) {
            log.error("Remove product in shopping bag failed", e);
            return false;
        }
    }

    /**
     * purchase shopping cart use for guest
     *
     * @param shoppingCartDto the shopping cart
     * @return the receipt
     */
    public List<ReceiptDto> purchaseShoppingCart(@NotNull ShoppingCartDto shoppingCartDto,
                                                 @NotNull PaymentDetailsDto paymentDetailsDto,
                                                 @NotNull BillingAddressDto billingAddressDto) {
        ShoppingCart shoppingCart = convertToShoppingCart(shoppingCartDto);
        PaymentDetails paymentDetails = Objects.nonNull(paymentDetailsDto) ? modelMapper.map(paymentDetailsDto, PaymentDetails.class) : null;
        BillingAddress billingAddress = Objects.nonNull(billingAddressDto) ? modelMapper.map(billingAddressDto, BillingAddress.class) : null;
        List<Receipt> receipts = tradingSystem.purchaseShoppingCartGuest(shoppingCart, paymentDetails, billingAddress);
        return Objects.nonNull(receipts) ? convertReceiptList(receipts) : null;
    }

    /**
     * purchase shopping cart of resisted user
     *
     * @param username - the username that want purchase shopping cart
     * @return the receipt
     */
    public List<ReceiptDto> purchaseShoppingCart(@NotBlank String username,
                                                 @NotNull PaymentDetailsDto paymentDetailsDto,
                                                 @NotNull BillingAddressDto billingAddressDto,
                                                 UUID uuid) {
            UserSystem user = tradingSystem.getUser(username, uuid);
            PaymentDetails paymentDetails = Objects.nonNull(paymentDetailsDto) ? modelMapper.map(paymentDetailsDto, PaymentDetails.class) : null;
            BillingAddress billingAddress = Objects.nonNull(billingAddressDto) ? modelMapper.map(billingAddressDto, BillingAddress.class) : null;
            List<Receipt> receipts = tradingSystem.purchaseShoppingCart(paymentDetails, billingAddress, user);
            return Objects.nonNull(receipts) ? convertReceiptList(receipts) : null;
    }

    public List<StoreDto> getOwnerStores(String ownerUsername, UUID uuid) {
        UserSystem user = tradingSystem.getUser(ownerUsername, uuid);
        return convertStoreList(new LinkedList<>(user.getOwnedStores()));
    }

    public List<StoreDto> getMangeStores(String manageUsername, UUID uuid) {
        UserSystem user = tradingSystem.getUser(manageUsername, uuid);
        return convertStoreList(new LinkedList<>(user.getManagedStores()));
    }

    public List<StoreDto> getStores() {
        Set<Store> stores = tradingSystem.getStores();
        return convertStoreList(new LinkedList<>(stores));
    }

    public List<ProductDto> getProducts() {
        Set<Product> products = tradingSystem.getProducts();
        return convertProductDtoList(new LinkedList<>(products));
    }

    public void connectNotificationSystem(String username, UUID uuid, String principal) {
        Observer user = tradingSystem.getUser(username, uuid);
        tradingSystem.connectNotificationSystem(user, principal);
    }

    public void sendNotification(List<Notification> notifications) {
        List<NotificationDto> notificationDtos = convertNotificationList(notifications);
        serviceFacade.sendNotification(notificationDtos);
    }

    public List<String> getCategories() {
        return ProductCategory.getCategories();
    }

    public List<String> getOperationsCanDo(String manageUsername, int storeId, UUID uuid) {
        UserSystem user = tradingSystem.getUser(manageUsername, uuid);
        Store ownerStore = user.getManagerStore(storeId);
        return ownerStore.getOperationsCanDo(user);
    }

    public List<String> getAllOperationOfManger() {
        return StorePermission.getStringPermissions();
    }

    ///////////////////////add discounts////////////////////////
    /**
     * add visible discount to the store for some products
     * @param ownerUsername the owner of the store
     * @param storeId
     * @param uuid the unique id of the owner
     * @param visibleDiscountDto the discount parameters objects that inserted by the owner
     * @return true for success
     */
    public boolean addVisibleDiscountPolicy(String ownerUsername,
                                            int storeId, UUID uuid,
                                            VisibleDiscountDto visibleDiscountDto) {
        try {
            Store store = tradingSystem.getStore(storeId);
            UserSystem owner = tradingSystem.getUser(ownerUsername, uuid);
            //convert Products of type dto to the type of the products in the domain
            Calendar endTime = visibleDiscountDto.getEndTime();
            double discount = visibleDiscountDto.getDiscountPercentage();
            Map<Product,Integer> products= convertDtoProductHashToProductHashFromStore
                    (visibleDiscountDto.getProductsUnderThisDiscount(),store);
            //create the visible discount
            VisibleDiscount visibleDiscount = factoryObjects.createVisibleDiscount(endTime, discount, products);
            //create products list for the addition
            return store.addDiscountForProduct(owner,visibleDiscount);

        }catch (TradingSystemException exception){
            log.error("failed to add discount policy");
            return false;
        }
    }

    /**
     * convert from hash of ProductDto with their amounts into
     * Product map with its amount
     * @param productsUnderThisDiscount
     * @param store the store that products belongs to
     * @return productsHash
     */
    private Map<Product, Integer> convertDtoProductHashToProductHashFromStore
    (Map<ProductDto, Integer> productsUnderThisDiscount, Store store) {
        Map<Product, Integer> productsHash = new HashMap<>();
        for (ProductDto productDto : productsUnderThisDiscount.keySet()) {
            Product productFromStore = store.getProduct(productDto.getProductSn());
            //add the product object exist in store with the required amount for discount
            productsHash.put(productFromStore, productsUnderThisDiscount.get(productDto));
        }
        return productsHash;
    }

    /**
     * add visible discount to the store for some products
     * @param ownerUsername the owner of the store
     * @param storeId
     * @param uuid the unique id of the owner
     * @return true for success
     */
    public boolean addConditionalDiscountPolicy(String ownerUsername,
                                                int storeId, UUID uuid,
                                                ConditionalProductDiscountDto conditionProdDiscountDto) {
        try{
            Store store = tradingSystem.getStore(storeId);
            UserSystem owner = tradingSystem.getUser(ownerUsername,uuid);

            //convert dto to domain conditional product discount
            Map<Product,Integer> productUnderDisc =
                    convertDtoProductHashToProductHashFromStore(conditionProdDiscountDto.getProductsUnderThisDiscount(),store);
            Calendar endTime = conditionProdDiscountDto.getEndTime();
            double discount = conditionProdDiscountDto.getDiscountPercentage();
            String description = conditionProdDiscountDto.getDescription();
            Map<Product,Integer> productsAmountsToApply =
                    convertDtoProductHashToProductHashFromStore(conditionProdDiscountDto.getAmountOfProductsForApplyDiscounts(),store);
            //create the conditional discount
            ConditionalProductDiscount conditionalProdDiscount = factoryObjects.
                    createCondProductDiscountDiscount(productUnderDisc,endTime,discount,description,
                            productsAmountsToApply);

            return store.addDiscountForProduct(owner,conditionalProdDiscount);

        }catch (TradingSystemException exception){
            log.error("failed to add discount policy");
            return false;
        }
    }

    /**
     * add visible discount to the store for some products
     * @param ownerUsername the owner of the store
     * @param storeId
     * @param uuid the unique id of the owner
     * @param conditionalStoreDiscountDto the discount info inserted by the owner
     * @return true for success
     */
    public boolean addConditionalStoreDiscountPolicy(String ownerUsername,
                                                     int storeId, UUID uuid,
                                                     ConditionalStoreDiscountDto conditionalStoreDiscountDto) {
        try{
            Store store = tradingSystem.getStore(storeId);
            UserSystem owner = tradingSystem.getUser(ownerUsername,uuid);
            ConditionalStoreDiscount storeDiscount = factoryObjects.
                    createCondStoreDiscount(conditionalStoreDiscountDto.getEndTime(),
                            conditionalStoreDiscountDto.getDiscountPercentage(),
                            conditionalStoreDiscountDto.getDescription(),
                            conditionalStoreDiscountDto.getMinPrice());

            store.addDiscountForProduct(owner, storeDiscount);
            return true;
        } catch (TradingSystemException exception) {
            log.error("failed to add discount policy");
            return false;
        }
    }

    /**
     * add visible discount to the store for some products
     * @param ownerUsername the owner of the store
     * @param storeId
     * @param uuid the unique id of the owner
     * @param conditionalComposedDto the discount info inserted by the owner
     * @return true for success
     */
    public boolean addConditionalComposedDiscountPolicy(String ownerUsername,
                                                        int storeId, UUID uuid,
                                                        ConditionalComposedDto conditionalComposedDto) {
        try{
            Store store = tradingSystem.getStore(storeId);
            UserSystem owner = tradingSystem.getUser(ownerUsername,uuid);
            //init parameters of composed discount
            //convert the conditions list to discounts
            CompositeOperator operator = conditionalComposedDto.getCompositeOperator();
            Map<Integer, DiscountPolicy> composedDiscounts =
                    convertDiscountsMapFromDto(conditionalComposedDto.getComposedDiscounts(),store);
            //convert to applying discounts
            Map<Integer, DiscountPolicy> discountsToApply =
                    convertDiscountsMapFromDto(conditionalComposedDto.getDiscountsToApply(),store);
            Calendar endTime = conditionalComposedDto.getEndTime();
            double discountPercentage = conditionalComposedDto.getDiscountPercentage();
            String description = conditionalComposedDto.getDescription();
            //get the store the discount belongs to

            ConditionalComposedDiscount composedDiscount = factoryObjects. //create the discount
                    createComposedDiscount(operator,composedDiscounts,discountsToApply,endTime,
                    discountPercentage,description);
            //add the composed new discount to the store
            return store.addDiscountForProduct(owner,composedDiscount);
        }catch (TradingSystemException exception){
            log.error("failed to add discount policy");
            return false;
        }
    }

    /**
     * convert from map of DiscountDto's into DiscountPolicy
     * @param discountDtoMap the map to convert
     * @return discountHash - the <Integer,Product> map related to <Integer,ProductDto> received
     */
    private Map<Integer, DiscountPolicy> convertDiscountsMapFromDto
    (Map<Integer, DiscountPolicyDto> discountDtoMap,Store store){
        Map<Integer,DiscountPolicy> discountHash = new HashMap<>();
        try{
            for(Integer discountId: discountDtoMap.keySet()){
                DiscountPolicy discountPolicy = store.getDiscountPolicyById(discountId);
                discountHash.put(discountId,discountPolicy);
            }
        }catch (TradingSystemException e){
            return null;
        }
        return discountHash;
    }

    public Pair<Double, Double> getTotalPriceOfShoppingCart(ShoppingCartDto shoppingCartDto) {
        log.info("get Total Price Of Shopping Cart");
        ShoppingCart shoppingCart = convertToShoppingCart(shoppingCartDto);
        return tradingSystem.getTotalPrices(shoppingCart);
    }

    public boolean addProductToShoppingCart(String username, int amount, ProductDto productDto, UUID uuid) {
        UserSystem user = tradingSystem.getUser(username,uuid);
        Product product = modelMapper.map(productDto, Product.class);
        Store store = tradingSystem.getStore(product.getStoreId());
        return user.saveProductInShoppingBag(store, product, amount);
    }

    public  List<ProductShoppingCartDto> getShoppingCart(String username, UUID uuid) {
        UserSystem user = tradingSystem.getUser(username,uuid);
        Type listType = new TypeToken<List<ProductShoppingCartDto>>() {}.getType();
        return modelMapper.map(user.getShoppingCart(), listType);
    }

    public Pair<Double, Double> getTotalPriceOfShoppingCart(String username, UUID uuid) {
        UserSystem user = tradingSystem.getUser(username,uuid);
        return tradingSystem.getTotalPrices(user.getShoppingCart());
    }


    public Set<UserSystemDto> getUsers(String administratorUsername, UUID uuid) {
        UserSystem user = tradingSystem.getUser(administratorUsername,uuid);
        Set<UserSystem> users = tradingSystem.getUsers(user);
        return convertSetUsersToSetUserDto(users);
    }

    //////////////////////edit discounts
    /**
     * convert information to match the edit function of the discount in the domain
     * @param ownerUsername editor
     * @param storeId store belongs to discount
     * @param uuid unique id of the user
     * @param visibleDiscountEditDto information of discount to update
     * @return true in case of successful operation
     */
    public boolean editVisibleDiscount(String ownerUsername, int storeId, UUID uuid,
                                       VisibleDiscountEditDto visibleDiscountEditDto) {
        try{
            UserSystem owner = tradingSystem.getUser(ownerUsername,uuid);
            Store store = tradingSystem.getStore(storeId);
            //discount to update
            VisibleDiscount discountPolicy = (VisibleDiscount) store.getDiscountPolicyById(visibleDiscountEditDto.getId());
            //get dto values as modifications
            Calendar endTime = visibleDiscountEditDto.getEndTime();
            double percentage = visibleDiscountEditDto.getDiscountPercentage();
            Map<Product,Integer> prodToAdd = convertDtoProductHashToProductHashFromStore(visibleDiscountEditDto.
                    getProductsToAdd(),store);
            Map<Product,Integer> prodToDel = convertDtoProductHashToProductHashFromStore(visibleDiscountEditDto.
                    getProductsToDelete(),store);
            return store.editVisibleDiscount(owner,discountPolicy,endTime,percentage,prodToAdd,prodToDel);
        }catch (TradingSystemException e){
            return false;
        }
    }

    /**
     * convert information to match the edit function of the discount in the domain
     * @param ownerUsername editor
     * @param storeId store belongs to discount
     * @param uuid unique id of the user
     * @param productEditDto information of discount to update
     * @return true in case of successful operation
     */
    public boolean editConditionalProductDiscount(String ownerUsername, int storeId, UUID uuid,
                                                  ConditionalProductEditDto productEditDto) {
        try{
            UserSystem owner = tradingSystem.getUser(ownerUsername,uuid);
            Store store = tradingSystem.getStore(storeId);
            //discount to update
            ConditionalProductDiscount discountPolicy = (ConditionalProductDiscount) store.getDiscountPolicyById(productEditDto.getId());
            //get dto values as modifications
            String description = productEditDto.getDescription();
            Calendar endTime = productEditDto.getEndTime();
            double percentage = productEditDto.getDiscountPercentage();
            Map<Product,Integer> prodToAdd = convertDtoProductHashToProductHashFromStore(productEditDto.
                    getProductsToAdd(),store);
            Map<Product,Integer> prodToDel = convertDtoProductHashToProductHashFromStore(productEditDto.
                    getProductsToDelete(),store);
            Map<Product,Integer> applyToDel = convertDtoProductHashToProductHashFromStore(productEditDto.
                    getApplyToDelete(),store);
            Map<Product,Integer> applyToAdd = convertDtoProductHashToProductHashFromStore(productEditDto.
                    getApplyToAdd(),store);
            return store.editConditionalProductDiscount(owner,discountPolicy,
                    endTime,percentage,prodToAdd,prodToDel,applyToAdd,applyToDel,description);
        }catch (TradingSystemException e){
            return false;
        }
    }


    /**
     * convert information to match the edit function of the discount in the domain
     * @param ownerUsername editor
     * @param storeId store belongs to discount
     * @param uuid unique id of the user
     * @param storeDiscountEditDtoEditDto information of discount to update
     * @return true in case of successful operation
     */
    public boolean editConditionalStoreDiscount(String ownerUsername, int storeId, UUID uuid,
                                                ConditionalStoreDiscountEditDto storeDiscountEditDtoEditDto) {
        try{
            UserSystem owner = tradingSystem.getUser(ownerUsername,uuid);
            Store store = tradingSystem.getStore(storeId);
            //discount to update
            ConditionalStoreDiscount discountPolicy = (ConditionalStoreDiscount) store.getDiscountPolicyById(storeDiscountEditDtoEditDto.getId());
            //get dto values as modifications
            String description = storeDiscountEditDtoEditDto.getDescription();
            Calendar endTime = storeDiscountEditDtoEditDto.getEndTime();
            double percentage = storeDiscountEditDtoEditDto.getDiscountPercentage();
            double minPrice = storeDiscountEditDtoEditDto.getMinPrice();
            return store.editConditionalStoreDiscount(owner,discountPolicy,
                    endTime,percentage,minPrice,description);
        }catch (TradingSystemException e){
            return false;
        }
    }


    /**
     * convert information to match the edit function of the discount in the domain
     * @param ownerUsername editor
     * @param storeId store belongs to discount
     * @param uuid unique id of the user
     * @param composedEditDto information of discount to update
     * @return true in case of successful operation
     */
    public boolean editConditionalComposedDiscount(String ownerUsername, int storeId, UUID uuid,
                                                   ConditionalComposedDiscountEditDto composedEditDto) {
        try{
            UserSystem owner = tradingSystem.getUser(ownerUsername,uuid);
            Store store = tradingSystem.getStore(storeId);
            //discount to update
            ConditionalComposedDiscount discountPolicy = (ConditionalComposedDiscount) store.getDiscountPolicyById(composedEditDto.getId());
            //get dto values as modifications
            Calendar endTime = composedEditDto.getEndTime();
            double percentage = composedEditDto.getDiscountPercentage();
            Map<Integer,DiscountPolicy> composedToAdd = convertDiscountsMapFromDtoAsInStore(composedEditDto.
                    getComposedToAdd(),store);
            Map<Integer,DiscountPolicy> composedToDel = convertDiscountsMapFromDtoAsInStore(composedEditDto.
                    getComposedToDelete(),store);
            Map<Integer,DiscountPolicy> applyToDel = convertDiscountsMapFromDtoAsInStore(composedEditDto.
                    getApplyToDelete(),store);
            Map<Integer,DiscountPolicy> applyToAdd = convertDiscountsMapFromDtoAsInStore(composedEditDto.
                    getApplyToAdd(),store);
            return store.editConditionalComposedDiscount(owner,discountPolicy,
                    endTime,percentage,composedEditDto.getOperator(),composedToAdd,composedToDel,applyToAdd,applyToDel);
        }catch (TradingSystemException e){
            return false;
        }
    }

    /**
     * removes a discount from store
     * @param ownerUsername the one tries to remove
     * @param storeId the store to remove from
     * @param uuid unique id of the user
     * @param discountId to remove
     * @return true in case of successful deletion
     */
    public boolean removeDiscountFromStore(String ownerUsername, int storeId, UUID uuid, int discountId) {
        try{
            Store store = tradingSystem.getStore(storeId);
            UserSystem owner = tradingSystem.getUser(ownerUsername,uuid);
            DiscountPolicy discountPolicy = store.getDiscountPolicyById(discountId);
            return store.removeDiscount(owner,discountPolicy);
        }catch (TradingSystemException e){
            return false;
        }
    }


    //////////////////////////////// converters ///////////////////////////

    /**
     * convert from map of DiscountDto's into DiscountPolicy
     * @param discountDtoMap the map to convert
     * @return discountHash - the <Integer,Product> map related to <Integer,ProductDto> received
     */
    private Map<Integer, DiscountPolicy> convertDiscountsMapFromDtoAsInStore
    (Map<Integer, DiscountPolicyDto> discountDtoMap,Store store){
        Map<Integer,DiscountPolicy> discountHash = new HashMap<>();
        try{
            for(Integer discountId: discountDtoMap.keySet()){
                DiscountPolicy discountPolicy = store.getDiscountPolicyById(discountId);
                discountHash.put(discountId,discountPolicy);
            }
        }catch (TradingSystemException e){
            return null;
        }
        return discountHash;
    }


    private ShoppingCart convertToShoppingCart(ShoppingCartDto shoppingCartDto) {
        Map<Store, ShoppingBag> shoppingCartMap = new HashMap<>();
        if (Objects.nonNull(shoppingCartDto)) {
            shoppingCartDto.getShoppingBags()
                    .forEach((key1, value1) -> {
                        Store store = tradingSystem.getStore(key1);
                        Map<Product, Integer> shoppingBagMap = new HashMap<>();
                        value1.getProductListFromStore()
                                .forEach((key, value) -> {
                                    Product product = store.getProduct(key);
                                    shoppingBagMap.put(product, value);
                                });
                        shoppingCartMap.put(store, ShoppingBag.builder().productListFromStore(shoppingBagMap).build());
                    });
        }
        return ShoppingCart.builder()
                .shoppingBagsList(shoppingCartMap)
                .build();
    }


    /**
     * converter of Receipt list to ReceiptDto list
     *
     * @param receipts - list of receipts
     * @return list of ReceiptDto
     */
    private List<ReceiptDto> convertReceiptList(@NotNull List<@NotNull Receipt> receipts) {
        Type listType = new TypeToken<List<ReceiptDto>>() {}.getType();
        return modelMapper.map(receipts, listType);
    }

    /**
     * converter of Receipt list to ReceiptDto list
     *
     * @param stores - list of stores
     * @return list of ReceiptDto
     */
    private List<StoreDto> convertStoreList(@NotNull List<@NotNull Store> stores) {
        Type listType = new TypeToken<List<StoreDto>>() {}.getType();
        return modelMapper.map(stores, listType);
    }

    /**
     * converter of Product list to ProductDto list
     *
     * @param products - list of products
     * @return list of ProductDto
     */
    private List<ProductDto> convertProductDtoList(@NotNull List<@NotNull Product> products) {
        Type listType = new TypeToken<List<ProductDto>>() {}.getType();
        return modelMapper.map(products, listType);
    }

    /**
     * converter of ProductDto list to Product list
     *
     * @param productDtos - list of productDtos
     * @return list of products
     */
    private List<Product> converterProductsList(@NotNull List<@NotNull ProductDto> productDtos) {
        Type listType = new TypeToken<List<Product>>() {}.getType();
        return modelMapper.map(productDtos, listType);
    }

    private List<NotificationDto> convertNotificationList(@NotNull List<@NotNull Notification> notifications) {
        Type listType = new TypeToken<List<NotificationDto>>() {}.getType();
        return modelMapper.map(notifications, listType);
    }

    private Set<UserSystemDto> convertSetUsersToSetUserDto(Set<UserSystem> users) {
        Type listType = new TypeToken<Set<UserSystemDto>>() {}.getType();
        return modelMapper.map(users, listType);
    }

    /**
     * get all discounts of store with id received
     *
     * @param ownerUsername
     * @param storeId id of the store to get its discounts
     * @param uuid
     * @return
     */
    public List<DiscountPolicyDto> getAllStoreDiscounts(String ownerUsername, int storeId, UUID uuid) {
        UserSystem user = tradingSystem.getUser(ownerUsername, uuid);
        Store store = user.getOwnerStore(storeId);
        List<DiscountPolicy> allDiscounts = store.getDiscountPolicies();
        return convertStoreDiscountsToDtos(allDiscounts);
    }

    public List<String> getAllUsernameNotOwnerNotManger(String ownerUsername, int storeId, UUID uuid) {
        UserSystem user = tradingSystem.getUser(ownerUsername, uuid);
        Store store = user.getOwnerStore(storeId);
        return tradingSystem.getAllUsernameNotOwnerNotManger(store);
    }

    public List<String> getMySubOwners(String ownerUsername, int storeId, UUID uuid) {
        UserSystem user = tradingSystem.getUser(ownerUsername, uuid);
        Store store = user.getOwnerStore(storeId);
        return store.getMySubOwners(ownerUsername);
    }


    public List<ManagerDto> getMySubMangers(String ownerUsername, int storeId, UUID uuid) {
        UserSystem user = tradingSystem.getUser(ownerUsername, uuid);
        Store store = user.getOwnerStore(storeId);
        Type listType = new TypeToken<List<ManagerDto>>() {}.getType();
        return modelMapper.map(store.getMySubMangers(ownerUsername), listType);
    }
    /**
     * returns list of converted domain discounts to dto type discounts
     * @param allDiscounts discount policy type discounts
     * @return
     */
    private List<DiscountPolicyDto> convertStoreDiscountsToDtos(List<DiscountPolicy> allDiscounts) {
        List<DiscountPolicyDto> allStoreDiscountsDtos = new ArrayList<>();
        for(DiscountPolicy discount: allDiscounts){
            DiscountPolicyDto discountDto = convertDiscountToDto(discount);
            allStoreDiscountsDtos.add(discountDto);
        }
        return allStoreDiscountsDtos;
    }

    /**
     * returns a single discount converted fro domain type to dto type
     * @param discount to convert
     * @return
     */
    private DiscountPolicyDto convertDiscountToDto(DiscountPolicy discount) {
       //set all default discountDto fields
        CompositeOperator operator =null;
        Map<ProductDto,Integer> productsUnderThisDiscount = null;
        Calendar endTime = null;
        double discountPercentage = 0;
        String conditionDescription = null;
        Map<ProductDto,Integer> amountOfProductsForApplyDiscounts = null;
        double minPrice = -1;
        Map<Integer, DiscountPolicyDto> composedDiscounts = null;;
        Map<Integer, DiscountPolicyDto> discountsToApply = null;;

        endTime = discount.getEndTime();
        discountPercentage = discount.getDiscountPercentage();

        if(discount instanceof VisibleDiscount){
            productsUnderThisDiscount = convertProductsMapToDto(discount.
                    getProductsUnderThisDiscount());
        }
        else if(discount instanceof ConditionalProductDiscount){
            productsUnderThisDiscount = convertProductsMapToDto(discount.
                    getProductsUnderThisDiscount());
            conditionDescription = ((ConditionalProductDiscount) discount).getConditionDescription();
            amountOfProductsForApplyDiscounts = convertProductsMapToDto(((ConditionalProductDiscount) discount).
                    getAmountOfProductsForApplyDiscounts());
        }
        else if(discount instanceof ConditionalStoreDiscount){
            conditionDescription = ((ConditionalStoreDiscount) discount).getConditionDescription();
            minPrice = ((ConditionalStoreDiscount) discount).getMinPrice();
        }
        else if(discount instanceof ConditionalComposedDiscount){
            conditionDescription = ((ConditionalComposedDiscount) discount).getConditionDescription();
            operator = ((ConditionalComposedDiscount) discount).getCompositeOperator();
            composedDiscounts = convertDiscountsMapToDto(((ConditionalComposedDiscount) discount).
                    getComposedDiscounts());
            discountsToApply = convertDiscountsMapToDto(((ConditionalComposedDiscount) discount).
                    getDiscountsToApply());
        }
        return new DiscountPolicyDto(productsUnderThisDiscount,endTime,discountPercentage,
                conditionDescription,amountOfProductsForApplyDiscounts,minPrice,
                composedDiscounts,discountsToApply,operator);
    }

    /**
     * convert map of discount policies to their dtos
     * @param discountsToApply
     * @return
     */
    private Map<Integer, DiscountPolicyDto> convertDiscountsMapToDto(Map<Integer, DiscountPolicy> discountsToApply) {
        Map<Integer,DiscountPolicyDto> dtoDiscountsMap = new HashMap<>();
        for(Integer discountId: discountsToApply.keySet()){
            DiscountPolicy discount = discountsToApply.get(discountId);
            DiscountPolicyDto discountDto = convertDiscountToDto(discount);
            dtoDiscountsMap.put(discountId,discountDto);
        }
        return dtoDiscountsMap;
    }

    /**
     * convert map of Product to a map of ProductDto
     * @param productsUnderThisDiscount
     * @return
     */
    private Map<ProductDto, Integer> convertProductsMapToDto
    (Map<Product, Integer> productsUnderThisDiscount) {
        Map<ProductDto,Integer> productsDtos = new HashMap<>();
        for(Product product: productsUnderThisDiscount.keySet()){
            ProductDto productDto = convertProductToDto(product);
            productsDtos.put(productDto,productsUnderThisDiscount.get(product));
        }
        return productsDtos;
    }

    /**
     * convert from Product to Product Dto
     * @param p product to convert
     * @return
     */
    private ProductDto convertProductToDto(Product p) {
        return new ProductDto(p.getProductSn(),p.getName(),p.getCategory().category,
                p.getAmount(),p.getCost(),p.getOriginalCost(),p.getRank(),p.getStoreId());
    }

    public List<String> getPermissionCantDo(String ownerUsername, int storeId, String managerUsername, UUID uuid) {
        try {
            UserSystem ownerUser = tradingSystem.getUser(ownerUsername, uuid);
            Store ownedStore = ownerUser.getOwnerStore(storeId);
            UserSystem managerStore = ownedStore.getManager(ownerUser, managerUsername);
            Set<StorePermission> permissionOfManager = ownedStore.getPermissionCantDo(ownerUser, managerStore);
            return StorePermission.getStringPermissions(permissionOfManager);
        } catch (TradingSystemException e) {
            log.error("get permission failed", e);
            return null;
        }
    }

    public boolean isOwner(String username, int storeId, UUID uuid) {
        UserSystem ownerUser = tradingSystem.getUser(username, uuid);
        return ownerUser.isOwner(storeId);
    }
}

package com.wsep202.TradingSystem.domain.trading_system_management;


import com.wsep202.TradingSystem.domain.exception.*;
import com.wsep202.TradingSystem.domain.factory.FactoryObjects;
import com.wsep202.TradingSystem.domain.trading_system_management.discount.*;
import com.wsep202.TradingSystem.domain.trading_system_management.notification.Notification;
import com.wsep202.TradingSystem.domain.trading_system_management.notification.Observer;
import com.wsep202.TradingSystem.domain.trading_system_management.ownerStore.OwnerToApprove;
import com.wsep202.TradingSystem.domain.trading_system_management.policy_purchase.*;
import com.wsep202.TradingSystem.domain.trading_system_management.purchase.BillingAddress;
import com.wsep202.TradingSystem.domain.trading_system_management.purchase.PaymentDetails;
import com.wsep202.TradingSystem.domain.trading_system_management.statistics.DailyVisitor;
import com.wsep202.TradingSystem.domain.trading_system_management.statistics.DailyVisitorsField;
import com.wsep202.TradingSystem.domain.trading_system_management.statistics.RequestGetDailyVisitors;
import com.wsep202.TradingSystem.domain.trading_system_management.statistics.UpdateDailyVisitor;
import com.wsep202.TradingSystem.dto.*;
import com.wsep202.TradingSystem.service.ServiceFacade;
import javafx.util.Pair;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.lang.reflect.Type;
import java.util.*;

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

    private final TradingSystemDao tradingSystemDao;

    /**
     * UC 3.7
     * view purchase history of user logged in
     *
     * @param userName - must be logged in
     * @return all the receipt of the user
     */
    public List<ReceiptDto> viewPurchaseHistory(@NotBlank String userName, UUID uuid) {
        try {
            UserSystem user = tradingSystem.getUser(userName, uuid);   //get registered user by his username
            List<Receipt> receipts = new LinkedList<>(user.getReceipts()); //get user receipts
            return convertReceiptList(receipts);
        } catch (TradingSystemException e) {
            log.error("Tried to view his purchase history and failed", e);
            return null;
        }
    }

    /**
     * UC 6.4.1
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
            List<Receipt> receipts = new LinkedList<>(store.getReceipts());
            return convertReceiptList(receipts);
        } catch (TradingSystemException e) {
            log.error("Tried to view purchase history of store failed", e);
            return null;
        }
    }

    /**
     * UC 6.4.2
     * administrator view purchase history of user
     *
     * @param administratorUsername the user name of the admin
     * @param userName              - the userName that want view purchase history
     * @return all the receipt of the user
     */
    public List<ReceiptDto> viewPurchaseHistory(@NotBlank String administratorUsername, @NotBlank String userName, UUID uuid) {
        try {
            UserSystem userByAdmin = tradingSystem.getUserByAdmin(administratorUsername, userName, uuid);
            return convertReceiptList(new LinkedList<>(userByAdmin.getReceipts()));
        } catch (TradingSystemException e) {
            log.error("Tried to view purchase history of user and failed", e);
            return null;
        }
    }

    /**
     * UC 5.1
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
            return convertReceiptList(new LinkedList<>(mangerStore.getManagerStore(storeId).getReceipts()));
        } catch (TradingSystemException e) {
            log.error("The user is not a manager of the store ,so cant see purchase history of store", e);
            return null;
        }
    }

    /**
     * UC 4.10
     * Owner view purchase history of store
     *
     * @param ownerUserName the owner Username of the store manger that want view purchase history
     * @param storeId       - the store that want view the purchase history
     * @return all the receipt of the store
     */
    public List<ReceiptDto> viewPurchaseHistoryOfOwner(@NotBlank String ownerUserName, int storeId, UUID uuid) {
        try {
            UserSystem user = tradingSystem.getUser(ownerUserName, uuid);
            return convertReceiptList(new LinkedList<>(user.getOwnerStore(storeId).getReceipts()));
        } catch (TradingSystemException e) {
            log.error("Can't see history of the store", e);
            return null;
        }
    }

    /**
     * UC 4.1
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
            if (cost<0 || amount<0) {
                log.error("cost and amount can't be negative");
                return null;
            }
            UserSystem user = tradingSystem.getUser(ownerUsername, uuid); //get registered user with ownerUsername
            Store ownerStore = user.getOwnerOrManagerWithPermission(storeId, StorePermission.EDIT_PRODUCT); //verify he owns store with storeId
            //convert to a category we can add to the product
            ProductCategory productCategory = ProductCategory.getProductCategory(category);
            Product product = factoryObjects.createProduct(productName, productCategory, amount, cost, storeId);
            Product productRes = tradingSystemDao.addProductToStore(ownerStore, user, product);
            return Objects.nonNull(productRes) ? modelMapper.map(productRes, ProductDto.class) : null;
        } catch (TradingSystemException e) {
            log.error("add product failed", e);
            return null;
        }
    }

    public List<DiscountDto> getStoreDiscounts(String username, int storeId, UUID uuid) {
        UserSystem user = tradingSystem.getUser(username, uuid); //get registered user with ownerUsername
        Store store = user.getOwnerOrManagerWithPermission(storeId, StorePermission.EDIT_DISCOUNT); //verify he owns store with storeId
        List<Discount> storeDiscounts = store.getStoreDiscounts(user);
        return convertDiscountList(storeDiscounts);
    }

    /**
     * UC 4.2
     * owner wants remove a discount policy from store
     *
     * @param username   - name of owner
     * @param storeId    - the id of the store
     * @param discountId - id of discount to remove
     * @param uuid       - users UUID
     * @return true if success, else false
     */
    public boolean removeDiscount(String username, int storeId, int discountId, UUID uuid) {
        UserSystem user = tradingSystem.getUser(username, uuid); //get registered user with ownerUsername
        Store store = user.getOwnerOrManagerWithPermission(storeId, StorePermission.EDIT_DISCOUNT); //verify he owns store with storeId
        return tradingSystemDao.removeDiscount(store, user, discountId);
    }

    /**
     * UC 4.2
     * owner wants add a new policy ir edit an existing one a discount policy from store
     *
     * @param username - name of owner
     * @param storeId  - the id of the store
     * @param uuid     - users UUID
     * @return true if success, else false
     */
    public DiscountDto addEditDiscount(String username, int storeId, DiscountDto discountDto, UUID uuid) {
        UserSystem user = tradingSystem.getUser(username, uuid); //get registered user with ownerUsername
        Store store = user.getOwnerOrManagerWithPermission(storeId, StorePermission.EDIT_DISCOUNT);
        Discount discount = modelMapper.map(discountDto, Discount.class);
        Discount discountRes = tradingSystemDao.addEditDiscount(store, user, discount);
        return Objects.nonNull(discountRes) ? modelMapper.map(discountRes, DiscountDto.class) : null;
    }

    /**
     * UC 4.2
     * owner wants add a new policy ir edit an existing one a discount policy from store
     *
     * @param username - name of owner
     * @param storeId  - the id of the store
     * @param uuid     - users UUID
     * @return true if success, else false
     */

    public PurchasePolicyDto addEditPurchase(String username, int storeId, PurchasePolicyDto purchasePolicyDto, UUID uuid) {
        UserSystem user = tradingSystem.getUser(username, uuid); //get registered user with ownerUsername
        Store store = user.getOwnerOrManagerWithPermission(storeId, StorePermission.EDIT_PURCHASE_POLICY);
        Purchase purchase = modelMapper.map(purchasePolicyDto, Purchase.class);
        if(purchasePolicyDto.getMin() > purchasePolicyDto.getMax())
            throw new PurchasePolicyException("Received minimum > maximum --> didn't insert policy");
        Purchase purchaseRes = tradingSystemDao.addEditPurchase(store, user, purchase);
        return Objects.nonNull(purchaseRes) ? modelMapper.map(purchaseRes, PurchasePolicyDto.class) : null;
    }

    public List<String> getCompositeOperators(String username, int storeId, UUID uuid) {
        UserSystem user = tradingSystem.getUser(username, uuid);
        return CompositeOperator.getStringCompositeOperators();
    }

    /**
     * UC 4.1
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
            Store ownerStore = user.getOwnerOrManagerWithPermission(storeId, StorePermission.EDIT_PRODUCT); //verify he is owner of the store
            return tradingSystemDao.deleteProductFromStore(ownerStore, user, productSn);
        } catch (TradingSystemException e) {
            log.error("deleteProduct from store failed", e);
            return false;
        }
    }

    /**
     * UC 4.1
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
            Store ownerStore = user.getOwnerOrManagerWithPermission(storeId, StorePermission.EDIT_PRODUCT);
            return tradingSystemDao.editProduct(ownerStore, user, productSn, productName, category, amount, cost);
        } catch (TradingSystemException e) {
            log.error("editProduct failed", e);
            return false;
        }
    }

    /**
     * UC 4.3
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
     * UC 4.5
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
            Store ownedStore = ownerUser.getOwnerOrManagerWithPermission(storeId, StorePermission.EDIT_Managers);
            MangerStore mangerStore = tradingSystem.addMangerToStore(ownedStore, ownerUser, newManagerUsername);
            return Objects.nonNull(mangerStore) ? modelMapper.map(mangerStore, ManagerDto.class) : null;
        } catch (TradingSystemException e) {
            log.error("Add manager failed", e);
            return null;
        }
    }

    /**
     * UC 4.6
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
            Store ownedStore = ownerUser.getOwnerOrManagerWithPermission(storeId, StorePermission.EDIT_Managers);
            UserSystem managerStore = ownedStore.getManager(ownerUser, managerUsername);
            StorePermission storePermission = StorePermission.getStorePermission(permission);
            return tradingSystemDao.addPermissionToManager(ownedStore, ownerUser, managerStore, storePermission);
        } catch (TradingSystemException e) {
            log.error("Add permission failed", e);
            return false;
        }
    }

    /**
     * UC 4.6
     * remove permissions from manager
     *
     * @param ownerUsername   - owner of the store
     * @param storeId         - id of store
     * @param managerUsername - manager of store
     * @param permission      - permission to remove
     * @param uuid            - UUID of owner
     * @return true if success, else false
     */
    public boolean removePermission(String ownerUsername, int storeId, String managerUsername, String permission, UUID uuid) {
        try {
            UserSystem ownerUser = tradingSystem.getUser(ownerUsername, uuid);
            Store ownedStore = ownerUser.getOwnerStore(storeId);
            UserSystem managerStore = ownedStore.getManager(ownerUser, managerUsername);
            StorePermission storePermission = StorePermission.getStorePermission(permission);
            return tradingSystemDao.removePermission(ownedStore, ownerUser, managerStore, storePermission);
        } catch (TradingSystemException e) {
            log.error("Add permission failed", e);
            return false;
        }
    }

    /**
     * see managers permissions
     *
     * @param ownerUsername   - owner of store
     * @param storeId         - store id
     * @param managerUsername - manager of store
     * @param uuid            - UUID of owner
     * @return
     */
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
     * UC 4.7
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
            Store ownedStore = ownerUser.getOwnerOrManagerWithPermission(storeId, StorePermission.EDIT_Managers);    //verify the remover is owner
            UserSystem managerStore = ownedStore.getManager(ownerUser, managerUsername);
            return tradingSystem.removeManager(ownedStore, ownerUser, managerStore);
        } catch (TradingSystemException e) {
            log.error("remove manager failed", e);
            return false;
        }
    }

    /**
     * UC 4.4
     * remove owner from the store by the owner that appointed him
     *
     * @param ownerUsername owner that appointed the manger
     * @param storeId       - the id that of the store that want remove the owner
     * @param ownerToRemove - the owner that needs to be removed
     * @param uuid
     * @return true if succeed
     */
    public boolean removeOwner(@NotBlank String ownerUsername, int storeId, @NotBlank String ownerToRemove, UUID uuid) {
        try {
            UserSystem ownerUser = tradingSystem.getUser(ownerUsername, uuid); //get registered user
            Store ownedStore = ownerUser.getOwnerStore(storeId);    //verify the remover is owner
            UserSystem removeOwner = ownedStore.getOwnerToRemove(ownerUser, ownerToRemove);
            return tradingSystem.removeOwner(ownedStore, ownerUser, removeOwner);
        } catch (TradingSystemException e) {
            log.error("remove owner failed", e);
            return false;
        }
    }

    /**
     * UC 3.1
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
     * UC 3.2
     * open new store
     *
     * @param usernameOwner - the user that open the store
     * @param storeName     - the name of the new store
     * @return true if succeed
     */
    public StoreDto openStore(@NotBlank String usernameOwner, @NotBlank String storeName, String description, UUID uuid) {
        try {
            UserSystem user = tradingSystem.getUser(usernameOwner, uuid);
            Store store = tradingSystem.openStore(user, storeName, description);
            return modelMapper.map(store, StoreDto.class);
        } catch (TradingSystemException e) {
            log.error("failed to open store", e);
            return null;
        }
    }

    /**
     * UC 2.2
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
     * UC 2.3
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
     * UC 2.6
     *
     * @param username  the username that want save ShoppingBag
     * @param storeId   the storeId that the product belong to
     * @param productSn - the sn of the prodcut
     * @param amount    - the amount that want save
     * @return true if succeed
     */
    public boolean saveProductInShoppingBag(@NotBlank String username, int storeId, int productSn, int amount, UUID uuid) {
        try {
            log.info("save product " + productSn + "in bag of store " + storeId);
            Store store = tradingSystem.getStore(storeId);
            Product product = store.getProduct(productSn);
            ShoppingCart shoppingCart = tradingSystem.getShoppingCart(username, uuid);
            return tradingSystemDao.saveProductInShoppingBag(username, shoppingCart, store, product, amount);
        } catch (TradingSystemException e) {
            log.error("saveInBag", e);
            return false;
        }
    }

    /**
     * UC 2.7
     * view products in shopping cart
     *
     * @param username the username that want view the ShoppingBag
     * @return shopping bag
     */
    public ShoppingCartDto watchShoppingCart(@NotBlank String username, UUID uuid) {
        try {
            ShoppingCart shoppingCart = tradingSystem.getShoppingCart(username, uuid);
            return Objects.nonNull(shoppingCart) ? modelMapper.map(shoppingCart, ShoppingCartDto.class) : null;
        } catch (TradingSystemException e) {
            log.error("view products in shopping bag", e);
            return null;
        }
    }

    /**
     * UC 2.7
     * remove product in shopping bag
     *
     * @param username  the username that want remove product from the ShoppingBag
     * @param storeId   - the id of the store that the product belong to
     * @param productSn - the sn of the product
     * @return true if succeed
     */
    public boolean removeProductInShoppingBag(@NotBlank String username, int storeId, int productSn, UUID uuid) {
        try {
            Store store = tradingSystem.getStore(storeId);
            Product product = store.getProduct(productSn);
            ShoppingCart shoppingCart = tradingSystem.getShoppingCart(username, uuid);
            return tradingSystemDao.removeProductInShoppingBag(username, shoppingCart, store, product);
        } catch (TradingSystemException e) {
            log.error("Remove product in shopping bag failed", e);
            return false;
        }
    }

    /**
     * UC 2.8
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
     * UC 2.8
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

    public Pair<Double, Double> getTotalPriceOfShoppingCart(ShoppingCartDto shoppingCartDto) {
        log.info("get Total Price Of Shopping Cart");
        ShoppingCart shoppingCart = convertToShoppingCart(shoppingCartDto);
        return tradingSystem.getTotalPrices(shoppingCart);
    }

    /**
     * UC 2.7
     * add a new product to cart
     *
     * @param username   - name of the user
     * @param amount     - amount of the product
     * @param productDto - the product details
     * @param uuid       - users UUID
     * @return true if added, else false
     */
    public boolean addProductToShoppingCart(String username, int amount, ProductDto productDto, UUID uuid) {
        Product product = modelMapper.map(productDto, Product.class);
        Store store = tradingSystem.getStore(product.getStoreId());
        ShoppingCart shoppingCart = tradingSystem.getShoppingCart(username, uuid);
        return tradingSystemDao.saveProductInShoppingBag(username, shoppingCart, store, product, amount);
    }

    public List<ProductShoppingCartDto> getShoppingCart(String username, UUID uuid) {
        ShoppingCart shoppingCart = tradingSystem.getShoppingCart(username, uuid);
        Type listType = new TypeToken<List<ProductShoppingCartDto>>() {
        }.getType();
        return modelMapper.map(shoppingCart, listType);
    }

    public Pair<Double, Double> getTotalPriceOfShoppingCart(String username, UUID uuid) {
        ShoppingCart shoppingCart = tradingSystem.getShoppingCart(username, uuid);
        return tradingSystem.getTotalPrices(shoppingCart);
    }


    public Set<UserSystemDto> getUsers(String administratorUsername, UUID uuid) {
        UserSystem user = tradingSystem.getUser(administratorUsername, uuid);
        Set<UserSystem> users = tradingSystem.getUsers(user);
        return convertSetUsersToSetUserDto(users);
    }


    //////////////////////////////// converters ///////////////////////////

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
                        shoppingCartMap.put(store, ShoppingBag.builder()
                                .productListFromStore(shoppingBagMap).build());
                    });
        }
        return ShoppingCart.builder()
                .shoppingBagsList(shoppingCartMap)
                .build();
    }

    /**
     * get all discounts of store with id received
     *
     * @param ownerUsername
     * @param storeId       id of the store to get its discounts
     * @param uuid
     * @return
     */
    public List<DiscountDto> getDiscountsSimple(String ownerUsername, int storeId, UUID uuid) {
        UserSystem user = tradingSystem.getUser(ownerUsername, uuid);
        Store store = user.getOwnerStore(storeId);
        List<Discount> allDiscounts = store.getDiscountSimple();
        return convertDiscountList(allDiscounts);
    }

    public List<DiscountDto> getAlltDiscounts(String ownerUsername, int storeId, UUID uuid) {
        UserSystem user = tradingSystem.getUser(ownerUsername, uuid);
        Store store = user.getOwnerStore(storeId);
        List<Discount> allDiscounts = new LinkedList<>(store.getDiscounts());
        return convertDiscountList(allDiscounts);
    }

    public List<PurchasePolicyDto> getAllStorePurchases(String ownerUsername, int storeId, UUID uuid) {
        UserSystem user = tradingSystem.getUser(ownerUsername, uuid);
        Store store = user.getOwnerStore(storeId);
        List<Purchase> allPurchases = new LinkedList<>(store.getPurchasePolicies());
        return convertPurchaseList(allPurchases);
    }

    public List<String> getAllUsernameNotOwnerNotManger(String ownerUsername, int storeId, UUID uuid) {
        UserSystem user = tradingSystem.getUser(ownerUsername, uuid);
        Store store = user.getOwnerOrManagerWithPermission(storeId, StorePermission.EDIT_Managers);
        return tradingSystem.getAllUsernameNotOwnerNotMangerNotWaiting(store);
    }

    public List<String> getMySubOwners(String ownerUsername, int storeId, UUID uuid) {
        UserSystem user = tradingSystem.getUser(ownerUsername, uuid);
        Store store = user.getOwnerStore(storeId);
        return store.getMySubOwners(ownerUsername);
    }

    public List<ManagerDto> getMySubMangers(String ownerUsername, int storeId, UUID uuid) {
        UserSystem user = tradingSystem.getUser(ownerUsername, uuid);
        Store store = user.getOwnerOrManagerWithPermission(storeId, StorePermission.EDIT_Managers);
        Type listType = new TypeToken<List<ManagerDto>>() {
        }.getType();
        return modelMapper.map(store.getMySubMangers(ownerUsername), listType);
    }

    public List<String> getPermissionCantDo(String ownerUsername, int storeId, String managerUsername, UUID uuid) {
        try {
            UserSystem ownerUser = tradingSystem.getUser(ownerUsername, uuid);
            Store ownedStore = ownerUser.getOwnerOrManagerWithPermission(storeId, StorePermission.EDIT_Managers);
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

    /**
     * UC 2.7
     * edit products in cart
     *
     * @return true if changed
     */
    public boolean changeProductAmountInShoppingBag(String username, int storeId, int amount, int productSn, UUID uuid) {
        ShoppingCart shoppingCart = tradingSystem.getShoppingCart(username, uuid);
        return tradingSystemDao.changeProductAmountInShoppingBag(username, shoppingCart, storeId, amount, productSn);
    }

    /**
     * converter of Receipt list to ReceiptDto list
     *
     * @param receipts - list of receipts
     * @return list of ReceiptDto
     */
    private List<ReceiptDto> convertReceiptList(@NotNull List<@NotNull Receipt> receipts) {
        Type listType = new TypeToken<List<ReceiptDto>>() {
        }.getType();
        return modelMapper.map(receipts, listType);
    }

    /**
     * converter of Receipt list to ReceiptDto list
     *
     * @param stores - list of stores
     * @return list of ReceiptDto
     */
    private List<StoreDto> convertStoreList(@NotNull List<@NotNull Store> stores) {
        Type listType = new TypeToken<List<StoreDto>>() {
        }.getType();
        return modelMapper.map(stores, listType);
    }

    /**
     * converter of Product list to ProductDto list
     *
     * @param products - list of products
     * @return list of ProductDto
     */
    private List<ProductDto> convertProductDtoList(@NotNull List<@NotNull Product> products) {
        Type listType = new TypeToken<List<ProductDto>>() {
        }.getType();
        return modelMapper.map(products, listType);
    }

    private List<NotificationDto> convertNotificationList(@NotNull List<@NotNull Notification> notifications) {
        Type listType = new TypeToken<List<NotificationDto>>() {
        }.getType();
        return modelMapper.map(notifications, listType);
    }

    private List<DiscountDto> convertDiscountList(@NotNull List<@NotNull Discount> discounts) {
        Type listType = new TypeToken<List<DiscountDto>>() {
        }.getType();
        return modelMapper.map(discounts, listType);
    }

    private List<PurchasePolicyDto> convertPurchaseList(@NotNull List<@NotNull Purchase> purchases) {
        Type listType = new TypeToken<List<PurchasePolicyDto>>() {
        }.getType();
        return modelMapper.map(purchases, listType);
    }

    private Set<UserSystemDto> convertSetUsersToSetUserDto(Set<UserSystem> users) {
        Type listType = new TypeToken<Set<UserSystemDto>>() {
        }.getType();
        return modelMapper.map(users, listType);
    }

    public List<OwnerToApproveDto> getMyOwnerToApprove(String ownerUsername, UUID uuid) {
        return convertSetOwnerToApproveToSetOwnerToApproveDto(tradingSystemDao.getMyOwnerToApprove(ownerUsername, uuid));
    }

    private List<OwnerToApproveDto> convertSetOwnerToApproveToSetOwnerToApproveDto(Set<OwnerToApprove> ownerToApproves) {
        Type listType = new TypeToken<List<OwnerToApproveDto>>() {
        }.getType();
        return modelMapper.map(ownerToApproves, listType);
    }

    public boolean approveOwner(String ownerUsername, int storeId, String ownerToApprove, boolean status, UUID uuid) {
        UserSystem ownerUser = tradingSystem.getUser(ownerUsername, uuid);
        Store ownedStore = ownerUser.getOwnerOrManagerWithPermission(storeId, StorePermission.EDIT_Managers);
        return tradingSystemDao.approveOwner(ownedStore, ownerUser, ownerToApprove, status);
    }

    public List<DailyVisitorDto> getDailyVisitors(String username, RequestGetDailyVisitorsDto requestGetDailyVisitorsDto, UUID uuid){
        RequestGetDailyVisitors requestGetDailyVisitors = modelMapper.map(requestGetDailyVisitorsDto, RequestGetDailyVisitors.class);
        return convertSetDailyVisitorsToDailyVisitorsDtoSet(tradingSystem.getDailyVisitors(username, requestGetDailyVisitors, uuid));
    }

    public void updateDailyVisitor(String dailyVisitorsField){
        tradingSystemDao.updateDailyVisitors(DailyVisitorsField.getDailyVisitorsField(dailyVisitorsField));
    }
    private List<DailyVisitorDto> convertSetDailyVisitorsToDailyVisitorsDtoSet(Set<DailyVisitor> dailyVisitors) {
        Type listType = new TypeToken<List<DailyVisitorDto>>() {
        }.getType();
        return modelMapper.map(dailyVisitors, listType);
    }

    public boolean sendDailyVisitor(UpdateDailyVisitor updateDailyVisitor) {
        return serviceFacade.sendDailyVisitor(updateDailyVisitor);
    }

    public void stopDailyVisitors(String username, UUID uuid) {
        tradingSystem.stopDailyVisitors(username, uuid);
    }
}

package com.wsep202.TradingSystem.domain.trading_system_management;

import com.wsep202.TradingSystem.domain.factory.FactoryObjects;
import com.wsep202.TradingSystem.domain.trading_system_management.discount.Discount;
import com.wsep202.TradingSystem.domain.trading_system_management.policy_purchase.Purchase;
import com.wsep202.TradingSystem.domain.trading_system_management.purchase.BillingAddress;
import com.wsep202.TradingSystem.domain.trading_system_management.purchase.PaymentDetails;
import com.wsep202.TradingSystem.dto.DiscountDto;
import com.wsep202.TradingSystem.dto.ProductDto;
import com.wsep202.TradingSystem.dto.PurchasePolicyDto;
import com.wsep202.TradingSystem.dto.ReceiptDto;
import com.wsep202.TradingSystem.helprTests.AssertionHelperTest;
import com.wsep202.TradingSystem.service.ServiceFacade;
import org.junit.jupiter.api.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.wsep202.TradingSystem.helprTests.SetUpObjects.setUpDiscounts;
import static com.wsep202.TradingSystem.helprTests.SetUpObjects.setUpReceipts;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TradingSystemFacadeTest {
    @Autowired
    private ModelMapper modelMapper = new ModelMapper();
//required entities for tests
    private TradingSystem tradingSystem;
    private TradingSystemFacade tradingSystemFacade;
    private UserSystem userSystem;
    private Store store;
    private FactoryObjects factoryObjects;
    private PaymentDetails paymentDetails;
    private BillingAddress billingAddress;
    private ServiceFacade serviceFacade;
    private MultipartFile userImage;
    private ShoppingCart shoppingCart;
    private TradingSystemDao tradingSystemDao;
    private Product product;
    private Discount discount;

    @AfterEach
    void tearDown() {
    }

    @Nested
    public class TradingSystemFacadeTestUnit {

        @BeforeEach
        void setUp() {
            discount = mock(Discount.class);
            product = mock(Product.class);
            store = mock(Store.class);
            userSystem = mock(UserSystem.class);
            tradingSystem = mock(TradingSystem.class);
            factoryObjects = mock(FactoryObjects.class);
            serviceFacade = mock(ServiceFacade.class);
            tradingSystemDao = mock(TradingSystemDaoImpl.class);
            tradingSystemFacade = new TradingSystemFacade(tradingSystem, modelMapper, factoryObjects, serviceFacade, tradingSystemDao);
            paymentDetails = mock(PaymentDetails.class);
            billingAddress = mock(BillingAddress.class);
            userImage = mock(MultipartFile.class);
            shoppingCart = mock(ShoppingCart.class);
        }

        /**
         * test: logged in user view his purchase history
         * get the purchase history of logged in user with uuid
         * @pre user is logged in with receipts
         * UC 3.7
         */
        @Test
        void userViewHisPurchaseHistory() {
            String userName = "loggedInUser";
            UUID uuid = new UUID(1,1);
            when(tradingSystem.getUser(userName,uuid)).thenReturn(userSystem);
            Set<Receipt> userReceipts = setUpReceipts();
            when(userSystem.getReceipts()).thenReturn(userReceipts);
            List<ReceiptDto> returnedReceipts = tradingSystemFacade.viewPurchaseHistory(userName,uuid);
            AssertionHelperTest.assertReceipts(userReceipts.stream().collect(Collectors.toList()), returnedReceipts);
        }
        /**
         * test:
         * UC 6.4.1
         * administrator view purchase history of store
         * @pre user is logged in as admin.
         * ask to watch over exist store with receipts
         *
         */
        @Test
        void adminTestViewPurchaseHistoryOfStore() {
            String adminName = "admin";
            UUID uuid = new UUID(1,1);
            int storeId = 0;
            when(tradingSystem.getStoreByAdmin(adminName,storeId,uuid)).thenReturn(store);
            Set<Receipt> storeReceipts = setUpReceipts();
            when(store.getReceipts()).thenReturn(storeReceipts);
            List<ReceiptDto> actualReceipts = tradingSystemFacade.viewPurchaseHistory(adminName,storeId,uuid);
            AssertionHelperTest.assertReceipts(storeReceipts.stream().collect(Collectors.toList()), actualReceipts);
        }
        /**
         * test:
         * UC 6.4.2
         * administrator view purchase history of user
         * @pre user is logged in as admin.
         * ask to watch over exist user with receipts
         *
         */
        @Test
        void adminTestViewPurchaseHistoryOfUser() {
            String adminName = "admin";
            UUID uuid = new UUID(1,1);
            String userName = "user name";
            when(userSystem.getUserName()).thenReturn(userName);
            when(tradingSystem.getUserByAdmin(adminName,userSystem.getUserName(),uuid)).thenReturn(userSystem);
            Set<Receipt> userReceipts = setUpReceipts();
            when(userSystem.getReceipts()).thenReturn(userReceipts);
            List<ReceiptDto> actualReceipts = tradingSystemFacade.viewPurchaseHistory(adminName,userName,uuid);
            AssertionHelperTest.assertReceipts(userReceipts.stream().collect(Collectors.toList()), actualReceipts);
        }

        /**
         * test:
         * UC 5.1
         * Manager view purchase history of store
         * @pre user is logged in as manager
         * and ask to watch over his store receipts
         */
        @Test
        void viewPurchaseHistoryOfManager() {
            String managerName = "managerName";
            UUID uuid = new UUID(1,1);
            int storeId = 0;
            when(tradingSystem.getUser(managerName,uuid)).thenReturn(userSystem);
            when(userSystem.getManagerStore(storeId)).thenReturn(store);
            Set<Receipt> storeReceipts = setUpReceipts();
            when(store.getReceipts()).thenReturn(storeReceipts);
            List<ReceiptDto> actualReceipts = tradingSystemFacade.viewPurchaseHistoryOfManager(managerName,storeId,uuid);
            AssertionHelperTest.assertReceipts(storeReceipts.stream().collect(Collectors.toList()), actualReceipts);
        }
        /**
         * test:
         * UC 4.10
         * Owner view purchase history of owned store
         * @pre user is logged in as owner
         * and ask to watch over his store receipts
         */
        @Test
        void viewPurchaseHistoryOfOwner() {
            String ownerName = "ownerUserName";
            UUID uuid = new UUID(1,1);
            int storeId = 0;
            when(tradingSystem.getUser(ownerName,uuid)).thenReturn(userSystem);
            when(userSystem.getOwnerStore(storeId)).thenReturn(store);
            Set<Receipt> storeReceipts = setUpReceipts();
            when(store.getReceipts()).thenReturn(storeReceipts);
            List<ReceiptDto> actualReceipts = tradingSystemFacade.viewPurchaseHistoryOfOwner(ownerName,storeId,uuid);
            AssertionHelperTest.assertReceipts(storeReceipts.stream().collect(Collectors.toList()), actualReceipts);
        }

        /**
         * test add product to store by owner
         * @pre there is owner with store in the system
         * @post the store has the added product
         */
        @Test
        void addProduct() {
            String prodName = "product name";
            String prodCategory = "health";
            int amount = 10;
            double cost = 50;
            int storeId = 0;
            String ownerUserName = "ownerName";
            UUID uuid = new UUID(1,1);
            //product mock
            when(product.getOriginalCost()).thenReturn(cost);
            when(product.getCost()).thenReturn(cost);
            when(product.getProductSn()).thenReturn(0);
            when(product.getName()).thenReturn(prodName);
            when(product.getAmount()).thenReturn(amount);
            when(product.getStoreId()).thenReturn(storeId);
            when(product.getCategory()).thenReturn(ProductCategory.HEALTH);
            when(product.getRank()).thenReturn(5);
            //trading system mock
            when(tradingSystem.getUser(ownerUserName,uuid)).thenReturn(userSystem); //get the system user presentation of owner
            //owner user mock
            when(userSystem.getOwnerOrManagerWithPermission(storeId,StorePermission.EDIT_PRODUCT)).thenReturn(store); //get owner store
            //factory object mock
            when(factoryObjects.createProduct(prodName,ProductCategory.HEALTH,amount,cost,storeId)).thenReturn(product);//creation of product
            //tradingSystemDao mock
            when(tradingSystemDao.addProductToStore(store,userSystem,product)).thenReturn(product);//res
            ProductDto actualInsertedProduct = tradingSystemFacade.addProduct(ownerUserName,storeId,prodName,prodCategory,amount,cost,uuid);
            AssertionHelperTest.assertProduct(product,actualInsertedProduct);
        }

        /**
         * test that a store discounts list is received by the facade
         * @pre store with discounts is exist
         *      the user is owner in the store or manager with edit discount permission
         */
        @Test
        void getStoreDiscounts() {
            String userName = "user name";
            UUID uuid = new UUID(1,1);
            when(tradingSystem.getUser(userName,uuid)).thenReturn(userSystem);
            int storeId = 0;
            when(userSystem.getOwnerOrManagerWithPermission(storeId,StorePermission.EDIT_DISCOUNT)).thenReturn(store);
            List<Discount> storeDiscounts = setUpDiscounts();
            when(store.getStoreDiscounts(userSystem)).thenReturn(storeDiscounts);
            List<DiscountDto> actualDiscountsInStore = tradingSystemFacade.getStoreDiscounts(userName,storeId,uuid);
            AssertionHelperTest.assertDiscounts(storeDiscounts,actualDiscountsInStore);
        }

        /**
         * UC 4.2 - owner can add discounts in a store
         * @pre there is user registered as owner of store with discounts
         *
         * */
        @Test
        void addDiscount() {

            Discount disc = Discount.builder().discountId(-1).build();
            DiscountDto discountDto = mock(DiscountDto.class);
            String ownerUsername = "owner username";
            UUID uuid = new UUID(1,1);
            int storeId = 0;
            when(tradingSystem.getUser(ownerUsername,uuid)).thenReturn(userSystem);
            when(userSystem.getOwnerOrManagerWithPermission(storeId,StorePermission.EDIT_DISCOUNT)).thenReturn(store);
            //mock the model mapper
            modelMapper = mock(ModelMapper.class);
            when(modelMapper.map(discountDto,Discount.class)).thenReturn(disc);
            when(modelMapper.map(disc,DiscountDto.class)).thenReturn(discountDto);
            when(tradingSystemDao.addEditDiscount(store,userSystem,disc)).thenReturn(disc);
            tradingSystemFacade = new TradingSystemFacade(tradingSystem, modelMapper, factoryObjects, serviceFacade, tradingSystemDao);
            Assertions.assertEquals(discountDto,tradingSystemFacade.addEditDiscount(ownerUsername,storeId,discountDto,uuid));
        }
        /**
         * UC 4.2 - owner can edit discounts in a store
         * @pre there is user registered as owner of store with discounts
         *
         * */
        @Test
        void EditDiscount() {
            Discount disc = Discount.builder().discountId(1).build();
            DiscountDto discountDto = mock(DiscountDto.class);
            String ownerUsername = "owner username";
            UUID uuid = new UUID(1,1);
            int storeId = 0;
            when(tradingSystem.getUser(ownerUsername,uuid)).thenReturn(userSystem);
            when(userSystem.getOwnerOrManagerWithPermission(storeId,StorePermission.EDIT_DISCOUNT)).thenReturn(store);
            //mock the model mapper
            modelMapper = mock(ModelMapper.class);
            when(modelMapper.map(discountDto,Discount.class)).thenReturn(disc);
            when(modelMapper.map(disc,DiscountDto.class)).thenReturn(discountDto);
            when(tradingSystemDao.addEditDiscount(store,userSystem,disc)).thenReturn(disc);
            tradingSystemFacade = new TradingSystemFacade(tradingSystem, modelMapper, factoryObjects, serviceFacade, tradingSystemDao);
            Assertions.assertEquals(discountDto,tradingSystemFacade.addEditDiscount(ownerUsername,storeId,discountDto,uuid));
        }
        /**
         * UC 4.2 - owner can add purchase policy in a store
         * @pre there is user registered as owner of store
         *
         * */
        @Test
        void addPurchase() {
            Purchase pur = Purchase.builder().purchaseId(-1).build();
            PurchasePolicyDto purchasePolicyDto = mock(PurchasePolicyDto.class);
            String ownerUsername = "owner username";
            UUID uuid = new UUID(1,1);
            int storeId = 0;
            when(tradingSystem.getUser(ownerUsername,uuid)).thenReturn(userSystem);
            when(userSystem.getOwnerOrManagerWithPermission(storeId,StorePermission.EDIT_PURCHASE_POLICY)).thenReturn(store);
            //mock the model mapper
            modelMapper = mock(ModelMapper.class);
            when(modelMapper.map(purchasePolicyDto,Purchase.class)).thenReturn(pur);
            when(modelMapper.map(pur,PurchasePolicyDto.class)).thenReturn(purchasePolicyDto);
            when(tradingSystemDao.addEditPurchase(store,userSystem,pur)).thenReturn(pur);
            tradingSystemFacade = new TradingSystemFacade(tradingSystem, modelMapper, factoryObjects, serviceFacade, tradingSystemDao);
            Assertions.assertEquals(purchasePolicyDto,tradingSystemFacade.addEditPurchase(ownerUsername,storeId,purchasePolicyDto,uuid));
        }

        /**
         * UC 4.2 - owner can edit purchase policy in a store
         * @pre there is user registered as owner of store
         *
         * */
        @Test
        void editPurchase() {
            Purchase pur = Purchase.builder().purchaseId(1).build();
            PurchasePolicyDto purchasePolicyDto = mock(PurchasePolicyDto.class);
            String ownerUsername = "owner username";
            UUID uuid = new UUID(1,1);
            int storeId = 0;
            when(tradingSystem.getUser(ownerUsername,uuid)).thenReturn(userSystem);
            when(userSystem.getOwnerOrManagerWithPermission(storeId,StorePermission.EDIT_PURCHASE_POLICY)).thenReturn(store);
            //mock the model mapper
            modelMapper = mock(ModelMapper.class);
            when(modelMapper.map(purchasePolicyDto,Purchase.class)).thenReturn(pur);
            when(modelMapper.map(pur,PurchasePolicyDto.class)).thenReturn(purchasePolicyDto);
            when(tradingSystemDao.addEditPurchase(store,userSystem,pur)).thenReturn(pur);
            tradingSystemFacade = new TradingSystemFacade(tradingSystem, modelMapper, factoryObjects, serviceFacade, tradingSystemDao);
            Assertions.assertEquals(purchasePolicyDto,tradingSystemFacade.addEditPurchase(ownerUsername,storeId,purchasePolicyDto,uuid));
        }

        /**
         *test delete product from store
         * @pre there is product with inserted SN in the store
         * the inserted username belongs to registered owner of the store
         * the uuid fit to the owner
         */
        @Test
        void deleteProductFromStore() {
            int productSN = 1;  //the product SN that related to a product in the store
            when(product.getProductSn()).thenReturn(productSN);
            String ownerUsername = "owner name";
            UUID uuid = new UUID(1,1);
            when(tradingSystem.getUser(ownerUsername,uuid)).thenReturn(userSystem);
            int storeId = 0;
            when(userSystem.getOwnerOrManagerWithPermission(storeId,StorePermission.EDIT_PRODUCT)).thenReturn(store);
            when(tradingSystemDao.deleteProductFromStore(store,userSystem,productSN)).thenReturn(true);
            Assertions.assertTrue(tradingSystemFacade.deleteProductFromStore(ownerUsername,storeId,product.getProductSn(),uuid));
        }

        /**
         * test UC 4.1
         * owner can edit product in his store - verify overwriting product params when they are valid
         */
        @Test
        void editProduct() {
            //valid arguments for editing product
            String ownerName = "owner name";
            int storeId = 0;
            int productSN = 1;
            String prodName = "product name";
            String category = "health";
            int amount = 10;
            double cost = 89;
            //the uuid of the owner
            UUID uuid = new UUID(1,1);
            //mocks
            when(tradingSystem.getUser(ownerName,uuid)).thenReturn(userSystem);
            when(userSystem.getOwnerOrManagerWithPermission(storeId,StorePermission.EDIT_PRODUCT)).thenReturn(store);
            when(tradingSystemDao.editProduct(store,userSystem,productSN,prodName,category,amount,cost)).thenReturn(true);
            //success: facade indicate the received product edited successfully
            Assertions.assertTrue(tradingSystemFacade.editProduct(ownerName,storeId,productSN,prodName,category,amount,cost,uuid));
        }

        @Test
        void addOwner() {
        }

        @Test
        void addManager() {
        }

        @Test
        void addPermission() {
        }

        @Test
        void removePermission() {
        }

        @Test
        void getPermissionOfManager() {
        }

        @Test
        void removeManager() {
        }

        @Test
        void removeOwner() {
        }

        @Test
        void logout() {
        }

        @Test
        void openStore() {
        }

        @Test
        void registerUser() {
        }

        @Test
        void login() {
        }

        @Test
        void saveProductInShoppingBag() {
        }

        @Test
        void watchShoppingCart() {
        }

        @Test
        void removeProductInShoppingBag() {
        }

        @Test
        void purchaseShoppingCart() {
        }

        @Test
        void testPurchaseShoppingCart() {
        }

        @Test
        void getOwnerStores() {
        }

        @Test
        void getMangeStores() {
        }

        @Test
        void getStores() {
        }

        @Test
        void getProducts() {
        }

        @Test
        void connectNotificationSystem() {
        }

        @Test
        void sendNotification() {
        }

        @Test
        void getCategories() {
        }

        @Test
        void getOperationsCanDo() {
        }

        @Test
        void getAllOperationOfManger() {
        }

        @Test
        void getTotalPriceOfShoppingCart() {
        }

        @Test
        void addProductToShoppingCart() {
        }

        @Test
        void getShoppingCart() {
        }

        @Test
        void testGetTotalPriceOfShoppingCart() {
        }

        @Test
        void getUsers() {
        }

        @Test
        void getDiscountsSimple() {
        }

        @Test
        void getAlltDiscounts() {
        }

        @Test
        void getAllStorePurchases() {
        }

        @Test
        void getAllUsernameNotOwnerNotManger() {
        }

        @Test
        void getMySubOwners() {
        }

        @Test
        void getMySubMangers() {
        }

        @Test
        void getPermissionCantDo() {
        }

        @Test
        void isOwner() {
        }

        @Test
        void changeProductAmountInShoppingBag() {
        }

        @Test
        void getMyOwnerToApprove() {
        }

        @Test
        void approveOwner() {
        }

        @Test
        void getDailyVisitors() {
        }

        @Test
        void updateDailyVisitor() {
        }

        @Test
        void sendDailyVisitor() {
        }

        @Test
        void stopDailyVisitors() {
        }
    }

    @Nested
    public class TradingSystemFacadeTestIntegration {

        @BeforeEach
        void setUp() {
        }


        @Test
        void viewPurchaseHistory() {
        }

        @Test
        void testViewPurchaseHistory() {
        }

        @Test
        void testViewPurchaseHistory1() {
        }

        @Test
        void viewPurchaseHistoryOfManager() {
        }

        @Test
        void viewPurchaseHistoryOfOwner() {
        }

        @Test
        void addProduct() {
        }

        @Test
        void getStoreDiscounts() {
        }

        @Test
        void removeDiscount() {
        }

        @Test
        void addEditDiscount() {
        }

        @Test
        void addEditPurchase() {
        }

        @Test
        void getCompositeOperators() {
        }

        @Test
        void deleteProductFromStore() {
        }

        @Test
        void editProduct() {
        }

        @Test
        void addOwner() {
        }

        @Test
        void addManager() {
        }

        @Test
        void addPermission() {
        }

        @Test
        void removePermission() {
        }

        @Test
        void getPermissionOfManager() {
        }

        @Test
        void removeManager() {
        }

        @Test
        void removeOwner() {
        }

        @Test
        void logout() {
        }

        @Test
        void openStore() {
        }

        @Test
        void registerUser() {
        }

        @Test
        void login() {
        }

        @Test
        void saveProductInShoppingBag() {
        }

        @Test
        void watchShoppingCart() {
        }

        @Test
        void removeProductInShoppingBag() {
        }

        @Test
        void purchaseShoppingCart() {
        }

        @Test
        void testPurchaseShoppingCart() {
        }

        @Test
        void getOwnerStores() {
        }

        @Test
        void getMangeStores() {
        }

        @Test
        void getStores() {
        }

        @Test
        void getProducts() {
        }

        @Test
        void connectNotificationSystem() {
        }

        @Test
        void sendNotification() {
        }

        @Test
        void getCategories() {
        }

        @Test
        void getOperationsCanDo() {
        }

        @Test
        void getAllOperationOfManger() {
        }

        @Test
        void getTotalPriceOfShoppingCart() {
        }

        @Test
        void addProductToShoppingCart() {
        }

        @Test
        void getShoppingCart() {
        }

        @Test
        void testGetTotalPriceOfShoppingCart() {
        }

        @Test
        void getUsers() {
        }

        @Test
        void getDiscountsSimple() {
        }

        @Test
        void getAlltDiscounts() {
        }

        @Test
        void getAllStorePurchases() {
        }

        @Test
        void getAllUsernameNotOwnerNotManger() {
        }

        @Test
        void getMySubOwners() {
        }

        @Test
        void getMySubMangers() {
        }

        @Test
        void getPermissionCantDo() {
        }

        @Test
        void isOwner() {
        }

        @Test
        void changeProductAmountInShoppingBag() {
        }

        @Test
        void getMyOwnerToApprove() {
        }

        @Test
        void approveOwner() {
        }

        @Test
        void getDailyVisitors() {
        }

        @Test
        void updateDailyVisitor() {
        }

        @Test
        void sendDailyVisitor() {
        }

        @Test
        void stopDailyVisitors() {
        }

    }


}

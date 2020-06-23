package com.wsep202.TradingSystem.domain.trading_system_management;

import com.wsep202.TradingSystem.domain.factory.FactoryObjects;
import com.wsep202.TradingSystem.domain.trading_system_management.discount.Discount;
import com.wsep202.TradingSystem.domain.trading_system_management.policy_purchase.Purchase;
import com.wsep202.TradingSystem.domain.trading_system_management.purchase.BillingAddress;
import com.wsep202.TradingSystem.domain.trading_system_management.purchase.PaymentDetails;
import com.wsep202.TradingSystem.domain.trading_system_management.statistics.UpdateDailyVisitor;
import com.wsep202.TradingSystem.dto.*;
import com.wsep202.TradingSystem.helprTests.AssertionHelperTest;
import com.wsep202.TradingSystem.service.ServiceFacade;
import javafx.util.Pair;
import org.junit.jupiter.api.*;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Type;
import java.util.ArrayList;
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
    private MangerStore mangerStore;

    @AfterEach
    void tearDown() {
    }

    @Nested
    public class TradingSystemFacadeTestUnit {

        @BeforeEach
        void setUp() {
            mangerStore = mock(MangerStore.class);
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

        /**
         * test UC 4.3 - add owner to store
         * @pre the app. is owner of the store with valid uuid in the system
         */
        @Test
        void addOwner() {
            //params for the facade func.
            String ownerName = "owner name";    //the appointing owner
            int storeId = 0; //the store to own
            String newOwnerName = "new owner"; //owner to appoint on this store
            UUID uuid = new UUID(1,1);
            //mocks
            when(tradingSystem.getUser(ownerName,uuid)).thenReturn(userSystem);
            when(userSystem.getOwnerStore(storeId)).thenReturn(store);
            when(tradingSystem.addOwnerToStore(store,userSystem,newOwnerName)).thenReturn(true);
            Assertions.assertTrue(tradingSystemFacade.addOwner(ownerName,storeId,newOwnerName,uuid));
        }
        /**
         * test UC 4.5 - add manager to store with default permissions
         * @pre the appointing. is owner of the store with valid uuid in the system
         */
        @Test
        void addManager() {
            //manager Store object of the new manager user
            ManagerDto mangerDto = mock(ManagerDto.class);
            //params for the facade func.
            String ownerName = "owner name";    //the appointing owner
            int storeId = 0; //the store to own
            String newManagerName = "new manager"; //owner to appoint on this store
            UUID uuid = new UUID(1,1);
            //mocks
            when(tradingSystem.getUser(ownerName,uuid)).thenReturn(userSystem);
            when(userSystem.getOwnerOrManagerWithPermission(storeId,StorePermission.EDIT_Managers)).thenReturn(store);
            when(tradingSystem.addMangerToStore(store,userSystem,newManagerName)).thenReturn(mangerStore);
            modelMapper = mock(ModelMapper.class);
            when(modelMapper.map(mangerStore, ManagerDto.class)).thenReturn(mangerDto);
            tradingSystemFacade = new TradingSystemFacade(tradingSystem,modelMapper,factoryObjects,serviceFacade,tradingSystemDao);
            Assertions.assertEquals(mangerDto,tradingSystemFacade.addManager(ownerName,storeId,newManagerName,uuid));
        }

        /**
         * test
         * UC 4.6
         * add permission the manger in the store
         * @pre the appointing owner is owner in the store and has permissions to edit managers
         */
        @Test
        void addPermission() {
            //the function arguments
            String appointingOwnerName = "owner name"; //appointing
            int storeId = 0;    //store the manager belongs to
            String managerEditedName = "name of edited manager"; //manager to edit
            String permission = "edit managers";    //the permoission of the owner
            UUID uuid = new UUID(1,1);
            //mocks
            when(tradingSystem.getUser(appointingOwnerName,uuid)).thenReturn(userSystem);//get the user system of owner
            when(userSystem.getOwnerOrManagerWithPermission(storeId,StorePermission.EDIT_Managers)).thenReturn(store);
            when(store.getManager(userSystem,managerEditedName)).thenReturn(userSystem);//get user system of manager
            when(tradingSystemDao.addPermissionToManager(store,userSystem,userSystem,StorePermission.EDIT_Managers)).thenReturn(true);
            Assertions.assertTrue(tradingSystemFacade.addPermission(appointingOwnerName,storeId,managerEditedName,permission,uuid));
        }

        /**
         * test
         * UC 4.6
         * remove permissions from manager
         * @pre the remover is owner in the store and the manager is manager in the store
         */
        @Test
        void removePermission() {
            //the function arguments
            String appointingOwnerName = "owner name"; //appointing
            int storeId = 0;    //store the manager belongs to
            String managerEditedName = "name of edited manager"; //manager to edit
            String permission = "edit managers";    //the permoission of the owner
            UUID uuid = new UUID(1,1);
            //mocks
            when(tradingSystem.getUser(appointingOwnerName,uuid)).thenReturn(userSystem);//get the user system of owner
            when(userSystem.getOwnerStore(storeId)).thenReturn(store);
            when(store.getManager(userSystem,managerEditedName)).thenReturn(userSystem);//get the usersystem object of manager
            when(tradingSystemDao.removePermission(store,userSystem,userSystem,StorePermission.EDIT_Managers)).thenReturn(true);
            Assertions.assertTrue(tradingSystemFacade.removePermission(appointingOwnerName,storeId,managerEditedName,permission,uuid));
        }


        /**
         * test
         * UC 4.7
         * remove manger from the store by the owner that appointed him
         */
        @Test
        void removeManager() {
            //params for func
            String appointingOwnerName = "owner name";
            int storeId = 0;
            String managerToRemoveName = "managerNameToRemove";
            UUID uuid = new UUID(1,1);
            //mocks
            when(tradingSystem.getUser(appointingOwnerName,uuid)).thenReturn(userSystem);//get usersystem of owner
            when(userSystem.getOwnerOrManagerWithPermission(storeId,StorePermission.EDIT_Managers)).thenReturn(store);
            when(store.getManager(userSystem,managerToRemoveName)).thenReturn(userSystem);//get usersystem of manager to remove
            when(tradingSystem.removeManager(store,userSystem,userSystem)).thenReturn(true);
            Assertions.assertTrue(tradingSystemFacade.removeManager(appointingOwnerName,storeId,managerToRemoveName,uuid));
        }

        /**
         * UC 4.4
         * remove owner from the store by the owner that appointed him
         */
        @Test
        void removeOwner() {
            //params for func
            String appointingOwnerName = "owner name";
            int storeId = 0;
            String ownerToRemoveName = "ownerNameToRemove";
            UUID uuid = new UUID(1,1);
            //mocks
            when(tradingSystem.getUser(appointingOwnerName,uuid)).thenReturn(userSystem);//get usersystem of owner
            when(userSystem.getOwnerStore(storeId)).thenReturn(store);
            when(store.getOwnerToRemove(userSystem,ownerToRemoveName)).thenReturn(userSystem);//get usersystem of manager to remove
            when(tradingSystem.removeOwner(store,userSystem,userSystem)).thenReturn(true);
            Assertions.assertTrue(tradingSystemFacade.removeOwner(appointingOwnerName,storeId,ownerToRemoveName,uuid));
        }

        /**
         * UC 3.1
         * the username logout from the system
         * @pre user with username name is logged in
         */
        @Test
        void logout() {
            String userTologOutName = "username";
            UUID uuid = new UUID(1,1);
            when(tradingSystem.getUser(userTologOutName,uuid)).thenReturn(userSystem);
            when(tradingSystem.logout(userSystem)).thenReturn(true);
            Assertions.assertTrue(tradingSystemFacade.logout(userTologOutName,uuid));
        }

        /**
         * test
         * UC 3.2
         * open new store
         */
        @Test
        void openStore() {
            StoreDto demoOpenedStoreDto = mock(StoreDto.class);
            //fuc. params
            String owner = "opener of the store";
            String stroreName = "storeName";
            String description = "store description";
            UUID uuid = new UUID(1,1);
            //mocks
            when(tradingSystem.getUser(owner,uuid)).thenReturn(userSystem);
            when(tradingSystem.openStore(userSystem,stroreName,description)).thenReturn(store);
            modelMapper = mock(ModelMapper.class);
            when(modelMapper.map(store,StoreDto.class)).thenReturn(demoOpenedStoreDto);
            tradingSystemFacade = new TradingSystemFacade(tradingSystem,modelMapper,factoryObjects,serviceFacade,tradingSystemDao);
            Assertions.assertEquals(demoOpenedStoreDto,tradingSystemFacade.openStore(owner,stroreName,description,uuid));
        }

        /**
         * test
         * UC 2.2
         * register new user to the system
         */
        @Test
        void registerUser() {
            // params for func + image
            String nameToRegister = "username";
            String passToRegister = "password";
            String firstName = "use";
            String lastName = "r";
            //mocks
            when(factoryObjects.createSystemUser(nameToRegister,firstName,lastName,passToRegister)).thenReturn(userSystem);
            when(tradingSystem.registerNewUser(userSystem,userImage)).thenReturn(true);
            Assertions.assertTrue(tradingSystemFacade.registerUser(nameToRegister,passToRegister,firstName,lastName,userImage));
        }

        /**
         * test
         * UC 2.3
         * user login to the system
         */
        @Test
        void login() {
            //valid uuid ans and loggedin as not admin
            Pair<UUID,Boolean> loginRes = new Pair<>(new UUID(1,1),false);
            String username = "username";
            String password = "password";
            when(tradingSystem.login(username,password)).thenReturn(loginRes);
            Assertions.assertEquals(loginRes,tradingSystemFacade.login(username,password));
        }

        /**
         * test UC 2.6 save product iin shopping bag
         */
        @Test
        void saveProductInShoppingBag() {
            //paramas to func
            String username = "saving user";
            int storeId = 0; //the store related to the bag
            int productSN = 1; //the id of the product to save
            int amount = 10; //quantity to save from that product
            UUID uuid = new UUID(1,1);
            //mocks
            when(tradingSystem.getStore(storeId)).thenReturn(store);
            when(store.getProduct(productSN)).thenReturn(product);
            when(tradingSystem.getShoppingCart(username,uuid)).thenReturn(shoppingCart);
            when(tradingSystemDao.saveProductInShoppingBag(username,shoppingCart,store,product,amount)).thenReturn(true);
        }

        /**test
         * UC 2.7
         * view products in shopping cart
         * @pre user with username has is registered in the system
         */
        @Test
        void watchShoppingCart() {
            String username = "username";
            UUID uuid = new UUID(1,1);
            //mocks
            ShoppingCartDto shoppingCartDto = mock(ShoppingCartDto.class);  //the returned SC
            when(tradingSystem.getShoppingCart(username,uuid)).thenReturn(shoppingCart);
            modelMapper = mock(ModelMapper.class);
            when(modelMapper.map(shoppingCart,ShoppingCartDto.class)).thenReturn(shoppingCartDto);
            tradingSystemFacade = new TradingSystemFacade(tradingSystem,modelMapper,factoryObjects,serviceFacade,tradingSystemDao);
            Assertions.assertEquals(shoppingCartDto,tradingSystemFacade.watchShoppingCart(username,uuid));
        }

        /**
         * test
         * UC 2.7
         * remove product in shopping bag
         * @pre the product is in shopping bag of the remover user
         */
        @Test
        void removeProductInShoppingBag() {
            //params for method
            String username = "username";
            int storeId = 0;
            int productSN = 1;
            UUID uuid = new UUID(1,1);
            //mocks
            when(tradingSystem.getStore(storeId)).thenReturn(store);
            when(store.getProduct(productSN)).thenReturn(product);
            when(tradingSystem.getShoppingCart(username,uuid)).thenReturn(shoppingCart);
            when(tradingSystemDao.removeProductInShoppingBag(username,shoppingCart,store,product)).thenReturn(true);
            Assertions.assertTrue(tradingSystemFacade.removeProductInShoppingBag(username,storeId,productSN,uuid));
        }

        /**
         *test ------------this TC is tested in integration--------------------
         * UC 2.8
         * purchase shopping cart use for guest
         */
        @Test
        void purchaseShoppingCart() {
        }

        /**
         * test
         * UC 2.8
         * purchase shopping cart of resisted user
         */
        @Test
        void testPurchaseShoppingCart() {
        }
        /**--------------------------------------------------**/

        /**
         * -----------tested in integration---------------------
         */
        @Test
        void connectNotificationSystem() {
        }

        @Test
        void sendNotification() {
        }
        /**-------------------------------------------------------------*/

        /**
         * UC 2.7
         * add a new product to cart
         */
        @Test
        void addProductToShoppingCart() {
            //params
            String userName = "name";
            int amount = 10;
            ProductDto productDto = mock(ProductDto.class);
            UUID uuid = new UUID(1,1);
            int storeId = 0;
            //mocks
            when(product.getStoreId()).thenReturn(storeId);
            modelMapper = mock(ModelMapper.class);
            when(modelMapper.map(productDto,Product.class)).thenReturn(product);
            when(tradingSystem.getStore(storeId)).thenReturn(store);
            when(tradingSystem.getShoppingCart(userName,uuid)).thenReturn(shoppingCart);
            when(tradingSystemDao.saveProductInShoppingBag(userName,shoppingCart,store,product,amount)).thenReturn(true);
            tradingSystemFacade = new TradingSystemFacade(tradingSystem,modelMapper,factoryObjects,serviceFacade,tradingSystemDao);
            Assertions.assertTrue(tradingSystemFacade.addProductToShoppingCart(userName,amount,productDto,uuid));

        }

        /**
         * test functionality of get price of
         * cart thru the facade
         */
        @Test
        void testGetTotalPriceOfShoppingCart() {
            String username = "name";
            UUID uuid = new UUID(1,1);
            Pair<Double,Double> resPair = new Pair<>(1.1,1.1);
            when(tradingSystem.getShoppingCart(username,uuid)).thenReturn(shoppingCart);
            when(tradingSystem.getTotalPrices(shoppingCart)).thenReturn(resPair);
            Assertions.assertEquals(resPair,tradingSystemFacade.getTotalPriceOfShoppingCart(username,uuid));
        }

        /**
         * test if some user is owner in store with id store id
         */
        @Test
        void isOwner() {
            String username = "name";
            int storeId = 0;
            UUID uuid = new UUID(1,1);
            //mocks
            when(tradingSystem.getUser(username,uuid)).thenReturn(userSystem);
            when(userSystem.isOwner(storeId)).thenReturn(true);
            Assertions.assertTrue(tradingSystemFacade.isOwner(username,storeId,uuid));
        }

        /**
         * test
         * UC 2.7
         * edit products in cart
         */
        @Test
        void changeProductAmountInShoppingBag() {
            //params
            String username = "name";
            int storeId = 0;
            int amount = 10;
            int productSN = 1;
            UUID uuid = new UUID(1,1);
            //mocks
            when(tradingSystem.getShoppingCart(username,uuid)).thenReturn(shoppingCart);
            when(tradingSystemDao.changeProductAmountInShoppingBag(username,shoppingCart,storeId,amount,productSN)).thenReturn(true);
            Assertions.assertTrue(tradingSystemFacade.changeProductAmountInShoppingBag(username,storeId,amount,productSN,uuid));
        }

        /**
         * test functionality of approve owner thru facade
         */
        @Test
        void approveOwner() {
            String ownerName = "oName";
            int storeId = 0;
            String ownerToApprove = "to approve";
            boolean status = true;
            UUID uuid = new UUID(1,1);
            //mocks
            when(tradingSystem.getUser(ownerName,uuid)).thenReturn(userSystem);
            when(userSystem.getOwnerOrManagerWithPermission(storeId,StorePermission.EDIT_Managers)).thenReturn(store);
            when(tradingSystemDao.approveOwner(store,userSystem,ownerToApprove,status)).thenReturn(true);
            Assertions.assertTrue(tradingSystemFacade.approveOwner(ownerName,storeId,ownerToApprove,status,uuid));
        }

        /**
         * send daily visitor activation thru facade
         */
        @Test
        void sendDailyVisitor() {
            UpdateDailyVisitor updateDailyVisitor = mock(UpdateDailyVisitor.class);
            when(serviceFacade.sendDailyVisitor(updateDailyVisitor)).thenReturn(true);
            Assertions.assertTrue(tradingSystemFacade.sendDailyVisitor(updateDailyVisitor));
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

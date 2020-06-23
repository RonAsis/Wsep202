package com.wsep202.TradingSystem.domain.trading_system_management;

import com.github.rozidan.springboot.modelmapper.WithModelMapper;
import com.wsep202.TradingSystem.config.ObjectMapperConfig;
import com.wsep202.TradingSystem.config.TradingSystemConfiguration;
import com.wsep202.TradingSystem.config.httpSecurity.HttpSecurityConfig;
import com.wsep202.TradingSystem.domain.factory.FactoryObjects;
import com.wsep202.TradingSystem.domain.trading_system_management.discount.Discount;
import com.wsep202.TradingSystem.domain.trading_system_management.policy_purchase.Purchase;
import com.wsep202.TradingSystem.domain.trading_system_management.purchase.BillingAddress;
import com.wsep202.TradingSystem.domain.trading_system_management.purchase.PaymentDetails;
import com.wsep202.TradingSystem.domain.trading_system_management.statistics.UpdateDailyVisitor;
import com.wsep202.TradingSystem.dto.*;
import com.wsep202.TradingSystem.helprTests.AssertionHelperTest;
import com.wsep202.TradingSystem.service.ServiceFacade;
import com.wsep202.TradingSystem.service.user_service.BuyerRegisteredService;
import com.wsep202.TradingSystem.service.user_service.GuestService;
import javafx.util.Pair;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.security.core.parameters.P;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

import static com.wsep202.TradingSystem.helprTests.SetUpObjects.*;
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

    @ExtendWith(SpringExtension.class)
    @ContextConfiguration(classes = {TradingSystemConfiguration.class, HttpSecurityConfig.class, ObjectMapperConfig.class})
    @SpringBootTest(args = {"admin","admin"})
    @WithModelMapper
    @Nested
    public class TradingSystemFacadeTestIntegration {

        @Autowired
        private ModelMapper modelMapper = new ModelMapper();
        //required entities for tests
        @Autowired
        private TradingSystem tradingSystem;
        @Autowired
        private TradingSystemFacade tradingSystemFacade;
        private UserSystem userSystem;
        private Store store;
        @Autowired
        private FactoryObjects factoryObjects;
        private PaymentDetails paymentDetails;
        private BillingAddress billingAddress;
        @Autowired
        private ServiceFacade serviceFacade;
        private MultipartFile userImage;
        private ShoppingCart shoppingCart;
        @Autowired
        private TradingSystemDao tradingSystemDao;
        private Product product;
        private Discount discount;
        private MangerStore mangerStore;
        UserSystem user;

        int name = 0;

        @BeforeEach
        void setUp() {
        }

        @AfterEach
        void tearDown() {
            tradingSystemDao.setIsLogins(new HashMap<>());
            tradingSystemDao.setStores(new HashSet<>());
            tradingSystemDao.setUsers(new HashSet<>());
        }

        /**
         * test: logged in user view his purchase history
         * get the purchase history of logged in user with uuid
         * @pre user is logged in with receipts
         * UC 3.7
         */
        @Test
        void userViewHisPurchaseHistory() {
            user = setUserSystem();
          //  user.setUserName("4");
            setupRegister(user);
            UUID uuid = setupLogin(user);
            Set<Receipt> receipts = setUpReceipts();
            UserSystem userInSystem = tradingSystem.getUser(user.getUserName(),uuid);
            userInSystem.setReceipts(receipts);
            //call the function
            //tradingSystem.getUser(user.getUserName(), uuid).setReceipts(receipts);
            List<ReceiptDto> receiptDtos = tradingSystemFacade.viewPurchaseHistory(user.getUserName(),uuid);
            AssertionHelperTest.assertReceipts(receipts.stream().collect(Collectors.toList()), receiptDtos);
        }


        /**
         * view purchase history of admin
         *  * UC 6.4.1
         */
        @Test
        void testViewPurchaseHistoryAdmin() {
            UUID uuid = setupLogin("admin","admin");
            Set<Receipt> receipts = setUpReceipts();
            UserSystem userInSystem = tradingSystem.getUser("admin",uuid);
            userInSystem.setReceipts(receipts);
            //call the function
            //tradingSystem.getUser(user.getUserName(), uuid).setReceipts(receipts);
            List<ReceiptDto> receiptDtos = tradingSystemFacade.viewPurchaseHistory("admin",uuid);
            AssertionHelperTest.assertReceipts(receipts.stream().collect(Collectors.toList()), receiptDtos);
        }


        /**
         * UC 5.1
         * view purchase history of store as manager
         * 1. register 2 users
         * 2. login with one of them
         * 3. open store
         * 4. add manager (the second)
         * 4.5. add receipts to the store
         * 5. login with the second
         * 6.view history after create receipts for him
         */
        @Test
        void testViewPurchaseHistoryManager() {
            int storeId = 0;
            UserSystem user1 = setUserSystem();
            user1.setUserName("2");
            UserSystem user2 = setUserSystem2();
            user2.setUserName("3");
            setupRegister(user1);
            setupRegister(user2);
            UUID uuid = setupLogin(user1);
            setupOpenStore(user1,uuid);
            Store store1 = tradingSystem.getStore(storeId);
            Set<Receipt> receipts = setUpReceipts();
            store1.setReceipts(receipts);
            setupAddManager(user1,storeId,user2.getUserName(),uuid);
            UUID uuid2 = setupLogin(user2);
            List<ReceiptDto> receiptDtos = tradingSystemFacade.viewPurchaseHistoryOfManager(user2.getUserName(),storeId,uuid2);
            AssertionHelperTest.assertReceipts(receipts.stream().collect(Collectors.toList()), receiptDtos);
        }

        /**
         * UC 4.10
         * owner view purchase history of store
         */
        @Test
        void viewPurchaseHistoryOfOwner() {
            int storeId = 0;
            UserSystem user1 = setUserSystem();
            user1.setUserName("1");
            setupRegister(user1);
            UUID uuid = setupLogin(user1);
            setupOpenStore(user1,uuid);
            Store store1 = tradingSystem.getStore(storeId);
            Set<Receipt> receipts = setUpReceipts();
            store1.setReceipts(receipts);
            List<ReceiptDto> receiptDtos = tradingSystemFacade.viewPurchaseHistoryOfOwner(user1.getUserName(),storeId,uuid);
            AssertionHelperTest.assertReceipts(receipts.stream().collect(Collectors.toList()), receiptDtos);
        }

        /**
         * test UC 4.1
         * add product to store
         * 1. register
         * 2.login
         * 3.open store
         * 4.add product
         */
        @Test
        void addProduct() {
            int storeId = 0;
            UserSystem user1 = setUserSystem();
            user1.setUserName("koko");
            setupRegister(user1);
            UUID uuid = setupLogin(user1);
            setupOpenStore(user1,uuid);
            Set<Product> products = setUpProducts();
            Product product = products.iterator().next();
            product.setStoreId(storeId);
            ProductDto productDto = tradingSystemFacade.addProduct(user1.getUserName(),storeId,product.getName()
                    ,product.getCategory().category
                    ,product.getAmount(),product.getCost()
                    ,uuid);
            AssertionHelperTest.assertProduct(product,productDto);
        }

        /**
         * UC 4.2
         * owner wants add a new policy  an existing one a discount policy from store
         */
        @Test
        void addDiscount() {
            int storeId = 0;
            UserSystem user1 = setUserSystem();
            setupRegister(user1);
            UUID uuid = setupLogin(user1);
            setupOpenStore(user1,uuid);
            List<Discount> discounts = setUpDiscounts();
            Discount discount = discounts.get(0);
            DiscountDto discountDto1 = modelMapper.map(discount,DiscountDto.class);
            DiscountDto discountDto2 = tradingSystemFacade.addEditDiscount(user1.getUserName(),storeId,
                    discountDto1,uuid);
            AssertionHelperTest.assertionDiscount(discount,discountDto2);
        }
        /**
         * UC 4.2
         * owner wants edit a new policy or edit an existing one a discount policy from store
         * 1. add discount
         * 2. edit discount
         */
        @Test
        void editDiscount() {
            //first add
            int storeId = 0;
            UserSystem user1 = setUserSystem();
            setupRegister(user1);
            UUID uuid = setupLogin(user1);
            setupOpenStore(user1,uuid);
            List<Discount> discounts = setUpDiscounts();
            Discount discount = discounts.get(0);
            DiscountDto discountDto1 = modelMapper.map(discount,DiscountDto.class);
            DiscountDto discountDto2 = tradingSystemFacade.addEditDiscount(user1.getUserName(),storeId,
                    discountDto1,uuid);
            //now edit
            //discount should have id>0 so go to edit
            DiscountDto discountDto3 = tradingSystemFacade.addEditDiscount(user1.getUserName(),storeId,
                    discountDto2,uuid);
            AssertionHelperTest.assertionDiscount(discount,discountDto3);
        }

        /**
         * UC 4.2
         * owner wants add a new policy from store
         */
        @Test
        void addPurchase() {
            int storeId = 0;
            UserSystem user1 = setUserSystem();
            setupRegister(user1);
            UUID uuid = setupLogin(user1);
            setupOpenStore(user1,uuid);
            List<Purchase> purchases = setUpPurchases();
            Purchase purchase = purchases.get(0);
            PurchasePolicyDto purchaseDto1 = modelMapper.map(purchase,PurchasePolicyDto.class);
            PurchasePolicyDto purchaseDto2 = tradingSystemFacade.addEditPurchase(user1.getUserName(),storeId,
                    purchaseDto1,uuid);
            AssertionHelperTest.assertionPurchase(purchase,purchaseDto2);
        }
        /**
         * UC 4.2
         * owner wants edit a new policy from store
         */
        @Test
        void editPurchase() {
            int storeId = 0;
            UserSystem user1 = setUserSystem();
            setupRegister(user1);
            UUID uuid = setupLogin(user1);
            setupOpenStore(user1,uuid);
            List<Purchase> purchases = setUpPurchases();
            Purchase purchase = purchases.get(0);
            PurchasePolicyDto purchaseDto1 = modelMapper.map(purchase,PurchasePolicyDto.class);
            PurchasePolicyDto purchaseDto2 = tradingSystemFacade.addEditPurchase(user1.getUserName(),storeId,
                    purchaseDto1,uuid);
            //now edit
            //purchase should have id>0 so go to edit
            PurchasePolicyDto purchaseDto3 = tradingSystemFacade.addEditPurchase(user1.getUserName(),storeId,
                    purchaseDto2,uuid);
            AssertionHelperTest.assertionPurchase(purchase,purchaseDto3);
        }

        /**
         * test: delete product from store
         * @pre there is product to delete in store
         * 1. add product to store to meet pre
         * 2. delete product
         */
        @Test
        void deleteProductFromStore() {
            //add
            int storeId = 0;
            UserSystem user1 = setUserSystem();
            setupRegister(user1);
            UUID uuid = setupLogin(user1);
            setupOpenStore(user1,uuid);
            Set<Product> products = setUpProducts();
            Product product = products.iterator().next();
            product.setStoreId(storeId);
            ProductDto productDto = tradingSystemFacade.addProduct(user1.getUserName(),storeId,product.getName()
                    ,product.getCategory().category
                    ,product.getAmount(),product.getCost()
                    ,uuid);
            //delete the product from store
            Assertions.assertTrue(tradingSystemFacade.deleteProductFromStore(user1.getUserName(),storeId,
                    0,uuid));
        }

        /**
         * test UC 4.1
         * edit product
         * 1.add product
         * 2.edit
         */
        @Test
        void editProduct() {
            int storeId = 0;
            UserSystem user1 = setUserSystem();
            setupRegister(user1);
            UUID uuid = setupLogin(user1);
            setupOpenStore(user1,uuid);
            Set<Product> products = setUpProducts();
            Product product = products.iterator().next();
            product.setStoreId(storeId);
            ProductDto productDto = tradingSystemFacade.addProduct(user1.getUserName(),storeId,product.getName()
                    ,product.getCategory().category
                    ,product.getAmount(),product.getCost()
                    ,uuid);
            //edit
            //change amount
            product.setAmount(7);
            boolean res = tradingSystemFacade.editProduct(user1.getUserName(),storeId,1,product.getName(),
                    product.getCategory().category,product.getAmount(),product.getCost(),uuid);
            Assertions.assertTrue(res);
        }

        /**
         * UC 4.3
         * add new owner to store
         * owner appoint new owner to his store
         */
        @Test
        void addOwner() {
            int storeId = 0;
            UserSystem user1 = setUserSystem();
            UserSystem user2 = setUserSystem2();    //appointee
            setupRegister(user1);
            setupRegister(user2);
            UUID uuid = setupLogin(user1);
            setupOpenStore(user1,uuid);
            //add owner
            boolean res = tradingSystemFacade.addOwner(user1.getUserName(),storeId,
                    user2.getUserName(),uuid);
            Assertions.assertTrue(res);

        }

        /**
         * UC 4.5
         * add manger to the store with the default permission
         */
        @Test
        void addManager() {
            int storeId = 0;
            UserSystem user1 = setUserSystem();
            UserSystem user2 = setUserSystem2();    //appointee
            setupRegister(user1);
            setupRegister(user2);
            UUID uuid = setupLogin(user1);
            setupOpenStore(user1,uuid);
            //add manager
            ManagerDto managerDto = tradingSystemFacade.addManager(user1.getUserName(),storeId,
                    user2.getUserName(),uuid);
            Assertions.assertEquals(user2.getUserName(),managerDto.getUsername());
        }

        /**
         * UC 4.6
         * add permission the manger in the store
         */
        @Test
        void addPermission() {
            int storeId = 0;
            UserSystem user1 = setUserSystem();
            UserSystem user2 = setUserSystem2();    //appointee
            setupRegister(user1);
            setupRegister(user2);
            UUID uuid = setupLogin(user1);
            setupOpenStore(user1,uuid);
            //add manager
            setupAddManager(user1,storeId,user2.getUserName(),uuid);
            //add permission
            Assertions.assertTrue(tradingSystemFacade.addPermission(user1.getUserName(),storeId,user2.getUserName(),
                    StorePermission.EDIT_DISCOUNT.function,uuid));
        }

        /**
         * UC 4.6
         * remove permission the manger in the store
         * 1. add permission
         * 2. remove permission
         */
        @Test
        void removePermission() {
            //add permission
            int storeId = 0;
            UserSystem user1 = setUserSystem();
            UserSystem user2 = setUserSystem2();    //appointee
            setupRegister(user1);
            setupRegister(user2);
            UUID uuid = setupLogin(user1);
            setupOpenStore(user1,uuid);
            //add manager
            setupAddManager(user1,storeId,user2.getUserName(),uuid);
            //add permission
            setupAddPermission(user1,storeId,user2, StorePermission.EDIT_DISCOUNT.function,uuid);
            Assertions.assertTrue(tradingSystemFacade.removePermission(user1.getUserName(),
                    storeId,user2.getUserName(),StorePermission.EDIT_DISCOUNT.function,uuid));
        }

        /**
         * test UC 4.7
         * remove manager
         */
        @Test
        void removeManager() {
            //first add manager
            int storeId = 0;
            UserSystem user1 = setUserSystem();
            UserSystem user2 = setUserSystem2();    //appointee
            setupRegister(user1);
            setupRegister(user2);
            UUID uuid = setupLogin(user1);
            setupOpenStore(user1,uuid);
            //add manager
            ManagerDto managerDto = tradingSystemFacade.addManager(user1.getUserName(),storeId,
                    user2.getUserName(),uuid);
            //remove manager
            Assertions.assertTrue(tradingSystemFacade.removeManager(user1.getUserName(),storeId,
                    user2.getUserName(),uuid));
        }

        /**
         * test UC 4.4
         * remove owner from the store by the owner that appointed him
         */
        @Test
        void removeOwner() {
            int storeId = 0;
            UserSystem user1 = setUserSystem();
            UserSystem user2 = setUserSystem2();    //appointee
            setupRegister(user1);
            setupRegister(user2);
            UUID uuid = setupLogin(user1);
            setupOpenStore(user1,uuid);
            //add owner
             tradingSystemFacade.addOwner(user1.getUserName(),storeId,
                    user2.getUserName(),uuid);
            //remove owner
            boolean res = tradingSystemFacade.removeOwner(user1.getUserName(),storeId,
                    user2.getUserName(),uuid);
            Assertions.assertTrue(res);
        }

        /**test
         * UC 3.1
         * the user name logout from the system
         */
        @Test
        void logout() {
            UserSystem user1 = setUserSystem();
            setupRegister(user1);
            UUID uuid = setupLogin(user1);
            boolean res = tradingSystemFacade.logout(user1.getUserName(),uuid);
            Assertions.assertTrue(res);
        }

        /**
         * test
         * UC 3.2
         * open new store
         */
        @Test
        void openStore() {
            UserSystem user1 = setUserSystem();
            setupRegister(user1);
            UUID uuid = setupLogin(user1);
            Store store = setupCreateStore();
            StoreDto storeDto = tradingSystemFacade.openStore(user1.getUserName(),store.getStoreName(),
                    store.getDescription(),uuid);
            AssertionHelperTest.assertionStore(store,storeDto);
        }


        /**
         * test
         * UC 2.2
         * register new user to the system
         */
        @Test
        void registerUser() {
            UserSystem user1 = setUserSystem();
            boolean res = tradingSystemFacade.registerUser(user1.getUserName(),user1.getPassword(),
                    user1.getFirstName(),user1.getLastName(),null);
            Assertions.assertTrue(res);
        }

        /**
         * test
         * UC 2.3
         * user login to the system
         */
        @Test
        void login() {
            UserSystem user1 = setUserSystem();
            setupRegister(user1);
            Pair<UUID,Boolean> res = tradingSystemFacade.login(user1.getUserName(),user1.getPassword());
            Assertions.assertNotNull(res.getKey());
        }

        /**
         * UC 2.6 - save product in shopping bag
         * 1. register
         * 2. login
         * 3. open store
         * 4. add product to store
         * 5. add product to bag
         */
        @Test
        void saveProductInShoppingBag() {
            int storeId = 0;
            UserSystem user1 = setUserSystem();
            setupRegister(user1);
            UUID uuid = setupLogin(user1);
            setupOpenStore(user1,uuid);
            Product product = factoryObjects.createProduct("mouse",ProductCategory.ELECTRONICS,
                    8,39,storeId);
            setupAddProduct(user1,uuid,storeId,product);
            boolean res = tradingSystemFacade.saveProductInShoppingBag(user1.getUserName(),storeId,
                    product.getProductSn(), 2,uuid);
            Assertions.assertTrue(res);

        }

        /**
         * UC 2.7
         * view products in shopping cart
         * verify that the logged in user have shopping cart
         */
        @Test
        void watchShoppingCart() {
            int storeId = 0;
            UserSystem user1 = setUserSystem();
            setupRegister(user1);
            UUID uuid = setupLogin(user1);
            ShoppingCartDto shoppingCartDto = tradingSystemFacade
                    .watchShoppingCart(user1.getUserName(),uuid);
            Assertions.assertNotNull(shoppingCartDto);
        }

        /**
         * UC 2.7
         * remove product in shopping bag
         * 1. add product to shopping bag
         * 2. remove the product from shopping bag
         */
        @Test
        void removeProductInShoppingBag() {
            //add
            int storeId = 0;
            UserSystem user1 = setUserSystem();
            setupRegister(user1);
            UUID uuid = setupLogin(user1);
            setupOpenStore(user1,uuid);
            Product product = factoryObjects.createProduct("mouse",ProductCategory.ELECTRONICS,
                    8,39,storeId);
            setupAddProduct(user1,uuid,storeId,product);
            tradingSystemFacade.saveProductInShoppingBag(user1.getUserName(),storeId,
                    product.getProductSn(), 2,uuid);
            //remove
            boolean res = tradingSystemFacade.removeProductInShoppingBag(user1.getUserName(),
                    storeId,product.getProductSn(),uuid);
            Assertions.assertTrue(res);
        }

        /**
         * UC 2.8
         * purchase shopping cart use for guest
         */
        @Test
        void purchaseShoppingCart() {
            //open store and product
            int storeId = 0;
            UserSystem user1 = setUserSystem();
            setupRegister(user1);
            UUID uuid = setupLogin(user1);
            setupOpenStore(user1,uuid);
            Product product = factoryObjects.createProduct("mouse",ProductCategory.ELECTRONICS,
                    8,39,storeId);
            setupAddProduct(user1,uuid,storeId,product);
            ShoppingCartDto shoppingCartDto = createCartDto();
            PaymentDetailsDto paymentDetails = createPaymentDto();
            BillingAddressDto billingAddressDto = createBillingAddrDto();
            List<ReceiptDto> receipts = tradingSystemFacade.purchaseShoppingCart(shoppingCartDto,
                    paymentDetails,billingAddressDto);
            Assertions.assertNotNull(receipts);
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

        ///////////////////////////setups

        /**
         * user 1 gives function permission to user 2
         */
        private void setupAddPermission(UserSystem user1, int storeId, UserSystem user2, String function, UUID uuid) {
            tradingSystemFacade.addPermission(user1.getUserName(),storeId,user2.getUserName(),function,uuid);
        }

        /**
         * user1 adds username as owner of the store with id storeId
         */
        private void setupAddManager(UserSystem user1, int storeId, String userName, UUID uuid) {
            tradingSystemFacade.addManager(user1.getUserName(),storeId,userName,uuid);
        }

        private void setupAddProduct(UserSystem user,UUID uuid,int storeId, Product product){
            tradingSystemFacade.addProduct(user.getUserName(),storeId,product.getName(),
                    product.getCategory().category,product.getAmount(),product.getCost(),uuid);
        }

        /**
         * create basic user for the tests
         */
        private UserSystem setUserSystem() {
            UserSystem user = UserSystem.builder()
                    .userName("barp")
                    .password("12345678")
                    .firstName("bar")
                    .lastName("per")
                    .isAdmin(false)
                    .build();
            return user;
        }
        private UserSystem setUserSystem2() {
            UserSystem user = UserSystem.builder()
                    .userName("sponge")
                    .password("12345677")
                    .firstName("sp")
                    .lastName("bob")
                    .isAdmin(false)
                    .build();
            return user;
        }
        /**
         * login the received user
         * return the uuid for the session of the logged in user
         */
        private UUID setupLogin(UserSystem user) {
            Pair<UUID,Boolean> res;
            res = tradingSystemFacade.login(user.getUserName(),user.getPassword());
            return res.getKey();
        }
        private UUID setupLogin(String username, String password) {
            Pair<UUID,Boolean> res;
            res = tradingSystemFacade.login(username,password);
            return res.getKey();
        }

        /**
         * register the received user to the system
         * @param user
         */
        private void setupRegister(UserSystem user) {
            boolean ans=tradingSystemFacade.registerUser(user.getUserName(),user.getPassword(),
                    user.getFirstName(),user.getLastName(),null);
            int i=0;
            i++;
        }
        /**
         * open store with user1
         */
        private void setupOpenStore(UserSystem user1,UUID uuid) {
            tradingSystemFacade.openStore(user1.getUserName(),""+user1.getUserName()+" store",
                    "store description",uuid);
        }

        /**
         * create store
         */
        private Store setupCreateStore() {
            return Store.builder()
                    .storeId(0)
                    .storeName("store name")
                    .description("store desc")
                    .build();
        }
        /**
         * create shopping cart
         */
        private ShoppingCartDto createCartDto() {
            Map<Integer,ShoppingBagDto> bags = new HashMap<>();
            Map<Integer,Integer> oneBag = new HashMap<>();
            oneBag.put(0,1);    //productid 1 with amount 1 in bag
            bags.put(0, ShoppingBagDto.builder()
                    .productListFromStore(oneBag)
                    .build());
            return ShoppingCartDto.builder()
                    .shoppingBags(bags)
                    .build();
        }
        //payment method and billing address
        private PaymentDetailsDto createPaymentDto() {
            return PaymentDetailsDto.builder()
                    .ccv("123")
                    .creditCardNumber("123456789")
                    .holderIDNumber("305026487")
                    .holderName("bill")
                    .month("3")
                    .year("2222")
                    .build();
        }

        private BillingAddressDto createBillingAddrDto() {
            return BillingAddressDto.builder()
                    .address("gilon 1")
                    .city("gilom")
                    .country("astonia")
                    .customerFullName("cust full name")
                    .zipCode("2010300")
                    .build();
        }

    }


}

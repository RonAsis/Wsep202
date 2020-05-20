package com.wsep202.TradingSystem.domain.trading_system_management;


//import Externals.PasswordSaltPair;
import com.wsep202.TradingSystem.config.TradingSystemConfiguration;
import com.wsep202.TradingSystem.domain.exception.*;
import com.wsep202.TradingSystem.domain.factory.FactoryObjects;
import com.wsep202.TradingSystem.domain.trading_system_management.purchase.BillingAddress;
import com.wsep202.TradingSystem.domain.trading_system_management.purchase.PaymentDetails;
import javafx.util.Pair;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

import static com.wsep202.TradingSystem.domain.trading_system_management.policy_purchase.Formatter.formatter;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class TradingSystemTest {

    /**
     * unit tests for the TradingSystem
     */
    @Nested
    public class TradingSystemTestUnit {

        ExternalServiceManagement externalServiceManagement;
        private TradingSystem tradingSystem;
        private TradingSystemDao tradingSystemDao;
        private UserSystem userSystem;
        private UserSystem userSystem1;
        private UserSystem userSystem2;
        private UserSystem userToRegister;
        private FactoryObjects factoryObjects;
        private UserSystem admin;
        private Store store;
        private Store store1;
        private Product product;
        private PaymentDetails paymentDetails;
        private BillingAddress billingAddress;
        private ShoppingCart testShoppingCart;
        private ShoppingBag testShoppingBag;
        private Product testProduct;
        private Receipt receipt;

        @BeforeEach
        void setUp() {
            externalServiceManagement = mock(ExternalServiceManagement.class);
            admin = UserSystem.builder()
                    .userName("admin")
                    .password("admin")
                    .build();
            tradingSystemDao = mock(TradingSystemDao.class);
            when(externalServiceManagement.getEncryptedPasswordAndSalt(admin.getPassword())).thenReturn(new PasswordSaltPair("hash","admin"));
            tradingSystem = new TradingSystem(externalServiceManagement, admin, tradingSystemDao);
            doNothing().when(externalServiceManagement).connect();
            userSystem = mock(UserSystem.class);
            userSystem1 = mock(UserSystem.class);
            userSystem2 = mock(UserSystem.class);
            factoryObjects = new FactoryObjects();
            String username = "usernameTest";
            String password = "passwordTest";
            String fName = "moti";
            String lName = "Banana";
            userToRegister = new UserSystem(username,fName,lName,password);
            store = mock(Store.class);
            store1 = mock(Store.class);
            product = mock(Product.class);
            paymentDetails = mock(PaymentDetails.class);
            billingAddress = mock(BillingAddress.class);
            testShoppingCart = mock(ShoppingCart.class);
            testShoppingBag = mock(ShoppingBag.class);
            receipt = mock(Receipt.class);
            doNothing().when(userSystem1).setOwnedStores(new HashSet<Store>());
            doNothing().when(userSystem1).setManagedStores(new HashSet<Store>());
            doNothing().when(userSystem).setOwnedStores(new HashSet<Store>());
            doNothing().when(userSystem).setManagedStores(new HashSet<Store>());
           // doNothing().when(tradingSystemDao).registerAdmin(admin);
        }

//        /**
//         * the following checks registration of valid user
//         */
//        @Test
//        void registerNewUserPositive() {
//            //mockup
//            registerAsSetupPass();
//            //setup
//            //the following user details are necessary for the login tests
//            //success: registration done. valid user details
//            Assertions.assertTrue(tradingSystem.registerNewUser(userToRegister,null));
//        }

        /**
         * checks handling with failure of registration
         * this test has to run after its respective positive test
         */
        @Test
        void registerNewUserWithARegisteredUser() {
            //registration with already registered user
            registerAsSetup(); //setup test of registration
            //fail: this user is already registered
            Assertions.assertFalse(tradingSystem.registerNewUser(userToRegister,null));
        }

        /**
         * checks handling with failure of null
         */
        @Test
        void registerNewUserNullUser() {
            //fail: this for null object
            Assertions.assertFalse(tradingSystem.registerNewUser(null,null));
        }


        /**
         * This test check if the login method succeeds when the parameters
         * are correct.
         */
        @Test
        void loginPositive(){
            setUpLogin();
            //mockup
            doNothing().when(userSystem).login();
            Pair<UUID, Boolean> ans = tradingSystem.login(userSystem.getUserName(),"passwordTest");
            Assertions.assertNotNull(ans.getKey());
        }

        /**
         * check that for a non-registered user the method login() fails
         */
        @Test
        void loginNotRegistered(){
            setUpLoginFail();
            Pair<UUID, Boolean> ans = tradingSystem.login(userSystem1.getUserName(),userSystem1.getPassword());
            Assertions.assertNull(ans);
            //check that the user wasn't added to logged-in map
        }

        /**
         * check that for a logged in user the method login() fails
         */
        @Test
        void loginAlreadyLoggedIn(){
            setUpLoginLoggedIn();
            Pair<UUID, Boolean> ans = tradingSystem.login(userSystem.getUserName(),"passwordTest");
            Assertions.assertNull(ans);
            //check that the user wasn't added again to logged-in map
        }

        /**
         * check the logout functionality of exists user in the system
         */
        @Test
        void logoutPositive() {
            setUpForLogout();
            //before remove there is 1 logged-in user
            Assertions.assertEquals(1, tradingSystem.getUsersLogin().size());
            //check that the right user is logged-in
            Assertions.assertTrue(tradingSystem.getUsersLogin().keySet().contains("usernameTest"));
            //check the ans from method is true
            Assertions.assertTrue(tradingSystem.logout(userSystem));
            //after remove there are 0 logged-in user
            Assertions.assertEquals(0, tradingSystem.getUsersLogin().size());
            //check that the user is not in logged-in list
            Assertions.assertFalse(tradingSystem.getUsersLogin().keySet().contains("usernameTest"));
        }


        /**
         * check handling with logout failure
         */
        @Test
        void logoutUserNotLoggedIn() {
            //mockup
            when(userSystem.isLogin()).thenReturn(false);
            when(userSystem.getUserName()).thenReturn("IAmUser");
            Assertions.assertFalse(tradingSystem.logout(userSystem));
        }

        /**
         * check that for null object returns false
         */
        @Test
        void logoutNullUser() {
            Assertions.assertFalse(tradingSystem.logout(null));
        }

        /**
         * get one of the administrators in the system
          */
        @Test
        void getAdministratorUserPositive() {
            setUpGetAdmin();
            //success: the correct admin returned back
            Assertions.assertEquals(admin,tradingSystem.getAdministratorUser("admin"));
        }

        /**
         * check handling with failure get user that is not an admin
         */
        @Test
        void getAdministratorUserNegative() {
            //fail: the requested user is not administrator in the system
            Throwable exception = Assertions.assertThrows(NotAdministratorException.class,()->tradingSystem.getAdministratorUser("moshe"));
            Assertions.assertEquals("The username '" + "moshe" + "' is not Administrator",exception.getMessage());
        }

        /**
         * check the getStoreByAdmin() functionality in case of exists admin in the system
         */
        @Test
        void getStoreByAdminPositive() {
            setUpForGetStoreAdmin();
            //check that the store that returns is the right one
            Assertions.assertEquals(tradingSystem.getStoreByAdmin
                    ("admin", 1, tradingSystem.getUsersLogin().get("admin")),store);
            tearDownGetStoreAdmin();
        }

        /**
         * check the getStoreByAdmin() functionality in case of not exists admin in the system
         */
        @Test
        void getStoreByAdminNotAnAdmin() {
            setUpForGetStoreAdmin();
            Assertions.assertThrows(NotAdministratorException.class, () -> {
                doNothing().when(store).setStoreId(1);
                tradingSystem.getStoreByAdmin("userSystem", 1, UUID.randomUUID());
            });
            tearDownGetStoreAdmin();
        }

        /**
         * check the getStoreByAdmin() with a wrong UUID code
         */
        @Test
        void getStoreByAdminWrongUUID() {
            setUpForGetStoreAdmin();
            Assertions.assertThrows(NotAdministratorException.class, () -> {
                doNothing().when(store).setStoreId(1);
                tradingSystem.getStoreByAdmin("admin", 1, UUID.randomUUID());
            });
            tearDownGetStoreAdmin();
        }

        /**
         * check the getStoreByAdmin() with a null object
         */
        @Test
        void getStoreByAdminNullObject() {
           // setUpForGetStoreAdmin();
            Assertions.assertThrows(NotAdministratorException.class, () -> {
                doNothing().when(store).setStoreId(1);
                tradingSystem.getStoreByAdmin(null, 1, UUID.randomUUID());
            });
            //tearDownGetStoreAdmin();
        }

        /**
         * check the getStore() functionality in case of exists store in the system
         */
        @Test
        void getStorePositive() {
            setUpGetStore();
            //check that this is the right store
            Assertions.assertEquals(store, tradingSystem.getStore(store.getStoreId()));
        }

        /**
         * check the getStore() functionality in case of not exists store in the system
         */
        @Test
        void getStoreNegative() {
            setUpGetStore();
            Assertions.assertThrows(StoreDontExistsException.class, () -> {
                tradingSystem.getStore(2);
            });
        }

        /**
         * check the getUser() functionality in case of exists user in the system
         */
        @Test
        void getUserPositive() {
            //set up an admin for test
            setUpGetUser();
            //check that the right user is returned
            Assertions.assertEquals(tradingSystem.getUser("userToReturn", tradingSystem.getUsersLogin().get("userToReturn")),userSystem);
            tearDownGetStoreAdmin();
        }


        /**
         * check the getUser() functionality in case of not exists user in the system
         */
        @Test
        void getUserWrongUser() {
            setUpGetUser();
            //check that the user does not exists in system
           Assertions.assertNull(tradingSystem.getUser("WrongUser", UUID.randomUUID()));
           tearDownGetStoreAdmin();
        }

        /**
         * check the getUserByAdmin() functionality in case of exists admin in the system
         */
        @Test
        void getUserByAdminPositive() {
            setUpGetUserByAdmin();
           Assertions.assertEquals(tradingSystem.getUserByAdmin
                    ("admin", "userToReturn", tradingSystem.getUsersLogin().get("admin")),userSystem);
            tearDownGetStoreAdmin();
        }


        /**
         * check the getUserByAdmin() functionality in case of not exists admin in the system
         */
        @Test
        void getUserByAdminNegative(){
            setUpGetUserByAdmin();
            Assertions.assertThrows(UserDontExistInTheSystemException.class, () -> {
                registerAsSetup();
                doNothing().when(userToRegister).setUserName("userToRegister");
                tradingSystem.getUserByAdmin(
                        "admin", "userToRegister", tradingSystem.getUsersLogin().get("admin"));
            });
            tearDownGetStoreAdmin();
        }


        /**
         * check the addMangerToStore() functionality in case of success in addNewManageStore and addManager
         */
        @Test
        void addMangerToStorePositive() {
            // userSystem <==> ownerUser
            // userSystem1 <==> newManagerUser
            when(store.addManager(userSystem,userSystem1)).thenReturn(any());
            when(userSystem1.addNewManageStore(store)).thenReturn(true);
            Assertions.assertNotNull(tradingSystem.addMangerToStore(store, userSystem, userSystem1.getUserName()));
        }

        /**
         * check the addMangerToStore() functionality in case of not initialized parameters.
         */
        @Test
        void addMangerToStoreNullParams() {
            Store storeNull = mock(Store.class);
            UserSystem userSystemNull1 = mock(UserSystem.class);
            UserSystem userSystemNull2 = mock(UserSystem.class);
            Assertions.assertFalse(tradingSystem.addMangerToStore(storeNull, userSystemNull1, userSystemNull2.getUserName()) != null);
        }

        /**
         * check the addMangerToStore() functionality in case of failure in addNewManageStore and addManager
         */
        @Test
        void addMangerToStoreNegative() {
            // userSystem <==> ownerUser
            // userSystem1 <==> newManagerUser
            when(store.addManager(userSystem,userSystem1)).thenReturn(any());
            when(userSystem1.addNewManageStore(store)).thenReturn(false);
            Assertions.assertNull(tradingSystem.addMangerToStore(store, userSystem, userSystem1.getUserName()));
        }

        /**
         * check the addOwnerToStore() functionality in case of success in addNewOwnedStore and addOwner
         */
        @Test
        void addOwnerToStorePositive() {
            // userSystem <==> ownerUser
            // userSystem1 <==> newManagerUser
            when(store.addOwner(userSystem,userSystem1)).thenReturn(true);
            when(userSystem1.addNewOwnedStore(store)).thenReturn(true);
            when(userSystem1.addNewOwnedStore(store)).thenReturn(true);
            when(tradingSystemDao.getUserSystem(userSystem1.getUserName())).thenReturn(Optional.of(userSystem1));
            Assertions.assertTrue(tradingSystem.addOwnerToStore(store, userSystem, userSystem1.getUserName()));
        }

        /**
         * check the addOwnerToStore() functionality in case of not initialized parameters.
         */
        @Test
        void addOwnerToStoreNullParams() {
            //set up for test
            setUpAddOwner();
            //check that the addition of the new owner was successful
            when(tradingSystemDao.getUserSystem(userSystem1.getUserName())).thenReturn(Optional.of(userSystem1));
            Assertions.assertTrue(tradingSystem.addOwnerToStore(store, userSystem, userSystem1.getUserName()));
            //check the store contains this user as owner
            Assertions.assertTrue(store.getOwners().contains(userSystem1));
            //check that the new owner contains the store
            Assertions.assertEquals(store,userSystem1.getOwnerStore(store.getStoreId()));
        }

        /**
         * check the addOwnerToStore() functionality in case of failure in addNewOwnedStore and addOwner
         */
        @Test
        void addOwnerToStoreWrongStoreOwner() {
            setUpAddOwnerWrongOwner();
            //try to add a new owner to store with a non owner of store
            when(tradingSystemDao.getUserSystem(userSystem1.getUserName())).thenReturn(Optional.of(userSystem1));
            Assertions.assertFalse(tradingSystem.addOwnerToStore(store, userSystem2, userSystem1.getUserName()));
            //check that the store didn't add the user as owner
            Assertions.assertFalse(store.getOwners().contains(userSystem1));

        }

        /**
         * check the addOwnerToStore() method when one of the parameters is null
         */
        @Test
        void addOwnerToStoreNullObject() {
            //try to add a null new owner
            Assertions.assertFalse(tradingSystem.addOwnerToStore(store, userSystem, null));
            //try to add new owner to a null store
            when(tradingSystemDao.getUserSystem(userSystem1.getUserName())).thenReturn(Optional.of(userSystem1));
            Assertions.assertFalse(tradingSystem.addOwnerToStore(null, userSystem, userSystem1.getUserName()));
            //old owner null
            Assertions.assertFalse(tradingSystem.addOwnerToStore(store, null, userSystem1.getUserName()));
        }

        /**
         * check the filterByRangePrice method, checks that the returned products are filtered by a given price
         */
        @Test
        void filterByRangePrice() {
            List<Product> products = setUpProductsForFilterTests();
            int max = products.size();
            for (int min = -1; min < 100; min++) {
                List<Product> productsActual = tradingSystem.filterByRangePrice(products, min, max);
                int finalMin = min;
                int finalMax = max;
                List<Product> productsExpected = products.stream()
                        .filter(product -> finalMin <= product.getCost() && product.getCost() <= finalMax)
                        .collect(Collectors.toList());
                max--;
                Assertions.assertEquals(productsExpected, productsActual);
            }
        }

        /**
         * check the filterByProductRank method, checks that the returned products are filtered by a given product rank
         */
        @Test
        void filterByProductRank() {
            List<Product> products = setUpProductsForFilterTests();
            for (int rank = -1; rank < 100; rank++) {
                List<Product> productsActual = tradingSystem.filterByProductRank(products, rank);
                int finalRank = rank;
                List<Product> productsExpected = products.stream()
                        .filter(product -> finalRank <= product.getRank())
                        .collect(Collectors.toList());
                Assertions.assertEquals(productsExpected, productsActual);
            }
        }

        /**
         * check the filterByStoreRank method, checks that the returned products are filtered by a given store rank
         */
        @Test
        void filterByStoreRank() {
            //initial
            List<Product> products = setUpProductsForFilterTests();
            List<Store> stores = setUpStoresForFilterTests(products);

            //mocks for this function
            tradingSystem = mock(TradingSystem.class);
            when(tradingSystem.filterByStoreRank(anyList(), anyInt())).thenCallRealMethod();
            when(tradingSystem.getStore(anyInt())).then(invocation ->{
                int storeId = invocation.getArgument(0);
                return stores.get(storeId);
            });

            // the tests
            for (int rank = -1; rank < 100; rank++) {
                List<Product> productsActual = tradingSystem.filterByStoreRank(products, rank);
                int finalRank = rank;
                List<Product> productsExpected = products.stream()
                        .filter(product -> {
                            int storeId = product.getStoreId();
                            Store store = stores.get(storeId);
                            return finalRank <= store.getRank();
                        })
                        .collect(Collectors.toList());
                Assertions.assertEquals(productsExpected, productsActual);
            }
        }

        /**
         * check the filterByStoreCategory method, checks that the returned products are filtered by a given store category
         */
        @Test
        void filterByStoreCategory() {
            List<Product> products = setUpProductsForFilterTests();
            for (int min = -1; min < 100; min++) {
                for (int categoryIndex = 0; categoryIndex < ProductCategory.values().length; categoryIndex++) {
                    ProductCategory category = ProductCategory.values()[categoryIndex];
                    List<Product> productsActual = tradingSystem.filterByStoreCategory(products, category);
                    int finalMin = min;
                    List<Product> productsExpected = products.stream()
                            .filter(product -> product.getCategory() == category)
                            .collect(Collectors.toList());
                    Assertions.assertEquals(productsExpected, productsActual);
                }
            }
        }

        /**
         * This test check if the purchaseShoppingCart method succeeds
         * when all the parameters are correct.
         */
        @Test
        void guestPurchaseShoppingCartPositive() {
            setUpPurchaseCartSuc();
            List<Receipt> receipts = tradingSystem.purchaseShoppingCartGuest(testShoppingCart,paymentDetails,billingAddress);
            //check that the return object is not null
            Assertions.assertNotNull(receipts);
            //check that the returned object contains the right receipt
            Assertions.assertTrue(receipts.contains(receipt));
        }

        /**
         * This test check if the purchaseShoppingCart method fails
         * when the cart is empty.
         */
        @Test
        void guestPurchaseShoppingCartEmptyCart() {
            setUpEmptyCart();
            //check that the system does not allow to buy an empty cart
            Assertions.assertNull(tradingSystem.purchaseShoppingCartGuest(testShoppingCart,paymentDetails,billingAddress));
        }

        /**
         * This test check if the purchaseShoppingCart method fails
         * when one of the insert parameters is null.
         */
        @Test
        void guestPurchaseShoppingCartNullObject() {
            setUpNotEmptyCart();
            //check that the system does not allow to buy a null cart
            Assertions.assertNull(tradingSystem.purchaseShoppingCartGuest(null,paymentDetails,billingAddress));
            //check that the system does not allow to use a null payment details
            Assertions.assertNull(tradingSystem.purchaseShoppingCartGuest(testShoppingCart,null,billingAddress));
            //check that the system does not allow to use a null billing address
            Assertions.assertNull(tradingSystem.purchaseShoppingCartGuest(null,paymentDetails,null));
        }

        /**
         * This test check if the purchaseShoppingCart method fails
         * when one of the products is not in stock.
         */
        @Test
        void guestPurchaseShoppingCartProductsNotInStock() {
            setUpProductNotInStock();
            //check that the system does not allow to buy an empty cart
            Assertions.assertThrows(NotInStockException.class, ()->
                    tradingSystem.purchaseShoppingCartGuest(testShoppingCart,paymentDetails,billingAddress));
        }

        /**
         * This test check if the purchaseShoppingCart method fails
         * when there is a problem with charge system and the product are not removed from cart.
         */
        @Test
        void guestPurchaseShoppingCartChargeFail() {
            setUpChargeFail();
            //check that the charge fails
            Assertions.assertThrows(ChargeException.class, ()->
                    tradingSystem.purchaseShoppingCartGuest(testShoppingCart,paymentDetails,billingAddress));
            //check that product is in the cart after charge failed
            Assertions.assertTrue(testShoppingCart.getShoppingBag(store).getProductListFromStore().containsKey(product));
        }

        /**
         * This test check if the purchaseShoppingCart method fails
         * when there is a problem with deliver system and the product are not removed from cart.
         */
        @Test
        void guestPurchaseShoppingCartDeliverFail() {
            setUpFailDelivery();
            //check that the deliver fails
            Assertions.assertThrows(DeliveryRequestException.class, ()->
                    tradingSystem.purchaseShoppingCartGuest(testShoppingCart,paymentDetails,billingAddress));
            //check that product is in the cart after deliver failed
            Assertions.assertTrue(testShoppingCart.getShoppingBag(store).getProductListFromStore().containsKey(product));
        }

        /**
         * This test check if the purchaseShoppingCart method succeeds when the parameters
         * are correct.
         */
        @Test
        void registeredPurchaseShoppingCartPositive() {
            setUpForRegisteredPurchaseSuc();
            when(userSystem2.getShoppingCart()).thenReturn(testShoppingCart);
            when(userSystem2.getUserName()).thenReturn("PurchaseRegisteredUser");
            List<Receipt> receipts = tradingSystem.purchaseShoppingCart(paymentDetails,billingAddress,userSystem2);
            //check that the returned object is not null
            Assertions.assertNotNull(receipts);
            //check that the returned object contains the right receipt
            Assertions.assertTrue(receipts.contains(receipt));
        }

        /**
         * This test check if the purchaseShoppingCart method fails
         * when the user is null
         */
        @Test
        void registeredPurchaseShoppingCartNullUser() {
            //check that the system does not allow for a null user to purchase
            Assertions.assertNull(tradingSystem.purchaseShoppingCart(paymentDetails,billingAddress,null));
        }

        /**
         * This test checks that the store's opening succeeds
         */
        @Test
        void openStoreSuccess() {
            setUpUsersForOpenStore();
            Store openStore = tradingSystem.openStore(userSystem,store.getStoreName(),"This store is a cloth store");
            //check the store was opened in the system
            Assertions.assertNotNull(openStore);
            //check that the name of the new store is correct
            Assertions.assertEquals("castro",openStore.getStoreName());
            //check that this user is the store's owner
            Assertions.assertTrue(openStore.getOwners().contains(userSystem));
           }


        /**
         * This test check if the openStore method fails when
         * one of the parameters is null.
         */
        @Test
        void openStoreNullObject(){
            //check that a store can't be opened with null user
            Assertions.assertNull(tradingSystem.openStore(null,"castro", "store didn't open"));
            //check that a store can't be opened with null store name
            Assertions.assertNull(tradingSystem.openStore(userSystem,null, "store didn't open"));
        }

        /**
         * This test check if the openStore method fails when store name is empty
         */
        @Test
        void openStoreBlankStoreName(){
            //check that a store can't have a blank name
            Assertions.assertNull(tradingSystem.openStore(userSystem,"", ""));
        }

        /**
         * This test check if the removeManager method succeeds when the parameters
         * are correct.
         */
        @Test
        void removeManagerSuccess(){
            setUpUsersForRemoveManagerSuc();
            //check that remove success
            Assertions.assertTrue(tradingSystem.removeManager(store, userSystem, userSystem1));
            //check that the removed user is not a manager in the store
            Assertions.assertThrows(NoManagerInStoreException.class,
                    () ->store.getManager(userSystem,userSystem1.getUserName()));
        }

        /**
         * This test check if the removeManager method fails
         * when trying to remove an user who is not a manager.
         */
        @Test
        void removeManagerWhoIsNotAManager(){
            setUpUsersNotManager();
            //can't remove from null store
            Assertions.assertFalse(tradingSystem.removeManager(store,userSystem,userSystem1));
            //store does not exists
            Assertions.assertFalse(tradingSystem.removeManager(store,userSystem2,userSystem));
            //1 users is not registered
            Assertions.assertFalse(tradingSystem.removeManager(store1,userSystem1,userSystem));
        }

        /**
         * This test check if the removeManager method fails
         * when inserting the wrong owner
         */
        @Test
        void removeManagerNotAnOwner(){
            setUpNotOwner();
            //can't remove manger with wrong owner
            Assertions.assertFalse(tradingSystem.removeManager(store,userSystem1,userSystem2));
            //check that the manager was not removed
            Assertions.assertEquals(userSystem2, store.getManager(userSystem,userSystem2.getUserName()));
        }

        /**
         * This test check if the removeManager method fails
         * when inserting the wrong store.
         */
        @Test
        void removeManagerWrongStore(){
            setUpWrongStore();
            //can't remove manger from a wrong store
            Assertions.assertFalse(tradingSystem.removeManager(store,userSystem1,userSystem2));
        }

        /**
         * This test check if the removeManager method fails when the parameters
         * are wrong.
         */
        @Test
        void removeManagerNullObject(){
            //can't remove from null store
            Assertions.assertFalse(tradingSystem.removeManager(null,userSystem,userSystem1));
            //can't remove with null owner
            Assertions.assertFalse(tradingSystem.removeManager(store,null,userSystem1));
            //can't remove a null manager from store
            Assertions.assertFalse(tradingSystem.removeManager(store,userSystem,null));
        }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // ********************************** Set Up Functions For Tests ********************************** //
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



        /**
         * set a user in logged in map
         */
        private void setUpLoginLoggedIn(){
            when(tradingSystemDao.isRegistered(any())).thenReturn(true);
            when(tradingSystemDao.getUserSystem("AlreadyLoggedInUser")).thenReturn(Optional.ofNullable(userSystem));
            when(tradingSystemDao.isAdmin("AlreadyLoggedInUser")).thenReturn(false);
            when(externalServiceManagement.isAuthenticatedUserPassword(any(),any())).thenReturn(true);
            when(userSystem.getUserName()).thenReturn("AlreadyLoggedInUser");
            doNothing().when(userSystem).login();
            Map<String, UUID> usersLogin = new HashMap<>();
            usersLogin.put(userSystem.getUserName(),UUID.randomUUID());
            tradingSystem.setUsersLogin(usersLogin);
        }
        /**
         * set up for login to fail
         */
        private void setUpLoginFail(){
            when(userSystem1.getUserName()).thenReturn("BadLoginUser");
            when(userSystem1.getPassword()).thenReturn("12345abc");
            when(tradingSystemDao.isRegistered(any())).thenReturn(false);
        }

        /**
         *
         */
        private void setUpLogin(){
            when(tradingSystemDao.isRegistered(any())).thenReturn(true);
            when(tradingSystemDao.getUserSystem("userTest")).thenReturn(Optional.ofNullable(userSystem));
            when(tradingSystemDao.isAdmin("userTest")).thenReturn(false);
            when(externalServiceManagement.isAuthenticatedUserPassword(any(),any())).thenReturn(true);
            when(userSystem.getUserName()).thenReturn("userTest");
            doNothing().when(userSystem).login();
        }

        private void setUpForLogout(){
            Map<String, UUID> usersLogin = new HashMap<>();
            usersLogin.put("usernameTest", UUID.randomUUID());
            tradingSystem.setUsersLogin(usersLogin);
            when(userSystem.getUserName()).thenReturn("usernameTest");
            when(userSystem.isLogin()).thenReturn(true);
        }

        /**
         * sets up store for openStore test
         */
        private void setUpUsersForOpenStore(){
            Set<UserSystem> owners = new HashSet<>();
            owners.add(userSystem1);
            when(store.getOwners()).thenReturn(owners);
            doNothing().when(tradingSystemDao).addStore(store);
            when(store.getStoreName()).thenReturn("castro");
            when(userSystem.addNewOwnedStore(any())).thenReturn(true);
        }

        /**
         * set admin in system for test
         */
        private void setUpGetAdmin(){
            when(tradingSystemDao.getAdministratorUser("admin")).thenReturn(Optional.ofNullable(admin));
        }

        /**
         * sets up store for removeManager test
         */
        private void setUpUsersForRemoveManagerSuc(){
            when(store.removeManager(userSystem, userSystem1)).thenReturn(true);
            when(store.getStoreId()).thenReturn(1);
            when(userSystem1.getUserName()).thenReturn("managerToRemove");
            when(userSystem.getUserName()).thenReturn("OwnerStore");
            when(store.getManager(userSystem,userSystem1.getUserName())).
                    thenThrow(NoManagerInStoreException.class);
        }

        private void registerAsSetupPass() {
            userToRegister = mock(UserSystem.class);
            when(userToRegister.getPassword()).thenReturn("12");
            doNothing().when(userToRegister).setPassword("pass");
            doNothing().when(userToRegister).setSalt("salt");
            when(userToRegister.getUserName()).thenReturn("usernameTest");
            when(userToRegister.getFirstName()).thenReturn("usersFirstName");
            when(userToRegister.getLastName()).thenReturn("usersLastName");
            when(userToRegister.getPassword()).thenReturn("IAmPassword");
            when(userToRegister.isValidUser()).thenReturn(true);
            doNothing().when(userToRegister).setImageUrl(any());
            PasswordSaltPair passwordSaltPair = new PasswordSaltPair("123345543gf","salt");
            when(externalServiceManagement.getEncryptedPasswordAndSalt(userToRegister.getPassword())).thenReturn(passwordSaltPair);
            when(passwordSaltPair.getHashedPassword()).thenReturn("123345543gf");
            when(passwordSaltPair.getSalt()).thenReturn("salt");
          //  doNothing().when(ImageUtil.saveImage(ImagePath.USER_IMAGE_DIC + userToRegister.getUserName(), userImage));
//            when(ImageUtil.saveImage(ImagePath.USER_IMAGE_DIC + userToRegister.getUserName(), userImage)).thenReturn("IAmPass");
        }
        /**
         * setup of successful pre registration
         */
        private void registerAsSetup(){
            //mockup
            userToRegister = mock(UserSystem.class);
            when(userToRegister.getPassword()).thenReturn("12");
            when(externalServiceManagement.getEncryptedPasswordAndSalt(userToRegister.getPassword()))
                    .thenReturn(new PasswordSaltPair("pass","salt"));
            doNothing().when(userToRegister).setPassword("pass");
            doNothing().when(userToRegister).setSalt("salt");
            when(userToRegister.getUserName()).thenReturn("usernameTest");
            when(userToRegister.getFirstName()).thenReturn("firstNameTest");
            when(userToRegister.getLastName()).thenReturn("lastNameTest");
            //setup
            //the following user details are necessary for the login tests
            tradingSystem.registerNewUser(userToRegister,null);
        }


        /**
         * add an admin to list for test
         */
        private void setUpForGetStoreAdmin(){
            Map<String, UUID> loginUser = new HashMap<>();
            loginUser.put("admin",UUID.randomUUID());
            tradingSystem.setUsersLogin(loginUser);
            when(tradingSystemDao.isAdmin("admin")).thenReturn(true);
            when(tradingSystemDao.getStore(1)).thenReturn(Optional.ofNullable(store));
        }

        /**
         * delete the added admin to the login list
         */
        private void tearDownGetStoreAdmin(){
            Map<String, UUID> loginUser = new HashMap<>();
            tradingSystem.setUsersLogin(loginUser);
        }

        /**
         * set up a user for test and aet up the right user to return
         */
        private void setUpGetUser(){
            when(tradingSystemDao.getUserSystem("userToReturn")).thenReturn(Optional.ofNullable(userSystem));
            Map<String, UUID> loginUsers = new HashMap<>();
            UUID uuid = UUID.randomUUID();
            when(userSystem.getUserName()).thenReturn("userToReturn");
            loginUsers.put(userSystem.getUserName(), uuid);
            tradingSystem.setUsersLogin(loginUsers);
        }

        /**
         * set up an admin for test and aet up the right user to return
         */
        private void setUpGetUserByAdmin(){
            when(tradingSystemDao.getUserSystem("userToReturn")).thenReturn(Optional.ofNullable(userSystem));
            Map<String, UUID> loginUsers = new HashMap<>();
            loginUsers.put("admin", UUID.randomUUID());
            tradingSystem.setUsersLogin(loginUsers);
            when(tradingSystemDao.isAdmin("admin")).thenReturn(true);
        }

        /**
         * setUp Products For Filter Tests
         */
        private List<Product> setUpProductsForFilterTests() {
            List<Product> products = new LinkedList<>();
            for (int counter = 0; counter < 10; counter++) {
                products.add(Product.builder()
                        .cost(counter)
                        .rank(counter)
                        .category(ProductCategory.values()[counter % ProductCategory.values().length])
                        .storeId(counter)
                        .build());
            }
            return products;
        }

        /**
         * set store in trading system
         */
        private void setUpGetStore(){
            when(store.getStoreId()).thenReturn(1);
            when(tradingSystemDao.getStore(store.getStoreId())).thenReturn(Optional.ofNullable(store));
        }

        /**
         * set up owner and store for test
         */
        private void setUpAddOwner(){
            when(store.addOwner(userSystem,userSystem1)).thenReturn(true);
            when(userSystem1.addNewOwnedStore(store)).thenReturn(true);
            when(store.getStoreId()).thenReturn(1);
            when(userSystem1.getUserName()).thenReturn("newOwnerStore");
            Set<UserSystem> owners = new HashSet<>();
            owners.add(userSystem1);
            when(store.getOwners()).thenReturn(owners);
            when(userSystem1.getOwnerStore(store.getStoreId())).thenReturn(store);
            when(store.addOwner(userSystem2,userSystem1)).thenReturn(false);
        }

        /**
         * set up owner and store
         */
        private void setUpAddOwnerWrongOwner(){
            when(store.addOwner(userSystem,userSystem1)).thenReturn(true);
            when(userSystem1.addNewOwnedStore(store)).thenReturn(true);
            when(store.getStoreId()).thenReturn(1);
            when(userSystem1.getUserName()).thenReturn("newOwnerStore");
            Set<UserSystem> owners = new HashSet<>();
            when(store.getOwners()).thenReturn(owners);
            when(store.addOwner(userSystem2,userSystem1)).thenReturn(false);
        }

        /**
         * setUp Stores For Filter Tests
         */
        private List<Store> setUpStoresForFilterTests(List<Product> products) {
            List<Store> stores = new LinkedList<>();
            for (int counter = 0; counter < products.size(); counter++) {
                stores.add(Store.builder()
                        .storeId(counter)
                        .rank(counter)
                        .build());
            }
            return stores;
        }

        /**
         * set up wrong store for removeManager
         */
        private void setUpWrongStore(){
            when(store.removeManager(userSystem, userSystem1)).thenReturn(false);
            when(userSystem.getUserName()).thenReturn("RightManager");
            when(userSystem1.getUserName()).thenReturn("RightOwner");
            when(store.getStoreId()).thenReturn(1);
        }

        /**
         * set up a wrong owner and right manager for removeManager
         */
        private void setUpNotOwner(){
            when(store.removeManager(userSystem1, userSystem2)).thenReturn(false);
            when(userSystem2.getUserName()).thenReturn("RightManager");
            when(userSystem1.getUserName()).thenReturn("wrongOwner");
            when(store.getStoreId()).thenReturn(1);
            when(store.getManager(userSystem,userSystem2.getUserName())).thenReturn(userSystem2);
        }

        /**
         * set up a right owner and wrong manager for removeManager
         */
        private void setUpUsersNotManager(){
            when(store.removeManager(userSystem, userSystem1)).thenReturn(false);
            when(userSystem1.getUserName()).thenReturn("WrongUserToRemove");
            when(store.getStoreId()).thenReturn(1);
        }

        /**
         * set up all needed object for test guestPurchaseShoppingCartDeliverFail
         */
        private void setUpFailDelivery(){
            doNothing().when(testShoppingCart).applyDiscountPolicies();
            when(product.getName()).thenReturn("Bamba");
            when(store.getStoreName()).thenReturn("Shufersal-Dil");
            when(testShoppingCart.getNumOfBagsInCart()).thenReturn(1);
            Map<Store, ShoppingBag> bagList = new HashMap<>();
            Map<Product, Integer> productsList = new HashMap<>();
            productsList.put(product,2);
            testShoppingBag.setProductListFromStore(productsList);
            bagList.put(store,testShoppingBag);
            testShoppingCart.setShoppingBagsList(bagList);
            when(testShoppingCart.getShoppingBag(store)).thenReturn(testShoppingBag);
            when(testShoppingBag.getProductListFromStore()).thenReturn(productsList);
            when(externalServiceManagement.charge(any(),any())).thenReturn(true);
            when(externalServiceManagement.cancelCharge(any(),any())).thenReturn(true);
            when(externalServiceManagement.deliver(any(),any())).
                    thenThrow(new DeliveryRequestException("The Delivery request rejected"));
        }

        /**
         * set up product not in stock
         */
        private void setUpProductNotInStock(){
            when(testShoppingCart.getNumOfBagsInCart()).thenReturn(1);
            Map<Store, ShoppingBag> bagList= new HashMap<>();
            when(testShoppingCart.getShoppingBagsList()).thenReturn(bagList);
            when(product.getName()).thenReturn("Bamba");
            when(store.getStoreName()).thenReturn("Shufersal-Dil");
            when(testShoppingCart.isAllBagsInStock()).thenThrow(NotInStockException.class);
        }

        /**
         * set the cart & store for guestPurchaseShoppingCartChargeFail test
         */
        private void setUpChargeFail(){
            doNothing().when(testShoppingCart).applyDiscountPolicies();
            when(product.getName()).thenReturn("Bamba");
            when(store.getStoreName()).thenReturn("Shufersal-Dil");
            when(testShoppingCart.getNumOfBagsInCart()).thenReturn(1);
            Map<Store, ShoppingBag> bagList = new HashMap<>();
            Map<Product, Integer> productsList = new HashMap<>();
            productsList.put(product,2);
            testShoppingBag.setProductListFromStore(productsList);
            bagList.put(store,testShoppingBag);
            testShoppingCart.setShoppingBagsList(bagList);
            when(testShoppingCart.getShoppingBag(store)).thenReturn(testShoppingBag);
            when(testShoppingBag.getProductListFromStore()).thenReturn(productsList);
            when(externalServiceManagement.charge(any(),any())).thenThrow(ChargeException.class);
        }
        /**
         * set up objects for guestPurchaseShoppingCartNullObject test
         */
        private void setUpNotEmptyCart(){
            when(testShoppingCart.getNumOfProductsInCart()).thenReturn(1);
            Map<Store, ShoppingBag> bagList= new HashMap<>();
            when(testShoppingCart.getShoppingBagsList()).thenReturn(bagList);
        }

        /**
         * set empty cart for test guestPurchaseShoppingCartEmptyCart
         */
        private void setUpEmptyCart(){
            when(testShoppingCart.getNumOfProductsInCart()).thenReturn(0);
        }

        /**
         * set up all needed objects for guestPurchaseShoppingCartPositive test
         */
        private void setUpPurchaseCartSuc(){
            when(product.getName()).thenReturn("Bamba");
            when(store.getStoreName()).thenReturn("Shufersal-Dil");
            when(testShoppingCart.getNumOfBagsInCart()).thenReturn(1);
            Map<Store, ShoppingBag> bagList = new HashMap<>();
            Map<Product, Integer> productsList = new HashMap<>();
            productsList.put(product,2);
            testShoppingBag.setProductListFromStore(productsList);
            bagList.put(store,testShoppingBag);
            testShoppingCart.setShoppingBagsList(bagList);
            when(testShoppingCart.getShoppingBag(store)).thenReturn(testShoppingBag);
            when(externalServiceManagement.charge(any(),any())).thenReturn(true);
            when(externalServiceManagement.deliver(any(),any())).thenReturn(true);
            doNothing().when(testShoppingCart).updateAllAmountsInStock();
            when(testShoppingCart.isAllBagsInStock()).thenReturn(true);
            doNothing().when(testShoppingCart).applyDiscountPolicies();
            ArrayList<Receipt> receipts = new ArrayList<>();
            receipts.add(receipt);
            when(testShoppingCart.createReceipts(any())).thenReturn(receipts);
        }

        /**
         * set up for registered user purchase cart
         */
        private void setUpForRegisteredPurchaseSuc(){
            setUpPurchaseCartSuc();
            when(userSystem2.getShoppingCart()).thenReturn(testShoppingCart);
            when(userSystem2.getUserName()).thenReturn("PurchaseRegisteredUser");
        }
    }

// ************************************* Trading System Test Integration ************************************* //

    /**
     * Integration tests for the TradingSystem
     */
    @Nested
    @ContextConfiguration(classes = {TradingSystemConfiguration.class})
    @SpringBootTest(args = {"admin", "admin"})
    public class TradingSystemTestIntegration {

        @Autowired
        private TradingSystem tradingSystem;
        private TradingSystemDao tradingSystemDao;
        private UserSystem userToRegister;
        private UserSystem userToOpenStore;
        private UserSystem testUser;
        private UserSystem userSystem2;
        private Store store;
        private Store storeToOpen;
        private Store storeToOpen1;
        private Product product;
        private UserSystem admin;
        private FactoryObjects factoryObjects;
        private Product testProduct;
        private Product testProduct1;
        private PaymentDetails paymentDetails;
        private PaymentDetails paymentDetails1;
        private BillingAddress billingAddress;
        private BillingAddress billingAddress1;
        private Store store1;
        private UserSystem newManager;
        private UserSystem storeOwner;
        private UserSystem wrongOwner;
        private ShoppingCart testShoppingCart;
        private ShoppingBag testShoppingBag1;
        private ShoppingBag testShoppingBag2;
        ExternalServiceManagement externalServiceManagement;

        @MockBean // for pass compilation
        private ModelMapper modelMapper;

        @BeforeEach
        void setUp() {
            String username = "usernameTest";
            String password = "passwordTest";
            String fName = "moti";
            String lName = "Banana";
            userToRegister = new UserSystem(username,fName,lName,password);
            store = new Store();
            product = new Product();
            store.setStoreId(1);
            product.setName("dollhouse");
            product.setCategory(ProductCategory.TOYS_HOBBIES);
            admin = UserSystem.builder()
                    .userName("admin")
                    .password("admin")
                    .build();
            userSystem2 = new UserSystem("coolAvi","Avi","Neelavi","1234erf");
            newManager = new UserSystem("NewManagerTest", "Manager", "Test", "MyPassword345");
            tradingSystemDao = new TradingSystemDaoImpl();
            externalServiceManagement = mock(ExternalServiceManagement.class);
            tradingSystem = new TradingSystem(externalServiceManagement, admin, tradingSystemDao);
            PasswordSaltPair psp = externalServiceManagement.getEncryptedPasswordAndSalt(admin.getPassword());
//            admin.setPassword(psp.getHashedPassword());
        ///    admin.setSalt(psp.getSalt());
            factoryObjects = new FactoryObjects();
        }

//        /**
//         * the following checks registration of valid user
//         */
//        @Test
//        void registerNewUser() {
//            //the following user details are necessary for the login tests
//            Assertions.assertTrue(tradingSystem.registerNewUser(userToRegister, null));
//        }

//        /**
//         * checks handling with failure of registration
//         * this test has to run after its respective positive test
//         */
//        @Test
//        void registerNewUserNegative() {
//            //registration with already registered user
//            registerAsSetup(); //first registration
//            Assertions.assertFalse(tradingSystem.registerNewUser(userToRegister,null));  //second registration
//        }

//        /**
//         * check if the login method works
//         */
//        @Test
//        void login() {
//            //check login of regular user
//            String password = "test login";
//            UserSystem user = UserSystem.builder()
//                    .userName("test login")
//                    .password(password)
//                    .build();
//            tradingSystem.registerNewUser(user);
//            tradingSystem.login(user, false, password);
//            //the following register should register usernameTest as username
//            // and passwordTest as password
//            registerAsSetup();  //register user test as setup for login
//            boolean ans = tradingSystem.login(userToRegister.getUserName(),"passwordTest");
//            Assertions.assertTrue(ans);
//            //check login of admin
//            //TODO
//        }

//        /**
//         * test handling with login failure
//         */
//        @Test
//        void loginNegative(){
//            String username = "username";
//            String password = "password";
//            String fName = "mati";
//            String lName = "Tut";
//            UserSystem user = new UserSystem(username,fName,lName,password);
//            user.setSalt("salt");
//            Pair<UUID, Boolean> loggedIn = tradingSystem.login(user.getUserName(),"password");
//            Assertions.assertTrue(loggedIn == null);
//        }

//        /**
//         * check the logout functionality of exists user in the system
//         */
//        @Test
//        void logout() {
//            //setup of login for the logout
//            String password = "Moti";
//            UserSystem user = UserSystem.builder()
//                    .userName("usernameTest")
//                    .password(password)
//                    .firstName("Banana")
//                    .lastName("passwordTest").build();
//            tradingSystem.registerNewUser(user);
//            tradingSystem.login(user, false, password);
//            Assertions.assertTrue(tradingSystem.logout(user));
//        }

        /**
         * check handling with logout failure
         */
        @Test
        void logoutNegative() {
            Assertions.assertFalse(tradingSystem.logout(userToRegister));
        }


//        /**
//         * check the getStoreByAdmin() functionality in case of exists admin in the system
//         */
//        @Test
//        void getStoreByAdminPositive() {
//            tradingSystem.insertStoreToStores(store);
//            Assertions.assertEquals(tradingSystem.getStoreByAdmin("admin", 1, uuid),store);
//        }
//
//        /**
//         * check the getStoreByAdmin() functionality in case of not exists admin in the system
//         */
//        @Test
//        void getStoreByAdminNegative() {
//            Assertions.assertThrows(NotAdministratorException.class, () -> {
//                tradingSystem.insertStoreToStores(store);
//                tradingSystem.getStoreByAdmin("userSystem", 1, uuid);
//            });
//        }

//        /**
//         * check the getStore() functionality in case of exists store in the system
//         */
//        @Test
//        void getStorePositive() {
//            tradingSystem.insertStoreToStores(store);
//            Assertions.assertEquals(tradingSystem.getStore(1),store);
//        }
//
//        /**
//         * check the getStore() functionality in case of not exists store in the system
//         */
//        @Test
//        void getStoreNegative() {
//            Assertions.assertThrows(StoreDontExistsException.class, () -> {
//                tradingSystem.insertStoreToStores(store);
//                tradingSystem.getStore(2);
//            });
//        }

//        /**
//         * check the getUser() functionality in case of exists user in the system
//         */
//        @Test
//        void getUserPositive() {
//            // register "userToRegister" to the users list in trading system
//            registerAsSetup();
//            Assertions.assertEquals(tradingSystem.getUser("usernameTest", uuid),userToRegister);
//        }
//
//        /**
//         * check the getUser() functionality in case of not exists user in the system
//         */
//        @Test
//        void getUserNegative() {
//            // register "userToRegister" to the users list in trading system
//            Assertions.assertThrows(UserDontExistInTheSystemException.class, () -> {
//                registerAsSetup();
//                tradingSystem.getUser("userToRegister", uuid);
//            });
//        }

//        /**
//         * check the getUserByAdmin() functionality in case of exists admin in the system
//         */
//        @Test
//        void getUserByAdminPositive() {
//            registerAsSetup();
//            Assertions.assertEquals(tradingSystem.getUserByAdmin("admin", "usernameTest", uuid),userToRegister);
//        }
//
//        /**
//         * check the getUserByAdmin() functionality in case of not exists admin in the system
//         */
//        @Test
//        void getUserByAdminNegative(){
//            Assertions.assertThrows(NotAdministratorException.class, () -> {
//                registerAsSetup();
//                tradingSystem.getUserByAdmin("userSystem", "usernameTest", uuid);
//            });
//        }

//        /**
//         * check the searchProductByName() functionality in case of exists product in store in the system
//         */
//        @Test
//        void searchProductByNamePositive() {
//            tradingSystem.insertStoreToStores(store);
//            HashSet<Product> products = new HashSet<Product>();
//            products.add(product);
//            HashSet<UserSystem> owners = new HashSet<UserSystem>();
//            owners.add(userToRegister);
//            store.setOwners(owners);
//            store.addNewProduct(userToRegister, product);
//            // converted both to arrays because one ahd ArrayList type and the other has Set<Product> type
//            Assertions.assertArrayEquals(tradingSystem.searchProductByName("dollhouse").toArray(),products.toArray());
//        }

//        /**
//         * check the searchProductByName() functionality in case of not exists product in store in the system
//         */
//        @Test
//        void searchProductByNameNegative() {
//            tradingSystem.insertStoreToStores(store);
//            HashSet<Product> products = new HashSet<Product>();
//            products.add(product);
//            HashSet<UserSystem> owners = new HashSet<UserSystem>();
//            owners.add(userToRegister);
//            store.setOwners(owners);
//            store.addNewProduct(userToRegister, product);
//            // The disjoint method returns true if its two arguments have no elements in common.
//            Assertions.assertTrue(Collections.disjoint(tradingSystem.searchProductByName("puppy"), products));
//        }

//        /**
//         * check the searchProductByCategory() functionality in case of exists product in store in the system
//         */
//        @Test
//        void searchProductByCategoryPositive() {
//            tradingSystem.insertStoreToStores(store);
//            HashSet<Product> products = new HashSet<Product>();
//            products.add(product);
//            HashSet<UserSystem> owners = new HashSet<UserSystem>();
//            owners.add(userToRegister);
//            store.setOwners(owners);
//            store.addNewProduct(userToRegister, product);
//            // converted both to arrays because one ahd ArrayList type and the other has Set<Product> type
//            Assertions.assertArrayEquals(tradingSystem.searchProductByCategory(ProductCategory.TOYS_HOBBIES).toArray(),products.toArray());
//        }

//        /**
//         * check the searchProductByCategory() functionality in case of not exists product in store in the system
//         */
//        @Test
//        void searchProductByCategoryNegative() {
//            tradingSystem.insertStoreToStores(store);
//            HashSet<Product> products = new HashSet<Product>();
//            products.add(product);
//            product.setCategory(ProductCategory.BOOKS_MOVIES_MUSIC);
//            HashSet<UserSystem> owners = new HashSet<UserSystem>();
//            owners.add(userToRegister);
//            store.setOwners(owners);
//            store.addNewProduct(userToRegister, product);
//            // The disjoint method returns true if its two arguments have no elements in common.
//            Assertions.assertTrue(Collections.disjoint(tradingSystem.searchProductByCategory(ProductCategory.TOYS_HOBBIES), products));
//        }

//        /**
//         * check the searchProductByKeyWords() functionality in case of exists product in store in the system
//         */
//        @Test
//        void searchProductByKeyWordsPositive() {
//            tradingSystem.insertStoreToStores(store);
//            HashSet<Product> products = new HashSet<Product>();
//            products.add(product);
//            HashSet<UserSystem> owners = new HashSet<UserSystem>();
//            owners.add(userToRegister);
//            store.setOwners(owners);
//            store.addNewProduct(userToRegister, product);
//            List<String> keyWords = new ArrayList<String>();
//            keyWords.add("doll");
//            keyWords.add("house");
//            // converted both to arrays because one ahd ArrayList type and the other has Set<Product> type
//            Assertions.assertArrayEquals(tradingSystem.searchProductByKeyWords(keyWords).toArray(),products.toArray());
//        }

//        /**
//         * check the searchProductByKeyWords() functionality in case of not exists product in store in the system
//         */
//        @Test
//        void searchProductByKeyWordsNegative() {
//            tradingSystem.insertStoreToStores(store);
//            HashSet<Product> products = new HashSet<Product>();
//            products.add(product);
//            HashSet<UserSystem> owners = new HashSet<UserSystem>();
//            owners.add(userToRegister);
//            store.setOwners(owners);
//            store.addNewProduct(userToRegister, product);
//            List<String> keyWords = new ArrayList<String>();
//            keyWords.add("elephant");
//            keyWords.add("pig");
//            // The disjoint method returns true if its two arguments have no elements in common.
//            Assertions.assertTrue(Collections.disjoint(tradingSystem.searchProductByKeyWords(keyWords), products));
//        }

        /**
         * check the filterByRangePrice method, checks that the returned products are filtered by a given price
         */
        @Test
        void filterByRangePrice() {
            List<Product> products = setUpProductsForFilterTests();
            int max = products.size();
            for (int min = -1; min < 100; min++) {
                List<Product> productsActual = tradingSystem.filterByRangePrice(products, min, max);
                int finalMin = min;
                int finalMax = max;
                List<Product> productsExpected = products.stream()
                        .filter(product -> finalMin <= product.getCost() && product.getCost() <= finalMax)
                        .collect(Collectors.toList());
                max--;
                Assertions.assertEquals(productsExpected, productsActual);
            }
        }

        /**
         * check the filterByStoreRank method, checks that the returned products are filtered by a given store rank
         */
        @Test
        @Disabled
        void filterByStoreRank() {
            //initial
            List<Product> products = setUpProductsForFilterTests();
            List<Store> stores = (setUpStoresForFilterTests(products));
            Set<Store> storesSet = new HashSet<>((stores));
            /*UserSystem admin = UserSystem.builder()
                    .userName("admin")
                    .password("admin")
                    .build();*/
           //tradingSystem = new TradingSystem(new ExternalServiceManagement(), storesSet, admin);

            // the tests
            for (int rank = -1; rank < 100; rank++) {
                List<Product> productsActual = tradingSystem.filterByStoreRank(products, rank);
                int finalRank = rank;
                List<Product> productsExpected = products.stream()
                        .filter(product -> {
                            int storeId = product.getStoreId();
                            Store store = stores.get(storeId);
                            return finalRank <= store.getRank();
                        })
                        .collect(Collectors.toList());
                Assertions.assertEquals(productsExpected, productsActual);
            }
        }

        /**
         * check the filterByStoreCategory method, checks that the returned products are filtered by a given store category
         */
        @Test
        void filterByStoreCategory() {
            List<Product> products = setUpProductsForFilterTests();
            for (int min = -1; min < 100; min++) {
                for (int categoryIndex = 0; categoryIndex < ProductCategory.values().length; categoryIndex++) {
                    ProductCategory category = ProductCategory.values()[categoryIndex];
                    List<Product> productsActual = tradingSystem.filterByStoreCategory(products, category);
                    int finalMin = min;
                    List<Product> productsExpected = products.stream()
                            .filter(product -> product.getCategory() == category)
                            .collect(Collectors.toList());
                    Assertions.assertEquals(productsExpected, productsActual);
                }
            }
        }

//        /**
//         * This test check if the openStore method succeeds when the parameters
//         * are correct.
//         */
//        @Test
//        void openStoreSuccess(){
//            setUpForOpenStoreSuc();
//            //before open store there is no stores in the system
//            Assertions.assertEquals(0, tradingSystem.getStoresList().size());
//            //check that the store opened
//            Assertions.assertTrue(tradingSystem.openStore(userToOpenStore, new PurchasePolicy(), new DiscountPolicy(), "castro"));
//            //after opening 1 store there needs to be a store in the system
//            Assertions.assertEquals(1, tradingSystem.getStoresList().size());
//        }

//        /**
//         * This test check if the openStore method fails when the parameters
//         * are wrong.
//         */
//        @Test
//        void openStoreFail(){
//            setUpForOpenStoreFail();
//            //before open store there is 1 (added in setUp) store in the system
//            Assertions.assertEquals(1, tradingSystem.getStoresList().size());
//            //fail to open an existing store
//            Assertions.assertFalse(tradingSystem.openStore(userToOpenStore, storeToOpen.getPurchasePolicy(), storeToOpen.getDiscountPolicy(), storeToOpen.getStoreName()));
//            //after fail opening there is still  1 store in the system
//            Assertions.assertEquals(1, tradingSystem.getStoresList().size());
//        }

        /**
         * This test check if the purchaseShoppingCart method succeeds
         * when all the parameters are correct.
         */
        @Test
        void guestPurchaseShoppingCartPositive() {
            setUpPurchaseCartSuc();
            List<Receipt> receipts = tradingSystem.purchaseShoppingCartGuest(testShoppingCart,paymentDetails,billingAddress);
            //check that the return object is not null
            Assertions.assertNotNull(receipts);
            //check that the product was bought
            Assertions.assertTrue(receipts.get(0).getProductsBought().containsKey(product));
            //check that the total price is correct
           Assertions.assertEquals(Double.parseDouble(formatter.format(product.getCost()*5)),receipts.get(0).getAmountToPay());

        }

        /**
         * This test check if the purchaseShoppingCart method fails
         * when the cart is empty.
         */
        @Test
        void guestPurchaseShoppingCartEmptyCart() {
            setUpEmptyCart();
            //check that the system does not allow to buy an empty cart
            Assertions.assertNull(tradingSystem.purchaseShoppingCartGuest(testShoppingCart,paymentDetails,billingAddress));
        }

        /**
         * This test check if the purchaseShoppingCart method fails
         * when one of the insert parameters is null.
         */
        @Test
        void guestPurchaseShoppingCartNullObject() {
            setUpNotEmptyCart();
            //check that the system does not allow to buy a null cart
            Assertions.assertNull(tradingSystem.purchaseShoppingCartGuest(null,paymentDetails,billingAddress));
            //check that the system does not allow to use a null payment details
            Assertions.assertNull(tradingSystem.purchaseShoppingCartGuest(testShoppingCart,null,billingAddress));
            //check that the system does not allow to use a null billing address
            Assertions.assertNull(tradingSystem.purchaseShoppingCartGuest(null,paymentDetails,null));
        }

        /**
         * This test check if the purchaseShoppingCart method fails
         * when one of the products is not in stock.
         */
        @Test
        void guestPurchaseShoppingCartProductsNotInStock() {
            setUpProductNotInStock();
            //check that the system does not allow to buy an empty cart
            Assertions.assertThrows(NotInStockException.class, ()->
                    tradingSystem.purchaseShoppingCartGuest(testShoppingCart,paymentDetails,billingAddress));
        }

        /**
         * This test check if the purchaseShoppingCart method fails
         * when there is a problem with charge system and the product are not removed from cart.
         */
        @Test
        void guestPurchaseShoppingCartChargeFail() {
            setUpChargeFail();
            //check that the charge fails
            Assertions.assertThrows(ChargeException.class, ()->
                    tradingSystem.purchaseShoppingCartGuest(testShoppingCart,paymentDetails,billingAddress));
            //check that product is in the cart after charge failed
            Assertions.assertTrue(testShoppingCart.getShoppingBag(store).getProductListFromStore().containsKey(product));
        }

        /**
         * This test check if the purchaseShoppingCart method fails
         * when there is a problem with deliver system and the product are not removed from cart.
         */
        @Test
        void guestPurchaseShoppingCartDeliverFail() {
            setUpFailDelivery();
            //check that the deliver fails
            Assertions.assertThrows(DeliveryRequestException.class, ()->
                    tradingSystem.purchaseShoppingCartGuest(testShoppingCart,paymentDetails,billingAddress));
            //check that product is in the cart after deliver failed
            Assertions.assertTrue(testShoppingCart.getShoppingBag(store).getProductListFromStore().containsKey(product));
        }

        /**
         * This test check if the purchaseShoppingCart method succeeds when the parameters
         * are correct.
         */
        @Test
        void registeredPurchaseShoppingCartPositive() {
            setUpForRegisteredPurchaseSuc();
            when(userSystem2.getShoppingCart()).thenReturn(testShoppingCart);
            when(userSystem2.getUserName()).thenReturn("PurchaseRegisteredUser");
            List<Receipt> receipts = tradingSystem.purchaseShoppingCart(paymentDetails,billingAddress,userSystem2);
            //check that the returned object is not null
            Assertions.assertNotNull(receipts);
            //check that the returned object contains the right receipt
           // Assertions.assertTrue(receipts.contains(receipt));
        }
//        /**
//         * This test check if the purchaseShoppingCart method succeeds when the parameters
//         * are correct. for guest and registered it's the same process.
//         */
//        @Test
//        @Disabled
//        void registeredPurchaseShoppingCartSuccess() {
//            setUpForPurchaseCart();
//            List <Receipt> receipts = tradingSystem.purchaseShoppingCart(paymentDetails, billingAddress, userToOpenStore);
//            //check that the receipts is not null
//            Assertions.assertNotNull(receipts);
//            Receipt receivedReceipt = receipts.iterator().next();
//            //check that store id match
//            Assertions.assertEquals(storeToOpen.getStoreId(), receivedReceipt.getStoreId());
//            //check that the user name, who bought the product, match
//            Assertions.assertEquals(userToOpenStore.getUserName(), receivedReceipt.getUserName());
//            //check that the cost match
//            Assertions.assertEquals(testProduct.getCost()*3, receivedReceipt.getAmountToPay());
//            //check that the product name match
//            Assertions.assertEquals(testProduct.getName(), receivedReceipt.getProductsBought().keySet().iterator().next().getName());
//        }
//
//        /**
//         * This test check if the purchaseShoppingCart method fails when the parameters
//         * are wrong. for guest and registered it's the same process.
//         */
//        @Test
//        void registeredPurchaseShoppingCartFail() {
//            setUpForPurchaseCartFail();
//            //can't send a null object
//            Assertions.assertNull(tradingSystem.purchaseShoppingCart(null, billingAddress, userToOpenStore));
//            //product amount in store is 45, try to but 47
//            Assertions.assertNull(tradingSystem.purchaseShoppingCart(paymentDetails, billingAddress, userToOpenStore));
//            //card number is wrong
//            Assertions.assertNull(tradingSystem.purchaseShoppingCart(paymentDetails1, billingAddress, userToOpenStore));
//            //wrong zip code
//            Assertions.assertNull(tradingSystem.purchaseShoppingCart(paymentDetails,billingAddress1,testUser));
//        }

        /**
         * This test check if the addManager method succeeds when the parameters
         * are correct.
         */
        @Test
        void addManagerSuccess(){
            setUpAddManagerAndOwner();
            //check that the new manager added to store
            Assertions.assertNotNull(tradingSystem.addMangerToStore(store1, storeOwner, newManager.getUserName()));
            //check that the number of managers of the store is 1
            Assertions.assertEquals(1, store1.getManagers().size());
            //check that right manager was added
            Assertions.assertEquals(newManager, store1.getManager(storeOwner, newManager.getUserName()));
        }

        private void setUpAddManagerAndOwner(){
            storeOwner = new UserSystem("OwnerTest","IAmOwner","Test","OwnerPassword");
            tradingSystem.registerNewUser(storeOwner,null);
            tradingSystem.registerNewUser(newManager,null);
            store1 = tradingSystem.openStore(storeOwner,"LeeOffice", "We Print Stuff");
        }

        /**
         * This test check if the addManager method fails when the parameters
         * are wrong.
         */
        @Test
        void addManagerNotAnOwner(){
            setUpAddManagerAndOwnerFail();
            //not an owner of the store, can't appoint a manager
            Assertions.assertNull(tradingSystem.addMangerToStore(store1,storeOwner,newManager.getUserName()));
            //check that the number of managers of the store is still 0
            Assertions.assertEquals(0, store1.getManagers().size());
        }

        private void setUpAddManagerAndOwnerFail(){
            storeOwner = new UserSystem("OwnerTest","IAmOwner","Test","OwnerPassword");
            tradingSystem.registerNewUser(storeOwner,null);
            tradingSystem.registerNewUser(newManager,null);
            tradingSystem.registerNewUser(userSystem2, null);
            store1 = tradingSystem.openStore(userSystem2,"LeeOffice", "We Print Stuff");
        }

        /**
         * This test check if the addManager method fails when the parameters
         * are wrong.
         */
        @Test
        void addManagerObjectNull(){
            setUpAddManagerAndOwner();
            //can't appoint a null manager
            Assertions.assertNull(tradingSystem.addMangerToStore(store1,storeOwner,null));
            //can't appoint a manager with null owner
            Assertions.assertNull(tradingSystem.addMangerToStore(store1,null,newManager.getUserName()));
            //can't appoint a manager with null store
            Assertions.assertNull(tradingSystem.addMangerToStore(null,storeOwner,newManager.getUserName()));
            //check that the number of managers of the store is still 0
            Assertions.assertEquals(0, store1.getManagers().size());
        }

        /**
         * This test check if the addManager method fails when the parameters
         * are wrong.
         */
        @Test
        void addManagerEmptyUserNameManager(){
            setUpAddManagerAndOwner();
            //can't appoint a null manager
            Assertions.assertNull(tradingSystem.addMangerToStore(store1,storeOwner,""));
            //check that the number of managers of the store is still 0
            Assertions.assertEquals(0, store1.getManagers().size());
        }

        /**
         * This test check if the addManager method fails when the parameters
         * are wrong.
         */
        @Test
        void addManagerAppointMyself(){
            setUpAddManagerAndOwner();
            //can't appoint a null manager
            Assertions.assertNull(tradingSystem.addMangerToStore(store1,storeOwner,storeOwner.getUserName()));
            //check that the number of managers of the store is still 0
            Assertions.assertEquals(0, store1.getManagers().size());
        }

        /**
         * This test check if the addOwner method succeeds when the parameters
         * are correct.
         */
        @Test
        void addOwnerSuccess(){
            setUpAddManagerAndOwner();
            //check that the new owner added to store
            Assertions.assertTrue(tradingSystem.addOwnerToStore(store1, storeOwner, newManager.getUserName()));
            //check that the number of owners of the store is 2
            Assertions.assertEquals(2, store1.getOwners().size());
            //check that the new owner is in store
            Assertions.assertTrue(store1.getOwners().contains(newManager));
        }

        /**
         * This test check if the addOwner method fails when the parameters
         * are wrong.
         */
        @Test
        void addOwnerNotAnOwner(){
            setUpAddManagerAndOwnerFail();
            //not an owner of the store, can't appoint an owner
            Assertions.assertFalse(tradingSystem.addOwnerToStore(store1,storeOwner,newManager.getUserName()));
            //check that the number of owners of the store is still 1
            Assertions.assertEquals(1, store1.getOwners().size());
        }

        /**
         * This test check if the addManager method fails when the parameters
         * are wrong.
         */
        @Test
        void addOwnerObjectNull(){
            setUpAddManagerAndOwner();
            //can't appoint a null owner
            Assertions.assertFalse(tradingSystem.addOwnerToStore(store1,storeOwner,null));
            //can't appoint an owner with null owner
            Assertions.assertFalse(tradingSystem.addOwnerToStore(store1,null,newManager.getUserName()));
            //can't appoint an owner with null store
            Assertions.assertFalse(tradingSystem.addOwnerToStore(null,storeOwner,newManager.getUserName()));
            //check that the number of owners of the store is still 1
            Assertions.assertEquals(1, store1.getOwners().size());
        }

        /**
         * This test check if the addManager method fails when the parameters
         * are wrong.
         */
        @Test
        void addOwnerEmptyUserNameManager(){
            setUpAddManagerAndOwner();
            //can't appoint a null manager
            Assertions.assertFalse(tradingSystem.addOwnerToStore(store1,storeOwner,""));
            //check that the number of owners of the store is still 1
            Assertions.assertEquals(1, store1.getOwners().size());
        }

        /**
         * This test check if the addManager method fails when the parameters
         * are wrong.
         */
        @Test
        void addOwnerAppointMyself(){
            setUpAddManagerAndOwner();
            //can't appoint a null manager
            Assertions.assertFalse(tradingSystem.addOwnerToStore(store1,storeOwner,storeOwner.getUserName()));
            //check that the number of owners of the store is still 1
            Assertions.assertEquals(1, store1.getOwners().size());
            //check that the new owner is in store
            Assertions.assertTrue(store1.getOwners().contains(storeOwner));
        }

        /**
         * This test check if the removeManager method succeeds when the parameters
         * are correct.
         */
        @Test
        void removeManagerSuccess(){
            setUpUsersForRemoveManagerSuc();
            //before remove there is 1 manager in store
            Assertions.assertEquals(1, store1.getManagers().size());
            //check that the newManager is really a store manager
            Assertions.assertEquals(newManager ,store1.getManager(storeOwner,newManager.getUserName()));
            //check that remove success
            Assertions.assertTrue(tradingSystem.removeManager(store1, storeOwner, newManager));
            //after remove there are 0 managers in store
            Assertions.assertEquals(0, store1.getManagers().size());
            //check that the newManager is no longer a manager
            Assertions.assertFalse(store1.getManagers().contains(newManager));

        }

        private void setUpUsersForRemoveManagerSuc(){
            storeOwner = new UserSystem("OwnerTest","IAmOwner","Test","OwnerPassword");
            tradingSystem.registerNewUser(storeOwner,null);
            tradingSystem.registerNewUser(newManager,null);
            store1 = tradingSystem.openStore(storeOwner,"LeeOffice", "We Print Stuff");
            tradingSystem.addMangerToStore(store1,storeOwner,newManager.getUserName());
        }

//        /**
//         * This test check if the removeManager method fails when the parameters
//         * are wrong.
//         */
//        @Test
//        void removeManagerFail(){
//            setUpUsersForRemoveManager();
//            //can't remove from null store
//            Assertions.assertFalse(tradingSystem.removeManager(null,storeOwner,newManager));
//            //store does not exists
//            Assertions.assertFalse(tradingSystem.removeManager(store,storeOwner,newManager));
//            //1 users is not registered
//            Assertions.assertFalse(tradingSystem.removeManager(store1,wrongOwner,newManager));
//        }



        // ************************************ Set Up For Tests ************************************ //

        /**
         * set up of successful pre registration
         */
        private void registerAsSetup(){
           // tradingSystem.registerNewUser(userToRegister);
        }

        /**
         * setUp Products For Filter Tests
         */
        private List<Product> setUpProductsForFilterTests() {
            List<Product> products = new LinkedList<>();
            for (int counter = 0; counter < 10; counter++) {
                products.add(Product.builder()
                        .cost(counter)
                        .rank(counter)
                        .category(ProductCategory.values()[counter % ProductCategory.values().length])
                        .storeId(counter)
                        .build());
            }
            return products;
        }

        /**
         * setUp Stores For Filter Tests
         */
        private List<Store> setUpStoresForFilterTests(List<Product> products) {
            List<Store> stores = new LinkedList<>();
            for (int counter = 0; counter < products.size(); counter++) {
                stores.add(Store.builder()
                        .storeId(counter)
                        .rank(counter)
                        .build());
            }
            return stores;
        }

        /**
         * setUp for openStore
         */
        private void setUpForOpenStoreSuc(){
            userToOpenStore = getUserSystemBuild();
            //Set<UserSystem> users = new HashSet<>();
            //users.add(userToOpenStore);
            //tradingSystem.setUsersList(users);
           // tradingSystem.registerNewUser(userToOpenStore);
        }

        /**
         * setUp for openStore
         */
        private void setUpForOpenStoreFail(){
            userToOpenStore = getUserSystemBuild();
            Set<UserSystem> users = new HashSet<>();
            users.add(userToOpenStore);
         //   tradingSystem.setUsersList(users);
            storeToOpen = new Store(userToOpenStore, "castro");
          //  tradingSystem.insertStoreToStores(storeToOpen);
        }

        /**
         * setUp for purchaseCart
         */
        private void setUpForPurchaseCart(){
            userToOpenStore = getUserSystemBuild();
            storeToOpen = Store.builder()
                            .storeId(storeToOpen.getStoreId())
                            .storeName("MovieStore").build();
            //tradingSystem.insertStoreToStores(storeToOpen);
            testProduct = Product.builder()
                            .name("Harry-Potter")
                            .category(ProductCategory.BOOKS_MOVIES_MUSIC)
                            .amount(45)
                            .cost(12.9)
                            .storeId(storeToOpen.getStoreId())
                            .build();
            userToOpenStore.saveProductInShoppingBag(storeToOpen,testProduct,3);
            paymentDetails = new PaymentDetails("123456789", 237, "333333339");
            billingAddress = new BillingAddress("Israel Israeli", "Ben-Gurion 1", "Beer Sheva", "Israel","1234567");
        }

        /**
         * setUp for purchaseCart
         */
        private void setUpForPurchaseCartFail(){
            userToOpenStore = getUserSystemBuild();
            testUser = getUser();
            storeToOpen = Store.builder()
                    .storeId(storeToOpen.getStoreId())
                    .storeName("MovieStore").build();
            storeToOpen1 = Store.builder()
                    .storeId(storeToOpen1.getStoreId()+1)
                    .storeName("MovieStoreVIP").build();

          //  tradingSystem.insertStoreToStores(storeToOpen);
          //  tradingSystem.insertStoreToStores(storeToOpen1);
            testProduct = Product.builder()
                    .name("Harry-Potter")
                    .category(ProductCategory.BOOKS_MOVIES_MUSIC)
                    .amount(45)
                    .cost(12.9)
                    .storeId(storeToOpen.getStoreId())
                    .build();
            testProduct1 = Product.builder()
                    .name("Hunger Games")
                    .category(ProductCategory.BOOKS_MOVIES_MUSIC)
                    .amount(45)
                    .cost(12.9)
                    .storeId(storeToOpen1.getStoreId())
                    .build();
            userToOpenStore.saveProductInShoppingBag(storeToOpen,testProduct,47);
            testUser.saveProductInShoppingBag(storeToOpen1,testProduct1,5);
            paymentDetails = new PaymentDetails("123456789", 237, "333333339");
            paymentDetails1 = new PaymentDetails( "12345", 237, "333333339");
            billingAddress = new BillingAddress("Israel Israeli", "Ben-Gurion 1", "Beer Sheva", "Israel","1234567");
            billingAddress1 = new BillingAddress("Ragnar Lodbrok", "Main Tent", "Kattegat", "Sweden","1");
        }

        /**
         * makes a user for tests
         */
        private UserSystem getUserSystemBuild(){
            return UserSystem.builder()
                    .userName("coolIsrael")
                    .password("Isra123")
                    .firstName("Israel")
                    .lastName("Israeli").build();
        }

//        /**
//         * set owner, user to be manager and store
//         */
//        private void setUpAddManagerAndOwner(){
//            storeOwner = getUserSystemBuild();
//            wrongOwner = getWrongOwner();
//            newManager = getUser();
//            Set<UserSystem> usersList = new HashSet<>();
//            usersList.add(storeOwner);
//            usersList.add(newManager);
//            tradingSystem.setUsersList(usersList);
//            store1 = new Store(storeOwner, "MovieStore");
//            tradingSystem.insertStoreToStores(store1);
//        }

        private UserSystem getWrongOwner(){
            return UserSystem.builder()
                    .userName("Bjorn_Ironside")
                    .password("IronSide12")
                    .firstName("Bjorn")
                    .lastName("Lodbrok").build();
        }

        private UserSystem getUser(){
            return UserSystem.builder()
                    .userName("KingRagnar")
                    .password("Odin12")
                    .firstName("Ragnar")
                    .lastName("Lodbrok").build();
        }

//        /**
//         * sets up store for removeManager test
//         */
//        private void setUpUsersForRemoveManager(){
//            storeOwner = getUserSystemBuild();
//            newManager = getUser();
//            wrongOwner = getWrongOwner();
//            //tradingSystem.insertStoreToStores(store1);
//            Set<UserSystem> users = new HashSet<>();
//            users.add(storeOwner);
//            users.add(newManager);
//            tradingSystem.setUsersList(users);
//           // tradingSystem.openStore(storeOwner,new PurchasePolicy(), new DiscountPolicy(), "castro");
//            //tradingSystem.openStore(wrongOwner,new PurchasePolicy(), new DiscountPolicy(), "zara");
//            //store1 = tradingSystem.getStore(0);
//            store = Store.builder()
//                    .purchasePolicy(new PurchasePolicy())
//                    .discountPolicy(new DiscountPolicy())
//                    .storeName("MovieStore")
//                    .storeId(store1.getStoreId()+1).build();
//        }
//
//        /**
//         * sets up store for removeManager test
//         */
//        private void setUpUsersForRemoveManagerSuc(){
//            storeOwner = getUser();
//            newManager = getWrongOwner();
//            //tradingSystem.insertStoreToStores(store1);
//            Set<UserSystem> users = new HashSet<>();
//            users.add(storeOwner);
//            users.add(newManager);
//            tradingSystem.setUsersList(users);
//            tradingSystem.openStore(storeOwner,new PurchasePolicy(), new DiscountPolicy(), "castro");
//            store1 = storeOwner.getOwnedStores().iterator().next();
//        }
        /**
         * set up all needed object for test guestPurchaseShoppingCartDeliverFail
         */
        private void setUpFailDelivery(){
            doNothing().when(testShoppingCart).applyDiscountPolicies();
            when(product.getName()).thenReturn("Bamba");
            when(store.getStoreName()).thenReturn("Shufersal-Dil");
            when(testShoppingCart.getNumOfBagsInCart()).thenReturn(1);
            Map<Store, ShoppingBag> bagList = new HashMap<>();
            Map<Product, Integer> productsList = new HashMap<>();
            productsList.put(product,2);
            testShoppingBag1.setProductListFromStore(productsList);
            bagList.put(store,testShoppingBag1);
            testShoppingCart.setShoppingBagsList(bagList);
            when(testShoppingCart.getShoppingBag(store)).thenReturn(testShoppingBag1);
            when(testShoppingBag1.getProductListFromStore()).thenReturn(productsList);
            when(externalServiceManagement.charge(any(),any())).thenReturn(true);
            when(externalServiceManagement.cancelCharge(any(),any())).thenReturn(true);
            when(externalServiceManagement.deliver(any(),any())).
                    thenThrow(new DeliveryRequestException("The Delivery request rejected"));
        }

        /**
         * set up product not in stock
         */
        private void setUpProductNotInStock(){
            when(testShoppingCart.getNumOfBagsInCart()).thenReturn(1);
            Map<Store, ShoppingBag> bagList= new HashMap<>();
            when(testShoppingCart.getShoppingBagsList()).thenReturn(bagList);
            when(product.getName()).thenReturn("Bamba");
            when(store.getStoreName()).thenReturn("Shufersal-Dil");
            when(testShoppingCart.isAllBagsInStock()).thenThrow(NotInStockException.class);
        }

        /**
         * set the cart & store for guestPurchaseShoppingCartChargeFail test
         */
        private void setUpChargeFail(){
            doNothing().when(testShoppingCart).applyDiscountPolicies();
            when(product.getName()).thenReturn("Bamba");
            when(store.getStoreName()).thenReturn("Shufersal-Dil");
            when(testShoppingCart.getNumOfBagsInCart()).thenReturn(1);
            Map<Store, ShoppingBag> bagList = new HashMap<>();
            Map<Product, Integer> productsList = new HashMap<>();
            productsList.put(product,2);
            testShoppingBag1.setProductListFromStore(productsList);
            bagList.put(store,testShoppingBag1);
            testShoppingCart.setShoppingBagsList(bagList);
            when(testShoppingCart.getShoppingBag(store)).thenReturn(testShoppingBag1);
            when(testShoppingBag1.getProductListFromStore()).thenReturn(productsList);
            when(externalServiceManagement.charge(any(),any())).thenThrow(ChargeException.class);
        }
        /**
         * set up objects for guestPurchaseShoppingCartNullObject test
         */
        private void setUpNotEmptyCart(){
            when(testShoppingCart.getNumOfProductsInCart()).thenReturn(1);
            Map<Store, ShoppingBag> bagList= new HashMap<>();
            when(testShoppingCart.getShoppingBagsList()).thenReturn(bagList);
        }

        /**
         * set empty cart for test guestPurchaseShoppingCartEmptyCart
         */
        private void setUpEmptyCart(){
            when(testShoppingCart.getNumOfProductsInCart()).thenReturn(0);
        }

        /**
         * set up all needed objects for guestPurchaseShoppingCartPositive test
         */
        private void setUpPurchaseCartSuc(){
            testShoppingCart = new ShoppingCart();
            store = new Store(userSystem2, "Shufersal-Dil");
            product = new Product("Bamba", ProductCategory.HEALTH, 10000, 4.99, store.getStoreId());
            store.addNewProduct(userSystem2,product);
            testShoppingBag1 = new ShoppingBag(store);
            testShoppingBag1.addProductToBag(product,5);
            testShoppingCart.addBagToCart(store,testShoppingBag1);
            when(externalServiceManagement.charge(any(),any())).thenReturn(true);
            when(externalServiceManagement.deliver(any(),any())).thenReturn(true);
            paymentDetails = paymentDetails = new PaymentDetails("123456789", 237, "333333339");
            billingAddress = new BillingAddress("Israel Israeli", "Ben-Gurion 1", "Beer Sheva", "Israel","1234567");
        }

        /**
         * set up for registered user purchase cart
         */
        private void setUpForRegisteredPurchaseSuc(){
            setUpPurchaseCartSuc();
            when(userSystem2.getShoppingCart()).thenReturn(testShoppingCart);
            when(userSystem2.getUserName()).thenReturn("PurchaseRegisteredUser");
        }
    }
}

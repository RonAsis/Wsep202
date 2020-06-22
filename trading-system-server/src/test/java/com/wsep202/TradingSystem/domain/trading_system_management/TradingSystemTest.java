package com.wsep202.TradingSystem.domain.trading_system_management;


//import Externals.PasswordSaltPair;
import com.wsep202.TradingSystem.config.TradingSystemConfiguration;
import com.wsep202.TradingSystem.config.httpSecurity.HttpSecurityConfig;
import com.wsep202.TradingSystem.domain.exception.*;
import com.wsep202.TradingSystem.domain.factory.FactoryObjects;
import com.wsep202.TradingSystem.domain.trading_system_management.notification.Publisher;
import com.wsep202.TradingSystem.domain.trading_system_management.notification.Subject;
import com.wsep202.TradingSystem.domain.trading_system_management.purchase.BillingAddress;
import com.wsep202.TradingSystem.domain.trading_system_management.purchase.PaymentDetails;
import javafx.util.Pair;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

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
        private TradingSystemFacade tradingSystemFacade;
        private UserSystem userSystem;
        private UserSystem userSystem1;
        private UserSystem userSystem2;
        private UserSystem userToRegister;
        private FactoryObjects factoryObjects;
        private UserSystem admin;
        private UserSystem admin1;
        private Store store;
        private Store store1;
        private Product product;
        private PaymentDetails paymentDetails;
        private BillingAddress billingAddress;
        private ShoppingCart testShoppingCart;
        private ShoppingBag testShoppingBag;
        private Product testProduct;
        private Receipt receipt;
        private PasswordEncoder passwordEncoder;
        private UUID uuid;

        @BeforeEach
        void setUp() {
            externalServiceManagement = mock(ExternalServiceManagement.class);
            admin = UserSystem.builder()
                    .userName("admin")
                    .password("admin")
                    .build();
            admin1 = mock(UserSystem.class);
            passwordEncoder = mock(PasswordEncoder.class);
            tradingSystemDao = mock(TradingSystemDao.class);
            tradingSystemFacade = mock(TradingSystemFacade.class);
            when(externalServiceManagement.getEncryptedPasswordAndSalt(admin.getPassword())).thenReturn(new PasswordSaltPair("hash","admin"));
            tradingSystem = new TradingSystem(externalServiceManagement, admin, tradingSystemDao, passwordEncoder);
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

        /**
         * UC 2.2
         * the following checks registration of valid user
         */
        @Test
        void registerNewUserPositive() {
            //mockup
            registerAsSetupPass();
            //setup
            //the following user details are necessary for the login tests
            //success: registration done. valid user details
            Assertions.assertTrue(tradingSystem.registerNewUser(userToRegister,null));
        }

        /**
         * UC 2.2
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
         * UC 2.2
         * checks handling with failure of null
         */
        @Test
        void registerNewUserNullUser() {
            //fail: this for null object
            Assertions.assertFalse(tradingSystem.registerNewUser(null,null));
        }


        /**
         * UC 2.3
         * This test check if the login method succeeds when the parameters
         * are correct.
         */
        @Test
        void loginPositive(){
            setUpLogin();
            //mockup
            //doNothing().when(userSystem).login();
            Pair<UUID, Boolean> ans = tradingSystem.login(userSystem.getUserName(),"passwordTest");
            Assertions.assertNotNull(ans.getKey());
        }

        /**
         * UC 2.3
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
         * UC 2.3
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
         * UC 3.1
         * check the logout functionality of exists user in the system
         */
        @Test
        void logoutPositive() {
            setUpForLogout();
//            //before remove there is 1 logged-in user
//            Assertions.assertEquals(1, tradingSystem.usersLoggedInSystem());
            //check that the right user is logged-in
            Assertions.assertTrue(tradingSystem.isLoggein("usernameTest"));
            //check the ans from method is true
            Assertions.assertTrue(tradingSystem.logout(userSystem));
            //after remove there are 0 logged-in user
            Assertions.assertEquals(0, tradingSystem.usersLoggedInSystem());
//            //check that the user is not in logged-in list
//            Assertions.assertFalse(tradingSystem.isLoggein("usernameTest"));
        }


        /**
         * UC 3.1
         * check handling with logout failure
         */
        @Test
        void logoutUserNotLoggedIn() {
            //mockup
            when(userSystem.getUserName()).thenReturn("IAmUser");
            when(tradingSystemDao.isLogin(userSystem.getUserName())).thenReturn(false);
            Assertions.assertFalse(tradingSystem.logout(userSystem));
        }

        /**
         * UC 3.1
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
                    ("admin1", 1, uuid) ,store);
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
                tradingSystem.getStoreByAdmin("userSystem", 1, uuid);
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
                tradingSystem.getStoreByAdmin("admin1", 1, UUID.randomUUID());
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
            Assertions.assertEquals(tradingSystem.getUser(userSystem.getUserName(), uuid),userSystem);
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
                    ("admin", "userToReturn", uuid),userSystem);
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
                        "admin", "userToRegister", uuid);
            });
            tearDownGetStoreAdmin();
        }


        /**
         * UC 4.5
         * check the addMangerToStore() functionality in case of success in
         * addNewManageStore and addManager
         */
        @Test
        void addMangerToStorePositive() {
            // userSystem <==> ownerUser
            // userSystem1 <==> newManagerUser
            when(tradingSystemDao.getUserSystem(userSystem1.getUserName())).thenReturn(Optional.ofNullable(userSystem1));
            when(store.appointAdditionManager(userSystem,userSystem1)).thenReturn(new MangerStore(userSystem1));
            when(userSystem1.addNewManageStore(store)).thenReturn(true);
            Assertions.assertNotNull(tradingSystem.addMangerToStore(store, userSystem, userSystem1.getUserName()));
        }

        /**
         * UC 4.5
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
         * UC 4.5
         * check the addMangerToStore() functionality in case of failure in
         * addNewManageStore and addManager
         */
        @Test
        void addMangerToStoreNegative() {
            // userSystem <==> ownerUser
            // userSystem1 <==> newManagerUser
            when(store.appointAdditionManager(userSystem,userSystem1)).thenReturn(any());
            when(userSystem1.addNewManageStore(store)).thenReturn(false);
            Assertions.assertNull(tradingSystem.addMangerToStore(store, userSystem, userSystem1.getUserName()));
        }

        /**
         * UC 4.3
         * check the addOwnerToStore() functionality in case of success in
         * addNewOwnedStore and addOwner, the system needs to make the second user an owner Immediate.
         */
        @Test
        void addOwnerToStoreOneOwner() {
            // userSystem <==> ownerUser
            // userSystem1 <==> newManagerUser
            setUpForAddOwnerOne();
            Assertions.assertTrue(tradingSystem.addOwnerToStore(store, userSystem, userSystem1.getUserName()));
        }

        private void setUpForAddOwnerOne(){
            Set<UserSystem> userAgreement = new HashSet<>();
            userAgreement.add(userSystem);
            when(store.addOwner(userSystem,userSystem1)).thenReturn(userAgreement);
            when(userSystem1.addNewOwnedStore(store)).thenReturn(true);
            when(userSystem1.addNewOwnedStore(store)).thenReturn(true);
            when(userSystem1.getUserName()).thenReturn("thatMyName");
            when(tradingSystemDao.getUserSystem(userSystem1.getUserName())).thenReturn(Optional.of(userSystem1));
        }
//        /**
//         * UC 4.3
//         * check the addOwnerToStore() functionality in case of success in
//         * addNewOwnedStore and addOwner, the system not going to add the 3 user owner Immediate.
//         */
//        @Test
//        void addOwnerToStoreTwoOwners() {
//            // userSystem <==> ownerUser
//            // userSystem1 <==> newManagerUser
//            when(store.addOwner(userSystem,userSystem1)).thenReturn(notNull());
//            when(userSystem1.addNewOwnedStore(store)).thenReturn(true);
//            when(userSystem1.addNewOwnedStore(store)).thenReturn(true);
//            when(tradingSystemDao.getUserSystem(userSystem1.getUserName())).thenReturn(Optional.of(userSystem1));
//            Assertions.assertTrue(tradingSystem.addOwnerToStore(store, userSystem, userSystem1.getUserName()));
//        }

//        /**
//         * UC 4.3
//         * check the addOwnerToStore() functionality in case of not initialized parameters.
//         */
//        @Test
//        void addOwnerToStoreEmptyParams() {
//            //set up for test
//            setUpAddOwner();
//            //check that the addition of the new owner was successful
//            when(tradingSystemDao.getUserSystem(userSystem1.getUserName())).thenReturn(Optional.of(userSystem1));
//            Assertions.assertTrue(tradingSystem.addOwnerToStore(store, userSystem, userSystem1.getUserName()));
//            //check the store contains this user as owner
//            Assertions.assertTrue(store.getOwnersUsername().contains(userSystem1.getUserName()));
//            //check that the new owner contains the store
//            Assertions.assertEquals(store,userSystem1.getOwnerStore(store.getStoreId()));
//        }

        /**
         * UC 4.3
         * check the addOwnerToStore() functionality in case of failure
         * in addNewOwnedStore and addOwner
         */
        @Test
        void addOwnerToStoreWrongStoreOwner() {
            setUpAddOwnerWrongOwner();
            //try to add a new owner to store with a non owner of store
            when(tradingSystemDao.getUserSystem(userSystem1.getUserName())).thenReturn(Optional.of(userSystem1));
            Assertions.assertFalse(tradingSystem.addOwnerToStore(store, userSystem2, userSystem1.getUserName()));
            //check that the store didn't add the user as owner
            Assertions.assertFalse(store.getOwnersUsername().contains(userSystem1.getUserName()));

        }

        /**
         * UC 4.3
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

//        /**
//         * UC 2.5
//         * check the filterByRangePrice method, checks that the returned products are filtered by a given price
//         */
//        @Test
//        void filterByRangePrice() {
//            List<Product> products = setUpProductsForFilterTests();
//            int max = products.size();
//            for (int min = -1; min < 100; min++) {
//                List<Product> productsActual = tradingSystem.filterByRangePrice(products, min, max);
//                int finalMin = min;
//                int finalMax = max;
//                List<Product> productsExpected = products.stream()
//                        .filter(product -> finalMin <= product.getCost() && product.getCost() <= finalMax)
//                        .collect(Collectors.toList());
//                max--;
//                Assertions.assertEquals(productsExpected, productsActual);
//            }
//        }

//        /**
//         * UC 2.5
//         * check the filterByProductRank method, checks that the returned products are filtered by a given product rank
//         */
//        @Test
//        void filterByProductRank() {
//            List<Product> products = setUpProductsForFilterTests();
//            for (int rank = -1; rank < 100; rank++) {
//                List<Product> productsActual = tradingSystem.filterByProductRank(products, rank);
//                int finalRank = rank;
//                List<Product> productsExpected = products.stream()
//                        .filter(product -> finalRank <= product.getRank())
//                        .collect(Collectors.toList());
//                Assertions.assertEquals(productsExpected, productsActual);
//            }
//        }

//        /**
//         * UC 2.5
//         * check the filterByStoreRank method, checks that the returned products are filtered by a given store rank
//         */
//        @Test
//        void filterByStoreRank() {
//            //initial
//            List<Product> products = setUpProductsForFilterTests();
//            List<Store> stores = setUpStoresForFilterTests(products);
//
//            //mocks for this function
//            tradingSystem = mock(TradingSystem.class);
//            when(tradingSystem.filterByStoreRank(anyList(), anyInt())).thenCallRealMethod();
//            when(tradingSystem.getStore(anyInt())).then(invocation ->{
//                int storeId = invocation.getArgument(0);
//                return stores.get(storeId);
//            });

            // the tests
//            for (int rank = -1; rank < 100; rank++) {
//                List<Product> productsActual = tradingSystem.filterByStoreRank(products, rank);
//                int finalRank = rank;
//                List<Product> productsExpected = products.stream()
//                        .filter(product -> {
//                            int storeId = product.getStoreId();
//                            Store store = stores.get(storeId);
//                            return finalRank <= store.getRank();
//                        })
//                        .collect(Collectors.toList());
//                Assertions.assertEquals(productsExpected, productsActual);
//            }
//        }

//        /**
//         * UC 2.5
//         * check the filterByStoreCategory method, checks that the returned products are filtered by a given store category
//         */
//        @Test
//        void filterByStoreCategory() {
//            List<Product> products = setUpProductsForFilterTests();
//            for (int min = -1; min < 100; min++) {
//                for (int categoryIndex = 0; categoryIndex < ProductCategory.values().length; categoryIndex++) {
//                    ProductCategory category = ProductCategory.values()[categoryIndex];
//                    List<Product> productsActual = tradingSystem.filterByStoreCategory(products, category);
//                    int finalMin = min;
//                    List<Product> productsExpected = products.stream()
//                            .filter(product -> product.getCategory() == category)
//                            .collect(Collectors.toList());
//                    Assertions.assertEquals(productsExpected, productsActual);
//                }
//            }
//        }

        /**
         * UC 2.8
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
         * UC 2.8
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
         * UC 2.8
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
         * UC 2.8
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
         * UC 2.8
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
         * UC 2.8
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
         * UC 3.2
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
            Assertions.assertTrue(openStore.getOwnersUsername().contains(userSystem.getUserName()));
           }


        /**
         * UC 3.2
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
         * UC 3.2
         * This test check if the openStore method fails when store name is empty
         */
        @Test
        void openStoreBlankStoreName(){
            //check that a store can't have a blank name
            Assertions.assertNull(tradingSystem.openStore(userSystem,"", ""));
        }

        /**
         * UC 4.7
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
         * UC 4.7
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
         * UC 4.7
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
         * UC 4.7
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
         * UC 4.7
         * This test check if the removeManager method fails
         * when one of the parameters is null
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

        /**
         * UC 4.4
         * This test check if the removeOwner method succeeds
         * when the parameters are correct.
         */
        @Test
        void removeOwnerSuc(){
            setUpForRemoveOwner();
            //check before remove if userSystem is an owner of the store
            Assertions.assertTrue(store.isOwner(userSystem));
            //check that the user was successfully removed from list
            Assertions.assertTrue(tradingSystem.removeOwner(store,userSystem1,userSystem));
            //check that userSystem is no longer an owner of the store
            Assertions.assertTrue(!store.getOwnersUsername().contains(userSystem.getUserName()));
        }

        /**
         * UC 4.4
         * This test check if the removeOwner method fails
         * when one of the parameters is null
         */
        @Test
        void removeOwnerNullObject(){
            setUpForRemoveOwner();
            //check before remove if userSystem is an owner of the store
            Assertions.assertTrue(store.isOwner(userSystem));
            //can't remove from null store
            Assertions.assertFalse(tradingSystem.removeOwner(null,userSystem1,userSystem));
            //can't remove with null owner
            Assertions.assertFalse(tradingSystem.removeOwner(store,null,userSystem));
            //can't remove a null owner from store
            Assertions.assertFalse(tradingSystem.removeManager(store,userSystem1,null));
            //check after fail remove that the userSystem is still an owner
            Assertions.assertTrue(store.isOwner(userSystem));
        }

        /**
         * UC 4.4
         * This test check if the removeManager method fails
         * when one of the users is not an owner of the store
         */
        @Test
        void removeOwnerNotAnOwner(){
            setUpForRemoveOwner();
            int numberOfOwners = store.getOwnersUsername().size();
            //check that the user is an owner of the store
            Assertions.assertTrue(store.isOwner(userSystem));
            //can't remove owner because userSystem2 is not an owner
            Assertions.assertFalse(tradingSystem.removeOwner(store,userSystem2,userSystem));
            //check that the user is still an owner of the store
            Assertions.assertTrue(store.isOwner(userSystem));
            //can't remove owner because userSystem2 is not an owner
            Assertions.assertFalse(tradingSystem.removeOwner(store,userSystem1,userSystem2));
            //check after fail remove the numbers of owners didn't change
            Assertions.assertEquals(numberOfOwners,store.getOwnersUsername().size());
        }

        /**
         * UC 4.4
         * This test check if the removeManager method fails
         * when both of the users are owners but for a different store
         */
        @Test
        void removeOwnerWrongStore(){
            setUpForRemoveOwner();
            //check that the user is not an owner in this store
            Assertions.assertFalse(store1.isOwner(userSystem));
            //can't remove an owner that is not an owner in this store
            Assertions.assertFalse(tradingSystem.removeOwner(store1,userSystem1,userSystem));
            //check that the user is still not an owner in this store
            Assertions.assertFalse(store1.isOwner(userSystem));
        }


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // ********************************** Set Up Functions For Tests ********************************** //
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        private void setUpForRemoveOwner(){
            when(userSystem.getUserName()).thenReturn("RemovedManager");
            when(store.getStoreId()).thenReturn(2);
            when(store.removeOwner(userSystem1,userSystem)).thenReturn(true);
            Set<OwnersAppointee> owners = new HashSet<>();
            OwnersAppointee appointee = new OwnersAppointee(userSystem1);
            owners.add(appointee);
            store.setAppointedOwners(owners);
            when(store.isOwner(userSystem)).thenReturn(true);
            when(store.removeOwner(userSystem1,userSystem2)).thenReturn(false);
            when(store.removeOwner(userSystem2,userSystem)).thenReturn(false);
            when(store1.removeOwner(userSystem1,userSystem)).thenReturn(false);
            when(store1.isOwner(userSystem1)).thenReturn(false);
            when(store1.isOwner(userSystem)).thenReturn(false);
        }

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
            //Map<String, UUID> usersLogin = new HashMap<>();
            //usersLogin.put(userSystem.getUserName(),UUID.randomUUID());
            when(tradingSystemDao.isRegistered(userSystem)).thenReturn(true);
            //tradingSystem.setUsersLogin(usersLogin);
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
            when(userSystem.getPassword()).thenReturn("passwordTest");
            when(passwordEncoder.matches(userSystem.getPassword(),userSystem.getPassword())).thenReturn(true);
            doNothing().when(userSystem).login();
        }

        private void setUpForLogout(){
            //Map<String, UUID> usersLogin = new HashMap<>();
            uuid = UUID.randomUUID();
            //usersLogin.put("usernameTest", UUID.randomUUID());
            //tradingSystem.setUsersLogin(usersLogin);
            when(userSystem.getUserName()).thenReturn("usernameTest");
            when(tradingSystemDao.isLogin(userSystem.getUserName())).thenReturn(true);
            tradingSystem.setSubject(mock(Subject.class));
            when(tradingSystem.getSubject().unRegDailyVisitor(userSystem.getUserName())).thenReturn(true);
            when(tradingSystemDao.usersLoggedInSystem()).thenReturn(0);
            //when(userSystem.isLogin()).thenReturn(true);
        }

        /**
         * sets up store for openStore test
         */
        private void setUpUsersForOpenStore(){
            Set<String> owners = new HashSet<>();
            when(userSystem1.getUserName()).thenReturn("userForOpenStoreTest");
            owners.add(userSystem1.getUserName());
            when(store.getOwnersUsername()).thenReturn(owners);
            doNothing().when(tradingSystemDao).addStore(store);
            when(tradingSystemDao.isRegistered(userSystem)).thenReturn(true);
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
        //    doNothing().when(userToRegister).setPassword("pass");
          //  doNothing().when(userToRegister).setSalt("salt");
            when(userToRegister.getUserName()).thenReturn("usernameTest");
            when(userToRegister.getFirstName()).thenReturn("usersFirstName");
            when(userToRegister.getLastName()).thenReturn("usersLastName");
            when(userToRegister.getPassword()).thenReturn("IAmPassword");
            when(userToRegister.isValidUser()).thenReturn(true);
           // doNothing().when(userToRegister).setImageUrl(any());
            PasswordSaltPair passwordSaltPair = mock(PasswordSaltPair.class);
            when(passwordSaltPair.getHashedPassword()).thenReturn("123345543gf");
            when(passwordSaltPair.getSalt()).thenReturn("salt");
            when(externalServiceManagement.getEncryptedPasswordAndSalt(userToRegister.getPassword())).thenReturn(passwordSaltPair);
            passwordEncoder = mock(PasswordEncoder.class);
            when(passwordEncoder.encode(userToRegister.getPassword())).thenReturn("IAmPassword");
         //   doNothing().when(userToRegister).setPassword(userToRegister.getPassword());
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
//            Map<String, UUID> loginUser = new HashMap<>();
//            loginUser.put("admin",UUID.randomUUID());
//            tradingSystem.setUsersLogin(loginUser);
            when(admin1.getUserName()).thenReturn("admin1");
            when(tradingSystemDao.getUserSystem(admin1.getUserName())).thenReturn(Optional.ofNullable(admin1));
            when(admin1.getPassword()).thenReturn("iAmPasswordAdmin1");
            when(tradingSystemDao.isAdmin("admin1")).thenReturn(true);
            when(tradingSystemDao.isRegistered(admin1)).thenReturn(true);
            when(passwordEncoder.matches(admin1.getPassword(),admin1.getPassword())).thenReturn(true);
            tradingSystem.registerNewUser(admin1,null);
            Pair<UUID,Boolean> ans = tradingSystem.login(admin1.getUserName(),admin1.getPassword());
            uuid = ans.getKey();
            when(tradingSystemDao.isValidUuid(admin1.getUserName(),uuid)).thenReturn(true);
            when(tradingSystemDao.isLogin(admin1.getUserName())).thenReturn(true);
            when(tradingSystemDao.getStore(1)).thenReturn(Optional.ofNullable(store));
        }

        /**
         * delete the added admin to the login list
         */
        private void tearDownGetStoreAdmin(){
            //Map<String, UUID> loginUser = new HashMap<>();
            //tradingSystem.setUsersLogin(loginUser);
        }

        /**
         * set up a user for test and aet up the right user to return
         */
        private void setUpGetUser(){
            when(tradingSystemDao.getUserSystem("userToReturn")).thenReturn(Optional.ofNullable(userSystem));
            //Map<String, UUID> loginUsers = new HashMap<>();
            //UUID uuid = UUID.randomUUID();
            when(userSystem.getUserName()).thenReturn("userToReturn");
            when(userSystem.getPassword()).thenReturn("userToReturnPassword");
            when(tradingSystemDao.isRegistered(userSystem)).thenReturn(true);
            when(passwordEncoder.matches(userSystem.getPassword(),userSystem.getPassword())).thenReturn(true);
            tradingSystem.registerNewUser(userSystem,null);
            Pair<UUID,Boolean> ans = tradingSystem.login(userSystem.getUserName(),userSystem.getPassword());
            uuid = ans.getKey();
            when(tradingSystemDao.isLogin(userSystem.getUserName())).thenReturn(true);
            when(tradingSystemDao.isValidUuid(userSystem.getUserName(),uuid)).thenReturn(true);
            //loginUsers.put(userSystem.getUserName(), uuid);

            //tradingSystem.setUsersLogin(loginUsers);
        }

        /**
         * set up an admin for test and aet up the right user to return
         */
        private void setUpGetUserByAdmin(){
            when(tradingSystemDao.getUserSystem("userToReturn")).thenReturn(Optional.ofNullable(userSystem));
            //Map<String, UUID> loginUsers = new HashMap<>();
            //loginUsers.put("admin", UUID.randomUUID());
            //tradingSystem.setUsersLogin(loginUsers);
            uuid = UUID.randomUUID();
            when(tradingSystemDao.isLogin("admin")).thenReturn(true);
            when(tradingSystemDao.isValidUuid("admin",uuid)).thenReturn(true);
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
            when(store.addOwner(userSystem,userSystem1)).thenReturn(notNull());
            when(userSystem1.addNewOwnedStore(store)).thenReturn(true);
            when(store.getStoreId()).thenReturn(1);
            when(userSystem1.getUserName()).thenReturn("newOwnerStore");
            Set<String> owners = new HashSet<>();
            when(userSystem1.getUserName()).thenReturn("UserForAddUserTest");
            owners.add(userSystem1.getUserName());
            when(store.getOwnersUsername()).thenReturn(owners);
            when(userSystem1.getOwnerStore(store.getStoreId())).thenReturn(store);
            when(store.addOwner(userSystem2,userSystem1)).thenReturn(null);
        }

        /**
         * set up owner and store
         */
        private void setUpAddOwnerWrongOwner(){
            when(store.addOwner(userSystem,userSystem1)).thenReturn(notNull());
            when(userSystem1.addNewOwnedStore(store)).thenReturn(true);
            when(store.getStoreId()).thenReturn(1);
            when(userSystem1.getUserName()).thenReturn("newOwnerStore");
            Set<String> owners = new HashSet<>();
            when(store.getOwnersUsername()).thenReturn(owners);
            when(store.addOwner(userSystem2,userSystem1)).thenReturn(null);
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
            when(externalServiceManagement.charge(any(),any())).thenReturn(10001);
            when(externalServiceManagement.cancelCharge(any(),any(),any())).thenReturn(-1);
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
            when(externalServiceManagement.charge(any(),any())).thenReturn(10001);
            when(externalServiceManagement.deliver(any(),any())).thenReturn(10002);
            doNothing().when(testShoppingCart).updateAllAmountsInStock();
            when(testShoppingCart.isAllBagsInStock()).thenReturn(true);
            doNothing().when(testShoppingCart).applyDiscountPolicies();
            ArrayList<Receipt> receipts = new ArrayList<>();
            receipts.add(receipt);
            when(testShoppingCart.createReceipts(any(),anyInt(),anyInt())).thenReturn(receipts);
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
    @ContextConfiguration(classes = {TradingSystemConfiguration.class, PasswordEncoder.class, HttpSecurityConfig.class})
    @SpringBootTest(args = {"admin", "admin"})
    public class TradingSystemTestIntegration {

        @Autowired
        private TradingSystem tradingSystem1;
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
        private UUID uuid;
        private PasswordEncoder passwordEncoder;
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
            product.setName("dollhouse");
            product.setCategory(ProductCategory.TOYS_HOBBIES);
            admin = new UserSystem("admin","ad","min","admin",true);
            userSystem2 = new UserSystem("coolAvi","Avi","Neelavi","1234erf");
            newManager = new UserSystem("NewManagerTest", "Manager", "Test", "MyPassword345");
            tradingSystemDao = new TradingSystemDaoImpl();
            externalServiceManagement = mock(ExternalServiceManagement.class);
            setEncoder();
            tradingSystem1 = new TradingSystem(externalServiceManagement, admin, tradingSystemDao, passwordEncoder);
            PasswordSaltPair psp = externalServiceManagement.getEncryptedPasswordAndSalt(admin.getPassword());
          //  admin.setPassword(psp.getHashedPassword());
          //  admin.setSalt(psp.getSalt());
            factoryObjects = new FactoryObjects();
        }

        private void setEncoder(){
            String encodingId = "bcrypt";
            Map<String, PasswordEncoder> encoders = new HashMap<>();
            encoders.put(encodingId, new BCryptPasswordEncoder());
            encoders.put("ldap", new org.springframework.security.crypto.password.LdapShaPasswordEncoder());
            encoders.put("MD4", new org.springframework.security.crypto.password.Md4PasswordEncoder());
            encoders.put("MD5", new org.springframework.security.crypto.password.MessageDigestPasswordEncoder("MD5"));
            encoders.put("noop", org.springframework.security.crypto.password.NoOpPasswordEncoder.getInstance());
            encoders.put("pbkdf2", new Pbkdf2PasswordEncoder());
            encoders.put("scrypt", new SCryptPasswordEncoder());
            encoders.put("SHA-1", new org.springframework.security.crypto.password.MessageDigestPasswordEncoder("SHA-1"));
            encoders.put("SHA-256", new org.springframework.security.crypto.password.MessageDigestPasswordEncoder("SHA-256"));
            encoders.put("sha256", new org.springframework.security.crypto.password.StandardPasswordEncoder());
            passwordEncoder = new DelegatingPasswordEncoder(encodingId, encoders);
        }

        /**
         * UC 2.2
         * the following checks registration of valid user
         */
        @Test
        void registerNewUser() {
            //the following user details are necessary for the login tests
            Assertions.assertTrue(tradingSystem1.registerNewUser(userToRegister, null));
            //check that the user that was just registered is in the users list
            Assertions.assertTrue(tradingSystemDao.getUsers().contains(userToRegister));
        }

        /**
         * UC 2.2
         * checks handling with failure of registration
         * this test has to run after its respective positive test
         */
        @Test
        void registerNewUserNegative() {
            //registration with already registered user
            registerAsSetup(); //first registration
            //check that the user we want to register is already registered
            Assertions.assertTrue(tradingSystemDao.getUsers().contains(userToRegister));
            // try to registered again
            Assertions.assertFalse(tradingSystem1.registerNewUser(userToRegister,null));  //second registration
        }

        /**
         * UC 2.3
         * check if the login method works
         */
        @Test
        void login() {
            //register a user first
            when(externalServiceManagement.isAuthenticatedUserPassword(userToRegister.getPassword()
                    ,userToRegister)).thenReturn(true);
            //when(passwordEncoder.matches(userToRegister.getPassword(), userToRegister.getPassword())).thenReturn(true);
            tradingSystem1.registerNewUser(userToRegister,null);
            //try to login
            Pair<UUID,Boolean> ans = tradingSystem1.login(userToRegister.getUserName(),"passwordTest");
            //check that login process worked
            Assertions.assertNotNull(ans);
            //check that it's not an admin
            Assertions.assertFalse(ans.getValue());
        }

        /**
         * check if the login method works for an admin
         */
        @Test
        void loginAdmin() {
            tradingSystem1.registerNewUser(admin,null);
            tradingSystemDao.registerAdmin(admin);
            when(externalServiceManagement.isAuthenticatedUserPassword(admin.getPassword()
                    ,admin)).thenReturn(true);
            //try to login
            Pair<UUID,Boolean> ans = tradingSystem1.login(admin.getUserName(),"admin");
            //check that login process worked
            Assertions.assertNotNull(ans);
            //check that it's an admin
            Assertions.assertTrue(ans.getValue());
        }

        /**
         * UC 2.3
         * test handling with login failure
         */
        @Test
        void loginNegative(){
            UserSystem user = new UserSystem("TestUser","mati","tut","passwordForTest");
            user.setSalt("salt");
            //try to login a non-registered user
            Pair<UUID, Boolean> loggedIn = tradingSystem1.login(user.getUserName(),user.getPassword());
            //check that login method failed
            Assertions.assertTrue(loggedIn == null);
            //check that the user is not in logged-in map
            Assertions.assertFalse(tradingSystem1.isLoggein(user.getUserName()));
        }

        /**
         * UC 3.1
         * check the logout functionality of exists user in the system
         */
        @Test
        void logout() {
            //setup of login for the logout
            String password = "passwordTest";
            UserSystem user = new UserSystem("logoutTestUser","Banana","Mort",password,false);
            //register the user first
            tradingSystem1.registerNewUser(user,null);
            when(externalServiceManagement.isAuthenticatedUserPassword(password
                    ,user)).thenReturn(true);
            //login
            tradingSystem1.login(user.getUserName(), password);
            //check that the user is in logged-in map
            Assertions.assertTrue(tradingSystem1.isLoggein(user.getUserName()));
            //check that the logout method works
            Assertions.assertTrue(tradingSystem1.logout(user));
            //check that the user is not in logged-in map
            Assertions.assertFalse(tradingSystem1.isLoggein(user.getUserName()));
        }

        /**
         * UC 3.1
         * check handling with logout failure
         */
        @Test
        void logoutNegative() {
            //check that the user is not in logged-in map
            Assertions.assertFalse(tradingSystem1.isLoggein(userToRegister.getUserName()));
            //try to logout a not logged-in user
            Assertions.assertFalse(tradingSystem1.logout(userToRegister));
            //check that the user is not in logged-in map
            Assertions.assertFalse(tradingSystem1.isLoggein(userToRegister.getUserName()));
        }


        /**
         * check the getStoreByAdmin() functionality in case of exists admin in the system
         */
        @Test
        void getStoreByAdminPositive() {
            setUpStore();
            //login first
            when(externalServiceManagement.isAuthenticatedUserPassword("admin"
                    ,admin)).thenReturn(true);
            Pair<UUID,Boolean> ans = tradingSystem1.login(admin.getUserName(),"admin");
            //check that the returned store is the correct one
            Assertions.assertEquals(store,
                    tradingSystem1.getStoreByAdmin("admin", store.getStoreId(), ans.getKey()));
        }

        /**
         *open a store for the test
         */
        private void setUpStore(){
            tradingSystem1.registerNewUser(admin,null);
            tradingSystemDao.registerAdmin(admin);
            userToOpenStore = new UserSystem("userToGetStoreTest","myFirstName",
                    "myLastName","myPassword");
            tradingSystem1.registerNewUser(userToOpenStore,null);
            tradingSystem1.login(userToOpenStore.getUserName(),"myPassword");
            store = tradingSystem1.openStore(userToOpenStore,"StoreForTest","We-Test-Staff");
        }

        /**
         * check the getStoreByAdmin() functionality in case of not exists admin in the system
         */
        @Test
        void getStoreByAdminNegative() {
            setUpStore();
            Assertions.assertThrows(NotAdministratorException.class, () -> {
                tradingSystem1.getStoreByAdmin("userSystem", store.getStoreId(), null);
            });
        }

        /**
         * check the getStore() functionality in case of exists store in the system
         */
        @Test
        void getStorePositive() {
            setUpStore();
            //check that the right store returns
            Assertions.assertEquals(store,tradingSystem1.getStore(store.getStoreId()));
        }

        /**
         * check the getStore() functionality in case of not exists store in the system
         */
        @Test
        void getStoreNegative() {
            Assertions.assertThrows(StoreDontExistsException.class, () -> {
                tradingSystem1.getStore(2);
            });
        }

        /**
         * check the getUser() functionality in case of exists user in the system
         */
        @Test
        void getUserPositive() {
            String password = "passwordForTest";
            userToRegister = new UserSystem("getUserTest","user","system",password,false);
            // register "userToRegister" to the users list in trading system
            tradingSystem1.registerNewUser(userToRegister,null);
            when(externalServiceManagement.isAuthenticatedUserPassword(userToRegister.getPassword()
                    ,userToRegister)).thenReturn(true);
            //login user
            Pair<UUID,Boolean> login = tradingSystem1.login(userToRegister.getUserName(),password);
            //check that the right user returns
            Assertions.assertEquals(userToRegister,
                    tradingSystem1.getUser(userToRegister.getUserName(), login.getKey()));
        }

        /**
         * check the getUser() functionality in case of not exists user in the system
         */
        @Test
        void getUserNegative() {
            // register "userToRegister" to the users list in trading system
            //check that there is no user with this name
            Assertions.assertNull(tradingSystem1.getUser("iAmNotHereTest", UUID.randomUUID()));
        }

        /**
         * check the getUserByAdmin() functionality in case of exists admin in the system
         */
        @Test
        void getUserByAdminPositive() {
            setUpGetUserByAdmin();
            Assertions.assertEquals(userToRegister,tradingSystem1.getUserByAdmin("admin",
                    userToRegister.getUserName(), uuid));
        }

        private void setUpGetUserByAdmin(){
            registerAsSetup();
            tradingSystem1.registerNewUser(admin,null);
            tradingSystemDao.registerAdmin(admin);
            when(externalServiceManagement.isAuthenticatedUserPassword("admin",admin)).thenReturn(true);
            uuid = tradingSystem1.login(admin.getUserName(),"admin").getKey();
        }

        /**
         * check the getUserByAdmin() functionality in case of not exists admin in the system
         */
        @Test
        void getUserByAdminNegative(){
            registerAsSetup();
            //check that the method fails
            Assertions.assertNull(tradingSystem1.getUserByAdmin("userSystem", "usernameTest", uuid));
        }

//        /**
//         * UC 2.5
//         * check the searchProductByName() functionality in case of exists product in store in the system
//         */
//        @Test
//        void searchProductByNamePositive() {
//            setUpSearchProduct();
//            HashSet<Product> products = new HashSet<>();
//            products.add(product);
//            store.addNewProduct(userToRegister, product);
//            // converted both to arrays because one ahd ArrayList type and the other has Set<Product> type
//            Assertions.assertArrayEquals(products.toArray(),
//                    tradingSystem.searchProductByName("dollhouse").toArray());
//        }

        private void setUpSearchProduct(){
            storeOwner = new UserSystem("ownerForSearchTest", "owner", "store","12345fg");
            tradingSystem1.registerNewUser(storeOwner,null);
            store = tradingSystem1.openStore(storeOwner,"Dolls","we have dolls");
            store.addNewProduct(storeOwner,product);
        }

//        /**
//         * UC 2.5
//         * check the searchProductByName() functionality in case of not exists product in store in the system
//         */
//        @Test
//        void searchProductByNameNegative() {
//            setUpSearchProduct();
//            HashSet<Product> products = new HashSet<>();
//            products.add(product);
//            // The disjoint method returns true if its two arguments have no elements in common.
//            Assertions.assertTrue(Collections.disjoint(products, tradingSystem.searchProductByName("puppy")));
//        }

//        /**
//         * UC 2.5
//         * check the searchProductByCategory() functionality in case of exists product in store in the system
//         */
//        @Test
//        void searchProductByCategoryPositive() {
//            setUpSearchProduct();
//            HashSet<Product> products = new HashSet<>();
//            products.add(product);
//            // converted both to arrays because one ahd ArrayList type and the other has Set<Product> type
//            Assertions.assertArrayEquals(products.toArray(),
//                    tradingSystem.searchProductByCategory(ProductCategory.TOYS_HOBBIES).toArray());
//        }

//        /**
//         * UC 2.5
//         * check the searchProductByCategory() functionality in case of not exists product in store in the system
//         */
//        @Test
//        void searchProductByCategoryNegative() {
//            setUpSearchProduct();
//            HashSet<Product> products = new HashSet<>();
//            product.setCategory(ProductCategory.BOOKS_MOVIES_MUSIC);
//            products.add(product);
//            // The disjoint method returns true if its two arguments have no elements in common.
//            Assertions.assertTrue(Collections.disjoint(products,
//                    tradingSystem.searchProductByCategory(ProductCategory.TOYS_HOBBIES)));
//        }

//        /**
//         * UC 2.5
//         * check the searchProductByKeyWords() functionality in case of exists product in store in the system
//         */
//        @Test
//        void searchProductByKeyWordsPositive() {
//            setUpSearchProduct();
//            HashSet<Product> products = new HashSet<>();
//            products.add(product);
//            List<String> keyWords = new ArrayList<>();
//            keyWords.add("doll");
//            keyWords.add("house");
//            // converted both to arrays because one ahd ArrayList type and the other has Set<Product> type
//            Assertions.assertArrayEquals(products.toArray(),
//                    tradingSystem.searchProductByKeyWords(keyWords).toArray());
//        }

//        /**
//         * UC 2.5
//         * check the searchProductByKeyWords() functionality in case of not exists product in store in the system
//         */
//        @Test
//        void searchProductByKeyWordsNegative() {
//            setUpSearchProduct();
//            HashSet<Product> products = new HashSet<>();
//            products.add(product);
//            List<String> keyWords = new ArrayList<>();
//            keyWords.add("elephant");
//            keyWords.add("pig");
//            // The disjoint method returns true if its two arguments have no elements in common.
//            Assertions.assertTrue(Collections.disjoint(tradingSystem.searchProductByKeyWords(keyWords), products));
//        }

//        /**
//         * UC 2.5
//         * check the filterByRangePrice method, checks that the returned products are filtered by a given price
//         */
//        @Test
//        void filterByRangePrice() {
//            List<Product> products = setUpProductsForFilterTests();
//            int max = products.size();
//            for (int min = -1; min < 100; min++) {
//                List<Product> productsActual = tradingSystem.filterByRangePrice(products, min, max);
//                int finalMin = min;
//                int finalMax = max;
//                List<Product> productsExpected = products.stream()
//                        .filter(product -> finalMin <= product.getCost() && product.getCost() <= finalMax)
//                        .collect(Collectors.toList());
//                max--;
//                Assertions.assertEquals(productsExpected, productsActual);
//            }
//        }

//        /**
//         * check the filterByStoreRank method, checks that the returned products are filtered by a given store rank
//         */
//        @Test
//        @Disabled
//        void filterByStoreRank() {
//            //initial
//            List<Product> products = setUpProductsForFilterTests();
//            List<Store> stores = (setUpStoresForFilterTests(products));
//            Set<Store> storesSet = new HashSet<>((stores));
//            /*UserSystem admin = UserSystem.builder()
//                    .userName("admin")
//                    .password("admin")
//                    .build();*/
//           //tradingSystem = new TradingSystem(new ExternalServiceManagement(), storesSet, admin);
//
//            // the tests
//            for (int rank = -1; rank < 100; rank++) {
//                List<Product> productsActual = tradingSystem.filterByStoreRank(products, rank);
//                int finalRank = rank;
//                List<Product> productsExpected = products.stream()
//                        .filter(product -> {
//                            int storeId = product.getStoreId();
//                            Store store = stores.get(storeId);
//                            return finalRank <= store.getRank();
//                        })
//                        .collect(Collectors.toList());
//                Assertions.assertEquals(productsExpected, productsActual);
//            }
//        }

//        /**
//         * UC 2.5
//         * check the filterByStoreCategory method, checks that the returned products are filtered by a given store category
//         */
//        @Test
//        void filterByStoreCategory() {
//            List<Product> products = setUpProductsForFilterTests();
//            for (int min = -1; min < 100; min++) {
//                for (int categoryIndex = 0; categoryIndex < ProductCategory.values().length; categoryIndex++) {
//                    ProductCategory category = ProductCategory.values()[categoryIndex];
//                    List<Product> productsActual = tradingSystem.filterByStoreCategory(products, category);
//                    int finalMin = min;
//                    List<Product> productsExpected = products.stream()
//                            .filter(product -> product.getCategory() == category)
//                            .collect(Collectors.toList());
//                    Assertions.assertEquals(productsExpected, productsActual);
//                }
//            }
//        }

        /**
         * UC 3.2
         * This test check if the openStore method succeeds when the parameters
         * are correct.
         */
        @Test
        void openStoreSuccess(){
            setUpForOpenStoreSuc();
            //before open store there is no stores in the system
            Assertions.assertEquals(0, tradingSystem1.getStores().size());
            //check that the store opened
            store = tradingSystem1.openStore(userToOpenStore, "castro", "cloth");
            Assertions.assertNotNull(store);
            //after opening 1 store there needs to be a store in the system
            Assertions.assertEquals(1, tradingSystem1.getStores().size());
            //check that the store that opened has the same name
            Assertions.assertEquals(store.getStoreName(), tradingSystem1.getStore(store.getStoreId()).getStoreName());
            //check the owner is userToOpenStore
            Assertions.assertTrue(store.getOwnersUsername().contains(userToOpenStore.getUserName()));
            //check that the user is really an owner
            Assertions.assertEquals(store ,userToOpenStore.getOwnerStore(store.getStoreId()));
        }

        /**
         * UC 3.2
         * This test check if the openStore method fails
         * when the user that try's to open the store is not registered
         */
        @Test
        void openStoreNonRegisterUser(){
            UserSystem userTest = new UserSystem("NotRegistered","notIn","system","23rfgt");
            //before open store there are 0 stores in the system
            Assertions.assertEquals(0, tradingSystem1.getStores().size());
            //fail to open an existing store
            Assertions.assertNull(tradingSystem1.openStore(userTest, "Can'tOpen","This store"));
            //after fail opening there are still 0 stores in the system
            Assertions.assertEquals(0, tradingSystem1.getStores().size());
        }

        /**
         * UC 3.2
         * This test check if the openStore method fails when the parameters
         * are wrong.
         */
        @Test
        void openStoreNullObject(){
            setUpOpenStore();
            //before open store there is 1 store in the system
            Assertions.assertEquals(1, tradingSystem1.getStores().size());
            //fail to open with null parameters
            Assertions.assertNull(tradingSystem1.openStore(userToOpenStore,null,"i am a bad store"));
            //fail to open with null parameters
            Assertions.assertNull(tradingSystem1.openStore(userToOpenStore,"badStore",null));
            //fail to open with null parameters
            Assertions.assertNull(tradingSystem1.openStore(null, "nullUser","can't open"));
            //after fail opening there is still 1 store in the system
            Assertions.assertEquals(1, tradingSystem1.getStores().size());
        }

        private void setUpOpenStore(){
            userToOpenStore = new UserSystem("ownerForTestOpenStore","ownerStore","Test","34546trgf");
            tradingSystem1.registerNewUser(userToOpenStore,null);
            when(externalServiceManagement.isAuthenticatedUserPassword(userToOpenStore.getPassword(),userToOpenStore)).thenReturn(true);
            tradingSystem1.login(userToOpenStore.getUserName(),userToOpenStore.getPassword());
            store = tradingSystem1.openStore(userToOpenStore, "Toys","we sell toys");
        }

        /**
         * UC 2.8
         * This test check if the purchaseShoppingCart method succeeds
         * when all the parameters are correct.
         */
        @Test
        void guestPurchaseShoppingCartPositive() {
            setUpPurchaseCartSuc();
            List<Receipt> receipts = tradingSystem1.purchaseShoppingCartGuest(testShoppingCart,paymentDetails,billingAddress);
            //check that the return object is not null
            Assertions.assertNotNull(receipts);
            //check that the product was bought
            Assertions.assertTrue(receipts.get(0).getProductsBought().containsKey(product));
            //check that the total price is correct
           Assertions.assertEquals(Double.parseDouble(formatter.format(product.getCost()*5)),receipts.get(0).getAmountToPay());

        }

        /**
         * UC 2.8
         * This test check if the purchaseShoppingCart method fails
         * when the cart is empty.
         */
        @Test
        void guestPurchaseShoppingCartEmptyCart() {
            setUpEmptyCart();
            //check that the system does not allow to buy an empty cart
            Assertions.assertNull(tradingSystem1.purchaseShoppingCartGuest(testShoppingCart,paymentDetails,billingAddress));
        }

        /**
         * UC 2.8
         * This test check if the purchaseShoppingCart method fails
         * when one of the insert parameters is null.
         */
        @Test
        void guestPurchaseShoppingCartNullObject() {
            setUpNotEmptyCart();
            //check that the system does not allow to buy a null cart
            Assertions.assertNull(tradingSystem1.purchaseShoppingCartGuest(null,paymentDetails,billingAddress));
            //check that the system does not allow to use a null payment details
            Assertions.assertNull(tradingSystem1.purchaseShoppingCartGuest(testShoppingCart,null,billingAddress));
            //check that the system does not allow to use a null billing address
            Assertions.assertNull(tradingSystem1.purchaseShoppingCartGuest(null,paymentDetails,null));
        }

        /**
         * UC 2.8
         * This test check if the purchaseShoppingCart method fails
         * when one of the products is not in stock.
         */
        @Test
        void guestPurchaseShoppingCartProductsNotInStock() {
            setUpProductNotInStock();
            //check that the system does not allow to buy an empty cart
            Assertions.assertThrows(NotInStockException.class, ()->
                    tradingSystem1.purchaseShoppingCartGuest(testShoppingCart,paymentDetails,billingAddress));
        }

        /**
         * UC 2.8
         * This test check if the purchaseShoppingCart method fails
         * when there is a problem with charge system and the product are not removed from cart.
         */
        @Test
        void guestPurchaseShoppingCartChargeFail() {
            setUpChargeFail();
            int amountOfProductInStore = store.getProduct(product.getProductSn()).getAmount();
            //check that the charge fails
            Assertions.assertThrows(ChargeException.class, ()->
                    tradingSystem1.purchaseShoppingCartGuest(testShoppingCart,paymentDetails,billingAddress));
            //check that product is in the cart after charge failed
            Assertions.assertTrue(testShoppingCart.getShoppingBag(store).getProductListFromStore().containsKey(product));
            //check that the amount of the product didn't change in store after fail charge
            Assertions.assertEquals(amountOfProductInStore, store.getProduct(product.getProductSn()).getAmount());
        }

        /**
         * UC 2.8
         * This test check if the purchaseShoppingCart method fails
         * when there is a problem with deliver system and the product are not removed from cart.
         */
        @Test
        void guestPurchaseShoppingCartDeliverFail() {
            setUpFailDelivery();
            int amountOfProductInStore = store.getProduct(product.getProductSn()).getAmount();
            //check that the deliver fails
            Assertions.assertThrows(DeliveryRequestException.class, ()->
                    tradingSystem1.purchaseShoppingCartGuest(testShoppingCart,paymentDetails,billingAddress));
            //check that product is in the cart after deliver failed
            Assertions.assertTrue(testShoppingCart.getShoppingBag(store).getProductListFromStore().containsKey(product));
            //check that the amount of the product didn't change in store after fail charge
            Assertions.assertEquals(amountOfProductInStore, store.getProduct(product.getProductSn()).getAmount());
        }

        /**
         * UC 2.8
         * This test check if the purchaseShoppingCart method succeeds when the parameters
         * are correct.
         */
        @Test
        void registeredPurchaseShoppingCartPositive() {
            setUpForRegisteredPurchaseSuc();
            int amountOfProductInStore = store.getProduct(product.getProductSn()).getAmount();
            List<Receipt> receipts = tradingSystem1.purchaseShoppingCart(paymentDetails,billingAddress,userSystem2);
            //check that the returned object is not null
            Assertions.assertNotNull(receipts);
            //check that the returned object contains the right receipt
            Assertions.assertTrue(userSystem2.getReceipts().containsAll(receipts));
            //check the stock in store updated
            Assertions.assertTrue(amountOfProductInStore > store.getProduct(product.getProductSn()).getAmount());
        }

        /**
         * UC 4.5
         * This test check if the addManager method succeeds when the parameters
         * are correct.
         */
        @Test
        void addManagerSuccess(){
            setUpAddManagerAndOwner();
            //check that the new manager added to store
            Assertions.assertNotNull(tradingSystem1.addMangerToStore(store1, storeOwner, newManager.getUserName()));
            //check that the number of managers of the store is 1
            Assertions.assertEquals(1, store1.getManagersStore().size());
            //check that right manager was added
            Assertions.assertEquals(newManager, store1.getManager(storeOwner, newManager.getUserName()));
        }

        private void setUpAddManagerAndOwner(){
            storeOwner = new UserSystem("OwnerTest","IAmOwner","Test","OwnerPassword");
            newManager = new UserSystem("Manager", "IAm","Manager","ManagerPassword");
            tradingSystem1.registerNewUser(storeOwner,null);
            tradingSystem1.registerNewUser(newManager,null);
            tradingSystem1.login(storeOwner.getUserName(),"OwnerPassword");
            tradingSystem1.login(newManager.getUserName(),"ManagerPassword");
            store1 = tradingSystem1.openStore(storeOwner,"LeeOffice", "We Print Stuff");
        }

        /**
         * UC 4.5
         * This test check if the addManager method fails when the parameters
         * are wrong.
         */
        @Test
        void addManagerNotAnOwner(){
            setUpAddManagerAndOwnerFail();
            //not an owner of the store, can't appoint a manager
            Assertions.assertNull(tradingSystem1.addMangerToStore(store1,storeOwner,newManager.getUserName()));
            //check that the number of managers of the store is still 0
            Assertions.assertEquals(0, store1.getManagersStore().size());
        }

        private void setUpAddManagerAndOwnerFail(){
            storeOwner = new UserSystem("OwnerTest","IAmOwner","Test","OwnerPassword");
            tradingSystem1.registerNewUser(storeOwner,null);
            tradingSystem1.registerNewUser(newManager,null);
            tradingSystem1.registerNewUser(userSystem2, null);
            store1 = tradingSystem1.openStore(userSystem2,"LeeOffice", "We Print Stuff");
        }

        /**
         * UC 4.5
         * This test check if the addManager method fails when the parameters
         * are wrong.
         */
        @Test
        void addManagerObjectNull(){
            setUpAddManagerAndOwner();
            //can't appoint a null manager
            Assertions.assertNull(tradingSystem1.addMangerToStore(store1,storeOwner,null));
            //can't appoint a manager with null owner
            Assertions.assertNull(tradingSystem1.addMangerToStore(store1,null,newManager.getUserName()));
            //can't appoint a manager with null store
            Assertions.assertNull(tradingSystem1.addMangerToStore(null,storeOwner,newManager.getUserName()));
            //check that the number of managers of the store is still 0
            Assertions.assertEquals(0, store1.getManagersStore().size());
        }

        /**
         * UC 4.5
         * This test check if the addManager method fails when the parameters
         * are wrong.
         */
        @Test
        void addManagerEmptyUserNameManager(){
            setUpAddManagerAndOwner();
            //can't appoint a null manager
            Assertions.assertNull(tradingSystem1.addMangerToStore(store1,storeOwner,""));
            //check that the number of managers of the store is still 0
            Assertions.assertEquals(0, store1.getManagersStore().size());
        }

        /**
         * UC 4.5
         * This test check if the addManager method fails when the parameters
         * are wrong.
         */
        @Test
        void addManagerAppointMyself(){
            setUpAddManagerAndOwner();
            //can't appoint a himself
            Assertions.assertNull(tradingSystem1.addMangerToStore(store1,storeOwner,storeOwner.getUserName()));
            //check that the number of managers of the store is still 0
            Assertions.assertEquals(0, store1.getManagersStore().size());
        }

        /**
         * UC 4.3
         * This test check if the addOwner method succeeds when the parameters
         * are correct.
         */
        @Test
        void addOwnerSuccess(){
            setUpAddManagerAndOwner();
            //check that the new owner added to store
            Assertions.assertTrue(tradingSystem1.addOwnerToStore(store1, storeOwner, newManager.getUserName()));
            //check that the number of owners of the store is 2
            Assertions.assertEquals(2, store1.getOwnersUsername().size());
            //check that the new owner is in store
            Assertions.assertTrue(store1.getOwnersUsername().contains(newManager.getUserName()));
        }

        /**
         * UC 4.3
         * This test check if the addOwner method fails when the parameters
         * are wrong.
         */
        @Test
        void addOwnerNotAnOwner(){
            setUpAddManagerAndOwnerFail();
            //not an owner of the store, can't appoint an owner
            Assertions.assertFalse(tradingSystem1.addOwnerToStore(store1,storeOwner,newManager.getUserName()));
            //check that the number of owners of the store is still 1
            Assertions.assertEquals(1, store1.getOwnersUsername().size());
        }

        /**
         * UC 4.3
         * This test check if the addManager method fails when the parameters
         * are wrong.
         */
        @Test
        void addOwnerObjectNull(){
            setUpAddManagerAndOwner();
            //can't appoint a null owner
            Assertions.assertFalse(tradingSystem1.addOwnerToStore(store1,storeOwner,null));
            //can't appoint an owner with null owner
            Assertions.assertFalse(tradingSystem1.addOwnerToStore(store1,null,newManager.getUserName()));
            //can't appoint an owner with null store
            Assertions.assertFalse(tradingSystem1.addOwnerToStore(null,storeOwner,newManager.getUserName()));
            //check that the number of owners of the store is still 1
            Assertions.assertEquals(1, store1.getOwnersUsername().size());
        }

        /**
         * UC 4.3
         * This test check if the addManager method fails when the parameters
         * are wrong.
         */
        @Test
        void addOwnerEmptyUserNameOwner(){
            setUpAddManagerAndOwner();
            //can't appoint a empty owner
            Assertions.assertFalse(tradingSystem1.addOwnerToStore(store1,storeOwner,""));
            //check that the number of owners of the store is still 1
            Assertions.assertEquals(1, store1.getOwnersUsername().size());
        }

        /**
         * UC 4.3
         * This test check if the addManager method fails when the parameters
         * are wrong.
         */
        @Test
        void addOwnerAppointMyself(){
            setUpAddManagerAndOwner();
            //can't appoint a null manager
            Assertions.assertFalse(tradingSystem1.addOwnerToStore(store1,storeOwner,storeOwner.getUserName()));
            //check that the number of owners of the store is still 1
            Assertions.assertEquals(1, store1.getOwnersUsername().size());
            //check that the new owner is in store
            Assertions.assertTrue(store1.getOwnersUsername().contains(storeOwner.getUserName()));
        }

        /**
         * UC 4.7
         * This test check if the removeManager method succeeds when the parameters
         * are correct.
         */
        @Test
        void removeManagerSuccess(){
            setUpUsersForRemoveManagerSuc();
            //before remove there is 1 manager in store
            Assertions.assertEquals(1, store1.getManagersStore().size());
            //check that the newManager is really a store manager
            Assertions.assertEquals(newManager ,store1.getManager(storeOwner,newManager.getUserName()));
            //check that remove success
            Assertions.assertTrue(tradingSystem1.removeManager(store1, storeOwner, newManager));
            //after remove there are 0 managers in store
            Assertions.assertEquals(0, store1.getManagersStore().size());
            //check that the newManager is no longer a manager
            Assertions.assertFalse(store1.getManagersStore().contains(newManager));

        }

        private void setUpUsersForRemoveManagerSuc(){
            storeOwner = new UserSystem("OwnerTest","IAmOwner","Test","OwnerPassword");
            tradingSystem1.registerNewUser(storeOwner,null);
            tradingSystem1.registerNewUser(newManager,null);
            store1 = tradingSystem1.openStore(storeOwner,"LeeOffice", "We Print Stuff");
            tradingSystem1.addMangerToStore(store1,storeOwner,newManager.getUserName());
        }

        /**
         * UC 4.7
         * This test check if the removeManager method fails when the parameters
         * are wrong.
         */
        @Test
        void removeManagerNotAnOwner(){
            setUpRemoveManagerFail();
            //check that newManager is a manager in store
            Assertions.assertEquals(newManager,store.getManager(userToOpenStore,newManager.getUserName()));
            //check that the method fails
            Assertions.assertFalse(tradingSystem1.removeManager(store,wrongOwner,newManager));
            //check that newManager is a manager in store
            Assertions.assertEquals(newManager,store.getManager(userToOpenStore,newManager.getUserName()));
        }

        /**
         * UC 4.7
         * This test check if the removeManager method fails when the parameters
         * are wrong.
         */
        @Test
        void removeManagerNotAManager(){
            setUpRemoveManagerFail();
            //check that there are managers in store
            Assertions.assertEquals(1,store.getManagersStore().size());
            //check that the method fails
            Assertions.assertFalse(tradingSystem1.removeManager(store,userToOpenStore,wrongOwner));
            //check that the number of managers didn't change
            Assertions.assertEquals(1,store.getManagersStore().size());
        }

        /**
         * UC 4.7
         * This test check if the removeManager method fails when the parameters
         * are wrong.
         */
        @Test
        void removeManagerNullObject(){
            setUpRemoveManagerFail();
            //can't remove from null store
            Assertions.assertFalse(tradingSystem1.removeManager(null,storeOwner,newManager));
            //can't remove with null owner
            Assertions.assertFalse(tradingSystem1.removeManager(store,null,newManager));
            //can't remove a null manager
            Assertions.assertFalse(tradingSystem1.removeManager(store1,wrongOwner,null));
        }

        private void setUpRemoveManagerFail(){
            userToOpenStore = new UserSystem("removeManagerTest","user","forTest", "1234rfght");
            tradingSystem1.registerNewUser(userToOpenStore,null);
            store = tradingSystem1.openStore(userToOpenStore,"StoreForTest","i dont know");
            store.appointAdditionManager(userToOpenStore,newManager);
        }

        /**
         * UC 4.4
         * This test check if the removeOwner method succeeds
         * when the parameters are correct.
         */
        @Test
        void removeOwnerSuc(){
            setUpForRemoveOwner();
            //check before remove if userSystem is an owner of the store
            Assertions.assertTrue(store.isOwner(userToOpenStore));
            //check before remove if userSystem is an owner of the store
            Assertions.assertTrue(store.isOwner(storeOwner));
            //check that the user was successfully removed from list
            Assertions.assertTrue(tradingSystem1.removeOwner(store,userToOpenStore,storeOwner));
            //check that userSystem is no longer an owner of the store
            Assertions.assertFalse(store.isOwner(storeOwner));
        }

        /**
         * UC 4.4
         * This test check if the removeOwner method succeeds
         * when the parameters are correct.
         */
        @Test
        void removeOwnerSucTwoOwners(){
            setUpForRemoveOwner();
            //approve 3'd owner
            store.approveOwner(userToOpenStore,newManager.getUserName(),true);
            //check before remove if userSystem is an owner of the store
            Assertions.assertTrue(store.isOwner(userToOpenStore));
            //check before remove if userSystem is an owner of the store
            Assertions.assertTrue(store.isOwner(storeOwner));
            //check before remove if userSystem is an owner of the store
            Assertions.assertTrue(store.isOwner(newManager));
            //check that the user was successfully removed from list
            Assertions.assertTrue(tradingSystem1.removeOwner(store,userToOpenStore,storeOwner));
            //check that userSystem is no longer an owner of the store
            Assertions.assertFalse(store.isOwner(storeOwner));
            //appointed by storeOwner, needs to be deleted if storeOwner wad removed
            Assertions.assertFalse(store.isOwner(newManager));
        }



        /**
         * UC 4.4
         * This test check if the removeManager method fails
         * when 2 users are owners of the store but
         * the removing owner didn't appoint the owner that needs to be removed
         */
        @Test
        void removeOwnerNotAppointedBy(){
            setUpForRemoveOwner();
            //check before remove if userSystem is an owner of the store
            Assertions.assertTrue(store.isOwner(userToOpenStore));
            //check before remove if userSystem is an owner of the store
            Assertions.assertTrue(store.isOwner(storeOwner));
            //check that the method fails
            Assertions.assertFalse(tradingSystem1.removeOwner(store,storeOwner,userToOpenStore));
            //check after fail remove userToOpenStore is still an owner of the store
            Assertions.assertTrue(store.isOwner(userToOpenStore));
            //check after fail remove storeOwner is still an owner of the store
            Assertions.assertTrue(store.isOwner(storeOwner));
        }

        private void setUpForRemoveOwner(){
            userToOpenStore = new UserSystem("openStoreUserTest", "open", "storeUser", "123weer");
            storeOwner = new UserSystem("storeOwnerTest","storeOwner","Test","456tyu");
            newManager = new UserSystem("newManagerTest","manager","storeTest","678uio");
            tradingSystem1.registerNewUser(userToOpenStore,null);
            tradingSystem1.registerNewUser(storeOwner,null);
            tradingSystem1.registerNewUser(newManager,null);
            tradingSystem1.login(userToOpenStore.getUserName(),"123weer");
            tradingSystem1.login(storeOwner.getUserName(),"456tyu");
            tradingSystem1.login(newManager.getUserName(),"678uio");
            store = tradingSystem1.openStore(userToOpenStore,"newKindStore","we print stuff");
            tradingSystem1.addOwnerToStore(store,userToOpenStore,storeOwner.getUserName());
            tradingSystem1.addOwnerToStore(store,storeOwner,newManager.getUserName());
            // store.addOwner(userToOpenStore,storeOwner);
            //  store.addOwner(storeOwner,newManager);
            store1 = tradingSystem1.openStore(newManager,"ZARA", "cloth");
        }

        /**
         * UC 4.4
         * This test check if the removeOwner method fails
         * when one of the parameters is null
         */
        @Test
        void removeOwnerNullObject(){
            setUpForRemoveOwner();
            //check before remove if userSystem is an owner of the store
            Assertions.assertTrue(store.isOwner(userToOpenStore));
            //check before remove if userSystem is an owner of the store
            Assertions.assertTrue(store.isOwner(storeOwner));
            //can't remove from null store
            Assertions.assertFalse(tradingSystem1.removeOwner(null,userToOpenStore,storeOwner));
            //can't remove with null owner
            Assertions.assertFalse(tradingSystem1.removeOwner(store,null,storeOwner));
            //can't remove a null owner from store
            Assertions.assertFalse(tradingSystem1.removeManager(store,userToOpenStore,null));
            //check after fail remove that the userSystem is still an owner
            Assertions.assertTrue(store.isOwner(userToOpenStore));
            //check after fail remove that the userSystem is still an owner
            Assertions.assertTrue(store.isOwner(storeOwner));
        }

        /**
         * UC 4.4
         * This test check if the removeOwner method fails
         * when an owner try's to remove himself
         */
        @Test
        void removeOwnerHimself(){
            setUpForRemoveOwner();
            //check before remove if userSystem is an owner of the store
            Assertions.assertTrue(store.isOwner(userToOpenStore));
            //can't remove himself from store
            Assertions.assertFalse(tradingSystem1.removeOwner(store,userToOpenStore,userToOpenStore));
            //check after fail remove that the userSystem is still an owner
            Assertions.assertTrue(store.isOwner(userToOpenStore));
        }

        /**
         * UC 4.4
         * This test check if the removeManager method fails
         * when one of the users is not an owner of the store
         */
        @Test
        void removeOwnerNotAnOwner(){
            setUpForRemoveOwner();
            int numberOfOwners = store.getOwnersUsername().size();
            //check that the user is an owner of the store
            Assertions.assertTrue(store.isOwner(storeOwner));
            //can't remove owner because userSystem2 is not an owner
            Assertions.assertFalse(tradingSystem1.removeOwner(store,userSystem2,storeOwner));
            //check that the user is still an owner of the store
            Assertions.assertTrue(store.isOwner(storeOwner));
            //can't remove owner because userSystem2 is not an owner
            Assertions.assertFalse(tradingSystem1.removeOwner(store,storeOwner,userSystem2));
            //check after fail remove the numbers of owners didn't change
            Assertions.assertEquals(numberOfOwners,store.getOwnersUsername().size());
        }

        /**
         * UC 4.4
         * This test check if the removeManager method fails
         * when both of the users are owners but for a different store
         */
        @Test
        void removeOwnerWrongStore(){
            setUpForRemoveOwner();
            //check that the user is not an owner in this store
            Assertions.assertFalse(store1.isOwner(storeOwner));
            //can't remove an owner that is not an owner in this store
            Assertions.assertFalse(tradingSystem1.removeOwner(store1,userToOpenStore,storeOwner));
            //check that the user is still not an owner in this store
            Assertions.assertFalse(store1.isOwner(storeOwner));
        }




        // ************************************ Set Up For Tests ************************************ //

        /**
         * set up of successful pre registration
         */
        private void registerAsSetup(){
            userToRegister = new UserSystem("userForTest","iAm","User","myPassword",false);
            tradingSystem1.registerNewUser(userToRegister,null);
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
            tradingSystem1.registerNewUser(userToOpenStore,null);
            when(externalServiceManagement.isAuthenticatedUserPassword(userToOpenStore.getPassword(),userToOpenStore)).thenReturn(true);
            uuid = tradingSystem1.login(userToOpenStore.getUserName(),"Isra123").getKey();
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
            paymentDetails = new PaymentDetails("123456789", "237", "333333339","12","2022","myName");
            billingAddress = new BillingAddress("Israel Israeli", "Ben-Gurion 1", "Beer Sheva", "Israel","1234567");
        }

        /**
         * setUp for purchaseCart
         */
        private void setUpForPurchaseCartFail(){
            setUpPurchaseCartSuc();
            userToOpenStore = getUserSystemBuild();
            testUser = getUser();
            tradingSystem1.registerNewUser(userToOpenStore,null);
            tradingSystem1.registerNewUser(testUser,null);
            tradingSystem1.login(userToOpenStore.getUserName(),"Isra123");
            tradingSystem1.login(testUser.getUserName(),"Odin12");
            storeToOpen = tradingSystem1.openStore(storeOwner,"MovieStore","we hae movies");
            storeToOpen1 = tradingSystem1.openStore(storeOwner,"MovieStoreVIP", "we have movies vip");
            testProduct = new Product("Harry-Potter",ProductCategory.BOOKS_MOVIES_MUSIC,45,12.9,storeToOpen.getStoreId());
            storeToOpen.addNewProduct(storeOwner,testProduct);
            testProduct1 = new Product("Hunger Games", ProductCategory.BOOKS_MOVIES_MUSIC, 45, 12.9,storeToOpen1.getStoreId());
            storeToOpen1.addNewProduct(storeOwner,testProduct1);
            //userToOpenStore.saveProductInShoppingBag(storeToOpen,testProduct,47);
            testUser.saveProductInShoppingBag(storeToOpen1,testProduct1,5);
            paymentDetails = new PaymentDetails("123456789", "237", "333333339","12","2022",userToOpenStore.getFirstName());
            paymentDetails1 = new PaymentDetails( "12345", "237", "333333339","12","2022",userToOpenStore.getFirstName());
            billingAddress = new BillingAddress("Israel Israeli", "Ben-Gurion 1", "Beer Sheva", "Israel","1234567");
            billingAddress1 = new BillingAddress("Ragnar Lodbrok", "Main Tent", "Kattegat", "Sweden","1");
           // when(externalServiceManagement.)
        }

        /**
         * makes a user for tests
         */
        private UserSystem getUserSystemBuild(){
            return new UserSystem("coolIsrael","Israel","Israeli","Isra123",false);
//            return UserSystem.builder()
//                    .userName("coolIsrael")
//                    .password("Isra123")
//                    .firstName("Israel")
//                    .lastName("Israeli")
//                    .salt("salt").build();
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
            return new UserSystem("Bjorn_Ironside","Bjorn","Lodbrok","IronSide12",false);
        }

        private UserSystem getUser(){
            return new UserSystem("KingRagnar","Ragnar","Lodbrok","Odin12",false);
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
//            doNothing().when(testShoppingCart).applyDiscountPolicies();
//            when(product.getName()).thenReturn("Bamba");
//            when(store.getStoreName()).thenReturn("Shufersal-Dil");
//            when(testShoppingCart.getNumOfBagsInCart()).thenReturn(1);
//            Map<Store, ShoppingBag> bagList = new HashMap<>();
//            Map<Product, Integer> productsList = new HashMap<>();
//            productsList.put(product,2);
//            testShoppingBag1.setProductListFromStore(productsList);
//            bagList.put(store,testShoppingBag1);
//            testShoppingCart.setShoppingBagsList(bagList);
//            when(testShoppingCart.getShoppingBag(store)).thenReturn(testShoppingBag1);
//            when(testShoppingBag1.getProductListFromStore()).thenReturn(productsList);
            setUpPurchaseCartSuc();
            when(externalServiceManagement.charge(any(),any())).thenReturn(10001);
            when(externalServiceManagement.cancelCharge(any(),any(),any())).thenReturn(10001);
            when(externalServiceManagement.deliver(any(),any())).thenReturn(-1);
            //when(externalServiceManagement.deliver(any(),any())).
            //        thenThrow(new DeliveryRequestException("The Delivery request rejected"));
        }

        /**
         * set up product not in stock
         */
        private void setUpProductNotInStock(){
            setUpPurchaseCartSuc();
            testShoppingBag1 = new ShoppingBag(store);
            testShoppingBag1.addProductToBag(product,10);
            testShoppingCart = new ShoppingCart();
            testShoppingCart.addBagToCart(store,testShoppingBag1);
            store.editProduct(storeOwner,product.getProductSn(),product.getName(),
                   "health",5,product.getCost());
//            when(testShoppingCart.getNumOfBagsInCart()).thenReturn(1);
//            Map<Store, ShoppingBag> bagList= new HashMap<>();
//            when(testShoppingCart.getShoppingBagsList()).thenReturn(bagList);
//            when(product.getName()).thenReturn("Bamba");
//            when(store.getStoreName()).thenReturn("Shufersal-Dil");
//            when(testShoppingCart.isAllBagsInStock()).thenThrow(NotInStockException.class);
        }

        /**
         * set the cart & store for guestPurchaseShoppingCartChargeFail test
         */
        private void setUpChargeFail(){
            setUpPurchaseCartSuc();
            when(externalServiceManagement.charge(any(),any())).thenReturn(-1);//charge failed
//            doNothing().when(testShoppingCart).applyDiscountPolicies();
//            when(product.getName()).thenReturn("Bamba");
//            when(store.getStoreName()).thenReturn("Shufersal-Dil");
//            when(testShoppingCart.getNumOfBagsInCart()).thenReturn(1);
//            Map<Store, ShoppingBag> bagList = new HashMap<>();
//            Map<Product, Integer> productsList = new HashMap<>();
//            productsList.put(product,2);
//            testShoppingBag1.setProductListFromStore(productsList);
//            bagList.put(store,testShoppingBag1);
//            testShoppingCart.setShoppingBagsList(bagList);
//            when(testShoppingCart.getShoppingBag(store)).thenReturn(testShoppingBag1);
//            when(testShoppingBag1.getProductListFromStore()).thenReturn(productsList);
//            when(externalServiceManagement.charge(any(),any())).thenThrow(ChargeException.class);
        }
        /**
         * set up objects for guestPurchaseShoppingCartNullObject test
         */
        private void setUpNotEmptyCart(){
            setUpPurchaseCartSuc();
//            when(testShoppingCart.getNumOfProductsInCart()).thenReturn(1);
//            Map<Store, ShoppingBag> bagList= new HashMap<>();
//            when(testShoppingCart.getShoppingBagsList()).thenReturn(bagList);
        }

        /**
         * set empty cart for test guestPurchaseShoppingCartEmptyCart
         */
        private void setUpEmptyCart(){
            setUpPurchaseCartSuc();
            testShoppingCart = new ShoppingCart();
        }

        /**
         * set up all needed objects for guestPurchaseShoppingCartPositive test
         */
        private void setUpPurchaseCartSuc(){
            testShoppingCart = new ShoppingCart();
            storeOwner = new UserSystem("OwnerForTest","userOwner","test","myPassword1");
            tradingSystem1.registerNewUser(storeOwner,null);
            tradingSystem1.login(storeOwner.getUserName(),"myPassword1");
            store = tradingSystem1.openStore(storeOwner,"Shufersal-Dil","super-market");
            product = new Product("Bamba", ProductCategory.HEALTH, 10000, 4.99, store.getStoreId());
            store.addNewProduct(storeOwner,product);
            testShoppingBag1 = new ShoppingBag(store);
            testShoppingBag1.addProductToBag(product,5);
            testShoppingCart.addBagToCart(store,testShoppingBag1);
            when(externalServiceManagement.charge(any(),any())).thenReturn(10001);
            when(externalServiceManagement.deliver(any(),any())).thenReturn(10002);
            paymentDetails = new PaymentDetails("123456789", "237", "333333339","12","2022","Guest");
            billingAddress = new BillingAddress("Israel Israeli", "Ben-Gurion 1", "Beer Sheva", "Israel","1234567");
        }

        /**
         * set up for registered user purchase cart
         */
        private void setUpForRegisteredPurchaseSuc(){
            setUpPurchaseCartSuc();
            userSystem2 = new UserSystem("userForPurchase","myName","myLastName","passwordUser");
            tradingSystem1.registerNewUser(userSystem2,null);
            uuid = tradingSystem1.login(userSystem2.getUserName(),"passwordUser").getKey();
            paymentDetails = new PaymentDetails("123456789", "237", "333333339","12","2022",userSystem2.getFirstName());
            userSystem2.setShoppingCart(testShoppingCart);
            //when(userSystem2.getShoppingCart()).thenReturn(testShoppingCart);
            //when(userSystem2.getUserName()).thenReturn("PurchaseRegisteredUser");
        }
    }
}

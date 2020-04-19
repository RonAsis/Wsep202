package com.wsep202.TradingSystem.domain.trading_system_management;


//import Externals.PasswordSaltPair;
import com.github.rozidan.springboot.modelmapper.WithModelMapper;
import com.wsep202.TradingSystem.domain.config.TradingSystemConfiguration;
import com.wsep202.TradingSystem.domain.exception.NotAdministratorException;
import com.wsep202.TradingSystem.domain.exception.ProductDoesntExistException;
import com.wsep202.TradingSystem.domain.exception.StoreDontExistsException;
import com.wsep202.TradingSystem.domain.exception.UserDontExistInTheSystemException;
import com.wsep202.TradingSystem.domain.factory.FactoryObjects;
import com.wsep202.TradingSystem.domain.mapping.TradingSystemMapper;
import org.assertj.core.api.Assert;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.invocation.InvocationOnMock;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.parameters.P;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;
import java.util.stream.Collectors;

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
        private UserSystem userSystem;
        private UserSystem userSystem1;
        private UserSystem userSystem2;
        private UserSystem userToRegister;
        private FactoryObjects factoryObjects;
        private UserSystem admin;
        private Store store;
        private Store store1;
        private Product product;

        @BeforeEach
        void setUp() {
            externalServiceManagement = mock(ExternalServiceManagement.class);
            admin = UserSystem.builder()
                    .userName("admin")
                    .password("admin")
                    .build();
            when(externalServiceManagement.getEncryptedPasswordAndSalt(admin.getPassword())).thenReturn(new PasswordSaltPair("hash","admin"));
            tradingSystem = new TradingSystem(externalServiceManagement, admin);
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
            doNothing().when(userSystem1).setOwnedStores(new HashSet<Store>());
            doNothing().when(userSystem1).setManagedStores(new HashSet<Store>());
            doNothing().when(userSystem).setOwnedStores(new HashSet<Store>());
            doNothing().when(userSystem).setManagedStores(new HashSet<Store>());
        }

        /**
         * checks if there is admin with username admin in the systen
         */
        @Test
        void isAdminPositive(){
            //success: returns true because there is admin in the system with username "admin"
            Assertions.assertTrue(tradingSystem.isAdmin("admin"));
        }

        /**
         * checks case of check about user that is not admin
         */
        @Test
        void isAdminNegative(){
            //fail: there is no user with this user name as admin
            Assertions.assertFalse(tradingSystem.isAdmin("nimda"));
        }

        /**
         * the following checks registration of valid user
         */
        @Test
        void registerNewUserPositive() {
            //mockup
            userToRegister = mock(UserSystem.class);
            when(userToRegister.getPassword()).thenReturn("");
            when(externalServiceManagement.getEncryptedPasswordAndSalt(userToRegister.getPassword()))
            .thenReturn(new PasswordSaltPair("pass","salt"));
            doNothing().when(userToRegister).setPassword("pass");
            doNothing().when(userToRegister).setSalt("salt");
            when(userToRegister.getUserName()).thenReturn("usernameTest");
            //setup
            //the following user details are necessary for the login tests
            //success: registration done. valid user details
            Assertions.assertTrue(tradingSystem.registerNewUser(userToRegister));
        }

        /**
         * checks handling with failure of registration
         * this test has to run after its respective positive test
         */
        @Test
        void registerNewUserNegative() {
            //registration with already registered user
            registerAsSetup(); //setup test of registration
            //fail: this user is already registered
            Assertions.assertFalse(tradingSystem.registerNewUser(userToRegister));
        }

        @Test
        void loginPositive(){
            //mockup
            when(userSystem.getUserName()).thenReturn("usernameTest");
            doNothing().when(userSystem).login();
            when(externalServiceManagement.isAuthenticatedUserPassword("passwordTest",userSystem))
                    .thenReturn(true);
            //the following register should register usernameTest as username
            // and passwordTest as password
            registerAsSetup();  //register user test as setup for login
            boolean ans = tradingSystem.login(userSystem,false,"passwordTest");
            Assertions.assertTrue(ans);
        }

        /**
         * test handling with login failure
         */
        @Test
        void loginNegative(){
            //mockup
            when(userSystem.getUserName()).thenReturn("usernameTest");
            doNothing().when(userSystem).login();
            boolean ans = tradingSystem.login(userSystem,false,"passwordTest");
            Assertions.assertFalse(ans);
        }

        /**
         * check the logout functionality of exists user in the system
         */
        @Test
        void logoutPositive() {
            //mockup
            when(userSystem.isLogin()).thenReturn(true);
            Assertions.assertTrue(tradingSystem.logout(userSystem));
        }

        /**
         * check handling with logout failure
         */
        @Test
        void logoutNegative() {
            //mockup
            when(userSystem.isLogin()).thenReturn(false);
            Assertions.assertFalse(tradingSystem.logout(userSystem));
        }

        /**
         * get one of the administrators in the system
          */
        @Test
        void getAdministratorUserPositive() {
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
            doNothing().when(store).setStoreId(1);
            when(store.getStoreId()).thenReturn(1);
            tradingSystem.insertStoreToStores(store);
            Assertions.assertEquals(tradingSystem.getStoreByAdmin("admin", 1),store);
        }

        /**
         * check the getStoreByAdmin() functionality in case of not exists admin in the system
         */
        @Test
        void getStoreByAdminNegative() {
            Assertions.assertThrows(NotAdministratorException.class, () -> {
                doNothing().when(store).setStoreId(1);
                tradingSystem.getStoreByAdmin("userSystem", 1);
            });
        }

        /**
         * check the getStore() functionality in case of exists store in the system
         */
        @Test
        void getStorePositive() {
            doNothing().when(store).setStoreId(1);
            when(store.getStoreId()).thenReturn(1);
            tradingSystem.insertStoreToStores(store);
            Assertions.assertEquals(tradingSystem.getStore(1),store);
        }

        /**
         * check the getStore() functionality in case of not exists store in the system
         */
        @Test
        void getStoreNegative() {
            Assertions.assertThrows(StoreDontExistsException.class, () -> {
                doNothing().when(store).setStoreId(1);
                when(store.getStoreId()).thenReturn(2);
                tradingSystem.insertStoreToStores(store);
                tradingSystem.getStore(1);
            });
        }

        /**
         * check the getUser() functionality in case of exists user in the system
         */
        @Test
        void getUserPositive() {
            // register "userToRegister" to the users list in trading system
            registerAsSetup();
            doNothing().when(userToRegister).setUserName("userToRegister");
            when(userToRegister.getUserName()).thenReturn("userToRegister");
            Assertions.assertEquals(tradingSystem.getUser("userToRegister"),userToRegister);
        }

        /**
         * check the getUser() functionality in case of not exists user in the system
         */
        @Test
        void getUserNegative() {
            // register "userToRegister" to the users list in trading system
            Assertions.assertThrows(UserDontExistInTheSystemException.class, () -> {
                registerAsSetup();
                doNothing().when(userToRegister).setUserName("userToRegister");
                when(userToRegister.getUserName()).thenReturn("userSystem");
                tradingSystem.getUser("userToRegister");
            });
        }

        /**
         * check the getUserByAdmin() functionality in case of exists admin in the system
         */
        @Test
        void getUserByAdminPositive() {
            registerAsSetup();
            doNothing().when(userToRegister).setUserName("userToRegister");
            when(userToRegister.getUserName()).thenReturn("userToRegister");
            Assertions.assertEquals(tradingSystem.getUserByAdmin("admin", "userToRegister"),userToRegister);
        }

        /**
         * check the getUserByAdmin() functionality in case of not exists admin in the system
         */
        @Test
        void getUserByAdminNegative(){
            Assertions.assertThrows(NotAdministratorException.class, () -> {
                registerAsSetup();
                doNothing().when(userToRegister).setUserName("userToRegister");
                tradingSystem.getUserByAdmin("userSystem", "userToRegister");
            });
        }


        /**
         * check the addMangerToStore() functionality in case of success in addNewManageStore and addManager
         */
        @Test
        void addMangerToStorePositive() {
            // userSystem <==> ownerUser
            // userSystem1 <==> newManagerUser
            when(store.addManager(userSystem,userSystem1)).thenReturn(true);
            when(userSystem1.addNewManageStore(store)).thenReturn(true);
            Assertions.assertTrue(tradingSystem.addMangerToStore(store, userSystem, userSystem1));
        }

        /**
         * check the addMangerToStore() functionality in case of not initialized parameters.
         */
        @Test
        void addMangerToStoreNullParams() {
            Store storeNull = mock(Store.class);
            UserSystem userSystemNull1 = mock(UserSystem.class);
            UserSystem userSystemNull2 = mock(UserSystem.class);
            Assertions.assertFalse(tradingSystem.addMangerToStore(storeNull, userSystemNull1, userSystemNull2));
        }

        /**
         * check the addMangerToStore() functionality in case of failure in addNewManageStore and addManager
         */
        @Test
        void addMangerToStoreNegative() {
            // userSystem <==> ownerUser
            // userSystem1 <==> newManagerUser
            when(store.addManager(userSystem,userSystem1)).thenReturn(false);
            when(userSystem1.addNewManageStore(store)).thenReturn(false);
            Assertions.assertFalse(tradingSystem.addMangerToStore(store, userSystem, userSystem1));
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
            Assertions.assertTrue(tradingSystem.addOwnerToStore(store, userSystem, userSystem1));
        }

        /**
         * check the addOwnerToStore() functionality in case of not initialized parameters.
         */
        @Test
        void addOwnerToStoreNullParams() {
            // userSystem <==> ownerUser
            // userSystem1 <==> newManagerUser
            when(store.addOwner(userSystem,userSystem1)).thenReturn(true);
            when(userSystem1.addNewOwnedStore(store)).thenReturn(true);
            Assertions.assertTrue(tradingSystem.addOwnerToStore(store, userSystem, userSystem1));
        }

        /**
         * check the addOwnerToStore() functionality in case of failure in addNewOwnedStore and addOwner
         */
        @Test
        void addOwnerToStoreNegative() {
            // userSystem <==> ownerUser
            // userSystem1 <==> newManagerUser
            when(store.addOwner(userSystem,userSystem1)).thenReturn(false);
            when(userSystem1.addNewOwnedStore(store)).thenReturn(false);
            Assertions.assertFalse(tradingSystem.addOwnerToStore(store, userSystem, userSystem1));
        }



        /**
         * check the searchProductByName() functionality in case of exists product in store in the system
         */
        @Test
        void searchProductByNamePositive() {
            doNothing().when(store).setStoreId(1);
            when(store.getStoreId()).thenReturn(1);
            tradingSystem.insertStoreToStores(store);
            doNothing().when(product).setName("dollhouse");
            HashSet<Product> products = new HashSet<Product>();
            products.add(product);
            when(store.searchProductByName("dollhouse")).thenReturn(products);
            // converted both to arrays because one ahd ArrayList type and the other has Set<Product> type
            Assertions.assertArrayEquals(tradingSystem.searchProductByName("dollhouse").toArray(),products.toArray());
        }

        /**
         * check the searchProductByName() functionality in case of not exists product in store in the system
         */
        @Test
        void searchProductByNameNegative() {
            doNothing().when(store).setStoreId(1);
            when(store.getStoreId()).thenReturn(1);
            tradingSystem.insertStoreToStores(store);
            doNothing().when(product).setName("dollhouse");
            HashSet<Product> products = new HashSet<Product>();
            products.add(product);
            when(store.searchProductByName("dollhouse")).thenReturn(new HashSet<>());
            // The disjoint method returns true if its two arguments have no elements in common.
            Assertions.assertTrue(Collections.disjoint(tradingSystem.searchProductByName("dollhouse"), products));
        }

        /**
         * check the searchProductByCategory() functionality in case of exists product in store in the system
         */
        @Test
        void searchProductByCategoryPositive() {
            doNothing().when(store).setStoreId(1);
            when(store.getStoreId()).thenReturn(1);
            tradingSystem.insertStoreToStores(store);
            doNothing().when(product).setCategory(ProductCategory.TOYS_HOBBIES);
            HashSet<Product> products = new HashSet<Product>();
            products.add(product);
            when(store.searchProductByCategory(ProductCategory.TOYS_HOBBIES)).thenReturn(products);
            // converted both to arrays because one ahd ArrayList type and the other has Set<Product> type
            Assertions.assertArrayEquals(tradingSystem.searchProductByCategory(ProductCategory.TOYS_HOBBIES).toArray(),products.toArray());
        }

        /**
         * check the searchProductByCategory() functionality in case of not exists product in store in the system
         */
        @Test
        void searchProductByCategoryNegative() {
            doNothing().when(store).setStoreId(1);
            when(store.getStoreId()).thenReturn(1);
            tradingSystem.insertStoreToStores(store);
            doNothing().when(product).setCategory(ProductCategory.TOYS_HOBBIES);
            HashSet<Product> products = new HashSet<Product>();
            products.add(product);
            when(store.searchProductByCategory(ProductCategory.TOYS_HOBBIES)).thenReturn(new HashSet<>());
            // The disjoint method returns true if its two arguments have no elements in common.
            Assertions.assertTrue(Collections.disjoint(tradingSystem.searchProductByCategory(ProductCategory.TOYS_HOBBIES), products));
        }

        /**
         * check the searchProductByKeyWords() functionality in case of exists product in store in the system
         */
        @Test
        void searchProductByKeyWordsPositive() {
            doNothing().when(store).setStoreId(1);
            when(store.getStoreId()).thenReturn(1);
            tradingSystem.insertStoreToStores(store);
            doNothing().when(product).setName("dollhouse");
            List<String> keyWords = new ArrayList<String>();
            keyWords.add("doll");
            keyWords.add("house");
            HashSet<Product> products = new HashSet<Product>();
            products.add(product);
            when(store.searchProductByKeyWords(keyWords)).thenReturn(products);
            // converted both to arrays because one ahd ArrayList type and the other has Set<Product> type
            Assertions.assertArrayEquals(tradingSystem.searchProductByKeyWords(keyWords).toArray(),products.toArray());
        }

        /**
         * check the searchProductByKeyWords() functionality in case of not exists product in store in the system
         */
        @Test
        void searchProductByKeyWordsNegative() {
            doNothing().when(store).setStoreId(1);
            when(store.getStoreId()).thenReturn(1);
            tradingSystem.insertStoreToStores(store);
            doNothing().when(product).setName("dollhouse");
            List<String> keyWords = new ArrayList<String>();
            keyWords.add("elephant");
            keyWords.add("pig");
            HashSet<Product> products = new HashSet<Product>();
            products.add(product);
            when(store.searchProductByKeyWords(keyWords)).thenReturn(new HashSet<>());
            // The disjoint method returns true if its two arguments have no elements in common.
            Assertions.assertTrue(Collections.disjoint(tradingSystem.searchProductByKeyWords(keyWords), products));
        }

        // TODO - RON = ADD COMMENTS
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

        // TODO - RON = ADD COMMENTS
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

        // TODO - RON = ADD COMMENTS
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

        // TODO - RON = ADD COMMENTS
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
         * This test check if the purchaseShoppingCart method succeeds when the parameters
         * are correct.
         */
        @Test
        void guestPurchaseShoppingCartPositive() {

        }

        /**
         * This test check if the purchaseShoppingCart method fails when the parameters
         * are wrong.
         */
        @Test
        void guestPurchaseShoppingCartNegative() {
        }

        /**
         * This test check if the purchaseShoppingCart method succeeds when the parameters
         * are correct.
         */
        @Test
        void registeredPurchaseShoppingCartPositive() {
        }

        /**
         * This test check if the purchaseShoppingCart method fails when the parameters
         * are wrong.
         */
        @Test
        void registeredPurchaseShoppingCartNegative() {
        }

        // TODO - KSENIA = go over mocks
        /**
         * This test checks that the store's opening succeeds
         */
        @Test
        void openStoreSuccess() {
            setUpUsersForOpenStoreTestSuc();
            //before opening new store there are 0 stores in the system
            Assertions.assertEquals(0, tradingSystem.getStoresList().size());
            tradingSystem.openStore(userSystem2, new PurchasePolicy(), new DiscountPolicy(), "castro");
            //after opening 1 store there need to be one store in system
            Assertions.assertEquals(1, tradingSystem.getStoresList().size());
            //check the store named "castro" saved in the system.
            Store openStore = tradingSystem.getStoresList().stream().filter(store2 -> store2.getStoreName().equals("castro")).findFirst().get();
            Assertions.assertNotNull(openStore);
           }

        // TODO - KSENIA = go over mocks
        /**
         * This test checks that the store's opening fails
         */
        @Test
        void openStoreFail(){
            setUpUsersForOpenStoreTestFail();
            //before opening new store there are 1 (added in setUp) stores in the system
            Assertions.assertEquals(1, tradingSystem.getStoresList().size());
            // the user is not registered
            Assertions.assertFalse(tradingSystem.openStore(new UserSystem(), new PurchasePolicy(), new DiscountPolicy(), "Castro"));
            //check that after fail open store the number of stores in the system is still 1
            Assertions.assertEquals(1, tradingSystem.getStoresList().size());
            //can't have a null parameter
            Assertions.assertFalse(tradingSystem.openStore(userSystem2, null, new DiscountPolicy(), "Castro"));
            //can't open a store that is already exists
            Assertions.assertFalse(tradingSystem.openStore(userSystem2,store1.getPurchasePolicy(),store1.getDiscountPolicy(),store1.getStoreName()));
        }

        // ********************************** Set Up Functions For Tests ********************************** //

        private void setUpPurchase(){
            ShoppingCart shoppingCart1 = mock(ShoppingCart.class);
            ShoppingCart shoppingCart2 = mock(ShoppingCart.class);
            PaymentDetails paymentDetails = mock(PaymentDetails.class);
            BillingAddress billingAddress = mock(BillingAddress.class);
            ShoppingBag shoppingBag = mock(ShoppingBag.class);
            Store store = mock(Store.class);
            List<Integer> storeList = new LinkedList<>();
            Map<Product,Integer> products = new HashMap<>();
            storeList.add(1);
            when(externalServiceManagement.charge(paymentDetails,shoppingCart1)).thenReturn(storeList);
            when(shoppingCart1.getShoppingBag(store)).thenReturn(shoppingBag);
            when(shoppingBag.getTotalCostOfBag()).thenReturn(50.25);
            tradingSystem.insertStoreToStores(store);
            when(store.getStoreId()).thenReturn(1);
            when(shoppingBag.getProductListFromStore()).thenReturn(products);
            when(shoppingCart1.removeBagFromCart(store,shoppingBag)).thenReturn(true);
            when(new ShoppingCart()).thenReturn(shoppingCart2);
            when(shoppingCart2.addBagToCart(store, shoppingBag)).thenReturn(true);
            when(externalServiceManagement.deliver(billingAddress,shoppingCart2)).thenReturn(true);
        }
        /**
         * sets up users for openStore test
         */
        private void setUpUsersForOpenStoreTestSuc(){
            Set<UserSystem> users = new HashSet<>();
            users.add(userSystem2);
            tradingSystem.setUsersList(users);
        }

        /**
         * sets up store for openStore test
         */
        private void setUpUsersForOpenStoreTestFail(){
            when(store1.getStoreName()).thenReturn("castro");
            when(store1.getDiscountPolicy()).thenReturn(new DiscountPolicy());
            when(store1.getPurchasePolicy()).thenReturn(new PurchasePolicy());
            tradingSystem.insertStoreToStores(store1);
        }

        /**
         * setup of successful pre registration
         */
        private void registerAsSetup(){
            //mockup
            userToRegister = mock(UserSystem.class);
            when(userToRegister.getPassword()).thenReturn("");
            when(externalServiceManagement.getEncryptedPasswordAndSalt(userToRegister.getPassword()))
                    .thenReturn(new PasswordSaltPair("pass","salt"));
            doNothing().when(userToRegister).setPassword("pass");
            doNothing().when(userToRegister).setSalt("salt");
            when(userToRegister.getUserName()).thenReturn("usernameTest");
            //setup
            //the following user details are necessary for the login tests
            tradingSystem.registerNewUser(userToRegister);
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
        private UserSystem userToRegister;
        private UserSystem userToOpenStore;
        private Store store;
        private Store storeToOpen;
        private Product product;
        private UserSystem admin;
        private FactoryObjects factoryObjects;
        private ShoppingCart shoppingCart;
        private Product testProduct;
        private ShoppingBag shoppingBag;
        private PaymentDetails paymentDetails;
        private BillingAddress billingAddress;

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
            ExternalServiceManagement externalServiceManagement = new ExternalServiceManagement();
            tradingSystem = new TradingSystem(externalServiceManagement, admin);
            PasswordSaltPair psp = externalServiceManagement.getEncryptedPasswordAndSalt(admin.getPassword());
            admin.setPassword(psp.getHashedPassword());
            admin.setSalt(psp.getSalt());
            factoryObjects = new FactoryObjects();
        }

        /**
         * the following checks registration of valid user
         */
        @Test
        void registerNewUser() {
            //the following user details are necessary for the login tests
            Assertions.assertTrue(tradingSystem.registerNewUser(userToRegister));
        }

        /**
         * checks handling with failure of registration
         * this test has to run after its respective positive test
         */
        @Test
        void registerNewUserNegative() {
            //registration with already registered user
            registerAsSetup(); //first registration
            Assertions.assertFalse(tradingSystem.registerNewUser(userToRegister));  //second registration
        }

        //TODO - KSENIA = ADD COMMENTS
        @Test
        void login() {
            //check login of regular user
            String password = "test login";
            UserSystem user = UserSystem.builder()
                    .userName("test login")
                    .password(password)
                    .build();
            tradingSystem.registerNewUser(user);
            tradingSystem.login(user, false, password);
            //check login of admin
            //TODO
        }

        //TODO - KSENIA = ADD COMMENTS
        @Test
        void loginRegularUser(){
            //the following register should register usernameTest as username
            // and passwordTest as password
            registerAsSetup();  //register user test as setup for login
            boolean ans = tradingSystem.login(userToRegister,false,"passwordTest");
            Assertions.assertTrue(ans);
        }

        /**
         * test handling with login failure
         */
        @Test
        void loginRegularUserNegative(){
            String username = "username";
            String password = "password";
            String fName = "mati";
            String lName = "Tut";
            UserSystem user = new UserSystem(username,fName,lName,password);
            user.setSalt("salt");
            Assertions.assertFalse(tradingSystem.login(user,false,"password"));
        }

        /**
         * check the logout functionality of exists user in the system
         */
        @Test
        void logout() {
            //setup of login for the logout
            String password = "Moti";
            UserSystem user = UserSystem.builder()
                    .userName("usernameTest")
                    .password(password)
                    .firstName("Banana")
                    .lastName("passwordTest").build();
            tradingSystem.registerNewUser(user);
            tradingSystem.login(user, false, password);
            Assertions.assertTrue(tradingSystem.logout(user));
        }

        /**
         * check handling with logout failure
         */
        @Test
        void logoutNegative() {
            Assertions.assertFalse(tradingSystem.logout(userToRegister));
        }

        @Test
        void getAdministratorUser() {
        }

        /**
         * check the getStoreByAdmin() functionality in case of exists admin in the system
         */
        @Test
        void getStoreByAdminPositive() {
            tradingSystem.insertStoreToStores(store);
            Assertions.assertEquals(tradingSystem.getStoreByAdmin("admin", 1),store);
        }

        /**
         * check the getStoreByAdmin() functionality in case of not exists admin in the system
         */
        @Test
        void getStoreByAdminNegative() {
            Assertions.assertThrows(NotAdministratorException.class, () -> {
                tradingSystem.insertStoreToStores(store);
                tradingSystem.getStoreByAdmin("userSystem", 1);
            });
        }

        /**
         * check the getStore() functionality in case of exists store in the system
         */
        @Test
        void getStorePositive() {
            tradingSystem.insertStoreToStores(store);
            Assertions.assertEquals(tradingSystem.getStore(1),store);
        }

        /**
         * check the getStore() functionality in case of not exists store in the system
         */
        @Test
        void getStoreNegative() {
            Assertions.assertThrows(StoreDontExistsException.class, () -> {
                tradingSystem.insertStoreToStores(store);
                tradingSystem.getStore(2);
            });
        }

        /**
         * check the getUser() functionality in case of exists user in the system
         */
        @Test
        void getUserPositive() {
            // register "userToRegister" to the users list in trading system
            registerAsSetup();
            Assertions.assertEquals(tradingSystem.getUser("usernameTest"),userToRegister);
        }

        /**
         * check the getUser() functionality in case of not exists user in the system
         */
        @Test
        void getUserNegative() {
            // register "userToRegister" to the users list in trading system
            Assertions.assertThrows(UserDontExistInTheSystemException.class, () -> {
                registerAsSetup();
                tradingSystem.getUser("userToRegister");
            });
        }

        /**
         * check the getUserByAdmin() functionality in case of exists admin in the system
         */
        @Test
        void getUserByAdminPositive() {
            registerAsSetup();
            Assertions.assertEquals(tradingSystem.getUserByAdmin("admin", "usernameTest"),userToRegister);
        }

        /**
         * check the getUserByAdmin() functionality in case of not exists admin in the system
         */
        @Test
        void getUserByAdminNegative(){
            Assertions.assertThrows(NotAdministratorException.class, () -> {
                registerAsSetup();
                tradingSystem.getUserByAdmin("userSystem", "usernameTest");
            });
        }

        /**
         * check the searchProductByName() functionality in case of exists product in store in the system
         */
        @Test
        void searchProductByNamePositive() {
            tradingSystem.insertStoreToStores(store);
            HashSet<Product> products = new HashSet<Product>();
            products.add(product);
            HashSet<UserSystem> owners = new HashSet<UserSystem>();
            owners.add(userToRegister);
            store.setOwners(owners);
            store.addNewProduct(userToRegister, product);
            // converted both to arrays because one ahd ArrayList type and the other has Set<Product> type
            Assertions.assertArrayEquals(tradingSystem.searchProductByName("dollhouse").toArray(),products.toArray());
        }

        /**
         * check the searchProductByName() functionality in case of not exists product in store in the system
         */
        @Test
        void searchProductByNameNegative() {
            tradingSystem.insertStoreToStores(store);
            HashSet<Product> products = new HashSet<Product>();
            products.add(product);
            HashSet<UserSystem> owners = new HashSet<UserSystem>();
            owners.add(userToRegister);
            store.setOwners(owners);
            store.addNewProduct(userToRegister, product);
            // The disjoint method returns true if its two arguments have no elements in common.
            Assertions.assertTrue(Collections.disjoint(tradingSystem.searchProductByName("puppy"), products));
        }

        /**
         * check the searchProductByCategory() functionality in case of exists product in store in the system
         */
        @Test
        void searchProductByCategoryPositive() {
            tradingSystem.insertStoreToStores(store);
            HashSet<Product> products = new HashSet<Product>();
            products.add(product);
            HashSet<UserSystem> owners = new HashSet<UserSystem>();
            owners.add(userToRegister);
            store.setOwners(owners);
            store.addNewProduct(userToRegister, product);
            // converted both to arrays because one ahd ArrayList type and the other has Set<Product> type
            Assertions.assertArrayEquals(tradingSystem.searchProductByCategory(ProductCategory.TOYS_HOBBIES).toArray(),products.toArray());
        }

        /**
         * check the searchProductByCategory() functionality in case of not exists product in store in the system
         */
        @Test
        void searchProductByCategoryNegative() {
            tradingSystem.insertStoreToStores(store);
            HashSet<Product> products = new HashSet<Product>();
            products.add(product);
            product.setCategory(ProductCategory.BOOKS_MOVIES_MUSIC);
            HashSet<UserSystem> owners = new HashSet<UserSystem>();
            owners.add(userToRegister);
            store.setOwners(owners);
            store.addNewProduct(userToRegister, product);
            // The disjoint method returns true if its two arguments have no elements in common.
            Assertions.assertTrue(Collections.disjoint(tradingSystem.searchProductByCategory(ProductCategory.TOYS_HOBBIES), products));
        }

        /**
         * check the searchProductByKeyWords() functionality in case of exists product in store in the system
         */
        @Test
        void searchProductByKeyWordsPositive() {
            tradingSystem.insertStoreToStores(store);
            HashSet<Product> products = new HashSet<Product>();
            products.add(product);
            HashSet<UserSystem> owners = new HashSet<UserSystem>();
            owners.add(userToRegister);
            store.setOwners(owners);
            store.addNewProduct(userToRegister, product);
            List<String> keyWords = new ArrayList<String>();
            keyWords.add("doll");
            keyWords.add("house");
            // converted both to arrays because one ahd ArrayList type and the other has Set<Product> type
            Assertions.assertArrayEquals(tradingSystem.searchProductByKeyWords(keyWords).toArray(),products.toArray());
        }

        /**
         * check the searchProductByKeyWords() functionality in case of not exists product in store in the system
         */
        @Test
        void searchProductByKeyWordsNegative() {
            tradingSystem.insertStoreToStores(store);
            HashSet<Product> products = new HashSet<Product>();
            products.add(product);
            HashSet<UserSystem> owners = new HashSet<UserSystem>();
            owners.add(userToRegister);
            store.setOwners(owners);
            store.addNewProduct(userToRegister, product);
            List<String> keyWords = new ArrayList<String>();
            keyWords.add("elephant");
            keyWords.add("pig");
            // The disjoint method returns true if its two arguments have no elements in common.
            Assertions.assertTrue(Collections.disjoint(tradingSystem.searchProductByKeyWords(keyWords), products));
        }

        // TODO - RON = ADD COMMENTS
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

        // TODO - RON = ADD COMMENTS
        @Test
        void filterByStoreRank() {
            //initial
            List<Product> products = setUpProductsForFilterTests();
            List<Store> stores = (setUpStoresForFilterTests(products));
            Set<Store> storesSet = new HashSet<>((stores));
            /*UserSystem admin = UserSystem.builder()
                    .userName("admin")
                    .password("admin")
                    .build();*/
            tradingSystem = new TradingSystem(new ExternalServiceManagement(), storesSet, admin);

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

        // TODO - RON = ADD COMMENTS
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

        @Test
        void openStoreSuccess(){
            setUpForOpenStoreSuc();
            //before open store there is no stores in the system
            Assertions.assertEquals(0, tradingSystem.getStoresList().size());
            //check that the store opened
            Assertions.assertTrue(tradingSystem.openStore(userToOpenStore, new PurchasePolicy(), new DiscountPolicy(), "castro"));
            //after opening 1 store there needs to be a store in the system
            Assertions.assertEquals(1, tradingSystem.getStoresList().size());
        }

        @Test
        void openStoreFail(){
            setUpForOpenStoreFail();
            //before open store there is 1 (added in setUp) store in the system
            Assertions.assertEquals(1, tradingSystem.getStoresList().size());
            //fail to open an existing store
            Assertions.assertFalse(tradingSystem.openStore(userToOpenStore, storeToOpen.getPurchasePolicy(), storeToOpen.getDiscountPolicy(), storeToOpen.getStoreName()));
            //after fail opening there is still  1 store in the system
            Assertions.assertEquals(1, tradingSystem.getStoresList().size());
        }

        @Test
        void registeredPurchaseShoppingCartSuccess() {
            setUpForPurchaseCart();
            List <Receipt> receipts = tradingSystem.purchaseShoppingCart(paymentDetails, billingAddress, userToOpenStore);
            //check that the receipts is not null
            Assertions.assertNotNull(receipts);
            Receipt receivedReceipt = receipts.iterator().next();
            //check that store id match
            Assertions.assertEquals(storeToOpen.getStoreId(), receivedReceipt.getStoreId());
            //check that the user name, who bought the product, match
            Assertions.assertEquals(userToOpenStore.getUserName(), receivedReceipt.getUserName());
            //check that the cost match
            Assertions.assertEquals(testProduct.getCost()*3, receivedReceipt.getAmountToPay());
            //check that the product name match
            Assertions.assertEquals(testProduct.getName(), receivedReceipt.getProductsBought().keySet().iterator().next().getName());
        }

        @Test
        void registeredPurchaseShoppingCartFail() {
        }

        // ************************************ Set Up For Tests ************************************ //

        /**
         * set up of successful pre registration
         */
        private void registerAsSetup(){
            tradingSystem.registerNewUser(userToRegister);
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

        private void setUpForOpenStoreSuc(){
            userToOpenStore = getUserSystemBuild();
            Set<UserSystem> users = new HashSet<>();
            users.add(userToOpenStore);
            tradingSystem.setUsersList(users);
        }

        private void setUpForOpenStoreFail(){
            userToOpenStore = getUserSystemBuild();
            Set<UserSystem> users = new HashSet<>();
            users.add(userToOpenStore);
            tradingSystem.setUsersList(users);
            storeToOpen = new Store(userToOpenStore,new PurchasePolicy(), new DiscountPolicy(), "castro");
            tradingSystem.insertStoreToStores(storeToOpen);
        }

        private void setUpForPurchaseCart(){
            userToOpenStore = getUserSystemBuild();
            shoppingCart = new ShoppingCart();
            storeToOpen = Store.builder()
                            .purchasePolicy(new PurchasePolicy())
                            .discountPolicy(new DiscountPolicy())
                            .storeName("MovieStore").build();
                    //new Store(userToOpenStore,new PurchasePolicy(), new DiscountPolicy(), "MoveStore");
            tradingSystem.insertStoreToStores(storeToOpen);
            testProduct = Product.builder()
                            .name("Harry-Potter")
                            .category(ProductCategory.BOOKS_MOVIES_MUSIC)
                            .amount(45)
                            .cost(12.9)
                            .storeId(storeToOpen.getStoreId()).build();
           // shoppingBag = new ShoppingBag(storeToOpen);
           // shoppingBag.addProductToBag(testProduct,3);
           // shoppingCart.addBagToCart(storeToOpen, shoppingBag);
            userToOpenStore.saveProductInShoppingBag(storeToOpen,testProduct,3);
            paymentDetails = new PaymentDetails(CardAction.PAY, "123456789", "12", "2024", "Israel Israeli", 237, "333333339");
            billingAddress = new BillingAddress("Israel Israeli", "Ben-Gurion 1", "Beer Sheva", "Israel","1234567");
        }

        private void setUpForPurchaseCartFail(){

        }

        private UserSystem getUserSystemBuild(){
            return UserSystem.builder()
                    .userName("coolIsrael")
                    .password("Isra123")
                    .firstName("Israel")
                    .lastName("Israeli").build();
        }
    }
}
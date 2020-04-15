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
        private UserSystem userToRegister;
        private FactoryObjects factoryObjects;
        private UserSystem admin;
        private Store store;
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
            factoryObjects = new FactoryObjects();
            String username = "usernameTest";
            String password = "passwordTest";
            String fName = "moti";
            String lName = "Banana";
            userToRegister = new UserSystem(username,fName,lName,password);
            store = mock(Store.class);
            product = mock(Product.class);
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

        @Test
        void buyShoppingCart() {
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
            Assertions.assertFalse(tradingSystem.registerNewUser(userToRegister));
        }


        @Test
        void login() {
            //check login of regular user
            loginRegularUserPositive();
            //check login of admin
            //TODO
        }

        @Test
        void loginRegularUserPositive(){
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
        void loginRegularUserNegative(){
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

        @Test
        void purchaseShoppingCart() {
        }

        @Test
        void testPurchaseShoppingCart() {
        }

        // TODO - KSENIA = go over mocks
        /**
         * This test checks that the store's opening succeeds
         */
        @Test
        void openStoreSuccess() {
            /*Set<UserSystem> testUsersList = setUpUsersForOpenStoreTest();
            //number of stores in the system before addition
            int numOfStoreInSystemBefore = tradingSystem.getStoresList().size();
            //all the parameters are correct, the method should return true
            Assertions.assertTrue(tradingSystem.openStore((UserSystem)testUsersList.toArray()[0], DiscountType.OPEN_DISCOUNT, PurchaseType.BUY_IMMEDIATELY, new PurchasePolicy(),
                    new DiscountPolicy(), "Castro"));
            //number of stores in the system after addition
            int numOfStoreInSystemAfter = tradingSystem.getStoresList().size();
            // numOfStoreInSystemBefore +1 == numOfStoreInSystemAfter
            Assertions.assertFalse(numOfStoreInSystemBefore == numOfStoreInSystemAfter);
            //all the parameters are correct, the method should return true
            Assertions.assertTrue(tradingSystem.openStore((UserSystem)testUsersList.toArray()[1], DiscountType.OPEN_DISCOUNT, PurchaseType.BUY_IMMEDIATELY, new PurchasePolicy(),
                    new DiscountPolicy(), "Fox"));
            // number of stores in the system should be 2
            Assertions.assertTrue(tradingSystem.getStoresList().size() == 2);
            // check if there is a store named 'Castro' in the system
            Assertions.assertTrue(tradingSystem.getStoresList().stream()
                    .anyMatch(store -> store.getStoreName().equals("Castro")));*/
        }

        // TODO - KSENIA = go over mocks
        /**
         * This test checks that the store's opening fails
         */
        @Test
        void openStoreFail(){
            int numOfStoreInSystemBefore = tradingSystem.getStoresList().size();
            // the user is not registered
            Assertions.assertFalse(tradingSystem.openStore(new UserSystem(), DiscountType.OPEN_DISCOUNT, PurchaseType.BUY_IMMEDIATELY,
                    new PurchasePolicy(), new DiscountPolicy(), "Castro"));
            int numOfStoreInSystemAfter = tradingSystem.getStoresList().size();
            // check that the store named "Castro" wasn't added to the store list
            Assertions.assertTrue(numOfStoreInSystemBefore == numOfStoreInSystemAfter);
        }

        // ********************************** Set Up Functions For Tests ********************************** //

        /**
         * sets up users for openStore test
         * @return a list of users
         */
        private Set<UserSystem> setUpUsersForOpenStoreTest(){
            UserSystem testUser1 = new UserSystem("DaniDin", "Dani", "Din", "123dsa");
            UserSystem testUser2 = new UserSystem("YuvalMevulbal", "Yuval", "Mevulbal", "456hgf");
            Set<UserSystem> users = new HashSet<>();
            users.add(testUser1);
            users.add(testUser2);
            tradingSystem.setUsersList(users);
            return users;
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

        //@Autowired // TODO - RON - TAKE CARE OF IT
        ExternalServiceManagement externalServiceManagement;
        private TradingSystem tradingSystem;
        private UserSystem userToRegister;
        private Store store;
        private Product product;
        private UserSystem admin;
        private FactoryObjects factoryObjects;

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
            externalServiceManagement = new ExternalServiceManagement();
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
        void purchaseShoppingCart() {
        }

        @Test
        void testPurchaseShoppingCart() {
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
    }
}
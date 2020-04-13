package com.wsep202.TradingSystem.domain.trading_system_management;

import Externals.PasswordSaltPair;
import com.github.rozidan.springboot.modelmapper.WithModelMapper;
import com.wsep202.TradingSystem.domain.config.TradingSystemConfiguration;
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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class TradingSystemTest {



    @AfterEach
    void tearDown() {
    }

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

        }

        //TODO create the test after we will know the identity of the admin name
        //TODO and after creation of register Admin tests
        @Test
        void isAdmin(){

        }
        @Test
        void buyShoppingCart() {
        }

        /**
         * the following checks registration of valid user
         */
        @Test
        void registerNewUser() {
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
            registerNewUser(); //setup test of registration
            Assertions.assertFalse(tradingSystem.registerNewUser(userToRegister));
        }

    /*  the following tested method is private
        @Test
        void isRegisteredUser(){
            when(userSystem.getUserName()).thenReturn("usernameTest");
            Assertions.assertTrue(tradingSystem.isRegisteredUser(userSystem));
        }
        */
        @Test
        void login() {
            //check login of regular user
            loginRegularUser();
            //check login of admin
            //TODO
        }
        @Test
        void loginRegularUser(){
            //mockup
            when(userSystem.getUserName()).thenReturn("usernameTest");
            doNothing().when(userSystem).login();
            when(externalServiceManagement.isAuthenticatedUserPassword("passwordTest",userSystem))
                    .thenReturn(true);
            //the following register should register usernameTest as username
            // and passwordTest as password
            registerNewUser();  //register user test as setup for login
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
        void logout() {
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

        //TODO after we will know how to register we'll test it and the we test the getter bellow
        @Test
        void getAdministratorUser() {
        }

        @Test
        void getStore() {
        }

        @Test
        void testGetStore() {
        }

        @Test
        void getUser() {
        }

        @Test
        void getUserByAdmin() {
        }

        @Test
        void searchProductByName() {
        }

        @Test
        void searchProductByCategory() {
        }

        @Test
        void searchProductByKeyWords() {
        }

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

    @Test
    void openStore() {
    }

    /////////////////////////////////////////setups functions for tests /////////////////////////

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

///////////////////////////////////////////////////////////////////////////////

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

    @MockBean // for pass compilation
    private ModelMapper modelMapper;

    @BeforeEach
    void setUp() {
        String username = "usernameTest";
        String password = "passwordTest";
        String fName = "moti";
        String lName = "Banana";
        userToRegister = new UserSystem(username,fName,lName,password);
    }

    @Test
    void buyShoppingCart() {
    }

    /**
     * the following checks registration of valid user
     */
    @Test
    void registerNewUser() {
        UserSystem userToRegistration = UserSystem.builder()
                .userName("test register!")
                .password("test register")
                .build();
        //setup
        //the following user details are necessary for the login tests
        Assertions.assertTrue(tradingSystem.registerNewUser(userToRegistration));
    }

    /**
     * checks handling with failure of registration
     * this test has to run after its respective positive test
     */
    @Test
    void registerNewUserNegative() {
        //registration with already registered user
        UserSystem userToRegistration = UserSystem.builder()
                .userName("registerNewUserNegative")
                .password("registerNewUserNegative")
                .build();
        Assertions.assertTrue(tradingSystem.registerNewUser(userToRegistration)); //setup
        Assertions.assertFalse(tradingSystem.registerNewUser(userToRegistration));
    }

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
//    @Test
//    void loginRegularUser(){
//        //the following register should register usernameTest as username
//        // and passwordTest as password
//        registerNewUser();  //register user test as setup for login
//        boolean ans = tradingSystem.login(userToRegister,false,"passwordTest");
//        Assertions.assertTrue(ans);
//    }

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

    @Test
    void getStore() {
    }

    @Test
    void testGetStore() {
    }

    @Test
    void getUser() {
    }

    @Test
    void getUserByAdmin() {
    }

    @Test
    void searchProductByName() {
    }

    @Test
    void searchProductByCategory() {
    }

    @Test
    void searchProductByKeyWords() {
    }

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

    @Test
    void filterByStoreRank() {
        //initial
        List<Product> products = setUpProductsForFilterTests();
        List<Store> stores = (setUpStoresForFilterTests(products));
        Set<Store> storesSet = new HashSet<>((stores));
        UserSystem admin = UserSystem.builder()
                .userName("admin")
                .password("admin")
                .build();
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

    @Test
    void openStore() {
    }

    /////////////////////////////setup for tests //////////////////////////
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
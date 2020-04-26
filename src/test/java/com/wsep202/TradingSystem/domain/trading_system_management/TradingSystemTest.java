package com.wsep202.TradingSystem.domain.trading_system_management;


//import Externals.PasswordSaltPair;
import com.wsep202.TradingSystem.config.TradingSystemConfiguration;
import com.wsep202.TradingSystem.domain.exception.NotAdministratorException;
import com.wsep202.TradingSystem.domain.exception.StoreDontExistsException;
import com.wsep202.TradingSystem.domain.exception.UserDontExistInTheSystemException;
import com.wsep202.TradingSystem.domain.factory.FactoryObjects;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
        private PaymentDetails paymentDetails;
        private BillingAddress billingAddress;
        private ShoppingCart testshoppingCart;
        private ShoppingBag testShoppingBag;
        private Product testProduct;

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
            when(userToRegister.getPassword()).thenReturn("12");
            when(externalServiceManagement.getEncryptedPasswordAndSalt(userToRegister.getPassword()))
            .thenReturn(new PasswordSaltPair("pass","salt"));
            doNothing().when(userToRegister).setPassword("pass");
            doNothing().when(userToRegister).setSalt("salt");
            when(userToRegister.getUserName()).thenReturn("usernameTest");
            when(userToRegister.getFirstName()).thenReturn("usersFirstName");
            when(userToRegister.getLastName()).thenReturn("usersLastName");
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

        /**
         * This test check if the login method succeeds when the parameters
         * are correct.
         */
        @Test
        void loginPositive(){
            setUpLogin();
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
            Assertions.assertEquals(tradingSystem.getStoreByAdmin("admin", 1, uuid),store);
        }

        /**
         * check the getStoreByAdmin() functionality in case of not exists admin in the system
         */
        @Test
        void getStoreByAdminNegative() {
            Assertions.assertThrows(NotAdministratorException.class, () -> {
                doNothing().when(store).setStoreId(1);
                tradingSystem.getStoreByAdmin("userSystem", 1, uuid);
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
            Assertions.assertEquals(tradingSystem.getUser("userToRegister", uuid),userToRegister);
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
                tradingSystem.getUser("userToRegister", uuid);
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
            Assertions.assertEquals(tradingSystem.getUserByAdmin("admin", "userToRegister", uuid),userToRegister);
        }

        /**
         * check the getUserByAdmin() functionality in case of not exists admin in the system
         */
        @Test
        void getUserByAdminNegative(){
            Assertions.assertThrows(NotAdministratorException.class, () -> {
                registerAsSetup();
                doNothing().when(userToRegister).setUserName("userToRegister");
                tradingSystem.getUserByAdmin("userSystem", "userToRegister", uuid);
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
            setUpForPurchase();
          //  List<Receipt> receipts = tradingSystem.purchaseShoppingCart(paymentDetails,billingAddress,userSystem2);

        }

        /**
         * This test check if the purchaseShoppingCart method fails when the parameters
         * are wrong.
         */
        @Test
        void registeredPurchaseShoppingCartNegative() {
        }

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

        /**
         * This test check if the purchaseShoppingCart method fails when the parameters
         * are wrong.
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
            //name of store can't be empty
            Assertions.assertFalse(tradingSystem.openStore(userSystem2, store1.getPurchasePolicy(),store1.getDiscountPolicy(), ""));
        }

        /**
         * This test check if the removeManager method succeeds when the parameters
         * are correct.
         */
        @Test
        void removeManagerSuccess(){
            setUpUsersForRemoveManagerSuc();
            //check that remove success
            Assertions.assertTrue(tradingSystem.removeManager(store1, userSystem, userSystem2));
        }

        /**
         * This test check if the removeManager method fails when the parameters
         * are wrong.
         */
        @Test
        void removeManagerFail(){
            setUpUsersForRemoveManager();
            //can't remove from null store
            Assertions.assertFalse(tradingSystem.removeManager(null,userSystem2,userSystem));
            //store does not exists
            Assertions.assertFalse(tradingSystem.removeManager(store,userSystem2,userSystem));
            //1 users is not registered
            Assertions.assertFalse(tradingSystem.removeManager(store1,userSystem1,userSystem));
        }

        // ********************************** Set Up Functions For Tests ********************************** //


        /**
         *
         */
        private void setUpLogin(){
            Set<UserSystem> userList = new HashSet<>();
            userList.add(userSystem);
            tradingSystem.setUsersList(userList);
        }

        /**
         * set up all objects for purchase
         */
        private void setUpForPurchase(){
            paymentDetails = mock(PaymentDetails.class);
            billingAddress = mock(BillingAddress.class);
            testshoppingCart = mock(ShoppingCart.class);
            testShoppingBag = mock(ShoppingBag.class);
            userSystem2 = mock(UserSystem.class);
            store1 = mock(Store.class);
            when(store1.getStoreId()).thenReturn(1);
            tradingSystem.insertStoreToStores(store1);
            testProduct = mock(Product.class);
            List<Integer> listOfStoreId = new LinkedList<>();
            listOfStoreId.add(store1.getStoreId());
            Map<Product, Integer> productList = new HashMap<>();
            productList.put(testProduct,1);
            Map<Store,ShoppingBag> bagList = new HashMap<>();
            bagList.put(store1,testShoppingBag);
            when(testShoppingBag.getProductAmount(testProduct)).thenReturn(1);
            when(testShoppingBag.getProductListFromStore()).thenReturn(productList);
            when(testProduct.getAmount()).thenReturn(50);
            doNothing().when(testProduct).setAmount(49);
            when(userSystem2.getUserName()).thenReturn("Ragnar");
            when(testshoppingCart.getShoppingBagsList()).thenReturn(bagList);
            when(testshoppingCart.getShoppingBag(store1)).thenReturn(testShoppingBag);
            when(testShoppingBag.getTotalCostOfBag()).thenReturn(12.0);
            when(testProduct.getCost()).thenReturn(12.0);
            when(userSystem2.getShoppingCart()).thenReturn(testshoppingCart);
            when(testshoppingCart.removeBagFromCart(store1,testShoppingBag)).thenReturn(true);
            when(externalServiceManagement.charge(paymentDetails,testshoppingCart)).thenReturn(listOfStoreId);
            when(externalServiceManagement.cancelCharge(paymentDetails,testshoppingCart)).thenReturn(true);
            when(externalServiceManagement.deliver(billingAddress,testshoppingCart)).thenReturn(true);
        }
        /**
         * sets up users for openStore test
         */
        private void setUpUsersForOpenStoreTestSuc(){
            Set<UserSystem> users = new HashSet<>();
            when(userSystem2.getUserName()).thenReturn("UserSystem2");
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
         * sets up store for removeManager test
         */
        private void setUpUsersForRemoveManager(){
            when(store1.getStoreName()).thenReturn("castro");
            when(store1.getDiscountPolicy()).thenReturn(new DiscountPolicy());
            when(store1.getPurchasePolicy()).thenReturn(new PurchasePolicy());
            tradingSystem.insertStoreToStores(store1);
            Set<UserSystem> users = new HashSet<>();
            when(userSystem2.getUserName()).thenReturn("UserSystem2");
            when(userSystem.getUserName()).thenReturn("UserSystem");
            users.add(userSystem2);
            users.add(userSystem);
            tradingSystem.setUsersList(users);
        }

        /**
         * sets up store for removeManager test
         */
        private void setUpUsersForRemoveManagerSuc(){
            when(store1.getStoreName()).thenReturn("castro");
            when(store1.getDiscountPolicy()).thenReturn(new DiscountPolicy());
            when(store1.getPurchasePolicy()).thenReturn(new PurchasePolicy());
            tradingSystem.insertStoreToStores(store1);
            Set<UserSystem> users = new HashSet<>();
            when(userSystem2.getUserName()).thenReturn("UserSystem2");
            when(userSystem.getUserName()).thenReturn("UserSystem");
            users.add(userSystem2);
            users.add(userSystem);
            tradingSystem.setUsersList(users);
            when(store1.removeManager(userSystem,userSystem2)).thenReturn(true);
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
        private UserSystem testUser;
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

        /**
         * check if the login method works
         */
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
            //the following register should register usernameTest as username
            // and passwordTest as password
            registerAsSetup();  //register user test as setup for login
            boolean ans = tradingSystem.login(userToRegister,false,"passwordTest");
            Assertions.assertTrue(ans);
            //check login of admin
            //TODO
        }

        /**
         * test handling with login failure
         */
        @Test
        void loginNegative(){
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


        /**
         * check the getStoreByAdmin() functionality in case of exists admin in the system
         */
        @Test
        void getStoreByAdminPositive() {
            tradingSystem.insertStoreToStores(store);
            Assertions.assertEquals(tradingSystem.getStoreByAdmin("admin", 1, uuid),store);
        }

        /**
         * check the getStoreByAdmin() functionality in case of not exists admin in the system
         */
        @Test
        void getStoreByAdminNegative() {
            Assertions.assertThrows(NotAdministratorException.class, () -> {
                tradingSystem.insertStoreToStores(store);
                tradingSystem.getStoreByAdmin("userSystem", 1, uuid);
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
            Assertions.assertEquals(tradingSystem.getUser("usernameTest", uuid),userToRegister);
        }

        /**
         * check the getUser() functionality in case of not exists user in the system
         */
        @Test
        void getUserNegative() {
            // register "userToRegister" to the users list in trading system
            Assertions.assertThrows(UserDontExistInTheSystemException.class, () -> {
                registerAsSetup();
                tradingSystem.getUser("userToRegister", uuid);
            });
        }

        /**
         * check the getUserByAdmin() functionality in case of exists admin in the system
         */
        @Test
        void getUserByAdminPositive() {
            registerAsSetup();
            Assertions.assertEquals(tradingSystem.getUserByAdmin("admin", "usernameTest", uuid),userToRegister);
        }

        /**
         * check the getUserByAdmin() functionality in case of not exists admin in the system
         */
        @Test
        void getUserByAdminNegative(){
            Assertions.assertThrows(NotAdministratorException.class, () -> {
                registerAsSetup();
                tradingSystem.getUserByAdmin("userSystem", "usernameTest", uuid);
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
         * This test check if the openStore method succeeds when the parameters
         * are correct.
         */
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

        /**
         * This test check if the openStore method fails when the parameters
         * are wrong.
         */
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

        /**
         * This test check if the purchaseShoppingCart method succeeds when the parameters
         * are correct. for guest and registered it's the same process.
         */
        @Test
        @Disabled
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

        /**
         * This test check if the purchaseShoppingCart method fails when the parameters
         * are wrong. for guest and registered it's the same process.
         */
        @Test
        void registeredPurchaseShoppingCartFail() {
            setUpForPurchaseCartFail();
            //can't send a null object
            Assertions.assertNull(tradingSystem.purchaseShoppingCart(null, billingAddress, userToOpenStore));
            //product amount in store is 45, try to but 47
            Assertions.assertNull(tradingSystem.purchaseShoppingCart(paymentDetails, billingAddress, userToOpenStore));
            //card number is wrong
            Assertions.assertNull(tradingSystem.purchaseShoppingCart(paymentDetails1, billingAddress, userToOpenStore));
            //wrong zip code
            Assertions.assertNull(tradingSystem.purchaseShoppingCart(paymentDetails,billingAddress1,testUser));
        }

        /**
         * This test check if the addManager method succeeds when the parameters
         * are correct.
         */
        @Test
        void addManagerSuccess(){
            setUpAddManagerAndOwner();
            //check that the new manager added to store
            Assertions.assertTrue(tradingSystem.addMangerToStore(store1, storeOwner, newManager));
            //check that the number of managers of the store is 1
            Assertions.assertEquals(1, store1.getManagers().size());
            //check that right manager was added
            Assertions.assertEquals(newManager, store1.getManager(storeOwner, newManager.getUserName()));
        }

        /**
         * This test check if the addManager method fails when the parameters
         * are wrong.
         */
        @Test
        void addManagerFail(){
            setUpAddManagerAndOwner();
            //can't appoint a null manager
            Assertions.assertFalse(tradingSystem.addMangerToStore(store1,storeOwner,null));
            //not an owner of the store, can't appoint a manager
            Assertions.assertFalse(tradingSystem.addMangerToStore(store1,wrongOwner,newManager));
        }

        /**
         * This test check if the addOwner method succeeds when the parameters
         * are correct.
         */
        @Test
        void addOwnerSuccess(){
            setUpAddManagerAndOwner();
            //check that the new owner added to store
            Assertions.assertTrue(tradingSystem.addOwnerToStore(store1, storeOwner, newManager));
            //check that the number of owners of the store is 2
            Assertions.assertEquals(2, store1.getOwners().size());
        }

        /**
         * This test check if the addOwner method fails when the parameters
         * are wrong.
         */
        @Test
        void addOwnerFail(){
            setUpAddManagerAndOwner();
            //can't appoint a null owner
            Assertions.assertFalse(tradingSystem.addOwnerToStore(store1,storeOwner,null));
            //not an owner of the store, can't appoint an owner
            Assertions.assertFalse(tradingSystem.addOwnerToStore(store1,wrongOwner,newManager));
        }

        /**
         * This test check if the removeManager method succeeds when the parameters
         * are correct.
         */
        @Test
        void removeManagerSuccess(){
            setUpUsersForRemoveManagerSuc();
            tradingSystem.addMangerToStore(store1, storeOwner, newManager);
            //check that remove success
            Assertions.assertTrue(tradingSystem.removeManager(store1, storeOwner, newManager));
        }

        /**
         * This test check if the removeManager method fails when the parameters
         * are wrong.
         */
        @Test
        void removeManagerFail(){
       //     setUpUsersForRemoveManager();
            //can't remove from null store
            Assertions.assertFalse(tradingSystem.removeManager(null,storeOwner,newManager));
            //store does not exists
            Assertions.assertFalse(tradingSystem.removeManager(store,storeOwner,newManager));
            //1 users is not registered
            Assertions.assertFalse(tradingSystem.removeManager(store1,wrongOwner,newManager));
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

        /**
         * setUp for openStore
         */
        private void setUpForOpenStoreSuc(){
            userToOpenStore = getUserSystemBuild();
            //Set<UserSystem> users = new HashSet<>();
            //users.add(userToOpenStore);
            //tradingSystem.setUsersList(users);
            tradingSystem.registerNewUser(userToOpenStore);
        }

        /**
         * setUp for openStore
         */
        private void setUpForOpenStoreFail(){
            userToOpenStore = getUserSystemBuild();
            Set<UserSystem> users = new HashSet<>();
            users.add(userToOpenStore);
            tradingSystem.setUsersList(users);
            storeToOpen = new Store(userToOpenStore,new PurchasePolicy(), new DiscountPolicy(), "castro");
            tradingSystem.insertStoreToStores(storeToOpen);
        }

        /**
         * setUp for purchaseCart
         */
        private void setUpForPurchaseCart(){
            userToOpenStore = getUserSystemBuild();
            storeToOpen = Store.builder()
                            .purchasePolicy(new PurchasePolicy())
                            .discountPolicy(new DiscountPolicy())
                            .storeName("MovieStore").build();
            tradingSystem.insertStoreToStores(storeToOpen);
            testProduct = Product.builder()
                            .name("Harry-Potter")
                            .category(ProductCategory.BOOKS_MOVIES_MUSIC)
                            .amount(45)
                            .cost(12.9)
                            .storeId(storeToOpen.getStoreId())
                            .discountType(DiscountType.NONE)
                            .purchaseType(PurchaseType.BUY_IMMEDIATELY).build();
            userToOpenStore.saveProductInShoppingBag(storeToOpen,testProduct,3);
            paymentDetails = new PaymentDetails(CardAction.PAY, "123456789", "12", "2024", "Israel Israeli", 237, "333333339");
            billingAddress = new BillingAddress("Israel Israeli", "Ben-Gurion 1", "Beer Sheva", "Israel","1234567");
        }

        /**
         * setUp for purchaseCart
         */
        private void setUpForPurchaseCartFail(){
            userToOpenStore = getUserSystemBuild();
            testUser = getUser();
            storeToOpen = Store.builder()
                    .purchasePolicy(new PurchasePolicy())
                    .discountPolicy(new DiscountPolicy())
                    .storeName("MovieStore").build();
            storeToOpen1 = Store.builder()
                    .purchasePolicy(new PurchasePolicy())
                    .discountPolicy(new DiscountPolicy())
                    .storeName("MovieStoreVIP").build();
            tradingSystem.insertStoreToStores(storeToOpen);
            tradingSystem.insertStoreToStores(storeToOpen1);
            testProduct = Product.builder()
                    .name("Harry-Potter")
                    .category(ProductCategory.BOOKS_MOVIES_MUSIC)
                    .amount(45)
                    .cost(12.9)
                    .storeId(storeToOpen.getStoreId())
                    .discountType(DiscountType.NONE)
                    .purchaseType(PurchaseType.BUY_IMMEDIATELY).build();
            testProduct1 = Product.builder()
                    .name("Hunger Games")
                    .category(ProductCategory.BOOKS_MOVIES_MUSIC)
                    .amount(45)
                    .cost(12.9)
                    .storeId(storeToOpen1.getStoreId())
                    .discountType(DiscountType.NONE)
                    .purchaseType(PurchaseType.BUY_IMMEDIATELY).build();
            userToOpenStore.saveProductInShoppingBag(storeToOpen,testProduct,47);
            testUser.saveProductInShoppingBag(storeToOpen1,testProduct1,5);
            paymentDetails = new PaymentDetails(CardAction.PAY, "123456789", "12", "2024", "Israel Israeli", 237, "333333339");
            paymentDetails1 = new PaymentDetails(CardAction.PAY, "12345", "12", "2024", "Israel Israeli", 237, "333333339");
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

        /**
         * set owner, user to be manager and store
         */
        private void setUpAddManagerAndOwner(){
            storeOwner = getUserSystemBuild();
            wrongOwner = getWrongOwner();
            newManager = getUser();
            Set<UserSystem> usersList = new HashSet<>();
            usersList.add(storeOwner);
            usersList.add(newManager);
            tradingSystem.setUsersList(usersList);
            store1 = new Store(storeOwner, new PurchasePolicy(), new DiscountPolicy(), "MovieStore");
            tradingSystem.insertStoreToStores(store1);
        }

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

        /**
         * sets up store for removeManager test
         */
        private void setUpUsersForRemoveManager(){
            storeOwner = getUserSystemBuild();
            newManager = getUser();
            wrongOwner = getWrongOwner();
            //tradingSystem.insertStoreToStores(store1);
            Set<UserSystem> users = new HashSet<>();
            users.add(storeOwner);
            users.add(newManager);
            tradingSystem.setUsersList(users);
           // tradingSystem.openStore(storeOwner,new PurchasePolicy(), new DiscountPolicy(), "castro");
            //tradingSystem.openStore(wrongOwner,new PurchasePolicy(), new DiscountPolicy(), "zara");
            //store1 = tradingSystem.getStore(0);
            store = Store.builder()
                    .purchasePolicy(new PurchasePolicy())
                    .discountPolicy(new DiscountPolicy())
                    .storeName("MovieStore")
                    .storeId(store1.getStoreId()+1).build();
        }

        /**
         * sets up store for removeManager test
         */
        private void setUpUsersForRemoveManagerSuc(){
            storeOwner = getUser();
            newManager = getWrongOwner();
            //tradingSystem.insertStoreToStores(store1);
            Set<UserSystem> users = new HashSet<>();
            users.add(storeOwner);
            users.add(newManager);
            tradingSystem.setUsersList(users);
            tradingSystem.openStore(storeOwner,new PurchasePolicy(), new DiscountPolicy(), "castro");
            store1 = storeOwner.getOwnedStores().iterator().next();
        }
    }
}
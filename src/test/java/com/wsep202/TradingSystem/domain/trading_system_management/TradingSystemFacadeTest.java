//package com.wsep202.TradingSystem.domain.trading_system_management;
//
//import com.github.rozidan.springboot.modelmapper.WithModelMapper;
//import com.wsep202.TradingSystem.config.TestConfig;
//import com.wsep202.TradingSystem.config.TradingSystemConfiguration;
//import com.wsep202.TradingSystem.domain.factory.FactoryObjects;
//import com.wsep202.TradingSystem.domain.mapping.TradingSystemMapper;
//import com.wsep202.TradingSystem.dto.*;
//import com.wsep202.TradingSystem.helprTests.AssertionHelperTest;
//import com.wsep202.TradingSystem.helprTests.ExternalServiceManagementMock;
//import org.junit.jupiter.api.*;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.modelmapper.ModelMapper;
//import org.modelmapper.TypeToken;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//
//import javax.validation.constraints.NotNull;
//import java.lang.reflect.Type;
//import java.util.*;
//
//import static com.wsep202.TradingSystem.helprTests.SetUpObjects.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(SpringExtension.class)
//@WithModelMapper(basePackageClasses = TradingSystemMapper.class)
//class TradingSystemFacadeTest {
//
//    @Autowired
//    private ModelMapper modelMapper;
//
//    @Nested
//    public class TradingSystemFacadeTestUnit {
//
//        private TradingSystem tradingSystem;
//
//        private TradingSystemFacade tradingSystemFacade;
//
//        private UserSystem userSystem;
//
//        private Store store;
//
//        private FactoryObjects factoryObjects;
//
//        private PaymentDetails paymentDetails;
//
//        private BillingAddress billingAddress;
//
//        @BeforeEach
//        public void setUp() {
//            store = mock(Store.class);
//            userSystem = mock(UserSystem.class);
//            tradingSystem = mock(TradingSystem.class);
//            factoryObjects = mock(FactoryObjects.class);
//            tradingSystemFacade = new TradingSystemFacade(tradingSystem, modelMapper, factoryObjects);
//            paymentDetails = mock(PaymentDetails.class);
//            billingAddress = mock(BillingAddress.class);
//        }
//
//        @Test
//        void viewPurchaseHistoryUser() {
//            List<Receipt> receipts = setUpReceipts();
//            String userNameTest = "userNameTest";
//            when(tradingSystem.getUser(userNameTest, uuid)).thenReturn(userSystem);
//            when(userSystem.getReceipts()).thenReturn(receipts);
//            List<ReceiptDto> receiptDtos = tradingSystemFacade.viewPurchaseHistory(userNameTest);
//            AssertionHelperTest.assertReceipts(receipts, receiptDtos);
//        }
//
//        @Test
//        void viewPurchaseHistoryAdministratorOfStore() {
//            //initial
//            List<Receipt> receipts = setUpReceipts();
//            Store store = mock(Store.class);
//            String administratorUsername = "administratorUsername";
//            int storeId = 1;
//
//            //mock
//            when(tradingSystem.getStoreByAdmin(administratorUsername, storeId, uuid)).thenReturn(store);
//            when(store.getReceipts()).thenReturn(receipts);
//
//            //test
//            List<ReceiptDto> receiptDtos = tradingSystemFacade.viewPurchaseHistory(administratorUsername, storeId);
//            AssertionHelperTest.assertReceipts(receipts, receiptDtos);
//        }
//
//        @Test
//        void viewPurchaseHistoryAdministratorOfUser() {
//            //initial
//            List<Receipt> receipts = setUpReceipts();
//            String administratorUsername = "administratorUsername";
//            String username = "usernameTest";
//
//            //mock
//            when(tradingSystem.getUserByAdmin(administratorUsername, username, uuid)).thenReturn(userSystem);
//            when(userSystem.getReceipts()).thenReturn(receipts);
//
//            //test
//            List<ReceiptDto> receiptDtos = tradingSystemFacade.viewPurchaseHistory(administratorUsername, username);
//            AssertionHelperTest.assertReceipts(receipts, receiptDtos);
//        }
//
//        @Test
//        void viewPurchaseHistoryOfManager() {
//            //initial
//            List<Receipt> receipts = setUpReceipts();
//            String managerUsername = "managerUsername";
//            int storeId = 1;
//
//            //mock
//            when(tradingSystem.getUser(managerUsername, uuid)).thenReturn(userSystem);
//            when(userSystem.getManagerStore(storeId)).thenReturn(store);
//            when(store.getReceipts()).thenReturn(receipts);
//
//            //test
//            List<ReceiptDto> receiptDtos = tradingSystemFacade.viewPurchaseHistoryOfManager(managerUsername, storeId, uuid);
//            AssertionHelperTest.assertReceipts(receipts, receiptDtos);
//        }
//
//        @Test
//        void viewPurchaseHistoryOfOwner() {
//            //initial
//            List<Receipt> receipts = setUpReceipts();
//            String ownerUsername = "ownerUsername";
//            int storeId = 1;
//
//            //mock
//            when(tradingSystem.getUser(ownerUsername, uuid)).thenReturn(userSystem);
//            when(userSystem.getOwnerStore(storeId)).thenReturn(store);
//            when(store.getReceipts()).thenReturn(receipts);
//
//            //test
//            List<ReceiptDto> receiptDtos = tradingSystemFacade.viewPurchaseHistoryOfOwner(ownerUsername, storeId);
//            AssertionHelperTest.assertReceipts(receipts, receiptDtos);
//        }
//
//        @Test
//        void addProduct() {
//            //initial
//            String ownerUsername = "ownerUsername";
//            int storeId = 1;
//
//            //mock
//            when(tradingSystem.getUser(ownerUsername, uuid)).thenReturn(userSystem);
//            when(userSystem.getOwnerStore(storeId)).thenReturn(store);
//            when(store.addNewProduct(any(), any())).thenReturn(true);
//
//            //test
//            Arrays.stream(ProductCategory.values()).forEach(productCategory -> {
//                String productName = "productName " + productCategory.category;
//                Assertions.assertTrue(tradingSystemFacade
//                        .addProduct(ownerUsername, storeId, productName,
//                                productCategory.category, 1, 1));
//            });
//        }
//
//        @Test
//        void deleteProductFromStore() {
//            //initial
//            String ownerUsername = "ownerUsername";
//            int storeId = 1;
//            int productSn = 1;
//
//            //mock
//            when(tradingSystem.getUser(ownerUsername, uuid)).thenReturn(userSystem);
//            when(userSystem.getOwnerStore(storeId)).thenReturn(store);
//            when(store.removeProductFromStore(userSystem, productSn)).thenReturn(true);
//
//            Assertions.assertTrue(tradingSystemFacade
//                    .deleteProductFromStore(ownerUsername, storeId, productSn));
//        }
//
//        @Test
//        void editProduct() {
//            //initial
//            String ownerUsername = "ownerUsername";
//            int storeId = 1;
//            int productSn = 1;
//            String productName = "productName";
//            String category = ProductCategory.values()[0].category;
//            int amount = 1;
//            double cost = 3434;
//
//            //mock
//            when(tradingSystem.getUser(ownerUsername, uuid)).thenReturn(userSystem);
//            when(userSystem.getOwnerStore(storeId)).thenReturn(store);
//            when(store.editProduct(userSystem, productSn, productName, category, amount, cost)).thenReturn(true);
//
//            //test
//            Assertions.assertTrue(tradingSystemFacade
//                    .editProduct(ownerUsername, storeId, productSn, productName, category, amount, cost, uuid));
//        }
//
//        @Test
//        void addOwner() {
//            //initial
//            String ownerUsername = "ownerUsername";
//            String newOwnerUsername = "newOwnerUsername";
//
//            int storeId = 1;
//
//            //mock
//            UserSystem newOwner = mock(UserSystem.class);
//            when(tradingSystem.getUser(ownerUsername, uuid)).thenReturn(userSystem);
//            when(tradingSystem.getUser(newOwnerUsername, uuid)).thenReturn(newOwner);
//            when(userSystem.getOwnerStore(storeId)).thenReturn(store);
//            when(tradingSystem.addOwnerToStore(store, userSystem, newOwner)).thenReturn(true);
//
//            //test
//            Assertions.assertTrue(tradingSystemFacade
//                    .addOwner(ownerUsername, storeId, newOwnerUsername, uuid));
//        }
//
//        @Test
//        void addManager() {
//            //initial
//            String ownerUsername = "ownerUsername";
//            String newManagerUsername = "newManagerUsername";
//
//            int storeId = 1;
//
//            //mock
//            UserSystem newOwner = mock(UserSystem.class);
//            when(tradingSystem.getUser(ownerUsername, uuid)).thenReturn(userSystem);
//            when(tradingSystem.getUser(newManagerUsername, uuid)).thenReturn(newOwner);
//            when(userSystem.getOwnerStore(storeId)).thenReturn(store);
//            when(tradingSystem.addMangerToStore(store, userSystem, newOwner)).thenReturn(true);
//
//            //test
//            Assertions.assertTrue(tradingSystemFacade
//                    .addManager(ownerUsername, storeId, newManagerUsername, uuid));
//        }
//
//        @Test
//        void addPermission() {
//            //initial
//            String ownerUsername = "ownerUsername";
//            String managerUsername = "newManagerUsername";
//            String storePermission = StorePermission.VIEW.function;
//            int storeId = 1;
//
//            //mock
//            UserSystem managerStore = mock(UserSystem.class);
//            when(tradingSystem.getUser(ownerUsername, uuid)).thenReturn(userSystem);
//            when(userSystem.getOwnerStore(storeId)).thenReturn(store);
//            when(store.getManager(userSystem, managerUsername)).thenReturn(managerStore);
//            when(store.addPermissionToManager(userSystem, managerStore, StorePermission.getStorePermission(storePermission)))
//                    .thenReturn(true);
//            //test
//            Assertions.assertTrue(tradingSystemFacade
//                    .addPermission(ownerUsername, storeId, managerUsername, storePermission, uuid));
//        }
//
//        @Test
//        void removeManager() {
//            //initial
//            String ownerUsername = "ownerUsername";
//            String managerUsername = "newManagerUsername";
//            int storeId = 1;
//
//            //mock
//            UserSystem managerStore = mock(UserSystem.class);
//            when(tradingSystem.getUser(ownerUsername, uuid)).thenReturn(userSystem);
//            when(userSystem.getOwnerStore(storeId)).thenReturn(store);
//            when(store.getManager(userSystem, managerUsername)).thenReturn(managerStore);
//            when(tradingSystem.removeManager(store, userSystem, managerStore)).thenReturn(true);
//            //test
//            Assertions.assertTrue(tradingSystemFacade
//                    .removeManager(ownerUsername, storeId, managerUsername, uuid));
//        }
//
//        @Test
//        void logout() {
//            //initial
//            String userName = "username";
//
//            //mock
//            when(tradingSystem.getUser(userName, uuid)).thenReturn(userSystem);
//            when(userSystem.logout()).thenReturn(true);
//
//            //test
//            Assertions.assertTrue(tradingSystemFacade
//                    .logout(userName));
//        }
//
//        @Test
//        void openStore() {
//            //init
//            String ownerUsername = "ownerUsername";
//            PurchasePolicyDto purchasePolicyDto = PurchasePolicyDto.builder().build();
//            DiscountPolicyDto discountPolicyDto = DiscountPolicyDto.builder().build();
//            String discountType = DiscountType.NONE.type;
//            String purchaseType = PurchaseType.BUY_IMMEDIATELY.type;
//            String storeName = "storeName";
//
//            //mock
//            ModelMapper modelMapper = mock(ModelMapper.class);
//            tradingSystemFacade = new TradingSystemFacade(tradingSystem, modelMapper, factoryObjects);
//            PurchasePolicy purchasePolicy = mock(PurchasePolicy.class);
//            DiscountPolicy discountPolicy = mock(DiscountPolicy.class);
//
//            when(tradingSystem.getUser(ownerUsername, uuid)).thenReturn(userSystem);
//            when(modelMapper.map(purchasePolicyDto, PurchasePolicy.class)).thenReturn(purchasePolicy);
//            when(modelMapper.map(discountPolicyDto, DiscountPolicy.class)).thenReturn(discountPolicy);
//            when(tradingSystem.openStore(userSystem, purchasePolicy, discountPolicy, storeName)).thenReturn(true);
//
//            //test
//            // Assertions.assertTrue(tradingSystemFacade
//            //    .openStore(ownerUsername, purchasePolicyDto, discountPolicyDto, discountType, purchaseType, storeName));
//        }
//
//        @Test
//        void registerUser() {
//            String userName = "username";
//            String password = "password";
//            String firstName = "firstName";
//            String lastName = "lastName";
//            when(factoryObjects.createSystemUser(userName, firstName, lastName, password)).thenReturn(userSystem);
//            when(tradingSystem.registerNewUser(userSystem)).thenReturn(true);
//
//            //test
//            Assertions.assertTrue(tradingSystemFacade
//                    .registerUser(userName, password, firstName, lastName));
//        }
//
//        @Test
//        void login() {
//            //initial
//            String userName = "username";
//            String password = "password";
//
//            //mock
//            when(tradingSystem.getUser(userName, uuid)).thenReturn(userSystem);
//            when(tradingSystem.login(userSystem, false, password)).thenReturn(true);
//
//            //test
//            Assertions.assertTrue(tradingSystemFacade
//                    .login(userName, password));
//        }
//
//        @Test
//        void viewStoreInfo() {
//            //init
//            Store store = createStore();
//            int storeId = 1;
//
//            //mock
//            when(tradingSystem.getStore(storeId)).thenReturn(store);
//            StoreDto storeDto = tradingSystemFacade.viewStoreInfo(storeId);
//            AssertionHelperTest.assertionStore(store, storeDto);
//        }
//
//        @Test
//        void viewProduct() {
//            //init
//            int storeId = 1;
//            int productId = 1;
//            Product product = createProduct(productId);
//
//            //mock
//            when(tradingSystem.getStore(storeId)).thenReturn(store);
//            when(store.getProduct(productId)).thenReturn(product);
//            ProductDto productDto = tradingSystemFacade.viewProduct(storeId, productId);
//            AssertionHelperTest.assertProduct(product, productDto);
//        }
//
//        @Test
//        void searchProductByName() {
//            List<Product> products = new ArrayList<>(setUpProducts());
//            String productName = "productName";
//            when(tradingSystem.searchProductByName(productName)).thenReturn(products);
//            List<ProductDto> productDtos = tradingSystemFacade.searchProductByName(productName);
//            AssertionHelperTest.assertProducts(new HashSet<>(products), new HashSet<>(productDtos));
//        }
//
//        @Test
//        void searchProductByCategory() {
//            String category = ProductCategory.values()[0].category;
//            List<Product> products = new ArrayList<>(setUpProducts());
//            when(tradingSystem.searchProductByCategory(ProductCategory.getProductCategory(category))).thenReturn(products);
//            List<ProductDto> productDtos = tradingSystemFacade.searchProductByCategory(category);
//            AssertionHelperTest.assertProducts(new HashSet<>(products), new HashSet<>(productDtos));
//        }
//
//        @Test
//        void searchProductByKeyWords() {
//            List<Product> products = new ArrayList<>(setUpProducts());
//            List<String> keyWords = new LinkedList<>();
//            keyWords.add("test-key-words");
//            when(tradingSystem.searchProductByKeyWords(keyWords)).thenReturn(products);
//            List<ProductDto> productDtos = tradingSystemFacade.searchProductByKeyWords(keyWords);
//            AssertionHelperTest.assertProducts(new HashSet<>(products), new HashSet<>(productDtos));
//        }
//
//        @Test
//        void filterByRangePrice() {
//            int minPrice = 0;
//            int maxPrice = 10;
//            List<Product> products = new ArrayList<>(setUpProducts());
//            List<ProductDto> productsDtoArg = convertProductDtoList(products);
//            when(tradingSystem.filterByRangePrice(products, minPrice, maxPrice)).thenReturn(products);
//            List<ProductDto> productDtos = tradingSystemFacade.filterByRangePrice(productsDtoArg, minPrice, maxPrice);
//            AssertionHelperTest.assertProducts(new HashSet<>(products), new HashSet<>(productDtos));
//        }
//
//        @Test
//        void filterByProductRank() {
//            int rank = 0;
//            List<Product> products = new ArrayList<>(setUpProducts());
//            List<ProductDto> productsDtoArg = convertProductDtoList(products);
//            when(tradingSystem.filterByProductRank(products, rank)).thenReturn(products);
//            List<ProductDto> productDtos = tradingSystemFacade.filterByProductRank(productsDtoArg, rank);
//            AssertionHelperTest.assertProducts(new HashSet<>(products), new HashSet<>(productDtos));
//        }
//
//        @Test
//        void filterByStoreRank() {
//            int rank = 0;
//            List<Product> products = new ArrayList<>(setUpProducts());
//            List<ProductDto> productsDtoArg = convertProductDtoList(products);
//            when(tradingSystem.filterByStoreRank(products, rank)).thenReturn(products);
//            List<ProductDto> productDtos = tradingSystemFacade.filterByStoreRank(productsDtoArg, rank);
//            AssertionHelperTest.assertProducts(new HashSet<>(products), new HashSet<>(productDtos));
//        }
//
//        @Test
//        void filterByStoreCategory() {
//            String category = ProductCategory.values()[0].category;
//            List<Product> products = new ArrayList<>(setUpProducts());
//            List<ProductDto> productsDtoArg = convertProductDtoList(products);
//            when(tradingSystem.filterByStoreCategory(products, ProductCategory.getProductCategory(category))).thenReturn(products);
//            List<ProductDto> productDtos = tradingSystemFacade.filterByStoreCategory(productsDtoArg, category);
//            AssertionHelperTest.assertProducts(new HashSet<>(products), new HashSet<>(productDtos));
//        }
//
//        @Test
//        void saveProductInShoppingBag() {
//            //initial
//            String username = "username";
//            int storeId = 1;
//            int productSn = 1;
//            int amount = 1;
//
//            //mock
//            Product product = mock(Product.class);
//            when(tradingSystem.getUser(username, uuid)).thenReturn(userSystem);
//            when(tradingSystem.getStore(storeId)).thenReturn(store);
//            when(store.getProduct(productSn)).thenReturn(product);
//            when(userSystem.saveProductInShoppingBag(store, product, amount)).thenReturn(true);
//
//            Assertions.assertTrue(tradingSystemFacade.saveProductInShoppingBag(username, storeId, productSn, amount));
//        }
//
//        @Test
//        void viewProductsInShoppingCart() {
//            //init
//            String username = "username";
//            ShoppingCart shoppingCart = setUpShoppingCart();
//            //mock
//            when(tradingSystem.getUser(username, uuid)).thenReturn(userSystem);
//            when(userSystem.getShoppingCart()).thenReturn(shoppingCart);
//            ShoppingCartDto shoppingCartDto = tradingSystemFacade.viewProductsInShoppingCart(username);
//            AssertionHelperTest.assertShoppingCart(shoppingCart, shoppingCartDto);
//        }
//
//        @Test
//        void removeProductInShoppingBag() {
//            //initial
//            String username = "username";
//            int storeId = 1;
//            int productSn = 1;
//
//            //mock
//            Product product = mock(Product.class);
//            when(tradingSystem.getUser(username, uuid)).thenReturn(userSystem);
//            when(tradingSystem.getStore(storeId)).thenReturn(store);
//            when(store.getProduct(productSn)).thenReturn(product);
//            when(userSystem.removeProductInShoppingBag(store, product)).thenReturn(true);
//            Assertions.assertTrue(userSystem.removeProductInShoppingBag(store, product));
//        }
//
//        @Test
//        void purchaseShoppingCart() {
//            //init
//            String username = "username";
//            PaymentDetailsDto paymentDetailsDto = modelMapper.map(paymentDetails, PaymentDetailsDto.class);
//            BillingAddressDto billingAddressDto = modelMapper.map(billingAddress, BillingAddressDto.class);
//            List<Receipt> receiptsExpected = setUpReceipts();
//            List<ReceiptDto> receiptDtos = convertReceiptDtoList(receiptsExpected);
//            //mock
//            when(tradingSystem.getUser(username, uuid)).thenReturn(userSystem);
//            when(tradingSystem.purchaseShoppingCart(any(), any(), any())).thenReturn(receiptsExpected);
//
//            List<ReceiptDto> receiptDtoAcutal = tradingSystemFacade.purchaseShoppingCart(username, paymentDetailsDto, billingAddressDto);
//            AssertionHelperTest.assertReceipts(receiptsExpected, receiptDtoAcutal);
//        }
//
//        @Test
//        void purchaseShoppingCartTest() {
//            //init
//            ShoppingCart shoppingCart = setUpShoppingCart();
//            ShoppingCartDto shoppingCartDto = modelMapper.map(shoppingCart, ShoppingCartDto.class);
//            PaymentDetailsDto paymentDetailsDto = modelMapper.map(paymentDetails, PaymentDetailsDto.class);
//            BillingAddressDto billingAddressDto = modelMapper.map(billingAddress, BillingAddressDto.class);
//            List<Receipt> receiptsExpected = setUpReceipts();
//            //mock
//            when(tradingSystem.purchaseShoppingCartGuest(any(), any(), any())).thenReturn(receiptsExpected);
//
//            List<ReceiptDto> receiptDtoActual = tradingSystemFacade.purchaseShoppingCart(shoppingCartDto, paymentDetailsDto, billingAddressDto);
//            AssertionHelperTest.assertReceipts(receiptsExpected, receiptDtoActual);
//        }
//    }
//
//    // ******************************* integration test ******************************* //
//    @Nested
//    @ContextConfiguration(classes = {TestConfig.class, TradingSystemConfiguration.class})
//    @WithModelMapper(basePackageClasses = TradingSystemMapper.class)
//    @SpringBootTest(args = {"admin", "admin"})
//    public class TradingSystemFacadeTestIntegration {
//
//        @Autowired
//        private TradingSystemFacade tradingSystemFacade;
//
//        @Autowired
//        private TradingSystem tradingSystem;
//
//        @Autowired
//        private ExternalServiceManagementMock externalServiceManagementMock;
//
//        private Set<UserSystem> userSystems;
//        private List<Receipt> receipts;
//        private Set<Store> stores;
//        private UserSystem currUser;
//        private UserSystem testUserSystem;
//        private UserSystem admin;
//
//        @BeforeEach
//        void setUp() {
//            admin = UserSystem.builder()
//                    .userName("admin")
//                    .password("admin")
//                    .build();
//            addUsers();
//            Optional<UserSystem> userSystemOptional = userSystems.stream().findFirst();
//            Assertions.assertTrue(userSystemOptional.isPresent());
//            currUser = userSystemOptional.get();
//            //addStores();
//        }
//
//        private void addStores() {
//            stores = setUpStores();
//
//            stores.forEach(store ->
//                    /*tradingSystemFacade.openStore(currUser.getUserName(), new PurchasePolicyDto(store.getPurchasePolicy().isAllAllowed(),
//                            store.getPurchasePolicy().getWhoCanBuyStatus()), new DiscountPolicyDto(store.getDiscountPolicy().isAllAllowed(),
//                            store.getDiscountPolicy().getWhoCanBuyStatus()), store.getDiscountType().type, store.getPurchaseType().type,
//                            store.getStoreName()));*/
//                    tradingSystem.openStore(currUser, store.getPurchasePolicy(), store.getDiscountPolicy(), store.getStoreName()));
//        }
//
//        void addUsers() {
//            userSystems = setupUsers();
//            userSystems.forEach(userSystem ->
//                    tradingSystem.registerNewUser(userSystem));
//        }
//
//        /**
//         * check the viewPurchaseHistoryUser() functionality in case of exists user in the system
//         */
//        @Test
//        void viewPurchaseHistoryUserPositive() {
//            receipts = setUpReceipts();
//            currUser.setReceipts(receipts);
//            //call the function
//            tradingSystem.getUser(currUser.getUserName(), uuid).setReceipts(receipts);
//            List<ReceiptDto> receiptDtos = tradingSystemFacade.viewPurchaseHistory(currUser.getUserName());
//            AssertionHelperTest.assertReceipts(receipts, receiptDtos);
//        }
//
//        /**
//         * check the viewPurchaseHistoryUser() functionality in case of not exists user in the system
//         */
//        @Test
//        void viewPurchaseHistoryUserNotExist() {
//            //call the function
//            List<ReceiptDto> receiptDtos = tradingSystemFacade.viewPurchaseHistory("userNotExist");
//            Assertions.assertNull(receiptDtos);
//        }
//
//        /**
//         * check the viewPurchaseHistoryAdministratorOfStore() functionality in case of exists admin and store in the system
//         */
//        @Test
//        void viewPurchaseHistoryAdministratorOfStorePositive() {
//            // create a user
//            testUserSystem = UserSystem.builder()
//                    .userName("KingRagnar")
//                    .password("Odin12")
//                    .firstName("Ragnar")
//                    .lastName("Lodbrok").build();
//            tradingSystemFacade.registerUser(testUserSystem.getUserName(), testUserSystem.getPassword(), testUserSystem.getFirstName(), testUserSystem.getLastName());
//
//            // opens a store
//            tradingSystemFacade.openStore(testUserSystem.getUserName(), new PurchasePolicyDto(), new DiscountPolicyDto(), "castro");
//            //tradingSystem.openStore(tradingSystem.getUser(testUserSystem.getUserName()),new PurchasePolicy(), new DiscountPolicy(), "castro");
//            Store store = this.tradingSystem.getStoresList().stream().filter(store1 -> store1.getStoreName().equals("castro")).findFirst().get();
//            receipts = setUpReceipts();
//            store.setReceipts(receipts);
//
//            // call the function
//            List<ReceiptDto> receiptDtos = tradingSystemFacade.viewPurchaseHistory(admin.getUserName(), store.getStoreId());
//            AssertionHelperTest.assertReceipts(receipts, receiptDtos);
//        }
//
//        /**
//         * check the viewPurchaseHistoryAdministratorOfStore() functionality in case of exists admin and not exist
//         * store in the system.
//         */
//        @Test
//        void viewPurchaseHistoryAdministratorOfStoreNegative() {
//            // create a user
//            testUserSystem = UserSystem.builder()
//                    .userName("KingRagnar")
//                    .password("Odin12")
//                    .firstName("Ragnar")
//                    .lastName("Lodbrok").build();
//            tradingSystemFacade.registerUser(testUserSystem.getUserName(), testUserSystem.getPassword(), testUserSystem.getFirstName(), testUserSystem.getLastName());
//
//            //call the function
//            List<ReceiptDto> receiptDtos = tradingSystemFacade.viewPurchaseHistory(admin.getUserName(), 100000);
//            Assertions.assertNull(receiptDtos);
//        }
//
//        /**
//         * check the viewPurchaseHistoryAdministratorOfUser() functionality in case of exists admin and user in the system
//         */
//        @Test
//        void viewPurchaseHistoryAdministratorOfUserPositive() {
//            // create a user
//            testUserSystem = UserSystem.builder()
//                    .userName("KingRagnar")
//                    .password("Odin12")
//                    .firstName("Ragnar")
//                    .lastName("Lodbrok").build();
//            tradingSystemFacade.registerUser(testUserSystem.getUserName(), testUserSystem.getPassword(), testUserSystem.getFirstName(), testUserSystem.getLastName());
//            List<Receipt> receipts = setUpReceipts();
//            testUserSystem.setReceipts(receipts);
//            tradingSystem.getUser(testUserSystem.getUserName(), uuid).setReceipts(receipts);
//
//            //call the function
//            List<ReceiptDto> receiptDtos = tradingSystemFacade.viewPurchaseHistory(admin.getUserName(), testUserSystem.getUserName());
//            AssertionHelperTest.assertReceipts(receipts, receiptDtos);
//        }
//
//        /**
//         * check the viewPurchaseHistoryAdministratorOfUser() functionality in case of exists admin and not exist
//         * user in the system.
//         */
//        @Test
//        void viewPurchaseHistoryAdministratorOfUserNegative() {
//            // create a user
//            testUserSystem = UserSystem.builder()
//                    .userName("KingRagnar")
//                    .password("Odin12")
//                    .firstName("Ragnar")
//                    .lastName("Lodbrok").build();
//
//            // call the function
//            tradingSystemFacade.registerUser(testUserSystem.getUserName(), testUserSystem.getPassword(), testUserSystem.getFirstName(), testUserSystem.getLastName());
//            List<ReceiptDto> receiptDtos = tradingSystemFacade.viewPurchaseHistory(admin.getUserName(), "moran");
//            Assertions.assertNull(receiptDtos);
//        }
//
//        /**
//         * check the viewPurchaseHistoryOfManager() functionality in case of exists store and user in the system
//         */
//        @Test
//        void viewPurchaseHistoryOfManagerPositive() {
//            // create a user owner
//            UserSystem userSystemOwner = UserSystem.builder()
//                    .userName("KingRagnar")
//                    .password("Odin12")
//                    .firstName("Ragnar")
//                    .lastName("Lodbrok").build();
//            tradingSystemFacade.registerUser(userSystemOwner.getUserName(), userSystemOwner.getPassword(), userSystemOwner.getFirstName(), userSystemOwner.getLastName());
//            List<Receipt> receipts = setUpReceipts();
//            userSystemOwner.setReceipts(receipts);
//            tradingSystem.getUser(userSystemOwner.getUserName(), uuid).setReceipts(receipts);
//            UserSystem updatedOwner = tradingSystem.getUser(userSystemOwner.getUserName(), uuid);
//
//            // create a user manager
//            UserSystem userSystemManager = UserSystem.builder()
//                    .userName("KingRagnarManager")
//                    .password("Odin12Manager")
//                    .firstName("RagnarManager")
//                    .lastName("LodbrokManager").build();
//            tradingSystemFacade.registerUser(userSystemManager.getUserName(), userSystemManager.getPassword(), userSystemManager.getFirstName(), userSystemManager.getLastName());
//            userSystemManager.setReceipts(receipts);
//            tradingSystem.getUser(userSystemManager.getUserName(), uuid).setReceipts(receipts);
//
//            // create a store
//            tradingSystem.openStore(updatedOwner, new PurchasePolicy(), new DiscountPolicy(), "castro");
//            Store store = this.tradingSystem.getStoresList().stream().filter(store1 -> store1.getStoreName().equals("castro")).findFirst().get();
//            store.setReceipts(receipts);
//            tradingSystemFacade.addManager(updatedOwner.getUserName(), store.getStoreId(), userSystemManager.getUserName(), uuid);
//
//            // call the function
//            List<ReceiptDto> receiptDtos = tradingSystemFacade.viewPurchaseHistoryOfManager(userSystemManager.getUserName(), store.getStoreId(), uuid);
//            AssertionHelperTest.assertReceipts(receipts, receiptDtos);
//        }
//
//        /**
//         * check the viewPurchaseHistoryOfManager() functionality in case of exists store and not exist user in the system
//         */
//        @Test
//        void viewPurchaseHistoryOfManagerNotExistUser() {
//            // create a user
//            testUserSystem = UserSystem.builder()
//                    .userName("KingRagnar")
//                    .password("Odin12")
//                    .firstName("Ragnar")
//                    .lastName("Lodbrok").build();
//            tradingSystemFacade.registerUser(testUserSystem.getUserName(), testUserSystem.getPassword(), testUserSystem.getFirstName(), testUserSystem.getLastName());
//
//            // create a store
//            tradingSystemFacade.openStore(testUserSystem.getUserName(), new PurchasePolicyDto(), new DiscountPolicyDto(),
//                    "castro");
//            Store store = this.tradingSystem.getStoresList().stream().filter(store1 -> store1.getStoreName().equals("castro")).findFirst().get();
//            store.setReceipts(receipts);
//            tradingSystemFacade.addManager(testUserSystem.getUserName(), store.getStoreId(), testUserSystem.getUserName(), uuid);
//
//            UserSystem moran = new UserSystem("Moran", "moran", "neptune", "momo");
//            // call the function
//            List<ReceiptDto> receiptDtos = tradingSystemFacade.viewPurchaseHistoryOfManager(moran.getUserName(), store.getStoreId(), uuid);
//            Assertions.assertNull(receiptDtos);
//            //assertReceipts(receipts, receiptDtos);
//        }
//
//        /**
//         * check the viewPurchaseHistoryOfManager() functionality in case of exists user and not exist store in the system
//         */
//        @Test
//        void viewPurchaseHistoryOfManagerNotExistStore() {
//            // create a user
//            testUserSystem = UserSystem.builder()
//                    .userName("KingRagnar")
//                    .password("Odin12")
//                    .firstName("Ragnar")
//                    .lastName("Lodbrok").build();
//            tradingSystemFacade.registerUser(testUserSystem.getUserName(), testUserSystem.getPassword(), testUserSystem.getFirstName(), testUserSystem.getLastName());
//
//            // call the function
//            List<ReceiptDto> receiptDtos = tradingSystemFacade.viewPurchaseHistoryOfManager(testUserSystem.getUserName(), 1, uuid);
//            Assertions.assertNull(receiptDtos);
//            //assertReceipts(receipts, receiptDtos);
//        }
//
//        /**
//         * check the viewPurchaseHistoryOfOwner() functionality in case of exists store and user in the system
//         */
//        @Test
//        void viewPurchaseHistoryOfOwnerPositive() {
//            // create a user owner
//            UserSystem userSystemOwner = UserSystem.builder()
//                    .userName("KingRagnar")
//                    .password("Odin12")
//                    .firstName("Ragnar")
//                    .lastName("Lodbrok").build();
//            tradingSystemFacade.registerUser(userSystemOwner.getUserName(), userSystemOwner.getPassword(), userSystemOwner.getFirstName(), userSystemOwner.getLastName());
//            List<Receipt> receipts = setUpReceipts();
//            userSystemOwner.setReceipts(receipts);
//            tradingSystem.getUser(userSystemOwner.getUserName(), uuid).setReceipts(receipts);
//            UserSystem updatedOwner = tradingSystem.getUser(userSystemOwner.getUserName(), uuid);
//
//            // create a store
//            tradingSystem.openStore(updatedOwner, new PurchasePolicy(), new DiscountPolicy(), "castro");
//            Store store = this.tradingSystem.getStoresList().stream().filter(store1 -> store1.getStoreName().equals("castro")).findFirst().get();
//            store.setReceipts(receipts);
//
//            // call the function
//            List<ReceiptDto> receiptDtos = tradingSystemFacade.viewPurchaseHistoryOfOwner(updatedOwner.getUserName(), store.getStoreId());
//            AssertionHelperTest.assertReceipts(receipts, receiptDtos);
//        }
//
//        /**
//         * check the viewPurchaseHistoryOfOwner() functionality in case of exists store and not exist user in the system
//         */
//        @Test
//        void viewPurchaseHistoryOfOwnerNotExistUser() {
//            // create a user owner
//            UserSystem userSystemOwner = UserSystem.builder()
//                    .userName("KingRagnar")
//                    .password("Odin12")
//                    .firstName("Ragnar")
//                    .lastName("Lodbrok").build();
//            tradingSystemFacade.registerUser(userSystemOwner.getUserName(), userSystemOwner.getPassword(), userSystemOwner.getFirstName(), userSystemOwner.getLastName());
//            List<Receipt> receipts = setUpReceipts();
//            userSystemOwner.setReceipts(receipts);
//            tradingSystem.getUser(userSystemOwner.getUserName(), uuid).setReceipts(receipts);
//            UserSystem updatedOwner = tradingSystem.getUser(userSystemOwner.getUserName(), uuid);
//
//            // create a store
//            tradingSystem.openStore(updatedOwner, new PurchasePolicy(), new DiscountPolicy(), "castro");
//            Store store = this.tradingSystem.getStoresList().stream().filter(store1 -> store1.getStoreName().equals("castro")).findFirst().get();
//            store.setReceipts(receipts);
//
//            // call the function
//            List<ReceiptDto> receiptDtos = tradingSystemFacade.viewPurchaseHistoryOfOwner("moran", store.getStoreId());
//            Assertions.assertNull(receiptDtos);
//            //assertReceipts(receipts, receiptDtos);
//        }
//
//        /**
//         * check the viewPurchaseHistoryOfOwner() functionality in case of exists user and not exist store in the system
//         */
//        @Test
//        void viewPurchaseHistoryOfOwnerNotExistStore() {
//            // create a user owner
//            UserSystem userSystemOwner = UserSystem.builder()
//                    .userName("KingRagnar")
//                    .password("Odin12")
//                    .firstName("Ragnar")
//                    .lastName("Lodbrok").build();
//            tradingSystemFacade.registerUser(userSystemOwner.getUserName(), userSystemOwner.getPassword(), userSystemOwner.getFirstName(), userSystemOwner.getLastName());
//            List<Receipt> receipts = setUpReceipts();
//            userSystemOwner.setReceipts(receipts);
//            tradingSystem.getUser(userSystemOwner.getUserName(), uuid).setReceipts(receipts);
//            UserSystem updatedOwner = tradingSystem.getUser(userSystemOwner.getUserName(), uuid);
//
//            // call the function
//            List<ReceiptDto> receiptDtos = tradingSystemFacade.viewPurchaseHistoryOfOwner(updatedOwner.getUserName(), 100000);
//            Assertions.assertNull(receiptDtos);
//            //assertReceipts(receipts, receiptDtos);
//        }
//
//        /**
//         * check the addProduct() functionality in case of exists store and userOwner and productName in the system
//         */
//        @Test
//        void addProductPositive() {
//            // create a user owner
//            UserSystem userSystemOwner = UserSystem.builder()
//                    .userName("KingRagnar")
//                    .password("Odin12")
//                    .firstName("Ragnar")
//                    .lastName("Lodbrok").build();
//            tradingSystemFacade.registerUser(userSystemOwner.getUserName(), userSystemOwner.getPassword(), userSystemOwner.getFirstName(), userSystemOwner.getLastName());
//            UserSystem updatedOwner = tradingSystem.getUser(userSystemOwner.getUserName(), uuid);
//
//            // create a store
//            tradingSystem.openStore(updatedOwner, new PurchasePolicy(), new DiscountPolicy(), "castro");
//            Store store = this.tradingSystem.getStoresList().stream().filter(store1 -> store1.getStoreName().equals("castro")).findFirst().get();
//
//            // create a product and call the function
//            //Product product = new Product("table", ProductCategory.HOME_GARDEN, 10,100,store.getStoreId());
//            Assertions.assertTrue(tradingSystemFacade.addProduct(updatedOwner.getUserName(), store.getStoreId(), "table",
//                    ProductCategory.HOME_GARDEN.category, 10, 100));
//        }
//
//        /**
//         * check the addProduct() functionality in case of not exists store and exist userOwner and productName in the system
//         */
//        @Test
//        void addProductNotExistStore() {
//            // create a user owner
//            UserSystem userSystemOwner = UserSystem.builder()
//                    .userName("KingRagnar")
//                    .password("Odin12")
//                    .firstName("Ragnar")
//                    .lastName("Lodbrok").build();
//            tradingSystemFacade.registerUser(userSystemOwner.getUserName(), userSystemOwner.getPassword(), userSystemOwner.getFirstName(), userSystemOwner.getLastName());
//            UserSystem updatedOwner = tradingSystem.getUser(userSystemOwner.getUserName(), uuid);
//
//            // create a product and call the function
//            //Product product = new Product("table", ProductCategory.HOME_GARDEN, 10, 100, 100000);
//            Assertions.assertFalse(tradingSystemFacade.addProduct(updatedOwner.getUserName(), 100000, "table",
//                    ProductCategory.HOME_GARDEN.category, 10, 100));
//        }
//
//        /**
//         * check the addProduct() functionality in case of not exists userOwner and exist store and productName in the system
//         */
//        @Test
//        void addProductNotExistUserOwner() {
//            // create a demo user
//            UserSystem testUserSystem = UserSystem.builder()
//                    .userName("KingRagnarTest")
//                    .password("Odin12")
//                    .firstName("Ragnar")
//                    .lastName("Lodbrok").build();
//
//            // create a user owner
//            UserSystem userSystemOwner = UserSystem.builder()
//                    .userName("KingRagnar")
//                    .password("Odin12")
//                    .firstName("Ragnar")
//                    .lastName("Lodbrok").build();
//            tradingSystemFacade.registerUser(userSystemOwner.getUserName(), userSystemOwner.getPassword(), userSystemOwner.getFirstName(), userSystemOwner.getLastName());
//            UserSystem updatedOwner = tradingSystem.getUser(userSystemOwner.getUserName(), uuid);
//
//            // create a store
//            tradingSystem.openStore(updatedOwner, new PurchasePolicy(), new DiscountPolicy(), "castro");
//            Store store = this.tradingSystem.getStoresList().stream().filter(store1 -> store1.getStoreName().equals("castro")).findFirst().get();
//
//            // create a product and call the function
//            //Product product = new Product("table", ProductCategory.HOME_GARDEN, 10, 100, 100000);
//            Assertions.assertFalse(tradingSystemFacade.addProduct(testUserSystem.getUserName(), store.getStoreId(), "table",
//                    ProductCategory.HOME_GARDEN.category, 10, 100));
//        }
//
//        /**
//         * check the deleteProduct() functionality in case of exists store and userOwner and productName in the system
//         */
//        @Test
//        @Disabled
//        void deleteProductFromStorePositive() {
//            // create a user owner
//            UserSystem userSystemOwner = UserSystem.builder()
//                    .userName("KingRagnar")
//                    .password("Odin12")
//                    .firstName("Ragnar")
//                    .lastName("Lodbrok").build();
//            tradingSystemFacade.registerUser(userSystemOwner.getUserName(), userSystemOwner.getPassword(), userSystemOwner.getFirstName(), userSystemOwner.getLastName());
//            UserSystem updatedOwner = tradingSystem.getUser(userSystemOwner.getUserName(), uuid);
//
//            // create a store
//            tradingSystem.openStore(updatedOwner, new PurchasePolicy(), new DiscountPolicy(), "castro");
//            Store store = this.tradingSystem.getStoresList().stream().filter(store1 -> store1.getStoreName().equals("castro")).findFirst().get();
//
//            // create a product adding it to the system
//            //Product product = new Product("table", ProductCategory.HOME_GARDEN, 10,100,store.getStoreId());
//            tradingSystemFacade.addProduct(updatedOwner.getUserName(), store.getStoreId(), "table",
//                    ProductCategory.HOME_GARDEN.category, 10, 100);
//
//            // call to the function
//            Assertions.assertTrue(tradingSystemFacade.deleteProductFromStore(updatedOwner.getUserName(),
//                    store.getStoreId(), 0));
//        }
//
//        /**
//         * check the deleteProduct() functionality in case of not exists store and exist userOwner and productName in the system
//         */
//        @Test
//        void deleteProductNotExistStore() {
//            // create a user owner
//            UserSystem userSystemOwner = UserSystem.builder()
//                    .userName("KingRagnar")
//                    .password("Odin12")
//                    .firstName("Ragnar")
//                    .lastName("Lodbrok").build();
//            tradingSystemFacade.registerUser(userSystemOwner.getUserName(), userSystemOwner.getPassword(), userSystemOwner.getFirstName(), userSystemOwner.getLastName());
//            UserSystem updatedOwner = tradingSystem.getUser(userSystemOwner.getUserName(), uuid);
//
//            // create a product adding it to the system
//            //Product product = new Product("table", ProductCategory.HOME_GARDEN, 10,100,store.getStoreId());
//            tradingSystemFacade.addProduct(updatedOwner.getUserName(), 100000, "table",
//                    ProductCategory.HOME_GARDEN.category, 10, 100);
//
//            // call to the function
//            Assertions.assertFalse(tradingSystemFacade.deleteProductFromStore(updatedOwner.getUserName(),
//                    100000, 0));
//        }
//
//        /**
//         * check the deleteProduct() functionality in case of not exists userOwner and exist store and productName in the system
//         */
//        @Test
//        void deleteProductNotExistUserOwner() {
//            // create a demo user
//            UserSystem testUserSystem = UserSystem.builder()
//                    .userName("KingRagnarTest")
//                    .password("Odin12")
//                    .firstName("Ragnar")
//                    .lastName("Lodbrok").build();
//
//            // create a user owner
//            UserSystem userSystemOwner = UserSystem.builder()
//                    .userName("KingRagnar")
//                    .password("Odin12")
//                    .firstName("Ragnar")
//                    .lastName("Lodbrok").build();
//            tradingSystemFacade.registerUser(userSystemOwner.getUserName(), userSystemOwner.getPassword(), userSystemOwner.getFirstName(), userSystemOwner.getLastName());
//            UserSystem updatedOwner = tradingSystem.getUser(userSystemOwner.getUserName(), uuid);
//
//            // create a store
//            tradingSystem.openStore(updatedOwner, new PurchasePolicy(), new DiscountPolicy(), "castro");
//            Store store = this.tradingSystem.getStoresList().stream().filter(store1 -> store1.getStoreName().equals("castro")).findFirst().get();
//
//            // create a product adding it to the system
//            //Product product = new Product("table", ProductCategory.HOME_GARDEN, 10,100,store.getStoreId());
//            tradingSystemFacade.addProduct(testUserSystem.getUserName(), store.getStoreId(), "table",
//                    ProductCategory.HOME_GARDEN.category, 10, 100);
//
//            // call to the function
//            Assertions.assertFalse(tradingSystemFacade.deleteProductFromStore(testUserSystem.getUserName(),
//                    store.getStoreId(), 0));
//
//        }
//
//        /**
//         * check the editProduct() functionality in case of exists store and userOwner and productName in the system
//         */
//        @Test
//        void editProductPositive() {
//            // create a user owner
//            UserSystem userSystemOwner = UserSystem.builder()
//                    .userName("KingRagnar")
//                    .password("Odin12")
//                    .firstName("Ragnar")
//                    .lastName("Lodbrok").build();
//            tradingSystemFacade.registerUser(userSystemOwner.getUserName(), userSystemOwner.getPassword(),
//                    userSystemOwner.getFirstName(), userSystemOwner.getLastName());
//            UserSystem updatedOwner = tradingSystem.getUser(userSystemOwner.getUserName(), uuid);
//
//            // create a store
//            tradingSystem.openStore(updatedOwner, new PurchasePolicy(), new DiscountPolicy(), "castro");
//            Store store = this.tradingSystem.getStoresList().stream().filter(store1 -> store1.getStoreName().equals("castro")).findFirst().get();
//
//            // create a product adding it to the system
//            //Product product = new Product("table", ProductCategory.HOME_GARDEN, 10,100,store.getStoreId());
//            tradingSystemFacade.addProduct(updatedOwner.getUserName(), store.getStoreId(), "table",
//                    ProductCategory.HOME_GARDEN.category, 10, 100);
//
//            // call to the function
//            Assertions.assertTrue(tradingSystemFacade.editProduct(updatedOwner.getUserName(), store.getStoreId(),
//                    store.products.stream().findFirst().get().getProductSn(), "table" + "vnjfkvj", ProductCategory.HOME_GARDEN.category, 10 + 10, 100, uuid));
//        }
//
//        /**
//         * check the editProduct() functionality in case of not exists store and exist userOwner and productName in the system
//         */
//        @Test
//        void editProductNotExistStore() {
//            // create a user owner
//            UserSystem userSystemOwner = UserSystem.builder()
//                    .userName("KingRagnar")
//                    .password("Odin12")
//                    .firstName("Ragnar")
//                    .lastName("Lodbrok").build();
//            tradingSystemFacade.registerUser(userSystemOwner.getUserName(), userSystemOwner.getPassword(), userSystemOwner.getFirstName(), userSystemOwner.getLastName());
//            UserSystem updatedOwner = tradingSystem.getUser(userSystemOwner.getUserName(), uuid);
//
//            // create a store
//            tradingSystem.openStore(updatedOwner, new PurchasePolicy(), new DiscountPolicy(), "castro");
//            Store store = this.tradingSystem.getStoresList().stream().filter(store1 -> store1.getStoreName().equals("castro")).findFirst().get();
//
//            // create a product adding it to the system
//            //Product product = new Product("table", ProductCategory.HOME_GARDEN, 10,100,store.getStoreId());
//            tradingSystemFacade.addProduct(updatedOwner.getUserName(), store.getStoreId(), "table",
//                    ProductCategory.HOME_GARDEN.category, 10, 100);
//
//            // call to the function
//            Assertions.assertFalse(tradingSystemFacade.editProduct(updatedOwner.getUserName(), 100000, 1,
//                    "table" + "vnjfkvj", ProductCategory.HOME_GARDEN.category, 10 + 10, 100, uuid));
//
//        }
//
//        /**
//         * check the editProduct() functionality in case of not exists userOwner and exist store and productName in the system
//         */
//        @Test
//        void editProductNotExistUserOwner() {
//            // create a demo user
//            UserSystem testUserSystem = UserSystem.builder()
//                    .userName("KingRagnarTest")
//                    .password("Odin12")
//                    .firstName("Ragnar")
//                    .lastName("Lodbrok").build();
//
//            // create a user owner
//            UserSystem userSystemOwner = UserSystem.builder()
//                    .userName("KingRagnar")
//                    .password("Odin12")
//                    .firstName("Ragnar")
//                    .lastName("Lodbrok").build();
//            tradingSystemFacade.registerUser(userSystemOwner.getUserName(), userSystemOwner.getPassword(), userSystemOwner.getFirstName(), userSystemOwner.getLastName());
//            UserSystem updatedOwner = tradingSystem.getUser(userSystemOwner.getUserName(), uuid);
//
//            // create a store
//            tradingSystem.openStore(updatedOwner, new PurchasePolicy(), new DiscountPolicy(), "castro");
//            Store store = this.tradingSystem.getStoresList().stream().filter(store1 -> store1.getStoreName().equals("castro")).findFirst().get();
//
//            // create a product adding it to the system
//            //Product product = new Product("table", ProductCategory.HOME_GARDEN, 10,100,store.getStoreId());
//            tradingSystemFacade.addProduct(testUserSystem.getUserName(), store.getStoreId(), "table",
//                    ProductCategory.HOME_GARDEN.category, 10, 100);
//
//            // call to the function
//            Assertions.assertFalse(tradingSystemFacade.editProduct(testUserSystem.getUserName(), store.getStoreId(), 1,
//                    "table" + "vnjfkvj", ProductCategory.HOME_GARDEN.category, 10 + 10, 100, uuid));
//        }
//
//        /**
//         * check the addOwner() functionality in case of exists store and userOwner and newUserOwner in the system
//         */
//        @Test
//        void addOwnerPositive() {
//            // create a user owner
//            UserSystem userSystemOwner = UserSystem.builder()
//                    .userName("KingRagnar")
//                    .password("Odin12")
//                    .firstName("Ragnar")
//                    .lastName("Lodbrok").build();
//            tradingSystemFacade.registerUser(userSystemOwner.getUserName(), userSystemOwner.getPassword(), userSystemOwner.getFirstName(), userSystemOwner.getLastName());
//            UserSystem updatedOwner = tradingSystem.getUser(userSystemOwner.getUserName(), uuid);
//
//            // create a new user owner
//            UserSystem userSystemOwnerNew = UserSystem.builder()
//                    .userName("KingRagnarOwnerNew")
//                    .password("Odin12OwnerNew")
//                    .firstName("RagnarOwnerNew")
//                    .lastName("LodbrokOwnerNew").build();
//            tradingSystemFacade.registerUser(userSystemOwnerNew.getUserName(), userSystemOwnerNew.getPassword(),
//                    userSystemOwnerNew.getFirstName(), userSystemOwnerNew.getLastName());
//            UserSystem updatedOwnerNew = tradingSystem.getUser(userSystemOwnerNew.getUserName(), uuid);
//
//            // create a store
//            tradingSystem.openStore(updatedOwner, new PurchasePolicy(), new DiscountPolicy(), "castro");
//            Store store = this.tradingSystem.getStoresList().stream().filter(store1 -> store1.getStoreName().equals("castro")).findFirst().get();
//
//            // call the function
//            Assertions.assertTrue(tradingSystemFacade.addOwner(updatedOwner.getUserName(), store.getStoreId(),
//                    updatedOwnerNew.getUserName(), uuid));
//        }
//
//        /**
//         * check the addOwner() functionality in case of not exists store and exist of userOwner and of newUserOwner
//         * in the system
//         */
//        @Test
//        void addOwnerNotExistStore() {
//            // create a user owner
//            UserSystem userSystemOwner = UserSystem.builder()
//                    .userName("KingRagnar")
//                    .password("Odin12")
//                    .firstName("Ragnar")
//                    .lastName("Lodbrok").build();
//            tradingSystemFacade.registerUser(userSystemOwner.getUserName(), userSystemOwner.getPassword(),
//                    userSystemOwner.getFirstName(), userSystemOwner.getLastName());
//            UserSystem updatedOwner = tradingSystem.getUser(userSystemOwner.getUserName(), uuid);
//
//            // create a new user owner
//            UserSystem userSystemOwnerNew = UserSystem.builder()
//                    .userName("KingRagnarOwnerNew")
//                    .password("Odin12OwnerNew")
//                    .firstName("RagnarOwnerNew")
//                    .lastName("LodbrokOwnerNew").build();
//            tradingSystemFacade.registerUser(userSystemOwnerNew.getUserName(), userSystemOwnerNew.getPassword(),
//                    userSystemOwnerNew.getFirstName(), userSystemOwnerNew.getLastName());
//            UserSystem updatedOwnerNew = tradingSystem.getUser(userSystemOwnerNew.getUserName(), uuid);
//
//            // call the function
//            Assertions.assertFalse(tradingSystemFacade.addOwner(updatedOwner.getUserName(), 1000000,
//                    updatedOwnerNew.getUserName(), uuid));
//
//        }
//
//        /**
//         * check the addOwner() functionality in case of not exists newUserOwner and exist of store and of userOwner
//         * in the system
//         */
//        @Test
//        void addOwnerNotExistUserNewOwner() {
//            // create a user owner
//            UserSystem userSystemOwner = UserSystem.builder()
//                    .userName("KingRagnar")
//                    .password("Odin12")
//                    .firstName("Ragnar")
//                    .lastName("Lodbrok").build();
//            tradingSystemFacade.registerUser(userSystemOwner.getUserName(), userSystemOwner.getPassword(),
//                    userSystemOwner.getFirstName(), userSystemOwner.getLastName());
//            UserSystem updatedOwner = tradingSystem.getUser(userSystemOwner.getUserName(), uuid);
//
//            // create a new user owner
//            UserSystem userSystemOwnerNew = UserSystem.builder()
//                    .userName("KingRagnarOwnerNeww")
//                    .password("Odin12OwnerNew")
//                    .firstName("RagnarOwnerNew")
//                    .lastName("LodbrokOwnerNew").build();
//
//            // create a store
//            tradingSystem.openStore(userSystemOwner, new PurchasePolicy(), new DiscountPolicy(), "castro");
//            Store store = this.tradingSystem.getStoresList().stream().filter(store1 -> store1.getStoreName().equals("castro")).findFirst().get();
//
//            // call the function
//            Assertions.assertFalse(tradingSystemFacade.addOwner(updatedOwner.getUserName(), store.getStoreId(),
//                    userSystemOwnerNew.getUserName(), uuid));
//        }
//
//        /**
//         * check the addManager() functionality in case of exists store and userOwner and newManagerUser in the system
//         */
//        @Test
//        void addManagerPositive() {
//            // create a user owner
//            UserSystem userSystemOwner = UserSystem.builder()
//                    .userName("KingRagnar")
//                    .password("Odin12")
//                    .firstName("Ragnar")
//                    .lastName("Lodbrok").build();
//            tradingSystemFacade.registerUser(userSystemOwner.getUserName(), userSystemOwner.getPassword(),
//                    userSystemOwner.getFirstName(), userSystemOwner.getLastName());
//            UserSystem updatedOwner = tradingSystem.getUser(userSystemOwner.getUserName(), uuid);
//
//            // create a user newManagerUser
//            UserSystem userSystemManager = UserSystem.builder()
//                    .userName("KingRagnarManager")
//                    .password("Odin12Manager")
//                    .firstName("RagnarManager")
//                    .lastName("LodbrokManager").build();
//            tradingSystemFacade.registerUser(userSystemManager.getUserName(), userSystemManager.getPassword(), userSystemManager.getFirstName(), userSystemManager.getLastName());
//            UserSystem updatedManager = tradingSystem.getUser(userSystemManager.getUserName(), uuid);
//
//            // create a store
//            tradingSystem.openStore(updatedOwner, new PurchasePolicy(), new DiscountPolicy(), "castro");
//            Store store = this.tradingSystem.getStoresList().stream().filter(store1 -> store1.getStoreName().equals("castro")).findFirst().get();
//
//            // call the function
//            Assertions.assertTrue(tradingSystemFacade.addManager(updatedOwner.getUserName(), store.getStoreId(),
//                    updatedManager.getUserName(), uuid));
//        }
//
//        /**
//         * check the addManager() functionality in case of not exists store and exist of userOwner and of newUserOwner
//         * in the system
//         */
//        @Test
//        void addManagerNotExistStore() {
//            // create a user owner
//            UserSystem userSystemOwner = UserSystem.builder()
//                    .userName("KingRagnar")
//                    .password("Odin12")
//                    .firstName("Ragnar")
//                    .lastName("Lodbrok").build();
//            tradingSystemFacade.registerUser(userSystemOwner.getUserName(), userSystemOwner.getPassword(),
//                    userSystemOwner.getFirstName(), userSystemOwner.getLastName());
//            UserSystem updatedOwner = tradingSystem.getUser(userSystemOwner.getUserName(), uuid);
//
//            // create a user newManagerUser
//            UserSystem userSystemManager = UserSystem.builder()
//                    .userName("KingRagnarManager")
//                    .password("Odin12Manager")
//                    .firstName("RagnarManager")
//                    .lastName("LodbrokManager").build();
//            tradingSystemFacade.registerUser(userSystemManager.getUserName(), userSystemManager.getPassword(), userSystemManager.getFirstName(), userSystemManager.getLastName());
//            UserSystem updatedManager = tradingSystem.getUser(userSystemManager.getUserName(), uuid);
//
//            // call the function
//            Assertions.assertFalse(tradingSystemFacade.addManager(updatedOwner.getUserName(), 100000,
//                    updatedManager.getUserName(), uuid));
//        }
//
//        /**
//         * check the addManager() functionality in case of not exists store and exist of userOwner and of newUserOwner
//         * in the system
//         */
//        @Test
//        void addManagerNotExistNewManager() {
//            // create a user owner
//            UserSystem userSystemOwner = UserSystem.builder()
//                    .userName("KingRagnar")
//                    .password("Odin12")
//                    .firstName("Ragnar")
//                    .lastName("Lodbrok").build();
//            tradingSystemFacade.registerUser(userSystemOwner.getUserName(), userSystemOwner.getPassword(),
//                    userSystemOwner.getFirstName(), userSystemOwner.getLastName());
//            UserSystem updatedOwner = tradingSystem.getUser(userSystemOwner.getUserName(), uuid);
//
//            // create a user newManagerUser
//            UserSystem userSystemManager = UserSystem.builder()
//                    .userName("KingRagnarNewManager")
//                    .password("Odin12Manager")
//                    .firstName("RagnarManager")
//                    .lastName("LodbrokManager").build();
//
//            // create a store
//            tradingSystem.openStore(updatedOwner, new PurchasePolicy(), new DiscountPolicy(), "castro");
//            Store store = this.tradingSystem.getStoresList().stream().filter(store1 -> store1.getStoreName().equals("castro")).findFirst().get();
//
//            // call the function
//            Assertions.assertFalse(tradingSystemFacade.addManager(updatedOwner.getUserName(), store.getStoreId(),
//                    userSystemManager.getUserName(), uuid));
//
//        }
//
//        /**
//         * check the addPermission() functionality in case of exists store and userOwner and userManager in the system
//         */
//        @Test
//        void addPermissionPositive() {
//            // create a user owner
//            UserSystem userSystemOwner = UserSystem.builder()
//                    .userName("KingRagnar")
//                    .password("Odin12")
//                    .firstName("Ragnar")
//                    .lastName("Lodbrok").build();
//            tradingSystemFacade.registerUser(userSystemOwner.getUserName(), userSystemOwner.getPassword(),
//                    userSystemOwner.getFirstName(), userSystemOwner.getLastName());
//            UserSystem updatedOwner = tradingSystem.getUser(userSystemOwner.getUserName(), uuid);
//
//            // create a user userManager
//            UserSystem userSystemManager = UserSystem.builder()
//                    .userName("KingRagnarManager")
//                    .password("Odin12Manager")
//                    .firstName("RagnarManager")
//                    .lastName("LodbrokManager").build();
//            tradingSystemFacade.registerUser(userSystemManager.getUserName(), userSystemManager.getPassword(), userSystemManager.getFirstName(), userSystemManager.getLastName());
//            UserSystem updatedManager = tradingSystem.getUser(userSystemManager.getUserName(), uuid);
//
//            // create a store
//            tradingSystem.openStore(updatedOwner, new PurchasePolicy(), new DiscountPolicy(), "castro");
//            Store store = this.tradingSystem.getStoresList().stream().filter(store1 -> store1.getStoreName().equals("castro")).findFirst().get();
//            // add userManager to manage the store
//            tradingSystemFacade.addManager(updatedOwner.getUserName(), store.getStoreId(), updatedManager.getUserName(), uuid);
//
//            // call the function
//            Assertions.assertTrue(tradingSystemFacade.addPermission(updatedOwner.getUserName(), store.getStoreId(),
//                    updatedManager.getUserName(), StorePermission.EDIT.function, uuid));
//        }
//
//        /**
//         * check the addPermission() functionality in case of not exists store and exist userOwner and userManager in the system
//         */
//        @Test
//        void addPermissionNotExistStore() {
//            // create a user owner
//            UserSystem userSystemOwner = UserSystem.builder()
//                    .userName("KingRagnar")
//                    .password("Odin12")
//                    .firstName("Ragnar")
//                    .lastName("Lodbrok").build();
//            tradingSystemFacade.registerUser(userSystemOwner.getUserName(), userSystemOwner.getPassword(),
//                    userSystemOwner.getFirstName(), userSystemOwner.getLastName());
//            UserSystem updatedOwner = tradingSystem.getUser(userSystemOwner.getUserName(), uuid);
//
//            // create a user userManager
//            UserSystem userSystemManager = UserSystem.builder()
//                    .userName("KingRagnarManager")
//                    .password("Odin12Manager")
//                    .firstName("RagnarManager")
//                    .lastName("LodbrokManager").build();
//            tradingSystemFacade.registerUser(userSystemManager.getUserName(), userSystemManager.getPassword(), userSystemManager.getFirstName(), userSystemManager.getLastName());
//            UserSystem updatedManager = tradingSystem.getUser(userSystemManager.getUserName(), uuid);
//
//            // call the function
//            Assertions.assertFalse(tradingSystemFacade.addPermission(updatedOwner.getUserName(), 100000,
//                    updatedManager.getUserName(), StorePermission.EDIT.function, uuid));
//
//        }
//        //////////////////////////////////////////////////////////////////////////
//
//        /**
//         * check the removeManager() functionality in case of exists store and userOwner and userManager in the system
//         */
//        @Test
//        void removeManager() {
//            // create a user owner
//            UserSystem userSystemOwner = UserSystem.builder()
//                    .userName("KingRagnarr")
//                    .password("Odin12")
//                    .firstName("Ragnar")
//                    .lastName("Lodbrok").build();
//            tradingSystemFacade.registerUser(userSystemOwner.getUserName(), userSystemOwner.getPassword(),
//                    userSystemOwner.getFirstName(), userSystemOwner.getLastName());
//            UserSystem updatedOwner = tradingSystem.getUser(userSystemOwner.getUserName(), uuid);
//
//            // create a user newManagerUser
//            UserSystem userSystemManager = UserSystem.builder()
//                    .userName("KingRagnarManagerr")
//                    .password("Odin12Manager")
//                    .firstName("RagnarManager")
//                    .lastName("LodbrokManager").build();
//            tradingSystemFacade.registerUser(userSystemManager.getUserName(), userSystemManager.getPassword(), userSystemManager.getFirstName(), userSystemManager.getLastName());
//            UserSystem updatedManager = tradingSystem.getUser(userSystemManager.getUserName(), uuid);
//
//            // create a store
//            tradingSystem.openStore(updatedOwner, new PurchasePolicy(), new DiscountPolicy(), "castroo");
//            Store store = this.tradingSystem.getStoresList().stream().filter(store1 -> store1.getStoreName().equals("castroo")).findFirst().get();
//
//            tradingSystemFacade.addManager(updatedOwner.getUserName(), store.getStoreId(),
//                    updatedManager.getUserName(), uuid);
//
//            // call the function
//            Assertions.assertTrue(tradingSystemFacade.removeManager(updatedOwner.getUserName(), store.getStoreId(),
//                    updatedManager.getUserName(), uuid));
//        }
//
//        /**
//         * check the logout() functionality in case of exists user that is login in the system
//         */
//        @Test
//        void logout() {
//            // create a user owner
//            UserSystem userSystem = UserSystem.builder()
//                    .userName("KingRagnar")
//                    .password("Odin12")
//                    .firstName("Ragnar")
//                    .lastName("Lodbrok").build();
//            tradingSystemFacade.registerUser(userSystem.getUserName(), userSystem.getPassword(),
//                    userSystem.getFirstName(), userSystem.getLastName());
//            UserSystem updatedUser = tradingSystem.getUser(userSystem.getUserName(), uuid);
//
//            // login the user
//            tradingSystemFacade.login(updatedUser.getUserName(), updatedUser.getPassword());
//
//            // call the function
//            Assertions.assertTrue(tradingSystemFacade.logout(updatedUser.getUserName()));
//        }
//
//        /**
//         * check the openStore() functionality in case of exists userOwner in the system
//         */
//        @Test
//        void openStore() {
//            // create a user owner
//            UserSystem userSystemOwner = UserSystem.builder()
//                    .userName("KingRagnarr")
//                    .password("Odin12")
//                    .firstName("Ragnar")
//                    .lastName("Lodbrok").build();
//            tradingSystemFacade.registerUser(userSystemOwner.getUserName(), userSystemOwner.getPassword(),
//                    userSystemOwner.getFirstName(), userSystemOwner.getLastName());
//            UserSystem updatedOwner = tradingSystem.getUser(userSystemOwner.getUserName(), uuid);
//
//            Assertions.assertTrue(tradingSystemFacade.openStore(updatedOwner.getUserName(), new PurchasePolicyDto(),
//                    new DiscountPolicyDto(), "zaraa"));
//        }
//
//        /**
//         * check the registerUser() functionality in case of not exists user in the system
//         */
//        @Test
//        void registerUser() {
//            Assertions.assertTrue(tradingSystemFacade.registerUser("moranush", "123", "moran", "neptune"));
//        }
//
//        /**
//         * check the login() functionality in case of exists user in the system
//         */
//        @Test
//        void login() {
//            // create a user owner
//            UserSystem userSystem = UserSystem.builder()
//                    .userName("KingRagnardd")
//                    .password("Odin12")
//                    .firstName("Ragnar")
//                    .lastName("Lodbrok").build();
//            tradingSystemFacade.registerUser(userSystem.getUserName(), userSystem.getPassword(),
//                    userSystem.getFirstName(), userSystem.getLastName());
//            UserSystem updatedUser = tradingSystem.getUser(userSystem.getUserName(), uuid);
//
//            // login the user
//            Assertions.assertTrue(tradingSystemFacade.login(updatedUser.getUserName(), "Odin12"));
//        }
//
//        /**
//         * check the viewStoreInfo() functionality in case of exists store in the system
//         */
//        @Test
//        void viewStoreInfo() {
//            // create a user owner
//            UserSystem userSystemOwner = UserSystem.builder()
//                    .userName("KingRagnar")
//                    .password("Odin12")
//                    .firstName("Ragnar")
//                    .lastName("Lodbrok").build();
//            tradingSystemFacade.registerUser(userSystemOwner.getUserName(), userSystemOwner.getPassword(),
//                    userSystemOwner.getFirstName(), userSystemOwner.getLastName());
//            UserSystem updatedOwner = tradingSystem.getUser(userSystemOwner.getUserName(), uuid);
//
//            // create a store
//            tradingSystem.openStore(updatedOwner, new PurchasePolicy(), new DiscountPolicy(), "castro");
//            Store store = this.tradingSystem.getStoresList().stream().filter(store1 -> store1.getStoreName().equals("castro")).findFirst().get();
//            store.setPurchaseType(PurchaseType.BUY_IMMEDIATELY);
//            store.setDiscountType(DiscountType.NONE);
//
//            // call the function
//            StoreDto storeDto = tradingSystemFacade.viewStoreInfo(store.getStoreId());
//            AssertionHelperTest.assertionStore(store, storeDto);
//        }
//
//        /**
//         * check the viewProduct() functionality in case of exists store in the system
//         */
//        @Test
//        void viewProduct() {
//            // create a user owner
//            UserSystem userSystemOwner = UserSystem.builder()
//                    .userName("KingRagnar")
//                    .password("Odin12")
//                    .firstName("Ragnar")
//                    .lastName("Lodbrok").build();
//            tradingSystemFacade.registerUser(userSystemOwner.getUserName(), userSystemOwner.getPassword(),
//                    userSystemOwner.getFirstName(), userSystemOwner.getLastName());
//            UserSystem updatedOwner = tradingSystem.getUser(userSystemOwner.getUserName(), uuid);
//
//            // create a store
//            tradingSystem.openStore(updatedOwner, new PurchasePolicy(), new DiscountPolicy(), "castro");
//            Store store = this.tradingSystem.getStoresList().stream().filter(store1 -> store1.getStoreName().equals("castro")).findFirst().get();
//
//            // add a product
//            tradingSystemFacade.addProduct(userSystemOwner.getUserName(), store.getStoreId(), "table",
//                    ProductCategory.HOME_GARDEN.category, 10, 100);
//            Product product = this.tradingSystem.getStoresList().stream().filter(store1 -> store1.getStoreId() == store.getStoreId())
//                    .findFirst().get().products.stream().filter(product1 -> product1.getName().equals("table")).findFirst().get();
//
//            ProductDto productDto = tradingSystemFacade.viewProduct(store.getStoreId(), product.getProductSn());
//            AssertionHelperTest.assertProduct(product, productDto);
//        }
//
//        /**
//         * check the searchProductByName() functionality in case of exists product in the system
//         */
//        @Test
//        void searchProductByName() {
//            // create a user owner
//            UserSystem userSystemOwner = UserSystem.builder()
//                    .userName("KingRagnar")
//                    .password("Odin12")
//                    .firstName("Ragnar")
//                    .lastName("Lodbrok").build();
//            tradingSystemFacade.registerUser(userSystemOwner.getUserName(), userSystemOwner.getPassword(),
//                    userSystemOwner.getFirstName(), userSystemOwner.getLastName());
//            UserSystem updatedOwner = tradingSystem.getUser(userSystemOwner.getUserName(), uuid);
//
//            // create a store
//            tradingSystem.openStore(updatedOwner, new PurchasePolicy(), new DiscountPolicy(), "castro");
//            Store store = this.tradingSystem.getStoresList().stream().filter(store1 -> store1.getStoreName().equals("castro")).findFirst().get();
//
//            // add a product
//            tradingSystemFacade.addProduct(userSystemOwner.getUserName(), store.getStoreId(), "table",
//                    ProductCategory.HOME_GARDEN.category, 10, 100);
//            Product product = this.tradingSystem.getStoresList().stream().filter(store1 -> store1.getStoreId() == store.getStoreId())
//                    .findFirst().get().products.stream().filter(product1 -> product1.getName().equals("table")).findFirst().get();
//
//            //call the function
//            List<ProductDto> productDtoList = tradingSystemFacade.searchProductByName(product.getName());
//            AssertionHelperTest.assertProduct(product, productDtoList.get(0));
//        }
//
//        /**
//         * check the searchProductByCategory() functionality in case of exists product in the system
//         */
//        @Test
//        void searchProductByCategory() {
//            // create a user owner
//            UserSystem userSystemOwner = UserSystem.builder()
//                    .userName("KingRagnar")
//                    .password("Odin12")
//                    .firstName("Ragnar")
//                    .lastName("Lodbrok").build();
//            tradingSystemFacade.registerUser(userSystemOwner.getUserName(), userSystemOwner.getPassword(),
//                    userSystemOwner.getFirstName(), userSystemOwner.getLastName());
//            UserSystem updatedOwner = tradingSystem.getUser(userSystemOwner.getUserName(), uuid);
//
//            // create a store
//            tradingSystem.openStore(updatedOwner, new PurchasePolicy(), new DiscountPolicy(), "castro");
//            Store store = this.tradingSystem.getStoresList().stream().filter(store1 -> store1.getStoreName().equals("castro")).findFirst().get();
//
//            // add a product
//            tradingSystemFacade.addProduct(userSystemOwner.getUserName(), store.getStoreId(), "pic",
//                    ProductCategory.COLLECTIBLES_ART.category, 10, 100);
//            Product product = this.tradingSystem.getStoresList().stream().filter(store1 -> store1.getStoreId() == store.getStoreId())
//                    .findFirst().get().products.stream().filter(product1 -> product1.getName().equals("pic")).findFirst().get();
//
//            //call the function
//            List<ProductDto> productDtoList = tradingSystemFacade.searchProductByCategory(product.getCategory().category);
//            AssertionHelperTest.assertProduct(product, productDtoList.get(0));
//        }
//
//        /**
//         * check the searchProductByKeyWords() functionality in case of exists product in the system
//         */
//        @Test
//        void searchProductByKeyWords() {
//            // create a user owner
//            UserSystem userSystemOwner = UserSystem.builder()
//                    .userName("KingRagnar")
//                    .password("Odin12")
//                    .firstName("Ragnar")
//                    .lastName("Lodbrok").build();
//            tradingSystemFacade.registerUser(userSystemOwner.getUserName(), userSystemOwner.getPassword(),
//                    userSystemOwner.getFirstName(), userSystemOwner.getLastName());
//            UserSystem updatedOwner = tradingSystem.getUser(userSystemOwner.getUserName(), uuid);
//
//            // create a store
//            tradingSystem.openStore(updatedOwner, new PurchasePolicy(), new DiscountPolicy(), "castro");
//            Store store = this.tradingSystem.getStoresList().stream().filter(store1 -> store1.getStoreName().equals("castro")).findFirst().get();
//
//            // add a product
//            tradingSystemFacade.addProduct(userSystemOwner.getUserName(), store.getStoreId(), "phone",
//                    ProductCategory.HOME_GARDEN.category, 10, 100);
//            Product product = this.tradingSystem.getStoresList().stream().filter(store1 -> store1.getStoreId() == store.getStoreId())
//                    .findFirst().get().products.stream().filter(product1 -> product1.getName().equals("phone")).findFirst().get();
//
//            //call the function
//            List<String> listKeyWords = new ArrayList<>();
//            listKeyWords.add(product.getName());
//            List<ProductDto> productDtoList = tradingSystemFacade.searchProductByKeyWords(listKeyWords);
//            AssertionHelperTest.assertProduct(product, productDtoList.get(0));
//        }
//
//        /**
//         * check the filterByRangePrice() functionality in case of exists products in the system
//         */
//        @Test
//        void filterByRangePrice() {
//            // create a user owner
//            UserSystem userSystemOwner = UserSystem.builder()
//                    .userName("KingRagnar")
//                    .password("Odin12")
//                    .firstName("Ragnar")
//                    .lastName("Lodbrok").build();
//            tradingSystemFacade.registerUser(userSystemOwner.getUserName(), userSystemOwner.getPassword(),
//                    userSystemOwner.getFirstName(), userSystemOwner.getLastName());
//            UserSystem updatedOwner = tradingSystem.getUser(userSystemOwner.getUserName(), uuid);
//
//            // create a store
//            tradingSystem.openStore(updatedOwner, new PurchasePolicy(), new DiscountPolicy(), "castro");
//            Store store = this.tradingSystem.getStoresList().stream().filter(store1 -> store1.getStoreName().equals("castro")).findFirst().get();
//
//            // add a product1
//            tradingSystemFacade.addProduct(userSystemOwner.getUserName(), store.getStoreId(), "table",
//                    ProductCategory.HOME_GARDEN.category, 10, 100);
//            Product product1 = this.tradingSystem.getStoresList().stream().filter(store1 -> store1.getStoreId() == store.getStoreId())
//                    .findFirst().get().products.stream().filter(product3 -> product3.getName().equals("table")).findFirst().get();
//
//            // add a product2
//            tradingSystemFacade.addProduct(userSystemOwner.getUserName(), store.getStoreId(), "desk",
//                    ProductCategory.HOME_GARDEN.category, 10, 50);
//            Product product2 = this.tradingSystem.getStoresList().stream().filter(store1 -> store1.getStoreId() == store.getStoreId())
//                    .findFirst().get().products.stream().filter(product3 -> product3.getName().equals("desk")).findFirst().get();
//
//            // arrange the actual list to be returned
//            List<ProductDto> productsToFilter = new ArrayList<>();
//            productsToFilter.add(new ProductDto(product1.getProductSn(), product1.getName(), product1.getCategory().category, product1.getAmount(),
//                    product1.getCost(), product1.getRank(), product1.getStoreId(), product1.getDiscountType(), product1.getPurchaseType()));
//            productsToFilter.add(new ProductDto(product2.getProductSn(), product2.getName(), product2.getCategory().category, product2.getAmount(),
//                    product2.getCost(), product2.getRank(), product2.getStoreId(), product2.getDiscountType(), product2.getPurchaseType()));
//            // call the function
//            List<ProductDto> productsFilteredActual = tradingSystemFacade.filterByRangePrice(productsToFilter, 50, 100);
//
//            // arrange the expected list to be returned
//            List<ProductDto> productsExpected = new ArrayList<>();
//            productsExpected.add(new ProductDto(product1.getProductSn(), product1.getName(), product1.getCategory().category, product1.getAmount(),
//                    product1.getCost(), product1.getRank(), product1.getStoreId(), product1.getDiscountType(), product1.getPurchaseType()));
//            productsExpected.add(new ProductDto(product2.getProductSn(), product2.getName(), product2.getCategory().category, product2.getAmount(),
//                    product2.getCost(), product2.getRank(), product2.getStoreId(), product2.getDiscountType(), product2.getPurchaseType()));
//
//            Assertions.assertEquals(productsExpected, productsFilteredActual);
//        }
//
//        /**
//         * check the filterByProductRank() functionality in case of exists products in the system
//         */
//        @Test
//        void filterByProductRank() {
//            // create a user owner
//            UserSystem userSystemOwner = UserSystem.builder()
//                    .userName("KingRagnar")
//                    .password("Odin12")
//                    .firstName("Ragnar")
//                    .lastName("Lodbrok").build();
//            tradingSystemFacade.registerUser(userSystemOwner.getUserName(), userSystemOwner.getPassword(),
//                    userSystemOwner.getFirstName(), userSystemOwner.getLastName());
//            UserSystem updatedOwner = tradingSystem.getUser(userSystemOwner.getUserName(), uuid);
//
//            // create a store
//            tradingSystem.openStore(updatedOwner, new PurchasePolicy(), new DiscountPolicy(), "castro");
//            Store store = this.tradingSystem.getStoresList().stream().filter(store1 -> store1.getStoreName().equals("castro")).findFirst().get();
//
//            // add a product1
//            tradingSystemFacade.addProduct(userSystemOwner.getUserName(), store.getStoreId(), "table",
//                    ProductCategory.HOME_GARDEN.category, 10, 100);
//            Product product1 = this.tradingSystem.getStoresList().stream().filter(store1 -> store1.getStoreId() == store.getStoreId())
//                    .findFirst().get().products.stream().filter(product3 -> product3.getName().equals("table")).findFirst().get();
//            product1.setRank(3);
//
//            // add a product2
//            tradingSystemFacade.addProduct(userSystemOwner.getUserName(), store.getStoreId(), "desk",
//                    ProductCategory.HOME_GARDEN.category, 10, 100);
//            Product product2 = this.tradingSystem.getStoresList().stream().filter(store1 -> store1.getStoreId() == store.getStoreId())
//                    .findFirst().get().products.stream().filter(product3 -> product3.getName().equals("desk")).findFirst().get();
//            product2.setRank(5);
//
//            // arrange the actual list to be returned
//            List<ProductDto> productsToFilter = new ArrayList<>();
//            productsToFilter.add(new ProductDto(product1.getProductSn(), product1.getName(), product1.getCategory().category, product1.getAmount(),
//                    product1.getCost(), product1.getRank(), product1.getStoreId(), product1.getDiscountType(), product1.getPurchaseType()));
//            productsToFilter.add(new ProductDto(product2.getProductSn(), product2.getName(), product2.getCategory().category, product2.getAmount(),
//                    product2.getCost(), product2.getRank(), product2.getStoreId(), product2.getDiscountType(), product2.getPurchaseType()));
//            // call the function
//            List<ProductDto> productsFilteredActual = tradingSystemFacade.filterByProductRank(productsToFilter, 5);
//
//            // arrange the expected list to be returned
//            List<ProductDto> productsExpected = new ArrayList<>();
//            productsExpected.add(new ProductDto(product2.getProductSn(), product2.getName(), product2.getCategory().category, product2.getAmount(),
//                    product2.getCost(), product2.getRank(), product2.getStoreId(), product2.getDiscountType(), product2.getPurchaseType()));
//
//            Assertions.assertEquals(productsExpected, productsFilteredActual);
//        }
//
//        /**
//         * check the filterByStoreRank() functionality in case of exists products in the system
//         */
//        @Test
//        void filterByStoreRank() {
//            // create a user owner
//            UserSystem userSystemOwner = UserSystem.builder()
//                    .userName("KingRagnar")
//                    .password("Odin12")
//                    .firstName("Ragnar")
//                    .lastName("Lodbrok").build();
//            tradingSystemFacade.registerUser(userSystemOwner.getUserName(), userSystemOwner.getPassword(),
//                    userSystemOwner.getFirstName(), userSystemOwner.getLastName());
//            UserSystem updatedOwner = tradingSystem.getUser(userSystemOwner.getUserName(), uuid);
//
//            // create a store1
//            tradingSystem.openStore(updatedOwner, new PurchasePolicy(), new DiscountPolicy(), "castro");
//            Store store1 = this.tradingSystem.getStoresList().stream().filter(store3 -> store3.getStoreName().equals("castro")).findFirst().get();
//            store1.setRank(3);
//            // create a store2
//            tradingSystem.openStore(updatedOwner, new PurchasePolicy(), new DiscountPolicy(), "dutiMoti");
//            Store store2 = this.tradingSystem.getStoresList().stream().filter(store3 -> store3.getStoreName().equals("dutiMoti")).findFirst().get();
//            store2.setRank(5);
//
//            // add a product1 for store1
//            tradingSystemFacade.addProduct(userSystemOwner.getUserName(), store1.getStoreId(), "table",
//                    ProductCategory.HOME_GARDEN.category, 10, 100);
//            Product product1 = this.tradingSystem.getStoresList().stream().filter(store3 -> store3.getStoreId() == store1.getStoreId())
//                    .findFirst().get().products.stream().filter(product3 -> product3.getName().equals("table")).findFirst().get();
//            // add a product1 for store2
//            tradingSystemFacade.addProduct(userSystemOwner.getUserName(), store2.getStoreId(), "table",
//                    ProductCategory.HOME_GARDEN.category, 10, 100);
//            Product product2 = this.tradingSystem.getStoresList().stream().filter(store3 -> store3.getStoreId() == store2.getStoreId())
//                    .findFirst().get().products.stream().filter(product3 -> product3.getName().equals("table")).findFirst().get();
//
//            // arrange the actual list to be returned
//            List<ProductDto> productsToFilter = new ArrayList<>();
//            productsToFilter.add(new ProductDto(product1.getProductSn(), product1.getName(), product1.getCategory().category, product1.getAmount(),
//                    product1.getCost(), product1.getRank(), product1.getStoreId(), product1.getDiscountType(), product1.getPurchaseType()));
//            productsToFilter.add(new ProductDto(product2.getProductSn(), product2.getName(), product2.getCategory().category, product2.getAmount(),
//                    product2.getCost(), product2.getRank(), product2.getStoreId(), product2.getDiscountType(), product2.getPurchaseType()));
//            // call the function
//            List<ProductDto> productsFilteredActual = tradingSystemFacade.filterByStoreRank(productsToFilter, 5);
//
//            // arrange the expected list to be returned
//            List<ProductDto> productsExpected = new ArrayList<>();
//            productsExpected.add(new ProductDto(product2.getProductSn(), product2.getName(), product2.getCategory().category, product2.getAmount(),
//                    product2.getCost(), product2.getRank(), product2.getStoreId(), product2.getDiscountType(), product2.getPurchaseType()));
//
//            Assertions.assertEquals(productsExpected, productsFilteredActual);
//
//        }
//
//        /**
//         * check the filterByStoreCategory() functionality in case of exists products in the system
//         */
//        @Test
//        void filterByStoreCategory() {
//            // create a user owner
//            UserSystem userSystemOwner = UserSystem.builder()
//                    .userName("KingRagnar")
//                    .password("Odin12")
//                    .firstName("Ragnar")
//                    .lastName("Lodbrok").build();
//            tradingSystemFacade.registerUser(userSystemOwner.getUserName(), userSystemOwner.getPassword(),
//                    userSystemOwner.getFirstName(), userSystemOwner.getLastName());
//            UserSystem updatedOwner = tradingSystem.getUser(userSystemOwner.getUserName(), uuid);
//
//            // create a store
//            tradingSystem.openStore(updatedOwner, new PurchasePolicy(), new DiscountPolicy(), "castro");
//            Store store = this.tradingSystem.getStoresList().stream().filter(store1 -> store1.getStoreName().equals("castro")).findFirst().get();
//
//            // add a product1
//            tradingSystemFacade.addProduct(userSystemOwner.getUserName(), store.getStoreId(), "table",
//                    ProductCategory.HEALTH.category, 10, 100);
//            Product product1 = this.tradingSystem.getStoresList().stream().filter(store1 -> store1.getStoreId() == store.getStoreId())
//                    .findFirst().get().products.stream().filter(product3 -> product3.getName().equals("table")).findFirst().get();
//
//            // add a product2
//            tradingSystemFacade.addProduct(userSystemOwner.getUserName(), store.getStoreId(), "desk",
//                    ProductCategory.HOME_GARDEN.category, 10, 100);
//            Product product2 = this.tradingSystem.getStoresList().stream().filter(store1 -> store1.getStoreId() == store.getStoreId())
//                    .findFirst().get().products.stream().filter(product3 -> product3.getName().equals("desk")).findFirst().get();
//
//            // arrange the actual list to be returned
//            List<ProductDto> productsToFilter = new ArrayList<>();
//            productsToFilter.add(new ProductDto(product1.getProductSn(), product1.getName(), product1.getCategory().category, product1.getAmount(),
//                    product1.getCost(), product1.getRank(), product1.getStoreId(), product1.getDiscountType(), product1.getPurchaseType()));
//            productsToFilter.add(new ProductDto(product2.getProductSn(), product2.getName(), product2.getCategory().category, product2.getAmount(),
//                    product2.getCost(), product2.getRank(), product2.getStoreId(), product2.getDiscountType(), product2.getPurchaseType()));
//            // call the function
//            List<ProductDto> productsFilteredActual = tradingSystemFacade.filterByStoreCategory(productsToFilter, ProductCategory.HOME_GARDEN.category);
//
//            // arrange the expected list to be returned
//            List<ProductDto> productsExpected = new ArrayList<>();
//            productsExpected.add(new ProductDto(product2.getProductSn(), product2.getName(), product2.getCategory().category, product2.getAmount(),
//                    product2.getCost(), product2.getRank(), product2.getStoreId(), product2.getDiscountType(), product2.getPurchaseType()));
//
//            Assertions.assertEquals(productsExpected, productsFilteredActual);
//        }
//
//        @Test
//        void saveProductInShoppingBag() {
//            // create a user owner
//            UserSystem userSystemOwner = UserSystem.builder()
//                    .userName("KingRagnar")
//                    .password("Odin12")
//                    .firstName("Ragnar")
//                    .lastName("Lodbrok").build();
//            tradingSystemFacade.registerUser(userSystemOwner.getUserName(), userSystemOwner.getPassword(),
//                    userSystemOwner.getFirstName(), userSystemOwner.getLastName());
//            UserSystem updatedOwner = tradingSystem.getUser(userSystemOwner.getUserName(), uuid);
//
//            // create a store
//            tradingSystem.openStore(updatedOwner, new PurchasePolicy(), new DiscountPolicy(), "castro");
//            Store store = this.tradingSystem.getStoresList().stream().filter(store1 -> store1.getStoreName().equals("castro")).findFirst().get();
//
//            // add a product1
//            tradingSystemFacade.addProduct(userSystemOwner.getUserName(), store.getStoreId(), "table",
//                    ProductCategory.HEALTH.category, 10, 100);
//            Product product1 = this.tradingSystem.getStoresList().stream().filter(store1 -> store1.getStoreId() == store.getStoreId())
//                    .findFirst().get().products.stream().filter(product3 -> product3.getName().equals("table")).findFirst().get();
//
//            Assertions.assertTrue(tradingSystemFacade.saveProductInShoppingBag(userSystemOwner.getUserName(), store.getStoreId(), product1.getProductSn(), 1));
//        }
//
//        @Test
//        void viewProductsInShoppingCart() {
//            // create a user owner
//            UserSystem userSystemOwner = UserSystem.builder()
//                    .userName("KingRagnar")
//                    .password("Odin12")
//                    .firstName("Ragnar")
//                    .lastName("Lodbrok").build();
//            tradingSystemFacade.registerUser(userSystemOwner.getUserName(), userSystemOwner.getPassword(),
//                    userSystemOwner.getFirstName(), userSystemOwner.getLastName());
//            UserSystem updatedOwner = tradingSystem.getUser(userSystemOwner.getUserName(), uuid);
//            ShoppingCart shoppingCart = setUpShoppingCart();
//
//            ShoppingCartDto shoppingCartDto = tradingSystemFacade.viewProductsInShoppingCart(userSystemOwner.getUserName());
//            AssertionHelperTest.assertShoppingCart(shoppingCart, shoppingCartDto);
//        }
//
//        @Test
//        void removeProductInShoppingBag() {
//            // create a user owner
//            UserSystem userSystemOwner = setUpOwnerStore();
//            tradingSystemFacade.registerUser(userSystemOwner.getUserName(), userSystemOwner.getPassword(),
//                    userSystemOwner.getFirstName(), userSystemOwner.getLastName());
//            UserSystem updatedOwner = tradingSystem.getUser(userSystemOwner.getUserName(), uuid);
//
//            // create a store
//            tradingSystem.openStore(updatedOwner, new PurchasePolicy(), new DiscountPolicy(), "castro");
//            Store store = this.tradingSystem.getStoresList().stream().filter(store1 -> store1.getStoreName().equals("castro")).findFirst().get();
//
//            // add a product1
//            tradingSystemFacade.addProduct(updatedOwner.getUserName(), store.getStoreId(), "table",
//                    ProductCategory.HEALTH.category, 10, 100);
//            Product product1 = this.tradingSystem.getStoresList().stream().filter(store1 -> store1.getStoreId() == store.getStoreId())
//                    .findFirst().get().products.stream().filter(product3 -> product3.getName().equals("table")).findFirst().get();
//
//            // add the product to the shopping bag
//            updatedOwner.saveProductInShoppingBag(store, product1, 1);
//
//            Assertions.assertTrue(tradingSystemFacade.removeProductInShoppingBag(updatedOwner.getUserName(), store.getStoreId(), product1.getProductSn()));
//        }
//
//
//        @Test
//        void purchaseShoppingCart() {
//            PaymentDetails paymentDetails = new PaymentDetails();
//            BillingAddress billingAddress = new BillingAddress();
//
//            // create a user owner
//            UserSystem userSystemOwner = setUpOwnerStore();
//            tradingSystemFacade.registerUser(userSystemOwner.getUserName(), userSystemOwner.getPassword(),
//                    userSystemOwner.getFirstName(), userSystemOwner.getLastName());
//            UserSystem updatedOwner = tradingSystem.getUser(userSystemOwner.getUserName(), uuid);
//
//            // create a store
//            tradingSystem.openStore(updatedOwner, new PurchasePolicy(), new DiscountPolicy(), "castro");
//            Store store = this.tradingSystem.getStoresList().stream().filter(store1 -> store1.getStoreName().equals("castro")).findFirst().get();
//
//            // add a product1
//            tradingSystemFacade.addProduct(updatedOwner.getUserName(), store.getStoreId(), "table",
//                    ProductCategory.HEALTH.category, 10, 100);
//            Product product1 = this.tradingSystem.getStoresList().stream().filter(store1 -> store1.getStoreId() == store.getStoreId())
//                    .findFirst().get().products.stream().filter(product3 -> product3.getName().equals("table")).findFirst().get();
//
//            // add the product to the shopping bag
//            updatedOwner.saveProductInShoppingBag(store, product1, 1);
//
//
//            ShoppingCartDto shoppingCartDto = modelMapper.map(updatedOwner.getShoppingCart(), ShoppingCartDto.class);
//            PaymentDetailsDto paymentDetailsDto = modelMapper.map(paymentDetails, PaymentDetailsDto.class);
//            BillingAddressDto billingAddressDto = modelMapper.map(billingAddress, BillingAddressDto.class);
//            List<ReceiptDto> actualReceipt = tradingSystemFacade.purchaseShoppingCart(shoppingCartDto, paymentDetailsDto, billingAddressDto);
//
//            Map<Product, Integer> productsBought = new HashMap<>();
//            productsBought.put(product1, 1);
//            Receipt expectedReceipt = new Receipt(store.getStoreId(), updatedOwner.getUserName(), product1.getCost(), productsBought);
//            //todo
//            //assertionReceipt(expectedReceipt, actualReceipt);
//        }
//
//        @Test
//        void testPurchaseShoppingCart() {
//            //setUp //TODO
//            PaymentDetails paymentDetails = setUpPaymentDetails();
//            BillingAddress billingAddress = setUpBillingAddress();
//            UserSystem userSystemOwner = setUpOwnerStore();
//            //initial
//            tradingSystemFacade.registerUser(userSystemOwner.getUserName(), userSystemOwner.getPassword(),
//                    userSystemOwner.getFirstName(), userSystemOwner.getLastName());
//            UserSystem regUser = tradingSystem.getUser(userSystemOwner.getUserName(), uuid);
//            tradingSystem.openStore(regUser, new PurchasePolicy(), new DiscountPolicy(), "castro");
//            Store store = this.tradingSystem.getStoresList().stream().filter(store1 -> store1.getStoreName().equals("castro")).findFirst().get();
//            tradingSystemFacade.addProduct(regUser.getUserName(), store.getStoreId(), "table",
//                    ProductCategory.HEALTH.category, 10, 100);
//            Product product = this.tradingSystem.getStoresList().stream().filter(store1 -> store1.getStoreId() == store.getStoreId())
//                    .findFirst().get().products.stream().filter(product3 -> product3.getName().equals("table")).findFirst().get();
//            // add the product to the shopping bag
//            regUser.saveProductInShoppingBag(store, product, 1);
//
//            // for call the function
//            PaymentDetailsDto paymentDetailsDto = modelMapper.map(paymentDetails, PaymentDetailsDto.class);
//            BillingAddressDto billingAddressDto = modelMapper.map(billingAddress, BillingAddressDto.class);
//            //ReceiptDto actualReceipt = tradingSystemFacade.purchaseShoppingCart(regUser.getUserName(), paymentDetailsDto, billingAddressDto);
//
//            Map<Product, Integer> productsBought = new HashMap<>();
//            productsBought.put(product, 1);
//            Receipt expectedReceipt = new Receipt(store.getStoreId(), regUser.getUserName(), product.getCost(), productsBought);
//
//            //assertionReceipt(expectedReceipt, actualReceipt);
//        }
//    }
//
//
//
//
//
//    // ******************************* General ******************************* //
//    private List<ProductDto> convertProductDtoList(List<Product> products) {
//        Type listType = new TypeToken<List<ProductDto>>() {
//        }.getType();
//        return modelMapper.map(products, listType);
//    }
//
//    private List<ReceiptDto> convertReceiptDtoList(@NotNull List<@NotNull Receipt> receipts) {
//        Type listType = new TypeToken<List<ReceiptDto>>(){}.getType();
//        return modelMapper.map(receipts, listType);
//    }
//}
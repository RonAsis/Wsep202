package com.wsep202.TradingSystem.domain.trading_system_management;

import com.github.rozidan.springboot.modelmapper.WithModelMapper;
import com.wsep202.TradingSystem.domain.config.TradingSystemConfiguration;
import com.wsep202.TradingSystem.domain.factory.FactoryObjects;
import com.wsep202.TradingSystem.domain.mapping.TradingSystemMapper;
import com.wsep202.TradingSystem.service.user_service.dto.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.validation.constraints.NotNull;
import java.lang.reflect.Type;
import java.util.*;

import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@WithModelMapper(basePackageClasses = TradingSystemMapper.class)
class TradingSystemFacadeTest {

    @Autowired
    private ModelMapper modelMapper;

    @Nested
    public class TradingSystemFacadeTestUnit {

        private TradingSystem tradingSystem;

        private TradingSystemFacade tradingSystemFacade;

        private UserSystem userSystem;

        private Store store;

        private FactoryObjects factoryObjects;

        private PaymentDetails paymentDetails;

        private BillingAddress billingAddress;

        @BeforeEach
        public void setUp() {
            store = mock(Store.class);
            userSystem = mock(UserSystem.class);
            tradingSystem = mock(TradingSystem.class);
            factoryObjects = mock(FactoryObjects.class);
            tradingSystemFacade = new TradingSystemFacade(tradingSystem, modelMapper, factoryObjects);
            paymentDetails = mock(PaymentDetails.class);
            billingAddress = mock(BillingAddress.class);
        }

        @Test
        void viewPurchaseHistoryUser() {
            List<Receipt> receipts = setUpReceipts();
            String userNameTest = "userNameTest";
            when(tradingSystem.getUser(userNameTest)).thenReturn(userSystem);
            when(userSystem.getReceipts()).thenReturn(receipts);
            List<ReceiptDto> receiptDtos = tradingSystemFacade.viewPurchaseHistory(userNameTest);
            assertReceipts(receipts, receiptDtos);
        }

        @Test
        void viewPurchaseHistoryAdministratorOfStore() {
            //initial
            List<Receipt> receipts = setUpReceipts();
            Store store = mock(Store.class);
            String administratorUsername = "administratorUsername";
            int storeId = 1;

            //mock
            when(tradingSystem.getStoreByAdmin(administratorUsername, storeId)).thenReturn(store);
            when(store.getReceipts()).thenReturn(receipts);

            //test
            List<ReceiptDto> receiptDtos = tradingSystemFacade.viewPurchaseHistory(administratorUsername, storeId);
            assertReceipts(receipts, receiptDtos);
        }

        @Test
        void viewPurchaseHistoryAdministratorOfUser() {
            //initial
            List<Receipt> receipts = setUpReceipts();
            String administratorUsername = "administratorUsername";
            String username = "usernameTest";

            //mock
            when(tradingSystem.getUserByAdmin(administratorUsername, username)).thenReturn(userSystem);
            when(userSystem.getReceipts()).thenReturn(receipts);

            //test
            List<ReceiptDto> receiptDtos = tradingSystemFacade.viewPurchaseHistory(administratorUsername, username);
            assertReceipts(receipts, receiptDtos);
        }

        @Test
        void viewPurchaseHistoryOfManager() {
            //initial
            List<Receipt> receipts = setUpReceipts();
            String managerUsername = "managerUsername";
            int storeId = 1;

            //mock
            when(tradingSystem.getUser(managerUsername)).thenReturn(userSystem);
            when(userSystem.getManagerStore(storeId)).thenReturn(store);
            when(store.getReceipts()).thenReturn(receipts);

            //test
            List<ReceiptDto> receiptDtos = tradingSystemFacade.viewPurchaseHistoryOfManager(managerUsername, storeId);
            assertReceipts(receipts, receiptDtos);
        }

        @Test
        void viewPurchaseHistoryOfOwner() {
            //initial
            List<Receipt> receipts = setUpReceipts();
            String ownerUsername = "ownerUsername";
            int storeId = 1;

            //mock
            when(tradingSystem.getUser(ownerUsername)).thenReturn(userSystem);
            when(userSystem.getOwnerStore(storeId)).thenReturn(store);
            when(store.getReceipts()).thenReturn(receipts);

            //test
            List<ReceiptDto> receiptDtos = tradingSystemFacade.viewPurchaseHistoryOfOwner(ownerUsername, storeId);
            assertReceipts(receipts, receiptDtos);
        }

        @Test
        void addProduct() {
            //initial
            String ownerUsername = "ownerUsername";
            int storeId = 1;

            //mock
            when(tradingSystem.getUser(ownerUsername)).thenReturn(userSystem);
            when(userSystem.getOwnerStore(storeId)).thenReturn(store);
            when(store.addNewProduct(any(), any())).thenReturn(true);

            //test
            Arrays.stream(ProductCategory.values()).forEach(productCategory -> {
                String productName = "productName " + productCategory.category;
                Assertions.assertTrue(tradingSystemFacade
                        .addProduct(ownerUsername, storeId, productName,
                                productCategory.category, 1, 1));
            });
        }

        @Test
        void deleteProductFromStore() {
            //initial
            String ownerUsername = "ownerUsername";
            int storeId = 1;
            int productSn = 1;

            //mock
            when(tradingSystem.getUser(ownerUsername)).thenReturn(userSystem);
            when(userSystem.getOwnerStore(storeId)).thenReturn(store);
            when(store.removeProductFromStore(userSystem, productSn)).thenReturn(true);

            Assertions.assertTrue(tradingSystemFacade
                    .deleteProductFromStore(ownerUsername, storeId, productSn));
        }

        @Test
        void editProduct() {
            //initial
            String ownerUsername = "ownerUsername";
            int storeId = 1;
            int productSn = 1;
            String productName = "productName";
            String category = ProductCategory.values()[0].category;
            int amount = 1;
            double cost = 3434;

            //mock
            when(tradingSystem.getUser(ownerUsername)).thenReturn(userSystem);
            when(userSystem.getOwnerStore(storeId)).thenReturn(store);
            when(store.editProduct(userSystem, productSn, productName, category, amount, cost)).thenReturn(true);

            //test
            Assertions.assertTrue(tradingSystemFacade
                    .editProduct(ownerUsername, storeId, productSn, productName, category, amount, cost));
        }

        @Test
        void addOwner() {
            //initial
            String ownerUsername = "ownerUsername";
            String newOwnerUsername = "newOwnerUsername";

            int storeId = 1;

            //mock
            UserSystem newOwner = mock(UserSystem.class);
            when(tradingSystem.getUser(ownerUsername)).thenReturn(userSystem);
            when(tradingSystem.getUser(newOwnerUsername)).thenReturn(newOwner);
            when(userSystem.getOwnerStore(storeId)).thenReturn(store);
            when(tradingSystem.addOwnerToStore(store, userSystem, newOwner)).thenReturn(true);

            //test
            Assertions.assertTrue(tradingSystemFacade
                    .addOwner(ownerUsername, storeId, newOwnerUsername));
        }

        @Test
        void addManager() {
            //initial
            String ownerUsername = "ownerUsername";
            String newManagerUsername = "newManagerUsername";

            int storeId = 1;

            //mock
            UserSystem newOwner = mock(UserSystem.class);
            when(tradingSystem.getUser(ownerUsername)).thenReturn(userSystem);
            when(tradingSystem.getUser(newManagerUsername)).thenReturn(newOwner);
            when(userSystem.getOwnerStore(storeId)).thenReturn(store);
            when(tradingSystem.addMangerToStore(store, userSystem, newOwner)).thenReturn(true);

            //test
            Assertions.assertTrue(tradingSystemFacade
                    .addManager(ownerUsername, storeId, newManagerUsername));
        }

        @Test
        void addPermission() {
            //initial
            String ownerUsername = "ownerUsername";
            String managerUsername = "newManagerUsername";
            String storePermission = StorePermission.VIEW.function;
            int storeId = 1;

            //mock
            UserSystem managerStore = mock(UserSystem.class);
            when(tradingSystem.getUser(ownerUsername)).thenReturn(userSystem);
            when(userSystem.getOwnerStore(storeId)).thenReturn(store);
            when(store.getManager(userSystem, managerUsername)).thenReturn(managerStore);
            when(store.addPermissionToManager(userSystem, managerStore, StorePermission.getStorePermission(storePermission)))
                    .thenReturn(true);
            //test
            Assertions.assertTrue(tradingSystemFacade
                    .addPermission(ownerUsername, storeId, managerUsername, storePermission));
        }

        @Test
        void removeManager() {
            //initial
            String ownerUsername = "ownerUsername";
            String managerUsername = "newManagerUsername";
            int storeId = 1;

            //mock
            UserSystem managerStore = mock(UserSystem.class);
            when(tradingSystem.getUser(ownerUsername)).thenReturn(userSystem);
            when(userSystem.getOwnerStore(storeId)).thenReturn(store);
            when(store.getManager(userSystem, managerUsername)).thenReturn(managerStore);
            when(tradingSystem.removeManager(store, userSystem, managerStore)).thenReturn(true);
            //test
            Assertions.assertTrue(tradingSystemFacade
                    .removeManager(ownerUsername, storeId, managerUsername));
        }

        @Test
        void logout() {
            //initial
            String userName = "username";

            //mock
            when(tradingSystem.getUser(userName)).thenReturn(userSystem);
            when(userSystem.logout()).thenReturn(true);

            //test
            Assertions.assertTrue(tradingSystemFacade
                    .logout(userName));
        }

        @Test
        void openStore() {
            //init
            String ownerUsername = "ownerUsername";
            PurchasePolicyDto purchasePolicyDto = PurchasePolicyDto.builder().build();
            DiscountPolicyDto discountPolicyDto = DiscountPolicyDto.builder().build();
            String discountType = DiscountType.NONE.type;
            String purchaseType = PurchaseType.BUY_IMMEDIATELY.type;
            String storeName = "storeName";

            //mock
            ModelMapper modelMapper = mock(ModelMapper.class);
            tradingSystemFacade = new TradingSystemFacade(tradingSystem, modelMapper, factoryObjects);
            PurchasePolicy purchasePolicy = mock(PurchasePolicy.class);
            DiscountPolicy discountPolicy = mock(DiscountPolicy.class);

            when(tradingSystem.getUser(ownerUsername)).thenReturn(userSystem);
            when(modelMapper.map(purchasePolicyDto, PurchasePolicy.class)).thenReturn(purchasePolicy);
            when(modelMapper.map(discountPolicyDto, DiscountPolicy.class)).thenReturn(discountPolicy);
            when(tradingSystem.openStore(userSystem, purchasePolicy, discountPolicy, storeName)).thenReturn(true);

            //test
            // Assertions.assertTrue(tradingSystemFacade
            //    .openStore(ownerUsername, purchasePolicyDto, discountPolicyDto, discountType, purchaseType, storeName));
        }

        @Test
        void registerUser() {
            String userName = "username";
            String password = "password";
            String firstName = "firstName";
            String lastName = "lastName";
            when(factoryObjects.createSystemUser(userName, firstName, lastName, password)).thenReturn(userSystem);
            when(tradingSystem.registerNewUser(userSystem)).thenReturn(true);

            //test
            Assertions.assertTrue(tradingSystemFacade
                    .registerUser(userName, password, firstName, lastName));
        }

        @Test
        void login() {
            //initial
            String userName = "username";
            String password = "password";

            //mock
            when(tradingSystem.getUser(userName)).thenReturn(userSystem);
            when(tradingSystem.login(userSystem, false, password)).thenReturn(true);

            //test
            Assertions.assertTrue(tradingSystemFacade
                    .login(userName, password));
        }

        @Test
        void viewStoreInfo() {
            //init
            Store store = createStore();
            int storeId = 1;

            //mock
            when(tradingSystem.getStore(storeId)).thenReturn(store);
            StoreDto storeDto = tradingSystemFacade.viewStoreInfo(storeId);
            assertionStore(store, storeDto);
        }

        @Test
        void viewProduct() {
            //init
            int storeId = 1;
            int productId = 1;
            Product product = createProduct(productId);

            //mock
            when(tradingSystem.getStore(storeId)).thenReturn(store);
            when(store.getProduct(productId)).thenReturn(product);
            ProductDto productDto = tradingSystemFacade.viewProduct(storeId, productId);
            assertProduct(product, productDto);
        }

        @Test
        void searchProductByName() {
            List<Product> products = new ArrayList<>(setUpProducts());
            String productName = "productName";
            when(tradingSystem.searchProductByName(productName)).thenReturn(products);
            List<ProductDto> productDtos = tradingSystemFacade.searchProductByName(productName);
            assertProducts(new HashSet<>(products), new HashSet<>(productDtos));
        }

        @Test
        void searchProductByCategory() {
            String category = ProductCategory.values()[0].category;
            List<Product> products = new ArrayList<>(setUpProducts());
            when(tradingSystem.searchProductByCategory(ProductCategory.getProductCategory(category))).thenReturn(products);
            List<ProductDto> productDtos = tradingSystemFacade.searchProductByCategory(category);
            assertProducts(new HashSet<>(products), new HashSet<>(productDtos));
        }

        @Test
        void searchProductByKeyWords() {
            List<Product> products = new ArrayList<>(setUpProducts());
            List<String> keyWords = new LinkedList<>();
            keyWords.add("test-key-words");
            when(tradingSystem.searchProductByKeyWords(keyWords)).thenReturn(products);
            List<ProductDto> productDtos = tradingSystemFacade.searchProductByKeyWords(keyWords);
            assertProducts(new HashSet<>(products), new HashSet<>(productDtos));
        }

        @Test
        void filterByRangePrice() {
            int minPrice = 0;
            int maxPrice = 10;
            List<Product> products = new ArrayList<>(setUpProducts());
            List<ProductDto> productsDtoArg = convertProductDtoList(products);
            when(tradingSystem.filterByRangePrice(products, minPrice, maxPrice)).thenReturn(products);
            List<ProductDto> productDtos = tradingSystemFacade.filterByRangePrice(productsDtoArg, minPrice, maxPrice);
            assertProducts(new HashSet<>(products), new HashSet<>(productDtos));
        }

        @Test
        void filterByProductRank() {
            int rank = 0;
            List<Product> products = new ArrayList<>(setUpProducts());
            List<ProductDto> productsDtoArg = convertProductDtoList(products);
            when(tradingSystem.filterByProductRank(products, rank)).thenReturn(products);
            List<ProductDto> productDtos = tradingSystemFacade.filterByProductRank(productsDtoArg, rank);
            assertProducts(new HashSet<>(products), new HashSet<>(productDtos));
        }

        @Test
        void filterByStoreRank() {
            int rank = 0;
            List<Product> products = new ArrayList<>(setUpProducts());
            List<ProductDto> productsDtoArg = convertProductDtoList(products);
            when(tradingSystem.filterByStoreRank(products, rank)).thenReturn(products);
            List<ProductDto> productDtos = tradingSystemFacade.filterByStoreRank(productsDtoArg, rank);
            assertProducts(new HashSet<>(products), new HashSet<>(productDtos));
        }

        @Test
        void filterByStoreCategory() {
            String category = ProductCategory.values()[0].category;
            List<Product> products = new ArrayList<>(setUpProducts());
            List<ProductDto> productsDtoArg = convertProductDtoList(products);
            when(tradingSystem.filterByStoreCategory(products, ProductCategory.getProductCategory(category))).thenReturn(products);
            List<ProductDto> productDtos = tradingSystemFacade.filterByStoreCategory(productsDtoArg, category);
            assertProducts(new HashSet<>(products), new HashSet<>(productDtos));
        }

        @Test
        void saveProductInShoppingBag() {
            //initial
            String username = "username";
            int storeId = 1;
            int productSn = 1;
            int amount = 1;

            //mock
            Product product = mock(Product.class);
            when(tradingSystem.getUser(username)).thenReturn(userSystem);
            when(tradingSystem.getStore(storeId)).thenReturn(store);
            when(store.getProduct(productSn)).thenReturn(product);
            when(userSystem.saveProductInShoppingBag(store, product, amount)).thenReturn(true);

            Assertions.assertTrue(tradingSystemFacade.saveProductInShoppingBag(username, storeId, productSn, amount));
        }

        @Test
        void viewProductsInShoppingCart() {
            //init
            String username = "username";
            ShoppingCart shoppingCart = createShoppingCart();
            //mock
            when(tradingSystem.getUser(username)).thenReturn(userSystem);
            when(userSystem.getShoppingCart()).thenReturn(shoppingCart);
            ShoppingCartDto shoppingCartDto = tradingSystemFacade.viewProductsInShoppingCart(username);
            assertShoppingCart(shoppingCart, shoppingCartDto);
        }

        @Test
        void removeProductInShoppingBag() {
            //initial
            String username = "username";
            int storeId = 1;
            int productSn = 1;

            //mock
            Product product = mock(Product.class);
            when(tradingSystem.getUser(username)).thenReturn(userSystem);
            when(tradingSystem.getStore(storeId)).thenReturn(store);
            when(store.getProduct(productSn)).thenReturn(product);
            when(userSystem.removeProductInShoppingBag(store, product)).thenReturn(true);
            Assertions.assertTrue(userSystem.removeProductInShoppingBag(store, product));
        }

        @Test
        void purchaseShoppingCart() {
            //init
            PaymentDetailsDto paymentDetailsDto = modelMapper.map(paymentDetails, PaymentDetailsDto.class);
            BillingAddressDto billingAddressDto = modelMapper.map(billingAddress, BillingAddressDto.class);
            List<Receipt> receiptsExpected = setUpReceipts();
            List<ReceiptDto> receiptDtos = convertReceiptDtoList(receiptsExpected);
            //mock
            when(tradingSystem.getUser(userSystem.getUserName())).thenReturn(userSystem);
            when(tradingSystem.purchaseShoppingCart(paymentDetails, billingAddress, userSystem)).thenReturn(receiptsExpected);

            List<ReceiptDto> receiptDtoAcutal = tradingSystemFacade.purchaseShoppingCart(userSystem.getUserName(), paymentDetailsDto, billingAddressDto);
            assertReceipts(receiptsExpected, receiptDtoAcutal);
        }

        @Test
        void purchaseShoppingCartTest() {

        }
    }

    // ******************************* integration test ******************************* //
    @Nested
    @ContextConfiguration(classes = {TradingSystemConfiguration.class})
    @WithModelMapper(basePackageClasses = TradingSystemMapper.class)
    @SpringBootTest(args = {"admin", "admin"})
    public class TradingSystemFacadeTestIntegration {

        @Autowired
        private TradingSystemFacade tradingSystemFacade;

        @Autowired
        private TradingSystem tradingSystem;

        private Set<UserSystem> userSystems;
        private List<Receipt> receipts;
        private Set<Store> stores;
        private UserSystem currUser;
        private UserSystem testUserSystem;
        private UserSystem admin;

        @BeforeEach
        void setUp() {
            admin = UserSystem.builder()
                    .userName("admin")
                    .password("admin")
                    .build();
            addUsers();
            Optional<UserSystem> userSystemOptional = userSystems.stream().findFirst();
            Assertions.assertTrue(userSystemOptional.isPresent());
            currUser = userSystemOptional.get();
            //addStores();
        }

        private void addStores() {
            stores = setUpStores();

            stores.forEach(store ->
                    /*tradingSystemFacade.openStore(currUser.getUserName(), new PurchasePolicyDto(store.getPurchasePolicy().isAllAllowed(),
                            store.getPurchasePolicy().getWhoCanBuyStatus()), new DiscountPolicyDto(store.getDiscountPolicy().isAllAllowed(),
                            store.getDiscountPolicy().getWhoCanBuyStatus()), store.getDiscountType().type, store.getPurchaseType().type,
                            store.getStoreName()));*/
                    tradingSystem.openStore(currUser, store.getPurchasePolicy(), store.getDiscountPolicy(), store.getStoreName()));
        }

        void addUsers() {
            userSystems = setupUsers();
            userSystems.forEach(userSystem ->
                    tradingSystem.registerNewUser(userSystem));
        }

        /**
         * check the viewPurchaseHistoryUser() functionality in case of exists user in the system
         */
        @Test
        void viewPurchaseHistoryUserPositive() {
            receipts = setUpReceipts();
            currUser.setReceipts(receipts);
            //call the function
            tradingSystem.getUser(currUser.getUserName()).setReceipts(receipts);
            List<ReceiptDto> receiptDtos = tradingSystemFacade.viewPurchaseHistory(currUser.getUserName());
            assertReceipts(receipts, receiptDtos);
        }

        /**
         * check the viewPurchaseHistoryUser() functionality in case of not exists user in the system
         */
        @Test
        void viewPurchaseHistoryUserNotExist() {
            //call the function
            List<ReceiptDto> receiptDtos = tradingSystemFacade.viewPurchaseHistory("userNotExist");
            Assertions.assertNull(receiptDtos);
        }

        /**
         * check the viewPurchaseHistoryAdministratorOfStore() functionality in case of exists admin and store in the system
         */
        @Test
        void viewPurchaseHistoryAdministratorOfStorePositive() {
            // create a user
            testUserSystem = UserSystem.builder()
                    .userName("KingRagnar")
                    .password("Odin12")
                    .firstName("Ragnar")
                    .lastName("Lodbrok").build();
            tradingSystemFacade.registerUser(testUserSystem.getUserName(), testUserSystem.getPassword(), testUserSystem.getFirstName(), testUserSystem.getLastName());

            // opens a store
            tradingSystemFacade.openStore(testUserSystem.getUserName(), new PurchasePolicyDto(), new DiscountPolicyDto(), "castro");
            //tradingSystem.openStore(tradingSystem.getUser(testUserSystem.getUserName()),new PurchasePolicy(), new DiscountPolicy(), "castro");
            Store store = this.tradingSystem.getStoresList().stream().filter(store1 -> store1.getStoreName().equals("castro")).findFirst().get();
            receipts = setUpReceipts();
            store.setReceipts(receipts);

            // call the function
            List<ReceiptDto> receiptDtos = tradingSystemFacade.viewPurchaseHistory(admin.getUserName(), store.getStoreId());
            assertReceipts(receipts, receiptDtos);
        }

        /**
         * check the viewPurchaseHistoryAdministratorOfStore() functionality in case of exists admin and not exist
         * store in the system.
         */
        @Test
        void viewPurchaseHistoryAdministratorOfStoreNegative() {
            // create a user
            testUserSystem = UserSystem.builder()
                    .userName("KingRagnar")
                    .password("Odin12")
                    .firstName("Ragnar")
                    .lastName("Lodbrok").build();
            tradingSystemFacade.registerUser(testUserSystem.getUserName(), testUserSystem.getPassword(), testUserSystem.getFirstName(), testUserSystem.getLastName());

            //call the function
            List<ReceiptDto> receiptDtos = tradingSystemFacade.viewPurchaseHistory(admin.getUserName(), 100000);
            Assertions.assertNull(receiptDtos);
        }

        /**
         * check the viewPurchaseHistoryAdministratorOfUser() functionality in case of exists admin and user in the system
         */
        @Test
        void viewPurchaseHistoryAdministratorOfUserPositive() {
            // create a user
            testUserSystem = UserSystem.builder()
                    .userName("KingRagnar")
                    .password("Odin12")
                    .firstName("Ragnar")
                    .lastName("Lodbrok").build();
            tradingSystemFacade.registerUser(testUserSystem.getUserName(), testUserSystem.getPassword(), testUserSystem.getFirstName(), testUserSystem.getLastName());
            List<Receipt> receipts = setUpReceipts();
            testUserSystem.setReceipts(receipts);
            tradingSystem.getUser(testUserSystem.getUserName()).setReceipts(receipts);

            //call the function
            List<ReceiptDto> receiptDtos = tradingSystemFacade.viewPurchaseHistory(admin.getUserName(), testUserSystem.getUserName());
            assertReceipts(receipts, receiptDtos);
        }

        /**
         * check the viewPurchaseHistoryAdministratorOfUser() functionality in case of exists admin and not exist
         * user in the system.
         */
        @Test
        void viewPurchaseHistoryAdministratorOfUserNegative() {
            // create a user
            testUserSystem = UserSystem.builder()
                    .userName("KingRagnar")
                    .password("Odin12")
                    .firstName("Ragnar")
                    .lastName("Lodbrok").build();

            // call the function
            tradingSystemFacade.registerUser(testUserSystem.getUserName(), testUserSystem.getPassword(), testUserSystem.getFirstName(), testUserSystem.getLastName());
            List<ReceiptDto> receiptDtos = tradingSystemFacade.viewPurchaseHistory(admin.getUserName(), "moran");
            Assertions.assertNull(receiptDtos);
        }

        /**
         * check the viewPurchaseHistoryOfManager() functionality in case of exists store and user in the system
         */
        @Test
        void viewPurchaseHistoryOfManagerPositive() {
            // create a user owner
            UserSystem userSystemOwner = UserSystem.builder()
                    .userName("KingRagnar")
                    .password("Odin12")
                    .firstName("Ragnar")
                    .lastName("Lodbrok").build();
            tradingSystemFacade.registerUser(userSystemOwner.getUserName(), userSystemOwner.getPassword(), userSystemOwner.getFirstName(), userSystemOwner.getLastName());
            List<Receipt> receipts = setUpReceipts();
            userSystemOwner.setReceipts(receipts);
            tradingSystem.getUser(userSystemOwner.getUserName()).setReceipts(receipts);
            UserSystem updatedOwner = tradingSystem.getUser(userSystemOwner.getUserName());

            // create a user manager
            UserSystem userSystemManager = UserSystem.builder()
                    .userName("KingRagnarManager")
                    .password("Odin12Manager")
                    .firstName("RagnarManager")
                    .lastName("LodbrokManager").build();
            tradingSystemFacade.registerUser(userSystemManager.getUserName(), userSystemManager.getPassword(), userSystemManager.getFirstName(), userSystemManager.getLastName());
            userSystemManager.setReceipts(receipts);
            tradingSystem.getUser(userSystemManager.getUserName()).setReceipts(receipts);

            // create a store
            tradingSystem.openStore(updatedOwner, new PurchasePolicy(), new DiscountPolicy(), "castro");
            Store store = this.tradingSystem.getStoresList().stream().filter(store1 -> store1.getStoreName().equals("castro")).findFirst().get();
            store.setReceipts(receipts);
            tradingSystemFacade.addManager(updatedOwner.getUserName(), store.getStoreId(), userSystemManager.getUserName());

            // call the function
            List<ReceiptDto> receiptDtos = tradingSystemFacade.viewPurchaseHistoryOfManager(userSystemManager.getUserName(), store.getStoreId());
            assertReceipts(receipts, receiptDtos);
        }

        /**
         * check the viewPurchaseHistoryOfManager() functionality in case of exists store and not exist user in the system
         */
        @Test
        void viewPurchaseHistoryOfManagerNotExistUser() {
            // create a user
            testUserSystem = UserSystem.builder()
                    .userName("KingRagnar")
                    .password("Odin12")
                    .firstName("Ragnar")
                    .lastName("Lodbrok").build();
            tradingSystemFacade.registerUser(testUserSystem.getUserName(), testUserSystem.getPassword(), testUserSystem.getFirstName(), testUserSystem.getLastName());

            // create a store
            tradingSystemFacade.openStore(testUserSystem.getUserName(), new PurchasePolicyDto(), new DiscountPolicyDto(),
                    "castro");
            Store store = this.tradingSystem.getStoresList().stream().filter(store1 -> store1.getStoreName().equals("castro")).findFirst().get();
            store.setReceipts(receipts);
            tradingSystemFacade.addManager(testUserSystem.getUserName(), store.getStoreId(), testUserSystem.getUserName());

            UserSystem moran = new UserSystem("Moran", "moran", "neptune", "momo");
            // call the function
            List<ReceiptDto> receiptDtos = tradingSystemFacade.viewPurchaseHistoryOfManager(moran.getUserName(), store.getStoreId());
            Assertions.assertNull(receiptDtos);
            //assertReceipts(receipts, receiptDtos);
        }

        /**
         * check the viewPurchaseHistoryOfManager() functionality in case of exists user and not exist store in the system
         */
        @Test
        void viewPurchaseHistoryOfManagerNotExistStore() {
            // create a user
            testUserSystem = UserSystem.builder()
                    .userName("KingRagnar")
                    .password("Odin12")
                    .firstName("Ragnar")
                    .lastName("Lodbrok").build();
            tradingSystemFacade.registerUser(testUserSystem.getUserName(), testUserSystem.getPassword(), testUserSystem.getFirstName(), testUserSystem.getLastName());

            // call the function
            List<ReceiptDto> receiptDtos = tradingSystemFacade.viewPurchaseHistoryOfManager(testUserSystem.getUserName(), 1);
            Assertions.assertNull(receiptDtos);
            //assertReceipts(receipts, receiptDtos);
        }

        /**
         * check the viewPurchaseHistoryOfOwner() functionality in case of exists store and user in the system
         */
        @Test
        void viewPurchaseHistoryOfOwnerPositive() {
            // create a user owner
            UserSystem userSystemOwner = UserSystem.builder()
                    .userName("KingRagnar")
                    .password("Odin12")
                    .firstName("Ragnar")
                    .lastName("Lodbrok").build();
            tradingSystemFacade.registerUser(userSystemOwner.getUserName(), userSystemOwner.getPassword(), userSystemOwner.getFirstName(), userSystemOwner.getLastName());
            List<Receipt> receipts = setUpReceipts();
            userSystemOwner.setReceipts(receipts);
            tradingSystem.getUser(userSystemOwner.getUserName()).setReceipts(receipts);
            UserSystem updatedOwner = tradingSystem.getUser(userSystemOwner.getUserName());

            // create a store
            tradingSystem.openStore(updatedOwner, new PurchasePolicy(), new DiscountPolicy(), "castro");
            Store store = this.tradingSystem.getStoresList().stream().filter(store1 -> store1.getStoreName().equals("castro")).findFirst().get();
            store.setReceipts(receipts);

            // call the function
            List<ReceiptDto> receiptDtos = tradingSystemFacade.viewPurchaseHistoryOfOwner(updatedOwner.getUserName(), store.getStoreId());
            assertReceipts(receipts, receiptDtos);
        }

        /**
         * check the viewPurchaseHistoryOfOwner() functionality in case of exists store and not exist user in the system
         */
        @Test
        void viewPurchaseHistoryOfOwnerNotExistUser() {
            // create a user owner
            UserSystem userSystemOwner = UserSystem.builder()
                    .userName("KingRagnar")
                    .password("Odin12")
                    .firstName("Ragnar")
                    .lastName("Lodbrok").build();
            tradingSystemFacade.registerUser(userSystemOwner.getUserName(), userSystemOwner.getPassword(), userSystemOwner.getFirstName(), userSystemOwner.getLastName());
            List<Receipt> receipts = setUpReceipts();
            userSystemOwner.setReceipts(receipts);
            tradingSystem.getUser(userSystemOwner.getUserName()).setReceipts(receipts);
            UserSystem updatedOwner = tradingSystem.getUser(userSystemOwner.getUserName());

            // create a store
            tradingSystem.openStore(updatedOwner, new PurchasePolicy(), new DiscountPolicy(), "castro");
            Store store = this.tradingSystem.getStoresList().stream().filter(store1 -> store1.getStoreName().equals("castro")).findFirst().get();
            store.setReceipts(receipts);

            // call the function
            List<ReceiptDto> receiptDtos = tradingSystemFacade.viewPurchaseHistoryOfOwner("moran", store.getStoreId());
            Assertions.assertNull(receiptDtos);
            //assertReceipts(receipts, receiptDtos);
        }

        /**
         * check the viewPurchaseHistoryOfOwner() functionality in case of exists user and not exist store in the system
         */
        @Test
        void viewPurchaseHistoryOfOwnerNotExistStore() {
            // create a user owner
            UserSystem userSystemOwner = UserSystem.builder()
                    .userName("KingRagnar")
                    .password("Odin12")
                    .firstName("Ragnar")
                    .lastName("Lodbrok").build();
            tradingSystemFacade.registerUser(userSystemOwner.getUserName(), userSystemOwner.getPassword(), userSystemOwner.getFirstName(), userSystemOwner.getLastName());
            List<Receipt> receipts = setUpReceipts();
            userSystemOwner.setReceipts(receipts);
            tradingSystem.getUser(userSystemOwner.getUserName()).setReceipts(receipts);
            UserSystem updatedOwner = tradingSystem.getUser(userSystemOwner.getUserName());

            // call the function
            List<ReceiptDto> receiptDtos = tradingSystemFacade.viewPurchaseHistoryOfOwner(updatedOwner.getUserName(), 100000);
            Assertions.assertNull(receiptDtos);
            //assertReceipts(receipts, receiptDtos);
        }

        /**
         * check the addProduct() functionality in case of exists store and userOwner and productName in the system
         */
        @Test
        void addProductPositive() {
            // create a user owner
            UserSystem userSystemOwner = UserSystem.builder()
                    .userName("KingRagnar")
                    .password("Odin12")
                    .firstName("Ragnar")
                    .lastName("Lodbrok").build();
            tradingSystemFacade.registerUser(userSystemOwner.getUserName(), userSystemOwner.getPassword(), userSystemOwner.getFirstName(), userSystemOwner.getLastName());
            UserSystem updatedOwner = tradingSystem.getUser(userSystemOwner.getUserName());

            // create a store
            tradingSystem.openStore(updatedOwner, new PurchasePolicy(), new DiscountPolicy(), "castro");
            Store store = this.tradingSystem.getStoresList().stream().filter(store1 -> store1.getStoreName().equals("castro")).findFirst().get();

            // create a product and call the function
            //Product product = new Product("table", ProductCategory.HOME_GARDEN, 10,100,store.getStoreId());
            Assertions.assertTrue(tradingSystemFacade.addProduct(updatedOwner.getUserName(), store.getStoreId(), "table",
                    ProductCategory.HOME_GARDEN.category, 10, 100));
        }

        /**
         * check the addProduct() functionality in case of not exists store and exist userOwner and productName in the system
         */
        @Test
        void addProductNotExistStore() {
            // create a user owner
            UserSystem userSystemOwner = UserSystem.builder()
                    .userName("KingRagnar")
                    .password("Odin12")
                    .firstName("Ragnar")
                    .lastName("Lodbrok").build();
            tradingSystemFacade.registerUser(userSystemOwner.getUserName(), userSystemOwner.getPassword(), userSystemOwner.getFirstName(), userSystemOwner.getLastName());
            UserSystem updatedOwner = tradingSystem.getUser(userSystemOwner.getUserName());

            // create a product and call the function
            //Product product = new Product("table", ProductCategory.HOME_GARDEN, 10, 100, 100000);
            Assertions.assertFalse(tradingSystemFacade.addProduct(updatedOwner.getUserName(), 100000, "table",
                    ProductCategory.HOME_GARDEN.category, 10, 100));
        }

        /**
         * check the addProduct() functionality in case of not exists userOwner and exist store and productName in the system
         */
        @Test
        void addProductNotExistUserOwner() {
            // create a demo user
            UserSystem testUserSystem = UserSystem.builder()
                    .userName("KingRagnarTest")
                    .password("Odin12")
                    .firstName("Ragnar")
                    .lastName("Lodbrok").build();

            // create a user owner
            UserSystem userSystemOwner = UserSystem.builder()
                    .userName("KingRagnar")
                    .password("Odin12")
                    .firstName("Ragnar")
                    .lastName("Lodbrok").build();
            tradingSystemFacade.registerUser(userSystemOwner.getUserName(), userSystemOwner.getPassword(), userSystemOwner.getFirstName(), userSystemOwner.getLastName());
            UserSystem updatedOwner = tradingSystem.getUser(userSystemOwner.getUserName());

            // create a store
            tradingSystem.openStore(updatedOwner, new PurchasePolicy(), new DiscountPolicy(), "castro");
            Store store = this.tradingSystem.getStoresList().stream().filter(store1 -> store1.getStoreName().equals("castro")).findFirst().get();

            // create a product and call the function
            //Product product = new Product("table", ProductCategory.HOME_GARDEN, 10, 100, 100000);
            Assertions.assertFalse(tradingSystemFacade.addProduct(testUserSystem.getUserName(), store.getStoreId(), "table",
                    ProductCategory.HOME_GARDEN.category, 10, 100));
        }

        /**
         * check the deleteProduct() functionality in case of exists store and userOwner and productName in the system
         */
        @Test
        void deleteProductFromStorePositive() {
            // create a user owner
            UserSystem userSystemOwner = UserSystem.builder()
                    .userName("KingRagnar")
                    .password("Odin12")
                    .firstName("Ragnar")
                    .lastName("Lodbrok").build();
            tradingSystemFacade.registerUser(userSystemOwner.getUserName(), userSystemOwner.getPassword(), userSystemOwner.getFirstName(), userSystemOwner.getLastName());
            UserSystem updatedOwner = tradingSystem.getUser(userSystemOwner.getUserName());

            // create a store
            tradingSystem.openStore(updatedOwner, new PurchasePolicy(), new DiscountPolicy(), "castro");
            Store store = this.tradingSystem.getStoresList().stream().filter(store1 -> store1.getStoreName().equals("castro")).findFirst().get();

            // create a product adding it to the system
            //Product product = new Product("table", ProductCategory.HOME_GARDEN, 10,100,store.getStoreId());
            tradingSystemFacade.addProduct(updatedOwner.getUserName(), store.getStoreId(), "table",
                    ProductCategory.HOME_GARDEN.category, 10, 100);

            // call to the function
            Assertions.assertTrue(tradingSystemFacade.deleteProductFromStore(updatedOwner.getUserName(),
                    store.getStoreId(), 0));
        }

        /**
         * check the deleteProduct() functionality in case of not exists store and exist userOwner and productName in the system
         */
        @Test
        void deleteProductNotExistStore() {
            // create a user owner
            UserSystem userSystemOwner = UserSystem.builder()
                    .userName("KingRagnar")
                    .password("Odin12")
                    .firstName("Ragnar")
                    .lastName("Lodbrok").build();
            tradingSystemFacade.registerUser(userSystemOwner.getUserName(), userSystemOwner.getPassword(), userSystemOwner.getFirstName(), userSystemOwner.getLastName());
            UserSystem updatedOwner = tradingSystem.getUser(userSystemOwner.getUserName());

            // create a product adding it to the system
            //Product product = new Product("table", ProductCategory.HOME_GARDEN, 10,100,store.getStoreId());
            tradingSystemFacade.addProduct(updatedOwner.getUserName(), 100000, "table",
                    ProductCategory.HOME_GARDEN.category, 10, 100);

            // call to the function
            Assertions.assertFalse(tradingSystemFacade.deleteProductFromStore(updatedOwner.getUserName(),
                    100000, 0));
        }

        /**
         * check the deleteProduct() functionality in case of not exists userOwner and exist store and productName in the system
         */
        @Test
        void deleteProductNotExistUserOwner() {
            // create a demo user
            UserSystem testUserSystem = UserSystem.builder()
                    .userName("KingRagnarTest")
                    .password("Odin12")
                    .firstName("Ragnar")
                    .lastName("Lodbrok").build();

            // create a user owner
            UserSystem userSystemOwner = UserSystem.builder()
                    .userName("KingRagnar")
                    .password("Odin12")
                    .firstName("Ragnar")
                    .lastName("Lodbrok").build();
            tradingSystemFacade.registerUser(userSystemOwner.getUserName(), userSystemOwner.getPassword(), userSystemOwner.getFirstName(), userSystemOwner.getLastName());
            UserSystem updatedOwner = tradingSystem.getUser(userSystemOwner.getUserName());

            // create a store
            tradingSystem.openStore(updatedOwner, new PurchasePolicy(), new DiscountPolicy(), "castro");
            Store store = this.tradingSystem.getStoresList().stream().filter(store1 -> store1.getStoreName().equals("castro")).findFirst().get();

            // create a product adding it to the system
            //Product product = new Product("table", ProductCategory.HOME_GARDEN, 10,100,store.getStoreId());
            tradingSystemFacade.addProduct(testUserSystem.getUserName(), store.getStoreId(), "table",
                    ProductCategory.HOME_GARDEN.category, 10, 100);

            // call to the function
            Assertions.assertFalse(tradingSystemFacade.deleteProductFromStore(testUserSystem.getUserName(),
                    store.getStoreId(), 0));

        }

        /**
         * check the editProduct() functionality in case of exists store and userOwner and productName in the system
         */
        @Test
        void editProductPositive() {
            // create a user owner
            UserSystem userSystemOwner = UserSystem.builder()
                    .userName("KingRagnar")
                    .password("Odin12")
                    .firstName("Ragnar")
                    .lastName("Lodbrok").build();
            tradingSystemFacade.registerUser(userSystemOwner.getUserName(), userSystemOwner.getPassword(),
                    userSystemOwner.getFirstName(), userSystemOwner.getLastName());
            UserSystem updatedOwner = tradingSystem.getUser(userSystemOwner.getUserName());

            // create a store
            tradingSystem.openStore(updatedOwner, new PurchasePolicy(), new DiscountPolicy(), "castro");
            Store store = this.tradingSystem.getStoresList().stream().filter(store1 -> store1.getStoreName().equals("castro")).findFirst().get();

            // create a product adding it to the system
            //Product product = new Product("table", ProductCategory.HOME_GARDEN, 10,100,store.getStoreId());
            tradingSystemFacade.addProduct(updatedOwner.getUserName(), store.getStoreId(), "table",
                    ProductCategory.HOME_GARDEN.category, 10, 100);

            // call to the function
            Assertions.assertTrue(tradingSystemFacade.editProduct(updatedOwner.getUserName(), store.getStoreId(),
                    store.products.stream().findFirst().get().getProductSn(), "table" + "vnjfkvj", ProductCategory.HOME_GARDEN.category, 10 + 10, 100));
        }

        /**
         * check the editProduct() functionality in case of not exists store and exist userOwner and productName in the system
         */
        @Test
        void editProductNotExistStore() {
            // create a user owner
            UserSystem userSystemOwner = UserSystem.builder()
                    .userName("KingRagnar")
                    .password("Odin12")
                    .firstName("Ragnar")
                    .lastName("Lodbrok").build();
            tradingSystemFacade.registerUser(userSystemOwner.getUserName(), userSystemOwner.getPassword(), userSystemOwner.getFirstName(), userSystemOwner.getLastName());
            UserSystem updatedOwner = tradingSystem.getUser(userSystemOwner.getUserName());

            // create a store
            tradingSystem.openStore(updatedOwner, new PurchasePolicy(), new DiscountPolicy(), "castro");
            Store store = this.tradingSystem.getStoresList().stream().filter(store1 -> store1.getStoreName().equals("castro")).findFirst().get();

            // create a product adding it to the system
            //Product product = new Product("table", ProductCategory.HOME_GARDEN, 10,100,store.getStoreId());
            tradingSystemFacade.addProduct(updatedOwner.getUserName(), store.getStoreId(), "table",
                    ProductCategory.HOME_GARDEN.category, 10, 100);

            // call to the function
            Assertions.assertFalse(tradingSystemFacade.editProduct(updatedOwner.getUserName(), 100000, 1,
                    "table" + "vnjfkvj", ProductCategory.HOME_GARDEN.category, 10 + 10, 100));

        }

        /**
         * check the editProduct() functionality in case of not exists userOwner and exist store and productName in the system
         */
        @Test
        void editProductNotExistUserOwner() {
            // create a demo user
            UserSystem testUserSystem = UserSystem.builder()
                    .userName("KingRagnarTest")
                    .password("Odin12")
                    .firstName("Ragnar")
                    .lastName("Lodbrok").build();

            // create a user owner
            UserSystem userSystemOwner = UserSystem.builder()
                    .userName("KingRagnar")
                    .password("Odin12")
                    .firstName("Ragnar")
                    .lastName("Lodbrok").build();
            tradingSystemFacade.registerUser(userSystemOwner.getUserName(), userSystemOwner.getPassword(), userSystemOwner.getFirstName(), userSystemOwner.getLastName());
            UserSystem updatedOwner = tradingSystem.getUser(userSystemOwner.getUserName());

            // create a store
            tradingSystem.openStore(updatedOwner, new PurchasePolicy(), new DiscountPolicy(), "castro");
            Store store = this.tradingSystem.getStoresList().stream().filter(store1 -> store1.getStoreName().equals("castro")).findFirst().get();

            // create a product adding it to the system
            //Product product = new Product("table", ProductCategory.HOME_GARDEN, 10,100,store.getStoreId());
            tradingSystemFacade.addProduct(testUserSystem.getUserName(), store.getStoreId(), "table",
                    ProductCategory.HOME_GARDEN.category, 10, 100);

            // call to the function
            Assertions.assertFalse(tradingSystemFacade.editProduct(testUserSystem.getUserName(), store.getStoreId(), 1,
                    "table" + "vnjfkvj", ProductCategory.HOME_GARDEN.category, 10 + 10, 100));
        }

        /**
         * check the addOwner() functionality in case of exists store and userOwner and newUserOwner in the system
         */
        @Test
        void addOwnerPositive() {
            // create a user owner
            UserSystem userSystemOwner = UserSystem.builder()
                    .userName("KingRagnar")
                    .password("Odin12")
                    .firstName("Ragnar")
                    .lastName("Lodbrok").build();
            tradingSystemFacade.registerUser(userSystemOwner.getUserName(), userSystemOwner.getPassword(), userSystemOwner.getFirstName(), userSystemOwner.getLastName());
            UserSystem updatedOwner = tradingSystem.getUser(userSystemOwner.getUserName());

            // create a new user owner
            UserSystem userSystemOwnerNew = UserSystem.builder()
                    .userName("KingRagnarOwnerNew")
                    .password("Odin12OwnerNew")
                    .firstName("RagnarOwnerNew")
                    .lastName("LodbrokOwnerNew").build();
            tradingSystemFacade.registerUser(userSystemOwnerNew.getUserName(), userSystemOwnerNew.getPassword(),
                    userSystemOwnerNew.getFirstName(), userSystemOwnerNew.getLastName());
            UserSystem updatedOwnerNew = tradingSystem.getUser(userSystemOwnerNew.getUserName());

            // create a store
            tradingSystem.openStore(updatedOwner, new PurchasePolicy(), new DiscountPolicy(), "castro");
            Store store = this.tradingSystem.getStoresList().stream().filter(store1 -> store1.getStoreName().equals("castro")).findFirst().get();

            // call the function
            Assertions.assertTrue(tradingSystemFacade.addOwner(updatedOwner.getUserName(), store.getStoreId(),
                    updatedOwnerNew.getUserName()));
        }

        /**
         * check the addOwner() functionality in case of not exists store and exist of userOwner and of newUserOwner
         * in the system
         */
        @Test
        void addOwnerNotExistStore() {
            // create a user owner
            UserSystem userSystemOwner = UserSystem.builder()
                    .userName("KingRagnar")
                    .password("Odin12")
                    .firstName("Ragnar")
                    .lastName("Lodbrok").build();
            tradingSystemFacade.registerUser(userSystemOwner.getUserName(), userSystemOwner.getPassword(),
                    userSystemOwner.getFirstName(), userSystemOwner.getLastName());
            UserSystem updatedOwner = tradingSystem.getUser(userSystemOwner.getUserName());

            // create a new user owner
            UserSystem userSystemOwnerNew = UserSystem.builder()
                    .userName("KingRagnarOwnerNew")
                    .password("Odin12OwnerNew")
                    .firstName("RagnarOwnerNew")
                    .lastName("LodbrokOwnerNew").build();
            tradingSystemFacade.registerUser(userSystemOwnerNew.getUserName(), userSystemOwnerNew.getPassword(),
                    userSystemOwnerNew.getFirstName(), userSystemOwnerNew.getLastName());
            UserSystem updatedOwnerNew = tradingSystem.getUser(userSystemOwnerNew.getUserName());

            // call the function
            Assertions.assertFalse(tradingSystemFacade.addOwner(updatedOwner.getUserName(), 1000000,
                    updatedOwnerNew.getUserName()));

        }

        /**
         * check the addOwner() functionality in case of not exists newUserOwner and exist of store and of userOwner
         * in the system
         */
        @Test
        void addOwnerNotExistUserNewOwner() {
            // create a user owner
            UserSystem userSystemOwner = UserSystem.builder()
                    .userName("KingRagnar")
                    .password("Odin12")
                    .firstName("Ragnar")
                    .lastName("Lodbrok").build();
            tradingSystemFacade.registerUser(userSystemOwner.getUserName(), userSystemOwner.getPassword(),
                    userSystemOwner.getFirstName(), userSystemOwner.getLastName());
            UserSystem updatedOwner = tradingSystem.getUser(userSystemOwner.getUserName());

            // create a new user owner
            UserSystem userSystemOwnerNew = UserSystem.builder()
                    .userName("KingRagnarOwnerNeww")
                    .password("Odin12OwnerNew")
                    .firstName("RagnarOwnerNew")
                    .lastName("LodbrokOwnerNew").build();

            // create a store
            tradingSystem.openStore(userSystemOwner, new PurchasePolicy(), new DiscountPolicy(), "castro");
            Store store = this.tradingSystem.getStoresList().stream().filter(store1 -> store1.getStoreName().equals("castro")).findFirst().get();

            // call the function
            Assertions.assertFalse(tradingSystemFacade.addOwner(updatedOwner.getUserName(), store.getStoreId(),
                    userSystemOwnerNew.getUserName()));
        }

        /**
         * check the addManager() functionality in case of exists store and userOwner and newManagerUser in the system
         */
        @Test
        void addManagerPositive() {
            // create a user owner
            UserSystem userSystemOwner = UserSystem.builder()
                    .userName("KingRagnar")
                    .password("Odin12")
                    .firstName("Ragnar")
                    .lastName("Lodbrok").build();
            tradingSystemFacade.registerUser(userSystemOwner.getUserName(), userSystemOwner.getPassword(),
                    userSystemOwner.getFirstName(), userSystemOwner.getLastName());
            UserSystem updatedOwner = tradingSystem.getUser(userSystemOwner.getUserName());

            // create a user newManagerUser
            UserSystem userSystemManager = UserSystem.builder()
                    .userName("KingRagnarManager")
                    .password("Odin12Manager")
                    .firstName("RagnarManager")
                    .lastName("LodbrokManager").build();
            tradingSystemFacade.registerUser(userSystemManager.getUserName(), userSystemManager.getPassword(), userSystemManager.getFirstName(), userSystemManager.getLastName());
            UserSystem updatedManager = tradingSystem.getUser(userSystemManager.getUserName());

            // create a store
            tradingSystem.openStore(updatedOwner, new PurchasePolicy(), new DiscountPolicy(), "castro");
            Store store = this.tradingSystem.getStoresList().stream().filter(store1 -> store1.getStoreName().equals("castro")).findFirst().get();

            // call the function
            Assertions.assertTrue(tradingSystemFacade.addManager(updatedOwner.getUserName(), store.getStoreId(),
                    updatedManager.getUserName()));
        }

        /**
         * check the addManager() functionality in case of not exists store and exist of userOwner and of newUserOwner
         * in the system
         */
        @Test
        void addManagerNotExistStore() {
            // create a user owner
            UserSystem userSystemOwner = UserSystem.builder()
                    .userName("KingRagnar")
                    .password("Odin12")
                    .firstName("Ragnar")
                    .lastName("Lodbrok").build();
            tradingSystemFacade.registerUser(userSystemOwner.getUserName(), userSystemOwner.getPassword(),
                    userSystemOwner.getFirstName(), userSystemOwner.getLastName());
            UserSystem updatedOwner = tradingSystem.getUser(userSystemOwner.getUserName());

            // create a user newManagerUser
            UserSystem userSystemManager = UserSystem.builder()
                    .userName("KingRagnarManager")
                    .password("Odin12Manager")
                    .firstName("RagnarManager")
                    .lastName("LodbrokManager").build();
            tradingSystemFacade.registerUser(userSystemManager.getUserName(), userSystemManager.getPassword(), userSystemManager.getFirstName(), userSystemManager.getLastName());
            UserSystem updatedManager = tradingSystem.getUser(userSystemManager.getUserName());

            // call the function
            Assertions.assertFalse(tradingSystemFacade.addManager(updatedOwner.getUserName(), 100000,
                    updatedManager.getUserName()));
        }

        /**
         * check the addManager() functionality in case of not exists store and exist of userOwner and of newUserOwner
         * in the system
         */
        @Test
        void addManagerNotExistNewManager() {
            // create a user owner
            UserSystem userSystemOwner = UserSystem.builder()
                    .userName("KingRagnar")
                    .password("Odin12")
                    .firstName("Ragnar")
                    .lastName("Lodbrok").build();
            tradingSystemFacade.registerUser(userSystemOwner.getUserName(), userSystemOwner.getPassword(),
                    userSystemOwner.getFirstName(), userSystemOwner.getLastName());
            UserSystem updatedOwner = tradingSystem.getUser(userSystemOwner.getUserName());

            // create a user newManagerUser
            UserSystem userSystemManager = UserSystem.builder()
                    .userName("KingRagnarNewManager")
                    .password("Odin12Manager")
                    .firstName("RagnarManager")
                    .lastName("LodbrokManager").build();

            // create a store
            tradingSystem.openStore(updatedOwner, new PurchasePolicy(), new DiscountPolicy(), "castro");
            Store store = this.tradingSystem.getStoresList().stream().filter(store1 -> store1.getStoreName().equals("castro")).findFirst().get();

            // call the function
            Assertions.assertFalse(tradingSystemFacade.addManager(updatedOwner.getUserName(), store.getStoreId(),
                    userSystemManager.getUserName()));

        }

        /**
         * check the addPermission() functionality in case of exists store and userOwner and userManager in the system
         */
        @Test
        void addPermissionPositive() {
            // create a user owner
            UserSystem userSystemOwner = UserSystem.builder()
                    .userName("KingRagnar")
                    .password("Odin12")
                    .firstName("Ragnar")
                    .lastName("Lodbrok").build();
            tradingSystemFacade.registerUser(userSystemOwner.getUserName(), userSystemOwner.getPassword(),
                    userSystemOwner.getFirstName(), userSystemOwner.getLastName());
            UserSystem updatedOwner = tradingSystem.getUser(userSystemOwner.getUserName());

            // create a user userManager
            UserSystem userSystemManager = UserSystem.builder()
                    .userName("KingRagnarManager")
                    .password("Odin12Manager")
                    .firstName("RagnarManager")
                    .lastName("LodbrokManager").build();
            tradingSystemFacade.registerUser(userSystemManager.getUserName(), userSystemManager.getPassword(), userSystemManager.getFirstName(), userSystemManager.getLastName());
            UserSystem updatedManager = tradingSystem.getUser(userSystemManager.getUserName());

            // create a store
            tradingSystem.openStore(updatedOwner, new PurchasePolicy(), new DiscountPolicy(), "castro");
            Store store = this.tradingSystem.getStoresList().stream().filter(store1 -> store1.getStoreName().equals("castro")).findFirst().get();
            // add userManager to manage the store
            tradingSystemFacade.addManager(updatedOwner.getUserName(), store.getStoreId(), updatedManager.getUserName());

            // call the function
            Assertions.assertTrue(tradingSystemFacade.addPermission(updatedOwner.getUserName(), store.getStoreId(),
                    updatedManager.getUserName(), StorePermission.EDIT.function));
        }

        /**
         * check the addPermission() functionality in case of not exists store and exist userOwner and userManager in the system
         */
        @Test
        void addPermissionNotExistStore() {
            // create a user owner
            UserSystem userSystemOwner = UserSystem.builder()
                    .userName("KingRagnar")
                    .password("Odin12")
                    .firstName("Ragnar")
                    .lastName("Lodbrok").build();
            tradingSystemFacade.registerUser(userSystemOwner.getUserName(), userSystemOwner.getPassword(),
                    userSystemOwner.getFirstName(), userSystemOwner.getLastName());
            UserSystem updatedOwner = tradingSystem.getUser(userSystemOwner.getUserName());

            // create a user userManager
            UserSystem userSystemManager = UserSystem.builder()
                    .userName("KingRagnarManager")
                    .password("Odin12Manager")
                    .firstName("RagnarManager")
                    .lastName("LodbrokManager").build();
            tradingSystemFacade.registerUser(userSystemManager.getUserName(), userSystemManager.getPassword(), userSystemManager.getFirstName(), userSystemManager.getLastName());
            UserSystem updatedManager = tradingSystem.getUser(userSystemManager.getUserName());

            // call the function
            Assertions.assertFalse(tradingSystemFacade.addPermission(updatedOwner.getUserName(), 100000,
                    updatedManager.getUserName(), StorePermission.EDIT.function));

        }
        //////////////////////////////////////////////////////////////////////////

        /**
         * check the removeManager() functionality in case of exists store and userOwner and userManager in the system
         */
        @Test
        void removeManager() {
            // create a user owner
            UserSystem userSystemOwner = UserSystem.builder()
                    .userName("KingRagnar")
                    .password("Odin12")
                    .firstName("Ragnar")
                    .lastName("Lodbrok").build();
            tradingSystemFacade.registerUser(userSystemOwner.getUserName(), userSystemOwner.getPassword(),
                    userSystemOwner.getFirstName(), userSystemOwner.getLastName());
            UserSystem updatedOwner = tradingSystem.getUser(userSystemOwner.getUserName());

            // create a user newManagerUser
            UserSystem userSystemManager = UserSystem.builder()
                    .userName("KingRagnarManager")
                    .password("Odin12Manager")
                    .firstName("RagnarManager")
                    .lastName("LodbrokManager").build();
            tradingSystemFacade.registerUser(userSystemManager.getUserName(), userSystemManager.getPassword(), userSystemManager.getFirstName(), userSystemManager.getLastName());
            UserSystem updatedManager = tradingSystem.getUser(userSystemManager.getUserName());

            // create a store
            tradingSystem.openStore(updatedOwner, new PurchasePolicy(), new DiscountPolicy(), "castro");
            Store store = this.tradingSystem.getStoresList().stream().filter(store1 -> store1.getStoreName().equals("castro")).findFirst().get();

            tradingSystemFacade.addManager(updatedOwner.getUserName(), store.getStoreId(),
                    updatedManager.getUserName());

            // call the function
            Assertions.assertTrue(tradingSystemFacade.removeManager(updatedOwner.getUserName(), store.getStoreId(),
                    updatedManager.getUserName()));
        }

        /**
         * check the logout() functionality in case of exists user that is login in the system
         */
        @Test
        void logout() {
            // create a user owner
            UserSystem userSystem = UserSystem.builder()
                    .userName("KingRagnar")
                    .password("Odin12")
                    .firstName("Ragnar")
                    .lastName("Lodbrok").build();
            tradingSystemFacade.registerUser(userSystem.getUserName(), userSystem.getPassword(),
                    userSystem.getFirstName(), userSystem.getLastName());
            UserSystem updatedUser = tradingSystem.getUser(userSystem.getUserName());

            // login the user
            tradingSystemFacade.login(updatedUser.getUserName(), updatedUser.getPassword());

            // call the function
            Assertions.assertTrue(tradingSystemFacade.logout(updatedUser.getUserName()));
        }

        /**
         * check the openStore() functionality in case of exists userOwner in the system
         */
        @Test
        void openStore() {
            // create a user owner
            UserSystem userSystemOwner = UserSystem.builder()
                    .userName("KingRagnarr")
                    .password("Odin12")
                    .firstName("Ragnar")
                    .lastName("Lodbrok").build();
            tradingSystemFacade.registerUser(userSystemOwner.getUserName(), userSystemOwner.getPassword(),
                    userSystemOwner.getFirstName(), userSystemOwner.getLastName());
            UserSystem updatedOwner = tradingSystem.getUser(userSystemOwner.getUserName());

            Assertions.assertTrue(tradingSystemFacade.openStore(updatedOwner.getUserName(), new PurchasePolicyDto(),
                    new DiscountPolicyDto(), "zaraa"));
        }

        /**
         * check the registerUser() functionality in case of not exists user in the system
         */
        @Test
        void registerUser() {
            Assertions.assertTrue(tradingSystemFacade.registerUser("moranush", "123", "moran", "neptune"));
        }

        /**
         * check the login() functionality in case of exists user in the system
         */
        @Test
        void login() {
            // create a user owner
            UserSystem userSystem = UserSystem.builder()
                    .userName("KingRagnardd")
                    .password("Odin12")
                    .firstName("Ragnar")
                    .lastName("Lodbrok").build();
            tradingSystemFacade.registerUser(userSystem.getUserName(), userSystem.getPassword(),
                    userSystem.getFirstName(), userSystem.getLastName());
            UserSystem updatedUser = tradingSystem.getUser(userSystem.getUserName());

            // login the user
            Assertions.assertTrue(tradingSystemFacade.login(updatedUser.getUserName(), "Odin12"));
        }

        /**
         * check the viewStoreInfo() functionality in case of exists store in the system
         */
        @Test
        void viewStoreInfo() {
            // create a user owner
            UserSystem userSystemOwner = UserSystem.builder()
                    .userName("KingRagnar")
                    .password("Odin12")
                    .firstName("Ragnar")
                    .lastName("Lodbrok").build();
            tradingSystemFacade.registerUser(userSystemOwner.getUserName(), userSystemOwner.getPassword(),
                    userSystemOwner.getFirstName(), userSystemOwner.getLastName());
            UserSystem updatedOwner = tradingSystem.getUser(userSystemOwner.getUserName());

            // create a store
            tradingSystem.openStore(updatedOwner, new PurchasePolicy(), new DiscountPolicy(), "castro");
            Store store = this.tradingSystem.getStoresList().stream().filter(store1 -> store1.getStoreName().equals("castro")).findFirst().get();
            store.setPurchaseType(PurchaseType.BUY_IMMEDIATELY);
            store.setDiscountType(DiscountType.NONE);

            // call the function
            StoreDto storeDto = tradingSystemFacade.viewStoreInfo(store.getStoreId());
            assertionStore(store, storeDto);
        }

        /**
         * check the viewProduct() functionality in case of exists store in the system
         */
        @Test
        void viewProduct() {
            // create a user owner
            UserSystem userSystemOwner = UserSystem.builder()
                    .userName("KingRagnar")
                    .password("Odin12")
                    .firstName("Ragnar")
                    .lastName("Lodbrok").build();
            tradingSystemFacade.registerUser(userSystemOwner.getUserName(), userSystemOwner.getPassword(),
                    userSystemOwner.getFirstName(), userSystemOwner.getLastName());
            UserSystem updatedOwner = tradingSystem.getUser(userSystemOwner.getUserName());

            // create a store
            tradingSystem.openStore(updatedOwner, new PurchasePolicy(), new DiscountPolicy(), "castro");
            Store store = this.tradingSystem.getStoresList().stream().filter(store1 -> store1.getStoreName().equals("castro")).findFirst().get();

            // add a product
            tradingSystemFacade.addProduct(userSystemOwner.getUserName(), store.getStoreId(), "table",
                    ProductCategory.HOME_GARDEN.category, 10, 100);
            Product product = this.tradingSystem.getStoresList().stream().filter(store1 -> store1.getStoreId() == store.getStoreId())
                    .findFirst().get().products.stream().filter(product1 -> product1.getName().equals("table")).findFirst().get();

            ProductDto productDto = tradingSystemFacade.viewProduct(store.getStoreId(), product.getProductSn());
            assertProduct(product, productDto);
        }

        /**
         * check the searchProductByName() functionality in case of exists product in the system
         */
        @Test
        void searchProductByName() {
            // create a user owner
            UserSystem userSystemOwner = UserSystem.builder()
                    .userName("KingRagnar")
                    .password("Odin12")
                    .firstName("Ragnar")
                    .lastName("Lodbrok").build();
            tradingSystemFacade.registerUser(userSystemOwner.getUserName(), userSystemOwner.getPassword(),
                    userSystemOwner.getFirstName(), userSystemOwner.getLastName());
            UserSystem updatedOwner = tradingSystem.getUser(userSystemOwner.getUserName());

            // create a store
            tradingSystem.openStore(updatedOwner, new PurchasePolicy(), new DiscountPolicy(), "castro");
            Store store = this.tradingSystem.getStoresList().stream().filter(store1 -> store1.getStoreName().equals("castro")).findFirst().get();

            // add a product
            tradingSystemFacade.addProduct(userSystemOwner.getUserName(), store.getStoreId(), "table",
                    ProductCategory.HOME_GARDEN.category, 10, 100);
            Product product = this.tradingSystem.getStoresList().stream().filter(store1 -> store1.getStoreId() == store.getStoreId())
                    .findFirst().get().products.stream().filter(product1 -> product1.getName().equals("table")).findFirst().get();

            //call the function
            List<ProductDto> productDtoList = tradingSystemFacade.searchProductByName(product.getName());
            assertProduct(product, productDtoList.get(0));
        }

        /**
         * check the searchProductByCategory() functionality in case of exists product in the system
         */
        @Test
        void searchProductByCategory() {
            // create a user owner
            UserSystem userSystemOwner = UserSystem.builder()
                    .userName("KingRagnar")
                    .password("Odin12")
                    .firstName("Ragnar")
                    .lastName("Lodbrok").build();
            tradingSystemFacade.registerUser(userSystemOwner.getUserName(), userSystemOwner.getPassword(),
                    userSystemOwner.getFirstName(), userSystemOwner.getLastName());
            UserSystem updatedOwner = tradingSystem.getUser(userSystemOwner.getUserName());

            // create a store
            tradingSystem.openStore(updatedOwner, new PurchasePolicy(), new DiscountPolicy(), "castro");
            Store store = this.tradingSystem.getStoresList().stream().filter(store1 -> store1.getStoreName().equals("castro")).findFirst().get();

            // add a product
            tradingSystemFacade.addProduct(userSystemOwner.getUserName(), store.getStoreId(), "pic",
                    ProductCategory.COLLECTIBLES_ART.category, 10, 100);
            Product product = this.tradingSystem.getStoresList().stream().filter(store1 -> store1.getStoreId() == store.getStoreId())
                    .findFirst().get().products.stream().filter(product1 -> product1.getName().equals("pic")).findFirst().get();

            //call the function
            List<ProductDto> productDtoList = tradingSystemFacade.searchProductByCategory(product.getCategory().category);
            assertProduct(product, productDtoList.get(0));
        }

        /**
         * check the searchProductByKeyWords() functionality in case of exists product in the system
         */
        @Test
        void searchProductByKeyWords() {
            // create a user owner
            UserSystem userSystemOwner = UserSystem.builder()
                    .userName("KingRagnar")
                    .password("Odin12")
                    .firstName("Ragnar")
                    .lastName("Lodbrok").build();
            tradingSystemFacade.registerUser(userSystemOwner.getUserName(), userSystemOwner.getPassword(),
                    userSystemOwner.getFirstName(), userSystemOwner.getLastName());
            UserSystem updatedOwner = tradingSystem.getUser(userSystemOwner.getUserName());

            // create a store
            tradingSystem.openStore(updatedOwner, new PurchasePolicy(), new DiscountPolicy(), "castro");
            Store store = this.tradingSystem.getStoresList().stream().filter(store1 -> store1.getStoreName().equals("castro")).findFirst().get();

            // add a product
            tradingSystemFacade.addProduct(userSystemOwner.getUserName(), store.getStoreId(), "phone",
                    ProductCategory.HOME_GARDEN.category, 10, 100);
            Product product = this.tradingSystem.getStoresList().stream().filter(store1 -> store1.getStoreId() == store.getStoreId())
                    .findFirst().get().products.stream().filter(product1 -> product1.getName().equals("phone")).findFirst().get();

            //call the function
            List<String> listKeyWords = new ArrayList<>();
            listKeyWords.add(product.getName());
            List<ProductDto> productDtoList = tradingSystemFacade.searchProductByKeyWords(listKeyWords);
            assertProduct(product, productDtoList.get(0));
        }

        /**
         * check the filterByRangePrice() functionality in case of exists products in the system
         */
        @Test
        void filterByRangePrice() {
            // create a user owner
            UserSystem userSystemOwner = UserSystem.builder()
                    .userName("KingRagnar")
                    .password("Odin12")
                    .firstName("Ragnar")
                    .lastName("Lodbrok").build();
            tradingSystemFacade.registerUser(userSystemOwner.getUserName(), userSystemOwner.getPassword(),
                    userSystemOwner.getFirstName(), userSystemOwner.getLastName());
            UserSystem updatedOwner = tradingSystem.getUser(userSystemOwner.getUserName());

            // create a store
            tradingSystem.openStore(updatedOwner, new PurchasePolicy(), new DiscountPolicy(), "castro");
            Store store = this.tradingSystem.getStoresList().stream().filter(store1 -> store1.getStoreName().equals("castro")).findFirst().get();

            // add a product1
            tradingSystemFacade.addProduct(userSystemOwner.getUserName(), store.getStoreId(), "table",
                    ProductCategory.HOME_GARDEN.category, 10, 100);
            Product product1 = this.tradingSystem.getStoresList().stream().filter(store1 -> store1.getStoreId() == store.getStoreId())
                    .findFirst().get().products.stream().filter(product3 -> product3.getName().equals("table")).findFirst().get();

            // add a product2
            tradingSystemFacade.addProduct(userSystemOwner.getUserName(), store.getStoreId(), "desk",
                    ProductCategory.HOME_GARDEN.category, 10, 50);
            Product product2 = this.tradingSystem.getStoresList().stream().filter(store1 -> store1.getStoreId() == store.getStoreId())
                    .findFirst().get().products.stream().filter(product3 -> product3.getName().equals("desk")).findFirst().get();

            // arrange the actual list to be returned
            List<ProductDto> productsToFilter = new ArrayList<>();
            productsToFilter.add(new ProductDto(product1.getProductSn(), product1.getName(), product1.getCategory().category, product1.getAmount(),
                    product1.getCost(), product1.getRank(), product1.getStoreId(), product1.getDiscountType(), product1.getPurchaseType()));
            productsToFilter.add(new ProductDto(product2.getProductSn(), product2.getName(), product2.getCategory().category, product2.getAmount(),
                    product2.getCost(), product2.getRank(), product2.getStoreId(), product2.getDiscountType(), product2.getPurchaseType()));
            // call the function
            List<ProductDto> productsFilteredActual = tradingSystemFacade.filterByRangePrice(productsToFilter, 50, 100);

            // arrange the expected list to be returned
            List<ProductDto> productsExpected = new ArrayList<>();
            productsExpected.add(new ProductDto(product1.getProductSn(), product1.getName(), product1.getCategory().category, product1.getAmount(),
                    product1.getCost(), product1.getRank(), product1.getStoreId(), product1.getDiscountType(), product1.getPurchaseType()));
            productsExpected.add(new ProductDto(product2.getProductSn(), product2.getName(), product2.getCategory().category, product2.getAmount(),
                    product2.getCost(), product2.getRank(), product2.getStoreId(), product2.getDiscountType(), product2.getPurchaseType()));

            Assertions.assertEquals(productsExpected, productsFilteredActual);
        }

        /**
         * check the filterByProductRank() functionality in case of exists products in the system
         */
        @Test
        void filterByProductRank() {
            // create a user owner
            UserSystem userSystemOwner = UserSystem.builder()
                    .userName("KingRagnar")
                    .password("Odin12")
                    .firstName("Ragnar")
                    .lastName("Lodbrok").build();
            tradingSystemFacade.registerUser(userSystemOwner.getUserName(), userSystemOwner.getPassword(),
                    userSystemOwner.getFirstName(), userSystemOwner.getLastName());
            UserSystem updatedOwner = tradingSystem.getUser(userSystemOwner.getUserName());

            // create a store
            tradingSystem.openStore(updatedOwner, new PurchasePolicy(), new DiscountPolicy(), "castro");
            Store store = this.tradingSystem.getStoresList().stream().filter(store1 -> store1.getStoreName().equals("castro")).findFirst().get();

            // add a product1
            tradingSystemFacade.addProduct(userSystemOwner.getUserName(), store.getStoreId(), "table",
                    ProductCategory.HOME_GARDEN.category, 10, 100);
            Product product1 = this.tradingSystem.getStoresList().stream().filter(store1 -> store1.getStoreId() == store.getStoreId())
                    .findFirst().get().products.stream().filter(product3 -> product3.getName().equals("table")).findFirst().get();
            product1.setRank(3);

            // add a product2
            tradingSystemFacade.addProduct(userSystemOwner.getUserName(), store.getStoreId(), "desk",
                    ProductCategory.HOME_GARDEN.category, 10, 100);
            Product product2 = this.tradingSystem.getStoresList().stream().filter(store1 -> store1.getStoreId() == store.getStoreId())
                    .findFirst().get().products.stream().filter(product3 -> product3.getName().equals("desk")).findFirst().get();
            product2.setRank(5);

            // arrange the actual list to be returned
            List<ProductDto> productsToFilter = new ArrayList<>();
            productsToFilter.add(new ProductDto(product1.getProductSn(), product1.getName(), product1.getCategory().category, product1.getAmount(),
                    product1.getCost(), product1.getRank(), product1.getStoreId(), product1.getDiscountType(), product1.getPurchaseType()));
            productsToFilter.add(new ProductDto(product2.getProductSn(), product2.getName(), product2.getCategory().category, product2.getAmount(),
                    product2.getCost(), product2.getRank(), product2.getStoreId(), product2.getDiscountType(), product2.getPurchaseType()));
            // call the function
            List<ProductDto> productsFilteredActual = tradingSystemFacade.filterByProductRank(productsToFilter, 5);

            // arrange the expected list to be returned
            List<ProductDto> productsExpected = new ArrayList<>();
            productsExpected.add(new ProductDto(product2.getProductSn(), product2.getName(), product2.getCategory().category, product2.getAmount(),
                    product2.getCost(), product2.getRank(), product2.getStoreId(), product2.getDiscountType(), product2.getPurchaseType()));

            Assertions.assertEquals(productsExpected, productsFilteredActual);
        }

        /**
         * check the filterByStoreRank() functionality in case of exists products in the system
         */
        @Test
        void filterByStoreRank() {
            // create a user owner
            UserSystem userSystemOwner = UserSystem.builder()
                    .userName("KingRagnar")
                    .password("Odin12")
                    .firstName("Ragnar")
                    .lastName("Lodbrok").build();
            tradingSystemFacade.registerUser(userSystemOwner.getUserName(), userSystemOwner.getPassword(),
                    userSystemOwner.getFirstName(), userSystemOwner.getLastName());
            UserSystem updatedOwner = tradingSystem.getUser(userSystemOwner.getUserName());

            // create a store1
            tradingSystem.openStore(updatedOwner, new PurchasePolicy(), new DiscountPolicy(), "castro");
            Store store1 = this.tradingSystem.getStoresList().stream().filter(store3 -> store3.getStoreName().equals("castro")).findFirst().get();
            store1.setRank(3);
            // create a store2
            tradingSystem.openStore(updatedOwner, new PurchasePolicy(), new DiscountPolicy(), "dutiMoti");
            Store store2 = this.tradingSystem.getStoresList().stream().filter(store3 -> store3.getStoreName().equals("dutiMoti")).findFirst().get();
            store2.setRank(5);

            // add a product1 for store1
            tradingSystemFacade.addProduct(userSystemOwner.getUserName(), store1.getStoreId(), "table",
                    ProductCategory.HOME_GARDEN.category, 10, 100);
            Product product1 = this.tradingSystem.getStoresList().stream().filter(store3 -> store3.getStoreId() == store1.getStoreId())
                    .findFirst().get().products.stream().filter(product3 -> product3.getName().equals("table")).findFirst().get();
            // add a product1 for store2
            tradingSystemFacade.addProduct(userSystemOwner.getUserName(), store2.getStoreId(), "table",
                    ProductCategory.HOME_GARDEN.category, 10, 100);
            Product product2 = this.tradingSystem.getStoresList().stream().filter(store3 -> store3.getStoreId() == store2.getStoreId())
                    .findFirst().get().products.stream().filter(product3 -> product3.getName().equals("table")).findFirst().get();

            // arrange the actual list to be returned
            List<ProductDto> productsToFilter = new ArrayList<>();
            productsToFilter.add(new ProductDto(product1.getProductSn(), product1.getName(), product1.getCategory().category, product1.getAmount(),
                    product1.getCost(), product1.getRank(), product1.getStoreId(), product1.getDiscountType(), product1.getPurchaseType()));
            productsToFilter.add(new ProductDto(product2.getProductSn(), product2.getName(), product2.getCategory().category, product2.getAmount(),
                    product2.getCost(), product2.getRank(), product2.getStoreId(), product2.getDiscountType(), product2.getPurchaseType()));
            // call the function
            List<ProductDto> productsFilteredActual = tradingSystemFacade.filterByStoreRank(productsToFilter, 5);

            // arrange the expected list to be returned
            List<ProductDto> productsExpected = new ArrayList<>();
            productsExpected.add(new ProductDto(product2.getProductSn(), product2.getName(), product2.getCategory().category, product2.getAmount(),
                    product2.getCost(), product2.getRank(), product2.getStoreId(), product2.getDiscountType(), product2.getPurchaseType()));

            Assertions.assertEquals(productsExpected, productsFilteredActual);

        }

        /**
         * check the filterByStoreCategory() functionality in case of exists products in the system
         */
        @Test
        void filterByStoreCategory() {
            // create a user owner
            UserSystem userSystemOwner = UserSystem.builder()
                    .userName("KingRagnar")
                    .password("Odin12")
                    .firstName("Ragnar")
                    .lastName("Lodbrok").build();
            tradingSystemFacade.registerUser(userSystemOwner.getUserName(), userSystemOwner.getPassword(),
                    userSystemOwner.getFirstName(), userSystemOwner.getLastName());
            UserSystem updatedOwner = tradingSystem.getUser(userSystemOwner.getUserName());

            // create a store
            tradingSystem.openStore(updatedOwner, new PurchasePolicy(), new DiscountPolicy(), "castro");
            Store store = this.tradingSystem.getStoresList().stream().filter(store1 -> store1.getStoreName().equals("castro")).findFirst().get();

            // add a product1
            tradingSystemFacade.addProduct(userSystemOwner.getUserName(), store.getStoreId(), "table",
                    ProductCategory.HEALTH.category, 10, 100);
            Product product1 = this.tradingSystem.getStoresList().stream().filter(store1 -> store1.getStoreId() == store.getStoreId())
                    .findFirst().get().products.stream().filter(product3 -> product3.getName().equals("table")).findFirst().get();

            // add a product2
            tradingSystemFacade.addProduct(userSystemOwner.getUserName(), store.getStoreId(), "desk",
                    ProductCategory.HOME_GARDEN.category, 10, 100);
            Product product2 = this.tradingSystem.getStoresList().stream().filter(store1 -> store1.getStoreId() == store.getStoreId())
                    .findFirst().get().products.stream().filter(product3 -> product3.getName().equals("desk")).findFirst().get();

            // arrange the actual list to be returned
            List<ProductDto> productsToFilter = new ArrayList<>();
            productsToFilter.add(new ProductDto(product1.getProductSn(), product1.getName(), product1.getCategory().category, product1.getAmount(),
                    product1.getCost(), product1.getRank(), product1.getStoreId(), product1.getDiscountType(), product1.getPurchaseType()));
            productsToFilter.add(new ProductDto(product2.getProductSn(), product2.getName(), product2.getCategory().category, product2.getAmount(),
                    product2.getCost(), product2.getRank(), product2.getStoreId(), product2.getDiscountType(), product2.getPurchaseType()));
            // call the function
            List<ProductDto> productsFilteredActual = tradingSystemFacade.filterByStoreCategory(productsToFilter, ProductCategory.HOME_GARDEN.category);

            // arrange the expected list to be returned
            List<ProductDto> productsExpected = new ArrayList<>();
            productsExpected.add(new ProductDto(product2.getProductSn(), product2.getName(), product2.getCategory().category, product2.getAmount(),
                    product2.getCost(), product2.getRank(), product2.getStoreId(), product2.getDiscountType(), product2.getPurchaseType()));

            Assertions.assertEquals(productsExpected, productsFilteredActual);
        }

        @Test
        void saveProductInShoppingBag() {
            // create a user owner
            UserSystem userSystemOwner = UserSystem.builder()
                    .userName("KingRagnar")
                    .password("Odin12")
                    .firstName("Ragnar")
                    .lastName("Lodbrok").build();
            tradingSystemFacade.registerUser(userSystemOwner.getUserName(), userSystemOwner.getPassword(),
                    userSystemOwner.getFirstName(), userSystemOwner.getLastName());
            UserSystem updatedOwner = tradingSystem.getUser(userSystemOwner.getUserName());

            // create a store
            tradingSystem.openStore(updatedOwner, new PurchasePolicy(), new DiscountPolicy(), "castro");
            Store store = this.tradingSystem.getStoresList().stream().filter(store1 -> store1.getStoreName().equals("castro")).findFirst().get();

            // add a product1
            tradingSystemFacade.addProduct(userSystemOwner.getUserName(), store.getStoreId(), "table",
                    ProductCategory.HEALTH.category, 10, 100);
            Product product1 = this.tradingSystem.getStoresList().stream().filter(store1 -> store1.getStoreId() == store.getStoreId())
                    .findFirst().get().products.stream().filter(product3 -> product3.getName().equals("table")).findFirst().get();

            Assertions.assertTrue(tradingSystemFacade.saveProductInShoppingBag(userSystemOwner.getUserName(), store.getStoreId(), product1.getProductSn(), 1));
        }

        @Test
        void viewProductsInShoppingCart() {
            // create a user owner
            UserSystem userSystemOwner = UserSystem.builder()
                    .userName("KingRagnar")
                    .password("Odin12")
                    .firstName("Ragnar")
                    .lastName("Lodbrok").build();
            tradingSystemFacade.registerUser(userSystemOwner.getUserName(), userSystemOwner.getPassword(),
                    userSystemOwner.getFirstName(), userSystemOwner.getLastName());
            UserSystem updatedOwner = tradingSystem.getUser(userSystemOwner.getUserName());
            ShoppingCart shoppingCart = createShoppingCart();

            ShoppingCartDto shoppingCartDto = tradingSystemFacade.viewProductsInShoppingCart(userSystemOwner.getUserName());
            assertShoppingCart(shoppingCart, shoppingCartDto);
        }

        @Test
        void removeProductInShoppingBag() {
            // create a user owner
            UserSystem userSystemOwner = setUpOwnerStore();
            tradingSystemFacade.registerUser(userSystemOwner.getUserName(), userSystemOwner.getPassword(),
                    userSystemOwner.getFirstName(), userSystemOwner.getLastName());
            UserSystem updatedOwner = tradingSystem.getUser(userSystemOwner.getUserName());

            // create a store
            tradingSystem.openStore(updatedOwner, new PurchasePolicy(), new DiscountPolicy(), "castro");
            Store store = this.tradingSystem.getStoresList().stream().filter(store1 -> store1.getStoreName().equals("castro")).findFirst().get();

            // add a product1
            tradingSystemFacade.addProduct(updatedOwner.getUserName(), store.getStoreId(), "table",
                    ProductCategory.HEALTH.category, 10, 100);
            Product product1 = this.tradingSystem.getStoresList().stream().filter(store1 -> store1.getStoreId() == store.getStoreId())
                    .findFirst().get().products.stream().filter(product3 -> product3.getName().equals("table")).findFirst().get();

            // add the product to the shopping bag
            updatedOwner.saveProductInShoppingBag(store, product1, 1);

            Assertions.assertTrue(tradingSystemFacade.removeProductInShoppingBag(updatedOwner.getUserName(), store.getStoreId(), product1.getProductSn()));
        }


        @Test
        void purchaseShoppingCart() {
            PaymentDetails paymentDetails = new PaymentDetails();
            BillingAddress billingAddress = new BillingAddress();

            // create a user owner
            UserSystem userSystemOwner = setUpOwnerStore();
            tradingSystemFacade.registerUser(userSystemOwner.getUserName(), userSystemOwner.getPassword(),
                    userSystemOwner.getFirstName(), userSystemOwner.getLastName());
            UserSystem updatedOwner = tradingSystem.getUser(userSystemOwner.getUserName());

            // create a store
            tradingSystem.openStore(updatedOwner, new PurchasePolicy(), new DiscountPolicy(), "castro");
            Store store = this.tradingSystem.getStoresList().stream().filter(store1 -> store1.getStoreName().equals("castro")).findFirst().get();

            // add a product1
            tradingSystemFacade.addProduct(updatedOwner.getUserName(), store.getStoreId(), "table",
                    ProductCategory.HEALTH.category, 10, 100);
            Product product1 = this.tradingSystem.getStoresList().stream().filter(store1 -> store1.getStoreId() == store.getStoreId())
                    .findFirst().get().products.stream().filter(product3 -> product3.getName().equals("table")).findFirst().get();

            // add the product to the shopping bag
            updatedOwner.saveProductInShoppingBag(store, product1, 1);


            ShoppingCartDto shoppingCartDto = modelMapper.map(updatedOwner.getShoppingCart(), ShoppingCartDto.class);
            PaymentDetailsDto paymentDetailsDto = modelMapper.map(paymentDetails, PaymentDetailsDto.class);
            BillingAddressDto billingAddressDto = modelMapper.map(billingAddress, BillingAddressDto.class);
            ReceiptDto actualReceipt = tradingSystemFacade.purchaseShoppingCart(shoppingCartDto, paymentDetailsDto, billingAddressDto);

            Map<Product, Integer> productsBought = new HashMap<>();
            productsBought.put(product1, 1);
            Receipt expectedReceipt = new Receipt(store.getStoreId(), updatedOwner.getUserName(), product1.getCost(), productsBought);

            assertionReceipt(expectedReceipt, actualReceipt);
        }

        @Test
        void testPurchaseShoppingCart() {
            //setUp //TODO
            PaymentDetails paymentDetails = setUpPaymentDetails();
            BillingAddress billingAddress = setUpBillingAddress();
            UserSystem userSystemOwner = setUpOwnerStore();
            //initial
            tradingSystemFacade.registerUser(userSystemOwner.getUserName(), userSystemOwner.getPassword(),
                    userSystemOwner.getFirstName(), userSystemOwner.getLastName());
            UserSystem regUser = tradingSystem.getUser(userSystemOwner.getUserName());
            tradingSystem.openStore(regUser, new PurchasePolicy(), new DiscountPolicy(), "castro");
            Store store = this.tradingSystem.getStoresList().stream().filter(store1 -> store1.getStoreName().equals("castro")).findFirst().get();
            tradingSystemFacade.addProduct(regUser.getUserName(), store.getStoreId(), "table",
                    ProductCategory.HEALTH.category, 10, 100);
            Product product = this.tradingSystem.getStoresList().stream().filter(store1 -> store1.getStoreId() == store.getStoreId())
                    .findFirst().get().products.stream().filter(product3 -> product3.getName().equals("table")).findFirst().get();
            // add the product to the shopping bag
            regUser.saveProductInShoppingBag(store, product, 1);

            // for call the function
            PaymentDetailsDto paymentDetailsDto = modelMapper.map(paymentDetails, PaymentDetailsDto.class);
            BillingAddressDto billingAddressDto = modelMapper.map(billingAddress, BillingAddressDto.class);
            //ReceiptDto actualReceipt = tradingSystemFacade.purchaseShoppingCart(regUser.getUserName(), paymentDetailsDto, billingAddressDto);

            Map<Product, Integer> productsBought = new HashMap<>();
            productsBought.put(product, 1);
            Receipt expectedReceipt = new Receipt(store.getStoreId(), regUser.getUserName(), product.getCost(), productsBought);

            //assertionReceipt(expectedReceipt, actualReceipt);
        }
    }


    // ******************************* assert functions ******************************* //
    private void assertUserSystem(Set<UserSystem> userSystems, Set<UserSystemDto> userSystemDtos) {
        if (Objects.nonNull(userSystems)) {
            userSystems.forEach(userSystemExpected -> {
                Optional<UserSystemDto> userSystemOptional = userSystemDtos.stream()
                        .filter(userSystem1 -> userSystem1.getUserName().equals(userSystemExpected.getUserName()))
                        .findFirst();
                Assertions.assertTrue(userSystemOptional.isPresent());
                UserSystemDto userSystemDto = userSystemOptional.get();
                Assertions.assertEquals(userSystemExpected.getUserName(), userSystemDto.getUserName());
                Assertions.assertEquals(userSystemExpected.getFirstName(), userSystemDto.getFirstName());
                Assertions.assertEquals(userSystemExpected.getLastName(), userSystemDto.getLastName());
                assertSetStore(userSystemExpected.getOwnedStores(), userSystemDto.getOwnedStores());
                assertSetStore(userSystemExpected.getManagedStores(), userSystemDto.getManagedStores());
                assertShoppingCart(userSystemExpected.getShoppingCart(), userSystemDto.getShoppingCart());
                assertReceipts(userSystemExpected.getReceipts(), userSystemDto.getReceipts());
            });
        }
    }

    private void assertShoppingCart(ShoppingCart shoppingCartExpected, ShoppingCartDto shoppingCartActual) {
        shoppingCartExpected.getShoppingBagsList().forEach((storeKey, shoppingBagExpected) -> {
            Map.Entry<StoreDto, ShoppingBagDto> storeDtoShoppingBagDtoEntry = shoppingCartActual.getShoppingBagsList().entrySet().stream()
                    .filter(entry -> entry.getKey().getStoreId() == storeKey.getStoreId())
                    .findFirst().orElseThrow(RuntimeException::new);
            ShoppingBagDto shoppingBagDto = storeDtoShoppingBagDtoEntry.getValue();
            Assertions.assertNotNull(shoppingBagDto);
            //Assertions.assertEquals(shoppingBagExpected.getProductListFromStore(), shoppingBagDto.getProductListFromStore());
        });
    }

    private void assertSetStore(Set<Store> stores, Set<StoreDto> storeDtos) {
        stores.forEach(
                storeExpected -> {
                    Optional<StoreDto> storeDtoOpt = storeDtos.stream()
                            .filter(storeDto -> storeDto.getStoreId() == storeExpected.getStoreId())
                            .findFirst();
                    Assertions.assertTrue(storeDtoOpt.isPresent());
                    StoreDto storeDto = storeDtoOpt.get();
                    assertionStore(storeExpected, storeDto);
                }
        );
    }

    private void assertPurchasePolicy(PurchasePolicy purchasePolicy, PurchasePolicyDto purchasePolicy1) {
        Assertions.assertEquals(purchasePolicy.getWhoCanBuyStatus(), purchasePolicy1.getWhoCanBuyStatus());
        Assertions.assertEquals(purchasePolicy.isAllAllowed(), purchasePolicy1.isAllAllowed());
    }

    private void assertProducts(Set<Product> products, Set<ProductDto> productsDtos) {
        products.forEach(product -> {
            Optional<ProductDto> productDtoOptional = productsDtos.stream().filter(productDto -> productDto.getProductSn() == product.getProductSn())
                    .findFirst();
            Assertions.assertTrue(productDtoOptional.isPresent());
            assertProduct(product, productDtoOptional.get());
        });
    }

    private void assertProduct(Product product, ProductDto productDto) {
        Assertions.assertEquals(product.getProductSn(), productDto.getProductSn());
        Assertions.assertEquals(product.getName(), productDto.getName());
        Assertions.assertEquals(product.getCategory().category, productDto.getCategory());
        Assertions.assertEquals(product.getAmount(), productDto.getAmount());
        Assertions.assertEquals(product.getCost(), productDto.getCost());
        Assertions.assertEquals(product.getRank(), productDto.getRank());
        Assertions.assertEquals(product.getStoreId(), productDto.getStoreId());
    }

    private void assertDiscountPolicy(DiscountPolicy discountPolicy, DiscountPolicyDto discountPolicy1) {
        Assertions.assertEquals(discountPolicy.getWhoCanBuyStatus(), discountPolicy1.getWhoCanBuyStatus());
        Assertions.assertEquals(discountPolicy.isAllAllowed(), discountPolicy1.isAllAllowed());
    }

    private void assertReceipts(List<Receipt> receipts, List<ReceiptDto> receiptDtos) {
        if (Objects.nonNull(receipts)) {
            Assertions.assertEquals(receipts.size(), receiptDtos.size());
            receipts.forEach(
                    receipt -> {
                        Optional<ReceiptDto> receiptDtoOpt = receiptDtos.stream()
                                .filter(receiptDto -> receiptDto.getReceiptSn() == receipt.getReceiptSn())
                                .findFirst();
                        Assertions.assertTrue(receiptDtoOpt.isPresent());
                        ReceiptDto receiptDto = receiptDtoOpt.get();
                        assertionReceipt(receipt, receiptDto);
                    }
            );
        }
    }

    private void assertionReceipt(Receipt receipt, ReceiptDto receiptDto) {
        Assertions.assertEquals(receipt.getReceiptSn(), receiptDto.getReceiptSn());
        Assertions.assertEquals(receipt.getStoreId(), receiptDto.getStoreId());
        Assertions.assertEquals(receipt.getUserName(), receiptDto.getUserName());
        Assertions.assertEquals(receipt.getAmountToPay(), receiptDto.getAmountToPay());
        assertMapProducts(receipt.getProductsBought(), receiptDto.getProductsBought());
    }

    private void assertMapProducts(Map<Product, Integer> products, Map<ProductDto, Integer> productsDtos) {
        products.keySet().forEach(product -> {
            Optional<ProductDto> productDtoOptional = productsDtos.keySet().stream().filter(productDto ->
                    productDto.getProductSn() == product.getProductSn())
                    .findFirst();
            Assertions.assertTrue(productDtoOptional.isPresent());
            assertProduct(product, productDtoOptional.get());
            Assertions.assertEquals(products.get(product).intValue(), productsDtos.get(productDtoOptional.get()).intValue());
        });
    }

    private void assertionStore(Store store, StoreDto storeDto) {
        Assertions.assertEquals(store.getStoreId(), storeDto.getStoreId());
        Assertions.assertEquals(store.getStoreName(), storeDto.getStoreName());
        assertProducts(store.getProducts(), storeDto.getProducts());
        assertPurchasePolicy(store.getPurchasePolicy(), storeDto.getPurchasePolicy());
        assertDiscountPolicy(store.getDiscountPolicy(), storeDto.getDiscountPolicy());
        Assertions.assertEquals(store.getDiscountType().type, storeDto.getDiscountType());
        Assertions.assertEquals(store.getPurchaseType().type, storeDto.getPurchaseType());
        assertReceipts(store.getReceipts(), storeDto.getReceipts());
        Assertions.assertEquals(store.getRank(), storeDto.getRank());
    }

// ******************************* set up ******************************* //

    private UserSystem setUpOwnerStore() {
        return UserSystem.builder()
                .userName("KingRagnar")
                .password("Odin12")
                .firstName("Ragnar")
                .lastName("Lodbrok").build();
    }

    private BillingAddress setUpBillingAddress() {
        return BillingAddress.builder()
                .zipCode("1234567")
                .build();
    }

    private PaymentDetails setUpPaymentDetails() {
        return PaymentDetails.builder()
                .creditCardNumber("123456789")
                .ccv(3)
                .month("10")
                .year("2021")
                .build();
    }

    private List<Receipt> setUpReceipts() {
        List<Receipt> receipts = new ArrayList<>();
        for (int counter = 0; counter <= 10; counter++) {
            receipts.add(Receipt.builder()
                    .receiptSn(counter)
                    .storeId(counter)
                    .userName("username" + counter)
                    .purchaseDate(new Date())
                    .amountToPay(counter)
                    .productsBought(createMapOfProducts())
                    .build());
        }
        return receipts;
    }

    private Set<Product> setUpProducts() {
        Set<Product> products = new HashSet<>();
        for (int counter = 0; counter <= 10; counter++) {
            products.add(Product.builder()
                    .productSn(counter)
                    .name("productName" + counter)
                    .category(ProductCategory.values()[counter % ProductCategory.values().length])
                    .amount(counter)
                    .cost(counter)
                    .rank(counter)
                    .storeId(counter)
                    .build());
        }
        return products;
    }

    private Map<Product, Integer> createMapOfProducts() {
        Map<Product, Integer> products = new HashMap<>();
        for (int counter = 1; counter <= 10; counter++) {
            products.put((Product.builder()
                    .productSn(counter)
                    .name("productName" + counter)
                    .category(ProductCategory.values()[counter % ProductCategory.values().length])
                    .amount(counter + 1)
                    .cost(counter)
                    .rank(counter)
                    .storeId(counter)
                    .build()), counter);
        }
        return products;
    }

    private Set<UserSystem> setupUsers() {
        Set<UserSystem> userSystems = new HashSet<>();
        for (int counter = 0; counter <= 10; counter++) {
            userSystems.add(UserSystem.builder()
                    .userName("username" + counter)
                    .password("password" + counter)
                    .firstName("firstName" + counter)
                    .lastName("lastName" + counter)
                    .isLogin(false)
                    .build());
        }
        return userSystems;
    }

    private Set<Store> setUpStores() {
        Set<Store> stores = new HashSet<>();
        for (int counter = 0; counter <= 10; counter++) {
            stores.add(Store.builder()
                    .storeId(counter)
                    .discountType(DiscountType.NONE)
                    .purchaseType(PurchaseType.BUY_IMMEDIATELY)
                    .purchasePolicy(new PurchasePolicy())
                    .discountPolicy(new DiscountPolicy())
                    .storeName("storeName" + counter)
                    .rank(counter)
                    .build());
        }
        return stores;
    }

    private Store createStore() {
        //init
        int storeId = 1;
        int rank = 1;
        String storeName = "storeName";

        //init products
        Set<Product> products = setUpProducts();

        PurchasePolicy purchasePolicy = new PurchasePolicy();
        DiscountPolicy discountPolicy = new DiscountPolicy();
        Set<UserSystem> owners = setupUsers();
        List<Receipt> receipts = setUpReceipts();

        return Store.builder()
                .storeId(storeId)
                .owners(owners)
                .storeName(storeName)
                .discountPolicy(discountPolicy)
                .discountType(DiscountType.NONE)
                .products(products)
                .receipts(receipts)
                .rank(rank)
                .purchaseType(PurchaseType.BUY_IMMEDIATELY)
                .purchasePolicy(purchasePolicy)
                .build();
    }

    private Product createProduct(int productId) {
        return Product.builder()
                .storeId(productId)
                .category(ProductCategory.values()[productId % ProductCategory.values().length])
                .rank(productId)
                .cost(productId)
                .amount(productId)
                .name("product" + productId)
                .productSn(productId)
                .build();
    }

    private ShoppingCart createShoppingCart() {
        //create shoppingBags
        Map<Integer, Integer> shoppingBagMap = new HashMap<>();
        for (int counter = 0; counter < 10; counter++) {
            shoppingBagMap.put(counter, counter);
        }
        //ShoppingBag shoppingBag = new ShoppingBag(shoppingBagMap);

        Map<Store, ShoppingBag> shoppingBags = new HashMap<>();
        Set<Store> stores = setUpStores();
        //stores.forEach(store1 -> shoppingBags.put(store1, shoppingBag));

        // create ShoppingCart
        return ShoppingCart.builder()
                .shoppingBagsList(shoppingBags)
                .build();

    }

    // ******************************* General ******************************* //
    private List<ProductDto> convertProductDtoList(List<Product> products) {
        Type listType = new TypeToken<List<ProductDto>>() {
        }.getType();
        return modelMapper.map(products, listType);
    }

    private List<ReceiptDto> convertReceiptDtoList(@NotNull List<@NotNull Receipt> receipts) {
        Type listType = new TypeToken<List<ReceiptDto>>(){}.getType();
        return modelMapper.map(receipts, listType);
    }
}
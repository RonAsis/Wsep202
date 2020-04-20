package com.wsep202.TradingSystem.domain.trading_system_management;

import com.github.rozidan.springboot.modelmapper.WithModelMapper;
import com.wsep202.TradingSystem.domain.config.TradingSystemConfiguration;
import com.wsep202.TradingSystem.domain.exception.UserDontExistInTheSystemException;
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
            when(tradingSystem.addOwnerToStore(store,userSystem, newOwner)).thenReturn(true);

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
            when(tradingSystem.addMangerToStore(store,userSystem, newOwner)).thenReturn(true);

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
            String discountType = DiscountType.OPEN_DISCOUNT.type;
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
            Assertions.assertTrue(tradingSystemFacade
                    .openStore(ownerUsername, purchasePolicyDto, discountPolicyDto, discountType, purchaseType, storeName));
        }

        @Test
        void registerUser() {
            String userName = "username";
            String password = "password";
            String firstName = "firstName";
            String lastName = "lastName";
            when(factoryObjects.createSystemUser(userName, password, firstName, lastName)).thenReturn(userSystem);
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
            when(tradingSystem.filterByProductRank(products,rank)).thenReturn(products);
            List<ProductDto> productDtos = tradingSystemFacade.filterByProductRank(productsDtoArg, rank);
            assertProducts(new HashSet<>(products), new HashSet<>(productDtos));
        }

        @Test
        void filterByStoreRank() {
            int rank = 0;
            List<Product> products = new ArrayList<>(setUpProducts());
            List<ProductDto> productsDtoArg = convertProductDtoList(products);
            when(tradingSystem.filterByStoreRank(products,rank)).thenReturn(products);
            List<ProductDto> productDtos = tradingSystemFacade.filterByStoreRank(productsDtoArg, rank);
            assertProducts(new HashSet<>(products), new HashSet<>(productDtos));
        }

        @Test
        void filterByStoreCategory() {
            String category = ProductCategory.values()[0].category;
            List<Product> products = new ArrayList<>(setUpProducts());
            List<ProductDto> productsDtoArg = convertProductDtoList(products);
            when(tradingSystem.filterByStoreCategory(products,ProductCategory.getProductCategory(category))).thenReturn(products);
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
            ShoppingCart shoppingCart = createShoppingCart();
            ShoppingCartDto shoppingCartDto =  modelMapper.map(shoppingCart, ShoppingCartDto.class);
            PaymentDetailsDto paymentDetailsDto = modelMapper.map(paymentDetails, PaymentDetailsDto.class);
            BillingAddressDto billingAddressDto = modelMapper.map(billingAddress, BillingAddressDto.class);
            List<Receipt> receipts = setUpReceipts();
            Receipt receipt = receipts.get(0);

            when(tradingSystem.purchaseShoppingCart(any(ShoppingCart.class),paymentDetails,billingAddress)).thenReturn(receipts);
            ReceiptDto receiptDto = tradingSystemFacade.purchaseShoppingCart(shoppingCartDto,paymentDetailsDto,billingAddressDto);
            assertReceipts(Collections.singletonList(receipt), Collections.singletonList(receiptDto));
        }

        @Test
        void testPurchaseShoppingCart() {
            //init
            String username = "username";
            ShoppingCart shoppingCart = createShoppingCart();
            ShoppingCartDto shoppingCartDto =  modelMapper.map(shoppingCart, ShoppingCartDto.class);
            PaymentDetailsDto paymentDetailsDto = modelMapper.map(paymentDetails, PaymentDetailsDto.class);
            BillingAddressDto billingAddressDto = modelMapper.map(billingAddress, BillingAddressDto.class);
            List<Receipt> receipts = setUpReceipts();
            Receipt receipt = receipts.get(0);

            //mock
            when(tradingSystem.getUser(username)).thenReturn(userSystem);
            when(tradingSystem.purchaseShoppingCart(paymentDetails, billingAddress, userSystem)).thenReturn(receipts);
            ReceiptDto receiptDto = tradingSystemFacade.purchaseShoppingCart(username,paymentDetailsDto,billingAddressDto);
            assertReceipts(Collections.singletonList(receipt), Collections.singletonList(receiptDto));
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
        private  List<Receipt> receipts;
        private Set<Store> stores;
        private UserSystem currUser;
        private UserSystem testUserSystem;
        private UserSystem admin;

        @BeforeEach
        void setUp(){
            admin = UserSystem.builder()
                    .userName("admin")
                    .password("admin")
                    .build();
            addUsers();
            Optional<UserSystem> userSystemOptional = userSystems.stream().findFirst();
            Assertions.assertTrue(userSystemOptional.isPresent());
            currUser = userSystemOptional.get();
            receipts = setUpReceipts();
            currUser.setReceipts(receipts);
            addStores();
        }

        private void addStores() {
            stores = setUpStores();
            stores.forEach(store ->
                    tradingSystem.openStore(currUser, store.getPurchasePolicy(), store.getDiscountPolicy(), store.getStoreName())
            );
        }

        void addUsers(){
            userSystems = setupUsers();
            userSystems.forEach(userSystem ->
                    tradingSystem.registerNewUser(userSystem));
            currUser = userSystems.stream().findFirst().get();
        }

        /**
         * check the viewPurchaseHistoryUser() functionality in case of exists user in the system
         */
        @Test
        void viewPurchaseHistoryUserPositive() {
            List<Receipt> receipts = setUpReceipts();
            currUser.setReceipts(receipts);
            List<ReceiptDto> receiptDtos = tradingSystemFacade.viewPurchaseHistory(currUser.getUserName());
            assertReceipts(receipts, receiptDtos);
        }

        /**
         * check the viewPurchaseHistoryUser() functionality in case of not exists user in the system
         */
        @Test
        void viewPurchaseHistoryUserNotExist() {
            Assertions.assertThrows(UserDontExistInTheSystemException.class, () -> {
                List<ReceiptDto> receiptDtos = tradingSystemFacade.viewPurchaseHistory("userNotExist");
                assertReceipts(receipts, receiptDtos);
            });
        }

        /**
         * check the viewPurchaseHistoryAdministratorOfStore() functionality in case of exists admin and store in the system
         */
        @Test
        void viewPurchaseHistoryAdministratorOfStorePositive() {
            testUserSystem = UserSystem.builder()
                    .userName("KingRagnar")
                    .password("Odin12")
                    .firstName("Ragnar")
                    .lastName("Lodbrok").build();
            tradingSystemFacade.registerUser(testUserSystem.getUserName(),testUserSystem.getPassword(),testUserSystem.getFirstName(),testUserSystem.getLastName());
            tradingSystem.openStore(tradingSystem.getUser(testUserSystem.getUserName()),new PurchasePolicy(), new DiscountPolicy(), "castro");
            /*stores.forEach(store ->
                    tradingSystem.openStore(currUser, store.getPurchasePolicy(), store.getDiscountPolicy(), store.getStoreName())
            );*/
            Optional<Store> storeOptional = stores.stream().findFirst();
            Assertions.assertTrue(storeOptional.isPresent());
            Store store = storeOptional.get();
            store.setReceipts(receipts);
            List<ReceiptDto> receiptDtos = tradingSystemFacade.viewPurchaseHistory(admin.getUserName(), store.getStoreId());
            assertReceipts(receipts, receiptDtos);
        }

        @Test
        void viewPurchaseHistoryAdministratorOfStoreNegative() {

        }

        @Test
        void viewPurchaseHistoryAdministratorOfUser() {
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
        void removeManager() {
        }
        //////////////////////////////////////////////////////////////////////////

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
        void viewStoreInfo() {
        }

        @Test
        void viewProduct() {
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
        }

        @Test
        void filterByProductRank() {
        }

        @Test
        void filterByStoreRank() {
        }

        @Test
        void filterByStoreCategory() {
        }

        @Test
        void saveProductInShoppingBag() {
        }

        @Test
        void viewProductsInShoppingCart() {
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
            Assertions.assertEquals(shoppingBagExpected.getProductListFromStore(), shoppingBagDto.getProductListFromStore());
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
        if(Objects.nonNull(receipts)) {
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
        //Assertions.assertEquals(receipt.getPurchaseDate(), receiptDto.getPurchaseDate());
        Assertions.assertEquals(receipt.getAmountToPay(), receiptDto.getAmountToPay());
        assertMapProducts(receipt.getProductsBought(), receiptDto.getProductsBought());
    }

    private void assertMapProducts(Map<Product,Integer> products, Map<ProductDto, Integer> productsDtos) {
        products.keySet().forEach(product -> {
            Optional<ProductDto> productDtoOptional = productsDtos.keySet().stream().filter(productDto ->
                    productDto.getProductSn() == product.getProductSn())
                    .findFirst();
            Assertions.assertTrue(productDtoOptional.isPresent());
            assertProduct(product, productDtoOptional.get());
            Assertions.assertEquals(products.get(product).intValue(),productsDtos.get(productDtoOptional.get()).intValue());
        });
        /*products.get(product).forEach(quantityProduct -> {
            Optional<Integer> quantityProductDtoOptional = productsDtos.values().stream().filter(quantityProductDto ->
                    quantityProductDto.intValue() == quantityProduct.intValue())
                    .findFirst();
            Assertions.assertTrue(quantityProductDtoOptional.isPresent());
            Assertions.assertEquals(quantityProduct, quantityProductDtoOptional);
        });*/
    }

    private void assertionStore(Store store, StoreDto storeDto) {
        Assertions.assertEquals(store.getStoreId(), storeDto.getStoreId());
        Assertions.assertEquals(store.getStoreName(), storeDto.getStoreName());
        assertProducts(store.getProducts(), storeDto.getProducts());
        assertPurchasePolicy(store.getPurchasePolicy(), storeDto.getPurchasePolicy());
        assertDiscountPolicy(store.getDiscountPolicy(), storeDto.getDiscountPolicy());
        Assertions.assertEquals(store.getDiscountType().type, storeDto.getDiscountType());
        Assertions.assertEquals(store.getPurchaseType().type, storeDto.getPurchaseType());
        assertUserSystem(store.getOwners(), storeDto.getOwners());
        assertReceipts(store.getReceipts(), storeDto.getReceipts());
        Assertions.assertEquals(store.getRank(), storeDto.getRank());
    }

// ******************************* set up ******************************* //

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
                    .amount(counter+1)
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
                    .discountType(DiscountType.OPEN_DISCOUNT)
                    .purchaseType(PurchaseType.BUY_IMMEDIATELY)
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
        Set<Product> products =  setUpProducts();

        PurchasePolicy purchasePolicy = new PurchasePolicy();
        DiscountPolicy discountPolicy = new DiscountPolicy();
        Set<UserSystem> owners = setupUsers();
        List<Receipt> receipts = setUpReceipts();

        return Store.builder()
                .storeId(storeId)
                .owners(owners)
                .storeName(storeName)
                .discountPolicy(discountPolicy)
                .discountType(DiscountType.OPEN_DISCOUNT)
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
        for(int counter =0; counter< 10 ; counter++){
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
        Type listType = new TypeToken<List<ProductDto>>(){}.getType();
        return modelMapper.map(products, listType);
    }
}
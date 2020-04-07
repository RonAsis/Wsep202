package com.wsep202.TradingSystem.domain.trading_system_management;

import com.github.rozidan.springboot.modelmapper.WithModelMapper;
import com.wsep202.TradingSystem.domain.config.TradingSystemConfiguration;
import com.wsep202.TradingSystem.domain.factory.FactoryObjects;
import com.wsep202.TradingSystem.domain.mapping.TradingSystemMapper;
import com.wsep202.TradingSystem.service.user_service.dto.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.mockito.Mockito.*;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;

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

        @BeforeEach
        public void setUp() {
            store = mock(Store.class);
            userSystem = mock(UserSystem.class);
            tradingSystem = mock(TradingSystem.class);
            factoryObjects = mock(FactoryObjects.class);
            tradingSystemFacade = new TradingSystemFacade(tradingSystem, modelMapper, factoryObjects);
        }

        @Test
        void viewPurchaseHistoryUser() {
            List<Receipt> receipts = setUpReceipts();
            String userNameTest = "userNameTest";
            when(tradingSystem.getUser(userNameTest)).thenReturn(userSystem);
            when(userSystem.getReceipts()).thenReturn(receipts);
            List<ReceiptDto> receiptDtos = tradingSystemFacade.viewPurchaseHistory(userNameTest);
            assertRecipes(receipts, receiptDtos);
        }

        @Test
        void viewPurchaseHistoryAdministratorOfStore() {
            //initial
            List<Receipt> receipts = setUpReceipts();
            Store store = mock(Store.class);
            String administratorUsername = "administratorUsername";
            int storeId = 1;

            //mock
            when(tradingSystem.getStore(administratorUsername, storeId)).thenReturn(store);
            when(store.getReceipts()).thenReturn(receipts);

            //test
            List<ReceiptDto> receiptDtos = tradingSystemFacade.viewPurchaseHistory(administratorUsername, storeId);
            assertRecipes(receipts, receiptDtos);
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
            assertRecipes(receipts, receiptDtos);
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
            assertRecipes(receipts, receiptDtos);
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
            assertRecipes(receipts, receiptDtos);
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
            when(store.addOwner(userSystem, newOwner)).thenReturn(true);

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
            when(store.addManager(userSystem, newOwner)).thenReturn(true);

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
            when(store.removeManager(userSystem, managerStore)).thenReturn(true);
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
            when(tradingSystem.openStore(userSystem, DiscountType.getDiscountType(discountType),
                    PurchaseType.getPurchaseType(purchaseType), purchasePolicy, discountPolicy, storeName)).thenReturn(true);

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

        private void assertionStore(Store store, StoreDto storeDto) {
            Assertions.assertEquals(store.getStoreId(), storeDto.getStoreId());
            Assertions.assertEquals(store.getStoreName(), storeDto.getStoreName());
            assertProducts(store.getProducts(), storeDto.getProducts());
            assertPurchasePolicy(store.getPurchasePolicy(), storeDto.getPurchasePolicy());
            assertDiscountPolicy(store.getDiscountPolicy(), storeDto.getDiscountPolicy());
            Assertions.assertEquals(store.getDiscountType().type, storeDto.getDiscountType());
            Assertions.assertEquals(store.getPurchaseType().type, storeDto.getPurchaseType());
            assertUserSystem(store.getOwners(), storeDto.getOwners());
            assertRecipes(store.getReceipts(), storeDto.getReceipts());
            Assertions.assertEquals(store.getRank(), storeDto.getRank());
        }


        private List<ReceiptDto> receipts;

        private void assertUserSystem(Set<UserSystem> userSystems, Set<UserSystemDto> userSystemDtos) {
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
                assertRecipes(userSystemExpected.getReceipts(), userSystemDto.getReceipts());
            });
        }

        private void assertShoppingCart(ShoppingCart shoppingCartExpected, ShoppingCartDto shoppingCartActual) {
            shoppingCartExpected.getShoppingBags().forEach((storeKey, shoppingBagExpected) -> {
                ShoppingBagDto shoppingBagDto = shoppingCartActual.getShoppingBags().get(storeKey);
                Assertions.assertNotNull(shoppingBagDto);
                Assertions.assertEquals(shoppingBagExpected.getMapProductSnToAmount(), shoppingBagDto.getMapProductSnToAmount());
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
            //TODO when PurchasePolicy and PurchasePolicyDto are ready
        }

        private void assertProducts(Map<ProductCategory, Set<Product>> products, Map<ProductCategory, Set<ProductDto>> products1) {
        }

        private void assertDiscountPolicy(DiscountPolicy discountPolicy, DiscountPolicyDto discountPolicy1) {
            //TODO when discountPolicy and DiscountPolicyDto are ready
        }

        private void assertRecipes(List<Receipt> receipts, List<ReceiptDto> receiptDtos) {
            Assertions.assertEquals(receipts.size(), receiptDtos.size());
            receipts.forEach(
                    receipt -> {
                        //TODO - compare the receipts
                    }
            );
        }

        private List<Receipt> setUpReceipts() {
            List<Receipt> receipts = new ArrayList<>();
            for (int counter = 0; counter <= 10; counter++) {
                receipts.add(Receipt.builder() //TODO - when Receipt will be ready
                        .build());
            }
            return receipts;
        }

        private Set<Product> setUpProducts() {
            Set<Product> products = new HashSet<>();
            for (int counter = 0; counter <= 10; counter++) {
                products.add(Product.builder()
                        .name("productName" + counter)
                        .amount(counter)
                        .category(ProductCategory.values()[counter % ProductCategory.values().length])
                        .productSn(counter)
                        .storeId(counter)
                        .cost(counter)
                        .rank(counter)
                        .build());
            }
            return products;
        }

        private Set<UserSystem> setupUsers() {
            Set<UserSystem> userSystems = new HashSet<>();
            for (int counter = 0; counter <= 10; counter++) {
                userSystems.add(UserSystem.builder()
                        .firstName("firstName" + counter)
                        .lastName("lastName" + counter)
                        .userName("username" + counter)
                        .isLogin(false)
                        .password("password" + counter)
                        .receipts(setUpReceipts())
                        .shoppingCart(new ShoppingCart())
                        .ownedStores(setUpStores())
                        .managedStores(setUpStores())
                        .build());
            }
            return userSystems;
        }

        private Set<Store> setUpStores() {
            Set<Store> stores = new HashSet<>();
            for (int counter = 0; counter <= 10; counter++) {
                stores.add(Store.builder()
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
            Map<ProductCategory, Set<Product>> products = new HashMap<>();
            Set<Product> productsSet = setUpProducts();
            products.putIfAbsent(ProductCategory.BOOKS_MOVIES_MUSIC, productsSet);

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
    }

    ///////////////////////////////integration test /////////////////////
    @Nested
    @ContextConfiguration(classes = {TradingSystemConfiguration.class})
    @WithModelMapper(basePackageClasses = TradingSystemMapper.class)
    public class TradingSystemFacadeTestIntegration {

        @Autowired
        private TradingSystemFacade tradingSystemFacade;

        @Test
        void viewPurchaseHistoryUser() {
        }

        @Test
        void viewPurchaseHistoryAdministratorOfStore() {
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
}
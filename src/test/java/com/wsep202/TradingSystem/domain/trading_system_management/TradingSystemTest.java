package com.wsep202.TradingSystem.domain.trading_system_management;

import org.junit.jupiter.api.*;
import org.mockito.invocation.InvocationOnMock;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.Mockito.*;

class TradingSystemTest {

    private TradingSystem tradingSystem;

    @AfterEach
    void tearDown() {
    }

    /**
     * unit tests for the TradingSystem
     */
    @Nested
    public class TradingSystemTestUnit {

        @BeforeEach
        void setUp() {
            ExternalServiceManagement externalServiceManagement = mock(ExternalServiceManagement.class);
            tradingSystem = new TradingSystem(externalServiceManagement);
            doNothing().when(externalServiceManagement).connect();
        }

        @Test
        void buyShoppingCart() {
        }

        @Test
        void registerNewUser() {
        }

        @Test
        void login() {
        }

        @Test
        void closeStore() {
        }

        @Test
        void logout() {
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
            List<Store> stores = setUpStoresForFilterTests(products);

            //mocks for this function
            tradingSystem = mock(TradingSystem.class);
            when(tradingSystem.filterByProductRank(anyList(), anyInt())).thenCallRealMethod();
            when(tradingSystem.getStore(anyInt())).then(invocation ->{
                int storeId = invocation.getArgument(0);
                return stores.get(storeId);
            });

            // the tests
            for (int rank = -1; rank < 100; rank++) {
                List<Product> productsActual = tradingSystem.filterByProductRank(products, rank);
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
public class TradingSystemTestIntegration {

    @Test
    void buyShoppingCart() {
    }

    @Test
    void registerNewUser() {
    }

    @Test
    void login() {
    }

    @Test
    void closeStore() {
    }

    @Test
    void logout() {
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
    void purchaseShoppingCart() {
    }

    @Test
    void testPurchaseShoppingCart() {
    }

    @Test
    void openStore() {
    }
}
}
package com.wsep202.TradingSystem.domain.trading_system_management.policy_purchase;

import com.wsep202.TradingSystem.domain.exception.PurchasePolicyException;
import com.wsep202.TradingSystem.domain.trading_system_management.Product;
import com.wsep202.TradingSystem.domain.trading_system_management.purchase.BillingAddress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ShoppingBagDetailsPolicyTest {

    @Nested
    public class ShoppingBagDetailsPolicyTestUnit {

        ShoppingBagDetailsPolicy shoppingBagDetailsPolicy;
        int min, max;
        Purchase testPurchase;
        Map<Product, Integer> testProducts;
        BillingAddress testBillingAddress1;

        @BeforeEach
        void setUp() {
            min =2;
            max =4;
            shoppingBagDetailsPolicy = ShoppingBagDetailsPolicy.builder()
                                        .min(min)
                                        .max(max).build();
            testProducts = new HashMap<>();
            testPurchase = mock(Purchase.class);
            when(testPurchase.getPurchaseId()).thenReturn((long) 12);
            Product product1 = mock(Product.class);
            Product product2 = mock(Product.class);
            testProducts.put(product1, 3);
            testProducts.put(product2, 2);
            testBillingAddress1 = mock(BillingAddress.class);
            when(testBillingAddress1.getCountry()).thenReturn("Israel");
        }

        @Test
        void isApprovedSuc() {
            //check that num of products is ok
            assertTrue(testProducts.size() <= max && testProducts.size() >= min);
            //check that method works
            assertTrue(shoppingBagDetailsPolicy.isApproved(testPurchase, testProducts, testBillingAddress1));
        }

        @Test
        void isApprovedBadAmount() {
            for (int i =0; i<5; i++){
                Product product = mock(Product.class);
                testProducts.put(product,i);
            }
            //check that there are to many products in bag
            assertFalse(testProducts.size() <= max);
            //check that method fails
            assertThrows(PurchasePolicyException.class, () -> shoppingBagDetailsPolicy.isApproved(testPurchase, testProducts, testBillingAddress1));
        }

        @Test
        void isApprovedBadMin() {
            shoppingBagDetailsPolicy.setMin(-1);
            //check that method fails
            assertThrows(PurchasePolicyException.class, () -> shoppingBagDetailsPolicy.isApproved(testPurchase, testProducts, testBillingAddress1));
        }

        @Test
        void editSuc() {
            //check that the min is right
            assertEquals(min, shoppingBagDetailsPolicy.getMin());
            //check that method works
            assertTrue(shoppingBagDetailsPolicy.edit(testPurchase, 1, 4));
            //check that min & max changed
            assertTrue(shoppingBagDetailsPolicy.getMin() == 1 && shoppingBagDetailsPolicy.getMax() == 4);
        }

        @Test
        void editLessThanZero() {
            //check that the min is right
            assertEquals(min, shoppingBagDetailsPolicy.getMin());
            //check that method works
            assertFalse(shoppingBagDetailsPolicy.edit(testPurchase, -1, 4));
            //check that the min didn't change
            assertEquals(min, shoppingBagDetailsPolicy.getMin());
        }

        @Test
        void editMinBiggerThanMax() {
            //check that the min is right
            assertEquals(min, shoppingBagDetailsPolicy.getMin());
            //check that method works
            assertFalse(shoppingBagDetailsPolicy.edit(testPurchase, 4, 1));
            //check that the min didn't change
            assertEquals(min, shoppingBagDetailsPolicy.getMin());
        }
    }

/////////////////////////////////////////////////////////////////////////////////////////Integration///////////////////////////////////////////////////////////////////////////////
        @Nested
        public class ShoppingBagDetailsPolicyTestIntegration {

            ShoppingBagDetailsPolicy shoppingBagDetailsPolicy;
            int min, max;
            Purchase testPurchase;
            Map<Product, Integer> testProducts;
            BillingAddress testBillingAddress1;

            @BeforeEach
            void setUp() {
                min =2;
                max =4;
                shoppingBagDetailsPolicy = ShoppingBagDetailsPolicy.builder()
                        .min(min)
                        .max(max).build();
                testPurchase = new Purchase(shoppingBagDetailsPolicy,PurchaseType.SHOPPING_BAG_DETAILS,"shopping policy test");
                testProducts = new HashMap<>();
                testBillingAddress1 = new BillingAddress("Will Smith", "5 st'", "London", "UK", "12345678");
                Product product1 = new Product();
                Product product2 = new Product();
                testProducts.put(product1, 3);
                testProducts.put(product2, 2);
            }

            @Test
            void isApprovedSuc() {
                //check that num of products is ok
                assertTrue(testProducts.size() <= max && testProducts.size() >= min);
                //check that method works
                assertTrue(shoppingBagDetailsPolicy.isApproved(testPurchase, testProducts, testBillingAddress1));
            }

            @Test
            void isApprovedBadAmount() {
                for (int i =0; i<5; i++){
                    Product product = mock(Product.class);
                    testProducts.put(product,i);
                }
                //check that there are to many products in bag
                assertFalse(testProducts.size() <= max);
                //check that method fails
                assertThrows(PurchasePolicyException.class, () -> shoppingBagDetailsPolicy.isApproved(testPurchase, testProducts, testBillingAddress1));
            }

            @Test
            void isApprovedBadMin() {
                shoppingBagDetailsPolicy.setMin(-1);
                //check that method fails
                assertThrows(PurchasePolicyException.class, () -> shoppingBagDetailsPolicy.isApproved(testPurchase, testProducts, testBillingAddress1));
            }

            @Test
            void editSuc() {
                //check that the min is right
                assertEquals(min, shoppingBagDetailsPolicy.getMin());
                //check that method works
                assertTrue(shoppingBagDetailsPolicy.edit(testPurchase, 1, 4));
                //check that min & max changed
                assertTrue(shoppingBagDetailsPolicy.getMin() == 1 && shoppingBagDetailsPolicy.getMax() == 4);
            }

            @Test
            void editLessThanZero() {
                //check that the min is right
                assertEquals(min, shoppingBagDetailsPolicy.getMin());
                //check that method works
                assertFalse(shoppingBagDetailsPolicy.edit(testPurchase, -1, 4));
                //check that the min didn't change
                assertEquals(min, shoppingBagDetailsPolicy.getMin());
            }

            @Test
            void editMinBiggerThanMax() {
                //check that the min is right
                assertEquals(min, shoppingBagDetailsPolicy.getMin());
                //check that method works
                assertFalse(shoppingBagDetailsPolicy.edit(testPurchase, 4, 1));
                //check that the min didn't change
                assertEquals(min, shoppingBagDetailsPolicy.getMin());
            }
        }
}

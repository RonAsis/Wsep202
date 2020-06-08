package com.wsep202.TradingSystem.domain.trading_system_management.policy_purchase;

import com.wsep202.TradingSystem.domain.exception.PurchasePolicyException;
import com.wsep202.TradingSystem.domain.trading_system_management.Product;
import com.wsep202.TradingSystem.domain.trading_system_management.ProductCategory;
import com.wsep202.TradingSystem.domain.trading_system_management.purchase.BillingAddress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ProductDetailsPolicyTest {

    @Nested
    public class ProductDetailsPolicyTestUnit {
        ProductDetailsPolicy productDetailsPolicy;
        int min, max;
        int productSN;
        Purchase testPurchase;
        Map<Product, Integer> testProducts;
        BillingAddress testBillingAddress1;
        Product product1;
        Product product2;


        @BeforeEach
        void setUp() {
            min = 2;
            max = 4;
            productSN = 123456789;
            productDetailsPolicy = ProductDetailsPolicy.builder()
                                    .min(min)
                                    .max(max)
                                    .productId(productSN).build();
            testProducts = new HashMap<>();
            testPurchase = mock(Purchase.class);
            when(testPurchase.getPurchaseId()).thenReturn(12);
            product1 = mock(Product.class);
            when(product1.getProductSn()).thenReturn(productSN);
            product2 = mock(Product.class);
            when(product2.getProductSn()).thenReturn(34567891);
            testProducts.put(product1, 3);
            testProducts.put(product2, 2);
            testBillingAddress1 = mock(BillingAddress.class);
            when(testBillingAddress1.getCountry()).thenReturn("Israel");
        }

        @Test
        void isApprovedSuc() {
            //check that the method works
            assertTrue(productDetailsPolicy.isApproved(testPurchase,testProducts, testBillingAddress1));
        }

        @Test
        void isApprovedBadSn() {
            testProducts.remove(product1);
            //check that the method works
            assertTrue(productDetailsPolicy.isApproved(testPurchase,testProducts, testBillingAddress1));
        }

        @Test
        void isApprovedBadMax() {
            productDetailsPolicy.setMax(2);
            //check that the method fails
            assertThrows(PurchasePolicyException.class, () -> productDetailsPolicy.isApproved(testPurchase, testProducts, testBillingAddress1));
        }

        @Test
        void editBadSN() {
            //check that the min is right
            assertEquals(min, productDetailsPolicy.getMin());
            //check that method fails
            assertFalse(productDetailsPolicy.edit(testPurchase, 1, 4,3456321));
            //check that the min is right
            assertEquals(min, productDetailsPolicy.getMin());
        }

        @Test
        void editSuc() {
            //check that the min is right
            assertEquals(min, productDetailsPolicy.getMin());
            //check that method works
            assertTrue(productDetailsPolicy.edit(testPurchase, 1, 4,productSN));
            //check that min & max changed
            assertTrue(productDetailsPolicy.getMin() == 1 && productDetailsPolicy.getMax() == 4);
        }

        @Test
        void editLessThanZero() {
            //check that the min is right
            assertEquals(min, productDetailsPolicy.getMin());
            //check that method works
            assertFalse(productDetailsPolicy.edit(testPurchase, -1, 4,productSN));
            //check that the min didn't change
            assertEquals(min, productDetailsPolicy.getMin());
        }

        @Test
        void editMinBiggerThanMax() {
            //check that the min is right
            assertEquals(min, productDetailsPolicy.getMin());
            //check that method works
            assertFalse(productDetailsPolicy.edit(testPurchase, 4, 1,productSN));
            //check that the min didn't change
            assertEquals(min, productDetailsPolicy.getMin());
        }
    }
////////////////////////////////////////////////////////////////////////////Integration////////////////////////////////////////////////////////////////////////////////////////////

    @Nested
    public class ProductDetailsPolicyTestIntegration {
        ProductDetailsPolicy productDetailsPolicy;
        int min, max;
        int productSN;
        Purchase testPurchase;
        Map<Product, Integer> testProducts;
        BillingAddress testBillingAddress1;
        Product product1;
        Product product2;


        @BeforeEach
        void setUp() {
            min = 2;
            max = 4;
            product1 = new Product("product1", ProductCategory.HEALTH, 50, 23, 1);
            productSN = product1.getProductSn();
            productDetailsPolicy = ProductDetailsPolicy.builder()
                    .min(min)
                    .max(max)
                    .productId(productSN).build();
            testProducts = new HashMap<>();
            testPurchase = new Purchase(productDetailsPolicy, PurchaseType.BUY_IMMEDIATELY);
            product2 = new Product("product2", ProductCategory.HEALTH, 50, 12, 1);
            testProducts.put(product1, 3);
            testProducts.put(product2, 2);
            testBillingAddress1 = new BillingAddress("Will Smith", "5 st'", "London", "UK", "12345678");
        }

        @Test
        void isApprovedSuc() {
            //check that the method works
            assertTrue(productDetailsPolicy.isApproved(testPurchase,testProducts, testBillingAddress1));
        }

        @Test
        void isApprovedBadSn() {
            testProducts.remove(product1);
            //check that the method works
            assertTrue(productDetailsPolicy.isApproved(testPurchase,testProducts, testBillingAddress1));
        }

        @Test
        void isApprovedBadMax() {
            productDetailsPolicy.setMax(2);
            //check that the method fails
            assertThrows(PurchasePolicyException.class, () -> productDetailsPolicy.isApproved(testPurchase, testProducts, testBillingAddress1));
        }

        @Test
        void editBadSN() {
            //check that the min is right
            assertEquals(min, productDetailsPolicy.getMin());
            //check that method fails
            assertFalse(productDetailsPolicy.edit(testPurchase, 1, 4,3456321));
            //check that the min is right
            assertEquals(min, productDetailsPolicy.getMin());
        }

        @Test
        void editSuc() {
            //check that the min is right
            assertEquals(min, productDetailsPolicy.getMin());
            //check that method works
            assertTrue(productDetailsPolicy.edit(testPurchase, 1, 4,productSN));
            //check that min & max changed
            assertTrue(productDetailsPolicy.getMin() == 1 && productDetailsPolicy.getMax() == 4);
        }

        @Test
        void editLessThanZero() {
            //check that the min is right
            assertEquals(min, productDetailsPolicy.getMin());
            //check that method works
            assertFalse(productDetailsPolicy.edit(testPurchase, -1, 4,productSN));
            //check that the min didn't change
            assertEquals(min, productDetailsPolicy.getMin());
        }

        @Test
        void editMinBiggerThanMax() {
            //check that the min is right
            assertEquals(min, productDetailsPolicy.getMin());
            //check that method works
            assertFalse(productDetailsPolicy.edit(testPurchase, 4, 1,productSN));
            //check that the min didn't change
            assertEquals(min, productDetailsPolicy.getMin());
        }
    }
}
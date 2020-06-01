package com.wsep202.TradingSystem.domain.trading_system_management.policy_purchase;

import com.wsep202.TradingSystem.domain.exception.PurchasePolicyException;
import com.wsep202.TradingSystem.domain.trading_system_management.Product;
import com.wsep202.TradingSystem.domain.trading_system_management.ProductCategory;
import com.wsep202.TradingSystem.domain.trading_system_management.discount.CompositeOperator;
import com.wsep202.TradingSystem.domain.trading_system_management.purchase.BillingAddress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ComposedPurchaseTest {

    @Nested
    public class ComposedPurchaseTestUnit {

        ComposedPurchase composedPurchase;
        List<Purchase> composedPurchasePolicies;
        ProductDetailsPolicy productDetailsPolicy1;
        UserDetailsPolicy userDetailsPolicy;
        ProductDetailsPolicy productDetailsPolicy2;
        Purchase purchase1;
        Purchase purchase2;
        Purchase purchase3;
        Purchase purchase4;
        Map<Product, Integer> testProducts;
        BillingAddress testBillingAddress1;
        Product product1;
        Product product2;

        @BeforeEach
        void setUp() {
            productDetailsPolicy1 = mock(ProductDetailsPolicy.class);
            productDetailsPolicy2 = mock(ProductDetailsPolicy.class);
            userDetailsPolicy = mock(UserDetailsPolicy.class);
            testProducts = new HashMap<>();
            product1 = mock(Product.class);
            product2 = mock(Product.class);
            testProducts.put(product1, 3);
            testProducts.put(product2, 2);
            testBillingAddress1 = mock(BillingAddress.class);
            when(testBillingAddress1.getCountry()).thenReturn("Israel");
            purchase1 = mock(Purchase.class);
            purchase2 = mock(Purchase.class);
            purchase3 = mock(Purchase.class);
            purchase4 = mock(Purchase.class);
            when(purchase1.getPurchaseId()).thenReturn(12);
            when(productDetailsPolicy1.isApproved(purchase1,testProducts,testBillingAddress1)).thenReturn(true);
            when(purchase2.getPurchaseId()).thenReturn(13);
            when(productDetailsPolicy2.isApproved(purchase1,testProducts,testBillingAddress1)).thenReturn(true);
            when(purchase3.getPurchaseId()).thenReturn(14);
            when(purchase4.isApproved(testProducts,testBillingAddress1)).thenReturn(false);
            when(purchase4.getPurchaseId()).thenReturn(15);
            composedPurchasePolicies = new LinkedList<>();
            composedPurchasePolicies.add(purchase2);
            composedPurchasePolicies.add(purchase3);
            composedPurchasePolicies.add(purchase4);
            when(purchase2.getPurchasePolicy()).thenReturn(productDetailsPolicy1);
            when(purchase3.getPurchasePolicy()).thenReturn(productDetailsPolicy2);
            when(purchase4.getPurchasePolicy()).thenReturn(userDetailsPolicy);
            composedPurchase = new ComposedPurchase(CompositeOperator.AND,composedPurchasePolicies);
        }

        @Test
        void isApprovedAND() {
            //check that method works
            assertFalse(composedPurchase.isApproved(purchase1,testProducts,testBillingAddress1));
        }

        @Test
        void isApprovedOR() {
            composedPurchase.setCompositeOperator(CompositeOperator.OR);
            //check that method works
            assertTrue(composedPurchase.isApproved(purchase1,testProducts,testBillingAddress1));
        }

        @Test
        void isApprovedXOR() {
            composedPurchase.setCompositeOperator(CompositeOperator.XOR);
            //check that method works
            assertThrows(PurchasePolicyException.class, () -> composedPurchase.isApproved(purchase1, testProducts, testBillingAddress1));
        }
    }

//////////////////////////////////////////////////////////////////////////////////////////////Integration/////////////////////////////////////////////////////////////////////////////

    @Nested
    public class ComposedPurchaseTestIntegration {
        ComposedPurchase composedPurchase;
        List<Purchase> composedPurchasePolicies;
        ProductDetailsPolicy productDetailsPolicy1;
        UserDetailsPolicy userDetailsPolicy;
        ProductDetailsPolicy productDetailsPolicy2;
        Purchase purchase1;
        Purchase purchase2;
        Purchase purchase3;
        Purchase purchase4;
        Map<Product, Integer> testProducts;
        BillingAddress testBillingAddress1;
        Product product1;
        Product product2;

        @BeforeEach
        void setUp() {
            product1 = new Product("product1", ProductCategory.HEALTH, 50, 23, 1);
            product2 = new Product("product2", ProductCategory.HEALTH, 50, 12, 1);
            Set<String> countriesPermitted = new HashSet<>();
            countriesPermitted.add("Israel");
            countriesPermitted.add("Italy");
            countriesPermitted.add("UK");
            productDetailsPolicy1 = new ProductDetailsPolicy(1, 4, product1.getProductSn());
            productDetailsPolicy2 = new ProductDetailsPolicy(1, 3, product2.getProductSn());
            userDetailsPolicy = new UserDetailsPolicy(countriesPermitted);
            testProducts = new HashMap<>();
            testProducts.put(product1, 3);
            testProducts.put(product2, 2);
            testBillingAddress1 = new BillingAddress("Will Smith", "5 st'", "London", "UK", "12345678");
            purchase2 = new Purchase(productDetailsPolicy1, PurchaseType.BUY_IMMEDIATELY);
            purchase3 = new Purchase(productDetailsPolicy2, PurchaseType.BUY_IMMEDIATELY);
            purchase4 = new Purchase(userDetailsPolicy, PurchaseType.BUY_IMMEDIATELY);
            composedPurchasePolicies = new LinkedList<>();
            composedPurchasePolicies.add(purchase2);
            composedPurchasePolicies.add(purchase3);
            composedPurchasePolicies.add(purchase4);
            composedPurchase = new ComposedPurchase(CompositeOperator.AND,composedPurchasePolicies);
            purchase1 = new Purchase(composedPurchase, PurchaseType.BUY_IMMEDIATELY);
        }

        @Test
        void isApprovedAND() {
            //check that method works
            assertTrue(composedPurchase.isApproved(purchase1,testProducts,testBillingAddress1));
        }

        @Test
        void isApprovedOR() {
            composedPurchase.setCompositeOperator(CompositeOperator.OR);
            //check that method works
            assertTrue(composedPurchase.isApproved(purchase1,testProducts,testBillingAddress1));
        }

        @Test
        void isApprovedXOR() {
            composedPurchase.setCompositeOperator(CompositeOperator.XOR);
            //check that method works
            assertTrue(composedPurchase.isApproved(purchase1, testProducts, testBillingAddress1));
        }
    }
}
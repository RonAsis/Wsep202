package com.wsep202.TradingSystem.domain.trading_system_management.policy_purchase;

import com.wsep202.TradingSystem.domain.trading_system_management.Product;
import com.wsep202.TradingSystem.domain.trading_system_management.purchase.BillingAddress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SystemDetailsPolicyTest {

    @Nested
    public class SystemDetailsPolicyTestUnit {
        SystemDetailsPolicy systemDetailsPolicy;
        Set<Day> days;
        Purchase testPurchase;
        Map<Product, Integer> testProducts;


        @BeforeEach
        void setUp() {
            days = new HashSet<>();
            days.add(Day.Sunday);
            days.add(Day.Monday);
            days.add(Day.Thursday);
            systemDetailsPolicy = SystemDetailsPolicy.builder()
                    .storeWorkDays(days)
                    .build();
            testProducts = new HashMap<>();
            testPurchase = mock(Purchase.class);
            when(testPurchase.getPurchaseId()).thenReturn((long) 12);
            Product product1 = mock(Product.class);
            Product product2 = mock(Product.class);
            testProducts.put(product1, 3);
            testProducts.put(product2, 2);
        }


        @Test
        void editNullObject() {
            //check that the number of days is not 0
            assertTrue(!days.isEmpty());
            //check that the method fails
            assertFalse(systemDetailsPolicy.edit(testPurchase, null));
            //check that the number of days is not 0
            assertTrue(!days.isEmpty());
        }

        @Test
        void editNullEmpty() {
            Set<Day> dayTest = new HashSet<>();
            //check that the number of days is not 0
            assertTrue(!days.isEmpty());
            //check that the method fails
            assertFalse(systemDetailsPolicy.edit(testPurchase, dayTest));
            //check that the number of days is not 0
            assertTrue(!days.isEmpty());
        }

        @Test
        void editSuc() {
            Set<Day> dayTest = new HashSet<>();
            dayTest.add(Day.Friday);
            //check that monday is in policy
            assertTrue(systemDetailsPolicy.getStoreWorkDays().contains(Day.Monday));
            //check that method succeeds
            assertTrue(systemDetailsPolicy.edit(testPurchase, dayTest));
            //check that monday is not in policy
            assertFalse(systemDetailsPolicy.getStoreWorkDays().contains(Day.Monday));
            //check that friday is in policy
            assertTrue(systemDetailsPolicy.getStoreWorkDays().contains(Day.Friday));
        }
    }
///////////////////////////////////////////////////////////////////////////////////////Integration////////////////////////////////////////////////////////////////////////////////

    @Nested
    public class SystemDetailsPolicyTestIntegration {

        SystemDetailsPolicy systemDetailsPolicy;
        Set<Day> days;
        Purchase testPurchase;
        Map<Product, Integer> testProducts;



        @BeforeEach
        void setUp() {
            days = new HashSet<>();
            days.add(Day.Sunday);
            days.add(Day.Monday);
            days.add(Day.Thursday);
            systemDetailsPolicy = SystemDetailsPolicy.builder()
                    .storeWorkDays(days)
                    .build();
            testProducts = new HashMap<>();
            testPurchase = new Purchase(systemDetailsPolicy, PurchaseType.SYSTEM_DETAILS,"test system policy");
            Product product1 = new Product();
            Product product2 = new Product();
            testProducts.put(product1, 3);
            testProducts.put(product2, 2);
        }

        @Test
        void editNullObject() {
            //check that the number of days is not 0
            assertTrue(!days.isEmpty());
            //check that the method fails
            assertFalse(systemDetailsPolicy.edit(testPurchase, null));
            //check that the number of days is not 0
            assertTrue(!days.isEmpty());
        }

        @Test
        void editNullEmpty() {
            Set<Day> dayTest = new HashSet<>();
            //check that the number of days is not 0
            assertTrue(!days.isEmpty());
            //check that the method fails
            assertFalse(systemDetailsPolicy.edit(testPurchase, dayTest));
            //check that the number of days is not 0
            assertTrue(!days.isEmpty());
        }

        @Test
        void editSuc() {
            Set<Day> dayTest = new HashSet<>();
            dayTest.add(Day.Friday);
            //check that monday is in policy
            assertTrue(systemDetailsPolicy.getStoreWorkDays().contains(Day.Monday));
            //check that method succeeds
            assertTrue(systemDetailsPolicy.edit(testPurchase, dayTest));
            //check that monday is not in policy
            assertFalse(systemDetailsPolicy.getStoreWorkDays().contains(Day.Monday));
            //check that friday is in policy
            assertTrue(systemDetailsPolicy.getStoreWorkDays().contains(Day.Friday));
        }
    }
}

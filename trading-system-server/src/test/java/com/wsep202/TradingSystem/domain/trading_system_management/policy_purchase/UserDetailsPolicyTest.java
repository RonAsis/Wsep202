//package com.wsep202.TradingSystem.domain.trading_system_management.policy_purchase;
//
//import com.wsep202.TradingSystem.domain.exception.PurchasePolicyException;
//import com.wsep202.TradingSystem.domain.trading_system_management.Product;
//import com.wsep202.TradingSystem.domain.trading_system_management.policy_purchase.Purchase;
//import com.wsep202.TradingSystem.domain.trading_system_management.policy_purchase.PurchaseType;
//import com.wsep202.TradingSystem.domain.trading_system_management.policy_purchase.UserDetailsPolicy;
//import com.wsep202.TradingSystem.domain.trading_system_management.purchase.BillingAddress;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//
//import java.util.*;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.when;
//
//class UserDetailsPolicyTest {
//    @Nested
//    public class UserDetailsPolicyTestUnit {
//        UserDetailsPolicy userDetailsPolicy;
//        Set<String> countriesPermitted;
//        Purchase testPurchase;
//        Map<Product, Integer> testProducts;
//        BillingAddress testBillingAddress1;
//        BillingAddress testBillingAddress2;
//
//
//        @BeforeEach
//        void setUp() {
//            countriesPermitted = new HashSet<>();
//            countriesPermitted.add("Israel");
//            countriesPermitted.add("Italy");
//            countriesPermitted.add("UK");
//            userDetailsPolicy = UserDetailsPolicy.builder()
//                    .countriesPermitted(countriesPermitted)
//                    .build();
//            testProducts = new HashMap<>();
//            testPurchase = mock(Purchase.class);
//            when(testPurchase.getPurchaseId()).thenReturn(12);
//            testBillingAddress1 = mock(BillingAddress.class);
//            when(testBillingAddress1.getCountry()).thenReturn("UK");
//            testBillingAddress2 = mock(BillingAddress.class);
//            when(testBillingAddress2.getCountry()).thenReturn("Iran");
//            Product product1 = mock(Product.class);
//            Product product2 = mock(Product.class);
//            testProducts.put(product1, 3);
//            testProducts.put(product2, 2);
//        }
//
//        @Test
//        void isApprovedSuccess() {
//            //check that UK is in the list
//            assertTrue(userDetailsPolicy.getCountriesPermitted().contains("UK"));
//            //check that the method succeeded
//            assertTrue(userDetailsPolicy.isApproved(testPurchase, testProducts, testBillingAddress1));
//            //check that UK is in the list
//            assertTrue(userDetailsPolicy.getCountriesPermitted().contains("UK"));
//        }
//
//        @Test
//        void isApprovedFail() {
//            //check that Iran is not in the list
//            assertFalse(userDetailsPolicy.getCountriesPermitted().contains("Iran"));
//            //check that the method failed
//            assertThrows(PurchasePolicyException.class, () -> userDetailsPolicy.isApproved(testPurchase, testProducts, testBillingAddress2));
//
//        }
//
//        @Test
//        void editSuc() {
//            Set<String> countries = new HashSet<>();
//            countries.add("Turkey");
//            //check that Israel is in the list
//            assertTrue(userDetailsPolicy.getCountriesPermitted().contains("Israel"));
//            //check that the method succeeded
//            assertTrue(userDetailsPolicy.edit(testPurchase, countries));
//            //check that Israel is not in the list
//            assertFalse(userDetailsPolicy.getCountriesPermitted().contains("Israel"));
//            //check that Turkey is in the list
//            assertTrue(userDetailsPolicy.getCountriesPermitted().contains("Turkey"));
//        }
//
//        @Test
//        void editNullObject() {
//            //check that Israel is in the list
//            assertTrue(userDetailsPolicy.getCountriesPermitted().contains("Israel"));
//            //check that the method fails
//            assertFalse(userDetailsPolicy.edit(testPurchase, null));
//            //check that Israel is in the list
//            assertTrue(userDetailsPolicy.getCountriesPermitted().contains("Israel"));
//        }
//
//        @Test
//        void editEmpty() {
//            Set<String> countries = new HashSet<>();
//            //check that Israel is in the list
//            assertTrue(userDetailsPolicy.getCountriesPermitted().contains("Israel"));
//            //check that the method fails
//            assertFalse(userDetailsPolicy.edit(testPurchase, countries));
//            //check that Israel is in the list
//            assertTrue(userDetailsPolicy.getCountriesPermitted().contains("Israel"));
//        }
//    }
//
/////////////////////////////////////////////////////////////////////////////////////////////Integration//////////////////////////////////////////////////////////////////////////////
//    @Nested
//    public class UserDetailsPolicyTestIntegration {
//        UserDetailsPolicy userDetailsPolicy;
//        Set<String> countriesPermitted;
//        Purchase testPurchase;
//        Map<Product, Integer> testProducts;
//        BillingAddress testBillingAddress1;
//        BillingAddress testBillingAddress2;
//
//
//        @BeforeEach
//        void setUp() {
//            countriesPermitted = new HashSet<>();
//            countriesPermitted.add("Israel");
//            countriesPermitted.add("Italy");
//            countriesPermitted.add("UK");
//            userDetailsPolicy = UserDetailsPolicy.builder()
//                    .countriesPermitted(countriesPermitted)
//                    .build();
//            testProducts = new HashMap<>();
//            testPurchase = new Purchase(userDetailsPolicy, PurchaseType.BUY_IMMEDIATELY);
//            testBillingAddress1 = new BillingAddress("Will Smith", "5 st'", "London", "UK", "12345678");
//            testBillingAddress2 = new BillingAddress("Will Smith", "5 st'", "Somewhere", "Iran", "12345678");
//            Product product1 = new Product();
//            Product product2 = new Product();
//            testProducts.put(product1, 3);
//            testProducts.put(product2, 2);
//        }
//
//        @Test
//        void isApprovedSuccess() {
//            //check that UK is in the list
//            assertTrue(userDetailsPolicy.getCountriesPermitted().contains("UK"));
//            //check that the method succeeded
//            assertTrue(userDetailsPolicy.isApproved(testPurchase, testProducts, testBillingAddress1));
//            //check that UK is in the list
//            assertTrue(userDetailsPolicy.getCountriesPermitted().contains("UK"));
//        }
//
//        @Test
//        void isApprovedFail() {
//            //check that Iran is not in the list
//            assertFalse(userDetailsPolicy.getCountriesPermitted().contains("Iran"));
//            //check that the method failed
//            assertThrows(PurchasePolicyException.class, () -> userDetailsPolicy.isApproved(testPurchase, testProducts, testBillingAddress2));
//
//        }
//
//        @Test
//        void editSuc() {
//            Set<String> countries = new HashSet<>();
//            countries.add("Turkey");
//            //check that Israel is in the list
//            assertTrue(userDetailsPolicy.getCountriesPermitted().contains("Israel"));
//            //check that the method succeeded
//            assertTrue(userDetailsPolicy.edit(testPurchase, countries));
//            //check that Israel is not in the list
//            assertFalse(userDetailsPolicy.getCountriesPermitted().contains("Israel"));
//            //check that Turkey is in the list
//            assertTrue(userDetailsPolicy.getCountriesPermitted().contains("Turkey"));
//        }
//
//        @Test
//        void editNullObject() {
//            //check that Israel is in the list
//            assertTrue(userDetailsPolicy.getCountriesPermitted().contains("Israel"));
//            //check that the method fails
//            assertFalse(userDetailsPolicy.edit(testPurchase, null));
//            //check that Israel is in the list
//            assertTrue(userDetailsPolicy.getCountriesPermitted().contains("Israel"));
//        }
//
//        @Test
//        void editEmpty() {
//            Set<String> countries = new HashSet<>();
//            //check that Israel is in the list
//            assertTrue(userDetailsPolicy.getCountriesPermitted().contains("Israel"));
//            //check that the method fails
//            assertFalse(userDetailsPolicy.edit(testPurchase, countries));
//            //check that Israel is in the list
//            assertTrue(userDetailsPolicy.getCountriesPermitted().contains("Israel"));
//        }
//    }
//}

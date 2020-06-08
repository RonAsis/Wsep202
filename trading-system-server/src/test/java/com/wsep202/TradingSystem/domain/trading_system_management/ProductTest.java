package com.wsep202.TradingSystem.domain.trading_system_management;

import org.junit.jupiter.api.*;

class ProductTest {

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product("Dollhouse", ProductCategory.TOYS_HOBBIES, 10, 100, 1);
    }

    @Nested
    public class ProductTestUnit {

        @BeforeEach
        void setUp() {
        }

        /**
         * checks the positive tests in the function "increasesProductAmount()"
         */
        @Test
        void increasesProductAmountPositive() {
            // increases the produce amount with positive legal number.
            int productOldAmount = product.getAmount();
            int addedAmount = 2;
            boolean ans = product.increasesProductAmount(addedAmount);
            Assertions.assertTrue(ans);
            Assertions.assertEquals(product.getAmount(), productOldAmount + addedAmount);
        }

        /**
         * checks the negative tests in the function "increasesProductAmount()"
         */
        @Test
        void increasesProductAmountNegative() {
            // tries to increase with zero amount.
            int productOldAmount = product.getAmount();
            int addedAmount = 0;
            boolean ans = product.increasesProductAmount(addedAmount);
            Assertions.assertFalse(ans);
            // the amount didn't change
            Assertions.assertEquals(product.getAmount(), productOldAmount);

            // tries to increase with negative amount.
            addedAmount = -2;
            ans = product.increasesProductAmount(addedAmount);
            Assertions.assertFalse(ans);
            // the amount didn't change
            Assertions.assertEquals(product.getAmount(), productOldAmount);
        }

        /**
         * checks the positive tests in the function "reducesProductAmount()"
         */
        @Test
        void reducesProductAmountPositive() {
            // reduces the produce amount with positive legal number.
            int productOldAmount = product.getAmount();
            int removalAmount = 2;
            boolean ans = product.reducesProductAmount(removalAmount);
            Assertions.assertTrue(ans);
            Assertions.assertEquals(product.getAmount(), productOldAmount - removalAmount);
        }

        /**
         * checks the negative tests in the function "reducesProductAmount()"
         */
        @Test
        void reducesProductAmountNegative() {
            // tries to reduce with zero amount.
            int productOldAmount = product.getAmount();
            int removalAmount = 0;
            boolean ans = product.reducesProductAmount(removalAmount);
            Assertions.assertFalse(ans);
            // the amount didn't change
            Assertions.assertEquals(product.getAmount(), productOldAmount);

            // tries to increase with negative amount.
            removalAmount = -2;
            ans = product.reducesProductAmount(removalAmount);
            Assertions.assertFalse(ans);
            // the amount didn't change
            Assertions.assertEquals(product.getAmount(), productOldAmount);
        }
    }

    @Nested
    public class ProductTestIntegration {

        @BeforeEach
        void setUp() {
        }

        @Test
        void increasesProductAmount() {
        }

        @Test
        void reducesProductAmount() {
        }
    }
}
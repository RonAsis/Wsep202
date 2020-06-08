package com.wsep202.TradingSystem.domain.trading_system_management;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ProductCategoryTest {

    ProductCategory productCategory;

    @BeforeEach
    void setUp() {
        productCategory = ProductCategory.TOYS_HOBBIES;
    }

    @Nested
    public class ProductCategoryUnitTest {

        @BeforeEach
        void setUp() {

        }

        /**
         * checks the positive tests in the function "getProductCategory()"
         */
        @Test
        void getProductCategoryPositive() {
            Assertions.assertEquals(productCategory.category, ProductCategory.TOYS_HOBBIES.category);
            Assertions.assertEquals(productCategory, ProductCategory.TOYS_HOBBIES);
        }

        /**
         * checks the negative tests in the function "getProductCategory()"
         */
        @Test
        void getProductCategoryNegative() {
            Assertions.assertNotEquals(productCategory.category, ProductCategory.BOOKS_MOVIES_MUSIC.category);
            Assertions.assertNotEquals(productCategory, ProductCategory.BOOKS_MOVIES_MUSIC);
        }
    }
}
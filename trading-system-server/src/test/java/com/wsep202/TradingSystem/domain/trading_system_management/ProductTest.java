package com.wsep202.TradingSystem.domain.trading_system_management;

import org.junit.jupiter.api.*;
import org.springframework.security.core.parameters.P;

import java.util.LinkedList;
import java.util.List;

class ProductTest {
    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product("Barbie Dollhouse", ProductCategory.TOYS_HOBBIES, 10, 100, 1);
    }

    /**
     * increasing the amount by a valid amount
     */
    @Test
    void increasesProductAmountValidAmount() {
        // increases the produce amount with positive legal number.
        int productOldAmount = product.getAmount();
        int addedAmount = 2;
        boolean ans = product.increasesProductAmount(addedAmount);
        Assertions.assertTrue(ans);
        Assertions.assertEquals(productOldAmount + addedAmount, product.getAmount());
    }

    /**
     * increasing the amount by zero - invalid amount
     */
    @Test
    void increasesProductAmountZeroAmount() {
        // tries to increase with zero amount.
        int productOldAmount = product.getAmount();
        int addedAmount = 0;
        boolean ans = product.increasesProductAmount(addedAmount);
        Assertions.assertFalse(ans);
        // the amount didn't change
        Assertions.assertEquals(productOldAmount, product.getAmount());
    }

    /**
     * increasing the amount by a negative amount - invalid amount
     */
    @Test
    void increasesProductAmountNegativeAmount() {
        int productOldAmount = product.getAmount();
        // tries to increase with negative amount.
        int addedAmount = -2;
        boolean ans = product.increasesProductAmount(addedAmount);
        Assertions.assertFalse(ans);
        // the amount didn't change
        Assertions.assertEquals(productOldAmount, product.getAmount());
    }

    /**
     * reducing the amount by a valid amount
     */
    @Test
    void reducesProductAmountValidAmount() {
        // reduces the produce amount with positive legal number.
        int productOldAmount = product.getAmount();
        int removalAmount = 2;
        boolean ans = product.reducesProductAmount(removalAmount);
        Assertions.assertTrue(ans);
        Assertions.assertEquals(productOldAmount - removalAmount, product.getAmount());
    }

    /**
     * reducing the amount by a zero amount - invalid amount
     */
    @Test
    void reducesProductAmountZeroAmount() {
        // tries to reduce with zero amount.
        int productOldAmount = product.getAmount();
        int removalAmount = 0;
        boolean ans = product.reducesProductAmount(removalAmount);
        Assertions.assertFalse(ans);
        // the amount didn't change
        Assertions.assertEquals(productOldAmount, product.getAmount());
    }

    /**
     * reducing the amount by a zero amount - invalid amount
     */
    @Test
    void reducesProductAmountNegativeAmount() {
        // tries to increase with negative amount.
        int productOldAmount = product.getAmount();
        int removalAmount = -2;
        boolean ans = product.reducesProductAmount(removalAmount);
        Assertions.assertFalse(ans);
        // the amount didn't change
        Assertions.assertEquals(productOldAmount, product.getAmount());
    }

    /**
     * checking if the cloned product's data is the same as the returned product's data
     */
    @Test
    void cloneProduct(){
        Product returnedProduct = product.cloneProduct();
        // making sure that the cloned product is new
        Assertions.assertNotEquals(product, returnedProduct);
        // making sure that the cloned product's data is the same as the returnedProduct's data
        Assertions.assertEquals(product.getName(), returnedProduct.getName());
        Assertions.assertEquals(product.getCategory(), returnedProduct.getCategory());
        Assertions.assertEquals(product.getAmount(), returnedProduct.getAmount());
        Assertions.assertEquals(product.getCost(), returnedProduct.getCost());
        Assertions.assertEquals(product.getStoreId(), returnedProduct.getStoreId());
        Assertions.assertEquals(product.getProductSn(), returnedProduct.getProductSn());
        Assertions.assertEquals(product.getOriginalCost(), returnedProduct.getOriginalCost());
        Assertions.assertEquals(product.getRank(), returnedProduct.getRank());
    }
}
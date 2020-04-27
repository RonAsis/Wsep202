package com.wsep202.TradingSystem.domain.trading_system_management;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ShoppingBagTest {

    /**
     * unit tests for ShoppingBag class
     */
    @Nested
    public class ShoppingBagUnit {
        Store testStore1;
        ShoppingBag testShoppingBag;
        Product testProduct;
        Product testProduct2;
        Product testProduct3;
        Product testProduct4;
        Product testProduct5;

        @BeforeEach
        void setUp() {
            testStore1 = mock(Store.class);
            testProduct = mock(Product.class);
            testProduct2 = mock(Product.class);
            testProduct3 = mock(Product.class);
            testProduct4 = mock(Product.class);
            testProduct5 = mock(Product.class);
            testShoppingBag = new ShoppingBag(testStore1);
            when(testStore1.getStoreId()).thenReturn(1);
            when(testProduct2.getCost()).thenReturn(1999.99);
            when(testProduct2.getStoreId()).thenReturn(1);
            when(testProduct3.getCost()).thenReturn(199.85);
            when(testProduct3.getStoreId()).thenReturn(1);
            when(testProduct4.getCost()).thenReturn(100.50);
            when(testProduct4.getStoreId()).thenReturn(2);
            when(testProduct.getAmount()).thenReturn(50);
            when(testProduct2.getAmount()).thenReturn(50);
            when(testProduct3.getAmount()).thenReturn(50);
            when(testProduct4.getAmount()).thenReturn(50);
            when(testProduct5.getAmount()).thenReturn(5);
            when(testProduct3.getStoreId()).thenReturn(1);
        }

        /**
         * This test check if the addProduct method succeeds when the parameters
         * are correct.
         */
        @Test
        void addProductToBagSuccess() {
            //check that the product was added successfully, needs to return true
            assertTrue(testShoppingBag.addProductToBag(testProduct2, 3));
            //checks that the number of the type product is 1
            assertEquals(1, testShoppingBag.getNumOfProductsInBag());
            //check that the product was really added to the shopping bag
            assertTrue(testShoppingBag.getProductListFromStore().containsKey(testProduct2));
            //check that the total cost of the bag is correct
            assertEquals(3 * testProduct2.getCost(), testShoppingBag.getTotalCostOfBag());
            testShoppingBag.addProductToBag(testProduct3, 1);
            //check that after adding 2 types of product that the bag really contains 2 products
            assertEquals(2, testShoppingBag.getNumOfProductsInBag());
            testShoppingBag.addProductToBag(testProduct2, 1);
            //check that there are still 2 products, after adding an exciting product
            assertEquals(2, testShoppingBag.getNumOfProductsInBag());
            //change the amount of an exciting product to 0
            assertTrue(testShoppingBag.addProductToBag(testProduct3, -1));
            //check that there is only 1 type of product in the bag
            assertEquals(1, testShoppingBag.getNumOfProductsInBag());
        }

        /**
         * This test check if the addProduct method fails when the parameters
         * are wrong.
         */
        @Test
        void addProductToBagFail() {
            //checks that testProduct4 can't be added to the shoppingBag, because it's from a dif store
            assertFalse(testShoppingBag.addProductToBag(testProduct4, 3));
            //check that the method does not add a null product
            assertFalse(testShoppingBag.addProductToBag(null, 4));
            //after to fail addition to bag, check that the cost of the bag is 0
            assertEquals(0, testShoppingBag.getTotalCostOfBag());
            //after to fail addition to bag, check that the num of products in the bag is 0
            assertEquals(0, testShoppingBag.getNumOfProductsInBag());
            testShoppingBag.addProductToBag(testProduct2, 1);
            //the amount of a product can't be less than 0
            assertFalse(testShoppingBag.addProductToBag(testProduct2, -2));
            //check that product amount needs to be greater than 0
            assertFalse(testShoppingBag.addProductToBag(testProduct3, 0));
            //check that product amount needs to be greater than 0
            assertFalse(testShoppingBag.addProductToBag(testProduct3, -1));
            //not enough products in store
            assertFalse(testShoppingBag.addProductToBag(testProduct5,10));
        }

        /**
         * This test check if the removeProduct method succeeds when the parameters
         * are correct.
         */
        @Test
        void removeProductFromBagSuccess() {
            setUpForRemove();
            //check that the product is removed from the bag
            assertTrue(testShoppingBag.removeProductFromBag(testProduct2));
            //check that the number of products in the bag is updated
            assertEquals(1, testShoppingBag.getNumOfProductsInBag());
            //check that the total cost of the bag is updated
            assertEquals((testProduct3.getCost() * 2), testShoppingBag.getTotalCostOfBag());
            //check that the product is removed from the bag
            assertTrue(testShoppingBag.removeProductFromBag(testProduct3));
            //check that the number of products in the bag is updated
            assertEquals(0, testShoppingBag.getNumOfProductsInBag());
            //check that the total cost of the bag is updated
            assertEquals(0, testShoppingBag.getTotalCostOfBag());
        }

        /**
         * This test check if the removeProduct method fails when the parameters
         * are wrong.
         */
        @Test
        void removeProductFromBagFail() {
            setUpForRemove();
            //try to remove a null product
            assertFalse(testShoppingBag.removeProductFromBag(null));
            //try to remove a product that has a different store id
            assertFalse(testShoppingBag.removeProductFromBag(testProduct4));
            //try to remove a product that does not exists
            assertFalse(testShoppingBag.removeProductFromBag(testProduct));
            //check that the number of products didn't change
            assertEquals(2, testShoppingBag.getNumOfProductsInBag());
            //check the total cost of the bag didn't change
            assertEquals((testProduct2.getCost() * 2) + (testProduct3.getCost() * 2), testShoppingBag.getTotalCostOfBag());
        }

        /**
         * This test check if the getProductAmount method succeeds when the parameters
         * are correct.
         */
        @Test
        void getProductAmountSuccess(){
            setUpForProductAmount();
            //check that the amount is correct
            assertEquals(2, testShoppingBag.getProductAmount(testProduct3));
            //check that the amount is correct
            assertEquals(5, testShoppingBag.getProductAmount(testProduct4));
        }

        /**
         * This test check if the getProductAmount method fails when the parameters
         * are wrong.
         */
        @Test
        void getProductAmountFail(){
            setUpForProductAmount();
            //can't have a null product
            assertEquals(-1, testShoppingBag.getProductAmount(null));
            //product is not in the bag
            assertEquals(-1, testShoppingBag.getProductAmount(testProduct));
        }

        /**
         * set up products in bag for getProductAmount method
         */
        private void setUpForProductAmount(){
            Map<Product,Integer> productList = new HashMap<>();
            productList.put(testProduct3, 2);
            productList.put(testProduct4, 5);
            testShoppingBag.setProductListFromStore(productList);
        }

        /**
         * set products in the shoppingBag for remove method
         */
        private void setUpForRemove() {
            Map<Product, Integer> testProductList = new HashMap<>();
            testProductList.put(testProduct2, 2);
            testProductList.put(testProduct3, 2);
            testShoppingBag.setProductListFromStore(testProductList);
            testShoppingBag.setNumOfProductsInBag(2);
            testShoppingBag.setTotalCostOfBag((testProduct2.getCost() * 2) + (testProduct3.getCost() * 2));
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////Integration//////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Integration tests for ShoppingBag class
     */
    @Nested
    public class ShoppingBagIntegration {
        Store testStore1;
        ShoppingBag testShoppingBag;
        Product testProduct;
        Product testProduct2;
        Product testProduct3;
        Product testProduct4;

        @BeforeEach
        void setUp() {
            testStore1 = Store.builder()
                    .storeName("MovieStore")
                    .purchasePolicy(new PurchasePolicy())
                    .discountPolicy(new DiscountPolicy())
                    .build();
            testProduct = new Product("Hunger Games", ProductCategory.BOOKS_MOVIES_MUSIC, 45, 12.9, testStore1.getStoreId());
            testProduct2 = new Product("Harry Potter", ProductCategory.BOOKS_MOVIES_MUSIC, 45, 12.9, testStore1.getStoreId());
            testProduct3 = new Product("Games of Thrones", ProductCategory.BOOKS_MOVIES_MUSIC, 45, 14.9, testStore1.getStoreId());
            testProduct4 = new Product("The Hobbit", ProductCategory.BOOKS_MOVIES_MUSIC, 45, 10.9, testStore1.getStoreId());
            testShoppingBag = new ShoppingBag(testStore1);
        }

        /**
         * This test check if the add & remove methods succeeds when the parameters
         * are correct.
         */
        @Test
        void addAndRemoveSuccess(){
            testShoppingBag.addProductToBag(testProduct2, 2);
            //check that the amount of products is 1 after add
            assertEquals(1,testShoppingBag.getNumOfProductsInBag());
            //check that the added product is deleted from bag
            assertTrue(testShoppingBag.removeProductFromBag(testProduct2));
            //check that number of products is 0 after remove
            assertEquals(0,testShoppingBag.getNumOfProductsInBag());
        }

        /**
         * This test check if the add & remove methods fails when the parameters
         * are wrong.
         */
        @Test
        void addAndRemoveFail(){
            testShoppingBag.addProductToBag(testProduct2, -1);
            //can't remove a product that was never added
            assertFalse(testShoppingBag.removeProductFromBag(testProduct2));
            //check that there are 0 products in bag
            assertEquals(0, testShoppingBag.getNumOfProductsInBag());
            testShoppingBag.addProductToBag(testProduct2,2);
            //can't remove a product that was never added
            assertFalse(testShoppingBag.removeProductFromBag(testProduct3));
            //try to add 50 products when the amount in store is 45
            assertFalse(testShoppingBag.addProductToBag(testProduct4, 50));
        }

        /**
         * This test check if the getProductAmount method succeeds when the parameters
         * are correct.
         */
        @Test
        void getProductAmountSuccess(){
            testShoppingBag.addProductToBag(testProduct, 4);
            //check that the amount is correct
            assertEquals(4, testShoppingBag.getProductAmount(testProduct));
        }

        /**
         * This test check if the getProductAmount method fails when the parameters
         * are wrong.
         */
        @Test
        void getProductAmountFail(){
            testShoppingBag.addProductToBag(testProduct, 4);
            //can't have a null product
            assertEquals(-1, testShoppingBag.getProductAmount(null));
            //product is not in the bag
            assertEquals(-1, testShoppingBag.getProductAmount(testProduct3));
        }

    }
}

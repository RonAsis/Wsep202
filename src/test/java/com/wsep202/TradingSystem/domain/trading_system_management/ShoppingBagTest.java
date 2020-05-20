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
            when(testProduct2.getProductSn()).thenReturn(2222);
            when(testProduct3.getCost()).thenReturn(199.85);
            when(testProduct3.getStoreId()).thenReturn(1);
            when(testProduct3.getProductSn()).thenReturn(3333);
            when(testProduct4.getCost()).thenReturn(100.50);
            when(testProduct4.getStoreId()).thenReturn(2);
            when(testProduct4.getProductSn()).thenReturn(4444);
            when(testProduct.getAmount()).thenReturn(50);
            when(testProduct.getProductSn()).thenReturn(1111);
            when(testProduct2.getAmount()).thenReturn(50);
            when(testProduct3.getAmount()).thenReturn(50);
            when(testProduct4.getAmount()).thenReturn(50);
            when(testProduct5.getAmount()).thenReturn(5);
            when(testProduct5.getProductSn()).thenReturn(5555);
            when(testProduct5.getStoreId()).thenReturn(1);
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
            assertEquals(1, testShoppingBag.getNumOfProducts());
            //check that the product was really added to the shopping bag
            assertEquals(testProduct2.getProductSn(), testShoppingBag.containProduct(testProduct2.getProductSn()).getProductSn());
            //check that the total cost of the bag is correct
            assertEquals(3 * testProduct2.getCost(), testShoppingBag.getTotalCostOfBag());
            testShoppingBag.addProductToBag(testProduct3, 1);
            //check that after adding 2 types of product that the bag really contains 2 products
            assertEquals(2, testShoppingBag.getNumOfProducts());
            //change amount of an existing product
            testShoppingBag.addProductToBag(testProduct2, 1);
            //check that there are still 2 products, after adding an exciting product
            assertEquals(2, testShoppingBag.getNumOfProducts());
        }

        /**
         * This test check if the addProduct method fails
         * when the the product from a different store.
         */
        @Test
        void addProductFromWrongStore() {
            //checks that testProduct4 can't be added to the shoppingBag, because it's from a dif store
            assertFalse(testShoppingBag.addProductToBag(testProduct4, 3));
            //after to fail addition to bag, check that the num of products in the bag is 0
            assertEquals(0, testShoppingBag.getNumOfProducts());
        }

        /**
         * This test check if the addProduct method fails
         * when trying to add null product.
         */
        @Test
        void addProductNull(){
            //check that the method does not add a null product
            assertFalse(testShoppingBag.addProductToBag(null, 4));
            //after to fail addition to bag, check that the cost of the bag is 0
            assertEquals(0, testShoppingBag.getTotalCostOfBag());
            //after to fail addition to bag, check that the num of products in the bag is 0
            assertEquals(0, testShoppingBag.getNumOfProducts());
        }

        /**
         * This test check if the addProduct method fails
         * when trying to add amount that equals to zero or less.
         */
        @Test
        void addProductBadAmount(){
            //the amount of a product can't be less than 0
            assertFalse(testShoppingBag.addProductToBag(testProduct2, -2));
            //check that product amount needs to be greater than 0
            assertFalse(testShoppingBag.addProductToBag(testProduct3, 0));
            //after to fail addition to bag, check that the cost of the bag is 0
            assertEquals(0, testShoppingBag.getTotalCostOfBag());
            //after to fail addition to bag, check that the num of products in the bag is 0
            assertEquals(0, testShoppingBag.getNumOfProducts());
        }

        /**
         * This test check if the addProduct method fails
         * when trying to add a product when there is not enough in store.
         */
        @Test
        void addProductNotEnoughInStore(){
            //not enough products in store
            assertFalse(testShoppingBag.addProductToBag(testProduct5,10));
            //after to fail addition to bag, check that the cost of the bag is 0
            assertEquals(0, testShoppingBag.getTotalCostOfBag());
            //after to fail addition to bag, check that the num of products in the bag is 0
            assertEquals(0, testShoppingBag.getNumOfProducts());
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
            assertEquals(1, testShoppingBag.getNumOfProducts());
            //check that the total cost of the bag is updated
            assertEquals((testProduct3.getCost() * 2), testShoppingBag.getTotalCostOfBag());
            //check that the product is removed from the bag
            assertTrue(testShoppingBag.removeProductFromBag(testProduct3));
            //check that the number of products in the bag is updated
            assertEquals(0, testShoppingBag.getNumOfProducts());
            //check that the total cost of the bag is updated
            assertEquals(0, testShoppingBag.getTotalCostOfBag());
        }

        /**
         * This test check if the removeProduct method fails
         * when the product is null
         */
        @Test
        void removeProductNullProduct() {
            setUpForRemove();
            //try to remove a null product
            assertFalse(testShoppingBag.removeProductFromBag(null));
            //check that there are still 2 products in bag
            assertEquals(2, testShoppingBag.getNumOfProducts());
            //check the total cost of the bag didn't change
            assertEquals((testProduct2.getCost() * 2) + (testProduct3.getCost() * 2), testShoppingBag.getTotalCostOfBag());
        }

        /**
         * This test check if the removeProduct method fails
         * when the product is from a different store
         */
        @Test
        void removeProductDiffStore() {
            setUpForRemove();
            //try to remove a product that has a different store id
            assertFalse(testShoppingBag.removeProductFromBag(testProduct4));
            //check that there are still 2 products in bag
            assertEquals(2, testShoppingBag.getNumOfProducts());
            //check the total cost of the bag didn't change
            assertEquals((testProduct2.getCost() * 2) + (testProduct3.getCost() * 2), testShoppingBag.getTotalCostOfBag());
        }

        /**
         * This test check if the removeProduct method fails
         * when the product is not in the bag
         */
        @Test
        void removeProductNotInBag() {
            setUpForRemove();
            //try to remove a product that does not exists in bag
            assertFalse(testShoppingBag.removeProductFromBag(testProduct));
            //check that there are still 2 products in bag
            assertEquals(2, testShoppingBag.getNumOfProducts());
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
            assertEquals(2, testShoppingBag.getProductAmount(testProduct3.getProductSn()));
            //check that the amount is correct
            assertEquals(5, testShoppingBag.getProductAmount(testProduct4.getProductSn()));
        }

        /**
         * This test check if the getProductAmount method fails
         * when the input is a product that does not exists in bag.
         */
        @Test
        void getProductAmountProductNotInBag(){
            setUpForProductAmount();
            //product is not in the bag
            assertEquals(-1, testShoppingBag.getProductAmount(testProduct.getProductSn()));
        }


        /**
         * This test check if the changeAmountOfProductInBag method succeeds
         * when the parameters are correct
         */
        @Test
        void changeAmountOfProductInBagSuccess(){
            setUpForRemove();
            //check that before change the bag has 2 items of product testProduct3
            assertEquals(2,testShoppingBag.getProductListFromStore().get(testProduct3));
            //check if the change worked
            assertTrue(testShoppingBag.changeAmountOfProductInBag(testProduct3.getProductSn(),4));
            //check that after change the bag has 4 items of product testProduct3
            assertEquals(4,testShoppingBag.getProductListFromStore().get(testProduct3));
        }

        /**
         * This test check if the getProductAmount method fails
         * when the serial number does not mach any product
         */
        @Test
        void changeAmountOfProductInBagWrongSerialNum(){
            setUpForRemove();
            //try to change a product amount in bag with wrong serial number
            assertFalse(testShoppingBag.changeAmountOfProductInBag(9,3));
            //check that number of products didn't change
            assertEquals(2, testShoppingBag.getProductListFromStore().get(testProduct2));
            //check that number of products didn't change
            assertEquals(2, testShoppingBag.getProductListFromStore().get(testProduct3));
        }

        /**
         * This test check if the getProductAmount method fails
         * when the new amount is zero or less
         */
        @Test
        void changeAmountOfProductInBagBadAmount(){
            setUpForRemove();
            //try to change a product amount in bag to 0
            assertFalse(testShoppingBag.changeAmountOfProductInBag(testProduct2.getProductSn(),0));
            //check that number of products didn't change
            assertEquals(2, testShoppingBag.getProductListFromStore().get(testProduct2));
            //try to change a product amount in bag to -1
            assertFalse(testShoppingBag.changeAmountOfProductInBag(testProduct3.getProductSn(),-1));
            //check that number of products didn't change
            assertEquals(2, testShoppingBag.getProductListFromStore().get(testProduct3));
        }

        /**
         * This test check if the getProductAmount method fails
         * when the new amount is zero or less
         */
        @Test
        void containProductSuc(){
            setUpForRemove();
            //check that the product is in the bag
            assertEquals(testProduct2,testShoppingBag.containProduct(testProduct2.getProductSn()));
            //check that the number of products didn't change
            assertEquals(2, testShoppingBag.getNumOfProducts());
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
        Product testProduct5;

        @BeforeEach
        void setUp() {
            testStore1 = Store.builder()
                    .storeName("MovieStore")
                    .build();
            testProduct = new Product("Hunger Games", ProductCategory.BOOKS_MOVIES_MUSIC, 45, 12.9, testStore1.getStoreId());
            testProduct2 = new Product("Harry Potter", ProductCategory.BOOKS_MOVIES_MUSIC, 45, 12.9, testStore1.getStoreId());
            testProduct3 = new Product("Games of Thrones", ProductCategory.BOOKS_MOVIES_MUSIC, 45, 14.9, testStore1.getStoreId());
            testProduct4 = new Product("The Hobbit", ProductCategory.BOOKS_MOVIES_MUSIC, 45, 10.9, testStore1.getStoreId());
            testProduct5 = new Product("Lord of The Ring", ProductCategory.BOOKS_MOVIES_MUSIC, 45,19.9,testStore1.getStoreId()+1);
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
            assertEquals(1,testShoppingBag.getNumOfProducts());
            //check that the added product is deleted from bag
            assertTrue(testShoppingBag.removeProductFromBag(testProduct2));
            //check that number of products is 0 after remove
            assertEquals(0,testShoppingBag.getNumOfProducts());
        }

        /**
         * This test check if the addProduct methods fails
         * when the product is from different store
         */
        @Test
        void addProductFromDiffStore(){
            assertFalse(testShoppingBag.addProductToBag(testProduct5, 2));
            //check that the amount of products is 0 after add
            assertEquals(0,testShoppingBag.getNumOfProducts());
            //check that the cost of the bag is 0
            assertEquals(0,testShoppingBag.getTotalCostOfBag());
        }

        /**
         * This test check if the addProduct methods fails
         * when there is not enough products in the store
         */
        @Test
        void addProductNotEnoughInStore(){
            assertFalse(testShoppingBag.addProductToBag(testProduct5, 200));
            //check that the amount of products is 0 after add
            assertEquals(0,testShoppingBag.getNumOfProducts());
            //check that the cost of the bag is 0
            assertEquals(0,testShoppingBag.getTotalCostOfBag());
        }

        /**
         * This test check if the addProduct methods succeeds
         * when you try to add the same product again.
         * The method will update the amount of the product in bag
         */
        @Test
        void addProductSameProduct(){
            assertTrue(testShoppingBag.addProductToBag(testProduct2, 2));
            //check that the amount of products is 1 after add
            assertEquals(1,testShoppingBag.getNumOfProducts());
            //check that the cost of the bag is testProduct2.getCost()*2
            assertEquals(testProduct2.getCost()*2,testShoppingBag.getTotalCostOfBag());
            //update the amount of an existing product in bag
            assertTrue(testShoppingBag.addProductToBag(testProduct2, 3));
            //check that the amount of product is still 1
            assertEquals(1,testShoppingBag.getNumOfProducts());
            //check that the cost of the bag is testProduct2.getCost()*3
            assertEquals(testProduct2.getCost()*3,testShoppingBag.getTotalCostOfBag());
        }

        /**
         * This test check if the addProduct methods fails
         * when the amount of the product is less than 0.
         */
        @Test
        void addProductBadAmount(){
            //no products in bag
            assertEquals(0,testShoppingBag.getNumOfProducts());
            //can't add a product with amount less than 0
            assertFalse(testShoppingBag.addProductToBag(testProduct2,-1));
            //no products in bag after bad addition
            assertEquals(0,testShoppingBag.getNumOfProducts());
        }

        /**
         * This test check if the removeProduct methods fails
         * when the product is not in store
         */
        @Test
        void removeNotExistingObject(){
            testShoppingBag.addProductToBag(testProduct2,2);
            //check before remove there is only 1 product in bag
            assertEquals(1, testShoppingBag.getNumOfProducts());
            //try to remove a product that does not exists in bag
            assertFalse(testShoppingBag.removeProductFromBag(testProduct3));
            //check after remove there is still 1 product in bag
            assertEquals(1, testShoppingBag.getNumOfProducts());
        }

        /**
         * This test check if the getProductAmount method succeeds when the parameters
         * are correct.
         */
        @Test
        void getProductAmountSuccess(){
            testShoppingBag.addProductToBag(testProduct, 4);
            //check that the amount is correct
            assertEquals(4, testShoppingBag.getProductAmount(testProduct.getProductSn()));
        }

        /**
         * This test check if the getProductAmount method fails
         * when the product does not exists
         */
        @Test
        void getProductAmountProductDoesNotExists(){
            testShoppingBag.addProductToBag(testProduct, 4);
            //product is not in the bag
            assertEquals(-1, testShoppingBag.getProductAmount(testProduct3.getProductSn()));
        }


        /**
         * This test check if the changeAmountOfProductInBag method succeeds
         * when the parameters are correct.
         */
        @Test
        void changeAmountOfProductInBagSuc(){
            testShoppingBag.addProductToBag(testProduct, 4);
            //check that the the method works
            assertTrue(testShoppingBag.changeAmountOfProductInBag(testProduct.getProductSn(),5));
            //check that the amount changed
            assertEquals(5,testShoppingBag.getProductAmount(testProduct.getProductSn()));
        }

        /**
         * This test check if the getProductAmount method fails
         * when the product does not exists
         */
        @Test
        void changeAmountOfProductInBagProductDoesNotExists(){
            testShoppingBag.addProductToBag(testProduct, 4);
            //check that the the method fails
            assertFalse(testShoppingBag.changeAmountOfProductInBag(testProduct2.getProductSn(),5));
            //check that the amount of the testProduct didn't change
            assertEquals(4,testShoppingBag.getProductAmount(testProduct.getProductSn()));
        }

        /**
         * This test check if the changeAmountOfProductInBag method succeeds
         * when the parameters are correct.
         */
        @Test
        void containProductSuc(){
            testShoppingBag.addProductToBag(testProduct2,2);
            //check that it's the same product
            assertEquals(testProduct2.getProductSn(),
                    testShoppingBag.containProduct(testProduct2.getProductSn()).getProductSn());
        }

        /**
         * This test check if the containProduct method fails
         * when the product does not exists
         */
        @Test
        void containProductNotInBag(){
            testShoppingBag.addProductToBag(testProduct2,2);
            //check that the product is not in the bag
            assertNull(testShoppingBag.containProduct(testProduct3.getProductSn()));
        }
    }
}

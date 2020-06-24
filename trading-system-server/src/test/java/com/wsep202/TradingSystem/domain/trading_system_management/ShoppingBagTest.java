package com.wsep202.TradingSystem.domain.trading_system_management;

import org.junit.jupiter.api.Assertions;
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
    public class ShoppingBagUnitTest {
        Store store;
        ShoppingBag shoppingBag;
        Product product1;
        Product product2;
        Product product3;
        Product product4;
        Product product5;

        @BeforeEach
        void setUp() {
            store = mock(Store.class);
            product1 = mock(Product.class);
            product2 = mock(Product.class);
            product3 = mock(Product.class);
            product4 = mock(Product.class);
            product5 = mock(Product.class);
            shoppingBag = new ShoppingBag(store);

            when(store.getStoreId()).thenReturn(1);

            when(product1.getStoreId()).thenReturn(1);
            when(product1.getProductSn()).thenReturn(1111);
            when(product1.getAmount()).thenReturn(50);

            when(product2.getStoreId()).thenReturn(1);
            when(product2.getProductSn()).thenReturn(2222);
            when(product2.getAmount()).thenReturn(50);
            when(product2.getCost()).thenReturn(1999.8);
            when(product2.getOriginalCost()).thenReturn(1999.8);

            when(product3.getStoreId()).thenReturn(1);
            when(product3.getProductSn()).thenReturn(3333);
            when(product3.getAmount()).thenReturn(50);
            when(product3.getCost()).thenReturn(199.5);

            when(product4.getStoreId()).thenReturn(2);
            when(product4.getProductSn()).thenReturn(4444);
            when(product4.getAmount()).thenReturn(50);
            when(product4.getCost()).thenReturn(100.5);

            when(product5.getStoreId()).thenReturn(1);
            when(product5.getProductSn()).thenReturn(5555);
            when(product5.getAmount()).thenReturn(5);
        }

        /**
         * This test check if the addProduct method succeeds when the parameters
         * are correct.
         */
        @Test
        void addProductToBagSuccess() {
            //check that the product was added successfully, needs to return true
            int amountToAddProduct2 = 3;
            assertTrue(shoppingBag.addProductToBag(product2, amountToAddProduct2));

            //checks that the number of the type product is 1
            assertEquals(1, shoppingBag.getNumOfProducts());

            //check that the product was really added to the shopping bag
            assertEquals(product2.getProductSn(), shoppingBag.containProduct(product2.getProductSn()).getProductSn());

            //check that the total cost of the bag is correct
            assertEquals(amountToAddProduct2 * product2.getCost(), shoppingBag.getTotalCostOfBag());

            // adding another product to the bag
            int amountToAddProduct3 = 1;
            shoppingBag.addProductToBag(product3, amountToAddProduct3);

            //check that after adding 2 types of product that the bag really contains 2 products
            assertEquals(2, shoppingBag.getNumOfProducts());

            //check that the product was really added to the shopping bag
            assertEquals(product3.getProductSn(), shoppingBag.containProduct(product3.getProductSn()).getProductSn());

            //check that the total cost of the bag is correct
            double expectedTotalCost = amountToAddProduct2 * product2.getCost() + amountToAddProduct3 * product3.getCost();
            assertEquals(expectedTotalCost, shoppingBag.getTotalCostOfBag());
        }

        /**
         * This test check if the addProduct method succeeds when the parameters
         * are correct and if the method changes the amount in the bag correctly
         */
        @Test
        void addProductToBagAndChangeAmountInBag() {
            //check that the product was added successfully, needs to return true
            int amountToAddProduct2 = 3;
            assertTrue(shoppingBag.addProductToBag(product2, amountToAddProduct2));

            //checks that the number of the type product is 1
            assertEquals(1, shoppingBag.getNumOfProducts());

            //check that the product was really added to the shopping bag
            assertEquals(product2.getProductSn(), shoppingBag.containProduct(product2.getProductSn()).getProductSn());

            //check that the total cost of the bag is correct
            assertEquals(amountToAddProduct2 * product2.getCost(), shoppingBag.getTotalCostOfBag());

            int newAmountToAddProduct2 = amountToAddProduct2 + 2;
            //change amount of an existing product
            shoppingBag.addProductToBag(product2, newAmountToAddProduct2);

            //check that there is only one product, after adding an exciting product
            assertEquals(1, shoppingBag.getNumOfProducts());

            //check that the product is in the shopping bag
            assertEquals(product2.getProductSn(), shoppingBag.containProduct(product2.getProductSn()).getProductSn());

            //check that the total cost of the bag is correct
            assertEquals(newAmountToAddProduct2 * product2.getCost(), shoppingBag.getTotalCostOfBag());
        }

        /**
         * This test check if the addProduct method fails
         * when trying to add null product.
         */
        @Test
        void addProductNull() {
            //check that the method does not add a null product
            assertFalse(shoppingBag.addProductToBag(null, 4));
            //after to fail addition to bag, check that the cost of the bag is 0
            assertEquals(0, shoppingBag.getTotalCostOfBag());
            //after to fail addition to bag, check that the num of products in the bag is 0
            assertEquals(0, shoppingBag.getNumOfProducts());
        }

        /**
         * This test checks if the addProduct method fails
         * when trying to add amount that is negative.
         */
        @Test
        void addProductNegativeAmount() {
            //the amount of a product can't be less than 0
            assertFalse(shoppingBag.addProductToBag(product2, -2));
            //after to fail addition to bag, check that the cost of the bag is 0
            assertEquals(0, shoppingBag.getTotalCostOfBag());
            //after to fail addition to bag, check that the num of products in the bag is 0
            assertEquals(0, shoppingBag.getNumOfProducts());
        }

        /**
         * This test check if the addProduct method fails
         * when trying to add amount that equals to zero.
         */
        @Test
        void addProductZeroAmount() {
            //check that product amount needs to be greater than 0
            assertFalse(shoppingBag.addProductToBag(product2, 0));
            //after to fail addition to bag, check that the cost of the bag is 0
            assertEquals(0, shoppingBag.getTotalCostOfBag());
            //after to fail addition to bag, check that the num of products in the bag is 0
            assertEquals(0, shoppingBag.getNumOfProducts());
        }

        /**
         * This test check if the addProduct method fails
         * when trying to add a product when there is not enough in store.
         */
        @Test
        void addProductNotEnoughInStore() {
            //not enough products in store
            assertFalse(shoppingBag.addProductToBag(product5, 10000));
            //after to fail addition to bag, check that the cost of the bag is 0
            assertEquals(0, shoppingBag.getTotalCostOfBag());
            //after to fail addition to bag, check that the num of products in the bag is 0
            assertEquals(0, shoppingBag.getNumOfProducts());
        }

        /**
         * This test check if the removeProduct method succeeds when the parameters
         * are correct.
         */
        @Test
        void removeProductsFromBagSuccess() {
            setUpForRemove();
            //check that the product is removed from the bag
            assertTrue(shoppingBag.removeProductFromBag(product2));
            //check that the number of products in the bag is updated
            assertEquals(1, shoppingBag.getNumOfProducts());
            //check that the total cost of the bag is updated
            assertEquals((product3.getCost() * 2), shoppingBag.getTotalCostOfBag());
            //check that the product is removed from the bag
            assertTrue(shoppingBag.removeProductFromBag(product3));
            //check that the number of products in the bag is updated
            assertEquals(0, shoppingBag.getNumOfProducts());
            //check that the total cost of the bag is updated
            assertEquals(0, shoppingBag.getTotalCostOfBag());
        }

        /**
         * This test check if the removeProduct method fails
         * when the product is null
         */
        @Test
        void removeProductNullProduct() {
            setUpForRemove();
            //try to remove a null product
            assertFalse(shoppingBag.removeProductFromBag(null));
            //check that there are still 2 products in bag
            assertEquals(2, shoppingBag.getNumOfProducts());
            //check the total cost of the bag didn't change
            assertEquals((product2.getCost() * 2) + (product3.getCost() * 2), shoppingBag.getTotalCostOfBag());
        }

        /**
         * This test check if the removeProduct method fails
         * when the product is from a different store
         */
        @Test
        void removeProductDifferentStore() {
            setUpForRemove();
            //try to remove a product that has a different store id
            assertFalse(shoppingBag.removeProductFromBag(product4));
            //check that there are still 2 products in bag
            assertEquals(2, shoppingBag.getNumOfProducts());
            //check the total cost of the bag didn't change
            assertEquals((product2.getCost() * 2) + (product3.getCost() * 2), shoppingBag.getTotalCostOfBag());
        }

        /**
         * This test check if the removeProduct method fails
         * when the product is not in the bag
         */
        @Test
        void removeProductNotInBag() {
            setUpForRemove();
            //try to remove a product that does not exists in bag
            assertFalse(shoppingBag.removeProductFromBag(product1));
            //check that there are still 2 products in bag
            assertEquals(2, shoppingBag.getNumOfProducts());
            //check the total cost of the bag didn't change
            assertEquals((product2.getCost() * 2) + (product3.getCost() * 2), shoppingBag.getTotalCostOfBag());
        }

        /**
         * This test check if the changeAmountOfProductInBag method succeeds
         * when the parameters are correct
         */
        @Test
        void changeAmountOfProductInBagSuccess() {
            setUpForRemove();
            //check that before change the bag has 2 items of product testProduct3
            assertEquals(2, shoppingBag.getProductListFromStore().get(product3));
            //check if the change worked
            assertTrue(shoppingBag.changeAmountOfProductInBag(product3.getProductSn(), 4));
            //check that after change the bag has 4 items of product testProduct3
            assertEquals(4, shoppingBag.getProductListFromStore().get(product3));
        }

        /**
         * This test check if the getProductAmount method fails
         * when the serial number does not mach any product
         */
        @Test
        void changeAmountOfProductInBagWrongSerialNum() {
            setUpForRemove();
            //try to change a product amount in bag with wrong serial number
            assertFalse(shoppingBag.changeAmountOfProductInBag(9, 3));
            //check that number of products didn't change
            assertEquals(2, shoppingBag.getProductListFromStore().get(product2));
            //check that number of products didn't change
            assertEquals(2, shoppingBag.getProductListFromStore().get(product3));
        }

        /**
         * This test check if the getProductAmount method fails
         * when the new amount is zero or less
         */
        @Test
        void changeAmountOfProductInBagBadAmount() {
            setUpForRemove();
            //try to change a product amount in bag to 0
            assertFalse(shoppingBag.changeAmountOfProductInBag(product2.getProductSn(), 0));
            //check that number of products didn't change
            assertEquals(2, shoppingBag.getProductListFromStore().get(product2));
            //try to change a product amount in bag to -1
            assertFalse(shoppingBag.changeAmountOfProductInBag(product3.getProductSn(), -1));
            //check that number of products didn't change
            assertEquals(2, shoppingBag.getProductListFromStore().get(product3));
        }

        /**
         * This test check if the getProductAmount method succeeds when the parameters
         * are correct.
         */
        @Test
        void getProductAmountSuccess() {
            setUpForProductAmount();
            //check that the amount is correct
            assertEquals(2, shoppingBag.getProductAmount(product3.getProductSn()));
            //check that the amount is correct
            assertEquals(5, shoppingBag.getProductAmount(product4.getProductSn()));
        }

        /**
         * This test check if the getProductAmount method fails
         * when the input is a product that does not exists in bag.
         */
        @Test
        void getProductAmountProductNotInBag() {
            setUpForProductAmount();
            //product is not in the bag
            assertEquals(-1, shoppingBag.getProductAmount(product1.getProductSn()));
        }

        /**
         * This test check if the getProductAmount method fails
         * when the new amount is zero or less
         */
        @Test
        void containProductSuc() {
            setUpForRemove();
            //check that the product is in the bag
            assertEquals(product2, shoppingBag.containProduct(product2.getProductSn()));
            //check that the number of products didn't change
            assertEquals(2, shoppingBag.getNumOfProducts());
        }

        /**
         * This test checks if getTotalCostOfBag fails when the bag is empty
         */
        @Test
        void getTotalCostOfEmptyBag() {
            Assertions.assertEquals(0, shoppingBag.getTotalCostOfBag());
        }

        /**
         * This test checks if getTotalCostOfBag fails when the bag isn't empty
         */
        @Test
        void getTotalCostOfBag() {
            int amount = 2;
            shoppingBag.addProductToBag(product2, amount);
            Assertions.assertEquals(product2.getCost() * amount, shoppingBag.getTotalCostOfBag());
        }

        /**
         * This test checks if getOriginalTotalCostOfBag fails when the bag is empty
         */
        @Test
        void getOriginalTotalCostOfEmptyBag() {
            Assertions.assertEquals(0, shoppingBag.getOriginalTotalCostOfBag());
        }

        /**
         * This test checks if getOriginalTotalCostOfBag fails when the bag is not empty
         */
        @Test
        void getOriginalTotalCostOfBag() {
            int amount = 2;
            shoppingBag.addProductToBag(product2, amount);
            Assertions.assertEquals(product2.getCost() * amount, shoppingBag.getOriginalTotalCostOfBag());
        }

        /**
         * This test checks if getNumOfProducts fails when the bag is empty
         */
        @Test
        void getNumOfProductsEmptyBag(){
            Assertions.assertEquals(0, shoppingBag.getNumOfProducts());
        }

        /**
         * This test checks if getNumOfProducts fails when the bag is not empty
         */
        @Test
        void getNumOfProductsBag(){
            int amount = 2;
            shoppingBag.addProductToBag(product2, amount);
            Assertions.assertEquals(1, shoppingBag.getNumOfProducts());
        }

        /**
         * set up products in bag for getProductAmount method
         */
        private void setUpForProductAmount(){
            Map<Product,Integer> productList = new HashMap<>();
            productList.put(product3, 2);
            productList.put(product4, 5);
            shoppingBag.setProductListFromStore(productList);
        }

        /**
         * set products in the shoppingBag for remove method
         */
        private void setUpForRemove() {
            Map<Product, Integer> testProductList = new HashMap<>();
            testProductList.put(product2, 2);
            testProductList.put(product3, 2);
            shoppingBag.setProductListFromStore(testProductList);
        }
    }

    // ****************************************** Integration ******************************************
    /**
     * Integration tests for ShoppingBag class
     */
    @Nested
    public class ShoppingBagIntegration {
        Store store;
        ShoppingBag shoppingBag;
        Product product1;
        Product product2;
        Product product3;
        Product product4;
        Product product5;

        @BeforeEach
        void setUp() {
            store = Store.builder()
                    .storeName("MovieStore")
                    .build();
            product1 = new Product("Hunger Games", ProductCategory.BOOKS_MOVIES_MUSIC, 45, 12.5, store.getStoreId());
            product2 = new Product("Harry Potter", ProductCategory.BOOKS_MOVIES_MUSIC, 45, 12.6, store.getStoreId());
            product3 = new Product("Games of Thrones", ProductCategory.BOOKS_MOVIES_MUSIC, 45, 14.5, store.getStoreId());
            product4 = new Product("The Hobbit", ProductCategory.BOOKS_MOVIES_MUSIC, 45, 10.8, store.getStoreId());
            product5 = new Product("Lord of The Ring", ProductCategory.BOOKS_MOVIES_MUSIC, 45,19.5,store.getStoreId()+1);

            product1.setProductSn(1);
            product2.setProductSn(2);
            product3.setProductSn(3);
            product4.setProductSn(4);
            product5.setProductSn(5);

            shoppingBag = new ShoppingBag(store);
        }


        /**
         * This test check if the addProduct method succeeds when the parameters
         * are correct.
         */
        @Test
        void addProductToBagSuccess() {
            //check that the product was added successfully, needs to return true
            int amountToAddProduct2 = 3;
            assertTrue(shoppingBag.addProductToBag(product2, amountToAddProduct2));
            //checks that the number of the type product is 1
            assertEquals(1, shoppingBag.getNumOfProducts());
            //check that the product was really added to the shopping bag
            assertEquals(product2.getProductSn(), shoppingBag.containProduct(product2.getProductSn()).getProductSn());
            //check that the total cost of the bag is correct
            assertEquals(amountToAddProduct2 * product2.getCost(), shoppingBag.getTotalCostOfBag());
            // adding another product to the bag
            int amountToAddProduct3 = 1;
            shoppingBag.addProductToBag(product3, amountToAddProduct3);
            //check that after adding 2 types of product that the bag really contains 2 products
            assertEquals(2, shoppingBag.getNumOfProducts());

            //check that the product was really added to the shopping bag
            assertEquals(product3.getProductSn(), shoppingBag.containProduct(product3.getProductSn()).getProductSn());

            //check that the total cost of the bag is correct
            double expectedTotalCost = amountToAddProduct2 * product2.getCost() + amountToAddProduct3 * product3.getCost();
            assertEquals(expectedTotalCost, shoppingBag.getTotalCostOfBag());
        }

        /**
         * This test check if the addProduct method succeeds when the parameters
         * are correct and if the method changes the amount in the bag correctly
         */
        @Test
        void addProductToBagAndChangeAmountInBag() {
            //check that the product was added successfully, needs to return true
            int amountToAddProduct2 = 3;
            assertTrue(shoppingBag.addProductToBag(product2, amountToAddProduct2));

            //checks that the number of the type product is 1
            assertEquals(1, shoppingBag.getNumOfProducts());

            //check that the product was really added to the shopping bag
            assertEquals(product2.getProductSn(), shoppingBag.containProduct(product2.getProductSn()).getProductSn());

            //check that the total cost of the bag is correct
            assertEquals(amountToAddProduct2 * product2.getCost(), shoppingBag.getTotalCostOfBag());

            int newAmountToAddProduct2 = amountToAddProduct2 + 2;
            //change amount of an existing product
            shoppingBag.addProductToBag(product2, newAmountToAddProduct2);

            //check that there is only one product, after adding an exciting product
            assertEquals(1, shoppingBag.getNumOfProducts());

            //check that the product is in the shopping bag
            assertEquals(product2.getProductSn(), shoppingBag.containProduct(product2.getProductSn()).getProductSn());

            //check that the total cost of the bag is correct
            assertEquals(newAmountToAddProduct2 * product2.getCost(), shoppingBag.getTotalCostOfBag());
        }

        /**
         * This test check if the addProduct method fails
         * when trying to add null product.
         */
        @Test
        void addProductNull() {
            //check that the method does not add a null product
            assertFalse(shoppingBag.addProductToBag(null, 4));
            //after to fail addition to bag, check that the cost of the bag is 0
            assertEquals(0, shoppingBag.getTotalCostOfBag());
            //after to fail addition to bag, check that the num of products in the bag is 0
            assertEquals(0, shoppingBag.getNumOfProducts());
        }

        /**
         * This test checks if the addProduct method fails
         * when trying to add amount that is negative.
         */
        @Test
        void addProductNegativeAmount() {
            //the amount of a product can't be less than 0
            assertFalse(shoppingBag.addProductToBag(product2, -2));
            //after to fail addition to bag, check that the cost of the bag is 0
            assertEquals(0, shoppingBag.getTotalCostOfBag());
            //after to fail addition to bag, check that the num of products in the bag is 0
            assertEquals(0, shoppingBag.getNumOfProducts());
        }

        /**
         * This test check if the addProduct method fails
         * when trying to add amount that equals to zero.
         */
        @Test
        void addProductZeroAmount() {
            //check that product amount needs to be greater than 0
            assertFalse(shoppingBag.addProductToBag(product2, 0));
            //after to fail addition to bag, check that the cost of the bag is 0
            assertEquals(0, shoppingBag.getTotalCostOfBag());
            //after to fail addition to bag, check that the num of products in the bag is 0
            assertEquals(0, shoppingBag.getNumOfProducts());
        }

        /**
         * This test check if the addProduct method fails
         * when trying to add a product when there is not enough in store.
         */
        @Test
        void addProductNotEnoughInStore() {
            //not enough products in store
            assertFalse(shoppingBag.addProductToBag(product5, 10000));
            //after to fail addition to bag, check that the cost of the bag is 0
            assertEquals(0, shoppingBag.getTotalCostOfBag());
            //after to fail addition to bag, check that the num of products in the bag is 0
            assertEquals(0, shoppingBag.getNumOfProducts());
        }

        /**
         * This test check if the removeProduct method succeeds when the parameters
         * are correct.
         */
        @Test
        void removeProductsFromBagSuccess() {
            setUpForRemove();
            //check that the product is removed from the bag
            assertTrue(shoppingBag.removeProductFromBag(product2));
            //check that the number of products in the bag is updated
            assertEquals(1, shoppingBag.getNumOfProducts());
            //check that the total cost of the bag is updated
            assertEquals((product3.getCost() * 2), shoppingBag.getTotalCostOfBag());
            //check that the product is removed from the bag
            assertTrue(shoppingBag.removeProductFromBag(product3));
            //check that the number of products in the bag is updated
            assertEquals(0, shoppingBag.getNumOfProducts());
            //check that the total cost of the bag is updated
            assertEquals(0, shoppingBag.getTotalCostOfBag());
        }

        /**
         * This test check if the removeProduct method fails
         * when the product is null
         */
        @Test
        void removeProductNullProduct() {
            setUpForRemove();
            //try to remove a null product
            assertFalse(shoppingBag.removeProductFromBag(null));
            //check that there are still 2 products in bag
            assertEquals(2, shoppingBag.getNumOfProducts());
            //check the total cost of the bag didn't change
            assertEquals((product2.getCost() * 2) + (product3.getCost() * 2), shoppingBag.getTotalCostOfBag());
        }

        /**
         * This test check if the removeProduct method fails
         * when the product is from a different store
         */
        @Test
        void removeProductDifferentStore() {
            setUpForRemove();
            //try to remove a product that has a different store id
            assertFalse(shoppingBag.removeProductFromBag(product4));
            //check that there are still 2 products in bag
            assertEquals(2, shoppingBag.getNumOfProducts());
            //check the total cost of the bag didn't change
            assertEquals((product2.getCost() * 2) + (product3.getCost() * 2), shoppingBag.getTotalCostOfBag());
        }

        /**
         * This test check if the removeProduct method fails
         * when the product is not in the bag
         */
        @Test
        void removeProductNotInBag() {
            setUpForRemove();
            //try to remove a product that does not exists in bag
            assertFalse(shoppingBag.removeProductFromBag(product1));
            //check that there are still 2 products in bag
            assertEquals(2, shoppingBag.getNumOfProducts());
            //check the total cost of the bag didn't change
            assertEquals((product2.getCost() * 2) + (product3.getCost() * 2), shoppingBag.getTotalCostOfBag());
        }

        /**
         * This test check if the changeAmountOfProductInBag method succeeds
         * when the parameters are correct
         */
        @Test
        void changeAmountOfProductInBagSuccess() {
            setUpForRemove();
            //check that before change the bag has 2 items of product testProduct3
            assertEquals(2, shoppingBag.getProductListFromStore().get(product3));
            //check if the change worked
            assertTrue(shoppingBag.changeAmountOfProductInBag(product3.getProductSn(), 4));
            //check that after change the bag has 4 items of product testProduct3
            assertEquals(4, shoppingBag.getProductListFromStore().get(product3));
        }

        /**
         * This test check if the getProductAmount method fails
         * when the serial number does not mach any product
         */
        @Test
        void changeAmountOfProductInBagWrongSerialNum() {
            setUpForRemove();
            //try to change a product amount in bag with wrong serial number
            assertFalse(shoppingBag.changeAmountOfProductInBag(9, 3));
            //check that number of products didn't change
            assertEquals(2, shoppingBag.getProductListFromStore().get(product2));
            //check that number of products didn't change
            assertEquals(2, shoppingBag.getProductListFromStore().get(product3));
        }

        /**
         * This test check if the getProductAmount method fails
         * when the new amount is zero or less
         */
        @Test
        void changeAmountOfProductInBagBadAmount() {
            setUpForRemove();
            //try to change a product amount in bag to 0
            assertFalse(shoppingBag.changeAmountOfProductInBag(product2.getProductSn(), 0));
            //check that number of products didn't change
            assertEquals(2, shoppingBag.getProductListFromStore().get(product2));
            //try to change a product amount in bag to -1
            assertFalse(shoppingBag.changeAmountOfProductInBag(product3.getProductSn(), -1));
            //check that number of products didn't change
            assertEquals(2, shoppingBag.getProductListFromStore().get(product3));
        }

        /**
         * This test check if the getProductAmount method succeeds when the parameters
         * are correct.
         */
        @Test
        void getProductAmountSuccess() {
            setUpForProductAmount();
            //check that the amount is correct
            assertEquals(2, shoppingBag.getProductAmount(product3.getProductSn()));
            //check that the amount is correct
            assertEquals(5, shoppingBag.getProductAmount(product4.getProductSn()));
        }

        /**
         * This test check if the getProductAmount method fails
         * when the input is a product that does not exists in bag.
         */
        @Test
        void getProductAmountProductNotInBag() {
            setUpForProductAmount();
            //product is not in the bag
            assertEquals(-1, shoppingBag.getProductAmount(product1.getProductSn()));
        }

        /**
         * This test check if the getProductAmount method fails
         * when the new amount is zero or less
         */
        @Test
        void containProductSuc() {
            setUpForRemove();
            //check that the product is in the bag
            assertEquals(product2, shoppingBag.containProduct(product2.getProductSn()));
            //check that the number of products didn't change
            assertEquals(2, shoppingBag.getNumOfProducts());
        }

        /**
         * This test checks if getTotalCostOfBag fails when the bag is empty
         */
        @Test
        void getTotalCostOfEmptyBag() {
            Assertions.assertEquals(0, shoppingBag.getTotalCostOfBag());
        }

        /**
         * This test checks if getTotalCostOfBag fails when the bag isn't empty
         */
        @Test
        void getTotalCostOfBag() {
            int amount = 2;
            shoppingBag.addProductToBag(product2, amount);
            Assertions.assertEquals(product2.getCost() * amount, shoppingBag.getTotalCostOfBag());
        }

        /**
         * This test checks if getOriginalTotalCostOfBag fails when the bag is empty
         */
        @Test
        void getOriginalTotalCostOfEmptyBag() {
            Assertions.assertEquals(0, shoppingBag.getOriginalTotalCostOfBag());
        }

        /**
         * This test checks if getOriginalTotalCostOfBag fails when the bag is not empty
         */
        @Test
        void getOriginalTotalCostOfBag() {
            int amount = 2;
            shoppingBag.addProductToBag(product2, amount);
            Assertions.assertEquals(product2.getCost() * amount, shoppingBag.getOriginalTotalCostOfBag());
        }

        /**
         * This test checks if getNumOfProducts fails when the bag is empty
         */
        @Test
        void getNumOfProductsEmptyBag(){
            Assertions.assertEquals(0, shoppingBag.getNumOfProducts());
        }

        /**
         * This test checks if getNumOfProducts fails when the bag is not empty
         */
        @Test
        void getNumOfProductsBag(){
            int amount = 2;
            shoppingBag.addProductToBag(product2, amount);
            Assertions.assertEquals(1, shoppingBag.getNumOfProducts());
        }

        /**
         * set up products in bag for getProductAmount method
         */
        private void setUpForProductAmount(){
            Map<Product,Integer> productList = new HashMap<>();
            productList.put(product3, 2);
            productList.put(product4, 5);
            shoppingBag.setProductListFromStore(productList);
        }

        /**
         * set products in the shoppingBag for remove method
         */
        private void setUpForRemove() {
            Map<Product, Integer> testProductList = new HashMap<>();
            testProductList.put(product2, 2);
            testProductList.put(product3, 2);
            shoppingBag.setProductListFromStore(testProductList);
        }
    }
}

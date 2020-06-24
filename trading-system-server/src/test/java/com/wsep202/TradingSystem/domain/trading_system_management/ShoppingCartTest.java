package com.wsep202.TradingSystem.domain.trading_system_management;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ShoppingCartTest {

    /**
     * Unit tests for ShoppingCart class
     */
    @Nested
    public class ShoppingCartUnit {
        ShoppingCart shoppingCart;
        ShoppingBag shoppingBag1;
        ShoppingBag shoppingBag2;
        ShoppingBag shoppingBag3;
        Store store1;
        Store store2;
        Store store3;
        Store store4;
        Product product1;
        Product product2;
        Product product3;
        Receipt receipt;

        @BeforeEach
        void setUp() {
            shoppingCart = new ShoppingCart();
            shoppingBag1 = mock(ShoppingBag.class);
            shoppingBag2 = mock(ShoppingBag.class);
            shoppingBag3 = mock(ShoppingBag.class);
            store1 = mock(Store.class);
            store2 = mock(Store.class);
            store3 = mock(Store.class);
            store4 = mock(Store.class);
            product1 = mock(Product.class);
            product2 = mock(Product.class);
            product3 = mock(Product.class);
            receipt = mock(Receipt.class);
            when(shoppingBag1.getNumOfProducts()).thenReturn(5);
            when(shoppingBag1.getTotalCostOfBag()).thenReturn(55.25);
            when(shoppingBag2.getNumOfProducts()).thenReturn(2);
            when(shoppingBag2.getTotalCostOfBag()).thenReturn(99.1);
            when(shoppingBag3.getNumOfProducts()).thenReturn(1);
            when(shoppingBag3.getTotalCostOfBag()).thenReturn(50.0);
            when(shoppingBag3.getProductAmount(product1.getProductSn())).thenReturn(1);
            when(shoppingBag3.changeAmountOfProductInBag(anyInt(), eq(3))).thenReturn(true);
            when(shoppingBag3.changeAmountOfProductInBag(anyInt(), eq(-3))).thenReturn(false);
            when(store1.getStoreId()).thenReturn(1);
            when(store2.getStoreId()).thenReturn(2);
            when(store3.getStoreId()).thenReturn(3);
            when(store4.getStoreId()).thenReturn(4);
            when(store3.isAllInStock(any())).thenReturn(true);
            when(store4.isAllInStock(any())).thenReturn(false);
            when(store4.createReceipt(any(), anyString(), anyInt(), anyInt())).thenReturn(receipt);
            when(product1.getName()).thenReturn("product1");
            when(product1.getCost()).thenReturn(50.0);
            when(product1.getProductSn()).thenReturn(1111);
            when(product1.getStoreId()).thenReturn(3);
            when(product2.getName()).thenReturn("product2");
            when(product2.getCost()).thenReturn(55.0);
            when(product2.getProductSn()).thenReturn(2222);
            when(product2.getStoreId()).thenReturn(3);
            when(product3.getName()).thenReturn("product3");
            when(product3.getCost()).thenReturn(55.0);
            when(product3.getProductSn()).thenReturn(3333);
            when(product3.getAmount()).thenReturn(10);
            when(product3.getStoreId()).thenReturn(3);
        }

        /**
         * This test check if the addBag method succeeds
         * when the parameters are correct.
         */
        @Test
        void addBagToCartSuccess() {
            //check that the addition was successful
            assertTrue(shoppingCart.addBagToCart(store1, shoppingBag1));
            //check that the number of products in cart is correct
            assertEquals(shoppingBag1.getNumOfProducts(), shoppingCart.getNumOfProductsInCart());
            //check if there is only 1 shopping bag in cart
            assertEquals(1, shoppingCart.getNumOfBagsInCart());
            //check the total cost of the cart is correct
            assertEquals(shoppingBag1.getTotalCostOfBag(), shoppingCart.getTotalCartCost());
            //check that the addition was successful
            assertTrue(shoppingCart.addBagToCart(store2, shoppingBag2));
            //check that the number of products in cart is correct
            assertEquals(shoppingBag2.getNumOfProducts() + shoppingBag1.getNumOfProducts(),
                    shoppingCart.getNumOfProductsInCart());
            //check if there are 2 shopping bags in cart
            assertEquals(2, shoppingCart.getNumOfBagsInCart());
            //check the total cost of the cart is correct
            assertEquals(shoppingBag1.getTotalCostOfBag() + shoppingBag2.getTotalCostOfBag(),
                    shoppingCart.getTotalCartCost());
        }

        /**
         * This test check if the addBag method fails
         * when trying to add a null store
         */
        @Test
        void addBagToCartNullStore() {
            //check that the cart cant add a null object
            assertFalse(shoppingCart.addBagToCart(null, shoppingBag2));
            //check that after fail addition there is 0 bags in cart
            assertEquals(0, shoppingCart.getNumOfBagsInCart());
            //check that the total cost of the cart is 0 after fail additions
            assertEquals(0, shoppingCart.getTotalCartCost());
        }

        /**
         * This test check if the addBag method fails
         * when trying to add a null bag
         */
        @Test
        void addBagToCartNullBag() {
            //check that the cart cant add a null object
            assertFalse(shoppingCart.addBagToCart(store2, null));
            //check that after fail addition there is 0 bags in cart
            assertEquals(0, shoppingCart.getNumOfBagsInCart());
            //check that the total cost of the cart is 0 after fail additions
            assertEquals(0, shoppingCart.getTotalCartCost());
        }

        /**
         * This test check if the addBag method fails
         * when trying to add the same bag
         */
        @Test
        void addBagToCartExistingBag() {
            setUpForAddNRemoveNGetBag();
            //check that the amount in cart is 1 before bad addition
            assertEquals(1, shoppingCart.getNumOfBagsInCart());
            //check that the cart can't add the same bag twice
            assertFalse(shoppingCart.addBagToCart(store1, shoppingBag1));
            //check that the amount of bags didnt change after 2 wrong addition
            assertEquals(1, shoppingCart.getNumOfBagsInCart());
            //check that the total cost didn't change after fail addition
            assertEquals(shoppingBag1.getTotalCostOfBag(), shoppingCart.getTotalCartCost());
        }

        /**
         * This test check if the removeBag method succeeds when the parameters
         * are correct.
         */
        @Test
        void removeBagFromCartSuccess() {
            setUpForAddNRemoveNGetBag();
            //check that there is 1 bag before delete
            assertEquals(1, shoppingCart.getNumOfBagsInCart());
            //check that the remove of the bag was successful
            assertTrue(shoppingCart.removeBagFromCart(store1, shoppingBag1));
            //after remove check that there is no products in the bag
            assertEquals(0, shoppingCart.getNumOfProductsInCart());
            //after remove check that cost is 0
            assertEquals(0, shoppingCart.getTotalCartCost());
            //after remove check that there is no bags in the cart
            assertEquals(0, shoppingCart.getNumOfBagsInCart());
        }

        /**
         * This test check if the removeProduct method fails
         * when the store is null
         */
        @Test
        void removeBagFromCartNullStore() {
            setUpForAddNRemoveNGetBag();
            //check that there is 1 bag before delete
            assertEquals(1, shoppingCart.getNumOfBagsInCart());
            //check that the cart does not remove a null object
            assertFalse(shoppingCart.removeBagFromCart(null, shoppingBag2));
            //check that there is 1 bag after fail delete
            assertEquals(1, shoppingCart.getNumOfBagsInCart());
            //check after fail removes the total cost of the bag didn't change
            assertEquals(shoppingBag1.getTotalCostOfBag(), shoppingCart.getTotalCartCost());
        }

        /**
         * This test check if the removeProduct method fails
         * when the store is null
         */
        @Test
        void removeBagFromCartNullBag() {
            setUpForAddNRemoveNGetBag();
            //check that there is 1 bag before delete
            assertEquals(1, shoppingCart.getNumOfBagsInCart());
            //check that the cart does not remove a null object
            assertFalse(shoppingCart.removeBagFromCart(store2, null));
            //check that there is 1 bag after fail delete
            assertEquals(1, shoppingCart.getNumOfBagsInCart());
            //check after fail removes the total cost of the bag didn't change
            assertEquals(shoppingBag1.getTotalCostOfBag(), shoppingCart.getTotalCartCost());
        }

        /**
         * This test check if the getBag method succeeds when the parameters
         * are correct.
         */
        @Test
        void getShoppingBagSuccess() {
            setUpForAddNRemoveNGetBag();
            //check that the cart returns the correct bag
            assertEquals(shoppingBag1, shoppingCart.getShoppingBag(store1));
        }

        /**
         * This test check if the getBag method fails when the parameters
         * are wrong.
         */
        @Test
        void getShoppingBagNotInCart() {
            setUpForAddNRemoveNGetBag();
            //check that there is no shopping bag for a store that does not exists in cart
            assertNull(shoppingCart.getShoppingBag(store2));
        }

        /**
         * This test check if the getBag method fails when the parameters
         * are wrong.
         */
        @Test
        void getShoppingBagNullStore() {
            setUpForAddNRemoveNGetBag();
            //check that there is no shopping bag for a null object
            assertNull(shoppingCart.getShoppingBag(null));
        }

        /**
         * This test check if the removeProductInCart method succeeds when the parameters
         * are correct.
         */
        @Test
        void removeProductInCartSuccess() {
            setUpForRemoveProduct();
            //before remove check there is 1 item in cart
            assertEquals(1, shoppingCart.getNumOfProductsInCart());
            //before remove check there is 1 bag in cart
            assertEquals(1, shoppingCart.getNumOfBagsInCart());
            //check that removal was successful
            assertTrue(shoppingCart.removeProductInCart(store3, product1));
        }

        /**
         * This test check if the removeProductInCart method fails
         * when the bag is not in the cart
         */
        @Test
        void removeProductInCartBagNotInCart() {
            setUpForRemoveProduct();
            //before remove check there is 1 item in cart
            assertEquals(1, shoppingCart.getNumOfProductsInCart());
            //can't remove a product that does not exists in cart
            assertFalse(shoppingCart.removeProductInCart(store2, product1));
            //after fail remove check there is still 1 item in cart
            assertEquals(1, shoppingCart.getNumOfProductsInCart());
        }

        /**
         * This test check if the removeProductInCart method fails
         * when the one of the store is null
         */
        @Test
        void removeProductInCartNullStore() {
            setUpForRemoveProduct();
            //before remove check there is 1 item in cart
            assertEquals(1, shoppingCart.getNumOfProductsInCart());
            //can't remove from a null store
            assertFalse(shoppingCart.removeProductInCart(null, product1));
            //after fail remove check there is still 1 item in cart
            assertEquals(1, shoppingCart.getNumOfProductsInCart());
        }

        /**
         * This test check if the removeProductInCart method fails
         * when the one of the product is null
         */
        @Test
        void removeProductInCartNullProduct() {
            setUpForRemoveProduct();
            //can't remove null from cart
            assertFalse(shoppingCart.removeProductInCart(store3, null));
            //after fail remove check there is still 1 item in cart
            assertEquals(1, shoppingCart.getNumOfProductsInCart());
        }

        /**
         * This test check if the addProductToCart method succeeds
         * when the parameters are correct.
         */
        @Test
        void addProductToCartSuc() {
            setUpForRemoveProduct();
            //check before addition that there is 1 product in cart
            assertEquals(1, shoppingCart.getNumOfProductsInCart());
            //check that the method works
            assertTrue(shoppingCart.addProductToCart(store3, product2, 2));
            //check after addition that there are 2 product in cart
            //assertEquals(2, testShoppingCart.getNumOfProductsInCart());
        }

        /**
         * This test check if the watchShoppingCart method fails
         * when the store is null
         */
        @Test
        void addProductToCartNullStore() {
            setUpForRemoveProduct();
            //check before addition that there is 1 products in cart
            assertEquals(1, shoppingCart.getNumOfProductsInCart());
            //check that the method works
            assertFalse(shoppingCart.addProductToCart(null, product2, 2));
            //check after addition that there is still 1 products in cart
            assertEquals(1, shoppingCart.getNumOfProductsInCart());
        }

        /**
         * This test check if the addProductToCart method fails
         * when the product is null
         */
        @Test
        void addProductToCartNullProduct() {
            setUpForRemoveProduct();
            //check before addition that there is 1 products in cart
            assertEquals(1, shoppingCart.getNumOfProductsInCart());
            //check that the method works
            assertFalse(shoppingCart.addProductToCart(store3, null, 2));
            //check after addition that there is still 1 products in cart
            assertEquals(1, shoppingCart.getNumOfProductsInCart());
        }

        /**
         * This test check if the addProductToCart method fails
         * when the bag is not in the cart
         */
        @Test
        void addProductToCartBagNotInCart() {
            setUpForRemoveProduct();
            //check before addition that there is 1 products in cart
            assertEquals(1, shoppingCart.getNumOfProductsInCart());
            //check that the method works
            assertFalse(shoppingCart.addProductToCart(store1, product2, 2));
            //check after addition that there is still 1 products in cart
            assertEquals(1, shoppingCart.getNumOfProductsInCart());
        }

        /**
         * This test check if the watchShoppingCart method succeeds
         * when the parameters are correct.
         */
        @Test
        void watchShoppingCartSuc() {
            setUpWatch();
            Map<Product, Integer> returnedProducts = shoppingCart.watchShoppingCart();
            assertTrue(returnedProducts.containsKey(product1));
        }

        /**
         * This test check if the watchShoppingCart method fails when the parameters
         * are wrong.
         */
        @Test
        void watchShoppingCartFail() {
            setUpWatch();
            Map<Product, Integer> returnedProducts = shoppingCart.watchShoppingCart();
            assertFalse(returnedProducts.containsKey(product2));
        }

        /**
         * This test check if the isAllBagsInStock method fails when the parameters
         * are correct.
         */
        @Test
        void isAllBagsInStock() {
            shoppingBag3.addProductToBag(product3, 1);
            shoppingCart.addBagToCart(store3, shoppingBag3);
            Assertions.assertTrue(shoppingCart.isAllBagsInStock());
        }

        /**
         * This test check if the isAllBagsInStock method fails when there aren't enough of product in stock
         */
        @Test
        void isAllBagsInStockOutOfStock() {
            shoppingBag3.addProductToBag(product3, 10000);
            shoppingCart.addBagToCart(store4, shoppingBag3);
            Assertions.assertFalse(shoppingCart.isAllBagsInStock());
        }

        /**
         * This test check if the createReceipts method fails when the parameters
         * are correct.
         */
        @Test
        void createReceipts() {
            shoppingBag3.addProductToBag(product3, 1);
            shoppingCart.addBagToCart(store4, shoppingBag3);
            ArrayList<Receipt> purchaseReceipts = shoppingCart.createReceipts("buyer", 1, 1);
            Assertions.assertNotNull(purchaseReceipts);
            Assertions.assertEquals(1, purchaseReceipts.size());
            Assertions.assertEquals(purchaseReceipts.get(0), receipt);
        }

        /**
         * This test check if the createReceipts method fails when the shopping cart is empty
         */
        @Test
        void createReceiptsEmptyShoppingCart() {
            ArrayList<Receipt> purchaseReceipts = shoppingCart.createReceipts("buyer", 1, 1);
            Assertions.assertNotNull(purchaseReceipts);
            Assertions.assertEquals(0, purchaseReceipts.size());
            Assertions.assertEquals(new ArrayList<>(), purchaseReceipts);
        }

        /**
         * This test checks if the changeProductAmountInShoppingBag method fails when the shopping cart is not empty
         */
        @Test
        void changeProductAmountInShoppingBagPositiveAmount() {
            shoppingBag3.addProductToBag(product3, 1);
            shoppingCart.addBagToCart(store3, shoppingBag3);
            Assertions.assertTrue(shoppingCart.changeProductAmountInShoppingBag(
                    store3.getStoreId(), 3, product3.getProductSn()));
        }

        /**
         * This test checks if the changeProductAmountInShoppingBag method fails when the shopping cart is not empty
         */
        @Test
        void changeProductAmountInShoppingBagNegativeAmount() {
            shoppingBag3.addProductToBag(product3, 1);
            shoppingCart.addBagToCart(store3, shoppingBag3);
            Assertions.assertFalse(shoppingCart.changeProductAmountInShoppingBag(
                    store3.getStoreId(), -3, product3.getProductSn()));
        }

        /**
         * This test checks if the changeProductAmountInShoppingBag method fails when the shopping cart is empty
         */
        @Test
        void changeProductAmountInShoppingBagEmptyShoppingCart() {
            Assertions.assertFalse(shoppingCart.changeProductAmountInShoppingBag(1, 10, 3));
        }

        /**
         * Adds a shopping bag to the cart
         */
        private void setUpForAddNRemoveNGetBag() {
            Map<Store, ShoppingBag> shoppingBagsList = new HashMap<>();
            shoppingBagsList.put(store1, shoppingBag1);
            shoppingCart.setShoppingBagsList(shoppingBagsList);
        }

        /**
         * set up product and shopping bag for watchShoppingCart test
         */
        private void setUpWatch() {
            Map<Product, Integer> products = new HashMap<>();
            products.put(product1, 1);
            shoppingBag3.setProductListFromStore(products);
            when(shoppingBag3.getProductListFromStore()).thenReturn(products);
            Map<Store, ShoppingBag> bags = new HashMap<>();
            bags.put(store3, shoppingBag3);
            shoppingCart.setShoppingBagsList(bags);
        }

        /**
         * Adds a shopping bag to the cart
         */
        private void setUpForRemoveProduct() {
            Map<Product, Integer> productList = new HashMap<>();
            productList.put(product1, 1);
            productList.put(product2, 2);
            shoppingBag3.setProductListFromStore(productList);
            Map<Store, ShoppingBag> shoppingBagsList = new HashMap<>();
            shoppingBagsList.put(store3, shoppingBag3);
            shoppingCart.setShoppingBagsList(shoppingBagsList);
            when(shoppingBag3.getProductAmount(product1.getProductSn())).thenReturn(1);
            when(store3.getProduct(product1.getProductSn())).thenReturn(product1);
            when(store3.getProduct(product2.getProductSn())).thenReturn(product2);
        }
    }

    // *************************************** Integration ***************************************

    /**
     * Integration tests for ShoppingCart class
     */
    @Nested
    public class ShoppingCartIntegration {
        UserSystem userSystem;
        ShoppingCart shoppingCart;
        ShoppingBag shoppingBag1;
        ShoppingBag shoppingBag2;
        ShoppingBag shoppingBag3;
        Store store1;
        Store store2;
        Store store3;
        Product product1;
        Product product2;
        Product product3;
        Product product4;

        @BeforeEach
        void setUp() {
            userSystem = new UserSystem();
            userSystem.setUserName("userSystem");

            shoppingCart = new ShoppingCart();
            store1 = Store.builder()
                    .storeName("MovieStore")
                    .build();
            store1.setStoreId(1);
            store2 = Store.builder()
                    .storeName("MovieStoreVIP")
                    .storeId(store1.getStoreId() + 1)
                    .build();
            store3 = Store.builder()
                    .storeName("Sports")
                    .storeId(store2.getStoreId() + 1)
                    .build();
            shoppingBag1 = new ShoppingBag(store1);
            shoppingBag2 = new ShoppingBag(store2);
            shoppingBag3 = new ShoppingBag(store3);
            product1 = new Product("Hunger Games", ProductCategory.BOOKS_MOVIES_MUSIC, 45, 12.9, store1.getStoreId());
            product2 = new Product("Harry Potter", ProductCategory.BOOKS_MOVIES_MUSIC, 45, 12.9, store2.getStoreId());
            product3 = new Product("Golf club", ProductCategory.SPORTING_GOODS, 100, 99.9, store3.getStoreId());
            product4 = new Product("The Big Brother", ProductCategory.SPORTING_GOODS, 100, 99.9, store2.getStoreId());
            product1.setProductSn(1);
            product2.setProductSn(2);
            product3.setProductSn(3);
            product4.setProductSn(4);
        }

        /**
         * This test check if the add & remove methods succeeds when the parameters
         * are correct.
         */
        @Test
        void addAndRemoveSuccess() {
            shoppingCart.addBagToCart(store1, shoppingBag1);
            //check that the bag added to cart
            assertEquals(1, shoppingCart.getNumOfBagsInCart());
            //check that the cost is correct
            assertEquals(shoppingBag1.getTotalCostOfBag(), shoppingCart.getTotalCartCost());
            shoppingCart.removeBagFromCart(store1, shoppingBag1);
            //check that the bag was removed
            assertEquals(0, shoppingCart.getNumOfBagsInCart());
            // check that the cost is correct
            assertEquals(0, shoppingCart.getTotalCartCost());
        }

        /**
         * This test check if the add & get methods succeeds when the parameters
         * are correct.
         */
        @Test
        void addAndGetSuccess() {
            shoppingCart.addBagToCart(store1, shoppingBag1);
            //check that it's the same bag
            assertEquals(shoppingBag1, shoppingCart.getShoppingBag(store1));
        }

        /**
         * This test check if the add & get methods fails when the parameters
         * are wrong.
         */
        @Test
        void addAndGetFail() {
            shoppingCart.addBagToCart(store1, shoppingBag1);
            //not the same store
            assertNull(shoppingCart.getShoppingBag(store2));
        }

        /**
         * This test check if the remove & get methods succeeds when the parameters
         * are correct.
         */
        @Test
        void removeAndGetSuccess() {
            SetUpForRemoveAndGet();
            //check that the bag is not empty and holds testShoppingBag1
            assertEquals(shoppingBag1, shoppingCart.getShoppingBag(store1));
            shoppingCart.removeBagFromCart(store1, shoppingBag1);
            //check that there is no bag to return for this store
            assertNull(shoppingCart.getShoppingBag(store1));
        }

        /**
         * This test check if the remove & get methods fails when the parameters
         * are wrong.
         */
        @Test
        void removeAndGetFail() {
            SetUpForRemoveAndGet();
            shoppingCart.removeBagFromCart(store1, shoppingBag1);
            //check that the second bag is still in cart
            assertNotNull(shoppingCart.getShoppingBag(store2));
        }

        /**
         * This method adds bags to cart for remove and get tests
         */
        private void SetUpForRemoveAndGet() {
            Map<Store, ShoppingBag> shoppingBagsList = new HashMap<>();
            shoppingBagsList.put(store1, shoppingBag1);
            shoppingBagsList.put(store2, shoppingBag2);
            shoppingCart.setShoppingBagsList(shoppingBagsList);
        }

        /**
         * This test check if the add & remove & get methods succeeds when the parameters
         * are correct.
         */
        @Test
        void addAndRemoveAndGetSuccess() {
            assertTrue(shoppingCart.addBagToCart(store1, shoppingBag1));
            //check that the correct bag is in the cart
            assertEquals(shoppingBag1, shoppingCart.getShoppingBag(store1));
            shoppingCart.removeBagFromCart(store1, shoppingBag1);
            //check that there is no shopping bag for this store
            assertNull(shoppingCart.getShoppingBag(store1));
        }

        /**
         * This test check if the removeProduct method fails
         * when one of the parameters is null
         */
        @Test
        void removeProductFromCartNullProduct() {
            int numOfProductsInCart = shoppingCart.getNumOfProductsInCart();
            //can't remove a null product
            assertFalse(shoppingCart.removeProductInCart(store2, null));
            //number of products didn't change after fail remove
            assertEquals(numOfProductsInCart, shoppingCart.getNumOfProductsInCart());
        }

        /**
         * This test check if the removeProduct method fails
         * when one of the parameters is null
         */
        @Test
        void removeProductFromCartNullStore() {
            int numOfProductsInCart = shoppingCart.getNumOfProductsInCart();
            //can't remove a null product
            assertFalse(shoppingCart.removeProductInCart(null, product2));
            //number of products didn't change after fail remove
            assertEquals(numOfProductsInCart, shoppingCart.getNumOfProductsInCart());
        }

        /**
         * This test check if the removeProduct method succeeds when the parameters
         * are correct.
         */
        @Test
        void watchShoppingCartSuc() {
            //add products to cart
            shoppingBag3.addProductToBag(product3, 3);
            shoppingBag2.addProductToBag(product2, 2);
            shoppingCart.addBagToCart(store3, shoppingBag3);
            shoppingCart.addBagToCart(store2, shoppingBag2);
            Map<Product, Integer> products = shoppingCart.watchShoppingCart();
            //check that the products that was added are the returned products
            assertTrue(products.containsKey(product3));
            assertTrue(products.containsKey(product2));
        }

        @Test
        void watchShoppingCartFail() {
            //add products to cart
            shoppingBag3.addProductToBag(product3, 3);
            shoppingCart.addBagToCart(store3, shoppingBag3);
            Map<Product, Integer> products = shoppingCart.watchShoppingCart();
            //false for product that was never added
            assertFalse(products.containsKey(product2));
        }

        /**
         * This test check if the removeProduct method succeeds when the parameters
         * are correct.
         */
        @Test
        void isAllBagsInStockSuc() {
            shoppingBag2.addProductToBag(product2, 2);
            //check that all the products are in stock in the store
            assertTrue(shoppingCart.isAllBagsInStock());
        }

        /**
         * This test check if the removeProduct method fails
         * when the amount is bigger than the amount in store
         */
        @Test
        void isAllBagsInStockFail() {
            shoppingBag2.addProductToBag(product2, 20000);
            //check that there is not enough in store
            assertTrue(shoppingCart.isAllBagsInStock());
        }

        /**
         * set up for remove test, put a product to remove later
         */
        private void setUpForRemoveProduct() {
            shoppingBag3.addProductToBag(product3, 2);
            shoppingCart.addBagToCart(store3, shoppingBag3);
        }

        @Test
        void createReceiptsSuc() {
            setUpForCreateReceipts();
            ArrayList<Receipt> purchased = shoppingCart.createReceipts("TestBuyer", 1, 1);
            //check that there are to receipts returned
            assertTrue(purchased.size() == 2);
            //check that one the receipts is equal to the testStore2 id
            assertTrue(purchased.get(0).getStoreId() == store2.getStoreId() ||
                    purchased.get(1).getStoreId() == store2.getStoreId());
        }

        @Test
        void createReceiptsEmpty() {
            ArrayList<Receipt> purchased = shoppingCart.createReceipts("TestBuyer", 1, 1);
            //check that there is mo receipts
            assertTrue(purchased.size() == 0);
        }

        private void setUpForCreateReceipts() {
            shoppingBag3.addProductToBag(product3, 2);
            shoppingBag2.addProductToBag(product2, 2);
            shoppingCart.addBagToCart(store2, shoppingBag2);
            shoppingCart.addBagToCart(store3, shoppingBag3);
        }

        /**
         * This test check if the createReceipts method fails when the shopping cart is empty
         */
        @Test
        void createReceiptsEmptyShoppingCart() {
            ArrayList<Receipt> purchaseReceipts = shoppingCart.createReceipts("buyer", 1, 1);
            Assertions.assertNotNull(purchaseReceipts);
            Assertions.assertEquals(0, purchaseReceipts.size());
            Assertions.assertEquals(new ArrayList<>(), purchaseReceipts);
        }

        /**
         * This test checks if the changeProductAmountInShoppingBag method fails when the shopping cart is not empty
         */
        @Test
        void changeProductAmountInShoppingBagPositiveAmount() {
            shoppingBag3.addProductToBag(product3, 1);
            shoppingCart.addBagToCart(store3, shoppingBag3);
            Assertions.assertTrue(shoppingCart.changeProductAmountInShoppingBag(
                    store3.getStoreId(), 3, product3.getProductSn()));
        }

        /**
         * This test checks if the changeProductAmountInShoppingBag method fails when the shopping cart is not empty
         */
        @Test
        void changeProductAmountInShoppingBagNegativeAmount() {
            shoppingBag3.addProductToBag(product3, 1);
            shoppingCart.addBagToCart(store3, shoppingBag3);
            Assertions.assertFalse(shoppingCart.changeProductAmountInShoppingBag(
                    store3.getStoreId(), -3, product3.getProductSn()));
        }

        /**
         * This test check if the addProductToCart method fails
         * when the product is null
         */
        @Test
        void addProductToCartNullProduct() {
            setUpForRemoveProduct();
            //check before addition that there is 1 products in cart
            assertEquals(1, shoppingCart.getNumOfProductsInCart());
            //check that the method works
            assertFalse(shoppingCart.addProductToCart(store3, null, 2));
            //check after addition that there is still 1 products in cart
            assertEquals(1, shoppingCart.getNumOfProductsInCart());
        }

        /**
         * This test check if the removeProductInCart method fails
         * when the one of the store is null
         */
        @Test
        void removeProductInCartNullStore() {
            setUpForRemoveProduct();
            //before remove check there is 1 item in cart
            assertEquals(1, shoppingCart.getNumOfProductsInCart());
            //can't remove from a null store
            assertFalse(shoppingCart.removeProductInCart(null, product1));
            //after fail remove check there is still 1 item in cart
            assertEquals(1, shoppingCart.getNumOfProductsInCart());
        }

        /**
         * This test check if the removeProductInCart method fails
         * when the one of the product is null
         */
        @Test
        void removeProductInCartNullProduct() {
            setUpForRemoveProduct();
            //can't remove null from cart
            assertFalse(shoppingCart.removeProductInCart(store3, null));
            //after fail remove check there is still 1 item in cart
            assertEquals(1, shoppingCart.getNumOfProductsInCart());
        }
    }
}
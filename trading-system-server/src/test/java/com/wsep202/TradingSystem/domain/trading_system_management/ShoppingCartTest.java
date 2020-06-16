//package com.wsep202.TradingSystem.domain.trading_system_management;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Map;
//
//import static com.wsep202.TradingSystem.domain.trading_system_management.policy_purchase.Formatter.formatter;
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.when;
//
//class ShoppingCartTest {
//
//    /**
//     * Unit tests for ShoppingCart class
//     */
//    @Nested
//    public class ShoppingCartUnit {
//        ShoppingCart testShoppingCart;
//        ShoppingBag testShoppingBag1;
//        ShoppingBag testShoppingBag2;
//        ShoppingBag testShoppingBag3;
//        Store testStore1;
//        Store testStore2;
//        Store testStore3;
//        Product testProduct;
//        Product testProduct1;
//        Receipt receipt;
//
//
//        @BeforeEach
//        void setUp() {
//            testShoppingCart = new ShoppingCart();
//            testShoppingBag1 = mock(ShoppingBag.class);
//            testShoppingBag2 = mock(ShoppingBag.class);
//            testShoppingBag3 = mock(ShoppingBag.class);
//            testStore1 = mock(Store.class);
//            testStore2 = mock(Store.class);
//            testStore3 = mock(Store.class);
//            testProduct = mock(Product.class);
//            testProduct1 = mock(Product.class);
//            receipt = mock(Receipt.class);
//            when(testShoppingBag1.getNumOfProducts()).thenReturn(5);
//            when(testShoppingBag1.getTotalCostOfBag()).thenReturn(55.25);
//            when(testShoppingBag2.getNumOfProducts()).thenReturn(2);
//            when(testShoppingBag2.getTotalCostOfBag()).thenReturn(99.1);
//            when(testShoppingBag3.getNumOfProducts()).thenReturn(1);
//            when(testShoppingBag3.getTotalCostOfBag()).thenReturn(50.0);
//            when(testShoppingBag3.getProductAmount(testProduct.getProductSn())).thenReturn(1);
//            when(testStore1.getStoreId()).thenReturn(1);
//            when(testStore2.getStoreId()).thenReturn(2);
//            when(testStore3.getStoreId()).thenReturn(3);
//            when(testProduct.getName()).thenReturn("testProduct");
//            when(testProduct.getCost()).thenReturn(50.0);
//            when(testProduct.getProductSn()).thenReturn(1111);
//            when(testProduct.getStoreId()).thenReturn(3);
//            when(testProduct1.getName()).thenReturn("testProduct1");
//            when(testProduct1.getCost()).thenReturn(55.0);
//            when(testProduct1.getProductSn()).thenReturn(2222);
//            when(testProduct1.getStoreId()).thenReturn(3);
//        }
//
//        /**
//         * This test check if the addBag method succeeds
//         * when the parameters are correct.
//         */
//        @Test
//        void addBagToCartSuccess() {
//            //check that the addition was successful
//            assertTrue(testShoppingCart.addBagToCart(testStore1, testShoppingBag1));
//            //check that the number of products in cart is correct
//            assertEquals(testShoppingBag1.getNumOfProducts(), testShoppingCart.getNumOfProductsInCart());
//            //check if there is only 1 shopping bag in cart
//            assertEquals(1, testShoppingCart.getNumOfBagsInCart());
//            //check the total cost of the cart is correct
//            assertEquals(testShoppingBag1.getTotalCostOfBag(), testShoppingCart.getTotalCartCost());
//            //check that the addition was successful
//            assertTrue(testShoppingCart.addBagToCart(testStore2, testShoppingBag2));
//            //check that the number of products in cart is correct
//            assertEquals(testShoppingBag2.getNumOfProducts() + testShoppingBag1.getNumOfProducts(),
//                    testShoppingCart.getNumOfProductsInCart());
//            //check if there are 2 shopping bags in cart
//            assertEquals(2, testShoppingCart.getNumOfBagsInCart());
//            //check the total cost of the cart is correct
//            assertEquals(testShoppingBag1.getTotalCostOfBag() + testShoppingBag2.getTotalCostOfBag(),
//                    testShoppingCart.getTotalCartCost());
//        }
//
//        /**
//         * This test check if the addBag method fails
//         * when trying to add a null object
//         */
//        @Test
//        void addBagToCartNullObject() {
//            //check that the cart cant add a null object
//            assertFalse(testShoppingCart.addBagToCart(null, testShoppingBag2));
//            //check that after fail addition there is 0 bags in cart
//            assertEquals(0, testShoppingCart.getNumOfBagsInCart());
//            //check that the cart cant add a null object
//            assertFalse(testShoppingCart.addBagToCart(testStore2, null));
//            //check that after fail addition there is 0 bags in cart
//            assertEquals(0, testShoppingCart.getNumOfBagsInCart());
//            //check that the total cost of the cart is 0 after fail additions
//            assertEquals(0, testShoppingCart.getTotalCartCost());
//        }
//
//        /**
//         * This test check if the addBag method fails
//         * when trying to add the same bag
//         */
//        @Test
//        void addBagToCartExistingBag() {
//            setUpForAddNRemoveNGetBag();
//            //check that the amount in cart is 1 before bad addition
//            assertEquals(1, testShoppingCart.getNumOfBagsInCart());
//            //check that the cart can't add the same bag twice
//            assertFalse(testShoppingCart.addBagToCart(testStore1, testShoppingBag1));
//            //check that the amount of bags didnt change after 2 wrong addition
//            assertEquals(1, testShoppingCart.getNumOfBagsInCart());
//            //check that the total cost didn't change after fail addition
//            assertEquals(testShoppingBag1.getTotalCostOfBag(), testShoppingCart.getTotalCartCost());
//        }
//
//        /**
//         * Adds a shopping bag to the cart
//         */
//        private void setUpForAddNRemoveNGetBag() {
//            Map<Store, ShoppingBag> shoppingBagsList = new HashMap<>();
//            shoppingBagsList.put(testStore1, testShoppingBag1);
//            testShoppingCart.setShoppingBagsList(shoppingBagsList);
//        }
//
//        /**
//         * This test check if the removeBag method succeeds when the parameters
//         * are correct.
//         */
//        @Test
//        void removeBagFromCartSuccess() {
//            setUpForAddNRemoveNGetBag();
//            //check that there is 1 bag before delete
//            assertEquals(1, testShoppingCart.getNumOfBagsInCart());
//            //check that the remove of the bag was successful
//            assertTrue(testShoppingCart.removeBagFromCart(testStore1, testShoppingBag1));
//            //after remove check that there is no products in the bag
//            assertEquals(0, testShoppingCart.getNumOfProductsInCart());
//            //after remove check that cost is 0
//            assertEquals(0, testShoppingCart.getTotalCartCost());
//            //after remove check that there is no bags in the cart
//            assertEquals(0, testShoppingCart.getNumOfBagsInCart());
//        }
//
//        /**
//         * This test check if the removeProduct method fails
//         * when the the bag does not exists in cart
//         */
//        @Test
//        void removeBagFromCartBagNotExists() {
//            setUpForAddNRemoveNGetBag();
//            //check that there is 1 bag before delete
//            assertEquals(1, testShoppingCart.getNumOfBagsInCart());
//            //check that the cart does not remove a shopping bag that does not exists
//            assertFalse(testShoppingCart.removeBagFromCart(testStore2, testShoppingBag2));
//            //check after fail removes the number of shopping bags didn't change
//            assertEquals(1, testShoppingCart.getNumOfBagsInCart());
//            //check after fail removes the number of products in cart didn't change
//            assertEquals(testShoppingBag1.getNumOfProducts(), testShoppingCart.getNumOfProductsInCart());
//            //check after fail removes the total cost of the bag didn't change
//            assertEquals(testShoppingBag1.getTotalCostOfBag(), testShoppingCart.getTotalCartCost());
//        }
//
//        /**
//         * This test check if the removeProduct method fails
//         * when the objects that needs to be removed are null
//         */
//        @Test
//        void removeBagFromCartNullObject() {
//            setUpForAddNRemoveNGetBag();
//            //check that there is 1 bag before delete
//            assertEquals(1, testShoppingCart.getNumOfBagsInCart());
//            //check that the cart does not remove a null object
//            assertFalse(testShoppingCart.removeBagFromCart(null, testShoppingBag2));
//            //check that the cart does not remove a null object
//            assertFalse(testShoppingCart.removeBagFromCart(testStore2, null));
//            //check that there is 1 bag after fail delete
//            assertEquals(1, testShoppingCart.getNumOfBagsInCart());
//            //check after fail removes the total cost of the bag didn't change
//            assertEquals(testShoppingBag1.getTotalCostOfBag(), testShoppingCart.getTotalCartCost());
//        }
//
//        /**
//         * This test check if the getBag method succeeds when the parameters
//         * are correct.
//         */
//        @Test
//        void getShoppingBagSuccess() {
//            setUpForAddNRemoveNGetBag();
//            //check that the cart returns the correct bag
//            assertEquals(testShoppingBag1, testShoppingCart.getShoppingBag(testStore1));
//        }
//
//        /**
//         * This test check if the getBag method fails when the parameters
//         * are wrong.
//         */
//        @Test
//        void getShoppingBagNotInCart() {
//            setUpForAddNRemoveNGetBag();
//            //check that there is no shopping bag for a store that does not exists in cart
//            assertNull(testShoppingCart.getShoppingBag(testStore2));
//        }
//
//        /**
//         * This test check if the getBag method fails when the parameters
//         * are wrong.
//         */
//        @Test
//        void getShoppingBagNull() {
//            setUpForAddNRemoveNGetBag();
//            //check that there is no shopping bag for a null object
//            assertNull(testShoppingCart.getShoppingBag(null));
//        }
//
//        /**
//         * This test check if the updateNumProduct method succeeds when the parameters
//         * are correct.
//         */
//        @Test
//        void removeProductInCartSuccess() {
//            setUpForRemoveProduct();
//            //before remove check there is 1 item in cart
//            assertEquals(1, testShoppingCart.getNumOfProductsInCart());
//            //before remove check there is 1 bag in cart
//            assertEquals(1, testShoppingCart.getNumOfBagsInCart());
//            //check that removal was successful
//            assertTrue(testShoppingCart.removeProductInCart(testStore3, testProduct));
//        }
//
//        /**
//         * This test check if the updateNumProduct method fails
//         * when the bag is not in the cart
//         */
//        @Test
//        void removeProductInCartBagNotInCart() {
//            setUpForRemoveProduct();
//            //before remove check there is 1 item in cart
//            assertEquals(1, testShoppingCart.getNumOfProductsInCart());
//            //can't remove a product that does not exists in cart
//            assertFalse(testShoppingCart.removeProductInCart(testStore2, testProduct));
//            //after fail remove check there is still 1 item in cart
//            assertEquals(1, testShoppingCart.getNumOfProductsInCart());
//        }
//
//        /**
//         * This test check if the updateNumProduct method fails
//         * when the one of the parameters is null
//         */
//        @Test
//        void removeProductInCartNull() {
//            setUpForRemoveProduct();
//            //before remove check there is 1 item in cart
//            assertEquals(1, testShoppingCart.getNumOfProductsInCart());
//            //can't remove from a null store
//            assertFalse(testShoppingCart.removeProductInCart(null, testProduct));
//            //can't remove null from cart
//            assertFalse(testShoppingCart.removeProductInCart(testStore3, null));
//            //after fail remove check there is still 1 item in cart
//            assertEquals(1, testShoppingCart.getNumOfProductsInCart());
//        }
//
//        /**
//         * This test check if the watchShoppingCart method succeeds
//         * when the parameters are correct.
//         */
//        @Test
//        void addProductToCartSuc() {
//            setUpForRemoveProduct();
//            //check before addition that there is 1 product in cart
//            assertEquals(1, testShoppingCart.getNumOfProductsInCart());
//            //check that the method works
//            assertTrue(testShoppingCart.addProductToCart(testStore3, testProduct1, 2));
//            //check after addition that there are 2 product in cart
//            //assertEquals(2, testShoppingCart.getNumOfProductsInCart());
//        }
//
//        /**
//         * This test check if the watchShoppingCart method fails
//         * when one of hte parameters is null
//         */
//        @Test
//        void addProductToCartNull() {
//            setUpForRemoveProduct();
//            //check before addition that there is 1 products in cart
//            assertEquals(1, testShoppingCart.getNumOfProductsInCart());
//            //check that the method works
//            assertFalse(testShoppingCart.addProductToCart(null, testProduct1, 2));
//            //check that the method works
//            assertFalse(testShoppingCart.addProductToCart(testStore3, null, 2));
//            //check after addition that there is still 1 products in cart
//            assertEquals(1, testShoppingCart.getNumOfProductsInCart());
//        }
//
//        /**
//         * This test check if the watchShoppingCart method fails
//         * when the bag is not in the cart
//         */
//        @Test
//        void addProductToCartBagNotInCart() {
//            setUpForRemoveProduct();
//            //check before addition that there is 1 products in cart
//            assertEquals(1, testShoppingCart.getNumOfProductsInCart());
//            //check that the method works
//            assertFalse(testShoppingCart.addProductToCart(testStore1, testProduct1, 2));
//            //check after addition that there is still 1 products in cart
//            assertEquals(1, testShoppingCart.getNumOfProductsInCart());
//        }
//
//        /**
//         * This test check if the watchShoppingCart method succeeds
//         * when the parameters are correct.
//         */
//        @Test
//        void watchShoppingCartSuc() {
//            setUpWatch();
//            Map<Product, Integer> returnedProducts = testShoppingCart.watchShoppingCart();
//            assertTrue(returnedProducts.containsKey(testProduct));
//        }
//
//        /**
//         * This test check if the watchShoppingCart method fails when the parameters
//         * are wrong.
//         */
//        @Test
//        void watchShoppingCartFail() {
//            setUpWatch();
//            Map<Product, Integer> returnedProducts = testShoppingCart.watchShoppingCart();
//            assertFalse(returnedProducts.containsKey(testProduct1));
//        }
//
//        /**
//         * set up product and shopping bag for watchShoppingCart test
//         */
//        private void setUpWatch() {
//            Map<Product, Integer> products = new HashMap<>();
//            products.put(testProduct, 1);
//            testShoppingBag3.setProductListFromStore(products);
//            when(testShoppingBag3.getProductListFromStore()).thenReturn(products);
//            Map<Store, ShoppingBag> bags = new HashMap<>();
//            bags.put(testStore3, testShoppingBag3);
//            testShoppingCart.setShoppingBagsList(bags);
//        }
//
//        /**
//         * This test check if the createReceipts method succeeds
//         * when the parameters are correct.
//         */
//        @Test
//        void createReceiptsSuc() {
//            setUpForCreateReceipts();
//            ArrayList<Receipt> purchaseReceipts = testShoppingCart.createReceipts("testBuyer");
//            //check that there where 1 receipt
//            assertTrue(purchaseReceipts.size() != 0);
//            //check that the receipt mach
//            assertEquals(receipt, purchaseReceipts.get(0));
//        }
//
//        private void setUpForCreateReceipts() {
//            when(testStore1.createReceipt(testShoppingBag3, "testBuyer")).thenReturn(receipt);
//            Map<Product, Integer> productList = new HashMap<>();
//            productList.put(testProduct, 1);
//            testShoppingBag3.setProductListFromStore(productList);
//            Map<Store, ShoppingBag> shoppingBagsList = new HashMap<>();
//            shoppingBagsList.put(testStore1, testShoppingBag3);
//            testShoppingCart.setShoppingBagsList(shoppingBagsList);
//        }
//
//        /**
//         * Adds a shopping bag to the cart
//         */
//        private void setUpForRemoveProduct() {
//            Map<Product, Integer> productList = new HashMap<>();
//            productList.put(testProduct, 1);
//            productList.put(testProduct1, 2);
//            testShoppingBag3.setProductListFromStore(productList);
//            Map<Store, ShoppingBag> shoppingBagsList = new HashMap<>();
//            shoppingBagsList.put(testStore3, testShoppingBag3);
//            testShoppingCart.setShoppingBagsList(shoppingBagsList);
//            when(testShoppingBag3.getProductAmount(testProduct.getProductSn())).thenReturn(1);
//        }
//    }
//    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//    ///////////////////////////////////////////////////////Integration//////////////////////////////////////////////////
//    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
//    /**
//     * Integration tests for ShoppingCart class
//     */
//    @Nested
//    public class ShoppingCartIntegration {
//        ShoppingCart testShoppingCart;
//        ShoppingBag testShoppingBag1;
//        ShoppingBag testShoppingBag2;
//        ShoppingBag testShoppingBag3;
//        Store testStore1;
//        Store testStore2;
//        Store testStore3;
//        Product testProduct;
//        Product testProduct2;
//        Product testProduct3;
//        Product testProduct4;
//
//        @BeforeEach
//        void setUp() {
//            testShoppingCart = new ShoppingCart();
//            testStore1 = Store.builder()
//                    .storeName("MovieStore")
//                    .build();
//            testStore2 = Store.builder()
//                    .storeName("MovieStoreVIP")
//                    .storeId(testStore1.getStoreId() + 1)
//                    .build();
//            testStore3 = Store.builder()
//                    .storeName("Sports")
//                    .storeId(testStore2.getStoreId() + 1)
//                    .build();
//            testShoppingBag1 = new ShoppingBag(testStore1);
//            testShoppingBag2 = new ShoppingBag(testStore2);
//            testShoppingBag3 = new ShoppingBag(testStore3);
//            testProduct = new Product("Hunger Games", ProductCategory.BOOKS_MOVIES_MUSIC, 45, 12.9, testStore1.getStoreId());
//            testProduct2 = new Product("Harry Potter", ProductCategory.BOOKS_MOVIES_MUSIC, 45, 12.9, testStore2.getStoreId());
//            testProduct3 = new Product("Golf club", ProductCategory.SPORTING_GOODS, 100, 99.9, testStore3.getStoreId());
//            testProduct4 = new Product("The Big Brother", ProductCategory.SPORTING_GOODS, 100, 99.9, testStore2.getStoreId());
//        }
//
//        /**
//         * This test check if the add & remove methods succeeds when the parameters
//         * are correct.
//         */
//        @Test
//        void addAndRemoveSuccess() {
//            testShoppingCart.addBagToCart(testStore1, testShoppingBag1);
//            //check that the bag added to cart
//            assertEquals(1, testShoppingCart.getNumOfBagsInCart());
//            //check that the cost is correct
//            assertEquals(testShoppingBag1.getTotalCostOfBag(), testShoppingCart.getTotalCartCost());
//            testShoppingCart.removeBagFromCart(testStore1, testShoppingBag1);
//            //check that the bag was removed
//            assertEquals(0, testShoppingCart.getNumOfBagsInCart());
//            // check that the cost is correct
//            assertEquals(0, testShoppingCart.getTotalCartCost());
//        }
//
//        /**
//         * This test check if the add & remove methods fails when the parameters
//         * are wrong.
//         */
//        @Test
//        void addAndRemoveFail() {
//            testShoppingCart.addBagToCart(testStore1, testShoppingBag1);
//            //can't remove the bag because diff stores
//            assertFalse(testShoppingCart.removeBagFromCart(testStore2, testShoppingBag1));
//            //check that the number of bags didn't change
//            assertEquals(1, testShoppingCart.getNumOfBagsInCart());
//        }
//
//        /**
//         * This test check if the add & get methods succeeds when the parameters
//         * are correct.
//         */
//        @Test
//        void addAndGetSuccess() {
//            testShoppingCart.addBagToCart(testStore1, testShoppingBag1);
//            //check that it's the same bag
//            assertEquals(testShoppingBag1, testShoppingCart.getShoppingBag(testStore1));
//        }
//
//        /**
//         * This test check if the add & get methods fails when the parameters
//         * are wrong.
//         */
//        @Test
//        void addAndGetFail() {
//            testShoppingCart.addBagToCart(testStore1, testShoppingBag1);
//            //not the same store
//            assertNull(testShoppingCart.getShoppingBag(testStore2));
//        }
//
//        /**
//         * This test check if the remove & get methods succeeds when the parameters
//         * are correct.
//         */
//        @Test
//        void removeAndGetSuccess() {
//            SetUpForRemoveAndGet();
//            //check that the bag is not empty and holds testShoppingBag1
//            assertEquals(testShoppingBag1, testShoppingCart.getShoppingBag(testStore1));
//            testShoppingCart.removeBagFromCart(testStore1, testShoppingBag1);
//            //check that there is no bag to return for this store
//            assertNull(testShoppingCart.getShoppingBag(testStore1));
//        }
//
//        /**
//         * This test check if the remove & get methods fails when the parameters
//         * are wrong.
//         */
//        @Test
//        void removeAndGetFail() {
//            SetUpForRemoveAndGet();
//            testShoppingCart.removeBagFromCart(testStore1, testShoppingBag1);
//            //check that the second bag is still in cart
//            assertNotNull(testShoppingCart.getShoppingBag(testStore2));
//        }
//
//        /**
//         * This method adds bags to cart for remove and get tests
//         */
//        private void SetUpForRemoveAndGet() {
//            Map<Store, ShoppingBag> shoppingBagsList = new HashMap<>();
//            shoppingBagsList.put(testStore1, testShoppingBag1);
//            shoppingBagsList.put(testStore2, testShoppingBag2);
//            testShoppingCart.setShoppingBagsList(shoppingBagsList);
//        }
//
//        /**
//         * This test check if the add & remove & get methods succeeds when the parameters
//         * are correct.
//         */
//        @Test
//        void addAndRemoveAndGetSuccess() {
//            assertTrue(testShoppingCart.addBagToCart(testStore1, testShoppingBag1));
//            //check that the correct bag is in the cart
//            assertEquals(testShoppingBag1, testShoppingCart.getShoppingBag(testStore1));
//            testShoppingCart.removeBagFromCart(testStore1, testShoppingBag1);
//            //check that there is no shopping bag for this store
//            assertNull(testShoppingCart.getShoppingBag(testStore1));
//        }
//
//        /**
//         * This test check if the add & remove & get methods fails when the parameters
//         * are wrong.
//         */
//        @Test
//        void addAndRemoveAndGetFail() {
//            testShoppingCart.addBagToCart(testStore1, testShoppingBag1);
//            //not the same store
//            assertNull(testShoppingCart.getShoppingBag(testStore2));
//            //can't remove a bag that does not exists in cart
//            assertFalse(testShoppingCart.removeBagFromCart(testStore2, testShoppingBag2));
//        }
//
//        /**
//         * This test check if the removeProduct method succeeds when the parameters
//         * are correct.
//         */
//        @Test
//        void removeProductFromCartSuccess() {
//            setUpForRemoveProduct();
//            //before remove there is 1 item in the cart (added in set up)
//            assertEquals(1, testShoppingCart.getNumOfProductsInCart());
//            //check that the remove is successful
//            assertTrue(testShoppingCart.removeProductInCart(testStore3, testProduct3));
//            //after remove there is no items in the cart
//            assertEquals(0, testShoppingCart.getNumOfProductsInCart());
//            //check there is no bags in cart
//            assertEquals(0, testShoppingCart.getNumOfBagsInCart());
//            //check that after removal the total cost of the cart is 0
//             assertEquals(0, testShoppingCart.getTotalCartCost());
//        }
//
//        /**
//         * This test check if the removeProduct method fails
//         * when the product is not in the cart
//         */
//        @Test
//        void removeProductFromCurtFail() {
//            int numOfProductsInCart = testShoppingCart.getNumOfProductsInCart();
//            //can't remove a product that does not exists
//            assertFalse(testShoppingCart.removeProductInCart(testStore2, testProduct2));
//            //number of products didn't change after fail remove
//            assertEquals(numOfProductsInCart, testShoppingCart.getNumOfProductsInCart());
//        }
//
//        /**
//         * This test check if the removeProduct method fails
//         * when one of the parameters is null
//         */
//        @Test
//        void removeProductFromCurtNull() {
//            int numOfProductsInCart = testShoppingCart.getNumOfProductsInCart();
//            //can't remove a null product
//            assertFalse(testShoppingCart.removeProductInCart(testStore2, null));
//            //number of products didn't change after fail remove
//            assertEquals(numOfProductsInCart, testShoppingCart.getNumOfProductsInCart());
//            //can't remove a null product
//            assertFalse(testShoppingCart.removeProductInCart(null, testProduct2));
//            //number of products didn't change after fail remove
//            assertEquals(numOfProductsInCart, testShoppingCart.getNumOfProductsInCart());
//        }
//
//        /**
//         * This test check if the removeProduct method succeeds when the parameters
//         * are correct.
//         */
//        @Test
//        void watchShoppingCartSuc() {
//            //add products to cart
//            testShoppingBag3.addProductToBag(testProduct3, 3);
//            testShoppingBag2.addProductToBag(testProduct2, 2);
//            testShoppingCart.addBagToCart(testStore3, testShoppingBag3);
//            testShoppingCart.addBagToCart(testStore2, testShoppingBag2);
//            Map<Product, Integer> products = testShoppingCart.watchShoppingCart();
//            //check that the products that was added are the returned products
//            assertTrue(products.containsKey(testProduct3));
//            assertTrue(products.containsKey(testProduct2));
//        }
//
//        @Test
//        void watchShoppingCartFail() {
//            //add products to cart
//            testShoppingBag3.addProductToBag(testProduct3, 3);
//            testShoppingCart.addBagToCart(testStore3, testShoppingBag3);
//            Map<Product, Integer> products = testShoppingCart.watchShoppingCart();
//            //false for product that was never added
//            assertFalse(products.containsKey(testProduct2));
//        }
//
//        /**
//         * This test check if the removeProduct method succeeds when the parameters
//         * are correct.
//         */
//        @Test
//        void addProductToCartSuc() {
//            testShoppingBag2.addProductToBag(testProduct2, 1);
//            testShoppingCart.addBagToCart(testStore2, testShoppingBag2);
//            int numOfProductsInCart = testShoppingCart.getNumOfProductsInCart();
//            double totalPrice = testShoppingCart.getTotalCartCost();
//            //check that the product was added successfully
//            assertTrue(testShoppingCart.addProductToCart(testStore2, testProduct4, 2));
//            //check that the number of the products increased by 1
//            assertEquals(numOfProductsInCart + 1, testShoppingCart.getNumOfProductsInCart());
//            //check that the total price of the cart increased
//            assertEquals(Double.parseDouble(formatter.format(totalPrice + testProduct4.getCost() * 2)),
//                    testShoppingCart.getTotalCartCost());
//        }
//
//        /**
//         * This test check if the removeProduct method fails
//         * when the the bag of the product does not exists
//         */
//        @Test
//        void addProductToCartBagNotInCart() {
//            int numOfProductsInCart = testShoppingCart.getNumOfProductsInCart();
//            double totalPrice = testShoppingCart.getTotalCartCost();
//            //check that the product was not added to cart
//            assertFalse(testShoppingCart.addProductToCart(testStore2, testProduct4, 2));
//            //check that the number of the products didn't change
//            assertEquals(numOfProductsInCart, testShoppingCart.getNumOfProductsInCart());
//            //check that the total price of the cart didn't change
//            assertEquals(totalPrice, testShoppingCart.getTotalCartCost());
//        }
//
//        /**
//         * This test check if the removeProduct method succeeds when the parameters
//         * are correct.
//         */
//        @Test
//        void isAllBagsInStockSuc() {
//            testShoppingBag2.addProductToBag(testProduct2, 2);
//            //check that all the products are in stock in the store
//            assertTrue(testShoppingCart.isAllBagsInStock());
//        }
//
//        /**
//         * This test check if the removeProduct method fails
//         * when the amount is bigger than the amount in store
//         */
//        @Test
//        void isAllBagsInStockFail() {
//            testShoppingBag2.addProductToBag(testProduct2, 20000);
//            //check that there is not enough in store
//            assertTrue(testShoppingCart.isAllBagsInStock());
//        }
//
//        /**
//         * set up for remove test, put a product to remove later
//         */
//        private void setUpForRemoveProduct() {
//            testShoppingBag3.addProductToBag(testProduct3, 2);
//            testShoppingCart.addBagToCart(testStore3, testShoppingBag3);
//        }
//
//        @Test
//        void createReceiptsSuc(){
//            setUpForCreateReceipts();
//            ArrayList<Receipt> purchased = testShoppingCart.createReceipts("TestBuyer");
//            //check that there are to receipts returned
//            assertTrue(purchased.size() == 2);
//            //check that one the receipts is equal to the testStore2 id
//            assertTrue(purchased.get(0).getStoreId() == testStore2.getStoreId() ||
//                    purchased.get(1).getStoreId() == testStore2.getStoreId());
//        }
//
//        @Test
//        void createReceiptsEmpty(){
//            ArrayList<Receipt> purchased = testShoppingCart.createReceipts("TestBuyer");
//            //check that there is mo receipts
//            assertTrue(purchased.size() == 0);
//        }
//
//        private void setUpForCreateReceipts(){
//            testShoppingBag3.addProductToBag(testProduct3,2);
//            testShoppingBag2.addProductToBag(testProduct2,2);
//            testShoppingCart.addBagToCart(testStore2,testShoppingBag2);
//            testShoppingCart.addBagToCart(testStore3,testShoppingBag3);
//        }
//    }
//}

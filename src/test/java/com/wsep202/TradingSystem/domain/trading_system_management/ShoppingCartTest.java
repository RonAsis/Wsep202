package com.wsep202.TradingSystem.domain.trading_system_management;

import com.wsep202.TradingSystem.domain.trading_system_management.purchase.PurchasePolicy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ShoppingCartTest {

    /**
     * Unit tests for ShoppingCart class
     */
    @Nested
    public class ShoppingCartUnit {
    ShoppingCart testShoppingCart;
    ShoppingBag testShoppingBag1;
    ShoppingBag testShoppingBag2;
    ShoppingBag testShoppingBag3;
    Store testStore1;
    Store testStore2;
    Store testStore3;
    Product testProduct;
    Product testProduct1;


    @BeforeEach
    void setUp() {
        testShoppingCart = new ShoppingCart();
        testShoppingBag1 = mock(ShoppingBag.class);
        testShoppingBag2 = mock(ShoppingBag.class);
        testShoppingBag3 = mock(ShoppingBag.class);
        testStore1 = mock(Store.class);
        testStore2 = mock(Store.class);
        testStore3 = mock(Store.class);
        testProduct = mock(Product.class);
        testProduct1 = mock(Product.class);
        when(testShoppingBag1.getNumOfProductsInBag()).thenReturn(5);
        when(testShoppingBag1.getTotalCostOfBag()).thenReturn(55.25);
        when(testShoppingBag2.getNumOfProductsInBag()).thenReturn(2);
        when(testShoppingBag2.getTotalCostOfBag()).thenReturn(99.1);
        when(testShoppingBag3.getNumOfProductsInBag()).thenReturn(1);
        when(testShoppingBag3.getTotalCostOfBag()).thenReturn(50.0);
        when(testShoppingBag3.getStoreOfProduct()).thenReturn(testStore3);
        when(testShoppingBag3.getProductAmount(testProduct)).thenReturn(1);
        when(testShoppingBag2.getStoreOfProduct()).thenReturn(testStore3);
        when(testShoppingBag1.getStoreOfProduct()).thenReturn(testStore3);
        when(testStore1.getStoreId()).thenReturn(1);
        when(testStore2.getStoreId()).thenReturn(2);
        when(testStore3.getStoreId()).thenReturn(3);
        when(testProduct.getName()).thenReturn("testProduct");
        when(testProduct.getCost()).thenReturn(50.0);
        when(testProduct.getStoreId()).thenReturn(3);
    }

    /**
     * This test check if the addBag method succeeds when the parameters
     * are correct.
     */
    @Test
    void addBagToCartSuccess() {
        //check that the addition was successful
        assertTrue(testShoppingCart.addBagToCart(testStore1,testShoppingBag1));
        //check that the number of products in cart is correct
        assertEquals(testShoppingBag1.getNumOfProductsInBag(),testShoppingCart.getNumOfProductsInCart());
        //check if there is only 1 shopping bag in cart
        assertEquals(1, testShoppingCart.getNumOfBagsInCart());
        //check the total cost of the cart is correct
        assertEquals(testShoppingBag1.getTotalCostOfBag(), testShoppingCart.getTotalCartCost());
        //check that the addition was successful
        assertTrue(testShoppingCart.addBagToCart(testStore2,testShoppingBag2));
        //check that the number of products in cart is correct
        assertEquals(testShoppingBag2.getNumOfProductsInBag()+testShoppingBag1.getNumOfProductsInBag(),testShoppingCart.getNumOfProductsInCart());
        //check if there are 2 shopping bags in cart
        assertEquals(2, testShoppingCart.getNumOfBagsInCart());
        //check the total cost of the cart is correct
        assertEquals(testShoppingBag1.getTotalCostOfBag()+testShoppingBag2.getTotalCostOfBag(), testShoppingCart.getTotalCartCost());
    }

    /**
     * This test check if the addBag method fails when the parameters
     * are wrong.
     */
    @Test
    void addBagToCartFail() {
        setUpForAddNRemoveNGetBag();
        //check that the cart cant add a null object
        assertFalse(testShoppingCart.addBagToCart(null, testShoppingBag2));
        //check that the cart cant add a null object
        assertFalse(testShoppingCart.addBagToCart(testStore2, null));
        //check that the amount of bags didnt change after 2 wrong addition
        assertEquals(1,testShoppingCart.getNumOfBagsInCart());
        //check that the cart cant add the same bag twice
        assertFalse(testShoppingCart.addBagToCart(testStore1,testShoppingBag1));
        //check that the amount of bags didnt change after 2 wrong addition
        assertEquals(1,testShoppingCart.getNumOfBagsInCart());
        //check that the number of products in the cart is correct
        assertEquals(testShoppingBag1.getNumOfProductsInBag(),testShoppingCart.getNumOfProductsInCart());
        //check that the total cost of the cart is correct
        assertEquals(testShoppingBag1.getTotalCostOfBag(),testShoppingCart.getTotalCartCost());
    }

    /**
     * Adds a shopping bag to the cart
     */
    private void setUpForAddNRemoveNGetBag(){
        Map<Store, ShoppingBag> shoppingBagsList = new HashMap<>();
        shoppingBagsList.put(testStore1,testShoppingBag1);
        testShoppingCart.setShoppingBagsList(shoppingBagsList);
    }

    /**
     * This test check if the removeBag method succeeds when the parameters
     * are correct.
     */
    @Test
    void removeBagFromCartSuccess() {
        setUpForAddNRemoveNGetBag();
        //check that there is 1 bag before delete
        assertEquals(1,testShoppingCart.getNumOfBagsInCart());
        //check that the remove of the bag was successful
        assertTrue(testShoppingCart.removeBagFromCart(testStore1,testShoppingBag1));
        //after remove check that there is no products in the bag
        assertEquals(0,testShoppingCart.getNumOfProductsInCart());
        //after remove check that cost is 0
        assertEquals(0,testShoppingCart.getTotalCartCost());
        //after remove check that there is no bags in the cart
        assertEquals(0,testShoppingCart.getNumOfBagsInCart());
    }

    /**
     * This test check if the removeProduct method fails when the parameters
     * are wrong.
     */
    @Test
    void removeBagFromCartFail() {
        setUpForAddNRemoveNGetBag();
        //check that there is 1 bag before delete
        assertEquals(1,testShoppingCart.getNumOfBagsInCart());
        //check that the cart does not remove a null object
        assertFalse(testShoppingCart.removeBagFromCart(null,testShoppingBag2));
        //check that the cart does not remove a null object
        assertFalse(testShoppingCart.removeBagFromCart(testStore2,null));
        //check that the cart does not remove a shopping bag that does not exists
        assertFalse(testShoppingCart.removeBagFromCart(testStore2,testShoppingBag2));
        //check after fail removes the number of shopping bags didn't change
        assertEquals(1,testShoppingCart.getNumOfBagsInCart());
        //check after fail removes the number of products in cart didn't change
        assertEquals(testShoppingBag1.getNumOfProductsInBag(),testShoppingCart.getNumOfProductsInCart());
        //check after fail removes the total cost of the bag didn't change
        assertEquals(testShoppingBag1.getTotalCostOfBag(),testShoppingCart.getTotalCartCost());
    }

    /**
     * This test check if the getBag method succeeds when the parameters
     * are correct.
     */
    @Test
    void getShoppingBagSuccess() {
        setUpForAddNRemoveNGetBag();
        //check that the cart returns the correct bag
        assertEquals(testShoppingBag1,testShoppingCart.getShoppingBag(testStore1));
    }

    /**
     * This test check if the getBag method fails when the parameters
     * are wrong.
     */
    @Test
    void getShoppingBagFail() {
        setUpForAddNRemoveNGetBag();
        //check that there is no shopping bag for a null object
        assertNull(testShoppingCart.getShoppingBag(null));
        //check that there is no shopping bag for a store that does not exists in cart
        assertNull(testShoppingCart.getShoppingBag(testStore2));
    }

    /**
     * This test check if the updateNumProduct method succeeds when the parameters
     * are correct.
     */
    @Test
    void removeProductInCartSuccess(){
        setUpForRemoveProduct();
        //before remove check there is 1 item in cart
        assertEquals(1, testShoppingCart.getNumOfProductsInCart());
        //before remove check there is 1 bag in cart
        assertEquals(1, testShoppingCart.getNumOfBagsInCart());
        //check that removal was successful
        assertTrue(testShoppingCart.removeProductInCart(testStore3,testShoppingBag3,testProduct));
        //check there is no items in cart
        assertEquals(0, testShoppingCart.getNumOfProductsInCart());
        //check there is no bags in cart
        assertEquals(0, testShoppingCart.getNumOfBagsInCart());
        //check that after removal the total cost of the cart is 0
        assertEquals(0, testShoppingCart.getTotalCartCost());
    }

    /**
     * This test check if the updateNumProduct method fails when the parameters
     * are wrong.
     */
    @Test
    void removeProductInCartFail(){
        //can't remove null from cart
        assertFalse(testShoppingCart.removeProductInCart(testStore3,null, testProduct));
        //can't remove a product that does not exists in cart
        assertFalse(testShoppingCart.removeProductInCart(testStore2,testShoppingBag2,testProduct));
    }

    /**
     * This test check if the watchShoppingCart method succeeds when the parameters
     * are correct.
     */
    @Test
    void watchShoppingCartSuc(){
        setUpWatch();
        Map<Product, Integer> returnedProducts = testShoppingCart.watchShoppingCart();
        assertTrue(returnedProducts.containsKey(testProduct));
    }

    /**
     * This test check if the watchShoppingCart method fails when the parameters
     * are wrong.
     */
    @Test
    void watchShoppingCartFail(){
        setUpWatch();
        Map<Product, Integer> returnedProducts = testShoppingCart.watchShoppingCart();
        assertFalse(returnedProducts.containsKey(testProduct1));
    }

        /**
         * set up product and shopping bag for watchShoppingCart test
         */
    private void setUpWatch(){
        Map<Product, Integer> products = new HashMap<>();
        products.put(testProduct,1);
        testShoppingBag3.setProductListFromStore(products);
        when(testShoppingBag3.getProductListFromStore()).thenReturn(products);
//        when(testProduct.getDiscountType()).thenReturn(DiscountType.VISIBLE_DISCOUNT);
        Map<Store,ShoppingBag> bags= new HashMap<>();
        bags.put(testStore3,testShoppingBag3);
        testShoppingCart.setShoppingBagsList(bags);
    }

    /**
     * Adds a shopping bag to the cart
     */
    private void setUpForRemoveProduct(){
        Map<Product, Integer> productList = new HashMap<>();
        productList.put(testProduct,1);
        testShoppingBag3.setProductListFromStore(productList);
        Map<Store, ShoppingBag> shoppingBagsList = new HashMap<>();
        shoppingBagsList.put(testStore3,testShoppingBag3);
        testShoppingCart.setShoppingBagsList(shoppingBagsList);
        when(testShoppingBag3.getProductAmount(testProduct)).thenReturn(1);
    }
}
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////Integration//////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Integration tests for ShoppingCart class
 */
@Nested
public class ShoppingCartIntegration {
        ShoppingCart testShoppingCart;
        ShoppingBag testShoppingBag1;
        ShoppingBag testShoppingBag2;
        ShoppingBag testShoppingBag3;
        Store testStore1;
        Store testStore2;
        Store testStore3;
        Product testProduct;
        Product testProduct2;
        Product testProduct3;

        @BeforeEach
        void setUp() {
            testShoppingCart = new ShoppingCart();
            testStore1 = Store.builder()
                    .storeName("MovieStore")
//                    .purchasePolicy(new PurchasePolicy())
//                    .discountPolicy(new DiscountPolicy())
                    .build();
            testStore2 = Store.builder()
                    .storeName("MovieStoreVIP")
//                    .purchasePolicy(new PurchasePolicy())
//                    .discountPolicy(new DiscountPolicy())
                    .storeId(testStore1.getStoreId()+1)
                    .build();
            testStore3 = Store.builder()
                    .storeName("Sports")
//                    .purchasePolicy(new PurchasePolicy())
//                    .discountPolicy(new DiscountPolicy())
                    .storeId(testStore2.getStoreId()+1)
                    .build();
            testShoppingBag1 = new ShoppingBag(testStore1);
            testShoppingBag2 = new ShoppingBag(testStore2);
            testShoppingBag3 = new ShoppingBag(testStore3);
            testProduct = new Product("Hunger Games", ProductCategory.BOOKS_MOVIES_MUSIC, 45, 12.9, testStore1.getStoreId());
            testProduct2 = new Product("Harry Potter", ProductCategory.BOOKS_MOVIES_MUSIC, 45, 12.9, testStore2.getStoreId());
            testProduct3 = new Product("Golf club", ProductCategory.SPORTING_GOODS, 100, 99.9, testStore3.getStoreId());
        }

        /**
         * This test check if the add & remove methods succeeds when the parameters
         * are correct.
         */
         @Test void addAndRemoveSuccess(){
             testShoppingCart.addBagToCart(testStore1,testShoppingBag1);
             //check that the bag added to cart
             assertEquals(1,testShoppingCart.getNumOfBagsInCart());
             //check that the cost is correct
             assertEquals(testShoppingBag1.getTotalCostOfBag(),testShoppingCart.getTotalCartCost());
             testShoppingCart.removeBagFromCart(testStore1,testShoppingBag1);
             //check that the bag was removed
             assertEquals(0,testShoppingCart.getNumOfBagsInCart());
             // check that the cost is correct
             assertEquals(0,testShoppingCart.getTotalCartCost());
        }

        /**
         * This test check if the add & remove methods fails when the parameters
         * are wrong.
         */
        @Test
        void addAndRemoveFail(){
            testShoppingCart.addBagToCart(testStore1,testShoppingBag1);
            //can't remove the bag because diff stores
            assertFalse(testShoppingCart.removeBagFromCart(testStore2,testShoppingBag1));
            //check that the number of bags didn't change
            assertEquals(1,testShoppingCart.getNumOfBagsInCart());
        }

        /**
         * This test check if the add & get methods succeeds when the parameters
         * are correct.
         */
        @Test
        void addAndGetSuccess(){
            testShoppingCart.addBagToCart(testStore1,testShoppingBag1);
            //check that it's the same bag
            assertEquals(testShoppingBag1, testShoppingCart.getShoppingBag(testStore1));
        }

        /**
         * This test check if the add & get methods fails when the parameters
         * are wrong.
         */
        @Test
        void addAndGetFail(){
            testShoppingCart.addBagToCart(testStore1,testShoppingBag1);
            //not the same store
            assertNull(testShoppingCart.getShoppingBag(testStore2));
        }

        /**
         * This test check if the remove & get methods succeeds when the parameters
         * are correct.
         */
        @Test
        void removeAndGetSuccess(){
            SetUpForRemoveAndGet();
            //check that the bag is not empty and holds testShoppingBag1
            assertEquals(testShoppingBag1, testShoppingCart.getShoppingBag(testStore1));
            testShoppingCart.removeBagFromCart(testStore1, testShoppingBag1);
            //check that there is no bag to return for this store
            assertNull(testShoppingCart.getShoppingBag(testStore1));
        }

        /**
         * This test check if the remove & get methods fails when the parameters
         * are wrong.
         */
        @Test
        void removeAndGetFail(){
            SetUpForRemoveAndGet();
            testShoppingCart.removeBagFromCart(testStore1,testShoppingBag1);
            //check that the second bag is still in cart
            assertNotNull(testShoppingCart.getShoppingBag(testStore2));
        }

        /**
         * This method adds bags to cart for remove and get tests
         */
        private void SetUpForRemoveAndGet(){
            Map<Store, ShoppingBag> shoppingBagsList = new HashMap<>();
            shoppingBagsList.put(testStore1,testShoppingBag1);
            shoppingBagsList.put(testStore2,testShoppingBag2);
            testShoppingCart.setShoppingBagsList(shoppingBagsList);
        }

        /**
         * This test check if the add & remove & get methods succeeds when the parameters
         * are correct.
         */
        @Test
        void addAndRemoveAndGetSuccess(){
            assertTrue(testShoppingCart.addBagToCart(testStore1,testShoppingBag1));
            //check that the correct bag is in the cart
            assertEquals(testShoppingBag1, testShoppingCart.getShoppingBag(testStore1));
            testShoppingCart.removeBagFromCart(testStore1,testShoppingBag1);
            //check that there is no shopping bag for this store
            assertNull(testShoppingCart.getShoppingBag(testStore1));
        }

        /**
         * This test check if the add & remove & get methods fails when the parameters
         * are wrong.
         */
        @Test
        void addAndRemoveAndGetFail(){
            testShoppingCart.addBagToCart(testStore1, testShoppingBag1);
            //not the same store
            assertNull(testShoppingCart.getShoppingBag(testStore2));
            //can't remove a bag that does not exists in cart
            assertFalse(testShoppingCart.removeBagFromCart(testStore2,testShoppingBag2));
        }

        /**
         * This test check if the removeProduct method succeeds when the parameters
         * are correct.
         */
        @Test
        void removeProductFromCurtSuccess(){
            setUpForRemoveProduct();
            //before remove there is 1 item in the cart (added in set up)
            assertEquals(1, testShoppingCart.getNumOfProductsInCart());
            //check that the remove is successful
            assertTrue(testShoppingCart.removeProductInCart(testStore3, testShoppingBag3, testProduct3));
            //after remove there is no items in the cart
            assertEquals(0, testShoppingCart.getNumOfProductsInCart());
        }

        /**
         * This test check if the removeProduct method fails when the parameters
         * are wrong.
         */
        @Test
        void removeProductFromCurtFail(){
            //can't remove a null product
            assertFalse(testShoppingCart.removeProductInCart(testStore2, testShoppingBag2, null));
            //can't remove a product that does not exists
            assertFalse(testShoppingCart.removeProductInCart(testStore2, testShoppingBag2, testProduct2));
        }

        @Test
        void watchShoppingCartSuc(){
            //add products to cart
            testShoppingBag3.addProductToBag(testProduct3,3);
            testShoppingBag2.addProductToBag(testProduct2,2);
            testShoppingCart.addBagToCart(testStore3,testShoppingBag3);
            testShoppingCart.addBagToCart(testStore2,testShoppingBag2);
            Map<Product,Integer> products = testShoppingCart.watchShoppingCart();
            //check that the products that was added are the returned products
            assertTrue(products.containsKey(testProduct3));
            assertTrue(products.containsKey(testProduct2));
        }

        @Test
        void watchShoppingCartFail(){
            //add products to cart
            testShoppingBag3.addProductToBag(testProduct3,3);
            testShoppingCart.addBagToCart(testStore3,testShoppingBag3);
            Map<Product,Integer> products = testShoppingCart.watchShoppingCart();
            //false for product that was never added
            assertFalse(products.containsKey(testProduct2));
        }

        /**
         * set up for remove test, put a product to remove later
         */
        private void setUpForRemoveProduct(){
                testShoppingBag3.addProductToBag(testProduct3,2);
                testShoppingCart.addBagToCart(testStore3, testShoppingBag3);
            }

}
}

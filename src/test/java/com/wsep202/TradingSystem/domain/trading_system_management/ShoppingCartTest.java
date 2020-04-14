package com.wsep202.TradingSystem.domain.trading_system_management;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ShoppingCartTest {

    ShoppingCart testShoppingCart;
    ShoppingBag testShoppingBag1;
    ShoppingBag testShoppingBag2;
    Store testStore1;
    Store testStore2;

    @BeforeEach
    void setUp() {
        testShoppingCart = new ShoppingCart();
        testShoppingBag1 = mock(ShoppingBag.class);
        testShoppingBag2 = mock(ShoppingBag.class);
        testStore1 = mock(Store.class);
        testStore2 = mock(Store.class);
        when(testShoppingBag1.getNumOfProductsInBag()).thenReturn(5);
        when(testShoppingBag1.getTotalCostOfBag()).thenReturn(55.25);
        when(testShoppingBag2.getNumOfProductsInBag()).thenReturn(2);
        when(testShoppingBag1.getTotalCostOfBag()).thenReturn(99.1);
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
        testShoppingCart.setNumOfBagsInCart(1);
        testShoppingCart.setNumOfProductsInCart(testShoppingBag1.getNumOfProductsInBag());
        testShoppingCart.setTotalCartCost(testShoppingBag1.getTotalCostOfBag());
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

    /////////////////////////////////////////////////////////////////////////////////

/**
 * Integration tests for ShoppingCart class
 */
@Nested
public class ShoppingCartIntegration {
        ShoppingCart testShoppingCart;
        ShoppingBag testShoppingBag1;
        ShoppingBag testShoppingBag2;
        Store testStore1;
        Store testStore2;

        @BeforeEach
        void setUp() {
            testShoppingCart = new ShoppingCart();
            testShoppingBag1 = mock(ShoppingBag.class);
            testShoppingBag2 = mock(ShoppingBag.class);
            testStore1 = mock(Store.class);
            testStore2 = mock(Store.class);
            when(testShoppingBag1.getNumOfProductsInBag()).thenReturn(5);
            when(testShoppingBag1.getTotalCostOfBag()).thenReturn(55.25);
            when(testShoppingBag2.getNumOfProductsInBag()).thenReturn(2);
            when(testShoppingBag1.getTotalCostOfBag()).thenReturn(99.1);
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
            testShoppingCart.addBagToCart(testStore1,testShoppingBag1);
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
    }
}
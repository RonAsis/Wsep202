package com.wsep202.TradingSystem.domain.trading_system_management;

import com.wsep202.TradingSystem.domain.exception.NoManagerInStoreException;
import com.wsep202.TradingSystem.domain.exception.NoOwnerInStoreException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserSystemTest {

    /**
     * Unit tests for UserSystem class
     */
    @Nested
    public class UserSystemUnit {

        UserSystem testUserSystem;
        Store testStore1;
        Store testStore2;
        Store testStore3;
        ShoppingCart testShoppingCart;
        ShoppingBag testShoppingBag;
        Product testProduct1;


        @BeforeEach
        void setUp() {
            testUserSystem = UserSystem.builder().build();
            testStore1 = mock(Store.class);
            testStore2 = mock(Store.class);
            testStore3 = mock(Store.class);
            testShoppingCart = mock(ShoppingCart.class);
            testShoppingBag = mock(ShoppingBag.class);
            testProduct1 = mock(Product.class);
            Set<Store> testStoreList = new HashSet<>();
            testStoreList.add(testStore1);
            testUserSystem.setManagedStores(testStoreList);
            testUserSystem.setOwnedStores(testStoreList);
            testUserSystem.setShoppingCart(testShoppingCart);
            when(testStore1.getStoreId()).thenReturn(1);
            when(testStore2.getStoreId()).thenReturn(2);
            when(testStore3.getStoreId()).thenReturn(3);
            when(testShoppingCart.getShoppingBag(testStore1)).thenReturn(testShoppingBag);
            when(testShoppingBag.addProductToBag(testProduct1,3)).thenReturn(true);
        }

        /**
         * This test check if the addOwner method succeeds when the parameters
         * are correct.
         */
        @Test
        void addNewOwnedStoreSuccess() {
            //check that the store was added to the owned list
            assertTrue(testUserSystem.addNewOwnedStore(testStore3));
            //check that testStore3 was added
            assertTrue(testUserSystem.getOwnedStores().contains(testStore3));
            //check that the store was added to the owned list
            assertTrue(testUserSystem.addNewOwnedStore(testStore2));
        }

        /**
         * This test check if the addOwner method fails when the parameters
         * are wrong.
         */
        @Test
        void addNewOwnedStoreFail() {
            //check that a null store can't be added
            assertFalse(testUserSystem.addNewOwnedStore(null));
            //was already added in setUp, check that a store can't be added twice
            assertFalse(testUserSystem.addNewOwnedStore(testStore1));
        }

        /**
         * This test check if the addManager method succeeds when the parameters
         * are correct.
         */
        @Test
        void addNewManageStoreSuccess() {
            //check that the store was added to the owned list
            assertTrue(testUserSystem.addNewManageStore(testStore3));
            //check that testStore3 was added
            assertTrue(testUserSystem.getManagedStores().contains(testStore3));
            //check that the store was added to the owned list
            assertTrue(testUserSystem.addNewManageStore(testStore2));
        }

        /**
         * This test check if the addManager method fails when the parameters
         * are wrong.
         */
        @Test
        void addNewManageStoreFail() {
            //check that a null store can't be added
            assertFalse(testUserSystem.addNewManageStore(null));
            //was already added in setUp, check that a store can't be added twice
            assertFalse(testUserSystem.addNewManageStore(testStore1));
        }

        /**
         * This test check if the removeManged method succeeds when the parameters
         * are correct.
         */
        @Test
        void removeManagedStoreSuccess(){
            //check before remove there is 1 store in managed stores list
//            assertEquals(1, testUserSystem.getManagedStores().size());
//            //check that the removal was successful
//            assertTrue(testUserSystem.removeManagedStore(testStore1));
//            //check after the removal there is no store in managed stores list
//            assertEquals(0, testUserSystem.getManagedStores().size());
        }

        /**
         * This test check if the removeManged method fails when the parameters
         * are wrong.
         */
        @Test
        void removeManagedStoreFail(){
            //check before remove there is 1 store in managed stores list
            assertEquals(1, testUserSystem.getManagedStores().size());
            //check that the removal failed
            assertFalse(testUserSystem.removeManagedStore(testStore2));
            //check after the removal there is still 1 store in managed stores list
            assertEquals(1, testUserSystem.getManagedStores().size());
            //can't remove a null store
            assertFalse(testUserSystem.removeManagedStore(null));
        }

        /**
         * This test check if the getOwner method succeeds when the parameters
         * are correct.
         */
        @Test
        void getOwnerStoreSuccess() {
            //check that the right store comes back
            assertTrue( testUserSystem.isOwner(testStore2.getStoreId()));
            //check that store is still in the list after getOwnerStore
            assertTrue(testUserSystem.getOwnedStores().contains(testStore1));
        }

        /**
         * This test check if the getOwner method fails when the parameters
         * are wrong.
         */
        @Test
        void getOwnerStoreFail() {
            assertFalse( testUserSystem.isOwner(testStore2.getStoreId()));
        }

        /**
         * This test check if the getManager method succeeds when the parameters
         * are correct.
         */
        @Test
        void getManagerStoreSuccess() {
            //check that the right store comes back
            assertEquals(testStore1, testUserSystem.getManagerStore(testStore1.getStoreId()));
            //check that store is still in the list after getOwnerStore
            assertTrue(testUserSystem.getManagedStores().contains(testStore1));
        }

        /**
         * This test check if the getManager method fails when the parameters
         * are wrong.
         */
        @Test
        void getManagerStoreFail() {
            //check that for a store that does not exists the method return exception
            assertThrows(NoManagerInStoreException.class, ()->{
                testUserSystem.getManagerStore(testStore2.getStoreId());
            });
        }

        /**
         * This test check if the saveProduct method succeeds when the parameters
         * are correct.
         */
        @Test
        void saveProductInShoppingBagSuccess() {
            //check that for existing shopping bag addition of a new product is successful
            assertTrue(testUserSystem.saveProductInShoppingBag(testStore1,testProduct1,3));
        }

        /**
         * This test check if the removeProduct method fails when the parameters
         * are wrong.
         */
        @Test
        void removeProductInShoppingBagFail() {
            //can't remove a product with null store
            assertFalse(testUserSystem.removeProductInShoppingBag(null,testProduct1));
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////Integration//////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Integration tests for UserSystem class
     */
    @Nested
    public class UserSystemIntegration {
        UserSystem testUserSystem;
        Store testStore1;
        Store testStore2;
        Product testProduct1;
        Product testProduct2;


        @BeforeEach
        void setUp() {
            testUserSystem = UserSystem.builder().build();
            testStore1 = Store.builder()
                            .storeName("MovieStore")
//                            .purchasePolicy(new PurchasePolicy())
//                            .discountPolicy(new DiscountPolicy())
                            .build();
            testStore2 = Store.builder()
                    .storeName("MovieStoreVIP")
//                    .purchasePolicy(new PurchasePolicy())
//                    .discountPolicy(new DiscountPolicy())
                    .storeId(testStore1.getStoreId()+1)
                    .build();
            testProduct1 = new Product("Hunger Games", ProductCategory.BOOKS_MOVIES_MUSIC, 45, 12.9, testStore1.getStoreId());
            testProduct2 = new Product("Harry Potter", ProductCategory.BOOKS_MOVIES_MUSIC, 45, 12.9, testStore2.getStoreId());
        }

        /**
         * This test check if the addOwner & getOwner methods succeeds when the parameters
         * are correct.
         */
        @Test
        void addNewOwnedStoreAndGetOwnerSuccess() {
            //check that the store was added
            assertTrue(testUserSystem.addNewOwnedStore(testStore1));
            //check that number of owned stores after addition is 1
            assertEquals(1, testUserSystem.getOwnedStores().size());
            //check that the object are equals
            assertTrue(testUserSystem.isOwner(testStore2.getStoreId()));
            //check that there are still 1 store in the owned list
            assertEquals(1, testUserSystem.getOwnedStores().size());
        }

        /**
         * This test check if the addOwner & getOwner methods fails when the parameters
         * are wrong.
         */
        @Test
        void addNewOwnedStoreAndGetOwnerFail() {
            testUserSystem.addNewOwnedStore(testStore1);
            //check if a store the wasn't added is in the list
            assertFalse( testUserSystem.isOwner(testStore2.getStoreId()));
        }

        /**
         * This test check if the addManager & getManager methods succeeds when the parameters
         * are correct.
         */
        @Test
        void addNewMangedStoreAndGetOwnerSuccess() {
            //check that the store was added
            assertTrue(testUserSystem.addNewManageStore(testStore1));
            //check that number of owned stores after addition is 1
            assertEquals(1, testUserSystem.getManagedStores().size());
            //check that the object are equals
            assertEquals(testStore1,testUserSystem.getManagerStore(testStore1.getStoreId()));
            //check that there are still 1 store in the owned list
            assertEquals(1, testUserSystem.getManagedStores().size());
        }

        /**
         * This test check if the addManager & getManager methods fails when the parameters
         * are wrong.
         */
        @Test
        void addNewManagedStoreAndGetOwnerFail() {
            testUserSystem.addNewManageStore(testStore1);
            //check if a store the wasn't added is in the list
            assertThrows(NoManagerInStoreException.class, ()->{
                testUserSystem.getManagerStore(testStore2.getStoreId());
            });
        }

        /**
         * This test check if the addManager & removeManager methods succeeds when the parameters
         * are correct.
         */
        @Test
        void addMangedAndRemoveManagedSuccess(){
            testUserSystem.addNewManageStore(testStore1);
            //check that after addition there is 1 store in managed store
            assertEquals(1, testUserSystem.getManagedStores().size());
            testUserSystem.removeManagedStore(testStore1);
            //check that after removal there is 0 stores in managed store
            assertEquals(0, testUserSystem.getManagedStores().size());
        }

        /**
         * This test check if the addManager & removeManager methods fails when the parameters
         * are wrong.
         */
        @Test
        void addMangedAndRemoveManagedFail(){
            testUserSystem.addNewManageStore(testStore1);
            //check that after addition there is 1 store in managed store
            assertEquals(1, testUserSystem.getManagedStores().size());
            //try to remove a store that does mot exists
            testUserSystem.removeManagedStore(testStore2);
            //check that after removal there is still 1 stores in managed store
            assertEquals(1, testUserSystem.getManagedStores().size());
        }

        /**
         * This test check if the addManager & getManager & removeManager methods succeeds when the parameters
         * are correct.
         */
        @Test
        void addRemoveGetManagedSuccess(){
            //check that before addition the managed list is empty
            assertEquals(0, testUserSystem.getManagedStores().size());
            testUserSystem.addNewManageStore(testStore1);
            testUserSystem.addNewManageStore(testStore2);
            //check after addition there are 2 stores in managed list
            assertEquals(2, testUserSystem.getManagedStores().size());
            //check that the removal is successful
            assertTrue(testUserSystem.removeManagedStore(testStore1));
            //check the store that wasn't remove  is still in the list
            assertEquals(testStore2, testUserSystem.getManagerStore(testStore2.getStoreId()));
            //check that the number of stores is in the list is 1
            assertEquals(1, testUserSystem.getManagedStores().size());
        }

        /**
         * This test check if the addManager & getManager & removeManager methods fails when the parameters
         * are wrong.
         */
        @Test
        void addRemoveGetManagedFail(){
            testUserSystem.addNewManageStore(testStore1);
            //check that the store that was added is different from testStore2
            assertThrows(NoManagerInStoreException.class, ()->{
                testUserSystem.getManagerStore(testStore2.getStoreId());
            });
            //can't remove store that was never added
            assertFalse(testUserSystem.removeManagedStore(testStore2));
        }

        @Test
        void saveProductAndRemoveBagSuccess(){
            //add first item to cart
            Assertions.assertTrue(testUserSystem.saveProductInShoppingBag(testStore1, testProduct1,3));
            //check there is 1 item in the cart after addition
            Assertions.assertEquals(1,testUserSystem.getShoppingCart().getNumOfBagsInCart());
            //add second item to cart
            Assertions.assertTrue(testUserSystem.saveProductInShoppingBag(testStore2, testProduct2,3));
            //check there are 2 item in the cart after second addition
            Assertions.assertEquals(2,testUserSystem.getShoppingCart().getNumOfBagsInCart());
            //remove first item
            Assertions.assertTrue(testUserSystem.removeProductInShoppingBag(testStore1,testProduct1));
            //check there is 1 item in the cart after removal
            Assertions.assertEquals(1,testUserSystem.getShoppingCart().getNumOfBagsInCart());
            //remove the second item
            Assertions.assertTrue(testUserSystem.removeProductInShoppingBag(testStore2,testProduct2));
            //check there are no items in the cart after removal
            Assertions.assertEquals(0,testUserSystem.getShoppingCart().getNumOfBagsInCart());
        }

        @Test
        void saveProductAndRemoveBagFail(){
            assertTrue(testUserSystem.saveProductInShoppingBag(testStore1, testProduct1,3));
            //check there is 1 item in the cart after addition
            Assertions.assertEquals(1,testUserSystem.getShoppingCart().getNumOfBagsInCart());
            //wrong store and product
            Assertions.assertFalse(testUserSystem.removeProductInShoppingBag(testStore2,testProduct2));
            //right store wrong product
            Assertions.assertFalse(testUserSystem.removeProductInShoppingBag(testStore1,testProduct2));
            //wrong store right product
            Assertions.assertFalse(testUserSystem.removeProductInShoppingBag(testStore2,testProduct1));
        }
    }
}

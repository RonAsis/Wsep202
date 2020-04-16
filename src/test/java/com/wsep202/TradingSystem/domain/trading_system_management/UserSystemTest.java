package com.wsep202.TradingSystem.domain.trading_system_management;

import com.wsep202.TradingSystem.domain.exception.NoManagerInStoreException;
import com.wsep202.TradingSystem.domain.exception.NoOwnerInStoreException;
import org.apache.catalina.User;
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
     * Unit tests for ShoppingCart class
     */
    @Nested
    public class UserSystemUnit {

        UserSystem testUserSystem;
        Store testStore1;
        Store testStore2;
        ShoppingCart testShoppingCart;
        ShoppingBag testShoppingBag;
        Product testProduct1;
        Product testProduct2;


        @BeforeEach
        void setUp() {
            testUserSystem = new UserSystem("CoolIsrael", "Israel", "Israeli", "123456");
            testStore1 = mock(Store.class);
            testStore2 = mock(Store.class);
            testShoppingCart = mock(ShoppingCart.class);
            testShoppingBag = mock(ShoppingBag.class);
            testProduct1 = mock(Product.class);
            testProduct2 = mock(Product.class);
            Set<Store> testStoreList = new HashSet<>();
            testStoreList.add(testStore1);
            testUserSystem.setManagedStores(testStoreList);
            testUserSystem.setOwnedStores(testStoreList);
            testUserSystem.setShoppingCart(testShoppingCart);
            when(testStore1.getStoreId()).thenReturn(1);
            when(testStore2.getStoreId()).thenReturn(2);
            when(testShoppingCart.getShoppingBag(testStore1)).thenReturn(testShoppingBag);
            when(testShoppingBag.addProductToBag(testProduct1,3)).thenReturn(true);
            //when(testShoppingCart.getShoppingBag())
        }

        @Test
        void addNewOwnedStoreSuccess() {
        }

        @Test
        void addNewOwnedStoreFail() {
        }

        @Test
        void addNewManageStoreSuccess() {
        }

        @Test
        void addNewManageStoreFail() {
        }

        @Test
        void getOwnerStoreSuccess() {
            //check that the right store comes back
            assertEquals(testStore1, testUserSystem.getOwnerStore(testStore1.getStoreId()));
        }

        @Test
        void getOwnerStoreFail() {
            //check that for a store that does not exists the method return exception
            assertThrows(NoOwnerInStoreException.class, ()->{
                testUserSystem.getOwnerStore(testStore2.getStoreId());
            });
        }

        @Test
        void getManagerStoreSuccess() {
            //check that the right store comes back
            assertEquals(testStore1, testUserSystem.getManagerStore(testStore1.getStoreId()));
        }

        @Test
        void getManagerStoreFail() {
            //check that for a store that does not exists the method return exception
            assertThrows(NoManagerInStoreException.class, ()->{
                testUserSystem.getManagerStore(testStore2.getStoreId());
            });
        }

        @Test
        void saveProductInShoppingBagSuccess() {
            //check that for existing shopping bag addition of a new product is successful
            assertTrue(testUserSystem.saveProductInShoppingBag(testStore1,testProduct1,3));
            //
 //           assertTrue(testUserSystem.saveProductInShoppingBag(testStore2,testProduct2,4));
        }

        @Test
        void saveProductInShoppingBagFail() {

        }

        @Test
        void removeProductInShoppingBag() {
        }
    }

    /////////////////////////////////////////////////////////////////////////////////

    /**
     * Integration tests for ShoppingCart class
     */
    @Nested
    public class UserSystemIntegration {

    }
}
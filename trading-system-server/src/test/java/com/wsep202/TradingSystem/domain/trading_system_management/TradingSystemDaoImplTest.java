package com.wsep202.TradingSystem.domain.trading_system_management;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class TradingSystemDaoImplTest {

    TradingSystemDaoImpl tradingSystemDao;
    UserSystem adminUser;

    @BeforeEach
    void setUp() {
        tradingSystemDao = new TradingSystemDaoImpl();
        adminUser = mock(UserSystem.class);
        tradingSystemDao.registerAdmin(adminUser);
        when(adminUser.getUserName()).thenReturn("admin");
    }

    @Test
    void isRegistered() {
    }

    @Test
    void addUserSystem() {
    }

    @Test
    void getUserSystem() {
    }

    /**
     * checks if there is admin with username admin in the systen
     */
    @Test
    void isAdminForRegisteredAdmin(){
        //success: returns true because there is admin in the system with username "admin"
        assertTrue(tradingSystemDao.isAdmin("admin"));
    }

    /**
     * checks case of check about user that is not admin
     */
    @Test
    void isAdminForANonRegisteredUser(){
        //fail: there is no user with this user name as admin
        assertFalse(tradingSystemDao.isAdmin("nimda"));
    }

    @Test
    void getAdministratorUser() {
    }

    @Test
    void getStore() {
    }

//    /**
//     * check the searchProductByName() functionality in case of exists product in store in the system
//     */
//    @Test
//    void searchProductByNamePositive() {
//        doNothing().when(store).setStoreId(1);
//        when(store.getStoreId()).thenReturn(1);
//        tradingSystem.insertStoreToStores(store);
//        doNothing().when(product).setName("dollhouse");
//        HashSet<Product> products = new HashSet<Product>();
//        products.add(product);
//        when(store.searchProductByName("dollhouse")).thenReturn(products);
//        // converted both to arrays because one ahd ArrayList type and the other has Set<Product> type
//        Assertions.assertArrayEquals(tradingSystem.searchProductByName("dollhouse").toArray(),products.toArray());
//    }
//
//    /**
//     * check the searchProductByName() functionality in case of not exists product in store in the system
//     */
//    @Test
//    void searchProductByNameNegative() {
//        doNothing().when(store).setStoreId(1);
//        when(store.getStoreId()).thenReturn(1);
//        tradingSystem.insertStoreToStores(store);
//        doNothing().when(product).setName("dollhouse");
//        HashSet<Product> products = new HashSet<Product>();
//        products.add(product);
//        when(store.searchProductByName("dollhouse")).thenReturn(new HashSet<>());
//        // The disjoint method returns true if its two arguments have no elements in common.
//        Assertions.assertTrue(Collections.disjoint(tradingSystem.searchProductByName("dollhouse"), products));
//    }
    @Test
    void searchProductByName() {
    }

//    /**
//     * check the searchProductByCategory() functionality in case of exists product in store in the system
//     */
//    @Test
//    void searchProductByCategoryPositive() {
//        doNothing().when(store).setStoreId(1);
//        when(store.getStoreId()).thenReturn(1);
//        tradingSystem.insertStoreToStores(store);
//        doNothing().when(product).setCategory(ProductCategory.TOYS_HOBBIES);
//        HashSet<Product> products = new HashSet<Product>();
//        products.add(product);
//        when(store.searchProductByCategory(ProductCategory.TOYS_HOBBIES)).thenReturn(products);
//        // converted both to arrays because one ahd ArrayList type and the other has Set<Product> type
//        Assertions.assertArrayEquals(tradingSystem.searchProductByCategory(ProductCategory.TOYS_HOBBIES).toArray(),products.toArray());
//    }
//
//    /**
//     * check the searchProductByCategory() functionality in case of not exists product in store in the system
//     */
//    @Test
//    void searchProductByCategoryNegative() {
//        doNothing().when(store).setStoreId(1);
//        when(store.getStoreId()).thenReturn(1);
//        tradingSystem.insertStoreToStores(store);
//        doNothing().when(product).setCategory(ProductCategory.TOYS_HOBBIES);
//        HashSet<Product> products = new HashSet<Product>();
//        products.add(product);
//        when(store.searchProductByCategory(ProductCategory.TOYS_HOBBIES)).thenReturn(new HashSet<>());
//        // The disjoint method returns true if its two arguments have no elements in common.
//        Assertions.assertTrue(Collections.disjoint(tradingSystem.searchProductByCategory(ProductCategory.TOYS_HOBBIES), products));
//    }

//    /**
//     * check the searchProductByKeyWords() functionality in case of exists product in store in the system
//     */
//    @Test
//    void searchProductByKeyWordsPositive() {
//        doNothing().when(store).setStoreId(1);
//        when(store.getStoreId()).thenReturn(1);
//        tradingSystem.insertStoreToStores(store);
//        doNothing().when(product).setName("dollhouse");
//        List<String> keyWords = new ArrayList<String>();
//        keyWords.add("doll");
//        keyWords.add("house");
//        HashSet<Product> products = new HashSet<Product>();
//        products.add(product);
//        when(store.searchProductByKeyWords(keyWords)).thenReturn(products);
//        // converted both to arrays because one ahd ArrayList type and the other has Set<Product> type
//        Assertions.assertArrayEquals(tradingSystem.searchProductByKeyWords(keyWords).toArray(),products.toArray());
//    }
//
//    /**
//     * check the searchProductByKeyWords() functionality in case of not exists product in store in the system
//     */
//    @Test
//    void searchProductByKeyWordsNegative() {
//        doNothing().when(store).setStoreId(1);
//        when(store.getStoreId()).thenReturn(1);
//        tradingSystem.insertStoreToStores(store);
//        doNothing().when(product).setName("dollhouse");
//        List<String> keyWords = new ArrayList<String>();
//        keyWords.add("elephant");
//        keyWords.add("pig");
//        HashSet<Product> products = new HashSet<Product>();
//        products.add(product);
//        when(store.searchProductByKeyWords(keyWords)).thenReturn(new HashSet<>());
//        // The disjoint method returns true if its two arguments have no elements in common.
//        Assertions.assertTrue(Collections.disjoint(tradingSystem.searchProductByKeyWords(keyWords), products));
//    }
    @Test
    void searchProductByCategory() {
    }

    @Test
    void searchProductByKeyWords() {
    }

    @Test
    void addStore() {
    }

    @Test
    void getStores() {
    }

    @Test
    void getProducts() {
    }
}

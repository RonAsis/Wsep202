package com.wsep202.TradingSystem.repositoriesTests;

import com.wsep202.TradingSystem.TradingSystemApplication;
import com.wsep202.TradingSystem.domain.trading_system_management.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TradingSystemApplication.class, TradingSystemDataBaseDao.class})
//@SpringBootTest(args = {"admin","admin"})
/*@MockBean(classes = {
        testsss.class
})*/
@DataJpaTest
public class StoreRepositoryTests {
    @Autowired
    private TradingSystemDataBaseDao tradingSystemDataBaseDao;
    MultipartFile image = null;

    @BeforeEach
    void setUp() {
    }

    @Test
    void addStoreANDgetStorePositive() {
        UserSystem userSystemOwner = new UserSystem("usernamePos", "name", false, "lname", "password", false);
        Store store = new Store(userSystemOwner,"storeName");
        tradingSystemDataBaseDao.addStore(store,userSystemOwner);
        assertEquals(tradingSystemDataBaseDao.getStore(store.getStoreId()).get(),store);
    }

    @Test
    void getStoresPositive() {
        UserSystem userSystemOwner = new UserSystem("usernamePos", "name", false, "lname", "password", false);
        Store store0 = new Store(userSystemOwner,"storeName0");
        Store store1 = new Store(userSystemOwner,"storeName1");
        Store store2 = new Store(userSystemOwner,"storeName2");
        Store store3 = new Store(userSystemOwner,"storeName3");
        tradingSystemDataBaseDao.addStore(store0,userSystemOwner);
        tradingSystemDataBaseDao.addStore(store1,userSystemOwner);
        tradingSystemDataBaseDao.addStore(store2,userSystemOwner);
        tradingSystemDataBaseDao.addStore(store3,userSystemOwner);
        Set<Store> res = tradingSystemDataBaseDao.getStores();
        assertEquals(4,res.size());
    }

    @Test
    void getProductsPositive(){
        UserSystem userSystemOwner = new UserSystem("usernamePos", "name", false, "lname", "password", false);
        Product product0 = new Product("coockie0", ProductCategory.HEALTH,10,10,1);
        Product product1 = new Product("coockie1", ProductCategory.HEALTH,10,10,1);
        Product product2 = new Product("coockie2", ProductCategory.HEALTH,10,10,1);
        Store store0 = new Store(userSystemOwner,"storeName0");
        store0.addNewProduct(userSystemOwner,product0);
        store0.addNewProduct(userSystemOwner,product1);
        store0.addNewProduct(userSystemOwner,product2);
        Store store1 = new Store(userSystemOwner,"storeName1");
        Product product4 = new Product("coockie0", ProductCategory.HEALTH,10,10,1);
        Product product3 = new Product("coockie3", ProductCategory.HEALTH,10,10,1);
        store1.addNewProduct(userSystemOwner,product3);
        store1.addNewProduct(userSystemOwner,product3);
        store1.addNewProduct(userSystemOwner,product4);
        store1.addNewProduct(userSystemOwner,product2);
        tradingSystemDataBaseDao.addStore(store0,userSystemOwner);
        tradingSystemDataBaseDao.addStore(store1,userSystemOwner);
        Set<Product> res = tradingSystemDataBaseDao.getProducts();
        assertEquals(5,res.size());
//        System.out.println(res.size());
//        for(Product product : res)
//            System.out.println(product.getProductSn());
    }

    @Test
    void searchProductByNamePositive(){
        UserSystem userSystemOwner = new UserSystem("usernamePos", "name", false, "lname", "password", false);
        Product product0 = new Product("coockie0", ProductCategory.HEALTH,10,10,1);
        Product product1 = new Product("coockie0", ProductCategory.HEALTH,10,10,1);
        Product product2 = new Product("coockie2", ProductCategory.HEALTH,10,10,1);
        Store store0 = new Store(userSystemOwner,"storeName0");
        store0.addNewProduct(userSystemOwner,product0);
        store0.addNewProduct(userSystemOwner,product1);
        store0.addNewProduct(userSystemOwner,product2);
        Store store1 = new Store(userSystemOwner,"storeName1");
        Product product4 = new Product("coockie0", ProductCategory.HEALTH,10,10,1);
        Product product3 = new Product("coockie3", ProductCategory.HEALTH,10,10,1);
        store1.addNewProduct(userSystemOwner,product3);
        store1.addNewProduct(userSystemOwner,product3);
        store1.addNewProduct(userSystemOwner,product4);
        store1.addNewProduct(userSystemOwner,product2);
        tradingSystemDataBaseDao.addStore(store0,userSystemOwner);
        tradingSystemDataBaseDao.addStore(store1,userSystemOwner);
        List<Product> res = tradingSystemDataBaseDao.searchProductByName("coockie0");
        assertEquals(3,res.size());
    }

    @Test
    void searchProductByCategoryPositive(){
        UserSystem userSystemOwner = new UserSystem("usernamePos", "name", false, "lname", "password", false);
        Product product0 = new Product("coockie0", ProductCategory.HEALTH,10,10,1);
        Product product1 = new Product("coockie5", ProductCategory.HEALTH,10,10,1);
        Product product2 = new Product("coockie2", ProductCategory.BOOKS_MOVIES_MUSIC,10,10,1);
        Store store0 = new Store(userSystemOwner,"storeName0");
        store0.addNewProduct(userSystemOwner,product0);
        store0.addNewProduct(userSystemOwner,product1);
        store0.addNewProduct(userSystemOwner,product2);
        Store store1 = new Store(userSystemOwner,"storeName1");
        Product product4 = new Product("coockie1", ProductCategory.HEALTH,10,10,1);
        Product product3 = new Product("coockie3", ProductCategory.TOYS_HOBBIES,10,10,1);
        store1.addNewProduct(userSystemOwner,product3);
        store1.addNewProduct(userSystemOwner,product3);
        store1.addNewProduct(userSystemOwner,product4);
        store1.addNewProduct(userSystemOwner,product2);
        tradingSystemDataBaseDao.addStore(store0,userSystemOwner);
        tradingSystemDataBaseDao.addStore(store1,userSystemOwner);
        List<Product> res = tradingSystemDataBaseDao.searchProductByCategory(ProductCategory.HEALTH);
        assertEquals(3,res.size());
    }

    @Test
    void searchProductByKeyWordsPositive(){
        UserSystem userSystemOwner = new UserSystem("usernamePos", "name", false, "lname", "password", false);
        Product product0 = new Product("coockie0", ProductCategory.HEALTH,10,10,1);
        Product product1 = new Product("coockie2", ProductCategory.HEALTH,10,10,1);
        Product product2 = new Product("coockie2", ProductCategory.BOOKS_MOVIES_MUSIC,10,10,1);
        Store store0 = new Store(userSystemOwner,"storeName0");
        store0.addNewProduct(userSystemOwner,product0);
        store0.addNewProduct(userSystemOwner,product1);
        store0.addNewProduct(userSystemOwner,product2);
        Store store1 = new Store(userSystemOwner,"storeName1");
        Product product4 = new Product("coockie1", ProductCategory.HEALTH,10,10,1);
        Product product3 = new Product("coockie3", ProductCategory.TOYS_HOBBIES,10,10,1);
        store1.addNewProduct(userSystemOwner,product3);
        store1.addNewProduct(userSystemOwner,product3);
        store1.addNewProduct(userSystemOwner,product4);
        store1.addNewProduct(userSystemOwner,product2);
        tradingSystemDataBaseDao.addStore(store0,userSystemOwner);
        tradingSystemDataBaseDao.addStore(store1,userSystemOwner);
        List<String> keyWords = new ArrayList<String>();
        keyWords.add("kie");
        keyWords.add("co");
        List<Product> res = tradingSystemDataBaseDao.searchProductByKeyWords(keyWords);
        assertEquals(5,res.size());
        List<String> keyWords1 = new ArrayList<String>();
        keyWords1.add("kie");
        keyWords1.add("2");
        List<Product> res1 = tradingSystemDataBaseDao.searchProductByKeyWords(keyWords1);
        assertEquals(2,res1.size());
        keyWords1.add("kie");
        keyWords1.add("co");
        res1 = tradingSystemDataBaseDao.searchProductByKeyWords(keyWords1);
        assertEquals(2,res1.size());
    }
}
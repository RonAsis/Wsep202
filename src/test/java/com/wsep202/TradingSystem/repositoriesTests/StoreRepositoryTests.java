package com.wsep202.TradingSystem.repositoriesTests;

import com.wsep202.TradingSystem.TradingSystemApplication;
import com.wsep202.TradingSystem.domain.trading_system_management.TradingSystemDataBaseDao;
import com.wsep202.TradingSystem.domain.trading_system_management.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.multipart.MultipartFile;

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
        tradingSystemDataBaseDao.addStore(store);
        assertEquals(tradingSystemDataBaseDao.getStore(store.getStoreId()).get(),store);
    }

    @Test
    void getStoresPositive() {
        UserSystem userSystemOwner = new UserSystem("usernamePos", "name", false, "lname", "password", false);
        Store store0 = new Store(userSystemOwner,"storeName0");
        Store store1 = new Store(userSystemOwner,"storeName1");
        Store store2 = new Store(userSystemOwner,"storeName2");
        Store store3 = new Store(userSystemOwner,"storeName3");
        tradingSystemDataBaseDao.addStore(store0);
        tradingSystemDataBaseDao.addStore(store1);
        tradingSystemDataBaseDao.addStore(store2);
        tradingSystemDataBaseDao.addStore(store3);
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
        tradingSystemDataBaseDao.addStore(store0);
        tradingSystemDataBaseDao.addStore(store1);
        Set<Product> res = tradingSystemDataBaseDao.getProducts();
        assertEquals(5,res.size());
//        System.out.println(res.size());
//        for(Product product : res)
//            System.out.println(product.getProductSn());
    }

}

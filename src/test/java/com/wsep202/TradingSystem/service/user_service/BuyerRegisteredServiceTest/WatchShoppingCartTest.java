package com.wsep202.TradingSystem.service.user_service.BuyerRegisteredServiceTest;

import com.github.rozidan.springboot.modelmapper.WithModelMapper;
import com.wsep202.TradingSystem.domain.config.TradingSystemConfiguration;
import com.wsep202.TradingSystem.domain.trading_system_management.UserSystem;
import com.wsep202.TradingSystem.service.user_service.BuyerRegisteredService;
import com.wsep202.TradingSystem.service.user_service.GuestService;
import com.wsep202.TradingSystem.service.user_service.SellerOwnerService;
import com.wsep202.TradingSystem.service.user_service.dto.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Map;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TradingSystemConfiguration.class, GuestService.class, BuyerRegisteredService.class, SellerOwnerService.class})
@SpringBootTest(args = {"admin","admin"})
@WithModelMapper

public class WatchShoppingCartTest {
    @Autowired
    GuestService guestService;
    @Autowired
    BuyerRegisteredService buyerRegisteredService;
    @Autowired
    SellerOwnerService sellerOwnerService;

    StoreDto storeDto;
    ProductDto productDto;
    UserSystem userSystem;
    UserSystem owner;

    @BeforeEach
    void setUp() {
        userSystem = new UserSystem("username", "name", "lname", "pass");
        openStore();
        registerUser();
    }

    @AfterEach
    void tearDown() {
        this.buyerRegisteredService.clearDS();
    }

    /**
     * view a user's empty shopping cart
     */
    @Test
    void viewEmptyShoppingCart() {
        Assertions.assertNull(this.buyerRegisteredService.watchShoppingCart(this.userSystem.getUserName()));

    }

    /**
     * view an invalid user's shopping cart
     */
    @Test
    void viewShoppingCartInvalidUser() {
        Assertions.assertNull(this.buyerRegisteredService.watchShoppingCart(
                this.userSystem.getUserName()+"Not"));
    }

    /**
     * view a valid user's shopping cart
     */
    @Test
    void viewShoppingCartValidUserNotEmptyCart() {
        Assertions.assertTrue(this.buyerRegisteredService.saveProductInShoppingBag(
                this.userSystem.getUserName(), this.storeDto.getStoreId(),
                this.productDto.getProductSn(), 1));
        Assertions.assertNotNull(this.buyerRegisteredService.watchShoppingCart(
                this.userSystem.getUserName()));
    }

    /**
     * opening a new store.
     */
    void openStore(){
        owner = new UserSystem("owner","name","lname","pass");
        // registering the owner
        Assertions.assertTrue(this.guestService.registerUser(owner.getUserName(), owner.getPassword(),
                owner.getFirstName(), owner.getLastName()));

        // opening a new store, owned by owner
        Assertions.assertTrue(this.buyerRegisteredService.openStore(owner.getUserName(),
                new PurchasePolicyDto(), new DiscountPolicyDto(), "storeName"));
        // getting the storeDto of the store the owner opened
        this.storeDto = this.guestService.getStoresDtos().get(0); // getting the storeDto of the store the owner opened

        // adding a product to the owner's store
        Assertions.assertTrue(this.sellerOwnerService.addProduct(owner.getUserName(), storeDto.getStoreId(),
                "motor", "motors", 20, 20));
        // getting the productDto of the added product

        this.productDto = (ProductDto) this.buyerRegisteredService.getStoresDtos().get(0).getProducts().toArray()[0];

    }

    /**
     * register user into the system
     */
    private void registerUser() {
        this.guestService.registerUser(userSystem.getUserName(), userSystem.getPassword(),
                userSystem.getFirstName(), userSystem.getLastName());
    }

}

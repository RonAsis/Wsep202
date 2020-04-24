package com.wsep202.TradingSystem.service.user_service.SellerOwnerServiceTest;

import com.github.rozidan.springboot.modelmapper.WithModelMapper;
import com.wsep202.TradingSystem.config.TradingSystemConfiguration;
import com.wsep202.TradingSystem.domain.trading_system_management.UserSystem;
import com.wsep202.TradingSystem.service.user_service.BuyerRegisteredService;
import com.wsep202.TradingSystem.service.user_service.GuestService;
import com.wsep202.TradingSystem.service.user_service.SellerOwnerService;
import com.wsep202.TradingSystem.dto.DiscountPolicyDto;
import com.wsep202.TradingSystem.dto.PurchasePolicyDto;
import com.wsep202.TradingSystem.dto.StoreDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TradingSystemConfiguration.class, GuestService.class, BuyerRegisteredService.class, SellerOwnerService.class})
@SpringBootTest(args = {"admin","admin"})
@WithModelMapper

// *********** UC 4.1.1 - adding products to store ***********
public class AddProductTest {
    @Autowired
    GuestService guestService;
    @Autowired
    BuyerRegisteredService buyerRegisteredService;
    @Autowired
    SellerOwnerService sellerOwnerService;

    StoreDto storeDto;
    UserSystem owner;
    String productName = "product name";
    String productCategory = "motors";

    @BeforeEach
    void setUp() {
        openStoreAndRegisterOwner();
    }

    @AfterEach
    void tearDown() {

    }

    /**
     * add a valid product
     */
    @Test
    void addValidProduct() {
        Assertions.assertTrue(this.sellerOwnerService.addProduct(this.owner.getUserName(),
                this.storeDto.getStoreId(), this.productName, this.productCategory, 10, 20));
    }

    /**
     * add product
     * invalid owner
     */
    @Test
    void addProductInvalidOwner() {
        Assertions.assertFalse(this.sellerOwnerService.addProduct(this.owner.getUserName()+"Not",
                this.storeDto.getStoreId(), this.productName, this.productCategory, 10, 20));
    }

    /**
     * add product
     * invalid store
     */
    @Test
    void addValidProductInvalidStore() {
        Assertions.assertFalse(this.sellerOwnerService.addProduct(this.owner.getUserName(),
                this.storeDto.getStoreId()+5, this.productName, this.productCategory, 10, 20));
    }

    /**
     * add product
     * invalid owner
     * invalid store
     */
    @Test
    void addProductInvalidOwnerInvalidStore() {
        Assertions.assertFalse(this.sellerOwnerService.addProduct(this.owner.getUserName()+"Not",
                this.storeDto.getStoreId()+5, this.productName, this.productCategory, 10, 20));
    }

    /**
     * add a valid product
     * invalid category
     */
    @Test
    void addValidProductInvalidCategory() {
        Assertions.assertFalse(this.sellerOwnerService.addProduct(this.owner.getUserName(),
                this.storeDto.getStoreId(), this.productName, this.productCategory+"Not", 10, 20));
    }

    /**
     * add product
     * invalid owner
     * invalid category
     */
    @Test
    void addProductInvalidOwnerInvalidCategory() {
        Assertions.assertFalse(this.sellerOwnerService.addProduct(this.owner.getUserName()+"Not",
                this.storeDto.getStoreId(), this.productName, this.productCategory+"Not", 10, 20));
    }

    /**
     * add product
     * invalid store
     * invalid category
     */
    @Test
    void addValidProductInvalidStoreInvalidCategory() {
        Assertions.assertFalse(this.sellerOwnerService.addProduct(this.owner.getUserName(),
                this.storeDto.getStoreId()+5, this.productName, this.productCategory+5, 10, 20));
    }

    /**
     * add product
     * invalid owner
     * invalid store
     * invalid category
     */
    @Test
    void addProductInvalidOwnerInvalidStoreInvalidCategory() {
        Assertions.assertFalse(this.sellerOwnerService.addProduct(this.owner.getUserName()+"Not",
                this.storeDto.getStoreId()+5, this.productName, this.productCategory+5, 10, 20));
    }



    /**
     * opening a new store and adding a product to it
     */
    void openStoreAndRegisterOwner(){
        this.owner = new UserSystem("owner","name","lname","pass");
        // registering the owner
        Assertions.assertTrue(this.guestService.registerUser(owner.getUserName(), owner.getPassword(),
                owner.getFirstName(), owner.getLastName()));

        // opening a new store, owned by owner
        Assertions.assertTrue(this.buyerRegisteredService.openStore(owner.getUserName(),
                new PurchasePolicyDto(), new DiscountPolicyDto(), "storeName"));

        // getting the storeDto of the store the owner opened
        this.storeDto = this.guestService.getStoresDtos().get(0);
    }
}

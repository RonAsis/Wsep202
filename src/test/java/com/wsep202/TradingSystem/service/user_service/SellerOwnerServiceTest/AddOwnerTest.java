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

// *********** UC 4.3 - appointing a new store owner ***********
public class AddOwnerTest {
    @Autowired
    GuestService guestService;
    @Autowired
    BuyerRegisteredService buyerRegisteredService;
    @Autowired
    SellerOwnerService sellerOwnerService;

    StoreDto storeDto;
    UserSystem owner;
    UserSystem newOwner;

    @BeforeEach
    void setUp() {
        openStoreAndRegisterOwner();
        registerUser();
    }

    @AfterEach
    void tearDown() {
        this.sellerOwnerService.clearDS();
    }

    /**
     * add a valid owner to a valid store, by an owner that is valid
     */
    @Test
    void addValidOwnerRegisteredOwnerValidStore() {
        Assertions.assertTrue(this.sellerOwnerService.addOwner(this.owner.getUserName(),
                this.storeDto.getStoreId(), this.newOwner.getUserName()));
    }

    /**
     * add a valid owner to a valid store, by an owner that is invalid
     */
    @Test
    void addValidOwnerNotRegisteredOwnerValidStore() {
        Assertions.assertFalse(this.sellerOwnerService.addOwner(this.owner.getUserName()+"Not",
                this.storeDto.getStoreId(), this.newOwner.getUserName()));
    }

    /**
     * add a valid owner to an invalid store, by an owner that is valid
     */
    @Test
    void addValidOwnerRegisteredOwnerInvalidStore() {
        Assertions.assertFalse(this.sellerOwnerService.addOwner(this.owner.getUserName(),
                this.storeDto.getStoreId()+5, this.newOwner.getUserName()));
    }

    /**
     * add an invalid owner to a valid store, by an owner that is valid
     */
    @Test
    void addInvalidOwnerRegisteredOwnerValidStore() {
        Assertions.assertFalse(this.sellerOwnerService.addOwner(this.owner.getUserName(),
                this.storeDto.getStoreId(), this.newOwner.getUserName()+"Not"));
    }

    /**
     * add an invalid owner to a valid store, by an owner that is invalid
     */
    @Test
    void addInvalidOwnerNotRegisteredOwnerValidStore() {
        Assertions.assertFalse(this.sellerOwnerService.addOwner(this.owner.getUserName()+"Not",
                this.storeDto.getStoreId(), this.newOwner.getUserName()+"Not"));
    }

    /**
     * add a valid owner to an invalid store, by an owner that is invalid
     */
    @Test
    void addValidOwnerNotRegisteredOwnerInvalidStore() {
        Assertions.assertFalse(this.sellerOwnerService.addOwner(this.owner.getUserName()+"Not",
                this.storeDto.getStoreId()+5, this.newOwner.getUserName()));
    }

    /**
     * add an invalid owner to an invalid store, by an owner that is valid
     */
    @Test
    void addInvalidOwnerRegisteredOwnerInvalidStore() {
        Assertions.assertFalse(this.sellerOwnerService.addOwner(this.owner.getUserName()+"Not",
                this.storeDto.getStoreId(), this.newOwner.getUserName()+"Not"));
    }

    /**
     * add an invalid owner to an invalid store, by an owner that is invalid
     */
    @Test
    void addInvalidOwnerNotRegisteredOwnerInvalidStore() {
        Assertions.assertFalse(this.sellerOwnerService.addOwner(this.owner.getUserName()+"Not",
                this.storeDto.getStoreId()+5, this.newOwner.getUserName()+"Not"));
    }

    /**
     * opening a new store and registering its owner
     */
    void openStoreAndRegisterOwner(){
        owner = new UserSystem("owner","name","lname","pass");
        // registering the owner
        Assertions.assertTrue(this.guestService.registerUser(owner.getUserName(), owner.getPassword(),
                owner.getFirstName(), owner.getLastName()));

        // opening a new store, owned by owner
        Assertions.assertTrue(this.buyerRegisteredService.openStore(owner.getUserName(),
                new PurchasePolicyDto(), new DiscountPolicyDto(), "storeName"));

        // getting the storeDto of the store the owner opened
        this.storeDto = this.guestService.getStoresDtos().get(0);
    }

    /**
     * register user into the system
     */
    private void registerUser() {
        this.newOwner = new UserSystem("newOwner", "name", "lname", "password");
        Assertions.assertTrue(this.guestService.registerUser(newOwner.getUserName(), newOwner.getPassword(),
                newOwner.getFirstName(), newOwner.getLastName()));
    }
}

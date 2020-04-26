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

// *********** UC 4.7 - removing a store manager ***********
public class RemoveManagerTest {
    @Autowired
    GuestService guestService;
    @Autowired
    BuyerRegisteredService buyerRegisteredService;
    @Autowired
    SellerOwnerService sellerOwnerService;

    StoreDto storeDto;
    UserSystem owner;
    UserSystem oldManager;
    UserSystem ownerNotAppointee;
//
//    @BeforeEach
//    void setUp() {
//        openStoreAndRegisterOwner();
//        registerManagerAndAddAsStoreManager();
//    }
//
//    @AfterEach
//    void tearDown() {
//        this.sellerOwnerService.clearDS();
//    }
//
//    /**
//     * remove a valid manager
//     */
//    @Test
//    void removeValidManager() {
//        Assertions.assertTrue(this.sellerOwnerService.removeManager(this.owner.getUserName(),
//                this.storeDto.getStoreId(), this.oldManager.getUserName()));
//    }
//
//    /**
//     * remove a valid manager
//     * invalid owner
//     */
//    @Test
//    void removeValidManagerInvalidOwner() {
//        Assertions.assertFalse(this.sellerOwnerService.removeManager(this.owner.getUserName()+"Not",
//                this.storeDto.getStoreId(), this.oldManager.getUserName()));
//    }
//
//    /**
//     * remove a valid manager
//     * invalid store
//     */
//    @Test
//    void removeValidManagerInvalidStore() {
//        Assertions.assertFalse(this.sellerOwnerService.removeManager(this.owner.getUserName(),
//                this.storeDto.getStoreId()+5, this.oldManager.getUserName()));
//    }
//
//    /**
//     * remove a valid manager
//     * invalid owner
//     * invalid store
//     */
//    @Test
//    void removeValidManagerInvalidOwnerInvalidStore() {
//        Assertions.assertFalse(this.sellerOwnerService.removeManager(this.owner.getUserName()+"Not",
//                this.storeDto.getStoreId()+5, this.oldManager.getUserName()));
//    }
//
//    /**
//     * remove an invalid manager
//     */
//    @Test
//    void removeInvalidManager() {
//        Assertions.assertFalse(this.sellerOwnerService.removeManager(this.owner.getUserName(),
//                this.storeDto.getStoreId(), this.oldManager.getUserName()+"Not"));
//    }
//
//    /**
//     * remove an invalid manager
//     * invalid owner
//     */
//    @Test
//    void removeInvalidManagerInvalidOwner() {
//        Assertions.assertFalse(this.sellerOwnerService.removeManager(this.owner.getUserName()+"Not",
//                this.storeDto.getStoreId(), this.oldManager.getUserName()+"Not"));
//    }
//
//    /**
//     * remove an invalid manager
//     * invalid store
//     */
//    @Test
//    void removeInvalidManagerInvalidStore() {
//        Assertions.assertFalse(this.sellerOwnerService.removeManager(this.owner.getUserName(),
//                this.storeDto.getStoreId()+5, this.oldManager.getUserName()+"Not"));
//    }
//
//    /**
//     * remove an invalid manager
//     * invalid owner
//     * invalid store
//     */
//    @Test
//    void removeInvalidManagerInvalidOwnerInvalidStore() {
//        registerSecondOwnerAndAddAppointAsOwner();
//        Assertions.assertFalse(this.sellerOwnerService.removeManager(this.owner.getUserName()+"Not",
//                this.storeDto.getStoreId()+5, this.oldManager.getUserName()+"Not"));
//    }
//
//    /**
//     * remove a valid manager, by an owner who isn't his appointee
//     */
//    @Test
//    void removeValidManagerByNotAppointee() {
//        registerSecondOwnerAndAddAppointAsOwner();
//        Assertions.assertFalse(this.sellerOwnerService.removeManager(this.ownerNotAppointee.getUserName(),
//                this.storeDto.getStoreId(), this.oldManager.getUserName()));
//    }
//
//    /**
//     * opening a new store and registering its owner
//     */
//    void openStoreAndRegisterOwner(){
//        owner = new UserSystem("owner","name","lname","pass");
//        // registering the owner
//        Assertions.assertTrue(this.guestService.registerUser(owner.getUserName(), owner.getPassword(),
//                owner.getFirstName(), owner.getLastName()));
//
//        // opening a new store, owned by owner
//        Assertions.assertTrue(this.buyerRegisteredService.openStore(owner.getUserName(),
//                new PurchasePolicyDto(), new DiscountPolicyDto(), "storeName", uuid));
//
//        // getting the storeDto of the store the owner opened
//        this.storeDto = this.guestService.getStoresDtos().get(0);
//    }
//
//    /**
//     * register manager and add him as store's manager
//     */
//    private void registerManagerAndAddAsStoreManager() {
//        this.oldManager = new UserSystem("oldManager", "name", "lname", "password");
//        Assertions.assertTrue(this.guestService.registerUser(oldManager.getUserName(), oldManager.getPassword(),
//                oldManager.getFirstName(), oldManager.getLastName()));
//
//        Assertions.assertTrue(this.sellerOwnerService.addManager(this.owner.getUserName(),
//                this.storeDto.getStoreId(), this.oldManager.getUserName()));
//    }
//
//    /**
//     * register manager and add him as store's manager
//     */
//    private void registerSecondOwnerAndAddAppointAsOwner() {
//        this.ownerNotAppointee = new UserSystem("owner not appointee", "name", "lname", "password");
//        Assertions.assertTrue(this.guestService.registerUser(ownerNotAppointee.getUserName(), ownerNotAppointee.getPassword(),
//                ownerNotAppointee.getFirstName(), ownerNotAppointee.getLastName()));
//
//        Assertions.assertTrue(this.sellerOwnerService.addOwner(this.owner.getUserName(),
//                this.storeDto.getStoreId(), this.ownerNotAppointee.getUserName()));
//    }

}

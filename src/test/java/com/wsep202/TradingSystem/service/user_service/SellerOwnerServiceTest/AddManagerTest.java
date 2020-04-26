//package com.wsep202.TradingSystem.service.user_service.SellerOwnerServiceTest;
//
//import com.github.rozidan.springboot.modelmapper.WithModelMapper;
//import com.wsep202.TradingSystem.config.TradingSystemConfiguration;
//import com.wsep202.TradingSystem.domain.trading_system_management.UserSystem;
//import com.wsep202.TradingSystem.service.user_service.BuyerRegisteredService;
//import com.wsep202.TradingSystem.service.user_service.GuestService;
//import com.wsep202.TradingSystem.service.user_service.SellerOwnerService;
//import com.wsep202.TradingSystem.dto.DiscountPolicyDto;
//import com.wsep202.TradingSystem.dto.PurchasePolicyDto;
//import com.wsep202.TradingSystem.dto.StoreDto;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//
//@ExtendWith(SpringExtension.class)
//@ContextConfiguration(classes = {TradingSystemConfiguration.class, GuestService.class, BuyerRegisteredService.class, SellerOwnerService.class})
//@SpringBootTest(args = {"admin","admin"})
//@WithModelMapper
//
//// *********** UC 4.5 - appointing a new store manager ***********
//public class AddManagerTest {
//    @Autowired
//    GuestService guestService;
//    @Autowired
//    BuyerRegisteredService buyerRegisteredService;
//    @Autowired
//    SellerOwnerService sellerOwnerService;
//
//    StoreDto storeDto;
//    UserSystem owner;
//    UserSystem newManager;
//
////    @BeforeEach
////    void setUp() {
////        openStoreAndRegisterOwner();
////        registerUser();
////    }
//
////    @AfterEach
////    void tearDown() {
////        this.sellerOwnerService.clearDS();
////    }
////
////    /**
////     * add a valid manager to a valid store, by an owner that is valid
////     */
////    @Test
////    void addValidManagerRegisteredOwnerValidStore() {
////        Assertions.assertTrue(this.sellerOwnerService.addManager(this.owner.getUserName(),
////                this.storeDto.getStoreId(), this.newManager.getUserName()));
////    }
////
////    /**
////     * add a valid manager to a valid store, by an owner that is invalid
////     */
////    @Test
////    void addValidManagerNotRegisteredOwnerValidStore() {
////        Assertions.assertFalse(this.sellerOwnerService.addManager(this.owner.getUserName()+"Not",
////                this.storeDto.getStoreId(), this.newManager.getUserName()));
////    }
////
////    /**
////     * add a valid manager to an invalid store, by an owner that is valid
////     */
////    @Test
////    void addValidManagerRegisteredOwnerInvalidStore() {
////        Assertions.assertFalse(this.sellerOwnerService.addManager(this.owner.getUserName(),
////                this.storeDto.getStoreId()+5, this.newManager.getUserName()));
////    }
////
////    /**
////     * add an invalid manager to a valid store, by an owner that is valid
////     */
////    @Test
////    void addInvalidManagerRegisteredOwnerValidStore() {
////        Assertions.assertFalse(this.sellerOwnerService.addManager(this.owner.getUserName(),
////                this.storeDto.getStoreId(), this.newManager.getUserName()+"Not"));
////    }
////
////    /**
////     * add an invalid manager to a valid store, by an owner that is invalid
////     */
////    @Test
////    void addInvalidManagerNotRegisteredOwnerValidStore() {
////        Assertions.assertFalse(this.sellerOwnerService.addManager(this.owner.getUserName()+"Not",
////                this.storeDto.getStoreId(), this.newManager.getUserName()+"Not"));
////    }
////
////    /**
////     * add a valid manager to an invalid store, by an owner that is invalid
////     */
////    @Test
////    void addValidManagerNotRegisteredOwnerInvalidStore() {
////        Assertions.assertFalse(this.sellerOwnerService.addManager(this.owner.getUserName()+"Not",
////                this.storeDto.getStoreId()+5, this.newManager.getUserName()));
////    }
////
////    /**
////     * add an invalid manager to an invalid store, by an owner that is valid
////     */
////    @Test
////    void addInvalidManagerRegisteredOwnerInvalidStore() {
////        Assertions.assertFalse(this.sellerOwnerService.addManager(this.owner.getUserName()+"Not",
////                this.storeDto.getStoreId(), this.newManager.getUserName()+"Not"));
////    }
////
////    /**
////     * add an invalid manager to an invalid store, by an owner that is invalid
////     */
////    @Test
////    void addInvalidManagerNotRegisteredOwnerInvalidStore() {
////        Assertions.assertFalse(this.sellerOwnerService.addManager(this.owner.getUserName()+"Not",
////                this.storeDto.getStoreId()+5, this.newManager.getUserName()+"Not"));
////    }
////
////    /**
////     * opening a new store and registering its owner
////     */
////    void openStoreAndRegisterOwner(){
////        owner = new UserSystem("owner","name","lname","pass");
////        // registering the owner
////        Assertions.assertTrue(this.guestService.registerUser(owner.getUserName(), owner.getPassword(),
////                owner.getFirstName(), owner.getLastName()));
////
////        // opening a new store, owned by owner
////        Assertions.assertTrue(this.buyerRegisteredService.openStore(owner.getUserName(),
////                new PurchasePolicyDto(), new DiscountPolicyDto(), "storeName", uuid));
////
////        // getting the storeDto of the store the owner opened
////        this.storeDto = this.guestService.getStoresDtos().get(0);
////    }
////
////    /**
////     * register user into the system
////     */
////    private void registerUser() {
////        this.newManager = new UserSystem("newManager", "name", "lname", "password");
////        Assertions.assertTrue(this.guestService.registerUser(newManager.getUserName(), newManager.getPassword(),
////                newManager.getFirstName(), newManager.getLastName()));
////    }
//}

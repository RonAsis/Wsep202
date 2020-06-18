//package com.wsep202.TradingSystem.service.user_service.SellerOwnerServiceTest;
//
//import com.github.rozidan.springboot.modelmapper.WithModelMapper;
//import com.wsep202.TradingSystem.config.ObjectMapperConfig;
//import com.wsep202.TradingSystem.config.TradingSystemConfiguration;
//import com.wsep202.TradingSystem.dto.ProductDto;
//import com.wsep202.TradingSystem.dto.StoreDto;
//import com.wsep202.TradingSystem.dto.UserSystemDto;
//import com.wsep202.TradingSystem.service.user_service.*;
//import javafx.util.Pair;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.util.UUID;
//
//@ExtendWith(SpringExtension.class)
//@ContextConfiguration(classes = {TradingSystemConfiguration.class, ObjectMapperConfig.class, GuestService.class, BuyerRegisteredService.class, SellerOwnerService.class, SellerManagerService.class})
//@SpringBootTest(args = {"admin","admin"})
//@WithModelMapper
//
//// *********** UC 4.3 - appointing a new store owner ***********
//public class AddOwnerTest {
//    @Autowired
//    GuestService guestService;
//    @Autowired
//    BuyerRegisteredService buyerRegisteredService;
//    @Autowired
//    SellerOwnerService sellerOwnerService;
//    @Autowired
//    SellerManagerService sellerManagerService;
//
//    ServiceTestsHelper helper;
//    UserSystemDto user = new UserSystemDto("username","name","lname");
//    UserSystemDto newOwner = new UserSystemDto("newOwner","name","lname");
//    String userPassword = "password";
//    MultipartFile image = null;
//    UUID uuid;
//    int storeId;
//
//
//    @BeforeEach
//    void setUp() {
//        if (this.helper == null || this.helper.getGuestService() == null ) {
//            this.helper = new ServiceTestsHelper(this.guestService, this.buyerRegisteredService, this.sellerOwnerService);
//        }
//        this.helper.registerUser(this.newOwner.getUserName(), this.userPassword,
//                this.newOwner.getFirstName(), this.newOwner.getLastName(), image);
//        this.helper.registerUser(this.user.getUserName(), this.userPassword,
//                this.user.getFirstName(), this.user.getLastName(), image);
//        Pair<UUID, Boolean> returnedValue = this.helper.loginUser(this.user.getUserName(),
//                this.userPassword);
//        if (returnedValue != null){
//            this.uuid = returnedValue.getKey();
//        }
//        Pair<StoreDto, ProductDto> returnedValueOpen = this.helper.openStoreAndAddProduct(this.user, this.uuid);
//        if (returnedValueOpen != null){
//            this.storeId = returnedValueOpen.getKey().getStoreId();
//        }
//    }
//
//    @AfterEach
//    void tearDown(){
//        this.helper.logoutUser(this.user.getUserName(), this.uuid);
//    }
//
//    /**
//     * add a valid owner to a valid store, by an owner that is valid
//     */
//    @Test
//    void addValidOwnerRegisteredOwnerValidStore() {
//        Assertions.assertTrue(this.sellerOwnerService.addOwner(this.user.getUserName(),
//                this.storeId, this.newOwner.getUserName(), this.uuid));
//    }
//
//    /**
//     * add a valid manager to a valid store, by an owner that is invalid
//     */
//    @Test
//    void addValidManagerNotRegisteredOwnerValidStore() {
//        Assertions.assertThrows(Exception.class, ()-> {
//            this.sellerOwnerService.addManager("NotRegistered",
//                    this.storeId, this.newOwner.getUserName(), this.uuid);
//        });
//    }
//
//    /**
//     * add a valid manager to an invalid store, by an owner that is valid
//     */
//    @Test
//    void addValidManagerRegisteredOwnerInvalidStore() {
//        Assertions.assertNull(this.sellerOwnerService.addManager(this.user.getUserName(),
//                this.storeId + 8, this.newOwner.getUserName(), this.uuid));
//    }
//
//    /**
//     * add an invalid manager to a valid store, by an owner that is valid
//     */
//    @Test
//    void addInvalidManagerRegisteredOwnerValidStore() {
//        Assertions.assertNull(this.sellerOwnerService.addManager(this.user.getUserName(),
//                this.storeId, "NotRegistered", this.uuid));
//    }
//
//    /**
//     * add an invalid manager to a valid store, by an owner that is invalid
//     */
//    @Test
//    void addInvalidManagerNotRegisteredOwnerValidStore() {
//        Assertions.assertThrows(Exception.class, ()-> {
//            this.sellerOwnerService.addManager("NotRegistered",
//                    this.storeId, "NotRegistered2", this.uuid);
//        });
//    }
//
//    /**
//     * add a valid manager to an invalid store, by an owner that is invalid
//     */
//    @Test
//    void addValidManagerNotRegisteredOwnerInvalidStore() {
//        Assertions.assertThrows(Exception.class, ()-> {
//            this.sellerOwnerService.addManager("NotRegistered",
//                    this.storeId + 8, this.newOwner.getUserName(), this.uuid);
//        });
//    }
//
//    /**
//     * add an invalid manager to an invalid store, by an owner that is valid
//     */
//    @Test
//    void addInvalidManagerRegisteredOwnerInvalidStore() {
//        Assertions.assertNull(this.sellerOwnerService.addManager(this.user.getUserName(),
//                this.storeId + 8, this.newOwner.getUserName(), this.uuid));
//    }
//
//    /**
//     * add an invalid manager to an invalid store, by an owner that is invalid
//     */
//    @Test
//    void addInvalidManagerNotRegisteredOwnerInvalidStore() {
//        Assertions.assertThrows(Exception.class, ()-> {
//            this.sellerOwnerService.addManager("NotRegistered",
//                    this.storeId + 8, "NotRegistered2", this.uuid);
//        });
//    }
//}

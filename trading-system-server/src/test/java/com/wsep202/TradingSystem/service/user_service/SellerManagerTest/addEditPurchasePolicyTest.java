//package com.wsep202.TradingSystem.service.user_service.SellerManagerTest;
//
//import com.github.rozidan.springboot.modelmapper.WithModelMapper;
//import com.wsep202.TradingSystem.config.ObjectMapperConfig;
//import com.wsep202.TradingSystem.config.TradingSystemConfiguration;
//import com.wsep202.TradingSystem.service.user_service.*;
//import com.wsep202.TradingSystem.dto.*;
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
//// *********** UC 4.2 (inherited from owner) - adding/ editing the store's purchase's policies ***********
//public class addEditPurchasePolicyTest {
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
//    UserSystemDto owner = new UserSystemDto("username","name","lname");
//    UserSystemDto manager = new UserSystemDto("manager","name","lname");
//    String userPassword = "password";
//    MultipartFile image = null;
//    UUID uuid;
//    private StoreDto storeDto;
//    int counter = 0;
//    private PurchasePolicyDto purchasePolicyDto = new PurchasePolicyDto();
//
//    @BeforeEach
//    void setUp() {
//        if (this.helper == null || this.helper.getGuestService() == null ) {
//            this.helper = new ServiceTestsHelper(this.guestService, this.buyerRegisteredService, this.sellerOwnerService);
//        }
//        this.manager.setUserName(this.manager.getUserName()+counter);
//        this.helper.registerUser(this.manager.getUserName(), this.userPassword,
//                this.manager.getFirstName(), this.manager.getLastName(), image);
//        this.owner.setUserName(this.owner.getUserName()+counter);
//        this.helper.registerUser(this.owner.getUserName(), this.userPassword,
//                this.owner.getFirstName(), this.owner.getLastName(), image);
//        Pair<UUID, Boolean> returnedValueLogin = this.helper.loginUser(this.owner.getUserName(),
//                this.userPassword);
//        if (returnedValueLogin != null){
//            this.uuid = returnedValueLogin.getKey();
//        }
//        StoreDto storeDto = this.helper.openStoreAddProductAndAddManager(this.owner, this.uuid, this.manager.getUserName());
//        if (storeDto != null){
//            this.storeDto = storeDto;
//        }
//        this.helper.logoutUser(this.owner.getUserName(), this.uuid);
//        returnedValueLogin = this.helper.loginUser(this.manager.getUserName(),
//                this.userPassword);
//        if (returnedValueLogin != null){
//            this.uuid = returnedValueLogin.getKey();
//        }
//    }
//
//    @AfterEach
//    void tearDown(){
//        this.helper.logoutUser(this.manager.getUserName(), this.uuid);
//    }
//
//
//    /**
//     * a manager who's not permitted to add policies, adding a new purchase policy
//     */
//    @Test
//    void notPermittedManagerAddingNewPurchasePolicy() {
//        Assertions.assertThrows(Exception.class, () ->
//                {this.sellerManagerService.addEditPurchasePolicy(this.manager.getUserName(),
//                this.storeDto.getStoreId(), this.purchasePolicyDto, this.uuid);}
//                );
//    }
//
//    /**
//     * adding a new purchase policy, invalid store
//     */
//    @Test
//    void AddingNewPurchasePolicyInvalidStore() {
//        Assertions.assertThrows(Exception.class, ()->
//                {this.sellerManagerService.addEditPurchasePolicy(this.manager.getUserName(),
//                        this.storeDto.getStoreId()+5, this.purchasePolicyDto, this.uuid);}
//        );
//    }
//
//    /**
//     * adding a new purchase policy, invalid manager
//     */
//    @Test
//    void AddingNewPurchasePolicyInvalidManager() {
//        Assertions.assertThrows(Exception.class, ()->
//                {this.sellerManagerService.addEditPurchasePolicy(this.manager.getUserName()+"notManager",
//                        this.storeDto.getStoreId(), this.purchasePolicyDto, this.uuid); }
//        );
//    }
//
//    /**
//     * adding a new null purchase policy
//     */
//    @Test
//    void addingNullPurchasePolicy() {
//        Assertions.assertThrows(Exception.class, ()->
//                {this.sellerManagerService.addEditPurchasePolicy(this.manager.getUserName(),
//                this.storeDto.getStoreId(), null, this.uuid); }
//        );
//    }
//
//    /**
//     * adding a new null purchase policy
//     */
//   // @Test
//    void permittedManagerAddingNewPurchasePolicy() {
//        this.helper.logoutUser(this.manager.getUserName(), this.uuid);
//        UUID ownerUuid = this.helper.loginUser(this.owner.getUserName(), this.userPassword).getKey();
//        this.helper.addPermission(this.owner.getUserName(), this.storeDto.getStoreId(), this.manager.getUserName(), "edit purchase policy", ownerUuid);
//        this.purchasePolicyDto.setMax(200);
//
//        this.helper.logoutUser(this.owner.getUserName(), ownerUuid);
//        this.uuid = this.helper.loginUser(this.manager.getUserName(), this.userPassword).getKey();
//
//      Assertions.assertNotNull(this.sellerManagerService.addEditPurchasePolicy(this.manager.getUserName(),
//                this.storeDto.getStoreId(), this.purchasePolicyDto, this.uuid));
//    }
//}

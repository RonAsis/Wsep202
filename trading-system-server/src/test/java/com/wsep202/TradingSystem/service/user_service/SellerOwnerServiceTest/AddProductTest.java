//package com.wsep202.TradingSystem.service.user_service.SellerOwnerServiceTest;
//
//import com.github.rozidan.springboot.modelmapper.WithModelMapper;
//import com.wsep202.TradingSystem.config.ObjectMapperConfig;
//import com.wsep202.TradingSystem.config.TradingSystemConfiguration;
//import com.wsep202.TradingSystem.domain.trading_system_management.UserSystem;
//import com.wsep202.TradingSystem.dto.ProductDto;
//import com.wsep202.TradingSystem.dto.UserSystemDto;
//import com.wsep202.TradingSystem.service.user_service.*;
//import com.wsep202.TradingSystem.dto.PurchasePolicyDto;
//import com.wsep202.TradingSystem.dto.StoreDto;
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
//// *********** UC 4.1.1 - adding products to store ***********
//public class AddProductTest {
//    @Autowired
//    GuestService guestService;
//    @Autowired
//    BuyerRegisteredService buyerRegisteredService;
//    @Autowired
//    SellerOwnerService sellerOwnerService;
//
//    ServiceTestsHelper helper;
//    UserSystemDto user = new UserSystemDto("username","name","lname");
//    UserSystemDto manager = new UserSystemDto("manager","name","lname");
//    String userPassword = "password";
//    MultipartFile image = null;
//    UUID uuid;
//    int storeId;
//    private ProductDto productDto;
//
//    @BeforeEach
//    void setUp() {
//        if (this.helper == null || this.helper.getGuestService() == null ) {
//            this.helper = new ServiceTestsHelper(this.guestService, this.buyerRegisteredService, this.sellerOwnerService);
//        }
//        this.helper.registerUser(this.manager.getUserName(), this.userPassword,
//                this.manager.getFirstName(), this.manager.getLastName(), image);
//        this.helper.registerUser(this.user.getUserName(), this.userPassword,
//                this.user.getFirstName(), this.user.getLastName(), image);
//        Pair<UUID, Boolean> returnedValueLogin = this.helper.loginUser(this.user.getUserName(),
//                this.userPassword);
//        if (returnedValueLogin != null){
//            this.uuid = returnedValueLogin.getKey();
//        }
//        Pair<StoreDto, ProductDto> returnedValueOpen = this.helper.openStoreAndAddProduct(this.user, this.uuid);
//        if (returnedValueOpen != null){
//            this.productDto = returnedValueOpen.getValue();
//            this.storeId = returnedValueOpen.getKey().getStoreId();
//        }
//    }
//
//    @AfterEach
//    void tearDown(){
//        this.helper.logoutUser(this.user.getUserName(), this.uuid);
//    }
//
//
//    /**
//     * add a valid product
//     */
//    @Test
//    void addValidProduct() {
//        Assertions.assertNotNull(this.sellerOwnerService.addProduct(this.user.getUserName(),
//                this.storeId, this.productDto.getName(), this.productDto.getCategory().toLowerCase(), 10, 20, uuid));
//    }
//
//    /**
//     * add a valid product,
//     * negative amount
//     */
//    @Test
//    void addValidProductNegativeAmount() {
//        Assertions.assertNull(this.sellerOwnerService.addProduct(this.user.getUserName(),
//                this.storeId, this.productDto.getName(), this.productDto.getCategory().toLowerCase(), -10, 20, uuid));
//    }
//
//    /**
//     * add a valid product,
//     * negative cost
//     */
//    @Test
//    void addValidProductNegativeCost() {
//        Assertions.assertNull(this.sellerOwnerService.addProduct(this.user.getUserName(),
//                this.storeId, this.productDto.getName(), this.productDto.getCategory().toLowerCase(), 10, -20, uuid));
//    }
//
//    /**
//     * add product
//     * invalid owner
//     */
//    @Test
//    void addProductInvalidOwner() {
//        Assertions.assertThrows(Exception.class, ()-> {
//            this.sellerOwnerService.addProduct(this.user.getUserName()+"Not",
//                    this.storeId, this.productDto.getName()+"new", this.productDto.getCategory().toLowerCase(), 10, 20, uuid);
//        });
//    }
//
//    /**
//     * add product
//     * invalid store
//     */
//    @Test
//    void addValidProductInvalidStore() {
//        Assertions.assertNull(this.sellerOwnerService.addProduct(this.user.getUserName(),
//                this.storeId+5, this.productDto.getName()+"new", this.productDto.getCategory().toLowerCase(), 10, 20, uuid));
//    }
//
//    /**
//     * add product
//     * invalid owner
//     * invalid store
//     */
//    @Test
//    void addProductInvalidOwnerInvalidStore() {
//        Assertions.assertThrows(Exception.class, ()-> {
//            this.sellerOwnerService.addProduct(this.user.getUserName()+"Not",
//                this.storeId+5, this.productDto.getName()+"new", this.productDto.getCategory().toLowerCase(), 10, 20, uuid);
//    });
//}
//
//    /**
//     * add a valid product
//     * invalid category
//     */
//    @Test
//    void addValidProductInvalidCategory() {
//        Assertions.assertNull(this.sellerOwnerService.addProduct(this.user.getUserName(),
//                this.storeId, this.productDto.getName()+"new", this.productDto.getCategory().toLowerCase()+"Not", 10, 20, uuid));
//    }
//
//    /**
//     * add product
//     * invalid owner
//     * invalid category
//     */
//    @Test
//    void addProductInvalidOwnerInvalidCategory() {
//        Assertions.assertThrows(Exception.class, ()-> {
//            this.sellerOwnerService.addProduct(this.user.getUserName()+"Not",
//                    this.storeId, this.productDto.getName()+"new", this.productDto.getCategory().toLowerCase()+"Not", 10, 20, uuid);
//        });
//    }
//
//    /**
//     * add product
//     * invalid store
//     * invalid category
//     */
//    @Test
//    void addValidProductInvalidStoreInvalidCategory() {
//        Assertions.assertNull(this.sellerOwnerService.addProduct(this.user.getUserName(),
//                this.storeId+5, this.productDto.getName()+"new", this.productDto.getCategory().toLowerCase()+5, 10, 20, uuid));
//    }
//
//    /**
//     * add product
//     * invalid owner
//     * invalid store
//     * invalid category
//     */
//    @Test
//    void addProductInvalidOwnerInvalidStoreInvalidCategory() {
//        Assertions.assertThrows(Exception.class, ()-> {
//            this.sellerOwnerService.addProduct(this.user.getUserName() + "Not",
//                    this.storeId + 5, this.productDto.getName() + "new", this.productDto.getCategory().toLowerCase() + 5, 10, 20, uuid);
//        });
//    }
//}

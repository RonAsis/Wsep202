//package com.wsep202.TradingSystem.service.user_service.BuyerRegisteredServiceTest;
//
//import com.github.rozidan.springboot.modelmapper.WithModelMapper;
//import com.wsep202.TradingSystem.config.ObjectMapperConfig;
//import com.wsep202.TradingSystem.config.TradingSystemConfiguration;
//import com.wsep202.TradingSystem.domain.trading_system_management.UserSystem;
//import com.wsep202.TradingSystem.service.user_service.BuyerRegisteredService;
//import com.wsep202.TradingSystem.service.user_service.GuestService;
//import com.wsep202.TradingSystem.service.user_service.SellerOwnerService;
//import com.wsep202.TradingSystem.dto.*;
//import com.wsep202.TradingSystem.service.user_service.ServiceTestsHelper;
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
//
//@ExtendWith(SpringExtension.class)
//@ContextConfiguration(classes = {TradingSystemConfiguration.class, ObjectMapperConfig.class, GuestService.class, BuyerRegisteredService.class, SellerOwnerService.class})
//@SpringBootTest(args = {"admin","admin"})
//@WithModelMapper
//
//// *********** UC 2.7.2 (inherited from guest) - removing a product from shopping bag ***********
//public class RemoveProductInShoppingBagTest {
//    @Autowired
//    GuestService guestService;
//    @Autowired
//    BuyerRegisteredService buyerRegisteredService;
//    @Autowired
//    SellerOwnerService sellerOwnerService;
//    ServiceTestsHelper helper;
//    UserSystemDto user = new UserSystemDto("username","name","lname");
//    String userPassword = "password";
//    MultipartFile image = null;
//    UUID uuid;
//    int storeId = 0;
//
//    @BeforeEach
//    void setUp() {
//        if (this.helper == null || this.helper.getGuestService() == null ) {
//            this.helper = new ServiceTestsHelper(this.guestService, this.buyerRegisteredService, this.sellerOwnerService);
//        }
//        this.helper.registerUser(this.user.getUserName(), this.userPassword,
//                this.user.getFirstName(), this.user.getLastName(), image);
//        Pair<UUID, Boolean> returnedValue = this.helper.loginUser(this.user.getUserName(),
//                this.userPassword);
//        if (returnedValue != null){
//            this.uuid = returnedValue.getKey();
//        }
//    }
//
//    @AfterEach
//    void tearDown(){
//        this.helper.logoutUser(this.user.getUserName(), this.uuid);
//    }
//
//    /**
//     * remove a product from empty shopping bag
//     */
//    @Test
//    void removeProductFromEmptyShoppingBag() {
//        Assertions.assertFalse(this.buyerRegisteredService.removeProductInShoppingBag(this.user.getUserName(),
//                10, 0, this.uuid));
//    }
//
//    /**
//     * remove a product from an empty username's shopping bag
//     */
//    @Test
//    void removeProductEmptyUsername() {
//        Assertions.assertFalse(this.buyerRegisteredService.removeProductInShoppingBag("",
//                0, 0, uuid));
//}
//
//    /**
//     * remove a product from a username who's not registered's shopping bag
//     */
//    @Test
//    void removeProductUserNotRegistered() {
//        Assertions.assertFalse(this.buyerRegisteredService.removeProductInShoppingBag("notRegistered",
//                0, 0, uuid));
//    }
//
//    /**
//     * remove a product that isn't in the user's shopping bag, from a registered user's shopping bag
//     */
//    @Test
//    void removeProductUserRegistered() {
//        Pair<StoreDto, ProductDto> returnedValue = this.helper.createOwnerOpenStoreAddProductAndAddToShoppingCart(this.user.getUserName(), this.uuid);
//        Assertions.assertTrue(this.buyerRegisteredService.removeProductInShoppingBag(this.user.getUserName(),
//                returnedValue.getKey().getStoreId(), returnedValue.getValue().getProductSn(), uuid));
//    }
//}

package com.wsep202.TradingSystem.service.user_service.BuyerRegisteredServiceTest;

import com.github.rozidan.springboot.modelmapper.WithModelMapper;
import com.wsep202.TradingSystem.config.ObjectMapperConfig;
import com.wsep202.TradingSystem.config.TradingSystemConfiguration;
import com.wsep202.TradingSystem.domain.trading_system_management.UserSystem;
import com.wsep202.TradingSystem.service.user_service.BuyerRegisteredService;
import com.wsep202.TradingSystem.service.user_service.GuestService;
import com.wsep202.TradingSystem.service.user_service.SellerOwnerService;
import com.wsep202.TradingSystem.dto.*;
import com.wsep202.TradingSystem.service.user_service.ServiceTestsHelper;
import javafx.util.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TradingSystemConfiguration.class, ObjectMapperConfig.class, GuestService.class, BuyerRegisteredService.class, SellerOwnerService.class})
@SpringBootTest(args = {"admin","admin"})
@WithModelMapper

// *********** UC 2.7.1 (inherited from guest) - watching the shopping cart ***********
public class WatchShoppingCartTest {
    @Autowired
    GuestService guestService;
    @Autowired
    BuyerRegisteredService buyerRegisteredService;
    @Autowired
    SellerOwnerService sellerOwnerService;
    ServiceTestsHelper helper;
    UserSystemDto user = new UserSystemDto("username","name","lname");
    String userPassword = "password";
    MultipartFile image = null;
    UUID uuid;

    @BeforeEach
    void setUp() {
        if (this.helper == null || this.helper.getGuestService() == null ) {
            this.helper = new ServiceTestsHelper(this.guestService, this.buyerRegisteredService, this.sellerOwnerService);
        }
        this.helper.registerUser(this.user.getUserName(), this.userPassword,
                this.user.getFirstName(), this.user.getLastName(), image);
        Pair<UUID, Boolean> returnedValue = this.helper.loginUser(this.user.getUserName(),
                this.userPassword);
        if (returnedValue != null){
            this.uuid = returnedValue.getKey();
        }
    }

    @AfterEach
    void tearDown(){
        this.helper.logoutUser(this.user.getUserName(), this.uuid);
    }

    /**
     * add a valid product in a registered user's shopping bag
     */
    @Test
    void addValidProductRegisteredUser() {
        Pair<StoreDto, ProductDto> returnedValue = this.helper.createOwnerOpenStoreAndAddProduct();
        this.buyerRegisteredService.addProductToShoppingCart(this.user.getUserName(),
                1, returnedValue.getValue(), this.uuid);
        Assertions.assertNotNull(this.buyerRegisteredService.watchShoppingCart(this.user.getUserName(), this.uuid));
    }

    /**
     * view a user's empty shopping cart
     */
    @Test
    void viewEmptyShoppingCart() {
        Assertions.assertEquals(new ShoppingCartDto(new HashMap<>()), this.buyerRegisteredService.watchShoppingCart(this.user.getUserName(), this.uuid));
    }

    /**
     * view an invalid user's shopping cart
     */
    @Test
    void viewShoppingCartInvalidUser() {
        Assertions.assertThrows(Exception.class, ()-> {
                    this.buyerRegisteredService.watchShoppingCart(
                "NotRegistered", this.uuid);
        });
    }

    /**
     * view a valid user's shopping cart
     */
    @Test
    void viewShoppingCartValidUserNotEmptyCart() {
        Assertions.assertThrows(Exception.class, ()-> {
            this.helper.openStoreAndAddProduct(this.user, this.uuid);
            this.buyerRegisteredService.watchShoppingCart(
                    this.user.getUserName(), uuid);
        });
    }

}

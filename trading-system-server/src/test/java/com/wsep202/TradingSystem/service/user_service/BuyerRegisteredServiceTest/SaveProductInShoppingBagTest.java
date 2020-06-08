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

import java.util.UUID;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TradingSystemConfiguration.class, ObjectMapperConfig.class, GuestService.class, BuyerRegisteredService.class, SellerOwnerService.class})
@SpringBootTest(args = {"admin","admin"})
@WithModelMapper

// *********** UC 2.6 (inherited from guest) - saving a product in a shopping bag ***********
public class SaveProductInShoppingBagTest {
    @Autowired
    GuestService guestService;
    @Autowired
    BuyerRegisteredService buyerRegisteredService;
    @Autowired
    SellerOwnerService sellerOwnerService;
    ServiceTestsHelper helper;
    UserSystemDto user = new UserSystemDto("username", "name", "lname");
    String userPassword = "password";
    MultipartFile image = null;
    UUID uuid;
    private StoreDto storeDto;
    private ProductDto productDto;

    @BeforeEach
    void setUp() {
        if (this.helper == null || this.helper.getGuestService() == null) {
            this.helper = new ServiceTestsHelper(this.guestService, this.buyerRegisteredService, this.sellerOwnerService);
        }
        this.helper.registerUser(this.user.getUserName(), this.userPassword,
                this.user.getFirstName(), this.user.getLastName(), image);
        Pair<UUID, Boolean> returnedValueLogin = this.helper.loginUser(this.user.getUserName(),
                this.userPassword);
        if (returnedValueLogin != null) {
            this.uuid = returnedValueLogin.getKey();
        }
        Pair<StoreDto, ProductDto> returnedValueOpen = this.helper.createOwnerOpenStoreAndAddProduct();
        if (returnedValueOpen != null) {
            this.storeDto = returnedValueOpen.getKey();
            this.productDto = returnedValueOpen.getValue();
        }
    }

    @AfterEach
    void tearDown() {
        this.helper.logoutUser(this.user.getUserName(), this.uuid);
    }

    /**
     * save a valid product in a registered user's shopping bag
     */
    @Test
    void saveValidProductRegisteredUser() {
        Assertions.assertTrue(this.buyerRegisteredService.saveProductInShoppingBag(this.user.getUserName(),
                this.storeDto.getStoreId(), this.productDto.getProductSn(), 1, this.uuid));
    }

    /**
     * save a valid product in a not registered user's shopping bag
     */
    @Test
    void saveValidProductNotRegisteredUser() {
        Assertions.assertThrows(Exception.class, ()-> {
            this.buyerRegisteredService.saveProductInShoppingBag("notRegistered",
                this.storeDto.getStoreId(), this.productDto.getProductSn(), 1, this.uuid);
    });
}

    /**
     * save an invalid product in a not registered user's shopping bag
     */
    @Test
    void saveInvalidProductNotRegisteredUser() {
        Assertions.assertFalse(this.buyerRegisteredService.saveProductInShoppingBag("notRegistered",
                this.storeDto.getStoreId(), this.productDto.getProductSn() + 10, 1, this.uuid));
    }


    /**
     * save an invalid product in a registered user's shopping bag
     */
    @Test
    void saveInvalidProductRegisteredUser() {
        Assertions.assertFalse(this.buyerRegisteredService.saveProductInShoppingBag(this.user.getUserName(),
                this.storeDto.getStoreId(), this.productDto.getProductSn() + 10, 1, this.uuid));
    }

    /**
     * save a valid product from invalid store in a registered user's shopping bag
     */
    @Test
    void saveValidProductInvalidStoreRegisteredUser() {
        Assertions.assertFalse(this.buyerRegisteredService.saveProductInShoppingBag(this.user.getUserName(),
                this.storeDto.getStoreId() + 10, this.productDto.getProductSn(), 1, this.uuid));
    }

    /**
     * save an invalid product from invalid store in a registered user's shopping bag
     */
    @Test
    void saveInvalidProductInvalidStoreRegisteredUser() {
        Assertions.assertFalse(this.buyerRegisteredService.saveProductInShoppingBag(this.user.getUserName(),
                this.storeDto.getStoreId() + 10, this.productDto.getProductSn() + 10, 1, this.uuid));
    }

    /**
     * save an invalid product from invalid store in a not registered user's shopping bag
     */
    @Test
    void saveInvalidProductInvalidStoreNotRegisteredUser() {
        Assertions.assertFalse(this.buyerRegisteredService.saveProductInShoppingBag("notRegistered",
                this.storeDto.getStoreId() + 10, this.productDto.getProductSn() + 10, 1, this.uuid));
    }

    /**
     * save a valid product from a valid store in a registered user's shopping bag,
     * negative amount
     */
    @Test
    void saveProductNegativeAmount() {
        Assertions.assertFalse(this.buyerRegisteredService.saveProductInShoppingBag(this.user.getUserName(),
                this.storeDto.getStoreId(), this.productDto.getProductSn(), -1, this.uuid));
    }

    /**
     * save a valid product from a valid store in a registered user's shopping bag,
     * large amount
     */
    @Test
    void saveProductLargeAmount() {
        Assertions.assertFalse(this.buyerRegisteredService.saveProductInShoppingBag(this.user.getUserName(),
                this.storeDto.getStoreId(), this.productDto.getProductSn(), 10000, this.uuid));
    }
}

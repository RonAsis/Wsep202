package com.wsep202.TradingSystem.service.user_service.BuyerRegisteredServiceTest;

import com.github.rozidan.springboot.modelmapper.WithModelMapper;
import com.wsep202.TradingSystem.config.ObjectMapperConfig;
import com.wsep202.TradingSystem.config.TradingSystemConfiguration;
import com.wsep202.TradingSystem.config.httpSecurity.HttpSecurityConfig;
import com.wsep202.TradingSystem.dto.ProductDto;
import com.wsep202.TradingSystem.dto.StoreDto;
import com.wsep202.TradingSystem.dto.UserSystemDto;
import com.wsep202.TradingSystem.service.user_service.BuyerRegisteredService;
import com.wsep202.TradingSystem.service.user_service.GuestService;
import com.wsep202.TradingSystem.service.user_service.SellerOwnerService;
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
@ContextConfiguration(classes = {TradingSystemConfiguration.class, HttpSecurityConfig.class, ObjectMapperConfig.class, GuestService.class, BuyerRegisteredService.class, SellerOwnerService.class})
@SpringBootTest(args = {"admin","admin"})
@WithModelMapper

// *********** UC 2.7.2 (inherited from guest) - editing a product in a shopping bag ***********
public class ChangeProductAmountInShoppingBagTest {
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
    private ProductDto productDto;
    private int storeId;

    @BeforeEach
    void setUp() {
        if (this.helper == null || this.helper.getGuestService() == null ) {
            this.helper = new ServiceTestsHelper(this.guestService, this.buyerRegisteredService, this.sellerOwnerService);
        }
        this.helper.registerUser(this.user.getUserName(), this.userPassword,
                this.user.getFirstName(), this.user.getLastName(), image);
        Pair<UUID, Boolean> returnedValueLogin = this.helper.loginUser(this.user.getUserName(),
                this.userPassword);
        if (returnedValueLogin != null) {
            this.uuid = returnedValueLogin.getKey();
        }
        Pair<Integer, ProductDto> returnedValue = this.helper.createOwnerOpenStoreAddProductAndAddToShoppingCart(this.user.getUserName(), this.uuid);
        if (returnedValueLogin != null) {
            this.productDto = returnedValue.getValue();
            this.storeId = returnedValue.getKey();
        }
    }

    @AfterEach
    void tearDown(){
        this.helper.logoutUser(this.user.getUserName(), this.uuid);
    }

    /**
     * change a valid product's amount in a registered user's shopping bag
     */
    @Test
    void changeAmountValidProductRegisteredUser() {
        Assertions.assertTrue(this.buyerRegisteredService.changeProductAmountInShoppingBag(this.user.getUserName(),
                this.storeId, 2, this.productDto.getProductSn(), this.uuid));
    }

    /**
     * change a valid product's amount in an unregistered user's shopping bag
     */
    @Test
    void changeAmountValidProductUnregisteredUser() {
        Assertions.assertThrows(Exception.class, ()-> {
            this.buyerRegisteredService.changeProductAmountInShoppingBag("NotRegistered",
                this.storeId, 2, this.productDto.getProductSn(), this.uuid);
    });
}

    /**
     * change an invalid product's amount in an unregistered user's shopping bag
     */
    @Test
    void changeAmountInvalidProductUnregisteredUser() {
        Assertions.assertThrows(Exception.class, ()-> {
                    this.buyerRegisteredService.changeProductAmountInShoppingBag("NotRegistered",
                this.storeId, 2, this.productDto.getProductSn() + 10, this.uuid);
        });
    }

    /**
     * change an invalid product's amount in an unregistered user's shopping bag, negative amount
     */
    @Test
    void changeAmountInvalidProductUnregisteredUserNegativeAmount() {
        Assertions.assertThrows(Exception.class, ()-> {
                    this.buyerRegisteredService.changeProductAmountInShoppingBag("NotRegistered",
                    this.storeId, -2, this.productDto.getProductSn()+10, this.uuid);
        });
    }

    /**
     * change an invalid product's amount in an unregistered user's shopping bag, negative amount
     */
    @Test
    void changeAmounValidProductRegisteredUserNegativeAmount() {
        Assertions.assertFalse(this.buyerRegisteredService.changeProductAmountInShoppingBag(this.user.getUserName(),
                this.storeId, -2, this.productDto.getProductSn(), this.uuid));
    }
}

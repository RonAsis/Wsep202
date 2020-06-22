package com.wsep202.TradingSystem.service.user_service.SellerOwnerServiceTest;

import com.github.rozidan.springboot.modelmapper.WithModelMapper;
import com.wsep202.TradingSystem.config.ObjectMapperConfig;
import com.wsep202.TradingSystem.config.TradingSystemConfiguration;
import com.wsep202.TradingSystem.config.httpSecurity.HttpSecurityConfig;
import com.wsep202.TradingSystem.domain.trading_system_management.UserSystem;
import com.wsep202.TradingSystem.service.user_service.*;
import com.wsep202.TradingSystem.dto.*;
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

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TradingSystemConfiguration.class, HttpSecurityConfig.class, ObjectMapperConfig.class, GuestService.class, BuyerRegisteredService.class, SellerOwnerService.class, SellerManagerService.class})
@SpringBootTest(args = {"admin","admin"})
@WithModelMapper

// *********** UC 4.10 - viewing store's history purchase ***********
public class ViewPurchaseHistoryTest {
    @Autowired
    GuestService guestService;
    @Autowired
    BuyerRegisteredService buyerRegisteredService;
    @Autowired
    SellerOwnerService sellerOwnerService;

    ServiceTestsHelper helper;
    UserSystemDto user = new UserSystemDto("username","name","lname");
    UserSystemDto manager = new UserSystemDto("manager","name","lname");
    String userPassword = "password";
    MultipartFile image = null;
    UUID uuid;
    int storeId = 0;

    @BeforeEach
    void setUp() {
        if (this.helper == null || this.helper.getGuestService() == null ) {
            this.helper = new ServiceTestsHelper(this.guestService, this.buyerRegisteredService, this.sellerOwnerService);
        }
        this.helper.registerUser(this.manager.getUserName(), this.userPassword,
                this.manager.getFirstName(), this.manager.getLastName(), image);
        this.helper.registerUser(this.user.getUserName(), this.userPassword,
                this.user.getFirstName(), this.user.getLastName(), image);
        Pair<UUID, Boolean> returnedValue = this.helper.loginUser(this.user.getUserName(),
                this.userPassword);
        if (returnedValue != null){
            this.uuid = returnedValue.getKey();
        }
        Pair<Integer, ProductDto> returnedValueOpen = this.helper.openStoreAndAddProduct(this.user, this.uuid);
        if (returnedValueOpen != null){
            this.storeId = returnedValueOpen.getKey();
        }
    }

    @AfterEach
    void tearDown(){
        this.helper.logoutUser(this.user.getUserName(), this.uuid);
    }


    /**
     * view the history purchase of a valid store
     * no purchases
     */
    @Test
    void viewHistoryNoPurchases() {
        List<ReceiptDto> returnedHistory = this.sellerOwnerService.viewPurchaseHistoryOfStoreOwner(
                this.user.getUserName(), this.storeId, this.uuid);
        Assertions.assertEquals(new LinkedList<>(), returnedHistory);
    }

    /**
     * view the history purchase of a valid store
     * invalid owner
     */
    @Test
    void viewHistoryNoPurchasesInvalidOwner() {
        Assertions.assertThrows(Exception.class, ()-> {
            this.sellerOwnerService.viewPurchaseHistoryOfStoreOwner(
                    this.user.getUserName()+"Not", this.storeId, this.uuid);
        });
    }

    /**
     * view the history purchase of an invalid store
     */
    @Test
    void viewHistoryNoPurchasesInvalidStore() {
        Assertions.assertNull(this.sellerOwnerService.viewPurchaseHistoryOfStoreOwner(
                this.user.getUserName(), this.storeId+5, this.uuid));
    }

    /**
     * view the history purchase of an invalid store
     * invalid owner
     */
    @Test
    void viewHistoryNoPurchasesInvalidStoreInvalidOwner() {
        Assertions.assertThrows(Exception.class, ()-> {
            this.sellerOwnerService.viewPurchaseHistoryOfStoreOwner(
                    this.user.getUserName()+"Not", this.storeId+5, this.uuid);
        });
    }

    /**
     * view the history purchase of a valid store
     */
    @Test
    void viewHistoryPurchases() {
        this.helper.createOwnerOpenStoreAddProductAddAndPurchaseShoppingCart(this.user.getUserName(), this.uuid);
        Assertions.assertNotNull(this.sellerOwnerService.viewPurchaseHistoryOfStoreOwner(
                this.user.getUserName(), this.storeId, this.uuid));
    }
}

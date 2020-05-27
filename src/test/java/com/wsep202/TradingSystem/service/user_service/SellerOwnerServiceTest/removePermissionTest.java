package com.wsep202.TradingSystem.service.user_service.SellerOwnerServiceTest;

import com.github.rozidan.springboot.modelmapper.WithModelMapper;
import com.wsep202.TradingSystem.config.ObjectMapperConfig;
import com.wsep202.TradingSystem.config.TradingSystemConfiguration;
import com.wsep202.TradingSystem.dto.StoreDto;
import com.wsep202.TradingSystem.dto.UserSystemDto;
import com.wsep202.TradingSystem.service.user_service.*;
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
@ContextConfiguration(classes = {TradingSystemConfiguration.class, ObjectMapperConfig.class, GuestService.class, BuyerRegisteredService.class, SellerOwnerService.class, SellerManagerService.class})
@SpringBootTest(args = {"admin","admin"})
@WithModelMapper

// *********** UC 4.6 - removing a permission from a manager ***********
public class removePermissionTest {
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
    int storeId;

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
        StoreDto returnedValueOpen = this.helper.openStoreAddProductAndAddManager(this.user, this.uuid, this.manager.getUserName());
        if (returnedValueOpen != null){
            this.storeId = returnedValueOpen.getStoreId();
        }
    }

    @AfterEach
    void tearDown(){
        this.sellerOwnerService.removeManager(this.user.getUserName(),
                this.storeId, this.manager.getUserName(), this.uuid);
        this.helper.logoutUser(this.user.getUserName(), this.uuid);
    }

    /**
     * remove a valid permission, that is basic
     */
    @Test
    void removeBasicPermission() {
        Assertions.assertTrue(this.sellerOwnerService.removePermission(this.user.getUserName(), this.storeId,
                this.manager.getUserName(), "view",this.uuid));
    }

    /**
     * remove a valid permission, that is basic
     */
    @Test
    void removeValidPermissionInvalidStore() {
        Assertions.assertFalse(this.sellerOwnerService.removePermission(this.user.getUserName(), this.storeId+5,
                this.manager.getUserName(), "view",this.uuid));
    }

    /**
     * remove a valid permission
     */
    @Test
    void removeNotAddedPermission() {
        Assertions.assertFalse(this.sellerOwnerService.removePermission(this.user.getUserName(), this.storeId,
                this.manager.getUserName(), "edit",this.uuid));
    }

    /**
     * remove invalid permission
     */
    @Test
    void removeInvalidPermission() {
        Assertions.assertFalse(this.sellerOwnerService.removePermission(this.user.getUserName(), this.storeId,
                this.manager.getUserName(), "NotPermission",this.uuid));
    }

    /**
     * remove invalid permission, invalid manager
     */
    @Test
    void removeValidPermissionInvalidManager() {
        Assertions.assertFalse(this.sellerOwnerService.removePermission(this.user.getUserName(), this.storeId,
                "NotManager", "NotPermission",this.uuid));
    }

    /**
     * remove invalid permission
     */
    @Test
    void removeInvalidPermissionInvalidOwner() {
        Assertions.assertThrows(Exception.class, ()-> {
            this.sellerOwnerService.removePermission("NotOwner", this.storeId,
                    this.manager.getUserName(), "NotPermission",this.uuid);
        });
    }
}

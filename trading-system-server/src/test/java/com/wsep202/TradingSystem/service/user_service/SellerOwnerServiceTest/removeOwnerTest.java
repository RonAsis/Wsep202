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

// *********** UC 4.4 - removing a store owner ***********
public class removeOwnerTest {
    @Autowired
    GuestService guestService;
    @Autowired
    BuyerRegisteredService buyerRegisteredService;
    @Autowired
    SellerOwnerService sellerOwnerService;
    @Autowired
    SellerManagerService sellerManagerService;

    ServiceTestsHelper helper;
    UserSystemDto user = new UserSystemDto("username", "name", "lname");
    UserSystemDto oldOwner = new UserSystemDto("oldOwner", "name", "lname");
    String userPassword = "password";
    MultipartFile image = null;
    UUID uuid;
    int storeId;


    @BeforeEach
    void setUp() {
        if (this.helper == null || this.helper.getGuestService() == null) {
            this.helper = new ServiceTestsHelper(this.guestService, this.buyerRegisteredService, this.sellerOwnerService);
        }
        this.helper.registerUser(this.oldOwner.getUserName(), this.userPassword,
                this.oldOwner.getFirstName(), this.oldOwner.getLastName(), image);
        this.helper.registerUser(this.user.getUserName(), this.userPassword,
                this.user.getFirstName(), this.user.getLastName(), image);
        Pair<UUID, Boolean> returnedValue = this.helper.loginUser(this.user.getUserName(),
                this.userPassword);
        if (returnedValue != null) {
            this.uuid = returnedValue.getKey();
        }
        StoreDto returnedValueOpen = this.helper.openStoreAddProductAndAddOwner(this.user, this.uuid, this.oldOwner.getUserName());
        if (returnedValueOpen != null) {
            this.storeId = returnedValueOpen.getStoreId();
        }
    }

    @AfterEach
    void tearDown() {
        this.sellerOwnerService.removeOwner(this.user.getUserName(),
                this.storeId, this.oldOwner.getUserName(), this.uuid);
        this.helper.logoutUser(this.user.getUserName(), this.uuid);
    }

    /**
     * remove a valid owner
     */
    @Test
    void removeValidOwner() {
        Assertions.assertTrue(this.sellerOwnerService.removeOwner(this.user.getUserName(),
                this.storeId, this.oldOwner.getUserName(), this.uuid));
    }

    /**
     * remove a valid owner
     * invalid owner
     */
    @Test
    void removeValidOwnerInvalidOwner() {
        Assertions.assertThrows(Exception.class, ()-> {
            this.sellerOwnerService.removeOwner("NotOwner",
                    this.storeId, this.oldOwner.getUserName(), this.uuid);
        });
    }

    /**
     * remove a valid owner
     * invalid store
     */
    @Test
    void removeValidOwnerInvalidStore() {
        Assertions.assertFalse(this.sellerOwnerService.removeOwner(this.user.getUserName(),
                8, this.oldOwner.getUserName(), this.uuid));
    }

    /**
     * remove a valid owner
     * invalid owner
     * invalid store
     */
    @Test
    void removeValidOwnerInvalidOwnerInvalidStore() {
        Assertions.assertThrows(Exception.class, () -> {
            this.sellerOwnerService.removeOwner("NotOwner",
                    8, this.oldOwner.getUserName(), this.uuid);
        });
    }

    /**
     * remove an invalid owner
     */
    @Test
    void removeInvalidOwner() {
        Assertions.assertFalse(this.sellerOwnerService.removeOwner(this.user.getUserName(),
                this.storeId, "NotOwner", this.uuid));
    }

    /**
     * remove an invalid owner
     * invalid owner
     */
    @Test
    void removeInvalidOwnerInvalidOwner() {
        Assertions.assertThrows(Exception.class, () -> {
            this.sellerOwnerService.removeOwner("NotOwner",
                    this.storeId, "NotOwner", this.uuid);
        });
    }

    /**
     * remove an invalid owner
     * invalid store
     */
    @Test
    void removeInvalidOwnerInvalidStore() {
        Assertions.assertFalse(this.sellerOwnerService.removeOwner(this.user.getUserName(),
                8, "NotOwner", this.uuid));
    }

    /**
     * remove a valid owner by an owner who's not his appointee
     */
    @Test
    void removeOwnerNotAppointee() {
        Assertions.assertThrows(Exception.class, () -> {
            this.helper.registerAndLoginUser("newUser", "newUserr", "newUser", "newUser", null);
            this.helper.addStoreOwner(this.user.getUserName(), this.storeId, "newUser", this.uuid);
            this.sellerOwnerService.addPermission("newUser", this.storeId,
                    this.oldOwner.getUserName(), "edit", this.uuid);
        });
    }
}


package com.wsep202.TradingSystem.service.user_service.SellerOwnerServiceTest;

import com.github.rozidan.springboot.modelmapper.WithModelMapper;
import com.wsep202.TradingSystem.config.ObjectMapperConfig;
import com.wsep202.TradingSystem.config.TradingSystemConfiguration;
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
// *********** UC 4.7 - removing a store manager ***********
public class RemoveManagerTest {
    @Autowired
    GuestService guestService;
    @Autowired
    BuyerRegisteredService buyerRegisteredService;
    @Autowired
    SellerOwnerService sellerOwnerService;
    @Autowired
    SellerManagerService sellerManagerService;

    ServiceTestsHelper helper;
    UserSystemDto user = new UserSystemDto("username","name","lname");
    UserSystemDto oldManager = new UserSystemDto("oldManager","name","lname");
    String userPassword = "password";
    MultipartFile image = null;
    UUID uuid;
    int storeId = 0;


    @BeforeEach
    void setUp() {
        if (this.helper == null || this.helper.getGuestService() == null ) {
            this.helper = new ServiceTestsHelper(this.guestService, this.buyerRegisteredService, this.sellerOwnerService);
        }
        this.helper.registerUser(this.oldManager.getUserName(), this.userPassword,
                this.oldManager.getFirstName(), this.oldManager.getLastName(), image);
        this.helper.registerUser(this.user.getUserName(), this.userPassword,
                this.user.getFirstName(), this.user.getLastName(), image);
        Pair<UUID, Boolean> returnedValue = this.helper.loginUser(this.user.getUserName(),
                this.userPassword);
        if (returnedValue != null){
            this.uuid = returnedValue.getKey();
        }
        this.helper.openStoreAddProductsAndAddManager(this.user, this.uuid, this.userPassword, this.oldManager.getUserName());
    }

    @AfterEach
    void tearDown(){
        this.sellerOwnerService.removeManager(this.user.getUserName(),
                this.storeId, this.oldManager.getUserName(), this.uuid);
        this.helper.logoutUser(this.user.getUserName(), this.uuid);
    }

    /**
     * remove a valid manager
     */
    @Test
    void removeValidManager() {
        Assertions.assertTrue(this.sellerOwnerService.removeManager(this.user.getUserName(),
                this.storeId, this.oldManager.getUserName(), this.uuid));
    }

    /**
     * remove a valid manager
     * invalid owner
     */
    @Test
    void removeValidManagerInvalidOwner() {
        try{
            Assertions.assertFalse(this.sellerOwnerService.removeManager("NotOwner",
                    this.storeId, this.oldManager.getUserName(), this.uuid));
        } catch (Exception e) {

        }
    }

    /**
     * remove a valid manager
     * invalid store
     */
    @Test
    void removeValidManagerInvalidStore() {
        Assertions.assertFalse(this.sellerOwnerService.removeManager(this.user.getUserName(),
                8, this.oldManager.getUserName(), this.uuid));
    }

    /**
     * remove a valid manager
     * invalid owner
     * invalid store
     */
    @Test
    void removeValidManagerInvalidOwnerInvalidStore() {
        try {
            Assertions.assertFalse(this.sellerOwnerService.removeManager("NotOwner",
                    8, this.oldManager.getUserName(), this.uuid));
        } catch (Exception e) {

        }
    }

    /**
     * remove an invalid manager
     */
    @Test
    void removeInvalidManager() {
        Assertions.assertFalse(this.sellerOwnerService.removeManager(this.user.getUserName(),
                this.storeId, "NotManager", this.uuid));
    }

    /**
     * remove an invalid manager
     * invalid owner
     */
    @Test
    void removeInvalidManagerInvalidOwner() {
        try{
            Assertions.assertFalse(this.sellerOwnerService.removeManager("NotOwner",
                    this.storeId, "NotManager", this.uuid));
        } catch (Exception e) {

        }
    }

    /**
     * remove an invalid manager
     * invalid store
     */
    @Test
    void removeInvalidManagerInvalidStore() {
        Assertions.assertFalse(this.sellerOwnerService.removeManager(this.user.getUserName(),
                8, "NotManager", this.uuid));
    }
}

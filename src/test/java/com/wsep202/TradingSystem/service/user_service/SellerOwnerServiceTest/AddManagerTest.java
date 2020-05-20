package com.wsep202.TradingSystem.service.user_service.SellerOwnerServiceTest;

import com.github.rozidan.springboot.modelmapper.WithModelMapper;
import com.wsep202.TradingSystem.config.ObjectMapperConfig;
import com.wsep202.TradingSystem.config.TradingSystemConfiguration;
import com.wsep202.TradingSystem.domain.trading_system_management.UserSystem;
import com.wsep202.TradingSystem.dto.UserSystemDto;
import com.wsep202.TradingSystem.service.user_service.*;
import com.wsep202.TradingSystem.dto.StoreDto;
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

// *********** UC 4.5 - appointing a new store manager ***********
public class AddManagerTest {
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
    UserSystemDto newManager = new UserSystemDto("newManager","name","lname");
    String userPassword = "password";
    MultipartFile image = null;
    UUID uuid;
    int storeId = 0;


    @BeforeEach
    void setUp() {
        if (this.helper == null || this.helper.getGuestService() == null ) {
            this.helper = new ServiceTestsHelper(this.guestService, this.buyerRegisteredService, this.sellerOwnerService);
        }
        this.helper.registerUser(this.newManager.getUserName(), this.userPassword,
                this.newManager.getFirstName(), this.newManager.getLastName(), image);
        this.helper.registerUser(this.user.getUserName(), this.userPassword,
                this.user.getFirstName(), this.user.getLastName(), image);
        Pair<UUID, Boolean> returnedValue = this.helper.loginUser(this.user.getUserName(),
                this.userPassword);
        if (returnedValue != null){
            this.uuid = returnedValue.getKey();
        }
        this.helper.openStoreAndAddProducts(this.user, this.userPassword, this.uuid);
    }

    @AfterEach
    void tearDown(){
        this.helper.logoutUser(this.user.getUserName(), this.uuid);
    }

    /**
     * add a valid manager to a valid store, by an owner that is valid
     */
    @Test
    void addValidManagerRegisteredOwnerValidStore() {
        try{
            Assertions.assertNotNull(this.sellerOwnerService.addManager(this.user.getUserName(),
                    this.storeId, this.newManager.getUserName(), this.uuid));
        } catch (Exception e) {

        }
    }

    /**
     * add a valid manager to a valid store, by an owner that is invalid
     */
    @Test
    void addValidManagerNotRegisteredOwnerValidStore() {
        try{
            Assertions.assertNull(this.sellerOwnerService.addManager("NotRegistered",
                    this.storeId, this.newManager.getUserName(), this.uuid));
        } catch (Exception e) {

        }
    }

    /**
     * add a valid manager to an invalid store, by an owner that is valid
     */
    @Test
    void addValidManagerRegisteredOwnerInvalidStore() {
        try{
            Assertions.assertNull(this.sellerOwnerService.addManager(this.user.getUserName(),
                    8, this.newManager.getUserName(), this.uuid));
        } catch (Exception e) {

        }
    }

    /**
     * add an invalid manager to a valid store, by an owner that is valid
     */
    @Test
    void addInvalidManagerRegisteredOwnerValidStore() {
        try{
            Assertions.assertNull(this.sellerOwnerService.addManager(this.user.getUserName(),
                    this.storeId, "NotRegistered", this.uuid));
        } catch (Exception e) {

        }
    }

    /**
     * add an invalid manager to a valid store, by an owner that is invalid
     */
    @Test
    void addInvalidManagerNotRegisteredOwnerValidStore() {
        try{
            Assertions.assertNull(this.sellerOwnerService.addManager("NotRegistered",
                    this.storeId, "NotRegistered2", this.uuid));
        } catch (Exception e) {

        }
    }

    /**
     * add a valid manager to an invalid store, by an owner that is invalid
     */
    @Test
    void addValidManagerNotRegisteredOwnerInvalidStore() {
        try{
            Assertions.assertNull(this.sellerOwnerService.addManager("NotRegistered",
                    8, this.newManager.getUserName(), this.uuid));
        } catch (Exception e) {

        }
    }

    /**
     * add an invalid manager to an invalid store, by an owner that is valid
     */
    @Test
    void addInvalidManagerRegisteredOwnerInvalidStore() {
        try {
            Assertions.assertNull(this.sellerOwnerService.addManager(this.user.getUserName(),
                    8, this.newManager.getUserName(), this.uuid));
        } catch (Exception e) {

        }
    }

    /**
     * add an invalid manager to an invalid store, by an owner that is invalid
     */
    @Test
    void addInvalidManagerNotRegisteredOwnerInvalidStore() {
        try{
            Assertions.assertNull(this.sellerOwnerService.addManager("NotRegistered",
                    8, "NotRegistered2", this.uuid));
        } catch (Exception e) {

        }
    }
}

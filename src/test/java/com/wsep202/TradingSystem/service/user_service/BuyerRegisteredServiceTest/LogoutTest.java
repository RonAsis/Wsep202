package com.wsep202.TradingSystem.service.user_service.BuyerRegisteredServiceTest;

import com.github.rozidan.springboot.modelmapper.WithModelMapper;
import com.wsep202.TradingSystem.config.ObjectMapperConfig;
import com.wsep202.TradingSystem.config.TradingSystemConfiguration;
import com.wsep202.TradingSystem.dto.UserSystemDto;
import com.wsep202.TradingSystem.service.user_service.BuyerRegisteredService;
import com.wsep202.TradingSystem.service.user_service.GuestService;
import com.wsep202.TradingSystem.service.user_service.ServiceTestsHelper;
import javafx.util.Pair;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TradingSystemConfiguration.class, ObjectMapperConfig.class, GuestService.class, BuyerRegisteredService.class})
@SpringBootTest(args = {"admin","admin"})
@WithModelMapper

// *********** UC 3.1 - logging out from the system ***********
public class LogoutTest {
    @Autowired
    GuestService guestService;
    @Autowired
    BuyerRegisteredService buyerRegisteredService;
    ServiceTestsHelper helper;
    UserSystemDto user = new UserSystemDto("username","name","lname");
    String userPassword = "password";
    MultipartFile image = null;
    UUID uuid;

    @BeforeEach
    void setUp() {
        if (this.helper == null || this.helper.getGuestService() == null ){
            this.helper = new ServiceTestsHelper(this.guestService, this.buyerRegisteredService);
        }
        this.helper.registerUser(this.user.getUserName(), this.userPassword,
                this.user.getFirstName(), this.user.getLastName(), image);
        Pair<UUID, Boolean> returnedValue = this.helper.loginUser(this.user.getUserName(),
                this.userPassword);
        if (returnedValue != null) {
            this.uuid = returnedValue.getKey();
        }
    }

    /**
     * logout a registered user.
     */
    @Test
    void logoutRegisteredUser() {
        Assertions.assertTrue(this.buyerRegisteredService.logout(
                this.user.getUserName(), this.uuid));
    }

    /**
     * logout a registered user with null uuid.
     */
    @Test
    void logoutRegisteredUserNullUUID() {
        try{
            Assertions.assertFalse(this.buyerRegisteredService.logout(
                    this.user.getUserName(), null));
        } catch (Exception e){

        }
    }
    /**
     * logout a user that isn't registered.
     */
    @Test
    void logoutNotRegisteredUser() {
        Assertions.assertFalse(this.buyerRegisteredService.logout(
                this.user.getUserName()+"NotRegistered", this.uuid));  }

    /**
     * logout a user that isn't registered, using the empty string.
     */
    @Test
    void logoutNotRegisteredUserEmptyUsername() {
        try {
            Assertions.assertFalse(this.buyerRegisteredService.logout("", this.uuid));
        } catch (Exception e){

        }
    }
}
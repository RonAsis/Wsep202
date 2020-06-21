package com.wsep202.TradingSystem.service.user_service.GuestServiceTest;

import com.github.rozidan.springboot.modelmapper.WithModelMapper;
import com.wsep202.TradingSystem.config.ObjectMapperConfig;
import com.wsep202.TradingSystem.config.TradingSystemConfiguration;
import com.wsep202.TradingSystem.config.httpSecurity.HttpSecurityConfig;
import com.wsep202.TradingSystem.dto.UserSystemDto;
import com.wsep202.TradingSystem.service.user_service.BuyerRegisteredService;
import com.wsep202.TradingSystem.service.user_service.GuestService;
import com.wsep202.TradingSystem.service.user_service.ServiceTestsHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TradingSystemConfiguration.class, HttpSecurityConfig.class, ObjectMapperConfig.class, GuestService.class, BuyerRegisteredService.class})
@SpringBootTest(args = {"admin","admin"})
@WithModelMapper

// *********** UC 2.2 - guest registration ***********
class RegisterTest {
    @Autowired
    GuestService guestService;
    @Autowired
    BuyerRegisteredService buyerRegisteredService;
    ServiceTestsHelper helper;
    UserSystemDto user = new UserSystemDto("usernamee","name","lname");
    String userPassword = "password";
    MultipartFile image = null;

    @BeforeEach
    void setUp() {
        if (this.helper == null || this.helper.getGuestService() == null ){
            this.helper = new ServiceTestsHelper(this.guestService, this.buyerRegisteredService);
        }
    }

    /**
     * Registering a user with an unused-valid username and details.
     */
    @Test
    void validUser() {
        Assertions.assertTrue(this.guestService.registerUser(this.user.getUserName(), this.userPassword,
                this.user.getFirstName(), this.user.getLastName(), image));
    }

    /**
     * Registering a user with a used username.
     */
    @Test
    void usedUsername() {
        this.helper.registerUser(this.user.getUserName()+"used", this.userPassword,
                this.user.getFirstName(), this.user.getLastName(), image); // registering the user
        Assertions.assertFalse(this.guestService.registerUser(this.user.getUserName()+"used", this.userPassword,
                this.user.getFirstName(), this.user.getLastName(), image)); // try to register the user again
    }

    /**
     * Registering a user with a null username.
     */
    @Test
    void nullUsername() {
        Assertions.assertFalse(this.guestService.registerUser(null, this.userPassword,
                this.user.getFirstName(), this.user.getLastName(), image));
    }

    /**
     * Registering a user with a null password.
     */
    @Test
    void nullPassword() {
        Assertions.assertFalse(this.guestService.registerUser(this.user.getUserName(), null,
                this.user.getFirstName(), this.user.getLastName(), image));
    }

    /**
     * Registering a user with a null first name.
     */
    @Test
    void nullFirstName() {
        Assertions.assertFalse(this.guestService.registerUser(this.user.getUserName(), this.userPassword,
                null, this.user.getLastName(), image));
    }

    /**
     * Registering a user with a null last name.
     */
    @Test
    void nullLastName() {
        Assertions.assertFalse(this.guestService.registerUser(this.user.getUserName(), this.userPassword,
                this.user.getFirstName(), null, image));
    }

    /**
     * Registering a user with an empty username.
     */
    @Test
    void emptyUsername() {
        Assertions.assertFalse(this.guestService.registerUser("", this.userPassword,
                this.user.getFirstName(), this.user.getLastName(), image));
    }

    /**
     * Registering a user with an empty password.
     */
    @Test
    void emptyPassword() {
        Assertions.assertFalse(this.guestService.registerUser(this.user.getUserName(), "",
                this.user.getFirstName(), this.user.getLastName(), image));
    }

    /**
     * Registering a user with an empty first name.
     */
    @Test
    void emptyFirstName() {
        Assertions.assertFalse(this.guestService.registerUser(this.user.getUserName(), this.userPassword,
                "", this.user.getLastName(), image));
    }

    /**
     * Registering a user with an empty last name.
     */
    @Test
    void emptyLastName() {
        Assertions.assertFalse(this.guestService.registerUser(this.user.getUserName(), this.userPassword,
                this.user.getFirstName(), "", image));
    }

    /**
     * Registering a user with a password that is too short
     */
    @Test
    void shortPassword() {
        Assertions.assertFalse(this.guestService.registerUser(this.user.getUserName(), "1234",
                this.user.getFirstName(), this.user.getLastName(), image));
    }
}

package com.wsep202.TradingSystem.service.user_service.GuestServiceTest;

import com.github.rozidan.springboot.modelmapper.WithModelMapper;
import com.wsep202.TradingSystem.domain.config.TradingSystemConfiguration;
import com.wsep202.TradingSystem.domain.trading_system_management.UserSystem;
import com.wsep202.TradingSystem.service.user_service.GuestService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TradingSystemConfiguration.class, GuestService.class})
@SpringBootTest(args = {"admin","admin"})
@WithModelMapper

// *********** UC 2.3 - guest login ***********
public class LoginTest {
    @Autowired
    GuestService guestService;
    UserSystem userSystem;

    @BeforeEach
    void setUp() {
        userSystem = new UserSystem("username","name","lname","pass");
    }

    @AfterEach
    void tearDown() {
        this.guestService.clearDS();
    }

    /**
     * log a registered user in to the system.
     */
    @Test
    void loginRegisteredUser() {
        registerUser();
        Assertions.assertTrue(this.guestService.login(userSystem.getUserName(), userSystem.getPassword()));
    }

    /**
     * log a registered user with invalid password in to the system.
     */
    @Test
    void loginRegisteredUserInValidPassword() {
        registerUser();
        Assertions.assertFalse(this.guestService.login(userSystem.getUserName(), userSystem.getPassword()+"invalid"));
    }

    /**
     * log a registered user with invalid username in to the system.
     */
    @Test
    void loginRegisteredUserInValidUsername() {
        registerUser();
        Assertions.assertFalse(this.guestService.login(userSystem.getUserName()+"invalid", userSystem.getPassword()));
    }

    /**
     * log a registered user with null username in to the system.
     */
    @Test
    void loginRegisteredUserNullUsername() {
        registerUser();
        Assertions.assertFalse(this.guestService.login(null, userSystem.getPassword()));
    }

    /**
     * log a registered user with null password in to the system.
     */
    @Test
    void loginRegisteredUserNullPassword() {
        registerUser();
        Assertions.assertFalse(this.guestService.login(userSystem.getUserName(), null));
    }
    /**
     * log a registered user with empty username in to the system.
     */
    @Test
    void loginRegisteredUserEmptyUsername() {
        registerUser();
        Assertions.assertFalse(this.guestService.login("", userSystem.getPassword()));
    }

    /**
     * log a registered user with empty password in to the system.
     */
    @Test
    void loginRegisteredUserEmptyPassword() {
        registerUser();
        Assertions.assertFalse(this.guestService.login(userSystem.getUserName(), ""));
    }

    /**
     * log an unregistered user in to the system.
     */
    @Test
    void loginUnregisteredUser() {
        Assertions.assertFalse(this.guestService.login(userSystem.getUserName(), userSystem.getPassword()));
    }

    /**
     * log an unregistered user with null username in to the system.
     */
    @Test
    void loginUnregisteredUserNullUsername() {
        Assertions.assertFalse(this.guestService.login(null, userSystem.getPassword()));
    }

    /**
     * log an unregistered user with null password in to the system.
     */
    @Test
    void loginUnregisteredUserNullPassword() {
        Assertions.assertFalse(this.guestService.login(userSystem.getUserName(), null));
    }
    /**
     * log an unregistered user with empty username in to the system.
     */
    @Test
    void loginUnregisteredUserEmptyUsername() {
        Assertions.assertFalse(this.guestService.login("", userSystem.getPassword()));
    }

    /**
     * log an unregistered user with empty password in to the system.
     */
    @Test
    void loginUnregisteredUserEmptyPassword() {
        Assertions.assertFalse(this.guestService.login(userSystem.getUserName(), ""));
    }

    /**
     * register user into the system
     */
    private void registerUser() {
        Assertions.assertTrue(this.guestService.registerUser(userSystem.getUserName(), userSystem.getPassword(),
                userSystem.getFirstName(), userSystem.getLastName()));
    }
}
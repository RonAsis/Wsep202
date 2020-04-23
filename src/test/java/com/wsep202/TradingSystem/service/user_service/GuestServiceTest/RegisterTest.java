package com.wsep202.TradingSystem.service.user_service.GuestServiceTest;

import com.github.rozidan.springboot.modelmapper.WithModelMapper;
import com.wsep202.TradingSystem.config.TradingSystemConfiguration;
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

// *********** UC 2.2 - guest registration ***********
class RegisterTest {
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
     * Registering a user with an unused-valid username and details.
     */
    @Test
    void validUser() {
        Assertions.assertTrue(guestService.registerUser(userSystem.getUserName(),  userSystem.getPassword(),
                userSystem.getFirstName(), userSystem.getLastName()));
    }

    /**
     * Registering a user with a used username.
     */
    @Test
    void usedUsername() {
        registerUser(); // register the user
        Assertions.assertFalse(guestService.registerUser(userSystem.getUserName(),  userSystem.getPassword(),
                userSystem.getFirstName(), userSystem.getLastName())); // try to register the user again
    }

    /**
     * Registering a user with a null username.
     */
    @Test
    void nullUsername() {
        Assertions.assertFalse(guestService.registerUser(null,  userSystem.getPassword(),
                userSystem.getFirstName(), userSystem.getLastName()));
    }

    /**
     * Registering a user with a null password.
     */
    @Test
    void nullPassword() {
        Assertions.assertFalse(guestService.registerUser(userSystem.getUserName(), null,
                userSystem.getFirstName(), userSystem.getLastName()));
    }

    /**
     * Registering a user with a null first name.
     */
    @Test
    void nullFirstName() {
        Assertions.assertFalse(guestService.registerUser(userSystem.getUserName(), userSystem.getPassword(),
                null, userSystem.getLastName()));
    }

    /**
     * Registering a user with a null last name.
     */
    @Test
    void nullLastName() {
        Assertions.assertFalse(guestService.registerUser(userSystem.getUserName(), userSystem.getPassword(),
                userSystem.getFirstName(), null));
    }

    /**
     * Registering a user with an empty username.
     */
    @Test
    void emptyUsername() {
        Assertions.assertFalse(guestService.registerUser("", userSystem.getFirstName(),
                userSystem.getLastName(), userSystem.getPassword()));
    }

    /**
     * Registering a user with an empty password.
     */
    @Test
    void emptyPassword() {
        Assertions.assertFalse(guestService.registerUser(userSystem.getUserName(), "",
                userSystem.getFirstName(), userSystem.getLastName()));
    }

    /**
     * Registering a user with an empty first name.
     */
    @Test
    void emptyFirstName() {
        Assertions.assertFalse(guestService.registerUser(userSystem.getUserName(), userSystem.getPassword(),
                "", userSystem.getLastName()));
    }

    /**
     * Registering a user with an empty last name.
     */
    @Test
    void emptyLastName() {
        Assertions.assertFalse(guestService.registerUser(userSystem.getUserName(), userSystem.getPassword(),
                userSystem.getFirstName(), ""));
    }

    /**
     * register user into the system
     */
    private void registerUser() {
        this.guestService.registerUser(userSystem.getUserName(), userSystem.getPassword(),
                userSystem.getFirstName(), userSystem.getLastName());
    }
}
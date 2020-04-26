package com.wsep202.TradingSystem.service.user_service.BuyerRegisteredServiceTest;

import com.github.rozidan.springboot.modelmapper.WithModelMapper;
import com.wsep202.TradingSystem.config.TradingSystemConfiguration;
import com.wsep202.TradingSystem.domain.trading_system_management.UserSystem;
import com.wsep202.TradingSystem.service.user_service.BuyerRegisteredService;
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
@ContextConfiguration(classes = {TradingSystemConfiguration.class, GuestService.class, BuyerRegisteredService.class})
@SpringBootTest(args = {"admin","admin"})
@WithModelMapper

// *********** UC 3.1 - logging out from the system ***********
public class LogoutTest {
    @Autowired
    GuestService guestService;
    @Autowired
    BuyerRegisteredService buyerRegisteredService;

    UserSystem userSystem;

    @BeforeEach
    void setUp() {
        userSystem = new UserSystem("username", "name", "lname", "pass");
    }

//    @AfterEach
//    void tearDown() {
//        this.buyerRegisteredService.clearDS();
//    }
//
//    /**
//     * logout a registered user.
//     */
//    @Test
//    void logoutRegisteredUser() {
//        registerUser();
//        Assertions.assertTrue(this.buyerRegisteredService.logout(this.userSystem.getUserName(), uuid));
//    }
//
//    /**
//     * logout a user that isn't registered.
//     */
//    @Test
//    void logoutNotRegisteredUser() {
//        registerUser();
//        Assertions.assertFalse(this.buyerRegisteredService.logout(this.userSystem.getUserName()+"not", uuid));
//    }
//
//    /**
//     * logout a user that isn't registered, using the empty string.
//     */
//    @Test
//    void logoutNotRegisteredUserEmptyUsername() {
//        Assertions.assertFalse(this.buyerRegisteredService.logout("", uuid));
//    }
//
//    /**
//     * register user into the system
//     */
//    private void registerUser() {
//        this.guestService.registerUser(userSystem.getUserName(), userSystem.getPassword(),
//                userSystem.getFirstName(), userSystem.getLastName());
//    }

}
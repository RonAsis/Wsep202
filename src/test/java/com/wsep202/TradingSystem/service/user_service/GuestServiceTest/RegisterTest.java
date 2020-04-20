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

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TradingSystemConfiguration.class, GuestService.class})
@SpringBootTest(args = {"admin","admin"})
@WithModelMapper
class RegisterTest {
    @Autowired
    GuestService guestService;
    UserSystem noyUser;
    @BeforeEach
    void setUp() {
        noyUser = new UserSystem("username","noy","asis","pass");

    }

    @AfterEach
    void tearDown() {
        this.guestService.clearDS();
    }


    //////////////UC 2.2////////////////////
    @Test
    void registerUserPositive() {
        Assertions.assertTrue(guestService.registerUser(noyUser.getUserName(),noyUser.getFirstName()
                ,noyUser.getLastName(),noyUser.getPassword()));
    }

    @Test
    void alreadyRegisterUserNegative() {
        registerSetup();
        Assertions.assertFalse(guestService.registerUser(noyUser.getUserName(),noyUser.getFirstName()
                ,noyUser.getLastName(),noyUser.getLastName()));
    }

    @Test
    void NullUsernameRegistrationNegative() {

        Assertions.assertFalse(guestService.registerUser(null,noyUser.getFirstName()
                ,noyUser.getLastName(),noyUser.getPassword()));
    }

    @Test
    void NullPasswordRegistrationNegative() {
        Assertions.assertFalse(guestService.registerUser(noyUser.getUserName(),noyUser.getFirstName()
                ,noyUser.getLastName(),null));
    }

    @Test
    void emptyUsernameRegistrationNegative() {

        Assertions.assertFalse(guestService.registerUser("",noyUser.getFirstName()
                ,noyUser.getLastName(),noyUser.getPassword()));
    }

    @Test
    void emptyPasswordRegistrationNegative() {
        Assertions.assertFalse(guestService.registerUser(noyUser.getUserName(),noyUser.getFirstName()
                ,noyUser.getLastName(),""));
    }

    /**
     * register user into the system
     */
    private void registerSetup() {
        this.guestService.registerUser(noyUser.getUserName(),noyUser.getPassword()
                ,noyUser.getFirstName(),noyUser.getLastName());
    }

    @Test
    void login() {
        registerSetup();
        Assertions.assertTrue(this.guestService.login(noyUser.getUserName(),noyUser.getPassword()));
    }

    @Test
    void viewStoreInfo() {
    }

    @Test
    void viewProduct() {
    }

    @Test
    void searchProductByName() {
    }

    @Test
    void searchProductByCategory() {
    }

    @Test
    void searchProductByKeyWords() {
    }

    @Test
    void filterByRangePrice() {
    }

    @Test
    void filterByProductRank() {
    }

    @Test
    void filterByStoreRank() {
    }

    @Test
    void filterByStoreCategory() {
    }

    @Test
    void purchaseShoppingCart() {
    }
}
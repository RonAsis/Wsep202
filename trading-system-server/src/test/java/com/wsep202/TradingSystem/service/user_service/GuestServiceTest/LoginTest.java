package com.wsep202.TradingSystem.service.user_service.GuestServiceTest;

import com.github.rozidan.springboot.modelmapper.WithModelMapper;
import com.wsep202.TradingSystem.config.ObjectMapperConfig;
import com.wsep202.TradingSystem.config.TradingSystemConfiguration;
import com.wsep202.TradingSystem.config.httpSecurity.HttpSecurityConfig;
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
@ContextConfiguration(classes = {TradingSystemConfiguration.class, HttpSecurityConfig.class, ObjectMapperConfig.class, GuestService.class, BuyerRegisteredService.class})
@SpringBootTest(args = {"admin","admin"})
@WithModelMapper

// *********** UC 2.3 - guest login ***********
public class LoginTest {
    @Autowired
    GuestService guestService;
    @Autowired
    BuyerRegisteredService buyerRegisteredService;
    ServiceTestsHelper helper;
    UserSystemDto user = new UserSystemDto("username","name","lname");
    String userPassword = "password";
    MultipartFile image = null;
    int counter = 0;

    @BeforeEach
    void setUp() {
        if (this.helper == null || this.helper.getGuestService() == null ){
            this.helper = new ServiceTestsHelper(this.guestService, this.buyerRegisteredService);
        }
        this.user.setUserName(this.user.getUserName()+this.counter);
        this.helper.registerUser(this.user.getUserName(), this.userPassword,
                this.user.getFirstName(), this.user.getLastName(), image);
    }

    /**
     * log a registered user in to the system.
     */
    @Test
    void loginRegisteredUser() {
        Pair<UUID, Boolean> returnedValue = this.guestService.login(this.user.getUserName(),
                this.userPassword);
        Assertions.assertNotNull(returnedValue);
        this.helper.logoutUser(this.user.getUserName(), returnedValue.getKey());
    }

    /**
     * log a registered user with invalid password in to the system.
     */
    @Test
    void loginRegisteredUserInvalidPassword() {
        Pair<UUID, Boolean> returnedValue = this.guestService.login(this.user.getUserName(),
                this.userPassword+"invalid");
        Assertions.assertNull(returnedValue);
    }

    /**
     * log a registered user with invalid username in to the system.
     */
    @Test
    void loginRegisteredUserInValidUsername() {
        Pair<UUID, Boolean> returnedValue = this.guestService.login(this.user.getUserName()+"invalid",
                this.userPassword);
        Assertions.assertNull(returnedValue);
    }

    /**
     * log a registered user with null username in to the system.
     */
    @Test
    void loginRegisteredUserNullUsername() {
        Pair<UUID, Boolean> returnedValue = this.guestService.login(null,
                this.userPassword);
        Assertions.assertNull(returnedValue);
    }

    /**
     * log a registered user with null password in to the system.
     */
    @Test
    void loginRegisteredUserNullPassword() {
        Assertions.assertThrows(Exception.class, ()-> {
            this.guestService.login(this.user.getUserName(),
                    null);
        });
    }

    /**
     * log a registered user with empty username in to the system.
     */
    @Test
    void loginRegisteredUserEmptyUsername() {
        Pair<UUID, Boolean> returnedValue = this.guestService.login("",
                this.userPassword);
        Assertions.assertNull(returnedValue);
    }

    /**
     * log a registered user with empty password in to the system.
     */
    @Test
    void loginRegisteredUserEmptyPassword() {
        Pair<UUID, Boolean> returnedValue = this.guestService.login(this.user.getUserName(),
                "");
        Assertions.assertNull(returnedValue);
    }
}

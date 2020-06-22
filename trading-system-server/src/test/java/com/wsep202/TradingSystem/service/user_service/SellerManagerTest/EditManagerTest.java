package com.wsep202.TradingSystem.service.user_service.SellerManagerTest;

import com.github.rozidan.springboot.modelmapper.WithModelMapper;
import com.wsep202.TradingSystem.config.ObjectMapperConfig;
import com.wsep202.TradingSystem.config.TradingSystemConfiguration;
import com.wsep202.TradingSystem.config.httpSecurity.HttpSecurityConfig;
import com.wsep202.TradingSystem.dto.PurchasePolicyDto;
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
@ContextConfiguration(classes = {TradingSystemConfiguration.class, HttpSecurityConfig.class, ObjectMapperConfig.class, GuestService.class, BuyerRegisteredService.class, SellerOwnerService.class, SellerManagerService.class})
@SpringBootTest(args = {"admin","admin"})
@WithModelMapper

public class EditManagerTest {
    @Autowired
    GuestService guestService;
    @Autowired
    BuyerRegisteredService buyerRegisteredService;
    @Autowired
    SellerOwnerService sellerOwnerService;
    @Autowired
    SellerManagerService sellerManagerService;

    ServiceTestsHelper helper;
    UserSystemDto owner = new UserSystemDto("username","name","lname");
    UserSystemDto manager = new UserSystemDto("manager","name","lname");
    String userPassword = "password";
    MultipartFile image = null;
    UUID uuid;
    private Integer storeId;
    int counter = 0;
    private PurchasePolicyDto purchasePolicyDto = new PurchasePolicyDto();

    @BeforeEach
    void setUp() {
        if (this.helper == null || this.helper.getGuestService() == null ) {
            this.helper = new ServiceTestsHelper(this.guestService, this.buyerRegisteredService, this.sellerOwnerService);
        }
        this.manager.setUserName(this.manager.getUserName()+counter);
        this.helper.registerUser(this.manager.getUserName(), this.userPassword,
                this.manager.getFirstName(), this.manager.getLastName(), image);
        this.owner.setUserName(this.owner.getUserName()+counter);
        this.helper.registerUser(this.owner.getUserName(), this.userPassword,
                this.owner.getFirstName(), this.owner.getLastName(), image);
        Pair<UUID, Boolean> returnedValueLogin = this.helper.loginUser(this.owner.getUserName(),
                this.userPassword);
        if (returnedValueLogin != null){
            this.uuid = returnedValueLogin.getKey();
        }
        Integer storeId = this.helper.openStoreAddProductAndAddManager(this.owner, this.uuid, this.manager.getUserName());
        if (storeId != null){
            this.storeId = storeId;
        }
        this.helper.logoutUser(this.owner.getUserName(), this.uuid);
        returnedValueLogin = this.helper.loginUser(this.manager.getUserName(),
                this.userPassword);
        if (returnedValueLogin != null){
            this.uuid = returnedValueLogin.getKey();
        }
    }

    @AfterEach
    void tearDown(){
        this.helper.logoutUser(this.manager.getUserName(), this.uuid);
    }


    /**
     * a manager who's not permitted to edit managers
     */
    @Test
    void notPermittedManagerEditingManager() {
//        this.sellerManagerService.editManager()
    }

}

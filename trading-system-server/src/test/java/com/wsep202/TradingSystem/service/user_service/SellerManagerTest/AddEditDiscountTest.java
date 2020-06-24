package com.wsep202.TradingSystem.service.user_service.SellerManagerTest;

import com.github.rozidan.springboot.modelmapper.WithModelMapper;
import com.wsep202.TradingSystem.config.ObjectMapperConfig;
import com.wsep202.TradingSystem.config.TradingSystemConfiguration;
import com.wsep202.TradingSystem.config.httpSecurity.HttpSecurityConfig;
import com.wsep202.TradingSystem.service.user_service.*;
import com.wsep202.TradingSystem.dto.*;
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

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TradingSystemConfiguration.class, HttpSecurityConfig.class, ObjectMapperConfig.class, GuestService.class, BuyerRegisteredService.class, SellerOwnerService.class, SellerManagerService.class})
@SpringBootTest(args = {"admin","admin"})
@WithModelMapper

// *********** UC 4.2 (inherited from owner) - adding/ editing the store's discounts ***********
public class AddEditDiscountTest {
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
    UserSystemDto manager = new UserSystemDto("manager","name","lname");
    String userPassword = "password";
    MultipartFile image = null;
    UUID uuid;
    private Integer storeId;
    private ProductDto productDto;
    int counter = 0;
    DiscountDto discountDto;

    @BeforeEach
    void setUp() {
        if (this.helper == null || this.helper.getGuestService() == null ) {
            this.helper = new ServiceTestsHelper(this.guestService, this.buyerRegisteredService, this.sellerOwnerService);
        }
        this.manager.setUserName(this.manager.getUserName()+counter);
        this.helper.registerUser(this.manager.getUserName(), this.userPassword,
                this.manager.getFirstName(), this.manager.getLastName(), image);
        this.user.setUserName(this.user.getUserName()+counter);
        this.helper.registerUser(this.user.getUserName(), this.userPassword,
                this.user.getFirstName(), this.user.getLastName(), image);
        this.uuid = this.helper.loginUser(this.user.getUserName(), this.userPassword).getKey();
        this.storeId = this.helper.openStoreAddProductAndAddManager(this.user, this.uuid, this.manager.getUserName());
        this.helper.logoutUser(this.user.getUserName(), this.uuid);
        this.uuid = this.helper.loginUser(this.manager.getUserName(), this.userPassword).getKey();
        this.discountDto = new DiscountDto();
        this.discountDto.setMinPrice(20);
    }

    @AfterEach
    void tearDown(){
        this.helper.logoutUser(this.manager.getUserName(), this.uuid);
    }


    /**
     * add discount
     */
    @Test
    void addDiscountPermittedManager() {
        this.helper.logoutUser(this.manager.getUserName(), this.uuid);
        this.uuid = this.helper.loginUser(this.user.getUserName(), this.userPassword).getKey();
        this.helper.addPermission(this.user.getUserName(), this.storeId, this.manager.getUserName(), "edit discount", this.uuid);
        this.helper.logoutUser(this.user.getUserName(), this.uuid);

        this.uuid = this.helper.loginUser(this.manager.getUserName(), this.userPassword).getKey();
        this.discountDto.setDiscountId(-1);
        Assertions.assertNotNull(this.sellerManagerService.addEditDiscount
                (this.manager.getUserName(), this.storeId, this.discountDto, this.uuid)); ;
    }

    /**
     * add discount
     * not permitted manager
     */
    @Test
    void addDiscountNotPermittedManager() {
        Assertions.assertThrows(Exception.class, ()-> {
            this.sellerManagerService.addEditDiscount(this.manager.getUserName(), this.storeId, this.discountDto, this.uuid);
        });
    }

    /**
     * add discount
     * invalid user
     */
    @Test
    void addDiscountInvalidManager() {
        Assertions.assertThrows(Exception.class, ()-> {
            this.sellerManagerService.addEditDiscount(this.manager.getUserName()+"Not", this.storeId, this.discountDto, this.uuid);
        });
    }


    /**
     * add discount
     * invalid store
     */
    @Test
    void addDiscountInvalidStore() {
        Assertions.assertThrows(Exception.class, ()-> {
            this.sellerManagerService.addEditDiscount(this.manager.getUserName(), this.storeId+15, this.discountDto, this.uuid);
        });
    }

    /**
     * add discount
     * invalid user
     */
    @Test
    void addDiscountNullDiscount() {
        Assertions.assertThrows(Exception.class, ()-> {
            this.sellerManagerService.addEditDiscount(this.manager.getUserName(), this.storeId, null, this.uuid);
        });
    }
}

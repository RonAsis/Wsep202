package com.wsep202.TradingSystem.service.user_service.SellerManagerTest;

import com.github.rozidan.springboot.modelmapper.WithModelMapper;
import com.wsep202.TradingSystem.config.ObjectMapperConfig;
import com.wsep202.TradingSystem.config.TradingSystemConfiguration;
import com.wsep202.TradingSystem.config.httpSecurity.HttpSecurityConfig;
import com.wsep202.TradingSystem.service.user_service.*;
import com.wsep202.TradingSystem.dto.*;
import io.swagger.models.auth.In;
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

// *********** UC 4.2 (inherited from owner) - removing a discount ***********
public class RemoveDiscountTest {
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
     * remove discount
     */
    @Test
    void removeDiscountPermittedManager() {
        this.helper.logoutUser(this.manager.getUserName(), this.uuid);
        this.uuid = this.helper.loginUser(this.user.getUserName(), this.userPassword).getKey();
        this.helper.addPermission(this.user.getUserName(), this.storeId, this.manager.getUserName(), "edit discount", this.uuid);
        this.helper.logoutUser(this.user.getUserName(), this.uuid);

        this.uuid = this.helper.loginUser(this.manager.getUserName(), this.userPassword).getKey();
        this.discountDto = this.sellerManagerService.addEditDiscount(this.manager.getUserName(), this.storeId, this.discountDto, this.uuid);
        Assertions.assertTrue(this.sellerManagerService.removeDiscount(
                this.manager.getUserName(), this.storeId, (int) this.discountDto.getDiscountId(), this.uuid)); ;
    }

    /**
     * remove discount
     * not permitted manager
     */
    @Test
    void removeDiscountNotPermittedManager() {
        Assertions.assertThrows(Exception.class, ()-> {
            this.sellerManagerService.addEditDiscount(this.manager.getUserName(), this.storeId, this.discountDto, this.uuid);
        });
    }

    /**
     * remove discount
     * invalid user
     */
    @Test
    void removeDiscountInvalidManager() {
        Assertions.assertThrows(Exception.class, ()-> {
            this.sellerManagerService.removeDiscount(this.manager.getUserName()+"Not", this.storeId, (int) this.discountDto.getDiscountId(), this.uuid);
        });
    }


    /**
     * remove discount
     * invalid store
     */
    @Test
    void removeDiscountInvalidStore() {
        Assertions.assertThrows(Exception.class, ()-> {
            this.sellerManagerService.removeDiscount(this.manager.getUserName(), this.storeId+15, (int) this.discountDto.getDiscountId(), this.uuid);
        });
    }
}

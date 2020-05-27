package com.wsep202.TradingSystem.service.user_service.SellerManagerTest;

import com.github.rozidan.springboot.modelmapper.WithModelMapper;
import com.wsep202.TradingSystem.config.ObjectMapperConfig;
import com.wsep202.TradingSystem.config.TradingSystemConfiguration;
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
@ContextConfiguration(classes = {TradingSystemConfiguration.class, ObjectMapperConfig.class, GuestService.class, BuyerRegisteredService.class, SellerOwnerService.class, SellerManagerService.class})
@SpringBootTest(args = {"admin","admin"})
@WithModelMapper

// *********** UC 5.1.1 - viewing purchase history ***********
public class ViewPurchaseHistoryTest {
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
    private StoreDto storeDto;
    private ProductDto productDto;
    int counter = 0;

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
        Pair<UUID, Boolean> returnedValueLogin = this.helper.loginUser(this.user.getUserName(),
                this.userPassword);
        if (returnedValueLogin != null){
            this.uuid = returnedValueLogin.getKey();
        }
        Pair<StoreDto, ProductDto> returnedValueOpen = this.helper.openStoreAndAddProduct(this.user, this.uuid);
        if (returnedValueOpen != null){
            this.storeDto = returnedValueOpen.getKey();
            this.productDto = returnedValueOpen.getValue();
        }
    }

    @AfterEach
    void tearDown(){
        this.helper.logoutUser(this.user.getUserName(), this.uuid);
    }


    /**
     * view the history purchase of a valid store
     * no purchases
     */
    @Test
    void ViewHistoryNoPurchases() {
        List<ReceiptDto> returnedHistory = this.sellerManagerService.viewPurchaseHistoryOfManager(
                this.user.getUserName(), this.storeDto.getStoreId(), this.uuid);
        Assertions.assertNull(returnedHistory);
    }

    /**
     * view the history purchase of a valid store
     * invalid owner
     */
    @Test
    void ViewHistoryNoPurchasesInvalidOwner() {
        Assertions.assertThrows(Exception.class, ()-> {
            this.sellerManagerService.viewPurchaseHistoryOfManager(
                    this.user.getUserName()+"Not", this.storeDto.getStoreId(), this.uuid);
        });
    }

    /**
     * view the history purchase of an invalid store
     */
    @Test
    void ViewHistoryNoPurchasesInvalidStore() {
        Assertions.assertNull(this.sellerManagerService.viewPurchaseHistoryOfManager(
                this.user.getUserName(), this.storeDto.getStoreId()+5, this.uuid));
    }

    /**
     * view the history purchase of an invalid store
     * invalid owner
     */
    @Test
    void ViewHistoryNoPurchasesInvalidStoreInvalidOwner() {
        Assertions.assertThrows(Exception.class, ()-> {
            this.sellerManagerService.viewPurchaseHistoryOfManager(
                    this.user.getUserName()+"Not", this.storeDto.getStoreId()+5, this.uuid);
        });
    }

    /**
     * view the history purchase of a valid store
     */
    @Test
    void ViewHistoryPurchases() {
        this.helper.addProductToShoppingCartAndPurchase(this.user.getUserName(), this.storeDto.getStoreId(), this.productDto.getProductSn(), this.uuid);
        Assertions.assertNotNull(this.sellerManagerService.viewPurchaseHistoryOfManager(
                this.user.getUserName(), this.storeDto.getStoreId(), this.uuid));
    }
}

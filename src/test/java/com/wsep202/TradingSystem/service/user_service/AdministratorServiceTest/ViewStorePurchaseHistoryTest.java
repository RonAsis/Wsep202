package com.wsep202.TradingSystem.service.user_service.AdministratorServiceTest;

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
@ContextConfiguration(classes = {TradingSystemConfiguration.class, ObjectMapperConfig.class, GuestService.class, BuyerRegisteredService.class, SellerOwnerService.class, AdministratorService.class})
@SpringBootTest(args = {"admin","admin"})
@WithModelMapper

// *********** UC 6.4.1 - viewing store's purchase history ***********
public class ViewStorePurchaseHistoryTest {
    @Autowired
    GuestService guestService;
    @Autowired
    BuyerRegisteredService buyerRegisteredService;
    @Autowired
    SellerOwnerService sellerOwnerService;
    @Autowired
    AdministratorService administratorService;
    ServiceTestsHelper helper;

    int storeId = 0;
    String adminUsername = "admin";
    String adminPassword = "admin";
    UUID uuid;

    @BeforeEach
    void setUp() {
        if (this.helper == null || this.helper.getGuestService() == null ) {
            this.helper = new ServiceTestsHelper(this.guestService, this.buyerRegisteredService, this.sellerOwnerService);
        }
        Pair<UUID, Boolean> returnedValue = this.helper.loginUser(this.adminUsername,
                this.adminPassword);
        if (returnedValue != null){
            this.uuid = returnedValue.getKey();
        }
    }

    @AfterEach
    void tearDown(){
        this.helper.logoutUser(this.adminUsername, this.uuid);
    }


    /**
     * view the history purchase of a valid store
     * no purchases
     */
    @Test
    void ViewHistoryNoPurchases() {
        this.helper.openStoreAndAddProducts();
        List<ReceiptDto> returnedHistory = this.administratorService.viewPurchaseHistory(
                this.adminUsername, this.storeId, this.uuid);
        Assertions.assertEquals(new LinkedList<>(), returnedHistory);
    }

    /**
     * view the history purchase of a valid store
     * invalid admin
     */
    @Test
    void ViewHistoryNoPurchasesInvalidAdmin() {
        Assertions.assertNull(this.administratorService.viewPurchaseHistory(
                "NotAdmin", this.storeId, this.uuid));
    }

    /**
     * view the history purchase of an invalid store
     */
    @Test
    void ViewHistoryNoPurchasesInvalidStore() {
        Assertions.assertNull(this.administratorService.viewPurchaseHistory(
                this.adminUsername, 8, this.uuid));
    }

    /**
     * view the history purchase of an invalid store
     * invalid admin
     */
    @Test
    void ViewHistoryNoPurchasesInvalidStoreInvalidAdmin() {
        Assertions.assertNull(this.administratorService.viewPurchaseHistory(
                "NotAdmin", 8, this.uuid));
    }

    /**
     * view the history purchase of a valid store
     */
    @Test
    void ViewHistoryPurchases() {
        this.helper.openStoreAddProductsAndPurchaseShoppingCart(this.adminUsername, this.uuid);
        Assertions.assertNotNull(this.administratorService.viewPurchaseHistory(
                this.adminUsername, this.storeId, this.uuid));
    }
}
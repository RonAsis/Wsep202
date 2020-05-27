package com.wsep202.TradingSystem.service.user_service.SellerOwnerServiceTest;

import com.github.rozidan.springboot.modelmapper.WithModelMapper;
import com.wsep202.TradingSystem.config.ObjectMapperConfig;
import com.wsep202.TradingSystem.config.TradingSystemConfiguration;
import com.wsep202.TradingSystem.dto.StoreDto;
import com.wsep202.TradingSystem.dto.UserSystemDto;
import com.wsep202.TradingSystem.service.user_service.*;
import com.wsep202.TradingSystem.dto.ProductDto;
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
@ContextConfiguration(classes = {TradingSystemConfiguration.class, ObjectMapperConfig.class, GuestService.class, BuyerRegisteredService.class, SellerOwnerService.class, SellerManagerService.class})
@SpringBootTest(args = {"admin","admin"})
@WithModelMapper
// *********** UC 4.1.2 - removing a product ***********
public class RemoveProductTest {
    @Autowired
    GuestService guestService;
    @Autowired
    BuyerRegisteredService buyerRegisteredService;
    @Autowired
    SellerOwnerService sellerOwnerService;

    ServiceTestsHelper helper;
    UserSystemDto user = new UserSystemDto("username","name","lname");
    UserSystemDto manager = new UserSystemDto("manager","name","lname");
    String userPassword = "password";
    MultipartFile image = null;
    UUID uuid;
    int storeId;
    private ProductDto productDto;

    @BeforeEach
    void setUp() {
        if (this.helper == null || this.helper.getGuestService() == null ) {
            this.helper = new ServiceTestsHelper(this.guestService, this.buyerRegisteredService, this.sellerOwnerService);
        }
        this.helper.registerUser(this.manager.getUserName(), this.userPassword,
                this.manager.getFirstName(), this.manager.getLastName(), image);
        this.helper.registerUser(this.user.getUserName(), this.userPassword,
                this.user.getFirstName(), this.user.getLastName(), image);
        Pair<UUID, Boolean> returnedValue = this.helper.loginUser(this.user.getUserName(),
                this.userPassword);
        if (returnedValue != null){
            this.uuid = returnedValue.getKey();
        }
        Pair<StoreDto, ProductDto> returnedValueOpen = this.helper.openStoreAndAddProduct(this.user, this.uuid);
        if (returnedValueOpen != null){
            this.productDto = returnedValueOpen.getValue();
            this.storeId = returnedValueOpen.getKey().getStoreId();
        }    }

    @AfterEach
    void tearDown(){
        this.helper.logoutUser(this.user.getUserName(), this.uuid);
    }

    /**
     * remove a valid product
     */
    @Test
    void removeValidProduct() {
        Assertions.assertTrue(this.sellerOwnerService.deleteProductFromStore(this.user.getUserName(),
                this.storeId, this.productDto.getProductSn(), this.uuid));
    }

    /**
     * remove a valid product twice
     */
    @Test
    void removeValidProductTwice() {
        this.sellerOwnerService.deleteProductFromStore(this.user.getUserName(),
                this.storeId, this.productDto.getProductSn(), this.uuid);
        Assertions.assertFalse(this.sellerOwnerService.deleteProductFromStore(this.user.getUserName(),
                this.storeId, this.productDto.getProductSn(), this.uuid));
    }

    /**
     * remove a valid product
     * invalid owner
     */
    @Test
    void removeValidProductInvalidOwner() {
        Assertions.assertThrows(Exception.class, ()-> {
            this.sellerOwnerService.deleteProductFromStore(this.user.getUserName()+"Not",
                    this.storeId, this.productDto.getProductSn(), this.uuid);
        });
    }

    /**
     * remove a valid product
     * invalid store
     */
    @Test
    void removeValidProductInvalidStore() {
        Assertions.assertFalse(this.sellerOwnerService.deleteProductFromStore(this.user.getUserName(),
                this.storeId+5, this.productDto.getProductSn(), this.uuid));
    }

    /**
     * remove a valid product
     * invalid owner
     * invalid store
     */
    @Test
    void removeValidProductInvalidOwnerInvalidStore() {
        Assertions.assertThrows(Exception.class, ()-> {
            this.sellerOwnerService.deleteProductFromStore(this.user.getUserName()+"Not",
                    this.storeId+5, this.productDto.getProductSn(), this.uuid);
        });
    }

    /**
     * remove an invalid product
     */
    @Test
    void removeInvalidProduct() {
        Assertions.assertFalse(this.sellerOwnerService.deleteProductFromStore(this.user.getUserName(),
                this.storeId, this.productDto.getProductSn()+5, this.uuid));
    }

    /**
     * remove an invalid product
     * invalid owner
     */
    @Test
    void removeInvalidProductInvalidOwner() {
        Assertions.assertThrows(Exception.class, ()-> {
            this.sellerOwnerService.deleteProductFromStore(this.user.getUserName()+"Not",
                    this.storeId, this.productDto.getProductSn()+5, this.uuid);
        });
    }

    /**
     * remove an invalid product
     * invalid store
     */
    @Test
    void removeInvalidProductInvalidStore() {
        Assertions.assertFalse(this.sellerOwnerService.deleteProductFromStore(this.user.getUserName(),
                this.storeId+5, this.productDto.getProductSn()+5, this.uuid));
    }
}

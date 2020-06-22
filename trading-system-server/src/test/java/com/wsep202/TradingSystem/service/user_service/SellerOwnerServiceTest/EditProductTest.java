package com.wsep202.TradingSystem.service.user_service.SellerOwnerServiceTest;

import com.github.rozidan.springboot.modelmapper.WithModelMapper;
import com.wsep202.TradingSystem.config.ObjectMapperConfig;
import com.wsep202.TradingSystem.config.TradingSystemConfiguration;
import com.wsep202.TradingSystem.config.httpSecurity.HttpSecurityConfig;
import com.wsep202.TradingSystem.dto.StoreDto;
import com.wsep202.TradingSystem.dto.UserSystemDto;
import com.wsep202.TradingSystem.service.user_service.*;
import com.wsep202.TradingSystem.dto.ProductDto;
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

import java.util.UUID;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TradingSystemConfiguration.class, HttpSecurityConfig.class, ObjectMapperConfig.class, GuestService.class, BuyerRegisteredService.class, SellerOwnerService.class, SellerManagerService.class})
@SpringBootTest(args = {"admin","admin"})
@WithModelMapper

// *********** UC 4.1.3 - editing a product’s detail ***********
public class EditProductTest {
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
    int storeId = 0;
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
        Pair<UUID, Boolean> returnedValueLogin = this.helper.loginUser(this.user.getUserName(),
                this.userPassword);
        if (returnedValueLogin != null){
            this.uuid = returnedValueLogin.getKey();
        }
        Pair<Integer, ProductDto> returnedValueOpen = this.helper.openStoreAndAddProduct(this.user, this.uuid);
        if (returnedValueOpen != null){
            this.productDto = returnedValueOpen.getValue();
            this.storeId = returnedValueOpen.getKey();
        }
    }

    @AfterEach
    void tearDown(){
        this.helper.logoutUser(this.user.getUserName(), this.uuid);
    }

    /**
     * edit a valid product, don't change it
     */
    @Test
    void editValidProductNoChanges() {
        Assertions.assertTrue(this.sellerOwnerService.editProduct(this.user.getUserName(),
                this.storeId, this.productDto.getProductSn(), this.productDto.getName(),
                this.productDto.getCategory().toLowerCase(), this.productDto.getAmount(), this.productDto.getCost(), this.uuid));
    }

    /**
     * edit a valid product, change name
     */
    @Test
    void editValidProductChangeName() {
        Assertions.assertTrue(this.sellerOwnerService.editProduct(this.user.getUserName(),
                this.storeId, this.productDto.getProductSn(), "New productName",
                this.productDto.getCategory().toLowerCase(), this.productDto.getAmount(), this.productDto.getCost(), this.uuid));
    }

    /**
     * edit a valid product, change category
     */
    @Test
    void editValidProductChangeCategory() {
        Assertions.assertTrue(this.sellerOwnerService.editProduct(this.user.getUserName(),
                this.storeId, this.productDto.getProductSn(), this.productDto.getName(),
                "health", this.productDto.getAmount(), this.productDto.getCost(), this.uuid));
    }
    /**
     * edit a valid product, change amount
     */
    @Test
    void editValidProductChangeAmount() {
        Assertions.assertTrue(this.sellerOwnerService.editProduct(this.user.getUserName(),
                this.storeId, this.productDto.getProductSn(), this.productDto.getName(),
                this.productDto.getCategory().toLowerCase(), this.productDto.getAmount()+100, this.productDto.getCost(), this.uuid));
    }

    /**
     * edit a valid product, change cost
     */
    @Test
    void editValidProductChangeCost() {
        Assertions.assertTrue(this.sellerOwnerService.editProduct(this.user.getUserName(),
                this.storeId, this.productDto.getProductSn(), this.productDto.getName(),
                this.productDto.getCategory().toLowerCase(), this.productDto.getAmount(), this.productDto.getCost()+100, this.uuid));
    }

    /**
     * edit a valid product, change name, category, amount and cost
     */
    @Test
    void editValidProductChangeNameCategoryAmountCost() {
        Assertions.assertTrue(this.sellerOwnerService.editProduct(this.user.getUserName(),
                this.storeId, this.productDto.getProductSn(), "new "+this.productDto.getName(),
                "health", this.productDto.getAmount()+100, this.productDto.getCost()+100, this.uuid));
    }

    /**
     * edit a valid product
     * invalid owner
     */
    @Test
    void editValidProductInvalidOwner() {
        Assertions.assertThrows(Exception.class, ()-> {
            this.sellerOwnerService.editProduct(this.user.getUserName()+"Not",
                    this.storeId, this.productDto.getProductSn(), this.productDto.getName(),
                    this.productDto.getCategory().toLowerCase(), this.productDto.getAmount(), this.productDto.getCost(), this.uuid);
        });
    }

    /**
     * edit a valid product
     * invalid store
     */
    @Test
    void editValidProductInvalidStore() {
        Assertions.assertFalse(this.sellerOwnerService.editProduct(this.user.getUserName(),
                this.storeId+5, this.productDto.getProductSn(), this.productDto.getName(),
                this.productDto.getCategory().toLowerCase(), this.productDto.getAmount(), this.productDto.getCost(), this.uuid));
    }

    /**
     * edit a valid product
     * invalid owner
     * invalid store
     */
    @Test
    void editValidProductInvalidOwnerInvalidStore() {
        Assertions.assertThrows(Exception.class, ()-> {
            this.sellerOwnerService.editProduct(this.user.getUserName()+"Not",
                    this.storeId+5, this.productDto.getProductSn(), this.productDto.getName(),
                    this.productDto.getCategory().toLowerCase(), this.productDto.getAmount(), this.productDto.getCost(), this.uuid);
        });
    }

    /**
     * invalid product
     */
    @Test
    void editInvalidProduct() {
        Assertions.assertFalse(this.sellerOwnerService.editProduct(this.user.getUserName(),
                this.storeId, this.productDto.getProductSn()+5, this.productDto.getName(),
                this.productDto.getCategory().toLowerCase(), this.productDto.getAmount(), this.productDto.getCost(), this.uuid));
    }

    /**
     * edit a valid product
     * invalid store
     */
    @Test
    void editValidProductInvalidStoreInvalidCost() {
        Assertions.assertFalse(this.sellerOwnerService.editProduct(this.user.getUserName(),
                this.storeId + 5, this.productDto.getProductSn(), this.productDto.getName(),
                this.productDto.getCategory().toLowerCase(), this.productDto.getAmount(), this.productDto.getCost() - 100, this.uuid));
    }

    /**
     * edit a valid product
     * negative amount
     */
    @Test
    void editValidProductNegativeAmount() {
        Assertions.assertFalse(this.sellerOwnerService.editProduct(this.user.getUserName(),
                this.storeId, this.productDto.getProductSn(), this.productDto.getName(),
                this.productDto.getCategory().toLowerCase(), -this.productDto.getAmount(), this.productDto.getCost(), this.uuid));
    }
    /**
     * edit a valid product
     * negative cost
     */
    @Test
    void editValidProductNegativeCost() {
        Assertions.assertFalse(this.sellerOwnerService.editProduct(this.user.getUserName(),
                this.storeId, this.productDto.getProductSn(), this.productDto.getName(),
                this.productDto.getCategory().toLowerCase(), this.productDto.getAmount(), -this.productDto.getCost(), this.uuid));
    }
}

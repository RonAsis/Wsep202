package com.wsep202.TradingSystem.service.user_service.BuyerRegisteredServiceTest;

import com.github.rozidan.springboot.modelmapper.WithModelMapper;
import com.wsep202.TradingSystem.config.ObjectMapperConfig;
import com.wsep202.TradingSystem.config.TradingSystemConfiguration;
import com.wsep202.TradingSystem.domain.trading_system_management.UserSystem;
import com.wsep202.TradingSystem.service.user_service.BuyerRegisteredService;
import com.wsep202.TradingSystem.service.user_service.GuestService;
import com.wsep202.TradingSystem.service.user_service.SellerOwnerService;
import com.wsep202.TradingSystem.dto.*;
import com.wsep202.TradingSystem.service.user_service.ServiceTestsHelper;
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

import java.util.List;
import java.util.UUID;


@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TradingSystemConfiguration.class, ObjectMapperConfig.class, GuestService.class, BuyerRegisteredService.class, SellerOwnerService.class})
@SpringBootTest(args = {"admin","admin"})
@WithModelMapper

// *********** UC 2.8 (inherited from guest) - purchasing shopping cart ***********
public class PurchaseShoppingCartBuyerTest {
    @Autowired
    GuestService guestService;
    @Autowired
    BuyerRegisteredService buyerRegisteredService;
    @Autowired
    SellerOwnerService sellerOwnerService;
    ServiceTestsHelper helper;
    UserSystemDto user = new UserSystemDto("username","name","lname");
    String userPassword = "password";
    MultipartFile image = null;
    UUID uuid;
int counter = 0;

    BillingAddressDto billingAddressDto = new BillingAddressDto(this.user.getFirstName()+" "+this.user.getLastName(),
            "address", "city", "country", "1234567");
    BillingAddressDto invalidBillingAddressDto = new BillingAddressDto(this.user.getFirstName()+" "+this.user.getLastName(),
            "address", "city", "country", "1");
    PaymentDetailsDto paymentDetailsDto = new PaymentDetailsDto( "123456789", 798, "123456789");
    PaymentDetailsDto invalidPaymentDetailsDto = new PaymentDetailsDto( "123456789", 1, "123456789");
    PurchaseRegisterBuyerDto purchaseRegisterBuyerDto = new PurchaseRegisterBuyerDto(this.paymentDetailsDto, this.billingAddressDto);
    PurchaseRegisterBuyerDto purchaseRegisterBuyerDtoInvalidBilling = new PurchaseRegisterBuyerDto(this.paymentDetailsDto, this.invalidBillingAddressDto);
    PurchaseRegisterBuyerDto purchaseRegisterBuyerDtoInvalidPayment = new PurchaseRegisterBuyerDto(this.invalidPaymentDetailsDto, this.billingAddressDto);

    @BeforeEach
    void setUp() {
        if (this.helper == null || this.helper.getGuestService() == null ) {
            this.helper = new ServiceTestsHelper(this.guestService, this.buyerRegisteredService, this.sellerOwnerService);
        }
        this.user.setUserName(this.user.getUserName()+this.counter);
        this.helper.registerUser(this.user.getUserName(), this.userPassword,
                this.user.getFirstName(), this.user.getLastName(), image);
        Pair<UUID, Boolean> returnedValue = this.helper.loginUser(this.user.getUserName(),
                this.userPassword);
        if (returnedValue != null){
            this.uuid = returnedValue.getKey();
        }
        this.counter++;
    }

    @AfterEach
    void tearDown(){
        this.helper.logoutUser(this.user.getUserName(), this.uuid);
    }

    /**
     * purchase shopping cart of user = ""
     */
    @Test
    void purchaseShoppingCartEmptyUsername() {
        Assertions.assertNull(this.buyerRegisteredService.purchaseShoppingCartBuyer("",
                this.purchaseRegisterBuyerDto, this.uuid));
    }

    /**
     * purchase shopping cart of a user that isn't registered
     */
    @Test
    void purchaseShoppingCartUserNotRegistered() {
        Assertions.assertNull(this.buyerRegisteredService.purchaseShoppingCartBuyer("NotRegistered",
                this.purchaseRegisterBuyerDto, this.uuid));
    }

    /**
     * purchase shopping cart of a registered user with an empty shopping cart
     */
    @Test
    void purchaseEmptyShoppingCartRegisteredUser() {
        Assertions.assertNull(this.buyerRegisteredService.purchaseShoppingCartBuyer(this.user.getUserName(),
                this.purchaseRegisterBuyerDto, this.uuid));
    }

    /**
     * purchase shopping cart of a registered user with an invalid purchaseRegisterBuyerDto
     */
    @Test
    void purchaseEmptyShoppingCartInvalidBillingAddress() {
        Assertions.assertNull(this.buyerRegisteredService.purchaseShoppingCartBuyer(this.user.getUserName(),
                this.purchaseRegisterBuyerDtoInvalidBilling, this.uuid));
    }

    /**
     * purchase shopping cart of a registered user with an invalid purchaseRegisterBuyerDto
     */
    @Test
    void purchaseEmptyShoppingCartInvalidPaymentDetails() {
        Assertions.assertNull(this.buyerRegisteredService.purchaseShoppingCartBuyer(this.user.getUserName(),
                this.purchaseRegisterBuyerDtoInvalidPayment, this.uuid));
    }

    /**
     * purchase shopping cart of a registered user with a product in his shopping cart
     */
    @Test
    void purchaseShoppingCartRegisteredUser() {
        this.helper.addProductToShoppingCart(this.user.getUserName(), this.uuid);
        Assertions.assertNotNull(this.buyerRegisteredService.purchaseShoppingCartBuyer(this.user.getUserName(),
                this.purchaseRegisterBuyerDto, this.uuid));
    }
}

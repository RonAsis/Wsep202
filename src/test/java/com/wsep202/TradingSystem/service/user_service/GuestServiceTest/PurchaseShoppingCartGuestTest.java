package com.wsep202.TradingSystem.service.user_service.GuestServiceTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.rozidan.springboot.modelmapper.WithModelMapper;
import com.wsep202.TradingSystem.config.ObjectMapperConfig;
import com.wsep202.TradingSystem.config.TradingSystemConfiguration;
import com.wsep202.TradingSystem.dto.*;
import com.wsep202.TradingSystem.service.user_service.BuyerRegisteredService;
import com.wsep202.TradingSystem.service.user_service.GuestService;
import com.wsep202.TradingSystem.service.user_service.SellerOwnerService;
import com.wsep202.TradingSystem.service.user_service.ServiceTestsHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TradingSystemConfiguration.class, ObjectMapperConfig.class, GuestService.class, BuyerRegisteredService.class, SellerOwnerService.class})
@SpringBootTest(args = {"admin","admin"})
@WithModelMapper

// *********** UC 2.8 - purchasing shopping cart ***********
public class PurchaseShoppingCartGuestTest {
    @Autowired
    GuestService guestService;
    @Autowired
    BuyerRegisteredService buyerRegisteredService;
    @Autowired
    SellerOwnerService sellerOwnerService;
    ServiceTestsHelper helper;
    ObjectMapper objectMapper;

    UserSystemDto user = new UserSystemDto("username","name","lname");
    String userPassword = "password";
    MultipartFile image = null;
    BillingAddressDto billingAddressDto = new BillingAddressDto(this.user.getFirstName()+" "+this.user.getLastName(),
            "address", "city", "country", "1234567");
    BillingAddressDto invalidBillingAddressDto = new BillingAddressDto(this.user.getFirstName()+" "+this.user.getLastName(),
            "address", "city", "country", "1");
    PaymentDetailsDto paymentDetailsDto = new PaymentDetailsDto( "123456789", 798, "123456789");
    PaymentDetailsDto invalidPaymentDetailsDto = new PaymentDetailsDto( "123456789", 1, "123456789");
    ShoppingCartDto shoppingCartDto;

    @BeforeEach
    void setUp() {
        if (this.helper == null || this.helper.getGuestService() == null ){
            this.helper = new ServiceTestsHelper(this.guestService, this.buyerRegisteredService,
                    this.sellerOwnerService);
        }
    }

    /**
     * purchase an empty shopping cart
     */
    @Test
    void purchaseEmptyShoppingCart() {
        this.shoppingCartDto = new ShoppingCartDto();
        PurchaseDto purchaseDto = new PurchaseDto(this.shoppingCartDto, this.paymentDetailsDto, this.billingAddressDto);
        //String purchaseDtoString = objectMapper.readValue(purchaseDto, PurchaseDto.class);
        //Assertions.assertNull(this.guestService.purchaseShoppingCartGuest(purchaseDtoString));
    }

    /**
     * purchase shopping with null payment details
     */
    @Test
    void purchaseShoppingCartNullPaymentDetails() {
        this.shoppingCartDto = new ShoppingCartDto();
        PurchaseDto purchaseDto = new PurchaseDto(this.shoppingCartDto, null, this.billingAddressDto);
        //String purchaseDtoString = objectMapper.readValue(purchaseDto, PurchaseDto.class);
        //Assertions.assertNull(this.guestService.purchaseShoppingCartGuest(purchaseDtoString));

    }

    /**
     * purchase shopping with null billing address
     */
    @Test
    void purchaseShoppingCartNullBillingAddress() {
        this.shoppingCartDto = new ShoppingCartDto();
        PurchaseDto purchaseDto = new PurchaseDto(this.shoppingCartDto, this.paymentDetailsDto, null);
        //String purchaseDtoString = objectMapper.readValue(purchaseDto, PurchaseDto.class);
        //Assertions.assertNull(this.guestService.purchaseShoppingCartGuest(purchaseDtoString));

    }
    /**
     * purchase shopping with invalid payment details
     */
    @Test
    void purchaseShoppingCartInvalidPaymentDetails() {
        this.shoppingCartDto = new ShoppingCartDto();
        PurchaseDto purchaseDto = new PurchaseDto(this.shoppingCartDto, this.invalidPaymentDetailsDto, this.billingAddressDto);
        //String purchaseDtoString = objectMapper.readValue(purchaseDto, PurchaseDto.class);
        //Assertions.assertNull(this.guestService.purchaseShoppingCartGuest(purchaseDtoString));

    }

    /**
     * purchase shopping with invalid billing address
     */
    @Test
    void purchaseShoppingCartInvalidBillingAddress() {
        this.shoppingCartDto = new ShoppingCartDto();
        PurchaseDto purchaseDto = new PurchaseDto(this.shoppingCartDto, this.paymentDetailsDto, this.invalidBillingAddressDto);
        //String purchaseDtoString = objectMapper.readValue(purchaseDto, PurchaseDto.class);
        //Assertions.assertNull(this.guestService.purchaseShoppingCartGuest(purchaseDtoString));

    }


}
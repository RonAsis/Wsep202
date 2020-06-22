package com.wsep202.TradingSystem.service.user_service.GuestServiceTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.rozidan.springboot.modelmapper.WithModelMapper;
import com.wsep202.TradingSystem.config.ObjectMapperConfig;
import com.wsep202.TradingSystem.config.TradingSystemConfiguration;
import com.wsep202.TradingSystem.config.httpSecurity.HttpSecurityConfig;
import com.wsep202.TradingSystem.dto.*;
import com.wsep202.TradingSystem.service.user_service.BuyerRegisteredService;
import com.wsep202.TradingSystem.service.user_service.GuestService;
import com.wsep202.TradingSystem.service.user_service.SellerOwnerService;
import com.wsep202.TradingSystem.service.user_service.ServiceTestsHelper;
import externals.ChargeSystem;
import externals.SupplySystem;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TradingSystemConfiguration.class,  HttpSecurityConfig.class, ObjectMapperConfig.class, GuestService.class, BuyerRegisteredService.class, SellerOwnerService.class})
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
    ObjectMapper objectMapper = new ObjectMapper();

    UserSystemDto user = new UserSystemDto("username","name","lname");
    String userPassword = "password";
    MultipartFile image = null;
    BillingAddressDto billingAddressDto = new BillingAddressDto(this.user.getFirstName()+" "+this.user.getLastName(),
            "address", "city", "country", "1234567");
    BillingAddressDto invalidBillingAddressDto = new BillingAddressDto(this.user.getFirstName()+" "+this.user.getLastName(),
            "address", "city", "country", "1");
    PaymentDetailsDto paymentDetailsDto = new PaymentDetailsDto( "123456789", "798", "123456789", "June", "2020", "username");
    PaymentDetailsDto invalidPaymentDetailsDto = new PaymentDetailsDto( "123456789", "1", "123456789", "June", "2020", "username");
    ShoppingCartDto shoppingCartDto = new ShoppingCartDto();

    //the external systems
    private ChargeSystem chargeSystem;
    private SupplySystem supplySystem;

    @BeforeEach
    void setUp() {
        if (this.helper == null || this.helper.getGuestService() == null ){
            this.helper = new ServiceTestsHelper(this.guestService, this.buyerRegisteredService,
                    this.sellerOwnerService);
        }
        chargeSystem = mock(ChargeSystem.class);
        supplySystem = mock(SupplySystem.class);
    }

    /**
     * purchase an empty shopping cart
     */
    @Test
    void purchaseEmptyShoppingCart() {
        PurchaseDto purchaseDto = new PurchaseDto(this.shoppingCartDto, this.paymentDetailsDto, this.billingAddressDto);
        try {
            String purchaseDtoString = objectMapper.writeValueAsString(purchaseDto);
            when(chargeSystem.sendPaymentTransaction(any(), any())).thenReturn(10002);
            when(supplySystem.deliver(any(), any())).thenReturn(11003);
            Assertions.assertNull(this.guestService.purchaseShoppingCartGuest(purchaseDtoString));
        } catch (Exception e) {
        }
    }

    /**
     * purchase shopping with null payment details
     */
    @Test
    void purchaseShoppingCartNullPaymentDetails() {
        PurchaseDto purchaseDto = new PurchaseDto(this.shoppingCartDto, null, this.billingAddressDto);
        try {
            String purchaseDtoString = objectMapper.writeValueAsString(purchaseDto);
            when(chargeSystem.sendPaymentTransaction(any(), any())).thenReturn(10002);
            when(supplySystem.deliver(any(), any())).thenReturn(11003);
            Assertions.assertNull(this.guestService.purchaseShoppingCartGuest(purchaseDtoString));
        } catch (Exception e) {
        }
    }

    /**
     * purchase shopping with null billing address
     */
    @Test
    void purchaseShoppingCartNullBillingAddress() {
        PurchaseDto purchaseDto = new PurchaseDto(this.shoppingCartDto, this.paymentDetailsDto, null);
        try {
            String purchaseDtoString = objectMapper.writeValueAsString(purchaseDto);
            when(chargeSystem.sendPaymentTransaction(any(), any())).thenReturn(10002);
            when(supplySystem.deliver(any(), any())).thenReturn(11003);
            Assertions.assertNull(this.guestService.purchaseShoppingCartGuest(purchaseDtoString));
        } catch (Exception e) {
        }
    }
    /**
     * purchase shopping with invalid payment details
     */
    @Test
    void purchaseShoppingCartInvalidPaymentDetails() {
        PurchaseDto purchaseDto = new PurchaseDto(this.shoppingCartDto, this.invalidPaymentDetailsDto, this.billingAddressDto);
        try {
            String purchaseDtoString = objectMapper.writeValueAsString(purchaseDto);
            when(chargeSystem.sendPaymentTransaction(any(), any())).thenReturn(10002);
            when(supplySystem.deliver(any(), any())).thenReturn(11003);
            Assertions.assertNull(this.guestService.purchaseShoppingCartGuest(purchaseDtoString));
        } catch (Exception e) {
        }
    }

    /**
     * purchase shopping with invalid billing address
     */
    @Test
    void purchaseShoppingCartInvalidBillingAddress() {
        PurchaseDto purchaseDto = new PurchaseDto(this.shoppingCartDto, this.paymentDetailsDto, this.invalidBillingAddressDto);
        try {
            String purchaseDtoString = objectMapper.writeValueAsString(purchaseDto);
            when(chargeSystem.sendPaymentTransaction(any(), any())).thenReturn(10002);
            when(supplySystem.deliver(any(), any())).thenReturn(11003);
            Assertions.assertNull(this.guestService.purchaseShoppingCartGuest(purchaseDtoString));
        } catch (Exception e) {
        }
    }
}

package com.wsep202.TradingSystem.service.user_service.GuestServiceTest;

import com.github.rozidan.springboot.modelmapper.WithModelMapper;
import com.wsep202.TradingSystem.config.TradingSystemConfiguration;
import com.wsep202.TradingSystem.domain.trading_system_management.CardAction;
import com.wsep202.TradingSystem.domain.trading_system_management.UserSystem;
import com.wsep202.TradingSystem.service.user_service.BuyerRegisteredService;
import com.wsep202.TradingSystem.service.user_service.GuestService;
import com.wsep202.TradingSystem.service.user_service.SellerOwnerService;
import com.wsep202.TradingSystem.dto.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TradingSystemConfiguration.class, GuestService.class, BuyerRegisteredService.class, SellerOwnerService.class})
@SpringBootTest(args = {"admin","admin"})
@WithModelMapper

// *********** UC 2.8 - purchasing shopping cart ***********
public class PurchaseShoppingCartTest {
    @Autowired
    GuestService guestService;
    @Autowired
    BuyerRegisteredService buyerRegisteredService;
    @Autowired
    SellerOwnerService sellerOwnerService;

    StoreDto storeDto;
    UserSystem userSystem;
    ProductDto productDto;
    ShoppingCartDto shoppingCartDto;
    PaymentDetailsDto paymentDetailsDto;
    BillingAddressDto billingAddressDto;
    private ReceiptDto receiptDto;

    @BeforeEach
    void setUp() {
        userSystem = new UserSystem("username","name","lname","pass");
        this.shoppingCartDto = new ShoppingCartDto();
        this.billingAddressDto = new BillingAddressDto(this.userSystem.getFirstName()+" "+this.userSystem.getLastName(),
                "address", "city", "country", "1234567");
        this.paymentDetailsDto = new PaymentDetailsDto(CardAction.PAY, "123456789", "month",
                "year", "Cardholder", 798, "id");

    }

    @AfterEach
    void tearDown() {

    }

    /**
     * purchase an empty shopping cart
     */
    @Test
    void purchaseEmptyShoppingCart() {
        Assertions.assertNull(this.guestService.purchaseShoppingCartGuest(this.shoppingCartDto,
                this.paymentDetailsDto, this.billingAddressDto));
    }

    /**
     * purchase shopping with invalid payment details
     */
    @Test
    void purchaseShoppingCartInvalidPaymentDetails() {
        Assertions.assertNull(this.guestService.purchaseShoppingCartGuest(this.shoppingCartDto,
                null, this.billingAddressDto));
    }

    /**
     * purchase shopping with invalid billing address
     */
    @Test
    void purchaseShoppingCartInvalidBillingAddress() {
        Assertions.assertNull(this.guestService.purchaseShoppingCartGuest(this.shoppingCartDto,
                this.paymentDetailsDto, null));
    }

    /**
     * opening a new store and adding a product to it
     */
    void openStoreAndAddProducts(){
        UserSystem owner = new UserSystem("owner","name","lname","pass");
        // registering the owner
        Assertions.assertTrue(this.guestService.registerUser(owner.getUserName(), owner.getPassword(),
                owner.getFirstName(), owner.getLastName()));

        // opening a new store, owned by owner
        Assertions.assertTrue(this.buyerRegisteredService.openStore(owner.getUserName(),
                new PurchasePolicyDto(), new DiscountPolicyDto(), "storeName"));

        // getting the storeDto of the store the owner opened
        this.storeDto = this.buyerRegisteredService.getStoresDtos().get(0);

        // adding a product to the owner's store
        Assertions.assertTrue(this.sellerOwnerService.addProduct(owner.getUserName(), storeDto.getStoreId(),
                "motor", "motors", 20, 20));
        // getting the productDto of the added product

        this.productDto = (ProductDto) this.buyerRegisteredService.getStoresDtos().get(0).getProducts().toArray()[0];
    }

//    /**
//     * buying a product from the store
//     */
//    void buyProduct(){
//        openStoreAndAddProducts();
//        int amount = 1;
//        Assertions.assertTrue(this.guestService.saveProductInShoppingBag(
//                this.storeDto.getStoreId(), this.productDto.getProductSn(), amount));
//
//        BillingAddressDto billingAddress = new BillingAddressDto(this.owner.getFirstName()+" "+this.owner.getLastName(),
//                "address", "city", "country", "1234567");
//        PaymentDetailsDto paymentDetailsDto = new PaymentDetailsDto(CardAction.PAY, "123456789", "month",
//                "year", "Cardholder", 798, "id");
//        this.receiptDto = this.buyerRegisteredService.purchaseShoppingCart(this.owner.getUserName(),
//                paymentDetailsDto, billingAddress);
//        Assertions.assertNotNull(this.receiptDto);
//        Assertions.assertEquals(amount,this.receiptDto.getProductsBought().get(this.productDto));
//    }
}

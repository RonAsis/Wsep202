package com.wsep202.TradingSystem.service.user_service.BuyerRegisteredServiceTest;

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

import java.util.List;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TradingSystemConfiguration.class, GuestService.class, BuyerRegisteredService.class, SellerOwnerService.class})
@SpringBootTest(args = {"admin","admin"})
@WithModelMapper

// *********** UC 2.8 (inherited from guest) - purchasing shopping cart ***********
public class PurchaseShoppingCartTest {

    @Autowired
    GuestService guestService;
    @Autowired
    BuyerRegisteredService buyerRegisteredService;
    @Autowired
    SellerOwnerService sellerOwnerService;
    UserSystem userSystem;
    StoreDto storeDto;
    private ProductDto productDto;
    private List<ReceiptDto> receiptDto;
    private UserSystem owner;
    private BillingAddressDto billingAddressDto;
    private PaymentDetailsDto paymentDetailsDto;


    @BeforeEach
    void setUp() {
        userSystem = new UserSystem("username", "name", "lname", "pass");
        this.billingAddressDto = new BillingAddressDto(this.userSystem.getFirstName()+" "+this.userSystem.getLastName(),
                "address", "city", "country", "1234567");
        this.paymentDetailsDto = new PaymentDetailsDto(CardAction.PAY, "123456789", "month",
                "year", "Cardholder", 798, "id");

    }

    @AfterEach
    void tearDown() {

    }

    /**
     * purchase shopping cart of user = ""
     */
    @Test
    void purchaseShoppingCartEmptyUsername() {
        Assertions.assertNull(this.buyerRegisteredService.purchaseShoppingCartBuyer("",
                new PaymentDetailsDto(), new BillingAddressDto()));
    }

    /**
     * purchase shopping cart of a user that isn't registered
     */
    @Test
    void purchaseShoppingCartUserNotRegistered() {
        Assertions.assertNull(this.buyerRegisteredService.purchaseShoppingCartBuyer("not registered",
                new PaymentDetailsDto(), new BillingAddressDto()));
    }

    /**
     * purchase shopping cart of a registered user with an empty shopping cart
     */
    @Test
    void purchaseEmptyShoppingCartRegisteredUser() {
        registerUser();
        Assertions.assertNull(this.buyerRegisteredService.purchaseShoppingCartBuyer(this.userSystem.getUserName(),
                new PaymentDetailsDto(), new BillingAddressDto()));
    }

    /**
     * purchase shopping cart of a not registered user
     */
    @Test
    void purchaseEmptyShoppingCartNotRegisteredUser() {
        registerUser();
        Assertions.assertNull(this.buyerRegisteredService.purchaseShoppingCartBuyer(this.userSystem.getUserName()+"Not",
                new PaymentDetailsDto(), new BillingAddressDto()));
    }

    /**
     * purchase shopping cart of a registered user with a product in his shopping cart
     */
    @Test
    void purchaseShoppingCartRegisteredUser() {
        registerUser();
        int amount = 1;
        addToCart(amount);
        this.receiptDto = this.buyerRegisteredService.purchaseShoppingCartBuyer(this.userSystem.getUserName(),
                this.paymentDetailsDto, this.billingAddressDto);
        Assertions.assertNotNull(this.receiptDto);
        Assertions.assertEquals(amount, this.receiptDto.get(0).getProductBoughtAmountByProductSn(this.productDto.getProductSn()));
    }

    /**
     * opening a new store and adding a product to it
     */
    void openStoreAndAddProducts(){
        this.owner = new UserSystem("owner","name","lname","pass");
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

    /**
     * register user into the system
     */
    private void registerUser() {
        this.guestService.registerUser(userSystem.getUserName(), userSystem.getPassword(),
                userSystem.getFirstName(), userSystem.getLastName());
    }

    /**
     * adding a product to the cart
     */
    void addToCart(int amount){
        openStoreAndAddProducts();
        Assertions.assertTrue(this.buyerRegisteredService.saveProductInShoppingBag(this.userSystem.getUserName(),
                this.storeDto.getStoreId(), this.productDto.getProductSn(), amount));
    }
}

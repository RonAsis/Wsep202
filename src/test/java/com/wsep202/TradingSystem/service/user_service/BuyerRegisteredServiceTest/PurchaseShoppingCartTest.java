package com.wsep202.TradingSystem.service.user_service.BuyerRegisteredServiceTest;

import com.github.rozidan.springboot.modelmapper.WithModelMapper;
import com.wsep202.TradingSystem.domain.config.TradingSystemConfiguration;
import com.wsep202.TradingSystem.domain.trading_system_management.TradingSystemFacade;
import com.wsep202.TradingSystem.domain.trading_system_management.UserSystem;
import com.wsep202.TradingSystem.service.user_service.BuyerRegisteredService;
import com.wsep202.TradingSystem.service.user_service.GuestService;
import com.wsep202.TradingSystem.service.user_service.SellerOwnerService;
import com.wsep202.TradingSystem.service.user_service.dto.*;
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


    @BeforeEach
    void setUp() {
        userSystem = new UserSystem("username", "name", "lname", "pass");
    }

    @AfterEach
    void tearDown() {
        this.buyerRegisteredService.clearDS();
    }

    /**
     * purchase shopping cart of user = ""
     */
    @Test
    void purchaseShoppingCartEmptyUsername() {
        Assertions.assertNull(this.buyerRegisteredService.purchaseShoppingCart("",
                new PaymentDetailsDto(), new BillingAddressDto()));
    }

    /**
     * purchase shopping cart of a user that isn't registered
     */
    @Test
    void purchaseShoppingCartUserNotRegistered() {
        Assertions.assertNull(this.buyerRegisteredService.purchaseShoppingCart("not registered",
                new PaymentDetailsDto(), new BillingAddressDto()));
    }

    /**
     * purchase shopping cart of a registered user with an empty shopping cart
     */
    @Test
    void purchaseEmptyShoppingCartRegisteredUser() {
        registerUser();
        Assertions.assertNull(this.buyerRegisteredService.purchaseShoppingCart(this.userSystem.getUserName(),
                new PaymentDetailsDto(), new BillingAddressDto()));
    }

//    /**
//     * purchase shopping cart of a registered user with a product in his shopping cart
//     */
//    @Test
//    void purchaseShoppingCartRegisteredUser() {
//        registerUser();
//        addProductToShoppingCart();
//        System.out.println("HGDHGdfddb");
//        Assertions.assertNotNull(this.buyerRegisteredService.purchaseShoppingCart(this.userSystem.getUserName(),
//                new PaymentDetailsDto(), new BillingAddressDto()));
//    }
//
//    private void addProductToShoppingCart() {
//        openStoreAndAddProducts();
//        System.out.println( this.storeDto.getStoreId());
//        System.out.println( this.productDto.getProductSn());
//        System.out.println( this.userSystem.getUserName());
//
//        Assertions.assertTrue(this.buyerRegisteredService.saveProductInShoppingBag(this.userSystem.getUserName(),
//                this.storeDto.getStoreId(), this.productDto.getProductSn(), 1));
//        System.out.println("333333333");
//    }


    /**
     * opening a new store and adding a product to it
     */
    void openStoreAndAddProducts(){
        UserSystem owner = new UserSystem("owner","name","lname","pass");
        // registering the owner
        Assertions.assertTrue(this.guestService.registerUser(owner.getUserName(), owner.getPassword(),
                owner.getFirstName(), owner.getLastName()));

        // opening a new store, owned by owner
        Assertions.assertTrue(this.buyerRegisteredService.openStore(owner.getUserName(), new PurchasePolicyDto(), new DiscountPolicyDto(),
                "Open discount", "Buy immediately", "storeName"));

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


}

//package com.wsep202.TradingSystem.service.user_service.BuyerRegisteredServiceTest;
//
//import com.github.rozidan.springboot.modelmapper.WithModelMapper;
//import com.wsep202.TradingSystem.config.TradingSystemConfiguration;
//import com.wsep202.TradingSystem.domain.trading_system_management.UserSystem;
//import com.wsep202.TradingSystem.service.user_service.BuyerRegisteredService;
//import com.wsep202.TradingSystem.service.user_service.GuestService;
//import com.wsep202.TradingSystem.service.user_service.SellerOwnerService;
//import com.wsep202.TradingSystem.dto.*;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@ExtendWith(SpringExtension.class)
//@ContextConfiguration(classes = {TradingSystemConfiguration.class, GuestService.class, BuyerRegisteredService.class, SellerOwnerService.class})
//@SpringBootTest(args = {"admin","admin"})
//@WithModelMapper
//
//// *********** UC 2.7.1 (inherited from guest) - viewing the products in the shopping cart ***********
//public class ViewProductsInShoppingCartTest {
//
//    @Autowired
//    GuestService guestService;
//    @Autowired
//    BuyerRegisteredService buyerRegisteredService;
//    @Autowired
//    SellerOwnerService sellerOwnerService;
//    UserSystem userSystem;
//    StoreDto storeDto;
//    private ProductDto productDto;
//
//
//    @BeforeEach
//    void setUp() {
//        userSystem = new UserSystem("username", "name", "lname", "pass");
//    }
//
//    @AfterEach
//    void tearDown() {
////        this.buyerRegisteredService.clearDS();
//    }
//
//    /**
//     * view the shopping cart of a user that isn't registered
//     */
//    @Test
//    void viewShoppingCartNotRegisteredUser() {
//        Assertions.assertNull(this.buyerRegisteredService.watchShoppingCart(this.userSystem.getUserName(), uuid));
//    }
//
//    /**
//     * view the shopping cart of a user that isn't registered and his username is ""
//     */
//    @Test
//    void viewShoppingCartNotRegisteredEmptyUsername() {
//        Assertions.assertNull(this.buyerRegisteredService.watchShoppingCart("", uuid));
//    }
//
//    /**
//     * view the shopping cart of a user that is registered, but his cart is empty
//     */
//    @Test
//    void viewEmptyShoppingCartRegisteredUser() {
//        registerUser();
//        ShoppingCartDto shoppingCartDto = new ShoppingCartDto();
//        Map<StoreDto, ShoppingBagDto> shoppingBagList = new HashMap<>();
//        shoppingCartDto.setShoppingBagsList(shoppingBagList);
//        Assertions.assertEquals(shoppingCartDto,
//                this.buyerRegisteredService.watchShoppingCart(this.userSystem.getUserName(), uuid));
//    }
//
//    /**
//     * opening a new store and adding a product to it
//     */
//    void openStoreAndAddProducts(){
//        UserSystem owner = new UserSystem("owner","name","lname","pass");
//        // registering the owner
//        Assertions.assertTrue(this.guestService.registerUser(owner.getUserName(), owner.getPassword(),
//                owner.getFirstName(), owner.getLastName()));
//
//        // opening a new store, owned by owner
//        Assertions.assertTrue(this.buyerRegisteredService.openStore(owner.getUserName(),
//                new PurchasePolicyDto(), new DiscountPolicyDto(),"storeName", uuid));
//
//        // getting the storeDto of the store the owner opened
//        this.storeDto = this.buyerRegisteredService.getStoresDtos().get(0);
//
//        // adding a product to the owner's store
//        Assertions.assertTrue(this.sellerOwnerService.addProduct(owner.getUserName(), storeDto.getStoreId(),
//                "motor", "motors", 20, 20, uuid));
//        // getting the productDto of the added product
//
//        this.productDto = (ProductDto) this.buyerRegisteredService.getStoresDtos().get(0).getProducts().toArray()[0];
//    }
//
//    /**
//     * register user into the system
//     */
//    private void registerUser() {
//        this.guestService.registerUser(userSystem.getUserName(), userSystem.getPassword(),
//                userSystem.getFirstName(), userSystem.getLastName());
//    }
//}

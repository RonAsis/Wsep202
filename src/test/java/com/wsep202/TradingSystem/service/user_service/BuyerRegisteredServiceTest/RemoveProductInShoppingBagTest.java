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

// *********** UC 2.7.2 (inherited from guest) - removing a product from shopping bag ***********
public class RemoveProductInShoppingBagTest {

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
     * remove a product from empty shopping bag
     */
    @Test
    void removeProductFromEmptyShoppingBag() {
        registerUser();
        openStoreAndAddProducts();
        Assertions.assertFalse(this.buyerRegisteredService.removeProductInShoppingBag(this.userSystem.getUserName(),
                1, 0));
    }

    /**
     * remove a product from an empty username's shopping bag
     */
    @Test
    void removeProductEmptyUsername() {
        openStoreAndAddProducts();
        Assertions.assertFalse(this.buyerRegisteredService.removeProductInShoppingBag("",
                0, 0));
    }

    /**
     * remove a product from a username who's not registered's shopping bag
     */
    @Test
    void removeProductUserNotRegistered() {
        registerUser();
        openStoreAndAddProducts();
        Assertions.assertFalse(this.buyerRegisteredService.removeProductInShoppingBag("notRegistered",
                0, 0));
    }

    /**
     * remove a product that isn't in the user's shopping bag, from a registered user's shopping bag
     */
    @Test
    void removeProductUserRegistered() {
        registerUser();
        openStoreAndAddProducts();
        Assertions.assertFalse(this.buyerRegisteredService.removeProductInShoppingBag(this.userSystem.getUserName(),
                0, 0));
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

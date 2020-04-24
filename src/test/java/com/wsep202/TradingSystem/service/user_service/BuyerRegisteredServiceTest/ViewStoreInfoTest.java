package com.wsep202.TradingSystem.service.user_service.BuyerRegisteredServiceTest;

import com.github.rozidan.springboot.modelmapper.WithModelMapper;
import com.wsep202.TradingSystem.config.TradingSystemConfiguration;
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

public class ViewStoreInfoTest {
    @Autowired
    GuestService guestService;
    @Autowired
    BuyerRegisteredService buyerRegisteredService;
    @Autowired
    SellerOwnerService sellerOwnerService;

    StoreDto storeDto;
    ProductDto productDto;
    UserSystem userSystem;

    @BeforeEach
    void setUp() {
        userSystem = new UserSystem("username", "name", "lname", "pass");
    }

    @AfterEach
    void tearDown() {

    }


    /**
     * view a valid store's information
     */
    @Test
    void viewValidStoreInfo() {
        openStore();
        registerUser();
        StoreDto returnedStoreDto = this.guestService.viewStoreInfo(this.storeDto.getStoreId());

        // comparing all of the store's fields
        Assertions.assertEquals(this.storeDto.getStoreId(), returnedStoreDto.getStoreId()); // id
        Assertions.assertEquals(this.storeDto.getStoreName(), returnedStoreDto.getStoreName()); // name
        Assertions.assertEquals(this.storeDto.getDiscountPolicy(), returnedStoreDto.getDiscountPolicy()); // discountPolicy
        Assertions.assertEquals(this.storeDto.getPurchasePolicy(), returnedStoreDto.getPurchasePolicy()); // purchasePolicy
        Assertions.assertEquals(this.storeDto.getDiscountType(), returnedStoreDto.getDiscountType()); // discountType
        Assertions.assertEquals(this.storeDto.getPurchaseType(), returnedStoreDto.getPurchaseType()); // purchaseType
        Assertions.assertEquals(this.storeDto.getRank(), returnedStoreDto.getRank()); // rank

        Object[] actualStoreProducts = this.storeDto.getProducts().toArray(); // products
        Object[] mightBeStoreProducts = returnedStoreDto.getProducts().toArray();
        Assertions.assertEquals(actualStoreProducts.length, mightBeStoreProducts.length);
        if (actualStoreProducts.length == mightBeStoreProducts.length) {
            for (int i = 0; i < actualStoreProducts.length; i++) {
                Assertions.assertEquals(((ProductDto) actualStoreProducts[i]).getProductSn(),
                        ((ProductDto) mightBeStoreProducts[i]).getProductSn());
            }
        }

        Object[] actualStoreReceipts = this.storeDto.getReceipts().toArray(); // storeReceipts
        Object[] mightBeStoreReceipts = returnedStoreDto.getReceipts().toArray();
        Assertions.assertEquals(actualStoreReceipts.length, mightBeStoreReceipts.length);
        if (actualStoreReceipts.length == mightBeStoreReceipts.length) {
            for (int i = 0; i < actualStoreReceipts.length; i++) {
                Assertions.assertEquals(((ReceiptDto) actualStoreReceipts[i]).getReceiptSn(),
                        ((ReceiptDto) mightBeStoreReceipts[i]).getReceiptSn());
            }
        }
    }
    /**
     * view an invalid store's information
     */
    @Test
    void viewInvalidStoreInfo() {
        registerUser();
        StoreDto returnedStoreDto = this.guestService.viewStoreInfo(5);
        Assertions.assertNull(returnedStoreDto);
    }

    /**
     * opening a new store.
     */
    void openStore(){
        UserSystem owner = new UserSystem("owner","name","lname","pass");
        // registering the owner
        Assertions.assertTrue(this.guestService.registerUser(owner.getUserName(), owner.getPassword(),
                owner.getFirstName(), owner.getLastName()));

        // opening a new store, owned by owner
        Assertions.assertTrue(this.buyerRegisteredService.openStore(owner.getUserName(),
                new PurchasePolicyDto(), new DiscountPolicyDto(), "storeName"));
        // getting the storeDto of the store the owner opened
        this.storeDto = this.guestService.getStoresDtos().get(0); // getting the storeDto of the store the owner opened
    }

    /**
     * register user into the system
     */
    private void registerUser() {
        this.guestService.registerUser(userSystem.getUserName(), userSystem.getPassword(),
                userSystem.getFirstName(), userSystem.getLastName());
    }
}

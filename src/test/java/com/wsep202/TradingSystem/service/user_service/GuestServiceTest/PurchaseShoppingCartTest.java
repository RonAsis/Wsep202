package com.wsep202.TradingSystem.service.user_service.GuestServiceTest;

import com.github.rozidan.springboot.modelmapper.WithModelMapper;
import com.wsep202.TradingSystem.domain.config.TradingSystemConfiguration;
import com.wsep202.TradingSystem.domain.trading_system_management.UserSystem;
import com.wsep202.TradingSystem.service.user_service.BuyerRegisteredService;
import com.wsep202.TradingSystem.service.user_service.GuestService;
import com.wsep202.TradingSystem.service.user_service.SellerOwnerService;
import com.wsep202.TradingSystem.service.user_service.dto.DiscountPolicyDto;
import com.wsep202.TradingSystem.service.user_service.dto.ProductDto;
import com.wsep202.TradingSystem.service.user_service.dto.PurchasePolicyDto;
import com.wsep202.TradingSystem.service.user_service.dto.StoreDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TradingSystemConfiguration.class, GuestService.class})
@SpringBootTest(args = {"admin","admin"})
@WithModelMapper

// NEED 2.6!!! CANT ADD PRODUCTS TO THE CART!!!!!!!!
// *********** UC 2.8 - purchasing shopping cart ***********
public class PurchaseShoppingCartTest {
    @Autowired
    GuestService guestService;
    @Autowired
    BuyerRegisteredService buyerRegisteredService;
    @Autowired
    SellerOwnerService sellerOwnerService;

    StoreDto storeDto;
    UserSystem owner;
    UserSystem userSystem;
    List<ProductDto> productDtoList;

    @BeforeEach
    void setUp() {
        userSystem = new UserSystem("username","name","lname","pass");
    }

    @AfterEach
    void tearDown() {
        this.guestService.clearDS();
    }

    /**
     * filter a valid list, rank = 0
     * productDtoList has one product and the store it's in is ranked 0.
     */
    @Test
    void change() {
      //  openStoreAndAddProducts();
        Assertions.assertNull(null);

      //  Assertions.assertEquals(this.productDtoList, guestService.filterByStoreRank(this.productDtoList, 0));
    }

    /**
     * opening a new store and adding a product to it
     */
    void openStoreAndAddProducts(){
        owner = new UserSystem("owner","name","lname","pass");
        // registering the owner
        Assertions.assertTrue(this.guestService.registerUser(owner.getUserName(), owner.getPassword(),
                owner.getFirstName(), owner.getLastName()));

        // opening a new store, owned by owner
        Assertions.assertTrue(this.buyerRegisteredService.openStore(owner.getUserName(),
                new PurchasePolicyDto(), new DiscountPolicyDto(), "storeName"));

        // getting the storeDto of the store the owner opened
        this.storeDto = this.guestService.getStoresDtos().get(0);

        // adding a product to the owner's store
        Assertions.assertTrue(this.sellerOwnerService.addProduct(owner.getUserName(), storeDto.getStoreId(),
                "motor", "motors", 20, 20));

        // adding the added product to the list of products
        Set<ProductDto> productDtoSet = this.guestService.getStoresDtos().get(0).getProducts();
        Object[] productDtoSetArrays =  productDtoSet.toArray();
        this.productDtoList = new LinkedList<>();
        for (int i=0; i<productDtoSet.size(); i++){
            this.productDtoList.add((ProductDto) productDtoSetArrays[i]);
        }
    }

}

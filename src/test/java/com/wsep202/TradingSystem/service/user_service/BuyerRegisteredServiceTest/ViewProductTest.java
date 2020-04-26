package com.wsep202.TradingSystem.service.user_service.BuyerRegisteredServiceTest;

import com.github.rozidan.springboot.modelmapper.WithModelMapper;
import com.wsep202.TradingSystem.config.TradingSystemConfiguration;
import com.wsep202.TradingSystem.domain.trading_system_management.UserSystem;
import com.wsep202.TradingSystem.service.user_service.BuyerRegisteredService;
import com.wsep202.TradingSystem.service.user_service.GuestService;
import com.wsep202.TradingSystem.service.user_service.SellerOwnerService;
import com.wsep202.TradingSystem.dto.DiscountPolicyDto;
import com.wsep202.TradingSystem.dto.ProductDto;
import com.wsep202.TradingSystem.dto.PurchasePolicyDto;
import com.wsep202.TradingSystem.dto.StoreDto;
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

// *********** UC 2.4.2 - viewing products ***********
public class ViewProductTest {
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
        this.buyerRegisteredService.clearDS();
    }

    /**
     * view a valid product in a valid store
     */
    @Test
    void viewValidProductInValidStore() {
        registerUser();
        openStoreAndAddProducts();
        ProductDto returnedProduct = buyerRegisteredService.viewProduct(this.productDto.getStoreId(), this.productDto.getProductSn());

        Assertions.assertEquals(this.productDto.getProductSn(), returnedProduct.getProductSn());
        Assertions.assertEquals(this.productDto.getName(), returnedProduct.getName());
        Assertions.assertEquals(this.productDto.getCategory(), returnedProduct.getCategory());
        Assertions.assertEquals(this.productDto.getAmount(), returnedProduct.getAmount());
        Assertions.assertEquals(this.productDto.getCost(), returnedProduct.getCost());
        Assertions.assertEquals(this.productDto.getRank(), returnedProduct.getRank());
        Assertions.assertEquals(this.productDto.getStoreId(), returnedProduct.getStoreId());
    }

    /**
     * view a valid product in an invalid store
     */
    @Test
    void viewValidProductInInvalidStore() {
        registerUser();
        openStoreAndAddProducts();
        ProductDto returnedProduct = buyerRegisteredService.viewProduct(
                this.storeDto.getStoreId()+5, this.productDto.getProductSn());
        Assertions.assertNull(returnedProduct);
    }

    /**
     * view an invalid product in a invalid store
     */
    @Test
    void viewInvalidProductInValidStore() {
        registerUser();
        openStoreAndAddProducts();
        ProductDto returnedProduct = buyerRegisteredService.viewProduct(
                this.storeDto.getStoreId(), this.productDto.getProductSn()+5);
        Assertions.assertNull(returnedProduct);
    }

    /**
     * view an invalid product in an invalid store
     */
    @Test
    void viewInvalidProductInInvalidStore() {
        registerUser();
        openStoreAndAddProducts();
        ProductDto returnedProduct = buyerRegisteredService.viewProduct(
                this.storeDto.getStoreId()+5, this.productDto.getProductSn()+5);
        Assertions.assertNull(returnedProduct);
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
                new PurchasePolicyDto(), new DiscountPolicyDto(), "storeName", uuid));

        // getting the storeDto of the store the owner opened
        this.storeDto = this.buyerRegisteredService.getStoresDtos().get(0);

        // adding a product to the owner's store
        Assertions.assertTrue(this.sellerOwnerService.addProduct(owner.getUserName(), storeDto.getStoreId(),
                "motor", "motors", 20, 20, uuid));

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

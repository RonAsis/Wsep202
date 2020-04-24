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

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TradingSystemConfiguration.class, GuestService.class, BuyerRegisteredService.class, SellerOwnerService.class})
@SpringBootTest(args = {"admin","admin"})
@WithModelMapper

// *********** UC 2.5.2.3 (inherited from guest) - filtering by store category ***********
public class FilterByStoreCategoryTest {
    @Autowired
    GuestService guestService;
    @Autowired
    BuyerRegisteredService buyerRegisteredService;
    @Autowired
    SellerOwnerService sellerOwnerService;

    StoreDto storeDto;
    UserSystem owner;
    List<ProductDto> productDtoList;

    @BeforeEach
    void setUp() {

    }

    @AfterEach
    void tearDown() {

    }

    /**
     * filter a valid list.
     * productDtoList has one product.
     */
    @Test
    void filterValidListReturnAllProducts() {
        openStoreAndAddProducts();
        String productCategory = "motors";
        Assertions.assertEquals(this.productDtoList, buyerRegisteredService.filterByStoreCategory(this.productDtoList, productCategory));
    }

    /**
     * filter a valid list.
     * productDtoList has one product and its category is "motors".
     */
    @Test
    void filterValidListReturnEmptyList() {
        openStoreAndAddProducts();
        String productCategory = "electronics";
        Assertions.assertEquals(new LinkedList<>(), buyerRegisteredService.filterByStoreCategory(this.productDtoList, productCategory));
    }

    /**
     * filter an empty list
     */
    @Test
    void filterEmptyList() {
        String productCategory = "electronics";
        List<ProductDto> products = new LinkedList<>();
        Assertions.assertEquals(new LinkedList<>(), buyerRegisteredService.filterByStoreCategory(products, productCategory));
    }

    /**
     * filter a valid list.
     * productDtoList has 5 products and their categories are: "motors"(2), "electronics"(1), "health"(2).
     */
    @Test
    void filterValidList() {
        openStoreAndAddProducts();
        addProductToStore();
        List<ProductDto> products = new LinkedList<>();
        String productCategory = "electronics";
        for (int i=0; i<this.productDtoList.size(); i++){
            if (this.productDtoList.get(i).getCategory().toLowerCase().equals(productCategory)){
                products.add(this.productDtoList.get(i));
            }
        }
        Assertions.assertEquals(products, buyerRegisteredService.filterByStoreCategory(this.productDtoList, productCategory));
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
        Assertions.assertTrue(this.buyerRegisteredService.openStore(owner.getUserName(), new PurchasePolicyDto(),
                new DiscountPolicyDto(), "storeName"));

        // getting the storeDto of the store the owner opened
        this.storeDto = this.buyerRegisteredService.getStoresDtos().get(0);

        // adding a product to the owner's store
        Assertions.assertTrue(this.sellerOwnerService.addProduct(owner.getUserName(), storeDto.getStoreId(),
                "motor", "motors", 20, 20));

        // adding the added product to the list of products
        Set<ProductDto> productDtoSet = this.buyerRegisteredService.getStoresDtos().get(0).getProducts();
        Object[] productDtoSetArrays =  productDtoSet.toArray();
        this.productDtoList = new LinkedList<>();
        for (int i=0; i<productDtoSet.size(); i++){
            this.productDtoList.add((ProductDto) productDtoSetArrays[i]);
        }
    }

    /**
     * adding a product to the store that owner opened
     */
    void addProductToStore(){
        // adding products to the owner's store
        Assertions.assertTrue(this.sellerOwnerService.addProduct(owner.getUserName(), storeDto.getStoreId(),
                "motor2", "electronics", 20, 50));

        Assertions.assertTrue(this.sellerOwnerService.addProduct(owner.getUserName(), storeDto.getStoreId(),
                "motor3", "motors", 20, 70));

        Assertions.assertTrue(this.sellerOwnerService.addProduct(owner.getUserName(), storeDto.getStoreId(),
                "motor4", "motors", 20, 80));

        Assertions.assertTrue(this.sellerOwnerService.addProduct(owner.getUserName(), storeDto.getStoreId(),
                "motor5", "health", 20, 100));

        // adding the added product to the list of products
        Set<ProductDto> productDtoSet = this.buyerRegisteredService.getStoresDtos().get(0).getProducts();
        Object[] productDtoSetArrays =  productDtoSet.toArray();
        this.productDtoList = new LinkedList<>();
        for (int i=0; i<productDtoSet.size(); i++){
            this.productDtoList.add((ProductDto) productDtoSetArrays[i]);
        }
    }

}

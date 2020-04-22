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
import org.springframework.security.access.method.P;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TradingSystemConfiguration.class, GuestService.class, BuyerRegisteredService.class, SellerOwnerService.class})
@SpringBootTest(args = {"admin","admin"})
@WithModelMapper

// *********** UC 2.5.1.2 - searching product by category ***********
public class SearchProductByCategoryTest {
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
        this.guestService.clearDS();
    }

    /**
     * productDtoList has one product.
     */
    @Test
    void searchAndReturnAllProducts() {
        openStoreAndAddProducts();
        String productCategory = "motors";
        Assertions.assertEquals(this.productDtoList, guestService.searchProductByCategory(productCategory));
    }

    /**
     * productDtoList has one product and its category is "motors".
     */
    @Test
    void searchAndReturnEmptyList() {
        openStoreAndAddProducts();
        String productCategory = "electronics";
        Assertions.assertEquals(new LinkedList<>(), guestService.searchProductByCategory(productCategory));
    }

    /**
     * search while there are no products in the stores.
     */
    @Test
    void searchWithNoProducts() {
        String productCategory = "electronics";
        Assertions.assertEquals(new LinkedList<>(), guestService.searchProductByCategory(productCategory));
    }

    /**
     * productDtoList has 5 products and their categories are: "motors"(2), "electronics"(1), "health"(2).
     */
    @Test
    void searchProducts() {
        openStoreAndAddProducts();
        addProductToStore();
        List<ProductDto> products = new LinkedList<>();
        String productCategory = "MOTORS";
        for (ProductDto productDto : this.productDtoList) {
            if (productDto.getCategory().equals(productCategory)) {
                products.add(productDto);
            }
        }
        List<ProductDto> returnedProducts = guestService.searchProductByCategory("motors");
        boolean sizesEqual = products.size() == returnedProducts.size();
        Assertions.assertTrue(sizesEqual);
        if (sizesEqual){
            Assertions.assertTrue(products.containsAll(returnedProducts));
        }
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
        Set<ProductDto> productDtoSet = this.guestService.getStoresDtos().get(0).getProducts();
        Object[] productDtoSetArrays =  productDtoSet.toArray();
        this.productDtoList = new LinkedList<>();
        for (int i=0; i<productDtoSet.size(); i++){
            this.productDtoList.add((ProductDto) productDtoSetArrays[i]);
        }
    }
}


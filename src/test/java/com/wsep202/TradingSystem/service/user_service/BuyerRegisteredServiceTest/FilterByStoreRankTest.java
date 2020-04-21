package com.wsep202.TradingSystem.service.user_service.BuyerRegisteredServiceTest;

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

// *********** UC 2.5.2.4 (inherited from guest) - filtering by store rank ***********
public class FilterByStoreRankTest {
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
        this.buyerRegisteredService.clearDS();
    }

    /**
     * filter a valid list, rank = 0
     * productDtoList has one product and the store it's in is ranked 0.
     */
    @Test
    void filterValidListReturnAllProducts() {
        openStoreAndAddProducts();
        Assertions.assertEquals(this.productDtoList, guestService.filterByStoreRank(this.productDtoList, 0));
    }

    /**
     * filter a valid list, rank = 5
     * productDtoList has one product and the store it's in is ranked 0.
     */
    @Test
    void filterValidListReturnEmptyList() {
        openStoreAndAddProducts();
        Assertions.assertEquals(new LinkedList<>(), guestService.filterByStoreRank(this.productDtoList, 5));
    }

    /**
     * filter an empty list
     */
    @Test
    void filterEmptyList() {
        List<ProductDto> products = new LinkedList<>();
        Assertions.assertEquals(new LinkedList<>(), guestService.filterByStoreRank(products, 0));
    }

//    /**
//     * filter a valid list, rank = 5
//     * productDtoList has one product and the store it's in is ranked 0.
//     */
//    @Test
//    void filterValidList() {
//        openStoreAndAddProducts();
//        addProductToStore();
//        //openSecondStoreAndAddProducts();
//
//        List<ProductDto> products = new LinkedList<>();
//        int rank = 2;
//        for (int i=0; i<this.productDtoList.size(); i++){
//            if (this.productDtoList.get(i).getStoreId()>=rank){
//                products.add(this.productDtoList.get(i));
//            }
//        }
//        Assertions.assertEquals(products, guestService.filterByProductRank(this.productDtoList, rank));
//    }

    /**
     * opening a new store and adding a product to it
     */
    void openStoreAndAddProducts(){
        owner = new UserSystem("owner","name","lname","pass");
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
                "motor2", "motors", 20, 20));

        Assertions.assertTrue(this.sellerOwnerService.addProduct(owner.getUserName(), storeDto.getStoreId(),
                "motor3", "motors", 20, 20));

        Assertions.assertTrue(this.sellerOwnerService.addProduct(owner.getUserName(), storeDto.getStoreId(),
                "motor4", "motors", 20, 20));

        Assertions.assertTrue(this.sellerOwnerService.addProduct(owner.getUserName(), storeDto.getStoreId(),
                "motor5", "motors", 20, 20));

        // adding the added product to the list of products
        Set<ProductDto> productDtoSet = this.buyerRegisteredService.getStoresDtos().get(0).getProducts();
        Object[] productDtoSetArrays =  productDtoSet.toArray();
        this.productDtoList = new LinkedList<>();
        for (int i=0; i<productDtoSet.size(); i++){
            this.productDtoList.add((ProductDto) productDtoSetArrays[i]);
        }
    }

}

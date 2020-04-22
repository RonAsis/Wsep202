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
@ContextConfiguration(classes = {TradingSystemConfiguration.class, GuestService.class, BuyerRegisteredService.class, SellerOwnerService.class})
@SpringBootTest(args = {"admin","admin"})
@WithModelMapper

// *********** UC 2.5.2.1 - filtering by price range ***********
public class FilterByRangePriceTest {
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
     * filter a valid list, min=0, max=100.
     * productDtoList has one product, that costs 20.
     */
    @Test
    void filterValidListReturnAllProducts() {
        openStoreAndAddProducts();
        Assertions.assertEquals(this.productDtoList, guestService.filterByRangePrice(this.productDtoList, 0, 100));
    }

    /**
     * filter a valid list, min=0, max=10.
     * productDtoList has one product, that costs 20.
     */
    @Test
    void filterValidListReturnEmptyList() {
        openStoreAndAddProducts();
        Assertions.assertEquals(new LinkedList<>(), guestService.filterByRangePrice(this.productDtoList, 0, 10));
    }

    /**
     * filter an empty list
     */
    @Test
    void filterEmptyList() {
        List<ProductDto> products = new LinkedList<>();
        Assertions.assertEquals(new LinkedList<>(), guestService.filterByRangePrice(products, 0, 100));
    }

    /**
     * filter a valid list.
     * productDtoList has 5 products, that cost 20, 50, 70, 80, 100.
     */
    @Test
    void filterValidList() {
        openStoreAndAddProducts();
        addProductToStore();
        List<ProductDto> products = new LinkedList<>();
        int min = 50;
        int max = 80;
        for (int i=0; i<this.productDtoList.size(); i++){
            if (this.productDtoList.get(i).getCost()>=min && this.productDtoList.get(i).getCost()<=max){
                products.add(this.productDtoList.get(i));
            }
        }
        Assertions.assertEquals(products, guestService.filterByRangePrice(this.productDtoList, min, max));
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
                "motor2", "motors", 20, 50));

        Assertions.assertTrue(this.sellerOwnerService.addProduct(owner.getUserName(), storeDto.getStoreId(),
                "motor3", "motors", 20, 70));

        Assertions.assertTrue(this.sellerOwnerService.addProduct(owner.getUserName(), storeDto.getStoreId(),
                "motor4", "motors", 20, 80));

        Assertions.assertTrue(this.sellerOwnerService.addProduct(owner.getUserName(), storeDto.getStoreId(),
                "motor5", "motors", 20, 100));

        // adding the added product to the list of products
        Set<ProductDto> productDtoSet = this.guestService.getStoresDtos().get(0).getProducts();
        Object[] productDtoSetArrays =  productDtoSet.toArray();
        this.productDtoList = new LinkedList<>();
        for (int i=0; i<productDtoSet.size(); i++){
            this.productDtoList.add((ProductDto) productDtoSetArrays[i]);
        }
    }
}

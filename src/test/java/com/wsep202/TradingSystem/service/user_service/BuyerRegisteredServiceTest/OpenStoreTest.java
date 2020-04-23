package com.wsep202.TradingSystem.service.user_service.GuestServiceTest;

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

import java.util.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TradingSystemConfiguration.class, GuestService.class, BuyerRegisteredService.class, SellerOwnerService.class})
@SpringBootTest(args = {"admin","admin"})
@WithModelMapper

// *********** UC 3.2 - opening a store ***********
public class OpenStoreTest {
    @Autowired
    GuestService guestService;
    @Autowired
    BuyerRegisteredService buyerRegisteredService;
    @Autowired
    SellerOwnerService sellerOwnerService;

    StoreDto actualStoreDto;
    StoreDto returnedStoreDto;
    UserSystem owner;
    UserSystemDto ownerDto;
    List<ProductDto> productDtoList;

    @BeforeEach
    void setUp() {
        owner = new UserSystem("username", "name", "lname", "pass");
        registerUser();
    }

    @AfterEach
    void tearDown() {
        this.buyerRegisteredService.clearDS();
    }

    /**
     * open a store with owner's username=""
     */
    @Test
    void openStoreNoOwner() {
        Assertions.assertFalse(this.buyerRegisteredService.openStore("",
                new PurchasePolicyDto(), new DiscountPolicyDto(), "storeName"));
    }

    /**
     * open a store with owner's username=""
     */
    @Test
    void openStoreInvalidOwner() {
        Assertions.assertFalse(this.buyerRegisteredService.openStore("notAUser",
                new PurchasePolicyDto(), new DiscountPolicyDto(),"storeName"));
    }

    /**
     * open a store with empty store name.
     */
    @Test
    void openStoreEmptyStoreName() {
        Assertions.assertFalse(this.buyerRegisteredService.openStore(this.owner.getUserName(),
                new PurchasePolicyDto(), new DiscountPolicyDto(), ""));
    }

//    /**
//     *
//     */
//    @Test
//    void openStoreValidUser() {
//        // opening a new store, owned by owner
//        Assertions.assertTrue(this.buyerRegisteredService.openStore(owner.getUserName(), new PurchasePolicyDto(), new DiscountPolicyDto(),
//                "Open discount", "Buy immediately", "storeName"));
//
//        // getting the storeDto of the store the owner opened
//        this.returnedStoreDto = this.buyerRegisteredService.getStoresDtos().get(0);
//
//        // saving the actual store
//        Set<UserSystemDto> ownersList = new HashSet<>();
////        Set<StoreDto> ownedStoresList = new HashSet<>();
////        this.ownerDto = this.buyerRegisteredService.getUsersDtos().get(0);
////        Object[] mightBeStoreOwners = this.ownerDto.getOwnedStores().toArray();
////        StoreDto nj = (StoreDto) mightBeStoreOwners[0];
////        System.out.println(nj.getStoreId());
////        ownedStoresList.add(nj);
////        ownersList.add(new UserSystemDto(this.ownerDto.getUserName(), this.ownerDto.getPassword(),
////                this.ownerDto.getSalt(), this.ownerDto.getFirstName(), this.ownerDto.getLastName(),
////                this.ownerDto.getManagedStores(), ownedStoresList, this.ownerDto.getShoppingCart(),
////                false, this.ownerDto.getReceipts()));
//        this.actualStoreDto = new StoreDto(0, "storeName", new HashSet<>(),
//                new PurchasePolicyDto(true, "allow all purchases"),
//                new DiscountPolicyDto(true, "allow all discounts"),
//                "Open discount","Buy immediately", ownersList, new LinkedList<>(), 0);
//
//        Assertions.assertEquals(this.actualStoreDto.getStoreId(), this.returnedStoreDto.getStoreId());
//        Assertions.assertEquals(this.actualStoreDto.getStoreName(), this.returnedStoreDto.getStoreName());
//        Assertions.assertEquals(this.actualStoreDto.getOwners(), this.returnedStoreDto.getOwners());
//        Assertions.assertEquals(this.actualStoreDto.getDiscountType(), this.returnedStoreDto.getDiscountType());
//        Assertions.assertEquals(this.actualStoreDto.getPurchaseType(), this.returnedStoreDto.getPurchaseType());
//        Assertions.assertEquals(this.actualStoreDto.getDiscountPolicy(), this.returnedStoreDto.getDiscountPolicy());
//        Assertions.assertEquals(this.actualStoreDto.getPurchasePolicy(), this.returnedStoreDto.getPurchasePolicy());
//        Assertions.assertEquals(this.actualStoreDto.getProducts(), this.returnedStoreDto.getProducts());
//        Assertions.assertEquals(this.actualStoreDto.getReceipts(), this.returnedStoreDto.getReceipts());
//        Assertions.assertEquals(this.actualStoreDto.getRank(), this.returnedStoreDto.getRank());
//
//    }

    /**
     * register user into the system
     */
    private void registerUser() {
        Assertions.assertTrue(this.guestService.registerUser(owner.getUserName(), owner.getPassword(),
                owner.getFirstName(), owner.getLastName()));
    }
}
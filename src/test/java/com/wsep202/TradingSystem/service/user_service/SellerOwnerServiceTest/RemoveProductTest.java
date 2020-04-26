//package com.wsep202.TradingSystem.service.user_service.SellerOwnerServiceTest;
//
//import com.github.rozidan.springboot.modelmapper.WithModelMapper;
//import com.wsep202.TradingSystem.config.TradingSystemConfiguration;
//import com.wsep202.TradingSystem.domain.trading_system_management.UserSystem;
//import com.wsep202.TradingSystem.service.user_service.BuyerRegisteredService;
//import com.wsep202.TradingSystem.service.user_service.GuestService;
//import com.wsep202.TradingSystem.service.user_service.SellerOwnerService;
//import com.wsep202.TradingSystem.dto.DiscountPolicyDto;
//import com.wsep202.TradingSystem.dto.ProductDto;
//import com.wsep202.TradingSystem.dto.PurchasePolicyDto;
//import com.wsep202.TradingSystem.dto.StoreDto;
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
//@ExtendWith(SpringExtension.class)
//@ContextConfiguration(classes = {TradingSystemConfiguration.class, GuestService.class, BuyerRegisteredService.class, SellerOwnerService.class})
//@SpringBootTest(args = {"admin","admin"})
//@WithModelMapper
//
//// *********** UC 4.1.2 - removing a product ***********
//public class RemoveProductTest {
//    @Autowired
//    GuestService guestService;
//    @Autowired
//    BuyerRegisteredService buyerRegisteredService;
//    @Autowired
//    SellerOwnerService sellerOwnerService;
//
//    StoreDto storeDto;
//    UserSystem owner;
//    ProductDto productDto;
//
////    @BeforeEach
////    void setUp() {
////        openStoreAndAddProducts();
////    }
////
////    @AfterEach
////    void tearDown() {
////        this.sellerOwnerService.clearDS();
////    }
////
////    /**
////     * remove a valid product
////     */
////    @Test
////    void removeValidProduct() {
////        Assertions.assertTrue(this.sellerOwnerService.deleteProductFromStore(this.owner.getUserName(),
////                this.storeDto.getStoreId(), this.productDto.getProductSn()));
////    }
////
////    /**
////     * remove a valid product twice
////     */
////    @Test
////    void removeValidProductTwice() {
////        Assertions.assertTrue(this.sellerOwnerService.deleteProductFromStore(this.owner.getUserName(),
////                this.storeDto.getStoreId(), this.productDto.getProductSn()));
////        Assertions.assertFalse(this.sellerOwnerService.deleteProductFromStore(this.owner.getUserName(),
////                this.storeDto.getStoreId(), this.productDto.getProductSn()));
////    }
////
////    /**
////     * remove a valid product
////     * invalid owner
////     */
////    @Test
////    void removeValidProductInvalidOwner() {
////        Assertions.assertFalse(this.sellerOwnerService.deleteProductFromStore(this.owner.getUserName()+"Not",
////                this.storeDto.getStoreId(), this.productDto.getProductSn()));
////    }
////
////    /**
////     * remove a valid product
////     * invalid store
////     */
////    @Test
////    void removeValidProductInvalidStore() {
////        Assertions.assertFalse(this.sellerOwnerService.deleteProductFromStore(this.owner.getUserName(),
////                this.storeDto.getStoreId()+5, this.productDto.getProductSn()));
////    }
////
////    /**
////     * remove a valid product
////     * invalid owner
////     * invalid store
////     */
////    @Test
////    void removeValidProductInvalidOwnerInvalidStore() {
////        Assertions.assertFalse(this.sellerOwnerService.deleteProductFromStore(this.owner.getUserName()+"Not",
////                this.storeDto.getStoreId()+5, this.productDto.getProductSn()));
////    }
////
////    /**
////     * remove an invalid product
////     */
////    @Test
////    void removeInvalidProduct() {
////        Assertions.assertFalse(this.sellerOwnerService.deleteProductFromStore(this.owner.getUserName(),
////                this.storeDto.getStoreId(), this.productDto.getProductSn()+5));
////    }
////
////    /**
////     * remove an invalid product
////     * invalid owner
////     */
////    @Test
////    void removeInvalidProductInvalidOwner() {
////        Assertions.assertFalse(this.sellerOwnerService.deleteProductFromStore(this.owner.getUserName()+"Not",
////                this.storeDto.getStoreId(), this.productDto.getProductSn()+5));
////    }
////
////    /**
////     * remove an invalid product
////     * invalid store
////     */
////    @Test
////    void removeInvalidProductInvalidStore() {
////        Assertions.assertFalse(this.sellerOwnerService.deleteProductFromStore(this.owner.getUserName(),
////                this.storeDto.getStoreId()+5, this.productDto.getProductSn()+5));
////    }
////
////    /**
////     * opening a new store and adding a product to it
////     */
////    void openStoreAndAddProducts(){
////        this.owner = new UserSystem("owner","name","lname","pass");
////        // registering the owner
////        Assertions.assertTrue(this.guestService.registerUser(owner.getUserName(), owner.getPassword(),
////                owner.getFirstName(), owner.getLastName()));
////
////        // opening a new store, owned by owner
////        Assertions.assertTrue(this.buyerRegisteredService.openStore(owner.getUserName(),
////                new PurchasePolicyDto(), new DiscountPolicyDto(), "storeName", uuid));
////
////        // getting the storeDto of the store the owner opened
////        this.storeDto = this.guestService.getStoresDtos().get(0);
////
////        // adding a product to the owner's store
////        Assertions.assertTrue(this.sellerOwnerService.addProduct(owner.getUserName(), storeDto.getStoreId(),
////                "motor", "motors", 20, 20, uuid));
////
////        // getting the productDto of the added product
////        this.productDto = (ProductDto) this.guestService.getStoresDtos().get(0).getProducts().toArray()[0];
////    }
//}

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
//// *********** UC 4.1.3 - editing a product’s detail ***********
//public class EditProductTest {
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
////
////    @BeforeEach
////    void setUp() {
////        openStoreAndAddProducts();
////    }
//
////    @AfterEach
////    void tearDown() {
////        this.sellerOwnerService.clearDS();
////    }
////
////    /**
////     * edit a valid product, don't change it
////     */
////    @Test
////    void EditValidProductNoChanges() {
////        Assertions.assertTrue(this.sellerOwnerService.editProduct(this.owner.getUserName(),
////                this.storeDto.getStoreId(), this.productDto.getProductSn(), this.productDto.getName(),
////                this.productDto.getCategory(), this.productDto.getAmount(), this.productDto.getCost()));
////    }
////
////    /**
////     * edit a valid product, change name
////     */
////    @Test
////    void EditValidProductChangeName() {
////        Assertions.assertTrue(this.sellerOwnerService.editProduct(this.owner.getUserName(),
////                this.storeDto.getStoreId(), this.productDto.getProductSn(), "New productName",
////                this.productDto.getCategory(), this.productDto.getAmount(), this.productDto.getCost()));
////    }
////
////    /**
////     * edit a valid product, change category
////     */
////    @Test
////    void EditValidProductChangeCategory() {
////        Assertions.assertTrue(this.sellerOwnerService.editProduct(this.owner.getUserName(),
////                this.storeDto.getStoreId(), this.productDto.getProductSn(), this.productDto.getName(),
////                "health", this.productDto.getAmount(), this.productDto.getCost()));
////    }
////    /**
////     * edit a valid product, change amount
////     */
////    @Test
////    void EditValidProductChangeAmount() {
////        Assertions.assertTrue(this.sellerOwnerService.editProduct(this.owner.getUserName(),
////                this.storeDto.getStoreId(), this.productDto.getProductSn(), this.productDto.getName(),
////                this.productDto.getCategory(), this.productDto.getAmount()+100, this.productDto.getCost()));
////    }
////
////    /**
////     * edit a valid product, change cost
////     */
////    @Test
////    void EditValidProductChangeCost() {
////        Assertions.assertTrue(this.sellerOwnerService.editProduct(this.owner.getUserName(),
////                this.storeDto.getStoreId(), this.productDto.getProductSn(), this.productDto.getName(),
////                this.productDto.getCategory(), this.productDto.getAmount(), this.productDto.getCost()+100));
////    }
////
////    /**
////     * edit a valid product, change name, category, amount and cost
////     */
////    @Test
////    void EditValidProductChangeNameCategoryAmountCost() {
////        Assertions.assertTrue(this.sellerOwnerService.editProduct(this.owner.getUserName(),
////                this.storeDto.getStoreId(), this.productDto.getProductSn(), "new "+this.productDto.getName(),
////                "health", this.productDto.getAmount()+100, this.productDto.getCost()+100));
////    }
////
////    /**
////     * edit a valid product
////     * invalid owner
////     */
////    @Test
////    void EditValidProductInvalidOwner() {
////        Assertions.assertFalse(this.sellerOwnerService.editProduct(this.owner.getUserName()+"Not",
////                this.storeDto.getStoreId(), this.productDto.getProductSn(), this.productDto.getName(),
////                this.productDto.getCategory(), this.productDto.getAmount(), this.productDto.getCost()));
////    }
////
////    /**
////     * edit a valid product
////     * invalid store
////     */
////    @Test
////    void EditValidProductInvalidStore() {
////        Assertions.assertFalse(this.sellerOwnerService.editProduct(this.owner.getUserName(),
////                this.storeDto.getStoreId()+5, this.productDto.getProductSn(), this.productDto.getName(),
////                this.productDto.getCategory(), this.productDto.getAmount(), this.productDto.getCost()));
////    }
////
////    /**
////     * edit a valid product
////     * invalid owner
////     * invalid store
////     */
////    @Test
////    void EditValidProductInvalidOwnerInvalidStore() {
////        Assertions.assertFalse(this.sellerOwnerService.editProduct(this.owner.getUserName()+"Not",
////                this.storeDto.getStoreId()+5, this.productDto.getProductSn(), this.productDto.getName(),
////                this.productDto.getCategory(), this.productDto.getAmount(), this.productDto.getCost()));
////    }
////
////    /**
////     * invalid product
////     */
////    @Test
////    void EditInvalidProduct() {
////        Assertions.assertFalse(this.sellerOwnerService.editProduct(this.owner.getUserName(),
////                this.storeDto.getStoreId(), this.productDto.getProductSn()+5, this.productDto.getName(),
////                this.productDto.getCategory(), this.productDto.getAmount(), this.productDto.getCost()));
////    }
////
////    /**
////     * invalid product
////     * invalid owner
////     */
////    @Test
////    void EditInvalidProductInvalidOwner() {
////        Assertions.assertFalse(this.sellerOwnerService.editProduct(this.owner.getUserName()+"Not",
////                this.storeDto.getStoreId(), this.productDto.getProductSn()+5, this.productDto.getName(),
////                this.productDto.getCategory(), this.productDto.getAmount(), this.productDto.getCost()));
////    }
////
////    /**
////     * invalid product
////     * invalid store
////     */
////    @Test
////    void EditInvalidProductInvalidStore() {
////        Assertions.assertFalse(this.sellerOwnerService.editProduct(this.owner.getUserName(),
////                this.storeDto.getStoreId()+5, this.productDto.getProductSn()+5, this.productDto.getName(),
////                this.productDto.getCategory(), this.productDto.getAmount(), this.productDto.getCost()));
////    }
////
////    /**
////     * invalid product
////     * invalid owner
////     * invalid store
////     */
////    @Test
////    void EditInvalidProductInvalidOwnerInvalidStore() {
////        Assertions.assertFalse(this.sellerOwnerService.editProduct(this.owner.getUserName()+"Not",
////                this.storeDto.getStoreId()+5, this.productDto.getProductSn()+5, this.productDto.getName(),
////                this.productDto.getCategory(), this.productDto.getAmount(), this.productDto.getCost()));
////    }
////
////    /**
////     * edit a valid product
////     * invalid category
////     */
////    @Test
////    void EditValidProductInvalidCategory() {
////        Assertions.assertTrue(this.sellerOwnerService.editProduct(this.owner.getUserName(),
////                this.storeDto.getStoreId(), this.productDto.getProductSn(), this.productDto.getName(),
////                this.productDto.getCategory(), this.productDto.getAmount(), this.productDto.getCost()));
////    }
////
////    /**
////     * edit a valid product
////     * invalid owner
////     * invalid category
////     */
////    @Test
////    void EditValidProductInvalidOwnerInvalidCategory() {
////        Assertions.assertFalse(this.sellerOwnerService.editProduct(this.owner.getUserName()+"Not",
////                this.storeDto.getStoreId(), this.productDto.getProductSn(), this.productDto.getName(),
////                this.productDto.getCategory()+"Not", this.productDto.getAmount(), this.productDto.getCost()));
////    }
////
////    /**
////     * edit a valid product
////     * invalid store
////     * invalid category
////     */
////    @Test
////    void EditValidProductInvalidStoreInvalidCategory() {
////        Assertions.assertFalse(this.sellerOwnerService.editProduct(this.owner.getUserName(),
////                this.storeDto.getStoreId()+5, this.productDto.getProductSn(), this.productDto.getName(),
////                this.productDto.getCategory()+"Not", this.productDto.getAmount(), this.productDto.getCost()));
////    }
////
////    /**
////     * edit a valid product
////     * invalid owner
////     * invalid store
////     * invalid category
////     */
////    @Test
////    void EditValidProductInvalidOwnerInvalidStoreInvalidCategory() {
////        Assertions.assertFalse(this.sellerOwnerService.editProduct(this.owner.getUserName()+"Not",
////                this.storeDto.getStoreId()+5, this.productDto.getProductSn(), this.productDto.getName(),
////                this.productDto.getCategory()+"Not", this.productDto.getAmount(), this.productDto.getCost()));
////    }
////
////    /**
////     * invalid product
////     * invalid category
////     */
////    @Test
////    void EditInvalidProductInvalidCategory() {
////        Assertions.assertFalse(this.sellerOwnerService.editProduct(this.owner.getUserName(),
////                this.storeDto.getStoreId(), this.productDto.getProductSn()+5, this.productDto.getName(),
////                this.productDto.getCategory()+"Not", this.productDto.getAmount(), this.productDto.getCost()));
////    }
////
////    /**
////     * invalid product
////     * invalid owner
////     * invalid category
////     */
////    @Test
////    void EditInvalidProductInvalidOwnerInvalidCategory() {
////        Assertions.assertFalse(this.sellerOwnerService.editProduct(this.owner.getUserName()+"Not",
////                this.storeDto.getStoreId(), this.productDto.getProductSn()+5, this.productDto.getName(),
////                this.productDto.getCategory()+"Not", this.productDto.getAmount(), this.productDto.getCost()));
////    }
////
////    /**
////     * invalid product
////     * invalid store
////     * invalid category
////     */
////    @Test
////    void EditInvalidProductInvalidStoreInvalidCategory() {
////        Assertions.assertFalse(this.sellerOwnerService.editProduct(this.owner.getUserName(),
////                this.storeDto.getStoreId()+5, this.productDto.getProductSn()+5, this.productDto.getName(),
////                this.productDto.getCategory()+"Not", this.productDto.getAmount(), this.productDto.getCost()));
////    }
////
////    /**
////     * invalid product
////     * invalid owner
////     * invalid store
////     * invalid category
////     */
////    @Test
////    void EditInvalidProductInvalidOwnerInvalidStoreInvalidCategory() {
////        Assertions.assertFalse(this.sellerOwnerService.editProduct(this.owner.getUserName()+"Not",
////                this.storeDto.getStoreId()+5, this.productDto.getProductSn()+5, this.productDto.getName(),
////                this.productDto.getCategory()+"Not", this.productDto.getAmount(), this.productDto.getCost()));
////    }
////    /**
////     * edit a valid product
////     * invalid owner
////     * invalid amount
////     */
////    @Test
////    void EditValidProductInvalidOwnerInvalidAmount() {
////        Assertions.assertFalse(this.sellerOwnerService.editProduct(this.owner.getUserName()+"Not",
////                this.storeDto.getStoreId(), this.productDto.getProductSn(), this.productDto.getName(),
////                this.productDto.getCategory(), this.productDto.getAmount()-100, this.productDto.getCost()));
////    }
////
////    /**
////     * edit a valid product
////     * invalid store
////     */
////    @Test
////    void EditValidProductInvalidStoreInvalidCost() {
////        Assertions.assertFalse(this.sellerOwnerService.editProduct(this.owner.getUserName(),
////                this.storeDto.getStoreId()+5, this.productDto.getProductSn(), this.productDto.getName(),
////                this.productDto.getCategory(), this.productDto.getAmount(), this.productDto.getCost()-100));
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
////        this.productDto.setCategory("motors");
////    }
//}

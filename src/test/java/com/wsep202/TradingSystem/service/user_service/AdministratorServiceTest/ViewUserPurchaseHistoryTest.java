package com.wsep202.TradingSystem.service.user_service.AdministratorServiceTest;

import com.github.rozidan.springboot.modelmapper.WithModelMapper;
import com.wsep202.TradingSystem.config.TradingSystemConfiguration;
import com.wsep202.TradingSystem.domain.trading_system_management.CardAction;
import com.wsep202.TradingSystem.domain.trading_system_management.UserSystem;
import com.wsep202.TradingSystem.service.user_service.*;
import com.wsep202.TradingSystem.dto.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.LinkedList;
import java.util.List;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TradingSystemConfiguration.class, GuestService.class, BuyerRegisteredService.class, SellerOwnerService.class, AdministratorService.class})
@SpringBootTest(args = {"admin","admin"})
@WithModelMapper

// *********** UC 6.4.2 - viewing user's purchase history ***********
public class ViewUserPurchaseHistoryTest {

    @Autowired
    GuestService guestService;
    @Autowired
    BuyerRegisteredService buyerRegisteredService;
    @Autowired
    SellerOwnerService sellerOwnerService;
    @Autowired
    AdministratorService administratorService;

    StoreDto storeDto;
    UserSystem owner;
    UserSystemDto admin;
    ProductDto productDto;
    List<ReceiptDto> receiptDto;

    @BeforeEach
    void setUp() {
        openStoreAndRegisterOwner();
        this.admin = this.administratorService.getAdministratorsDtos().get(0);
    }

    @AfterEach
    void tearDown() {

    }

    /**
     * view the history purchase of a valid user
     * no purchases
     */
    @Test
    void ViewHistoryNoPurchases() {
        List<ReceiptDto> returnedHistory = this.administratorService.viewPurchaseHistory(
                this.admin.getUserName(), this.owner.getUserName());
        Assertions.assertEquals(new LinkedList<>(), returnedHistory);
    }

    /**
     * view the history purchase of a valid user
     * invalid admin
     */
    @Test
    void ViewHistoryNoPurchasesInvalidAdmin() {
        Assertions.assertNull(this.administratorService.viewPurchaseHistory(
                this.admin.getUserName()+"Not", this.owner.getUserName()));
    }

    /**
     * view the history purchase of an invalid user
     */
    @Test
    void ViewHistoryNoPurchasesInvalidUser() {
        Assertions.assertNull(this.administratorService.viewPurchaseHistory(
                this.admin.getUserName(), this.owner.getUserName()+"Not"));
    }

    /**
     * view the history purchase of an invalid user
     * invalid admin
     */
    @Test
    void ViewHistoryNoPurchasesInvalidUserInvalidAdmin() {
        Assertions.assertNull(this.administratorService.viewPurchaseHistory(
                this.admin.getUserName()+"Not", this.owner.getUserName()+"Not"));
    }

    /**
     * view the history purchase of a valid user
     */
    @Test
    @Disabled
    void ViewHistoryPurchases() {
        buyProduct();
        List<ReceiptDto> returnedHistory = this.administratorService.viewPurchaseHistory(
                this.admin.getUserName(), this.owner.getUserName());
        Assertions.assertEquals(this.receiptDto.get(0).getReceiptSn(), returnedHistory.get(0).getReceiptSn());
    }


    /**
     * opening a new store and registering its owner
     */
    void openStoreAndRegisterOwner(){
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

        // getting the productDto of the added product
        this.productDto = (ProductDto) this.guestService.getStoresDtos().get(0).getProducts().toArray()[0];

    }

    /**
     * buying a product from the store
     */
    void buyProduct(){
        int amount = 1;
        Assertions.assertTrue(this.buyerRegisteredService.saveProductInShoppingBag(this.owner.getUserName(),
                this.storeDto.getStoreId(), this.productDto.getProductSn(), amount));

        BillingAddressDto billingAddress = new BillingAddressDto(this.owner.getFirstName()+" "+this.owner.getLastName(),
                "address", "city", "country", "1234567");
        PaymentDetailsDto paymentDetailsDto = new PaymentDetailsDto(CardAction.PAY, "123456789", "month",
                "year", "Cardholder", 798, "id");
        this.receiptDto = this.buyerRegisteredService.purchaseShoppingCartBuyer(this.owner.getUserName(),
                paymentDetailsDto, billingAddress);
        Assertions.assertNotNull(this.receiptDto);
        Assertions.assertEquals(amount, this.receiptDto.get(0).getProductBoughtAmountByProductSn(this.productDto.getProductSn()));
    }

}

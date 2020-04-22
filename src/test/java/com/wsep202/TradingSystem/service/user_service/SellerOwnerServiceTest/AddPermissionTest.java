package com.wsep202.TradingSystem.service.user_service.SellerOwnerServiceTest;

import com.github.rozidan.springboot.modelmapper.WithModelMapper;
import com.wsep202.TradingSystem.domain.config.TradingSystemConfiguration;
import com.wsep202.TradingSystem.domain.trading_system_management.UserSystem;
import com.wsep202.TradingSystem.service.user_service.BuyerRegisteredService;
import com.wsep202.TradingSystem.service.user_service.GuestService;
import com.wsep202.TradingSystem.service.user_service.SellerOwnerService;
import com.wsep202.TradingSystem.service.user_service.dto.DiscountPolicyDto;
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

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TradingSystemConfiguration.class, GuestService.class, BuyerRegisteredService.class, SellerOwnerService.class})
@SpringBootTest(args = {"admin","admin"})
@WithModelMapper

// *********** UC 4.6 - adding a permission to a manager ***********
public class AddPermissionTest {
    @Autowired
    GuestService guestService;
    @Autowired
    BuyerRegisteredService buyerRegisteredService;
    @Autowired
    SellerOwnerService sellerOwnerService;

    StoreDto storeDto;
    UserSystem owner;
    UserSystem manager;
    String permissionView = "view";
    String permissionEdit = "edit";

    @BeforeEach
    void setUp() {
        openStoreAndRegisterOwner();
        registerUserAndSetAsStoreManager();
    }

    @AfterEach
    void tearDown() {
        this.sellerOwnerService.clearDS();
    }
    /**
     * add a valid permission, that is already permitted
     */
    @Test
    void addAddedPermission() {
        Assertions.assertFalse(this.sellerOwnerService.addPermission(this.owner.getUserName(), this.storeDto.getStoreId(),
                this.manager.getUserName(), this.permissionView));
    }

    /**
     * add a valid permission, that isn't already permitted
     */
    @Test
    void addValidNotAddedPermission() {
        Assertions.assertTrue(this.sellerOwnerService.addPermission(this.owner.getUserName(), this.storeDto.getStoreId(),
                this.manager.getUserName(), this.permissionEdit));
    }

    /**
     * add an invalid permission
     */
    @Test
    void addInvalidPermission() {
        Assertions.assertFalse(this.sellerOwnerService.addPermission(this.owner.getUserName(), this.storeDto.getStoreId(),
                this.manager.getUserName(), this.permissionEdit+"Not"));
    }

    /**
     * add a valid permission, that is already permitted
     * invalid manager
     */
    @Test
    void addAddedPermissionInvalidManager() {
        Assertions.assertFalse(this.sellerOwnerService.addPermission(this.owner.getUserName(), this.storeDto.getStoreId(),
                this.manager.getUserName()+"Not", this.permissionView));
    }

    /**
     * add a valid permission, that isn't already permitted
     */
    @Test
    void addValidNotAddedPermissionInvalidManager() {
        Assertions.assertFalse(this.sellerOwnerService.addPermission(this.owner.getUserName(), this.storeDto.getStoreId(),
                this.manager.getUserName()+"Not", this.permissionEdit));
    }

    /**
     * add an invalid permission
     * invalid manager
     */
    @Test
    void addInvalidPermissionInvalidManager() {
        Assertions.assertFalse(this.sellerOwnerService.addPermission(this.owner.getUserName(), this.storeDto.getStoreId(),
                this.manager.getUserName()+"Not", this.permissionEdit+"Not"));
    }

    /**
     * add a valid permission, that is already permitted.
     * invalid owner
     */
    @Test
    void addAddedPermissionInvalidOwner() {
        Assertions.assertFalse(this.sellerOwnerService.addPermission(this.owner.getUserName()+"Not", this.storeDto.getStoreId(),
                this.manager.getUserName(), this.permissionView));
    }

    /**
     * add a valid permission, that isn't already permitted
     * invalid owner
     */
    @Test
    void addValidNotAddedPermissionInvalidOwner() {
        Assertions.assertFalse(this.sellerOwnerService.addPermission(this.owner.getUserName()+"Not", this.storeDto.getStoreId(),
                this.manager.getUserName(), this.permissionEdit));
    }

    /**
     * add an invalid permission
     * invalid owner
     */
    @Test
    void addInvalidPermissionInvalidOwner() {
        Assertions.assertFalse(this.sellerOwnerService.addPermission(this.owner.getUserName()+"Not", this.storeDto.getStoreId(),
                this.manager.getUserName(), this.permissionEdit+"Not"));
    }

    /**
     * add a valid permission, that is already permitted
     * invalid manager
     * invalid owner
     */
    @Test
    void addAddedPermissionInvalidManagerInvalidOwner() {
        Assertions.assertFalse(this.sellerOwnerService.addPermission(this.owner.getUserName()+"Not", this.storeDto.getStoreId(),
                this.manager.getUserName()+"Not", this.permissionView));
    }

    /**
     * add a valid permission, that isn't already permitted
     * invalid manager
     * invalid owner
     */
    @Test
    void addValidNotAddedPermissionInvalidManagerInvalidOwner() {
        Assertions.assertFalse(this.sellerOwnerService.addPermission(this.owner.getUserName()+"Not", this.storeDto.getStoreId(),
                this.manager.getUserName()+"Not", this.permissionEdit));
    }

    /**
     * add an invalid permission
     * invalid manager
     */
    @Test
    void addInvalidPermissionInvalidManagerInvalidOwner() {
        Assertions.assertFalse(this.sellerOwnerService.addPermission(this.owner.getUserName()+"Not", this.storeDto.getStoreId(),
                this.manager.getUserName()+"Not", this.permissionEdit+"Not"));
    }

    /**
     * add a valid permission, that is already permitted
     * invalid store
     */
    @Test
    void addAddedPermissionInvalidStore() {
      Assertions.assertFalse(this.sellerOwnerService.addPermission(this.owner.getUserName(), this.storeDto.getStoreId()+5,
               this.manager.getUserName(), this.permissionView));
    }

    /**
     * add a valid permission, that isn't already permitted
     * invalid store
     */
    @Test
    void addValidNotAddedPermissionInvalidStore() {
        Assertions.assertFalse(this.sellerOwnerService.addPermission(this.owner.getUserName(), this.storeDto.getStoreId()+5,
                this.manager.getUserName(), this.permissionEdit));
    }

    /**
     * add an invalid permission
     * invalid store
     */
    @Test
    void addInvalidPermissionInvalidStore() {
        Assertions.assertFalse(this.sellerOwnerService.addPermission(this.owner.getUserName(), this.storeDto.getStoreId()+5,
                this.manager.getUserName(), this.permissionEdit+"Not"));
    }

    /**
     * add a valid permission, that is already permitted
     * invalid manager
     * invalid store
     */
    @Test
    void addAddedPermissionInvalidManagerInvalidStore() {
        Assertions.assertFalse(this.sellerOwnerService.addPermission(this.owner.getUserName(), this.storeDto.getStoreId()+5,
                this.manager.getUserName()+"Not", this.permissionView));
    }

    /**
     * add a valid permission, that isn't already permitted
     * invalid store
     */
    @Test
    void addValidNotAddedPermissionInvalidManagerInvalidStore() {
        Assertions.assertFalse(this.sellerOwnerService.addPermission(this.owner.getUserName(), this.storeDto.getStoreId()+5,
                this.manager.getUserName()+"Not", this.permissionEdit));
    }

    /**
     * add an invalid permission
     * invalid manager
     * invalid store
     */
    @Test
    void addInvalidPermissionInvalidManagerInvalidStore() {
        Assertions.assertFalse(this.sellerOwnerService.addPermission(this.owner.getUserName(), this.storeDto.getStoreId()+5,
                this.manager.getUserName()+"Not", this.permissionEdit+"Not"));
    }

    /**
     * add a valid permission, that is already permitted.
     * invalid owner
     * invalid store
     */
    @Test
    void addAddedPermissionInvalidOwnerInvalidStore() {
        Assertions.assertFalse(this.sellerOwnerService.addPermission(this.owner.getUserName()+"Not", this.storeDto.getStoreId()+5,
                this.manager.getUserName(), this.permissionView));
    }

    /**
     * add a valid permission, that isn't already permitted
     * invalid owner
     * invalid store
     */
    @Test
    void addValidNotAddedPermissionInvalidOwnerInvalidStore() {
        Assertions.assertFalse(this.sellerOwnerService.addPermission(this.owner.getUserName()+"Not", this.storeDto.getStoreId()+5,
                this.manager.getUserName(), this.permissionEdit));
    }

    /**
     * add an invalid permission
     * invalid owner
     * invalid store
     */
    @Test
    void addInvalidPermissionInvalidOwnerInvalidStore() {
        Assertions.assertFalse(this.sellerOwnerService.addPermission(this.owner.getUserName()+"Not", this.storeDto.getStoreId()+5,
                this.manager.getUserName(), this.permissionEdit+"Not"));
    }

    /**
     * add a valid permission, that is already permitted
     * invalid manager
     * invalid owner
     * invalid store
     */
    @Test
    void addAddedPermissionInvalidManagerInvalidOwnerInvalidStore() {
        Assertions.assertFalse(this.sellerOwnerService.addPermission(this.owner.getUserName()+"Not", this.storeDto.getStoreId()+5,
                this.manager.getUserName()+"Not", this.permissionView));
    }

    /**
     * add a valid permission, that isn't already permitted
     * invalid manager
     * invalid owner
     * invalid store
     */
    @Test
    void addValidNotAddedPermissionInvalidManagerInvalidOwnerInvalidStore() {
        Assertions.assertFalse(this.sellerOwnerService.addPermission(this.owner.getUserName()+"Not", this.storeDto.getStoreId()+5,
                this.manager.getUserName()+"Not", this.permissionEdit));
    }

    /**
     * add an invalid permission
     * invalid manager
     * invalid store
     */
    @Test
    void addInvalidPermissionInvalidManagerInvalidOwnerInvalidStore() {
        Assertions.assertFalse(this.sellerOwnerService.addPermission(this.owner.getUserName()+"Not", this.storeDto.getStoreId()+5,
                this.manager.getUserName()+"Not", this.permissionEdit+"Not"));
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
    }

    /**
     * register user into the system and add him as a manager of storeDto
     */
    private void registerUserAndSetAsStoreManager() {
        this.manager = new UserSystem("manager", "name", "lname", "password");
        Assertions.assertTrue(this.guestService.registerUser(manager.getUserName(), manager.getPassword(),
                manager.getFirstName(), manager.getLastName()));
        Assertions.assertTrue(this.sellerOwnerService.addManager(this.owner.getUserName(),
                this.storeDto.getStoreId(), this.manager.getUserName()));
    }

}

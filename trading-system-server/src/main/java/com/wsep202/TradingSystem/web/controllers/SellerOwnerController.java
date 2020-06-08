package com.wsep202.TradingSystem.web.controllers;

import com.wsep202.TradingSystem.dto.*;
import com.wsep202.TradingSystem.service.user_service.SellerOwnerService;
import com.wsep202.TradingSystem.web.controllers.api.PublicApiPaths;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(PublicApiPaths.SELLER_OWNER_PATH)
@CrossOrigin(origins = "http://localhost:4200")
@Api(value = "API to seller owner", produces = "application/json")
@RequiredArgsConstructor
public class SellerOwnerController {

    private final SellerOwnerService sellerOwnerService;

    /**
     * View store purchase history
     */
    @ApiOperation(value = "view purchase history of owner")
    @GetMapping("view-purchase-history-of-owner/{ownerUsername}/{storeId}/{uuid}")
    public List<ReceiptDto> viewPurchaseHistoryOfOwner(@PathVariable String ownerUsername,
                                                       @PathVariable int storeId,
                                                       @PathVariable UUID uuid) {
        return sellerOwnerService.viewPurchaseHistoryOfStoreOwner(ownerUsername, storeId, uuid);
    }

    /**
     * add product
     */
    @ApiOperation(value = "add product")
    @PostMapping("add-product/{ownerUsername}/{storeId}/{productName}/{category}/{amount}/{cost}/{uuid}")
    public ProductDto addProduct(@PathVariable String ownerUsername,
                                 @PathVariable int storeId,
                                 @PathVariable String productName,
                                 @PathVariable String category,
                                 @PathVariable int amount,
                                 @PathVariable double cost,
                                 @PathVariable UUID uuid) {
        return sellerOwnerService.addProduct(ownerUsername, storeId, productName, category, amount, cost, uuid);
    }

    /**
     * remove product
     */
    @ApiOperation(value = "delete product from store")
    @PutMapping("delete-product-from-store/{ownerUsername}/{storeId}/{productSn}/{uuid}")
    public boolean deleteProductFromStore(@PathVariable String ownerUsername,
                                          @PathVariable int storeId,
                                          @PathVariable int productSn,
                                          @PathVariable UUID uuid) {
        return sellerOwnerService.deleteProductFromStore(ownerUsername, storeId, productSn, uuid);
    }

    /**
     * edit product
     */
    @ApiOperation(value = "edit product")
    @PutMapping("edit-product/{ownerUsername}/{storeId}/{productSn}/{productName}/{category}/{amount}/{cost}/{uuid}")
    public boolean editProduct(@PathVariable String ownerUsername,
                               @PathVariable int storeId,
                               @PathVariable int productSn,
                               @PathVariable String productName,
                               @PathVariable String category,
                               @PathVariable int amount,
                               @PathVariable double cost,
                               @PathVariable UUID uuid) {
        return sellerOwnerService.editProduct(ownerUsername, storeId, productSn, productName, category, amount, cost, uuid);
    }

    /**
     * add owner
     */
    @ApiOperation(value = "add owner")
    @PostMapping("add-owner/{ownerUsername}/{storeId}/{newOwnerUsername}/{uuid}")
    public boolean addOwner(@PathVariable String ownerUsername,
                            @PathVariable int storeId,
                            @PathVariable String newOwnerUsername,
                            @PathVariable UUID uuid) {
        return sellerOwnerService.addOwner(ownerUsername, storeId, newOwnerUsername, uuid);
    }

    /**
     * remove manager
     */
    @ApiOperation(value = "remove manager")
    @PostMapping("remove-manager/{ownerUsername}/{storeId}/{managerUsername}/{uuid}")
    public boolean removeManager(@PathVariable String ownerUsername,
                                 @PathVariable int storeId,
                                 @PathVariable String managerUsername,
                                 @PathVariable UUID uuid) {
        return sellerOwnerService.removeManager(ownerUsername, storeId, managerUsername, uuid);
    }

    /**
     * add permission
     */
    @ApiOperation(value = "add permission")
    @PutMapping("add-permission/{ownerUsername}/{storeId}/{managerUsername}/{permission}/{uuid}")
    public boolean addPermission(@PathVariable String ownerUsername,
                                 @PathVariable int storeId,
                                 @PathVariable String managerUsername,
                                 @PathVariable String permission,
                                 @PathVariable UUID uuid) {
        return sellerOwnerService.addPermission(ownerUsername, storeId, managerUsername, permission, uuid);
    }

    /**
     * get permission og manager
     */
    @ApiOperation(value = "add permission")
    @PutMapping("get-permission/{ownerUsername}/{storeId}/{managerUsername}/{uuid}")
    public List<String> getPermissionOfManager(@PathVariable String ownerUsername,
                                 @PathVariable int storeId,
                                 @PathVariable String managerUsername,
                                 @PathVariable UUID uuid) {
        return sellerOwnerService.getPermissionOfManager(ownerUsername, storeId, managerUsername, uuid);
    }

    /**
     * remove permission
     */
    @ApiOperation(value = "remove permission")
    @PutMapping("remove-permission/{ownerUsername}/{storeId}/{managerUsername}/{permission}/{uuid}")
    public boolean removePermission(@PathVariable String ownerUsername,
                                 @PathVariable int storeId,
                                 @PathVariable String managerUsername,
                                 @PathVariable String permission,
                                 @PathVariable UUID uuid) {
        return sellerOwnerService.removePermission(ownerUsername, storeId, managerUsername, permission, uuid);
    }

    /**
     * get permissions can add (all the permissions that manager can't do)
     */
    @ApiOperation(value = "get permissions can't do")
    @GetMapping("get-permissions-cant-do/{ownerUsername}/{storeId}/{managerUsername}/{uuid}")
    public List<String> getPermissionCantDo(@PathVariable String ownerUsername,
                                            @PathVariable int storeId,
                                            @PathVariable String managerUsername,
                                            @PathVariable UUID uuid) {
        return sellerOwnerService.getPermissionCantDo(ownerUsername, storeId, managerUsername, uuid);
    }

    /**
     * add manager
     */
    @ApiOperation(value = "add manager")
    @PostMapping("add-manager/{ownerUsername}/{storeId}/{newManagerUsername}/{uuid}")
    public ManagerDto addManager(@PathVariable String ownerUsername,
                                 @PathVariable int storeId,
                                 @PathVariable String newManagerUsername,
                                 @PathVariable UUID uuid) {
        return sellerOwnerService.addManager(ownerUsername, storeId, newManagerUsername, uuid);
    }

    /**
     * get Owner Stores
     */
    @ApiOperation(value = "get owner stores")
    @GetMapping("get-owner-stores/{ownerUsername}/{uuid}")
    public List<StoreDto> getOwnerStores(@PathVariable String ownerUsername,
                                         @PathVariable UUID uuid) {
        return sellerOwnerService.getOwnerStores(ownerUsername, uuid);
    }

    /**
     * get user not mangers not owner
     */
    @ApiOperation(value = "get user not manger not owner")
    @GetMapping("get-user-not-manger-not-owner/{ownerUsername}/{storeId}/{uuid}")
    public List<String> getAllUsernameNotOwnerNotManger(@PathVariable String ownerUsername,
                                         @PathVariable int storeId,
                                         @PathVariable UUID uuid) {
        return sellerOwnerService.getAllUsernameNotOwnerNotManger(ownerUsername, storeId,uuid);
    }

    /**
     * add manager
     */
    @ApiOperation(value = "get owner stores")
    @GetMapping("get-all-operation-manager/")
    public List<String> getAllOperationOfManger() {
        return sellerOwnerService.getAllOperationOfManger();
    }

    /**
     * get user not mangers not owner
     */
    @ApiOperation(value = "get my sub owners")
    @GetMapping("get-my-sub-owners/{ownerUsername}/{storeId}/{uuid}")
    public List<String> getMySubOwners(@PathVariable String ownerUsername,
                                                        @PathVariable int storeId,
                                                        @PathVariable UUID uuid) {
        return sellerOwnerService.getMySubOwners(ownerUsername, storeId,uuid);
    }

    /**
     * get user not mangers not owner
     */
    @ApiOperation(value = "get is owner")
    @GetMapping("is-owner/{username}/{storeId}/{uuid}")
    public boolean isOwner(@PathVariable String username,
                           @PathVariable int storeId,
                           @PathVariable UUID uuid) {
        return sellerOwnerService.isOwner(username, storeId,uuid);
    }

    /**
     * get user not mangers not owner
     */
    @ApiOperation(value = "get my sub owners")
    @GetMapping("get-my-sub-managers/{ownerUsername}/{storeId}/{uuid}")
    public List<ManagerDto> getMySubMangers(@PathVariable String ownerUsername,
                                       @PathVariable int storeId,
                                       @PathVariable UUID uuid) {
        return sellerOwnerService.getMySubMangers(ownerUsername, storeId,uuid);
    }

    /**
     * remove owner
     */
    @ApiOperation(value = "remove owner")
    @PostMapping("remove-owner/{ownerUsername}/{storeId}/{ownerToRemove}/{uuid}")
    public boolean removeOwner(@PathVariable String ownerUsername,
                                 @PathVariable int storeId,
                                 @PathVariable String ownerToRemove,
                                 @PathVariable UUID uuid) {
        return sellerOwnerService.removeOwner(ownerUsername, storeId, ownerToRemove, uuid);
    }

    /**
     * get my owner to approve
     */
    @ApiOperation(value = "get my owners to approve")
    @GetMapping("get-my-owner-to-approve/{ownerUsername}/{uuid}")
    public List<OwnerToApproveDto> getMyOwnerToApprove(@PathVariable String ownerUsername,
                                                       @PathVariable UUID uuid) {
        return sellerOwnerService.getMyOwnerToApprove(ownerUsername,uuid);
    }

    /**
     * approve owner
     */
    @ApiOperation(value = "approve owner")
    @PostMapping("approve-owner/{ownerUsername}/{storeId}/{ownerToApprove}/{status}/{uuid}")
    public boolean approveOwner(@PathVariable String ownerUsername,
                               @PathVariable int storeId,
                               @PathVariable String ownerToApprove,
                               @PathVariable boolean status,
                               @PathVariable UUID uuid) {
        return sellerOwnerService.approveOwner(ownerUsername, storeId, ownerToApprove, status, uuid);
    }

}

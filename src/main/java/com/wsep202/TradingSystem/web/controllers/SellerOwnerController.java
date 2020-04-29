package com.wsep202.TradingSystem.web.controllers;

import com.wsep202.TradingSystem.dto.ReceiptDto;
import com.wsep202.TradingSystem.dto.StoreDto;
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
    @GetMapping("view-purchase-history-of-owner/{userName}/{storeId}")
    public List<ReceiptDto> viewPurchaseHistoryOfOwner(@PathVariable String ownerUsername,
                                                       @PathVariable int storeId,
                                                       @RequestBody UUID uuid) {
        return sellerOwnerService.viewPurchaseHistoryOfStoreOwner(ownerUsername, storeId, uuid);
    }

    /**
     * add product
     */
    @ApiOperation(value = "add product")
    @PostMapping("add-product/{ownerUsername}/{storeId}/{productName}/{category}/{amount}/{cost}")
    public boolean addProduct(@PathVariable String ownerUsername,
                              @PathVariable int storeId,
                              @PathVariable String productName,
                              @PathVariable String category,
                              @PathVariable int amount,
                              @PathVariable double cost,
                              @RequestBody UUID uuid) {
        return sellerOwnerService.addProduct(ownerUsername, storeId, productName, category, amount, cost, uuid);
    }

    /**
     * remove product
     */
    @ApiOperation(value = "delete product from store")
    @PutMapping("delete-product-from-store/{ownerUsername}/{storeId}/{productSn}")
    public boolean deleteProductFromStore(@PathVariable String ownerUsername,
                                          @PathVariable int storeId,
                                          @PathVariable int productSn,
                                          @RequestBody UUID uuid) {
        return sellerOwnerService.deleteProductFromStore(ownerUsername, storeId, productSn, uuid);
    }

    /**
     * edit product
     */
    @ApiOperation(value = "edit product")
    @PutMapping("edit-product/{ownerUsername}/{storeId}/{productSn}/{productName}/{category}/{amount}/{cost}")
    public boolean editProduct(@PathVariable String ownerUsername,
                               @PathVariable int storeId,
                               @PathVariable int productSn,
                               @PathVariable String productName,
                               @PathVariable String category,
                               @PathVariable int amount,
                               @PathVariable double cost,
                               @RequestBody UUID uuid) {
        return sellerOwnerService.editProduct(ownerUsername, storeId, productSn, productName, category, amount, cost, uuid);
    }

    /**
     * add owner
     */
    @ApiOperation(value = "add owner")
    @PostMapping("add-owner/{ownerUsername}/{storeId}/{newOwnerUsername}")
    public boolean addOwner(@PathVariable String ownerUsername,
                            @PathVariable int storeId,
                            @PathVariable String newOwnerUsername,
                            @RequestBody UUID uuid) {
        return sellerOwnerService.addOwner(ownerUsername, storeId, newOwnerUsername, uuid);
    }

    /**
     * remove manager
     */
    @ApiOperation(value = "remove manager")
    @PostMapping("remove-manager/{ownerUsername}/{storeId}/{newManagerUsername}")
    public boolean removeManager(@PathVariable String ownerUsername,
                                 @PathVariable int storeId,
                                 @PathVariable String newManagerUsername,
                                 @RequestBody UUID uuid) {
        return sellerOwnerService.removeManager(ownerUsername, storeId, newManagerUsername, uuid);
    }

    /**
     * add permission
     */
    @ApiOperation(value = "add permission")
    @PutMapping("add-permission/{ownerUsername}/{storeId}/{newManagerUsername}/{permission}")
    public boolean addPermission(@PathVariable String ownerUsername,
                                 @PathVariable int storeId,
                                 @PathVariable String newManagerUsername,
                                 @PathVariable String permission,
                                 @RequestBody UUID uuid) {
        return sellerOwnerService.addPermission(ownerUsername, storeId, newManagerUsername, permission, uuid);
    }

    /**
     * add manager
     */
    @ApiOperation(value = "add manager")
    @PostMapping("add-manager/{ownerUsername}/{storeId}/{newManagerUsername}/{uuid}")
    public boolean addManager(@PathVariable String ownerUsername,
                              @PathVariable int storeId,
                              @PathVariable String newManagerUsername,
                              @PathVariable UUID uuid) {
        return sellerOwnerService.addManager(ownerUsername, storeId, newManagerUsername, uuid);
    }

    /**
     * add manager
     */
    @ApiOperation(value = "get owner stores")
    @GetMapping("get-owner-stores/{ownerUsername}/{uuid}")
    public List<StoreDto> getOwnerStores(@PathVariable String ownerUsername,
                                         @PathVariable UUID uuid) {
        return sellerOwnerService.getOwnerStores(ownerUsername, uuid);
    }
}

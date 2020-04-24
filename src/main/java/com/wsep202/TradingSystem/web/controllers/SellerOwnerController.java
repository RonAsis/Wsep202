package com.wsep202.TradingSystem.web.controllers;

import com.wsep202.TradingSystem.dto.ReceiptDto;
import com.wsep202.TradingSystem.service.user_service.SellerOwnerService;
import com.wsep202.TradingSystem.web.controllers.api.PublicApiPaths;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(PublicApiPaths.SELLER_OWNER_PATH)
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
                                                       @PathVariable int storeId){
        return sellerOwnerService.viewPurchaseHistoryOfOwner(ownerUsername,storeId);
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
                              @PathVariable double cost ){
        return sellerOwnerService.addProduct(ownerUsername, storeId, productName, category, amount, cost);
    }

    /**
     * remove product
     */
    @ApiOperation(value = "delete product from store")
    @PutMapping("delete-product-from-store/{ownerUsername}/{storeId}/{productSn}")
    public boolean deleteProductFromStore(@PathVariable String ownerUsername,
                                          @PathVariable int storeId,
                                          @PathVariable int productSn){
        return sellerOwnerService.deleteProductFromStore(ownerUsername, storeId, productSn);
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
                               @PathVariable double cost ){
        return sellerOwnerService.editProduct(ownerUsername, storeId, productSn, productName, category, amount, cost);
    }

    /**
     * add owner
     */
    @ApiOperation(value = "add owner")
    @PostMapping("add-owner/{ownerUsername}/{storeId}/{newOwnerUsername}")
    public boolean addOwner(@PathVariable String ownerUsername,
                            @PathVariable int storeId,
                            @PathVariable  String newOwnerUsername){
        return sellerOwnerService.addOwner(ownerUsername, storeId, newOwnerUsername);
    }

    /**
     * remove manager
     */
    @ApiOperation(value = "remove manager")
    @PostMapping("remove-manager/{ownerUsername}/{storeId}/{newManagerUsername}")
    public boolean removeManager(@PathVariable String ownerUsername,
                                 @PathVariable int storeId,
                                 @PathVariable String newManagerUsername){
        return sellerOwnerService.removeManager(ownerUsername, storeId, newManagerUsername);
    }

    /**
     * add permission
     */
    @ApiOperation(value = "add permission")
    @PutMapping("add-permission/{ownerUsername}/{storeId}/{newManagerUsername}/{permission}")
    public boolean addPermission(@PathVariable String ownerUsername,
                                 @PathVariable int storeId,
                                 @PathVariable String newManagerUsername,
                                 @PathVariable String permission){
        return sellerOwnerService.addPermission(ownerUsername, storeId, newManagerUsername, permission);
    }

    /**
     * add manager
     */
    @ApiOperation(value = "add manager")
    @PostMapping("add-manager/{ownerUsername}/{storeId}/{newManagerUsername}")
    public boolean addManager(@PathVariable String ownerUsername,
                              @PathVariable int storeId,
                              @PathVariable String newManagerUsername){
        return sellerOwnerService.addManager(ownerUsername, storeId, newManagerUsername);
    }
}

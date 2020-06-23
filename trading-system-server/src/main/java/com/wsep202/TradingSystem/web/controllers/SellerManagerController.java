package com.wsep202.TradingSystem.web.controllers;

import com.wsep202.TradingSystem.dto.*;
import com.wsep202.TradingSystem.service.user_service.SellerManagerService;
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
@RequestMapping(PublicApiPaths.SELLER_MANAGER_PATH)
@CrossOrigin(origins = "http://localhost:4200")
@Api(value = "API to seller manager", produces = "application/json")
@RequiredArgsConstructor
public class SellerManagerController {

    private final SellerManagerService sellerManagerService;

    /**
     * View store purchase history
     */
    @ApiOperation(value = "view purchase history of manager")
    @GetMapping("view-purchase-history-of-manager/{username}/{storeId}/{uuid}")
    public List<ReceiptDto> viewPurchaseHistoryOfManager(@PathVariable String username,
                                                         @PathVariable int storeId,
                                                         @PathVariable UUID uuid) {
        return sellerManagerService.viewPurchaseHistoryOfManager(username, storeId, uuid);
    }

    /**
     * add manager
     */
    @ApiOperation(value = "get manage stores")
    @GetMapping("get-manage-stores/{manageUsername}/{uuid}")
    public List<StoreDto> getManageStores(@PathVariable String manageUsername,
                                          @PathVariable UUID uuid) {
        return sellerManagerService.getMangeStores(manageUsername, uuid);
    }

    /**
     * add manager
     */
    @ApiOperation(value = "get operations can do")
    @GetMapping("get-operations-can-do/{manageUsername}/{storeId}/{uuid}")
    public List<String> getOperationsCanDo(@PathVariable String manageUsername,
                                           @PathVariable int storeId,
                                           @PathVariable UUID uuid) {
        return sellerManagerService.getOperationsCanDo(manageUsername, storeId, uuid);
    }

    /**
     * get store discounts
     */
    @ApiOperation(value = "get store discounts")
    @GetMapping("get-store-discounts/{username}/{storeId}/{uuid}")
    public List<DiscountDto> getStoreDiscounts(@PathVariable String username,
                                               @PathVariable int storeId,
                                               @PathVariable UUID uuid) {
        return sellerManagerService.getStoreDiscounts(username, storeId, uuid);
    }

    /**
     * get store discounts
     */
    @ApiOperation(value = "get composite operators")
    @GetMapping("get-composite-operators/{username}/{storeId}/{uuid}")
    public List<String> getCompositeOperators(@PathVariable String username,
                                              @PathVariable int storeId,
                                              @PathVariable UUID uuid) {
        return sellerManagerService.getCompositeOperators(username, storeId, uuid);
    }

    /**
     * get store discounts
     * @return
     */
    @ApiOperation(value = "get all Purchase Policies")
    @GetMapping("get-all-purchase-policies/{username}/{storeId}/{uuid}")
    public List<PurchasePolicyDto> getAllPurchasePolicies(@PathVariable String username,
                                                    @PathVariable int storeId,
                                                    @PathVariable UUID uuid) {
        return sellerManagerService.getAllStorePurchases(username, storeId, uuid);
    }
    
    /**
     * get store discounts
     */
    @ApiOperation(value = "get Discounts")
    @GetMapping("get-discounts/{username}/{storeId}/{uuid}")
    public List<DiscountDto> getAllDiscounts(@PathVariable String username,
                                          @PathVariable int storeId,
                                          @PathVariable UUID uuid) {
        return sellerManagerService.getAllDiscounts(username, storeId, uuid);
    }
    

    /**
     * add or edit discount
     */
    @ApiOperation(value = "add or edit discount")
    @PostMapping("add-discount/{username}/{storeId}/{uuid}")
    public DiscountDto addEditDiscount(@PathVariable String username,
                                       @PathVariable int storeId,
                                       @RequestBody DiscountDto discountDto,
                                       @PathVariable UUID uuid) {
        return sellerManagerService.addEditDiscount(username, storeId, discountDto, uuid);
    }

    /**
     * add or edit policy
     */
    @ApiOperation(value = "add or edit policy")
    @PostMapping("add-policy/{username}/{storeId}/{uuid}")
    public PurchasePolicyDto addEditPurchasePolicy(@PathVariable String username,
                                             @PathVariable int storeId,
                                             @RequestBody PurchasePolicyDto policyDto,
                                             @PathVariable UUID uuid) {
        return sellerManagerService.addEditPurchasePolicy(username, storeId, policyDto, uuid);
    }

    /**
     * get store purchase policies
     */
    @ApiOperation(value = "get simple Discounts")
    @GetMapping("get-simple-discounts/{username}/{storeId}/{uuid}")
    public List<DiscountDto> getSimpleDiscounts(@PathVariable String username,
                                                @PathVariable int storeId,
                                                @PathVariable UUID uuid) {
        return sellerManagerService.getSimpleDiscounts(username, storeId, uuid);
    }
}

package com.wsep202.TradingSystem.web.controllers;

import com.wsep202.TradingSystem.dto.DiscountDto;
import com.wsep202.TradingSystem.dto.ReceiptDto;
import com.wsep202.TradingSystem.dto.StoreDto;
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
     * remove discount
     */
    @ApiOperation(value = "remove discount")
    @PostMapping("remove-discount/{username}/{storeId}/{discountId}/{uuid}")
    public boolean removeDiscount(@PathVariable String username,
                                  @PathVariable int storeId,
                                  @PathVariable int discountId,
                                  @PathVariable UUID uuid) {
        return sellerManagerService.removeDiscount(username, storeId, discountId, uuid);
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
     */
    @ApiOperation(value = "get Discounts")
    @GetMapping("get-discounts/{username}/{storeId}/{uuid}")
    public List<DiscountDto> getDiscounts(@PathVariable String username,
                                          @PathVariable int storeId,
                                          @PathVariable UUID uuid) {
        return sellerManagerService.getDiscounts(username, storeId, uuid);
    }

    /**
     * add discount
     */
    @ApiOperation(value = "remove discount")
    @PostMapping("add-discount/{username}/{storeId}/{uuid}")
    public DiscountDto addDiscount(@PathVariable String username,
                                   @PathVariable int storeId,
                                   @RequestBody DiscountDto discountDto,
                                   @PathVariable UUID uuid) {
        return sellerManagerService.addDiscount(username, storeId, discountDto, uuid);
    }


}

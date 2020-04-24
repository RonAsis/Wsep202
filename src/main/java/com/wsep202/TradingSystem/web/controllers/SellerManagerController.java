package com.wsep202.TradingSystem.web.controllers;

import com.wsep202.TradingSystem.dto.ReceiptDto;
import com.wsep202.TradingSystem.service.user_service.SellerManagerService;
import com.wsep202.TradingSystem.web.controllers.api.PublicApiPaths;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(PublicApiPaths.SELLER_MANAGER_PATH)
@Api(value = "API to seller manager", produces = "application/json")
@RequiredArgsConstructor
public class SellerManagerController {

    private final SellerManagerService sellerManagerService;
    /**
     * View store purchase history
     */
    @ApiOperation(value = "view purchase history of manager")
    @GetMapping("view-purchase-history-of-manager/{userName}/{storeId}")
    public List<ReceiptDto> viewPurchaseHistoryOfManager(@PathVariable String userName,
                                                         @PathVariable int storeId){
        return sellerManagerService.viewPurchaseHistoryOfManager(userName, storeId);
    }

}

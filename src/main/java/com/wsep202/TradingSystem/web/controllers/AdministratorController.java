package com.wsep202.TradingSystem.web.controllers;

import com.wsep202.TradingSystem.dto.ReceiptDto;
import com.wsep202.TradingSystem.service.user_service.AdministratorService;
import com.wsep202.TradingSystem.web.controllers.api.PublicApiPaths;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(PublicApiPaths.ADMIN_PATH)
@Api(value = "API to admin", produces = "application/json")
@RequiredArgsConstructor
public class AdministratorController {

    private final AdministratorService administratorService;

    /**
     * View store purchase history
     */
    @ApiOperation(value = "View Purchase History Store")
    @GetMapping("view-purchase-history-store/{administratorUsername}/{storeId}")
    public List<ReceiptDto> viewPurchaseHistory(
            @PathVariable String administratorUsername,
            @PathVariable int storeId) {
        return administratorService.viewPurchaseHistory(administratorUsername, storeId);
    }

    /**
     * View buyer purchase history
     */
    @ApiOperation(value = "View Purchase History User")
    @GetMapping("view-purchase-history-user/{administratorUsername}/{userName}")
    public List<ReceiptDto> viewPurchaseHistory(@PathVariable String administratorUsername,
                                                @PathVariable String userName) {
        return administratorService.viewPurchaseHistory(administratorUsername, userName);
    }
}

package com.wsep202.TradingSystem.web.controllers;

import com.wsep202.TradingSystem.domain.trading_system_management.UserSystem;
import com.wsep202.TradingSystem.dto.DailyVisitorDto;
import com.wsep202.TradingSystem.dto.ReceiptDto;
import com.wsep202.TradingSystem.dto.RequestGetDailyVisitorsDto;
import com.wsep202.TradingSystem.dto.UserSystemDto;
import com.wsep202.TradingSystem.service.user_service.AdministratorService;
import com.wsep202.TradingSystem.web.controllers.api.PublicApiPaths;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(PublicApiPaths.ADMIN_PATH)
@CrossOrigin(origins = "http://localhost:4200")
@Api(value = "API to admin", produces = "application/json")
@RequiredArgsConstructor
public class AdministratorController {

    private final AdministratorService administratorService;

    /**
     * View store purchase history
     */
    @ApiOperation(value = "View Purchase History Store")
    @GetMapping("view-purchase-history-store/{administratorUsername}/{storeId}/{uuid}")
    public List<ReceiptDto> viewPurchaseHistory(
            @PathVariable String administratorUsername,
            @PathVariable int storeId,
            @PathVariable UUID uuid) {
        return administratorService.viewPurchaseHistory(administratorUsername, storeId, uuid);
    }
    /**
     * View buyer purchase history
     */

    @ApiOperation(value = "View Purchase History User")
    @GetMapping("view-purchase-history-user/{administratorUsername}/{username}/{uuid}")
    public List<ReceiptDto> viewPurchaseHistory(@PathVariable String administratorUsername,
                                                @PathVariable String username,
                                                @PathVariable UUID uuid) {
        return administratorService.viewPurchaseHistory(administratorUsername, username,uuid);
    }

    /**
     * View buyer purchase history
     */
    @ApiOperation(value = "get users")
    @GetMapping("get-users/{administratorUsername}/{uuid}")
    public Set<UserSystemDto> getUsers(@PathVariable String administratorUsername,
                                       @PathVariable UUID uuid) {
        return administratorService.getUsers(administratorUsername, uuid);
    }

    /**
     * View buyer purchase history
     */
    @ApiOperation(value = "get daily visitors")
    @PostMapping("get-daily-visitors/{username}/{uuid}")
    public List<DailyVisitorDto> getDailyVisitors(@PathVariable String username,
                                         @RequestBody RequestGetDailyVisitorsDto requestGetDailyVisitorsDto,
                                         @PathVariable UUID uuid) {
        return administratorService.getDailyVisitors(username, requestGetDailyVisitorsDto, uuid);
    }
}

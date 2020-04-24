package com.wsep202.TradingSystem.web.controllers;

import com.wsep202.TradingSystem.dto.ReceiptDto;
import com.wsep202.TradingSystem.service.user_service.AdministratorService;
import com.wsep202.TradingSystem.web.controllers.api.PublicApiPaths;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class AdministratorController {

    private final AdministratorService administratorService;

    /**
     * View store purchase history
     */
    @MessageMapping("/view-purchase-history-store")
    @SendTo(PublicApiPaths.CLIENT_DESTINATIONS_PREFIXED + "/view-purchase-history-store")
    public List<ReceiptDto> viewPurchaseHistory(String administratorUsername, int storeId){
        return administratorService.viewPurchaseHistory(administratorUsername, storeId);
    }

    /**
     * View buyer purchase history
     */
    @MessageMapping("/view-purchase-history-user")
    @SendTo(PublicApiPaths.CLIENT_DESTINATIONS_PREFIXED + "/view-purchase-history-user")
    public List<ReceiptDto> viewPurchaseHistory(String administratorUsername, String userName){
        return administratorService.viewPurchaseHistory(administratorUsername, userName);
    }
}

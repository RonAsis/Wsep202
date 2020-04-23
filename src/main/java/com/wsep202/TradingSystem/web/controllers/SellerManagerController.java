package com.wsep202.TradingSystem.web.controllers;

import com.wsep202.TradingSystem.dto.ReceiptDto;
import com.wsep202.TradingSystem.service.user_service.SellerManagerService;
import com.wsep202.TradingSystem.web.controllers.api.PublicApiPaths;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class SellerManagerController {

    private final SellerManagerService sellerManagerService;
    /**
     * View store purchase history
     */
    @MessageMapping("/view-purchase-history-of-manager")
    @SendTo(PublicApiPaths.CLIENT_DESTINATIONS_PREFIXED + "/view-purchase-history-of-manager")
    public List<ReceiptDto> viewPurchaseHistoryOfManager(String userName, int storeId){
        return sellerManagerService.viewPurchaseHistoryOfManager(userName, storeId);
    }

}

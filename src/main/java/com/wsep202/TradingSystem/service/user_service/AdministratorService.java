package com.wsep202.TradingSystem.service.user_service;

import com.wsep202.TradingSystem.domain.trading_system_management.TradingSystemFacade;
import com.wsep202.TradingSystem.domain.trading_system_management.UserSystem;
import com.wsep202.TradingSystem.dto.ReceiptDto;
import com.wsep202.TradingSystem.dto.UserSystemDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdministratorService {

    private final TradingSystemFacade tradingSystemFacade;

    /**
     * UC 6.4.1 - viewing store's purchase history.
     *
     * View store purchase history.
     */
    public List<ReceiptDto> viewPurchaseHistory(String administratorUsername, int storeId, UUID uuid){
        return tradingSystemFacade.viewPurchaseHistory(administratorUsername, storeId, uuid);
    }

    /**
     * UC 6.4.2 - viewing user's purchase history.
     *
     * View buyer purchase history.
     */
    public List<ReceiptDto> viewPurchaseHistory(String administratorUsername, String userName, UUID uuid){
        return tradingSystemFacade.viewPurchaseHistory(administratorUsername, userName, uuid);
    }

    public Set<UserSystemDto> getUsers(String administratorUsername, UUID uuid) {
        return tradingSystemFacade.getUsers(administratorUsername, uuid);
    }
}

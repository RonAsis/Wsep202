package com.wsep202.TradingSystem.service.user_service;

import com.wsep202.TradingSystem.domain.trading_system_management.TradingSystemFacade;
import com.wsep202.TradingSystem.dto.ReceiptDto;
import com.wsep202.TradingSystem.dto.StoreDto;
import com.wsep202.TradingSystem.dto.UserSystemDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdministratorService {

    private final TradingSystemFacade tradingSystemFacade;

    /**
     * View store purchase history
     */
    public List<ReceiptDto> viewPurchaseHistory(String administratorUsername, int storeId){
        return tradingSystemFacade.viewPurchaseHistory(administratorUsername, storeId);
    }

    /**
     * View buyer purchase history
     */
    public List<ReceiptDto> viewPurchaseHistory(String administratorUsername, String userName){
        return tradingSystemFacade.viewPurchaseHistory(administratorUsername, userName);
    }

    /**
     * a function that returns the list of stores that are saved in the system
     * @return - list of StoreDto's.
     */
    public List<StoreDto> getStoresDtos() {
        return this.tradingSystemFacade.getStoresDtos();
    }

    /**
     * a function that returns the list of users that are saved in the system
     * @return - list of UserSystemDto's.
     */
    public List<UserSystemDto> getUsersDtos() {
        return this.tradingSystemFacade.getUsersDtos();
    }

    /**
     * a function that returns the list of administrators that are saved in the system
     * @return - list of UserSystemDto's.
     */
    public List<UserSystemDto> getAdministratorsDtos() {
        return this.tradingSystemFacade.getAdministratorsDtos();
    }



}

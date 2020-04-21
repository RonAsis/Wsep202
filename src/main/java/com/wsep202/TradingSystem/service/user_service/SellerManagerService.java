package com.wsep202.TradingSystem.service.user_service;

import com.wsep202.TradingSystem.domain.trading_system_management.Receipt;
import com.wsep202.TradingSystem.domain.trading_system_management.TradingSystemFacade;
import com.wsep202.TradingSystem.service.user_service.dto.ReceiptDto;
import com.wsep202.TradingSystem.service.user_service.dto.StoreDto;
import com.wsep202.TradingSystem.service.user_service.dto.UserSystemDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class SellerManagerService {

    private final TradingSystemFacade tradingSystemFacade;

    /**
     * View store purchase history
     */
    public List<ReceiptDto> viewPurchaseHistory(String userName, int storeId){
        return tradingSystemFacade.viewPurchaseHistoryOfManager(userName, storeId);
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

    /**
     * a function to clear the data structures
     */
    public void clearDS(){
        this.tradingSystemFacade.clearDS();
    }


}
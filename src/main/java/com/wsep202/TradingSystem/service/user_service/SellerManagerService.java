package com.wsep202.TradingSystem.service.user_service;

import com.wsep202.TradingSystem.domain.trading_system_management.TradingSystemFacade;
import com.wsep202.TradingSystem.dto.DiscountDto;
import com.wsep202.TradingSystem.dto.ReceiptDto;
import com.wsep202.TradingSystem.dto.StoreDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class SellerManagerService {

    private final TradingSystemFacade tradingSystemFacade;

    /**
     * View store purchase history
     */
    public List<ReceiptDto> viewPurchaseHistoryOfManager(String userName,
                                                         int storeId,
                                                         UUID uuid){
        return tradingSystemFacade.viewPurchaseHistoryOfManager(userName, storeId, uuid);
    }

    public List<StoreDto> getMangeStores(String manageUsername, UUID uuid) {
        return tradingSystemFacade.getMangeStores(manageUsername, uuid);
    }

    public List<String> getOperationsCanDo(String manageUsername, int storeId, UUID uuid) {
        return tradingSystemFacade.getOperationsCanDo(manageUsername, storeId, uuid);
    }

    public List<DiscountDto> getStoreDiscounts(String username, int storeId, UUID uuid) {
        return tradingSystemFacade.getStoreDiscounts(username, storeId, uuid);
    }

    public boolean removeDiscount(String username, int storeId, int discountId, UUID uuid) {
        return tradingSystemFacade.removeDiscount(username, storeId, discountId, uuid);
    }

    public List<String> getCompositeOperators(String username, int storeId, UUID uuid) {
        return tradingSystemFacade.getCompositeOperators(username, storeId, uuid);
    }

    public List<DiscountDto> getDiscounts(String username, int storeId, UUID uuid) {
        return tradingSystemFacade.getAllStoreDiscounts(username, storeId, uuid);
    }

    public DiscountDto addEditDiscount(String username, int storeId, DiscountDto discountDto, UUID uuid) {
        return tradingSystemFacade.addEditDiscount(username, storeId, discountDto, uuid);
    }
}

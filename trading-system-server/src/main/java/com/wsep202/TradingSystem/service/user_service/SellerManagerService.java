package com.wsep202.TradingSystem.service.user_service;

import com.wsep202.TradingSystem.domain.trading_system_management.TradingSystemFacade;
import com.wsep202.TradingSystem.dto.*;
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
     * UC 5.1.1 - viewing the store's purchase history.
     * <p>
     * View store purchase history
     */
    public List<ReceiptDto> viewPurchaseHistoryOfManager(String userName,
                                                         int storeId,
                                                         UUID uuid) {
        return tradingSystemFacade.viewPurchaseHistoryOfManager(userName, storeId, uuid);
    }

    /**
     * UC 4.2 (inherited from owner) - removing a discount.
     */
    public boolean removeDiscount(String username, int storeId, int discountId, UUID uuid) {
        return tradingSystemFacade.removeDiscount(username, storeId, discountId, uuid);
    }

    /**
     * UC 4.2 (inherited from owner) - adding/ editing the store's discounts
     */
    public DiscountDto addEditDiscount(String username, int storeId, DiscountDto discountDto, UUID uuid) {
        return tradingSystemFacade.addEditDiscount(username, storeId, discountDto, uuid);
    }

    /**
     * UC 4.2 (inherited from owner) - adding/ editing the store's purchase's policies.
     */
    public PurchasePolicyDto addEditPurchasePolicy(String username, int storeId, PurchasePolicyDto purchaseDto, UUID uuid) {
        return tradingSystemFacade.addEditPurchase(username, storeId, purchaseDto, uuid);
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

    public List<String> getCompositeOperators(String username, int storeId, UUID uuid) {
        return tradingSystemFacade.getCompositeOperators(username, storeId, uuid);
    }

    public List<DiscountDto> getSimpleDiscounts(String username, int storeId, UUID uuid) {
        return tradingSystemFacade.getDiscountsSimple(username, storeId, uuid);
    }

    public List<DiscountDto> getAllDiscounts(String username, int storeId, UUID uuid) {
        return tradingSystemFacade.getAlltDiscounts(username, storeId, uuid);
    }

    public List<PurchasePolicyDto> getAllStorePurchases(String username, int storeId, UUID uuid) {
        return tradingSystemFacade.getAllStorePurchases(username, storeId, uuid);
    }
}

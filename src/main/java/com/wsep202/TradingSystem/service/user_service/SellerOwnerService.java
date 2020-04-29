package com.wsep202.TradingSystem.service.user_service;

import com.wsep202.TradingSystem.domain.trading_system_management.TradingSystemFacade;
import com.wsep202.TradingSystem.dto.ReceiptDto;
import com.wsep202.TradingSystem.dto.StoreDto;
import com.wsep202.TradingSystem.dto.UserSystemDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class SellerOwnerService {

    private final TradingSystemFacade tradingSystemFacade;

    /**
     * View store purchase history
     */
    public List<ReceiptDto> viewPurchaseHistoryOfStoreOwner(String ownerUsername,
                                                            int storeId,
                                                            UUID uuid){
        return tradingSystemFacade.viewPurchaseHistoryOfOwner(ownerUsername,storeId, uuid);
    }

    /**
     * add product
     */
    public boolean addProduct(String ownerUsername,
                              int storeId,
                              String productName,
                              String category,
                              int amount,
                              double cost,
                              UUID uuid){
         return tradingSystemFacade.addProduct(ownerUsername, storeId, productName, category, amount, cost, uuid);
    }

    /**
     * remove product
     */
    public boolean deleteProductFromStore(String ownerUsername,
                                          int storeId,
                                          int productSn,
                                          UUID uuid){
        return tradingSystemFacade.deleteProductFromStore(ownerUsername, storeId, productSn, uuid);
    }

    /**
     * edit product
     */
    public boolean editProduct(String ownerUsername,
                               int storeId,
                               int productSn,
                               String productName,
                               String category,
                               int amount,
                               double cost,
                               UUID uuid){
        return tradingSystemFacade.editProduct(ownerUsername, storeId, productSn, productName, category, amount, cost, uuid);
    }

    /**
     * add owner
     */
    public boolean addOwner(String ownerUsername,
                            int storeId,
                            String newOwnerUsername,
                            UUID uuid){
        return tradingSystemFacade.addOwner(ownerUsername, storeId, newOwnerUsername, uuid);
    }

    /**
     * remove manager
     */
    public boolean removeManager(String ownerUsername,
                                 int storeId,
                                 String newManagerUsername,
                                 UUID uuid){
        return tradingSystemFacade.removeManager(ownerUsername, storeId, newManagerUsername, uuid);
    }

    /**
     * add permission
     */
    public boolean addPermission(String ownerUsername,
                                 int storeId,
                                 String newManagerUsername,
                                 String permission,
                                 UUID uuid){
        return tradingSystemFacade.addPermission(ownerUsername, storeId, newManagerUsername, permission, uuid);
    }

    /**
     * add manager
     */
    public boolean addManager(String ownerUsername,
                              int storeId,
                              String newManagerUsername,
                              UUID uuid){
        return tradingSystemFacade.addManager(ownerUsername, storeId, newManagerUsername, uuid);
    }

    public List<StoreDto> getOwnerStores(String ownerUsername, UUID uuid) {
        return tradingSystemFacade.getOwnerStores(ownerUsername, uuid);
    }
}

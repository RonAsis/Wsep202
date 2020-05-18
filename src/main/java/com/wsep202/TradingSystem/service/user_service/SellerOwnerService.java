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
    public ProductDto addProduct(String ownerUsername,
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
    public ManagerDto addManager(String ownerUsername,
                              int storeId,
                              String newManagerUsername,
                              UUID uuid){
        return tradingSystemFacade.addManager(ownerUsername, storeId, newManagerUsername, uuid);
    }

    public List<StoreDto> getOwnerStores(String ownerUsername, UUID uuid) {
        return tradingSystemFacade.getOwnerStores(ownerUsername, uuid);
    }

    public List<String> getAllOperationOfManger() {
        return tradingSystemFacade.getAllOperationOfManger();
    }


    public List<String> getAllUsernameNotOwnerNotManger(String ownerUsername,
                                                        int storeId,
                                                        UUID uuid){
        return tradingSystemFacade.getAllUsernameNotOwnerNotManger(ownerUsername, storeId, uuid);
    }

    public List<String> getMySubOwners(String ownerUsername, int storeId, UUID uuid) {
        return tradingSystemFacade.getMySubOwners(ownerUsername, storeId, uuid);
    }

    public List<ManagerDto> getMySubMangers(String ownerUsername, int storeId, UUID uuid) {
        return tradingSystemFacade.getMySubMangers(ownerUsername, storeId, uuid);
    }

    public boolean removePermission(String ownerUsername, int storeId, String managerUsername, String permission, UUID uuid) {
        return tradingSystemFacade.removePermission(ownerUsername, storeId, managerUsername, permission, uuid);
    }

    public List<String> getPermissionOfManager(String ownerUsername, int storeId, String managerUsername, UUID uuid) {
        return tradingSystemFacade.getPermissionOfManager(ownerUsername, storeId, managerUsername, uuid);
    }

    public List<String> getPermissionCantDo(String ownerUsername, int storeId, String managerUsername, UUID uuid) {
        return tradingSystemFacade.getPermissionCantDo(ownerUsername, storeId, managerUsername, uuid);
    }

    public boolean isOwner(String username, int storeId, UUID uuid) {
        return tradingSystemFacade.isOwner(username, storeId, uuid);
    }
}

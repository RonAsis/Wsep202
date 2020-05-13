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
     * remove a discount from store
     * @param ownerUsername
     * @param storeId
     * @param uuid
     * @param discountId
     * @return
     */
    public boolean removeDiscountFromStore(String ownerUsername,
                                           int storeId,
                                           UUID uuid,
                                           int discountId){
        return tradingSystemFacade.removeDiscountFromStore(ownerUsername,storeId,uuid,discountId);
    }

////////////////////UC 4.2////////////////////////////////////////////////////////////////////////

    /**
     * owner adds a new visible discount
     * @param ownerUsername the owner does that add to his store
     * @param storeId the store that accepts the discount
     * @param uuid  of the owner
     * @param visibleDiscountDto - the info of the discount inserted by the owner
     * @return true on successful addition
     */
    public boolean addVisibleDiscountPolicy(String ownerUsername,
                                            int storeId,
                                            UUID uuid,
                                            VisibleDiscountDto visibleDiscountDto){
        return tradingSystemFacade.addVisibleDiscountPolicy(ownerUsername, storeId,
                uuid, visibleDiscountDto);
    }

    /**
     * edit discount of type visible
     * @param ownerUsername the editor
     * @param storeId store the discount belongs to
     * @param uuid id of the user - is unique
     * @param visibleDiscountEditDto discount info to update
     * @return
     */
    public boolean editVisibleDiscount(String ownerUsername,
                                       int storeId,
                                       UUID uuid,VisibleDiscountEditDto visibleDiscountEditDto){
        return tradingSystemFacade.editVisibleDiscount(ownerUsername,storeId,uuid,
                visibleDiscountEditDto);
    }

    /**
     * edit discount of type conditional on product level
     * @param ownerUsername the editor
     * @param storeId store the discount belongs to
     * @param uuid id of the user - is unique
     * @param productEditDiscountDto discount info to update
     * @return
     */
    public boolean editConditionalProductDiscount(String ownerUsername,
                                                  int storeId,
                                                  UUID uuid,ConditionalProductEditDto productEditDiscountDto){
        return tradingSystemFacade.editConditionalProductDiscount(ownerUsername,storeId,uuid,
                productEditDiscountDto);
    }

    /**
     * edit discount of type conditional on store level
     * @param ownerUsername the editor
     * @param storeId store the discount belongs to
     * @param uuid id of the user - is unique
     * @param conditionalStoreDiscount discount info to update
     * @return
     */
    public boolean editConditionalStoreDiscount(String ownerUsername,
                                                int storeId,
                                                UUID uuid,
                                                ConditionalStoreDiscountEditDto conditionalStoreDiscount){
        return tradingSystemFacade.editConditionalStoreDiscount(ownerUsername,storeId,uuid,
                conditionalStoreDiscount);
    }

    /**
     * edit discount of type conditional composed
     * @param ownerUsername the editor
     * @param storeId store the discount belongs to
     * @param uuid id of the user - is unique
     * @param composedDto discount info to update
     * @return
     */
    public boolean editConditionalComposedDiscount(String ownerUsername,
                                                   int storeId,
                                                   UUID uuid,
                                                   ConditionalComposedDiscountEditDto composedDto){
        return tradingSystemFacade.editConditionalComposedDiscount(ownerUsername,storeId,uuid,
                composedDto);
    }

    /**
     * UC 4.2 - owner adds a new conditional discount
     * @param ownerUsername the owner does that add to his store
     * @param storeId the store that accepts the discount
     * @param uuid  of the owner
     * @param conditionalProductDiscountDto the discount info inserted by the owner
     * @return true on successful addition
     */
    public boolean addConditionalProductDiscountPolicy(String ownerUsername,
                                                       int storeId,
                                                       UUID uuid,
                                                       ConditionalProductDiscountDto conditionalProductDiscountDto){
        return tradingSystemFacade.addConditionalDiscountPolicy(ownerUsername, storeId,
                uuid, conditionalProductDiscountDto);
    }

    /**
     * add conditional store discount
     * @param ownerUsername the creator
     * @param storeId id of store that will hold the discount
     * @param uuid of owner
     * @param conditionalStoreDiscountDto the discount info
     * @return true for successful operation
     */
    public boolean addConditionalStoreDiscountPolicy(String ownerUsername,
                                                     int storeId,
                                                     UUID uuid,
                                                     ConditionalStoreDiscountDto conditionalStoreDiscountDto){
        return tradingSystemFacade.addConditionalStoreDiscountPolicy(ownerUsername, storeId,
                uuid, conditionalStoreDiscountDto);
    }


    /**
     * add conditional composed discount
     * @param ownerUsername the creator
     * @param storeId id of store that will hold the discount
     * @param uuid of owner
     * @param conditionalComposedDto the discount info
     * @return true for successful operation
     */
    public boolean addConditionalComposedDiscountPolicy(String ownerUsername,
                                                        int storeId,
                                                        UUID uuid,
                                                        ConditionalComposedDto conditionalComposedDto){
        return tradingSystemFacade.addConditionalComposedDiscountPolicy(ownerUsername, storeId,
                uuid, conditionalComposedDto);
    }
//////////////////////////////////////////////////////////////////////////////////////////////////////////

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

    public List<DiscountPolicyDto> getAllStoreDiscounts(String ownerUsername,int storeId, UUID uuid){
        return tradingSystemFacade.getAllStoreDiscounts(ownerUsername, storeId, uuid);
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

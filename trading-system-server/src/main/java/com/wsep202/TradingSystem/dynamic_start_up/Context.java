package com.wsep202.TradingSystem.dynamic_start_up;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
public class Context {

    @Builder.Default
    private Map<String, UUID> mapUsernameToUUID;

    @Builder.Default
    private Map<Integer, Integer> mapStoreIdToRealStoreId;

    @Builder.Default
    private Map<Integer, Integer> mapProductIdToRealProduct;

    @Builder.Default
    private Map<Long,Long> mapDiscountIdToRealDiscount;

    @Builder.Default
    private Map<Long,Long> mapPurchasePolicyIdToRealPurchasePolicy;

    @Builder.Default
    private int storeIdCounter = 0;

    @Builder.Default
    private int productIdCounter = 0;

    @Builder.Default
    private int discountIdCounter = 0;

    @Builder.Default
    private int purchasePolicyIdCounter = 0;

    public Context() {
        mapUsernameToUUID = new HashMap<>();
        mapStoreIdToRealStoreId = new HashMap<>();
        mapProductIdToRealProduct = new HashMap<>();
        mapDiscountIdToRealDiscount = new HashMap<>();
        mapPurchasePolicyIdToRealPurchasePolicy = new HashMap<>();
        storeIdCounter = 0;
        productIdCounter = 0;
        discountIdCounter = 0;
        purchasePolicyIdCounter = 0;
    }

    public void addStoreId(int storeId) {
        mapStoreIdToRealStoreId.put(getStoreId(), storeId);
    }

    public void addUsernameUuid(String username, UUID uuid) {
        mapUsernameToUUID.put(username, uuid);
    }

    private int getStoreId(){
        return storeIdCounter++;
    }

    private int getProductId(){
        return productIdCounter++;
    }

    private long getDiscountId(){ return discountIdCounter++; }

    private long getPurchasePolicyId(){ return purchasePolicyIdCounter++; }

    //long!!!!!!!!!!!!!!!!!!!!!!!!or int???????
    public void addDiscountId(long discountId){
        mapDiscountIdToRealDiscount.put(getDiscountId(),discountId);
    }

    public void addPurchaseId(long purchaseId){
        mapPurchasePolicyIdToRealPurchasePolicy.put(getPurchasePolicyId(),purchaseId);
    }

    public void addProductId(int productSn) {
        mapProductIdToRealProduct.put(getProductId(), productSn);
    }

    public int getRealStoreId(int storeId) {
        return mapStoreIdToRealStoreId.get(storeId);
    }

    public UUID getUuid(String userName) {
        return mapUsernameToUUID.get(userName);
    }

    public void removeUuid(String username) {
        mapUsernameToUUID.remove(username);
    }
}

package com.wsep202.TradingSystem.domain.trading_system_management;

import com.wsep202.TradingSystem.exception.NoManagerInStoreException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@AllArgsConstructor
@Builder
@NoArgsConstructor
@Slf4j
@Data
public class Store {

    public static int storeIdAcc = 0;

    private int storeId;

    private String storeName;

    private Map<UserSystem, Set<UserSystem>> appointedOwners;

    private Map<UserSystem, Set<MangerStore>> appointedManagers;

    private Map<ProductCategory, Set<Product>> products;

    private PurchasePolicy purchasePolicy;

    private DiscountPolicy discountPolicy;

    private DiscountType discountType;

    private PurchaseType purchaseType;

    private Set<UserSystem> owners ;

    private List<Receipt> receipts;

    private int rank;

    public Store(UserSystem owner, PurchasePolicy purchasePolicy, DiscountPolicy discountPolicy, DiscountType discountType, PurchaseType purchaseType, String storeName){
        appointedOwners = new HashMap<>();
        appointedManagers = new HashMap<>();
        products = new HashMap<>();
        owners = new HashSet<>();
        this.storeName = storeName;
        owners.add(owner);
        this.discountPolicy = discountPolicy;
        this.purchasePolicy = purchasePolicy;
        this.discountType = discountType;
        this.purchaseType = purchaseType;
        this.storeId = getStoreIdAcc();
        this.rank = 0;
    }

    private int getStoreIdAcc(){
        return storeIdAcc++;
    }
    /**
     * add new owner to the store
     */
    public boolean appointAdditionOwner(UserSystem owner, UserSystem willBeOwner) {
        boolean appointed = false;
        if (owners.contains(owner) && !owners.contains(willBeOwner)) {
            appointedOwners.putIfAbsent(owner, new HashSet<>());
            appointedOwners.get(owner).add(willBeOwner);
            appointed = true;
        }
        return appointed;
    }

    /**
     * add new manager to the store
     */
    public boolean appointAdditionManager(UserSystem owner, MangerStore mangerStore) {
        boolean appointed = false;
        if (owners.contains(owner) && (!appointedManagers.containsKey(owner) || !appointedManagers.get(owner).contains(mangerStore))) {
            appointedManagers.putIfAbsent(owner, new HashSet<>());
            appointedManagers.get(owner).add(mangerStore);
            appointed = true;
        }
        return appointed;
    }

    /**
     * get product filtered by categories
     */
    public Set<Product> getProductFilterByCategory(Set<ProductCategory> categories) {
        return products.entrySet().stream()
                .filter(entry -> categories.contains(entry.getKey()))
                .map(Map.Entry::getValue)
                .reduce(new HashSet<>(),
                        (cur, acc) -> {
                            acc.addAll(cur);
                            return acc;
                        });
    }

    /**
     * get product filtered by cost range
     */
    public Set<Product> getProductFilterByCostRange(double min, double max) {
//        return products.values().stream()
//                .filter(product -> product.getCost() >= min && product.getCost() <= max))                        .filter(product -> product.getCost() >= min && product.getCost() <= max))
//                .reduce(new HashSet<Product>(),
//                        (cur, acc) -> {
//                            acc.a(cur);
//                            return acc;
//                        });
//
    return null;
    }

    /**
     * add new product
     */
    public boolean addNewProduct(UserSystem user, Product product){
        return false;
        //TODO
    }

    private boolean isManager(UserSystem user){
        return appointedManagers.entrySet().stream()
                .anyMatch(entry -> entry.getValue().stream()
                .anyMatch(mangerStore -> mangerStore.isTheUser(user)));
    }

    public List<Receipt> managerViewReceipts(UserSystem user) {
        if(isManager(user)){
            return receipts;
        }
        else {
            throw new NoManagerInStoreException(user.getUserName(), storeId);
        }
    }

    public List<Receipt> ownerViewReceipts(UserSystem user) {
        if(isOwner(user)){
            return receipts;
        }
        else {
            throw new NoManagerInStoreException(user.getUserName(), storeId);
        }
    }

    private boolean isOwner(UserSystem user) {
        return owners.contains(user);
    }

    public boolean removeProductFromStore(UserSystem user, String productName) {
        return false;
    }

    public boolean editProduct(UserSystem user, int productSn, String productName, String category, int amount, double cost) {
        return false;
    }

    public boolean addOwner(Store ownerStore, UserSystem newOwnerUser) {
        return false;
    }

    public boolean addManager(Store ownerStore, UserSystem newManagerUser) {
        return false;
    }
}

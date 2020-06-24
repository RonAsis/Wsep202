package com.wsep202.TradingSystem.domain.trading_system_management;

import com.wsep202.TradingSystem.domain.trading_system_management.discount.Discount;
import com.wsep202.TradingSystem.domain.trading_system_management.ownerStore.OwnerToApprove;
import com.wsep202.TradingSystem.domain.trading_system_management.policy_purchase.Purchase;
import com.wsep202.TradingSystem.domain.trading_system_management.statistics.DailyVisitor;
import com.wsep202.TradingSystem.domain.trading_system_management.statistics.DailyVisitorsField;
import com.wsep202.TradingSystem.domain.trading_system_management.statistics.RequestGetDailyVisitors;
import com.wsep202.TradingSystem.domain.trading_system_management.statistics.UpdateDailyVisitor;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public abstract class TradingSystemDao {

    private Map<String, UUID> usersLogin;

    public TradingSystemDao(){
        usersLogin = new HashMap<>();
    }
    abstract void registerAdmin(UserSystem admin);

    abstract boolean isRegistered(UserSystem userSystem);

    abstract void addUserSystem(UserSystem userToRegister, MultipartFile image);

    public abstract Optional<UserSystem> getUserSystem(String username);

    public abstract boolean isAdmin(String username);

    public abstract Optional<UserSystem> getAdministratorUser(String username);

    public abstract Optional<Store> getStore(int storeId);

    public abstract void addStore(Store newStore);

    public abstract Set<Store> getStores();

    public abstract Set<Product> getProducts();

    public abstract Set<UserSystem> getUsers();

    public abstract Product addProductToStore(Store store, UserSystem owner, Product product);

    public abstract boolean removeDiscount(Store store, UserSystem user, int discountId);

    public abstract Discount addEditDiscount(Store store, UserSystem user, Discount discount);

    public abstract Purchase addEditPurchase(Store store, UserSystem user, Purchase purchase);

    public abstract boolean deleteProductFromStore(Store ownerStore, UserSystem user, int productSn);

    public abstract boolean editProduct(Store ownerStore, UserSystem user, int productSn, String productName, String category, int amount, double cost);

    public abstract void updateStoreAndUserSystem(Store ownedStore, UserSystem userSystem);

    public abstract boolean addPermissionToManager(Store ownedStore, UserSystem ownerUser, UserSystem managerStore, StorePermission storePermission);

    public abstract boolean removePermission(Store ownedStore, UserSystem ownerUser, UserSystem managerStore, StorePermission storePermission);

    public abstract boolean saveProductInShoppingBag(String username, ShoppingCart shoppingCart, Store store, Product product, int amount);

    public abstract boolean removeProductInShoppingBag(String username, ShoppingCart shoppingCart, Store store, Product product);

    public void updateUser(UserSystem user){}

    public abstract boolean changeProductAmountInShoppingBag(String username, ShoppingCart shoppingCart, int storeId, int amount, int productSn);

    protected void updateStore(Store ownedStore){}

    public  boolean isLogin(String userName){
        return Objects.nonNull(usersLogin.get(userName));
    }

    public Optional<UpdateDailyVisitor> login(String userName, UUID uuid){
        usersLogin.put(userName, uuid);
        AtomicReference<UpdateDailyVisitor> updateDailyVisitor = new AtomicReference<>();
        getUserSystem(userName)
                .ifPresent(userSystem -> {
                    login(userSystem.getUserName(), userSystem.getShoppingCart());
                    updateDailyVisitor.set(userSystem.updateDaily(this));
                });
        return Optional.of(updateDailyVisitor.get());
    }

    public abstract void login(String username, ShoppingCart shoppingCart);

    public void logout(String userName){
        usersLogin.remove(userName);
        Optional<UserSystem> userSystemOptional = getUserSystem(userName);
        userSystemOptional.ifPresent(userSystem -> {
            userSystem.setPrincipal(null);
            saveShoppingCart(userSystem);
        });
    }

    public abstract void saveShoppingCart(UserSystem userSystem);

    public boolean isValidUuid(String username, UUID uuid){
        return usersLogin.get(username).equals(uuid);
    }

    public abstract ShoppingCart getShoppingCart(String username, UUID uuid);

    public abstract void loadShoppingCart(UserSystem user);
    
    protected void updateShoppingCart(UserSystem owner, List<UserSystem> userSystems, Store store, Product product){
        userSystems.forEach(userSystem -> {
            if(userSystem.removeProductInShoppingBag(store, product)){
                updateUser(userSystem);
            }
        });
        store.removeProductFromStore(owner, product.getProductSn());
    }

    public abstract Set<OwnerToApprove> getMyOwnerToApprove(String ownerUsername, UUID uuid);

    public abstract boolean approveOwner(Store ownedStore, UserSystem ownedStore1, String ownerToApprove, boolean status);

    public abstract void updateDailyVisitors(DailyVisitorsField dailyVisitorsField);

    public abstract Set<DailyVisitor> getDailyVisitors(String username, RequestGetDailyVisitors requestGetDailyVisitors, UUID uuid);
    public void setIsLogins(Map<String,UUID> usersLogin){
        this.usersLogin = usersLogin;
    }
    public void setStores(Set<Store> stores){}

    public void setUsers(Set<UserSystem> users){}

    public int usersLoggedInSystem(){
        return usersLogin.size();
    }
}

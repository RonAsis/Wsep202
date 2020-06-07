package com.wsep202.TradingSystem.domain.trading_system_management;

import com.wsep202.TradingSystem.domain.trading_system_management.discount.Discount;
import com.wsep202.TradingSystem.domain.trading_system_management.ownerStore.OwnerToApprove;
import com.wsep202.TradingSystem.dto.ManagerDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

public abstract class TradingSystemDao {

    private Map<String, UUID> usersLogin;

    public TradingSystemDao(){
        usersLogin = new HashMap<>();
    }
    abstract void registerAdmin(UserSystem admin);

    abstract boolean isRegistered(UserSystem userSystem);

    abstract void addUserSystem(UserSystem userToRegister, MultipartFile image);

    abstract Optional<UserSystem> getUserSystem(String username);

    abstract boolean isAdmin(String username);

    abstract Optional<UserSystem> getAdministratorUser(String username);

    abstract Optional<Store> getStore(int storeId);

    abstract void addStore(Store newStore);

    abstract Set<Store> getStores();

    abstract Set<Product> getProducts();

    abstract Set<UserSystem> getUsers();

    abstract Product addProductToStore(Store store, UserSystem owner, Product product);

    abstract boolean removeDiscount(Store store, UserSystem user, int discountId);

    abstract Discount addEditDiscount(Store store, UserSystem user, Discount discount);

    abstract boolean deleteProductFromStore(Store ownerStore, UserSystem user, int productSn);

    abstract boolean editProduct(Store ownerStore, UserSystem user, int productSn, String productName, String category, int amount, double cost);

    public void updateStoreAndUserSystem(Store ownedStore, UserSystem userSystem){}

    abstract boolean addPermissionToManager(Store ownedStore, UserSystem ownerUser, UserSystem managerStore, StorePermission storePermission);

    abstract boolean removePermission(Store ownedStore, UserSystem ownerUser, UserSystem managerStore, StorePermission storePermission);

    abstract boolean saveProductInShoppingBag(String username, ShoppingCart shoppingCart, Store store, Product product, int amount);

    abstract boolean removeProductInShoppingBag(String username, ShoppingCart shoppingCart, Store store, Product product);

    protected void updateUser(UserSystem user){}

    abstract boolean changeProductAmountInShoppingBag(String username, ShoppingCart shoppingCart, int storeId, int amount, int productSn);

    protected void updateStore(Store ownedStore){}

    public  boolean isLogin(String userName){
        return Objects.nonNull(usersLogin.get(userName));
    }

    public void login(String userName, UUID uuid){
        usersLogin.put(userName, uuid);
        getUserSystem(userName)
                .ifPresent(userSystem -> login(userSystem.getUserName(), userSystem.getShoppingCart()));
    }

    public abstract void login(String username, ShoppingCart shoppingCart);

    public void logout(String userName){
        usersLogin.remove(userName);
        saveShoppingCart(userName);
    }

    public abstract void saveShoppingCart(String username);

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
}

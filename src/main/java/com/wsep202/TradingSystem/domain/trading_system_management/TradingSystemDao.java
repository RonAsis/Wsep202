package com.wsep202.TradingSystem.domain.trading_system_management;

import com.wsep202.TradingSystem.domain.trading_system_management.discount.Discount;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.Set;

public interface TradingSystemDao {

    void registerAdmin(UserSystem admin);

    boolean isRegistered(UserSystem userSystem);

    void addUserSystem(UserSystem userToRegister, MultipartFile image);

    Optional<UserSystem> getUserSystem(String username);

    boolean isAdmin(String username);

    Optional<UserSystem> getAdministratorUser(String username);

    Optional<Store> getStore(int storeId);

    void addStore(Store newStore);

    Set<Store> getStores();

    Set<Product> getProducts();

    Set<UserSystem> getUsers();

    Product addProductToStore(Store store, UserSystem owner, Product product);

    boolean removeDiscount(Store store, UserSystem user, int discountId);

    Discount addEditDiscount(Store store, UserSystem user, Discount discount);

    boolean deleteProductFromStore(Store ownerStore, UserSystem user, int productSn);

    boolean editProduct(Store ownerStore, UserSystem user, int productSn, String productName, String category, int amount, double cost);

    default void updateStoreAndUserSystem(Store ownedStore, UserSystem userSystem){}

    boolean addPermissionToManager(Store ownedStore, UserSystem ownerUser, UserSystem managerStore, StorePermission storePermission);

    boolean removePermission(Store ownedStore, UserSystem ownerUser, UserSystem managerStore, StorePermission storePermission);

    boolean saveProductInShoppingBag(UserSystem user, Store store, Product product, int amount);

    boolean removeProductInShoppingBag(UserSystem user, Store store, Product product);

    default void updateUser(UserSystem user){}

    boolean changeProductAmountInShoppingBag(UserSystem user, int storeId, int amount, int productSn);

    default void updateStore(Store ownedStore){}
}

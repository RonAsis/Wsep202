package com.wsep202.TradingSystem.domain.trading_system_management;

import com.wsep202.TradingSystem.data_access_layer.StoreRepository;
import com.wsep202.TradingSystem.data_access_layer.UserRepository;
import com.wsep202.TradingSystem.domain.image.ImagePath;
import com.wsep202.TradingSystem.domain.image.ImageUtil;
import com.wsep202.TradingSystem.domain.trading_system_management.discount.Discount;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import sun.security.util.ArrayUtil;

import java.lang.reflect.Array;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class TradingSystemDataBaseDao implements TradingSystemDao {

    private final UserRepository userRepository;
    private final StoreRepository storeRepository;

    @Override
    @Transactional
    public void registerAdmin(UserSystem admin) {
        userRepository.save(admin);
    }

    @Override
    @Transactional
    public boolean isRegistered(UserSystem userSystem) {
        return userRepository.existsById(userSystem.getUserName());
    }

    @Override
    @Transactional
    public void addUserSystem(UserSystem userToRegister, MultipartFile image) {
        if (Objects.nonNull(image)) {
            String urlImage = ImageUtil.saveImage(ImagePath.ROOT_IMAGE_DIC + ImagePath.USER_IMAGE_DIC + image.getOriginalFilename(), image);
            userToRegister.setImageUrl(urlImage);
        }
        userRepository.save(userToRegister);
    }

    @Override
    @Transactional
    public Optional<UserSystem> getUserSystem(String username) {
        return userRepository.findById(username);
    }

    @Override
    @Transactional
    public boolean isAdmin(String username) {
        Optional<UserSystem> user = userRepository.findById(username);
        return user.isPresent() && user.get().isAdmin();
    }

    @Override
    @Transactional
    public Optional<UserSystem> getAdministratorUser(String username) {
        return userRepository.findOne(Example.of(UserSystem.builder()
                .userName(username)
                .isAdmin(true)
                .build()));
    }

    @Override
    @Transactional
    public Optional<Store> getStore(int storeId) {
        return storeRepository.findById(storeId);
    }

    @Override
    @Transactional
    public void addStore(Store newStore) {
        storeRepository.save(newStore);
    }

    @Override
    @Transactional
    public Set<Store> getStores() {
        return new HashSet<>(storeRepository.findAll());
    }

    @Override
    @Transactional
    public Set<Product> getProducts() {
        return storeRepository.findAll().stream()
                .map(Store::getProducts)
                .reduce(new HashSet<>(), (productsAcc, products) -> {
                    productsAcc.addAll(products);
                    return productsAcc;
                });
    }

    @Override
    @Transactional
    public Set<UserSystem> getUsers() {
        return new HashSet<>(userRepository.findAll());
    }

    @Override
    public Product addProductToStore(Store store, UserSystem owner, Product product) {
        if (store.addNewProduct(owner, product)) {
            storeRepository.save(store);
            product.setProductSn(store.getProducts().stream()
                    .map(Product::getProductSn)
                    .reduce(0, (acc, cur)-> {
                        if(cur> acc){
                            acc = cur;
                        }
                        return acc;
                    } ));
        }
        return product;
    }

    @Override
    public boolean removeDiscount(Store store, UserSystem user, int discountId) {
        boolean res = false;
        if (store.removeDiscount(user, discountId)) {
            storeRepository.save(store);
            res = true;
        }
        return res;
    }

    @Override
    public Discount addEditDiscount(Store store, UserSystem user, Discount discount) {
        Discount res = store.addEditDiscount(user, discount);
        if (Objects.nonNull(res)) {
            storeRepository.save(store);
        }
        return res;
    }

    @Override
    public boolean deleteProductFromStore(Store ownerStore, UserSystem user, int productSn) {
        boolean ans = ownerStore.removeProductFromStore(user, productSn);
        if (ans) {
            storeRepository.save(ownerStore);
            log.info(String.format("Delete productSn %d from store %d", productSn, ownerStore.getStoreId()));
        }
        return ans;
    }

    @Override
    public boolean editProduct(Store ownerStore, UserSystem user, int productSn, String productName, String category, int amount, double cost) {
        boolean ans = ownerStore.editProduct(user, productSn, productName, category, amount, cost);
        if (ans) {
            storeRepository.save(ownerStore);
        }
        return ans;
    }

    @Override
    public void updateStoreAndUserSystem(Store ownedStore, UserSystem userSystem) {
        storeRepository.save(ownedStore);
        userRepository.save(userSystem);
    }

    @Override
    public boolean addPermissionToManager(Store ownedStore, UserSystem ownerUser, UserSystem managerStore, StorePermission storePermission) {
        boolean ans = ownedStore.addPermissionToManager(ownerUser, managerStore, storePermission);
        if (ans) {
            storeRepository.save(ownedStore);
        }
        return ans;
    }

    @Override
    public boolean removePermission(Store ownedStore, UserSystem ownerUser, UserSystem managerStore, StorePermission storePermission) {
        boolean ans = ownedStore.removePermission(ownerUser, managerStore, storePermission);
        if (ans) {
            storeRepository.save(ownedStore);
        }
        return ans;
    }

    @Override
    public boolean saveProductInShoppingBag(UserSystem user, Store store, Product product, int amount) {
        Product productInShoppingBag = store.getProduct(product.getProductSn());
        boolean ans = user.saveProductInShoppingBag(store, productInShoppingBag, amount);
        if (ans) {
            userRepository.save(user);
        }
        return ans;
    }

    @Override
    public boolean removeProductInShoppingBag(UserSystem user, Store store, Product product) {
        boolean ans = user.removeProductInShoppingBag(store, product);
        if (ans) {
            userRepository.save(user);
        }
        return ans;
    }

    @Override
    public void updateUser(UserSystem user) {
        userRepository.save(user);
    }

    @Override
    public boolean changeProductAmountInShoppingBag(UserSystem user, int storeId, int amount, int productSn) {
        boolean ans = user.changeProductAmountInShoppingBag(storeId, amount, productSn);
        if(ans){
            updateUser(user);
        }
        return ans;
    }

    @Override
    public void updateStore(Store ownedStore){
        storeRepository.save(ownedStore);
    }
}

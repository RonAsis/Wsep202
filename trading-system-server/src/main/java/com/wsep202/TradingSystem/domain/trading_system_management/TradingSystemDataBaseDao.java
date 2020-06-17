package com.wsep202.TradingSystem.domain.trading_system_management;

import com.wsep202.TradingSystem.data_access_layer.DailyVisitorsRepository;
import com.wsep202.TradingSystem.data_access_layer.StoreRepository;
import com.wsep202.TradingSystem.data_access_layer.UserRepository;
import com.wsep202.TradingSystem.domain.exception.NotAdministratorException;
import com.wsep202.TradingSystem.domain.exception.UserDontExistInTheSystemException;
import com.wsep202.TradingSystem.domain.image.ImagePath;
import com.wsep202.TradingSystem.domain.image.ImageUtil;
import com.wsep202.TradingSystem.domain.trading_system_management.cashing.TradingSystemCashing;
import com.wsep202.TradingSystem.domain.trading_system_management.discount.Discount;
import com.wsep202.TradingSystem.domain.trading_system_management.ownerStore.OwnerToApprove;
import com.wsep202.TradingSystem.domain.trading_system_management.statistics.DailyVisitor;
import com.wsep202.TradingSystem.domain.trading_system_management.statistics.DailyVisitorsField;
import com.wsep202.TradingSystem.domain.trading_system_management.statistics.RequestGetDailyVisitors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TradingSystemDataBaseDao extends TradingSystemDao {

    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final DailyVisitorsRepository dailyVisitorsRepository;
    private final TradingSystemCashing tradingSystemCashing;

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
        if (tradingSystemCashing.getProducts().isEmpty()) {
            Set<Product> productSet = storeRepository.findAll().stream()
                    .map(Store::getProducts)
                    .reduce(new HashSet<>(), (productsAcc, products) -> {
                        productsAcc.addAll(products);
                        return productsAcc;
                    });
            tradingSystemCashing.addProducts(productSet);
        }
        return tradingSystemCashing.getProducts();
    }

    @Override
    @Transactional
    public Set<UserSystem> getUsers() {
        return new HashSet<>(userRepository.findAll());
    }

    @Override
    public Product addProductToStore(Store store, UserSystem owner, Product product) {
        if (store.addNewProduct(owner, product)) {
            Store storeSaved = storeRepository.save(store);
            int productSn = new LinkedList<>(storeSaved.getProducts()).getLast().getProductSn();
            product.setProductSn(productSn);
            tradingSystemCashing.addProduct(product);
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
            List<Discount> discounts = new LinkedList<>(storeRepository.findById(store.getStoreId()).get().getDiscounts());
            res.setDiscountId(discounts.get(discounts.size() - 1).getDiscountId());
        }
        return res;
    }

    @Override
    public boolean deleteProductFromStore(Store ownerStore, UserSystem user, int productSn) {
        boolean ans = ownerStore.validateCanEditProdcuts(user, productSn);
        if (ans) {
            updateDbWithCashing();
            updateShoppingCart(user, userRepository.findAll(), ownerStore, ownerStore.getProduct(productSn));
            storeRepository.save(ownerStore);
            log.info(String.format("Delete productSn %d from store %d", productSn, ownerStore.getStoreId()));
            tradingSystemCashing.removeProduct(productSn);
        }
        return ans;
    }

    private void updateDbWithCashing() {
        tradingSystemCashing.getShoppingCartMap().forEach((key, value) -> userRepository.findById(key)
                .ifPresent(userSystem -> {
                    userSystem.setShoppingCart(value);
                    userRepository.save(userSystem);
                }));
    }

    @Override
    public boolean editProduct(Store ownerStore, UserSystem user, int productSn, String productName, String category, int amount, double cost) {
        boolean ans = ownerStore.editProduct(user, productSn, productName, category, amount, cost);
        if (ans) {
            storeRepository.save(ownerStore);
            tradingSystemCashing.editProduct(ownerStore.getProduct(productSn));
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
    public boolean saveProductInShoppingBag(String username, ShoppingCart shoppingCart, Store store, Product product, int amount) {
        Product productInShoppingBag = store.getProduct(product.getProductSn());
        return shoppingCart.addProductToCart(store, productInShoppingBag, amount);
    }

    @Override
    public boolean removeProductInShoppingBag(String username, ShoppingCart shoppingCart, Store store, Product product) {
        boolean ans = shoppingCart.removeProductInCart(store, product);
        if (ans) {
            tradingSystemCashing.editShoppingCart(username, shoppingCart);
        }
        return ans;
    }

    @Override
    public void updateUser(UserSystem user) {
        userRepository.save(user);
    }

    @Override
    public boolean changeProductAmountInShoppingBag(String username, ShoppingCart shoppingCart, int storeId, int amount, int productSn) {
        boolean ans = shoppingCart.changeProductAmountInShoppingBag(storeId, amount, productSn);
        if (ans) {
            tradingSystemCashing.editShoppingCart(username, shoppingCart);
        }
        return ans;
    }

    @Override
    public void updateStore(Store ownedStore) {
        storeRepository.save(ownedStore);
    }

    @Override
    public void login(String username, ShoppingCart shoppingCart) {
        tradingSystemCashing.addShoppingCart(username, shoppingCart);
    }

    @Override
    public void saveShoppingCart(UserSystem userSystem) {
        ShoppingCart shoppingCart = tradingSystemCashing.removeShoppingCart(userSystem.getUserName());
        userSystem.setShoppingCart(shoppingCart);
        userRepository.save(userSystem);
    }

    @Override
    public ShoppingCart getShoppingCart(String username, UUID uuid) {
        if (isValidUuid(username, uuid)) {
            return tradingSystemCashing.getShoppingCart(username);
        }
        throw new UserDontExistInTheSystemException(username);
    }

    @Override
    public void loadShoppingCart(UserSystem user) {
        user.setShoppingCart(tradingSystemCashing.getShoppingCart(user.getUserName()));
    }

    @Override
    public Set<OwnerToApprove> getMyOwnerToApprove(String ownerUsername, UUID uuid) {
        if (isValidUuid(ownerUsername, uuid)) {
            return userRepository.findById(ownerUsername)
                    .map(userSystem -> userSystem.getOwnerToApproves())
                    .orElse(new HashSet<>());
        }
        return new HashSet<>();
    }

    @Override
    public boolean approveOwner(Store ownedStore, UserSystem ownerUser, String ownerToApprove, boolean status) {
        boolean res = ownedStore.approveOwner(ownerUser, ownerToApprove, status);
        if (res) {
            updateStoreAndUserSystem(ownedStore, ownerUser);
        }
        return res;
    }

    @Override
    public void updateDailyVisitors(DailyVisitorsField dailyVisitorsField) {
        Date toDay = new Date();
        dailyVisitorsRepository.findById(toDay)
                .map(dailyVisitor -> {
                    dailyVisitor.update(dailyVisitorsField);
                    return dailyVisitorsRepository.save(dailyVisitor);
                })
                .orElseGet(() -> {
                    DailyVisitor dailyVisitor = new DailyVisitor();
                    dailyVisitor.update(dailyVisitorsField);
                    return dailyVisitorsRepository.save(dailyVisitor);
                });
    }

    @Override
    public Set<DailyVisitor> getDailyVisitors(String username, RequestGetDailyVisitors requestGetDailyVisitors, UUID uuid) {
        if (isValidUuid(username, uuid) && isAdmin(username)) {
            Pageable pageable = PageRequest.of(requestGetDailyVisitors.getFirstIndex(), requestGetDailyVisitors.getLastIndex(), Sort.by("date").ascending());
            return new HashSet<>(dailyVisitorsRepository.findByDateBetween(requestGetDailyVisitors.getStart(), requestGetDailyVisitors.getEnd(), pageable));
        }
        throw new NotAdministratorException(username);
    }

}

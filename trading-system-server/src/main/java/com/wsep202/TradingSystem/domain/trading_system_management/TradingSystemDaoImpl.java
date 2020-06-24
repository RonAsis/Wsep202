package com.wsep202.TradingSystem.domain.trading_system_management;

import com.wsep202.TradingSystem.domain.exception.NotAdministratorException;
import com.wsep202.TradingSystem.domain.exception.UserDontExistInTheSystemException;
import com.wsep202.TradingSystem.domain.image.ImagePath;
import com.wsep202.TradingSystem.domain.image.ImageUtil;
import com.wsep202.TradingSystem.domain.trading_system_management.discount.Discount;
import com.wsep202.TradingSystem.domain.trading_system_management.ownerStore.OwnerToApprove;
import com.wsep202.TradingSystem.domain.trading_system_management.policy_purchase.Purchase;
import com.wsep202.TradingSystem.domain.trading_system_management.statistics.DailyVisitor;
import com.wsep202.TradingSystem.domain.trading_system_management.statistics.DailyVisitorsField;
import com.wsep202.TradingSystem.domain.trading_system_management.statistics.RequestGetDailyVisitors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
public class TradingSystemDaoImpl extends TradingSystemDao {

    private static int idAccStore = 0;
    private static int idAccUserSystem = 0;
    private static int idAccProduct = 0;
    private static int idAccDiscount = 0;
    private static int idAccPolicy = 0;
    private static int idAccReceipt = 0;

    private Set<Store> stores;
    private Set<UserSystem> users;
    private Set<UserSystem> administrators;
    private Set<DailyVisitor> dailyVisitors;

    public TradingSystemDaoImpl() {
        super();
        this.stores = new HashSet<>();
        this.users = new HashSet<>();
        this.administrators = new HashSet<>();
        dailyVisitors = new HashSet<>();
    }

    @Override
    public void registerAdmin(@NotNull UserSystem admin) {
        administrators.add(admin);
    }

    @Override
    public boolean isRegistered(UserSystem userSystem) {
        return users.stream()
                .anyMatch(userSystemReg -> userSystemReg.getUserName().equals(userSystem.getUserName())) ||
                administrators.stream()
                        .anyMatch(userSystemReg -> userSystemReg.getUserName().equals(userSystem.getUserName()));
    }

    @Override
    public void addUserSystem(UserSystem userToRegister, MultipartFile image) {
        String urlImage = null;
        if (Objects.nonNull(image)) {
            urlImage = ImageUtil.saveImage(ImagePath.ROOT_IMAGE_DIC + ImagePath.USER_IMAGE_DIC + image.getOriginalFilename(), image);
        }
        userToRegister.setImageUrl(urlImage);
        users.add(userToRegister);
    }

    @Override
    public Optional<UserSystem> getUserSystem(String username) {
        Optional<UserSystem> userSystem = findUserSystem(users, username);
        return userSystem.isPresent() ? userSystem : findUserSystem(administrators, username);
    }

    private Optional<UserSystem> findUserSystem(Set<UserSystem> userSystems, String username) {
        return userSystems.stream()
                .filter(user -> user.getUserName().equals(username))
                .findFirst();
    }

    @Override
    public boolean isAdmin(String username) {
        return administrators.stream()
                .anyMatch(userSystem -> userSystem.getUserName().equals(username));
    }

    /**
     * returns the User system object match the received string in case its an admin user
     * otherwise throw exception
     *
     * @param administratorUsername - admins user name
     * @return administrator type user
     */
    public Optional<UserSystem> getAdministratorUser(String administratorUsername) {
        return administrators.stream()
                .filter(user -> user.getUserName().equals(administratorUsername))
                .findFirst();
    }

    @Override
    public Optional<Store> getStore(int storeId) {
        return stores.stream()
                .filter(store -> store.getStoreId() == storeId).findFirst();
    }

    @Override
    public void addStore(Store newStore) {
        newStore.setStoreId(getNewIdStore());
        this.stores.add(newStore);
    }

    @Override
    public Set<Store> getStores() {
        return stores;
    }

    @Override
    public Set<Product> getProducts() {
        return stores.stream()
                .map(Store::getProducts)
                .reduce(new HashSet<>(), (productsAcc, products) -> {
                    productsAcc.addAll(products);
                    return productsAcc;
                });
    }

    @Override
    public Set<UserSystem> getUsers() {
        return this.users;
    }

    @Override
    public Product addProductToStore(Store store, UserSystem owner, Product product) {
        product.setProductSn(getNewIdProduct());
        return store.addNewProduct(owner, product) ? product : null;
    }

    @Override
    public boolean removeDiscount(Store store, UserSystem user, int discountId) {
        return store.removeDiscount(user, discountId);
    }

    @Override
    public Discount addEditDiscount(Store store, UserSystem user, Discount discount) {
        //discount.setDiscountId(getNewIdDiscount());
        return store.addEditDiscount(user, discount);
    }

    @Override
    public Purchase addEditPurchase(Store store, UserSystem user, Purchase purchase) {
        //purchase.setPurchaseId(getNewIdPolicy());
        return store.addEditPurchase(user, purchase);
    }

    @Override
    public boolean deleteProductFromStore(Store ownerStore, UserSystem user, int productSn) {
        boolean ans = ownerStore.validateCanEditProducts(user, productSn);
        if (ans) {
            updateShoppingCart(user, new LinkedList<>(users), ownerStore, ownerStore.getProduct(productSn));
            log.info(String.format("Delete productSn %d from store %d", productSn, ownerStore.getStoreId()));
            return true;
        }
        //return ownerStore.removeProductFromStore(user, productSn);
        return false;
    }

    @Override
    public boolean editProduct(Store ownerStore, UserSystem user, int productSn, String productName, String category, int amount, double cost) {
        return ownerStore.editProduct(user, productSn, productName, category, amount, cost);
    }

    @Override
    public void updateStoreAndUserSystem(Store ownedStore, UserSystem userSystem) {
        stores.add(ownedStore);
    }

    @Override
    public boolean addPermissionToManager(Store ownedStore, UserSystem ownerUser, UserSystem managerStore, StorePermission storePermission) {
        return ownedStore.addPermissionToManager(ownerUser, managerStore, storePermission);
    }

    @Override
    public boolean removePermission(Store ownedStore, UserSystem ownerUser, UserSystem managerStore, StorePermission storePermission) {
        return ownedStore.removePermission(ownerUser, managerStore, storePermission);
    }

    @Override
    public boolean saveProductInShoppingBag(String username, ShoppingCart shoppingCart, Store store, Product product, int amount) {
        return shoppingCart.addProductToCart(store, product.cloneProduct(), amount);
    }

    @Override
    public boolean removeProductInShoppingBag(String username, ShoppingCart shoppingCart, Store store, Product product) {
        return shoppingCart.removeProductInCart(store, product);
    }

    @Override
    public boolean changeProductAmountInShoppingBag(String username, ShoppingCart shoppingCart, int storeId, int amount, int productSn) {
        return shoppingCart.changeProductAmountInShoppingBag(storeId, amount, productSn);
    }

    @Override
    public void login(String username, ShoppingCart shoppingCart) {
        // Its need to be empty
    }

    @Override
    public void saveShoppingCart(UserSystem username) {
        // Its need to be empty
    }

    @Override
    public ShoppingCart getShoppingCart(String username, UUID uuid) {
        if(isValidUuid(username, uuid)){
            if (users.stream().anyMatch(userSystem -> userSystem.getUserName().equals(username)))
                return users.stream()
                        .filter(userSystem -> userSystem.getUserName().equals(username))
                        .findFirst()
                        .map(UserSystem::getShoppingCart)
                        .orElseThrow(()-> new UserDontExistInTheSystemException(username));
            else
                return administrators.stream()
                        .filter(userSystem -> userSystem.getUserName().equals(username))
                        .findFirst()
                        .map(UserSystem::getShoppingCart)
                        .orElseThrow(()-> new UserDontExistInTheSystemException(username));
        }
        throw new UserDontExistInTheSystemException(username);
    }

    @Override
    public void loadShoppingCart(UserSystem user) {
        // Its need to be empty
    }

    @Override
    public Set<OwnerToApprove> getMyOwnerToApprove(String ownerUsername, UUID uuid) {
        if (isValidUuid(ownerUsername, uuid)) {
            return users.stream()
                    .filter(userSystem -> userSystem.getUserName().equals(ownerUsername))
                    .findFirst()
                    .map(UserSystem::getOwnerToApproves)
                    .orElse(new HashSet<>());
        }
        return new HashSet<>();
    }

    @Override
    public boolean approveOwner(Store ownedStore, UserSystem ownerUser, String ownerToApprove, boolean status) {
        return ownedStore.approveOwner(ownerUser, ownerToApprove, status);
    }

    @Override
    public void updateDailyVisitors(DailyVisitorsField dailyVisitorsField) {
        Date toDay = new Date();
        dailyVisitors.stream()
                .filter(dailyVisitor -> DateUtils.isSameDay(dailyVisitor.getDate(), toDay))
                .findFirst()
                .map(dailyVisitor -> dailyVisitor.update(dailyVisitorsField))
                .orElseGet(() -> {
                    DailyVisitor dailyVisitor = new DailyVisitor();
                    dailyVisitors.add(dailyVisitor);
                    return dailyVisitor.update(dailyVisitorsField);
                });
    }

    @Override
    public Set<DailyVisitor> getDailyVisitors(String username, RequestGetDailyVisitors requestGetDailyVisitors, UUID uuid) {
        if (isValidUuid(username, uuid) && isAdmin(username)) {
            List<DailyVisitor> dailyVisitors = this.dailyVisitors.stream()
                    .filter(dailyVisitor -> dailyVisitor.getDate().after(requestGetDailyVisitors.getStart()) && dailyVisitor.getDate().before(requestGetDailyVisitors.getEnd()))
                    .collect(Collectors.toList());
            return IntStream.range(requestGetDailyVisitors.getFirstIndex(), requestGetDailyVisitors.getLastIndex())
                    .mapToObj(dailyVisitors::get)
                    .collect(Collectors.toSet());
        }
        throw new NotAdministratorException(username);
    }

    private int getNewIdStore() {
        return idAccStore++;
    }

    private int getNewIdProduct() {
        return idAccProduct++;
    }

    private int getNewIdDiscount() {
        return idAccDiscount++;
    }

    private int getNewIdPolicy() {
        return idAccPolicy++;
    }

}

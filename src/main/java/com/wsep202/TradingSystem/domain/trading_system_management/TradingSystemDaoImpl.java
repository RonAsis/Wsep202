package com.wsep202.TradingSystem.domain.trading_system_management;

import com.wsep202.TradingSystem.domain.exception.NotAdministratorException;
import com.wsep202.TradingSystem.domain.image.ImagePath;
import com.wsep202.TradingSystem.domain.image.ImageUtil;
import javafx.util.Pair;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.util.*;


public class TradingSystemDaoImpl implements TradingSystemDao {

    private Set<Store> stores;
    private Set<UserSystem> users;
    private Set<UserSystem> administrators;

    public TradingSystemDaoImpl() {
        this.stores = new HashSet<>();
        this.users = new HashSet<>();
        this.administrators = new HashSet<>();
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

    private Optional<UserSystem> findUserSystem(Set<UserSystem> userSystems, String username){
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
    public List<Product> searchProductByName(String productName) {
        return new ArrayList<>(stores.stream()
                .map(store -> store.searchProductByName(productName))
                .reduce((products, products2) -> {
                    products.addAll(products2);
                    return products;
                })
                .orElse(new HashSet<>()));
    }

    @Override
    public List<Product> searchProductByCategory(ProductCategory productCategory) {
        return new ArrayList<>(stores.stream()
                .map(store -> store.searchProductByCategory(productCategory))
                .reduce((products, products2) -> {
                    products.addAll(products2);
                    return products;
                })
                .orElse(new HashSet<>()));
    }

    @Override
    public List<Product> searchProductByKeyWords(List<String> keyWords) {
        return new ArrayList<>(stores.stream()
                .map(store -> store.searchProductByKeyWords(keyWords))
                .reduce((products, products2) -> {
                    products.addAll(products2);
                    return products;
                })
                .orElse(new HashSet<>()));
    }

    @Override
    public void addStore(Store newStore) {
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

}

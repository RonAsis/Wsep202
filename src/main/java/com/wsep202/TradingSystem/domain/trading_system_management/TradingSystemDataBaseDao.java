package com.wsep202.TradingSystem.domain.trading_system_management;

import com.wsep202.TradingSystem.domain.image.ImagePath;
import com.wsep202.TradingSystem.domain.image.ImageUtil;
import com.wsep202.TradingSystem.domain.trading_system_management.Repositories.StoreRepository;
import com.wsep202.TradingSystem.domain.trading_system_management.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TradingSystemDataBaseDao implements TradingSystemDao{

    private final UserRepository userRepository;
    private final StoreRepository storeRepository;

    @Override
    public void registerAdmin(UserSystem admin) {
        userRepository.save(admin);
    }

    private boolean isNull(Object object){
        if(object == null)
            return false;
        return true;
    }

    @Override
    public boolean isRegistered(UserSystem userSystem) {
        // if NULL returned then the user isn't register
        return isNull(userRepository.findByUserName(userSystem.getUserName()));
    }

    @Override
    @Transactional
    public void addUserSystem(UserSystem userToRegister, MultipartFile image) {
        String urlImage = null;
        if (Objects.nonNull(image)) {
            urlImage = ImageUtil.saveImage(ImagePath.ROOT_IMAGE_DIC + ImagePath.USER_IMAGE_DIC + image.getOriginalFilename(), image);
        }
        userToRegister.setImageUrl(urlImage);
        userRepository.save(userToRegister);
    }

    @Override
    public Optional<UserSystem> getUserSystem(String username) {
        return Optional.ofNullable(userRepository.findByUserName(username));
    }

    @Override
    public boolean isAdmin(String username) {
        return userRepository.findByUserName(username).isAdmin();
    }

    @Override
    public Optional<UserSystem> getAdministratorUser(String username) {
        Optional<UserSystem> userSystemOptional = Optional.ofNullable(userRepository.findByUserName(username));
        if(userSystemOptional != null)
            if(userSystemOptional.get().isAdmin())
                return userSystemOptional;
        return Optional.empty();
    }

    @Override
    public Optional<Store> getStore(int storeId) {
        return Optional.ofNullable(storeRepository.findByStoreId(storeId));
    }

    @Override
    public List<Product> searchProductByName(String productName) {
        return new ArrayList<>(storeRepository.findAll().stream()
                .map(store -> store.searchProductByName(productName))
                .reduce((products, products2) -> {
                    products.addAll(products2);
                    return products;
                })
                .orElse(new HashSet<>()));
    }

    @Override
    public List<Product> searchProductByCategory(ProductCategory productCategory) {
        return new ArrayList<>(storeRepository.findAll().stream()
                .map(store -> store.searchProductByCategory(productCategory))
                .reduce((products, products2) -> {
                    products.addAll(products2);
                    return products;
                })
                .orElse(new HashSet<>()));
    }

    @Override
    public List<Product> searchProductByKeyWords(List<String> keyWords) {
        return new ArrayList<>(storeRepository.findAll().stream()
                .map(store -> store.searchProductByKeyWords(keyWords))
                .reduce((products, products2) -> {
                    products.addAll(products2);
                    return products;
                })
                .orElse(new HashSet<>()));
    }

    @Override
    public void addStore(Store newStore) {
        storeRepository.save(newStore);
    }

    @Override
    public Set<Store> getStores() {
        List<Store> res = storeRepository.findAll();
        return res.stream().collect(Collectors.toSet());
    }

    @Override
    public Set<Product> getProducts() {
        return storeRepository.findAll().stream()
                .map(Store::getProducts)
                .reduce(new HashSet<>(), (productsAcc, products) -> {
                    productsAcc.addAll(products);
                    return productsAcc;
                });
    }

    @Override
    public Set<UserSystem> getUsers() {
        List<UserSystem> res = userRepository.findAll();
        return res.stream().collect(Collectors.toSet());
    }
}

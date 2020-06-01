package com.wsep202.TradingSystem.domain.trading_system_management;

import com.wsep202.TradingSystem.domain.image.ImagePath;
import com.wsep202.TradingSystem.domain.image.ImageUtil;
import com.wsep202.TradingSystem.data_access_layer.StoreRepository;
import com.wsep202.TradingSystem.data_access_layer.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class TradingSystemDataBaseDao implements TradingSystemDao{

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
        return userRepository.exists(Example.of(UserSystem.builder()
                .userName(username)
                .isAdmin(true)
                .build()));
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
}

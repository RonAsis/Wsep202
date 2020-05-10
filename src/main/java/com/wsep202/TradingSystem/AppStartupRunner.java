package com.wsep202.TradingSystem;

import com.github.javafaker.Faker;
import com.wsep202.TradingSystem.domain.trading_system_management.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import static com.wsep202.TradingSystem.domain.image.ImagePath.ROOT_IMAGE_DIC;
import static com.wsep202.TradingSystem.domain.image.ImagePath.USER_IMAGE_DIC;

@Slf4j
@Component
@RequiredArgsConstructor
public class AppStartupRunner implements ApplicationRunner {

    private final TradingSystem tradingSystem;

    private final int NUM_OF_OBJECTS = 10;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        createDirForImages();
        initialTheSystem();
    }

    private void createDirForImages() {
        log.info("creation the Directories for images");
        File imageRootDir = new File(ROOT_IMAGE_DIC);
        if (!imageRootDir.exists()) {
            if (imageRootDir.mkdir()) {
                log.info("The directory of the images created");
                File userDir = new File(imageRootDir, USER_IMAGE_DIC);
                userDir.mkdir();
            }
        } else {
            log.info("The Directory of the images exists");
        }
    }


    /////////////////////////// for Demo //////////////////////////////////////////////////////

    private void initialTheSystem() {
        List<UserSystem> users = initialUsers();
        initialStores(users);
        Set<Store> stores = tradingSystem.getStores();
        initialProductsForStores(stores);
    }

    private List<UserSystem> initialUsers() {
        return Arrays.stream(new UserSystem[NUM_OF_OBJECTS])
                .map(o -> {
                    Faker faker = new Faker();
                    UserSystem user = UserSystem.builder()
                            .userName(faker.name().username())
                            .firstName(faker.name().firstName())
                            .lastName(faker.name().lastName())
                            .password(faker.code().asin())
                            .build();
                    log.info(String.format("user: %s , %s", user.getUserName(), user.getPassword()));
                    tradingSystem.registerNewUser(user, null);
                    return user;
                })
                .collect(Collectors.toList());
    }

    private void initialStores(List<UserSystem> users) {
        Arrays.stream(new Store[NUM_OF_OBJECTS])
                .forEach(o -> {
                    Faker faker = new Faker();
                    UserSystem user = users.get(createRandomNumber(0, users.size() - 1));
                    String storeName = faker.company().name();
                    log.info(String.format("store: %s of the owner %s", storeName, user.getUserName()));
                    tradingSystem.openStore(user, storeName, faker.company().toString());
                });
    }

    private void initialProductsForStores(Set<Store> stores) {
        stores.forEach(store -> {
            UserSystem userSystem = store.getOwners().stream().findFirst().get();
            Arrays.stream(new Product[NUM_OF_OBJECTS]).forEach( product -> {
                Faker faker = new Faker();
                Product newProduct = new Product(faker.book().title(),
                        ProductCategory.values()[createRandomNumber(0, ProductCategory.values().length - 1)],
                        createRandomNumber(0, 100),
                        createRandomNumber(0, 10000), store.getStoreId());
                store.addNewProduct(userSystem, newProduct);
            });
        });
    }

    private int createRandomNumber(int min, int max) {
        return (int) (Math.random() * (max - min));
    }
}

package com.wsep202.TradingSystem;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.github.javafaker.Faker;
import com.wsep202.TradingSystem.domain.trading_system_management.*;
import com.wsep202.TradingSystem.domain.trading_system_management.policy_purchase.Purchase;
import com.wsep202.TradingSystem.dynamic_start_up.ActivationPaths;
import com.wsep202.TradingSystem.dynamic_start_up.InitialSystem;
import com.wsep202.TradingSystem.dynamic_start_up.Context;
import com.wsep202.TradingSystem.utils.FormatFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.wsep202.TradingSystem.domain.image.ImagePath.ROOT_IMAGE_DIC;
import static com.wsep202.TradingSystem.domain.image.ImagePath.USER_IMAGE_DIC;

@Slf4j
@Component
@RequiredArgsConstructor
public class AppStartupRunner implements ApplicationRunner {

    private final TradingSystem tradingSystem;

    private final TradingSystemFacade tradingSystemFacade;


    private ObjectMapper yamlReader = new ObjectMapper(new YAMLFactory());

    private final int NUM_OF_OBJECTS = 10;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        createDirForImages();
        if(thereIsInitialFiles()){
            initialAccordingByFiles();
        }else {
            initialTheSystem();
        }
    }

    ///////////////////////////////////////////// for files System 1.1 /////////////////////////
    private void initialAccordingByFiles() {
        Context context = new Context();
        try (Stream<Path> walk = Files.walk(ResourceUtils.getFile(ActivationPaths.INITIAL_FILES).toPath())) {

            //collect all the yaml files
            List<Path> pathList = walk.collect(Collectors.toList());

            pathList = FormatFile.filterYamlFiles(pathList);

            for (Path path : pathList) {
                doActions(path, context);
            }

        } catch (IOException e) {
            //if the directory don't exist
            log.error("The Directory don't exists : " + "\n\t" + "  {} ", e.getMessage(), e);
        }
    }

    private void doActions(Path path, Context context) {
        InputStream inputStream = getInputStream(path);
        Optional<InitialSystem> activityElement = getActivityElement(inputStream, path);
        activityElement.ifPresent(initialSystem -> {
            initialSystem.getActivities().forEach(activityDefinition -> activityDefinition.apply(context, tradingSystemFacade));
        });
    }

    /**
     * get input stream
     *
     * @param pathToWorkflowType - pathToWorkflowType
     * @return InputStream
     */
    private InputStream getInputStream(Path pathToWorkflowType) {
        InputStream inputstream = null;
        try {
            if (Objects.nonNull(pathToWorkflowType)) {
                inputstream = new FileInputStream(pathToWorkflowType.toFile());
            }
        } catch (FileNotFoundException e) {
            log.error("Cant open the file: {}", pathToWorkflowType, e);
        }

        //get the input stream
        return inputstream;
    }

    private Optional<InitialSystem> getActivityElement(InputStream inputStream, Path path) {
        InitialSystem initialSystem = null;
        try {
            // if the input stream is succeed
            if (Objects.nonNull(inputStream)) {
                initialSystem = FormatFile.mapper.readValue(inputStream, InitialSystem.class);
            }
        } catch (IOException e) {
            log.error("The Structure of the file is invalid Activity : {}", path, e);
        }
        return Optional.ofNullable(initialSystem);
    }

    private boolean thereIsInitialFiles() {
        File initialRootDir = new File(ActivationPaths.INITIAL_FILES);
        return initialRootDir.exists() && Objects.requireNonNull(initialRootDir.listFiles()).length > 0;
    }

    /////////////////////////////////////////////for images /////////////////////////////////////////
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
        initialPurchase(stores.stream().findFirst().get());
    }

    private void initialPurchase(Store store) {
//        List<Product> products = new ArrayList<>(store.getProducts());
//        UserSystem userSystem = new ArrayList<>(store.getOwners()).get(0);
//        Purchase purchase1 = Purchase.builder()
//                .min(3)
//                .productId(products.get(0).getProductSn())
//                .max(Integer.MAX_VALUE)
//                .build();
//
//        log.info("for test policy !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
//        log.info("username: " + userSystem.getUserName() + "password: " + userSystem.getPassword());
//        log.info("product 0: " + products.get(0).getName() + "product 1: " + products.get(1).getName());
//        log.info("Store name: " + store.getStoreName());
//
//        Purchase purchase2 = Purchase.builder()
//                .min(0)
//                .isShoppingBagPurchaseLimit(false)
//                .productId(products.get(1).getProductSn())
//                .max(5)
//                .build();
//
//        List<Purchase> purchases = new LinkedList<>();
//
//        purchases.add(purchase1);
//        purchases.add(purchase2);
//        Purchase purchaseComposed = Purchase.builder()
//                .isShoppingBagPurchaseLimit(true)
//                .min(0)
//                .composedPurchasePolicies(purchases)
//                .productId(products.get(1).getProductSn())
//                .max(5)
//                .build();
//
//        store.addPurchase(userSystem, purchaseComposed);
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

package com.wsep202.TradingSystem.helprTests;

import com.wsep202.TradingSystem.domain.trading_system_management.*;
import com.wsep202.TradingSystem.domain.trading_system_management.purchase.PurchasePolicy;

import java.util.*;

public class SetUpObjects {

    // ******************************* set up ******************************* //

    public static UserSystem setUpOwnerStore() {
        return UserSystem.builder()
                .userName("KingRagnar")
                .password("Odin12")
                .firstName("Ragnar")
                .lastName("Lodbrok").build();
    }

    public static BillingAddress setUpBillingAddress() {
        return BillingAddress.builder()
                .zipCode("1234567")
                .build();
    }

    public static PaymentDetails setUpPaymentDetails() {
        return PaymentDetails.builder()
                .creditCardNumber("123456789")
                .ccv(3)
                .month("10")
                .year("2021")
                .build();
    }

    public static List<Receipt> setUpReceipts() {
        List<Receipt> receipts = new ArrayList<>();
        for (int counter = 0; counter <= 10; counter++) {
            receipts.add(Receipt.builder()
                    .receiptSn(counter)
                    .storeId(counter)
                    .userName("username" + counter)
                    .purchaseDate(new Date())
                    .amountToPay(counter)
                    .productsBought(createMapOfProducts())
                    .build());
        }
        return receipts;
    }

    public static Set<Product> setUpProducts() {
        Set<Product> products = new HashSet<>();
        for (int counter = 0; counter <= 10; counter++) {
            products.add(Product.builder()
                    .productSn(counter)
                    .name("productName" + counter)
                    .category(ProductCategory.values()[counter % ProductCategory.values().length])
                    .amount(counter)
                    .cost(counter)
                    .rank(counter)
                    .storeId(counter)
                    .build());
        }
        return products;
    }

    public static Map<Product, Integer> createMapOfProducts() {
        Map<Product, Integer> products = new HashMap<>();
        for (int counter = 1; counter <= 10; counter++) {
            products.put((Product.builder()
                    .productSn(counter)
                    .name("productName" + counter)
                    .category(ProductCategory.values()[counter % ProductCategory.values().length])
                    .amount(counter + 1)
                    .cost(counter)
                    .rank(counter)
                    .storeId(counter)
                    .build()), counter);
        }
        return products;
    }

    public static Set<UserSystem> setupUsers() {
        Set<UserSystem> userSystems = new HashSet<>();
        for (int counter = 0; counter <= 10; counter++) {
            userSystems.add(UserSystem.builder()
                    .userName("username" + counter)
                    .password("password" + counter)
                    .firstName("firstName" + counter)
                    .lastName("lastName" + counter)
                    .isLogin(false)
                    .build());
        }
        return userSystems;
    }

    public static Set<Store> setUpStores() {
        Set<Store> stores = new HashSet<>();
        for (int counter = 0; counter <= 10; counter++) {
            stores.add(Store.builder()
                    .storeId(counter)
//                    .purchasePolicy(new PurchasePolicy())
//                    .discountPolicy(new DiscountPolicy())
                    .storeName("storeName" + counter)
                    .rank(counter)
                    .build());
        }
        return stores;
    }

    public static Store createStore() {
        //init
        int storeId = 1;
        int rank = 1;
        String storeName = "storeName";

        //init products
        Set<Product> products = setUpProducts();

//        PurchasePolicy purchasePolicy = new PurchasePolicy();
//        DiscountPolicy discountPolicy = new DiscountPolicy();
        Set<UserSystem> owners = setupUsers();
        List<Receipt> receipts = setUpReceipts();

        return Store.builder()
                .storeId(storeId)
                .owners(owners)
                .storeName(storeName)
//                .discountPolicy(discountPolicy)
                .products(products)
                .receipts(receipts)
                .rank(rank)
//                .purchasePolicy(purchasePolicy)
                .build();
    }

    public static Product createProduct(int productId) {
        return Product.builder()
                .storeId(productId)
                .category(ProductCategory.values()[productId % ProductCategory.values().length])
                .rank(productId)
                .cost(productId)
                .amount(productId)
                .name("product" + productId)
                .productSn(productId)
                .build();
    }

    public static ShoppingCart setUpShoppingCart() {
        //create shoppingBags
        Map<Integer, Integer> shoppingBagMap = new HashMap<>();
        for (int counter = 0; counter < 10; counter++) {
            shoppingBagMap.put(counter, counter);
        }
        //ShoppingBag shoppingBag = new ShoppingBag(shoppingBagMap);

        Map<Store, ShoppingBag> shoppingBags = new HashMap<>();
        Set<Store> stores = setUpStores();
        //stores.forEach(store1 -> shoppingBags.put(store1, shoppingBag));

        // create ShoppingCart
        return ShoppingCart.builder()
                .shoppingBagsList(shoppingBags)
                .build();

    }

}

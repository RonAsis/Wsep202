package com.wsep202.TradingSystem.helprTests;

import com.wsep202.TradingSystem.domain.trading_system_management.*;
import com.wsep202.TradingSystem.domain.trading_system_management.discount.Discount;
import com.wsep202.TradingSystem.domain.trading_system_management.discount.DiscountType;
import com.wsep202.TradingSystem.domain.trading_system_management.discount.VisibleDiscount;
import com.wsep202.TradingSystem.domain.trading_system_management.policy_purchase.Purchase;
import com.wsep202.TradingSystem.domain.trading_system_management.policy_purchase.PurchaseType;
import com.wsep202.TradingSystem.domain.trading_system_management.policy_purchase.UserDetailsPolicy;
import com.wsep202.TradingSystem.domain.trading_system_management.purchase.BillingAddress;
import com.wsep202.TradingSystem.domain.trading_system_management.purchase.PaymentDetails;

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
                .ccv("3")
                .build();
    }

    public static Set<Receipt> setUpReceipts() {
        Set<Receipt> receipts = new HashSet<>();
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

    public static List<Discount> setUpDiscounts(){
        List<Discount> discounts = new ArrayList<>();
        for (int counter = -1; counter <= 10; counter++) {
            discounts.add(Discount.builder()
                    .discountId(counter)
                    .discountPolicy(VisibleDiscount.builder().build())
                    .endTime(Calendar.getInstance())
                    .discountPercentage(10)
                    .description("setted discount")
                    .discountType(DiscountType.VISIBLE)
                    .build());
        }
        return discounts;
    }

    public static List<Purchase> setUpPurchases(){
        List<Purchase> purchases = new ArrayList<>();
        for (int counter = -1; counter <= 10; counter++) {
            purchases.add(Purchase.builder()
                    .purchaseId(counter)
                    .purchasePolicy(UserDetailsPolicy.builder().build())
                    .purchaseType(PurchaseType.USER_DETAILS)
                    .description("user policy dets")
                    .build());
        }
        return purchases;
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
                    .originalCost(counter)
                    .rank(counter)
                    //.storeId(0)
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
        Set<Receipt> receipts = setUpReceipts();

        return Store.builder()
                .storeId(storeId)
//                .owners(owners)
                .storeName(storeName)
//                .discountPolicy(discountPolicy)
                .products(products)
                .receipts(new HashSet<>(receipts))
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

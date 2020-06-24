package com.wsep202.TradingSystem.helprTests;

import com.wsep202.TradingSystem.domain.trading_system_management.*;
import com.wsep202.TradingSystem.domain.trading_system_management.discount.Discount;
import com.wsep202.TradingSystem.domain.trading_system_management.policy_purchase.Purchase;
import com.wsep202.TradingSystem.dto.*;
import org.junit.jupiter.api.Assertions;

import java.util.*;

public class AssertionHelperTest {

    // ******************************* assert functions ******************************* //
    public static void assertUserSystem(Set<UserSystem> userSystems, Set<UserSystemDto> userSystemDtos) {
        if (Objects.nonNull(userSystems)) {
            userSystems.forEach(userSystemExpected -> {
                Optional<UserSystemDto> userSystemOptional = userSystemDtos.stream()
                        .filter(userSystem1 -> userSystem1.getUserName().equals(userSystemExpected.getUserName()))
                        .findFirst();
                Assertions.assertTrue(userSystemOptional.isPresent());
                UserSystemDto userSystemDto = userSystemOptional.get();
                Assertions.assertEquals(userSystemExpected.getUserName(), userSystemDto.getUserName());
                Assertions.assertEquals(userSystemExpected.getFirstName(), userSystemDto.getFirstName());
                Assertions.assertEquals(userSystemExpected.getLastName(), userSystemDto.getLastName());
            });
        }
    }

    public static void assertShoppingCart(ShoppingCart shoppingCartExpected, ShoppingCartDto shoppingCartActual) {
        shoppingCartExpected.getShoppingBagsList().forEach((storeKey, shoppingBagExpected) -> {
//            Map.Entry<StoreDto, ShoppingBagDto> storeDtoShoppingBagDtoEntry = shoppingCartActual.getShoppingBags().entrySet().stream()
//                    .filter(entry -> entry.getKey().getStoreId() == storeKey.getStoreId())
//                    .findFirst().orElseThrow(RuntimeException::new);
            //ShoppingBagDto shoppingBagDto = storeDtoShoppingBagDtoEntry.getValue();
            //Assertions.assertNotNull(shoppingBagDto);
            //Assertions.assertEquals(shoppingBagExpected.getProductListFromStore(), shoppingBagDto.getProductListFromStore());
        });
    }

    public static void assertSetStore(Set<Store> stores, Set<StoreDto> storeDtos) {
        stores.forEach(
                storeExpected -> {
                    Optional<StoreDto> storeDtoOpt = storeDtos.stream()
                            .filter(storeDto -> storeDto.getStoreId() == storeExpected.getStoreId())
                            .findFirst();
                    Assertions.assertTrue(storeDtoOpt.isPresent());
                    StoreDto storeDto = storeDtoOpt.get();
                    assertionStore(storeExpected, storeDto);
                }
        );
    }



    public static void assertProducts(Set<Product> products, Set<ProductDto> productsDtos) {
        products.forEach(product -> {
            Optional<ProductDto> productDtoOptional = productsDtos.stream().filter(productDto -> productDto.getProductSn() == product.getProductSn())
                    .findFirst();
            Assertions.assertTrue(productDtoOptional.isPresent());
            assertProduct(product, productDtoOptional.get());
        });
    }

    public static void assertProduct(Product product, ProductDto productDto) {
        //Assertions.assertEquals(product.getProductSn(), productDto.getProductSn());
        Assertions.assertEquals(product.getName(), productDto.getName());
        //verify its ok the patch
        Assertions.assertEquals(product.getCategory().toString(), productDto.getCategory());
        Assertions.assertEquals(product.getAmount(), productDto.getAmount());
        Assertions.assertEquals(product.getCost(), productDto.getCost());
        Assertions.assertEquals(product.getOriginalCost(), productDto.getOriginalCost());
        //Assertions.assertEquals(product.getRank(), productDto.getRank());
        Assertions.assertEquals(product.getStoreId(), productDto.getStoreId());
    }

//    public static void assertDiscountPolicy(DiscountPolicy discountPolicy, DiscountPolicyDto discountPolicy1) {
//        Assertions.assertEquals(discountPolicy.getWhoCanBuyStatus(), discountPolicy1.getWhoCanBuyStatus());
//        Assertions.assertEquals(discountPolicy.isAllAllowed(), discountPolicy1.isAllAllowed());
//    }

    public static void assertReceipts(List<Receipt> receipts, List<ReceiptDto> receiptDtos) {
        if (Objects.nonNull(receipts)) {
            Assertions.assertEquals(receipts.size(), receiptDtos.size());
            receipts.forEach(
                    receipt -> {
                        Optional<ReceiptDto> receiptDtoOpt = receiptDtos.stream()
                                .filter(receiptDto -> receiptDto.getReceiptSn() == receipt.getReceiptSn())
                                .findFirst();
                        Assertions.assertTrue(receiptDtoOpt.isPresent());
                        ReceiptDto receiptDto = receiptDtoOpt.get();
                        assertionReceipt(receipt, receiptDto);
                    }
            );
        }
    }
    public static void assertDiscounts(List<Discount> discounts, List<DiscountDto> discountDtos) {
        if (Objects.nonNull(discounts)) {
            Assertions.assertEquals(discounts.size(), discountDtos.size());
            discounts.forEach(
                    discount -> {
                        Optional<DiscountDto> discountDtoOpt = discountDtos.stream()
                                .filter(receiptDto -> receiptDto.getDiscountId() == discount.getDiscountId())
                                .findFirst();
                        Assertions.assertTrue(discountDtoOpt.isPresent());
                        DiscountDto discountDto = discountDtoOpt.get();
                        assertionDiscount(discount, discountDto);
                    }
            );
        }
    }

    public static void assertPurchases(List<Purchase> purchases, List<PurchasePolicyDto> purchasePolicyDtos) {
        if (Objects.nonNull(purchases)) {
            Assertions.assertEquals(purchases.size(), purchasePolicyDtos.size());
            purchases.forEach(
                    purchase -> {
                        Optional<PurchasePolicyDto> discountDtoOpt = purchasePolicyDtos.stream()
                                .filter(receiptDto -> receiptDto.getPurchaseId() == purchase.getPurchaseId())
                                .findFirst();
                        Assertions.assertTrue(discountDtoOpt.isPresent());
                        PurchasePolicyDto purchasePolicyDto = discountDtoOpt.get();
                        assertionPurchase(purchase, purchasePolicyDto);
                    }
            );
        }
    }

    public static void assertionPurchase(Purchase purchase, PurchasePolicyDto purchasePolicyDto) {
        Assertions.assertEquals(purchase.getPurchaseId(), purchasePolicyDto.getPurchaseId());
        Assertions.assertEquals(purchase.getPurchaseType().toString(), purchasePolicyDto.getPurchaseType());
        Assertions.assertEquals(purchase.getDescription(), purchasePolicyDto.getDescription());

    }

    public static void assertionDiscount(Discount discount, DiscountDto discountDto) {
        Assertions.assertEquals(discount.getDiscountId(), discountDto.getDiscountId());
        Assertions.assertEquals(discount.getDiscountType().type, discountDto.getDiscountType().toLowerCase());
        Assertions.assertEquals(discount.getDescription(), discountDto.getDescription());
        Assertions.assertEquals(discount.getDiscountPercentage(), discountDto.getDiscountPercentage());
        Assertions.assertEquals(discount.getEndTime().getTime(),discountDto.getEndTime());
    }

    public static void assertionReceipt(Receipt receipt, ReceiptDto receiptDto) {
        Assertions.assertEquals(receipt.getReceiptSn(), receiptDto.getReceiptSn());
        Assertions.assertEquals(receipt.getStoreId(), receiptDto.getStoreId());
        Assertions.assertEquals(receipt.getUserName(), receiptDto.getUsername());
        Assertions.assertEquals(receipt.getAmountToPay(), receiptDto.getAmountToPay());
        // assertMapProducts(receipt.getProductsBought(), receiptDto.get());
    }

    public static void assertMapProducts(Map<Product, Integer> products, Map<ProductDto, Integer> productsDtos) {
        products.keySet().forEach(product -> {
            Optional<ProductDto> productDtoOptional = productsDtos.keySet().stream().filter(productDto ->
                    productDto.getProductSn() == product.getProductSn())
                    .findFirst();
            Assertions.assertTrue(productDtoOptional.isPresent());
            assertProduct(product, productDtoOptional.get());
            Assertions.assertEquals(products.get(product).intValue(), productsDtos.get(productDtoOptional.get()).intValue());
        });
    }

    public static void assertionStore(Store store, StoreDto storeDto) {
        Assertions.assertEquals(store.getStoreId(), storeDto.getStoreId());
        Assertions.assertEquals(store.getStoreName(), storeDto.getStoreName());
        assertProducts(store.getProducts(), storeDto.getProducts());
    }



}

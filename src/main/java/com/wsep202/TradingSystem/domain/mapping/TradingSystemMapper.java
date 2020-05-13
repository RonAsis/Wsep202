package com.wsep202.TradingSystem.domain.mapping;

import com.github.rozidan.springboot.modelmapper.TypeMapConfigurer;
import com.wsep202.TradingSystem.domain.trading_system_management.*;
import com.wsep202.TradingSystem.dto.*;
import org.modelmapper.Converter;
import org.modelmapper.TypeMap;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public class TradingSystemMapper {

    @Component
    public static class StoreToStoreDtoConverter extends TypeMapConfigurer<Store, StoreDto> {
        @Override
        public void configure(TypeMap<Store, StoreDto> typeMap) {
            Converter<DiscountType, String> discountTypeStringConverter =
                    ctx -> ctx.getSource() == null ? null : ctx.getSource().type;

//            typeMap.addMappings(mapper -> mapper.using(discountTypeStringConverter)
//                    .map(Store::getDiscountType, StoreDto::setDiscountType));
//
//            //
//            Converter<PurchaseType, String> purchaseTypeStringConverter =
//                    ctx -> ctx.getSource() == null ? null : ctx.getSource().type;
//
//            typeMap.addMappings(mapper -> mapper.using(purchaseTypeStringConverter)
//                    .map(Store::getPurchaseType, StoreDto::setPurchaseType));


        }
    }

    @Component
    public static class ProductToProductDtoConverter extends TypeMapConfigurer<Product, ProductDto> {
        @Override
        public void configure(TypeMap<Product, ProductDto> typeMap) {
            Converter<ProductCategory, String> productCategoryStringConverter =
                    ctx -> ctx.getSource() == null ? null : ctx.getSource().category;

            typeMap.addMappings(mapper -> mapper.using(productCategoryStringConverter)
                    .map(Product::getCategory, ProductDto::setCategory));
        }
    }

    @Component
    public static class ManagerStoreToManagerDtoListConverter extends TypeMapConfigurer<List<MangerStore>, List<ManagerDto>> {
        @Override
        public void configure(TypeMap<List<MangerStore>, List<ManagerDto>> typeMap) {
            typeMap.setConverter(context -> {
                List<ManagerDto> managerDtos = context.getSource().stream().map(mangerStore -> {
                    return ManagerDto.builder()
                            .username(mangerStore.getAppointedManager().getUserName())
                            .permissions(mangerStore.getStorePermissions().stream()
                                    .map(storePermission -> storePermission.function)
                                    .collect(Collectors.toList()))
                            .build();
                }).collect(Collectors.toList());
                context.getDestination().clear();
                context.getDestination().addAll(managerDtos);
                return context.getDestination();
            });
        }
    }

    @Component
    public static class ManagerStoreToManagerDtoConverter extends TypeMapConfigurer<MangerStore, ManagerDto> {
        @Override
        public void configure(TypeMap<MangerStore, ManagerDto> typeMap) {
            Converter<Set<StorePermission>, List<String>> permissionsConverter =
                    ctx -> ctx.getSource() == null ? null : ctx.getSource().stream()
                            .map(storePermission -> storePermission.function)
                            .collect(Collectors.toList());

            Converter<UserSystem, String> usernameConverter =
                    ctx -> ctx.getSource() == null ? null : ctx.getSource().getUserName();

            typeMap.addMappings(mapper -> mapper.using(permissionsConverter)
                    .map(MangerStore::getStorePermissions, ManagerDto::setPermissions));
            typeMap.addMappings(mapper -> mapper.using(usernameConverter)
                    .map(MangerStore::getAppointedManager, ManagerDto::setUsername));

        }
    }

    @Component
    public static class ProductDtoToProductConverter extends TypeMapConfigurer<ProductDto, Product> {
        @Override
        public void configure(TypeMap<ProductDto, Product> typeMap) {
            Converter<String, ProductCategory> productCategoryStringConverter =
                    ctx -> ctx.getSource() == null ? null : ProductCategory.getProductCategory(ctx.getSource());

            typeMap.addMappings(mapper -> mapper.using(productCategoryStringConverter)
                    .map(ProductDto::getCategory, Product::setCategory));
        }
    }

    @Component
    public static class ReceiptToReceiptDto extends TypeMapConfigurer<Receipt, ReceiptDto> {

        @Override
        public void configure(TypeMap<Receipt, ReceiptDto> typeMap) {
            typeMap.setPostConverter(context -> {
                Map<Product, Integer> productsBought = context.getSource().getProductsBought();
                if (Objects.nonNull(productsBought)) {
                    Map<ProductDto, Integer> productsBoughtDto = context.getDestination().getProductsBought();
                    Map<ProductDto, Integer> productsBoughtDtoPost = productsBoughtDto.entrySet().stream()
                            .collect(Collectors.toMap(
                                    entry -> {
                                        ProductDto productDto = entry.getKey();
                                        int productSn = productDto.getProductSn();
                                        ProductCategory productCategory = productsBought.keySet().stream()
                                                .filter(product -> product.getProductSn() == productSn)
                                                .map(Product::getCategory)
                                                .findFirst().orElse(ProductCategory.TOYS_HOBBIES);
                                        productDto.setCategory(productCategory.category);
                                        return productDto;
                                    }, Map.Entry::getValue
                            ));
                    context.getDestination().setProductsBought(productsBoughtDtoPost);
                }
                return context.getDestination();
            });
        }
    }

    @Component
    public static class ShoppingCartToProductShoppingCartDto extends TypeMapConfigurer<ShoppingCart, List<ProductShoppingCartDto>> {
        @Override
        public void configure(TypeMap<ShoppingCart, List<ProductShoppingCartDto>> typeMap) {
            typeMap.setConverter(context -> {
                Map<Store, ShoppingBag> shoppingBagsList = context.getSource().getShoppingBagsList();
                List<ProductShoppingCartDto> productShoppingCartDtos = new LinkedList<>();
                shoppingBagsList.values().stream()
                        .map(ShoppingBag::getProductListFromStore)
                        .forEach(shoppingBag -> shoppingBag.forEach((key, value) -> productShoppingCartDtos.add(ProductShoppingCartDto.builder()
                                .amountInShoppingCart(value)
                                .cost(key.getCost())
                                .name(key.getName())
                                .originalCost(key.getOriginalCost())
                                .productSn(key.getProductSn())
                                .storeId(key.getStoreId())
                                .build())));
                return productShoppingCartDtos;
            });
        }
    }
}

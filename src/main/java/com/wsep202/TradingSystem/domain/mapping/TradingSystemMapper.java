package com.wsep202.TradingSystem.domain.mapping;

import com.github.rozidan.springboot.modelmapper.TypeMapConfigurer;
import com.wsep202.TradingSystem.domain.trading_system_management.*;
import com.wsep202.TradingSystem.dto.*;
import org.modelmapper.Converter;
import org.modelmapper.TypeMap;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
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
    public static class ProductDtoToProductConverter extends TypeMapConfigurer<ProductDto, Product> {
        @Override
        public void configure(TypeMap<ProductDto, Product> typeMap) {
            Converter<String, ProductCategory > productCategoryStringConverter =
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
                if(Objects.nonNull(productsBought)) {
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
    public static class ShoppingCartToShoppingCartViewDto extends TypeMapConfigurer<ShoppingCart, ShoppingCartViewDto> {
        @Override
        public void configure(TypeMap<ShoppingCart, ShoppingCartViewDto> typeMap) {
            typeMap.setConverter(context -> {
                Map<Store, ShoppingBag> shoppingBagsList = context.getSource().getShoppingBagsList();
                Map<Integer, ShoppingBagViewDto> shoppingBags = shoppingBagsList.entrySet().stream()
                        .collect(Collectors.toMap(e -> e.getKey().getStoreId(),
                                e -> ShoppingBagViewDto.builder().
                                        productListFromStore(e.getValue().getProductListFromStore()).build()));
                return ShoppingCartViewDto.builder().shoppingBags(shoppingBags).build();
            });
        }
    }

}

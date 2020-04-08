package com.wsep202.TradingSystem.domain.mapping;

import com.github.rozidan.springboot.modelmapper.TypeMapConfigurer;
import com.wsep202.TradingSystem.domain.trading_system_management.*;
import com.wsep202.TradingSystem.service.user_service.dto.ProductDto;
import com.wsep202.TradingSystem.service.user_service.dto.StoreDto;
import org.modelmapper.Converter;
import org.modelmapper.TypeMap;
import org.springframework.stereotype.Component;

public class TradingSystemMapper {

    @Component
    public static class StoreToStoreDtoConverter extends TypeMapConfigurer<Store, StoreDto> {
        @Override
        public void configure(TypeMap<Store, StoreDto> typeMap) {
            Converter<DiscountType, String> discountTypeStringConverter =
                    ctx -> ctx.getSource() == null ? null : ctx.getSource().type;

            typeMap.addMappings(mapper -> mapper.using(discountTypeStringConverter)
                    .map(Store::getDiscountType, StoreDto::setDiscountType));

            //
            Converter<PurchaseType, String> purchaseTypeStringConverter =
                    ctx -> ctx.getSource() == null ? null : ctx.getSource().type;

            typeMap.addMappings(mapper -> mapper.using(purchaseTypeStringConverter)
                    .map(Store::getPurchaseType, StoreDto::setPurchaseType));


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
}

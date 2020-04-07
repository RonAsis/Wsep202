package com.wsep202.TradingSystem.domain.mapping;

import com.github.rozidan.springboot.modelmapper.TypeMapConfigurer;
import com.wsep202.TradingSystem.domain.trading_system_management.DiscountType;
import com.wsep202.TradingSystem.domain.trading_system_management.Product;
import com.wsep202.TradingSystem.domain.trading_system_management.PurchaseType;
import com.wsep202.TradingSystem.domain.trading_system_management.Store;
import com.wsep202.TradingSystem.service.user_service.dto.ProductDto;
import com.wsep202.TradingSystem.service.user_service.dto.StoreDto;
import org.modelmapper.Converter;
import org.modelmapper.TypeMap;
import org.springframework.stereotype.Component;

public class TradingSystemMapper {

    @Component
    public static class PurchaseTypeConverter extends TypeMapConfigurer<PurchaseType, String> {

        @Override
        public void configure(TypeMap<PurchaseType, String> typeMap) {
            Converter<PurchaseType, String> converter =
                    ctx -> ctx.getSource() == null ? null : ctx.getSource().type;

            typeMap.addMappings(mapper -> mapper.using(converter));
        }
    }

    @Component
    public static class DiscountTypeConverter extends TypeMapConfigurer<DiscountType, String> {

        @Override
        public void configure(TypeMap<DiscountType, String> typeMap) {
            Converter<DiscountType, String> converter =
                    ctx -> ctx.getSource() == null ? null : ctx.getSource().type;

            typeMap.addMappings(mapper -> mapper.using(converter));
        }
    }
}

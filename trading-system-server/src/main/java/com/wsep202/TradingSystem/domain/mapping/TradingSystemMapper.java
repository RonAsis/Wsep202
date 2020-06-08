package com.wsep202.TradingSystem.domain.mapping;

import com.github.rozidan.springboot.modelmapper.TypeMapConfigurer;
import com.wsep202.TradingSystem.domain.trading_system_management.*;
import com.wsep202.TradingSystem.domain.trading_system_management.discount.*;
import com.wsep202.TradingSystem.dto.*;
import org.modelmapper.Converter;
import org.modelmapper.TypeMap;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;


public class TradingSystemMapper {

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
                if (Objects.nonNull(context.getDestination())) {
                    context.getDestination().clear();
                    context.getDestination().addAll(managerDtos);
                }
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
            typeMap.addMappings(configurableMapExpression -> configurableMapExpression.skip(ReceiptDto::setProductsBought));
            typeMap.setPostConverter(context -> {
                Map<Product, Integer> productsBought = context.getSource().getProductsBought();
                if (Objects.nonNull(productsBought)) {
                    List<ProductDto> productDtos = context.getSource().getProductsBought().entrySet().stream()
                            .map(entry -> ProductDto.builder()
                                    .cost(entry.getKey().getCost())
                                    .name(entry.getKey().getName())
                                    .amount(entry.getValue())
                                    .originalCost(entry.getKey().getOriginalCost())
                                    .productSn(entry.getKey().getProductSn())
                                    .storeId(entry.getKey().getStoreId())
                                    .rank(entry.getKey().getRank())
                                    .category(entry.getKey().getCategory().category)
                                    .build())
                            .collect(Collectors.toList());
                    context.getDestination().setProductsBought(productDtos);
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

    @Component
    public static class DiscountDtoToDiscount extends TypeMapConfigurer<DiscountDto, Discount> {
        @Override
        public void configure(TypeMap<DiscountDto, Discount> typeMap) {
            typeMap.addMappings(configurableMapExpression -> configurableMapExpression.skip(Discount::setDiscountPolicy));

            Converter<String, DiscountType> stringToDiscountTypeConverter =
                    ctx -> ctx.getSource() == null ? null : DiscountType.getDiscountType(ctx.getSource());

            typeMap.addMappings(mapper -> mapper.using(stringToDiscountTypeConverter)
                    .map(DiscountDto::getDiscountType, Discount::setDiscountType));

            typeMap.setPostConverter(context -> {
                DiscountDto discountDto = context.getSource();
                Map<Product, Integer> amountOfProductsForApplyDiscounts = createDiscountMap(discountDto.getAmountOfProductsForApplyDiscounts());
                Map<Product, Integer> productsUnderThisDiscount = createDiscountMap(discountDto.getProductsUnderThisDiscount());
                CompositeOperator compositeOperator = Objects.nonNull(discountDto.getCompositeOperator()) ?
                        CompositeOperator.getCompositeOperators(discountDto.getCompositeOperator()) : null;
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(discountDto.getEndTime());
                DiscountPolicy discountPolicy;
                switch (context.getDestination().getDiscountType()) {
                    case COMPOSE:
                        discountPolicy = ComposedDiscount.builder()
                                .amountOfProductsForApplyDiscounts(amountOfProductsForApplyDiscounts)
                                .compositeOperator(compositeOperator)
                                .productsUnderThisDiscount(productsUnderThisDiscount)
                                .composedDiscounts(convertDiscountDtoToDiscount(discountDto.getComposedDiscounts()))
                                .build();
                        break;
                    case VISIBLE:
                        discountPolicy = VisibleDiscount.builder()
                                .amountOfProductsForApplyDiscounts(amountOfProductsForApplyDiscounts)
                                .build();
                        break;
                    case CONDITIONAL_PRODUCT:
                        discountPolicy = ConditionalProductDiscount.builder()
                                .productsUnderThisDiscount(productsUnderThisDiscount)
                                .productsApplyDiscounts(amountOfProductsForApplyDiscounts)
                                .build();
                        break;
                    case CONDITIONAL_STORE:
                        discountPolicy = ConditionalStoreDiscount.builder()
                                .minPrice(discountDto.getMinPrice())
                                .build();
                        break;
                    default:
                        discountPolicy = null;
                        break;
                }
                context.getDestination().setDiscountPolicy(discountPolicy);
                return context.getDestination();
            });
        }

        /**
         * the discounts of composite
         */
        private List<Discount> convertDiscountDtoToDiscount(List<DiscountDto> discounts) {
            return discounts.stream()
                    .map(discountDto -> {
                        Map<Product, Integer> amountOfProductsForApplyDiscounts = createDiscountMap(discountDto.getAmountOfProductsForApplyDiscounts());
                        Map<Product, Integer> productsUnderThisDiscount = createDiscountMap(discountDto.getProductsUnderThisDiscount());
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(discountDto.getEndTime());
                        Discount discount = Discount.builder()
                                .endTime(calendar)
                                .discountPercentage(discountDto.getDiscountPercentage())
                                .discountId(discountDto.getDiscountId())
                                .description(discountDto.getDescription())
                                .discountType(DiscountType.getDiscountType(discountDto.getDiscountType()))
                                .build();
                        DiscountPolicy discountPolicy;
                        switch (discount.getDiscountType()) {
                            case VISIBLE:
                                discountPolicy = VisibleDiscount.builder()
                                        .amountOfProductsForApplyDiscounts(amountOfProductsForApplyDiscounts)
                                        .build();
                                break;
                            case CONDITIONAL_PRODUCT:
                                discountPolicy = ConditionalProductDiscount.builder()
                                        .productsUnderThisDiscount(productsUnderThisDiscount)
                                        .productsApplyDiscounts(amountOfProductsForApplyDiscounts)
                                        .build();
                                break;
                            case CONDITIONAL_STORE:
                                discountPolicy = ConditionalStoreDiscount.builder()
                                        .minPrice(discountDto.getMinPrice())
                                        .build();
                                break;
                            default:
                                discountPolicy = null;
                                break;
                        }
                        discount.setDiscountPolicy(discountPolicy);
                        return discount;
                    }).collect(Collectors.toList());
        }

        Map<Product, Integer> createDiscountMap(List<ProductDto> productDtos) {
            return Objects.isNull(productDtos) ? null : productDtos.stream()
                    .collect(Collectors.toMap(
                            productDto ->
                                    Product.builder()
                                            .cost(productDto.getCost())
                                            .name(productDto.getName())
                                            .amount(productDto.getAmount())
                                            .originalCost(productDto.getOriginalCost())
                                            .productSn(productDto.getProductSn())
                                            .storeId(productDto.getStoreId())
                                            .rank(productDto.getRank())
                                            .build()
                            , ProductDto::getAmount
                    ));
        }
    }

    @Component
    public static class DiscountToDiscountDto extends TypeMapConfigurer<Discount, DiscountDto> {
        @Override
        public void configure(TypeMap<Discount, DiscountDto> typeMap) {
            typeMap.addMappings(configurableMapExpression -> configurableMapExpression.skip(DiscountDto::setAmountOfProductsForApplyDiscounts));
            typeMap.addMappings(configurableMapExpression -> configurableMapExpression.skip(DiscountDto::setProductsUnderThisDiscount));
            typeMap.addMappings(configurableMapExpression -> configurableMapExpression.skip(DiscountDto::setCompositeOperator));
            typeMap.addMappings(configurableMapExpression -> configurableMapExpression.skip(DiscountDto::setComposedDiscounts));
            typeMap.addMappings(configurableMapExpression -> configurableMapExpression.skip(DiscountDto::setDescription));
            typeMap.addMappings(configurableMapExpression -> configurableMapExpression.skip(DiscountDto::setMinPrice));

            Converter<DiscountType, String> DiscountTypeToStringConverter =
                    ctx -> ctx.getSource() == null ? null : ctx.getSource().type;

            typeMap.addMappings(mapper -> mapper.using(DiscountTypeToStringConverter)
                    .map(Discount::getDiscountType, DiscountDto::setDiscountType));

            typeMap.setPostConverter(context -> {
                Date date = context.getSource().getEndTime().getTime();
                context.getDestination().setEndTime(date);
                context.getDestination().setDescription(context.getSource().getDescription());
                switch (context.getSource().getDiscountType()) {
                    case COMPOSE:
                        ComposedDiscount composedDiscount = (ComposedDiscount) context.getSource().getDiscountPolicy();
                        context.getDestination().setAmountOfProductsForApplyDiscounts(convertProductsToMap(composedDiscount.getAmountOfProductsForApplyDiscounts()));
                        context.getDestination().setProductsUnderThisDiscount(convertProductsToMap(composedDiscount.getProductsUnderThisDiscount()));
                        context.getDestination().setCompositeOperator(composedDiscount.getCompositeOperator().name);
                        context.getDestination().setComposedDiscounts(convertToDiscountDto(composedDiscount.getComposedDiscounts()));
                        break;
                    case VISIBLE:
                        VisibleDiscount visibleDiscount = (VisibleDiscount) context.getSource().getDiscountPolicy();
                        List<ProductDto> amountOfProductsForApplyDiscounts = convertProductsToMap(visibleDiscount.getAmountOfProductsForApplyDiscounts());
                        context.getDestination().setAmountOfProductsForApplyDiscounts(amountOfProductsForApplyDiscounts);
                        break;
                    case CONDITIONAL_PRODUCT:
                        ConditionalProductDiscount conditionalProductDiscount = (ConditionalProductDiscount) context.getSource().getDiscountPolicy();
                        context.getDestination().setAmountOfProductsForApplyDiscounts(convertProductsToMap(conditionalProductDiscount.getProductsApplyDiscounts()));
                        context.getDestination().setProductsUnderThisDiscount(convertProductsToMap(conditionalProductDiscount.getProductsUnderThisDiscount()));
                        break;
                    case CONDITIONAL_STORE:
                        ConditionalStoreDiscount conditionalStoreDiscount = (ConditionalStoreDiscount) context.getSource().getDiscountPolicy();
                        context.getDestination().setMinPrice(conditionalStoreDiscount.getMinPrice());
                        break;
                    default:
                        break;
                }
                return context.getDestination();
            });
        }

        private List<DiscountDto> convertToDiscountDto(List<Discount> composedDiscounts) {
            return composedDiscounts.stream()
                    .map(discount -> {
                        DiscountDto discountDto = DiscountDto.builder()
                                .description(discount.getDescription())
                                .discountId(discount.getDiscountId())
                                .discountType(discount.getDiscountType().type)
                                .build();
                        Date date = discount.getEndTime().getTime();
                        discountDto.setEndTime(date);
                        switch (discount.getDiscountType()) {
                            case VISIBLE:
                                VisibleDiscount visibleDiscount = (VisibleDiscount) discount.getDiscountPolicy();
                                List<ProductDto> amountOfProductsForApplyDiscounts = convertProductsToMap(visibleDiscount.getAmountOfProductsForApplyDiscounts());
                                discountDto.setAmountOfProductsForApplyDiscounts(amountOfProductsForApplyDiscounts);
                                break;
                            case CONDITIONAL_PRODUCT:
                                ConditionalProductDiscount conditionalProductDiscount = (ConditionalProductDiscount) discount.getDiscountPolicy();
                                discountDto.setAmountOfProductsForApplyDiscounts(convertProductsToMap(conditionalProductDiscount.getProductsApplyDiscounts()));
                                discountDto.setProductsUnderThisDiscount(convertProductsToMap(conditionalProductDiscount.getProductsUnderThisDiscount()));
                                break;
                            case CONDITIONAL_STORE:
                                ConditionalStoreDiscount conditionalStoreDiscount = (ConditionalStoreDiscount)  discount.getDiscountPolicy();
                                discountDto.setMinPrice(conditionalStoreDiscount.getMinPrice());
                                break;
                            default:
                                break;
                        }
                        return discountDto;
                    }).collect(Collectors.toList());
        }

        private List<ProductDto> convertProductsToMap(Map<Product, Integer> amountOfProductsForApplyDiscounts) {
            return amountOfProductsForApplyDiscounts.entrySet().stream()
                    .map(entry -> ProductDto.builder()
                    .storeId(entry.getKey().getStoreId())
                            .originalCost(entry.getKey().getOriginalCost())
                            .productSn(entry.getKey().getProductSn())
                            .rank(entry.getKey().getRank())
                            .cost(entry.getKey().getCost())
                            .amount(entry.getValue())
                            .name(entry.getKey().getName())
                            .build())
                    .collect(Collectors.toList());
        }
    }

}

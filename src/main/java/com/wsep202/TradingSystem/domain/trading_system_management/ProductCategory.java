package com.wsep202.TradingSystem.domain.trading_system_management;

import com.wsep202.TradingSystem.exception.CategoryDontExistException;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;

public enum ProductCategory {

    MOTORS("motors"),
    BOOKS_MOVIES_MUSIC("books, movies and music"),
    ELECTRONICS ("electronics"),
    COLLECTIBLES_ART("collectibles art"),
    HOME_GARDEN("home garden"),
    SPORTING_GOODS("sporing goods"),
    TOYS_HOBBIES("toys hobbies"),
    BUSINESS_INDUSTRIAL("business industrial"),
    HEALTH("health");

    public final String category;

    ProductCategory(String category) {
        this.category = category;
    }

    public static ProductCategory getProductCategory(String category) {
        return Arrays.stream(ProductCategory.values())
                .filter(productCategory -> productCategory.category.equals(category))
                .findFirst().orElseThrow(() -> new CategoryDontExistException(category));
    }
}

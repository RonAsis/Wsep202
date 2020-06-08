package com.wsep202.TradingSystem.domain.trading_system_management;

import com.wsep202.TradingSystem.domain.exception.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

    /**
     * converts between category to ProductCategory enum if category exist.
     * @param category - the category that needs to be converted.
     * @return - the converted ProductCategory enum or CategoryDoestExistException if category doesn't exist.
     */
    public static ProductCategory getProductCategory(String category) {
        return Arrays.stream(ProductCategory.values())
                .filter(productCategory -> productCategory.category.equals(category)).findFirst()
                .orElseThrow(() -> new CategoryDoesntExistException(category));
    }

    public static List<String> getCategories(){
        return Arrays.stream(ProductCategory.values())
                .map(productCategory -> productCategory.category)
                .collect(Collectors.toList());
    }
}

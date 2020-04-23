package com.wsep202.TradingSystem.web.controllers;

import com.wsep202.TradingSystem.dto.*;
import com.wsep202.TradingSystem.service.user_service.GuestService;
import com.wsep202.TradingSystem.web.controllers.api.PublicApiPaths;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class GuestController {

    private final GuestService guestService;
    /**
     * register user to the system
     * @param userName user to register - unique
     */
    @MessageMapping("/register-user")
    @SendTo(PublicApiPaths.CLIENT_DESTINATIONS_PREFIXED + "/register-user")
    public boolean registerUser(String userName,
                                String password,
                                String firstName,
                                String lastName){
        return guestService.registerUser(userName, password, firstName, lastName);
    }

    /**
     * login user to the system
     */
    @MessageMapping("/login")
    @SendTo(PublicApiPaths.CLIENT_DESTINATIONS_PREFIXED + "/login")
    public boolean login(String userName,
                         String password){
        return guestService.login(userName, password);
    }

    /**
     * see store information
     * @param storeId
     */
    @MessageMapping("/view-store-info")
    @SendTo(PublicApiPaths.CLIENT_DESTINATIONS_PREFIXED + "/view-store-info")
    public StoreDto viewStoreInfo(int storeId){
        return guestService.viewStoreInfo(storeId);
    }

    /**
     * view product in store with store id info.
     * @param storeId belongs to the product to view
     * @param productId - product to see
     * @return
     */
    @MessageMapping("/view-product")
    @SendTo(PublicApiPaths.CLIENT_DESTINATIONS_PREFIXED + "/view-product")
    public ProductDto viewProduct(int storeId, int productId){
        return guestService.viewProduct(storeId, productId);
    }

    /**
     * search product by productName
     * @param productName - criteria for search
     */
    @MessageMapping("/search-product-by-name")
    @SendTo(PublicApiPaths.CLIENT_DESTINATIONS_PREFIXED + "/search-product-by-name")
    public List<ProductDto> searchProductByName(String productName){
        return guestService.searchProductByName(productName);
    }

    /**
     * search product by category
     * @param category criteria for search
     */
    @MessageMapping("/search-product-by-category")
    @SendTo(PublicApiPaths.CLIENT_DESTINATIONS_PREFIXED + "/search-product-by-category")
    public List<ProductDto> searchProductByCategory(String category){
        return guestService.searchProductByCategory(category);
    }

    /**
     * search product by KeyWords
     * @param keyWords criteria for search
     */
    @MessageMapping("/search-product-by-keywords")
    @SendTo(PublicApiPaths.CLIENT_DESTINATIONS_PREFIXED + "/search-product-by-keywords")
    public List<ProductDto> searchProductByKeyWords(List<String> keyWords){
        return guestService.searchProductByKeyWords(keyWords);
    }

    /**
     * filter products by range price
     * @param products to filter
     * @param min low threshold
     * @param max threshold
     */
    @MessageMapping("/filter-by-range-price")
    @SendTo(PublicApiPaths.CLIENT_DESTINATIONS_PREFIXED + "/filter-by-range-price")
    public List<ProductDto> filterByRangePrice(List<ProductDto> products, double min, double max){
        return guestService.filterByRangePrice(products, min, max);
    }

    /**
     * filter products by product rank
     * @param products to filter
     * @param rank filter by rank of product
     * @return
     */
    @MessageMapping("/filter-by-product-rank")
    @SendTo(PublicApiPaths.CLIENT_DESTINATIONS_PREFIXED + "/filter-by-product-rank")
    public List<ProductDto> filterByProductRank(List<ProductDto> products, int rank){
        return guestService.filterByProductRank(products, rank);
    }

    /**
     * filter products by  store rank
     * @param products to filter
     * @param rank filter by rank of store
     */
    @MessageMapping("/filter-by-store-rank")
    @SendTo(PublicApiPaths.CLIENT_DESTINATIONS_PREFIXED + "/filter-by-store-rank")
    public List<ProductDto> filterByStoreRank(List<ProductDto> products, int rank){
        return guestService.filterByStoreRank(products, rank);
    }

    /**
     * filter products by category
     * @param products to filter
     * @param category filter criteria
     */
    @MessageMapping("/filter-by-store-category")
    @SendTo(PublicApiPaths.CLIENT_DESTINATIONS_PREFIXED + "/filter-by-store-category")
    public List<ProductDto> filterByStoreCategory(List<ProductDto> products, String category){
        return guestService.filterByStoreCategory(products, category);
    }

    /**
     * purchase shopping cart
     * @param shoppingCart includes the bags of each store the user selected
     * @param paymentDetails    - charging info of the user
     * @param billingAddressDto - the destination to deliver the purchases
     */
    @MessageMapping("/purchase-shopping-cart-guest")
    @SendTo(PublicApiPaths.CLIENT_DESTINATIONS_PREFIXED + "/purchase-shopping-cart-guest")
    public List<ReceiptDto> purchaseShoppingCartGuest(ShoppingCartDto shoppingCart, PaymentDetailsDto paymentDetails, BillingAddressDto billingAddressDto){
        return guestService.purchaseShoppingCartGuest(shoppingCart, paymentDetails, billingAddressDto);
    }
}

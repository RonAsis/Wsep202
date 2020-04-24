package com.wsep202.TradingSystem.web.controllers;

import com.wsep202.TradingSystem.dto.*;
import com.wsep202.TradingSystem.service.user_service.GuestService;
import com.wsep202.TradingSystem.web.controllers.api.PublicApiPaths;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//@Controller
@Slf4j
@RestController
@RequestMapping(PublicApiPaths.GUEST_PATH)
@Api(value = "API to guest", produces = "application/json")
@RequiredArgsConstructor
public class GuestController {

    private final GuestService guestService;
    /**
     * register user to the system
     * @param userName user to register - unique
     */
    @ApiOperation(value = "register user")
    @PostMapping("register-user/{userName}/{password}/{firstName}/{lastName}")
    public boolean registerUser(@PathVariable String userName,
                                @PathVariable String password,
                                @PathVariable String firstName,
                                @PathVariable String lastName){
        return guestService.registerUser(userName, password, firstName, lastName);
    }

    /**
     * login user to the system
     */
    @ApiOperation(value = "login")
    @PutMapping("login/{userName}/{password}")
    public boolean login(@PathVariable String userName,
                         @PathVariable String password){
        return guestService.login(userName, password);
    }

    /**
     * see store information
     * @param storeId
     */
    @ApiOperation(value = "login")
    @GetMapping("view-store-info/{storeId}")
    public StoreDto viewStoreInfo(@PathVariable int storeId){
        return guestService.viewStoreInfo(storeId);
    }

    /**
     * view product in store with store id info.
     * @param storeId belongs to the product to view
     * @param productId - product to see
     * @return
     */
    @ApiOperation(value = "view product")
    @GetMapping("view-product/{storeId}/{productId}")
    public ProductDto viewProduct(@PathVariable int storeId,
                                  @PathVariable int productId){
        return guestService.viewProduct(storeId, productId);
    }

    /**
     * search product by productName
     * @param productName - criteria for search
     */
    @ApiOperation(value = "search product by name")
    @GetMapping("search-product-by-name/{productName}")
    public List<ProductDto> searchProductByName(@PathVariable String productName){
        return guestService.searchProductByName(productName);
    }

    /**
     * search product by category
     * @param category criteria for search
     */
    @ApiOperation(value = "search product by category")
    @GetMapping("search-product-by-category/{category}")
    public List<ProductDto> searchProductByCategory(@PathVariable String category){
        return guestService.searchProductByCategory(category);
    }

    /**
     * search product by KeyWords
     * @param keyWords criteria for search
     */
    @ApiOperation(value = "search product by keywords")
    @GetMapping("search-product-by-keywords/{keywords}")
    public List<ProductDto> searchProductByKeyWords(@RequestBody List<String> keyWords){
        return guestService.searchProductByKeyWords(keyWords);
    }

    /**
     * filter products by range price
     * @param products to filter
     * @param min low threshold
     * @param max threshold
     */
    @ApiOperation(value = "filter product by price")
    @GetMapping("filter-by-range-price/{min}/{max}")
    public List<ProductDto> filterByRangePrice(@RequestBody List<ProductDto> products,
                                               @PathVariable  double min,
                                               @PathVariable  double max){
        return guestService.filterByRangePrice(products, min, max);
    }

    /**
     * filter products by product rank
     * @param products to filter
     * @param rank filter by rank of product
     * @return
     */
    @ApiOperation(value = "filter product by product rank")
    @GetMapping("filter-by-product-rank/{rank}")
    public List<ProductDto> filterByProductRank(@RequestBody List<ProductDto> products,
                                                @PathVariable int rank){
        return guestService.filterByProductRank(products, rank);
    }

    /**
     * filter products by  store rank
     * @param products to filter
     * @param rank filter by rank of store
     */
    @ApiOperation(value = "filter product by store rank")
    @GetMapping("filter-by-store-rank/{rank}")
    public List<ProductDto> filterByStoreRank(@RequestBody List<ProductDto> products,
                                              @PathVariable int rank){
        return guestService.filterByStoreRank(products, rank);
    }

    /**
     * filter products by category
     * @param products to filter
     * @param category filter criteria
     */
    @ApiOperation(value = "filter product by category")
    @GetMapping("filter-by-store-rank/{category}")
    public List<ProductDto> filterByStoreCategory(@RequestBody List<ProductDto> products,
                                                  @PathVariable String category){
        return guestService.filterByStoreCategory(products, category);
    }

    /**
     * purchase shopping cart
     * @param shoppingCartDto includes the bags of each store the user selected
     * @param paymentDetailsDto    - charging info of the user
     * @param billingAddressDto - the destination to deliver the purchases
     */
    @ApiOperation(value = "purchase shopping cart guest")
    @PostMapping("purchase-shopping-cart-guest")
    public List<ReceiptDto> purchaseShoppingCartGuest(@RequestBody ShoppingCartDto shoppingCartDto,
                                                      @RequestBody PaymentDetailsDto paymentDetailsDto,
                                                      @RequestBody  BillingAddressDto billingAddressDto){
        return guestService.purchaseShoppingCartGuest(shoppingCartDto, paymentDetailsDto, billingAddressDto);
    }
}

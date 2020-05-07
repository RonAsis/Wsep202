package com.wsep202.TradingSystem.web.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.wsep202.TradingSystem.dto.*;
import com.wsep202.TradingSystem.service.user_service.GuestService;
import com.wsep202.TradingSystem.service.user_service.NotificationService;
import com.wsep202.TradingSystem.web.controllers.api.PublicApiPaths;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javafx.util.Pair;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(PublicApiPaths.GUEST_PATH)
@CrossOrigin(origins = "http://localhost:4200")
@Api(value = "API to guest", produces = "application/json")
@RequiredArgsConstructor
public class GuestController {

    private final GuestService guestService;
    private final NotificationService notificationService;

    /**
     * register user to the system
     * @param userName user to register - unique
     */
    @ApiOperation(value = "register user")
    @PostMapping("register-user/{userName}/{password}/{firstName}/{lastName}")
    public boolean registerUser(@PathVariable String userName,
                                @PathVariable String password,
                                @PathVariable String firstName,
                                @PathVariable String lastName,
                                @RequestParam(value = "imageFile", required=false) MultipartFile image){
        return guestService.registerUser(userName, password, firstName, lastName, image);
    }

    /**
     * login user to the system
     */
    @ApiOperation(value = "login")
    @PutMapping("login/{userName}/{password}")
    public Pair<UUID, Boolean> login(@PathVariable String userName,
                                    @PathVariable String password){
        Pair<UUID, Boolean> loginParam = guestService.login(userName, password);
        return loginParam;
    }

    /**
     * purchase shopping cart
     */
    @ApiOperation(value = "purchase shopping cart guest")
    @PostMapping("purchase-shopping-cart-guest")
    @ResponseBody
    public List<ReceiptDto> purchaseShoppingCartGuest(@RequestBody String purchaseDto){
        return guestService.purchaseShoppingCartGuest(purchaseDto);
    }

    /**
     * get stores
     */
    @ApiOperation(value = "get stores")
    @GetMapping("get-stores")
    public List<StoreDto> getStores() {
        return guestService.getStores();
    }

    /**
     * get products
     */
    @ApiOperation(value = "get products")
    @GetMapping("get-products")
    public List<ProductDto> getProducts() {
        return guestService.getProducts();
    }

    /**
     * get stores
     */
    @ApiOperation(value = "get categories")
    @GetMapping("get-categories")
    public List<String> getCategories() {
        return guestService.getCategories();
    }

    /**
     * get Total Price Of ShoppingCart
     */
    @ApiOperation(value = " get Total Price Of ShoppingCart")
    @PostMapping(value = "get-total-price-of-shopping-cart" ,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Pair<Double, Double> getTotalPriceOfShoppingCart(@RequestBody(required = false) String shoppingCart){
        return guestService.getTotalPriceOfShoppingCart(shoppingCart);
    }
}


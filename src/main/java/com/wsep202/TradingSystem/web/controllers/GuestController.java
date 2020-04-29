package com.wsep202.TradingSystem.web.controllers;

import com.wsep202.TradingSystem.dto.*;
import com.wsep202.TradingSystem.service.user_service.GuestService;
import com.wsep202.TradingSystem.web.controllers.api.PublicApiPaths;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javafx.util.Pair;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(PublicApiPaths.GUEST_PATH)
@CrossOrigin(origins = "http://localhost:4200")
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
    public Pair<UUID, Boolean> login(@PathVariable String userName,
                                    @PathVariable String password){
        return guestService.login(userName, password);
    }

    /**
     * see store information
     * @param storeId
     */
    @ApiOperation(value = "view-store-info")
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

    /**
     * add manager
     */
    @ApiOperation(value = "get stores")
    @GetMapping("get-stores/")
    public List<StoreDto> getManageStores() {
        return guestService.getStores();
    }
}

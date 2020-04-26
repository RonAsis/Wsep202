package com.wsep202.TradingSystem.web.controllers;

import com.wsep202.TradingSystem.domain.trading_system_management.Store;
import com.wsep202.TradingSystem.dto.*;
import com.wsep202.TradingSystem.service.user_service.BuyerRegisteredService;
import com.wsep202.TradingSystem.web.controllers.api.PublicApiPaths;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(PublicApiPaths.BUYER_REG_PATH)
@Api(value = "API to buyer registered", produces = "application/json")
@RequiredArgsConstructor
public class BuyerRegisteredController {

    private final BuyerRegisteredService buyerRegisteredService;

    /**
     * logout username from the system
     * @param userName user to logout
     */
    @ApiOperation(value = "logout")
    @PutMapping("logout/{userName}")
    public boolean logout(@PathVariable String userName,
                          @RequestBody UUID uuid) {
        return buyerRegisteredService.logout(userName, uuid);
    }

    /**
     * open store
     *
     * @param usernameOwner  the opener and first owner of store
     * @param purchasePolicy each store has policy for purchase on users and products
     * @param discountPolicy each store has policy for discount on products
     * @param storeName      - store name
     */
    @ApiOperation(value = "open store")
    @PostMapping("open-store/{usernameOwner}/{storeName}")
    public StoreDto openStore(@PathVariable String usernameOwner,
                           @RequestBody PurchasePolicyDto purchasePolicy,
                           @RequestBody DiscountPolicyDto discountPolicy,
                           @PathVariable String storeName,
                           @RequestBody UUID uuid) {
        return buyerRegisteredService.openStore(usernameOwner, purchasePolicy, discountPolicy, storeName, uuid);
    }

    /**
     * View buyer purchase history
     *
     * @param userName of the user the history belongs to
     */
    @ApiOperation(value = "view purchase history")
    @GetMapping("view-purchase-history/{userName}")
    public List<ReceiptDto> viewPurchaseHistory(@PathVariable String userName,
                                                @RequestBody UUID uuid) {
        return buyerRegisteredService.viewPurchaseHistory(userName, uuid);
    }

    /**
     * save product in shopping bag
     *
     * @param userName  the username of the user which save in his bag
     * @param storeId   store belobgs to the bag
     * @param productSn the identifier of the product
     * @param amount    quantity to save
     */
    @ApiOperation(value = "save product in shopping bag")
    @PostMapping("save-product-in-shopping-bag/{userName}/{storeId}/{productSn}/{amount}")
    public boolean saveProductInShoppingBag(@PathVariable String userName,
                                            @PathVariable int storeId,
                                            @PathVariable int productSn,
                                            @PathVariable int amount,
                                            @RequestBody UUID uuid) {
        return buyerRegisteredService.saveProductInShoppingBag(userName, storeId, productSn, amount, uuid);
    }

    /**
     * view product in shopping bag
     *
     * @param userName the user the bag belongs to
     */
    @ApiOperation(value = "watch shopping cart")
    @GetMapping("watch-shopping-cart/{username}")
    public ShoppingCartDto viewProductsInShoppingCart(@PathVariable String userName,
                                                      @RequestBody UUID uuid) {
        return buyerRegisteredService.watchShoppingCart(userName, uuid);
    }

    /**
     * remove product in shopping bag (edit)
     *
     * @param userName  the user which edit
     * @param storeId   the store belongs to the product
     * @param productSn identifier of product
     */
    @ApiOperation(value = "remove product in shopping bag")
    @PostMapping("remove-product-in-shopping-bag/{userName}/{storeId}/{productSn}")
    public boolean removeProductInShoppingBag(@PathVariable String userName,
                                              @PathVariable int storeId,
                                              @PathVariable int productSn,
                                              @RequestBody UUID uuid) {
        return buyerRegisteredService.removeProductInShoppingBag(userName, storeId, productSn, uuid);
    }

    /**
     * purchase shopping cart
     *
     * @param userName       user that purchase
     * @param paymentDetails info to charge of the user
     * @param billingAddress the destination of the delivery
     */
    @ApiOperation(value = "purchase shopping cart buyer")
    @PutMapping("purchase-shopping-cart-buyer/{userName}")
    @MessageMapping("/purchase-shopping-cart-buyer")
    public List<ReceiptDto> purchaseShoppingCartBuyer(@PathVariable String userName,
                                                      @RequestBody PaymentDetailsDto paymentDetails,
                                                      @RequestBody BillingAddressDto billingAddress,
                                                      @RequestBody UUID uuid) {
        return buyerRegisteredService.purchaseShoppingCartBuyer(userName, paymentDetails, billingAddress, uuid);
    }

}

package com.wsep202.TradingSystem.web.controllers;

import com.wsep202.TradingSystem.dto.*;
import com.wsep202.TradingSystem.service.user_service.BuyerRegisteredService;
import com.wsep202.TradingSystem.web.controllers.api.PublicApiPaths;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class BuyerRegisteredController {

    private final BuyerRegisteredService buyerRegisteredService;

    /**
     * logout username from the system
     * @param userName user to logout
     */
    @MessageMapping("/logout")
    @SendTo(PublicApiPaths.CLIENT_DESTINATIONS_PREFIXED + "/logout")
    public boolean logout(String userName){
        return buyerRegisteredService.logout(userName);
    }

    /**
     * open store
     * @param usernameOwner the opener and first owner of store
     * @param purchasePolicy each store has policy for purchase on users and products
     * @param discountPolicy each store has policy for discount on products
     * @param storeName - store name
     */
    @MessageMapping("/open-store")
    @SendTo(PublicApiPaths.CLIENT_DESTINATIONS_PREFIXED + "/open-store")
    public boolean openStore(String usernameOwner, PurchasePolicyDto purchasePolicy, DiscountPolicyDto discountPolicy, String storeName){
        return buyerRegisteredService.openStore(usernameOwner, purchasePolicy, discountPolicy, storeName);
    }

    /**
     * watching on personal shopping cart of user
     * @param username identify user
     */
    @MessageMapping("/watch-shopping-cart")
    @SendTo(PublicApiPaths.CLIENT_DESTINATIONS_PREFIXED + "/watch-shopping-cart")
    public Map<ProductDto,Integer> watchShoppingCart(String username){
        return buyerRegisteredService.watchShoppingCart(username);
    }

    /**
     * View buyer purchase history
     * @param userName of the user the history belongs to
     */
    @MessageMapping("/view-purchase-history")
    @SendTo(PublicApiPaths.CLIENT_DESTINATIONS_PREFIXED + "/view-purchase-history")
    public List<ReceiptDto> viewPurchaseHistory(String userName){
        return buyerRegisteredService.viewPurchaseHistory(userName);
    }

    /**
     *      * save product in shopping bag
     * @param userName the username of the user which save in his bag
     * @param storeId store belobgs to the bag
     * @param productSn the identifier of the product
     * @param amount quantity to save
     */
    @MessageMapping("/save-product-in-shopping-bag")
    @SendTo(PublicApiPaths.CLIENT_DESTINATIONS_PREFIXED + "/save-product-in-shopping-bag")
    public boolean saveProductInShoppingBag(String userName, int storeId, int productSn, int amount){
        return buyerRegisteredService.saveProductInShoppingBag(userName, storeId, productSn, amount);
    }

    /**
     *      * view product in shopping bag
     * @param userName the user the bag belongs to
     */
    @MessageMapping("/view-product-in-shopping-bag")
    @SendTo(PublicApiPaths.CLIENT_DESTINATIONS_PREFIXED + "/view-product-in-shopping-bag")
    public ShoppingCartDto viewProductsInShoppingCart(String userName){
        return buyerRegisteredService.viewProductsInShoppingCart(userName);
    }

    /**
     * remove product in shopping bag (edit)
     * @param userName the user which edit
     * @param storeId the store belongs to the product
     * @param productSn identifier of product
     */
    @MessageMapping("/remove-product-in-shopping-bag")
    @SendTo(PublicApiPaths.CLIENT_DESTINATIONS_PREFIXED + "/remove-product-in-shopping-bag")
    public boolean removeProductInShoppingBag(String userName, int storeId, int productSn){
        return buyerRegisteredService.removeProductInShoppingBag(userName, storeId, productSn);
    }

    /**
     * purchase shopping cart
     * @param userName user that purchase
     * @param paymentDetails info to charge of the user
     * @param billingAddress the destination of the delivery
     */
    @MessageMapping("/purchase-shopping-cart-buyer")
    @SendTo(PublicApiPaths.CLIENT_DESTINATIONS_PREFIXED + "/purchase-shopping-cart-buyer")
    public List<ReceiptDto> purchaseShoppingCartBuyer(String userName, PaymentDetailsDto paymentDetails, BillingAddressDto billingAddress){
        return buyerRegisteredService.purchaseShoppingCartBuyer(userName, paymentDetails, billingAddress);
    }

}

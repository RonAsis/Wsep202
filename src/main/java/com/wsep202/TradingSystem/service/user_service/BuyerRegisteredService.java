package com.wsep202.TradingSystem.service.user_service;

import com.wsep202.TradingSystem.domain.trading_system_management.*;
import com.wsep202.TradingSystem.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class BuyerRegisteredService {

    private final TradingSystemFacade tradingSystemFacade;

    /**
     * logout username from the system
     * @param userName user to logout
     * @param uuid
     * @return
     */
    public boolean logout(String userName,
                          UUID uuid){
        return tradingSystemFacade.logout(userName, uuid);
    }

    /**
     * open store
     * @param usernameOwner the opener and first owner of store
     * @param storeName
     * @param uuid
     * @return
     */
    public StoreDto openStore(String usernameOwner,
                              String storeName,
                              String description,
                              UUID uuid){
        return tradingSystemFacade.openStore(usernameOwner,  storeName, description, uuid);
    }

    /**
     * View buyer purchase history
     * @param userName of the user the history belongs to
     * @param uuid
     * @return
     */
    public List<ReceiptDto> viewPurchaseHistory(String userName, UUID uuid){
        return tradingSystemFacade.viewPurchaseHistory(userName, uuid);
    }

    /**
     *      * save product in shopping bag
     * @param userName the username of the user which save in his bag
     * @param storeId store belobgs to the bag
     * @param productSn the identifier of the product
     * @param amount quantity to save
     * @param uuid
     * @return
     */
    public boolean saveProductInShoppingBag(String userName,
                                            int storeId,
                                            int productSn,
                                            int amount,
                                            UUID uuid){
        return tradingSystemFacade.saveProductInShoppingBag(userName, storeId, productSn, amount, uuid);
    }

    /**
     * view product in shopping bag
     * @param userName the user the bag belongs to
     * @param uuid
     * @return
     */
    public ShoppingCartDto watchShoppingCart(String userName, UUID uuid){
        return tradingSystemFacade.watchShoppingCart(userName, uuid);
    }

    /**
     *      * remove product in shopping bag (edit)
     * @param userName the user which edit
     * @param storeId the store belongs to the product
     * @param productSn identifier of product
     * @param uuid
     * @return
     */
    public boolean removeProductInShoppingBag(String userName,
                                              int storeId,
                                              int productSn,
                                              UUID uuid){
        return tradingSystemFacade.removeProductInShoppingBag(userName, storeId, productSn, uuid);
    }

    /**
     * purchase shopping cart
     * @param userName user that purchase
     * @param paymentDetails info to charge of the user
     * @param billingAddress the destination of the delivery
     * @param uuid
     * @return
     */
    public List<ReceiptDto> purchaseShoppingCartBuyer(String userName,
                                                      PaymentDetailsDto paymentDetails,
                                                      BillingAddressDto billingAddress,
                                                      UUID uuid){
        return tradingSystemFacade.purchaseShoppingCart(userName, paymentDetails, billingAddress, uuid);
    }

    public boolean addProductToShoppingCart(String username, int amount, ProductDto productDto, UUID uuid) {
        return tradingSystemFacade.addProductToShoppingCart(username, amount, productDto, uuid);
    }

    public ShoppingCartDto getShoppingCart(String username, UUID uuid) {
        return tradingSystemFacade.getShoppingCart(username, uuid);
    }
}

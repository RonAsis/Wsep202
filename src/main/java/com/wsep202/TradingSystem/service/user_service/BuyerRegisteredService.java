package com.wsep202.TradingSystem.service.user_service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wsep202.TradingSystem.domain.trading_system_management.*;
import com.wsep202.TradingSystem.dto.*;
import javafx.util.Pair;
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

    private final ObjectMapper objectMapper;

    /**
     * UC 3.1 - logging out from the system.
     *
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
     * UC 3.2 - opening a store.
     *
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
     * UC 3.7 - viewing personal purchase history.
     *
     * View buyer purchase history
     * @param userName of the user the history belongs to
     * @param uuid
     * @return
     */
    public List<ReceiptDto> viewPurchaseHistory(String userName, UUID uuid){
        return tradingSystemFacade.viewPurchaseHistory(userName, uuid);
    }

    /**
     * UC 2.6 (inherited from guest) - saving a product in a shopping bag.
     *
     * save product in shopping bag
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
     * UC 2.7.1 (inherited from guest) - watching the shopping cart.
     *
     * view product in shopping bag
     * @param userName the user the bag belongs to
     * @param uuid
     * @return
     */
    public ShoppingCartDto watchShoppingCart(String userName, UUID uuid){
        return tradingSystemFacade.watchShoppingCart(userName, uuid);
    }

    /**
     * UC 2.7.2 (inherited from guest) - removing a product from shopping bag.
     *
     * remove product in shopping bag (edit)
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
     * UC 2.8 (inherited from guest) - purchasing shopping cart.
     *
     * purchase shopping cart
     * @param userName user that purchase
     * @param uuid
     * @return
     */
    public List<ReceiptDto> purchaseShoppingCartBuyer(String userName,
                                                      PurchaseRegisterBuyerDto purchaseRegisterBuyerDto,
                                                      UUID uuid){
        return tradingSystemFacade.purchaseShoppingCart(userName,
                purchaseRegisterBuyerDto.getPaymentDetailsDto(), purchaseRegisterBuyerDto.getBillingAddressDto(),  uuid);
    }

    /**
     * UC 2.6 (inherited from guest) - saving a product in a shopping bag.
     *
     * @param username - the user who wants to add the product to his shopping cart
     * @param amount - the new amount of the product in the shopping bag
     * @param productDto - the product which needs to be added to the shopping cart
     * @param uuid - the uuid of the user
     * @return
     */
    public boolean addProductToShoppingCart(String username, int amount, ProductDto productDto, UUID uuid) {
        return tradingSystemFacade.addProductToShoppingCart(username, amount, productDto, uuid);
    }

    /**
     * UC 2.7.2 (inherited from guest) - editing a product in a shopping bag.
     *
     * @param username - the user who's shopping bag needs to be edited
     * @param storeId - the store to which the product belongs to
     * @param amount - the new amount of the product in the shopping bag
     * @param productSn - the product which amount is to be changed
     * @param uuid - the uuid of the user
     * @return true if the change happened successfully
     */
    public boolean changeProductAmountInShoppingBag(String username, int storeId, int amount, int productSn, UUID uuid) {
        return tradingSystemFacade.changeProductAmountInShoppingBag(username, storeId, amount, productSn, uuid);
    }

    public List<ProductShoppingCartDto> getShoppingCart(String username, UUID uuid) {
        return tradingSystemFacade.getShoppingCart(username, uuid);
    }

    public Pair<Double, Double> getTotalPriceOfShoppingCart(String username, UUID uuid) {
        return tradingSystemFacade.getTotalPriceOfShoppingCart(username, uuid);
    }

}

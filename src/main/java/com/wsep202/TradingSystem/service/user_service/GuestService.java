package com.wsep202.TradingSystem.service.user_service;


import com.fasterxml.jackson.databind.JsonNode;
import com.wsep202.TradingSystem.domain.trading_system_management.*;
import com.wsep202.TradingSystem.dto.*;
import javafx.util.Pair;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class GuestService {

    private final TradingSystemFacade tradingSystemFacade;

    private final ObjectMapper objectMapper;
    /**
     * register user to the system
     * @param username user to register - unique
     * @param password
     * @param firstName
     * @param lastName
     * @param image
     * @return
     */
    public boolean registerUser(String username,
                                String password,
                                String firstName,
                                String lastName,
                                MultipartFile image){
        return tradingSystemFacade.registerUser(username, password, firstName, lastName, image);
    }

    /**
     * login user to the system
     * @param username
     * @param password
     * @return
     */
    public Pair<UUID, Boolean> login(String username,
                                     String password){
        return tradingSystemFacade.login(username, password);
    }

    /**
     *purchase shopping cart
     * @return
     * @param purchaseJsonNode
     */
    public List<ReceiptDto> purchaseShoppingCartGuest(String purchaseJsonNode){
        PurchaseDto purchaseDto = createPurchaseDto(purchaseJsonNode);
        return tradingSystemFacade.purchaseShoppingCart(purchaseDto.getShoppingCartDto(),
                purchaseDto.getPaymentDetailsDto(), purchaseDto.getBillingAddressDto());
    }

    public List<StoreDto> getStores() {
        return tradingSystemFacade.getStores();
    }

    public List<ProductDto> getProducts() {
        return tradingSystemFacade.getProducts();
    }

    public List<String> getCategories() {
        return tradingSystemFacade.getCategories();
    }

    public Pair<Double, Double> getTotalPriceOfShoppingCart(String shoppingCart) {
       return tradingSystemFacade.getTotalPriceOfShoppingCart(createShoppingCartDto(shoppingCart));
    }

    private ShoppingCartDto createShoppingCartDto(String shoppingCart){
        try {
            return objectMapper.readValue(shoppingCart, ShoppingCartDto.class);
        } catch (IOException e) {
            log.error("is not json string", e);
            return null;
        }
    }

    private PurchaseDto createPurchaseDto(String purchaseDto){
        try {
            return objectMapper.readValue(purchaseDto, PurchaseDto.class);
        } catch (IOException e) {
            log.error("is not json string", e);
            return null;
        }
    }
}

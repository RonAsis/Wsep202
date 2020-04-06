package com.wsep202.TradingSystem.service.user_service;

import com.wsep202.TradingSystem.domain.trading_system_management.*;
import com.wsep202.TradingSystem.service.user_service.dto.PurchaseHistoryDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BuyerRegisteredService {

    private final TradingSystemFacade tradingSystemFacade;

    /**
     * logout
     */
    public boolean logout(String userName){
        return tradingSystemFacade.logout(userName);
    }

    /**
     * open store
     */
    public boolean openStore(String usernameOwner, PurchasePolicy purchasePolicy, DiscountPolicy discountPolicy, String discountType,
                             String purchaseType, String storeName){
        return  tradingSystemFacade.openStore(usernameOwner, purchasePolicy, discountPolicy, discountType, purchaseType, storeName);
    }

    /**
     * View buyer purchase history
     */
    public List<Receipt> viewPurchaseHistory(String userName){
        return tradingSystemFacade.viewPurchaseHistory(userName);
    }


    /**
     * save product in shopping bag
     */
    public boolean saveProductInShoppingBag(String userName, int storeId, int productSn, int amount){
        return tradingSystemFacade.saveProductInShoppingBag(userName, storeId, productSn, amount);
    }

    /**
     * view product in shopping bag
     */
    public ShoppingCart viewProductsInShoppingCart(String userName){
        return tradingSystemFacade.viewProductsInShoppingCart(userName);
    }

    /**
     * remove product in shopping bag (edit)
     */
    public boolean removeProductInShoppingBag(String userName, int storeId, int productSn){
        return tradingSystemFacade.removeProductInShoppingBag(userName, storeId, productSn);
    }

    /**
     * purchase shopping cart
     */
    public Receipt purchaseShoppingCart(String userName){
        return tradingSystemFacade.purchaseShoppingCart(userName);
    }

}

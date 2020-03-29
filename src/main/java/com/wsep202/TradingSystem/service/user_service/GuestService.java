package com.wsep202.TradingSystem.service.user_service;


import com.wsep202.TradingSystem.domain.trading_system_management.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class GuestService {

    private final TradingSystemFacade tradingSystemFacade;

    /**
     * register
     */
    public boolean registerUser(String userName,
                                String password,
                                String firstName,
                                String lastName){
        return tradingSystemFacade.registerUser(userName, password, firstName, lastName);
    }

    /**
     * login
     */
    public boolean login(String userName,
                         String password){
        return tradingSystemFacade.login(userName, password);
    }

    /**
     * view store data
     */
    public Store viewStoreInfo(String storeId){
        return tradingSystemFacade.viewStoreInfo(storeId);
    }

    /**
     * view product in store
     */
    public Product viewProduct(String storeId, String productId){
        return tradingSystemFacade.viewProduct(storeId, productId);
    }

    /**
     * search product by productName
     */
    public List<Product> searchProductByName(String productName){
        return tradingSystemFacade.searchProductByName(productName);
    }

    /**
     * search product by category
     */
    public List<Product> searchProductByCategory(String category){
        return tradingSystemFacade.searchProductByCategory(category);
    }

    /**
     * search product by KeyWords
     */
    public List<Product> searchProductByKeyWords(List<String> keyWords){
        return tradingSystemFacade.searchProductByKeyWords(keyWords);
    }

    /**
     * filter products by range price
     */
    public List<Product> filterByRangePrice(double min, double max){
        return tradingSystemFacade.filterByRangePrice(min, max);
    }

    /**
     * filter products by product rank
     */
    public List<Product> filterByProductRank(int rank){
        return tradingSystemFacade.filterByProductRank(rank);
    }

    /**
     * filter products by  store rank
     */
    public List<Product> filterByStoreRank(int rank){
        return tradingSystemFacade.filterByStoreRank(rank);
    }

    /**
     * filter products by category
     */
    public List<Product> filterByStoreCategory(String category){
        return tradingSystemFacade.filterByStoreCategory(category);
    }

    /**
     * purchase shopping cart
     */
    public Receipt purchaseShoppingCart(ShoppingCart shoppingCart){
        return tradingSystemFacade.purchaseShoppingCart(shoppingCart);
    }
}

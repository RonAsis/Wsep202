package com.wsep202.TradingSystem.service.user_service;


import com.wsep202.TradingSystem.domain.trading_system_management.*;
import com.wsep202.TradingSystem.service.user_service.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotBlank;
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

    public void clearDS(){
        this.tradingSystemFacade.clearDS();
    }

    /**
     * login
     */
    public boolean login(String userName,
                         String password){
        return tradingSystemFacade.login(userName, password);
    }

    /**
     * uc 2.6 : save product in shopping bag
     * @param username the username that want to save ShoppingBag
     * @param storeId the storeId which the user visits in
     * @param productSn - the sn of the product the user wish to add
     * @param quantity - the amount of the product the user wish to add
     * @return true if succeed
     */
    public boolean saveProductInShoppingBag( String username, int storeId, int productSn, int quantity) {
        return tradingSystemFacade.saveProductInShoppingBag(username,  storeId,  productSn,  quantity);
    }



    /**
     * view store data
     */
    public StoreDto viewStoreInfo(int storeId){
        return tradingSystemFacade.viewStoreInfo(storeId);
    }

    /**
     * view product in store
     */
    public ProductDto viewProduct(int storeId, int productId){
        return tradingSystemFacade.viewProduct(storeId, productId);
    }

    /**
     * search product by productName
     */
    public List<ProductDto> searchProductByName(String productName){
        return tradingSystemFacade.searchProductByName(productName);
    }

    /**
     * search product by category
     */
    public List<ProductDto> searchProductByCategory(String category){
        return tradingSystemFacade.searchProductByCategory(category);
    }

    /**
     * search product by KeyWords
     */
    public List<ProductDto> searchProductByKeyWords(List<String> keyWords){
        return tradingSystemFacade.searchProductByKeyWords(keyWords);
    }

    /**
     * filter products by range price
     */
    public List<ProductDto> filterByRangePrice(List<ProductDto> products, double min, double max){
        return tradingSystemFacade.filterByRangePrice(products, min, max);
    }

    /**
     * filter products by product rank
     */
    public List<ProductDto> filterByProductRank(List<ProductDto> products, int rank){
        return tradingSystemFacade.filterByProductRank(products, rank);
    }

    /**
     * filter products by  store rank
     */
    public List<ProductDto> filterByStoreRank(List<ProductDto> products, int rank){
        return tradingSystemFacade.filterByStoreRank(products, rank);
    }

    /**
     * filter products by category
     */
    public List<ProductDto> filterByStoreCategory(List<ProductDto> products, String category){
        return tradingSystemFacade.filterByStoreCategory(products, category);
    }

    /**
     * purchase shopping cart
     */
    public ReceiptDto purchaseShoppingCart(ShoppingCartDto shoppingCart, PaymentDetailsDto paymentDetails, BillingAddressDto billingAddressDto){
        return tradingSystemFacade.purchaseShoppingCart(shoppingCart, paymentDetails, billingAddressDto);
    }
}

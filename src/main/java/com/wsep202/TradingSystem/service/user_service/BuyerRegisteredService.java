package com.wsep202.TradingSystem.service.user_service;

import com.wsep202.TradingSystem.domain.trading_system_management.*;
import com.wsep202.TradingSystem.service.user_service.dto.*;
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
    public boolean openStore(String usernameOwner, PurchasePolicyDto purchasePolicy, DiscountPolicyDto discountPolicy, String discountType,
                             String purchaseType, String storeName){
        return  tradingSystemFacade.openStore(usernameOwner, purchasePolicy, discountPolicy, discountType, purchaseType, storeName);
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
     * View buyer purchase history
     */
    public List<ReceiptDto> viewPurchaseHistory(String userName){
        return tradingSystemFacade.viewPurchaseHistory(userName);
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
     * save product in shopping bag
     */
    public boolean saveProductInShoppingBag(String userName, int storeId, int productSn, int amount){
        return tradingSystemFacade.saveProductInShoppingBag(userName, storeId, productSn, amount);
    }

    /**
     * view product in shopping bag
     */
    public ShoppingCartDto viewProductsInShoppingCart(String userName){
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
    public ReceiptDto purchaseShoppingCart(String userName, PaymentDetailsDto paymentDetails, BillingAddressDto billingAddress){
        return tradingSystemFacade.purchaseShoppingCart(userName, paymentDetails, billingAddress);
    }

    public void clearDS() {

    }
}

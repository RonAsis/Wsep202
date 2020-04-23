package com.wsep202.TradingSystem.service.user_service;

import com.wsep202.TradingSystem.domain.trading_system_management.*;
import com.wsep202.TradingSystem.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class BuyerRegisteredService {

    private final TradingSystemFacade tradingSystemFacade;

    /**
     * logout username from the system
     * @param userName user to logout
     * @return
     */
    public boolean logout(String userName){
        return tradingSystemFacade.logout(userName);
    }

    /**
     * open store
     * @param usernameOwner the opener and first owner of store
     * @param purchasePolicy each store has policy for purchase on users and products
     * @param discountPolicy each store has policy for discount on products
     * @param storeName
     * @return
     */
    public boolean openStore(String usernameOwner, PurchasePolicyDto purchasePolicy, DiscountPolicyDto discountPolicy, String storeName){
        return  tradingSystemFacade.openStore(usernameOwner, purchasePolicy, discountPolicy, storeName);
    }

    /**
     * see store information
     * @param storeId
     * @return
     */
    public StoreDto viewStoreInfo(int storeId){
        return tradingSystemFacade.viewStoreInfo(storeId);
    }

    /**
     * get product in certain store
     * @param storeId store to look at products at
     * @param productId the product we wish to see
     * @return
     */
    public ProductDto viewProduct(int storeId, int productId){
        return tradingSystemFacade.viewProduct(storeId, productId);
    }

    /**
     * watching on personal shopping cart of user
     * @param username identify user
     * @return
     */
    public Map<ProductDto,Integer> watchShoppingCart(String username){
        return tradingSystemFacade.watchShoppingCart(username);
    }

    /**
     * View buyer purchase history
     * @param userName of the user the history belongs to
     * @return
     */
    public List<ReceiptDto> viewPurchaseHistory(String userName){
        return tradingSystemFacade.viewPurchaseHistory(userName);
    }


    /**
     * search product by productName
     * @param productName - criteria for search
     * @return
     */
    public List<ProductDto> searchProductByName(String productName){
        return tradingSystemFacade.searchProductByName(productName);
    }

    /**
     * search product by category
     * @param category criteria for search
     * @return
     */
    public List<ProductDto> searchProductByCategory(String category){
        return tradingSystemFacade.searchProductByCategory(category);
    }

    /**
     * search product by KeyWords
     * @param keyWords criteria for search
     * @return
     */
    public List<ProductDto> searchProductByKeyWords(List<String> keyWords){
        return tradingSystemFacade.searchProductByKeyWords(keyWords);
    }

    /**
     * filter products by range price
     * @param products to filter
     * @param min low threshold
     * @param max threshold
     * @return
     */
    public List<ProductDto> filterByRangePrice(List<ProductDto> products, double min, double max){
        return tradingSystemFacade.filterByRangePrice(products, min, max);
    }

    /**
     * filter products by product rank
     * @param products to filter
     * @param rank filter by rank of product
     * @return
     */
    public List<ProductDto> filterByProductRank(List<ProductDto> products, int rank){
        return tradingSystemFacade.filterByProductRank(products, rank);
    }

    /**
     * filter products by  store rank
     * @param products to filter
     * @param rank filter by rank of store
     * @return
     */
    public List<ProductDto> filterByStoreRank(List<ProductDto> products, int rank){
        return tradingSystemFacade.filterByStoreRank(products, rank);
    }

    /**
     * filter products by category
     * @param products to filter
     * @param category filter criteria
     * @return
     */
    public List<ProductDto> filterByStoreCategory(List<ProductDto> products, String category){
        return tradingSystemFacade.filterByStoreCategory(products, category);
    }


    /**
     *      * save product in shopping bag
     * @param userName the username of the user which save in his bag
     * @param storeId store belobgs to the bag
     * @param productSn the identifier of the product
     * @param amount quantity to save
     * @return
     */
    public boolean saveProductInShoppingBag(String userName, int storeId, int productSn, int amount){
        return tradingSystemFacade.saveProductInShoppingBag(userName, storeId, productSn, amount);
    }

    /**
     *      * view product in shopping bag
     * @param userName the user the bag belongs to
     * @return
     */
    public ShoppingCartDto viewProductsInShoppingCart(String userName){
        return tradingSystemFacade.viewProductsInShoppingCart(userName);
    }

    /**
     *      * remove product in shopping bag (edit)
     * @param userName the user which edit
     * @param storeId the store belongs to the product
     * @param productSn identifier of product
     * @return
     */
    public boolean removeProductInShoppingBag(String userName, int storeId, int productSn){
        return tradingSystemFacade.removeProductInShoppingBag(userName, storeId, productSn);
    }

    /**
     *      * purchase shopping cart
     * @param userName user that purchase
     * @param paymentDetails info to charge of the user
     * @param billingAddress the destination of the delivery
     * @return
     */
    public List<ReceiptDto> purchaseShoppingCartBuyer(String userName, PaymentDetailsDto paymentDetails, BillingAddressDto billingAddress){
        return tradingSystemFacade.purchaseShoppingCart(userName, paymentDetails, billingAddress);
    }

    /**
     * a function that returns the list of stores that are saved in the system
     * @return - list of StoreDto's.
     */
    public List<StoreDto> getStoresDtos() {
        return this.tradingSystemFacade.getStoresDtos();
    }

    /**
     * a function that returns the list of users that are saved in the system
     * @return - list of UserSystemDto's.
     */
    public List<UserSystemDto> getUsersDtos() {
        return this.tradingSystemFacade.getUsersDtos();
    }

    /**
     * a function that returns the list of administrators that are saved in the system
     * @return - list of UserSystemDto's.
     */
    public List<UserSystemDto> getAdministratorsDtos() {
        return this.tradingSystemFacade.getAdministratorsDtos();
    }

    /**
     * a function to clear the data structures
     */
    public void clearDS(){
        this.tradingSystemFacade.clearDS();
    }

}

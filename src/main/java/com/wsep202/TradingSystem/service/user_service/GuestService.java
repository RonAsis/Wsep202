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
     * register user to the system
     * @param userName user to register - unique
     * @param password
     * @param firstName
     * @param lastName
     * @return
     */
    public boolean registerUser(String userName,
                                String password,
                                String firstName,
                                String lastName){
        return tradingSystemFacade.registerUser(userName, password, firstName, lastName);
    }

    /**
     * login user to the system
     * @param userName
     * @param password
     * @return
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
     * view store info by store id
     * @param storeId
     * @return
     */
    public StoreDto viewStoreInfo(int storeId){
        return tradingSystemFacade.viewStoreInfo(storeId);
    }

    /**
     * view product in store with store id info.
     * @param storeId belongs to the product to view
     * @param productId - product to see
     * @return
     */
    public ProductDto viewProduct(int storeId, int productId){
        return tradingSystemFacade.viewProduct(storeId, productId);
    }

    /**
     *      * search product by productName
     * @param productName
     * @return
     */
    public List<ProductDto> searchProductByName(String productName){
        return tradingSystemFacade.searchProductByName(productName);
    }

    /**
     *      * search product by category
     * @param category
     * @return
     */
    public List<ProductDto> searchProductByCategory(String category){
        return tradingSystemFacade.searchProductByCategory(category);
    }

    /**
     *      * search product by KeyWords
     * @param keyWords
     * @return
     */
    public List<ProductDto> searchProductByKeyWords(List<String> keyWords){
        return tradingSystemFacade.searchProductByKeyWords(keyWords);
    }

    /**
     *      * filter products by range price
     * @param products to filter
     * thresholds:
     * @param min
     * @param max
     * @return
     */
    public List<ProductDto> filterByRangePrice(List<ProductDto> products, double min, double max){
        return tradingSystemFacade.filterByRangePrice(products, min, max);
    }

    /**
     *      * filter products by product rank
     * @param products to filter
     * @param rank of product
     * @return
     */
    public List<ProductDto> filterByProductRank(List<ProductDto> products, int rank){
        return tradingSystemFacade.filterByProductRank(products, rank);
    }

    /**
     *      * filter products by store rank
     * @param products
     * @param rank - store rank to filter by
     * @return
     */
    public List<ProductDto> filterByStoreRank(List<ProductDto> products, int rank){
        return tradingSystemFacade.filterByStoreRank(products, rank);
    }

    /**
     *      * filter products by category
     * @param products
     * @param category
     * @return
     */
    public List<ProductDto> filterByStoreCategory(List<ProductDto> products, String category){
        return tradingSystemFacade.filterByStoreCategory(products, category);
    }

    /**
     *      * purchase shopping cart
     * @param shoppingCart includes the bags of each store the user selected
     * @param paymentDetails    - charging info of the user
     * @param billingAddressDto - the destination to deliver the purchases
     * @return
     */
    public List<ReceiptDto> purchaseShoppingCart(ShoppingCartDto shoppingCart, PaymentDetailsDto paymentDetails, BillingAddressDto billingAddressDto){
        return tradingSystemFacade.purchaseShoppingCart(shoppingCart, paymentDetails, billingAddressDto);
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

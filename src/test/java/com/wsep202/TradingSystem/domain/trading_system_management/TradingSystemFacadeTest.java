package com.wsep202.TradingSystem.domain.trading_system_management;

import com.github.rozidan.springboot.modelmapper.WithModelMapper;
import com.wsep202.TradingSystem.domain.config.TradingSystemConfiguration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TradingSystemConfiguration.class})
@WithModelMapper
class TradingSystemFacadeTest {

    @Autowired
    private TradingSystemFacade tradingSystemFacade;

    @BeforeEach
    public void setUp() {
        assertNotNull(tradingSystemFacade);
    }

    @Nested
    public class TradingSystemFacadeTestUnit {

        @Test
        void viewPurchaseHistoryUser() {
            //TODO
        }

        private List<Receipt> setUpReceipts(){
//            List<Receipt> receipts = new ArrayList<>();
//            for(int counter=0; counter <= 10 ; counter++){
//                receipts.add(Receipt)
//            }
//            return receipts;
            //TODO
        return null;
        }

        @Test
        void viewPurchaseHistoryUserException() {
            //TODO
        }

        @Test
        void viewPurchaseHistoryAdministratorOfStore() {
        }

        @Test
        void viewPurchaseHistoryAdministratorOfUser() {
        }

        @Test
        void viewPurchaseHistoryOfManager() {
        }

        @Test
        void viewPurchaseHistoryOfOwner() {
        }

        @Test
        void addProduct() {
        }

        @Test
        void deleteProductFromStore() {
        }

        @Test
        void editProduct() {
        }

        @Test
        void addOwner() {
        }

        @Test
        void addManager() {
        }

        @Test
        void addPermission() {
        }

        @Test
        void removeManager() {
        }

        @Test
        void logout() {
        }

        @Test
        void openStore() {
        }

        @Test
        void registerUser() {
        }

        @Test
        void login() {
        }

        @Test
        void viewStoreInfo() {
        }

        @Test
        void viewProduct() {
        }

        @Test
        void searchProductByName() {
        }

        @Test
        void searchProductByCategory() {
        }

        @Test
        void searchProductByKeyWords() {
        }

        @Test
        void filterByRangePrice() {
        }

        @Test
        void filterByProductRank() {
        }

        @Test
        void filterByStoreRank() {
        }

        @Test
        void filterByStoreCategory() {
        }

        @Test
        void saveProductInShoppingBag() {
        }

        @Test
        void viewProductsInShoppingCart() {
        }

        @Test
        void removeProductInShoppingBag() {
        }

        @Test
        void purchaseShoppingCart() {
        }

        @Test
        void testPurchaseShoppingCart() {
        }
    }

    ///////////////////////////////integration test /////////////////////
    @Nested
    public class TradingSystemFacadeTestIntegration {

        @Test
        void viewPurchaseHistoryUser() {
        }

        @Test
        void viewPurchaseHistoryAdministratorOfStore() {
        }

        @Test
        void viewPurchaseHistoryAdministratorOfUser() {
        }

        @Test
        void viewPurchaseHistoryOfManager() {
        }

        @Test
        void viewPurchaseHistoryOfOwner() {
        }

        @Test
        void addProduct() {
        }

        @Test
        void deleteProductFromStore() {
        }

        @Test
        void editProduct() {
        }

        @Test
        void addOwner() {
        }

        @Test
        void addManager() {
        }

        @Test
        void addPermission() {
        }

        @Test
        void removeManager() {
        }

        @Test
        void logout() {
        }

        @Test
        void openStore() {
        }

        @Test
        void registerUser() {
        }

        @Test
        void login() {
        }

        @Test
        void viewStoreInfo() {
        }

        @Test
        void viewProduct() {
        }

        @Test
        void searchProductByName() {
        }

        @Test
        void searchProductByCategory() {
        }

        @Test
        void searchProductByKeyWords() {
        }

        @Test
        void filterByRangePrice() {
        }

        @Test
        void filterByProductRank() {
        }

        @Test
        void filterByStoreRank() {
        }

        @Test
        void filterByStoreCategory() {
        }

        @Test
        void saveProductInShoppingBag() {
        }

        @Test
        void viewProductsInShoppingCart() {
        }

        @Test
        void removeProductInShoppingBag() {
        }

        @Test
        void purchaseShoppingCart() {
        }

        @Test
        void testPurchaseShoppingCart() {
        }
    }
}
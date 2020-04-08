package com.wsep202.TradingSystem.domain.trading_system_management;

import com.wsep202.TradingSystem.domain.exception.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * define user in the system
 */
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Slf4j
public class UserSystem {

    /**
     * the user name
     */
    @NotBlank
    private String userName;

    /**
     * the encryption password of the the user
     */
    @NotBlank
    private String password;

    /**
     * the first name of the user
     */
    private String firstName;

    /**
     * the last name of the user
     */
    private String lastName;

    @Builder.Default
    private Set<Store> managedStores = new HashSet<>();

    @Builder.Default
    private Set<Store> ownedStores = new HashSet<>();

    @Builder.Default
    private ShoppingCart shoppingCart = new ShoppingCart();

    private boolean isLogin = false;

    private List<Receipt> receipts;

    public UserSystem(String userName, String firstName, String lastName, String password){
        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
    }
    public void addNewStore(Store store) {
        if (Objects.nonNull(store)) {
            ownedStores.add(store);
        }
    }

    public void login(){
        isLogin = true;
    }

    public boolean logout(){
        isLogin = false;
        return !isLogin;
    }

    public Store getOwnerStore(int storeId) {
        return ownedStores.stream()
                .filter(store -> store.getStoreId() == storeId)
                .findFirst().orElseThrow(() -> new NoOwnerInStoreException(userName, storeId));
    }

    public Store getManagerStore(int storeId) {
        return managedStores.stream()
                .filter(store -> store.getStoreId() == storeId)
                .findFirst().orElseThrow(() -> new NoManagerInStoreException(userName, storeId));
    }

    public boolean saveProductInShoppingBag(Store store, Product product, int amount) {
        return false;
    }

    public boolean removeProductInShoppingBag(Store store, Product product) {
        return false;
    }
}

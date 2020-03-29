package com.wsep202.TradingSystem.domain.trading_system_management;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotBlank;
import java.util.HashSet;
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

    public void addNewStore(Store store) {
        if (Objects.nonNull(store)) {
            ownedStores.add(store);
        }
    }

    public void login(){
        isLogin = true;
    }

    public void logout(){
        isLogin = false;
    }
}

package com.wsep202.TradingSystem.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSystemDto {

    /**
     * the user name
     */
    private String userName;
    /**
     * the encryption password of the the user
     */
    private String password;
    /**
     * the salt we use to hash the password for the user
     */
    private String salt;
    /**
     * the first name of the user
     */
    private String firstName;
    /**
     * the last name of the user
     */
    private String lastName;
    /**
     * The stores that the user manages
     */
    private Set<StoreDto> managedStores;
    /**
     * The stores that the user own
     */
    private Set<StoreDto> ownedStores;
    /**
     * The user personal shopping cart
     */
    private ShoppingCartDto shoppingCart;
    /**
     * Show the stage of the user, logged-in or logged-out
     */
    private boolean isLogin = false;
    /**
     * The users personal receipts list
     */
    private List<ReceiptDto> receipts;
}

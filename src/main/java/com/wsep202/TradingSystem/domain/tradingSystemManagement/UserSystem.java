package com.wsep202.TradingSystem.domain.tradingSystemManagement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * define user in the system
 */
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class UserSystem {

    /**
     * the user name
     */
    private String userName;

    /**
     * the encryption password of the the user
     */
    private String password;

    /**
     * the first name of the user
     */
    private String firstName;

    /**
     * the last name of the user
     */
    private String lastName;

}

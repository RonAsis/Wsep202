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
     * the first name of the user
     */
    private String firstName;
    /**
     * the last name of the user
     */
    private String lastName;
}

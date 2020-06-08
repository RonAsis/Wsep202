package com.wsep202.TradingSystem.dto;

import com.wsep202.TradingSystem.domain.trading_system_management.discount.CompositeOperator;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Slf4j
public class UserDetailsPolicyDto {

    /**
     * The id of the discount to edit
     */
    private int id;
    /**
     * list of countries which their residents ca purchase in the store
     */
    private Set<String> countriesPermitted;

}

/**
 * this class represents the address information of a user in the system
 */

package com.wsep202.TradingSystem.domain.trading_system_management;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Slf4j

public class BillingAddress {
    private String fName;
    private String lName;
    private String address;
    private String phone;
}

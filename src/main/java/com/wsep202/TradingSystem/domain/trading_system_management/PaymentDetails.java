/**
 * this class represents all necessary payment datails of the customer for
 * purchasing the shopping items
 */
package com.wsep202.TradingSystem.domain.trading_system_management;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Slf4j
public class PaymentDetails {
    private String creditCardNumber;
    private Date validThru;
}

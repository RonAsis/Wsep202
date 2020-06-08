
package com.wsep202.TradingSystem.dto;
import com.wsep202.TradingSystem.domain.trading_system_management.policy_purchase.Day;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import java.util.Set;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Slf4j
public class SystemDetailsPolicyDto {
    /**
     * The id of the discount to edit
     */
    private int id;
    /**
     * the days in the week any user is permitted to perform a purchase
     */
    private Set<Day> storeWorkDays;

}

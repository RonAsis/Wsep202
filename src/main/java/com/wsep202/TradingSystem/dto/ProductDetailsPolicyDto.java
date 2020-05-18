

package com.wsep202.TradingSystem.dto;

import com.wsep202.TradingSystem.domain.trading_system_management.Day;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Slf4j
public class ProductDetailsPolicyDto {
    /**
     * The id of the discount to edit
     */
    private int id;
    /**
     * the SN of the product which have the purchase policy on.
     */
    private int productId;
    /**
     * the range of amounts defined as valid to buy product from the store
     */
    private int min;
    private int max;
}
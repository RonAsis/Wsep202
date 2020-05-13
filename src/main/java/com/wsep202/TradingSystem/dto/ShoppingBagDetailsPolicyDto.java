

package com.wsep202.TradingSystem.dto;

import lombok.*;
import lombok.extern.slf4j.Slf4j;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Slf4j
public class ShoppingBagDetailsPolicyDto {
    /**
     * The id of the discount to edit
     */
    private int id;
    /**
     * the range of amounts defined as valid to buy products in bag from the store
     */
    private int min;
    private int max;

}
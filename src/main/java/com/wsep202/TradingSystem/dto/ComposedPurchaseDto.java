


package com.wsep202.TradingSystem.dto;

import com.wsep202.TradingSystem.domain.trading_system_management.discount.CompositeOperator;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Slf4j
public class ComposedPurchaseDto {
    /**
     * The id of the discount
     */
    private int id;
    /**
     * logical operator between policies
     */
    private CompositeOperator compositeOperator;
    /**
     * children components of the composite Purchase policy
     * the operands of the composed Purchase policy
     */
    private List<PurchasePolicyDto> composedPurchasePolicies;
}

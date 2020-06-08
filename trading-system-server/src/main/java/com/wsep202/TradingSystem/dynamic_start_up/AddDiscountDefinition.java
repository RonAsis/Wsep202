package com.wsep202.TradingSystem.dynamic_start_up;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.wsep202.TradingSystem.domain.trading_system_management.TradingSystemFacade;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@JsonTypeName(AddDiscountDefinition.type)
@FieldNameConstants
@Data
@EqualsAndHashCode(callSuper = true)
public class AddDiscountDefinition extends ActivityDefinition{

    private String userName;
    private String storeId;
    // TODO add the rest fields

    public final static String type = "addDiscount";

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void apply(Context context, TradingSystemFacade tradingSystemFacade) {
        // TODO
    }
}

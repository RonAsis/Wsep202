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
@JsonTypeName(AddOwnerDefinition.type)
@FieldNameConstants
@Data
@EqualsAndHashCode(callSuper = true)
public class AddOwnerDefinition extends ActivityDefinition{

    private String userName;
    private int storeId;
    private String newOwnerUsername;

    public final static String type = "addOwner";

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void apply(Context context, TradingSystemFacade tradingSystemFacade) {
        tradingSystemFacade.addOwner(userName, context.getRealStoreId(storeId), newOwnerUsername, context.getUuid(userName));
    }
}

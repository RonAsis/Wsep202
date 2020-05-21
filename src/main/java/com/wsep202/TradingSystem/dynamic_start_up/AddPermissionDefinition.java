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
@JsonTypeName(AddPermissionDefinition.type)
@FieldNameConstants
@Data
@EqualsAndHashCode(callSuper = true)
public class AddPermissionDefinition extends ActivityDefinition{

    private String ownerUserName;
    private int storeId;
    private String managerUsername;
    private String permission;

    public final static String type = "addPermission";

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void apply(Context context, TradingSystemFacade tradingSystemFacade) {
        tradingSystemFacade.addPermission(ownerUserName, context.getRealStoreId(storeId), managerUsername, permission, context.getUuid(ownerUserName));
    }
}

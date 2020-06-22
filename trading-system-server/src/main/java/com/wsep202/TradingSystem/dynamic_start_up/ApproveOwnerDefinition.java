package com.wsep202.TradingSystem.dynamic_start_up;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.wsep202.TradingSystem.domain.trading_system_management.TradingSystemFacade;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

@AllArgsConstructor
@NoArgsConstructor
@JsonTypeName(ApproveOwnerDefinition.type)
@FieldNameConstants
@Data
@EqualsAndHashCode(callSuper = true)
public class ApproveOwnerDefinition extends ActivityDefinition {

    private String ownerUsername;
    private String ownerToApprove;
    private int storeId;
    private boolean status;

    public final static String type = "approveOwner";

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void apply(Context context, TradingSystemFacade tradingSystemFacade) {
        tradingSystemFacade.approveOwner(ownerUsername,
                context.getRealStoreId(storeId),
                ownerToApprove,status,context.getUuid(ownerUsername));
    }
}

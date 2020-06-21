package com.wsep202.TradingSystem.dynamic_start_up;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.wsep202.TradingSystem.domain.trading_system_management.TradingSystemFacade;
import com.wsep202.TradingSystem.dto.PurchasePolicyDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import java.util.Objects;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@JsonTypeName(AddPolicyPurchaseDefinition.type)
@FieldNameConstants
@Data
@EqualsAndHashCode(callSuper = true)
public class AddPolicyPurchaseDefinition extends ActivityDefinition{

    public final static String type = "addPolicyPurchase";

    private String userName;
    private int storeId;
    private PurchasePolicyDto policyDto;

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void apply(Context context, TradingSystemFacade tradingSystemFacade) {
        PurchasePolicyDto purchasePolicyDto = tradingSystemFacade.
                addEditPurchase(userName,context.getRealStoreId(storeId),policyDto, context.getUuid(userName));
        if(Objects.nonNull(purchasePolicyDto)){
            context.addPurchaseId(purchasePolicyDto.getPurchaseId());
        }
    }
}

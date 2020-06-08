package com.wsep202.TradingSystem.dynamic_start_up;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.wsep202.TradingSystem.domain.trading_system_management.Store;
import com.wsep202.TradingSystem.domain.trading_system_management.TradingSystemFacade;
import com.wsep202.TradingSystem.dto.StoreDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import java.util.Objects;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@JsonTypeName(OpenStoreDefinition.type)
@FieldNameConstants
@Data
@EqualsAndHashCode(callSuper = true)
public class OpenStoreDefinition extends ActivityDefinition{

    private String userNameOwner;
    private String storeName;
    private String description;

    public final static String type = "openStore";

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void apply(Context context, TradingSystemFacade tradingSystemFacade) {
        StoreDto store = tradingSystemFacade.openStore(userNameOwner, storeName, description, context.getUuid(userNameOwner));
        if(Objects.nonNull(store)) {
            context.addStoreId(store.getStoreId());
        }
    }
}

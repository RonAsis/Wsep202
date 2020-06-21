package com.wsep202.TradingSystem.dynamic_start_up;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.wsep202.TradingSystem.domain.trading_system_management.TradingSystemFacade;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = RegisterDefinition.class, name = RegisterDefinition.type),
        @JsonSubTypes.Type(value = LoginDefinition.class, name = LoginDefinition.type),
        @JsonSubTypes.Type(value = AddDiscountDefinition.class, name = AddDiscountDefinition.type),
        @JsonSubTypes.Type(value = AddManagerDefinition.class, name = AddManagerDefinition.type),
        @JsonSubTypes.Type(value = AddOwnerDefinition.class, name = AddOwnerDefinition.type),
        @JsonSubTypes.Type(value = AddPermissionDefinition.class, name = AddPermissionDefinition.type),
        @JsonSubTypes.Type(value = AddPolicyPurchaseDefinition.class, name = AddPolicyPurchaseDefinition.type),
        @JsonSubTypes.Type(value = AddProductDefinition.class, name = AddProductDefinition.type),
        @JsonSubTypes.Type(value = OpenStoreDefinition.class, name = OpenStoreDefinition.type),
        @JsonSubTypes.Type(value = ApproveOwnerDefinition.class, name = ApproveOwnerDefinition.type),
        @JsonSubTypes.Type(value = PurchaseBuyerUserDefinition.class, name = PurchaseBuyerUserDefinition.type),
        @JsonSubTypes.Type(value = LogoutDefinition.class, name = LogoutDefinition.type),

})
public abstract class ActivityDefinition {

    @JsonIgnore
    public abstract String getType();

    @JsonIgnore
    public abstract void apply(Context context, TradingSystemFacade tradingSystemFacade);
}

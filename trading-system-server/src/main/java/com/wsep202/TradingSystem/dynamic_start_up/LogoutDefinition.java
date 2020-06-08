package com.wsep202.TradingSystem.dynamic_start_up;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.wsep202.TradingSystem.domain.trading_system_management.TradingSystemFacade;
import javafx.util.Pair;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import java.util.Objects;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@JsonTypeName(LogoutDefinition.type)
@FieldNameConstants
@Data
@EqualsAndHashCode(callSuper = true)
public class LogoutDefinition extends ActivityDefinition {

    private String userName;

    public final static String type = "logout";

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void apply(Context context, TradingSystemFacade tradingSystemFacade) {
        boolean logout = tradingSystemFacade.logout(userName, context.getUuid(userName));
        if(logout){
            context.removeUuid(userName);
        }
    }
}

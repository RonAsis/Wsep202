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
@JsonTypeName(LoginDefinition.type)
@FieldNameConstants
@Data
@EqualsAndHashCode(callSuper = true)
public class LoginDefinition extends ActivityDefinition {

    private String userName;
    private String password;

    public final static String type = "login";

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void apply(Context context, TradingSystemFacade tradingSystemFacade) {
        Pair<UUID, Boolean> loginRes = tradingSystemFacade.login(userName, password);
        if (Objects.nonNull(loginRes)) {
            context.addUsernameUuid(userName, loginRes.getKey());
        }
    }
}

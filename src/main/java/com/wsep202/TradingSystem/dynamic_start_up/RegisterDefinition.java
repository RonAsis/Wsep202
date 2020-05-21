package com.wsep202.TradingSystem.dynamic_start_up;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.wsep202.TradingSystem.domain.trading_system_management.TradingSystemFacade;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.springframework.web.bind.annotation.PathVariable;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@JsonTypeName(RegisterDefinition.type)
@FieldNameConstants
@Data
@EqualsAndHashCode(callSuper = true)
public class RegisterDefinition extends ActivityDefinition{

    private String userName;
    private String password;
    private String firstName;
    private String lastName;

    public final static String type = "register";

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void apply(Context context, TradingSystemFacade tradingSystemFacade) {
        tradingSystemFacade.registerUser(userName, password, firstName, lastName, null);
    }
}

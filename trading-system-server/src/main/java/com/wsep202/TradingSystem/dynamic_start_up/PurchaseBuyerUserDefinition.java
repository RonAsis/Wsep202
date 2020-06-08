package com.wsep202.TradingSystem.dynamic_start_up;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.wsep202.TradingSystem.domain.trading_system_management.TradingSystemFacade;
import com.wsep202.TradingSystem.dto.BillingAddressDto;
import com.wsep202.TradingSystem.dto.PaymentDetailsDto;
import com.wsep202.TradingSystem.dto.ProductDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@JsonTypeName(PurchaseBuyerUserDefinition.type)
@FieldNameConstants
@Data
@EqualsAndHashCode(callSuper = true)
public class PurchaseBuyerUserDefinition extends ActivityDefinition{

    private String userName;
    private PaymentDetailsDto paymentDetailsDto;
    private BillingAddressDto billingAddressDto;

    public final static String type = "purchaseBuyer";

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void apply(Context context, TradingSystemFacade tradingSystemFacade) {
        tradingSystemFacade.purchaseShoppingCart(userName, paymentDetailsDto, billingAddressDto, context.getUuid(userName));
    }
}

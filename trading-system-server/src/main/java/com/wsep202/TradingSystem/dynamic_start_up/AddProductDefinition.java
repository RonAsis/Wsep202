package com.wsep202.TradingSystem.dynamic_start_up;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.wsep202.TradingSystem.domain.trading_system_management.TradingSystemFacade;
import com.wsep202.TradingSystem.dto.ProductDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import java.util.Objects;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@JsonTypeName(AddProductDefinition.type)
@FieldNameConstants
@Data
@EqualsAndHashCode(callSuper = true)
public class AddProductDefinition extends ActivityDefinition{

    private String userName;
    private int storeId;
    private String productName;
    private String category;
    private int amount;
    private double cost;

    public final static String type = "addProduct";

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void apply(Context context, TradingSystemFacade tradingSystemFacade) {
        ProductDto productDto = tradingSystemFacade.addProduct(userName, context.getRealStoreId(storeId),
                productName, category, amount, cost, context.getUuid(userName));
        if(Objects.nonNull(productDto)) {
            context.addProductId(productDto.getProductSn());
        }
    }
}

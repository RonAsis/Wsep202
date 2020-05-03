package com.wsep202.TradingSystem.dto;

import com.wsep202.TradingSystem.domain.trading_system_management.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiscountPolicyDto {


    protected Calendar endTime; //expiration date of the discount
    protected double discountPercentage;    //discount percentage for product
    protected ArrayList<Product> productsUnderThisDiscount;
}

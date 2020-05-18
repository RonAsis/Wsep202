package com.wsep202.TradingSystem.domain.trading_system_management.discount;

import com.wsep202.TradingSystem.domain.trading_system_management.Product;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Setter
@Getter
@Slf4j
@NoArgsConstructor
@Builder
public class ComposedDiscount extends DiscountPolicy {

    @Override
    public void applyDiscount(Discount discount, Map<Product, Integer> products) {
        if (!discount.getAmountOfProductsForApplyDiscounts().isEmpty() && isApprovedProducts(discount, products)) {
            discount.applyConditionalDiscount(products);
        } else {
            switch (discount.getCompositeOperator()) {
                case AND:
                    discount.getComposedDiscounts().forEach(discountPolicy -> discountPolicy.applyDiscount(products));
                    break;
                case OR:
                    AtomicBoolean oneAlreadyApply = new AtomicBoolean(false);
                    discount.getComposedDiscounts().forEach(discountCur -> {
                        if (!oneAlreadyApply.get()) {
                            if (discountCur.isApprovedProducts(products)) {
                                discountCur.applyDiscount(products);
                                oneAlreadyApply.set(true);
                            }
                        }
                        discountCur.setApplied(true);
                    });
                    break;
                case XOR:
                    AtomicInteger numOfPolicyApproved = new AtomicInteger(getNumOfPolicyApproved(discount, products));
                    numOfPolicyApproved = numOfPolicyApproved.get() % 2 != 0 ? numOfPolicyApproved : new AtomicInteger(numOfPolicyApproved.get() - 1);
                    AtomicInteger finalNumOfPolicyApproved = numOfPolicyApproved;
                    discount.getComposedDiscounts().forEach(discountCur -> {
                        if (finalNumOfPolicyApproved.get() > 0) {
                            if (discountCur.isApprovedProducts(products)) {
                                finalNumOfPolicyApproved.getAndDecrement();
                                discountCur.applyDiscount(products);
                            }
                        }
                        discountCur.setApplied(true);
                    });
                    break;
            }
        }
    }

    @Override
    public boolean isApprovedProducts(Discount discount, Map<Product, Integer> products) {
        boolean isApproved = !isExpired(discount);
        switch (discount.getCompositeOperator()) {
            case AND:
                isApproved = isApproved && discount.getComposedDiscounts().stream()
                        .allMatch(discountPolicy -> discountPolicy.isApprovedProducts(products));
                break;
            case OR:
                isApproved = isApproved && discount.getComposedDiscounts().stream()
                        .anyMatch(discountCur -> discountCur.isApprovedProducts(products));
                break;
            case XOR:
                isApproved = isApproved && getNumOfPolicyApproved(discount, products) % 2 != 0;
                break;
        }
        return isApproved;
    }

    @Override
    public void undoDiscount(Discount discount, Map<Product, Integer> products) {

    }

    private int getNumOfPolicyApproved(Discount discount, Map<Product, Integer> products) {
        return discount.getComposedDiscounts().stream()
                .filter(discountCur -> discountCur.isApprovedProducts(products))
                .toArray().length;
    }
}

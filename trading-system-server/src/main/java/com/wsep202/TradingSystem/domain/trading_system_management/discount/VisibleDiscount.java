package com.wsep202.TradingSystem.domain.trading_system_management.discount;
/**
 * products under discount with no condition
 */

import com.wsep202.TradingSystem.domain.exception.IllegalPercentageException;
import com.wsep202.TradingSystem.domain.trading_system_management.Product;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.Map;

@Data
@Slf4j
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class VisibleDiscount extends DiscountPolicy {

    /**
     * amount of product from to apply discount
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @MapKeyColumn(name = "amountOfProductsForApplyDiscounts")
    @Cascade(value = { CascadeType.ALL })
    @JoinTable()
    private Map<Product, Integer> amountOfProductsForApplyDiscounts;

    @Override
    public void applyDiscount(Discount discount, Map<Product, Integer> products) {
        verifyValidity(discount);
        if (!isExpired(discount)) {
            products.keySet().forEach(product -> {
                if (isProductHaveDiscount(amountOfProductsForApplyDiscounts, product)) {
                    double discountCost = (discount.getDiscountPercentage() / 100) * product.getOriginalCost();
                    setCostAfterDiscount(discount, product, discountCost);
                }
            });
        } else {  //check if needs to update back the price
            undoDiscount(discount, products);
        }
    }

    @Override
    public boolean isApprovedProducts(Discount discount, Map<Product, Integer> products) {
        verifyValidity(discount);
        return !isExpired(discount) && products.keySet().stream()
                .anyMatch(product -> isProductHaveDiscount(amountOfProductsForApplyDiscounts, product));
    }

    @Override
    public void undoDiscount(Discount discount, Map<Product, Integer> products) {
        products.keySet().forEach(product -> {
            if (isProductHaveDiscount(amountOfProductsForApplyDiscounts, product) && discount.isApplied()) {
                double discountCost = calculateDiscount(discount, product.getOriginalCost());
                product.setCost(product.getCost() + discountCost);    //update the price by discount
            }
        });
    }

    @Override
    public void removeProductFromDiscount(int productSn) {
        removeProductFromCollection(amountOfProductsForApplyDiscounts, productSn);
    }
    private void verifyValidity(Discount discount) {
        if (discount.getDiscountPercentage() < 0) {
            throw new IllegalPercentageException(discount.getDiscountId(), discount.getDiscountPercentage());
        }
    }

}

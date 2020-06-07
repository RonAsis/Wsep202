package com.wsep202.TradingSystem.domain.trading_system_management.discount;

import com.wsep202.TradingSystem.domain.exception.CompositeOperatorNullException;
import com.wsep202.TradingSystem.domain.trading_system_management.Product;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Setter
@Getter
@Slf4j
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Entity
public class ComposedDiscount extends DiscountPolicy {

    /**
     * amount of product from to apply discount
     */
    @MapKeyColumn(name = "amountOfProductsForApplyDiscounts")
    @ElementCollection(fetch = FetchType.EAGER)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private Map<Product, Integer> amountOfProductsForApplyDiscounts;

    /**
     * products that has the specified discount
     */
    @ElementCollection
    @MapKeyColumn(name = "productsUnderThisDiscount")
    private Map<Product, Integer> productsUnderThisDiscount;

    /**
     * children components of the composite conditional discount
     */
    @OneToMany(fetch = FetchType.EAGER, cascade={CascadeType.PERSIST,CascadeType.REMOVE}, orphanRemoval = true)
    private List<Discount> composedDiscounts;

    /**
     * the operation between the conditionals discounts
     */
    @Enumerated(EnumType.STRING)
    private CompositeOperator compositeOperator;

    @Override
    public void applyDiscount(Discount discount, Map<Product, Integer> products) {
        if (amountOfProductsForApplyDiscounts!=null&&!amountOfProductsForApplyDiscounts.isEmpty() && isApprovedProducts(discount, products)) {
            //composed discount terms are met so apply the discount as set in its properties.
            createConditionalDiscount().applyDiscount(discount, products);
        } else {    //for support in no "kefel mivtzaim" etc....
            switch (compositeOperator) {
                case AND:   //here we will try to apply all discounts
                    composedDiscounts.forEach(discountPolicy -> discountPolicy.applyDiscount(products));
                    break;
                case OR:    //no multiple discount applying - the first will be applied only
                    AtomicBoolean oneAlreadyApply = new AtomicBoolean(false);
                    composedDiscounts.forEach(discountCur -> {
                        if (!oneAlreadyApply.get()) {
                            if (discountCur.isApprovedProducts(products)) {
                                discountCur.applyDiscount(products);
                                oneAlreadyApply.set(true);
                            }
                        }
                        //discountCur.setApplied(true); they do it by alone
                    });
                    break;
                case XOR:   //as logic xor, we will try to apply odd amount of discounts
                    AtomicInteger numOfPolicyApproved = new AtomicInteger(getNumOfPolicyApproved(discount, products));
                    numOfPolicyApproved = numOfPolicyApproved.get() % 2 != 0 ? numOfPolicyApproved : new AtomicInteger(numOfPolicyApproved.get() - 1);
                    AtomicInteger finalNumOfPolicyApproved = numOfPolicyApproved;
                    composedDiscounts.forEach(discountCur -> {
                        if (finalNumOfPolicyApproved.get() > 0) {
                            if (discountCur.isApprovedProducts(products)) {
                                finalNumOfPolicyApproved.getAndDecrement();
                                discountCur.applyDiscount(products);
                            }
                        }
                        //discountCur.setApplied(true); they do it by alone
                    });
                    break;
            }
        }
    }


    private ConditionalProductDiscount createConditionalDiscount(){
        return ConditionalProductDiscount.builder()
                .productsApplyDiscounts(amountOfProductsForApplyDiscounts)
                .productsUnderThisDiscount(productsUnderThisDiscount)
                .build();
    }
    @Override
    public boolean isApprovedProducts(Discount discount, Map<Product, Integer> products) {
        boolean isApproved = !isExpired(discount);
        switch (compositeOperator) {
            case AND:
                isApproved = isApproved && composedDiscounts.stream()
                        .allMatch(discountPolicy -> discountPolicy.isApprovedProducts(products));
                break;
            case OR:
                isApproved = isApproved && composedDiscounts.stream()
                        .anyMatch(discountCur -> discountCur.isApprovedProducts(products));
                break;
            case XOR:
                isApproved = isApproved && getNumOfPolicyApproved(discount, products) % 2 != 0;
                break;
        }
        return isApproved;
    }

    @Override   //the undo will be done for each of the simple discounts in store anyway
    public void undoDiscount(Discount discount, Map<Product, Integer> products) {

    }

    @Override
    public void removeProductFromDiscount(int productSn) {
        removeProductFromCollection(amountOfProductsForApplyDiscounts, productSn);
        removeProductFromCollection(productsUnderThisDiscount, productSn);
    }

    private int getNumOfPolicyApproved(Discount discount, Map<Product, Integer> products) {
        return composedDiscounts.stream()
                .filter(discountCur -> discountCur.isApprovedProducts(products))
                .toArray().length;
    }
}

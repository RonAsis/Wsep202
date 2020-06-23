/**
 * this class represents a simple discount of conditional type discount
 */
package com.wsep202.TradingSystem.domain.trading_system_management.discount;

import com.wsep202.TradingSystem.domain.exception.ConditionalProductException;
import com.wsep202.TradingSystem.domain.exception.IllegalPercentageException;
import com.wsep202.TradingSystem.domain.trading_system_management.Product;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Cascade;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.MapKeyColumn;
import java.util.Map;

@Setter
@Getter
@Slf4j
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class ConditionalProductDiscount extends DiscountPolicy {

    /**
     * products that has the specified discount
     */
    @MapKeyColumn(name = "productsUnderThisDiscount")
    @ElementCollection(fetch = FetchType.EAGER)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private Map<Product, Integer> productsUnderThisDiscount;

    /**
     * amount of product from to apply discount
     */
    @MapKeyColumn(name = "productsApplyDiscounts")
    @ElementCollection(fetch = FetchType.EAGER)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private Map<Product, Integer> productsApplyDiscounts;

    @Override
    public void applyDiscount(Discount discount, Map<Product, Integer> products) {
        verifyValidity(discount);
        if (!isExpired(discount) && !discount.isApplied()) {
            boolean allConditionalPass = isApprovedProducts(discount, products);
            if (allConditionalPass) {
                products.forEach((product, amount) -> {
                    if (isApprovedCondition(discount, product, amount)) {
                        int amountProductInAmountOfProductsForApplyDiscounts = getAmountProductInAmountOfProductsForApplyDiscounts(discount, product);
                        amountProductInAmountOfProductsForApplyDiscounts = amountProductInAmountOfProductsForApplyDiscounts == -1 ? amount : amountProductInAmountOfProductsForApplyDiscounts;
                        double discountCost = calculateDiscount(discount.getDiscountPercentage(), amountProductInAmountOfProductsForApplyDiscounts,
                                amount, product.getOriginalCost());
                        product.setCost(product.getCost() - discountCost);    //update the price by discountCost
                    }
                });
            }
            discount.setApplied(true);  //discount already performed
        }
    }

    @Override
    public boolean isApprovedProducts(Discount discount, Map<Product, Integer> products) {
        verifyValidity(discount);
        return productsUnderThisDiscount.isEmpty() || !isExpired(discount) && products.entrySet().stream()
                .filter(productIntegerEntry -> isApprovedCondition(discount, productIntegerEntry.getKey(), productIntegerEntry.getValue()))
                .toArray().length == productsUnderThisDiscount.size();

    }

    @Override
    public void undoDiscount(Discount discount, Map<Product, Integer> products) {
        products.entrySet().forEach(productIntegerEntry -> {
            if(productsUnderThisDiscount.containsKey(productIntegerEntry.getKey())) {
                int amountProductInAmountOfProductsForApplyDiscounts = getAmountProductInAmountOfProductsForApplyDiscounts(discount, productIntegerEntry.getKey());
                double discountCost = calculateDiscount(discount.getDiscountPercentage(),amountProductInAmountOfProductsForApplyDiscounts,
                        productIntegerEntry.getValue(), productIntegerEntry.getKey().getOriginalCost());
                productIntegerEntry.getKey().setCost(productIntegerEntry.getKey().getCost() + discountCost);    //update the price by discountCost
            }
        });
    }

    @Override
    public void removeProductFromDiscount(int productSn) {
        removeProductFromCollection(productsUnderThisDiscount, productSn);
        removeProductFromCollection(productsApplyDiscounts, productSn);
    }

    private int getAmountProductInAmountOfProductsForApplyDiscounts(Discount discount, Product product) {
        return productsApplyDiscounts.entrySet().stream()
                .filter(productInDisCount -> productInDisCount.getKey().getProductSn() == product.getProductSn())
                .map(Map.Entry::getValue)
                .findFirst().orElse(0);
    }

    private double calculateDiscount(double discountPercentage, int amountToApply, Integer amountInBag, double price) {
        if (amountInBag == 0) {
            return 0;
        }
        return (((discountPercentage * amountToApply) / amountInBag) / 100) * price;
    }

    /**
     * checks weather the products stands in the condition or not
     * @param product to check
     * @param requiredAmount to check
     * @return  true if the products stands in terms for discount
     */

    public boolean isApprovedCondition(Discount discount,Product product, int requiredAmount) {
        return productsUnderThisDiscount.isEmpty() || (isProductHaveDiscount(productsUnderThisDiscount, product) && getAmountProductInAmountOfProductsForApplyDiscounts(discount, product) < requiredAmount);
    }

    private void verifyValidity(Discount discount) {
        if(discount.getDiscountPercentage()<0){
            throw new IllegalPercentageException(discount.getDiscountId(),discount.getDiscountPercentage());
        }
        if(this.productsUnderThisDiscount==null){
            throw new ConditionalProductException("There are no products as condition. check discountId: "+discount.getDiscountId());
        }
        if(this.productsApplyDiscounts ==null||
                productsApplyDiscounts.isEmpty()){
            throw new ConditionalProductException("There are no products amounts to apply discount. check discountId: "+discount.getDiscountId());
        }
    }

}

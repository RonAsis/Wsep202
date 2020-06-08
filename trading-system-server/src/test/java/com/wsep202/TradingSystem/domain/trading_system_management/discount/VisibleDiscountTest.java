package com.wsep202.TradingSystem.domain.trading_system_management.discount;


import com.wsep202.TradingSystem.domain.exception.*;
import com.wsep202.TradingSystem.domain.trading_system_management.Product;
import com.wsep202.TradingSystem.domain.trading_system_management.discount.CompositeOperator;
import com.wsep202.TradingSystem.domain.trading_system_management.discount.ConditionalStoreDiscount;
import com.wsep202.TradingSystem.domain.trading_system_management.discount.Discount;
import com.wsep202.TradingSystem.domain.trading_system_management.policy_purchase.Purchase;
import com.wsep202.TradingSystem.domain.trading_system_management.policy_purchase.PurchasePolicy;
import com.wsep202.TradingSystem.domain.trading_system_management.purchase.BillingAddress;
import io.swagger.models.auth.In;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;

import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class VisibleDiscountTest {
    //the components under test:
    VisibleDiscount visibleDiscountUT;  //unit under test
    Discount discount;//the discount details of the discount - percentage, end time, description.

    Product productUnderDiscount;   //product has discount
    Integer amount = 0; //doesn't relevant for logic of this type of discount- only used
    Map<Product,Integer> productsToCheckAndApply;   //the received products as parameters

    //products to apply the discount on
    Product product1;
    Product product2;

    @AfterEach
    void tearDown() {
    }

    @Nested
    public class VisibleDiscountTestUnit {
        @BeforeEach
        void setUp() {
            productUnderDiscount = mock(Product.class);
            when(productUnderDiscount.getProductSn()).thenReturn(0);
            when(productUnderDiscount.getCost()).thenReturn(50.0);
            when(productUnderDiscount.getOriginalCost()).thenReturn(50.0);

            setUpProductsToApply(); //set up products to apply

            productsToCheckAndApply = new HashMap<>();
        }



        /**
         * check applying discount on product that has discount
         * the discount params:
         * 1. end time not expired.
         * 2. discount percentage = 100%
         * 3. description = some descriptive description
         * success: the price of the product updated after discount.
         * other products price is not changed.
         */
        @Test
        void applyDiscountUnderDiscount100Positive() {
            //setup of the discount itself
            Calendar farEndTime = Calendar.getInstance();
            farEndTime.set(3000,12,12);
            setVisibleDiscount(productUnderDiscount);
            discount = setDiscount(100.0, farEndTime, "positive 100% not" +
                    " expired");
            productsToCheckAndApply.put(product1,amount);
            productsToCheckAndApply.put(product2,amount);
            discount.applyDiscount(productsToCheckAndApply);
            Assertions.assertEquals(product1.getCost(),0);
            Assertions.assertEquals(product2.getCost(),20.0);
            //verify original cost stays
            Assertions.assertEquals(product1.getOriginalCost(),10);
            Assertions.assertEquals(product2.getOriginalCost(),20);
        }

        /**
         * check applying discount on product that has discount
         * the discount params:
         * 1. end time not expired.
         * 2. discount percentage = 50%
         * 3. description = some descriptive description
         * success: the price of the product updated after discount.
         * other products price is not changed.
         */
        @Test
        void applyDiscountUnderDiscount50Positive() {
            //setup of the discount itself
            Calendar farEndTime = Calendar.getInstance();
            farEndTime.set(3000,12,12);
            setVisibleDiscount(productUnderDiscount);
            discount = setDiscount(50.0, farEndTime, "positive 50% not" +
                    " expired");
            productsToCheckAndApply.put(product1,amount);
            productsToCheckAndApply.put(product2,amount);
            discount.applyDiscount(productsToCheckAndApply);
            Assertions.assertEquals(product1.getCost(),5);
            Assertions.assertEquals(product2.getCost(),20.0);
            //verify original cost stays
            Assertions.assertEquals(product1.getOriginalCost(),10);
            Assertions.assertEquals(product2.getOriginalCost(),20);
        }

        /**
         * check applying discount on product that has discount and after expired, undo
         * the discount params:
         * success: the price of the product updated after discount and returned to the
         * previous after time of discount expired.
         * other products price is not changed.
         */
        @Test
        void applyUndo50AfterExpiredPositive() {
            //setup of the discount itself
            Calendar farEndTime = Calendar.getInstance();
            farEndTime.set(3000,12,12);
            setVisibleDiscount(productUnderDiscount);
            discount = setDiscount(50.0, farEndTime, "positive 50% not" +
                    " expired");
            productsToCheckAndApply.put(product1,amount);
            productsToCheckAndApply.put(product2,amount);
            //apply discount
            discount.applyDiscount(productsToCheckAndApply);
            Assertions.assertEquals(product1.getCost(),5);
            Assertions.assertEquals(product2.getCost(),20.0);
            //verify original cost stays
            Assertions.assertEquals(product1.getOriginalCost(),10);
            Assertions.assertEquals(product2.getOriginalCost(),20);
            Calendar expiredEndTime = Calendar.getInstance();
            expiredEndTime.set(1545,1,1);
            discount.setEndTime(expiredEndTime);

            //apply discount
            discount.applyDiscount(productsToCheckAndApply);
            Assertions.assertEquals(product1.getCost(),10);
            Assertions.assertEquals(product2.getCost(),20);
            Assertions.assertEquals(product1.getOriginalCost(),10);
            Assertions.assertEquals(product2.getOriginalCost(),20);
        }


        /**
         * check applying discount on product that has discount but expired
         * the discount params:
         * 1. end time expired.
         * 2. discount percentage = 50%
         * 3. description = some descriptive description
         * fail: the price of the product not updated after discount.
         * other products price is not changed.
         */
        @Test
        void applyDiscountExpiredNegative() {
            //setup of the discount itself
            Calendar farEndTime = Calendar.getInstance();
            farEndTime.set(1990,8,12);
            setVisibleDiscount(productUnderDiscount);
            discount = setDiscount(50.0, farEndTime, "negative 50% " +
                    " expired");
            productsToCheckAndApply.put(product1,amount);
            productsToCheckAndApply.put(product2,amount);
            discount.applyDiscount(productsToCheckAndApply);
            Assertions.assertEquals(product1.getCost(),10.0);
            Assertions.assertEquals(product2.getCost(),20.0);
            //verify original cost stays
            Assertions.assertEquals(product1.getOriginalCost(),10);
            Assertions.assertEquals(product2.getOriginalCost(),20);
        }

        /**
         * check applying discount on product that has discount but expired
         * the discount params:
         * 1. end time expired.
         * 2. discount percentage = -50%
         * 3. description = some descriptive description
         * fail: exception is thrown
         */
        @Test
        void applyDiscountInvalidDiscountNegative() {
            //setup of the discount itself
            Calendar farEndTime = Calendar.getInstance();
            farEndTime.set(3000,8,12);
            setVisibleDiscount(productUnderDiscount);
            discount = setDiscount(-50.0, farEndTime, "negative -50% " +
                    " expired");
            productsToCheckAndApply.put(product1,amount);
            productsToCheckAndApply.put(product2,amount);

            Throwable exception = Assertions.assertThrows(IllegalPercentageException.class, () -> discount.applyDiscount(productsToCheckAndApply));
            Assertions.assertEquals("got invalid percentage value: "+(-50.0)+" for discountId: "+discount.getDiscountId(), exception.getMessage());
        }

        /**
         * verifies there is no error while trying to apply on empty bag
         * fail: in addition applied failed
         */
        @Test
        void applyWithEmptyProductsNegative(){
            //setup of the discount itself
            Calendar farEndTime = Calendar.getInstance();
            farEndTime.set(3000,8,12);
            setVisibleDiscount(productUnderDiscount);
            discount = setDiscount(50.0, farEndTime, "negative empty basket " +
                    "not expired");
            //verify there is no exception
            discount.applyDiscount(productsToCheckAndApply);
        }

        /**
         * exception raised for null value bag
         * fail: in addition applied failed
         */
        @Test
        void applyWithNullProductsNegative(){
            //setup of the discount itself
            Calendar farEndTime = Calendar.getInstance();
            farEndTime.set(3000,8,12);
            setVisibleDiscount(productUnderDiscount);
            discount = setDiscount(50.0, farEndTime, "negative null basket " +
                    "not expired");
            productsToCheckAndApply = null;
            //verify there is  exception
            Throwable exception = Assertions.assertThrows(NullPointerException.class, () -> discount.applyDiscount(productsToCheckAndApply));
            Assertions.assertEquals(NullPointerException.class, exception.getClass());

        }


        /**
         * verify that when products list that has a product under discount
         * is received and discount is not expired:
         * success: returns true
         */
        @Test
        void isApprovedProductsPositive() {
            //setup of the discount itself
            Calendar farEndTime = Calendar.getInstance();
            farEndTime.set(3000,12,12);
            setVisibleDiscount(productUnderDiscount);
            discount = setDiscount(50.0, farEndTime, "positive 50% not" +
                    " expired");
            productsToCheckAndApply.put(product1,amount);   //has discount
            productsToCheckAndApply.put(product2,amount);
            Assertions.assertTrue(discount.isApprovedProducts(productsToCheckAndApply));
        }

        /**
         * verify that when products list that has no product under discount
         * is received and discount is not expired:
         * fail: returns false
         * (tells there is no need to apply discount on list)
         */
        @Test
        void isApprovedProductsNoUnderDiscountNegative() {
            //setup of the discount itself
            Calendar farEndTime = Calendar.getInstance();
            farEndTime.set(3000,12,12);
            setVisibleDiscount(productUnderDiscount);
            discount = setDiscount(50.0, farEndTime, "positive 50% not" +
                    " expired");
            productsToCheckAndApply.put(product2,amount);
            Assertions.assertFalse(discount.isApprovedProducts(productsToCheckAndApply));
        }

        /**
         * verify that when discount expired the term to apply rejected
         * fail: returns false
         * (tells there is no need to apply discount on list)
         */
        @Test
        void isApprovedProductsExpiredNegative() {
            //setup of the discount itself
            Calendar past = Calendar.getInstance();
            past.set(1925,12,12);
            setVisibleDiscount(productUnderDiscount);
            discount = setDiscount(50.0, past, "back to past" +
                    " expired");
            productsToCheckAndApply.put(product1,amount); //product under discount
            productsToCheckAndApply.put(product2,amount);
            Assertions.assertFalse(discount.isApprovedProducts(productsToCheckAndApply));
        }


        /**
         * verifies there is no error while trying to apply on empty bag
         * fail: in addition applied failed
         */
        @Test
        void isApprovedWithEmptyProductsNegative(){
            //setup of the discount itself
            Calendar farEndTime = Calendar.getInstance();
            farEndTime.set(3000,8,12);
            setVisibleDiscount(productUnderDiscount);
            discount = setDiscount(50.0, farEndTime, "negative empty basket " +
                    "not expired");
            //verify there is no exception
            discount.isApprovedProducts(productsToCheckAndApply);
        }

        /**
         * exception raised for null value bag
         * fail: in addition applied failed
         */
        @Test
        void isApprovedWithNullProductsNegative(){
            //setup of the discount itself
            Calendar farEndTime = Calendar.getInstance();
            farEndTime.set(3000,8,12);
            setVisibleDiscount(productUnderDiscount);
            discount = setDiscount(50.0, farEndTime, "negative null basket " +
                    "not expired");
            productsToCheckAndApply = null;
            //verify there is  exception
            Throwable exception = Assertions.assertThrows(NullPointerException.class, () -> discount.isApprovedProducts(productsToCheckAndApply));
            Assertions.assertEquals(NullPointerException.class, exception.getClass());

        }

        @Test
        void undoDiscount() { //tested in TC name: applyUndo50AfterExpiredPositive
        }

        ///////////////setups////////////////////////////
        /**
         * set up for products to try to apply the discount on
         */
        private void setUpProductsToApply() {
            product1 = Product.builder()
                    .cost(10.0)
                    .originalCost(10)
                    .productSn(0)
                    .build();
            product2 = Product.builder()
                    .cost(20.0)
                    .originalCost(20)
                    .productSn(1)
                    .build();
        }
        /**
         * setup for creating visible discount with that holds:
         * @param productUnderDiscount the product that has visible discount
         */
        private void setVisibleDiscount(Product productUnderDiscount) {
            //create map of products under discount
            Map<Product, Integer> mapOfUnder = new HashMap<>();
            mapOfUnder.put(productUnderDiscount,amount);
            visibleDiscountUT = VisibleDiscount.builder()
                    .amountOfProductsForApplyDiscounts(mapOfUnder)
                    .build();
        }

        /**
         * set the discount with its parameters as received
         * @param percentage
         * @param farEndTime
         * @param description
         */
        private Discount setDiscount(double percentage,
                                     Calendar farEndTime,
                                     String description) {

            return Discount.builder()
                    .discountPercentage(percentage)
                    .description(description)
                    .endTime(farEndTime)
                    .discountId(0)
                    .discountType(DiscountType.VISIBLE)
                    .discountPolicy(visibleDiscountUT)
                    .build();
        }

    }
    ////////////////////////////////////////////////////////////////////////
    @Nested
    public class VisibleDiscountTestIntegration {

        @BeforeEach
        void setUp() {
            productUnderDiscount = Product.builder()
                    .productSn(0)
                    .cost(50.0)
                    .originalCost(50.0)
                    .build();

            setUpProductsToApply(); //set up products to apply

            productsToCheckAndApply = new HashMap<>();
        }




        /**
         * check applying discount on product that has discount
         * the discount params:
         * 1. end time not expired.
         * 2. discount percentage = 100%
         * 3. description = some descriptive description
         * success: the price of the product updated after discount.
         * other products price is not changed.
         */
        @Test
        void applyDiscountUnderDiscount100Positive() {
            //setup of the discount itself
            Calendar farEndTime = Calendar.getInstance();
            farEndTime.set(3000,12,12);
            setVisibleDiscount(productUnderDiscount);
            discount = setDiscount(100.0, farEndTime, "positive 100% not" +
                    " expired");
            productsToCheckAndApply.put(product1,amount);
            productsToCheckAndApply.put(product2,amount);
            discount.applyDiscount(productsToCheckAndApply);
            Assertions.assertEquals(product1.getCost(),0);
            Assertions.assertEquals(product2.getCost(),20.0);
            //verify original cost stays
            Assertions.assertEquals(product1.getOriginalCost(),10);
            Assertions.assertEquals(product2.getOriginalCost(),20);
        }

        /**
         * check applying discount on product that has discount
         * the discount params:
         * 1. end time not expired.
         * 2. discount percentage = 50%
         * 3. description = some descriptive description
         * success: the price of the product updated after discount.
         * other products price is not changed.
         */
        @Test
        void applyDiscountUnderDiscount50Positive() {
            //setup of the discount itself
            Calendar farEndTime = Calendar.getInstance();
            farEndTime.set(3000,12,12);
            setVisibleDiscount(productUnderDiscount);
            discount = setDiscount(50.0, farEndTime, "positive 50% not" +
                    " expired");
            productsToCheckAndApply.put(product1,amount);
            productsToCheckAndApply.put(product2,amount);
            discount.applyDiscount(productsToCheckAndApply);
            Assertions.assertEquals(product1.getCost(),5);
            Assertions.assertEquals(product2.getCost(),20.0);
            //verify original cost stays
            Assertions.assertEquals(product1.getOriginalCost(),10);
            Assertions.assertEquals(product2.getOriginalCost(),20);
        }

        /**
         * check applying discount on product that has discount and after expired, undo
         * the discount params:
         * success: the price of the product updated after discount and returned to the
         * previous after time of discount expired.
         * other products price is not changed.
         */
        @Test
        void applyUndo50AfterExpiredPositive() {
            //setup of the discount itself
            Calendar farEndTime = Calendar.getInstance();
            farEndTime.set(3000,12,12);
            setVisibleDiscount(productUnderDiscount);
            discount = setDiscount(50.0, farEndTime, "positive 50% not" +
                    " expired");
            productsToCheckAndApply.put(product1,amount);
            productsToCheckAndApply.put(product2,amount);
            //apply discount
            discount.applyDiscount(productsToCheckAndApply);
            Assertions.assertEquals(product1.getCost(),5);
            Assertions.assertEquals(product2.getCost(),20.0);
            //verify original cost stays
            Assertions.assertEquals(product1.getOriginalCost(),10);
            Assertions.assertEquals(product2.getOriginalCost(),20);
            Calendar expiredEndTime = Calendar.getInstance();
            expiredEndTime.set(1545,1,1);
            discount.setEndTime(expiredEndTime);

            //apply discount
            discount.applyDiscount(productsToCheckAndApply);
            Assertions.assertEquals(product1.getCost(),10);
            Assertions.assertEquals(product2.getCost(),20);
            Assertions.assertEquals(product1.getOriginalCost(),10);
            Assertions.assertEquals(product2.getOriginalCost(),20);
        }


        /**
         * check applying discount on product that has discount but expired
         * the discount params:
         * 1. end time expired.
         * 2. discount percentage = 50%
         * 3. description = some descriptive description
         * fail: the price of the product not updated after discount.
         * other products price is not changed.
         */
        @Test
        void applyDiscountExpiredNegative() {
            //setup of the discount itself
            Calendar farEndTime = Calendar.getInstance();
            farEndTime.set(1990,8,12);
            setVisibleDiscount(productUnderDiscount);
            discount = setDiscount(50.0, farEndTime, "negative 50% " +
                    " expired");
            productsToCheckAndApply.put(product1,amount);
            productsToCheckAndApply.put(product2,amount);
            discount.applyDiscount(productsToCheckAndApply);
            Assertions.assertEquals(product1.getCost(),10.0);
            Assertions.assertEquals(product2.getCost(),20.0);
            //verify original cost stays
            Assertions.assertEquals(product1.getOriginalCost(),10);
            Assertions.assertEquals(product2.getOriginalCost(),20);
        }

        /**
         * check applying discount on product that has discount but expired
         * the discount params:
         * 1. end time expired.
         * 2. discount percentage = -50%
         * 3. description = some descriptive description
         * fail: exception is thrown
         */
        @Test
        void applyDiscountInvalidDiscountNegative() {
            //setup of the discount itself
            Calendar farEndTime = Calendar.getInstance();
            farEndTime.set(3000,8,12);
            setVisibleDiscount(productUnderDiscount);
            discount = setDiscount(-50.0, farEndTime, "negative -50% " +
                    " expired");
            productsToCheckAndApply.put(product1,amount);
            productsToCheckAndApply.put(product2,amount);

            Throwable exception = Assertions.assertThrows(IllegalPercentageException.class, () -> discount.applyDiscount(productsToCheckAndApply));
            Assertions.assertEquals("got invalid percentage value: "+(-50.0)+" for discountId: "+discount.getDiscountId(), exception.getMessage());
        }

        /**
         * verify that when products list that has a product under discount
         * is received and discount is not expired:
         * success: returns true
         */
        @Test
        void isApprovedProductsPositive() {
            //setup of the discount itself
            Calendar farEndTime = Calendar.getInstance();
            farEndTime.set(3000,12,12);
            setVisibleDiscount(productUnderDiscount);
            discount = setDiscount(50.0, farEndTime, "positive 50% not" +
                    " expired");
            productsToCheckAndApply.put(product1,amount);   //has discount
            productsToCheckAndApply.put(product2,amount);
            Assertions.assertTrue(discount.isApprovedProducts(productsToCheckAndApply));
        }

        /**
         * verify that when products list that has no product under discount
         * is received and discount is not expired:
         * fail: returns false
         * (tells there is no need to apply discount on list)
         */
        @Test
        void isApprovedProductsNoUnderDiscountNegative() {
            //setup of the discount itself
            Calendar farEndTime = Calendar.getInstance();
            farEndTime.set(3000,12,12);
            setVisibleDiscount(productUnderDiscount);
            discount = setDiscount(50.0, farEndTime, "positive 50% not" +
                    " expired");
            productsToCheckAndApply.put(product2,amount);
            Assertions.assertFalse(discount.isApprovedProducts(productsToCheckAndApply));
        }

        /**
         * verify that when discount expired the term to apply rejected
         * fail: returns false
         * (tells there is no need to apply discount on list)
         */
        @Test
        void isApprovedProductsExpiredNegative() {
            //setup of the discount itself
            Calendar past = Calendar.getInstance();
            past.set(1925,12,12);
            setVisibleDiscount(productUnderDiscount);
            discount = setDiscount(50.0, past, "back to past" +
                    " expired");
            productsToCheckAndApply.put(product1,amount); //product under discount
            productsToCheckAndApply.put(product2,amount);
            Assertions.assertFalse(discount.isApprovedProducts(productsToCheckAndApply));
        }

        /////////////setups and assist methods///////////////////////////////
        /**
         * set up for products to try to apply the discount on
         */
        private void setUpProductsToApply() {
            product1 = Product.builder()
                    .cost(10.0)
                    .originalCost(10)
                    .productSn(0)
                    .build();
            product2 = Product.builder()
                    .cost(20.0)
                    .originalCost(20)
                    .productSn(1)
                    .build();
        }
        /**
         * setup for creating visible discount with that holds:
         * @param productUnderDiscount the product that has visible discount
         */
        private void setVisibleDiscount(Product productUnderDiscount) {
            //create map of products under discount
            Map<Product, Integer> mapOfUnder = new HashMap<>();
            mapOfUnder.put(productUnderDiscount,amount);
            visibleDiscountUT = VisibleDiscount.builder()
                    .amountOfProductsForApplyDiscounts(mapOfUnder)
                    .build();
        }
        /**
         * set the discount with its parameters as received
         * @param percentage
         * @param farEndTime
         * @param description
         */
        private Discount setDiscount(double percentage,
                                     Calendar farEndTime,
                                     String description) {

            return Discount.builder()
                    .discountPercentage(percentage)
                    .description(description)
                    .endTime(farEndTime)
                    .discountId(0)
                    .discountType(DiscountType.VISIBLE)
                    .discountPolicy(visibleDiscountUT)
                    .build();
        }
    }
}

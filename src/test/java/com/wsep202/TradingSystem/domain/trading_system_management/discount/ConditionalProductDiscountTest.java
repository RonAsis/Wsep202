package com.wsep202.TradingSystem.domain.trading_system_management.discount;

import com.wsep202.TradingSystem.domain.exception.ConditionalProductException;
import com.wsep202.TradingSystem.domain.exception.IllegalPercentageException;
import com.wsep202.TradingSystem.domain.trading_system_management.Product;
import org.junit.jupiter.api.*;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ConditionalProductDiscountTest {

    //the components under test:
    ConditionalProductDiscount productDiscountUT;  //unit under test
    Discount discount;//the discount details of the discount - percentage, end time, description.

    //the received products as parameters
    Map<Product,Integer> productsToCheckAndApply;

    //products to apply the discount on
    Product product1;
    Product product2;
    Product product3;

    @AfterEach
    void tearDown() {
    }

    @Nested
    public class ConditionalProductDiscountTestIntegration {

        @BeforeEach
        void setUp() {
            setUpProductsToApply(); //set up products to apply
            productsToCheckAndApply = new HashMap<>();
        }

        /**
         * test the positive setup:
         * scenario of TC: (100% conditional discount)
         * 2 nature valley -> get 1 free
         * 1 cliff bar -> get 2nd and 3rd free
         */
        @Test
        void applyDiscountPositive() {
            //create the setup for TC
            Calendar time = Calendar.getInstance();
            time.set(3000, 1, 1);
            HashMap<Product, Integer> productsToBag = new HashMap<>();
            //bag of products to apply the discount on
            productsToBag.put(product1, 3);
            productsToBag.put(product2, 4);
            productsToBag.put(product3,2);
            //create the condition for discount
            HashMap<Product,Integer> conditionMap = new HashMap<>();
            conditionMap.put(product1,2);
            conditionMap.put(product2,1);
            //create the post condition apply discount map
            HashMap<Product,Integer> postMap = new HashMap<>();
            postMap.put(product1,1);
            postMap.put(product2,2);
            setUpProductDiscount(time,100.0,productsToBag,conditionMap,
                    postMap,"applyDiscountPositive");
            //apply discount
            discount.applyDiscount(productsToCheckAndApply);
            //get the bag prices original and after discount
            double totalAfterDiscount = getTotalCost(productsToCheckAndApply);
            double totalOrigCost = getTotalOrigCost(productsToCheckAndApply);
            //success compare prices value of bag
            Assertions.assertEquals(2220, totalAfterDiscount);
            Assertions.assertEquals(2430, totalOrigCost);
        }



        /**
         * test the positive setup:
         * scenario of TC: (10% conditional discount)
         * 2 nature valley -> get 1 in 10%
         * 1 cliff bar -> get 2nd and 3rd in 10%
         */
        @Test
        void applyDiscount10pPositive() {
            //create the setup for TC
            Calendar time = Calendar.getInstance();
            time.set(3000, 1, 1);
            HashMap<Product, Integer> productsToBag = new HashMap<>();
            //bag of products to apply the discount on
            productsToBag.put(product1, 3);
            productsToBag.put(product2, 4);
            productsToBag.put(product3,2);
            //create the condition for discount
            HashMap<Product,Integer> conditionMap = new HashMap<>();
            conditionMap.put(product1,2);
            conditionMap.put(product2,1);
            //create the post condition apply discount map
            HashMap<Product,Integer> postMap = new HashMap<>();
            postMap.put(product1,1);
            postMap.put(product2,2);
            setUpProductDiscount(time,10.0,productsToBag,conditionMap,
                    postMap,"applyDiscountPositive");
            //apply discount
            discount.applyDiscount(productsToCheckAndApply);
            //get the bag prices original and after discount
            double totalAfterDiscount = getTotalCost(productsToCheckAndApply);
            double totalOrigCost = getTotalOrigCost(productsToCheckAndApply);
            //success compare prices value of bag
            Assertions.assertEquals(2409, totalAfterDiscount);
            Assertions.assertEquals(2430, totalOrigCost);
        }



        /**
         * test applying discount and undo when expired
         */
        @Test
        void applyDiscount10pANDUndoPositive() {
            //create the setup for TC
            Calendar time = Calendar.getInstance();
            time.set(3000, 1, 1);
            HashMap<Product, Integer> productsToBag = new HashMap<>();
            //bag of products to apply the discount on
            productsToBag.put(product1, 3);
            productsToBag.put(product2, 4);
            productsToBag.put(product3,2);
            //create the condition for discount
            HashMap<Product,Integer> conditionMap = new HashMap<>();
            conditionMap.put(product1,2);
            conditionMap.put(product2,1);
            //create the post condition apply discount map
            HashMap<Product,Integer> postMap = new HashMap<>();
            postMap.put(product1,1);
            postMap.put(product2,2);
            setUpProductDiscount(time,10.0,productsToBag,conditionMap,
                    postMap,"applyDiscountPositive");
            //apply discount
            discount.applyDiscount(productsToCheckAndApply);
            //get the bag prices original and after discount
            double totalAfterDiscount = getTotalCost(productsToCheckAndApply);
            double totalOrigCost = getTotalOrigCost(productsToCheckAndApply);
            //success compare prices value of bag
            Assertions.assertEquals(2409, totalAfterDiscount);
            Assertions.assertEquals(2430, totalOrigCost);
            time.set(1234,1,1);
            discount.setEndTime(time);

            //apply discount - in this case expired so undone
            discount.applyDiscount(productsToCheckAndApply);
            //get the bag prices original and after discount
            totalAfterDiscount = getTotalCost(productsToCheckAndApply);
            totalOrigCost = getTotalOrigCost(productsToCheckAndApply);
            //success compare prices value of bag - undone successfully
            Assertions.assertEquals(2430, totalAfterDiscount);
            Assertions.assertEquals(2430, totalOrigCost);
        }

        /**
         * test applying discount that expired
         * fail: didn't apply
         */
        @Test
        void applyDiscountExpiredNegative() {
            //create the setup for TC
            Calendar time = Calendar.getInstance();
            time.set(1234,1,1);
            HashMap<Product, Integer> productsToBag = new HashMap<>();
            //bag of products to apply the discount on
            productsToBag.put(product1, 3);
            productsToBag.put(product2, 4);
            productsToBag.put(product3,2);
            //create the condition for discount
            HashMap<Product,Integer> conditionMap = new HashMap<>();
            conditionMap.put(product1,2);
            conditionMap.put(product2,1);
            //create the post condition apply discount map
            HashMap<Product,Integer> postMap = new HashMap<>();
            postMap.put(product1,1);
            postMap.put(product2,2);
            setUpProductDiscount(time,10.0,productsToBag,conditionMap,
                    postMap,"applyDiscountPositive");
            //apply discount
            discount.applyDiscount(productsToCheckAndApply);
            //get the bag prices original and after discount
            double totalAfterDiscount = getTotalCost(productsToCheckAndApply);
            double totalOrigCost = getTotalOrigCost(productsToCheckAndApply);
            //fail compare prices value of bag - didn't apply
            Assertions.assertEquals(2430, totalAfterDiscount);
            Assertions.assertEquals(2430, totalOrigCost);
        }


        /**
         * verify didn't get the discount because of percentage illegal
         * fail: exception thrown
         */
        @Test
        void applyDiscountInvalidPercentageNegative() {
            //create the setup for TC
            Calendar time = Calendar.getInstance();
            time.set(1234,1,1);
            HashMap<Product, Integer> productsToBag = new HashMap<>();
            //bag of products to apply the discount on
            productsToBag.put(product1, 3);
            productsToBag.put(product2, 4);
            productsToBag.put(product3,2);
            //create the condition for discount
            HashMap<Product,Integer> conditionMap = new HashMap<>();
            conditionMap.put(product1,2);
            conditionMap.put(product2,1);
            //create the post condition apply discount map
            HashMap<Product,Integer> postMap = new HashMap<>();
            postMap.put(product1,1);
            postMap.put(product2,2);
            setUpProductDiscount(time,-10.0,productsToBag,conditionMap,
                    postMap,"applyDiscountPositive");

            //apply discount and get exception
            Throwable exception = Assertions.assertThrows(IllegalPercentageException.class, () -> discount.applyDiscount(productsToCheckAndApply));
            Assertions.assertEquals("got invalid percentage value: " + (-10.0) + " for discountId: " + discount.getDiscountId(), exception.getMessage());
        }


        /**
         * test apply on empty condition products
         * success: buy 2 and get the 3rd in 10% discount
         * condition accepted in empty manner
         */
        @Test
        void applyDiscountEmptyConditionPositive() {
            //create the setup for TC
            Calendar time = Calendar.getInstance();
            time.set(3000, 1, 1);
            HashMap<Product, Integer> productsToBag = new HashMap<>();
            //bag of products to apply the discount on
            productsToBag.put(product1, 3);
            //create the condition for discount
            HashMap<Product,Integer> conditionMap = new HashMap<>();

            //create the post condition apply discount map
            HashMap<Product,Integer> postMap = new HashMap<>();
            postMap.put(product1,1);

            setUpProductDiscount(time,10.0,productsToBag,conditionMap,
                    postMap,"applyDiscountPositive");
            //apply discount
            discount.applyDiscount(productsToCheckAndApply);
            //get the bag prices original and after discount
            double totalAfterDiscount = getTotalCost(productsToCheckAndApply);
            double totalOrigCost = getTotalOrigCost(productsToCheckAndApply);
            //success compare prices value of bag
            Assertions.assertEquals(29, totalAfterDiscount);
            Assertions.assertEquals(30, totalOrigCost);
        }


        /**
         * test empty applying amounts products
         * fail throw exception
         */
        @Test
        void applyDiscountEmptyPostNegative() {
            //create the setup for TC
            Calendar time = Calendar.getInstance();
            time.set(3000, 1, 1);
            HashMap<Product, Integer> productsToBag = new HashMap<>();
            //bag of products to apply the discount on
            productsToBag.put(product1, 3);
            productsToBag.put(product2, 4);
            productsToBag.put(product3,2);
            //create the condition for discount
            HashMap<Product,Integer> conditionMap = new HashMap<>();
            conditionMap.put(product1,2);
            conditionMap.put(product2,1);
            //create the post condition apply discount map
            HashMap<Product,Integer> postMap = new HashMap<>();

            setUpProductDiscount(time,10.0,productsToBag,conditionMap,
                    postMap,"applyDiscountPositive");
            //apply discount and get exception
            Throwable exception = Assertions.assertThrows(ConditionalProductException.class, () -> discount.applyDiscount(productsToCheckAndApply));
            Assertions.assertEquals("There are no products amounts to apply discount. check discountId: "+discount.getDiscountId(), exception.getMessage());
        }

        /**
         * test Null value condition products
         * fail throw exception
         */
        @Test
        void applyDiscountNullConditionNegative() {
            //create the setup for TC
            Calendar time = Calendar.getInstance();
            time.set(3000, 1, 1);
            HashMap<Product, Integer> productsToBag = new HashMap<>();
            //bag of products to apply the discount on
            productsToBag.put(product1, 3);
            productsToBag.put(product2, 4);
            productsToBag.put(product3,2);
            //create the condition for discount
            HashMap<Product,Integer> conditionMap = null;
            //create the post condition apply discount map
            HashMap<Product,Integer> postMap = new HashMap<>();
            postMap.put(product1,1);
            postMap.put(product2,2);
            setUpProductDiscount(time,10.0,productsToBag,conditionMap,
                    postMap,"applyDiscountPositive");
            //apply discount and get exception
            Throwable exception = Assertions.assertThrows(ConditionalProductException.class, () -> discount.applyDiscount(productsToCheckAndApply));
            Assertions.assertEquals("There are no products as condition. check discountId: "+discount.getDiscountId(), exception.getMessage());

        }


        /**
         * test null value applying amounts products
         * fail throw exception
         */
        @Test
        void applyDiscountNullPostNegative() {
            //create the setup for TC
            Calendar time = Calendar.getInstance();
            time.set(3000, 1, 1);
            HashMap<Product, Integer> productsToBag = new HashMap<>();
            //bag of products to apply the discount on
            productsToBag.put(product1, 3);
            productsToBag.put(product2, 4);
            productsToBag.put(product3,2);
            //create the condition for discount
            HashMap<Product,Integer> conditionMap = new HashMap<>();
            conditionMap.put(product1,2);
            conditionMap.put(product2,1);
            //create the post condition apply discount map
            HashMap<Product,Integer> postMap = null;

            setUpProductDiscount(time,10.0,productsToBag,conditionMap,
                    postMap,"applyDiscountPositive");
            //apply discount and get exception
            Throwable exception = Assertions.assertThrows(ConditionalProductException.class, () -> discount.applyDiscount(productsToCheckAndApply));
            Assertions.assertEquals("There are no products amounts to apply discount. check discountId: "+discount.getDiscountId(), exception.getMessage());
        }


        /**
         * test empty shopping bag products
         * fail to apply but not throwing exception
         */
        @Test
        void applyDiscountEmptyBagNegative() {
            //create the setup for TC
            Calendar time = Calendar.getInstance();
            time.set(3000, 1, 1);
            HashMap<Product, Integer> productsToBag = new HashMap<>();

            //create the condition for discount
            HashMap<Product,Integer> conditionMap = new HashMap<>();
            conditionMap.put(product1,2);
            conditionMap.put(product2,1);
            //create the post condition apply discount map
            HashMap<Product,Integer> postMap = new HashMap<>();
            postMap.put(product1,1);
            postMap.put(product2,2);
            setUpProductDiscount(time,10.0,productsToBag,conditionMap,
                    postMap,"applyDiscountPositive");
            //apply discount and dont get exception
            discount.applyDiscount(productsToCheckAndApply);
        }

        /**
         * test null value shopping bag products
         * fail to apply and throws exception
         */
        @Test
        void applyDiscountNullBagNegative() {
            //create the setup for TC
            Calendar time = Calendar.getInstance();
            time.set(3000, 1, 1);
            HashMap<Product, Integer> productsToBag = new HashMap<>();
            //create the condition for discount
            HashMap<Product,Integer> conditionMap = new HashMap<>();
            conditionMap.put(product1,2);
            conditionMap.put(product2,1);
            //create the post condition apply discount map
            HashMap<Product,Integer> postMap = new HashMap<>();
            postMap.put(product1,1);
            postMap.put(product2,2);
            setUpProductDiscount(time,10.0,productsToBag,conditionMap,
                    postMap,"applyDiscountPositive");
            productsToCheckAndApply = null;
            //apply discount and get exception
            Throwable exception = Assertions.assertThrows(NullPointerException.class, () -> discount.applyDiscount(productsToCheckAndApply));
            Assertions.assertEquals(NullPointerException.class, exception.getClass());
        }
///////////////////////////////////////isApproved

        /**
         * test the positive setup:
         * scenario of TC: (100% conditional discount)
         * 2 nature valley -> get 1 free
         * 1 cliff bar -> get 2nd and 3rd free
         * success: verifies the discount is approved
         */
        @Test
        void isApprovedPositive() {
            //create the setup for TC
            Calendar time = Calendar.getInstance();
            time.set(3000, 1, 1);
            HashMap<Product, Integer> productsToBag = new HashMap<>();
            //bag of products to apply the discount on
            productsToBag.put(product1, 3);
            productsToBag.put(product2, 4);
            productsToBag.put(product3,2);
            //create the condition for discount
            HashMap<Product,Integer> conditionMap = new HashMap<>();
            conditionMap.put(product1,2);
            conditionMap.put(product2,1);
            //create the post condition apply discount map
            HashMap<Product,Integer> postMap = new HashMap<>();
            postMap.put(product1,1);
            postMap.put(product2,2);
            setUpProductDiscount(time,100.0,productsToBag,conditionMap,
                    postMap,"applyDiscountPositive");
            //apply discount
            Assertions.assertTrue(discount.isApprovedProducts(productsToCheckAndApply));
        }

        /**
         * test the positive setup:
         * scenario of TC: (10% conditional discount)
         * 2 nature valley -> get 1 in 10%
         * 1 cliff bar -> get 2nd and 3rd in 10%
         * success: is approved
         */
        @Test
        void isApproved10pPositive() {
            //create the setup for TC
            Calendar time = Calendar.getInstance();
            time.set(3000, 1, 1);
            HashMap<Product, Integer> productsToBag = new HashMap<>();
            //bag of products to apply the discount on
            productsToBag.put(product1, 3);
            productsToBag.put(product2, 4);
            productsToBag.put(product3,2);
            //create the condition for discount
            HashMap<Product,Integer> conditionMap = new HashMap<>();
            conditionMap.put(product1,2);
            conditionMap.put(product2,1);
            //create the post condition apply discount map
            HashMap<Product,Integer> postMap = new HashMap<>();
            postMap.put(product1,1);
            postMap.put(product2,2);
            setUpProductDiscount(time,10.0,productsToBag,conditionMap,
                    postMap,"applyDiscountPositive");
            //apply discount
            Assertions.assertTrue(discount.isApprovedProducts(productsToCheckAndApply));

        }



        /**
         * test applying discount and undo when expired
         * i.e. first isApproved returns true and the second false
         */
        @Test
        void isApproved10pANDUndoPositive() {
            //create the setup for TC
            Calendar time = Calendar.getInstance();
            time.set(3000, 1, 1);
            HashMap<Product, Integer> productsToBag = new HashMap<>();
            //bag of products to apply the discount on
            productsToBag.put(product1, 3);
            productsToBag.put(product2, 4);
            productsToBag.put(product3,2);
            //create the condition for discount
            HashMap<Product,Integer> conditionMap = new HashMap<>();
            conditionMap.put(product1,2);
            conditionMap.put(product2,1);
            //create the post condition apply discount map
            HashMap<Product,Integer> postMap = new HashMap<>();
            postMap.put(product1,1);
            postMap.put(product2,2);
            setUpProductDiscount(time,10.0,productsToBag,conditionMap,
                    postMap,"applyDiscountPositive");
            //apply discount
            Assertions.assertTrue(discount.isApprovedProducts(productsToCheckAndApply));

            time.set(1234,1,1);
            discount.setEndTime(time);
            //apply discount - in this case expired so undone
            Assertions.assertFalse(discount.isApprovedProducts(productsToCheckAndApply));
        }




        /**
         * verify didn't approved because of percentage illegal
         * fail: exception thrown
         */
        @Test
        void isApprovedInvalidPercentageNegative() {
            //create the setup for TC
            Calendar time = Calendar.getInstance();
            time.set(1234,1,1);
            HashMap<Product, Integer> productsToBag = new HashMap<>();
            //bag of products to apply the discount on
            productsToBag.put(product1, 3);
            productsToBag.put(product2, 4);
            productsToBag.put(product3,2);
            //create the condition for discount
            HashMap<Product,Integer> conditionMap = new HashMap<>();
            conditionMap.put(product1,2);
            conditionMap.put(product2,1);
            //create the post condition apply discount map
            HashMap<Product,Integer> postMap = new HashMap<>();
            postMap.put(product1,1);
            postMap.put(product2,2);
            setUpProductDiscount(time,-10.0,productsToBag,conditionMap,
                    postMap,"applyDiscountPositive");

            //apply discount and get exception
            Throwable exception = Assertions.assertThrows(IllegalPercentageException.class, () -> discount.isApprovedProducts(productsToCheckAndApply));
            Assertions.assertEquals("got invalid percentage value: " + (-10.0) + " for discountId: " + discount.getDiscountId(), exception.getMessage());
        }


        /**
         * test is approved on empty condition products
         * success: apply discount on products
         */
        @Test
        void isApprovedEmptyConditionPositive() {
            //create the setup for TC
            Calendar time = Calendar.getInstance();
            time.set(3000, 1, 1);
            HashMap<Product, Integer> productsToBag = new HashMap<>();
            //bag of products to apply the discount on
            productsToBag.put(product1, 3);
            productsToBag.put(product2, 4);
            productsToBag.put(product3,2);
            //create the condition for discount
            HashMap<Product,Integer> conditionMap = new HashMap<>();

            //create the post condition apply discount map
            HashMap<Product,Integer> postMap = new HashMap<>();
            postMap.put(product1,1);
            postMap.put(product2,2);
            setUpProductDiscount(time,10.0,productsToBag,conditionMap,
                    postMap,"applyDiscountPositive");
            //apply discount
            Assertions.assertTrue(discount.isApprovedProducts(productsToCheckAndApply));
        }


        /**
         * test is approved with discount with empty applying amounts products
         * fail throw exception
         */
        @Test
        void isApprovedEmptyPostNegative() {
            //create the setup for TC
            Calendar time = Calendar.getInstance();
            time.set(3000, 1, 1);
            HashMap<Product, Integer> productsToBag = new HashMap<>();
            //bag of products to apply the discount on
            productsToBag.put(product1, 3);
            productsToBag.put(product2, 4);
            productsToBag.put(product3,2);
            //create the condition for discount
            HashMap<Product,Integer> conditionMap = new HashMap<>();
            conditionMap.put(product1,2);
            conditionMap.put(product2,1);
            //create the post condition apply discount map
            HashMap<Product,Integer> postMap = new HashMap<>();

            setUpProductDiscount(time,10.0,productsToBag,conditionMap,
                    postMap,"applyDiscountPositive");
            //apply discount and get exception
            Throwable exception = Assertions.assertThrows(ConditionalProductException.class, () -> discount.isApprovedProducts(productsToCheckAndApply));
            Assertions.assertEquals("There are no products amounts to apply discount. check discountId: "+discount.getDiscountId(), exception.getMessage());
        }





        /**
         * test Null value condition products when trying to approve discount
         * fail throw exception
         */
        @Test
        void isApprovedNullConditionNegative() {
            //create the setup for TC
            Calendar time = Calendar.getInstance();
            time.set(3000, 1, 1);
            HashMap<Product, Integer> productsToBag = new HashMap<>();
            //bag of products to apply the discount on
            productsToBag.put(product1, 3);
            productsToBag.put(product2, 4);
            productsToBag.put(product3,2);
            //create the condition for discount
            HashMap<Product,Integer> conditionMap = null;
            //create the post condition apply discount map
            HashMap<Product,Integer> postMap = new HashMap<>();
            postMap.put(product1,1);
            postMap.put(product2,2);
            setUpProductDiscount(time,10.0,productsToBag,conditionMap,
                    postMap,"applyDiscountPositive");
            //apply discount and get exception
            Throwable exception = Assertions.assertThrows(ConditionalProductException.class, () -> discount.isApprovedProducts(productsToCheckAndApply));
            Assertions.assertEquals("There are no products as condition. check discountId: "+discount.getDiscountId(), exception.getMessage());

        }


        /**
         * test null value applying amounts products when perform isApproved
         * fail throw exception
         */
        @Test
        void isApprovedNullPostNegative() {
            //create the setup for TC
            Calendar time = Calendar.getInstance();
            time.set(3000, 1, 1);
            HashMap<Product, Integer> productsToBag = new HashMap<>();
            //bag of products to apply the discount on
            productsToBag.put(product1, 3);
            productsToBag.put(product2, 4);
            productsToBag.put(product3,2);
            //create the condition for discount
            HashMap<Product,Integer> conditionMap = new HashMap<>();
            conditionMap.put(product1,2);
            conditionMap.put(product2,1);
            //create the post condition apply discount map
            HashMap<Product,Integer> postMap = null;

            setUpProductDiscount(time,10.0,productsToBag,conditionMap,
                    postMap,"applyDiscountPositive");
            //apply discount and get exception
            Throwable exception = Assertions.assertThrows(ConditionalProductException.class, () -> discount.isApprovedProducts(productsToCheckAndApply));
            Assertions.assertEquals("There are no products amounts to apply discount. check discountId: "+discount.getDiscountId(), exception.getMessage());
        }


        /**
         * test isApproved on empty shopping bag products
         * fail to apply but not throwing exception
         */
        @Test
        void isApprovedEmptyBagNegative() {
            //create the setup for TC
            Calendar time = Calendar.getInstance();
            time.set(3000, 1, 1);
            HashMap<Product, Integer> productsToBag = new HashMap<>();

            //create the condition for discount
            HashMap<Product,Integer> conditionMap = new HashMap<>();
            conditionMap.put(product1,2);
            conditionMap.put(product2,1);
            //create the post condition apply discount map
            HashMap<Product,Integer> postMap = new HashMap<>();
            postMap.put(product1,1);
            postMap.put(product2,2);
            setUpProductDiscount(time,10.0,productsToBag,conditionMap,
                    postMap,"applyDiscountPositive");
            //apply discount and dont get exception
            Assertions.assertFalse(discount.isApprovedProducts(productsToCheckAndApply));
        }

        /**
         * test null value shopping bag products when performing isApproved
         * fail to apply and throws exception
         */
        @Test
        void isApprovedNullBagNegative() {
            //create the setup for TC
            Calendar time = Calendar.getInstance();
            time.set(3000, 1, 1);
            HashMap<Product, Integer> productsToBag = new HashMap<>();
            //create the condition for discount
            HashMap<Product,Integer> conditionMap = new HashMap<>();
            conditionMap.put(product1,2);
            conditionMap.put(product2,1);
            //create the post condition apply discount map
            HashMap<Product,Integer> postMap = new HashMap<>();
            postMap.put(product1,1);
            postMap.put(product2,2);
            setUpProductDiscount(time,10.0,productsToBag,conditionMap,
                    postMap,"applyDiscountPositive");
            productsToCheckAndApply = null;
            //apply discount and get exception
            Throwable exception = Assertions.assertThrows(NullPointerException.class, () -> discount.isApprovedProducts(productsToCheckAndApply));
            Assertions.assertEquals(NullPointerException.class, exception.getClass());
        }


        ///////////setup and assistant products

        /**
         * get the total original cost of the products in bag
         */
        private double getTotalOrigCost(Map<Product, Integer> productsToCheckAndApply) {
            double total = 0;
            for (Product product : productsToCheckAndApply.keySet()) {
                total += product.getOriginalCost() * productsToCheckAndApply.get(product);
            }
            return total;
        }

        /**
         * get the total cost of the products in bag
         */
        private double getTotalCost(Map<Product, Integer> productsToCheckAndApply) {
            double total = 0;
            for (Product product : productsToCheckAndApply.keySet()) {
                total += product.getCost() * productsToCheckAndApply.get(product);
            }
            return total;
        }

        /**
         * set the discount with its parameters as received
         *
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
                    .discountType(DiscountType.CONDITIONAL_PRODUCT)
                    .discountPolicy(productDiscountUT)
                    .build();
        }

        /**
         * generic set product cond. discount setup method
         */
        private void setUpProductDiscount(Calendar endTime,
                                          double discPercentage,
                                          HashMap<Product, Integer> productsToBuy,
                                          Map<Product,Integer> productsUnderThisDiscount,
                                          Map<Product,Integer> amountOfProductsForApplyDiscounts,
                                          String description) {
            //setup of the discount itself
            setProductDiscount(productsUnderThisDiscount,amountOfProductsForApplyDiscounts);
            discount = setDiscount(discPercentage, endTime, description);
            productsToCheckAndApply.putAll(productsToBuy);
        }

        /**
         * setup the conditional product discount
         * @param productsUnderThisDiscount products terms to get the discount
         * @param amountOfProductsForApplyDiscounts the amounts to apply the discount on
         *                                          each product
         */
        private void setProductDiscount(Map<Product, Integer> productsUnderThisDiscount, Map<Product, Integer> amountOfProductsForApplyDiscounts) {
            productDiscountUT = ConditionalProductDiscount.builder()
                    .productsUnderThisDiscount(productsUnderThisDiscount)
                    .amountOfProductsForApplyDiscounts(amountOfProductsForApplyDiscounts)
                    .build();
        }

        /**
         * set up for products to try to apply the discount on
         */
        private void setUpProductsToApply() {
            product1 = Product.builder()
                    .name("nature valley")
                    .cost(10.0)
                    .originalCost(10)
                    .productSn(0)
                    .build();
            product2 = Product.builder()
                    .name("cliff bar")
                    .cost(100.0)
                    .originalCost(100)
                    .productSn(1)
                    .build();
            product3 = Product.builder()
                    .name("playstation")
                    .cost(1000.0)
                    .originalCost(1000)
                    .productSn(2)
                    .build();
        }

    }
}

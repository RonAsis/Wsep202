package com.wsep202.TradingSystem.domain.trading_system_management.discount;

import com.wsep202.TradingSystem.domain.exception.IllegalMinPriceException;
import com.wsep202.TradingSystem.domain.exception.IllegalPercentageException;
import com.wsep202.TradingSystem.domain.trading_system_management.Product;
import org.hibernate.validator.constraints.URL;
import org.junit.jupiter.api.*;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ConditionalStoreDiscountTest {
    //the components under test:
    ConditionalStoreDiscount storeDiscountUT;  //unit under test
    Discount discount;//the discount details of the discount - percentage, end time, description.

    //the received products as parameters
    Map<Product,Integer> productsToCheckAndApply;

    //products to apply the discount on
    Product product1;
    Product product2;

    @AfterEach
    void tearDown() {
    }


    @Nested
    public class ConditionalStoreDiscountTestIntegration {
        @BeforeEach
        void setUp() {
            setUpProductsToApply(); //set up products to apply
            productsToCheckAndApply = new HashMap<>();

        }

        /**
         * apply discount of 10% on the shopping bag price which stands in the
         * min value threshold.
         * (the discount isn't expired)
         * success: the cost of the bag decreased by 10%
         */
        @Test
        void applyDiscount200minGet10Positive() {
            Calendar time = Calendar.getInstance();
            time.set(3000, 1, 1);
            HashMap<Product, Integer> productsToBag = new HashMap<>();
            productsToBag.put(product1, 2);
            productsToBag.put(product2, 2);
            setUpStoreDiscount(time, 200, 10, productsToBag,
                    "applyDiscount200minGet10Positive");
            //apply discount
            discount.applyDiscount(productsToCheckAndApply);
            //get the bag prices original and after discount
            double totalAfterDiscount = getTotalCost(productsToCheckAndApply);
            double totalOrigCost = getTotalOrigCost(productsToCheckAndApply);
            //success compare prices value of bag
            Assertions.assertEquals(180, totalAfterDiscount);
            Assertions.assertEquals(200, totalOrigCost);
        }

        /**
         * apply discount when valid and undo when expired
         * apply discount of 10% on the shopping bag price which stands in the
         * min value threshold.
         * (the discount isn't expired)
         * success: the cost of the bag decreased by 10%
         * set discount as expired
         * success: the cost of the bag increased by what increased
         */
        @Test
        void applyDiscount200minGet10ThenUndoPositive() {
            Calendar time = Calendar.getInstance();
            time.set(3000, 1, 1);
            HashMap<Product, Integer> productsToBag = new HashMap<>();
            productsToBag.put(product1, 2);
            productsToBag.put(product2, 2);
            setUpStoreDiscount(time, 200, 10, productsToBag,
                    "applyDiscount200minGet10ThenUndoPositive");
            //apply discount
            discount.applyDiscount(productsToCheckAndApply);
            //get the bag prices original and after discount
            double totalAfterDiscount = getTotalCost(productsToCheckAndApply);
            double totalOrigCost = getTotalOrigCost(productsToCheckAndApply);
            //success compare prices value of bag
            Assertions.assertEquals(180, totalAfterDiscount);
            Assertions.assertEquals(200, totalOrigCost);
            time.set(1234, 1, 1); //set time of discount as expired
            discount.setEndTime(time);
            //verify the price returned to its previous
            //apply discount
            discount.applyDiscount(productsToCheckAndApply);
            //get the bag prices original and after discount
            totalAfterDiscount = getTotalCost(productsToCheckAndApply);
            totalOrigCost = getTotalOrigCost(productsToCheckAndApply);
            //success compare prices value of bag
            Assertions.assertEquals(200, totalAfterDiscount);
            Assertions.assertEquals(200, totalOrigCost);
        }

        /**
         * The received bag's cost doesnt stands in terms for discount
         * 199 < 200 (minPrice mi threshold)
         */
        @Test
        void applyDiscount200minTotal199Negative() {
            Calendar time = Calendar.getInstance();
            time.set(3000, 1, 1); //not expired
            HashMap<Product, Integer> productsToBag = new HashMap<>();
            setupCustomTwoProductsToApply(99, 100);
            productsToBag.put(product1, 1);
            productsToBag.put(product2, 1);
            setUpStoreDiscount(time, 200, 10, productsToBag,
                    "applyDiscount200minTotal199Negative");
            //apply discount
            discount.applyDiscount(productsToCheckAndApply);
            //get the bag prices original and after discount
            double totalAfterDiscount = getTotalCost(productsToCheckAndApply);
            double totalOrigCost = getTotalOrigCost(productsToCheckAndApply);
            //fail compare prices value of bag - same prices stays
            Assertions.assertEquals(199, totalAfterDiscount);
            Assertions.assertEquals(199, totalOrigCost);
        }

        /**
         * verify didn't get the discount because expiration
         * fail: discount didn't apply
         */
        @Test
        void applyDiscountExpiredNegative() {
            Calendar time = Calendar.getInstance();
            time.set(1234, 1, 1); //not expired
            HashMap<Product, Integer> productsToBag = new HashMap<>();
            setupCustomTwoProductsToApply(200, 100); //300 >200 but expired
            productsToBag.put(product1, 1);
            productsToBag.put(product2, 1);
            setUpStoreDiscount(time, 200, 10, productsToBag,
                    "applyDiscountExpiredNegative");
            //apply discount
            discount.applyDiscount(productsToCheckAndApply);
            //get the bag prices original and after discount
            double totalAfterDiscount = getTotalCost(productsToCheckAndApply);
            double totalOrigCost = getTotalOrigCost(productsToCheckAndApply);
            //fail compare prices value of bag - same prices stays
            Assertions.assertEquals(300, totalAfterDiscount);
            Assertions.assertEquals(300, totalOrigCost);
        }

        /**
         * verify didn't get the discount because of percentage illegal
         * fail: exception thrown
         */
        @Test
        void applyDiscountInvalidPercentageNegative() {
            Calendar time = Calendar.getInstance();
            time.set(3000, 1, 1); //not expired
            HashMap<Product, Integer> productsToBag = new HashMap<>();
            setupCustomTwoProductsToApply(200, 100); //300 >200 but expired
            productsToBag.put(product1, 1);
            productsToBag.put(product2, 1);
            setUpStoreDiscount(time, 200, -5, productsToBag,
                    "applyDiscountExpiredNegative");
            //apply discount and get exception
            Throwable exception = Assertions.assertThrows(IllegalPercentageException.class, () -> discount.applyDiscount(productsToCheckAndApply));
            Assertions.assertEquals("got invalid percentage value: " + (-5.0) + " for discountId: " + discount.getDiscountId(), exception.getMessage());
        }


        /**
         * verify didn't get the discount because of minPrice illegal
         * fail: exception thrown
         */
        @Test
        void applyDiscountInvalidMinPriceNegative() {
            Calendar time = Calendar.getInstance();
            time.set(3000,1,1); //not expired
            HashMap<Product,Integer> productsToBag = new HashMap<>();
            setupCustomTwoProductsToApply(200,100); //300 >200 but expired
            productsToBag.put(product1,1);
            productsToBag.put(product2,1);
            setUpStoreDiscount(time,-10,20,productsToBag,
                    "applyDiscountExpiredNegative");
            //apply discount and get exception
            Throwable exception = Assertions.assertThrows(IllegalMinPriceException.class, () -> discount.applyDiscount(productsToCheckAndApply));
            Assertions.assertEquals("got invalid minPrice value: "+(-10.0)+" for discountId: "+discount.getDiscountId(), exception.getMessage());
        }

        /**
         * check applying discount on empty bag
         * fail: discount didn't apply but exception is not thrown
         */
        @Test
        void applyDiscountEmptyProductsBagNegative() {
            Calendar time = Calendar.getInstance();
            time.set(3000,1,1); //not expired
            HashMap<Product,Integer> productsToBag = new HashMap<>();
            setUpStoreDiscount(time,10,20,productsToBag,
                    "applyDiscountEmptyProductsBagNegative");
            //apply discount and exception hasn't been thrown
            discount.applyDiscount(productsToCheckAndApply);
        }

        /**
         * check applying discount on null value bag
         * fail: discount didn't apply and exception is thrown
         */
        @Test
        void applyDiscountNullProductsBagNegative() {
            Calendar time = Calendar.getInstance();
            time.set(3000,1,1); //not expired
            HashMap<Product,Integer> productsToBag = new HashMap<>();
            setUpStoreDiscount(time,10,20,productsToBag,
                    "applyDiscountEmptyProductsBagNegative");
            productsToCheckAndApply = null;
            //verify there is  exception
            Throwable exception = Assertions.assertThrows(NullPointerException.class, () -> discount.applyDiscount(productsToCheckAndApply));
            Assertions.assertEquals(NullPointerException.class, exception.getClass());
        }
///////////////////////////////////isApproved
        /**
         * check if need to apply discount of 10% on the shopping bag price which stands
         * in the min value threshold.
         * (the discount isn't expired)
         * success: return true
         */
        @Test
        void isApproved200minGet10Positive() {
            //prepare values for discount
            Calendar time = Calendar.getInstance();
            time.set(3000, 1, 1);
            HashMap<Product, Integer> productsToBag = new HashMap<>();
            productsToBag.put(product1, 2);
            productsToBag.put(product2, 2);
            //set discount
            setUpStoreDiscount(time, 200, 10, productsToBag,
                    "applyDiscount200minGet10Positive");
            //success: isApproved
            Assertions.assertTrue(discount.isApprovedProducts(productsToCheckAndApply));

        }

        /**
         * verify isApproved and after expired is not approving the discount
         * anymore on products
         */
        @Test
        void isApprovedNotExpiredThenExpiredSoNotApprovePositive() {
            Calendar time = Calendar.getInstance();
            time.set(3000, 1, 1);
            HashMap<Product, Integer> productsToBag = new HashMap<>();
            productsToBag.put(product1, 2);
            productsToBag.put(product2, 2);
            setUpStoreDiscount(time, 200, 10, productsToBag,
                    "applyDiscount200minGet10ThenUndoPositive");
            //success: isApproved
            Assertions.assertTrue(discount.isApprovedProducts(productsToCheckAndApply));
            //now discount expired
            time.set(1234, 1, 1); //set time of discount as expired
            discount.setEndTime(time);
            //fail: !isApproved
            Assertions.assertFalse(discount.isApprovedProducts(productsToCheckAndApply));

        }

        /**
         * The received bag's cost doesnt stands in terms for discount
         * 199 < 200 (minPrice min threshold) so verify is not approving
         */
        @Test
        void isApproved200minTotal199Negative() {
            Calendar time = Calendar.getInstance();
            time.set(3000, 1, 1); //not expired
            HashMap<Product, Integer> productsToBag = new HashMap<>();
            setupCustomTwoProductsToApply(99, 100);
            productsToBag.put(product1, 1);
            productsToBag.put(product2, 1);
            setUpStoreDiscount(time, 200, 10, productsToBag,
                    "applyDiscount200minTotal199Negative");
            //fail : bag price below minimum
            Assertions.assertFalse(discount.isApprovedProducts(productsToCheckAndApply));

        }



        /**
         * verify didn't approve the discount because of percentage illegal
         * fail: exception thrown
         */
        @Test
        void isApprovedInvalidPercentageNegative() {
            Calendar time = Calendar.getInstance();
            time.set(3000, 1, 1); //not expired
            HashMap<Product, Integer> productsToBag = new HashMap<>();
            setupCustomTwoProductsToApply(200, 100); //300 >200 but expired
            productsToBag.put(product1, 1);
            productsToBag.put(product2, 1);
            setUpStoreDiscount(time, 200, -5, productsToBag,
                    "isApprovedInvalidPercentageNegative");
            //apply discount and get exception
            Throwable exception = Assertions.assertThrows(IllegalPercentageException.class, () -> discount.isApprovedProducts(productsToCheckAndApply));
            Assertions.assertEquals("got invalid percentage value: " + (-5.0) + " for discountId: " + discount.getDiscountId(), exception.getMessage());
        }


        /**
         * verify didn't approve the discount because of minPrice illegal
         * fail: exception thrown
         */
        @Test
        void isApprovedInvalidMinPriceNegative() {
            Calendar time = Calendar.getInstance();
            time.set(3000,1,1); //not expired
            HashMap<Product,Integer> productsToBag = new HashMap<>();
            setupCustomTwoProductsToApply(200,100); //300 >200 but expired
            productsToBag.put(product1,1);
            productsToBag.put(product2,1);
            setUpStoreDiscount(time,-10,20,productsToBag,
                    "applyDiscountExpiredNegative");
            //apply discount and get exception
            Throwable exception = Assertions.assertThrows(IllegalMinPriceException.class, () -> discount.isApprovedProducts(productsToCheckAndApply));
            Assertions.assertEquals("got invalid minPrice value: "+(-10.0)+" for discountId: "+discount.getDiscountId(), exception.getMessage());
        }

        /**
         * check not approving empty bag
         * fail: discount didn't approved but exception is not thrown
         */
        @Test
        void isApproveEmptyProductsBagNegative() {
            Calendar time = Calendar.getInstance();
            time.set(3000,1,1); //not expired
            HashMap<Product,Integer> productsToBag = new HashMap<>();
            setUpStoreDiscount(time,10,20,productsToBag,
                    "applyDiscountEmptyProductsBagNegative");
            //apply discount and exception hasn't been thrown
            discount.isApprovedProducts(productsToCheckAndApply);
        }

        /**
         * check approving discount on null value bag
         * fail: discount didn't apply and exception is thrown
         */
        @Test
        void isApprovedNullProductsBagNegative() {
            Calendar time = Calendar.getInstance();
            time.set(3000,1,1); //not expired
            HashMap<Product,Integer> productsToBag = new HashMap<>();
            setUpStoreDiscount(time,10,20,productsToBag,
                    "applyDiscountEmptyProductsBagNegative");
            productsToCheckAndApply = null;
            //verify there is  exception
            Throwable exception = Assertions.assertThrows(NullPointerException.class, () -> discount.isApprovedProducts(productsToCheckAndApply));
            Assertions.assertEquals(NullPointerException.class, exception.getClass());
        }

        @Test
        void undoDiscount() {//tested under the applied test case name: applyDiscount200minGet10ThenUndoPositive
        }
        ////////////////////////setup and assistant methods////////////////////////////////

        /**
         * set two products to apply with custom price
         */
        private void setupCustomTwoProductsToApply(
                double price1,
                int price2) {
            product1 = Product.builder()
                    .cost(price1)
                    .originalCost(price1)
                    .productSn(0)
                    .build();
            product2 = Product.builder()
                    .cost(price2)
                    .originalCost(price2)
                    .productSn(1)
                    .build();

        }

        /**
         * set up for products to try to apply the discount on
         */
        private void setUpProductsToApply() {
            product1 = Product.builder()
                    .cost(50.0)
                    .originalCost(50)
                    .productSn(0)
                    .build();
            product2 = Product.builder()
                    .cost(50.0)
                    .originalCost(50)
                    .productSn(1)
                    .build();
        }

        /**
         * setup for creating store discount that holds:
         *
         * @param minPrice as term to get the value of the discount
         */
        private void setStoreDiscount(double minPrice) {
            //create map of products under discount
            storeDiscountUT = ConditionalStoreDiscount.builder()
                    .minPrice(minPrice)
                    .build();
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
                    .discountType(DiscountType.CONDITIONAL_STORE)
                    .discountPolicy(storeDiscountUT)
                    .build();
        }

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
         * generic set store discount setup method
         */
        private void setUpStoreDiscount(Calendar endTime, double minPrice,
                                        double discPercentage,
                                        HashMap<Product, Integer> productsToBuy,
                                        String description) {
            //setup of the discount itself
            setStoreDiscount(minPrice);  //set minimum of 200 bag cost purchase to stand in discount
            discount = setDiscount(discPercentage, endTime, description);
            productsToCheckAndApply.putAll(productsToBuy);
        }
    }


}

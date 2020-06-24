package com.wsep202.TradingSystem.domain.trading_system_management.discount;

import com.wsep202.TradingSystem.domain.exception.CompositeOperatorNullException;
import com.wsep202.TradingSystem.domain.exception.ConditionalProductException;
import com.wsep202.TradingSystem.domain.trading_system_management.Product;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ComposedDiscountTest {

    /////////////visible discount attributes/////////////
    Integer amount = 0; //doesn't relevant for logic of this type of discount- only used
    VisibleDiscount visibleDiscountUT;  //unit under test
    Product productUnderDiscount;   //product has discount
    //    //products to apply the discount on
//    Product product1vis;
//    Product product2vis;
    Calendar endTimeVis;
    Discount discountGenVis;//the discount details of the discount - percentage, end time, description.
    double percentageVis =10.0;

    ////////////conditional store discount///////////////
    ConditionalStoreDiscount storeDiscountUT;  //unit under test
    Discount discountGenStore;//the discount details of the discount - percentage, end time, description.
    Calendar endTimeStore;
    double percentageStore=50.0;
    double minPrice = 200.0;

    ////////////conditional product discount/////////////
    ConditionalProductDiscount productDiscountUT;  //unit under test
    Discount discountGenProduct;//the discount details of the discount - percentage, end time, description.
    Calendar endTimeProduct;
    double percentageProduct = 100.0;
    Map<Product,Integer> conditionMap;
    Map<Product,Integer> postMap;
    //products ass apply and terms the discount on
    Product product1;
    Product product2;
    Product product3;

    /////////////composed discount/////////////////////
    ComposedDiscount composedDiscountUT;
    Discount discountGenComposed;
    double percentageComposed = 100;
    Calendar endTimeComposed;
    HashMap<Product,Integer> postComposedMap;
    List<Discount> childDiscounts;
    /////////////generic discount attributes//////////////////
    Map<Product,Integer> productsToCheckAndApply;   //the received products as parameters




    @AfterEach
    void tearDown() {
    }

    @Nested
    public class VisibleDiscountTestIntegration {
        @BeforeEach
        void setUp() {
            //setUp visible discount
            setUpProductsUnderDiscountVis();    //init product under visible discount
            endTimeVis = Calendar.getInstance();  //set valid time for discount
            endTimeVis.set(3000,1,1);
            setVisibleDiscount(productUnderDiscount);   //create the instance of the visible discount
            setGenericDiscountVisible(percentageVis,
                    endTimeVis,"discount of 10% on cottage till 3000!");

            //setup conditional store discount
            endTimeStore = Calendar.getInstance();
            endTimeStore.set(3000,1,1);
            setUpStoreDiscount(endTimeStore, minPrice,percentageStore,"buy in 200 ILS in the store," +
                    "and get 50% discount on the shopping bag!");

            //setup conditional product discount
            setUpProductsToApply();
            endTimeProduct = Calendar.getInstance();    //valid expiration date
            endTimeProduct.set(3000,1,1);
            conditionMap = new HashMap<>(); //products terms table
            conditionMap.put(product1,2);
            conditionMap.put(product2,1);
            postMap = new HashMap<>(); //amount of products to apply discount
            postMap.put(product1,1);
            postMap.put(product2,2);
            setUpProductDiscount(endTimeProduct,percentageProduct,conditionMap,postMap,"buy " +
                    "2 nature valley and 1 cliff bar and get 1 nv free and 2 cb free!");

            //setup composite discount that includes the above "simple" discounts
            endTimeComposed =Calendar.getInstance();        //valid endtime for discount
            endTimeComposed.set(3000,1,1);
            childDiscounts = new ArrayList<>();  //make it composed of simple discounts
            childDiscounts.add(discountGenStore);
            childDiscounts.add(discountGenProduct);
            childDiscounts.add(discountGenVis);
            //create the post of the condition post map
            postComposedMap = new HashMap<>();
            //postComposedMap.put(product1,1);
            //postComposedMap.put(product2,2);

            setUpComposedDiscount(endTimeComposed,percentageComposed,childDiscounts,postComposedMap,"" +
                    "this is composed discount");
            productsToCheckAndApply = new HashMap<>();  //set the products to buy list -var
        }

////////////////////////note!! the changing of products price tested inside the simple discounts
        /////////////first tests without postAplying map POSITIVE (for kefel mivtzaim etc)////////////

        /**
         * apply the setup composed discount with OR operator
         * success: at least one of the discounts applied
         */
        @Test
        void applyORDiscountPositive() {
            //create products to check and apply
            productsToCheckAndApply.put(productUnderDiscount,5);
            // set operator OR
            composedDiscountUT.setCompositeOperator(CompositeOperator.OR);
            discountGenComposed.applyDiscount(productsToCheckAndApply);
            //verify visible applied on product under discount (at least one discount applied -the first)
            Assertions.assertTrue(discountGenVis.isApplied());
            Assertions.assertEquals(9,productUnderDiscount.getCost());
            Assertions.assertEquals(10,productUnderDiscount.getOriginalCost());
        }
        /**
         * apply the setup composed discount with AND operator
         * success: all discounts applied
         */
        @Test
        void applyANDDiscountPositive() {
            //create products to check and apply - for all discounts to approve
            productsToCheckAndApply.put(productUnderDiscount,5);
            productsToCheckAndApply.put(product1,3);
            productsToCheckAndApply.put(product2,4);
            productsToCheckAndApply.put(product3,2);
            // set operator AND
            composedDiscountUT.setCompositeOperator(CompositeOperator.AND);
            discountGenComposed.applyDiscount(productsToCheckAndApply);
            //verify all discounts applied
            Assertions.assertTrue(discountGenVis.isApplied());
            Assertions.assertTrue(discountGenProduct.isApplied());
            Assertions.assertTrue(discountGenStore.isApplied());
            //50% discount on store applied before abd then the 10% visible
            Assertions.assertEquals(4.5,productUnderDiscount.getCost());
            Assertions.assertEquals(10,productUnderDiscount.getOriginalCost());
            double totalAfterDiscounts = getTotalCost(productsToCheckAndApply);
            double totalOrigCost = getTotalOrigCost(productsToCheckAndApply);
            //success compare prices value of bag - all discounts applied as expected
            Assertions.assertEquals(1132.5, totalAfterDiscounts);
            Assertions.assertEquals(2480, totalOrigCost);
        }

        /**
         * apply the setup composed discount with XOR operator
         * success: only odd amount of products are applied
         */
        @Test
        void applyXORDiscountPositive() {
            //create products to check and apply
            productsToCheckAndApply.put(productUnderDiscount,5);
            productsToCheckAndApply.put(product1,3);
            productsToCheckAndApply.put(product2,4);
            productsToCheckAndApply.put(product3,2);
            // set operator XOR
            composedDiscountUT.setCompositeOperator(CompositeOperator.XOR);
            discountGenComposed.applyDiscount(productsToCheckAndApply);
            //verify only odd amount of discounts applied
            int appliedAmount = 0;
            if(discountGenVis.isApplied())
                appliedAmount++;
            if(discountGenProduct.isApplied())
                appliedAmount++;
            if(discountGenStore.isApplied())
                appliedAmount++;
            Assertions.assertTrue(appliedAmount%2!=0);
        }

        /////////////////////////////////negative

        /**
         * apply the setup composed discount with OR operator
         * fail: none of the discount should not apply
         */
        @Test
        void applyORDiscountNegative() {
            //create products to check and apply
            Product product = Product.builder() //the product doesn't stands in terms of any discount in composed
                    .productSn(10)
                    .cost(45)
                    .originalCost(45)
                    .build();
            productsToCheckAndApply.put(product,1);
            // set operator OR
            composedDiscountUT.setCompositeOperator(CompositeOperator.OR);
            discountGenComposed.applyDiscount(productsToCheckAndApply);
            //verify only visible applied on product under discount

            Assertions.assertEquals(45,product.getCost());
            Assertions.assertEquals(45,product.getOriginalCost());
        }
        /**
         * apply the setup composed discount with AND operator
         * fail: one of the discounts of the composed not apply
         * tried to apply all discounts and failed in visible
         */
        @Test
        void applyANDDiscountNegative() {
            //create products to check and apply
            Product product = Product.builder() //the product doesn't stands in terms of any discount in composed
                    .productSn(10)
                    .cost(45)
                    .originalCost(45)
                    .build();
            productsToCheckAndApply.put(product,5);
            // set operator AND
            composedDiscountUT.setCompositeOperator(CompositeOperator.AND);
            discountGenComposed.applyDiscount(productsToCheckAndApply);
            //verify only visible applied on product under discount
            Assertions.assertFalse(discountGenVis.isApplied());
            Assertions.assertTrue(discountGenProduct.isApplied());
            Assertions.assertTrue(discountGenStore.isApplied());
            Assertions.assertEquals(22.5,product.getCost());
            Assertions.assertEquals(45,product.getOriginalCost());
        }

        /**
         * apply the setup composed discount with XOR operator
         * success: only odd amount of products are applied
         * two are approved => only one should be applied:
         * the store discount or the conditional product
         */
        @Test
        void applyXORDiscountNegative() {
            //create products to check and apply
            productsToCheckAndApply.put(product1,3);
            productsToCheckAndApply.put(product2,4);
            productsToCheckAndApply.put(product3,2);
            // set operator XOR
            composedDiscountUT.setCompositeOperator(CompositeOperator.XOR);
            discountGenComposed.applyDiscount(productsToCheckAndApply);
            //verify only odd amount of discounts applied
            int appliedAmount = 0;
            if(discountGenVis.isApplied())
                appliedAmount++;
            if(discountGenProduct.isApplied())
                appliedAmount++;
            if(discountGenStore.isApplied())
                appliedAmount++;

            Assertions.assertTrue(appliedAmount%2!=0);
        }

        /**
         * verify an error raised as exception throwing in case
         * the composite operator is null
         */
        @Test
        void applyDiscountNullCompositeOperator(){
            productsToCheckAndApply.put(product1,1);
            Throwable exception = Assertions.assertThrows(CompositeOperatorNullException.class, () -> discountGenComposed.applyDiscount(productsToCheckAndApply));
            Assertions.assertEquals("The discount with id: 4 activated but it doesn't include operator!", exception.getMessage());
        }



        /**
         * verify exception isn't thrown
         * and discounts were applied (checked)
         */
        @Test
        void applyDiscountOREmptyProductsNegative(){
            composedDiscountUT.setCompositeOperator(CompositeOperator.OR);
            discountGenComposed.applyDiscount(productsToCheckAndApply);
            Assertions.assertTrue(discountGenProduct.isApplied());
            Assertions.assertTrue(discountGenStore.isApplied());
            Assertions.assertTrue(discountGenVis.isApplied());

        }

        /**
         * verify null pointer exception is raised in case the products are null value
         */
        @Test
        void applyDiscountORENullProductsNegative(){
            composedDiscountUT.setCompositeOperator(CompositeOperator.OR);
            productsToCheckAndApply = null;
            Throwable exception = Assertions.assertThrows(NullPointerException.class, () -> discountGenComposed.applyDiscount(productsToCheckAndApply));
            Assertions.assertEquals(NullPointerException.class,exception.getClass());
        }


        /**
         * verify exception isn't thrown
         * and discounts were not applied
         */
        @Test
        void applyDiscountANDEmptyProductsNegative(){
            composedDiscountUT.setCompositeOperator(CompositeOperator.AND);
            discountGenComposed.applyDiscount(productsToCheckAndApply);
        }

        /**
         * verify null pointer exception is raised in case the products are null value
         */
        @Test
        void applyDiscountANDENullProductsNegative(){
            composedDiscountUT.setCompositeOperator(CompositeOperator.AND);
            productsToCheckAndApply = null;
            Throwable exception = Assertions.assertThrows(NullPointerException.class, () -> discountGenComposed.applyDiscount(productsToCheckAndApply));
            Assertions.assertEquals(NullPointerException.class,exception.getClass());
        }


        /**
         * verify exception isn't thrown
         * and discounts were applied (checked)
         */
        @Test
        void applyDiscountXOREmptyProductsNegative(){
            composedDiscountUT.setCompositeOperator(CompositeOperator.XOR);
            discountGenComposed.applyDiscount(productsToCheckAndApply);
            Assertions.assertTrue(discountGenProduct.isApplied());
            Assertions.assertTrue(discountGenStore.isApplied());
            Assertions.assertTrue(discountGenVis.isApplied());

        }

        /**
         * verify null pointer exception is raised in case the products are null value
         */
        @Test
        void applyDiscountXORENullProductsNegative(){
            composedDiscountUT.setCompositeOperator(CompositeOperator.XOR);
            productsToCheckAndApply = null;
            Throwable exception = Assertions.assertThrows(NullPointerException.class, () -> discountGenComposed.applyDiscount(productsToCheckAndApply));
            Assertions.assertEquals(NullPointerException.class,exception.getClass());
        }

/////////////second: tests with postApplying map POSITIVE ////////////

        /**
         * apply OR composed discount with post and pre condition products maps
         * success: the discount applied on the products because at least one discount term met (visible)
         * buy 2 units from product1 and get the third free (100% discount)
         *
         */
        @Test
        void applyDiscountORPrePostPositive(){
            //create products to check and apply
            productsToCheckAndApply.put(product1,3);
            productsToCheckAndApply.put(productUnderDiscount,5);//this one will approve the visible discount
            // set operator OR
            composedDiscountUT.setCompositeOperator(CompositeOperator.OR);
            //set condition products map
            Map<Product,Integer> conditionMap = new HashMap<>();
            conditionMap.put(product1,2);
            //set post condition product map
            Map<Product,Integer> postMap = new HashMap<>();
            postMap.put(product1,1);
            composedDiscountUT.setProductsUnderThisDiscount(conditionMap);
            composedDiscountUT.setAmountOfProductsForApplyDiscounts(postMap);
            discountGenComposed.applyDiscount(productsToCheckAndApply);
            //verify the discount applied as conditional product. check total bag cost
            Assertions.assertEquals(20,product1.getCost()*3);
            Assertions.assertEquals(30,product1.getOriginalCost()*3);
        }

        /**
         * apply AND composed discount with post condition products maps
         * success: the discount applied on the products
         * buy 2 units from product1 and get the third free (100% discount)
         */
        @Test
        void applyDiscountANDPostPositive(){
            //create products to check and apply - for all discounts to approve
            productsToCheckAndApply.put(productUnderDiscount,5);
            productsToCheckAndApply.put(product1,3);
            productsToCheckAndApply.put(product2,4);
            productsToCheckAndApply.put(product3,2);
            // set operator AND
            composedDiscountUT.setCompositeOperator(CompositeOperator.AND);
            Map<Product,Integer> conditionMap = new HashMap<>();

            //set post condition product map
            Map<Product,Integer> postMap = new HashMap<>();
            postMap.put(product1,1);
            composedDiscountUT.setProductsUnderThisDiscount(conditionMap);
            composedDiscountUT.setAmountOfProductsForApplyDiscounts(postMap);
            discountGenComposed.applyDiscount(productsToCheckAndApply);
            //verify all discounts didn't applied taken in count only in isApproved
            Assertions.assertFalse(discountGenVis.isApplied());
            Assertions.assertFalse(discountGenProduct.isApplied());
            Assertions.assertFalse(discountGenStore.isApplied());
            //verify discount applied on the product in the post condition map only
            Assertions.assertEquals(20,product1.getCost()*3);
            Assertions.assertEquals(30,product1.getOriginalCost()*3);
        }

        /**
         * apply XOR composed discount with post condition products maps
         * success: the discount applied on the products because odd amount of discounts term met (1-visible)
         * buy 2 units from product1 and get the third free (100% discount)
         *
         */
        @Test
        void applyDiscountXORPostPositive(){
            //create products to check and apply
            productsToCheckAndApply.put(product1,3);
            productsToCheckAndApply.put(productUnderDiscount,5);//this one will approve the visible discount
            // set operator OR
            composedDiscountUT.setCompositeOperator(CompositeOperator.XOR);
            //set condition products map
            Map<Product,Integer> conditionMap = new HashMap<>();
            //set post condition product map
            Map<Product,Integer> postMap = new HashMap<>();
            postMap.put(product1,1);
            composedDiscountUT.setProductsUnderThisDiscount(conditionMap);
            composedDiscountUT.setAmountOfProductsForApplyDiscounts(postMap);
            discountGenComposed.applyDiscount(productsToCheckAndApply);
            //verify the discount applied as conditional product. check total bag cost
            Assertions.assertEquals(20,product1.getCost()*3);
            Assertions.assertEquals(30,product1.getOriginalCost()*3);
        }

        //apply pre post with no pre - success in empty manner in case of operator between discounts succeeded
        /**
         * apply OR composed discount with post condition products maps
         * success: the discount applied on the products because at least one discount term met (visible)
         * buy 2 units from product1 and get the third free (100% discount)
         *
         */
        @Test
        void applyDiscountORPostPositive(){
            //create products to check and apply
            productsToCheckAndApply.put(product1,3);
            productsToCheckAndApply.put(productUnderDiscount,5);//this one will approve the visible discount
            // set operator OR
            composedDiscountUT.setCompositeOperator(CompositeOperator.OR);

            //set post condition product map
            Map<Product,Integer> postMap = new HashMap<>();
            postMap.put(product1,1);
            composedDiscountUT.setAmountOfProductsForApplyDiscounts(postMap);
            composedDiscountUT.setProductsUnderThisDiscount(new HashMap<>());
            discountGenComposed.applyDiscount(productsToCheckAndApply);
            //verify the discount applied as conditional product. check total bag cost
            Assertions.assertEquals(20,product1.getCost()*3);
            Assertions.assertEquals(30,product1.getOriginalCost()*3);
        }

        /**
         * apply AND composed discount with post condition products maps
         * success: the discount applied on the products
         * buy 2 units from product1 and get the third free (100% discount)
         */
        @Test
        void applyDiscountANDPrePostPositive(){
            //create products to check and apply - for all discounts to approve
            productsToCheckAndApply.put(productUnderDiscount,5);
            productsToCheckAndApply.put(product1,3);
            productsToCheckAndApply.put(product2,4);
            productsToCheckAndApply.put(product3,2);
            // set operator AND
            composedDiscountUT.setCompositeOperator(CompositeOperator.AND);
            Map<Product,Integer> conditionMap = new HashMap<>();
            //set post condition product map
            Map<Product,Integer> postMap = new HashMap<>();
            postMap.put(product1,1);
            composedDiscountUT.setProductsUnderThisDiscount(conditionMap);
            composedDiscountUT.setAmountOfProductsForApplyDiscounts(postMap);
            discountGenComposed.applyDiscount(productsToCheckAndApply);
            //verify all discounts didn't applied taken in count only in isApproved
            Assertions.assertFalse(discountGenVis.isApplied());
            Assertions.assertFalse(discountGenProduct.isApplied());
            Assertions.assertFalse(discountGenStore.isApplied());
            //verify discount applied on the product in the post condition map only
            Assertions.assertEquals(20,product1.getCost()*3);
            Assertions.assertEquals(30,product1.getOriginalCost()*3);
        }

        /**
         * apply XOR composed discount with post condition products maps
         * success: the discount applied on the products because odd amount of discounts term met (1-visible)
         * buy 2 units from product1 and get the third free (100% discount)
         *
         */
        @Test
        void applyDiscountXORPrePostPositive(){
            //create products to check and apply
            productsToCheckAndApply.put(product1,3);
            productsToCheckAndApply.put(productUnderDiscount,5);//this one will approve the visible discount
            // set operator OR
            composedDiscountUT.setCompositeOperator(CompositeOperator.XOR);
            //set condition products map
            Map<Product,Integer> conditionMap = new HashMap<>();
            //set post condition product map
            Map<Product,Integer> postMap = new HashMap<>();
            postMap.put(product1,1);
            composedDiscountUT.setProductsUnderThisDiscount(conditionMap);
            composedDiscountUT.setAmountOfProductsForApplyDiscounts(postMap);
            discountGenComposed.applyDiscount(productsToCheckAndApply);
            //verify the discount applied as conditional product. check total bag cost
            Assertions.assertEquals(20,product1.getCost()*3);
            Assertions.assertEquals(30,product1.getOriginalCost()*3);
        }

        /**
         * apply OR with null pre products table
         */
        @Test
        void applyDiscountORPreNULLNegative(){
            //create products to check and apply
            productsToCheckAndApply.put(product1,3);
            productsToCheckAndApply.put(productUnderDiscount,5);//this one will approve the visible discount
            // set operator OR
            composedDiscountUT.setCompositeOperator(CompositeOperator.OR);

            //set post condition product map
            Map<Product,Integer> postMap = new HashMap<>();
            postMap.put(product1,1);
            composedDiscountUT.setAmountOfProductsForApplyDiscounts(postMap);

            //verify exception thrown
            Throwable exception = Assertions.assertThrows(ConditionalProductException.class, () -> discountGenComposed.applyDiscount(productsToCheckAndApply));
            Assertions.assertEquals("There are no products as condition. check discountId: "+4, exception.getMessage());

        }

        /**
         * apply AND with null pre products table
         */
        @Test
        void applyDiscountANDPreNULLNegative(){
            //create products to check and apply - for all discounts to approve
            productsToCheckAndApply.put(productUnderDiscount,5);
            productsToCheckAndApply.put(product1,3);
            productsToCheckAndApply.put(product2,4);
            productsToCheckAndApply.put(product3,2);
            // set operator AND
            composedDiscountUT.setCompositeOperator(CompositeOperator.AND);
            //set post condition product map
            Map<Product,Integer> postMap = new HashMap<>();
            postMap.put(product1,1);
            composedDiscountUT.setAmountOfProductsForApplyDiscounts(postMap);

            //verify exception thrown
            Throwable exception = Assertions.assertThrows(ConditionalProductException.class, () -> discountGenComposed.applyDiscount(productsToCheckAndApply));
            Assertions.assertEquals("There are no products as condition. check discountId: "+4, exception.getMessage());

        }

        /**
         * apply XOR with null pre products table
         */
        @Test
        void applyDiscountXORPreNULLNegative(){
            //create products to check and apply
            productsToCheckAndApply.put(product1,3);
            productsToCheckAndApply.put(productUnderDiscount,5);//this one will approve the visible discount
            // set operator OR
            composedDiscountUT.setCompositeOperator(CompositeOperator.XOR);
            //set post condition product map
            Map<Product,Integer> postMap = new HashMap<>();
            postMap.put(product1,1);
            composedDiscountUT.setAmountOfProductsForApplyDiscounts(postMap);
            //verify exception thrown
            Throwable exception = Assertions.assertThrows(ConditionalProductException.class, () -> discountGenComposed.applyDiscount(productsToCheckAndApply));
            Assertions.assertEquals("There are no products as condition. check discountId: "+4, exception.getMessage());
        }




////////////////////setups and assist methods////////////////////////////////
        ////////////////////visible/////////////////////////////////////////
        /**
         * set up for products to try to apply the discount on
         */
        private void setUpProductsUnderDiscountVis() {
            productUnderDiscount = Product.builder()
                    .name("Cottage")
                    .cost(10.0)
                    .originalCost(10)
                    .productSn(0)
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
        private void setGenericDiscountVisible(double percentage,
                                               Calendar farEndTime,
                                               String description) {

            discountGenVis = Discount.builder()
                    .discountPercentage(percentage)
                    .description(description)
                    .endTime(farEndTime)
                    .discountId(1)
                    .discountType(DiscountType.VISIBLE)
                    .discountPolicy(visibleDiscountUT)
                    .build();
        }

        ///////////////conditional store///////////////////////////////////////


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
        private Discount setDiscountGenVis(double percentage,
                                           Calendar farEndTime,
                                           String description) {

            return Discount.builder()
                    .discountPercentage(percentage)
                    .description(description)
                    .endTime(farEndTime)
                    .discountId(2)
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
                                        String description) {
            //setup of the discount itself
            setStoreDiscount(minPrice);  //set minimum of 200 bag cost purchase to stand in discount
            discountGenStore = setDiscountGenVis(discPercentage, endTime, description);
        }




        ///////////////conditional product//////////////////////////////////////
        /**
         * set the discount with its parameters as received
         *
         * @param percentage
         * @param farEndTime
         * @param description
         */
        private Discount setDiscountGenProduct(double percentage,
                                               Calendar farEndTime,
                                               String description) {

            return Discount.builder()
                    .discountPercentage(percentage)
                    .description(description)
                    .endTime(farEndTime)
                    .discountId(3)
                    .discountType(DiscountType.CONDITIONAL_PRODUCT)
                    .discountPolicy(productDiscountUT)
                    .build();
        }

        /**
         * generic set product cond. discount setup method
         */
        private void setUpProductDiscount(Calendar endTime,
                                          double discPercentage,
                                          Map<Product,Integer> productsUnderThisDiscount,
                                          Map<Product,Integer> amountOfProductsForApplyDiscounts,
                                          String description) {
            //setup of the discount itself
            setProductDiscount(productsUnderThisDiscount,amountOfProductsForApplyDiscounts);
            discountGenProduct = setDiscountGenProduct(discPercentage, endTime, description);
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
                    .productsApplyDiscounts(amountOfProductsForApplyDiscounts)
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
                    .productSn(4)
                    .build();
            product2 = Product.builder()
                    .name("cliff bar")
                    .cost(100.0)
                    .originalCost(100)
                    .productSn(5)
                    .build();
            product3 = Product.builder()
                    .name("playstation")
                    .cost(1000.0)
                    .originalCost(1000)
                    .productSn(6)
                    .build();
        }


        //////////////composite///////////////////////////////////////////////

        /**
         * set the discount with its parameters as received
         */
        private Discount setDiscountGenComposed(double percentage,
                                                Calendar farEndTime,
                                                String description) {

            return Discount.builder()
                    .discountPercentage(percentage)
                    .description(description)
                    .endTime(farEndTime)
                    .discountId(4)
                    .discountType(DiscountType.COMPOSE)
                    .discountPolicy(composedDiscountUT)
                    .build();
        }

        /**
         * setup the composed discount
         */
        private void setComposeDiscount(Map<Product,Integer> amountToApplyMap,List<Discount> composeDiscounts) {
            composedDiscountUT = ComposedDiscount.builder()
                    .composedDiscounts(composeDiscounts)
                    .amountOfProductsForApplyDiscounts(amountToApplyMap)
                    .build();
        }

        /**
         * generic set  composed discount setup method
         */
        private void setUpComposedDiscount(Calendar endTime,
                                           double discPercentage,
                                           List<Discount> composedDiscounts,
                                           Map<Product,Integer> amountToApplyMAp,
                                           String description) {
            //setup of the discount itself
            setComposeDiscount(amountToApplyMAp,composedDiscounts);
            discountGenComposed = setDiscountGenComposed(discPercentage, endTime, description);
        }
    }

}

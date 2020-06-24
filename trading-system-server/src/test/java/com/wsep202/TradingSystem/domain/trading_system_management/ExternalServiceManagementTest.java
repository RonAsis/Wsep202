package com.wsep202.TradingSystem.domain.trading_system_management;

import com.wsep202.TradingSystem.domain.exception.ExternalSystemException;
import com.wsep202.TradingSystem.domain.exception.IllegalProductCostOrAmountException;
import com.wsep202.TradingSystem.domain.trading_system_management.purchase.BillingAddress;
import com.wsep202.TradingSystem.domain.trading_system_management.purchase.PaymentDetails;
import externals.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class ExternalServiceManagementTest {
    private ExternalServiceManagement externalServiceManagement; //the UUT
    //the external systems
    private SecuritySystem securitySystem;
    private ChargeService chargeSystem;
    private SupplyService supplySystem;
    private UserSystem user;
    private ShoppingCart cart;
    private ShoppingBag bag;
    private PaymentDetails paymentDetails;
    private Store store;
    @AfterEach
    void tearDown() {
    }
    @Nested
    public class ExternalServiceManagementTestUnit{
        @BeforeEach
        void setUp() {
            externalServiceManagement = new ExternalServiceManagement();
            //configure mock for security system
            securitySystem = mock(SecuritySystem.class);
            String passwordAfterHash = "hashedPassword";
            String passwordTest = "password";
            String saltTest = "salt";
            when(securitySystem.generateSalt(512)).thenReturn(Optional.of(saltTest));
            when(securitySystem.hashPassword(passwordTest,saltTest)).thenReturn(Optional.of(passwordAfterHash));
            //configure mock for supply system
            supplySystem = mock(BGUSupplySystem.class);

            //configure mock for charge system
            chargeSystem = mock(ChargeSystem.class);
            paymentDetails = mock(PaymentDetails.class);
            cart = mock(ShoppingCart.class);
            bag = mock(ShoppingBag.class);
            store = mock(Store.class);
            //configure mockup of a user for the tests
            user = mock(UserSystem.class);
            //the hash key abd salt as they saved for the user
            when(user.getPassword()).thenReturn(passwordAfterHash);
            when(user.getSalt()).thenReturn(saltTest);

            //initialize the external with the mocks as external systems
            externalServiceManagement.connect(securitySystem,supplySystem,chargeSystem);
            externalServiceManagement.setSupplySystem(supplySystem);
            externalServiceManagement.setChargeSystem(chargeSystem);
        }

        @Test
        void connect() {
        }

        /**
         * checks that the received string is encrypted by the method
         * and saved with its salt in the returned pair
         */
        @Test
        void getEncryptedPasswordAndSalt() {
            //the pre defined params for inputs
            String passwordAfterHash = "hashedPassword";
            String passwordTest = "password";
            String saltTest = "salt";
            PasswordSaltPair expected = mock(PasswordSaltPair.class);
            when(expected.getHashedPassword()).thenReturn(passwordAfterHash);
            when(expected.getSalt()).thenReturn(saltTest);
            PasswordSaltPair actual = externalServiceManagement.getEncryptedPasswordAndSalt(passwordTest);
            //success: the encrypted password and its salt returned as expected
            Assertions.assertEquals(expected.getHashedPassword(),actual.getHashedPassword());
            Assertions.assertEquals(expected.getSalt(),actual.getSalt());
        }

        /**
         * checks the functionality of verification of correct password
         */
        @Test
        void isAuthenticatedUserPasswordPositive() {
            String passwordToCheck = "passwordTest"; //password to authenticate
            when(securitySystem.verifyPassword(passwordToCheck,user.getPassword(),user.getSalt())).thenReturn(true);
            //success: the method matched the inserted password with the saved key at the user
            Assertions.assertTrue(externalServiceManagement.isAuthenticatedUserPassword(passwordToCheck,user));
        }

        /**
         * checks the case of password verification failure
         */
        @Test
        void isAuthenticatedUserPasswordNegative() {
            String passwordToCheck = "not the password"; //password to authenticate
            when(securitySystem.verifyPassword(passwordToCheck,user.getPassword(),user.getSalt())).thenReturn(false);
            //fail: the method didn't match the inserted password with the saved key at the user
            Assertions.assertFalse(externalServiceManagement.isAuthenticatedUserPassword(passwordToCheck,user));
        }

        /**
         * test charging a customer by his shopping cart
         */
        @Test
        void chargePositive() {
            double priceOfEachBag = 5.5;    //some price for each bag to return
            Map<Store,ShoppingBag> shoppingBagsMap = new HashMap<>();   //bags to inject into cart
            shoppingBagsMap.put(store,bag);
            //expected failed stores to receive
            List<Integer> expectedStores = new LinkedList<>();
            when(cart.getShoppingBagsList()).thenReturn(shoppingBagsMap);
            when(bag.getTotalCostOfBag()).thenReturn(priceOfEachBag);
            when(store.getStoreName()).thenReturn("Keter");
            when(chargeSystem.sendPaymentTransaction(paymentDetails,cart)).thenReturn(10002);
            when(store.getStoreId()).thenReturn(2);
            expectedStores.add(store.getStoreId());
            //success: the charge succeeded
            Assertions.assertTrue(externalServiceManagement.charge(paymentDetails,cart)>=10000);
            Assertions.assertTrue(externalServiceManagement.charge(paymentDetails,cart)<=100000);
        }

        /**
         * check that when charging is failed by the charge system, the external notifies the same
         */
        @Test
        void chargeNegative() {
            double priceOfEachBag = 5.5;    //some price for each bag to return
            Map<Store,ShoppingBag> shoppingBagsMap = new HashMap<>();   //bags to inject into cart
            shoppingBagsMap.put(store,bag);
            List<Integer> expectedStores = new LinkedList<>();
            when(cart.getShoppingBagsList()).thenReturn(shoppingBagsMap);
            when(bag.getTotalCostOfBag()).thenReturn(priceOfEachBag);
            when(store.getStoreName()).thenReturn("Keter");
            when(chargeSystem.sendPaymentTransaction(paymentDetails,cart)).thenReturn(-1);
            Assertions.assertTrue(externalServiceManagement.charge(paymentDetails,cart)==-1);
//            //fail: the charge by the charge system failed for the received store in the list (store)
//            Assertions.assertThrows(ChargeException.class,
//                    ()->{
//                        externalServiceManagement.charge(paymentDetails,cart);
//                    });
        }

        /**
         * test cancel charging a customer by his shopping cart
         */
        @Test
        void cancelChargePositive() {
            double priceOfEachBag = 5.5;    //some price for each bag to return
            Map<Store,ShoppingBag> shoppingBagsMap = new HashMap<>();   //bags to inject into cart
            shoppingBagsMap.put(store,bag);
            //expected failed stores to receive
            List<Integer> expectedStores = new LinkedList<>();
            when(cart.getShoppingBagsList()).thenReturn(shoppingBagsMap);
            when(bag.getTotalCostOfBag()).thenReturn(priceOfEachBag);
            when(store.getStoreName()).thenReturn("Keter");
            when(store.getStoreId()).thenReturn(2);
            when(chargeSystem.cancelCharge(paymentDetails,"10002",cart)).thenReturn(1);
            expectedStores.add(store.getStoreId());
            //success: the cancellation of charge succeeded - the required refund sum > 0
           // externalServiceManagement.cancelCharge(paymentDetails,cart,"10002");
            Assertions.assertTrue(externalServiceManagement.cancelCharge(paymentDetails,cart,"10002")>0);
        }

        /**
         * test fail case of cancel charging a customer by his shopping cart
         */
        @Test
        void cancelChargeNegative() {
            double priceOfEachBag = 5.5;    //some price for each bag to return
            Map<Store,ShoppingBag> shoppingBagsMap = new HashMap<>();   //bags to inject into cart
            shoppingBagsMap.put(store,bag);
            //expected failed stores to receive
            List<Integer> expectedStores = new LinkedList<>();
            when(cart.getShoppingBagsList()).thenReturn(shoppingBagsMap);
            when(bag.getTotalCostOfBag()).thenReturn(priceOfEachBag);
            when(store.getStoreName()).thenReturn("Keter");
            when(store.getStoreId()).thenReturn(2);
            when(chargeSystem.cancelCharge(paymentDetails,"15",cart)).thenReturn(-1);
            expectedStores.add(store.getStoreId());
            //success: the cancellation of charge succeeded - the required refund sum > 0
           // externalServiceManagement.cancelCharge(paymentDetails,cart,"15");
            Assertions.assertTrue(externalServiceManagement.cancelCharge(paymentDetails,cart,"15")==-1);
        }



        /**
         * test that when delivery request is accepted by the supply system, the external service
         * system notifies accordingly
         */
        @Test
        void deliverPositive() {
            BillingAddress addrInfo = mock(BillingAddress.class);
            List<ShoppingBag> expectedBags = new ArrayList<>();     //bags that will be delivered
            expectedBags.add(bag);
            Map<Store,ShoppingBag> shoppingBagsMap = new HashMap<>();   //bags to inject into cart
            shoppingBagsMap.put(store,bag);
            when(cart.getShoppingBagsList()).thenReturn(shoppingBagsMap);
            when(supplySystem.deliver(addrInfo,cart)).thenReturn(11003);
            //success: the deliver request accepted so externalService notifies about that
            Assertions.assertTrue(externalServiceManagement.deliver(addrInfo,cart)>=10000);
            Assertions.assertTrue(externalServiceManagement.deliver(addrInfo,cart)<=100000);

        }
        /**
         * test that when delivery request is denied by the supply system, the external service
         * system notifies accordingly
         */
        @Test
        void deliverNegative() {
            BillingAddress addrInfo = mock(BillingAddress.class);
            List<ShoppingBag> expectedBags = new ArrayList<>();     //bags that will be delivered
            expectedBags.add(bag);
            Map<Store,ShoppingBag> shoppingBagsMap = new HashMap<>();   //bags to inject into cart
            shoppingBagsMap.put(store,bag);
            when(cart.getShoppingBagsList()).thenReturn(shoppingBagsMap);
            when(supplySystem.deliver(addrInfo,cart)).thenReturn(-1);
            Assertions.assertTrue(externalServiceManagement.deliver(addrInfo,cart)==-1);
//            //fail: the deliver request denied so externalService notifies about that
//            Assertions.assertThrows(DeliveryRequestException.class,
//                    ()->{
//                        externalServiceManagement.deliver(addrInfo,cart);
//                    });
        }

        /**
         * test that when delivery cancel request is accepted by the supply system, the external service
         * system notifies accordingly
         */
        @Test
        void cancelDeliveryPositive() {
            BillingAddress addrInfo = mock(BillingAddress.class);
            List<ShoppingBag> expectedBags = new ArrayList<>();     //bags that will be delivered
            expectedBags.add(bag);
            Map<Store,ShoppingBag> shoppingBagsMap = new HashMap<>();   //bags to inject into cart
            shoppingBagsMap.put(store,bag);
            when(cart.getShoppingBagsList()).thenReturn(shoppingBagsMap);
            when(supplySystem.cancelDelivery(addrInfo,cart,"11003")).thenReturn(1);
            //success: the deliver cancel request accepted so externalService notifies about that
            Assertions.assertTrue(externalServiceManagement.cancelDelivery(addrInfo,cart,"11003")==1);

        }

        /**
         * test that when delivery cancel request is failed by the supply system, the external service
         * system notifies accordingly
         */
        @Test
        void cancelDeliveryNegative() {
            BillingAddress addrInfo = mock(BillingAddress.class);
            List<ShoppingBag> expectedBags = new ArrayList<>();     //bags that will be delivered
            expectedBags.add(bag);
            Map<Store,ShoppingBag> shoppingBagsMap = new HashMap<>();   //no bags to inject into cart
            when(cart.getShoppingBagsList()).thenReturn(shoppingBagsMap);
            when(supplySystem.cancelDelivery(addrInfo,cart,"11")).thenReturn(-1);
            //fail: the deliver cancel request failed so externalService notifies about that
            Assertions.assertTrue(externalServiceManagement.cancelDelivery(addrInfo,cart,"11")==-1);
        }

    }




    @Nested
    public class ExternalServiceManagementTestIntegration{
        @BeforeEach
        void setUp() {


            externalServiceManagement = new ExternalServiceManagement();


            //configure mock for supply system
            supplySystem = mock(BGUSupplySystem.class);

            //configure mock for charge system
            chargeSystem = mock(BGUChargeSystem.class);

            paymentDetails = new PaymentDetails();
            store = new Store(user,"keter");
            cart = new ShoppingCart();
            bag = new ShoppingBag(store);
            externalServiceManagement.connect();
            externalServiceManagement.setChargeSystem(chargeSystem);
            externalServiceManagement.setSupplySystem(supplySystem);
        }

        @Test
        void connect() {
        }

        /**
         * test the case of trying to charge when disconnected from external system
         */
        @Test
        void noConnectionCharge(){
            when(chargeSystem.isConnected()).thenThrow(new ExternalSystemException("failed to handshake the BGU charge system."));
            when(chargeSystem.sendPaymentTransaction(paymentDetails,cart)).thenCallRealMethod();
            //fail: system indicates about charge failure by throw an exception
            Assertions.assertThrows(ExternalSystemException.class,
                    ()->{
                        externalServiceManagement.charge(paymentDetails,cart);
                    });
        }


        /**
         * test charging a customer by his shopping cart
         */
        @Test
        void chargePositive() {


            //bag.setStoreOfProduct(store);
            cart.addBagToCart(store,bag);
            //mock for charge system
            when(chargeSystem.sendPaymentTransaction(paymentDetails,cart)).thenReturn(10002);
            bag.addProductToBag(new Product("p",ProductCategory.BOOKS_MOVIES_MUSIC,2,2,store.getStoreId()),2);
            //success: the charge succeeded no failed stores received
            paymentDetails.setCreditCardNumber("123456789");    //valid card no. length
            Assertions.assertTrue(externalServiceManagement.charge(paymentDetails,cart)>=10000);
            Assertions.assertTrue(externalServiceManagement.charge(paymentDetails,cart)<=100000);

        }

        /**
         * check that when charging is failed by the charge system, the external notifies the same
         */
        @Test
        void chargeNegative() {
            //bag.setStoreOfProduct(store);
            cart.addBagToCart(store,bag);
            List<Integer> expectedStores = new LinkedList<>();
            //fail: the charge failed for shopping bag of store
            paymentDetails.setCreditCardNumber("1234567890");    //invalid card no. length
            when(chargeSystem.sendPaymentTransaction(paymentDetails,cart)).thenReturn(-1);
            bag.addProductToBag(new Product("p",ProductCategory.BOOKS_MOVIES_MUSIC,2,5.5,store.getStoreId()),2);
            Assertions.assertTrue(externalServiceManagement.charge(paymentDetails,cart)==-1);
//            //fail: system indicates about charge failure by throw an exception
//            Assertions.assertThrows(ChargeException.class,
//                    ()->{
//                        externalServiceManagement.charge(paymentDetails,cart);
//                    });
        }

        /**
         * test cancel charge of customer
         */
        @Test
        void cancelChargePositive() {

           // bag.setStoreOfProduct(store);
            cart.addBagToCart(store,bag);
            List<Integer> expectedStores = new LinkedList<>();
            expectedStores.add(store.getStoreId()); //the expected store that failed to be returned in list
            //success: the cancellation succeeded no failed stores received
            paymentDetails.setCreditCardNumber("123456789");    //valid card no. length
            //mock for charge system
            when(chargeSystem.cancelCharge(paymentDetails,"10002",cart)).thenReturn(1);
            bag.addProductToBag(new Product("p",ProductCategory.BOOKS_MOVIES_MUSIC,2,2,store.getStoreId()),2);

            //success: charge canceled
            Assertions.assertTrue(externalServiceManagement.cancelCharge(paymentDetails,cart,"10002")==1);
        }

        /**
         * test handling with failure of cancel charge of customer
         */
        @Test
        void cancelChargeNegative() {
            //bag.setStoreOfProduct(store);
            cart.addBagToCart(store,bag);
            List<Integer> expectedStores = new LinkedList<>();
            expectedStores.add(store.getStoreId()); //the expected store that failed to be returned in list
            //mock for charge system
            when(chargeSystem.cancelCharge(paymentDetails,"11",cart)).thenReturn(-1);
            bag.addProductToBag(new Product("p",ProductCategory.BOOKS_MOVIES_MUSIC,2,2,store.getStoreId()),2);

            //fail: the cancellation failed
            paymentDetails.setCreditCardNumber("123456789");    //valid card no. length
            Assertions.assertTrue(externalServiceManagement.cancelCharge(paymentDetails,cart,"11")==-1);
        }


        /**
         * test that when delivery request is accepted by the supply system, the external service
         * system notifies accordingly
         */
        @Test
        void deliverPositive() {
            BillingAddress addrInfo = new BillingAddress();
            addrInfo.setZipCode("2010300");    //set valid phone number
            bag.addProductToBag(new Product("p",ProductCategory.BOOKS_MOVIES_MUSIC,2,5.5,store.getStoreId()),2);
            //bag.setStoreOfProduct(store);
            cart.addBagToCart(store,bag);
            ArrayList<ShoppingBag> bags = new ArrayList<>();
            for(ShoppingBag bag: cart.getShoppingBagsList().values()){
                bags.add(bag);
            }
            when(supplySystem.deliver(addrInfo,cart)).thenReturn(11003);
            //success: the deliver request accepted so externalService notifies about that
            Assertions.assertTrue(externalServiceManagement.deliver(addrInfo,cart)>=10000);
            Assertions.assertTrue(externalServiceManagement.deliver(addrInfo,cart)<=100000);

        }
        /**
         * test that when delivery request is denied by the supply system, the external service
         * system notifies accordingly
         */
        @Test
        void deliverNegative() {
            BillingAddress addrInfo = new BillingAddress();
            addrInfo.setZipCode("201030005");    //set invalid phone number
            bag.addProductToBag(new Product("p",ProductCategory.BOOKS_MOVIES_MUSIC,2,5.5,store.getStoreId()),2);
            //bag.setStoreOfProduct(store);
            cart.addBagToCart(store,bag);
            ArrayList<ShoppingBag> bags = new ArrayList<>();
            for(ShoppingBag bag: cart.getShoppingBagsList().values()){
                bags.add(bag);
            }
            when(supplySystem.deliver(addrInfo,cart)).thenReturn(-1);
            Assertions.assertTrue(externalServiceManagement.deliver(addrInfo,cart)==-1);
        }

        /**
         * test that when cancel delivery request is made by the supply system, the external service
         * system notifies accordingly
         */
        @Test
        void cancelDeliverPositive() {
            BillingAddress addrInfo = new BillingAddress();
            addrInfo.setZipCode("2010300");    //set valid phone number
            bag.addProductToBag(new Product("p",ProductCategory.BOOKS_MOVIES_MUSIC,2,5.5,store.getStoreId()),2);
            //bag.setStoreOfProduct(store);
            cart.addBagToCart(store,bag);
            externalServiceManagement.cancelDelivery(addrInfo,cart,"11003");
            ArrayList<ShoppingBag> bags = new ArrayList<>();
            for(ShoppingBag bag: cart.getShoppingBagsList().values()){
                bags.add(bag);
            }
            when(supplySystem.cancelDelivery(addrInfo,cart,"11003")).thenReturn(1);
            //success: the cancel delivery request accepted so externalService notifies about that
            Assertions.assertTrue(externalServiceManagement.cancelDelivery(addrInfo,cart,"11003")==1);
        }

        /**
         * test that when cancel delivery request is failed by the supply system, the external service
         * system notifies accordingly
         */
        @Test
        void cancelDeliverNegative() {
            BillingAddress addrInfo = new BillingAddress();
            setupAddress(addrInfo);
            externalServiceManagement.setSupplySystem(supplySystem);
            when(supplySystem.cancelDelivery(addrInfo,cart,"11003")).thenReturn(-1);

            //fail: the cancel delivery failed - no bags to cancel
            Assertions.assertTrue(externalServiceManagement.cancelDelivery(addrInfo,cart,"11003")==-1);
        }

        /**
         * test the case of trying to charge when disconnected from external system
         */
        @Test
        void noConnectionDeliver(){
            BillingAddress addrInfo = new BillingAddress();
            setupAddress(addrInfo);
            when(supplySystem.isConnected()).thenThrow(new ExternalSystemException("failed to handshake the BGU charge system."));
            when(supplySystem.deliver(addrInfo,cart)).thenCallRealMethod();
            //fail: system indicates about charge failure by throw an exception
            Assertions.assertThrows(ExternalSystemException.class,
                    ()->{
                        externalServiceManagement.deliver(addrInfo,cart);
                    });
        }


        private void setupAddress(BillingAddress addrInfo) {
            addrInfo.setZipCode("2010300");    //set valid phone number
            addrInfo.setAddress("Rager, 140");
            addrInfo.setCity("beer sheva");
            addrInfo.setCountry("Israel");
            addrInfo.setCustomerFullName("moti banana");
        }

        ///////////////setups//////////////////////////////////////////////////////////
        private void getEncryptedPasswordAndSaltSetup() {
            PasswordSaltPair passwordSaltPair = externalServiceManagement.getEncryptedPasswordAndSalt(user.getPassword());
            user.setSalt(passwordSaltPair.getSalt());
            user.setPassword(passwordSaltPair.getHashedPassword());
        }
    }

}

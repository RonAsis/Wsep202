package com.wsep202.TradingSystem.domain.trading_system_management;

import com.wsep202.TradingSystem.domain.exception.NoManagerInStoreException;
import com.wsep202.TradingSystem.domain.exception.ProductDoesntExistException;
import externals.ChargeSystem;
import externals.SecuritySystem;
import externals.SupplySystem;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class ExternalServiceManagementTest {
    private ExternalServiceManagement externalServiceManagement; //the UUT
    //the external systems
    private SecuritySystem securitySystem;
    private ChargeSystem chargeSystem;
    private SupplySystem supplySystem;
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
            supplySystem = mock(SupplySystem.class);

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
            when(cart.getShoppingBagsList()).thenReturn(shoppingBagsMap);
            when(bag.getTotalCostOfBag()).thenReturn(priceOfEachBag);
            when(store.getStoreName()).thenReturn("Keter");
            when(chargeSystem.sendPaymentTransaction(store.getStoreName(),priceOfEachBag,paymentDetails)).thenReturn(true);
            //success: the charge succeeded
            Assertions.assertTrue(externalServiceManagement.charge(paymentDetails,cart));
        }

        /**
         * check that when charging is failed by the charge system, the external notifies the same
         */
        @Test
        void chargeNegative() {
            double priceOfEachBag = 5.5;    //some price for each bag to return
            Map<Store,ShoppingBag> shoppingBagsMap = new HashMap<>();   //bags to inject into cart
            shoppingBagsMap.put(store,bag);
            when(cart.getShoppingBagsList()).thenReturn(shoppingBagsMap);
            when(bag.getTotalCostOfBag()).thenReturn(priceOfEachBag);
            when(store.getStoreName()).thenReturn("Keter");
            when(chargeSystem.sendPaymentTransaction(store.getStoreName(),priceOfEachBag,paymentDetails)).thenReturn(false);
            //fail: the charge by the charge system failed
            Assertions.assertFalse(externalServiceManagement.charge(paymentDetails,cart));
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
            when(supplySystem.deliver(addrInfo,expectedBags)).thenReturn(true);
            //success: the deliver request accepted so externalService notifies about that
            Assertions.assertTrue(externalServiceManagement.deliver(addrInfo,cart));
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
            when(supplySystem.deliver(addrInfo,expectedBags)).thenReturn(false);
            //fail: the deliver request denied so externalService notifies about that
            Assertions.assertFalse(externalServiceManagement.deliver(addrInfo,cart));
        }

    }




    @Nested
    public class ExternalServiceManagementTestIntegration{
        @BeforeEach
        void setUp() {
            externalServiceManagement = new ExternalServiceManagement();
            securitySystem = new SecuritySystem();
            String passwordAfterHash = "hashedPassword";
            String passwordTest = "password";
            String saltTest = "salt";
            supplySystem = new SupplySystem();

            chargeSystem = new ChargeSystem();
            paymentDetails = new PaymentDetails();
            user = new UserSystem("username","luis","enrique",passwordTest);
            store = new Store(user,new PurchasePolicy(),new DiscountPolicy(),"keter");
            cart = new ShoppingCart();
            bag = new ShoppingBag(store);
            externalServiceManagement.connect();
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
            //success: the method encript the password and returns it as pair object and not null
            Assertions.assertTrue(externalServiceManagement.getEncryptedPasswordAndSalt(user.getPassword()) instanceof PasswordSaltPair);
            Assertions.assertNotNull(externalServiceManagement.getEncryptedPasswordAndSalt(user.getPassword()));
        }

        /**
         * checks the functionality of verification of correct password
         */
        @Test
        void isAuthenticatedUserPasswordPositive() {
            String passwordTest = "password";
            //create salt pair of password and its salt
            getEncryptedPasswordAndSaltSetup();
            //verify that when the password is correct we get true
            Assertions.assertTrue(externalServiceManagement.isAuthenticatedUserPassword(passwordTest,user));
        }

        /**
         * checks the case of password verification failure
         */
        @Test
        void isAuthenticatedUserPasswordNegative() {
            getEncryptedPasswordAndSaltSetup();
            String passwordToCheck = "not the password"; //password to authenticate
            //fail: the method didn't match the inserted password with the saved key at the user
            Assertions.assertFalse(externalServiceManagement.isAuthenticatedUserPassword(passwordToCheck,user));
        }



        /**
         * test charging a customer by his shopping cart
         */
        @Test
        void chargePositive() {
            bag.setTotalCostOfBag(5.5);
            bag.setStoreOfProduct(store);
            cart.addBagToCart(store,bag);
            //success: the charge succeeded
            paymentDetails.setCreditCardNumber("123456789");    //valid card no. length
            Assertions.assertTrue(externalServiceManagement.charge(paymentDetails,cart));
        }

        /**
         * check that when charging is failed by the charge system, the external notifies the same
         */
        @Test
        void chargeNegative() {
            bag.setTotalCostOfBag(5.5);
            bag.setStoreOfProduct(store);
            cart.addBagToCart(store,bag);
            //fail: the charge succeeded
            paymentDetails.setCreditCardNumber("1234567890");    //invalid card no. length
            Assertions.assertFalse(externalServiceManagement.charge(paymentDetails,cart));
        }

        /**
         * test that when delivery request is accepted by the supply system, the external service
         * system notifies accordingly
         */
        @Test
        void deliverPositive() {
            BillingAddress addrInfo = new BillingAddress();
            addrInfo.setPhone("0523456789");    //set valid phone number
            bag.setTotalCostOfBag(5.5);
            bag.setStoreOfProduct(store);
            cart.addBagToCart(store,bag);
            //success: the deliver request accepted so externalService notifies about that
            Assertions.assertTrue(externalServiceManagement.deliver(addrInfo,cart));
        }
        /**
         * test that when delivery request is denied by the supply system, the external service
         * system notifies accordingly
         */
        @Test
        void deliverNegative() {
            BillingAddress addrInfo = new BillingAddress();
            addrInfo.setPhone("052345678191");    //set invalid phone number
            bag.setTotalCostOfBag(5.5);
            bag.setStoreOfProduct(store);
            cart.addBagToCart(store,bag);
            //success: the deliver request accepted so externalService notifies about that
            Assertions.assertFalse(externalServiceManagement.deliver(addrInfo,cart));
        }
        ///////////////setups//////////////////////////////////////////////////////////
        private void getEncryptedPasswordAndSaltSetup() {
            PasswordSaltPair passwordSaltPair = externalServiceManagement.getEncryptedPasswordAndSalt(user.getPassword());
            user.setSalt(passwordSaltPair.getSalt());
            user.setPassword(passwordSaltPair.getHashedPassword());
        }
    }

}
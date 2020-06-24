package com.wsep202.TradingSystem.domain.trading_system_management;

import com.wsep202.TradingSystem.domain.exception.*;
import com.wsep202.TradingSystem.domain.trading_system_management.discount.*;
import com.wsep202.TradingSystem.domain.trading_system_management.ownerStore.StatusOwner;
import com.wsep202.TradingSystem.domain.trading_system_management.policy_purchase.Purchase;
import com.wsep202.TradingSystem.domain.trading_system_management.policy_purchase.PurchasePolicy;
import com.wsep202.TradingSystem.domain.trading_system_management.policy_purchase.PurchaseType;
import com.wsep202.TradingSystem.domain.trading_system_management.purchase.BillingAddress;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.Assert;

import java.util.*;

import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class StoreTest {
    private MangerStore managerStore; //the manager object of the manager user
    private MangerStore managerStore1; //the manager object of the manager user
    private UserSystem managerUser; //role as manager to add and get
    private UserSystem managerUser1; //role as manager to add and get
    private UserSystem newOwner;    //role as an appointed owner in the store
    private UserSystem fakeOwner;   //role as not owner in the store
    private UserSystem owner;   //role as appointing owner in the store
    private Store storeUT;  //unit under test
    private Purchase purchasePolicy;
    private ShoppingBag shoppingBag;
    private Discount discount;

    private Product product;    //product to add

    //integration attributes
    private UserSystem ownerRealUser;
    private UserSystem newOwnerReal;
    private UserSystem fakeOwnerReal;


    @AfterEach
    void tearDown() {
    }


    private boolean assertReceipt(Receipt receipt, int storeId, String buyerName, double totalCostOfBag,
                                  Map<Product,Integer> productListFromStore) {
        return ((receipt.getStoreId() == storeId) &&
                (receipt.getUserName().equals(buyerName)) &&
                (receipt.getAmountToPay() == totalCostOfBag) &&
                (receipt.getProductsBought().equals(productListFromStore)));
    }

    @Nested
    public class StoreTestUnit {

        @BeforeEach
        void setUp() {
            //new manager to add
            newOwner = mock(UserSystem.class);   //the user the owner want to appoint as a new store owner
            //stubbing
            when(newOwner.getUserName()).thenReturn("newOwner");

            //manager for get and add owner tests
            managerUser = mock(UserSystem.class);
            when(managerUser.getUserName()).thenReturn("managerUser");
            managerStore = new MangerStore(managerUser);

            //manager for get and add owner tests
            managerUser1 = mock(UserSystem.class);
            when(managerUser1.getUserName()).thenReturn("managerUser1");
            managerStore1 = new MangerStore(managerUser1);

            //not owner of the store
            fakeOwner = mock(UserSystem.class);
            when(fakeOwner.getUserName()).thenReturn("fakeOwner");

            //owner and opener of the store
            owner = mock(UserSystem.class);
            when(owner.getUserName()).thenReturn("owner");
            storeUT = new Store(owner, "Store under test");
            product = mock(Product.class);

            shoppingBag = mock(ShoppingBag.class);
            discount = mock(Discount.class);
        }

        private void addNewProductSetUp() {
            storeUT.addNewProduct(owner, product);
        }

        /**
         * get existing product in the store
         */
        @Test
        void getExistProductPositive() {
            addNewProductSetUp();
            when(product.getProductSn()).thenReturn(0);
            //success: get exist product
            Assertions.assertEquals(product, storeUT.getProduct(0));
        }

        /**
         * get not existing product in the store
         */
        @Test
        void getNotExistProductNegative() {
            when(product.getProductSn()).thenReturn(0);
            //success: get exist product
            Throwable exception = Assertions
                    .assertThrows(ProductDoesntExistException.class, () -> storeUT.getProduct(0));
            Assertions.assertEquals("A product with id '" +
                    0 + "' is not exist in store with id: '" + storeUT.getStoreId() +
                    "'", exception.getMessage());
        }

        /**
         * owner remove a product exists in store
         */
        @Test
        void removeExistsProductFromStorePositive() {
            //setup for remove product
            addNewProductSetUp();  //add product to the store
            Assertions.assertEquals(1, storeUT.getProducts().size()); //verify we starts with no products in store
            //add a product to the store which will be removed for the test
            when(product.getProductSn()).thenReturn(12);    // let product SN to for the check which one to remove
            Assertions.assertTrue(storeUT.removeProductFromStore(owner, 12));    //success: product is removed
            Assertions.assertEquals(0, storeUT.getProducts().size()); //verify we decreased no. of products in store
        }

        /**
         * owner remove a product that doesn't exist in store
         */
        @Test
        void removeNotExistsProductFromStoreNegative() {
            //setup for remove product
            addNewProductSetUp();  //add product to the store
            Assertions.assertEquals(1, storeUT.getProducts().size()); //verify we starts with no products in store
            //add a product to the store which will be removed for the test
            when(product.getProductSn()).thenReturn(12);    // let product SN to for the check which one to remove
            Assertions.assertFalse(storeUT.removeProductFromStore(owner, 4));    //fail: product not exist is not removed
            Assertions.assertEquals(1, storeUT.getProducts().size()); //verify we decreased no. of products in store
        }

        /**
         * user which is not an owner of the store removes a product that doesn't exist in store
         */
        @Test
        void notOwnerRemoveExistsProductFromStoreNegative() {
            UserSystem notOwner = mock(UserSystem.class);   //not an owner
            //setup for remove product. add a product to the store which will be removed for the test
            addNewProductSetUp();  //add product to the store
            Assertions.assertEquals(1, storeUT.getProducts().size()); //verify we starts with no products in store
            //stubs
            when(notOwner.getUserName()).thenReturn("Philip");
            when(product.getProductSn()).thenReturn(12);    // let product SN to for the check which one to remove
            Assertions.assertFalse(storeUT.removeProductFromStore(notOwner, 12));    //fail: not owner cannot remove product
            Assertions.assertEquals(1, storeUT.getProducts().size()); //verify we decreased no. of products in store
        }

        /**
         * owner validates that he can edit a given product
         */
        @Test
        void validateCanEditProductsPositive(){
            addNewProductSetUp();
            Assertions.assertTrue(storeUT.validateCanEditProducts(owner,product.getProductSn()));
        }

        /**
         * user that is not owner validates that he can edit a given product
         */
        @Test
        void validateCanEditProductsNegative(){
            addNewProductSetUp();
            Assertions.assertFalse(storeUT.validateCanEditProducts(managerUser,product.getProductSn()));
        }

        /**
         * owner adds new product
         */
        @Test
        void addNewProductPositive() {
            Assertions.assertEquals(0, storeUT.getProducts().size());     //verify there are no products in store
            Assertions.assertTrue(storeUT.addNewProduct(owner, product));    //success: the product added by the owner
            Assertions.assertEquals(1, storeUT.getProducts().size());     //verify amount of products in list increased
        }

        /**
         * user that is not an owner adds new product
         */
        @Test
        void addNewProductNegative() {
            Assertions.assertEquals(0, storeUT.getProducts().size());     //verify there are no products in store
            //fail: user which is no owner cannot add product to store
            Assertions.assertFalse(storeUT.addNewProduct(fakeOwner, product));
            Assertions.assertEquals(0, storeUT.getProducts().size());     //verify there are no products in store
        }

        /**
         * setting an existing product in store with product properties by an owner
         */
        @Test
        void editProductPositive() {
            //setup for adding a product to store
            addProductAsEditProductSetup();
            int productSN = product.getProductSn();  //the serial number of the product
            String name = "airpods";    //the new name
            String category = "electronics";   //new category
            int amount = 4; //new amount
            double cost = 100000.5; //new cost
            //success: the product edited
            Assertions.assertTrue(storeUT.editProduct(owner, productSN, name, category, amount, cost));
        }

        /**
         * setting an existing product in store but not by an owner so should fail.
         */
        @Test
        void notOwnerEditProductNegative() {
            //setup for adding a product to store
            addProductAsEditProductSetup();
            //not owner
            UserSystem notOwner = mock(UserSystem.class);   //not an owner
            when(notOwner.getUserName()).thenReturn("Donald");
            int productSN = 0;  //the serial number of the product
            String name = "airpods";    //the new name
            String category = "electronics";   //new category
            int amount = 4; //new amount
            double cost = 100000.5; //new cost
            //fail: the product is not edited
            Assertions.assertFalse(storeUT.editProduct(notOwner, productSN, name, category, amount, cost));

        }

        /**
         * setting not existing product in store with product properties by an owner
         */
        @Test
        void editNotExistProductNegative() {
            //setup for adding a product to store
            addProductAsEditProductSetup();
            int productSN = 23;  //the serial number of the product
            String name = "airpods";    //the new name
            String category = "electronics";   //new category
            int amount = 4; //new amount
            double cost = 100000.5; //new cost
            //fail: the product isn't edited
            Assertions.assertFalse(storeUT.editProduct(owner, productSN, name, category, amount, cost));
        }

        /**
         * creates a new receipt with right parameters
         */
        @Test
        void createReceiptPositive() {
            Map<Product, Integer> products = new HashMap<>();
            products.put(product,1);
            when(shoppingBag.getProductListFromStore()).thenReturn(products);
            Assertions.assertTrue(assertReceipt(storeUT.createReceipt(shoppingBag, owner.getUserName(), 1, 1),
                    storeUT.getStoreId(),owner.getUserName(), product.getCost(), products));    //success: the product added by the owner
        }

        /**
         * creates a new receipt with wrong parameters
         */
        @Test
        void createReceiptNegative() {
            Map<Product, Integer> products = null;
            when(shoppingBag.getProductListFromStore()).thenReturn(products);
            Receipt returnedReceipt = storeUT.createReceipt(shoppingBag, owner.getUserName(), 1, 1);
            Assertions.assertEquals(0, returnedReceipt.getAmountToPay());
        }


        /**
         * creates a new receipt with empty cart
         */
        @Test
        void createReceiptNegativeEmptyCart() {
            Map<Product, Integer> products = new HashMap<>();
            when(shoppingBag.getProductListFromStore()).thenReturn(products);
            Receipt returnedReceipt = storeUT.createReceipt(shoppingBag, owner.getUserName(), 1, 1);
            Assertions.assertEquals(0, returnedReceipt.getAmountToPay());
        }

        /**
         * check update amount of product in the stock (after purchase)
         */
        @Test
        public void editProductAmountInStockPositive() {
            addNewProductSetUp(); //add product to stock
            when(product.getProductSn()).thenReturn(0);
            doNothing().when(product).setAmount(3);
            Assertions.assertTrue(storeUT.editProductAmountInStock(0, 3));
        }

        /**
         * check not update amount of product that isn't in the stock
         */
        @Test
        public void editProductAmountInStockNegative() {
            addNewProductSetUp(); //add product to stock
            when(product.getProductSn()).thenReturn(0);
            doNothing().when(product).setAmount(3);
            Assertions.assertFalse(storeUT.editProductAmountInStock(1, 3));
        }

        /**
         * check that when product bout is in stock return true
         */
        @Test
        public void isAllInStockPositive() {
            Map<Product, Integer> productBag = new HashMap<>();
            productBag.put(product, 1);
            addNewProductSetUp();   //add the bought product to stock
            when(product.getAmount()).thenReturn(1);
            when(shoppingBag.getProductListFromStore()).thenReturn(productBag);
            when(product.getProductSn()).thenReturn(0);
            when(shoppingBag.getProductAmount(product.getProductSn())).thenReturn(1);//one bought
            Assertions.assertTrue(storeUT.isAllInStock(shoppingBag));
        }

        /**
         * check that when product bout is not in stock return exception
         */
        @Test
        public void isAllInStockNegative() {
            Map<Product, Integer> productBag = new HashMap<>();
            productBag.put(product, 1);
            addNewProductSetUp();   //add the bought product to stock
            when(product.getAmount()).thenReturn(1);    //amount in store
            when(shoppingBag.getProductListFromStore()).thenReturn(productBag);
            when(product.getProductSn()).thenReturn(0);
            when(shoppingBag.getProductAmount(product.getProductSn())).thenReturn(5);//five bought
            when(product.getName()).thenReturn("carbon");

            Throwable exception = Assertions
                    .assertThrows(NotInStockException.class, () -> storeUT.isAllInStock(shoppingBag));
            Assertions.assertEquals("The product: " + product.getName()
                            + " is out of stock in store: " + storeUT.getStoreName()
                    , exception.getMessage());
        }

        /**
         * check that getOwnersUsername returns the name of the owner of the store
         */
        @Test
        void getOwnersUsernamePositive(){
            Assertions.assertNotNull(storeUT.getOwnersUsername().stream()
                    .filter(owner1 -> owner1.equals(owner.getUserName())).findFirst().get());
        }

        /**
         * check that getOwnersUsername doesn't returns the name of a regulat user in the store
         */
        @Test
        void getOwnersUsernameNegative(){
            boolean result = storeUT.getOwnersUsername().stream()
                    .anyMatch(owner1 -> owner1.equals(managerUser.getUserName()));
            Assertions.assertFalse(result);
        }

        /**
         * checks that user that was approve
         */
        @Test
        void approveOwnerPositive(){
            storeUT.addOwner(owner, newOwner);
            storeUT.approveOwner(owner, newOwner.getUserName(), true);
            Assertions.assertEquals(2, storeUT.getOwnersUsername().size());
            storeUT.addOwner(owner, managerUser); // creates an appointing agreement
            Assertions.assertEquals(1, storeUT.getAppointingAgreements().size());
            storeUT.approveOwner(newOwner, managerUser.getUserName(), true);
            Assertions.assertEquals(0, storeUT.getAppointingAgreements().size());
            Assertions.assertEquals(3, storeUT.getOwnersUsername().size());
        }

        /**
         * checks that user that wasn't approved
         */
        @Test
        void dontApproveOwnerPositive(){
            storeUT.addOwner(owner, newOwner);
            storeUT.approveOwner(owner, newOwner.getUserName(), true);
            Assertions.assertEquals(2, storeUT.getOwnersUsername().size());
            storeUT.addOwner(owner, managerUser); // creates an appointing agreement
            Assertions.assertEquals(1, storeUT.getAppointingAgreements().size());
            storeUT.approveOwner(newOwner, managerUser.getUserName(), false);
            Assertions.assertEquals(0, storeUT.getAppointingAgreements().size());
            Assertions.assertEquals(2, storeUT.getOwnersUsername().size());
        }

        /**
         * checks that user that wasn't approved
         */
        @Test
        void approveOwnerNegative(){
            UserSystem notOwner = mock(UserSystem.class);
            when(notOwner.getUserName()).thenReturn("notOwner");

            storeUT.addOwner(owner, newOwner);
            storeUT.approveOwner(owner, newOwner.getUserName(), true);
            Assertions.assertEquals(2, storeUT.getOwnersUsername().size());
            storeUT.addOwner(owner, managerUser); // creates an appointing agreement
            Assertions.assertEquals(1, storeUT.getAppointingAgreements().size());

            Assertions.assertFalse(storeUT.getOwnersUsername().contains(notOwner.getUserName()));
            Assertions.assertFalse(storeUT.approveOwner(notOwner, managerUser.getUserName(), true));
            Assertions.assertEquals(1, storeUT.getAppointingAgreements().size());
            Assertions.assertEquals(2, storeUT.getOwnersUsername().size());
        }

        @Test
        void isApproveOwnerPositive(){
            storeUT.addOwner(owner, newOwner);
            storeUT.approveOwner(owner, newOwner.getUserName(), true);
            Assertions.assertEquals(2, storeUT.getOwnersUsername().size());
            storeUT.addOwner(owner, managerUser); // creates an appointing agreement
            Assertions.assertEquals(StatusOwner.WAITING, storeUT.isApproveOwner(managerUser));
        }

        @Test
        void isApproveOwnerNegative(){
            Assertions.assertEquals(StatusOwner.WAITING, storeUT.isApproveOwner(managerUser));
        }

        /**
         * This test check if the removeOwner method succeeds
         * when the parameters are correct, no managers was appointed by removed owner
         */
        @Test
        void removeOwnerNoManagers(){
            UserSystem ownerToRemove = mock(UserSystem.class);
            when(ownerToRemove.getUserName()).thenReturn("ownerToRemove");
            when(ownerToRemove.getOwnedStores()).thenReturn(new HashSet<>());

            storeUT.addOwner(owner, newOwner);
            storeUT.approveOwner(owner, newOwner.getUserName(), true);
            storeUT.addOwner(owner, ownerToRemove);
            storeUT.approveOwner(newOwner, ownerToRemove.getUserName(), true);

            Assertions.assertTrue(storeUT.removeOwner(owner, ownerToRemove));
            Assertions.assertFalse(storeUT.getOwnersUsername().contains(ownerToRemove.getUserName()));
        }

        /**
         * This test check if the removeOwner method succeeds
         * when the parameters are correct, with that managers was appointed
         * by removed owner.
         */
        @Test
        void removeOwnerWithManagers(){
            UserSystem ownerToRemove = mock(UserSystem.class);
            when(ownerToRemove.getUserName()).thenReturn("ownerToRemove");
            when(ownerToRemove.getOwnedStores()).thenReturn(new HashSet<>());

            storeUT.addOwner(owner, newOwner);
            storeUT.approveOwner(owner, newOwner.getUserName(), true);
            storeUT.addOwner(owner, ownerToRemove);
            storeUT.approveOwner(newOwner, ownerToRemove.getUserName(), true);
            storeUT.appointAdditionManager(ownerToRemove, managerUser);
            storeUT.appointAdditionManager(ownerToRemove, managerUser1);

            when(managerUser.removeManagedStore(storeUT)).thenReturn(true);
            when(managerUser1.removeManagedStore(storeUT)).thenReturn(true);
            when(managerUser.removeManagedStore(storeUT.getStoreId())).thenReturn(true);
            when(managerUser1.removeManagedStore(storeUT.getStoreId())).thenReturn(true);

            managerStore = mock(MangerStore.class);
            when(managerStore.getAppointedManager()).thenReturn(managerUser);
            managerStore1 = mock(MangerStore.class);
            when(managerStore1.getAppointedManager()).thenReturn(managerUser1);

            int amountOfManagers = storeUT.getManagersStore().size();
            Assertions.assertTrue(storeUT.removeOwner(owner, ownerToRemove));
            Assertions.assertFalse(storeUT.getOwnersUsername().contains(ownerToRemove.getUserName()));
            Assertions.assertEquals(amountOfManagers - 2, storeUT.getManagersStore().size());
        }

        /**
         * This test check if the removeOwner method succeeds
         * when the parameters are correct, with that managers was appointed
         * by removed owner.
         */
        @Test
        void removeOwnerWithManagersAndOwner(){
            UserSystem ownerToRemove = mock(UserSystem.class);
            when(ownerToRemove.getUserName()).thenReturn("ownerToRemove");
            when(ownerToRemove.getOwnedStores()).thenReturn(new HashSet<>());

            storeUT.addOwner(owner, ownerToRemove);
            storeUT.approveOwner(owner, ownerToRemove.getUserName(), true);
            storeUT.addOwner(ownerToRemove, newOwner);
            storeUT.approveOwner(owner, newOwner.getUserName(), true);
            storeUT.appointAdditionManager(newOwner, managerUser);
            storeUT.appointAdditionManager(newOwner, managerUser1);

            when(managerUser.removeManagedStore(storeUT)).thenReturn(true);
            when(managerUser1.removeManagedStore(storeUT)).thenReturn(true);
            when(managerUser.removeManagedStore(storeUT.getStoreId())).thenReturn(true);
            when(managerUser1.removeManagedStore(storeUT.getStoreId())).thenReturn(true);

            managerStore = mock(MangerStore.class);
            when(managerStore.getAppointedManager()).thenReturn(managerUser);
            managerStore1 = mock(MangerStore.class);
            when(managerStore1.getAppointedManager()).thenReturn(managerUser1);

            when(newOwner.removeOwnedStore(storeUT)).thenReturn(true);
            when(ownerToRemove.removeOwnedStore(storeUT)).thenReturn(true);
            when(newOwner.isOwner(storeUT.getStoreId())).thenReturn(true);
            when(ownerToRemove.isOwner(storeUT.getStoreId())).thenReturn(true);

            Assertions.assertTrue(storeUT.removeOwner(owner, ownerToRemove));

            Assertions.assertEquals(1, storeUT.getOwnersUsername().size());
            Assertions.assertEquals(0, storeUT.getManagersStore().size());
            Assertions.assertFalse(storeUT.getOwnersUsername().contains(ownerToRemove.getUserName()));
            Assertions.assertFalse(storeUT.getOwnersUsername().contains(newOwner.getUserName()));
        }

        @Test
        void removeOwnerNotAnOwner(){
            UserSystem ownerToRemove = mock(UserSystem.class);
            when(ownerToRemove.getUserName()).thenReturn("ownerToRemove");
            when(ownerToRemove.getOwnedStores()).thenReturn(new HashSet<>());

            storeUT.addOwner(owner, newOwner);
            storeUT.approveOwner(owner, newOwner.getUserName(), true);

            int amountOfOwners = storeUT.getOwnersUsername().size();
            Assertions.assertFalse(storeUT.removeOwner(owner, ownerToRemove));
            Assertions.assertFalse(storeUT.getOwnersUsername().contains(ownerToRemove.getUserName()));
            Assertions.assertEquals(amountOfOwners, storeUT.getOwnersUsername().size());
        }

        @Test
        void removeOwnerHimself(){
            int amountOfOwners = storeUT.getOwnersUsername().size();
            Assertions.assertFalse(storeUT.removeOwner(owner, owner));
            Assertions.assertTrue(storeUT.getOwnersUsername().contains(owner.getUserName()));
            Assertions.assertEquals(amountOfOwners, storeUT.getOwnersUsername().size());
        }
        @Test
        void getOwnerToRemovePositive(){
            UserSystem ownerToRemove = mock(UserSystem.class);
            when(ownerToRemove.getUserName()).thenReturn("ownerToRemove");
            when(ownerToRemove.getOwnedStores()).thenReturn(new HashSet<>());

            storeUT.addOwner(owner, newOwner);
            storeUT.approveOwner(owner, newOwner.getUserName(), true);
            storeUT.addOwner(owner, ownerToRemove);
            storeUT.approveOwner(newOwner, ownerToRemove.getUserName(), true);

            Assertions.assertEquals(ownerToRemove, storeUT.getOwnerToRemove(owner, ownerToRemove.getUserName()));
            Assertions.assertTrue(storeUT.getOwnersUsername().contains(ownerToRemove.getUserName()));
        }

        @Test
        void getOwnerToRemoveNegative(){
            UserSystem ownerToRemove = mock(UserSystem.class);
            when(ownerToRemove.getUserName()).thenReturn("ownerToRemove");
            when(ownerToRemove.getOwnedStores()).thenReturn(new HashSet<>());

            storeUT.addOwner(owner, newOwner);
            storeUT.approveOwner(owner, newOwner.getUserName(), true);

            Assertions.assertFalse(storeUT.getOwnersUsername().contains(ownerToRemove.getUserName()));
            Assertions.assertThrows(NoOwnerInStoreException.class, ()->
            {storeUT.getOwnerToRemove(owner, ownerToRemove.getUserName());
            });
        }

        @Test
        void getMySubOwnersPositive(){
            UserSystem newOwner2 = mock(UserSystem.class);
            when(newOwner2.getUserName()).thenReturn("newOwner2");
            when(newOwner2.getOwnedStores()).thenReturn(new HashSet<>());

            storeUT.addOwner(owner, newOwner);
            storeUT.approveOwner(owner, newOwner.getUserName(), true);
            storeUT.addOwner(owner, newOwner2);
            storeUT.approveOwner(newOwner, newOwner2.getUserName(), true);

            Assertions.assertTrue(storeUT.getMySubOwners(owner.getUserName()).contains(newOwner.getUserName()));
            Assertions.assertTrue(storeUT.getMySubOwners(owner.getUserName()).contains(newOwner2.getUserName()));
        }

        @Test
        void getMySubOwnersNegative(){
            UserSystem newOwner2 = mock(UserSystem.class);
            when(newOwner2.getUserName()).thenReturn("ownerToRemove");
            when(newOwner2.getOwnedStores()).thenReturn(new HashSet<>());

            storeUT.addOwner(owner, newOwner);
            storeUT.approveOwner(owner, newOwner.getUserName(), true);

            Assertions.assertFalse(storeUT.getOwnersUsername().contains(newOwner2.getUserName()));
            Assertions.assertEquals(new LinkedList<>(), storeUT.getMySubOwners(newOwner2.getUserName()));
        }

        @Test
        void isOwnerPositive(){
            storeUT.addOwner(owner, newOwner);
            storeUT.approveOwner(owner, newOwner.getUserName(), true);
            Assertions.assertTrue(storeUT.isOwner(owner));
            Assertions.assertTrue(storeUT.isOwner(newOwner));
        }

        @Test
        void isOwnerNegative(){
            Assertions.assertTrue(storeUT.isOwner(owner));
            Assertions.assertFalse(storeUT.isOwner(newOwner));
        }

        /**
         * @pre the appointing owner is registered as the store owner
         * adding an owner by an store owner
         */
        @Test
        void addOwnerPositive() {
            // there are no owners who need to approve this
            Assertions.assertTrue(storeUT.addOwner(owner, newOwner).isEmpty());    //Success: owner appointing a newOwner as a new owner
        }

        /**
         * @pre the appointing owner is not registered as the store owner
         * adding an owner by not store owner
         */
        @Test
        void addOwnerNegativeNotOwner() {
            UserSystem newOwner = mock(UserSystem.class);   //the user the fake owner want to appoint as a new store owner
            when(newOwner.getUserName()).thenReturn("newOwner");
            // null is returned since owner isn't really a store owner
            Assertions.assertNull(storeUT.addOwner(fakeOwner, newOwner)); //Fail: owner appointing a newOwner as a new owner
        }

        /**
         * @pre the appointed owner is already an owner in the store
         * adding an already owner by owner
         */
        @Test
        void addOwnerNegativeAlreadyOwner() {
            Assertions.assertNull(storeUT.addOwner(owner, owner)); //Fail: owner appointing a an already owner to be owner
        }

        @Test
        void appointAdditionManagerPositive(){
            UserSystem newOwner1 = mock(UserSystem.class);
            when(newOwner1.getUserName()).thenReturn("newOwner1");

            storeUT.addOwner(owner, newOwner);
            storeUT.approveOwner(owner, newOwner.getUserName(), true);
            storeUT.addOwner(newOwner, newOwner1);
            storeUT.approveOwner(owner, newOwner1.getUserName(), true);

            Assertions.assertEquals(0, storeUT.getManagersStore().size());
            Assertions.assertEquals(managerUser.getUserName(), storeUT.appointAdditionManager(newOwner, managerUser).getAppointedManager().getUserName());
            Assertions.assertEquals(1, storeUT.getManagersStore().size());
            Assertions.assertEquals(managerUser1.getUserName(), storeUT.appointAdditionManager(newOwner1, managerUser1).getAppointedManager().getUserName());
            Assertions.assertEquals(2, storeUT.getManagersStore().size());
        }

        @Test
        void appointAdditionManagerNegative(){
            // not an owner adding a manager
            Assertions.assertEquals(0, storeUT.getManagersStore().size());
            Assertions.assertNull(storeUT.appointAdditionManager(newOwner, managerUser));
            Assertions.assertEquals(0, storeUT.getManagersStore().size());
        }

        /**
         * owner remove one of managers appointed by him
         */
        @Test
        void removeManagerPositive() {
            storeUT.appointAdditionManager(owner, managerUser);
            Assertions.assertEquals(1, storeUT.getManagersStore().size());

            managerStore = mock(MangerStore.class);
            when(managerStore.getAppointedManager()).thenReturn(managerUser);
            when(managerUser.removeManagedStore(storeUT)).thenReturn(true);
            when(managerUser.removeManagedStore(storeUT.getStoreId())).thenReturn(true);
            managerStore = mock(MangerStore.class);
            when(managerStore.getAppointedManager()).thenReturn(managerUser);

            Assertions.assertTrue(storeUT.removeManager(owner, managerUser)); //success: owner can remove manager he appointed
            Assertions.assertEquals(0, storeUT.getManagersStore().size());
        }

        /**
         * user who is not owner cannot remove one of managers appointed by someone else
         */
        @Test
        void removeManagerNegative() {
            storeUT.appointAdditionManager(owner, managerUser);
            Assertions.assertEquals(1, storeUT.getManagersStore().size());

            managerStore = mock(MangerStore.class);
            when(managerStore.getAppointedManager()).thenReturn(managerUser);
            when(managerUser.removeManagedStore(storeUT)).thenReturn(true);
            when(managerUser.removeManagedStore(storeUT.getStoreId())).thenReturn(true);
            managerStore = mock(MangerStore.class);
            when(managerStore.getAppointedManager()).thenReturn(managerUser);

            Assertions.assertFalse(storeUT.removeManager(fakeOwner, managerUser)); //success: owner can remove manager he appointed
            Assertions.assertEquals(1, storeUT.getManagersStore().size());
        }
        /**
         * owner can add permissions to a manager he appointed
         */
        @Test
        void addPermissionToManagerPositive() {
            storeUT.appointAdditionManager(owner, managerUser);
            Assertions.assertEquals(1, storeUT.getManagersStore().size());

            managerStore = mock(MangerStore.class);
            when(managerStore.getAppointedManager()).thenReturn(managerUser);
            when(managerStore.addStorePermission(StorePermission.EDIT_PRODUCT)).thenReturn(true);
            //success: permission added to manager
            Assertions.assertTrue(storeUT.addPermissionToManager(owner, managerUser, StorePermission.EDIT_PRODUCT));
        }

        /**
         * not owner try to add permissions to a manager
         */
        @Test
        void AddPermissionToManagerNegative() {
            storeUT.appointAdditionManager(owner, managerUser);
            Assertions.assertEquals(1, storeUT.getManagersStore().size());

            managerStore = mock(MangerStore.class);
            when(managerStore.getAppointedManager()).thenReturn(managerUser);
            when(managerStore.addStorePermission(StorePermission.EDIT_PRODUCT)).thenReturn(true);
            //fail: permission is not added to manager
            Assertions.assertFalse(storeUT.addPermissionToManager(fakeOwner, managerUser, StorePermission.EDIT_PRODUCT));
        }

        /**
         * get exist manager in store
         */
        @Test
        void getManagerPositive() {
            storeUT.appointAdditionManager(owner, managerUser);
            Assertions.assertEquals(1, storeUT.getManagersStore().size());

            managerStore = mock(MangerStore.class);
            when(managerStore.getAppointedManager()).thenReturn(managerUser);

            Assertions.assertEquals(managerUser.getUserName(), storeUT.getManager(owner, managerUser.getUserName()).getUserName());
        }

        /**
         * try to get a manager with owner which didn't appoint him
         */
        @Test
        void getManagerNegative() {
            storeUT.appointAdditionManager(owner, managerUser);
            Assertions.assertEquals(1, storeUT.getManagersStore().size());

            managerStore = mock(MangerStore.class);
            when(managerStore.getAppointedManager()).thenReturn(managerUser);

            Assertions.assertNull(storeUT.getManager(fakeOwner, managerUser.getUserName()));
        }

        @Test
        void getOperationsCanDoPositive(){
            storeUT.appointAdditionManager(owner, managerUser);
            Assertions.assertEquals(1, storeUT.getManagersStore().size());
            managerStore = mock(MangerStore.class);
            when(managerStore.getAppointedManager()).thenReturn(managerUser);

            List<String> operations = storeUT.getOperationsCanDo(managerUser);
            Assertions.assertNotNull(operations);
            Assertions.assertEquals(1, operations.size());
            Assertions.assertTrue(operations.contains("view"));
        }

        @Test
        void getOperationsCanDoNegative(){
            List<String> operations = storeUT.getOperationsCanDo(managerUser);
            Assertions.assertEquals(new ArrayList<>(), operations);
        }

        @Test
        void getMySubMangersPositive(){
            storeUT.appointAdditionManager(owner, managerUser);
            storeUT.appointAdditionManager(owner, managerUser1);

            when(managerUser.removeManagedStore(storeUT)).thenReturn(true);
            when(managerUser1.removeManagedStore(storeUT)).thenReturn(true);
            when(managerUser.removeManagedStore(storeUT.getStoreId())).thenReturn(true);
            when(managerUser1.removeManagedStore(storeUT.getStoreId())).thenReturn(true);
            managerStore = mock(MangerStore.class);
            when(managerStore.getAppointedManager()).thenReturn(managerUser);
            managerStore1 = mock(MangerStore.class);
            when(managerStore1.getAppointedManager()).thenReturn(managerUser1);

            List<MangerStore> mangerStoreList = storeUT.getMySubMangers(owner.getUserName());
            Assertions.assertEquals(2, mangerStoreList.size());
            for (MangerStore mangerStore: mangerStoreList)
                Assertions.assertTrue(mangerStore.getAppointedManager().getUserName().equals(managerUser.getUserName())
                        || mangerStore.getAppointedManager().getUserName().equals(managerUser1.getUserName()));
        }

        @Test
        void getMySubMangersNegative(){
            Assertions.assertEquals(0, storeUT.getMySubMangers(fakeOwner.getUserName()).size());
        }

        /**
         * owner remove permission from manager permissions list
         */
        @Test
        void removePermissionPositive() {
            storeUT.appointAdditionManager(owner, managerUser);
            managerStore = mock(MangerStore.class);
            when(managerStore.getAppointedManager()).thenReturn(managerUser);
            when(managerStore.removeStorePermission(StorePermission.VIEW)).thenReturn(true);
            //success : owner can remove permission from the manager's permissions list
            Assertions.assertTrue(storeUT.removePermission(owner, managerUser, StorePermission.VIEW));
        }

        /**
         * not an owner try to remove permission from manager permissions list
         */
        @Test
        void removePermissionNegative() {
            storeUT.appointAdditionManager(owner, managerUser);
            managerStore = mock(MangerStore.class);
            when(managerStore.getAppointedManager()).thenReturn(managerUser);
            when(managerStore.addStorePermission(StorePermission.VIEW)).thenReturn(true);
            //fail: the user trying to remove is not an owner who appointed the manager
            Assertions.assertFalse(storeUT.removePermission(fakeOwner, managerUser, StorePermission.VIEW));
        }

        @Test
        void getPermissionOfManagerPositive(){
            storeUT.appointAdditionManager(owner, managerUser);
            Assertions.assertEquals(1, storeUT.getManagersStore().size());
            managerStore = mock(MangerStore.class);
            when(managerStore.getAppointedManager()).thenReturn(managerUser);

            Set<StorePermission> permissions = storeUT.getPermissionOfManager(owner, managerUser);
            Assertions.assertNotNull(permissions);
            Assertions.assertEquals(1, permissions.size());
            Assertions.assertEquals("view", permissions.stream().findFirst().get().function);
        }

        @Test
        void getPermissionOfManagerNegative(){
            Set<StorePermission> operations = storeUT.getPermissionOfManager(fakeOwner, managerUser);
            Assertions.assertNull(operations);
        }

        @Test
        void getManagersStorePositive(){
            storeUT.addOwner(owner, newOwner);
            storeUT.approveOwner(owner, newOwner.getUserName(), true);
            storeUT.appointAdditionManager(newOwner, managerUser);
            storeUT.appointAdditionManager(newOwner, managerUser1);

            managerStore = mock(MangerStore.class);
            when(managerStore.getAppointedManager()).thenReturn(managerUser);
            managerStore1 = mock(MangerStore.class);
            when(managerStore1.getAppointedManager()).thenReturn(managerUser1);

            Set<MangerStore> mangerStoreSet = storeUT.getManagersStore();
            Assertions.assertEquals(2, mangerStoreSet.size());
            for (MangerStore mangerStore:mangerStoreSet)
                Assertions.assertTrue(mangerStore.getAppointedManager().getUserName().equals(managerUser.getUserName())
                        || mangerStore.getAppointedManager().getUserName().equals(managerUser1.getUserName()));
        }

        @Test
        void getManagersStoreNegativeNoManagers(){
            Set<MangerStore> mangerStoreSet = storeUT.getManagersStore();
            Assertions.assertEquals(0, mangerStoreSet.size());
        }

        @Test
        void managerHavePermissionPositive(){
            storeUT.appointAdditionManager(owner, managerUser);
            Assertions.assertTrue(storeUT.managerHavePermission(managerUser.getUserName(), StorePermission.VIEW));
            Assertions.assertFalse(storeUT.managerHavePermission(managerUser.getUserName(), StorePermission.EDIT_PRODUCT));
            storeUT.addPermissionToManager(owner, managerUser, StorePermission.EDIT_PRODUCT);
            Assertions.assertTrue(storeUT.managerHavePermission(managerUser.getUserName(), StorePermission.EDIT_PRODUCT));
        }

        @Test
        void managerHavePermissionNegative(){
            Assertions.assertFalse(storeUT.managerHavePermission(managerUser.getUserName(), StorePermission.VIEW));
        }

        @Test
        void getPermissionCantDoPositive(){
            storeUT.appointAdditionManager(owner, managerUser);
            Set <StorePermission> storePermissions = storeUT.getPermissionCantDo(owner, managerUser);
            Assertions.assertFalse(storePermissions.contains(StorePermission.VIEW));
            Assertions.assertTrue(storePermissions.contains(StorePermission.EDIT_PRODUCT));

            storeUT.addPermissionToManager(owner, managerUser, StorePermission.EDIT_PRODUCT);
            storePermissions = storeUT.getPermissionCantDo(owner, managerUser);
            Assertions.assertFalse(storePermissions.contains(StorePermission.VIEW));
            Assertions.assertFalse(storePermissions.contains(StorePermission.EDIT_PRODUCT));
        }

        @Test
        void getPermissionCantDoNegative(){
            storeUT.appointAdditionManager(owner, managerUser);
            Assertions.assertNull(storeUT.getPermissionCantDo(fakeOwner, managerUser));
        }

        /**
         * add new purchase policy to store
         */
        @Test
        public void addPurchasePolicyPositive() {
            Assertions.assertEquals(0, storeUT.getPurchasePolicies().size());
            storeUT.addPurchasePolicy(owner, purchasePolicy);
            Assertions.assertEquals(1, storeUT.getPurchasePolicies().size());
        }

        @Test
        public void addPurchasePolicyNegative() {
            Assertions.assertEquals(0, storeUT.getPurchasePolicies().size());
            Assertions.assertThrows(NotAdministratorException.class, ()->
            {storeUT.addPurchasePolicy(fakeOwner, purchasePolicy);
            });
            Assertions.assertEquals(0, storeUT.getPurchasePolicies().size());
        }

        /**
         * check the case of purchase that pass the exist store policy
         * empty case: should approve there are no policies
         */
        @Test
        public void isApprovedPurchaseEmptyPoliciesPositive() {
            Map<Product, Integer> productBag = new HashMap<>();
            productBag.put(product, 1);
            BillingAddress billingAddress = mock(BillingAddress.class);
            Assertions.assertTrue(storeUT.isApprovedPurchasePolicies(productBag, billingAddress));
        }

        /**
         * check the case of purchase that pass the exist store policy
         * should approve with buyer from the permmited country
         */
        @Test
        public void isApprovedPurchasePoliciesPositive() {
            Map<Product, Integer> productBag = new HashMap<>();
            productBag.put(product, 1);
            Purchase purchase = mock(Purchase.class);
            when(purchase.getPurchasePolicy()).thenReturn(new PurchasePolicy());
            when(purchase.getPurchaseType()).thenReturn(PurchaseType.SHOPPING_BAG_DETAILS);
            storeUT.addPurchasePolicy(owner, purchase);
            BillingAddress billingAddress = BillingAddress.builder().country("Israel").build();
            Assertions.assertFalse(storeUT.isApprovedPurchasePolicies(productBag, billingAddress));
        }


        /**
         * check the case of purchase that pass the exist store policy
         * negative case: should approve with buyer from not permmited country
         */
        @Test
        public void isApprovedPurchasePoliciesNegative() {
            Map<Product, Integer> productBag = new HashMap<>();
            productBag.put(product, 1);
            Purchase purchase = mock(Purchase.class);
            when(purchase.getPurchasePolicy()).thenReturn(new PurchasePolicy());
            when(purchase.getPurchaseType()).thenReturn(PurchaseType.PRODUCT_DETAILS);
            storeUT.addPurchasePolicy(owner, purchase);
            BillingAddress billingAddress = BillingAddress.builder().country("Israel").build();
            Assertions.assertFalse(storeUT.isApprovedPurchasePolicies(productBag, billingAddress));
        }
        /**
         * edit existed purchase in store
         */
        @Test
        public void addEditPurchasePositive() {
            purchasePolicy = mock(Purchase.class);
            when(purchasePolicy.getPurchaseType()).thenReturn(PurchaseType.PRODUCT_DETAILS);
            Assertions.assertEquals(purchasePolicy, storeUT.addPurchasePolicy(owner, purchasePolicy));
            when(purchasePolicy.getPurchaseType()).thenReturn(PurchaseType.SHOPPING_BAG_DETAILS);
            Assertions.assertEquals(purchasePolicy, storeUT.addEditPurchase(owner, purchasePolicy));
        }

        /**
         * edit not existed purchase in store
         */
        @Test
        public void addEditPurchaseNegative() {
            purchasePolicy = mock(Purchase.class);
            when(purchasePolicy.getPurchaseType()).thenReturn(PurchaseType.PRODUCT_DETAILS);
            Assertions.assertEquals(purchasePolicy, storeUT.addPurchasePolicy(owner, purchasePolicy));
            Assertions.assertThrows(Exception.class, ()-> storeUT.addEditPurchase(fakeOwner, purchasePolicy));
        }

        @Test
        public void applyDiscountPoliciesPositive() {
            Map<Product, Integer> productBag = new HashMap<>();
            productBag.put(product, 1);
            Discount discount1 = mock(Discount.class);
            storeUT.addDiscount(owner, discount1);
            Assertions.assertFalse(discount1.isApplied());
            storeUT.applyDiscountPolicies(productBag);
            Assertions.assertFalse(discount1.isApplied());
        }

        @Test
        public void applyDiscountPoliciesNegative() {
            Map<Product, Integer> productBag = new HashMap<>();
            productBag.put(product, 1);
            Discount discount1 = mock(Discount.class);
            storeUT.addDiscount(owner,discount1);
            when(product.getCost()).thenReturn((double) -10);
            storeUT.applyDiscountPolicies(productBag);
            Assertions.assertFalse(discount1.isApplied());
        }

        @Test
        public void removeDiscountPositive(){
            Discount discount1 = mock(Discount.class);
            int discount1Id = (int) storeUT.addDiscount(owner, discount1).getDiscountId();
            when(discount1.getDiscountId()).thenReturn((long) discount1Id);
            Assertions.assertTrue(storeUT.removeDiscount(owner, (int) discount1.getDiscountId()));
        }

        @Test
        public void removeDiscountNegative(){
            Discount discount1 = mock(Discount.class);
            int discount1Id = (int) storeUT.addDiscount(owner, discount1).getDiscountId();
            when(discount1.getDiscountId()).thenReturn((long) discount1Id);
            Assertions.assertThrows(Exception.class, ()->
                    storeUT.removeDiscount(fakeOwner, (int) discount1.getDiscountId()));
        }

        @Test
        void getStoreDiscountsPositive(){
            Discount discount1 = mock(Discount.class);
            int discount1Id = (int) storeUT.addDiscount(owner, discount1).getDiscountId();
            when(discount1.getDiscountId()).thenReturn((long) discount1Id);
            Assertions.assertEquals(1, storeUT.getStoreDiscounts(owner).size());
            Assertions.assertTrue(storeUT.getStoreDiscounts(owner).contains(discount1));
        }

        @Test
        void getStoreDiscountsNegative(){
            Discount discount1 = mock(Discount.class);
            int discount1Id = (int) storeUT.addDiscount(owner, discount1).getDiscountId();
            when(discount1.getDiscountId()).thenReturn((long) discount1Id);
            Assertions.assertThrows(Exception.class,()->
                    storeUT.getStoreDiscounts(fakeOwner));
        }

        @Test
        public void addDiscountPositive() {
            Assertions.assertEquals(0, storeUT.getDiscounts().size());
            Assertions.assertNotNull(storeUT.addDiscount(owner, discount));
            Assertions.assertEquals(1, storeUT.getDiscounts().size());
        }

        @Test
        public void addDiscountNegative() {
            Assertions.assertEquals(0, storeUT.getDiscounts().size());
            Assertions.assertThrows(NotAdministratorException.class, ()->
                    storeUT.addDiscount(fakeOwner, discount));
            Assertions.assertEquals(0, storeUT.getDiscounts().size());
        }

        @Test
        public void editDiscountPositive() {
            discount = mock(Discount.class);
            when(discount.getDiscountId()).thenReturn((long) 0);
            when(discount.getDiscountPercentage()).thenReturn((double) 10);
            storeUT.addDiscount(owner, discount);
            when(discount.getDiscountPercentage()).thenReturn((double) 20);
            Assertions.assertEquals(20, storeUT.addEditDiscount(owner, discount).getDiscountPercentage());
        }

        @Test
        public void editDiscountNegative() {
            discount = mock(Discount.class);
            when(discount.getDiscountId()).thenReturn((long) 0);
            when(discount.getDiscountPercentage()).thenReturn((double) 10);
            storeUT.addDiscount(owner, discount);
            when(discount.getDiscountPercentage()).thenReturn((double) 20);
            Assertions.assertThrows(NotAdministratorException.class, ()->
                    storeUT.addEditDiscount(fakeOwner, discount));
        }

        @Test
        void getDiscountSimplePositive(){
            Discount discount1 = mock(Discount.class);
            Discount discount2 = mock(Discount.class);
            Discount discount3 = mock(Discount.class);
            when(discount1.getDiscountId()).thenReturn((long) 1);
            when(discount2.getDiscountId()).thenReturn((long) 2);
            when(discount3.getDiscountId()).thenReturn((long) 3);
            when(discount1.getDiscountType()).thenReturn(DiscountType.COMPOSE);
            when(discount2.getDiscountType()).thenReturn(DiscountType.VISIBLE);
            when(discount3.getDiscountType()).thenReturn(DiscountType.CONDITIONAL_PRODUCT);

            storeUT.addDiscount(owner, discount1);
            storeUT.addDiscount(owner, discount2);
            storeUT.addDiscount(owner, discount3);

            List<Discount> discounts = storeUT.getDiscountSimple();
            Assertions.assertEquals(2, discounts.size());
            for (Discount discount: discounts)
                Assertions.assertNotEquals(DiscountType.COMPOSE, discount.getDiscountType());
        }

        @Test
        void getDiscountSimpleNegative(){
            List<Discount> discounts = storeUT.getDiscountSimple();
            Assertions.assertEquals(0, discounts.size());
        }

        private void addProductAsEditProductSetup() {
            product = new Product("Crashpad", ProductCategory.SPORTING_GOODS, 2, 987, 12);
            storeUT.addNewProduct(owner, product);

        }
    }

    // **************************************** Integration Test ****************************************
    @Nested
    public class StoreTestIntegration {
        Discount discount1;
        Discount discount2;
        Discount discount3;
        Discount discount4;
        ComposedDiscount composedDiscount;
        Product product2;
        Product product3;

        @BeforeEach
        void setUp() {
            newOwner = new UserSystem("newOwner", "new", "york", "uni");
            owner = new UserSystem("owner", "I", "want", "sleep");

            //new manager to add
            newOwnerReal = new UserSystem("newOwnerReal", "n", "y", "u");   //the user the owner want to appoint as a new store owner

            //manager for get and add owner tests
            managerUser = new UserSystem("managerUser", "Alex", "Alex", "Alex");
            managerStore = new MangerStore(managerUser);

            managerUser1 = new UserSystem("managerUser1", "Alex", "Alex", "Alex");
            managerStore1 = new MangerStore(managerUser1);

            //not owner of the store
            fakeOwnerReal = new UserSystem("fakeOwnerReal", "Donald", "Trump", "DT123");

            //owner and opener of the store
            ownerRealUser = new UserSystem("ownerRealUser", "micha", "toti", "pass");
            storeUT = new Store(ownerRealUser, "Store under test");
            product = new Product("product", ProductCategory.SPORTING_GOODS, 1, 11, storeUT.getStoreId());
            product2 = Product.builder()
                    .cost(10)
                    .productSn(66)
                    .originalCost(10)
                    .name("product2")
                    .amount(100)
                    .storeId(storeUT.getStoreId())
                    .build();
            product3 = Product.builder()
                    .cost(20)
                    .productSn(67)
                    .originalCost(20)
                    .name("product3")
                    .storeId(storeUT.getStoreId())
                    .build();
            HashMap<Product,Integer> postProducts = new HashMap();
            postProducts.put(product2,1);
            postProducts.put(product3,1);
            ConditionalProductDiscount conditionalProductDiscount = ConditionalProductDiscount.builder()
                    .productsUnderThisDiscount(new HashMap<>())
                    //.amountOfProductsForApplyDiscounts(postProducts)
                    .build();
            Calendar endTime = Calendar.getInstance();
            endTime.set(3000,1,1);
            List<Discount> discountsComposed = new ArrayList<>();
            discount3 = Discount.builder().discountPercentage(10)
                    .endTime(endTime)
                    .description("cond")
                    .discountId(33)
                    .discountType(DiscountType.CONDITIONAL_PRODUCT)
                    .discountPolicy(conditionalProductDiscount)
                    .build();
            discountsComposed.add(discount3);
            Map<Product,Integer> productsVis = new HashMap<>();
            productsVis.put(product,1);
            discount1 = Discount.builder().discountPercentage(10)
                    .endTime(endTime)
                    .description("vis")
                    .discountId(34)
                    .discountType(DiscountType.VISIBLE)
                    .discountPolicy(VisibleDiscount.builder().amountOfProductsForApplyDiscounts(productsVis)
                            .build()).build();
            discountsComposed.add(discount1);
            discount2 = Discount.builder().discountPercentage(10)
                    .endTime(endTime)
                    .description("store")
                    .discountId(35)
                    .discountType(DiscountType.CONDITIONAL_STORE)
                    .discountPolicy(ConditionalStoreDiscount.builder().minPrice(4.8)
                            .build()).build();
            discountsComposed.add(discount2);
            composedDiscount = ComposedDiscount.builder()
                    .composedDiscounts(discountsComposed)
                    .compositeOperator(CompositeOperator.AND)
                    .build();
            Calendar time = Calendar.getInstance();
            time.set(3000,1,1);
            discount4 = Discount.builder()
                    .discountId(77)
                    .discountPercentage(1)
                    .discountType(DiscountType.COMPOSE)
                    .description("compose")
                    .endTime(time)
                    .discountPolicy(composedDiscount)
                    .build();
            discount = Discount.builder().build();  //add simple cased discount
            purchasePolicy = Purchase.builder().build(); //add simple cased purchase policy
        }

        /**
         * owner adds new product
         */
        @Test
        void addNewProductPositive() {
            Assertions.assertEquals(0, storeUT.getProducts().size());     //verify there are no products in store
            Assertions.assertTrue(storeUT.addNewProduct(ownerRealUser, product));    //success: the product added by the owner
            Assertions.assertEquals(1, storeUT.getProducts().size());     //verify amount of products in list increased
        }

        /**
         * user that is not an owner adds new product
         */
        @Test
        void addNewProductNegative() {
            Assertions.assertEquals(0, storeUT.getProducts().size());     //verify there are no products in store
            //fail: user which is no owner cannot add product to store
            Assertions.assertFalse(storeUT.addNewProduct(fakeOwnerReal, product));
            Assertions.assertEquals(0, storeUT.getProducts().size());     //verify there are no products in store
        }

        /**
         * owner remove a product exists in store
         */
        @Test
        void removeExistsProductFromStorePositive() {
            //setup for remove product
            addNewProductSetUp();  //add product to the store
            Assertions.assertEquals(1, storeUT.getProducts().size()); //verify we starts with no products in store
            //success: remove product from store
            Assertions.assertTrue(storeUT.removeProductFromStore(ownerRealUser, product.getProductSn()));    //success: product is removed
            Assertions.assertEquals(0, storeUT.getProducts().size()); //verify we decreased no. of products in store
        }

        /**
         * owner remove a product that doesn't exist in store
         */
        @Test
        void removeNotExistsProductFromStoreNegative() {
            //setup for remove product
            addNewProductSetUp();  //add product to the store
            Assertions.assertEquals(1, storeUT.getProducts().size()); //verify we starts with no products in store
            //add a product to the store which will be removed for the test
            //fail: cant remove product which is not exist
            Assertions.assertFalse(storeUT.removeProductFromStore(ownerRealUser, 122));    //fail: product not exist is not removed
            Assertions.assertEquals(1, storeUT.getProducts().size()); //verify we decreased no. of products in store
        }

        /**
         * user which is not an owner of the store removes a product that doesn't exist in store
         */
        @Test
        void notOwnerRemoveExistsProductFromStoreNegative() {
            //setup for remove product. add a product to the store which will be removed for the test
            addNewProductSetUp();  //add product to the store
            Assertions.assertEquals(1, storeUT.getProducts().size()); //verify we starts with no products in store
            //fail: only owner can remove products from his store
            Assertions.assertFalse(storeUT.removeProductFromStore(fakeOwnerReal, product.getProductSn()));    //fail: not owner cannot remove product
            Assertions.assertEquals(1, storeUT.getProducts().size()); //verify we decreased no. of products in store
        }
        /**
         * owner validates that he can edit a given product
         */
        @Test
        void validateCanEditProductsPositive(){
            addNewProductSetUp();
            Assertions.assertTrue(storeUT.validateCanEditProducts(ownerRealUser,product.getProductSn()));
        }

        /**
         * user that is not owner validates that he can edit a given product
         */
        @Test
        void validateCanEditProductsNegative(){
            addNewProductSetUp();
            Assertions.assertFalse(storeUT.validateCanEditProducts(managerUser,product.getProductSn()));
        }

        /**
         * setting an existing product in store with product properties by an owner
         */
        @Test
        void editProductPositive() {
            //setup for adding a product to store
            addProductAsEditProductSetup();
            int productSN = product.getProductSn();  //the serial number of the product
            String name = "airpods";    //the new name
            String category = "electronics";   //new category
            int amount = 4; //new amount
            double cost = 100000.5; //new cost
            //success: the product edited
            Assertions.assertTrue(storeUT.editProduct(ownerRealUser, productSN, name, category, amount, cost));
        }

        /**
         * setting an existing product in store but not by an owner so should fail.
         */
        @Test
        void notOwnerEditProductNegative() {
            //setup for adding a product to store
            addProductAsEditProductSetup();
            //not owner
            int productSN = 0;  //the serial number of the product
            String name = "airpods";    //the new name
            String category = "electronics";   //new category
            int amount = 4; //new amount
            double cost = 100000.5; //new cost
            //fail: the product is not edited
            Assertions.assertFalse(storeUT.editProduct(fakeOwnerReal, productSN, name, category, amount, cost));

        }

        /**
         * setting not existing product in store with product properties by an owner
         */
        @Test
        void editNotExistProductNegative() {
            //setup for adding a product to store
            addProductAsEditProductSetup();
            int productSN = 23;  //the serial number of the product
            String name = "airpods";    //the new name
            String category = "electronics";   //new category
            int amount = 4; //new amount
            double cost = 100000.5; //new cost
            //fail: the product isn't edited
            Assertions.assertFalse(storeUT.editProduct(ownerRealUser, productSN, name, category, amount, cost));
        }

        /**
         * creates a new receipt with right parameters
         */
        @Test
        void createReceiptPositive() {
            Map<Product, Integer> products = new HashMap<>();
            products.put(product2, 1);
            shoppingBag = new ShoppingBag();
            shoppingBag.addProductToBag(product2, 1);
            Assertions.assertTrue(assertReceipt(storeUT.createReceipt(shoppingBag, ownerRealUser.getUserName(), 1, 1),
                    storeUT.getStoreId(), ownerRealUser.getUserName(), product2.getCost(), products));    //success: the product added by the owner
        }

        /**
         * creates a new receipt with empty cart
         */
        @Test
        void createReceiptNegative() {
            shoppingBag = new ShoppingBag();
            Receipt returnedReceipt = storeUT.createReceipt(shoppingBag, ownerRealUser.getUserName(), 1, 1);
            Assertions.assertEquals(0, returnedReceipt.getAmountToPay());
        }

        /**
         * check update amount of product in the stock (after purchase)
         */
        @Test
        public void editProductAmountInStockPositive() {
            addNewProductSetUp(); //add product to stock
            Assertions.assertTrue(storeUT.editProductAmountInStock(0, 3));
        }

        /**
         * check not update amount of product that isn't in the stock
         */
        @Test
        public void editProductAmountInStockNegative() {
            addNewProductSetUp(); //add product to stock
            Assertions.assertFalse(storeUT.editProductAmountInStock(1, 3));
        }

        /**
         * check that when product bout is in stock return true
         */
        @Test
        public void isAllInStockPositive() {
            Map<Product, Integer> productBag = new HashMap<>();
            productBag.put(product, 1);
            addNewProductSetUp();   //add the bought product to stock
            shoppingBag = new ShoppingBag(productBag);
            Assertions.assertTrue(storeUT.isAllInStock(shoppingBag));
        }

        /**
         * check that when product bout is not in stock return exception
         */
        @Test
        public void isAllInStockNegative() {
            Map<Product, Integer> productBag = new HashMap<>();
            productBag.put(product, 100000);
            addNewProductSetUp();   //add the bought product to stock
            shoppingBag = new ShoppingBag(productBag);

            Throwable exception = Assertions
                    .assertThrows(NotInStockException.class, () -> storeUT.isAllInStock(shoppingBag));
            Assertions.assertEquals("The product: " + product.getName()
                            + " is out of stock in store: " + storeUT.getStoreName()
                    , exception.getMessage());
        }

        /**
         * check that getOwnersUsername returns the name of the owner of the store
         */
        @Test
        void getOwnersUsernamePositive(){
            Set<String> returnedOwners = storeUT.getOwnersUsername();
            Assertions.assertEquals(1, returnedOwners.size());
            Assertions.assertNotNull(returnedOwners.stream()
                    .filter(owner1 -> owner1.equals(ownerRealUser.getUserName())).findFirst().get());
        }

        /**
         * check that getOwnersUsername doesn't returns the name of a regulat user in the store
         */
        @Test
        void getOwnersUsernameNegative(){
            boolean result = storeUT.getOwnersUsername().stream()
                    .anyMatch(owner1 -> owner1.equals(managerUser.getUserName()));
            Assertions.assertFalse(result);
        }


        /**
         * checks that user that was approve
         */
        @Test
        void approveOwnerPositive(){
            storeUT.addOwner(ownerRealUser, newOwner);
            storeUT.approveOwner(ownerRealUser, newOwner.getUserName(), true);
            Assertions.assertEquals(2, storeUT.getOwnersUsername().size());
            storeUT.addOwner(ownerRealUser, managerUser); // creates an appointing agreement
            Assertions.assertEquals(1, storeUT.getAppointingAgreements().size());
            storeUT.approveOwner(newOwner, managerUser.getUserName(), true);
            Assertions.assertEquals(0, storeUT.getAppointingAgreements().size());
            Assertions.assertEquals(3, storeUT.getOwnersUsername().size());
        }

        /**
         * checks that user that wasn't approved
         */
        @Test
        void dontApproveOwnerPositive(){
            storeUT.addOwner(ownerRealUser, newOwner);
            storeUT.approveOwner(ownerRealUser, newOwner.getUserName(), true);
            Assertions.assertEquals(2, storeUT.getOwnersUsername().size());
            storeUT.addOwner(ownerRealUser, managerUser); // creates an appointing agreement
            Assertions.assertEquals(1, storeUT.getAppointingAgreements().size());
            storeUT.approveOwner(newOwner, managerUser.getUserName(), false);
            Assertions.assertEquals(0, storeUT.getAppointingAgreements().size());
            Assertions.assertEquals(2, storeUT.getOwnersUsername().size());
        }

        /**
         * checks that user that wasn't approved
         */
        @Test
        void approveOwnerNegative(){
            UserSystem notOwner = new UserSystem();
            notOwner.setUserName("notOwner");

            storeUT.addOwner(ownerRealUser, newOwner);
            storeUT.approveOwner(ownerRealUser, newOwner.getUserName(), true);
            Assertions.assertEquals(2, storeUT.getOwnersUsername().size());
            storeUT.addOwner(ownerRealUser, managerUser); // creates an appointing agreement
            Assertions.assertEquals(1, storeUT.getAppointingAgreements().size());

            Assertions.assertFalse(storeUT.getOwnersUsername().contains(notOwner.getUserName()));
            Assertions.assertFalse(storeUT.approveOwner(notOwner, managerUser.getUserName(), true));
            Assertions.assertEquals(1, storeUT.getAppointingAgreements().size());
            Assertions.assertEquals(2, storeUT.getOwnersUsername().size());
        }

        @Test
        void isApproveOwnerPositive(){
            storeUT.addOwner(ownerRealUser, newOwner);
            storeUT.approveOwner(ownerRealUser, newOwner.getUserName(), true);
            Assertions.assertEquals(2, storeUT.getOwnersUsername().size());
            storeUT.addOwner(ownerRealUser, managerUser); // creates an appointing agreement
            Assertions.assertEquals(StatusOwner.WAITING, storeUT.isApproveOwner(managerUser));
        }

        @Test
        void isApproveOwnerNegative(){
            Assertions.assertEquals(StatusOwner.WAITING, storeUT.isApproveOwner(managerUser));
        }

        /**
         * This test check if the removeOwner method succeeds
         * when the parameters are correct, no managers was appointed by removed owner
         */
        @Test
        void removeOwnerNoManagers(){
            UserSystem ownerToRemove = mock(UserSystem.class);
            when(ownerToRemove.getUserName()).thenReturn("ownerToRemove");
            when(ownerToRemove.getOwnedStores()).thenReturn(new HashSet<>());

            storeUT.addOwner(ownerRealUser, newOwner);
            storeUT.approveOwner(ownerRealUser, newOwner.getUserName(), true);
            storeUT.addOwner(ownerRealUser, ownerToRemove);
            storeUT.approveOwner(newOwner, ownerToRemove.getUserName(), true);

            Assertions.assertTrue(storeUT.removeOwner(ownerRealUser, ownerToRemove));
            Assertions.assertFalse(storeUT.getOwnersUsername().contains(ownerToRemove.getUserName()));
        }

        /**
         * This test check if the removeOwner method succeeds
         * when the parameters are correct, with that managers was appointed
         * by removed owner.
         */
        @Test
        void removeOwnerWithManagers(){
            UserSystem ownerToRemove = new UserSystem();
            ownerToRemove.setUserName("ownerToRemove");
            ownerToRemove.setOwnedStores(new HashSet<>());

            storeUT.addOwner(ownerRealUser, newOwner);
            storeUT.approveOwner(ownerRealUser, newOwner.getUserName(), true);
            storeUT.addOwner(ownerRealUser, ownerToRemove);
            storeUT.approveOwner(newOwner, ownerToRemove.getUserName(), true);
            storeUT.appointAdditionManager(ownerToRemove, managerUser);
            storeUT.appointAdditionManager(ownerToRemove, managerUser1);

            Assertions.assertTrue(storeUT.removeOwner(ownerRealUser, ownerToRemove));
            Assertions.assertFalse(storeUT.getOwnersUsername().contains(ownerToRemove.getUserName()));
            Assertions.assertEquals(0, storeUT.getManagersStore().size());
        }

        /**
         * This test check if the removeOwner method succeeds
         * when the parameters are correct, with that managers was appointed
         * by removed owner.
         */
        @Test
        void removeOwnerWithManagersAndOwner(){
            UserSystem ownerToRemove = new UserSystem();
            ownerToRemove.setUserName("ownerToRemove");
            ownerToRemove.setOwnedStores(new HashSet<>());

            storeUT.addOwner(ownerRealUser, ownerToRemove);
            storeUT.approveOwner(ownerRealUser, ownerToRemove.getUserName(), true);
            storeUT.addOwner(ownerToRemove, newOwner);
            storeUT.approveOwner(ownerRealUser, newOwner.getUserName(), true);
            storeUT.appointAdditionManager(newOwner, managerUser);
            storeUT.appointAdditionManager(newOwner, managerUser1);

            Assertions.assertTrue(storeUT.removeOwner(ownerRealUser, ownerToRemove));

            Assertions.assertEquals(1, storeUT.getOwnersUsername().size());
            Assertions.assertEquals(0, storeUT.getManagersStore().size());
            Assertions.assertFalse(storeUT.getOwnersUsername().contains(ownerToRemove.getUserName()));
            Assertions.assertFalse(storeUT.getOwnersUsername().contains(newOwner.getUserName()));
        }

        @Test
        void removeOwnerNotAnOwner(){
            UserSystem ownerToRemove = new UserSystem();
            ownerToRemove.setUserName("ownerToRemove");
            ownerToRemove.setOwnedStores(new HashSet<>());

            storeUT.addOwner(ownerRealUser, newOwner);
            storeUT.approveOwner(ownerRealUser, newOwner.getUserName(), true);

            int amountOfOwners = storeUT.getOwnersUsername().size();
            Assertions.assertFalse(storeUT.removeOwner(ownerRealUser, ownerToRemove));
            Assertions.assertFalse(storeUT.getOwnersUsername().contains(ownerToRemove.getUserName()));
            Assertions.assertEquals(amountOfOwners, storeUT.getOwnersUsername().size());
        }

        @Test
        void removeOwnerHimself(){
            int amountOfOwners = storeUT.getOwnersUsername().size();
            Assertions.assertFalse(storeUT.removeOwner(ownerRealUser, owner));
            Assertions.assertTrue(storeUT.getOwnersUsername().contains(ownerRealUser.getUserName()));
            Assertions.assertEquals(amountOfOwners, storeUT.getOwnersUsername().size());
        }

        @Test
        void getOwnerToRemovePositive(){
            UserSystem ownerToRemove = new UserSystem();
            ownerToRemove.setUserName("ownerToRemove");
            ownerToRemove.setOwnedStores(new HashSet<>());

            storeUT.addOwner(ownerRealUser, newOwner);
            storeUT.approveOwner(ownerRealUser, newOwner.getUserName(), true);
            storeUT.addOwner(ownerRealUser, ownerToRemove);
            storeUT.approveOwner(newOwner, ownerToRemove.getUserName(), true);

            Assertions.assertEquals(ownerToRemove, storeUT.getOwnerToRemove(ownerRealUser, ownerToRemove.getUserName()));
            Assertions.assertTrue(storeUT.getOwnersUsername().contains(ownerToRemove.getUserName()));
        }

        @Test
        void getOwnerToRemoveNegative(){
            UserSystem ownerToRemove = new UserSystem();
            ownerToRemove.setUserName("ownerToRemove");
            ownerToRemove.setOwnedStores(new HashSet<>());

            storeUT.addOwner(ownerRealUser, newOwner);
            storeUT.approveOwner(ownerRealUser, newOwner.getUserName(), true);

            Assertions.assertFalse(storeUT.getOwnersUsername().contains(ownerToRemove.getUserName()));
            Assertions.assertThrows(NoOwnerInStoreException.class, ()->
            {storeUT.getOwnerToRemove(owner, ownerToRemove.getUserName());
            });
        }

        @Test
        void getMySubOwnersPositive(){
            UserSystem newOwner2 = new UserSystem();
            newOwner2.setUserName("newOwner2");
            newOwner2.setOwnedStores(new HashSet<>());

            storeUT.addOwner(ownerRealUser, newOwner);
            storeUT.approveOwner(ownerRealUser, newOwner.getUserName(), true);
            storeUT.addOwner(ownerRealUser, newOwner2);
            storeUT.approveOwner(newOwner, newOwner2.getUserName(), true);

            Assertions.assertTrue(storeUT.getMySubOwners(ownerRealUser.getUserName()).contains(newOwner.getUserName()));
            Assertions.assertTrue(storeUT.getMySubOwners(ownerRealUser.getUserName()).contains(newOwner2.getUserName()));
        }

        @Test
        void getMySubOwnersNegative(){
            UserSystem newOwner2 = new UserSystem();
            newOwner2.setUserName("newOwner2");
            newOwner2.setOwnedStores(new HashSet<>());

            storeUT.addOwner(ownerRealUser, newOwner);
            storeUT.approveOwner(ownerRealUser, newOwner.getUserName(), true);

            Assertions.assertFalse(storeUT.getOwnersUsername().contains(newOwner2.getUserName()));
            Assertions.assertEquals(new LinkedList<>(), storeUT.getMySubOwners(newOwner2.getUserName()));
        }

        @Test
        void isOwnerPositive(){
            storeUT.addOwner(ownerRealUser, newOwner);
            storeUT.approveOwner(ownerRealUser, newOwner.getUserName(), true);
            Assertions.assertTrue(storeUT.isOwner(ownerRealUser));
            Assertions.assertTrue(storeUT.isOwner(newOwner));
        }

        @Test
        void isOwnerNegative(){
            Assertions.assertTrue(storeUT.isOwner(ownerRealUser));
            Assertions.assertFalse(storeUT.isOwner(newOwner));
        }

        /**
         * @pre the appointing owner is registered as the store owner
         * adding an owner by an store owner
         */
        @Test
        void addOwnerPositive() {
            // there are no owners who need to approve this
            Assertions.assertTrue(storeUT.addOwner(ownerRealUser, newOwner).isEmpty());    //Success: owner appointing a newOwner as a new owner
        }

        /**
         * @pre the appointing owner is not registered as the store owner
         * adding an owner by not store owner
         */
        @Test
        void addOwnerNegativeNotOwner() {
            UserSystem newOwner = new UserSystem();   //the user the fake owner want to appoint as a new store owner
            newOwner.setUserName("newOwner");
            // null is returned since owner isn't really a store owner
            Assertions.assertNull(storeUT.addOwner(fakeOwnerReal, newOwner)); //Fail: owner appointing a newOwner as a new owner
        }

        /**
         * @pre the appointed owner is already an owner in the store
         * adding an already owner by owner
         */
        @Test
        void addOwnerNegativeAlreadyOwner() {
            Assertions.assertNull(storeUT.addOwner(ownerRealUser, ownerRealUser)); //Fail: owner appointing a an already owner to be owner
        }


        @Test
        void appointAdditionManagerPositive(){
            UserSystem newOwner1 = new UserSystem();
            newOwner1.setUserName("newOwner1");

            storeUT.addOwner(ownerRealUser, newOwner);
            storeUT.approveOwner(ownerRealUser, newOwner.getUserName(), true);
            storeUT.addOwner(newOwner, newOwner1);
            storeUT.approveOwner(ownerRealUser, newOwner1.getUserName(), true);

            Assertions.assertEquals(0, storeUT.getManagersStore().size());
            Assertions.assertEquals(managerUser.getUserName(), storeUT.appointAdditionManager(newOwner, managerUser).getAppointedManager().getUserName());
            Assertions.assertEquals(1, storeUT.getManagersStore().size());
            Assertions.assertEquals(managerUser1.getUserName(), storeUT.appointAdditionManager(newOwner1, managerUser1).getAppointedManager().getUserName());
            Assertions.assertEquals(2, storeUT.getManagersStore().size());
        }

        @Test
        void appointAdditionManagerNegative(){
            // not an owner adding a manager
            Assertions.assertEquals(0, storeUT.getManagersStore().size());
            Assertions.assertNull(storeUT.appointAdditionManager(newOwner, managerUser));
            Assertions.assertEquals(0, storeUT.getManagersStore().size());
        }

        /**
         * owner remove one of managers appointed by him
         */
        @Test
        void removeManagerPositive() {
            storeUT.appointAdditionManager(ownerRealUser, managerUser);
            Assertions.assertEquals(1, storeUT.getManagersStore().size());
            Assertions.assertTrue(storeUT.removeManager(ownerRealUser, managerUser)); //success: owner can remove manager he appointed
            Assertions.assertEquals(0, storeUT.getManagersStore().size());
        }

        /**
         * user who is not owner cannot remove one of managers appointed by someone else
         */
        @Test
        void removeManagerNegative() {
            storeUT.appointAdditionManager(ownerRealUser, managerUser);
            Assertions.assertEquals(1, storeUT.getManagersStore().size());
            Assertions.assertFalse(storeUT.removeManager(fakeOwnerReal, managerUser)); //success: owner can remove manager he appointed
            Assertions.assertEquals(1, storeUT.getManagersStore().size());
        }

        /**
         * owner can add permissions to a manager he appointed
         */
        @Test
        void addPermissionToManagerPositive() {
            storeUT.appointAdditionManager(ownerRealUser, managerUser);
            Assertions.assertTrue(storeUT.addPermissionToManager(ownerRealUser, managerUser, StorePermission.EDIT_PRODUCT));
        }

        /**
         * not owner try to add permissions to a manager
         */
        @Test
        void AddPermissionToManagerNegative() {
            storeUT.appointAdditionManager(ownerRealUser, managerUser);
            Assertions.assertFalse(storeUT.addPermissionToManager(fakeOwnerReal, managerUser, StorePermission.EDIT_PRODUCT));
        }

        /**
         * get exist manager in store
         */
        @Test
        void getManagerPositive() {
            storeUT.appointAdditionManager(ownerRealUser, managerUser);
            Assertions.assertEquals(managerUser.getUserName(), storeUT.getManager(ownerRealUser, managerUser.getUserName()).getUserName());
        }

        /**
         * try to get a manager with owner who didn't appoint him
         */
        @Test
        void getManagerNegative() {
            storeUT.appointAdditionManager(ownerRealUser, managerUser);
            Assertions.assertNull(storeUT.getManager(fakeOwnerReal, managerUser.getUserName()));
        }

        @Test
        void getOperationsCanDoPositive(){
            storeUT.appointAdditionManager(ownerRealUser, managerUser);
            List<String> operations = storeUT.getOperationsCanDo(managerUser);
            Assertions.assertNotNull(operations);
            Assertions.assertEquals(1, operations.size());
            Assertions.assertTrue(operations.contains("view"));
        }

        @Test
        void getOperationsCanDoNegative(){
            List<String> operations = storeUT.getOperationsCanDo(managerUser);
            Assertions.assertEquals(new ArrayList<>(), operations);
        }

        @Test
        void getMySubMangersPositive(){
            storeUT.appointAdditionManager(ownerRealUser, managerUser);
            storeUT.appointAdditionManager(ownerRealUser, managerUser1);

            List<MangerStore> mangerStoreList = storeUT.getMySubMangers(ownerRealUser.getUserName());
            Assertions.assertEquals(2, mangerStoreList.size());
            for (MangerStore mangerStore: mangerStoreList)
                Assertions.assertTrue(mangerStore.getAppointedManager().getUserName().equals(managerUser.getUserName())
                        || mangerStore.getAppointedManager().getUserName().equals(managerUser1.getUserName()));
        }

        @Test
        void getMySubMangersNegative(){
            Assertions.assertEquals(0, storeUT.getMySubMangers(fakeOwnerReal.getUserName()).size());
        }

        /**
         * owner remove permission from manager permissions list
         */
        @Test
        void removePermissionPositive() {
            storeUT.appointAdditionManager(ownerRealUser, managerUser);
            //success : owner can remove permission from the manager's permissions list
            Assertions.assertTrue(storeUT.removePermission(ownerRealUser, managerUser, StorePermission.VIEW));
        }

        /**
         * not an owner try to remove permission from manager permissions list
         */
        @Test
        void removePermissionNegative() {
            storeUT.appointAdditionManager(ownerRealUser, managerUser);
            //fail: the user trying to remove is not an owner who appointed the manager
            Assertions.assertFalse(storeUT.removePermission(fakeOwnerReal, managerUser, StorePermission.VIEW));
        }

        @Test
        void getPermissionOfManagerPositive(){
            storeUT.appointAdditionManager(ownerRealUser, managerUser);
            Assertions.assertEquals(1, storeUT.getManagersStore().size());

            Set<StorePermission> permissions = storeUT.getPermissionOfManager(ownerRealUser, managerUser);
            Assertions.assertNotNull(permissions);
            Assertions.assertEquals(1, permissions.size());
            Assertions.assertEquals("view", permissions.stream().findFirst().get().function);
        }

        @Test
        void getPermissionOfManagerNegative(){
            Set<StorePermission> operations = storeUT.getPermissionOfManager(fakeOwnerReal, managerUser);
            Assertions.assertNull(operations);
        }

        @Test
        void getManagersStorePositive(){
            storeUT.addOwner(ownerRealUser, newOwner);
            storeUT.approveOwner(ownerRealUser, newOwner.getUserName(), true);
            storeUT.appointAdditionManager(newOwner, managerUser);
            storeUT.appointAdditionManager(newOwner, managerUser1);

            Set<MangerStore> mangerStoreSet = storeUT.getManagersStore();
            Assertions.assertEquals(2, mangerStoreSet.size());
            for (MangerStore mangerStore:mangerStoreSet)
                Assertions.assertTrue(mangerStore.getAppointedManager().getUserName().equals(managerUser.getUserName())
                        || mangerStore.getAppointedManager().getUserName().equals(managerUser1.getUserName()));
        }

        @Test
        void getManagersStoreNegativeNoManagers(){
            Set<MangerStore> mangerStoreSet = storeUT.getManagersStore();
            Assertions.assertEquals(0, mangerStoreSet.size());
        }

        @Test
        void managerHavePermissionPositive(){
            storeUT.appointAdditionManager(ownerRealUser, managerUser);
            Assertions.assertTrue(storeUT.managerHavePermission(managerUser.getUserName(), StorePermission.VIEW));
            Assertions.assertFalse(storeUT.managerHavePermission(managerUser.getUserName(), StorePermission.EDIT_PRODUCT));
            storeUT.addPermissionToManager(ownerRealUser, managerUser, StorePermission.EDIT_PRODUCT);
            Assertions.assertTrue(storeUT.managerHavePermission(managerUser.getUserName(), StorePermission.EDIT_PRODUCT));
        }

        @Test
        void managerHavePermissionNegative(){
            Assertions.assertFalse(storeUT.managerHavePermission(managerUser.getUserName(), StorePermission.VIEW));
        }

        @Test
        void getPermissionCantDoPositive(){
            storeUT.appointAdditionManager(ownerRealUser, managerUser);
            Set <StorePermission> storePermissions = storeUT.getPermissionCantDo(ownerRealUser, managerUser);
            Assertions.assertFalse(storePermissions.contains(StorePermission.VIEW));
            Assertions.assertTrue(storePermissions.contains(StorePermission.EDIT_PRODUCT));

            storeUT.addPermissionToManager(ownerRealUser, managerUser, StorePermission.EDIT_PRODUCT);
            storePermissions = storeUT.getPermissionCantDo(ownerRealUser, managerUser);
            Assertions.assertFalse(storePermissions.contains(StorePermission.VIEW));
            Assertions.assertFalse(storePermissions.contains(StorePermission.EDIT_PRODUCT));
        }

        @Test
        void getPermissionCantDoNegative(){
            storeUT.appointAdditionManager(ownerRealUser, managerUser);
            Assertions.assertNull(storeUT.getPermissionCantDo(fakeOwnerReal, managerUser));
        }

        /**
         * add new purchase policy to store
         */
        @Test
        public void addPurchasePolicyPositive() {
            Assertions.assertEquals(0, storeUT.getPurchasePolicies().size());
            storeUT.addPurchasePolicy(ownerRealUser, purchasePolicy);
            Assertions.assertEquals(1, storeUT.getPurchasePolicies().size());
        }

        @Test
        public void addPurchasePolicyNegative() {
            Assertions.assertEquals(0, storeUT.getPurchasePolicies().size());
            Assertions.assertThrows(NotAdministratorException.class, ()->
            {storeUT.addPurchasePolicy(fakeOwnerReal, purchasePolicy);
            });
            Assertions.assertEquals(0, storeUT.getPurchasePolicies().size());
        }

        /**
         * check the case of purchase that pass the exist store policy
         * empty case: should approve there are no policies
         */
        @Test
        public void isApprovedPurchaseEmptyPoliciesPositive() {
            Map<Product, Integer> productBag = new HashMap<>();
            productBag.put(product, 1);
            BillingAddress billingAddress = BillingAddress.builder().country("Israel").build();
            Assertions.assertTrue(storeUT.isApprovedPurchasePolicies(productBag, billingAddress));
        }

        /**
         * check the case of purchase that pass the exist store policy
         * should approve with buyer from the permmited country
         */
        @Test
        public void isApprovedPurchasePoliciesPositive() {
            Map<Product, Integer> productBag = new HashMap<>();
            productBag.put(product, 1);
            Purchase purchase = Purchase.builder()
                    .purchaseType(PurchaseType.SHOPPING_BAG_DETAILS)
                    .purchasePolicy(new PurchasePolicy())
                    .build();
            storeUT.addPurchasePolicy(ownerRealUser, purchase);
            BillingAddress billingAddress = BillingAddress.builder().country("Israel").build();
            Assertions.assertFalse(storeUT.isApprovedPurchasePolicies(productBag, billingAddress));
        }


        /**
         * check the case of purchase that pass the exist store policy
         * negative case: should approve with buyer from not permmited country
         */
        @Test
        public void isApprovedPurchasePoliciesNegative() {
            Map<Product, Integer> productBag = new HashMap<>();
            productBag.put(product, 1);
            Purchase purchase= Purchase.builder()
                    .purchaseType(PurchaseType.PRODUCT_DETAILS)
                    .purchasePolicy(new PurchasePolicy())
                    .build();
            storeUT.addPurchasePolicy(ownerRealUser, purchase);
            BillingAddress billingAddress = BillingAddress.builder().country("Israel").build();
            Assertions.assertFalse(storeUT.isApprovedPurchasePolicies(productBag, billingAddress));
        }

        /**
         * edit existed purchase in store
         */
        @Test
        public void addEditPurchasePositive() {
            purchasePolicy = Purchase.builder()
                    .purchaseType(PurchaseType.PRODUCT_DETAILS)
                    .build();
            Assertions.assertEquals(purchasePolicy, storeUT.addPurchasePolicy(ownerRealUser, purchasePolicy));
            purchasePolicy.setPurchaseType(PurchaseType.SHOPPING_BAG_DETAILS);
            Assertions.assertEquals(purchasePolicy, storeUT.addEditPurchase(ownerRealUser, purchasePolicy));
        }

        /**
         * edit not existed purchase in store
         */
        @Test
        public void addEditPurchaseNegative() {
            purchasePolicy = Purchase.builder()
                    .purchaseType(PurchaseType.PRODUCT_DETAILS)
                    .build();
            Assertions.assertEquals(purchasePolicy, storeUT.addPurchasePolicy(ownerRealUser, purchasePolicy));
            purchasePolicy.setPurchaseType(PurchaseType.SHOPPING_BAG_DETAILS);
            Assertions.assertThrows(Exception.class, ()-> storeUT.addEditPurchase(fakeOwnerReal, purchasePolicy));
        }

        /**
         * see success of applying discount from store on product in bag
         * all kinds of discounts are
         */
        @Test
        public void applyDiscountPoliciesPositive() {
            Map<Product, Integer> productBag = new HashMap<>();
            productBag.put(product, 1);
            storeUT.addDiscount(ownerRealUser, discount1);
            Assertions.assertFalse(discount1.isApplied());
            storeUT.applyDiscountPolicies(productBag);
            Assertions.assertFalse(discount1.isApplied());
        }

        /**
         * see fail of applying discount from store on product in bag
         * because of illegal negative price after discount
         * can't be checked under unit tests need integration
         */
        @Test
        public void applyDiscountPoliciesNegative() {
            Map<Product, Integer> productBag = new HashMap<>();
            productBag.put(product2, 1);
            storeUT.addDiscount(ownerRealUser,discount1);
            product2.setCost(-10);
            storeUT.applyDiscountPolicies(productBag);
            Assertions.assertFalse(discount1.isApplied());
        }

        @Test
        public void removeDiscountPositive(){
            Discount discount1 = new Discount();
            discount1.setDiscountId((long) 1);
            storeUT.addDiscount(ownerRealUser, discount1).getDiscountId();
            Assertions.assertTrue(storeUT.removeDiscount(ownerRealUser, (int) discount1.getDiscountId()));
        }

        @Test
        public void removeDiscountNegative(){
            Discount discount1 = mock(Discount.class);
            storeUT.addDiscount(ownerRealUser, discount1).getDiscountId();
            Assertions.assertThrows(Exception.class, ()->
                    storeUT.removeDiscount(fakeOwnerReal, (int) discount1.getDiscountId()));
        }


        @Test
        void getStoreDiscountsPositive(){
            Discount discount1 = new Discount();
            discount1.setDiscountId((long)1);
            storeUT.addDiscount(ownerRealUser, discount1);
            Assertions.assertEquals(1, storeUT.getStoreDiscounts(ownerRealUser).size());
            Assertions.assertTrue(storeUT.getStoreDiscounts(ownerRealUser).contains(discount1));
        }

        @Test
        void getStoreDiscountsNegative(){
            Discount discount1 = new Discount();
            discount1.setDiscountId((long)1);
            storeUT.addDiscount(ownerRealUser, discount1);
            Assertions.assertThrows(Exception.class,()->
                    storeUT.getStoreDiscounts(fakeOwnerReal));
        }

        @Test
        public void addDiscountPositive() {
            Assertions.assertEquals(0, storeUT.getDiscounts().size());
            Assertions.assertNotNull(storeUT.addDiscount(ownerRealUser, discount));
            Assertions.assertEquals(1, storeUT.getDiscounts().size());
        }

        @Test
        public void addDiscountNegative() {
            Assertions.assertEquals(0, storeUT.getDiscounts().size());
            Assertions.assertThrows(NotAdministratorException.class, ()->
                    storeUT.addDiscount(fakeOwnerReal, discount));
            Assertions.assertEquals(0, storeUT.getDiscounts().size());
        }

        @Test
        public void editDiscountPositive() {
            discount = Discount.builder()
                    .discountId(0)
                    .discountPercentage(10)
                    .endTime(Calendar.getInstance())
                    .description("")
                    .build();
            storeUT.addDiscount(ownerRealUser, discount);
            discount.setDiscountPercentage(20);
            Assertions.assertEquals(20, storeUT.addEditDiscount(ownerRealUser, discount).getDiscountPercentage());
        }

        @Test
        public void editDiscountNegative() {
            discount = Discount.builder()
                    .discountId(0)
                    .discountPercentage(10)
                    .endTime(Calendar.getInstance())
                    .description("")
                    .build();
            discount.setDiscountPercentage(20);
            Assertions.assertThrows(NotAdministratorException.class, ()->
                    storeUT.addEditDiscount(fakeOwnerReal, discount));
        }

        @Test
        void getDiscountSimplePositive(){
            Discount discount1 = Discount.builder()
                    .discountId(1)
                    .discountType(DiscountType.COMPOSE)
                    .build();
            Discount discount2 = Discount.builder()
                    .discountId(2)
                    .discountType(DiscountType.VISIBLE)
                    .build();
            Discount discount3 = Discount.builder()
                    .discountId(3)
                    .discountType(DiscountType.CONDITIONAL_PRODUCT)
                    .build();
            storeUT.addDiscount(ownerRealUser, discount1);
            storeUT.addDiscount(ownerRealUser, discount2);
            storeUT.addDiscount(ownerRealUser, discount3);

            List<Discount> discounts = storeUT.getDiscountSimple();
            Assertions.assertEquals(2, discounts.size());
            for (Discount discount: discounts)
                Assertions.assertNotEquals(DiscountType.COMPOSE, discount.getDiscountType());
        }
        @Test
        void getDiscountSimpleNegative(){
            List<Discount> discounts = storeUT.getDiscountSimple();
            Assertions.assertEquals(0, discounts.size());
        }

        @Test
        void getExistProductPositive() {
            addNewProductSetUp();
            //success: get exist product
            Assertions.assertEquals(product, storeUT.getProduct(product.getProductSn()));
        }

        @Test
        void getNotExistProductNegative() {
            //success: get exist product
            Throwable exception = Assertions.
                    assertThrows(ProductDoesntExistException.class, () -> storeUT.getProduct(33));
            Assertions.assertEquals("A product with id '" + 33 + "' is not exist in store with id: '" + storeUT.getStoreId() + "'", exception.getMessage());
        }

        private void addProductAsEditProductSetup() {
            product = new Product("Crashpad", ProductCategory.SPORTING_GOODS, 2, 987, 12);
            storeUT.addNewProduct(ownerRealUser, product);

        }

        private void addNewProductSetUp() {
            storeUT.addNewProduct(ownerRealUser, product);
        }
    }
}

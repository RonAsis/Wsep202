//package com.wsep202.TradingSystem.domain.trading_system_management;
//
//import com.wsep202.TradingSystem.domain.exception.*;
//import com.wsep202.TradingSystem.domain.trading_system_management.discount.*;
//import com.wsep202.TradingSystem.domain.trading_system_management.policy_purchase.Purchase;
//import com.wsep202.TradingSystem.domain.trading_system_management.purchase.BillingAddress;
//import org.junit.jupiter.api.*;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//
//import java.util.*;
//
//import static org.mockito.Mockito.*;
//
//@ExtendWith(SpringExtension.class)
//class StoreTest {
//    private MangerStore managerStore; //the manager object of the manager user
//    private UserSystem managerUser; //role as manager to add and get
//    private UserSystem newOwner;    //role as an appointed owner in the store
//    private UserSystem fakeOwner;   //role as not owner in the store
//    private UserSystem owner;   //role as appointing owner in the store
//    private Store storeUT;  //unit under test
//    private Purchase purchasePolicy;
//    private ShoppingBag shoppingBag;
//    private Discount discount;
//
//    private Product product;    //product to add
//
//    //integration attributes
//    private UserSystem ownerRealUser;
//    private UserSystem newOwnerReal;
//    private UserSystem fakeOwnerReal;
//
//
//    @AfterEach
//    void tearDown() {
//    }
//
//    @Nested
//    public class StoreTestUnit {
//
//        @BeforeEach
//        void setUp() {
//
//            //new manager to add
//            newOwner = mock(UserSystem.class);   //the user the owner want to appoint as a new store owner
//            //stubbing
//            when(newOwner.getUserName()).thenReturn("Jones");
//
//            //manager for get and add owner tests
//            managerUser = mock(UserSystem.class);
//            when(managerUser.getUserName()).thenReturn("Alex");
//            managerStore = new MangerStore(managerUser);
//
//            //not owner of the store
//            fakeOwner = mock(UserSystem.class);
//            when(fakeOwner.getUserName()).thenReturn("Donald");
//
//            //owner and opener of the store
//            owner = mock(UserSystem.class);
//            when(owner.getUserName()).thenReturn("Michael");
//            storeUT = new Store(owner, "Store under test");
//            product = mock(Product.class);
//
//            shoppingBag = mock(ShoppingBag.class);
//            discount = mock(Discount.class);
//            //doNothing().when(discount).setNewId();
//            when(discount.getDiscountId()).thenReturn(0);
//
//        }
//
//
//        /**
//         * @pre the appointing owner is registered as the store owner
//         * adding an owner by an store owner
//         */
//        @Test
//        void addOwnerPositive() {
//            Assertions.assertTrue(storeUT.addOwner(owner, newOwner));    //Success: owner appointing a newOwner as a new owner
//        }
//
//        /**
//         * @pre the appointing owner is not registered as the store owner
//         * adding an owner by not store owner
//         */
//        @Test
//        void addOwnerNegativeNotOwner() {
//            UserSystem newOwner = mock(UserSystem.class);   //the user the fake owner want to appoint as a new store owner
//            //stubbing
//            when(newOwner.getUserName()).thenReturn("Jones");
//            Assertions.assertFalse(storeUT.addOwner(fakeOwner, newOwner)); //Fail: owner appointing a newOwner as a new owner
//        }
//
//        /**
//         * @pre the appointed owner is already an owner in the store
//         * adding an already owner by owner
//         */
//        @Test
//        void addOwnerNegativeNotAlreadyOwner() {
//            Assertions.assertFalse(storeUT.addOwner(owner, owner)); //Fail: owner appointing a an already owner to be owner
//        }
//
//        /**
//         * owner adds new product
//         */
//        @Test
//        void addNewProductPositive() {
//            Assertions.assertEquals(0, storeUT.getProducts().size());     //verify there are no products in store
//            Assertions.assertTrue(storeUT.addNewProduct(owner, product));    //success: the product added by the owner
//            Assertions.assertEquals(1, storeUT.getProducts().size());     //verify amount of products in list increased
//        }
//
//        /**
//         * user that is not an owner adds new product
//         */
//        @Test
//        void addNewProductNegative() {
//            Assertions.assertEquals(0, storeUT.getProducts().size());     //verify there are no products in store
//            UserSystem fakeOwner = mock(UserSystem.class);
//            when(fakeOwner.getUserName()).thenReturn("Donald");
//            //fail: user which is no owner cannot add product to store
//            Assertions.assertFalse(storeUT.addNewProduct(fakeOwner, product));
//            Assertions.assertEquals(0, storeUT.getProducts().size());     //verify there are no products in store
//        }
//
//        /**
//         * owner remove a product exists in store
//         */
//        @Test
//        void removeExistsProductFromStorePositive() {
//            //setup for remove product
//            addNewProductSetUp();  //add product to the store
//            Assertions.assertEquals(1, storeUT.getProducts().size()); //verify we starts with no products in store
//            //add a product to the store which will be removed for the test
//            when(product.getProductSn()).thenReturn(12);    // let product SN to for the check which one to remove
//            Assertions.assertTrue(storeUT.removeProductFromStore(owner, 12));    //success: product is removed
//            Assertions.assertEquals(0, storeUT.getProducts().size()); //verify we decreased no. of products in store
//        }
//
//        /**
//         * owner remove a product that doesn't exist in store
//         */
//        @Test
//        void removeNotExistsProductFromStoreNegative() {
//            //setup for remove product
//            addNewProductSetUp();  //add product to the store
//            Assertions.assertEquals(1, storeUT.getProducts().size()); //verify we starts with no products in store
//            //add a product to the store which will be removed for the test
//            when(product.getProductSn()).thenReturn(12);    // let product SN to for the check which one to remove
//            Assertions.assertFalse(storeUT.removeProductFromStore(owner, 4));    //fail: product not exist is not removed
//            Assertions.assertEquals(1, storeUT.getProducts().size()); //verify we decreased no. of products in store
//        }
//
//        /**
//         * user which is not an owner of the store removes a product that doesn't exist in store
//         */
//        @Test
//        void notOwnerRemoveExistsProductFromStoreNegative() {
//            UserSystem notOwner = mock(UserSystem.class);   //not an owner
//            //setup for remove product. add a product to the store which will be removed for the test
//            addNewProductSetUp();  //add product to the store
//            Assertions.assertEquals(1, storeUT.getProducts().size()); //verify we starts with no products in store
//            //stubs
//            when(notOwner.getUserName()).thenReturn("Philip");
//            when(product.getProductSn()).thenReturn(12);    // let product SN to for the check which one to remove
//            Assertions.assertFalse(storeUT.removeProductFromStore(notOwner, 12));    //fail: not owner cannot remove product
//            Assertions.assertEquals(1, storeUT.getProducts().size()); //verify we decreased no. of products in store
//        }
//
//        /**
//         * setting an existing product in store with product properties by an owner
//         */
//        @Test
//        void editProductPositive() {
//            //setup for adding a product to store
//            addProductAsEditProductSetup();
//            int productSN = product.getProductSn();  //the serial number of the product
//            String name = "airpods";    //the new name
//            String category = "electronics";   //new category
//            int amount = 4; //new amount
//            double cost = 100000.5; //new cost
//            //success: the product edited
//            storeUT.editProduct(owner, productSN, name, category, amount, cost);
//            Assertions.assertTrue(storeUT.editProduct(owner, productSN, name, category, amount, cost));
//        }
//
//        /**
//         * setting an existing product in store but not by an owner so should fail.
//         */
//        @Test
//        void notOwnerEditProductNegative() {
//            //setup for adding a product to store
//            addProductAsEditProductSetup();
//            //not owner
//            UserSystem notOwner = mock(UserSystem.class);   //not an owner
//            when(notOwner.getUserName()).thenReturn("Donald");
//            int productSN = 0;  //the serial number of the product
//            String name = "airpods";    //the new name
//            String category = "electronics";   //new category
//            int amount = 4; //new amount
//            double cost = 100000.5; //new cost
//            //fail: the product is not edited
//            Assertions.assertFalse(storeUT.editProduct(notOwner, productSN, name, category, amount, cost));
//
//        }
//
//        /**
//         * setting not existing product in store with product properties by an owner
//         */
//        @Test
//        void editNotExistProductNegative() {
//            //setup for adding a product to store
//            addProductAsEditProductSetup();
//            int productSN = 23;  //the serial number of the product
//            String name = "airpods";    //the new name
//            String category = "electronics";   //new category
//            int amount = 4; //new amount
//            double cost = 100000.5; //new cost
//            //fail: the product isn't edited
//            Assertions.assertFalse(storeUT.editProduct(owner, productSN, name, category, amount, cost));
//        }
//
//        /**
//         * get exist manager in store
//         */
//        @Test
//        void getManagerPositive() {
//            //setup : adding alex as manager to the store
//            addManagerSetup();
//            //success: the manager alex is returned as expected
//            Assertions.assertEquals("Alex", storeUT.getManager(owner, "Alex").getUserName());
//
//        }
//
//
//        /**
//         * try to get a manager with owner which didn't appoint him
//         */
//        @Test
//        void notAppointingOwnerGetManagerNegative() {
//            //setup : adding alex as manager to the store
//            addManagerSetup();
//            //fail: the manager alex is not returned for the appointer fake
//            Assertions.assertEquals(null, storeUT.getManager(fakeOwner, "Alex"));
//        }
//
//        /**
//         * get exist manager object in store
//         */
//        @Test
//        void getManagerObjectPositive() {
//            //setup : adding alex as manager to the store
//            addManagerSetup();
//            //success: the manager alex is returned as expected
//            Assertions.assertEquals("Alex", storeUT.getManagerObject(owner, "Alex").getAppointedManager());
//        }
//
//        /**
//         * try to get a manager with owner which didn't appoint him
//         */
//        @Test
//        void notAppointingOwnerGetManagerObjectNegative() {
//            //setup : adding alex as manager to the store
//            addManagerSetup();
//            //fail: the manager alex is not returned for the appointer fake
//            Assertions.assertEquals(null, storeUT.getManagerObject(fakeOwner, "Alex"));
//        }
//
//        /**
//         * owner adds a manager to the store
//         * owner can add a manager who is not already manager or owner of the store
//         */
//        @Test
//        void addManagerPositive() {
//            //success: owner can add a manager who is not already manager or owner of the store
//            Assertions.assertTrue(storeUT.addManager(owner, managerUser) != null);
//        }
//
//        /**
//         * owner adds a manager to the store
//         * owner can't add a manager who is already manager
//         */
//        @Test
//        void addManagerAlreadyManagerNegative() {
//            addManagerSetup();  //adding the manager at the first time
//            //fail: owner can't add a manager who is already manager
//            Assertions.assertFalse(storeUT.addManager(owner, managerUser) != null);  //add manager again
//        }
//
//        /**
//         * owner adds a manager to the store
//         * owner can't add a manager who is already owner
//         */
//        @Test
//        void addManagerAlreadyOwnerNegative() {
//            addOwnerSetup();  //adding the manager at the first time
//            //fail: owner can't add a manager who is already owner in the store
//            Assertions.assertFalse(storeUT.addManager(owner, newOwner) != null);  //add manager again
//        }
//
//        /**
//         * owner adds a manager to the store
//         */
//        @Test
//        void notAppointingOwnerAddManagerNegative() {
//            //fail: not owner try to appoint a new manager for the store
//            Assertions.assertFalse(storeUT.addManager(fakeOwner, managerUser) != null);  //add manager again
//        }
//
//        /**
//         * owner can add permissions to a manager he appointed
//         */
//        @Test
//        void addPermissionToManagerPositive() {
//            addManagerSetup();  //add a manager to the store
//            //mock
//            managerStore = mock(MangerStore.class);
//            when(managerStore.getAppointedManager()).thenReturn(managerUser);
//            when(managerStore.addStorePermission(StorePermission.EDIT_PRODUCT)).thenReturn(true);
//            //success: permission added to manager
//            Assertions.assertTrue(storeUT.addPermissionToManager(owner, managerUser, StorePermission.EDIT_PRODUCT));
//        }
//
//        /**
//         * not owner try to add permissions to a manager
//         */
//        @Test
//        void notOwnerAddPermissionToManagerNegative() {
//            addManagerSetup();  //add a manager to the store
//            //mock
//            managerStore = mock(MangerStore.class);
//            when(managerStore.getAppointedManager()).thenReturn(managerUser);
//            when(managerStore.addStorePermission(StorePermission.EDIT_PRODUCT)).thenReturn(true);
//            //fail: permission is not added to manager
//            Assertions.assertFalse(storeUT.addPermissionToManager(fakeOwner, managerUser, StorePermission.EDIT_PRODUCT));
//        }
//
//        /**
//         * owner remove permission from manager permissions list
//         */
//        @Test
//        void removePermissionPositive() {
//            addManagerSetup();
//            //mock
//            managerStore = mock(MangerStore.class);
//            when(managerStore.getAppointedManager()).thenReturn(managerUser);
//            when(managerStore.removeStorePermission(StorePermission.VIEW)).thenReturn(true);
//            //success : owner can remove permission from the manager's permissions list
//            Assertions.assertTrue(storeUT.removePermissionFromManager(owner, managerUser, StorePermission.VIEW));
//        }
//
//        /**
//         * not an owner try to remove permission from manager permissions list
//         */
//        @Test
//        void removePermissionNegative() {
//            addManagerSetup();
//            //mock
//            managerStore = mock(MangerStore.class);
//            when(managerStore.getAppointedManager()).thenReturn(managerUser);
//            when(managerStore.addStorePermission(StorePermission.VIEW)).thenReturn(true);
//            //fail: the user trying to remove is not an owner who appointed the manager
//            Assertions.assertFalse(storeUT.removePermissionFromManager(fakeOwner, managerUser, StorePermission.VIEW));
//        }
//
//        /**
//         * owner remove one of managers appointed by him
//         */
//        @Test
//        void removeManagerPositive() {
//            //setup: insert manager into managers list of the store
//            addManagerSetup();
//            Assertions.assertEquals(1, storeUT.getManagers().size());
//            //mock
//            managerStore = mock(MangerStore.class);
//            when(managerStore.getAppointedManager()).thenReturn(managerUser);
//            //success: owner can remove manager he appointed
//            Assertions.assertTrue(storeUT.removeManager(owner, managerUser));
//            Assertions.assertEquals(0, storeUT.getManagers().size());
//        }
//
//        /**
//         * user who is not owner cannot remove one of managers appointed by someone else
//         */
//        @Test
//        void removeManagerNegative() {
//            //setup: insert manager into managers list of the store
//            addManagerSetup();
//            //mock
//            managerStore = mock(MangerStore.class);
//            when(managerStore.getAppointedManager()).thenReturn(managerUser);
//            //fail: only the owner can remove manager he appointed
//            Assertions.assertFalse(storeUT.removeManager(fakeOwner, managerUser));
//            Assertions.assertEquals(1, storeUT.getManagers().size());
//        }
//
//        /**
//         * This test check if the removeOwner method succeeds
//         * when the parameters are correct, no managers was appointed by removed owner
//         */
//        @Test
//        void removeOwnerNoManagers(){
//            setUpRemoveOwner();
//            int ownerLiseSize = storeUT.getOwners().size();
//            //check that the list of owners is not empty
//            Assertions.assertTrue(ownerLiseSize != 0);
//            //check before the remove that the user is an owner
//            Assertions.assertTrue(storeUT.getOwners().contains(managerUser));
//            //check that the removal was successful
//            Assertions.assertTrue(storeUT.removeOwner(owner,managerUser));
//            //removed 9 owners from store
//            Assertions.assertEquals(1, storeUT.getOwners().size());
//            //check that the removed owner does not contain the store in his owned store list
//            Assertions.assertFalse(managerUser.getOwnedStores().contains(storeUT));
//        }
//
//        /**
//         * This test check if the removeOwner method succeeds
//         * when the parameters are correct, with that managers was appointed
//         * by removed owner.
//         */
//        @Test
//        void removeOwnerWithManagers(){
//            setUpRemoveOwnerManager();
//            int ownerLiseSize = storeUT.getOwners().size();
//            //check that the list of owners is not empty
//            Assertions.assertTrue(ownerLiseSize != 0);
//            //check that this user is a manager in store
//            Assertions.assertEquals(ownerRealUser,storeUT.getManager(managerUser,ownerRealUser.getUserName()));
//            //check before the remove that the user is an owner
//            Assertions.assertTrue(storeUT.getOwners().contains(managerUser));
//            //check that the removal was successful
//            Assertions.assertTrue(storeUT.removeOwner(owner,managerUser));
//            //removed 9 owners from store
//            Assertions.assertEquals(1, storeUT.getOwners().size());
//            //check that the manager was removed from store
//            Assertions.assertFalse(storeUT.getManagers().contains(ownerRealUser));
//        }
//
//        @Test
//        void removeOwnerNotAnOwner(){
//            setUpRemoveOwner();
//            int ownerLiseSize = storeUT.getOwners().size();
//            //check that the list of owners is not empty
//            Assertions.assertTrue(ownerLiseSize != 0);
//            //check before the remove that the user is an owner
//            Assertions.assertTrue(storeUT.getOwners().contains(managerUser));
//            //someone who is not an owner can't remove a real owner
//            Assertions.assertFalse(storeUT.removeOwner(fakeOwner,managerUser));
//            //can't remove someone who is not an owner of the store
//            Assertions.assertFalse(storeUT.removeOwner(owner,fakeOwner));
//            //check that the numbers of owners didn't change
//            Assertions.assertEquals(ownerLiseSize, storeUT.getOwners().size());
//            //check after fail remove that managerUser is still an owner in store
//            Assertions.assertTrue(storeUT.getOwners().contains(managerUser));
//        }
//
//        @Test
//        void removeOwnerHimself(){
//            setUpRemoveOwner();
//            int ownerLiseSize = storeUT.getOwners().size();
//            //check that the list of owners is not empty
//            Assertions.assertTrue(ownerLiseSize != 0);
//            //check before the remove that the user is an owner
//            Assertions.assertTrue(storeUT.getOwners().contains(owner));
//            //an owner can't remove himself from the store
//            Assertions.assertFalse(storeUT.removeOwner(owner,owner));
//            //check that the numbers of owners didn't change
//            Assertions.assertEquals(ownerLiseSize, storeUT.getOwners().size());
//            //check after fail remove that owner is still an owner in store
//            Assertions.assertTrue(storeUT.getOwners().contains(owner));
//        }
//
//
//        /**
//         * get existing product in the store
//         */
//        @Test
//        void getExistProductPositive() {
//            addNewProductSetUp();
//            when(product.getProductSn()).thenReturn(33);
//            //success: get exist product
//            Assertions.assertEquals(product, storeUT.getProduct(33));
//        }
//
//        /**
//         * get not existing product in the store
//         */
//        @Test
//        void getNotExistProductNegative() {
//            when(product.getProductSn()).thenReturn(33);
//            //success: get exist product
//            Throwable exception = Assertions
//                    .assertThrows(ProductDoesntExistException.class, () -> storeUT.getProduct(33));
//            Assertions.assertEquals("A product with id '" +
//                    33 + "' is not exist in store with id: '" + storeUT.getStoreId() +
//                    "'", exception.getMessage());
//        }
//
//        @Test
//        void getAppointedOwnerSuc(){
//            setUpGetAppointedOwner();
//            //check that the right user returns
//            Assertions.assertEquals(newOwner,storeUT.getAppointedOwner(owner,newOwner.getUserName()));
//        }
//
//        private void setUpGetAppointedOwner(){
//            when(owner.getUserName()).thenReturn("ownerTest");
//            when(newOwner.getUserName()).thenReturn("newOwnerTest");
//            Set<UserSystem> owners = new HashSet<>();
//            owners.add(owner);
//            owners.add(newOwner);
//            storeUT.setOwners(owners);
//            Set<OwnersAppointee> appointedOwners = new HashSet<>();
//            Set<UserSystem> apOwner = new HashSet<>();
//            apOwner.add(newOwner);
//            OwnersAppointee ownersAppointee = new OwnersAppointee(owner.getUserName(),apOwner);
//            appointedOwners.add(ownersAppointee);
//            storeUT.setAppointedOwners(appointedOwners);
//        }
//
//        /**
//         * user which is manager in the store requests for receipts
//         */
//        @Test
//        void managerViewReceiptsPositive() {
//            addManagerSetup();
//            insertReceiptsSetup();
//            List<Receipt> receipts = storeUT.managerViewReceipts(managerUser);  //get the receipts of store
//            int i = 0;
//            for (Receipt receipt : receipts) {
//                //success : manager can view the receipts and their fields as well
//                Assertions.assertEquals(i, receipt.getAmountToPay());
//                i++;
//            }
//        }
//
//        /**
//         * user which is not manager in the store requests for receipts
//         */
//        @Test
//        void notManagerViewReceiptsNegative() {
//            Throwable exception = Assertions.assertThrows(NoManagerInStoreException.class, () -> storeUT.managerViewReceipts(fakeOwner));
//            //fail: the inserted user is not manager so cant see the receipts in store
//            Assertions.assertEquals("The user name '" + "Donald" + "' is not manager in the store number: '" + storeUT.getStoreId() + "'", exception.getMessage());
//        }
//
//        /**
//         * user which is manager in the store requests for receipts
//         */
//        @Test
//        void ownerViewReceiptsPositive() {
//            addOwnerSetup();
//            insertReceiptsSetup();
//            List<Receipt> receipts = storeUT.ownerViewReceipts(owner);  //get the receipts of store
//            int i = 0;
//            for (Receipt receipt : receipts) {
//                //success : manager can view the receipts and their fields as well
//                Assertions.assertEquals(i, receipt.getAmountToPay());
//                i++;
//            }
//        }
//
//        /**
//         * user which is not owner in the store requests for receipts
//         */
//        @Test
//        void notOwnerViewReceiptsNegative() {
//            Throwable exception = Assertions.assertThrows(NoManagerInStoreException.class, () -> storeUT.ownerViewReceipts(fakeOwner));
//            //fail: the inserted user is not owner so cant see the receipts in store
//            Assertions.assertEquals("The user name '" + "Donald" + "' is not manager in the store number: '" + storeUT.getStoreId() + "'", exception.getMessage());
//        }
//
//        /**
//         * functionality of watching the store information
//         */
//        @Test
//        void showStoreInfo() {
//            //success: shows the store info as string
//            Assertions.assertEquals("The store Store under test has ID: " + storeUT.getStoreId() + "", storeUT.showStoreInfo());
//        }
//
//        /**
//         * functionality of watching the store products information
//         */
//        @Test
//        void showProductsInStoreInfo() {
//            //success: no products in store so show empty string
//            Assertions.assertEquals("", storeUT.showProductsInStoreInfo());
//        }
//
//
//        /////////////////////updates after UC 4.2//////////////////////////
//
//        /***
//         * check that when product bout is in stock return true
//         */
//        @Test
//        public void isAllInStockPositive() {
//            Map<Product, Integer> productBag = new HashMap<>();
//            productBag.put(product, 1);
//            addNewProductSetUp();   //add the bought product to stock
//            when(product.getAmount()).thenReturn(1);
//            when(shoppingBag.getProductListFromStore()).thenReturn(productBag);
//            when(product.getProductSn()).thenReturn(0);
//            when(shoppingBag.getProductAmount(product.getProductSn())).thenReturn(1);//one bought
//            Assertions.assertTrue(storeUT.isAllInStock(shoppingBag));
//        }
//
//        /***
//         * check that when product bout is not in stock return exception
//         */
//        @Test
//        public void isAllInStockNegative() {
//            Map<Product, Integer> productBag = new HashMap<>();
//            productBag.put(product, 1);
//            addNewProductSetUp();   //add the bought product to stock
//            when(product.getAmount()).thenReturn(1);    //amount in store
//            when(shoppingBag.getProductListFromStore()).thenReturn(productBag);
//            when(product.getProductSn()).thenReturn(0);
//            when(shoppingBag.getProductAmount(product.getProductSn())).thenReturn(5);//five bought
//            when(product.getName()).thenReturn("carbon");
//
//            Throwable exception = Assertions
//                    .assertThrows(NotInStockException.class, () -> storeUT.isAllInStock(shoppingBag));
//            Assertions.assertEquals("The product: " + product.getName()
//                            + " is out of stock in store: " + storeUT.getStoreName()
//                    , exception.getMessage());
//        }
//
//
//        /**
//         * check update amount of product in the stock (after purchase)
//         */
//        @Test
//        public void editProductAmountInStockPositive() {
//            addNewProductSetUp(); //add product to stock
//            when(product.getProductSn()).thenReturn(0);
//            doNothing().when(product).setAmount(3);
//            Assertions.assertTrue(storeUT.editProductAmountInStock(0, 3));
//        }
//
//        /**
//         * check not update amount of inexist product in the stock
//         */
//        @Test
//        public void editProductAmountInStockNegative() {
//            addNewProductSetUp(); //add product to stock
//            when(product.getProductSn()).thenReturn(0);
//            doNothing().when(product).setAmount(3);
//            Assertions.assertFalse(storeUT.editProductAmountInStock(1, 3));
//        }
//
////////////////////setups///////////////////////////////////
//
//        private void insertReceiptsSetup() {
//            for (int i = 0; i < 10; i++) {
//                storeUT.getReceipts().add(Receipt.builder()
//                        .amountToPay(i)
//                        .productsBought(new HashMap<Product, Integer>())
//                        .purchaseDate(new Date())
//                        .storeId(0)
//                        .userName("felix")
//                        .build());
//            }
//        }
//
//        private void addProductAsEditProductSetup() {
//            product = new Product("Crashpad", ProductCategory.SPORTING_GOODS, 2, 987, 12);
//            storeUT.addNewProduct(owner, product);
//
//        }
//
//        private void addNewProductSetUp() {
//            storeUT.addNewProduct(owner, product);
//        }
//
//        private void addNewDiscountToStoreSetup() {
//            storeUT.addDiscount(owner, discount);
//        }
//
//        private void addManagerSetup() {
//            storeUT.addManager(owner, managerUser);  //adding alex as new manager in the store
//        }
//
//        private void addOwnerSetup() {
//            storeUT.addOwner(owner, newOwner);
//        }
//
//    }
//
//
//    @Nested
//    public class StoreTestIntegration {
//        Discount discount1;
//        Discount discount2;
//        Discount discount3;
//        Discount discount4;
//        ComposedDiscount composedDiscount;
//        Product product2;
//        Product product3;
//
//        @BeforeEach
//        void setUp() {
//            //new manager to add
//            newOwnerReal = new UserSystem("new", "n", "y", "u");   //the user the owner want to appoint as a new store owner
//
//            //manager for get and add owner tests
//            managerUser = new UserSystem("Alex", "Alex", "Alex", "Alex");
//            managerStore = new MangerStore(managerUser);
//
//            //not owner of the store
//            fakeOwnerReal = new UserSystem("DT", "Donald", "Trump", "DT123");
//
//            //owner and opener of the store
//            ownerRealUser = new UserSystem("Michael", "micha", "toti", "pass");
//            storeUT = new Store(ownerRealUser, "Store under test");
//            product = new Product("Bamba", ProductCategory.SPORTING_GOODS, 1, 11, storeUT.getStoreId());
//             product2 = Product.builder()
//                    .cost(10)
//                    .productSn(66)
//                    .originalCost(10)
//                    .name("doritos")
//                    .storeId(storeUT.getStoreId())
//                    .build();
//             product3 = Product.builder()
//                    .cost(20)
//                    .productSn(67)
//                    .originalCost(20)
//                    .name("doritos")
//                    .storeId(storeUT.getStoreId())
//                    .build();
//            HashMap<Product,Integer> postProducts = new HashMap();
//            postProducts.put(product2,1);
//            postProducts.put(product3,1);
//            ConditionalProductDiscount conditionalProductDiscount = ConditionalProductDiscount.builder()
//                    .productsUnderThisDiscount(new HashMap<>())
//                    .amountOfProductsForApplyDiscounts(postProducts)
//                    .build();
//            Calendar endTime = Calendar.getInstance();
//            endTime.set(3000,1,1);
//            List<Discount> discountsComposed = new ArrayList<>();
//             discount3 = Discount.builder().discountPercentage(10)
//                    .endTime(endTime)
//                    .description("cond")
//                    .discountId(33)
//                    .discountType(DiscountType.CONDITIONAL_PRODUCT)
//                    .discountPolicy(conditionalProductDiscount)
//                    .build();
//            discountsComposed.add(discount3);
//            Map<Product,Integer> productsVis = new HashMap<>();
//            productsVis.put(product,1);
//             discount1 = Discount.builder().discountPercentage(10)
//                    .endTime(endTime)
//                    .description("vis")
//                    .discountId(34)
//                    .discountType(DiscountType.VISIBLE)
//                    .discountPolicy(VisibleDiscount.builder().amountOfProductsForApplyDiscounts(productsVis)
//                                    .build()).build();
//            discountsComposed.add(discount1);
//             discount2 = Discount.builder().discountPercentage(10)
//                    .endTime(endTime)
//                    .description("store")
//                    .discountId(35)
//                    .discountType(DiscountType.CONDITIONAL_STORE)
//                    .discountPolicy(ConditionalStoreDiscount.builder().minPrice(4.8)
//                            .build()).build();
//            discountsComposed.add(discount2);
//             composedDiscount = ComposedDiscount.builder()
//                    .composedDiscounts(discountsComposed)
//                    .compositeOperator(CompositeOperator.AND)
//                    .build();
//            Calendar time = Calendar.getInstance();
//            time.set(3000,1,1);
//             discount4 = Discount.builder()
//                     .discountId(77)
//                     .discountPercentage(1)
//                     .discountType(DiscountType.COMPOSE)
//                     .description("compose")
//                     .endTime(time)
//                     .discountPolicy(composedDiscount)
//                     .build();
//            discount = Discount.builder().build();  //add simple cased discount
//            purchasePolicy = Purchase.builder().build(); //add simple cased purchase policy
//        }
//
//        /**
//         * verifies the method returns occurences of products with the name inserted
//         * in case there are in the store
//         */
//        @Test
//        void searchProductByNamePositive() {
//            addNewProductSetUp();
//            Set<Product> resultsForSearch = storeUT.searchProductByName("Bamba");
//            for (Product product : resultsForSearch) {
//                //success: there is one product in the list after filter by name PSP
//                Assertions.assertEquals("Bamba", product.getName());
//            }
//        }
//
//        /**
//         * check the case of searching for products with name that is not exist for any product
//         */
//        @Test
//        void searchProductByNameNegative() {
//            addNewProductSetUp();
//            Set<Product> resultsForSearch = storeUT.searchProductByName("PSP");
//            //fail: no results for the name football
//            Assertions.assertEquals(0, resultsForSearch.size());
//        }
//
//        /**
//         * get list of products only with the same category which inserted
//         */
//        @Test
//        void searchProductByCategoryPositive() {
//            addNewProductSetUp();
//            ProductCategory category = ProductCategory.SPORTING_GOODS;
//            Set<Product> resultsForSearch = storeUT.searchProductByCategory(category);
//            for (Product product : resultsForSearch) {
//                //success: all products in the list are in the same category
//                Assertions.assertEquals(category, product.getCategory());
//            }
//        }
//
//        /**
//         * handling with search of category which is not exist for the store products
//         */
//        @Test
//        void searchProductByCategoryNegative() {
//            addNewProductSetUp();
//            ProductCategory notInStoreCategory = ProductCategory.TOYS_HOBBIES;
//            Set<Product> resultsForSearch = storeUT.searchProductByCategory(notInStoreCategory);
//            //fail: the requested category not exist so empty list of products returned
//            Assertions.assertEquals(0, resultsForSearch.size());
//        }
//
//        /**
//         * verifies searching by keywords which is part of a name of product in store
//         * returns the proper products list
//         */
//        @Test
//        void searchProductByKeywordPositive() {
//            addNewProductSetUp();
//            List<String> keywords = new ArrayList<>();
//            keywords.add("mb");
//            Set<Product> resultsForSearch = storeUT.searchProductByKeyWords(keywords);
//            for (Product product : resultsForSearch) {
//                //success: there is match between keyword to product
//                Assertions.assertTrue(product.getName().contains("mb"));
//            }
//        }
//
//        /**
//         * test handling with searching of keyword which doesn't exist
//         */
//        @Test
//        void searchProductByKeywordNegative() {
//            addNewProductSetUp();
//            List<String> keywords = new ArrayList<>();
//            keywords.add("goo");
//            Set<Product> resultsForSearch = storeUT.searchProductByKeyWords(keywords);
//            //fail: there is no match between keyword to products
//            Assertions.assertEquals(0, resultsForSearch.size());
//        }
//
//
//        /**
//         * @pre the appointing owner is registered as the store owner
//         * adding an owner by an store owner
//         */
//        @Test
//        void addOwnerPositive() {
//            Assertions.assertTrue(storeUT.addOwner(ownerRealUser, newOwnerReal));    //Success: owner appointing a newOwner as a new owner
//        }
//
//        /**
//         * @pre the appointing owner is not registered as the store owner
//         * adding an owner by not store owner
//         */
//        @Test
//        void addOwnerNegativeNotOwner() {
//            Assertions.assertFalse(storeUT.addOwner(fakeOwnerReal, newOwnerReal)); //Fail: owner appointing a newOwner as a new owner
//        }
//
//        /**
//         * @pre the appointed owner is already an owner in the store
//         * adding an already owner by owner
//         */
//        @Test
//        void addOwnerNegativeNotAlreadyOwner() {
//            Assertions.assertFalse(storeUT.addOwner(ownerRealUser, ownerRealUser)); //Fail: owner appointing a an already owner to be owner
//        }
//
//        /**
//         * owner adds new product
//         */
//        @Test
//        void addNewProductPositive() {
//            Assertions.assertEquals(0, storeUT.getProducts().size());     //verify there are no products in store
//            Assertions.assertTrue(storeUT.addNewProduct(ownerRealUser, product));    //success: the product added by the owner
//            Assertions.assertEquals(1, storeUT.getProducts().size());     //verify amount of products in list increased
//        }
//
//        /**
//         * user that is not an owner adds new product
//         */
//        @Test
//        void addNewProductNegative() {
//            Assertions.assertEquals(0, storeUT.getProducts().size());     //verify there are no products in store
//            //fail: user which is no owner cannot add product to store
//            Assertions.assertFalse(storeUT.addNewProduct(fakeOwnerReal, product));
//            Assertions.assertEquals(0, storeUT.getProducts().size());     //verify there are no products in store
//        }
//
//        /**
//         * owner remove a product exists in store
//         */
//        @Test
//        void removeExistsProductFromStorePositive() {
//            //setup for remove product
//            addNewProductSetUp();  //add product to the store
//            Assertions.assertEquals(1, storeUT.getProducts().size()); //verify we starts with no products in store
//            //success: remove product from store
//            Assertions.assertTrue(storeUT.removeProductFromStore(ownerRealUser, product.getProductSn()));    //success: product is removed
//            Assertions.assertEquals(0, storeUT.getProducts().size()); //verify we decreased no. of products in store
//        }
//
//        /**
//         * owner remove a product that doesn't exist in store
//         */
//        @Test
//        void removeNotExistsProductFromStoreNegative() {
//            //setup for remove product
//            addNewProductSetUp();  //add product to the store
//            Assertions.assertEquals(1, storeUT.getProducts().size()); //verify we starts with no products in store
//            //add a product to the store which will be removed for the test
//            //fail: cant remove product which is not exist
//            Assertions.assertFalse(storeUT.removeProductFromStore(ownerRealUser, 122));    //fail: product not exist is not removed
//            Assertions.assertEquals(1, storeUT.getProducts().size()); //verify we decreased no. of products in store
//        }
//
//        /**
//         * user which is not an owner of the store removes a product that doesn't exist in store
//         */
//        @Test
//        void notOwnerRemoveExistsProductFromStoreNegative() {
//            //setup for remove product. add a product to the store which will be removed for the test
//            addNewProductSetUp();  //add product to the store
//            Assertions.assertEquals(1, storeUT.getProducts().size()); //verify we starts with no products in store
//            //fail: only owner can remove products from his store
//            Assertions.assertFalse(storeUT.removeProductFromStore(fakeOwnerReal, product.getProductSn()));    //fail: not owner cannot remove product
//            Assertions.assertEquals(1, storeUT.getProducts().size()); //verify we decreased no. of products in store
//        }
//
//        /**
//         * setting an existing product in store with product properties by an owner
//         */
//        @Test
//        void editProductPositive() {
//            //setup for adding a product to store
//            addProductAsEditProductSetup();
//            int productSN = product.getProductSn();  //the serial number of the product
//            String name = "airpods";    //the new name
//            String category = "electronics";   //new category
//            int amount = 4; //new amount
//            double cost = 100000.5; //new cost
//            //success: the product edited
//            storeUT.editProduct(ownerRealUser, productSN, name, category, amount, cost);
//            Assertions.assertTrue(storeUT.editProduct(ownerRealUser, productSN, name, category, amount, cost));
//        }
//
//        /**
//         * setting an existing product in store but not by an owner so should fail.
//         */
//        @Test
//        void notOwnerEditProductNegative() {
//            //setup for adding a product to store
//            addProductAsEditProductSetup();
//            //not owner
//            int productSN = 0;  //the serial number of the product
//            String name = "airpods";    //the new name
//            String category = "electronics";   //new category
//            int amount = 4; //new amount
//            double cost = 100000.5; //new cost
//            //fail: the product is not edited
//            Assertions.assertFalse(storeUT.editProduct(fakeOwnerReal, productSN, name, category, amount, cost));
//
//        }
//
//        /**
//         * setting not existing product in store with product properties by an owner
//         */
//        @Test
//        void editNotExistProductNegative() {
//            //setup for adding a product to store
//            addProductAsEditProductSetup();
//            int productSN = 23;  //the serial number of the product
//            String name = "airpods";    //the new name
//            String category = "electronics";   //new category
//            int amount = 4; //new amount
//            double cost = 100000.5; //new cost
//            //fail: the product isn't edited
//            Assertions.assertFalse(storeUT.editProduct(ownerRealUser, productSN, name, category, amount, cost));
//        }
//
//        /**
//         * get exist manager in store
//         */
//        @Test
//        void getManagerPositive() {
//            //setup : adding alex as manager to the store
//            addManagerSetup();
//            //success: the manager alex is returned as expected
//            Assertions.assertEquals("Alex", storeUT.getManager(ownerRealUser, "Alex").getUserName());
//        }
//
//
//        /**
//         * try to get a manager with owner which didn't appoint him
//         */
//        @Test
//        void notAppointingOwnerGetManagerNegative() {
//            //setup : adding alex as manager to the store
//            addManagerSetup();
//            //fail: the manager alex is not returned for the appointer fake
//            Assertions.assertEquals(null, storeUT.getManager(fakeOwnerReal, "Alex"));
//        }
//
//        /**
//         * get exist manager object in store
//         */
//        @Test
//        void getManagerObjectPositive() {
//            //setup : adding alex as manager to the store
//            addManagerSetup();
//            //success: the manager alex is returned as expected
//            Assertions.assertEquals("Alex", storeUT.getManagerObject(ownerRealUser, "Alex").getAppointedManager());
//        }
//
//        /**
//         * try to get a manager with owner which didn't appoint him
//         */
//        @Test
//        void notAppointingOwnerGetManagerObjectNegative() {
//            //setup : adding alex as manager to the store
//            addManagerSetup();
//            //fail: the manager alex is not returned for the appointer fake
//            Assertions.assertEquals(null, storeUT.getManagerObject(fakeOwnerReal, "Alex"));
//        }
//
//        /**
//         * owner adds a manager to the store
//         * owner can add a manager who is not already manager or owner of the store
//         */
//        @Test
//        void addManagerPositive() {
//            //success: owner can add a manager who is not already manager or owner of the store
//            Assertions.assertNotNull(storeUT.addManager(ownerRealUser, managerUser));
//        }
//
//        /**
//         * owner adds a manager to the store
//         * owner can't add a manager who is already manager
//         */
//        @Test
//        void addManagerAlreadyManagerNegative() {
//            addManagerSetup();  //adding the manager at the first time
//            //fail: owner can't add a manager who is already manager
//            Assertions.assertFalse(storeUT.addManager(ownerRealUser, managerUser) != null);  //add manager again
//        }
//
//        /**
//         * owner adds a manager to the store
//         * owner can't add a manager who is already owner
//         */
//        @Test
//        void addManagerAlreadyOwnerNegative() {
//            addOwnerSetup();  //adding the manager at the first time
//            //fail: owner can't add a manager who is already owner in the store
//            Assertions.assertNull(storeUT.addManager(ownerRealUser, newOwnerReal));  //add manager again
//        }
//
//        /**
//         * owner adds a manager to the store
//         */
//        @Test
//        void notAppointingOwnerAddManagerNegative() {
//            //fail: not owner try to appoint a new manager for the store
//            Assertions.assertNull(storeUT.addManager(fakeOwnerReal, managerUser));  //add manager again
//        }
//
//        /**
//         * owner can add permissions to a manager he appointed
//         */
//        @Test
//        void addPermissionToManagerPositive() {
//            addManagerSetup();  //add a manager to the store
//            //success: permission added to manager
//            Assertions.assertTrue(storeUT.addPermissionToManager(ownerRealUser, managerUser, StorePermission.EDIT_PRODUCT));
//        }
//
//        /**
//         * not owner try to add permissions to a manager
//         */
//        @Test
//        void notOwnerAddPermissionToManagerNegative() {
//            addManagerSetup();  //add a manager to the store
//            //fail: permission is not added to manager
//            Assertions.assertFalse(storeUT.addPermissionToManager(fakeOwnerReal, managerUser, StorePermission.EDIT_PRODUCT));
//        }
//
//        /**
//         * owner remove permission from manager permissions list
//         */
//        @Test
//        void removePermissionPositive() {
//            addManagerSetup();
//            //success : owner can remove permission from the manager's permissions list
//            Assertions.assertTrue(storeUT.removePermissionFromManager(ownerRealUser, managerUser, StorePermission.VIEW));
//        }
//
//        /**
//         * not an owner try to remove permission from manager permissions list
//         */
//        @Test
//        void removePermissionNegative() {
//            addManagerSetup();
//            //fail: the user trying to remove is not an owner who appointed the manager
//            Assertions.assertFalse(storeUT.removePermissionFromManager(fakeOwnerReal, managerUser, StorePermission.VIEW));
//        }
//
//        /**
//         * owner remove one of managers appointed by him
//         */
//        @Test
//        void removeManagerPositive() {
//            //setup: insert manager into managers list of the store
//            addManagerSetup();
//            Assertions.assertEquals(1, storeUT.getManagers().size());
//            //success: owner can remove manager he appointed
//            Assertions.assertTrue(storeUT.removeManager(ownerRealUser, managerUser));
//            Assertions.assertEquals(0, storeUT.getManagers().size());
//        }
//
//        /**
//         * user who is not owner cannot remove one of managers appointed by someone else
//         */
//        @Test
//        void removeManagerNegative() {
//            //setup: insert manager into managers list of the store
//            addManagerSetup();
//            //fail: only the owner can remove manager he appointed
//            Assertions.assertFalse(storeUT.removeManager(fakeOwnerReal, managerUser));
//            Assertions.assertEquals(1, storeUT.getManagers().size());
//        }
//
//        /**
//         * get existing product in the store
//         */
//        @Test
//        void getExistProductPositive() {
//            addNewProductSetUp();
//            //success: get exist product
//            Assertions.assertEquals(product, storeUT.getProduct(product.getProductSn()));
//        }
//
//        /**
//         * get existing product in the store
//         */
//        @Test
//        void getNotExistProductNegative() {
//            //success: get exist product
//            Throwable exception = Assertions.
//                    assertThrows(ProductDoesntExistException.class, () -> storeUT.getProduct(33));
//            Assertions.assertEquals("A product with id '" + 33 + "' is not exist in store with id: '" + storeUT.getStoreId() + "'", exception.getMessage());
//        }
//
//        /**
//         * user which is manager in the store requests for receipts
//         */
//        @Test
//        void managerViewReceiptsPositive() {
//            addManagerSetup();
//            insertReceiptsSetup();
//            List<Receipt> receipts = storeUT.managerViewReceipts(managerUser);  //get the receipts of store
//            int i = 0;
//            for (Receipt receipt : receipts) {
//                //success : manager can view the receipts and their fields as well
//                Assertions.assertEquals(i, receipt.getAmountToPay());
//                i++;
//            }
//        }
//
//        /**
//         * user which is not manager in the store requests for receipts
//         */
//        @Test
//        void notManagerViewReceiptsNegative() {
//            Throwable exception = Assertions.assertThrows(NoManagerInStoreException.class, () -> storeUT.managerViewReceipts(fakeOwnerReal));
//            //fail: the inserted user is not manager so cant see the receipts in store
//            Assertions.assertEquals("The user name '" + "DT" + "' is not manager in the store number: '" + storeUT.getStoreId() + "'", exception.getMessage());
//        }
//
//        /**
//         * user which is manager in the store requests for receipts
//         */
//        @Test
//        void ownerViewReceiptsPositive() {
//            addOwnerSetup();
//            insertReceiptsSetup();
//            List<Receipt> receipts = storeUT.ownerViewReceipts(ownerRealUser);  //get the receipts of store
//            int i = 0;
//            for (Receipt receipt : receipts) {
//                //success : manager can view the receipts and their fields as well
//                Assertions.assertEquals(i, receipt.getAmountToPay());
//                i++;
//            }
//        }
//
//        /**
//         * user which is not owner in the store requests for receipts
//         */
//        @Test
//        void notOwnerViewReceiptsNegative() {
//            Throwable exception = Assertions.assertThrows(NoManagerInStoreException.class, () -> storeUT.ownerViewReceipts(fakeOwnerReal));
//            //fail: the inserted user is not owner so cant see the receipts in store
//            Assertions.assertEquals("The user name '" + "DT" + "' is not manager in the store number: '" + storeUT.getStoreId() + "'", exception.getMessage());
//        }
//
//        /**
//         * functionality of watching the store information
//         */
//        @Test
//        void showStoreInfo() {
//            //success: shows the store info as string
//            Assertions.assertEquals("The store Store under test has ID: " + storeUT.getStoreId() + "", storeUT.showStoreInfo());
//        }
//
//        /**
//         * functionality of watching the store products information
//         */
//        @Test
//        void showProductsInStoreInfo() {
//            //success: no products in store so show empty string
//            Assertions.assertEquals("", storeUT.showProductsInStoreInfo());
//        }
//
//        //////////////////////////UC 4.2 ///////////////////////////////////////
//
//        /**
//         * see success of applying discount from store on product in bag
//         * all kinds of discounts are
//         */
//        @Test
//        public void applyDiscountPoliciesPositive() {
//            Map<Product, Integer> productBag = new HashMap<>();
//            productBag.put(product, 1);
//            productBag.put(product2,1);
//            productBag.put(product3,1);
//            addNewDiscountToStoreSetup();
//            storeUT.applyDiscountPolicies(productBag);
//            //success: all discounts were applied
//            Assertions.assertTrue(discount1.isApplied());
//            Assertions.assertTrue(discount2.isApplied());
//            Assertions.assertTrue(discount3.isApplied());
//
//
//        }
//
//
//        /**
//         * see fail of applying discount from store on product in bag
//         * because of illegal negative price after discount
//         * can't be checked under unit tests need integration
//         */
//        @Test
//        public void applyDiscountPoliciesNegative() {
//            Map<Product, Integer> productBag = new HashMap<>();
//            productBag.put(product2, 1);
//
//            addNewDiscountToStoreSetup();   //add discount
//            product2.setCost(-10);
//            Throwable exception = Assertions
//                    .assertThrows(IllegalProductPriceException.class, () -> storeUT.applyDiscountPolicies(productBag));
//            Assertions.assertEquals("The discount with id: " + 35 +
//                    " caused price to be equal or less than zero!", exception.getMessage());
//        }
//
//        /**
//         * check the case of purchase that pass the exist store policy
//         * empty case: should approve there are no policies
//         */
//        @Test
//        public void isApprovedPurchaseEmptyPoliciesPositive() {
//            Map<Product, Integer> productBag = new HashMap<>();
//            productBag.put(product, 1);
//            BillingAddress billingAddress = BillingAddress.builder().country("Israel").build();
//            storeUT.isApprovedPurchasePolicies(productBag, billingAddress);
//        }
//
//        /**
//         * check the case of purchase that pass the exist store policy
//         * should approve with buyer from the permmited country
//         */
//        @Test
//        public void isApprovedPurchasePoliciesPositive() {
////            Map<Product, Integer> productBag = new HashMap<>();
////            productBag.put(product, 1);
////            Set<String> countries = new HashSet<>();
////            countries.add("Israel");
////            Purchase purchasePolicy = Purchase.builder()
////                    .countriesPermitted(countries)
////                    .build();
////            addNewPurchasePolicyToStoreSetup(purchasePolicy);   //add discount
////            BillingAddress billingAddress = BillingAddress.builder().country("Israel").build();
////            storeUT.isApprovedPurchasePolicies(productBag, billingAddress);
//        }
//
//
//        /**
//         * check the case of purchase that pass the exist store policy
//         * negative case: should approve with buyer from not permmited country
//         */
//        @Test
//        public void isApprovedPurchasePoliciesNegative() {
////            Map<Product, Integer> productBag = new HashMap<>();
////            productBag.put(product, 1);
////            Set<String> countries = new HashSet<>();
////            countries.add("Uruguay");
////            Purchase purchasePolicy = Purchase.builder()
////                    .countriesPermitted(countries)
////                    .build();
////            addNewPurchasePolicyToStoreSetup(purchasePolicy);   //add discount
////            BillingAddress billingAddress = BillingAddress.builder().country("Israel").build();
////            Throwable exception = Assertions
////                    .assertThrows(PurchasePolicyException.class, () -> storeUT.isApprovedPurchasePolicies(productBag, billingAddress));
////            Assertions.assertEquals("Purchase policy error occured: Sorry, but your user details are incompatible with the store policy: " +
////                    "store doesn't make deliveries to: Israel", exception.getMessage());
//        }
//
//        /**
//         * add new discount to store
//         */
//        @Test
//        public void addDiscountToStore() {
//            Assertions.assertEquals(0, storeUT.getDiscounts().size());
//            storeUT.addDiscount(ownerRealUser, discount);
//            Assertions.assertEquals(1, storeUT.getDiscounts().size());
//        }
//
//        /**
//         * add new purchase policy to store
//         */
//        @Test
//        public void addPurchasePolicyToStore() {
//            Assertions.assertEquals(0, storeUT.getPurchasePolicies().size());
//            storeUT.addPurchasePolicy(ownerRealUser, purchasePolicy);
//            Assertions.assertEquals(1, storeUT.getPurchasePolicies().size());
//        }
//
////        /**
////         * edit existed discount in store
////         */
////        @Test
////        public void editDiscountPositive() {
////            discount = Discount.builder()
////                    .discountId(0)
////                    .discountPercentage(10)
////                    .amountOfProductsForApplyDiscounts(new HashMap<>())
////                    .endTime(Calendar.getInstance())
////                    .description("")
////                    .composedDiscounts(new ArrayList<>())
////                    .compositeOperator(CompositeOperator.OR)
////                    .isStoreDiscount(false)
////                    .minPrice(0)
////                    .productsUnderThisDiscount(new HashMap<>())
////                    .build();
////            addNewDiscountToStoreSetup(discount); //add discount to store
////            discount.setMinPrice(10); // set new min price to edit
////            Assertions.assertEquals(storeUT.addEditDiscount(ownerRealUser, discount).getMinPrice(), 10);
////        }
//
////        /**
////         * edit not existed discount in store
////         */
////        @Test
////        public void editDiscountNegative() {
////            discount = Discount.builder()
////                    .discountId(0)
////                    .discountPercentage(10)
////                    .amountOfProductsForApplyDiscounts(new HashMap<>())
////                    .endTime(Calendar.getInstance())
////                    .description("")
////                    .composedDiscounts(new ArrayList<>())
////                    .compositeOperator(CompositeOperator.OR)
////                    .isStoreDiscount(false)
////                    .minPrice(0)
////                    .productsUnderThisDiscount(new HashMap<>())
////                    .build();
////            discount.setMinPrice(10); // set new min price to edit
////            Assertions.assertNull(storeUT.addEditDiscount(ownerRealUser, discount));
////        }
//
//        /**
//         * edit existed purchase in store
//         */
//        @Test
//        public void editPurchasePositive() {
////            purchasePolicy = Purchase.builder()
////                    .min(0)
////                    .max(13)
////                    .isShoppingBagPurchaseLimit(false)
////                    .build();
////            addNewPurchasePolicyToStoreSetup(purchasePolicy); //add discount to store
////            purchasePolicy.setMax(100); // set new min price to edit
////            Assertions.assertEquals(storeUT.addEditPurchase(ownerRealUser, purchasePolicy).getMax(), 100);
//        }
//
//        /**
//         * edit not existed purchase in store
//         */
//        @Test
//        public void editPurchaseNegative() {
////            purchasePolicy = Purchase.builder()
////                    .min(0)
////                    .max(13)
////                    .isShoppingBagPurchaseLimit(false)
////                    .build();
////            purchasePolicy.setMax(100); // set new min price to edit
////            Assertions.assertNull(storeUT.addEditPurchase(ownerRealUser, purchasePolicy));
//        }
//
////////////////////setups///////////////////////////////////
//
//        /**
//         * add all types of discounts to the store
//         */
//        private void addNewDiscountToStoreSetup() {
//            //storeUT.addDiscount(ownerRealUser, discount);
//            storeUT.addDiscount(ownerRealUser,discount1);
//            storeUT.addDiscount(ownerRealUser,discount2);
//            storeUT.addDiscount(ownerRealUser,discount3);
//            storeUT.addDiscount(ownerRealUser,discount4);
//
//        }
//
//        private void addNewPurchasePolicyToStoreSetup(Purchase purchasePolicy) {
//            storeUT.addPurchasePolicy(ownerRealUser, purchasePolicy);
//        }
//
//        private void insertReceiptsSetup() {
//            for (int i = 0; i < 10; i++) {
//                storeUT.getReceipts().add(Receipt.builder()
//                        .amountToPay(i)
//                        .productsBought(new HashMap<Product, Integer>())
//                        .purchaseDate(new Date())
//                        .storeId(0)
//                        .userName("felix")
//                        .build());
//            }
//        }
//
//        private void addProductAsEditProductSetup() {
//            product = new Product("Crashpad", ProductCategory.SPORTING_GOODS, 2, 987, 12);
//            storeUT.addNewProduct(ownerRealUser, product);
//
//        }
//
//        private void addNewProductSetUp() {
//            storeUT.addNewProduct(ownerRealUser, product);
//        }
//
//        private void addManagerSetup() {
//            storeUT.addManager(ownerRealUser, managerUser);  //adding alex as new manager in the store
//        }
//
//        private void addOwnerSetup() {
//            storeUT.addOwner(ownerRealUser, newOwnerReal);
//        }
//
//    }
//
//    private void setUpRemoveOwner(){
//        Set<OwnersAppointee> appointedOwners = new HashSet<>();
//        Set<UserSystem> owners = new HashSet<>();
//        owners.add(owner);
//        owners.add(managerUser);
//        Set<UserSystem> ownerSet = new HashSet<>();
//        ownerSet.add(managerUser);
//        appointedOwners.add(new OwnersAppointee(owner.getUserName(),ownerSet));
//        ownerSet = makeOwnersSet(4, "Erik");
//        UserSystem usr = getUser(ownerSet);
//        appointedOwners.add(new OwnersAppointee(managerUser.getUserName(),ownerSet));
//        owners.addAll(ownerSet);
//        ownerSet = makeOwnersSet(2, "Fez");
//        appointedOwners.add(new OwnersAppointee(usr.getUserName(), ownerSet));
//        owners.addAll(ownerSet);
//        usr = getUser(ownerSet);
//        ownerSet = makeOwnersSet(2, "Michael");
//        appointedOwners.add(new OwnersAppointee(usr.getUserName(), ownerSet));
//        owners.addAll(ownerSet);
//        storeUT.setOwners(owners);
//        storeUT.setAppointedOwners(appointedOwners);
//        Set<Store> store = new HashSet<>();
//        store.add(storeUT);
//        owner.setOwnedStores(store);
//        managerUser.setOwnedStores(store);
//    }
//
//    private void setUpRemoveOwnerManager(){
//        setUpRemoveOwner();
//        ownerRealUser = mock(UserSystem.class);
//        when(ownerRealUser.getUserName()).thenReturn("IAmAManager");
//       // when(((MangerStore)any()).getAppointedManager()).thenReturn(ownerRealUser);
//        storeUT.addManager(managerUser,ownerRealUser);
//    }
//
//    private UserSystem getUser(Set<UserSystem> users){
//        for (UserSystem user: users) {
//            return user;
//        }
//        return null;
//    }
//
//    private Set<UserSystem> makeOwnersSet(int length, String name){
//        Set<UserSystem> tempOwners = new HashSet<>();
//        for (int i=0; i < length ; i++){
//            Set<Store> stores = new HashSet<>();
//            stores.add(storeUT);
//            UserSystem userSystem = UserSystem.builder()
//                    .userName(name+i)
//                    .ownedStores(stores).build();
//            tempOwners.add(userSystem);
//        }
//        return tempOwners;
//    }
//}

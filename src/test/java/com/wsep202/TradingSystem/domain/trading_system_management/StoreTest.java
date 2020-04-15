package com.wsep202.TradingSystem.domain.trading_system_management;

import com.wsep202.TradingSystem.domain.exception.NoManagerInStoreException;
import com.wsep202.TradingSystem.domain.exception.ProductDoesntExistException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.util.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class StoreTest {
    private MangerStore managerStore; //the manager object of the manager user
    private UserSystem managerUser; //role as manager to add and get
    private UserSystem newOwner;    //role as an appointed owner in the store
    private UserSystem fakeOwner;   //role as not owner in the store
    private UserSystem owner;   //role as appointing owner in the store
    private Store storeUT;  //unit under test
    private PurchasePolicy purchasePolicy;
    private DiscountPolicy discountPolicy;
    private Product product;    //product to add

    //integration attributes
    private UserSystem ownerRealUser;
    private UserSystem newOwnerReal;
    private UserSystem fakeOwnerReal;


    @AfterEach
    void tearDown() {
    }

    @Nested
    public class StoreTestUnit {

        @BeforeEach
        void setUp() {
            //new manager to add
            newOwner = mock(UserSystem.class);   //the user the owner want to appoint as a new store owner
            //stubbing
            when(newOwner.getUserName()).thenReturn("Jones");

            //manager for get and add owner tests
            managerUser = mock(UserSystem.class);
            when(managerUser.getUserName()).thenReturn("Alex");
            managerStore = new MangerStore(managerUser);

            //not owner of the store
            fakeOwner = mock(UserSystem.class);
            when(fakeOwner.getUserName()).thenReturn("Donald");

            purchasePolicy = new PurchasePolicy();
            discountPolicy = new DiscountPolicy();
            //owner and opener of the store
            owner = mock(UserSystem.class);
            when(owner.getUserName()).thenReturn("Michael");
            storeUT = new Store(owner,purchasePolicy,discountPolicy,"Store under test");
            product = mock(Product.class);
        }

        /**
         * verifies the method returns occurences of products with the name inserted
         * in case there are in the store
         */
        @Test
        void searchProductByNamePositive(){
            addNewProductSetUp();
            when(product.getName()).thenReturn("PSP");
            Set<Product> resultsForSearch = storeUT.searchProductByName("PSP");
            for(Product product : resultsForSearch){
                //success: there is one product in the list after filter by name PSP
                Assertions.assertEquals("PSP",product.getName());
            }
        }

        /**
         * check the case of searching for products with name that is not exist for any product
         */
        @Test
        void searchProductByNameNegative(){
            addNewProductSetUp();
            when(product.getName()).thenReturn("football");
            Set<Product> resultsForSearch = storeUT.searchProductByName("PSP");
            //fail: no results for the name football
            Assertions.assertEquals(0,resultsForSearch.size());
        }

        /**
         *  get list of products only with the same category which inserted
         */
        @Test
        void searchProductByCategoryPositive(){
            addNewProductSetUp();
            ProductCategory category = ProductCategory.HEALTH;
            when(product.getCategory()).thenReturn(category);
            Set<Product> resultsForSearch = storeUT.searchProductByCategory(ProductCategory.HEALTH);
            for(Product product : resultsForSearch){
                //success: all products in the list are in the same category
                Assertions.assertEquals(category,product.getCategory());
            }
        }

        /**
         *  handling with search of category which is not exist for the store products
         */
        @Test
        void searchProductByCategoryNegative(){
            addNewProductSetUp();
            ProductCategory notInStoreCategory = ProductCategory.TOYS_HOBBIES;
            ProductCategory category = ProductCategory.HEALTH;
            when(product.getCategory()).thenReturn(category);
            Set<Product> resultsForSearch = storeUT.searchProductByCategory(notInStoreCategory);
            //fail: the requested category not exist so empty list of products returned
            Assertions.assertEquals(0,resultsForSearch.size());
        }

        /**
         * verifies searching by keywords which is part of a name of product in store
         * returns the proper products list
         */
        @Test
        void searchProductByKeywordPositive(){
            addNewProductSetUp();
            when(product.getName()).thenReturn("foot ball");
            List<String> keywords = new ArrayList<>();
            keywords.add("foot");
            keywords.add(" b");
            Set<Product> resultsForSearch = storeUT.searchProductByKeyWords(keywords);
            for(Product product : resultsForSearch){
                //success: there is match between keyword to product
                Assertions.assertTrue(product.getName().contains("foot b"));
            }
        }

        /**
         * test handling with searching of keyword which doesn't exist
         */
        @Test
        void searchProductByKeywordNegative(){
            addNewProductSetUp();
            when(product.getName()).thenReturn("foot ball");
            List<String> keywords = new ArrayList<>();
            keywords.add("to");
            Set<Product> resultsForSearch = storeUT.searchProductByKeyWords(keywords);
            //fail: there is no match between keyword to products
            Assertions.assertEquals(0,resultsForSearch.size());
        }

        /**
         * @pre the appointing owner is registered as the store owner
         * adding an owner by an store owner
         */
        @Test
        void addOwnerPositive() {
            Assertions.assertTrue(storeUT.addOwner(owner,newOwner));    //Success: owner appointing a newOwner as a new owner
        }
        /**
         * @pre the appointing owner is not registered as the store owner
         * adding an owner by not store owner
         */
        @Test
        void addOwnerNegativeNotOwner() {
            UserSystem newOwner = mock(UserSystem.class);   //the user the fake owner want to appoint as a new store owner
            //stubbing
            when(newOwner.getUserName()).thenReturn("Jones");
            Assertions.assertFalse(storeUT.addOwner(fakeOwner,newOwner)); //Fail: owner appointing a newOwner as a new owner
        }

        /**
         * @pre the appointed owner is already an owner in the store
         * adding an already owner by owner
         */
        @Test
        void addOwnerNegativeNotAlreadyOwner() {
            Assertions.assertFalse(storeUT.addOwner(owner,owner)); //Fail: owner appointing a an already owner to be owner
        }
        /**
         * owner adds new product
         */
        @Test
        void addNewProductPositive() {
            Assertions.assertEquals(0,storeUT.products.size());     //verify there are no products in store
            Assertions.assertTrue(storeUT.addNewProduct(owner,product));    //success: the product added by the owner
            Assertions.assertEquals(1,storeUT.products.size());     //verify amount of products in list increased
        }
        /**
         * user that is not an owner adds new product
         */
        @Test
        void addNewProductNegative() {
            Assertions.assertEquals(0,storeUT.products.size());     //verify there are no products in store
            UserSystem fakeOwner = mock(UserSystem.class);
            when(fakeOwner.getUserName()).thenReturn("Donald");
            //fail: user which is no owner cannot add product to store
            Assertions.assertFalse(storeUT.addNewProduct(fakeOwner,product));
            Assertions.assertEquals(0,storeUT.products.size());     //verify there are no products in store
        }

        /**
         * owner remove a product exists in store
         */
        @Test
        void removeExistsProductFromStorePositive() {
            //setup for remove product
            addNewProductSetUp();  //add product to the store
            Assertions.assertEquals(1,storeUT.products.size()); //verify we starts with no products in store
            //add a product to the store which will be removed for the test
            when(product.getProductSn()).thenReturn(12);    // let product SN to for the check which one to remove
            Assertions.assertTrue(storeUT.removeProductFromStore(owner,12));    //success: product is removed
            Assertions.assertEquals(0,storeUT.products.size()); //verify we decreased no. of products in store
        }

        /**
         * owner remove a product that doesn't exist in store
         */
        @Test
        void removeNotExistsProductFromStoreNegative() {
            //setup for remove product
            addNewProductSetUp();  //add product to the store
            Assertions.assertEquals(1,storeUT.products.size()); //verify we starts with no products in store
            //add a product to the store which will be removed for the test
            when(product.getProductSn()).thenReturn(12);    // let product SN to for the check which one to remove
            Assertions.assertFalse(storeUT.removeProductFromStore(owner,4));    //fail: product not exist is not removed
            Assertions.assertEquals(1,storeUT.products.size()); //verify we decreased no. of products in store
        }

        /**
         * user which is not an owner of the store removes a product that doesn't exist in store
         */
        @Test
        void notOwnerRemoveExistsProductFromStoreNegative() {
            UserSystem notOwner = mock(UserSystem.class);   //not an owner
            //setup for remove product. add a product to the store which will be removed for the test
            addNewProductSetUp();  //add product to the store
            Assertions.assertEquals(1,storeUT.products.size()); //verify we starts with no products in store
            //stubs
            when(notOwner.getUserName()).thenReturn("Philip");
            when(product.getProductSn()).thenReturn(12);    // let product SN to for the check which one to remove
            Assertions.assertFalse(storeUT.removeProductFromStore(notOwner,12));    //fail: not owner cannot remove product
            Assertions.assertEquals(1,storeUT.products.size()); //verify we decreased no. of products in store
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
            String category = "electronics" ;   //new category
            int amount = 4; //new amount
            double cost = 100000.5; //new cost
            //success: the product edited
            storeUT.editProduct(owner,productSN,name,category,amount,cost);
            Assertions.assertTrue(storeUT.editProduct(owner,productSN,name,category,amount,cost));
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
            String category = "electronics" ;   //new category
            int amount = 4; //new amount
            double cost = 100000.5; //new cost
            //fail: the product is not edited
            Assertions.assertFalse(storeUT.editProduct(notOwner,productSN,name,category,amount,cost));

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
            String category = "electronics" ;   //new category
            int amount = 4; //new amount
            double cost = 100000.5; //new cost
            //fail: the product isn't edited
            Assertions.assertFalse(storeUT.editProduct(owner,productSN,name,category,amount,cost));
        }

        /**
         * get exist manager in store
         */
        @Test
        void getManagerPositive(){
            //setup : adding alex as manager to the store
            addManagerSetup();
            //success: the manager alex is returned as expected
            Assertions.assertEquals("Alex",storeUT.getManager(owner,"Alex").getUserName());

        }


        /**
         * try to get a manager with owner which didn't appoint him
         */
        @Test
        void notAppointingOwnerGetManagerNegative(){
            //setup : adding alex as manager to the store
            addManagerSetup();
            //fail: the manager alex is not returned for the appointer fake
            Assertions.assertEquals(null,storeUT.getManager(fakeOwner,"Alex"));
        }

        /**
         * get exist manager object in store
         */
        @Test
        void getManagerObjectPositive(){
            //setup : adding alex as manager to the store
            addManagerSetup();
            //success: the manager alex is returned as expected
            Assertions.assertEquals(managerStore,storeUT.getManagerObject(owner,"Alex"));
        }

        /**
         * try to get a manager with owner which didn't appoint him
         */
        @Test
        void notAppointingOwnerGetManagerObjectNegative(){
            //setup : adding alex as manager to the store
            addManagerSetup();
            //fail: the manager alex is not returned for the appointer fake
            Assertions.assertEquals(null,storeUT.getManagerObject(fakeOwner,"Alex"));
        }

        /**
         * owner adds a manager to the store
         * owner can add a manager who is not already manager or owner of the store
         */
        @Test
        void addManagerPositive() {
            //success: owner can add a manager who is not already manager or owner of the store
            Assertions.assertTrue(storeUT.addManager(owner,managerUser));
        }

        /**
         * owner adds a manager to the store
         * owner can't add a manager who is already manager
         */
        @Test
        void addManagerAlreadyManagerNegative() {
            addManagerSetup();  //adding the manager at the first time
            //fail: owner can't add a manager who is already manager
            Assertions.assertFalse(storeUT.addManager(owner,managerUser));  //add manager again
        }

        /**
         * owner adds a manager to the store
         * owner can't add a manager who is already owner
         */
        @Test
        void addManagerAlreadyOwnerNegative() {
            addOwnerSetup();  //adding the manager at the first time
            //fail: owner can't add a manager who is already owner in the store
            Assertions.assertFalse(storeUT.addManager(owner,newOwner));  //add manager again
        }

        /**
         * owner adds a manager to the store
         */
        @Test
        void notAppointingOwnerAddManagerNegative() {
            //fail: not owner try to appoint a new manager for the store
            Assertions.assertFalse(storeUT.addManager(fakeOwner,managerUser));  //add manager again
        }

        /**
         * owner can add permissions to a manager he appointed
         */
        @Test
        void addPermissionToManagerPositive() {
            addManagerSetup();  //add a manager to the store
            //mock
            managerStore = mock(MangerStore.class);
            when(managerStore.getAppointedManager()).thenReturn(managerUser);
            when(managerStore.addStorePermission(StorePermission.EDIT)).thenReturn(true);
            //success: permission added to manager
            Assertions.assertTrue(storeUT.addPermissionToManager(owner,managerUser,StorePermission.EDIT));
        }

        /**
         * not owner try to add permissions to a manager
         */
        @Test
        void notOwnerAddPermissionToManagerNegative() {
            addManagerSetup();  //add a manager to the store
            //mock
            managerStore = mock(MangerStore.class);
            when(managerStore.getAppointedManager()).thenReturn(managerUser);
            when(managerStore.addStorePermission(StorePermission.EDIT)).thenReturn(true);
            //fail: permission is not added to manager
            Assertions.assertFalse(storeUT.addPermissionToManager(fakeOwner,managerUser,StorePermission.EDIT));
        }

        /**
         * owner remove permission from manager permissions list
         */
        @Test
        void removePermissionPositive(){
            addManagerSetup();
            //mock
            managerStore = mock(MangerStore.class);
            when(managerStore.getAppointedManager()).thenReturn(managerUser);
            when(managerStore.removeStorePermission(StorePermission.VIEW)).thenReturn(true);
            //success : owner can remove permission from the manager's permissions list
            Assertions.assertTrue(storeUT.removePermissionFromManager(owner,managerUser,StorePermission.VIEW));
        }

        /**
         * not an owner try to remove permission from manager permissions list
         */
        @Test
        void removePermissionNegative(){
            addManagerSetup();
            //mock
            managerStore = mock(MangerStore.class);
            when(managerStore.getAppointedManager()).thenReturn(managerUser);
            when(managerStore.addStorePermission(StorePermission.VIEW)).thenReturn(true);
            //fail: the user trying to remove is not an owner who appointed the manager
            Assertions.assertFalse(storeUT.removePermissionFromManager(fakeOwner,managerUser,StorePermission.VIEW));
        }

        /**
         * owner remove one of managers appointed by him
         */
        @Test
        void removeManagerPositive() {
            //setup: insert manager into managers list of the store
            addManagerSetup();
            Assertions.assertEquals(1,storeUT.getManagers().size());
            //mock
            managerStore = mock(MangerStore.class);
            when(managerStore.getAppointedManager()).thenReturn(managerUser);
            //success: owner can remove manager he appointed
            Assertions.assertTrue(storeUT.removeManager(owner, managerUser));
            Assertions.assertEquals(0,storeUT.getManagers().size());
        }

        /**
         * user who is not owner cannot remove one of managers appointed by someone else
         */
        @Test
        void removeManagerNegative() {
            //setup: insert manager into managers list of the store
            addManagerSetup();
            //mock
            managerStore = mock(MangerStore.class);
            when(managerStore.getAppointedManager()).thenReturn(managerUser);
            //fail: only the owner can remove manager he appointed
            Assertions.assertFalse(storeUT.removeManager(fakeOwner, managerUser));
            Assertions.assertEquals(1,storeUT.getManagers().size());
        }

        /**
         * get existing product in the store
         */
        @Test
        void getExistProductPositive() {
            addNewProductSetUp();
            when(product.getProductSn()).thenReturn(33);
            //success: get exist product
            Assertions.assertEquals(product,storeUT.getProduct(33));
        }

        /**
         * get existing product in the store
         */
        @Test
        void getNotExistProductNegative() {
            when(product.getProductSn()).thenReturn(33);
            //success: get exist product
            Throwable exception = Assertions.assertThrows(ProductDoesntExistException.class,()->storeUT.getProduct(33));
            Assertions.assertEquals("A product with id '" + 33 + "' is not exist in store with id: '"+ storeUT.getStoreId() + "'",exception.getMessage());
        }

        /**
         * user which is manager in the store requests for receipts
         */
        @Test
        void managerViewReceiptsPositive() {
            addManagerSetup();
            insertReceiptsSetup();
            List<Receipt> receipts = storeUT.managerViewReceipts(managerUser);  //get the receipts of store
            int i=0;
            for(Receipt receipt : receipts){
                //success : manager can view the receipts and their fields as well
                Assertions.assertEquals(i,receipt.getAmountToPay());
                i++;
            }
        }

        /**
         * user which is not manager in the store requests for receipts
         */
        @Test
        void notManagerViewReceiptsNegative() {
            Throwable exception = Assertions.assertThrows(NoManagerInStoreException.class,()->storeUT.managerViewReceipts(fakeOwner));
            //fail: the inserted user is not manager so cant see the receipts in store
            Assertions.assertEquals("The user name '" + "Donald" + "' is not manager in the store number: '"+ storeUT.getStoreId() + "'",exception.getMessage());
        }

        /**
         * user which is manager in the store requests for receipts
         */
        @Test
        void ownerViewReceiptsPositive() {
            addOwnerSetup();
            insertReceiptsSetup();
            List<Receipt> receipts = storeUT.ownerViewReceipts(owner);  //get the receipts of store
            int i=0;
            for(Receipt receipt : receipts){
                //success : manager can view the receipts and their fields as well
                Assertions.assertEquals(i,receipt.getAmountToPay());
                i++;
            }
        }

        /**
         * user which is not owner in the store requests for receipts
         */
        @Test
        void notOwnerViewReceiptsNegative() {
            Throwable exception = Assertions.assertThrows(NoManagerInStoreException.class,()->storeUT.ownerViewReceipts(fakeOwner));
            //fail: the inserted user is not owner so cant see the receipts in store
            Assertions.assertEquals("The user name '" + "Donald" + "' is not manager in the store number: '"+ storeUT.getStoreId() + "'",exception.getMessage());
        }

        /**
         * functionality of watching the store information
         */
        @Test
        void showStoreInfo(){
            //success: shows the store info as string
            Assertions.assertEquals("The store Store under test has ID: " +storeUT.getStoreId()+ "",storeUT.showStoreInfo());
        }

        /**
         * functionality of watching the store products information
         */
        @Test
        void showProductsInStoreInfo(){
            //success: no products in store so show empty string
            Assertions.assertEquals("",storeUT.showProductsInStoreInfo());
        }

//////////////////setups///////////////////////////////////

        private void insertReceiptsSetup(){
            for(int i = 0; i < 10 ; i++){
                storeUT.getReceipts().add(Receipt.builder()
                        .amountToPay(i)
                        .productsBought(new HashSet<Product>())
                        .purchaseDate(new Date())
                        .storeId(0)
                        .userName("felix")
                        .build());
            }
        }

        private void addProductAsEditProductSetup() {
            product = new Product("Crashpad",ProductCategory.SPORTING_GOODS,2,987,12);
            storeUT.addNewProduct(owner, product);

        }
        private void addNewProductSetUp(){
            storeUT.addNewProduct(owner,product);
        }

        private void addManagerSetup(){
            storeUT.addManager(owner,managerUser);  //adding alex as new manager in the store
        }

        private void addOwnerSetup() {
            storeUT.addOwner(owner, newOwner);
        }

    }

    @Nested
    public class StoreTestIntegration {

        @BeforeEach
        void setUp() {
            //new manager to add
            newOwnerReal = new UserSystem("new","n","y","u");   //the user the owner want to appoint as a new store owner

            //manager for get and add owner tests
            managerUser = new UserSystem("Alex","Alex","Alex","Alex");
            managerStore = new MangerStore(managerUser);

            //not owner of the store
            fakeOwnerReal = new UserSystem("DT","Donald","Trump","DT123");

            purchasePolicy = new PurchasePolicy();
            discountPolicy = new DiscountPolicy();
            //owner and opener of the store
            ownerRealUser = new UserSystem("Michael","micha","toti","pass");
            storeUT = new Store(ownerRealUser,purchasePolicy,discountPolicy,"Store under test");
            product = new Product("Bamba",ProductCategory.SPORTING_GOODS,1,11,storeUT.getStoreId());
        }


        /**
         * @pre the appointing owner is registered as the store owner
         * adding an owner by an store owner
         */
        @Test
        void addOwnerPositive() {
            Assertions.assertTrue(storeUT.addOwner(ownerRealUser,newOwnerReal));    //Success: owner appointing a newOwner as a new owner
        }
        /**
         * @pre the appointing owner is not registered as the store owner
         * adding an owner by not store owner
         */
        @Test
        void addOwnerNegativeNotOwner() {
            Assertions.assertFalse(storeUT.addOwner(fakeOwnerReal,newOwnerReal)); //Fail: owner appointing a newOwner as a new owner
        }

        /**
         * @pre the appointed owner is already an owner in the store
         * adding an already owner by owner
         */
        @Test
        void addOwnerNegativeNotAlreadyOwner() {
            Assertions.assertFalse(storeUT.addOwner(ownerRealUser,ownerRealUser)); //Fail: owner appointing a an already owner to be owner
        }
        /**
         * owner adds new product
         */
        @Test
        void addNewProductPositive() {
            Assertions.assertEquals(0,storeUT.products.size());     //verify there are no products in store
            Assertions.assertTrue(storeUT.addNewProduct(ownerRealUser,product));    //success: the product added by the owner
            Assertions.assertEquals(1,storeUT.products.size());     //verify amount of products in list increased
        }
        /**
         * user that is not an owner adds new product
         */
        @Test
        void addNewProductNegative() {
            Assertions.assertEquals(0,storeUT.products.size());     //verify there are no products in store
            //fail: user which is no owner cannot add product to store
            Assertions.assertFalse(storeUT.addNewProduct(fakeOwnerReal,product));
            Assertions.assertEquals(0,storeUT.products.size());     //verify there are no products in store
        }

        /**
         * owner remove a product exists in store
         */
        @Test
        void removeExistsProductFromStorePositive() {
            //setup for remove product
            addNewProductSetUp();  //add product to the store
            Assertions.assertEquals(1,storeUT.products.size()); //verify we starts with no products in store
            //success: remove product from store
            Assertions.assertTrue(storeUT.removeProductFromStore(ownerRealUser,product.getProductSn()));    //success: product is removed
            Assertions.assertEquals(0,storeUT.products.size()); //verify we decreased no. of products in store
        }

        /**
         * owner remove a product that doesn't exist in store
         */
        @Test
        void removeNotExistsProductFromStoreNegative() {
            //setup for remove product
            addNewProductSetUp();  //add product to the store
            Assertions.assertEquals(1,storeUT.products.size()); //verify we starts with no products in store
            //add a product to the store which will be removed for the test
            //fail: cant remove product which is not exist
            Assertions.assertFalse(storeUT.removeProductFromStore(ownerRealUser,122));    //fail: product not exist is not removed
            Assertions.assertEquals(1,storeUT.products.size()); //verify we decreased no. of products in store
        }

        /**
         * user which is not an owner of the store removes a product that doesn't exist in store
         */
        @Test
        void notOwnerRemoveExistsProductFromStoreNegative() {
            //setup for remove product. add a product to the store which will be removed for the test
            addNewProductSetUp();  //add product to the store
            Assertions.assertEquals(1,storeUT.products.size()); //verify we starts with no products in store
            //fail: only owner can remove products from his store
            Assertions.assertFalse(storeUT.removeProductFromStore(fakeOwnerReal,product.getProductSn()));    //fail: not owner cannot remove product
            Assertions.assertEquals(1,storeUT.products.size()); //verify we decreased no. of products in store
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
            String category = "electronics" ;   //new category
            int amount = 4; //new amount
            double cost = 100000.5; //new cost
            //success: the product edited
            storeUT.editProduct(ownerRealUser,productSN,name,category,amount,cost);
            Assertions.assertTrue(storeUT.editProduct(ownerRealUser,productSN,name,category,amount,cost));
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
            String category = "electronics" ;   //new category
            int amount = 4; //new amount
            double cost = 100000.5; //new cost
            //fail: the product is not edited
            Assertions.assertFalse(storeUT.editProduct(fakeOwnerReal,productSN,name,category,amount,cost));

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
            String category = "electronics" ;   //new category
            int amount = 4; //new amount
            double cost = 100000.5; //new cost
            //fail: the product isn't edited
            Assertions.assertFalse(storeUT.editProduct(ownerRealUser,productSN,name,category,amount,cost));
        }

        /**
         * get exist manager in store
         */
        @Test
        void getManagerPositive(){
            //setup : adding alex as manager to the store
            addManagerSetup();
            //success: the manager alex is returned as expected
            Assertions.assertEquals("Alex",storeUT.getManager(ownerRealUser,"Alex").getUserName());
        }


        /**
         * try to get a manager with owner which didn't appoint him
         */
        @Test
        void notAppointingOwnerGetManagerNegative(){
            //setup : adding alex as manager to the store
            addManagerSetup();
            //fail: the manager alex is not returned for the appointer fake
            Assertions.assertEquals(null,storeUT.getManager(fakeOwnerReal,"Alex"));
        }

        /**
         * get exist manager object in store
         */
        @Test
        void getManagerObjectPositive(){
            //setup : adding alex as manager to the store
            addManagerSetup();
            //success: the manager alex is returned as expected
            Assertions.assertEquals(managerStore,storeUT.getManagerObject(ownerRealUser,"Alex"));
        }

        /**
         * try to get a manager with owner which didn't appoint him
         */
        @Test
        void notAppointingOwnerGetManagerObjectNegative(){
            //setup : adding alex as manager to the store
            addManagerSetup();
            //fail: the manager alex is not returned for the appointer fake
            Assertions.assertEquals(null,storeUT.getManagerObject(fakeOwnerReal,"Alex"));
        }

        /**
         * owner adds a manager to the store
         * owner can add a manager who is not already manager or owner of the store
         */
        @Test
        void addManagerPositive() {
            //success: owner can add a manager who is not already manager or owner of the store
            Assertions.assertTrue(storeUT.addManager(ownerRealUser,managerUser));
        }

        /**
         * owner adds a manager to the store
         * owner can't add a manager who is already manager
         */
        @Test
        void addManagerAlreadyManagerNegative() {
            addManagerSetup();  //adding the manager at the first time
            //fail: owner can't add a manager who is already manager
            Assertions.assertFalse(storeUT.addManager(ownerRealUser,managerUser));  //add manager again
        }

        /**
         * owner adds a manager to the store
         * owner can't add a manager who is already owner
         */
        @Test
        void addManagerAlreadyOwnerNegative() {
            addOwnerSetup();  //adding the manager at the first time
            //fail: owner can't add a manager who is already owner in the store
            Assertions.assertFalse(storeUT.addManager(ownerRealUser,newOwnerReal));  //add manager again
        }

        /**
         * owner adds a manager to the store
         */
        @Test
        void notAppointingOwnerAddManagerNegative() {
            //fail: not owner try to appoint a new manager for the store
            Assertions.assertFalse(storeUT.addManager(fakeOwnerReal,managerUser));  //add manager again
        }

        /**
         * owner can add permissions to a manager he appointed
         */
        @Test
        void addPermissionToManagerPositive() {
            addManagerSetup();  //add a manager to the store
            //success: permission added to manager
            Assertions.assertTrue(storeUT.addPermissionToManager(ownerRealUser,managerUser,StorePermission.EDIT));
        }

        /**
         * not owner try to add permissions to a manager
         */
        @Test
        void notOwnerAddPermissionToManagerNegative() {
            addManagerSetup();  //add a manager to the store
            //fail: permission is not added to manager
            Assertions.assertFalse(storeUT.addPermissionToManager(fakeOwnerReal,managerUser,StorePermission.EDIT));
        }

        /**
         * owner remove permission from manager permissions list
         */
        @Test
        void removePermissionPositive(){
            addManagerSetup();
            //success : owner can remove permission from the manager's permissions list
            Assertions.assertTrue(storeUT.removePermissionFromManager(ownerRealUser,managerUser,StorePermission.VIEW));
        }

        /**
         * not an owner try to remove permission from manager permissions list
         */
        @Test
        void removePermissionNegative(){
            addManagerSetup();
            //fail: the user trying to remove is not an owner who appointed the manager
            Assertions.assertFalse(storeUT.removePermissionFromManager(fakeOwnerReal,managerUser,StorePermission.VIEW));
        }

        /**
         * owner remove one of managers appointed by him
         */
        @Test
        void removeManagerPositive() {
            //setup: insert manager into managers list of the store
            addManagerSetup();
            Assertions.assertEquals(1,storeUT.getManagers().size());
            //success: owner can remove manager he appointed
            Assertions.assertTrue(storeUT.removeManager(ownerRealUser, managerUser));
            Assertions.assertEquals(0,storeUT.getManagers().size());
        }

        /**
         * user who is not owner cannot remove one of managers appointed by someone else
         */
        @Test
        void removeManagerNegative() {
            //setup: insert manager into managers list of the store
            addManagerSetup();
            //fail: only the owner can remove manager he appointed
            Assertions.assertFalse(storeUT.removeManager(fakeOwnerReal, managerUser));
            Assertions.assertEquals(1,storeUT.getManagers().size());
        }

        /**
         * get existing product in the store
         */
        @Test
        void getExistProductPositive() {
            addNewProductSetUp();
            //success: get exist product
            Assertions.assertEquals(product,storeUT.getProduct(product.getProductSn()));
        }

        /**
         * get existing product in the store
         */
        @Test
        void getNotExistProductNegative() {
            //success: get exist product
            Throwable exception = Assertions.assertThrows(ProductDoesntExistException.class,()->storeUT.getProduct(33));
            Assertions.assertEquals("A product with id '" + 33 + "' is not exist in store with id: '"+ storeUT.getStoreId() + "'",exception.getMessage());
        }

        /**
         * user which is manager in the store requests for receipts
         */
        @Test
        void managerViewReceiptsPositive() {
            addManagerSetup();
            insertReceiptsSetup();
            List<Receipt> receipts = storeUT.managerViewReceipts(managerUser);  //get the receipts of store
            int i=0;
            for(Receipt receipt : receipts){
                //success : manager can view the receipts and their fields as well
                Assertions.assertEquals(i,receipt.getAmountToPay());
                i++;
            }
        }

        /**
         * user which is not manager in the store requests for receipts
         */
        @Test
        void notManagerViewReceiptsNegative() {
            Throwable exception = Assertions.assertThrows(NoManagerInStoreException.class,()->storeUT.managerViewReceipts(fakeOwnerReal));
            //fail: the inserted user is not manager so cant see the receipts in store
            Assertions.assertEquals("The user name '" + "DT" + "' is not manager in the store number: '"+ storeUT.getStoreId() + "'",exception.getMessage());
        }

        /**
         * user which is manager in the store requests for receipts
         */
        @Test
        void ownerViewReceiptsPositive() {
            addOwnerSetup();
            insertReceiptsSetup();
            List<Receipt> receipts = storeUT.ownerViewReceipts(ownerRealUser);  //get the receipts of store
            int i=0;
            for(Receipt receipt : receipts){
                //success : manager can view the receipts and their fields as well
                Assertions.assertEquals(i,receipt.getAmountToPay());
                i++;
            }
        }

        /**
         * user which is not owner in the store requests for receipts
         */
        @Test
        void notOwnerViewReceiptsNegative() {
            Throwable exception = Assertions.assertThrows(NoManagerInStoreException.class,()->storeUT.ownerViewReceipts(fakeOwnerReal));
            //fail: the inserted user is not owner so cant see the receipts in store
            Assertions.assertEquals("The user name '" + "DT" + "' is not manager in the store number: '"+ storeUT.getStoreId() + "'",exception.getMessage());
        }

        /**
         * functionality of watching the store information
         */
        @Test
        void showStoreInfo(){
            //success: shows the store info as string
            Assertions.assertEquals("The store Store under test has ID: " +storeUT.getStoreId()+ "",storeUT.showStoreInfo());
        }

        /**
         * functionality of watching the store products information
         */
        @Test
        void showProductsInStoreInfo(){
            //success: no products in store so show empty string
            Assertions.assertEquals("",storeUT.showProductsInStoreInfo());
        }

//////////////////setups///////////////////////////////////

        private void insertReceiptsSetup(){
            for(int i = 0; i < 10 ; i++){
                storeUT.getReceipts().add(Receipt.builder()
                        .amountToPay(i)
                        .productsBought(new HashSet<Product>())
                        .purchaseDate(new Date())
                        .storeId(0)
                        .userName("felix")
                        .build());
            }
        }

        private void addProductAsEditProductSetup() {
            product = new Product("Crashpad",ProductCategory.SPORTING_GOODS,2,987,12);
            storeUT.addNewProduct(ownerRealUser, product);

        }
        private void addNewProductSetUp(){
            storeUT.addNewProduct(ownerRealUser,product);
        }

        private void addManagerSetup(){
            storeUT.addManager(ownerRealUser,managerUser);  //adding alex as new manager in the store
        }

        private void addOwnerSetup() {
            storeUT.addOwner(ownerRealUser, newOwnerReal);
        }
    }
}
package com.wsep202.TradingSystem.domain.trading_system_management;


import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;



import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class MangerStoreTest {
    private MangerStore mangerStore;
    private UserSystem managerUser;


    @AfterEach
    void tearDown() {
    }

    @Nested
    public class MangerStoreTestUnit {
        @BeforeEach
        void setUp() {
            //creating mockup for user of manager
            managerUser = mock(UserSystem.class);
            when(managerUser.getUserName()).thenReturn("manager");
            mangerStore = new MangerStore(managerUser);
        }

        /**
         * check that when given right user object of the user the answer is true
         */
        @Test
        void isTheUserCheckByObjectPositive() {
            //success: the user wrapped by the manager object is the right user
            Assertions.assertTrue(mangerStore.isTheUser(managerUser));
        }

        /**
         * check handling with incorrect user as the required manager
         */
        @Test
        void isTheUserCheckByObjectNegative() {
            UserSystem inCorrectUser = mock(UserSystem.class);
            //fail: the received user is not the one inside the manager store
            Assertions.assertFalse(mangerStore.isTheUser(inCorrectUser));
        }

        /**
         * check that the correct username given the answer is true
         */
        @Test
        void IsTheUserCheckByUsernamePositive() {
            //success: the user which is the manager has the name of the right user
            Assertions.assertTrue(mangerStore.isTheUser("manager"));
        }

        /**
         * check handling with username that doesn't match the user inside the manager object
         */
        @Test
        void IsTheUserCheckByUsernameNegative() {
            //fail: the user which is the manager has different username
            Assertions.assertFalse(mangerStore.isTheUser("boni"));
        }

        /**
         * check adding new permission for a manager
         */
        @Test
        void addStorePermissionPositive() {
            int amountPerms = mangerStore.getStorePermissions().size();
            Assertions.assertTrue(mangerStore.addStorePermission(StorePermission.EDIT_PRODUCT));
            Assertions.assertTrue(mangerStore.getStorePermissions().size()>amountPerms);
        }

        /**
         * check adding new  an already exist permission for a manager
         */
        @Test
        void addStorePermissionNegative() {
            int amountPerms = mangerStore.getStorePermissions().size();
            Assertions.assertFalse(mangerStore.addStorePermission(StorePermission.VIEW));
            Assertions.assertFalse(mangerStore.getStorePermissions().size()>amountPerms);
        }

        /**
         * removing a permission from the manager store permissions
         */
        @Test
        void removeStorePermissionPositive() {
            int amountPerms = mangerStore.getStorePermissions().size();
            Assertions.assertTrue(mangerStore.removeStorePermission(StorePermission.VIEW));
            Assertions.assertTrue(mangerStore.getStorePermissions().size()<amountPerms);

        }

        /**
         * trying to remove permission the manager hasn't
         */
        @Test
        void removeStorePermissionNegative() {
            int amountPerms = mangerStore.getStorePermissions().size();
            Assertions.assertFalse(mangerStore.removeStorePermission(StorePermission.EDIT_PRODUCT));
            Assertions.assertFalse(mangerStore.getStorePermissions().size()<amountPerms);

        }
    }

    @Nested
    public class MangerStoreTestIntegration {
        @BeforeEach
        void setUp() {
            //creating mockup for user of manager
            managerUser = new UserSystem("manager","man","ager","password");
            mangerStore = new MangerStore(managerUser);
        }


        /**
         * check that when given right user object of the user the answer is true
         */
        @Test
        void isTheUserCheckByObjectPositive() {
            //success: the user wrapped by the manager object is the right user
            Assertions.assertTrue(mangerStore.isTheUser(managerUser));
        }

        /**
         * check handling with incorrect user as the required manager
         */
        @Test
        void isTheUserCheckByObjectNegative() {
            UserSystem inCorrectUser = new UserSystem("ban","man","ager","password");
            //fail: the received user is not the one inside the manager store
            Assertions.assertFalse(mangerStore.isTheUser(inCorrectUser));
        }

        /**
         * check that the correct username given the answer is true
         */
        @Test
        void IsTheUserCheckByUsernamePositive() {
            //success: the user which is the manager has the name of the right user
            Assertions.assertTrue(mangerStore.isTheUser("manager"));
        }

        /**
         * check handling with username that doesn't match the user inside the manager object
         */
        @Test
        void IsTheUserCheckByUsernameNegative() {
            //fail: the user which is the manager has different username
            Assertions.assertFalse(mangerStore.isTheUser("boni"));
        }

        /**
         * check adding new permission for a manager
         */
        @Test
        void addStorePermissionPositive() {
            int amountPerms = mangerStore.getStorePermissions().size();
            Assertions.assertTrue(mangerStore.addStorePermission(StorePermission.EDIT_PRODUCT));
            Assertions.assertTrue(mangerStore.getStorePermissions().size()>amountPerms);
        }

        /**
         * check adding new  an already exist permission for a manager
         */
        @Test
        void addStorePermissionNegative() {
            int amountPerms = mangerStore.getStorePermissions().size();
            Assertions.assertFalse(mangerStore.addStorePermission(StorePermission.VIEW));
            Assertions.assertFalse(mangerStore.getStorePermissions().size()>amountPerms);
        }

        /**
         * removing a permission from the manager store permissions
         */
        @Test
        void removeStorePermissionPositive() {
            int amountPerms = mangerStore.getStorePermissions().size();
            Assertions.assertTrue(mangerStore.removeStorePermission(StorePermission.VIEW));
            Assertions.assertTrue(mangerStore.getStorePermissions().size()<amountPerms);

        }

        /**
         * trying to remove permission the manager hasn't
         */
        @Test
        void removeStorePermissionNegative() {
            int amountPerms = mangerStore.getStorePermissions().size();
            Assertions.assertFalse(mangerStore.removeStorePermission(StorePermission.EDIT_PRODUCT));
            Assertions.assertFalse(mangerStore.getStorePermissions().size()<amountPerms);

        }
    }
}

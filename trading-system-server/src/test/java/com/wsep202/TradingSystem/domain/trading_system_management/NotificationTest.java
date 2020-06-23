package com.wsep202.TradingSystem.domain.trading_system_management;

import com.github.rozidan.springboot.modelmapper.WithModelMapper;
import com.wsep202.TradingSystem.config.ObjectMapperConfig;
import com.wsep202.TradingSystem.config.TradingSystemConfiguration;
import com.wsep202.TradingSystem.config.httpSecurity.HttpSecurityConfig;
import com.wsep202.TradingSystem.domain.factory.FactoryObjects;
import com.wsep202.TradingSystem.domain.trading_system_management.notification.Notification;
import com.wsep202.TradingSystem.dto.UserSystemDto;
import com.wsep202.TradingSystem.service.ServiceFacade;
import com.wsep202.TradingSystem.service.user_service.BuyerRegisteredService;
import com.wsep202.TradingSystem.service.user_service.GuestService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TradingSystemConfiguration.class, HttpSecurityConfig.class, ObjectMapperConfig.class})
@SpringBootTest(args = {"admin","admin"})
@WithModelMapper
public class NotificationTest {
    private UserSystem owner1;
    private UserSystem owner2;
    private UserSystem newOwner;
    private UserSystem admin;
    private Store store;
    @Autowired
    private TradingSystemFacade tradingSystemFacade;
    @Autowired
    private TradingSystem tradingSystem;

    @BeforeEach
    void setUp() {
        admin = new UserSystem("admin", "admin", "admin", "admin");
        owner1 = new UserSystem("owner1", "owner1", "owner1", "password");
        owner2 = new UserSystem("owner2", "owner2", "owner2", "password");
        newOwner = new UserSystem("newOwner", "newOwner", "newOwner", "password");

        tradingSystemFacade.registerUser(owner1.getUserName(), owner1.getPassword(), owner1.getFirstName(), owner1.getLastName(), null);
        tradingSystemFacade.registerUser(owner2.getUserName(), owner2.getPassword(), owner2.getFirstName(), owner2.getLastName(), null);
        tradingSystemFacade.registerUser(newOwner.getUserName(), newOwner.getPassword(), newOwner.getFirstName(), newOwner.getLastName(), null);
    }

    @Test
    void noNotifications() {
        UUID uuid = tradingSystemFacade.login(admin.getUserName(), admin.getPassword()).getKey();
        UserSystem actualAdmin = tradingSystem.getUser(admin.getUserName(), uuid);
        Set<UserSystem> usersInSystem = tradingSystem.getUsers(actualAdmin);

        for(UserSystem userSystem: usersInSystem){
            userSystem.setPrincipal("principal");
            Assertions.assertEquals(userSystem.getNotifications(), new ArrayList<>());
        }

        tradingSystemFacade.logout(admin.getUserName(), uuid);
    }

    @Test
    void addedNewOwner() {
        ownerOpensStoreAndAppointsOwners();
        adminChecksUserNotification();
        adminChecksUserNotificationAfterApproval();
    }

    private void ownerOpensStoreAndAppointsOwners(){
        UUID uuid = tradingSystemFacade.login(owner1.getUserName(), owner1.getPassword()).getKey();
        store = new Store(owner1, "store");
        store.setDescription("description");
        tradingSystemFacade.openStore(owner1.getUserName(), store.getStoreName(), store.getDescription(), uuid);
        tradingSystemFacade.logout(owner1.getUserName(), uuid);

        uuid = tradingSystemFacade.login(owner1.getUserName(), owner1.getPassword()).getKey();
        tradingSystemFacade.addOwner(owner1.getUserName(), store.getStoreId(), owner2.getUserName(), uuid);
        tradingSystemFacade.addOwner(owner1.getUserName(), store.getStoreId(), newOwner.getUserName(), uuid);
        tradingSystemFacade.logout(owner1.getUserName(), uuid);
    }

    private void adminChecksUserNotification(){
        UUID uuid = tradingSystemFacade.login(admin.getUserName(), admin.getPassword()).getKey();
        UserSystem actualAdmin = tradingSystem.getUser(admin.getUserName(), uuid);
        Set<UserSystem> usersInSystem = tradingSystem.getUsers(actualAdmin);

        for(UserSystem userSystem: usersInSystem){
            userSystem.setPrincipal("principal");
            if (userSystem.getUserName().equals(owner2.getUserName())){
                List<Notification> notifications = userSystem.getNotifications();
                Assertions.assertNotNull(notifications);

                Notification addedAsNewOwner = notifications.get(0);
                String content = String.format("You are now owner of store %s on name %s", store.getStoreId(), store.getStoreName());
                Assertions.assertEquals(content, addedAsNewOwner.getContent());

                Notification approveOwnerNotification = notifications.get(1);
                content = String.format("You have a new Owner to approve in storeId %s", store.getStoreId());
                Assertions.assertEquals(content, approveOwnerNotification.getContent());
            }
            else{
                Assertions.assertEquals(userSystem.getNotifications(), new ArrayList<>());
            }
        }
        tradingSystemFacade.logout(admin.getUserName(), uuid);
    }

    private void adminChecksUserNotificationAfterApproval(){
        UUID uuid = tradingSystemFacade.login(owner2.getUserName(), owner2.getPassword()).getKey();
        tradingSystemFacade.approveOwner(owner2.getUserName(), store.getStoreId(), newOwner.getUserName(), true, uuid);
        tradingSystemFacade.logout(owner2.getUserName(), uuid);

        uuid = tradingSystemFacade.login(admin.getUserName(), admin.getPassword()).getKey();
        UserSystem actualAdmin = tradingSystem.getUser(admin.getUserName(), uuid);
        Set<UserSystem> usersInSystem = tradingSystem.getUsers(actualAdmin);

        for(UserSystem userSystem: usersInSystem){
            userSystem.setPrincipal("principal");
            if (userSystem.getUserName().equals(newOwner.getUserName())){
                List<Notification> notifications = userSystem.getNotifications();
                Assertions.assertNotNull(notifications);
                Notification addedAsNewOwner = notifications.get(0);
                String content = String.format("You are now owner of store %s on name %s", store.getStoreId(), store.getStoreName());
                Assertions.assertEquals(content, addedAsNewOwner.getContent());
            }
            else{
                Assertions.assertEquals(userSystem.getNotifications(), new ArrayList<>());
            }
        }
        tradingSystemFacade.logout(admin.getUserName(), uuid);
    }
}
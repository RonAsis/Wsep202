package com.wsep202.TradingSystem.repositoriesTests;

import com.wsep202.TradingSystem.TradingSystemApplication;
import com.wsep202.TradingSystem.domain.trading_system_management.TradingSystemDataBaseDao;
import com.wsep202.TradingSystem.domain.trading_system_management.UserSystem;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TradingSystemApplication.class, TradingSystemDataBaseDao.class})
//@SpringBootTest(args = {"admin","admin"})
/*@MockBean(classes = {
        testsss.class
})*/
@DataJpaTest
public class UserRepositoryTests {
    @Autowired
    private TradingSystemDataBaseDao tradingSystemDataBaseDao;
    MultipartFile image = null;

    @BeforeEach
    void setUp() {
    }

    @Test
    void registerAdminPositive(){
        UserSystem  userSystemAdmin = new UserSystem("usernameAdmin","name",false,"lname","password", true);
        tradingSystemDataBaseDao.registerAdmin(userSystemAdmin);
        assertTrue(tradingSystemDataBaseDao.isRegistered(userSystemAdmin));
    }

    @Test
    void isRegisteredPositive() {
        UserSystem  userSystem = new UserSystem("usernamePos","name",false,"lname","password", false);
        tradingSystemDataBaseDao.addUserSystem(userSystem,image);
        assertTrue(tradingSystemDataBaseDao.isRegistered(userSystem));
    }

    @Test
    void isRegisteredNegative() {
        UserSystem  userSystem = new UserSystem("usernamePos","name",false,"lname","password", false);
        assertFalse(tradingSystemDataBaseDao.isRegistered(userSystem));
    }

    @Test
    void addUserSystemPositive(){
        UserSystem  userSystem = new UserSystem("usernamePos","name",false,"lname","password", false);
        Path path = Paths.get("/path/to/the/file.txt");
        String name = "file.txt";
        String originalFileName = "file.txt";
        String contentType = "text/plain";
        byte[] content = null;
        try {
            content = Files.readAllBytes(path);
        } catch (final IOException e) {
        }
        MultipartFile result = new MockMultipartFile(name, originalFileName, contentType, content);
        tradingSystemDataBaseDao.addUserSystem(userSystem,result);
        assertTrue(tradingSystemDataBaseDao.isRegistered(userSystem));
    }

    @Test
    void getUserSystemPositive(){
        UserSystem  userSystem = new UserSystem("usernamePos","name",false,"lname","password", false);
        tradingSystemDataBaseDao.addUserSystem(userSystem,image);
        assertTrue(tradingSystemDataBaseDao.getUserSystem(userSystem.getUserName()).isPresent());
        Assertions.assertEquals(userSystem.getUserName(),tradingSystemDataBaseDao.getUserSystem(userSystem.getUserName()).get().getUserName());
        Assertions.assertEquals(userSystem.getFirstName(),tradingSystemDataBaseDao.getUserSystem(userSystem.getUserName()).get().getFirstName());
        Assertions.assertEquals(userSystem.isLogin(),tradingSystemDataBaseDao.getUserSystem(userSystem.getUserName()).get().isLogin());
        Assertions.assertEquals(userSystem.getLastName(),tradingSystemDataBaseDao.getUserSystem(userSystem.getUserName()).get().getLastName());
        Assertions.assertEquals(userSystem.getPassword(),tradingSystemDataBaseDao.getUserSystem(userSystem.getUserName()).get().getPassword());
        Assertions.assertEquals(userSystem.getImageUrl(),tradingSystemDataBaseDao.getUserSystem(userSystem.getUserName()).get().getImageUrl());
    }

    @Test
    void getUserSystemNegative(){
        UserSystem  userSystem = new UserSystem("usernamePos","name",false,"lname","password", false);
        assertFalse(tradingSystemDataBaseDao.getUserSystem(userSystem.getUserName()).isPresent());
    }

    @Test
    void isAdminPositive(){
        UserSystem  userSystem = new UserSystem("usernamePos","name",false,"lname","password", true);
        tradingSystemDataBaseDao.addUserSystem(userSystem,image);
        assertTrue(tradingSystemDataBaseDao.isAdmin(userSystem.getUserName()));
    }

    @Test
    void isAdminNegative(){
        UserSystem  userSystem = new UserSystem("usernamePos","name",false,"lname","password", false);
        tradingSystemDataBaseDao.addUserSystem(userSystem,image);
        assertFalse(tradingSystemDataBaseDao.isAdmin(userSystem.getUserName()));
    }

    @Test
    void getAdministratorUserPositive(){
        UserSystem  userSystem = new UserSystem("usernamePos","name",false,"lname","password", true);
        tradingSystemDataBaseDao.addUserSystem(userSystem,image);
        assertTrue(tradingSystemDataBaseDao.getAdministratorUser(userSystem.getUserName()).isPresent());
        Assertions.assertEquals(userSystem.getUserName(),tradingSystemDataBaseDao.getAdministratorUser(userSystem.getUserName()).get().getUserName());
        Assertions.assertEquals(userSystem.getFirstName(),tradingSystemDataBaseDao.getAdministratorUser(userSystem.getUserName()).get().getFirstName());
        Assertions.assertEquals(userSystem.isLogin(),tradingSystemDataBaseDao.getAdministratorUser(userSystem.getUserName()).get().isLogin());
        Assertions.assertEquals(userSystem.getLastName(),tradingSystemDataBaseDao.getAdministratorUser(userSystem.getUserName()).get().getLastName());
        Assertions.assertEquals(userSystem.getPassword(),tradingSystemDataBaseDao.getAdministratorUser(userSystem.getUserName()).get().getPassword());
        Assertions.assertEquals(userSystem.isAdmin(),tradingSystemDataBaseDao.getAdministratorUser(userSystem.getUserName()).get().isAdmin());
        Assertions.assertEquals(userSystem.getImageUrl(),tradingSystemDataBaseDao.getAdministratorUser(userSystem.getUserName()).get().getImageUrl());
    }

    @Test
    void getAdministratorUserNegative(){
        UserSystem  userSystem = new UserSystem("usernamePos","name",false,"lname","password", false);
        tradingSystemDataBaseDao.addUserSystem(userSystem,image);
        assertFalse(tradingSystemDataBaseDao.getAdministratorUser(userSystem.getUserName()).isPresent());
    }

    @Test
    void getUsersPositive(){
        UserSystem  userSystem1 = new UserSystem("usernamePos1","name",false,"lname","password", false);
        UserSystem  userSystem2 = new UserSystem("usernamePos2","name",false,"lname","password", false);
        UserSystem  userSystem3 = new UserSystem("usernamePos3","name",false,"lname","password", false);
        UserSystem  userSystem4 = new UserSystem("usernamePos4","name",false,"lname","password", false);
        tradingSystemDataBaseDao.addUserSystem(userSystem1,image);
        tradingSystemDataBaseDao.addUserSystem(userSystem2,image);
        tradingSystemDataBaseDao.addUserSystem(userSystem3,image);
        tradingSystemDataBaseDao.addUserSystem(userSystem4,image);
        Set<UserSystem> res = tradingSystemDataBaseDao.getUsers();
        assertEquals(4,res.size());
    }
}

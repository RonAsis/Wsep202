/**
 * this class test the functionality of the initFile by verifying the
 * trading system states is as configured in resources/test.yaml file
 * each test check the functionality of change the system state by some add definition functionality
 * and then read the log file to check that the operation made
 * here we assume that if we logged some operation then it works! because operation tested till that step.
 */
package com.wsep202.TradingSystem;



import com.wsep202.TradingSystem.domain.trading_system_management.TradingSystem;
import com.wsep202.TradingSystem.domain.trading_system_management.TradingSystemFacade;
import com.wsep202.TradingSystem.dynamic_start_up.ActivationPaths;
import com.wsep202.TradingSystem.dynamic_start_up.Context;
import com.wsep202.TradingSystem.dynamic_start_up.InitialSystem;
import com.wsep202.TradingSystem.utils.FormatFile;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


@TestPropertySource(locations= "classpath:init_files/test.yaml")
@ExtendWith(SpringExtension.class)
public class initFileTest {
    TradingSystem tradingSystemMock;
    TradingSystemFacade facadeMock;

    boolean isActivated; //tells if the required activityDefinition is activated

    @AfterEach
    void tearDown() {
        isActivated = false;
    }

    @BeforeEach
    void setUp() {
            tradingSystemMock = mock(TradingSystem.class);
            facadeMock = mock(TradingSystemFacade.class);

    }

    /**
     * the following check activation of the register facade method by the initial file
     */
    @Test
    void checkRegister(){
        doAnswer(new Answer<Void>() {   //activate flag is true when the proper method is called in facade
            @Override
            public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
                isActivated = true;
                return null;
            }
        }).when(facadeMock).registerUser(anyString(),anyString(),anyString(),anyString(),anyObject());
        verifyDefinitionTypeActivated("register",new Context());
    }

    /**
     * the following check activation of the login facade method by the initial file
     */
    @Test
    void checkLogin(){
        doAnswer(new Answer<Void>() {   //activate flag is true when the proper method is called in facade
            @Override
            public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
                isActivated = true;
                return null;
            }
        }).when(facadeMock).login(anyString(),anyString());
        verifyDefinitionTypeActivated("login",new Context());
    }

    /**
     * the following check activation of the logout facade method by the initial file
     */
    @Test
    void checkLogout(){
        doAnswer(new Answer<Void>() {   //activate flag is true when the proper method is called in facade
            @Override
            public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
                isActivated = true;
                return null;
            }
        }).when(facadeMock).logout(anyString(),anyObject());
        verifyDefinitionTypeActivated("logout",new Context());
    }

    /**
     * the following check activation of the open store facade method by the initial file
     */
    @Test
    void checkOpenStore(){
        doAnswer(new Answer<Void>() {   //activate flag is true when the proper method is called in facade
            @Override
            public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
                isActivated = true;
                return null;
            }
        }).when(facadeMock).openStore(anyString(),anyString(),anyString(),anyObject());
        verifyDefinitionTypeActivated("openStore", new Context());
    }

    /**
     * the following check activation of the addOwner facade method by the initial file
     */
    @Test
    void checkAddOwner(){
        doAnswer(new Answer<Void>() {   //activate flag is true when the proper method is called in facade
            @Override
            public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
                isActivated = true;
                return null;
            }
        }).when(facadeMock).addOwner(anyString(),anyInt(),anyString(),anyObject());
        //set mock context for the add owner
        Context context = mock(Context.class);
        when(context.getUuid(anyString())).thenReturn(new UUID(1,1));
        when(context.getRealStoreId(anyInt())).thenReturn(1);

        verifyDefinitionTypeActivated("addOwner",context);
    }

    /**
     * the following check activation of the approveOwner facade method by the initial file
     */
    @Test
    void checkApproveOwner(){
        doAnswer(new Answer<Void>() {   //activate flag is true when the proper method is called in facade
            @Override
            public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
                isActivated = true;
                return null;
            }
        }).when(facadeMock).approveOwner(anyString(),anyInt(),anyString(),anyBoolean(),anyObject());
        //set mock context for the add owner
        Context context = mock(Context.class);
        when(context.getUuid(anyString())).thenReturn(new UUID(1,1));
        when(context.getRealStoreId(anyInt())).thenReturn(1);

        verifyDefinitionTypeActivated("approveOwner",context);
    }

    /**
     * the following check activation of the add discount facade method by the initial file
     */
    @Test
    void checkAddDiscount(){
        doAnswer(new Answer<Void>() {   //activate flag is true when the proper method is called in facade
            @Override
            public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
                isActivated = true;
                return null;
            }
        }).when(facadeMock).addEditDiscount(anyString(),anyInt(),anyObject(),anyObject());
        //set mock context for the add owner
        Context context = mock(Context.class);
        when(context.getUuid(anyString())).thenReturn(new UUID(1,1));
        when(context.getRealStoreId(anyInt())).thenReturn(1);

        verifyDefinitionTypeActivated("addDiscount",context);
    }

    /**
     * the following check activation of the add manager facade method by the initial file
     */
    @Test
    void checkAddManager(){
        doAnswer(new Answer<Void>() {   //activate flag is true when the proper method is called in facade
            @Override
            public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
                isActivated = true;
                return null;
            }
        }).when(facadeMock).addManager(anyString(),anyInt(),anyString(),anyObject());
        //set mock context for the add owner
        Context context = mock(Context.class);
        when(context.getUuid(anyString())).thenReturn(new UUID(1,1));
        when(context.getRealStoreId(anyInt())).thenReturn(1);

        verifyDefinitionTypeActivated("addManager",context);
    }


    /**
     * the following check activation of the add permission facade method by the initial file
     */
    @Test
    void checkAddPermission(){
        doAnswer(new Answer<Void>() {   //activate flag is true when the proper method is called in facade
            @Override
            public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
                isActivated = true;
                return null;
            }
        }).when(facadeMock).addPermission(anyString(),anyInt(),anyString(),anyString(),anyObject());
        //set mock context for the add owner
        Context context = mock(Context.class);
        when(context.getUuid(anyString())).thenReturn(new UUID(1,1));
        when(context.getRealStoreId(anyInt())).thenReturn(1);

        verifyDefinitionTypeActivated("addPermission",context);
    }

    /**
     * the following check activation of the add product facade method by the initial file
     */
    @Test
    void checkAddProduct(){
        doAnswer(new Answer<Void>() {   //activate flag is true when the proper method is called in facade
            @Override
            public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
                isActivated = true;
                return null;
            }
        }).when(facadeMock).addProduct(anyString(),anyInt(),anyString(),anyString(),anyInt(),anyDouble(),anyObject());
        //set mock context for the add owner
        Context context = mock(Context.class);
        when(context.getUuid(anyString())).thenReturn(new UUID(1,1));
        when(context.getRealStoreId(anyInt())).thenReturn(1);

        verifyDefinitionTypeActivated("addProduct",context);
    }


    /**
     * the following check activation of the purchase shopping cart facade method by the initial file
     */
    @Test
    void checkPurchaseShoppingCart(){
        doAnswer(new Answer<Void>() {   //activate flag is true when the proper method is called in facade
            @Override
            public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
                isActivated = true;
                return null;
            }
        }).when(facadeMock).purchaseShoppingCart(anyString(),anyObject(),anyObject(),anyObject());
        //set mock context for the add owner
        Context context = mock(Context.class);
        when(context.getUuid(anyString())).thenReturn(new UUID(1,1));
        when(context.getRealStoreId(anyInt())).thenReturn(1);

        verifyDefinitionTypeActivated("purchaseBuyer",context);
    }



    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void verifyDefinitionTypeActivated(String type, Context context) {

        //get the path for the init file directory
        try (Stream<Path> walk = Files.walk(ResourceUtils.getFile("src/test/java/resources/init_files/").toPath())) {

            //collect all the yaml files
            List<Path> pathList = walk.collect(Collectors.toList());

            pathList = FormatFile.filterYamlFiles(pathList);

            for (Path path : pathList) {
                    doActions(path, context,type);    //activate the proper method
            }

        } catch (IOException e) {
            //if the directory don't exist
            System.out.println(e.getMessage());
        }
        Assertions.assertTrue(isActivated);
    }

    private void doActions(Path path, Context context,String type) {
        InputStream inputStream = getInputStream(path);
        Optional<InitialSystem> activityElement = getActivityElement(inputStream, path);
        activityElement.ifPresent(initialSystem -> {
            initialSystem.getActivities().forEach(activityDefinition -> {
                if(activityDefinition.getType().equals(type)) {
                    activityDefinition.apply(context, facadeMock);
                }
            }
            );
        });
    }


    /**
     * get input stream
     *
     * @param pathToWorkflowType - pathToWorkflowType
     * @return InputStream
     */
    private InputStream getInputStream(Path pathToWorkflowType) {
        InputStream inputstream = null;
        try {
            if (Objects.nonNull(pathToWorkflowType)) {
                inputstream = new FileInputStream(pathToWorkflowType.toFile());
            }
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }

        //get the input stream
        return inputstream;
    }

    private Optional<InitialSystem> getActivityElement(InputStream inputStream, Path path) {
        InitialSystem initialSystem = null;
        try {
            // if the input stream is succeed
            if (Objects.nonNull(inputStream)) {
                initialSystem = FormatFile.mapper.readValue(inputStream, InitialSystem.class);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return Optional.ofNullable(initialSystem);
    }

}

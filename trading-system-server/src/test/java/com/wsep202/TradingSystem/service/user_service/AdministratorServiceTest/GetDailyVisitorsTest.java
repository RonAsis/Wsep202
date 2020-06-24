package com.wsep202.TradingSystem.service.user_service.AdministratorServiceTest;

import com.github.rozidan.springboot.modelmapper.WithModelMapper;
import com.wsep202.TradingSystem.config.ObjectMapperConfig;
import com.wsep202.TradingSystem.config.TradingSystemConfiguration;
import com.wsep202.TradingSystem.config.httpSecurity.HttpSecurityConfig;
import com.wsep202.TradingSystem.dto.DailyVisitorDto;
import com.wsep202.TradingSystem.dto.RequestGetDailyVisitorsDto;
import com.wsep202.TradingSystem.service.user_service.*;
import javafx.util.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TradingSystemConfiguration.class, HttpSecurityConfig.class, ObjectMapperConfig.class, GuestService.class, BuyerRegisteredService.class, SellerOwnerService.class, AdministratorService.class})
@SpringBootTest(args = {"admin","admin"})
@WithModelMapper

// *********** UC 6.5 - watching daily information ***********
public class GetDailyVisitorsTest {
    @Autowired
    GuestService guestService;
    @Autowired
    BuyerRegisteredService buyerRegisteredService;
    @Autowired
    SellerOwnerService sellerOwnerService;
    @Autowired
    AdministratorService administratorService;
    ServiceTestsHelper helper;

    String adminUsername = "admin";
    String adminPassword = "admin";
    UUID uuid;

    @BeforeEach
    void setUp() {
        if (this.helper == null || this.helper.getGuestService() == null ) {
            this.helper = new ServiceTestsHelper(this.guestService, this.buyerRegisteredService, this.sellerOwnerService);
        }
        Pair<UUID, Boolean> returnedValue = this.helper.loginUser(this.adminUsername,
                this.adminPassword);
        if (returnedValue != null){
            this.uuid = returnedValue.getKey();
        }
    }

    @AfterEach
    void tearDown(){
        this.helper.logoutUser(this.adminUsername, this.uuid);
    }

    @Test
    void getDailyVisitorsValidInfo(){
        RequestGetDailyVisitorsDto requestGetDailyVisitorsDto = new RequestGetDailyVisitorsDto();
        Date date = new Date();
        date.setTime(0);
        requestGetDailyVisitorsDto.setStart(date);
        requestGetDailyVisitorsDto.setEnd(new Date());
        requestGetDailyVisitorsDto.setFirstIndex(0);
        requestGetDailyVisitorsDto.setLastIndex(1);
        List<DailyVisitorDto> dailyVisitorDtos = this.administratorService.getDailyVisitors(adminUsername, requestGetDailyVisitorsDto, this.uuid);
        Assertions.assertEquals(1, dailyVisitorDtos.size());
        Assertions.assertTrue(dailyVisitorDtos.get(0).getAdmins() > 0);
    }

    @Test
    void getDailyVisitorsStartAndEndAreNow(){
        RequestGetDailyVisitorsDto requestGetDailyVisitorsDto = new RequestGetDailyVisitorsDto();
        Date date = new Date();
        requestGetDailyVisitorsDto.setStart(date);
        requestGetDailyVisitorsDto.setEnd(date);
        requestGetDailyVisitorsDto.setFirstIndex(0);
        requestGetDailyVisitorsDto.setLastIndex(0);
        List<DailyVisitorDto> dailyVisitorDtos = this.administratorService.getDailyVisitors(adminUsername, requestGetDailyVisitorsDto, this.uuid);
        Assertions.assertEquals(0, dailyVisitorDtos.size());
    }

    @Test
    void getDailyVisitorsFirstIndexBiggerThanLastIndex(){
        RequestGetDailyVisitorsDto requestGetDailyVisitorsDto = new RequestGetDailyVisitorsDto();
        requestGetDailyVisitorsDto.setStart(new Date());
        requestGetDailyVisitorsDto.setEnd(new Date());
        requestGetDailyVisitorsDto.setFirstIndex(100);
        requestGetDailyVisitorsDto.setLastIndex(10);
        List<DailyVisitorDto> dailyVisitorDtos = this.administratorService.getDailyVisitors(adminUsername, requestGetDailyVisitorsDto, this.uuid);
        Assertions.assertEquals(0, dailyVisitorDtos.size());
    }

    @Test
    void getDailyVisitorsFirstIndexEqLastIndex(){
        RequestGetDailyVisitorsDto requestGetDailyVisitorsDto = new RequestGetDailyVisitorsDto();
        requestGetDailyVisitorsDto.setStart(new Date());
        requestGetDailyVisitorsDto.setEnd(new Date());
        requestGetDailyVisitorsDto.setFirstIndex(0);
        requestGetDailyVisitorsDto.setLastIndex(0);
        List<DailyVisitorDto> dailyVisitorDtos = this.administratorService.getDailyVisitors(adminUsername, requestGetDailyVisitorsDto, this.uuid);
        Assertions.assertEquals(0, dailyVisitorDtos.size());
    }
}

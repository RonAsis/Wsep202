package com.wsep202.TradingSystem.service;

import com.wsep202.TradingSystem.dto.NotificationDto;
import com.wsep202.TradingSystem.service.user_service.NotificationService;
import com.wsep202.TradingSystem.service.user_service.NotificationServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class ServiceFacade {

    private final NotificationService notificationService;

    public void sendNotification(List<NotificationDto> notificationDtos){
        notificationService.sendNotification(notificationDtos);
    }
}

package com.wsep202.TradingSystem.service;

import com.wsep202.TradingSystem.dto.NotificationDto;
import com.wsep202.TradingSystem.service.user_service.NotificationService;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class ServiceFacade {

    private final NotificationService notificationService;

    public void sendNotification(List<NotificationDto> notificationDtos){
        notificationService.sendNotification(notificationDtos);
    }
}

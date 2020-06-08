package com.wsep202.TradingSystem.service.user_service;

import com.wsep202.TradingSystem.domain.trading_system_management.TradingSystemFacade;
import com.wsep202.TradingSystem.dto.NotificationDto;
import com.wsep202.TradingSystem.web.controllers.api.PublicApiPaths;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Data
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final TradingSystemFacade tradingSystemFacade;

    @Override
    public void sendNotification(List<NotificationDto> notificationDtos) {
        notificationDtos.forEach(notificationDto ->
                simpMessagingTemplate.convertAndSendToUser(notificationDto.getPrincipal(), "/user" + PublicApiPaths.NOTIFICATION_PATH,
                        notificationDto.getContent()));
    }

    @Override
    public void addUser(String username, UUID uuid, String principal) {
        tradingSystemFacade.connectNotificationSystem(username, uuid, principal);
    }
}

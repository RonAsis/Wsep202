package com.wsep202.TradingSystem.service.user_service;

import com.wsep202.TradingSystem.domain.trading_system_management.TradingSystemFacade;
import com.wsep202.TradingSystem.domain.trading_system_management.statistics.UpdateDailyVisitor;
import com.wsep202.TradingSystem.dto.NotificationDto;
import com.wsep202.TradingSystem.web.controllers.api.PublicApiPaths;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.wsep202.TradingSystem.web.controllers.api.PublicApiPaths.CLIENT_DESTINATIONS_PREFIXED;

@Data
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final TradingSystemFacade tradingSystemFacade;

    @Override
    public void sendNotification(List<NotificationDto> notificationDtos) {
        notificationDtos.forEach(notificationDto -> {
            simpMessagingTemplate.convertAndSendToUser(notificationDto.getPrincipal(), PublicApiPaths.CLIENT_DESTINATIONS_PREFIXED + PublicApiPaths.NOTIFICATION_PATH,
                    notificationDto);
            log.info(String.format("Send Notification to %s with content %s", notificationDto.getPrincipal(), notificationDto.getContent()));
        });
    }

    @Override
    public void addUser(String username, UUID uuid, String principal) {
        tradingSystemFacade.connectNotificationSystem(username, uuid, principal);
    }

    @Override
    public boolean sendDailyVisitor(UpdateDailyVisitor updateDailyVisitor) {
        simpMessagingTemplate.convertAndSendToUser(updateDailyVisitor.getPrincipal(), PublicApiPaths.CLIENT_DESTINATIONS_PREFIXED + PublicApiPaths.DAILY_VISITOR_PATH,
                updateDailyVisitor);
        log.info(String.format("Send updateDailyVisitor to %s", updateDailyVisitor.getPrincipal()));
        return true;
    }
}

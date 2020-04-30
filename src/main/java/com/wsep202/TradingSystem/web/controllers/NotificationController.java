package com.wsep202.TradingSystem.web.controllers;

import com.wsep202.TradingSystem.dto.NotificationDto;
import com.wsep202.TradingSystem.service.user_service.NotificationService;
import com.wsep202.TradingSystem.web.controllers.api.PublicApiPaths;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.UUID;

@Slf4j
@Controller
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @MessageMapping("/connect-notification-system")
    @SendToUser(PublicApiPaths.NOTIFICATION_PATH)
    public NotificationDto connectNotificationSystem(String username, UUID uuid, Principal principal){
        log.info(String.format("The username : %s with principal name: %s,connect to notification system", username, principal.getName()));
        notificationService.addUser(username, uuid, principal.getName());
        return NotificationDto.builder()
        .content("You are connecting to the notification system")
        .username(username)
        .build();
    }
}

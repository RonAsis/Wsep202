package com.wsep202.TradingSystem.service.user_service;

import com.wsep202.TradingSystem.dto.NotificationDto;
import com.wsep202.TradingSystem.web.controllers.api.PublicApiPaths;

import java.util.List;
import java.util.UUID;

public interface NotificationService {

     void sendNotification(List<NotificationDto> notificationDtos);

    void addUser(String username, UUID uuid, String principal);
}

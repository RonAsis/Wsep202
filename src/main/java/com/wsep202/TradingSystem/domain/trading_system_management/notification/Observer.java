package com.wsep202.TradingSystem.domain.trading_system_management.notification;

import java.util.List;

public interface Observer {

    List<Notification> getNotifications();

    void newNotification(Notification notification);

    void connectNotificationSystem(Subject subject, String principal);
}

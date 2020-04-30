package com.wsep202.TradingSystem.domain.trading_system_management.notification;

import com.wsep202.TradingSystem.domain.exception.RegisterObserverException;
import com.wsep202.TradingSystem.domain.trading_system_management.TradingSystemFacade;
import lombok.RequiredArgsConstructor;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.CollectionUtils;

import java.util.*;

@RequiredArgsConstructor
@Slf4j
public class Publisher implements Subject {

    private final List<Observer> observers = new LinkedList<>();
    private final Object publisherSync = new Object();// for sync
    private Set<Observer> observersWithNotification = new HashSet<>();

    private final TradingSystemFacade tradingSystemFacade;

   @Override
    @Synchronized("publisherSync")
    public void register(Observer obj) {
        if (Objects.isNull(obj)) {
            throw new RegisterObserverException("Null Observer");
        }
        if (!observers.contains(obj)) {
            observers.add(obj);
        }
    }

    @Override
    @Synchronized("publisherSync")
    public void unregister(Observer obj) {
        observers.remove(obj);
    }

    @Override
    public void update(Observer obj) {
        observersWithNotification.add(obj);
    }

    @Override
    @Synchronized("publisherSync")
    @Scheduled(fixedRateString = "6000", initialDelayString = "0")
    public void notifyObservers() {
       log.info("notifyObservers");
        List<Notification> notifications = new LinkedList<>();
        observersWithNotification.forEach(observer -> {
            List<Notification> notificationsObserver = observer.getNotifications();
            if(CollectionUtils.isEmpty(notificationsObserver)) {
                notifications.addAll(notificationsObserver);
            }
        });
        tradingSystemFacade.sendNotification(notifications);
        observersWithNotification = new HashSet<>();
    }

    @Override
    @Synchronized("publisherSync")
    public void Broadcast(Notification notification) {
        observers.forEach(observer -> observer.newNotification(notification));
    }

}

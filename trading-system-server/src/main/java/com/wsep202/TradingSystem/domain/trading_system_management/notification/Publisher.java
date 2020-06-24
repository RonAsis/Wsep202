package com.wsep202.TradingSystem.domain.trading_system_management.notification;

import com.wsep202.TradingSystem.domain.exception.RegisterObserverException;
import com.wsep202.TradingSystem.domain.trading_system_management.TradingSystemDao;
import com.wsep202.TradingSystem.domain.trading_system_management.TradingSystemFacade;
import com.wsep202.TradingSystem.domain.trading_system_management.UserSystem;
import com.wsep202.TradingSystem.domain.trading_system_management.statistics.UpdateDailyVisitor;
import lombok.RequiredArgsConstructor;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.mariadb.jdbc.internal.util.dao.QueryException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.util.CollectionUtils;

import java.net.ConnectException;
import java.util.*;

@RequiredArgsConstructor
@Slf4j
public class Publisher implements Subject {

    private final List<Observer> observers = new LinkedList<>();
    private final Object publisherSync = new Object();// for sync
    private Set<Observer> observersWithNotification = new HashSet<>();
    private final TradingSystemDao tradingSystemDao;
    private final TradingSystemFacade tradingSystemFacade;
    private final Set<String> regToDailyVisitor = new HashSet<>();

    @Override
    @Synchronized("publisherSync")
    public void register(Observer obj) {
        if (Objects.isNull(obj)) {
            throw new RegisterObserverException("Null Observer");
        }
        if (observers.stream()
        .noneMatch(observer -> observer.getUserName().equals(obj.getUserName()))) {
            observers.add(obj);
        }
    }

    @Override
    @Synchronized("publisherSync")
    public void unregister(Observer obj) {
        observers.stream().filter(observer -> observer.getUserName().equals(obj.getUserName())).findFirst()
                .ifPresent(observers::remove);
    }

    @Override
    public void update(Observer obj) {
        if(observers.stream().noneMatch(observer -> observer.getUserName().equals(obj.getUserName()))){
            observersWithNotification.add(obj);
        }
    }

    @Override
    @Synchronized("publisherSync")
    @Scheduled(fixedRateString = "6000", initialDelayString = "0")
    public void notifyObservers() {
        try {
            List<Notification> notifications = new LinkedList<>();
            observersWithNotification.forEach(observer -> {
                Optional<UserSystem> userSystemOptional = tradingSystemDao.getUserSystem(observer.getUserName());
                if(userSystemOptional.isPresent()){
                    UserSystem userSystem = userSystemOptional.get();
                    List<Notification> notificationsObserver = userSystem.getNotifications();
                    tradingSystemDao.updateUser(userSystem);
                    notifications.addAll(notificationsObserver);
                }

            });
            if (!CollectionUtils.isEmpty(notifications)) {
                tradingSystemFacade.sendNotification(notifications);
                observersWithNotification = new HashSet<>();
            }
        }catch(CannotCreateTransactionException exception ){
            log.error("Connection to DB loss", exception);
        }
    }

    @Override
    @Synchronized("publisherSync")
    public void Broadcast(Notification notification) {
        observers.forEach(observer -> observer.newNotification(notification));
    }

    @Override
    public boolean regDailyVisitor(String username) {
        return regToDailyVisitor.add(username);
    }

    @Override
    public boolean unRegDailyVisitor(String username) {
        return regToDailyVisitor.remove(username);
    }

    @Override
    public void sendDailyVisitor(UpdateDailyVisitor updateDailyVisitor) {
        regToDailyVisitor.forEach(username -> {
                    Optional<Observer> optionalObserver = observers.stream().filter(oserver -> oserver.getUserName().equals(username)).findFirst();
                    optionalObserver.map(observer -> {
                        updateDailyVisitor.setPrincipal(observer.getPrincipal());
                       return tradingSystemFacade.sendDailyVisitor(updateDailyVisitor);
                    });
                });
    }

}

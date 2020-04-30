package com.wsep202.TradingSystem.domain.trading_system_management.notification;

import com.wsep202.TradingSystem.domain.trading_system_management.notification.Observer;

public interface Subject {

    public void register(Observer obj);

    public void unregister(Observer obj);

    public void update(Observer obj);

    public void notifyObservers();

    public void Broadcast(Notification notification);
}

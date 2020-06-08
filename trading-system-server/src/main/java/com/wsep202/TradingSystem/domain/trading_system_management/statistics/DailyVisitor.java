package com.wsep202.TradingSystem.domain.trading_system_management.statistics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@AllArgsConstructor
@Data
@Entity
public class DailyVisitor {

    @Temporal(TemporalType.DATE)
    @Id
    private Date date;

    private long guests;

    private long ownerStores;

    private long managerStores;

    private long simpleUser;

    private long admins;

    public DailyVisitor(){
        this.date = new Date();
    }

    public DailyVisitor update(DailyVisitorsField dailyVisitorsField) {
        switch (dailyVisitorsField){
            case ADMIN:
                admins++;
                break;
            case GUESTS:
                guests++;
                break;
            case MANAGER_STORES:
                managerStores++;
                break;
            case OWNERS_STORES:
                ownerStores++;
                break;
            case SIMPLE_USER:
                simpleUser++;
                break;
        }
        return this;
    }
}

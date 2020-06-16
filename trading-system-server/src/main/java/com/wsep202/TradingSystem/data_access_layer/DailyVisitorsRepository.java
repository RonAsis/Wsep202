package com.wsep202.TradingSystem.data_access_layer;

import com.wsep202.TradingSystem.domain.trading_system_management.statistics.DailyVisitor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.Set;

public interface DailyVisitorsRepository extends JpaRepository<DailyVisitor, Date> {

    Set<DailyVisitor> findByDateBetween(Date startDate, Date endDate);

    Set<DailyVisitor> findByDateBetween(Date startDate, Date endDate, Pageable pageable);

}

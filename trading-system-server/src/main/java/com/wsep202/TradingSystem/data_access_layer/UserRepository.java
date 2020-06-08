package com.wsep202.TradingSystem.data_access_layer;

import com.wsep202.TradingSystem.domain.trading_system_management.Store;
import com.wsep202.TradingSystem.domain.trading_system_management.UserSystem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;


public interface UserRepository extends JpaRepository<UserSystem, String> {
}

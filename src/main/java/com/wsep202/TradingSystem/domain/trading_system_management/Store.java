package com.wsep202.TradingSystem.domain.trading_system_management;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Slf4j
public class Store {

    @Builder.Default
    private Map<UserSystem , Set<UserSystem>> appointedOwners = new HashMap<>();

    @Builder.Default
    private Map<UserSystem, Set<MangerStore>> appointedManagers = new HashMap<>();

}

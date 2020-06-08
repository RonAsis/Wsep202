package com.wsep202.TradingSystem.domain.trading_system_management.discount;

import com.wsep202.TradingSystem.domain.exception.CompositeOperatorDoesntExistException;
import com.wsep202.TradingSystem.domain.exception.PermissionException;
import com.wsep202.TradingSystem.domain.trading_system_management.StorePermission;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public enum CompositeOperator {
    AND("and"),
    OR("or"),
    XOR("xor");

    public final String name;

    CompositeOperator(String name) {
        this.name = name;
    }

    public static CompositeOperator getCompositeOperators(String name) {
        return Arrays.stream(CompositeOperator.values())
                .filter(compositeOperator -> compositeOperator.name.equals(name))
                .findFirst().orElseThrow(() -> new CompositeOperatorDoesntExistException(name));
    }

    public static List<String> getStringCompositeOperators() {
        return Arrays.stream(CompositeOperator.values())
                .map(compositeOperator -> compositeOperator.name)
                .collect(Collectors.toList());
    }
}





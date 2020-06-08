package com.wsep202.TradingSystem.domain.trading_system_management.ownerStore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Id;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OwnerToApproveId implements Serializable {

    private int storeId;

    private String usernameToApprove;
}

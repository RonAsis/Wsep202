package com.wsep202.TradingSystem.domain.trading_system_management.ownerStore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@IdClass(OwnerToApproveId.class)
public class OwnerToApprove {

    @Id
    private int storeId;

    private String storeName;

    @Id
    private String usernameToApprove;
}

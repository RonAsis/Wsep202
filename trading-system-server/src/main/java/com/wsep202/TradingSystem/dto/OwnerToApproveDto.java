package com.wsep202.TradingSystem.dto;

import com.wsep202.TradingSystem.domain.trading_system_management.ownerStore.OwnerToApproveId;
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
public class OwnerToApproveDto {

    private int storeId;

    private String storeName;

    private String usernameToApprove;
}

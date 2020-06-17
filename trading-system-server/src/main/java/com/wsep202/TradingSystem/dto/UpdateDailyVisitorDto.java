package com.wsep202.TradingSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateDailyVisitorDto {

    /**
     * the username
     */
    private String principal;

    @Builder.Default
    private long guests = 0;

    @Builder.Default
    private long ownerStores = 0;

    @Builder.Default
    private long managerStores = 0;

    @Builder.Default
    private long simpleUser = 0;

    @Builder.Default
    private long admins = 0;
}

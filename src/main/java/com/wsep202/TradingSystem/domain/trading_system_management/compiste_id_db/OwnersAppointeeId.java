package com.wsep202.TradingSystem.domain.trading_system_management.compiste_id_db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Id;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OwnersAppointeeId implements Serializable{

    private String appointeeUser;

    private int storeId;

}

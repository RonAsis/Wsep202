package com.wsep202.TradingSystem.domain.trading_system_management;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
@Builder
@Entity
public class ManagersAppointee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String appointeeUser;

    @OneToMany(cascade = CascadeType.ALL)
    private Set<MangerStore> appointedManagers;

    public ManagersAppointee(String appointeeUser,  Set<MangerStore> appointedManagers) {
        this.appointeeUser = appointeeUser;
        this.appointedManagers = appointedManagers;

    }
}

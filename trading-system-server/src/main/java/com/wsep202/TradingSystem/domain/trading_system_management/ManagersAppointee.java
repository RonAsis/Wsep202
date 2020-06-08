package com.wsep202.TradingSystem.domain.trading_system_management;

import com.wsep202.TradingSystem.domain.exception.TradingSystemException;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.util.HashSet;
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

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<MangerStore> appointedManagers;

    public ManagersAppointee(String appointeeUser,  Set<MangerStore> appointedManagers) {
        this.appointeeUser = appointeeUser;
        this.appointedManagers = appointedManagers;
    }

    public ManagersAppointee(String appointeeUser) {
        this.appointeeUser = appointeeUser;
        this.appointedManagers = new HashSet<>();
    }
    public boolean removeManager(UserSystem user, int storeId) {
        MangerStore mangerStoreToRemove = appointedManagers.stream()
                .filter(mangerStore -> mangerStore.getAppointedManager().getUserName().equals(user.getUserName()))
                .findFirst()
                .orElseThrow(() -> new TradingSystemException(String.format("The user %s is not manager", user.getUserName())));
        appointedManagers.remove(mangerStoreToRemove);
        return user.removeManagedStore(storeId);
    }

    public void addManger(MangerStore newManagerStore) {
        appointedManagers.add(newManagerStore);
    }
}

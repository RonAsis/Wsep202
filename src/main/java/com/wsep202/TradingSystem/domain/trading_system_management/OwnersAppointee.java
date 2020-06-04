package com.wsep202.TradingSystem.domain.trading_system_management;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
@Builder
@Entity
public class OwnersAppointee implements Serializable {

    @Id
    private String appointeeUser;

    @Id
    private int storeId;

    @OneToMany(cascade = CascadeType.ALL)
    @Builder.Default
    private Set<UserSystem> appointedUsers = new HashSet<>();

    public OwnersAppointee(String appointeeUser, int storeId){
        this.appointeeUser = appointeeUser;
        this.appointedUsers = new HashSet<>();
        this.storeId = storeId;
    }

    public boolean addSubOwner(UserSystem newOwner){
        return appointedUsers.add(newOwner);
    }

    public boolean removeSubOwner(String username, Store store){
        return appointedUsers.stream()
                .filter(userSystem -> userSystem.getUserName().equals(username))
                .findFirst()
                .map(userSystem -> {
                    appointedUsers.remove(userSystem);
                    userSystem.removeOwnedStore(store);
                    return true;
                }).orElse(false);
    }
}

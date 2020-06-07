package com.wsep202.TradingSystem.domain.trading_system_management;

import com.wsep202.TradingSystem.domain.trading_system_management.ownerStore.OwnerToApprove;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private UserSystem appointeeUser;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Builder.Default
    private Set<UserSystem> appointedUsers = new HashSet<>();

    public OwnersAppointee(UserSystem appointeeUser){
        this.appointeeUser = appointeeUser;
        this.appointedUsers = new HashSet<>();
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

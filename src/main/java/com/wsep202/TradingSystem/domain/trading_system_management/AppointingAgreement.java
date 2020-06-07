package com.wsep202.TradingSystem.domain.trading_system_management;

import com.wsep202.TradingSystem.domain.trading_system_management.ownerStore.StatusOwner;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@Builder
@Slf4j
@Entity
public class AppointingAgreement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int appointingAgreementNumber;

    @Builder.Default
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private UserSystem newOwner;

    @Builder.Default
    private String appointee;

    @Builder.Default
    @ElementCollection
    @MapKeyColumn(name = "owner_name")
    private Map<String, StatusOwner> ownersAndApproval;

    public AppointingAgreement() {
    }

    public AppointingAgreement(UserSystem newOwner, String appointee, Set<String> owners) {
        this.newOwner = newOwner;
        this.appointee = appointee;
        owners.forEach(owner -> {
            if (owner.equals(appointee)) {
                this.ownersAndApproval.put(owner, StatusOwner.APPROVE);
            } else {
                this.ownersAndApproval.put(owner, StatusOwner.WAITING);
            }
        });
    }

    public StatusOwner changeApproval(String owner, StatusOwner statusOwner) {
        this.ownersAndApproval.replace(owner, statusOwner);
        return statusOwner;
    }

    public StatusOwner checkIfApproved() {
        return ownersAndApproval.values().stream()
                .filter(status -> status == StatusOwner.NOT_APPROVE)
                .findFirst()
                .orElse(ownersAndApproval.values().stream()
                .filter(statusOwner -> statusOwner == StatusOwner.WAITING)
                .findFirst().orElse(StatusOwner.APPROVE));
    }

}

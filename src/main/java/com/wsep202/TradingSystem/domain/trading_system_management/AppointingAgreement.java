package com.wsep202.TradingSystem.domain.trading_system_management;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.util.List;
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
    private String newOwner;

    @Builder.Default
    private String appointee;

    @Builder.Default
    @ElementCollection
    @MapKeyColumn(name = "owner_name")
    private Map<String, Boolean> ownersAndApproval;

    public AppointingAgreement() {
    }

    public AppointingAgreement(String newOwner, String appointee, Set<String> owners) {
        this.newOwner = newOwner;
        this.appointee = appointee;
        owners.forEach(owner -> {
            if (owner.equals(appointee)) {
                this.ownersAndApproval.put(owner, true);
            } else {
                this.ownersAndApproval.put(owner, null);
            }
        });
    }

    public void changeApproval(String owner, Boolean approval) {
        this.ownersAndApproval.replace(owner, approval);
    }

    public boolean checkIfApproved() {
        for (String owner : ownersAndApproval.keySet()) {
            if (!ownersAndApproval.get(owner)) {
                return false;
            }
        }
        return true; //tradingSystem.addOwnerToStore(ownerStore, ownerUser, newOwnerUsername);
    }

}

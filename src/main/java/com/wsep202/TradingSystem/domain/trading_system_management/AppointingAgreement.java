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
//@NoArgsConstructor
@Builder
@Slf4j
@Entity
public class AppointingAgreement {
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        private int appointingAgreementNumber;

        @Builder.Default
        String newOwner;

        @Builder.Default
        String appointee;

        @Builder.Default
        @ElementCollection
        @JoinTable()
        Map<String, Boolean> ownersAndApproval;

        public AppointingAgreement(String newOwner, String appointee, Set<UserSystem> owners) {
                this.newOwner = newOwner;
                this.appointee = appointee;
                for (UserSystem userSystem : owners) {
                        if (userSystem.getUserName().equals(appointee)) {
                                this.ownersAndApproval.put(userSystem.getUserName(), true);
                        } else {
                                this.ownersAndApproval.put(userSystem.getUserName(), null);
                        }
                }
        }

        public void changeApproval(String owner, Boolean approval) {
                this.ownersAndApproval.replace(owner, approval);
        }

        public boolean checkIfApproved (){
                for (String owner: ownersAndApproval.keySet()){
                        if (!ownersAndApproval.get(owner)){
                                return false;
                        }
                }
                return true; //tradingSystem.addOwnerToStore(ownerStore, ownerUser, newOwnerUsername);
        }

}
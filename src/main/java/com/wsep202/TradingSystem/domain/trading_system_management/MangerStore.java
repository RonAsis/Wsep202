package com.wsep202.TradingSystem.domain.trading_system_management;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Slf4j
@Entity
@Data
public class MangerStore implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    /**
     *  the permissions that were given to the manager
     */
    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = StorePermission.class, fetch = FetchType.EAGER)
    private Set<StorePermission> storePermissions;

    /**
     *  the actual user that appointed to be manager
     */
    @OneToOne(cascade = CascadeType.ALL)
    private UserSystem appointedManager;

    public MangerStore(UserSystem appointedManager) {
        this.storePermissions = new HashSet<>(Arrays.asList(StorePermission.VIEW));
        this.appointedManager = appointedManager;
    }

    /**
     * returns the actual user system object of the user wrapped by the manager object
     * @param user the userSystem object to return
     * @return
     */
    public boolean isTheUser(UserSystem user) {
        return appointedManager.equals(user);
    }

    /**
     * returns the actual user system object of the user wrapped by the manager object
     * @param username the username of the manager user to return
     * @return
     */
    public boolean isTheUser(String username) {
        return appointedManager.getUserName().equals(username);
    }

    /**
     * add new permission to this manager
     * @param permission the management option the manager received
     * @return true for success or false otherwise
     */
    public boolean addStorePermission(StorePermission permission){
        int sizeOfPermissions = storePermissions.size();
        this.storePermissions.add(permission);
        return sizeOfPermissions < storePermissions.size();
    }

    /**
     * delete permission from the manager permissions list
     * @param permission the permission to delete
     * @return true for success or false otherwise
     */
    public boolean removeStorePermission(StorePermission permission){
        //remove all permissions equals to permission received
        return storePermissions.removeIf(permission1 -> permission1 == permission);
    }

    public boolean canEdit() {
        return storePermissions.stream()
                .anyMatch(storePermission -> storePermission==(StorePermission.EDIT_PRODUCT));
    }

    public boolean canEditProduct() {
        return storePermissions.stream()
                .anyMatch(storePermission -> storePermission==(StorePermission.EDIT_PRODUCT));
    }

    public boolean canEditDiscount() {
        return storePermissions.stream()
                .anyMatch(storePermission -> storePermission==(StorePermission.EDIT_DISCOUNT));
    }

    public boolean canEditPurchasePolicy() {
        return storePermissions.stream()
                .anyMatch(storePermission -> storePermission==(StorePermission.EDIT_PURCHASE_POLICY));
    }
    public boolean removeManagedStore(Store storeToRemove){
      return appointedManager.removeOwnedStore(storeToRemove);
    }
}

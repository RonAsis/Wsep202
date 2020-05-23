package com.wsep202.TradingSystem.domain.trading_system_management;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Data
@Slf4j
public class MangerStore {

    public MangerStore(UserSystem appointedManager) {
        this.storePermissions = new HashSet<>(Arrays.asList(StorePermission.VIEW));
        this.appointedManager = appointedManager;
    }

    /**
     *  the permissions that were given to the manager
     */
    private Set<StorePermission> storePermissions;

    /**
     *  the actual user that appointed to be manager
     */
    private UserSystem appointedManager;

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
                .anyMatch(storePermission -> storePermission==(StorePermission.EDIT));
    }

    public boolean removeManagedStore(Store storeToRemove){
      return appointedManager.removeOwnedStore(storeToRemove);
    }
}

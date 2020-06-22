package com.wsep202.TradingSystem.domain.trading_system_management;

import com.wsep202.TradingSystem.domain.exception.*;
import com.wsep202.TradingSystem.domain.trading_system_management.discount.CompositeOperator;
import com.wsep202.TradingSystem.domain.trading_system_management.discount.Discount;
import com.wsep202.TradingSystem.domain.trading_system_management.discount.DiscountType;
import com.wsep202.TradingSystem.domain.trading_system_management.notification.Notification;
import com.wsep202.TradingSystem.domain.trading_system_management.ownerStore.OwnerToApprove;
import com.wsep202.TradingSystem.domain.trading_system_management.ownerStore.StatusOwner;
import com.wsep202.TradingSystem.domain.trading_system_management.policy_purchase.Day;
import com.wsep202.TradingSystem.domain.trading_system_management.policy_purchase.Purchase;
import com.wsep202.TradingSystem.domain.trading_system_management.policy_purchase.PurchaseType;
import com.wsep202.TradingSystem.domain.trading_system_management.purchase.BillingAddress;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@Builder
@NoArgsConstructor
@Slf4j
@Setter
@Getter
@Entity
public class Store {

    @Transient
    private final Object stockLock = new Object();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int storeId;

    private String storeName;

    /**
     * map to find all the owners as value appointed the owner who is in the fit key
     */
    @Builder.Default
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<OwnersAppointee> appointedOwners = new HashSet<>();

    /**
     * map  that holds users as key and their appointed managers as value
     */
    @Builder.Default
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<ManagersAppointee> appointedManagers = new HashSet<>();

    /**
     * The products that the store holds in it
     */
    @Builder.Default
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("productSn DESC")
    private Set<Product> products = new LinkedHashSet<>();

    /**
     * The set purchase policy for the store
     */
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<Purchase> purchasePolicies;

    /**
     * The set purchase policy for the store
     */
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("discountId DESC")
    private Set<Discount> discounts;

    /**
     * list of purchases made in the store
     */
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<Receipt> receipts;

    /**
     * the store rank
     */
    private int rank;

    /**
     * description
     */
    private String description;

    /**
     * list of appointing agreements of owners in the store
     */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<AppointingAgreement> appointingAgreements;

    /**
     * for new store
     */
    public Store(UserSystem owner, String storeName) {
        initStore(owner, storeName);
    }

    /**
     * for DB
     */
    public Store(String storeName, Set<OwnersAppointee> appointedOwners,
                 Set<ManagersAppointee> appointedManagers,
                 Set<Product> products,
                 Set<Purchase> purchasePolicies,
                 Set<Discount> discounts,
                 Set<Receipt> receipts,
                 int rank, String description,
                 Set<AppointingAgreement> appointingAgreements) {
        this.storeName = storeName;
        this.appointedOwners = appointedOwners;
        this.appointedManagers = appointedManagers;
        this.products = products;
        this.purchasePolicies = purchasePolicies;
        this.discounts = discounts;
        this.receipts = receipts;
        this.rank = rank;
        this.description = description;
        this.appointingAgreements = appointingAgreements;
    }

    private void initStore(UserSystem owner, String storeName) {
        //storeId = generateStoreSn();
        purchasePolicies = new HashSet<>();
        discounts = new HashSet<>();
        receipts = new HashSet<>();
        appointingAgreements = new HashSet<>();
        appointedOwners = new HashSet<>();
        appointedManagers = new HashSet<>();
        products = new LinkedHashSet<>();
        this.storeName = storeName;
        appointedOwners.add(new OwnersAppointee(owner));
        this.rank = 5;
    }

    public Store(UserSystem owner, String storeName, String description) {
        // storeId = generateStoreSn();
        initStore(owner, storeName);
        this.description = description;
    }

    /////////////////////////////////////////products////////////////////////////////////////////////

    /**
     * return a product with the SN received
     *
     * @param productId - SN of product to get
     * @return the fit product if exist in the store
     * otherwise exception is thrown
     */
    public Product getProduct(int productId) {
        return products.stream().filter(product -> product.getProductSn() == productId).findFirst()
                .orElseThrow(() -> new ProductDoesntExistException(productId, storeId));
    }
    @Synchronized("stockLock")
    public boolean removeProductFromStore(UserSystem user, int productSn) {
        if (validatePermission(user, StorePermission.EDIT_PRODUCT)) {  //only owner can remove products from its store
            Set<Product> duplicate = new HashSet<>(products);
            duplicate.stream()
                    .filter(product -> product.getProductSn() == productSn)
                    .forEach(products::remove);
            removeProductFromDiscount(productSn);
            //is present
            return duplicate.size() > products.size();
        }
        //the user is not an owner of the store so can't remove
        log.info("The product with id: " + productSn + " wasn't removed.\n" +
                "not owner");
        return false;
    }

    /**
     * verify the user usersystem has permission to edit product
     */
    public boolean validateCanEditProducts(UserSystem userSystem, int productSn) {
        return validatePermission(userSystem, StorePermission.EDIT_PRODUCT) &&
                products.stream()
                        .filter(product -> product.getProductSn() == productSn)
                        .findFirst().isPresent();
    }

    private boolean validatePermission(UserSystem user, StorePermission storePermission) {
        return isOwner(user) || isManagerWithPermission(user.getUserName(), storePermission);
    }


    /**
     * owner adds a new product to the store
     * @param user    the user wish to add the product
     * @param product the product to add to store
     * @return true for success
     */
    public boolean addNewProduct(UserSystem user, Product product) {
        if (validatePermission(user, StorePermission.EDIT_PRODUCT)) {  //verify the user is owner of the store
            products.add(product);
            log.info("The product: " + product.getName() + " added to the store: " + this.storeName);
            return true;
        }
        log.info("The product: " + product.getName() + " failed to add to the store: " + this.storeName);
        return false;
    }

    /**
     * edit of exist product parameters in the store
     * @param user        the user wish to add the product
     * @param productSn   the unique number of the product
     *       parameters of the product:
     * @param productName
     * @param category
     * @param amount
     * @param cost
     * @return true for success
     */
    @Synchronized("stockLock")
    public boolean editProduct(UserSystem user, int productSn, String productName, String category,
                               int amount, double cost) {
        if (validatePermission(user, StorePermission.EDIT_PRODUCT)) {   //the user is owner

            Optional<Product> optionalProduct = products.stream().
                    filter(product -> product.getProductSn() == productSn).findFirst();
            if (optionalProduct.isPresent()) {
                //remove the old
                products.remove(optionalProduct.get());
                //replace by updated
                optionalProduct.get().setOriginalCost(cost);
                optionalProduct.get().setName(productName);
                optionalProduct.get().setCategory(ProductCategory.getProductCategory(category));
                optionalProduct.get().setAmount(amount);
                log.info("The product " + productName + " edited successfully");
                products.add(optionalProduct.get());
                return true;
            }
        }
        log.info("Failed to edit the product: " + productName);
        return false;
    }

    ////////////////////////////////////////receipts ///////////////////////////////////////////////////////

    /**
     * The store creates receipt for the products purchased in the bag.
     * @param bag
     * @param buyerName
     * @return
     */
    public Receipt createReceipt(ShoppingBag bag, String buyerName,int payTransId, int suppTransId) {
        String payId = String.valueOf(payTransId);
        String supplyId = String.valueOf(suppTransId);
        return new Receipt(this.storeId, buyerName, bag.getTotalCostOfBag()
                , bag.getProductListFromStore(),payId,supplyId);
    }

    //////////////////////////////////////////////shopping cart /////////////////////////////////////////////

    /**
     * update amount of product in the stock
     * @param productSn
     * @param amount
     * @return true if operation succeeded
     */
    @Synchronized("stockLock")
    public boolean editProductAmountInStock(int productSn, int amount) {
        Optional<Product> product = products.stream().filter(p -> p.getProductSn() == productSn).findFirst();
        if (product.isPresent()) {    //update the product properties
            product.get().setAmount(amount);
            log.info("The product with id" + productSn + " edited successfully");
            return true;
        }
        log.info("Failed to edit amount of the product: " + productSn);
        return false;
    }

    /**
     * checks if all products in shopping bag are in stock of the store
     * @param bag the products list the user wish to purchase
     * @return true if all products in stock
     * otherwise exception
     */
    @Synchronized("stockLock")
    public boolean isAllInStock(ShoppingBag bag) throws TradingSystemException {
        for (Product product : bag.getProductListFromStore().keySet()) {
            int amount = bag.getProductAmount(product.getProductSn());
            Product productInStore = getProduct(product.getProductSn());
            if (amount > productInStore.getAmount()) {
                log.info("The product " + product.getName() + " is out of stock in " + storeName);
                throw new NotInStockException(product.getName(), this.storeName);   //th
            }
        }
        //all products in stock
        log.info("all products in stock");
        return true;
    }

    /**
     * update amount of each bag product in the stock of store
     * after the purchase
     * @param bag shopping bag
     */
    @Synchronized("stockLock")
    public void updateStock(ShoppingBag bag) {
        for (Product product : bag.getProductListFromStore().keySet()) {
            int purchasedAmount = bag.getProductAmount(product.getProductSn());
            Product productInStore = getProduct(product.getProductSn());
            editProductAmountInStock(productInStore.getProductSn(), productInStore.getAmount() - purchasedAmount);
        }
    }

    /////////////////////////////////////////////////owner /////////////////////////////////////////////

    /**
     * returns all the usernames of all owners in the store
     */
    public Set<String> getOwnersUsername() {
        return this.appointedOwners.stream()
                .map(ownersAppointee -> ownersAppointee.getAppointeeUser().getUserName())
                .collect(Collectors.toSet());
    }

    /**
     * check if a user that wants to be approved is approved
     * @param ownerUser
     * @param ownerToApprove
     * @param status
     * @return - true if the user approved, else returns false
     */
    public boolean approveOwner(UserSystem ownerUser, String ownerToApprove, boolean status) {
        return appointingAgreements.stream()
                .filter(appointingAgreement -> appointingAgreement.getNewOwner().getUserName().equals(ownerToApprove))
                .findFirst()
                .map(appointingAgreement -> {
                    appointingAgreement.changeApproval(ownerUser.getUserName(), status ? StatusOwner.APPROVE : StatusOwner.NOT_APPROVE);
                    ownerUser.removeAgreement(storeId, ownerToApprove);
                    isApproveOwner(appointingAgreement.getNewOwner());
                    log.info("The owner: "+ownerUser.getUserName()+" approved: "+ownerToApprove+"" +
                            "with status: "+status);
                    return true;
                }).orElse(false);
    }

    /**
     *
     * @param userSystemApproveOwner
     * @return
     */
    public StatusOwner isApproveOwner(UserSystem userSystemApproveOwner) {
        Optional<AppointingAgreement> agreement = appointingAgreements.stream()
                .filter(appointingAgreement -> appointingAgreement.getNewOwner().getUserName().equals(userSystemApproveOwner.getUserName()))
                .findFirst();
        StatusOwner statusOwner = agreement
                .map(AppointingAgreement::checkIfApproved)
                .orElse(StatusOwner.WAITING);
        if (statusOwner == StatusOwner.APPROVE) {
            agreement.map(appointingAgreement -> {
                return appointedOwners.stream()
                        .filter(appointedOwner -> appointedOwner.getAppointeeUser().getUserName().equals(appointingAgreement.getAppointee()))
                        .findFirst()
                        .map(appointedOwner -> {
                            appointedOwner.addSubOwner(userSystemApproveOwner);
                            userSystemApproveOwner.addNewOwnedStore(this);
                            userSystemApproveOwner.newNotification(Notification.builder()
                                    .content(String.format("You are now owner of store %s on name %s", getStoreId(), getStoreName()))
                                    .build());
                            appointingAgreements.remove(appointingAgreement);
                            return appointedOwners.add(new OwnersAppointee(userSystemApproveOwner));
                        });
            });
        } else if (statusOwner == StatusOwner.NOT_APPROVE && agreement.isPresent()) {
            appointingAgreements.remove(agreement.get());
            appointedOwners.forEach(appointedOwner ->
                    appointedOwner.getAppointeeUser().removeAgreement(getStoreId(), userSystemApproveOwner.getUserName()));
        }
        return statusOwner;
    }

    /**
     * remove ownerToRemove from the store's managers
     *
     * @param ownerStore the removing owner
     * @param ownerToRemove       the removed manager
     * @return true for successful operation
     */
    public boolean removeOwner(UserSystem ownerStore, UserSystem ownerToRemove) {
        boolean response = false;
        if(checkIsAppointedOf(ownerStore, ownerToRemove)) {
            getMySubMangers(ownerToRemove.getUserName()).forEach(subManager -> {
                removeManager(ownerToRemove, subManager.getAppointedManager());
            });
            if (isOwner(ownerStore) && removeOwnerRecursive(ownerStore, ownerToRemove)) {  //the ownerToRemove is able to remove his appointments
                response = appointedOwners.stream()
                        .filter(appointedOwner -> appointedOwner.getAppointeeUser().getUserName().equals(ownerStore.getUserName()))
                        .findFirst()
                        .map(appointedOwner -> appointedOwner.removeSubOwner(ownerToRemove.getUserName(), this))
                        .orElse(false);
            }
        }
        return response;
    }

    private boolean checkIsAppointedOf(UserSystem ownerStore, UserSystem ownerToRemove){
        return appointedOwners.stream()
                .filter(appointedOwner -> appointedOwner.getAppointeeUser().getUserName().equals(ownerStore.getUserName()))
                .findFirst()
                .map(appointedOwner -> appointedOwner.getAppointedUsers().stream()
                .anyMatch(subOwner -> subOwner.getUserName().equals(ownerToRemove.getUserName())))
                .orElse(false);
    }
    private boolean removeOwnerRecursive(UserSystem ownerStore, UserSystem user) {
        boolean response = false;
        if (isOwner(ownerStore)) {  //the user is able to remove his appointments
            response = appointedOwners.stream()
                    .filter(appointedOwner -> appointedOwner.getAppointeeUser().getUserName().equals(user.getUserName()))
                    .findFirst()
                    .map(appointedOwner -> {
                        appointedOwner.getAppointedUsers()
                                .forEach(subOwner -> removeOwnerRecursive(user, subOwner));
                        user.removeOwnedStore(this);
                        return appointedOwners.remove(appointedOwner);
                    })
                    .orElseGet(() -> user.removeManagedStore(this));
        }
        return response;
    }

    /**
     * This method is used to find all the owners that needs to be removed from the store
     *
     * @return set of all the owners that needs to be removed
     */
    private Set<UserSystem> findOwnersToRemove(UserSystem ownerToRemove, Set<UserSystem> ownersToRemove) {
        int ownerToRemoveIndex = getAppointeeOwnersIndex(ownerToRemove.getUserName());
        if (ownerToRemoveIndex != -1)
            if (new LinkedList<>(appointedOwners).get(ownerToRemoveIndex).getAppointedUsers() != null && (new LinkedList<>(appointedOwners).get(ownerToRemoveIndex).getAppointedUsers().size() > 0)) {
                ownersToRemove.addAll((new LinkedList<>(appointedOwners).get(ownerToRemoveIndex).getAppointedUsers()));
                for (UserSystem user : (new LinkedList<>(appointedOwners).get(ownerToRemoveIndex).getAppointedUsers())) {
                    findOwnersToRemove(user, ownersToRemove);
                }
            }
        return ownersToRemove;
    }

    /**
     * This method is used to find all the managers that needs to be removed from the store
     *
     * @return set of all the managers that needs to be removed
     */
    private Set<MangerStore> findManagersToRemove(Set<UserSystem> ownersToRemove, Set<MangerStore> managerToRemove) {
        for (UserSystem owner : ownersToRemove) {
            int ownerIndex = getAppointeeManagersIndex(owner.getUserName());
            if (ownerIndex != -1)
                if ((new LinkedList<>(appointedManagers).get(ownerIndex).getAppointedManagers() != null && !new LinkedList<>(appointedManagers).get(ownerIndex).getAppointedManagers().isEmpty()))
                    managerToRemove.addAll(new LinkedList<>(appointedManagers).get(ownerIndex).getAppointedManagers());
        }
        return managerToRemove;
    }

    /**
     * get the actual user of the manager appointed by the ownerToRemove with managerUserName
     * @param ownerToRemove     - the appointing owner
     * @return the user belongs to the manager username
     * or exception if not appointed by the owner
     * or null if the owner didn't appointed anyone
     */
    public UserSystem getOwnerToRemove(UserSystem owner, String ownerToRemove) {
        return appointedOwners.stream()
                .filter(appointedOwner -> appointedOwner.getAppointeeUser().getUserName().equals(owner.getUserName()))
                .map(appointedOwner -> appointedOwner.getAppointedUsers().stream()
                                        .filter(subOwner -> subOwner.getUserName().equals(ownerToRemove))
                                        .findFirst().orElseThrow(()-> new NoOwnerInStoreException(ownerToRemove, storeId)))
                .findFirst().orElseThrow(()-> new NoOwnerInStoreException(owner.getUserName(), storeId));
    }

    public List<String> getMySubOwners(String ownerUsername) {
        return appointedOwners.stream()
                .filter(userSystemSetEntry -> userSystemSetEntry.getAppointeeUser().getUserName().equals(ownerUsername))
                .findFirst()
                .map(OwnersAppointee::getAppointedUsers)
                .orElse(new HashSet<>())
                .stream()
                .map(UserSystem::getUserName)
                .collect(Collectors.toList());
    }

    /**
     * checks if the given user is owner in this store
     *
     * @param user
     * @return
     */
    public boolean isOwner(UserSystem user) {
        return (getOwnersUsername().stream().anyMatch(curUser -> curUser.equals(user.getUserName())));
    }

    private boolean checkIfExistsAgreement(String userName) {
        return appointingAgreements.stream()
                .anyMatch(appointingAgreement -> appointingAgreement.getNewOwner().equals(userName));
    }


    /**
     * add new owner to the appointed owners of the store
     *
     * @param owner
     * @param willBeOwner
     * @return true if owner added successfully
     */
    public Set<UserSystem> addOwner(UserSystem owner, UserSystem willBeOwner) {
        if (isOwner(owner) && !isOwner(willBeOwner) && !checkIfExistsAgreement(owner.getUserName())
                && !isOwner(willBeOwner) && !isManager(willBeOwner.getUserName())) {
            appointingAgreements.add(new AppointingAgreement( willBeOwner, owner.getUserName(), getOwnersUsername()));
            appointedOwners.stream()
                    .filter(appointedOwner -> !appointedOwner.getAppointeeUser().getUserName().equals(owner.getUserName()))
                    .forEach(appointedOwner -> {
                        appointedOwner.getAppointeeUser().addOwnerToApprove(storeId, storeName, willBeOwner.getUserName());
                    });
            return getAllOwnersNeedApprove(owner);
        }
        return null;
    }

    private Set<UserSystem> getAllOwnersNeedApprove(UserSystem owner){
        return  appointedOwners.stream()
                .filter(appointedOwner -> !appointedOwner.getAppointeeUser().getUserName().equals(owner.getUserName()))
                .map(OwnersAppointee::getAppointeeUser)
                .collect(Collectors.toSet());

    }

    /////////////////////////////////////// manager //////////////////////////////////////////////////

    /**
     * add new manager to the appointed owners of the store
     *
     * @param owner
     * @return true for successful addition
     */
    public MangerStore appointAdditionManager(UserSystem owner, UserSystem newManagerStore) {
        MangerStore newManager = null;
        if (validatePermission(owner, StorePermission.EDIT_Managers) && !isOwner(newManagerStore) &&
        !isManager(newManagerStore.getUserName())) {
            newManager = new MangerStore(newManagerStore);
            ManagersAppointee managersAppointee = findManagersAppointee(owner.getUserName());
            managersAppointee.addManger(newManager);
        }
        log.info(String.format("The user %s %s new manger %s to the store %d",
                owner.getUserName(), Objects.nonNull(newManager) ? "appointed" : "not appointed", newManagerStore.getUserName(), storeId));
        return newManager;
    }

    private ManagersAppointee findManagersAppointee(String appointedManagerUsername) {
        return appointedManagers.stream()
                .filter(appointedManager -> appointedManager.getAppointeeUser().equals(appointedManagerUsername))
                .findFirst()
                .orElseGet(() -> {
                    ManagersAppointee managersAppointee = new ManagersAppointee(appointedManagerUsername);
                    appointedManagers.add(managersAppointee);
                    return managersAppointee;
                });
    }

    /**
     * remove user from the store's managers
     *
     * @param ownerStore the removing owner
     * @param user       the removed manager
     * @return true for successful operation
     */
    public boolean removeManager(UserSystem ownerStore, UserSystem user) {
        boolean response = false;
        if ((validatePermission(ownerStore, StorePermission.EDIT_Managers))
                && removeManagerRecursive(ownerStore, user)) {  //the user is able to remove his appointments
            response = appointedManagers.stream()
                    .filter(appointedManager -> appointedManager.getAppointeeUser().equals(ownerStore.getUserName()))
                    .findFirst()
                    .map(appointedManager -> appointedManager.removeManager(user, storeId))
                    .orElse(false);
        }
        return response;
    }

    public boolean removeManagerRecursive(UserSystem ownerStore, UserSystem user) {
        boolean response = false;
        if (validatePermission(ownerStore, StorePermission.EDIT_Managers)) {  //the user is able to remove his appointments
            response = appointedManagers.stream()
                    .filter(appointedManager -> appointedManager.getAppointeeUser().equals(user.getUserName()))
                    .findFirst()
                    .map(appointedManager -> {
                        appointedManager.getAppointedManagers()
                                .forEach(subManager -> removeManagerRecursive(user, subManager.getAppointedManager()));
                        user.removeManagedStore(this);
                        return appointedManagers.remove(appointedManager);
                    })
                    .orElseGet(() -> user.removeManagedStore(this));
        }
        return response;
    }

    /**
     * add management options for a manager by an store owner
     *
     * @param ownerStore      - owner of this store
     * @param user            - the manager we wish to edit and add his permissions
     * @param storePermission
     * @return true if the added succeeded
     * otherwise false
     */
    public boolean addPermissionToManager(UserSystem ownerStore, UserSystem user, StorePermission storePermission) {
        boolean permissionAdded = false;
        if (validatePermission(ownerStore, StorePermission.EDIT_Managers)) {
            MangerStore mangerStore = getMangerStore(ownerStore, user);
            permissionAdded = mangerStore.addStorePermission(storePermission);
        }
        log.info(String.format("The permission %s %s by %s to %s in %d",
                storePermission.function, permissionAdded ? "added" : "not added", ownerStore.getUserName(), user.getUserName(), storeId));
        return permissionAdded;
    }

    private MangerStore getMangerStore(UserSystem ownerStore, UserSystem user) {
        return appointedManagers.stream()
                .filter(appointedManager -> appointedManager.getAppointeeUser().equals(ownerStore.getUserName()))
                .findFirst()
                .orElseThrow(() -> new TradingSystemException(String.format("The username %s is not manger and not owner in store %d",
                        ownerStore.getUserName(), storeId)))
                .getAppointedManagers().stream()
                .filter(storeManager -> storeManager.getAppointedManager().getUserName().equals(user.getUserName()))
                .findFirst()
                .orElseThrow(() -> new NoManagerInStoreException(user.getUserName(), storeId));
    }

    /**
     * get the actual user of the manager appointeed by the ownerUser with managerUserName
     *
     * @param ownerUser       - the appointing owner
     * @param managerUserName - the appointed manager
     * @return the user belongs to the manager username
     * or exception if not appointed by the owner
     * or null if the owner didn't appointed anyone
     */
    public UserSystem getManager(UserSystem ownerUser, String managerUserName) {
        int ownerUserIndex = getAppointeeManagersIndex(ownerUser.getUserName());
        if (ownerUserIndex != -1)
            if (new LinkedList<>(appointedManagers).get(ownerUserIndex).getAppointedManagers() == null)
                return null;
            else {
                return new LinkedList<>(appointedManagers).get(ownerUserIndex).getAppointedManagers().stream().
                        filter(mangerStore -> mangerStore.isTheUser(managerUserName))
                        .findFirst().orElseThrow(() -> new NoManagerInStoreException(managerUserName, storeId)).getAppointedManager();
            }
        else return null;
    }

    private int getAppointeeManagersIndex(String userName) {
        int i = 0;
        for (ManagersAppointee managersAppointee : appointedManagers) {
            if (managersAppointee.getAppointeeUser().equals(userName)) {
                return i;
            } else {
                i++;
            }
        }
        return -1; // userName doesn't exist in appointedOwners
    }

    /**
     * get the permissions of a manager
     *
     * @param userSystem
     * @return
     */
    public List<String> getOperationsCanDo(UserSystem userSystem) {
        return getManagersStore().stream()
                .filter(mangerStore -> mangerStore.isTheUser(userSystem))
                .findFirst()
                .map(mangerStore -> StorePermission.getStringPermissions(mangerStore.getStorePermissions()))
                .orElse(new ArrayList<>());
    }

    public List<MangerStore> getMySubMangers(String ownerUsername) {
        return new ArrayList<>(appointedManagers.stream()
                .filter(userSystemSetEntry -> userSystemSetEntry.getAppointeeUser().equals(ownerUsername))
                .findFirst()
                .map(ManagersAppointee::getAppointedManagers)
                .orElse(new HashSet<>()));
    }

    public boolean removePermission(UserSystem ownerStore, UserSystem user, StorePermission storePermission) {
        if (!isOwner(ownerStore)) {    //verify the editor is owner in the store
            log.error("couldn't remove permission:" + storePermission.function + " " +
                    "to: " + user.getUserName() + " by " + ownerStore.getUserName());
            return false;
        }
        return getManagersStore().stream().filter(mangerStore -> mangerStore.getAppointedManager().getUserName().equals(user.getUserName()))
                .findFirst().map(mangerStore -> mangerStore.removeStorePermission(storePermission))
                .orElse(false);
    }

    public Set<StorePermission> getPermissionOfManager(UserSystem ownerStore, UserSystem user) {
        if (!isOwner(ownerStore)) {    //verify the editor is owner in the store
            log.error("couldn't get permissions of " + user.getUserName() + " by " + ownerStore.getUserName());
            return null;
        } else {
            return getManagersStore().stream().filter(mangerStore -> mangerStore.getAppointedManager().getUserName().equals(user.getUserName()))
                    .findFirst().map(MangerStore::getStorePermissions)
                    .orElse(null);
        }
    }

    private boolean isManagerWithPermission(String username, StorePermission permission) {
        return getManagersStore().stream()
                .anyMatch(mangerStore -> mangerStore.getAppointedManager().getUserName().equals(username) &&
                        mangerStore.getStorePermissions().contains(permission));
    }

    private boolean isManager(String username) {
        return getManagersStore().stream()
                .anyMatch(mangerStore -> mangerStore.getAppointedManager().getUserName().equals(username));
    }

    public Set<MangerStore> getManagersStore() {
        return appointedManagers.stream()
                .map(ManagersAppointee::getAppointedManagers)
                .reduce(new HashSet<>(), (acc, cur) -> {
                    acc.addAll(cur);
                    return acc;
                });
    }

    public boolean managerHavePermission(String userName, StorePermission permission) {
        return getManagersStore().stream()
                .anyMatch(mangerStore -> mangerStore.getAppointedManager().getUserName().equals(userName) && mangerStore.managerHavePermission(permission));
    }

    public Set<StorePermission> getPermissionCantDo(UserSystem ownerStore, UserSystem user) {
        if (!isOwner(ownerStore) && !isManagerWithPermission(ownerStore.getUserName(), StorePermission.EDIT_Managers)) {    //verify the editor is owner in the store
            log.error(String.format("couldn't get permissions of %s by %S ", user.getUserName(), ownerStore.getUserName()));
            return null;
        } else {
            Set<StorePermission> storePermissions = getManagersStore().stream().filter(mangerStore -> mangerStore.getAppointedManager().getUserName().equals(user.getUserName()))
                    .findFirst().map(MangerStore::getStorePermissions)
                    .orElse(new HashSet<>());
            return Arrays.stream(StorePermission.values())
                    .filter(storePermission -> !storePermissions.contains(storePermission))
                    .collect(Collectors.toSet());
        }
    }

    /////////////////////////////////////////////// purchase policy //////////////////////////////////////

    /**
     * add purchase policy to store
     *
     * @param user
     * @param purchase
     * @return
     */
    public Purchase addPurchasePolicy(UserSystem user, Purchase purchase) {
        if (validatePermission(user, StorePermission.EDIT_PURCHASE_POLICY)) {  //verify the user is owner of the store
            purchasePolicies.add(purchase);
            return purchase;
        }
        throw new NotAdministratorException(String.format("%s not owner and not manager in the store %d", user.getUserName(), storeId));
    }
    /**
     * apply is approved on a shopping bag and user
     * to check if user can buy by the purchase policy
     */
    public boolean isApprovedPurchasePolicies(Map<Product, Integer> productsBag,BillingAddress userAddress) throws
            TradingSystemException{
        return purchasePolicies.stream()
                .filter(purchase1 -> !purchase1.isApproved(productsBag,userAddress))
                .toArray().length==0;
    }


    /**
     * remove specific purchase policy with purchaseId from store
     */
    public boolean removePurchase(UserSystem userSystem, int purchaseId) {
        if (validatePermission(userSystem, StorePermission.EDIT_PURCHASE_POLICY)) {
            Purchase purchase = purchasePolicies.stream()
                    .filter(purchase1 -> purchase1.getPurchaseId() == purchaseId)
                    .findFirst().orElseThrow(() -> new TradingSystemException(String.format("the purchase %d doesn't exist in store %d", purchaseId, storeId)));
            return purchasePolicies.remove(purchase);
        }
        throw new NoOwnerInStoreException(userSystem.getUserName(), this.getStoreId());
    }

    /**
     * get the purchase policies in store
     */
    public List<Purchase> getStorePurchasePolicies(UserSystem user) {
        if (validatePermission(user, StorePermission.EDIT_PURCHASE_POLICY)) {  //verify the user is owner of the store
            return new LinkedList<>(purchasePolicies);
        }
        throw new NotAdministratorException(String.format("%s not owner and not manager in the store %d", user.getUserName(), storeId));
    }


    /**
     * split to the proper method add or edit
     * @param user
     * @param purchase
     * @return
     */
    public Purchase addEditPurchase(UserSystem user, Purchase purchase) {
        if (purchase.getPurchaseId() < 0) {
            return addPurchasePolicy(user, purchase);
        } else {
            return editPurchase(user, purchase);
        }
    }

    private Purchase editPurchase(UserSystem user, Purchase purchase) {
        if (validatePermission(user, StorePermission.EDIT_PURCHASE_POLICY)) {  //verify the user is owner of the store
            Optional<Boolean> isEdit = purchasePolicies.stream()
                    .filter(purchase1 -> purchase1.getPurchaseId() == purchase.getPurchaseId())
                    .findFirst().map(purchase2 -> purchase2.editPurchase(purchase.getDescription(),purchase.getPurchasePolicy(),
                            purchase.getPurchaseType()));
            return isEdit.isPresent() ? purchase : null;
        }
        throw new NotAdministratorException(String.format("%s not owner and not manager in the store %d", user.getUserName(), storeId));
    }

    public List<Purchase> getPurchasePoliciesSimple() {
        return purchasePolicies.stream()
                .filter(purchase -> purchase.getPurchaseType() != PurchaseType.COMPOSED_POLICY)
                .collect(Collectors.toList());
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /////////////////////////////////////////////////////////discounts ///////////////////////////////////////

    /**
     * remove discounts that expired from store
     */
    private void updateExpiredDiscounts() {
        List<Discount> discounts = this.getDiscounts().stream()
                .filter(Discount::isExpired)
                .collect(Collectors.toList());
        this.discounts.removeAll(discounts);

    }

    private void removeProductFromDiscount(int productSn) {
        discounts.forEach(discount -> discount.removeProductFromDiscount(productSn));
    }

    /**
     * apply discounts on a shopping bag
     * update prices of products by store discounts
     */
    public void applyDiscountPolicies(Map<Product, Integer> productsBag) {
        updateExpiredDiscounts();   //remove discounts that their time is expired from store.
        discounts.forEach(discount -> discount.applyDiscount(productsBag));
    }



    public boolean removeDiscount(UserSystem userSystem, int discountId) {
        if (validatePermission(userSystem, StorePermission.EDIT_DISCOUNT)) {
            Discount discount = discounts.stream()
                    .filter(discountPolicy -> discountPolicy.getDiscountId() == discountId)
                    .findFirst().orElseThrow(() -> new TradingSystemException(String.format("the discount %d don't exist on store %d", discountId, storeId)));
            return discounts.remove(discount);
        }
        throw new NoOwnerInStoreException(userSystem.getUserName(), this.getStoreId());
    }

    /**
     * get the discounts in store
     *
     * @param user
     * @return
     */
    public List<Discount> getStoreDiscounts(UserSystem user) {
        if (validatePermission(user, StorePermission.EDIT_DISCOUNT)) {  //verify the user is owner of the store
            return new LinkedList<>(discounts);
        }
        throw new NotAdministratorException(String.format("%s not owner and not manager in the store %d", user.getUserName(), storeId));
    }

    /**
     * add new discount to store
     *
     * @param user     adder
     * @param discount to add
     * @return
     */
    public Discount addDiscount(UserSystem user, Discount discount) {
        if (validatePermission(user, StorePermission.EDIT_DISCOUNT)) {  //verify the user is owner of the store
            //discount.setNewId();  //generate new ID for the new discount
            discounts.add(discount);
            log.info("The discount with description: "+discount.getDescription()+"" +
                    " added successfully to the store with id: "+storeId);
            return discount;
        }
        log.info("The discount with id: "+discount.getDiscountId()+" and description: "+discount.getDescription()+"" +
                " failed to add to the store with id: "+storeId);
        throw new NotAdministratorException(String.format("%s not owner and not manager in the store %d", user.getUserName(), storeId));
    }

    public Discount addEditDiscount(UserSystem user, Discount discount) {
        if (discount.getDiscountId() < 0) {
            return addDiscount(user, discount);
        } else {
            return editDiscount(user, discount);
        }
    }

    private Discount editDiscount(UserSystem user, Discount discount) {
        if (validatePermission(user, StorePermission.EDIT_DISCOUNT)) {  //verify the user is owner of the store
            Optional<Boolean> isEdit = discounts.stream()
                    .filter(discountCur -> discountCur.getDiscountId() == discount.getDiscountId())
                    .findFirst().map(discountCur -> discountCur.editDiscount(discount.getDiscountPercentage(),
                            discount.getEndTime(), discount.getDescription(), discount.getDiscountPolicy(), discount.getDiscountType()));
            return isEdit.isPresent() ? discount : null;
        }
        throw new NotAdministratorException(String.format("%s not owner and not manager in the store %d", user.getUserName(), storeId));
    }

    public List<Discount> getDiscountSimple() {
        return discounts.stream()
                .filter(discount -> discount.getDiscountType() != DiscountType.COMPOSE)
                .collect(Collectors.toList());
    }

    /////////////////////////////////////////////////general //////////////////////////////////////////////////



    private int getAppointeeOwnersIndex(String userName) {
        int i = 0;
        for (OwnersAppointee ownersAppointee : appointedOwners) {
            if (ownersAppointee.getAppointeeUser().equals(userName)) {
                return i;
            } else {
                i++;
            }
        }
        return -1; // userName doesn't exist in appointedOwners
    }

    /**
     * checking if the given user has an appointing agreement
     *
     * @param userName - the user to be checked
     * @return true if the user has an appointing agreement
     */
    private boolean checkIfNewOwnerHasAppointingAgreement(String userName) {
        for (AppointingAgreement appointingAgreement : appointingAgreements) {
            if (appointingAgreement.getNewOwner().equals(userName)) {
                return true;
            }
        }
        return false;
    }



}

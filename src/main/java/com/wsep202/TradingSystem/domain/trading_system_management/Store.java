package com.wsep202.TradingSystem.domain.trading_system_management;

import com.wsep202.TradingSystem.domain.exception.*;
import com.wsep202.TradingSystem.domain.trading_system_management.discount.CompositeOperator;
import com.wsep202.TradingSystem.domain.trading_system_management.discount.Discount;
import com.wsep202.TradingSystem.domain.trading_system_management.discount.DiscountType;
import com.wsep202.TradingSystem.domain.trading_system_management.notification.Notification;
import com.wsep202.TradingSystem.domain.trading_system_management.policy_purchase.Day;
import com.wsep202.TradingSystem.domain.trading_system_management.policy_purchase.Purchase;
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
    private Set<Product> products = new LinkedHashSet<>();

    /**
     * The set purchase policy for the store
     */
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Purchase> purchasePolicies;

    /**
     * The set purchase policy for the store
     */
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Discount> discounts;

    /**
     * list of purchases made in the store
     */
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Receipt> receipts;

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
    @OneToMany(cascade = CascadeType.ALL)
    private List<AppointingAgreement> appointingAgreements;

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
                 List<Purchase> purchasePolicies,
                 List<Discount> discounts,
                 List<Receipt> receipts,
                 int rank, String description,
                 List<AppointingAgreement> appointingAgreements) {
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
        purchasePolicies = new ArrayList<>();
        discounts = new ArrayList<>();
        receipts = new LinkedList<>();
        appointingAgreements = new LinkedList<>();
        appointedOwners = new HashSet<>();
        appointedManagers = new HashSet<>();
        products = new LinkedHashSet<>();
        this.storeName = storeName;
        appointedOwners.add(new OwnersAppointee(owner.getUserName()));
        this.rank = 5;
    }

    public Store(UserSystem owner, String storeName, String description) {
        // storeId = generateStoreSn();
        initStore(owner, storeName);
        this.description = description;
    }

   /////////////////////////////////////////products////////////////////////////////////////////////
    /**
     * return a product with the SN reveived
     * @param productId - SN of product to get
     * @return the fit product if exist in the store
     * otherwise exception is thrown
     */
    public Product getProduct(int productId) {
        return products.stream().filter(product -> product.getProductSn() == productId).findFirst()
                .orElseThrow(() -> new ProductDoesntExistException(productId, storeId));
    }

    public boolean removeProductFromStore(UserSystem user, int productSn) {
        if (isOwner(user) || isManagerWithPermission(user.getUserName(), StorePermission.EDIT_PRODUCT)) {  //only owner can remove products from its store
            Set<Product> duplicate = new HashSet<>(products);
            duplicate.stream()
                    .filter(product -> product.getProductSn() == productSn)
                    .forEach(products::remove);

            //is present
            return duplicate.size() > products.size();
        }
        //the user is not an owner of the store so can't remove
        log.info("The product with id: " + productSn + " wasn't removed.\n" +
                "not owner");
        return false;
    }

    /**
     * owner adds a new product to the store
     *
     * @param user    the user wish to add the product
     * @param product the product to add to store
     * @return true for success
     */
    public boolean addNewProduct(UserSystem user, Product product) {
        if (isOwner(user) || isManagerWithPermission(user.getUserName(), StorePermission.EDIT_PRODUCT)) {  //verify the user is owner of the store
            products.add(product);
            log.info("The product: " + product.getName() + " added to the store: " + this.storeName);
            return true;
        }
        log.info("The product: " + product.getName() + " failed to add to the store: " + this.storeName);
        return false;
    }

    /**
     * edit of exist product parameters in the store
     *
     * @param user        the user wish to add the product
     * @param productSn   the unique number of the product
     *                    parameters of the product:
     * @param productName
     * @param category
     * @param amount
     * @param cost
     * @return true for success
     */
    public boolean editProduct(UserSystem user, int productSn, String productName, String category,
                               int amount, double cost) {
        if (isOwner(user) || isManagerWithPermission(user.getUserName(), StorePermission.EDIT_PRODUCT)) {   //the user is owner

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
    public Receipt createReceipt(ShoppingBag bag, String buyerName) {
        return new Receipt(this.storeId, buyerName, bag.getTotalCostOfBag()
                , bag.getProductListFromStore());
    }

    //////////////////////////////////////////////shopping cart /////////////////////////////////////////////
    /**
     * update amount of product in the stock
     * @param productSn
     * @param amount
     * @return true if operation succeeded
     */
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
     *
     * @param bag the products list the user wish to purchase
     * @return true if all products in stock
     * otherwise exception
     */
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
     *
     * @param bag shopping bag
     */
    public void updateStock(ShoppingBag bag) {
        for (Product product : bag.getProductListFromStore().keySet()) {
            int purchasedAmount = bag.getProductAmount(product.getProductSn());
            Product productInStore = getProduct(product.getProductSn());
            editProductAmountInStock(productInStore.getProductSn(), productInStore.getAmount() - purchasedAmount);
        }
    }

    /////////////////////////////////////////////////owner /////////////////////////////////////////////

    /**
     * This method is used to remove an owner from the store
     *
     * @param owner         - the appointing owner
     * @param ownerToRemove - the appointed owner that needs to be removed
     * @return true if succeeded, else false
     */
    public boolean removeOwner(UserSystem owner, UserSystem ownerToRemove) {
        if (isOwner(owner) && isOwner(ownerToRemove)
                && !owner.equals(ownerToRemove) && isAppointedBy(owner, ownerToRemove)) {
            return removeAppointedOwners(ownerToRemove);
        }
        return false;
    }

    public Set<String> getOwnersUsername(){
        return this.appointedOwners.stream()
                .map(OwnersAppointee::getAppointeeUser)
                .collect(Collectors.toSet());
    }

    /**
     * This method is used to remove all the owners and managers that was
     * appointed by ownerToRemove.
     *
     * @param ownerToRemove
     */
    private boolean removeAppointedOwners(UserSystem ownerToRemove) {
        Set<UserSystem> ownersToRemove = new HashSet<>();
        Set<MangerStore> managerToRemove = new HashSet<>();
        ownersToRemove.addAll(findOwnersToRemove(ownerToRemove, ownersToRemove));
        ownersToRemove.add(ownerToRemove);
        managerToRemove.addAll(findManagersToRemove(ownersToRemove, managerToRemove));
        for (UserSystem ownerToDelete : ownersToRemove) {
            appointedOwners.removeIf(appointedOwners-> appointedOwners.getAppointeeUser().equals(ownerToDelete.getUserName()));
            ownerToDelete.removeOwnedStore(this);
            ownerToDelete.newNotification(Notification.builder().content("you are fired").build());

        }
        for (MangerStore manager : managerToRemove) {
            appointedManagers.removeIf(appointedManager -> appointedManager.getAppointeeUser().equals(manager.getAppointedManager().getUserName()));
            manager.removeManagedStore(this);
            manager.getAppointedManager()
                    .newNotification(Notification.builder().content("you are fired").build());
        }
        return true;
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
     * get the actual user of the manager appointed by the ownerUser with managerUserName
     *
     * @param ownerUser     - the appointing owner
     * @param ownerUserName - the appointed manager
     * @return the user belongs to the manager username
     * or exception if not appointed by the owner
     * or null if the owner didn't appointed anyone
     */
    public UserSystem getAppointedOwner(UserSystem ownerUser, String ownerUserName) {
        int ownerUserIndex = getAppointeeOwnersIndex(ownerUser.getUserName());
        if (ownerUserIndex != -1)
            if (isOwner(ownerUser) && !new LinkedList<>(appointedOwners).get(ownerUserIndex).getAppointedUsers().isEmpty()) {
                for (UserSystem user : new LinkedList<>(appointedOwners).get(ownerUserIndex).getAppointedUsers()) {
                    if (user.getUserName().equals(ownerUserName))
                        return user;
                }
            }
        throw new NoOwnerInStoreException(ownerUserName, storeId);
    }

    public List<String> getMySubOwners(String ownerUsername) {
        return appointedOwners.stream()
                .filter(userSystemSetEntry -> userSystemSetEntry.getAppointeeUser().equals(ownerUsername))
                .findFirst()
                .map(OwnersAppointee::getAppointedUsers)
                .orElse(new HashSet<>()).stream()
                .map(UserSystem::getUserName)
                .collect(Collectors.toList());
    }

    /**
     * checks if the given user is owner in this store
     * @param user
     * @return
     */
    public boolean isOwner(UserSystem user) {
        return (getOwnersUsername().stream().anyMatch(curUser -> curUser.equals(user.getUserName())));
    }

    /////////////////////////////////////// manager //////////////////////////////////////////////////

    /**
     * add new manager to the appointed owners of the store
     * @param owner
     * @return true for successful addition
     */
    public MangerStore appointAdditionManager(UserSystem owner, UserSystem newManagerStore) {
        MangerStore newManager = null;
        if(isOwner(owner) || isManagerWithPermission(owner.getUserName(), StorePermission.EDIT_Managers)){
            newManager = new MangerStore(newManagerStore);
            ManagersAppointee managersAppointee = findManagersAppointee(owner.getUserName());
            managersAppointee.addManger(newManager);
        }
        log.info(String.format("The user %s %s new manger %s to the store %d",
                owner.getUserName(),Objects.nonNull(newManager) ? "appointed" : "not appointed" ,newManagerStore.getUserName(), storeId));
        return newManager;
    }

    private ManagersAppointee findManagersAppointee(String appointedManagerUsername){
        return appointedManagers.stream()
                .filter(appointedManager-> appointedManager.getAppointeeUser().equals(appointedManagerUsername))
                .findFirst()
                .orElseGet(()-> {
                    ManagersAppointee managersAppointee = new ManagersAppointee(appointedManagerUsername);
                    appointedManagers.add(managersAppointee);
                    return managersAppointee;
                });
    }

    /**
     * remove user from the store's managers
     * @param ownerStore the removing owner
     * @param user       the removed manager
     * @return true for successful operation
     */
    public boolean removeManager(UserSystem ownerStore, UserSystem user) {
        boolean response = false;
        if ((isOwner(ownerStore) || isManagerWithPermission(ownerStore.getUserName(), StorePermission.EDIT_Managers))
        && removeManagerRecursive(ownerStore, user)) {  //the user is able to remove his appointments
            response = appointedManagers.stream()
                    .filter(appointedManager-> appointedManager.getAppointeeUser().equals(ownerStore.getUserName()))
                    .findFirst()
                    .map(appointedManager -> appointedManager.removeManager(user, storeId))
                    .orElse(false);
        }
        return response;
    }

    public boolean removeManagerRecursive(UserSystem ownerStore, UserSystem user) {
        boolean response = false;
        if (isOwner(ownerStore) || isManagerWithPermission(ownerStore.getUserName(), StorePermission.EDIT_Managers)) {  //the user is able to remove his appointments
            response =  appointedManagers.stream()
                    .filter(appointedManager-> appointedManager.getAppointeeUser().equals(user.getUserName()))
                    .findFirst()
                    .map(appointedManager -> {
                        appointedManager.getAppointedManagers()
                                .forEach(subManager -> removeManagerRecursive(user, subManager.getAppointedManager()));
                        user.removeManagedStore(this);
                        return appointedManagers.remove(appointedManager);
                    })
                    .orElseGet(()->user.removeManagedStore(this));
        }
        return response;
    }

    /**
     * add management options for a manager by an store owner
     * @param ownerStore      - owner of this store
     * @param user            - the manager we wish to edit and add his permissions
     * @param storePermission
     * @return true if the added succeeded
     * otherwise false
     */
    public boolean addPermissionToManager(UserSystem ownerStore, UserSystem user, StorePermission storePermission) {
        boolean permissionAdded = false;
        if(isOwner(ownerStore) || isManagerWithPermission(ownerStore.getUserName(), StorePermission.EDIT_Managers)){
            MangerStore mangerStore = getMangerStore(ownerStore, user);
            permissionAdded = mangerStore.addStorePermission(storePermission);
        }
        log.info(String.format("The permission %s %s by %s to %s in %d",
                storePermission.function, permissionAdded? "added" : "not added", ownerStore.getUserName(), user.getUserName(), storeId));
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
    /**
     * checks if the user is manager in the store
     * @param user
     * @return
     */
    private boolean isManager(UserSystem user) {
        return appointedManagers.stream()
                .anyMatch(entry -> entry.getAppointedManagers().stream()
                        .anyMatch(mangerStore -> mangerStore.isTheUser(user)));
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

    private boolean isManagerWithPermission(String username, StorePermission permission){
        return getManagersStore().stream()
                .anyMatch(mangerStore -> mangerStore.getAppointedManager().getUserName().equals(username) &&
                        mangerStore.getStorePermissions().contains(permission));
    }

    public Set<MangerStore> getManagersStore(){
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
     * @param user
     * @param purchase
     * @return
     */
    public Purchase addPurchasePolicy(UserSystem user, Purchase purchase) {
        if (isOwner(user) || isManagerWithPermission(user.getUserName(), StorePermission.EDIT_PURCHASE_POLICY)) {  //verify the user is owner of the store
            //purchase.setNewId();  //generate new ID for the new discount
            purchasePolicies.add(purchase);
            return purchase;
        }
        throw new NotAdministratorException(String.format("%s not owner and not manager in the store %d", user.getUserName(), storeId));
    }

    /////////////////////////////////////////////////////////discounts ///////////////////////////////////////

    /**
     * apply discounts on a shopping bag
     * update prices of products by store discounts
     */
    public void applyDiscountPolicies(Map<Product, Integer> productsBag) {
        updateExpiredDiscounts();   //remove discounts that their time is expired from store.
        discounts.forEach(discount -> discount.applyDiscount(productsBag));
    }

    /**
     * remove discounts that expired from store
     */
    private void updateExpiredDiscounts() {
        List<Discount> discounts = this.getDiscounts().stream()
                .filter(Discount::isExpired)
                .collect(Collectors.toList());
        this.discounts.removeAll(discounts);

    }

    public boolean removeDiscount(UserSystem userSystem, int discountId) {
        if (isOwner(userSystem) || isManagerWithPermission(userSystem.getUserName(), StorePermission.EDIT_DISCOUNT)) {
            Discount discount = discounts.stream()
                    .filter(discountPolicy -> discountPolicy.getDiscountId() == discountId)
                    .findFirst().orElseThrow(() -> new TradingSystemException(String.format("the discount %d don't exist on store %d", discountId, storeId)));
            return discounts.remove(discount);
        }
        throw new NoOwnerInStoreException(userSystem.getUserName(), this.getStoreId());
    }
    /**
     * get the discounts in store
     * @param user
     * @return
     */
    public List<Discount> getStoreDiscounts(UserSystem user) {
        if (isOwner(user) || isManagerWithPermission(user.getUserName(), StorePermission.EDIT_DISCOUNT)) {  //verify the user is owner of the store
            return discounts;
        }
        throw new NotAdministratorException(String.format("%s not owner and not manager in the store %d", user.getUserName(), storeId));
    }

    /**
     * add new discount to store
     * @param user     adder
     * @param discount to add
     * @return
     */
    public Discount addDiscount(UserSystem user, Discount discount) {
        if (isOwner(user) || isManagerWithPermission(user.getUserName(), StorePermission.EDIT_DISCOUNT)) {  //verify the user is owner of the store
            //discount.setNewId();  //generate new ID for the new discount
            discounts.add(discount);
            return discount;
        }
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
        if (isOwner(user) || isManagerWithPermission(user.getUserName(), StorePermission.EDIT_DISCOUNT)) {  //verify the user is owner of the store
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


    //////////////////////////////////////////////////TODO need to fix all what in down  ////////////////////

    /**
     * This method is used to check if owner2 was appointed by owner1
     *
     * @return true if owner2 was appointed by owner1, else false
     */
    private boolean isAppointedBy(UserSystem owner1, UserSystem owner2) {
        int owner1Index = getAppointeeOwnersIndex(owner1.getUserName());
        if (owner1Index != -1)
            if (new LinkedList<>(appointedOwners).get(owner1Index).getAppointedUsers().size() > 0 && new LinkedList<>(appointedOwners).get(owner1Index).getAppointedUsers().contains(owner2))
                return true;
        return false;
    }

    /**
     * add new owner to the appointed owners of the store
     * @param owner
     * @param willBeOwner
     * @return true if owner added successfully
     */
    public boolean addOwner(UserSystem owner, UserSystem willBeOwner) {
        boolean appointed = false;
        //TODO BIG BLAGN
        if (isOwner(owner) && !isOwner(willBeOwner)) {
            if (!checkIfAppointeeExists(owner.getUserName())) {
                appointedOwners.add(new OwnersAppointee(owner.getUserName()));
            }

            int ownerIndex = getAppointeeOwnersIndex(owner.getUserName());
            if (ownerIndex != -1) {
                new LinkedList<>(appointedOwners).get(ownerIndex).getAppointedUsers().add(willBeOwner);
            }
            appointed = true;
            log.info("The user: " + willBeOwner.getUserName() + " added as appointed owner by the owner: " + owner.getUserName() + "" +
                    " for the store:" + this.storeName);
        }
        if (!appointed) {
            log.info("The user: " + willBeOwner.getUserName() + " couldn't be appointed as owner by: " + owner.getUserName() + " " +
                    "for the store: " + this.storeName);
        }
        return appointed;
    }

    private boolean checkIfAppointeeExists(String userName) {
        for (OwnersAppointee ownersAppointee : appointedOwners) {
            if (ownersAppointee.getAppointeeUser().equals(userName)) {
                return true;
            }
        }
        return false;
    }

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

    private Purchase editPurchase(UserSystem user, Purchase purchase, Set<String> countriesPermitted,
                                  Set<Day> storeWorkDays, int min, int max, int productId,
                                  CompositeOperator compositeOperator, List<Purchase> composedPurchasePolicies) {
//        if (isOwner(user) || managerCanEditPurchasePolicy(user.getUserName())) {  //verify the user is owner of the store
//            Optional<Boolean> isEdit = purchasePolicies.stream()
//                    .filter(purchaseCur -> purchaseCur.getPurchaseId() == purchase.getPurchaseId())
//                    .findFirst().map(purchaseCur -> purchaseCur.edit(countriesPermitted,storeWorkDays,
//                            min,max,productId,compositeOperator, composedPurchasePolicies));
//            return isEdit.isPresent() ? purchase : null;
//        }
        throw new NotAdministratorException(String.format("%s not owner and not manager in the store %d", user.getUserName(), storeId));
    }

    public Purchase addEditPurchase(UserSystem user, Purchase purchase, Set<String> countriesPermitted,
                                    Set<Day> storeWorkDays, int min, int max, int productId,
                                    CompositeOperator compositeOperator, List<Purchase> composedPurchasePolicies) {
        if (purchase.getPurchaseId() < 0) {
            return addPurchasePolicy(user, purchase);
        } else {
            return editPurchase(user, purchase, countriesPermitted, storeWorkDays,
                    min, max, productId, compositeOperator, composedPurchasePolicies);
        }
    }

    /**
     * apply purchase on a shopping bag
     */
    public void isApprovedPurchasePolicies(Map<Product, Integer> productsBag, BillingAddress userAddress)
            throws PurchasePolicyException {
        updateExpiredDiscounts();
        for (Purchase purchase : this.getPurchasePolicies()) {  //apply discounts on shoppingBag
            purchase.isApproved(productsBag, userAddress);
        }
    }

    /**
     * creating a new appointing agreement for the given new owner
     *
     * @param owner    - the user that wants to appoint new owner as an owner
     * @param newOwner - the user that owner wants to appoint as an owner
     * @return true if an appointing agreement was created successfully
     */
    public boolean createNewAppointingAgreement(UserSystem owner, UserSystem newOwner) {
        boolean response = false;
        if (isOwner(owner) && !isOwner(newOwner) && !checkIfNewOwnerHasAppointingAgreement(newOwner.getUserName())) {
            appointingAgreements.add(new AppointingAgreement(newOwner.getUserName(),
                    owner.getUserName(), getOwnersUsername()));
            log.info(String.format("A new appointing agreement was created for the user: %s by the owner: %s for the store: %s",
                    newOwner.getUserName(), owner.getUserName(), storeName));
            response = true;
        }
        log.info(String.format("Can't create a new appointing agreement for the user: %s  by the owner: %s  for the store: %s",
                newOwner.getUserName() ,owner.getUserName(), storeName));
        return response;
    }

}

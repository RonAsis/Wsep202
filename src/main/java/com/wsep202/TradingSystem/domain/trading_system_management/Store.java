package com.wsep202.TradingSystem.domain.trading_system_management;

import com.wsep202.TradingSystem.domain.exception.*;
import com.wsep202.TradingSystem.domain.trading_system_management.discount.*;
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
    @GeneratedValue(strategy= GenerationType.AUTO)
    private int storeId;        //unique identifier for the store

    private String storeName;

    //map to find all the owners as value appointed the owner who is in the fit key
    @Builder.Default
    @OneToMany(cascade = CascadeType.ALL)
    private List<OwnersAppointee> appointedOwners = new ArrayList<>();

    //map  that holds users as key and their appointed managers as value
    @Builder.Default
    @OneToMany(cascade = CascadeType.ALL)
    private List<ManagersAppointee> appointedManagers = new ArrayList<>();

    //The products that the store holds in it
    @Builder.Default
    @OneToMany(cascade = CascadeType.ALL)
    Set<Product> products = new HashSet<>();

    //The set purchase policy for the store
    @OneToMany(cascade = CascadeType.ALL)
    private List<Purchase> purchasePolicies;

    //The set purchase policy for the store
    @OneToMany(cascade = CascadeType.ALL)
    private List<Discount> discounts;

    //owners of the store
    @OneToMany(cascade = CascadeType.ALL)
    private Set<UserSystem> owners;

    //managers in the store
    @OneToMany(cascade = CascadeType.ALL)
    private Set<MangerStore> managers;

    //list of purchases made in the store
    @OneToMany(cascade = CascadeType.ALL)
    private List<Receipt> receipts;

    //the store rank
    private int rank;

    //description
    private String description;

    //list of appointing agreements of owners in the store
    @OneToMany(cascade = CascadeType.ALL)
    private List<AppointingAgreement> appointingAgreements;

    public Store(UserSystem owner, String storeName) {
        initStore(owner, storeName);
    }

    private void initStore(UserSystem owner, String storeName) {
        this.purchasePolicies = new ArrayList<>();
        this.discounts = new ArrayList<>();
        receipts = new LinkedList<>();
        appointingAgreements = new LinkedList<>();
        appointedOwners = new ArrayList<>();
        appointedManagers = new ArrayList<>();
        products = new HashSet<>();
        owners = new HashSet<>();
        managers = new HashSet<>();
        this.storeName = storeName;
        owners.add(owner);
        this.rank = 5;
    }

    public Store(UserSystem owner, String storeName, String description) {
        initStore(owner, storeName);
        this.description = description;
    }

    /**
     * creating a new appointing agreement for the given new owner
     * @param owner - the user that wants to appoint new owner as an owner
     * @param newOwner - the user that owner wants to appoint as an owner
     * @return true if an appointing agreement was created successfully
     */
    public boolean createNewAppointingAgreement(UserSystem owner, UserSystem newOwner) {
        if (isOwner(owner) && !isOwner(newOwner)) {
            if (!checkIfAppointeeExists(owner.getUserName()) && !checkIfNewOwnerHasAppointingAgreement(newOwner.getUserName())) {
                this.appointingAgreements.add(new AppointingAgreement(newOwner.getUserName(),
                        owner.getUserName(), this.owners));
                log.info("A new appointing agreement was created for the user: " + newOwner.getUserName() +
                        " by the owner: " + owner.getUserName() +
                        " for the store: " + this.storeName);
                return true;
            }
        }
        log.info("Can't create a new appointing agreement for the user: " + newOwner.getUserName() +
                " by the owner: " + owner.getUserName() +
                " for the store: " + this.storeName);
        return false;
    }

    /**
     * checking if the given user has an appointing agreement
     * @param userName - the user to be checked
     * @return true if the user has an appointing agreement
     */
    private boolean checkIfNewOwnerHasAppointingAgreement(String userName) {
        for(AppointingAgreement appointingAgreement : appointingAgreements){
            if(appointingAgreement.getNewOwner().equals(userName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * add new owner to the appointed owners of the store
     *
     * @param owner
     * @param willBeOwner
     * @return true if owner added successfully
     */
    private boolean appointAdditionOwner(UserSystem owner, UserSystem willBeOwner) {
        boolean appointed = false;

        if (isOwner(owner) && !isOwner(willBeOwner)) {
            if(!checkIfAppointeeExists(owner.getUserName())) {
                appointedOwners.add(new OwnersAppointee(owner.getUserName(), new HashSet<>()));
            }
            int ownerIndex = getAppointeeOwnersIndex(owner.getUserName());
            if(ownerIndex !=-1)
                appointedOwners.get(ownerIndex).getAppointedUsers().add(willBeOwner);
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
        for(OwnersAppointee ownersAppointee : appointedOwners){
            if(ownersAppointee.getAppointeeUser().equals(userName)) {
                return true;
            }
        }
        return false;
    }

    private int getAppointeeOwnersIndex(String userName) {
        int i=0;
        for(OwnersAppointee ownersAppointee : appointedOwners){
            if(ownersAppointee.getAppointeeUser().equals(userName)) {
                return i;
            }
            else {
                i++;
            }
        }
        return -1; // userName doesn't exist in appointedOwners
    }

    private int getAppointeeManagersIndex(String userName) {
        int i=0;
        for(ManagersAppointee managersAppointee : appointedManagers){
            if(managersAppointee.getAppointeeUser().equals(userName)) {
                return i;
            }
            else {
                i++;
            }
        }
        return -1; // userName doesn't exist in appointedOwners
    }

    /**
     * add a new owner to be owner of this store
     *
     * @param ownerStore
     * @param newOwnerUser
     * @return true if succeeded to add owner for the store
     */
    public boolean addOwner(UserSystem ownerStore, UserSystem newOwnerUser) {
        //appoint new user to be owner
        boolean isAppointedToOwner = appointAdditionOwner(ownerStore, newOwnerUser);
        if (isAppointedToOwner) { //appointing succeeded
            owners.add(newOwnerUser);    //add the new owner to the owners list
            log.info("The user: " + newOwnerUser.getUserName() + " added as an owner to the store: " + this.storeName +
                    " by: " + ownerStore.getUserName());
            return true;
        }
        log.info("The user: " + newOwnerUser.getUserName() + " couldn't be added as an owner to the " +
                "store: " + this.storeName +
                " by: " + ownerStore.getUserName());
        return false;
    }

    /**
     * add new manager to the appointed owners of the store
     *
     * @param owner
     * @param mangerStore
     * @return true for successful addition
     */
    private boolean appointAdditionManager(UserSystem owner, MangerStore mangerStore) {
        boolean appointed = false;
        //manager is not owner or manager in this store
        if (!(managersContains(mangerStore) || ownersContains(mangerStore.getAppointedManager()))) {
            appointedManagers.add(new ManagersAppointee(owner.getUserName(), new HashSet<>()));
            int ownerIndex = getAppointeeManagersIndex(owner.getUserName());
            if(ownerIndex != -1)
                appointedManagers.get(ownerIndex).getAppointedManagers().add(mangerStore);
            appointed = true;
            log.info("The user: " + mangerStore.getAppointedManager().getUserName() + " added as appointed manager by the owner: " + owner.getUserName() + "" +
                    " for the store:" + this.storeName);
        }
        if (!appointed) {
            log.info("The user: " + mangerStore.getAppointedManager().getUserName() + " couldn't be appointed as manager" +
                    " by: " + owner.getUserName() + " " +
                    "for the store: " + this.storeName);
        }
        return appointed;
    }

    /**
     * add a manager to be manager of this store
     *
     * @param ownerStore
     * @param newManagerUser
     * @return true for success otherwise false
     */
    public MangerStore addManager(UserSystem ownerStore, UserSystem newManagerUser) {
        if (isOwner(ownerStore)) {    //check if the user appointing is an owner so can appoint
            MangerStore newManager = new MangerStore(newManagerUser);
            boolean isAppointedToManager = appointAdditionManager(ownerStore, newManager);
            if (isAppointedToManager) {       //the manager appointed to manage the store successfully
                managers.add(newManager);
                log.info("The user: " + newManagerUser.getUserName() + " added as a manager to the store: " + this.storeName +
                        " by: " + ownerStore.getUserName());
                return newManager;
            }
        }
        //the addition of manager failed
        log.info("The user: " + newManagerUser.getUserName() + " couldn't be added as a manager to the store: "
                + this.storeName +
                " by: " + ownerStore.getUserName());
        return null;
    }

    private boolean removeAppointManager(UserSystem owner, MangerStore mangerStore) {
        boolean appointed = false;
        int ownerIndex = getAppointeeManagersIndex(owner.getUserName());
        if(ownerIndex == -1)
            return false;
        if ((managersContains(mangerStore) && appointedManagers.get(ownerIndex).getAppointedManagers()
                .stream().anyMatch(man -> man.equals(mangerStore)))) {
            appointedManagers.get(ownerIndex).getAppointedManagers().remove(mangerStore);
            if (appointedManagers.get(ownerIndex).getAppointedManagers().size() == 0) {
                appointedManagers.remove(owner);
            }
            appointed = true;
        }
        if (appointed) {
            log.info("The owner: " + owner.getUserName() + " removed: " + mangerStore.getAppointedManager().getUserName() + "" +
                    " from appointed managers of the store: " + this.storeName);
        } else {
            log.info("The owner: " + owner.getUserName() + "failed to remove: " + mangerStore.getAppointedManager().getUserName() + "" +
                    " from appointed managers of the store: " + this.storeName);
        }
        return appointed;
    }

    /**
     * remove user from the store's managers
     *
     * @param ownerStore the removing owner
     * @param user       the removed manager
     * @return true for successful operation
     */
    public boolean removeManager(UserSystem ownerStore, UserSystem user) {
        if (isOwner(ownerStore)) {  //the user is able to remove his appointments
            //get the manager to remove
            MangerStore manager = getManagerObject(ownerStore, user.getUserName());
            //remove from appointed managers Set
            boolean isRemovedFromAppointedManagers = removeAppointManager(ownerStore, manager);
            if (isRemovedFromAppointedManagers) {
                boolean removed = managers.remove(manager);   //remove from managers list as well
                if (removed) {
                    log.info(ownerStore.getUserName() + " removed: " + user.getUserName() + "" +
                            " from the store: " + storeName + " managers successfully");
                    return true;
                }
                log.info(ownerStore.getUserName() + " failed to remove: " + user.getUserName() + "" +
                        " from the store: " + storeName);
            }
        }
        //couldn't remove manager
        return false;
    }

    /**
     * This method is used to remove an owner from the store
     * @param owner - the appointing owner
     * @param ownerToRemove - the appointed owner that needs to be removed
     * @return true if succeeded, else false
     */
    public boolean removeOwner(UserSystem owner, UserSystem ownerToRemove){
        if (isOwner(owner) && isOwner(ownerToRemove)
                && !owner.equals(ownerToRemove) && isAppointedBy(owner, ownerToRemove)){
            removeAppointedOwners(ownerToRemove);
            return true;
        }
        return false;
    }

    /**
     * This method is used to remove all the owners and managers that was
     * appointed by ownerToRemove.
     * @param ownerToRemove
     */
    private void removeAppointedOwners(UserSystem ownerToRemove){
        Set<UserSystem> ownersToRemove = new HashSet<>();
        Set<MangerStore> managerToRemove = new HashSet<>();
        ownersToRemove.addAll(findOwnersToRemove(ownerToRemove,ownersToRemove));
        ownersToRemove.add(ownerToRemove);
        managerToRemove.addAll(findManagersToRemove(ownersToRemove,managerToRemove));
        for (UserSystem ownerToDelete: ownersToRemove) {
            appointedOwners.remove(ownerToDelete);
            owners.remove(ownerToDelete);
            appointedManagers.remove(ownerToDelete);
            ownerToDelete.removeOwnedStore(this);
        }
        for (MangerStore manager: managerToRemove) {
            managers.remove(manager);
            manager.removeManagedStore(this);
        }
    }

    /**
     * This method is used to find all the owners that needs to be removed from the store
     * @return set of all the owners that needs to be removed
     */
    private Set<UserSystem> findOwnersToRemove(UserSystem ownerToRemove, Set<UserSystem> ownersToRemove){
        int ownerToRemoveIndex = getAppointeeOwnersIndex(ownerToRemove.getUserName());
        if(ownerToRemoveIndex !=-1)
            if (appointedOwners.get(ownerToRemoveIndex).getAppointedUsers()!=null && appointedOwners.get(ownerToRemoveIndex).getAppointedUsers().size() > 0){
                ownersToRemove.addAll(appointedOwners.get(ownerToRemoveIndex).getAppointedUsers());
                for (UserSystem user:appointedOwners.get(ownerToRemoveIndex).getAppointedUsers()) {
                    findOwnersToRemove(user, ownersToRemove);
                }
            }
        return ownersToRemove;
    }

    /**
     * This method is used to find all the managers that needs to be removed from the store
     * @return set of all the managers that needs to be removed
     */
    private Set<MangerStore> findManagersToRemove(Set<UserSystem> ownersToRemove, Set<MangerStore> managerToRemove) {
        for (UserSystem owner: ownersToRemove) {
            int ownerIndex = getAppointeeManagersIndex(owner.getUserName());
            if(ownerIndex != -1)
                if (appointedManagers.get(ownerIndex).getAppointedManagers() != null && !appointedManagers.get(ownerIndex).getAppointedManagers().isEmpty())
                    managerToRemove.addAll(appointedManagers.get(ownerIndex).getAppointedManagers());
        }
        return managerToRemove;
    }

    /**
     * This method is used to check if owner2 was appointed by owner1
     * @return true if owner2 was appointed by owner1, else false
     */
    private boolean isAppointedBy(UserSystem owner1, UserSystem owner2){
        int owner1Index = getAppointeeOwnersIndex(owner1.getUserName());
        if(owner1Index !=-1)
            if (appointedOwners.get(owner1Index).getAppointedUsers().size() > 0 && appointedOwners.get(owner1Index).getAppointedUsers().contains(owner2))
                return true;
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
        if (isOwner(user) || managerCanEdit(user.getUserName())) {  //verify the user is owner of the store
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
        if (isOwner(user) || managerCanEdit(user.getUserName())) {   //the user is owner

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


    public boolean removeProductFromStore(UserSystem user, int productSn) {
        if (isOwner(user) || managerCanEdit(user.getUserName())) {  //only owner can remove products from its store
            Set<Product> duplicate = new HashSet<>(products);
            duplicate.stream()
                    .filter(product -> product.getProductSn()==productSn)
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
     * add management options for a manager by an store owner
     *
     * @param ownerStore      - owner of this store
     * @param user            - the manager we wish to edit and add his permissions
     * @param storePermission
     * @return true if the added succeeded
     * otherwise false
     */
    public boolean addPermissionToManager(UserSystem ownerStore, UserSystem user, StorePermission storePermission) {
        if (!isOwner(ownerStore)) {    //verify the editor is owner in the store
            log.error("couldn't add permission:" + storePermission.function + " " +
                    "to: " + user.getUserName() + " by " + ownerStore.getUserName());
            return false;
        }
        //holds the manager appointed by the received owner to be manager in this store
        MangerStore manager = getManagerObject(ownerStore, user.getUserName());
        boolean added = manager.addStorePermission(storePermission);    //add the permission
        if (!added) {
            log.info("couldn't add permission:" + storePermission.function + " " +
                    "to: " + user.getUserName() + " by " + ownerStore.getUserName());
            return false;
        }
        log.info("Permission:" + storePermission.function + " added" +
                "to: " + user.getUserName() + " by " + ownerStore.getUserName());
        return true;
    }

    /**
     * @param ownerStore
     * @param user
     * @param storePermission
     * @return
     */
    public boolean removePermissionFromManager(UserSystem ownerStore, UserSystem user, StorePermission storePermission) {
        if (!isOwner(ownerStore)) {    //verify the editor is owner in the store
            log.info("couldn't add permission:" + storePermission.function + " " +
                    "to: " + user.getUserName() + " by " + ownerStore.getUserName());
            return false;
        }
        //holds the manager appointed by the received owner to be manager in this store
        MangerStore manager = getManagerObject(ownerStore, user.getUserName());
        boolean removed = manager.removeStorePermission(storePermission);    //add the permission
        if (!removed) {
            log.info("couldn't remove permission:" + storePermission.function + " " +
                    "to: " + user.getUserName() + " by " + ownerStore.getUserName());
            return false;
        }
        log.info("Permission:" + storePermission.function + " removed" +
                "to: " + user.getUserName() + " by " + ownerStore.getUserName());
        return true;
    }


    /**
     * manager can see the store purchase history
     *
     * @param user
     * @return
     */
    public List<Receipt> managerViewReceipts(UserSystem user) {
        if (isManager(user)) {
            log.info(user.getUserName() + " manager viewed the store purchase history.");
            return receipts;
        } else {
            log.error(user.getUserName() + " cannot see the purchase history, he is not manager in the store.");
            throw new NoManagerInStoreException(user.getUserName(), storeId);
        }
    }

    public boolean addReceipt(Receipt receipt) {
        return this.receipts.add(receipt);
    }

    /**
     * owner can see the store purchase history
     *
     * @param user
     * @return
     */
    public List<Receipt> ownerViewReceipts(UserSystem user) {
        if (isOwner(user)) {
            log.info(user.getUserName() + " owner viewed the store purchase history.");
            return receipts;
        } else {
            log.error(user.getUserName() + " cannot see the purchase history, he is not owner in the store.");
            throw new NoManagerInStoreException(user.getUserName(), storeId);
        }
    }

    /**
     * shows the store information
     *
     * @return
     */
    public String showStoreInfo() {
        log.info("store info is presented");
        return "The store " + storeName + " has ID: " + getStoreId();
    }

    /**
     * shows the store information
     *
     * @return
     */
    public String showProductsInStoreInfo() {
        log.info("store's products info is presented");
        String productsInStoreInfo = "";
        for (Product product : this.products) {
            productsInStoreInfo += product.showProductInfo() + "\n";
        }
        return productsInStoreInfo;
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
        if(ownerUserIndex != -1)
            if(appointedManagers.get(ownerUserIndex).getAppointedManagers() == null)
                return null;
            else{
                return appointedManagers.get(ownerUserIndex).getAppointedManagers().stream().
                        filter(mangerStore -> mangerStore.isTheUser(managerUserName))
                        .findFirst().orElseThrow(() -> new NoManagerInStoreException(managerUserName, storeId)).getAppointedManager();
            }
        else return null;
    }

    /**
     * get the actual user of the manager appointed by the ownerUser with managerUserName
     *
     * @param ownerUser       - the appointing owner
     * @param ownerUserName - the appointed manager
     * @return the user belongs to the manager username
     * or exception if not appointed by the owner
     * or null if the owner didn't appointed anyone
     */
    public UserSystem getAppointedOwner(UserSystem ownerUser, String ownerUserName) {
        int ownerUserIndex = getAppointeeOwnersIndex(ownerUser.getUserName());
        if(ownerUserIndex !=-1)
            if (isOwner(ownerUser) && !appointedOwners.get(ownerUserIndex).getAppointedUsers().isEmpty()){
                for (UserSystem user:appointedOwners.get(ownerUserIndex).getAppointedUsers()) {
                    if (user.getUserName().equals(ownerUserName))
                        return user;
                }
            }
        throw new NoOwnerInStoreException(ownerUserName,storeId);
    }

    /**
     * get the manager object of the manager managerUserName appointed by ownerUser
     *
     * @param ownerUser       - the appointing owner
     * @param managerUserName - the appointed manager
     * @return the manager object which wraps the manager with manager username
     */
    public MangerStore getManagerObject(UserSystem ownerUser, String managerUserName) {
        int ownerUserIndex = getAppointeeManagersIndex(ownerUser.getUserName());
        if(ownerUserIndex != -1) {
            if (appointedManagers.get(ownerUserIndex).getAppointedManagers() == null)
                return null;
            else {
                return appointedManagers.get(ownerUserIndex).getAppointedManagers().stream().
                        filter(mangerStore -> mangerStore.isTheUser(managerUserName))
                        .findFirst().orElseThrow(() -> new NoManagerInStoreException(managerUserName, storeId));
            }
        }
        else return null;
    }


    /**
     * return a product with the SN reveived
     *
     * @param productId - SN of product to get
     * @return the fit product if exist in the store
     * otherwise exception is thrown
     */
    public Product getProduct(int productId) {
        return products.stream().filter(product -> product.getProductSn() == productId).findFirst()
                .orElseThrow(() -> new ProductDoesntExistException(productId, storeId));
    }

    private boolean ownersContains(UserSystem user) {
        return (owners.stream().anyMatch(curUser -> curUser.getUserName().equals(user.getUserName())));
    }

    private boolean managersContains(MangerStore user) {
        return (managers.stream().anyMatch(curUser -> curUser.getAppointedManager().getUserName().equals(user.getAppointedManager().getUserName())));
    }

    /**
     * checks if the user is manager in the store
     *
     * @param user
     * @return
     */
    private boolean isManager(UserSystem user) {
        return appointedManagers.stream()
                .anyMatch(entry -> entry.getAppointedManagers().stream()
                        .anyMatch(mangerStore -> mangerStore.isTheUser(user)));
    }

    /**
     * checks if the given user is owner in this store
     *
     * @param user
     * @return
     */
    public boolean isOwner(UserSystem user) {
        return ownersContains(user);
    }


    /**
     * search a product by a given productName
     *
     * @param productName - the name to search
     * @return - the product who has name equals to productName
     */
    public Set<Product> searchProductByName(String productName) {
        log.info("search product by name: " + productName + " has been performed");
        return products.stream()
                .filter(product -> product.getName().equals(productName))
                .collect(Collectors.toSet());
    }

    /**
     * search a product by a given productCategory
     *
     * @param productCategory - the category to search
     * @return - the product who has category equals to productCategory
     */
    public Set<Product> searchProductByCategory(ProductCategory productCategory) {
        log.info("search product by category: " + productCategory.category + " has been performed");
        return products.stream()
                .filter(product -> product.getCategory().category.equals(productCategory.category))
                .collect(Collectors.toSet());
    }

    /**
     * search a product by a given list of key words that contained in product name
     *
     * @param keyWords - the keyWords to search in the name
     * @return - the product who has keyWords contained in product name
     */
    public Set<Product> searchProductByKeyWords(List<String> keyWords) {
        log.info("search product by keyword: " + keyWords + " has been performed");
        return products.stream()
                .map(product -> product.productNameThatContainsKeyWords(keyWords)).filter(product -> product != null)
                .collect(Collectors.toSet());
    }

    /**
     * checks if all products in shopping bag are in stock of the store
     *
     * @param bag the products list the user wish to purchase
     * @return true if all products in stock
     * otherwise exception
     */
    @Synchronized("stockLock")
    public boolean isAllInStock(ShoppingBag bag) throws TradingSystemException{
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
    @Synchronized("stockLock")
    public void updateStock(ShoppingBag bag) {
        for (Product product : bag.getProductListFromStore().keySet()) {
            int purchasedAmount = bag.getProductAmount(product.getProductSn());
            Product productInStore = getProduct(product.getProductSn());
            editProductAmountInStock(productInStore.getProductSn(), productInStore.getAmount() - purchasedAmount);
        }
    }

    /**
     * The store creates receipt for the products purchased in the bag.
     *
     * @param bag
     * @param buyerName
     * @return
     */
    public Receipt createReceipt(ShoppingBag bag, String buyerName) {
        return new Receipt(this.storeId, buyerName, bag.getTotalCostOfBag()
                , bag.getProductListFromStore());
    }

    /**
     * update amount of product in the stock
     *
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
     * apply discounts on a shopping bag
     * update prices of products by store discounts
     */
    public void applyDiscountPolicies(Map<Product, Integer> productsBag) {
        updateExpiredDiscounts();   //remove discounts that their time is expired from store.
        for (Discount discount : this.getDiscounts()) {  //apply discounts on shoppingBag
            discount.applyDiscount(productsBag);
        }
    }

    /**
     * apply purchase on a shopping bag
     */
    public void isApprovedPurchasePolicies(Map<Product, Integer> productsBag, BillingAddress userAddress) {
        updateExpiredDiscounts();
        for (Purchase purchase : this.getPurchasePolicies()) {  //apply discounts on shoppingBag
            purchase.isApproved(productsBag, userAddress);
        }
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

    /**
     * set all the products in the received list with the received discount
     *
     * @param discount
     * @param owner    the owner of the store that gets the discount
     * @return true for success
     */
    public boolean addDiscountForProduct(UserSystem owner, Discount discount) {
        if (owner == null) {
            return false;
        }
        if (isOwner(owner)) {
            return this.discounts.add(discount);  //add the discount to store
        }
        //this is not an owner of the store
        log.error("The received user: " + owner.getUserName() + "is not owner");
        throw new NoOwnerInStoreException(owner.getUserName(), storeId);
    }

    /**
     * get the permissions of a manager
     * @param userSystem
     * @return
     */
    public List<String> getOperationsCanDo(UserSystem userSystem) {
        return managers.stream()
                .filter(mangerStore -> mangerStore.isTheUser(userSystem))
                .findFirst()
                .map(mangerStore -> StorePermission.getStringPermissions(mangerStore.getStorePermissions()))
                .orElse(new ArrayList<>());
    }

    /**
     * get discount in the store that has the received id
     *
     * @param id unique discount id
     * @return
     */
    public Discount getDiscountPolicyById(int id) {
        Optional<Discount> discountPolicy = this.discounts.stream().
                filter(discountPolicy1 -> discountPolicy1.getDiscountId() == id).findFirst();
        if (discountPolicy.isPresent()) {
            return discountPolicy.get();
        }
        //there is no discount with the requested id
        throw new DiscountPolicyNoSuchIDException(id, this.getStoreName());
    }

    public boolean removeDiscount(UserSystem userSystem, int discountId) {
        if (isOwner(userSystem) || isManager(userSystem)) {
            Discount discount = discounts.stream()
                    .filter(discountPolicy -> discountPolicy.getDiscountId() == discountId)
                    .findFirst().orElseThrow(() -> new TradingSystemException(String.format("the discount %d don't exist on store %d", discountId, storeId)));
            return discounts.remove(discount);
        }
        throw new NoOwnerInStoreException(userSystem.getUserName(), this.getStoreId());
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

    public List<MangerStore> getMySubMangers(String ownerUsername) {
        return appointedManagers.stream()
                .filter(userSystemSetEntry -> userSystemSetEntry.getAppointeeUser().equals(ownerUsername))
                .findFirst()
                .map(ManagersAppointee::getAppointedManagers)
                .orElse(new HashSet<>()).stream()
                .collect(Collectors.toList());
    }

    public boolean removePermission(UserSystem ownerStore, UserSystem user, StorePermission storePermission) {
        if (!isOwner(ownerStore)) {    //verify the editor is owner in the store
            log.error("couldn't remove permission:" + storePermission.function + " " +
                    "to: " + user.getUserName() + " by " + ownerStore.getUserName());
            return false;
        }
        return managers.stream().filter(mangerStore -> mangerStore.getAppointedManager().getUserName().equals(user.getUserName()))
                .findFirst().map(mangerStore -> mangerStore.removeStorePermission(storePermission))
                .orElse(false);
    }

    public Set<StorePermission> getPermissionOfManager(UserSystem ownerStore, UserSystem user) {
        if (!isOwner(ownerStore)) {    //verify the editor is owner in the store
            log.error("couldn't get permissions of " + user.getUserName() + " by " + ownerStore.getUserName());
            return null;
        } else {
            return managers.stream().filter(mangerStore -> mangerStore.getAppointedManager().getUserName().equals(user.getUserName()))
                    .findFirst().map(MangerStore::getStorePermissions)
                    .orElse(null);
        }
    }

    public Set<StorePermission> getPermissionCantDo(UserSystem ownerStore, UserSystem user) {
        if (!isOwner(ownerStore)) {    //verify the editor is owner in the store
            log.error("couldn't get permissions of " + user.getUserName() + " by " + ownerStore.getUserName());
            return null;
        } else {
            Set<StorePermission> storePermissions = managers.stream().filter(mangerStore -> mangerStore.getAppointedManager().getUserName().equals(user.getUserName()))
                    .findFirst().map(MangerStore::getStorePermissions)
                    .orElse(new HashSet<>());
            return Arrays.stream(StorePermission.values())
                    .filter(storePermission -> !storePermissions.contains(storePermission))
                    .collect(Collectors.toSet());
        }
    }

    public boolean managerCanEdit(String userName) {
        return managers.stream()
                .anyMatch(mangerStore -> mangerStore.getAppointedManager().getUserName().equals(userName) && mangerStore.canEdit());

    }

    /**
     * get the discounts in store
     * @param user
     * @return
     */
    public List<Discount> getStoreDiscounts(UserSystem user) {
        if (isOwner(user) || managerCanEdit(user.getUserName())) {  //verify the user is owner of the store
            return discounts;
        }
        throw new NotAdministratorException(String.format("%s not owner and not manager in the store %d", user.getUserName(), storeId));
    }

    /**
     * add new discount to store
     * @param user adder
     * @param discount to add
     * @return
     */
    public Discount addDiscount(UserSystem user, Discount discount) {
        if (isOwner(user) || managerCanEdit(user.getUserName())) {  //verify the user is owner of the store
            //discount.setNewId();  //generate new ID for the new discount
            discounts.add(discount);
            return discount;
        }
        throw new NotAdministratorException(String.format("%s not owner and not manager in the store %d", user.getUserName(), storeId));
    }

    /**
     * add purchase policy to store
     * @param user
     * @param purchase
     * @return
     */
    public Purchase addPurchase(UserSystem user, Purchase purchase) {
        if (isOwner(user) || managerCanEdit(user.getUserName())) {  //verify the user is owner of the store
            //purchase.setNewId();  //generate new ID for the new discount
            purchasePolicies.add(purchase);
            return purchase;
        }
        throw new NotAdministratorException(String.format("%s not owner and not manager in the store %d", user.getUserName(), storeId));
    }


    public Purchase getPurchasePolicyById(int id) {
        Optional<Purchase> purchasePolicy = this.purchasePolicies.stream().
                filter(purchasePolicy1 -> purchasePolicy1.getPurchaseId() == id).findFirst();
        if (purchasePolicy.isPresent()) {
            return purchasePolicy.get();
        }
        //there is no discount with the requested id
        throw new DiscountPolicyNoSuchIDException(id, this.getStoreName());
    }

    public Discount addEditDiscount(UserSystem user, Discount discount) {
        if (discount.getDiscountId() < 0) {
            return addDiscount(user, discount);
        } else {
            return editDiscount(user, discount);
        }
    }

    public Purchase addEditPurchase(UserSystem user, Purchase purchase) {
        if (purchase.getPurchaseId() < 0) {
            return addPurchase(user, purchase);
        } else {
            return editPurchase(user, purchase);
        }
    }

    private Discount editDiscount(UserSystem user, Discount discount) {
        if (isOwner(user) || managerCanEdit(user.getUserName())) {  //verify the user is owner of the store
            Optional<Boolean> isEdit = discounts.stream()
                    .filter(discountCur -> discountCur.getDiscountId() == discount.getDiscountId())
                    .findFirst().map(discountCur -> discountCur.editDiscount(discount.getDiscountPercentage(),
                            discount.getEndTime(),discount.getDescription(), discount.getDiscountPolicy(), discount.getDiscountType()));
            return isEdit.isPresent() ? discount : null;
        }
        throw new NotAdministratorException(String.format("%s not owner and not manager in the store %d", user.getUserName(), storeId));
    }

    private Purchase editPurchase(UserSystem user, Purchase purchase) {
        if (isOwner(user) || managerCanEdit(user.getUserName())) {  //verify the user is owner of the store
            Optional<Boolean> isEdit = purchasePolicies.stream()
                    .filter(purchaseCur -> purchaseCur.getPurchaseId() == purchase.getPurchaseId())
                    .findFirst().map(purchaseCur -> purchaseCur.editPurchase(purchase.getCountriesPermitted(),purchase.getStoreWorkDays(),
                            purchase.getMin(),purchase.getMax(),purchase.getProductId(),purchase.getCompositeOperator(),
                            purchase.getComposedPurchasePolicies(),purchase.isShoppingBagPurchaseLimit()));
            return isEdit.isPresent() ? purchase : null;
        }
        throw new NotAdministratorException(String.format("%s not owner and not manager in the store %d", user.getUserName(), storeId));
    }

    public List<Discount> getDiscountSimple(){
        return discounts.stream()
                .filter(discount -> discount.getDiscountType() != DiscountType.COMPOSE)
                .collect(Collectors.toList());
    }
}

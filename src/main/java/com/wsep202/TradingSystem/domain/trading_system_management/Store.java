package com.wsep202.TradingSystem.domain.trading_system_management;

import com.wsep202.TradingSystem.domain.exception.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@Builder
@NoArgsConstructor
@Slf4j
@Data
public class Store {


    public static int storeIdAcc = 0;

    private int storeId;        //unique identifier for the store

    private String storeName;

    //map to find all the owners as value appointed the owner who is in the fit key
    @Builder.Default
    private Map<UserSystem, Set<UserSystem>> appointedOwners = new HashMap<>();

    //map  that holds users as key and their appointed managers as value
    @Builder.Default
    private Map<UserSystem, Set<MangerStore>> appointedManagers = new HashMap<>();

    //The products that the store holds in it
    @Builder.Default
    Set<Product> products= new HashSet<>();

    //The set purchase policy for the store
    private PurchasePolicy purchasePolicy;

    //The set purchase policy for the store
    private DiscountPolicy discountPolicy;


    private DiscountType discountType;

    private PurchaseType purchaseType;

    //owners of the store
    private Set<UserSystem> owners ;

    //managers in the store
    private Set<MangerStore> managers;

    //list of purchases made in the store

    private List<Receipt> receipts;

    //the store rank
    private int rank;

    public Store(UserSystem owner, PurchasePolicy purchasePolicy, DiscountPolicy discountPolicy
            , String storeName){
        receipts = new LinkedList<>();
        appointedOwners = new HashMap<>();
        appointedManagers = new HashMap<>();
        products = new HashSet<>();
        owners = new HashSet<>();
        managers = new HashSet<MangerStore>();
        this.storeName = storeName;
        owners.add(owner);
        this.discountPolicy = discountPolicy;
        this.purchasePolicy = purchasePolicy;
        this.discountType = discountType;
        this.purchaseType = purchaseType;
        this.storeId = getStoreIdAcc();
        this.rank = 0;
    }

    /**
     * get and accumilate the store id accumulator
     * @return the store Id
     */
    private int getStoreIdAcc(){
        return storeIdAcc++;
    }

    /**
     * add new owner to the appointed owners of the store
     * @param owner
     * @param willBeOwner
     * @return true if owner added successfully
     */
    private boolean appointAdditionOwner(UserSystem owner, UserSystem willBeOwner) {
        boolean appointed = false;

        if(isOwner(owner) && !isOwner(willBeOwner)) {
            appointedOwners.putIfAbsent(owner, new HashSet<>());
            appointedOwners.get(owner).add(willBeOwner);
            appointed = true;
            log.info("The user: "+willBeOwner.getUserName()+" added as appointed owner by the owner: "+owner.getUserName()+ "" +
                    " for the store:"+ this.storeName);
        }
        if(!appointed){
            log.error("The user: "+willBeOwner.getUserName()+" couldn't be appointed as owner by: "+ owner.getUserName()+" " +
                    "for the store: "+this.storeName);
        }
        return appointed;
    }

    /**
     * add a new owner to be owner of this store
     * @param ownerStore
     * @param newOwnerUser
     * @return true if succeeded to add owner for the store
     */
    public boolean addOwner(UserSystem ownerStore, UserSystem newOwnerUser) {
        //appoint new user to be owner
        boolean isAppointedToOwner =  appointAdditionOwner(ownerStore,newOwnerUser);
        if(isAppointedToOwner){ //appointing succeeded
            owners.add(newOwnerUser);    //add the new owner to the owners list
            log.info("The user: "+newOwnerUser.getUserName()+" added as an owner to the store: "+this.storeName+
                    " by: "+ownerStore.getUserName());
            return true;
        }
        log.error("The user: "+newOwnerUser.getUserName()+" couldn't be added as an owner to the " +
                "store: "+this.storeName+
                " by: "+ownerStore.getUserName());
        return false;
    }

    /**
     * add new manager to the appointed owners of the store
     * @param owner
     * @param mangerStore
     * @return true for successful addition
     */
    private boolean appointAdditionManager(UserSystem owner, MangerStore mangerStore) {
        boolean appointed = false;
        //manager is not owner or manager in this store
        if (!(managersContains(mangerStore)||ownersContains(mangerStore.getAppointedManager()))) {
            appointedManagers.putIfAbsent(owner, new HashSet<>());
            appointedManagers.get(owner).add(mangerStore);
            appointed = true;
            log.info("The user: "+mangerStore.getAppointedManager().getUserName()+" added as appointed manager by the owner: "+owner.getUserName()+ "" +
                    " for the store:"+ this.storeName);
        }
        if(!appointed){
            log.error("The user: "+mangerStore.getAppointedManager().getUserName()+" couldn't be appointed as manager" +
                    " by: "+ owner.getUserName()+" " +
                    "for the store: "+this.storeName);
        }
        return appointed;
    }

    /**
     *  add a manager to be manager of this store
     * @param ownerStore
     * @param newManagerUser
     * @return true for success otherwise false
     */
    public boolean addManager(UserSystem ownerStore, UserSystem newManagerUser) {
        if(isOwner(ownerStore)){    //check if the user appointing is an owner so can appoint
            MangerStore newManager = new MangerStore(newManagerUser);
            boolean isAppointedToManager = appointAdditionManager(ownerStore,newManager);
            if(isAppointedToManager){       //the manager appointed to manage the store successfully
                managers.add(newManager);
                log.info("The user: "+newManagerUser.getUserName()+" added as a manager to the store: "+this.storeName+
                        " by: "+ownerStore.getUserName());
                return true;
            }
        }
        //the addition of manager failed
        log.error("The user: "+newManagerUser.getUserName()+" couldn't be added as a manager to the store: "
                +this.storeName+
                " by: "+ownerStore.getUserName());
        return false;
    }

    private boolean removeAppointManager(UserSystem owner, MangerStore mangerStore) {
        boolean appointed = false;
        if ((managersContains(mangerStore) && appointedManagers.get(owner).stream().anyMatch(man->man.equals(mangerStore)))) {
            appointedManagers.get(owner).remove(mangerStore);
            if(appointedManagers.get(owner).size()==0){
                appointedManagers.remove(owner);
            }
            appointed = true;
        }
        if (appointed){
            log.info("The owner: "+owner.getUserName()+" removed: "+mangerStore.getAppointedManager().getUserName()+"" +
                    " from appointed managers of the store: "+this.storeName);
        }
        else {
            log.error("The owner: "+owner.getUserName()+"failed to remove: "+mangerStore.getAppointedManager().getUserName()+"" +
                    " from appointed managers of the store: "+this.storeName);
        }
        return appointed;
    }

    /**
     * remove user from the store's managers
     * @param ownerStore the removing owner
     * @param user the removed manager
     * @return true for successful operation
     */
    public boolean removeManager(UserSystem ownerStore, UserSystem user) {
        if (isOwner(ownerStore)) {  //the user is able to remove his appointments
            //get the manager to remove
            MangerStore manager = getManagerObject(ownerStore,user.getUserName());
            //remove from appointed managers Set
            boolean isRemovedFromAppointedManagers = removeAppointManager(ownerStore,manager);
            if(isRemovedFromAppointedManagers) {
                boolean removed = managers.remove(manager);   //remove from managers list as well
                if(removed){
                    log.info(ownerStore.getUserName()+" removed: "+user.getUserName()+"" +
                            " from the store: "+storeName+ " managers successfully");
                    return true;
                }
                log.error(ownerStore.getUserName()+" failed to remove: "+user.getUserName()+"" +
                        " from the store: "+storeName);
            }
        }
        //couldn't remove manager
        return false;
    }


    /**
     * owner adds a new product to the store
     * @param user the user wish to add the product
     * @param product the product to add to store
     * @return true for success
     */
    public boolean addNewProduct(UserSystem user, Product product){
        if(isOwner(user)) {  //verify the user is owner of the store
            products.add(product);
            log.info("The product: "+product.getName()+" added to the store: "+this.storeName);
            return true;
        }
        log.error("The product: "+product.getName()+" failed to add to the store: "+this.storeName);
        return false;
    }

    /**
     * edit of exist product parameters in the store
     * @param user the user wish to add the product
     * @param productSn the unique number of the product
     *      parameters of the product:
     * @param productName
     * @param category
     * @param amount
     * @param cost
     * @return true for success
     */
    public boolean editProduct(UserSystem user, int productSn, String productName, String category,
                               int amount, double cost) {
        if(isOwner(user)){   //the user is owner
            Optional<Product> product = products.stream().filter(p -> p.getProductSn()==productSn).findFirst();
            if(product.isPresent()){    //update the product properties
                product.get().setName(productName);
                product.get().setCategory(ProductCategory.getProductCategory(category));
                product.get().setAmount(amount);
                product.get().setCost(cost);
                log.info("The product "+productName+" edited successfully");
                return true;
            }
        }
        log.error("Failed to edit the product: "+productName);
        return false;
    }

    /**
     * remove a product from the products set of the store
     * @param user - the user requests to remove
     * @param productSn - the unique identifier of the product
     * @return true if the removal succeeded otherwise false
     */
    public boolean removeProductFromStore(UserSystem user, int productSn) {
        if(isOwner(user)){  //only owner can remove products from its store
            int sizeOfProducts = products.size();
            products.removeIf(product -> product.getProductSn()==productSn);
            if(sizeOfProducts > products.size()){
                log.info("The product with id: "+ productSn+ " was removed successfully");
                return true;
            }
        }
        //the user is not an owner of the store so can't remove
        log.error("The product with id: "+ productSn+ " wasn't removed.");
        return false;
    }



    /**
     * add management options for a manager by an store owner
     * @param ownerStore - owner of this store
     * @param user - the manager we wish to edit and add his permissions
     * @param storePermission
     * @return  true if the added succeeded
     * otherwise false
     */
    public boolean addPermissionToManager(UserSystem ownerStore, UserSystem user, StorePermission storePermission) {
        if(!isOwner(ownerStore)) {    //verify the editor is owner in the store
            log.error("couldn't add permission:" + storePermission.function + " " +
                    "to: " + user.getUserName() + " by " + ownerStore.getUserName());
            return false;
        }
        //holds the manager appointed by the received owner to be manager in this store
        MangerStore manager = getManagerObject(ownerStore,user.getUserName());
        boolean added = manager.addStorePermission(storePermission);    //add the permission
        if(!added){
            log.error("couldn't add permission:" + storePermission.function + " " +
                    "to: " + user.getUserName() + " by " + ownerStore.getUserName());
            return false;
        }
        log.info("Permission:" + storePermission.function + " added" +
                "to: " + user.getUserName() + " by " + ownerStore.getUserName());
        return true;
    }

    /**
     *
     * @param ownerStore
     * @param user
     * @param storePermission
     * @return
     */
    public boolean removePermissionFromManager(UserSystem ownerStore, UserSystem user, StorePermission storePermission) {
        if(!isOwner(ownerStore)) {    //verify the editor is owner in the store
            log.error("couldn't add permission:" + storePermission.function + " " +
                    "to: " + user.getUserName() + " by " + ownerStore.getUserName());
            return false;
        }
        //holds the manager appointed by the received owner to be manager in this store
        MangerStore manager = getManagerObject(ownerStore,user.getUserName());
        boolean removed = manager.removeStorePermission(storePermission);    //add the permission
        if(!removed){
            log.error("couldn't remove permission:" + storePermission.function + " " +
                    "to: " + user.getUserName() + " by " + ownerStore.getUserName());
            return false;
        }
        log.info("Permission:" + storePermission.function + " removed" +
                "to: " + user.getUserName() + " by " + ownerStore.getUserName());
        return true;
    }


    /**
     * manager can see the store purchase history
     * @param user
     * @return
     */
    public List<Receipt> managerViewReceipts(UserSystem user) {
        if(isManager(user)){
            log.info(user.getUserName()+" manager viewed the store purchase history.");
            return receipts;
        }
        else {
            log.error(user.getUserName()+" cannot see the purchase history, he is not manager in the store.");
            throw new NoManagerInStoreException(user.getUserName(), storeId);
        }
    }

    /**
     * owner can see the store purchase history
     * @param user
     * @return
     */
    public List<Receipt> ownerViewReceipts(UserSystem user) {
        if(isOwner(user)){
            log.info(user.getUserName()+" owner viewed the store purchase history.");
            return receipts;
        }
        else {
            log.error(user.getUserName()+" cannot see the purchase history, he is not owner in the store.");
            throw new NoManagerInStoreException(user.getUserName(), storeId);
        }
    }

    /**
     * shows the store information
     * @return
     */
    public String showStoreInfo(){
        log.info("store info is presented");
        return "The store "+storeName+ " has ID: "+getStoreId();
    }

    /**
     * shows the store information
     * @return
     */
    public String showProductsInStoreInfo(){
        log.info("store's products info is presented");
        String productsInStoreInfo = "";
        for(Product product: this.products){
            productsInStoreInfo+=product.showProductInfo()+"\n";
        }
        return productsInStoreInfo;
    }


    /**
     * get the actual user of the manager appointeed by the ownerUser with managerUserName
     * @param ownerUser - the appointing owner
     * @param managerUserName - the appointed manager
     * @return the user belongs to the manager username
     * or exception if not appointed by the owner
     * or null if the owner didn't appointed anyone
     */
    public UserSystem getManager(UserSystem ownerUser, String managerUserName) {
        return appointedManagers.get(ownerUser) == null ? null : appointedManagers.get(ownerUser).stream()
                .filter(mangerStore -> mangerStore.isTheUser(managerUserName))
                .findFirst().orElseThrow(()-> new NoManagerInStoreException(managerUserName, storeId))
                .getAppointedManager();
    }

    /**
     * get the manager object of the manager managerUserName appointed by ownerUser
     * @param ownerUser - the appointing owner
     * @param managerUserName - the appointed manager
     * @return the manager object which wraps the manager with manager username
     */
    public MangerStore getManagerObject(UserSystem ownerUser, String managerUserName) {
        return appointedManagers.get(ownerUser) == null ? null : appointedManagers.get(ownerUser).stream()
                .filter(mangerStore -> mangerStore.isTheUser(managerUserName))
                .findFirst().orElseThrow(()-> new NoManagerInStoreException(managerUserName, storeId));
    }



    /**
     * return a product with the SN reveived
     * @param productId - SN of product to get
     * @return the fit product if exist in the store
     * otherwise exception is thrown
     */
    public Product getProduct(int productId) {
        return products.stream().filter(product -> product.getProductSn()==productId).findFirst()
                .orElseThrow(()->new ProductDoesntExistException(productId,storeId));
    }

    private boolean ownersContains(UserSystem user) {
        return (owners.stream().anyMatch(curUser -> curUser.getUserName().equals(user.getUserName())));
    }
    private boolean managersContains(MangerStore user) {
        return (managers.stream().anyMatch(curUser -> curUser.getAppointedManager().getUserName().equals(user.getAppointedManager().getUserName())));
    }
    /**
     * checks if the user is manager in the store
     * @param user
     * @return
     */
    private boolean isManager(UserSystem user){
        return appointedManagers.entrySet().stream()
                .anyMatch(entry -> entry.getValue().stream()
                        .anyMatch(mangerStore -> mangerStore.isTheUser(user)));
    }

    /**
     * checks if the given user is owner in this store
     * @param user
     * @return
     */
    private boolean isOwner(UserSystem user) {
        return ownersContains(user);
    }


    /**
     * search a product by a given productName
     * @param productName - the name to search
     * @return - the product who has name equals to productName
     */
    public Set<Product> searchProductByName(String productName){
        log.info("search product by name: "+productName+" has been performed");
        return products.stream()
                .filter(product -> product.getName().equals(productName))
                .collect(Collectors.toSet());
    }

    /**
     * search a product by a given productCategory
     * @param productCategory - the category to search
     * @return - the product who has category equals to productCategory
     */
    public Set<Product> searchProductByCategory(ProductCategory productCategory){
        log.info("search product by category: "+productCategory.category+" has been performed");
        return products.stream()
                .filter(product -> product.getCategory().category.equals(productCategory.category))
                .collect(Collectors.toSet());
    }

    /**
     * search a product by a given list of key words that contained in product name
     * @param keyWords - the keyWords to search in the name
     * @return - the product who has keyWords contained in product name
     */
    public Set<Product> searchProductByKeyWords(List<String> keyWords){
        log.info("search product by keyword: "+keyWords+" has been performed");
        return products.stream()
                .map(product -> product.productNameThatContainsKeyWords(keyWords)).filter(product -> product!=null)
                .collect(Collectors.toSet());
    }
}

package com.wsep202.TradingSystem.domain.trading_system_management;

import com.wsep202.TradingSystem.domain.exception.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@Builder
@NoArgsConstructor
@Slf4j
@Setter
@Getter
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
        }
        return appointed;
    }

    /**
     * add a new owner to be owner of this store
     * @param ownerStore
     * @param newOwnerUser
     * @return
     */
    public boolean addOwner(UserSystem ownerStore, UserSystem newOwnerUser) {
        //appoint new user to be owner
        boolean isAppointedToOwner =  appointAdditionOwner(ownerStore,newOwnerUser);
        if(isAppointedToOwner){ //appointing succeeded
            owners.add(newOwnerUser);    //add the new owner to the owners list
            return true;
        }
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
                return true;
            }
        }
        //the addition of manager failed
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
        return appointed;
    }

    /**
     *
     * @param ownerStore
     * @param user
     * @return
     */
    public boolean removeManager(UserSystem ownerStore, UserSystem user) {
        if (isOwner(ownerStore)) {  //the user is able to remove his appointments
            //get the manager to remove
            MangerStore manager = getManagerObject(ownerStore,user.getUserName());
            //remove from appointed managers Set
            boolean isRemovedFromAppointedManagers = removeAppointManager(ownerStore,manager);
            if(isRemovedFromAppointedManagers) {
                return managers.remove(manager);   //remove from managers list as well
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
            return true;
        }
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
                return true;
            }
        }
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
            return sizeOfProducts > products.size() ? true : false; //verify the product removed
        }
        //the user is not an owner of the store so can't remove
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
        if(!isOwner(ownerStore))    //verify the editor is owner in the store
            return false;
        //holds the manager appointed by the received owner to be manager in this store
        MangerStore manager = getManagerObject(ownerStore,user.getUserName());
        return manager.addStorePermission(storePermission);    //add the permission
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
            return false;
        }
        //holds the manager appointed by the received owner to be manager in this store
        MangerStore manager = getManagerObject(ownerStore,user.getUserName());
        return manager.removeStorePermission(storePermission);    //add the permission

    }


    /**
     * manager can see the store purchase history
     * @param user
     * @return
     */
    public List<Receipt> managerViewReceipts(UserSystem user) {
        if(isManager(user)){
            return receipts;
        }
        else {
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
            return receipts;
        }
        else {
            throw new NoManagerInStoreException(user.getUserName(), storeId);
        }
    }

    /**
     * shows the store information
     * @return
     */
    public String showStoreInfo(){
        return "The store "+storeName+ " has ID: "+getStoreId();
    }

    /**
     * shows the store information
     * @return
     */
    public String showProductsInStoreInfo(){
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

    //TODO ADDED by MORAN THE QUEEN. Moran, tu eres reina!

    /**
     * search a product by a given productName
     * @param productName - the name to search
     * @return - the product who has name equals to productName
     */
    public Set<Product> searchProductByName(String productName){
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
        return products.stream()
                .map(product -> product.productNameThatContainsKeyWords(keyWords)).filter(product -> product!=null)
                .collect(Collectors.toSet());
    }
}

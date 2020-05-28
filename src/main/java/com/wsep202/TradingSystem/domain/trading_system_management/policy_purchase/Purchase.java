package com.wsep202.TradingSystem.domain.trading_system_management.policy_purchase;
/**
 * the class defines a purchase policy in store
 */

import com.wsep202.TradingSystem.domain.trading_system_management.purchase.BillingAddress;
import com.wsep202.TradingSystem.domain.trading_system_management.Product;
import com.wsep202.TradingSystem.domain.trading_system_management.discount.CompositeOperator;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@Builder
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Purchase {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    protected int purchaseId;

    /**
     * list of countries that the store have deliveries to
     */
    @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
    private Set<String> countriesPermitted;
    /**
     * the days in the week any user is permitted to perform a purchase
     */
    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = Day.class, fetch = FetchType.EAGER)
    private Set<Day> storeWorkDays;
    /**
     * the valid range of amount of products to buy in a purchase
     * in ShoppingBagDetails it is the amount of different items
     * in ProductDetails it is the amount of items from a specific one product
     */
    private int min,max;
    /**
     * the SN of product which has limitations amounts
     */
    private int productId;
    /**
     * logical operator between policies
     */
    @Enumerated(EnumType.STRING)
    private CompositeOperator compositeOperator;

    /**
     * children components of the composite Purchase policy
     * the operands of the composed Purchase policy
     */
    @OneToMany(cascade = CascadeType.ALL)
    private List<Purchase> composedPurchasePolicies;
    /**
     * the types of purchase exist in the store
     */
    @ManyToMany
    @JoinTable()
    private Map<String, PurchasePolicy> purchasePolicies;
    /**
     * tells if its purchase policy at the shopping bag level
     */
    private boolean isShoppingBagPurchaseLimit;

    public Purchase(Set<String> countriesPermitted,
                    Set<Day> storeWorkDays,
                    int min, int max,
                    int productId,
                    CompositeOperator compositeOperator,
                    List<Purchase> composedPurchasePolicies,
                    boolean isShoppingBagPurchaseLimit) {
        this.countriesPermitted = countriesPermitted;
        this.storeWorkDays = storeWorkDays;
        this.min = min;
        this.max = max;
        this.productId = productId;
        this.compositeOperator = compositeOperator;
        this.composedPurchasePolicies = composedPurchasePolicies;
        this.isShoppingBagPurchaseLimit = isShoppingBagPurchaseLimit;
        initPurchasePolicies();
    }

    /**
     * initialize the types of purchase policies in store
     */
    private void initPurchasePolicies() {
        purchasePolicies = new HashMap<>();
        purchasePolicies.put(UserDetailsPolicy.class.getName(), new UserDetailsPolicy());
        purchasePolicies.put(SystemDetailsPolicy.class.getName(), new SystemDetailsPolicy());
        purchasePolicies.put(ShoppingBagDetailsPolicy.class.getName(), new ShoppingBagDetailsPolicy());
        purchasePolicies.put(ProductDetailsPolicy.class.getName(), new ProductDetailsPolicy());
        purchasePolicies.put(ComposedPurchase.class.getName(), new ComposedPurchase());

    }

    public boolean isApproved(Map<Product, Integer> products, BillingAddress userAddress){
        if(purchasePolicies==null){ //in case built thru bulder the init is not called
            initPurchasePolicies();
        }
        if (isComposed()) {
            return purchasePolicies.get(ComposedPurchase.class.getName()).isApproved(this,products,userAddress);
        } else if (isSystemDetails()) {
            return purchasePolicies.get(SystemDetailsPolicy.class.getName()).isApproved(this,products,userAddress);
        } else if(isUserDetails()){
            return purchasePolicies.get(UserDetailsPolicy.class.getName()).isApproved(this,products,userAddress);
        }else { //this is purchase amount limit policy
            return isApprovedPurchaseAmount(products, userAddress);
        }
    }

    private boolean isApprovedPurchaseAmount(Map<Product, Integer> products, BillingAddress userAddress) {
        if(isShoppingBagPurchaseLimit){
            return purchasePolicies.get(ShoppingBagDetailsPolicy.class.getName()).isApproved(this,products,userAddress);
        }
        else {  //purchase policy on single product
            return purchasePolicies.get(ProductDetailsPolicy.class.getName()).isApproved(this,products,userAddress);
        }
    }

    /**
     * edit purchase details
     */
    public boolean editPurchase(Set<String> countriesPermitted,
                    Set<Day> storeWorkDays,
                    int min, int max,
                    int productId,
                    CompositeOperator compositeOperator,
                    List<Purchase> composedPurchasePolicies,
                    boolean isShoppingBagPurchaseLimit) {
        this.countriesPermitted = countriesPermitted;
        this.storeWorkDays = storeWorkDays;
        this.min = min;
        this.max = max;
        this.productId = productId;
        this.compositeOperator = compositeOperator;
        this.composedPurchasePolicies = composedPurchasePolicies;
        this.isShoppingBagPurchaseLimit = isShoppingBagPurchaseLimit;
        return true;
    }

    /////////////////////////////////////is-methods/////////////////////////////////////////
    private boolean isComposed() {
        return composedPurchasePolicies!=null && !composedPurchasePolicies.isEmpty();
    }

    private boolean isSystemDetails() {
        return storeWorkDays!=null && !storeWorkDays.isEmpty();
    }

    private boolean isUserDetails() {
        return countriesPermitted!=null && !countriesPermitted.isEmpty();
    }

}

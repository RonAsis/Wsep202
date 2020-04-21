package com.wsep202.TradingSystem.domain.trading_system_management;

import externals.ChargeSystem;
import externals.SecuritySystem;
import externals.SupplySystem;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
@Slf4j

public class ExternalServiceManagement {

    private SecuritySystem securitySystem;
    private ChargeSystem chargeSystem;
    private SupplySystem supplySystem;

    /**
     * default connect if we don't have any prefers for the external systems
     */
    public void connect() {
        securitySystem = new SecuritySystem();
        chargeSystem = new ChargeSystem();
        supplySystem = new SupplySystem();
        log.info("The system is now connected to the external systems");

    }

    /**
     * initialize connection with external systems by user request
     * @param secSys   preferred security system
     * @param supSys   preferred supply system
     * @param chrgSys  preffered charge system
     */
    public void connect(SecuritySystem secSys, SupplySystem supSys, ChargeSystem chrgSys){
        this.securitySystem = secSys;
        this.supplySystem = supSys;
        this.chargeSystem = chrgSys;
        log.info("The system is now connected to the external systems");
    }
////////////////////////////////////////////////////////////////////////////////////
//////////////////////////interface to security system/////////////////////////////
//////////////////////////////////////////////////////////////////////////////////
    /**
     * Request to get hashed password and its salt from the Security system.
     * @param password - password received from the user
     * @return hashedPass - password after encryption
     */
    public PasswordSaltPair getEncryptedPasswordAndSalt(String password) {
        //get salt for the user password to hash with
        String userSalt = securitySystem.generateSalt(512).get();
        //generate unique password for the user using the salt
        String hashedPassword = securitySystem.hashPassword(password,userSalt).get();
        PasswordSaltPair passAndSalt = new PasswordSaltPair(hashedPassword,userSalt);
        return passAndSalt; //return the password generated with the used salt to generate it
    }

    /**
     * authenticate the user password against the saved password in DB
     * @param password the password to check
     * @param user the user we want to verify its password
     * @return true if the inserted password is correct
     * otherwise return false
     */
    public boolean isAuthenticatedUserPassword(String password, UserSystem user){
        boolean isLegitimateUser = securitySystem.verifyPassword(password,user.getPassword(),user.getSalt());
        return isLegitimateUser;
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////interface to charge system/////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * charge the customer for the items in the shopping cart
     * @param paymentDetails
     * @param cart
     * @return storesFailedToChargeForBags list of failed stores to charge
     */
    public List<Integer> charge(PaymentDetails paymentDetails, ShoppingCart cart){
        List<Integer> storesFailedToChargeForBags = new LinkedList<>(); //holds store ID of failed transactions for stores
        String logStatus = "failed";
        boolean isChargedForCurrentBag;     //flag tells if payment for current bag of store succeeded
        boolean isCharged = true;           //isCharged = true iff all charge iterations succeeded
        Map<Store, ShoppingBag> shoppingBags = cart.getShoppingBagsList();
        //make the payment for each store in the cart
        for (Store store : shoppingBags.keySet()){
            double calculatedPrice = calculateShoppingBagPrice(shoppingBags.get(store));
            isChargedForCurrentBag = chargeSystem.sendPaymentTransaction(store.getStoreName(),calculatedPrice,paymentDetails);
            isCharged = isCharged && isChargedForCurrentBag;    //all last charges succeeded ans current as well?
            if(isChargedForCurrentBag){    //add store that its bag didn't charged by the user
                storesFailedToChargeForBags.add(store.getStoreId());
            }
        }
        if(isCharged==true)
            logStatus = "succeeded";
        log.info("The user charging "+logStatus);
        return storesFailedToChargeForBags;
    }

    /**
     * calculate and returns the total price of the items in the shopping bag.
     * @param shoppingBag - the shopping bag we wish to calculate its price.
     * @return totalPrice as the price the customer needs to pay for the bag.
     */
    private double calculateShoppingBagPrice(ShoppingBag shoppingBag) {
        return shoppingBag.getTotalCostOfBag();
    }

    /**
     * The following request the chargeSystem to cancel charge of user belongs to the
     * payment details and get refund for the purchase of the cart from the stores inside it
     * @param paymentDetails
     * @param cart
     * @return  true for successful cancellation for all payment.
     */
    public boolean cancelCharge(PaymentDetails paymentDetails, ShoppingCart cart){
        String logStatus = "failed";
        boolean isRefund = true;           //isCharged = true iff all charge iterations succeeded
        Map<Store, ShoppingBag> shoppingBags = cart.getShoppingBagsList();
        //make the refund for each store in the cart
        for (Store store : shoppingBags.keySet()){
            double calculatedPrice = calculateShoppingBagPrice(shoppingBags.get(store));
            isRefund = isRefund && chargeSystem.cancelCharge(store.getStoreName(),calculatedPrice,paymentDetails);
                //all last charges succeeded ans current as well?
        }
        if(isRefund==true)
            logStatus = "succeeded";
        log.info("The user charging "+logStatus);
        return isRefund;
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////interface to supply system/////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * this method responsible for arrange the shopping bags in a list and send them to
     * delivery with the billing address of the customer
     * @param addressInfo - the billing address of the customer
     * @param cart - the shopping cart of the customer to deliver the products from
     * @return true if the request for delivery accepted
     * otherwise returns false
     */
    public boolean deliver(BillingAddress addressInfo,ShoppingCart cart){
        List<ShoppingBag> bags = new ArrayList<>();
        for(ShoppingBag bag : cart.getShoppingBagsList().values()){
            bags.add(bag);
        }
        return supplySystem.deliver(addressInfo,bags);
    }

    public boolean cancelDelivery(BillingAddress addressInfo, ShoppingCart cart){
        List<ShoppingBag> bags = new ArrayList<>();
        for(ShoppingBag bag : cart.getShoppingBagsList().values()){
            bags.add(bag);
        }
        return supplySystem.canceldelivery(addressInfo,bags);
    }
}


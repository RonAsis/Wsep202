package com.wsep202.TradingSystem.domain.trading_system_management;

import externals.ChargeSystem;
import externals.SecuritySystem;
import externals.SupplySystem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExternalServiceManagement {

    public void connect() {
        // if cant throw exception
        //TODO
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
        String userSalt = SecuritySystem.generateSalt(512).get();
        //generate unique password for the user using the salt
        String hashedPassword = SecuritySystem.hashPassword(password,userSalt).get();
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
        boolean isLegitimateUser = SecuritySystem.verifyPassword(password,user.getPassword(),user.getSalt());
        return isLegitimateUser;
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////interface to charge system/////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * charge the customer for the items in the shopping cart
     * @param paymentDetails
     * @param cart
     * @return
     */
    public boolean charge(PaymentDetails paymentDetails, ShoppingCart cart){
        boolean isCharged = true;
        Map<Store, ShoppingBag> shoppingBags = cart.getShoppingBags();
        //make the payment for each store in the cart
        for (Store store : shoppingBags.keySet()){
            int calculatedPrice = calculateShoppingBagPrice(shoppingBags.get(store));
            isCharged = isCharged && ChargeSystem.sendPaymentTransaction(store.getStoreName(),calculatedPrice,paymentDetails);
        }
        return isCharged;   //isCharged = true iff all charge iterations succeeded
    }

    /**
     * calculate and returns the total price of the items in the shopping bag.
     * @param shoppingBag - the shopping bag we wish to calculate its price.
     * @return totalPrice as the price the customer needs to pay for the bag.
     */
    private int calculateShoppingBagPrice(ShoppingBag shoppingBag) {
        int totalPrice = 0;
        for(Integer productPrice :shoppingBag.getMapProductSnToAmount().keySet()){
            totalPrice+= productPrice;  //add current product price
        }
        return totalPrice;
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////interface to supply system/////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * this method responsible for arrange the shopping bags in a list and send them to
     * delivery with the billing address of the customer
     * @param addressIfo - the billing address of the customer
     * @param cart - the shopping cart of the customer to deliver the products from
     * @return true if the request for delivery accepted
     * otherwise returns false
     */
    public boolean deliver(BillingAddress addressIfo,ShoppingCart cart){
      List<ShoppingBag> bags = new ArrayList<>();
      for(ShoppingBag bag : cart.getShoppingBags().values()){
          bags.add(bag);
      }
      return SupplySystem.deliver(addressIfo,bags);
    }
}

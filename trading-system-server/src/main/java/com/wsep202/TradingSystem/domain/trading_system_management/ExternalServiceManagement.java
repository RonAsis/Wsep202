
package com.wsep202.TradingSystem.domain.trading_system_management;

import com.wsep202.TradingSystem.domain.exception.ChargeException;
import com.wsep202.TradingSystem.domain.exception.DeliveryRequestException;
import com.wsep202.TradingSystem.domain.trading_system_management.purchase.BillingAddress;
import com.wsep202.TradingSystem.domain.trading_system_management.purchase.PaymentDetails;
import externals.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
@Getter
@Setter
@Slf4j
public class ExternalServiceManagement {
    /**
     * factories
     */
    private ChargeSystemFactory chargeFactory;
    private SupplySystemFactory supplyFactory;
    /**
     * Services
     */
    private SecuritySystem securitySystem;
    private ChargeService chargeSystem;
    private SupplyService supplySystem;

    /**
     * default connect if we don't have any prefers for the external systems
     */
    public void connect() {
        chargeFactory = new ChargeSystemFactory();
        supplyFactory = new SupplySystemFactory();

        chargeSystem = chargeFactory.createChargeSystem("https://cs-bgu-wsep.herokuapp.com/");
        supplySystem = supplyFactory.createSupplySystem("https://cs-bgu-wsep.herokuapp.com/");
        log.info("The system is now initialized with external systems services");
    }

    /**
     * initialize connection with external systems by user request
     * @param secSys   preferred security system
     * @param supSys   preferred supply system
     * @param chrgSys  preffered charge system
     */
    public void connect(SecuritySystem secSys, SupplyService supSys, ChargeService chrgSys){
        this.securitySystem = secSys;
        this.supplySystem = supSys;
        this.chargeSystem = chrgSys;
        log.info("The system is now initialized with external systems services");
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
    public int charge(PaymentDetails paymentDetails, ShoppingCart cart) throws ChargeException{
        int transId = chargeSystem.sendPaymentTransaction(paymentDetails,cart);
        if(transId>=10000 && transId <= 100000){
            log.info("charge succeeded for holder: "+paymentDetails.getHolderName());
        }
        else{
            log.info("charge failed for holder: "+paymentDetails.getHolderName());
        }
        return transId;
    }



    /**
     * The following request the chargeSystem to cancel charge of user belongs to the
     * payment details and get refund for the purchase of the cart from the stores inside it
     * @param paymentDetails
     * @param cart
     * @return  true for successful cancellation for all payment.
     */
    public int cancelCharge(PaymentDetails paymentDetails, ShoppingCart cart,String transactionId){
        int res = chargeSystem.cancelCharge(paymentDetails,transactionId,cart);
        if(res>0){
            log.info("card holder: "+paymentDetails.getHolderName()+" charged cancelled.");
        }else log.info("cancellation failed for holder: "+paymentDetails.getHolderName());
        return res;
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
    public int deliver(BillingAddress addressInfo, ShoppingCart cart) throws DeliveryRequestException {

        int transId = supplySystem.deliver(addressInfo,cart);
        if(transId >= 10000 && transId<=100000){
            log.info("supply accepted for: "+addressInfo.getCustomerFullName()+"by external system");
        }else{
            log.info("supply rejected for: "+addressInfo.getCustomerFullName()+" by external system");
        }
        return transId;
    }

    public int cancelDelivery(BillingAddress addressInfo, ShoppingCart cart, String suppTransId){

        int res = supplySystem.cancelDelivery(addressInfo,cart,suppTransId);
        if(res>0){
            log.info("supply cancelled successfully for: "+addressInfo.getCustomerFullName());
        }else {
            log.info("supply failed to cancel for: "+addressInfo.getCustomerFullName());
        }
        return res;
    }
}


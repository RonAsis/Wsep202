package externals;

import com.wsep202.TradingSystem.domain.exception.ChargeException;
import com.wsep202.TradingSystem.domain.trading_system_management.ShoppingBag;
import com.wsep202.TradingSystem.domain.trading_system_management.ShoppingCart;
import com.wsep202.TradingSystem.domain.trading_system_management.Store;
import com.wsep202.TradingSystem.domain.trading_system_management.purchase.PaymentDetails;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
@Slf4j
public class ChargeSystem implements ChargeService{

    /**
     * send payment transaction to the issue bank for charging the customer

     * @param paymentDetails - the credit card holder payment method
     * @return true for successful transaction
     * otherwise false
     */
    public int sendPaymentTransaction(PaymentDetails paymentDetails,ShoppingCart cart) {


        boolean isChargedForCurrentBag;     //flag tells if payment for current bag of store succeeded
        boolean isCharged = true;           //isCharged = true iff all charge iterations succeeded
        Map<Store, ShoppingBag> shoppingBags = cart.getShoppingBagsList();
        //make the payment for each store in the cart
        ShoppingCart chargedBags = new ShoppingCart();  //optional for cancelation deals in case of charge fail
        for (Store store : shoppingBags.keySet()){
            double calculatedPrice = calculateShoppingBagPrice(shoppingBags.get(store));
            isChargedForCurrentBag = isValidPayment(paymentDetails);
            isCharged = isCharged && isChargedForCurrentBag;    //all last charges succeeded ans current as well?
            if(isChargedForCurrentBag){    //add store that its bag didn't charged by the user
                log.info("Succeeded to charge bag in store: "+store.getStoreName());
                chargedBags.addBagToCart(store,shoppingBags.get(store));
            }else {
                log.info("failed to charge bag in store: "+store.getStoreName());
                cancelCharge(paymentDetails,String.valueOf(chargedBags.getNumOfBagsInCart()),cart);   //cancel charge of already succeeded deals
                throw new ChargeException("Charge refused for store: "+store.getStoreName());
            }
        }
        //all bags were charged
        return 10002;
    }




    private boolean isValidPayment(PaymentDetails paymentDetails) {
        //make some validation operations and then charge. e.g.:
        if(!isValidCardNumber(paymentDetails.getCreditCardNumber())){
            return false;
        }

        return true;
    }


    /**
     * calculate and returns the total price of the items in the shopping bag.
     * @param shoppingBag - the shopping bag we wish to calculate its price.
     * @return totalPrice as the price the customer needs to pay for the bag.
     */
    private double calculateShoppingBagPrice(ShoppingBag shoppingBag) {
        return shoppingBag.getTotalCostOfBag();
    }



    private boolean isValidCardNumber(String creditCardNumber) {
        return creditCardNumber.length()==9;
    }

    /**
     * get refund from the store for the payed price
     * @return true for successful refund
     */
    public int cancelCharge(PaymentDetails paymentDetails,String transactionId,ShoppingCart cart){

        String logStatus = "failed";
        boolean isRefund = true;           //isCharged = true iff all charge iterations succeeded
        Map<Store, ShoppingBag> shoppingBags = cart.getShoppingBagsList();
        //make the refund for each store in the cart
        for (Store store : shoppingBags.keySet()){
            double calculatedPrice = calculateShoppingBagPrice(shoppingBags.get(store));
            isRefund = isRefund && isLegalId(transactionId);
            //all last charges succeeded ans current as well?
        }
        if(isRefund==true) {
            logStatus = "succeeded";
            return 1;
        }
        log.info("The user charging "+logStatus);
        return -1;

    }

    private boolean isLegalId(String transactionId) {
        //temporarily check if it possible to refund
        if(Integer.parseInt(transactionId)>=10000){
            return true;
        }
        return false;
    }

    @Override
    public boolean isConnected() {
        return true;
    }
}

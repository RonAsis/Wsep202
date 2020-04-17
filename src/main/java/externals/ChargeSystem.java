package externals;

import com.wsep202.TradingSystem.domain.trading_system_management.PaymentDetails;

public class ChargeSystem {
    /**
     * send payment transaction to the issue bank for charging the customer
     * @param storeName - the store as destination account
     * @param calculatedPrice - the sum of the paymaent price
     * @param paymentDetails - the credit card holder payment method
     * @return true for successful transaction
     * otherwise false
     */
    public boolean sendPaymentTransaction(String storeName, double calculatedPrice, PaymentDetails paymentDetails) {
        //make some validation operations and then charge. e.g.:
        if(!isValidCardNumber(paymentDetails.getCreditCardNumber())){
            return false;
        }

        return true;
    }

    private boolean isValidCardNumber(String creditCardNumber) {
        return creditCardNumber.length()==9;
    }
}

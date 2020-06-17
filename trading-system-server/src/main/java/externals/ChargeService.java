package externals;

import com.wsep202.TradingSystem.domain.trading_system_management.ShoppingCart;
import com.wsep202.TradingSystem.domain.trading_system_management.purchase.PaymentDetails;

/**
 * represents the operations a charge system can do
 */
public interface ChargeService {
    /**
     * pay for a purchase at the trafing system
     * Output: transaction id -an integer in the range [10000,100000] which indicates a
     * transaction number if the transaction succeeds or -1 if the transaction has failed.
     */
    int sendPaymentTransaction(PaymentDetails paymentDetails, ShoppingCart cart);

    /**
     * This action is used for cancelling a payment transaction
     * Output: 1 if the cancelation has been successful or -1 if the cancellation has failed.
     */
    int cancelCharge(PaymentDetails paymentDetails, String transactionId, ShoppingCart cart);

    /**
     * perform handshake with the external system
     * This action is used for check the availabilityof the external systems.
     * returns true for successfull handshake
     */
    boolean isConnected();
}

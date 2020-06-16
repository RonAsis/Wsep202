package externals;

import com.wsep202.TradingSystem.domain.trading_system_management.ShoppingCart;
import com.wsep202.TradingSystem.domain.trading_system_management.purchase.BillingAddress;

public interface SupplyService {

    /**
     * This action  is used for dispatching a delivery to a costumer.
     * Output: transaction id -an integer in the range [10000,100000] which indicates a transaction number
     * if the transaction succeeds or -1 if the transaction has failed
     */
    int deliver(BillingAddress addressInfo, ShoppingCart cart);

    /**
     * This action type is used for cancelling a supply transaction
     * Output: 1 if the cancellation has been successful or -1 if the cancellation has failed
     */
    int cancelDelivery(BillingAddress addressInfo, ShoppingCart cart,String suppTransId);

    /**
     * perform handshake with the external system
     * This action is used for check the availabilityof the external systems.
     * returns true for successfull handshake
     */
    boolean isConnected();

}

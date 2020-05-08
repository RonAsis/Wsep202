package externals;

import com.wsep202.TradingSystem.domain.trading_system_management.BillingAddress;
import com.wsep202.TradingSystem.domain.trading_system_management.ShoppingBag;

import java.util.List;

public class SupplySystem {

    public boolean deliver(BillingAddress addressInfo, List<ShoppingBag> bags){
        //check if the address is valid for example
        if(!isValidZip(addressInfo.getZipCode())){
            return false;
        }
        return true;    //the request for delivery accepted
    }

    private boolean isValidZip(String zipCode) {
        return zipCode.length()==7;
    }

    /**
     * cancel delivery of the bags request for the user belongs to the Billing address.
     * @param addressInfo  shippment details of the user to cancel his delivery
     * @param bags the products to cancel their shipping
     * @return true for successful cancellation.
     */
    public boolean canceldelivery(BillingAddress addressInfo, List<ShoppingBag> bags) {
        //temporarily condition
        return bags.size()>0;
    }
}

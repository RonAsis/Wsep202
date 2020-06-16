package externals;

import com.wsep202.TradingSystem.domain.exception.DeliveryRequestException;
import com.wsep202.TradingSystem.domain.trading_system_management.ShoppingCart;
import com.wsep202.TradingSystem.domain.trading_system_management.purchase.BillingAddress;
import com.wsep202.TradingSystem.domain.trading_system_management.ShoppingBag;

import java.util.ArrayList;
import java.util.List;

public class SupplySystem implements SupplyService{

    public boolean isDelivered(BillingAddress addressInfo, List<ShoppingBag> bags){
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
    public int canceldeliveryOrig(BillingAddress addressInfo, List<ShoppingBag> bags) {
        //temporarily condition
        return bags.size();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public int deliver(BillingAddress addressInfo, ShoppingCart cart) {
        boolean isDelivered;
        List<ShoppingBag> bags = new ArrayList<>();
        for(ShoppingBag bag : cart.getShoppingBagsList().values()){
            bags.add(bag);
        }
        isDelivered = isDelivered(addressInfo,bags);
        if(isDelivered){
            return 11003;
        }
        throw new DeliveryRequestException("The Delivery request rejected.");
    }

    @Override
    public int cancelDelivery(BillingAddress addressInfo, ShoppingCart cart,String suppTransId) {
        List<ShoppingBag> bags = new ArrayList<>();
        for(ShoppingBag bag : cart.getShoppingBagsList().values()){
            bags.add(bag);
        }
        return canceldeliveryOrig(addressInfo,bags);
    }

    @Override
    public boolean isConnected() {
        return false;
    }
}

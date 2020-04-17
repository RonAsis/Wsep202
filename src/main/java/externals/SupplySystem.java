package externals;

import com.wsep202.TradingSystem.domain.trading_system_management.BillingAddress;
import com.wsep202.TradingSystem.domain.trading_system_management.Product;
import com.wsep202.TradingSystem.domain.trading_system_management.ShoppingBag;
import com.wsep202.TradingSystem.domain.trading_system_management.UserSystem;

import java.util.List;

public class SupplySystem {

    public boolean deliver(BillingAddress addressInfo, List<ShoppingBag> bags){
        //check if the address is valid for example
        if(!isValidPhone(addressInfo.getPhone())){
            return false;
        }
        return true;    //the request for delivery accepted
    }

    private boolean isValidPhone(String phoneNum) {
        return phoneNum.length()==10;
    }

}

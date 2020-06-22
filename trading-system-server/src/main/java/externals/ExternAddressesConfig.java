package externals;

/**
 * holds the addresses of external interfaces the trading system works with
 */
public class ExternAddressesConfig {
    //the known interfaces for possible external systems
    public final static String BGU_SUPPLY_SYS_ADDR = "https://cs-bgu-wsep.herokuapp.com/";//bgu supply sys
    public final static String BGU_CHARGE_SYS_ADDR = "https://cs-bgu-wsep.herokuapp.com/";//bgu charge sys

    //the actual addresses our system works with - here he define it
    public final static String ACTUAL_SUPPLY_SYS_ADDR = BGU_SUPPLY_SYS_ADDR;
    public final static String ACTUAL_CHARGE_SYS_ADDR = BGU_CHARGE_SYS_ADDR;

}

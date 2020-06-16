package externals;

public class ChargeSystemFactory {
    public ChargeSystemFactory() {
    }

    public ChargeService createChargeSystem(String url){
        if(url.contains("cs-bgu")){
            return new BGUChargeSystem();
        }
        else {
            return new ChargeSystem();
        }
    }
}

package externals;

public class SupplySystemFactory {
    public SupplySystemFactory() {
    }

    public SupplyService createSupplySystem(String url){
        if(url.contains("cs-bgu")){
            return new BGUSupplySystem();
        }
        else {
            return new SupplySystem();
        }
    }
}

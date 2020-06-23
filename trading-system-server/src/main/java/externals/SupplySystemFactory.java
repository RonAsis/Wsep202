package externals;
/**
 * creator of supply service to communicate with external api
 */

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SupplySystemFactory {
    public SupplySystemFactory() {
    }

    public SupplyService createSupplySystem(String url){
        if(url.contains("cs-bgu")){
            log.info("the supply factory created bgu charge system service.");
            return new BGUSupplySystem();
        }
        else {
            log.info("the supply factory created default charge system service.");
            return new SupplySystem();
        }
    }
}

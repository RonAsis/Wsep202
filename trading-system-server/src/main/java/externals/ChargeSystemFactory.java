package externals;

/**
 * creator of charge service to communicate with external api
 */
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChargeSystemFactory {
    public ChargeSystemFactory() {
    }

    public ChargeService createChargeSystem(String url){
        if(url.contains("cs-bgu")){
            log.info("charge factory created a bgu charge system");
            return new BGUChargeSystem();
        }
        else {
            log.info("charge factory created default charge system");
            return new ChargeSystem();
        }
    }
}

package externals;

import com.wsep202.TradingSystem.domain.exception.ExternalSystemException;
import com.wsep202.TradingSystem.domain.trading_system_management.ShoppingCart;
import com.wsep202.TradingSystem.domain.trading_system_management.purchase.PaymentDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Slf4j
public class BGUChargeSystem implements ChargeService {
    //web address of the external system api
    private static final String url = "https://cs-bgu-wsep.herokuapp.com/";
    private RestTemplate restTemplate;
    private HttpHeaders headers;


    public BGUChargeSystem() {
        this.restTemplate = new RestTemplate();
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    }

    @Override
    public int sendPaymentTransaction(PaymentDetails paymentDetails, ShoppingCart cart) {
        if (!isConnected()) {
            return -1;
        }
        try {
            MultiValueMap<String, String> postContent = new LinkedMultiValueMap<String, String>();
            postContent.add("action_type", "pay");
            postContent.add("card_number", paymentDetails.getCreditCardNumber());
            postContent.add("month", paymentDetails.getMonth());
            postContent.add("year", paymentDetails.getYear());
            postContent.add("holder", paymentDetails.getHolderName());
            postContent.add("ccv", paymentDetails.getCcv());
            postContent.add("id", paymentDetails.getHolderIDNumber());


            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(postContent, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            if (response.getBody() == null) {
                log.info("Get null body from external charge system on charge");
                return -1;
            }
            log.info(String.format("Get valid body [%s] from external charge system on charge", response.getBody()));
            return Integer.parseInt(response.getBody());
        } catch (ResourceAccessException e) {
            log.error("Can't connect to the charge system", e);
            throw new ExternalSystemException("failed to handshake the BGU charge system.");
        }
    }

    @Override
    public int cancelCharge(PaymentDetails paymentDetails, String transactionId, ShoppingCart cart) {
        if (!isConnected()) {
            return -1;
        }
        try {
            MultiValueMap<String, String> postContent = new LinkedMultiValueMap<String, String>();
            postContent.add("action_type", "cancel_pay");
            postContent.add("transaction_id", transactionId);
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(postContent, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            if (response.getBody() == null) {
                log.info("Get null body from external charge system on Cancel charge");
                return -1;
            }
            log.info(String.format("Get valid body [%s] from external charge system on Cancel charge", response.getBody()));
            return Integer.parseInt(response.getBody());
        } catch (ResourceAccessException e) {
            log.error("Can't connect to the charge system", e);
            throw new ExternalSystemException("failed to handshake the BGU charge system.");
        }
    }

    @Override
    public boolean isConnected() {

        try {
            MultiValueMap<String, String> postContent = new LinkedMultiValueMap<String, String>();
            postContent.add("action_type", "handshake");
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(postContent, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            return response.getBody().equals("OK");
        } catch (ResourceAccessException e) {
            throw new ExternalSystemException("failed to handshake the BGU charge system.");
        }
    }
}

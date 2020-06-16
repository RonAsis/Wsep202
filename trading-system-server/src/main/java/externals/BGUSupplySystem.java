package externals;

import com.wsep202.TradingSystem.domain.exception.ExternalSystemException;
import com.wsep202.TradingSystem.domain.trading_system_management.ShoppingCart;
import com.wsep202.TradingSystem.domain.trading_system_management.purchase.BillingAddress;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

public class BGUSupplySystem implements SupplyService {
    //web address of the external system api
    private static final String url = "https://cs-bgu-wsep.herokuapp.com/";
    private RestTemplate restTemplate;
    private HttpHeaders headers;

    public BGUSupplySystem(){
        this.restTemplate = new RestTemplate();
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    }

    @Override
    public int deliver(BillingAddress addressInfo, ShoppingCart cart) {
        if(!isConnected()){
            return -1;
        }
        try{
            MultiValueMap<String,String> postContent = new LinkedMultiValueMap<String, String>();
            postContent.add("action_type","supply");
            postContent.add("name",addressInfo.getCustomerFullName());
            postContent.add("address",addressInfo.getAddress());
            postContent.add("city",addressInfo.getCity());
            postContent.add("country",addressInfo.getCountry());
            postContent.add("zip",addressInfo.getZipCode());

            HttpEntity<MultiValueMap<String,String>> request = new HttpEntity<MultiValueMap<String,String>>(postContent,headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url,request,String.class);
            if(response.getBody()==null){
                return -1;
            }
            return Integer.parseInt(response.getBody());
        }catch (ResourceAccessException e){
            throw new ExternalSystemException("failed to handshake the BGU charge system.");
        }
    }

    @Override
    public int cancelDelivery(BillingAddress addressInfo, ShoppingCart cart, String suppTrandId) {
        if(!isConnected()){
            return -1;
        }
        try{
            MultiValueMap<String,String> postContent = new LinkedMultiValueMap<String, String>();
            postContent.add("action_type","cancel_supply");
            postContent.add("transaction_id",suppTrandId);
            HttpEntity<MultiValueMap<String,String>> request = new HttpEntity<MultiValueMap<String,String>>(postContent,headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url,request,String.class);
            if(response.getBody()==null)
                return -1;
            return Integer.parseInt(response.getBody());
        }catch (ResourceAccessException e){
            throw new ExternalSystemException("failed to handshake the BGU charge system.");
        }
    }

    @Override
    public boolean isConnected() {
        try{
            MultiValueMap<String,String> postContent = new LinkedMultiValueMap<String, String>();
            postContent.add("action_type","handshake");
            HttpEntity<MultiValueMap<String,String>> request = new HttpEntity<MultiValueMap<String,String>>(postContent,headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url,request,String.class);
            return response.getBody().equals("OK");
        }catch (ResourceAccessException e){
            throw new ExternalSystemException("failed to handshake the BGU charge system.");
        }
    }
}

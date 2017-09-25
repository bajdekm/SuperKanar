package ldc.accenture.superkanar.managers;


import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

import static org.apache.http.protocol.HTTP.USER_AGENT;


@Slf4j
public class RequestManager {

    @Autowired
    private Environment env;

    private static RequestManager instance = null;
    @Value("${tokenendpoint}")
    private  String token_endpoint;
    @Value("${insertalertendpoint}")
    private  String insert_alert_endpoint;
    @Value("${clientid}")
    private  String client_id;
    @Value("${clientsecret}")
    private  String client_secret;
    @Value("${granttype}")
    private  String grant_type;
    @Value("${accesstype}")
    private  String access_type;
    @Value("${callbackendpoint}")
    private  String callback_endpoint;
    @Value("${initendpoint}")
    private String init_endpoint;

    private Map<String,String> paramMap = new HashMap<>();

    private RequestManager(){}

    public static RequestManager getInstance() {
        if(instance == null){
            instance = new RequestManager();
        }
        return instance;
    }

    public void fillInParamMap(HttpServletRequest request){
        Enumeration enumeration = request.getParameterNames();
        String singleParamName;
        while(enumeration.hasMoreElements()){
            singleParamName = enumeration.nextElement().toString();
            this.paramMap.put(singleParamName , request.getParameter(singleParamName));
        }
        log.info(">> parameterMap: " + this.paramMap);
    }

    public String extractCodeFromResponse(){
        String toReturn = new String();
        if(!this.paramMap.isEmpty() && this.paramMap.containsKey("code")){
            toReturn = this.paramMap.get("code");
        }
        log.info(">> code: " + toReturn);
        return toReturn;
    }

    public void sendOAuthAuthRequest(String authCode){
        HttpResponse response = null;
        CloseableHttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(token_endpoint);

        List<NameValuePair> arguments = new ArrayList<>();
        arguments.add(new BasicNameValuePair("code",authCode));
        arguments.add(new BasicNameValuePair("grant_type",grant_type));
        arguments.add(new BasicNameValuePair("client_id",client_id));
        arguments.add(new BasicNameValuePair("client_secret",client_secret));
        arguments.add(new BasicNameValuePair("redirect_uri",callback_endpoint));
        arguments.add(new BasicNameValuePair("access_type",access_type));

        try{
            post.setEntity(new UrlEncodedFormEntity(arguments));
            response = client.execute(post);
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.info(">> OAuth Response: " + response.toString());
    }

    public void initializeHandshake() throws Exception{
        log.error(init_endpoint);
        String urlToRequest = init_endpoint + "response_type=code&" + "client_id="+ client_id
                + "&redirect_uri=" + callback_endpoint;
        URL url = new URL(urlToRequest);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);
        int responseCode = con.getResponseCode();
        log.info("Request: "+urlToRequest + " sent " + responseCode);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String output;
        StringBuffer response = new StringBuffer();

        while ((output = in.readLine()) != null) {
            response.append(output);
        }
        in.close();
        log.info(">> Auth response: " + response.toString());
    }


}

package managers;


import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Postman {
    private static HttpClient client;
   static Logger logger = LoggerFactory.getLogger(Postman.class);
    public static HttpClient getHttpClient() {
        if (client == null) {
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(300 * 1000).setSocketTimeout(300 * 1000).build();
            client = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build();
        }
        return client;
    }

    public static String POST(String requestbody,String url)throws  Exception {
        @SuppressWarnings("deprecation")
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader("Content-Type", "application/json");
        StringEntity entity = new StringEntity(requestbody, "UTF8");
        String strResponse =  null;
        entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        httpPost.setEntity(entity);

            HttpResponse response = httpClient.execute(httpPost);
            System.out.println("response::"+response);
            logger.info("Response to url " + url + " is " + response);
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
            }
            BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
            String output=null;
            while ((output = br.readLine()) != null) {
                strResponse = output;
            }
            return  strResponse;
    }
    public static Map<String,Object> GET(String url)throws Exception {
        HttpGet get = new HttpGet(url);
        String strResponse = null;
        get.setHeader("Content-Type", "application/json; charset=utf-8");
        HttpResponse response = getHttpClient().execute(get);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));
        int statuscode = response.getStatusLine().getStatusCode();
        logger.info("Status call to url " + url + " is " + statuscode);
        Map<String,Object> resp = new HashMap<String,Object>();
        resp.put("statuscode",statuscode);
        String line;

        while ((line = reader.readLine()) != null) {
            strResponse = line;
        }
        reader.close();

        if(strResponse==null || strResponse.isEmpty())
            strResponse = response.getStatusLine().getReasonPhrase();
        resp.put("response",strResponse);
        return resp;
    }
}

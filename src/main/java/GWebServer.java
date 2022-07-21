import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;

import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

public class GWebServer {
    private static GWebServer myInstance = null;

    public static String POST_URL  = "test.com";
    private static final Logger logger = Logger.getLogger(GreenMonitor.class.getName());
    private boolean test = false;

    private GWebServer()
    {

    }

    public static GWebServer getInstance()
    {
        if(myInstance == null)
            myInstance = new GWebServer();
        return myInstance;
    }

    public void setTest(boolean test)
    {
        this.test = test;
    }

    public boolean postPayload(String uri, JSONObject payload) throws URISyntaxException {

        CloseableHttpClient httpClient = HttpClientBuilder.create().build();

        try {
            logger.info("POST URI:"+uri);
            HttpPost request = new HttpPost(uri);
            StringEntity params =new StringEntity(payload.toString());
            request.addHeader("content-type", "application/json");
            request.addHeader("Accept","application/json");

            //x-api-key
            if(test)
                request.addHeader("x-api-key","PMAK-62d9509a2c2d0912aca658c6-99e1a4498a7edd79a73e9d4306ff0b9d46");

            request.setEntity(params);
            CloseableHttpResponse response = httpClient.execute(request);

            // handle response here...
            StatusLine statusLine = response.getStatusLine();

            logger.info(statusLine.getStatusCode() + " " + statusLine.getReasonPhrase());
            String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            logger.info("Response body: " + responseBody);

        }catch (Exception ex) {
            // handle exception here
            ex.printStackTrace();
        } finally {
            httpClient.getConnectionManager().shutdown();
        }

        return true;
    }
}

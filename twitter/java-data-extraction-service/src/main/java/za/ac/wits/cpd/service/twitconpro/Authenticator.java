package za.ac.wits.cpd.service.twitconpro;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.net.ssl.HttpsURLConnection;
import lombok.extern.java.Log;
import org.apache.commons.codec.binary.Base64;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;


/**
 *
 * @author Matsobane Khwinana (Matsobane.Khwinana@momentum.co.za)
 */
@Log
@Stateless
public class Authenticator {
    @EJB
    private HttpHelper httpHelper;

    public String requestBearerToken(String endPointUrl) throws IOException {
        HttpsURLConnection connection = null;
        String encodedCredentials = encodeKeys(CONSUMER_KEY, CONSUMER_SECRET);

        try {
            connection = createConnectionToTwitter(endPointUrl, connection, encodedCredentials);

            boolean success = httpHelper.writeRequest(connection, GRANT_TYPE_CLIENT_CREDENTIALS);
            if (success) {
                // Parse the JSON response into a JSON mapped object to fetch fields from.
                JSONObject obj = (JSONObject) JSONValue.parse(httpHelper.readResponse(connection));
                
                if (obj != null) {
                    String tokenType = (String) obj.get("token_type");
                    String token = (String) obj.get("access_token");
                    
                    return ((tokenType.equals("bearer")) && (token != null)) ? token : "";
                }
            }
            return new String();
        } catch (MalformedURLException e) {
            throw new IOException("Invalid endpoint URL specified.", e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private static String encodeKeys(String consumerKey, String consumerSecret) {

        try {
            String encodedConsumerKey = URLEncoder.encode(consumerKey, "UTF-8");
            String encodedConsumerSecret = URLEncoder.encode(consumerSecret, "UTF-8");
            String fullKey = String.format(FULL_KEY_FORMAT, encodedConsumerKey, encodedConsumerSecret);
            byte[] encodedBytes = Base64.encodeBase64(fullKey.getBytes());
            return new String(encodedBytes);

        } catch (UnsupportedEncodingException e) {
            return new String();
        }
    }

    private static HttpsURLConnection createConnectionToTwitter(String endPointUrl, HttpsURLConnection connection, String encodedCredentials) throws ProtocolException, MalformedURLException, IOException {
        URL url = new URL(endPointUrl);
        connection = (HttpsURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setRequestMethod(POST);
        connection.setRequestProperty(HOST, TWITTER_API_HOST);
        connection.setRequestProperty(USER_AGENT, APP_NAME);
        connection.setRequestProperty(AUTHORIZATION, BASIC+SPACE+encodedCredentials);
        connection.setRequestProperty(CONTENT_TYPE, APPLICATION_XWWW);
        connection.setRequestProperty(CONTENT_LENGTH, TWENTY9);
        connection.setUseCaches(false);
        return connection;
    }

    private static final String CONSUMER_SECRET = "Kyi74Lxf3ItQ7QQKrFh2zhCc9u8SKa6oagD24fxwcnL1L9jrxH";
    private static final String CONSUMER_KEY = "iAPIXgQMc4KQe683V9WnhNpNL";
    private static final String FULL_KEY_FORMAT = "%s:%s";
    private static final String GRANT_TYPE_CLIENT_CREDENTIALS = "grant_type=client_credentials";
    private static final String TWENTY9 = "29";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String APPLICATION_XWWW = "application/x-www-form-urlencoded;charset=UTF-8";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String SPACE = " ";
    private static final String BASIC = "Basic";
    private static final String AUTHORIZATION = "Authorization";
    private static final String APP_NAME = "ELEN7046_GROUP2";
    private static final String USER_AGENT = "User-Agent";
    private static final String TWITTER_API_HOST = "api.twitter.com";
    private static final String HOST = "Host";
    private static final String POST = "POST";
    
}

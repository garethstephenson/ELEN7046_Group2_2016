package za.ac.wits.cpd.service.bigdatavisual;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HttpsURLConnection;
import lombok.NonNull;
import lombok.extern.java.Log;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 *
 * @author Matsobane Khwinana (Matsobane.Khwinana@momentum.co.za)
 */
@Log
public class TweetsDataExtractor {

    public List<Tweet> extract(@NonNull String oscarHashTagUrl) {
        return fetchTweets(oscarHashTagUrl);
    }

    public List<Tweet> extract(@NonNull String oscarHashTagUrl, int count) {
        if(count<1)throw new IllegalArgumentException("The 'count' parameter value must greater then 0");
        
        String url = String.format("%s&count=%d", oscarHashTagUrl,count);
        return fetchTweets(url);
    }

    public List<Tweet> extract(String oscarHashTagUrl, Map<String, String> options) {
        if(options.size()<1)throw new IllegalArgumentException("The 'options' parameter value must not be empty");
        
        StringBuilder urlBuilder = toQueryString(oscarHashTagUrl, options);
        return fetchTweets(urlBuilder.toString());
    }

    
    /**
     * Fetches tweets from given Twitter URL.
     *
     * @param endPointUrl
     * @return
     */
    public List<Tweet> fetchTweets(String endPointUrl) {
        try {

            JSONArray hashArray = retrieveTwitterDatum(endPointUrl);
            if (hashArray != null && !hashArray.isEmpty()) {
                List<Tweet> hashtagTweets = new ArrayList<>();
                for (int i=0;i<hashArray.size();i++) {
                    JSONObject tweetResp = (JSONObject) hashArray.get(i);
                    FileHelper.write(tweetResp);
                    Tweet tweet = TweetMapper.toTweet(tweetResp);
                    log.log(Level.SEVERE,"**** Number of tweets before determining the location: {0}", hashArray.size());
                    if(tweet.getGeoLocation()!=null)
                        hashtagTweets.add(tweet);
                }
                if (!hashtagTweets.isEmpty()) {
                    log.log(Level.SEVERE,"+++++ Number of tweets after determining the location: {0}", hashtagTweets.size());
                    return hashtagTweets;
                }

            }

        } catch (IOException ex) {
            Logger.getLogger(TweetsDataExtractor.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

   

    /**
     * Fetches a time tweet from a given user's account endpoint URL.
     *
     * @param endPointUrl
     * @return
     */
    public List<Tweet> fetchTimelineTweet(String endPointUrl){
        HttpsURLConnection connection = null;
        try {
            JSONArray jsonArray = retrieveTwitterData(endPointUrl);

            if (jsonArray != null) {
                Iterator iterator = jsonArray.iterator();
                List<Tweet> tweets = new ArrayList<>();
                while (iterator.hasNext()) {
                    JSONObject o = (JSONObject) iterator.next();
                    log.log(Level.INFO, "JSON tweet: {0}", o.toJSONString());
                    tweets.add(TweetMapper.toTweet(o));
                }

                if(!tweets.isEmpty())return tweets;
            }

        } catch (MalformedURLException e) {
           log.log(Level.SEVERE, "Unable to fetch timeline tweets", e);
        } catch (IOException ex) {
            log.log(Level.SEVERE, "Unable to fetch timeline tweets", ex);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        
        return null;
    }

    public List<String> streamData(String OSCAR_HASHTAG, int count) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    private JSONArray retrieveTwitterData(String twitterUrl) throws IOException {
        String bearerToken = Authenticator.requestBearerToken(TWITTER_API_OAUTH2_URL);
        if (isValidString(bearerToken)) {
            HttpsURLConnection connection = createConnectionToTwitter(twitterUrl, bearerToken);
            return (JSONArray) JSONValue.parse(HttpHelper.readResponse(connection));
        }

        return null;
    }

    private JSONArray retrieveTwitterDatum(String twitterUrl) throws IOException {
        /*
            "search_metadata":{
            "completed_in":0.081,"max_id":723817151379988500,"max_id_str":"723817151379988481",
            "next_results":"?max_id=723784616449060863&q=%23OscarPistorius&include_entities=1",
            "query":"%23OscarPistorius",
            "refresh_url":"?since_id=723817151379988481&q=%23OscarPistorius&include_entities=1",
            "count":15,"since_id":0,"since_id_str":"0"}
         */

        String bearerToken = Authenticator.requestBearerToken(TWITTER_API_OAUTH2_URL);
        if (isValidString(bearerToken)) {
            JSONArray resp = new JSONArray();
            String nextResults = "";
            
            //do {
                String urlWithCursor = twitterUrl +"&cursor=0"+ nextResults;
                System.out.println("url: "+  urlWithCursor);
                HttpsURLConnection connection = createConnectionToTwitter(urlWithCursor, bearerToken);
                final JSONObject obj = (JSONObject) JSONValue.parse(HttpHelper.readResponse(connection));
                resp.addAll((JSONArray)obj.get("statuses"));
                
//                System.out.println("************ RESPONSE*********\n"+obj.toJSONString());
//                JSONObject metadata = (JSONObject)obj.get("search_metadata");
//                nextResults = metadata.get("next_results").toString();
//            } while (nextResults != null && !nextResults.isEmpty());
                System.out.println("First 5 statuses returned!!!\n: " + resp.toJSONString());
            if(!resp.isEmpty()){
                System.out.println("\n ******** return the first 5 statuses ********" );
                return resp;
            }
        }

        return null;
    }

    private static HttpsURLConnection createConnectionToTwitter(String endPointUrl, String bearerToken) throws IOException, ProtocolException, MalformedURLException {
        URL url = new URL(endPointUrl);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setRequestMethod(GET);
        connection.setRequestProperty(HOST, TWITTER_API_HOST);
        connection.setRequestProperty(USER_AGENT, APP_NAME);
        connection.setRequestProperty(AUTHORIZATION, BEARER + SPACE + bearerToken);
        connection.setUseCaches(false);
        return connection;
    }

    private static boolean isValidString(String bearerToken) {
        return bearerToken != null && !bearerToken.isEmpty();
    }

    private StringBuilder toQueryString(String oscarHashTagUrl, Map<String, String> options) {
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(oscarHashTagUrl);
        for (String key : options.keySet())
            if(key.equalsIgnoreCase(SINCE) ||key.equalsIgnoreCase(UNTIL)){
                urlBuilder.append(ENCODED_SPACE).append(key).append(ENCODED_COLON).append(options.get(key));
            }else{
                urlBuilder.append(AMPERSAND).append(key).append(EQUAL_SIGN).append(options.get(key));
            }
        
        return urlBuilder;
    }
    private static final String UNTIL = "until";
    private static final String SINCE = "since";
    private static final String ENCODED_COLON = "%3A";
    private static final String ENCODED_SPACE = "%20";
    
    private static final String EQUAL_SIGN = "=";
    private static final String AMPERSAND = "&";
    private static final String TWITTER_API_OAUTH2_URL = "https://api.twitter.com/oauth2/token";
    private static final String TWITTER_API_HOST = "api.twitter.com";
    private static final String AUTHORIZATION = "Authorization";
    private static final String APP_NAME = "ELEN7046_GROUP2";
    private static final String USER_AGENT = "User-Agent";
    private static final String BEARER = "Bearer";
    private static final String HOST = "Host";
    private static final String TEXT = "text";
    private static final String GET = "GET";
    private static final String SPACE = " ";
    private static final String EMPTY = "";
}

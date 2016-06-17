package za.ac.wits.cpd.service.twitconpro.impl;

import za.ac.wits.cpd.service.twitconpro.api.Tweet;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.ejb.EJB;
import javax.ejb.Stateless;
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
@Stateless
public class TweetsDataExtractor {
    
    @EJB
    private TweetMapper mapper;
    
    @EJB
    private HttpHelper httpHelper;
    
    @EJB
    private Authenticator authenticator;

    public List<Tweet> extractHashtagTweets(@NonNull String hashtag) {
        return fetchTweets(String.format(HASHTAG_URL_FORMAT, validateHashtag(hashtag)));
    }

    public List<Tweet> extractHashtagsTweets(@NonNull List<String> hashtags, Map<String, String> options) {
        validateOptions(options);
        
        StringBuilder queryStringBuilder = new StringBuilder();
        for (int x = 0;x<hashtags.size();x++) {
            queryStringBuilder.append(validateHashtag(hashtags.get(x)));
            queryStringBuilder.append(((x+1)!=hashtags.size())?"%20OR%20":"");
        }
        
        StringBuilder urlBuilder = toQueryString(queryStringBuilder.toString(), options);
        return fetchTweets(urlBuilder.toString());
    }
    public List<Tweet> extractHashtagTweets(@NonNull String hashtag, int count) {
        validateCount(count);

        String queryString = String.format("%s&count=%d", validateHashtag(hashtag), count);
        String url = String.format(HASHTAG_URL_FORMAT, queryString);
        return fetchTweets(url);
    }


    public List<Tweet> extractHashtagTweets(String hashtag, Map<String, String> options) {
        validateOptions(options);
        
        StringBuilder urlBuilder = toQueryString(validateHashtag(hashtag), options);
        return fetchTweets(urlBuilder.toString());
    }

    public Tweet extractTweetById(Long twitterID) {
        final String url = String.format(TWEET_BY_ID_URL, twitterID);
        JSONObject payload = retrieveTweetStatus(url);
        if (payload != null) {
            return mapper.toTweet(payload);
        }

        return null;
    }

    public List<String> streamData(String OSCAR_HASHTAG, int count) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    public List<Tweet> extractTimelineTweets(String endPointUrl) {
        return retrieveTimeline(endPointUrl);
    }

    private void validateOptions(Map<String, String> options) throws IllegalArgumentException {
        if (options.size() < 1) {
            throw new IllegalArgumentException("The 'options' parameter value must not be empty");
        }
    }

    private JSONObject retrieveTweetStatus(String twitterUrl) {
        try {
            String bearerToken = authenticator.requestBearerToken(TWITTER_API_OAUTH2_URL);
            if (isValidString(bearerToken)) {
                HttpsURLConnection connection = createConnectionToTwitter(twitterUrl, bearerToken);
                final JSONObject obj = (JSONObject) JSONValue.parse(httpHelper.readResponse(connection));
                if (obj != null) {
                    return obj;
                }
            }
        } catch (IOException ex) {
            log.log(Level.SEVERE, "Failed to intepret the twitter response", ex);
        }

        return null;
    }
    
    private JSONArray retrieveTwitterData(String twitterUrl) throws IOException {
        String bearerToken = authenticator.requestBearerToken(TWITTER_API_OAUTH2_URL);
        if (isValidString(bearerToken)) {
            HttpsURLConnection connection = createConnectionToTwitter(twitterUrl, bearerToken);
            return (JSONArray) JSONValue.parse(httpHelper.readResponse(connection));
        }

        return null;
    }

    private JSONArray retrieveTwitterStatuses(String twitterUrl) throws IOException {
        String bearerToken = authenticator.requestBearerToken(TWITTER_API_OAUTH2_URL);
        if (isValidString(bearerToken)) {
            JSONArray resp = new JSONArray();
            String urlWithCursor = twitterUrl + "&cursor=0";
            log.log(Level.INFO, "url: {0}", urlWithCursor);
            HttpsURLConnection connection = createConnectionToTwitter(urlWithCursor, bearerToken);
            final JSONObject obj = (JSONObject) JSONValue.parse(httpHelper.readResponse(connection));
            resp.addAll((JSONArray) obj.get(STATUSES));

            if (!resp.isEmpty()) {
                return resp;
            }
        }

        return null;
    }
    
    private List<Tweet> fetchTweets(String endPointUrl) {
        try {

            JSONArray hashArray = retrieveTwitterStatuses(endPointUrl);
            if (hashArray != null && !hashArray.isEmpty()) {
                List<Tweet> hashtagTweets = new ArrayList<>();
                for (int i = 0; i < hashArray.size(); i++) {
                    JSONObject tweetResp = (JSONObject) hashArray.get(i);
                    Tweet tweet = this.mapper.toTweet(tweetResp);
                    log.log(Level.INFO, "**** Number of tweets before determining the location: {0}", hashArray.size());
                    if (tweet.getGeoLocation() != null) {
                        hashtagTweets.add(tweet);
                    }
                }
                if (!hashtagTweets.isEmpty()) {
                    log.log(Level.SEVERE, "+++++ Number of tweets after determining the location: {0}", hashtagTweets.size());
                    return hashtagTweets;
                }

            }

        } catch (IOException ex) {
            log.log(Level.SEVERE, null, ex);
        }

        return null;
    }

    private List<Tweet> retrieveTimeline(String endPointUrl) {
        HttpsURLConnection connection = null;
        try {
            JSONArray jsonArray = retrieveTwitterData(endPointUrl);
            if (jsonArray != null) {
                Iterator iterator = jsonArray.iterator();
                List<Tweet> tweets = new ArrayList<>();
                while (iterator.hasNext())
                    tweets.add(this.mapper.toTweet((JSONObject) iterator.next()));
                if (!tweets.isEmpty()) return tweets;
            }
        }catch (MalformedURLException e) {
            log.log(Level.SEVERE, "Unable to fetch timeline tweets", e);
        }catch (IOException ex) {
            log.log(Level.SEVERE, "Unable to fetch timeline tweets", ex);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return null;
    }

    private String validateHashtag(String hashtag) {
        hashtag = urlEncodeHashtag(hashtag);
        hashtag = prependEncodedHashtagSign(hashtag);
        return hashtag;
    }

    private String prependEncodedHashtagSign(String hashtag) {
        hashtag = !hashtag.startsWith("%23")?"%23".concat(hashtag):hashtag;
        return hashtag;
    }

    private String urlEncodeHashtag(String hashtag) {
        hashtag = hashtag.startsWith("#")?hashtag.replace("#", "%23"):hashtag;
        return hashtag;
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

    private StringBuilder toQueryString(String hashtagParams, Map<String, String> options) {
        StringBuilder urlBuilder = new StringBuilder();
        
        urlBuilder.append(String.format(HASHTAG_URL_FORMAT, hashtagParams));
        options.keySet().stream().forEach((key) -> {
            if (key.equalsIgnoreCase(SINCE) || key.equalsIgnoreCase(UNTIL)) {
                urlBuilder.append(ENCODED_SPACE).append(key).append(ENCODED_COLON).append(options.get(key));
            } else {
                urlBuilder.append(AMPERSAND).append(key).append(EQUAL_SIGN).append(options.get(key));
            }
        });

        return urlBuilder;
    }
    
    private void validateCount(int count) throws IllegalArgumentException {
        if (count < 1) {
            throw new IllegalArgumentException("The 'count' parameter value must greater then 0");
        }
    }
    
    private static final String UNTIL = "until";
    private static final String SINCE = "since";
    private static final String EQUAL_SIGN = "=";
    private static final String AMPERSAND = "&";
    private static final String ENCODED_COLON = "%3A";
    private static final String ENCODED_SPACE = "%20";
    private static final String HASHTAG_URL_FORMAT = "https://api.twitter.com/1.1/search/tweets.json?q=%s";
    private static final String TWEET_BY_ID_URL = "https://api.twitter.com/1.1/statuses/show.json?id=%d";
    private static final String TWITTER_API_OAUTH2_URL = "https://api.twitter.com/oauth2/token";
    private static final String TWITTER_API_HOST = "api.twitter.com";
    private static final String AUTHORIZATION = "Authorization";
    private static final String APP_NAME = "ELEN7046_GROUP2";
    private static final String USER_AGENT = "User-Agent";
    private static final String STATUSES = "statuses";
    private static final String BEARER = "Bearer";
    private static final String HOST = "Host";
    private static final String TEXT = "text";
    private static final String GET = "GET";
    private static final String SPACE = " ";
    private static final String EMPTY = "";

}

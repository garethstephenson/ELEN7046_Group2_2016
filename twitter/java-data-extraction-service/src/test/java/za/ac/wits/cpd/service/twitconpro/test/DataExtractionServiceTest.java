package za.ac.wits.cpd.service.twitconpro.test;


import java.util.HashMap;
import java.util.Map;
import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import lombok.extern.java.Log;
import static org.hamcrest.CoreMatchers.is;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import static org.hamcrest.CoreMatchers.is;

/**
 *
 * @author Matsobane Khwinana (Matsobane.Khwinana@momentum.co.za)
 */
@Log
public class DataExtractionServiceTest {
    private Client client;

    @Before
    public void initClient(){
        this.client = ClientBuilder.newClient();
    }
    
    @Test
    public void testHello(){
        //Given
        WebTarget target = this.client.target("http://localhost:8080/java-data-extraction-service/rest/extract-data");
        
        //When
        Response response = target.request(MediaType.APPLICATION_JSON).accept(MediaType.TEXT_PLAIN).get();
        
        //Then
        assertThat(response.getStatus(), is(200));
        String payload = response.readEntity(String.class);
        log.severe(payload);
    }
    
    @Test
    public void testExtractAndStoreById() {
        //Given
        Long twitterId = 729674502339055617L;
        final String url = String.format(tweetByIdPathFormat,twitterId);
        log.severe(url);
        WebTarget target = this.client.target(url);
        
        //When
        Response response = target.request(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON).get();

        //When
        assertThat(response.getStatus(), is(200));
        JsonObject tweet = response.readEntity(JsonObject.class);
        log.severe(tweet.toString());
        assertTrue(tweet.getJsonNumber("twitterId").toString().startsWith(twitterId.toString()));
    }
    
//    @Ignore
//    @Test
//    public void testExtractAndStoreTrumpTweetsDuringPrimaries() {
//        
//        //Given
//        final List<String> hashtags = new ArrayList<>();
//        hashtags.add("donaldtrump");
//        hashtags.add("MakeAmericaGreatAgain");
//        hashtags.add("#nevertrump");
//        final Map<String, String> options = hundredTweetsDuringPrimariesOptions();
//        
//        final String url = String.format("http://localhost:8080/java-data-extraction-service/rest/extract-data/hashtags");
//        log.severe(url);
//        WebTarget target = this.client.target(url);
//        
//        //When
//        Response response = target.request(MediaType.APPLICATION_JSON)
//                            .accept(MediaType.APPLICATION_JSON).post(hashtags,List.class);
//
//        //When
//        assertThat(response.getStatus(), is(200));
//        String tweet = response.readEntity(String.class);
//        assertTrue(tweet.contains(twitterId.toString()));
//        
//        
//        
//        //Given
//
//
//        //When
//        List<Tweet> tweetData = this.dataExtractor.extractHashtagsTweets(hashtags, options);
//        for (Tweet tweet : tweetData) {
//            this.persistenceManager.persist(tweet);
//            Tweet dbTweet = persistenceManager.findByTwitterId(tweet.getTwitterId());
//
//            //Then 
//            //assertNotNull(dbTweet);
//        }
//    }
//
//    @Ignore
//    @Test
//    public void testExtractTrumpAndHilary10Times() {
//        for (int i = 0; i < 10; i++) {
//            log.log(Level.SEVERE, "Hilary attempt # {0}", i);
//            testExtractAndStoreHilaryTweetsDuringPrimaries();
//            log.log(Level.SEVERE, "Trump attempt # {0}", i);
//            testExtractAndStoreTrumpTweetsDuringPrimaries();
//        }
//    }
//
//
//    @Ignore
//    @Test
//    public void testExtractAndStoreHilaryTweetsDuringPrimaries() {
//        //https://twitter.com/search?q=%23hillaryclinton%20OR%20%23iamwither%20OR%20%23crookedhillary%20since%3A2016-02-01%20until%3A2016-06-07
//        //Given
//        final List<String> hashtags = new ArrayList<>();
//        hashtags.add("#iamwither");
//        hashtags.add("#hillaryclinton");
//        hashtags.add("#crookedhillary");
//
//        final Map<String, String> options = hundredTweetsDuringPrimariesOptions();
//
//        //When
//        List<Tweet> tweetData = this.dataExtractor.extractHashtagsTweets(hashtags, options);
//        for (Tweet tweet : tweetData) {
//            this.persistenceManager.persist(tweet);
//            Tweet dbTweet = persistenceManager.findByTwitterId(tweet.getTwitterId());
//
//            //Then 
//            //assertNotNull(dbTweet);
//        }
//    }

    private Map<String, String> hundredTweetsDuringPrimariesOptions() {
        final Map<String, String> options = new HashMap<>();
        options.put(COUNT, HUNDRED);
        options.put(SINCE, PRIMARIES_START_DATE);
        options.put(UNTIL, PRIMARIES_END_DATE);
        return options;
    }

    private final String tweetByIdPathFormat = "http://localhost:8080/java-data-extraction-service/rest/extract-data/id/%d";
    private static final String PRIMARIES_START_DATE = "2016-03-15";
    private static final String PRIMARIES_END_DATE = "2016-03-30";
    private static final String COUNT = "count";
    private static final String SINCE = "since";
    private static final String UNTIL = "until";
    private static final String HUNDRED = "100";
    private static final String FIVE = "5";
}

package za.ac.wits.cpd.service.twitconpro.test;


import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import lombok.extern.java.Log;
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
    public void testPing(){
        //Given
        WebTarget target = this.client.target("http://localhost:8080/historic-data-extraction-service/rest/extract-data");
        
        //When
        Response response = target.request(MediaType.APPLICATION_JSON).accept(MediaType.TEXT_PLAIN).get();
        
        //Then
        assertThat(response.getStatus(), is(200));
        String payload = response.readEntity(String.class);
        log.severe(payload);
    }
    
    @Test
    public void testExtractById() {
        //Given
        Long twitterId = 729674502339055617L;
        final String url = String.format(extractTweetByIdPathFormat,twitterId);
        WebTarget target = this.client.target(url);
        log.info(target.getUri().toString());
        
        //When
        Response response = target.request(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON).get();

        //When
        assertThat(response.getStatus(), is(200));
        JsonObject tweet = response.readEntity(JsonObject.class);
        log.severe(tweet.toString());
        assertTrue(tweet.getJsonNumber("twitterId").toString().startsWith(twitterId.toString()));
    }
    
    @Test
    public void testExtractAndStoreById() {
        //Given
        Long twitterId = 729674502339055617L;
        final String url = String.format(extractAndPersistTweetByIdPathFormat,twitterId);
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
    
    @Test
    public void testExtractHistoricEFFTweetsByHashtag(){
        //Given 
        String hashtag = "EFFmanifesto";
        WebTarget target = this.client.target(extractHistoryTweetsByHashtagUrl);
        
        //When
        Response response = target.queryParam("hashtag",hashtag)
                                .queryParam(COUNT, 900)
                                .queryParam(SINCE, EFF_MANIFESTO_LAUNCH_DATE)
                                .queryParam(UNTIL, THIRDY_DAYS_AFTER_EFF_MANIFESTO_LAUNCH)
                                .request(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .get();
        
        //Then
        assertThat(response.getStatus(), is(200));
        JsonArray tweets = response.readEntity(JsonArray.class);
        assertTrue(tweets.size()>0);
    } 
    
    @Test
    public void testExtractAndPersistHistoricEFFTweetsByHashtag(){
        //Given 
        WebTarget target = this.client.target(extractAndPersitHistoryTweetsByHashtagUrl);
        target = target.queryParam(HASHTAG,"EFFmanifesto")
                                .queryParam(COUNT, 900)
                                .queryParam(SINCE, EFF_MANIFESTO_LAUNCH_DATE)
                                .queryParam(UNTIL, THIRDY_DAYS_AFTER_EFF_MANIFESTO_LAUNCH);
        
        log.severe(target.getUri().toString());
        
        //When
        Response response = target.request(MediaType.APPLICATION_JSON)
                                    .accept(MediaType.APPLICATION_JSON)
                                    .get();
        
        
        //Then
        assertThat(response.getStatus(), is(200));
        JsonArray tweets = response.readEntity(JsonArray.class);
        assertTrue(tweets.size()>0);
    }       
    
    @Test
    public void testExtractHistoricTrumpTweetsDuringPrimaries() {
        //Given    
        log.severe(extractHistoryTweetsByHashtagsUrl);        
        WebTarget target = this.client.target(extractAndPersitHistoryTweetsByHashtagsUrl);
        
        //When
        Response response = target.queryParam(HASHTAGS,"donaldtrump")
                                .queryParam(HASHTAGS, "MakeAmericaGreatAgain")
                                .queryParam(HASHTAGS, "#nevertrump")
                                .queryParam(COUNT, 900)
                                .queryParam(SINCE, PRIMARIES_START_DATE)
                                .queryParam(UNTIL, PRIMARIES_END_DATE)
                                .request(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .get();

        //Then
        assertThat(response.getStatus(), is(200));
        JsonArray tweets = response.readEntity(JsonArray.class);
        assertTrue(tweets.size()>0);
    }
    
    @Test
    public void testExtractAndStoreHistoricTrumpTweetsDuringPrimaries() {
        //Given    
        log.severe(extractHistoryTweetsByHashtagsUrl);        
        WebTarget target = this.client.target(extractHistoryTweetsByHashtagsUrl);
        
        //When
        Response response = target.queryParam(HASHTAGS,"donaldtrump")
                                .queryParam(HASHTAGS, "MakeAmericaGreatAgain")
                                .queryParam(HASHTAGS, "#nevertrump")
                                .queryParam(COUNT, 900)
                                .queryParam(SINCE, PRIMARIES_START_DATE)
                                .queryParam(UNTIL, PRIMARIES_END_DATE)
                                .request(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .get();

        //Then
        assertThat(response.getStatus(), is(200));
        JsonArray tweets = response.readEntity(JsonArray.class);
        assertTrue(tweets.size()>0);
    }

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

    private final String extractTweetByIdPathFormat = "http://localhost:8080/historic-data-extraction-service/rest/extract-data/byId/%d";
    private final String extractHistoryTweetsByHashtagUrl = "http://localhost:8080/historic-data-extraction-service/rest/extract-data/historyByHashtag";
    private final String extractHistoryTweetsByHashtagsUrl = "http://localhost:8080/historic-data-extraction-service/rest/extract-data/historyByHashtags";
    private final String extractAndPersistTweetByIdPathFormat = "http://localhost:8080/historic-data-extraction-service/rest/extract-data/byIdAndPersist/%d";
    private final String extractAndPersitHistoryTweetsByHashtagUrl = "http://localhost:8080/historic-data-extraction-service/rest/extract-data/extractAndPersistHistoryByHashtag";
    private final String extractAndPersitHistoryTweetsByHashtagsUrl = "http://localhost:8080/historic-data-extraction-service/rest/extract-data/extractAndPersistHistoryByHashtags";
    
    private static final String THIRDY_DAYS_AFTER_EFF_MANIFESTO_LAUNCH = "2016-05-30";
    private static final String EFF_MANIFESTO_LAUNCH_DATE = "2016-04-30";
    private static final String PRIMARIES_START_DATE = "2016-03-15";
    private static final String PRIMARIES_END_DATE = "2016-03-30";
    private static final String HASHTAGS = "hashtags";
    private static final String HASHTAG = "hashtag";
    private static final String COUNT = "count";
    private static final String SINCE = "since";
    private static final String UNTIL = "until";
}

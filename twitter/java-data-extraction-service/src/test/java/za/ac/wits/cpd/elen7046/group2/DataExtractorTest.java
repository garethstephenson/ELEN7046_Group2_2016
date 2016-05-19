package za.ac.wits.cpd.elen7046.group2;

import java.util.Arrays;
import za.ac.wits.cpd.service.bigdatavisual.Tweet;
import za.ac.wits.cpd.service.bigdatavisual.TweetsDataExtractor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import lombok.extern.java.Log;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Matsobane Khwinana (Matsobane.Khwinana@momentum.co.za)
 */
@Log
public class DataExtractorTest {

    private TweetsDataExtractor extractor;

    @Before
    public void setUp() {
        this.extractor = new TweetsDataExtractor();
    }

    @Test
    public void testFetchTime() {
        //Given
        final String url = MATSO_ACCOUNT_TIMELINE;

        //When
        List<Tweet> tweets = this.extractor.extractTimelineTweets(url);

        //Then
        assertTrue(tweets != null && !tweets.isEmpty());
        assertTrue(tweets.get(0).getText().contains("Twitter API"));

    }

    @Test
    public void testExtractData() {
        //Given
        final String hashtag = "OscarPistorius";

        //When
        List<Tweet> tweetData = this.extractor.extractHashtagTweets(hashtag);

        //Then
        assertTrue(tweetData.size() == 13);
    }

    /* Negative test */
    @Test(expected = NullPointerException.class)
    public void testExtractNullUrlData() {
        //Given
        final String oscarHashTagUrl = null;

        //When
        List<Tweet> tweetData = this.extractor.extractHashtagTweets(oscarHashTagUrl);

        //Then
        assertTrue(tweetData.size() == 15);
    }

    @Test
    public void testExtractDataWithCount() {
        //Given
        final int count = 5;
        final String hashtag = "OscarPistorius";
        
        //When
        List<Tweet> tweetData = this.extractor.extractHashtagTweets(hashtag, count);

        //Then
        assertTrue(tweetData.size() == count);
    }

    @Test
    public void testExtractDataWithOptions() {
        //Given
        final String hashtag = "MakeAmericaGreatAgain";
        final Map<String, String> options = fiveTweetsBeforeNewYorkOptions();

        //When
        List<Tweet> tweetData = this.extractor.extractHashtagTweets(hashtag, options);

        //Then
        if (tweetData != null && !tweetData.isEmpty()) {
            for (Tweet tweet : tweetData){
                log.log(Level.SEVERE, "+++++++ tweet: {0}", tweet.toString());
                assertTrue(Arrays.toString(tweet.getHashtags()).contains(hashtag));
            }
        } else {
            fail("Tweet data returned was null or empty");
        }

    }

    @Test(expected = UnsupportedOperationException.class)
    public void testStreamData() {
        //Given
        final int count = 5; //&count=5;
        final String hashtag = "OscarPistorious";

        //When
        List<String> hashtagData = this.extractor.streamData(hashtag, count);

        //Then
        assertTrue(hashtagData.size() == 5);
    }

    @Test
    public void testFindTweetByID() {
        //Given
        final Long tweetId = 729674502339055617L;
        
        //When
        Tweet tweet = this.extractor.extractTweetById(tweetId);

        //Then
        assertTrue(Objects.equals(tweet.getTwitterId(), tweetId));
        assertTrue("URL returned " + tweet.getUrl(), EXPECTED_TWEET_URL.equalsIgnoreCase(tweet.getUrl()));
    }

    @Test
    public void testUrlFormating(){
        //Given
        String hashtag = "OscarPistorius";
        hashtag = hashtag.startsWith("#")?hashtag.replace("#", "%23"):hashtag;
        hashtag = !hashtag.startsWith("%23")?"%23".concat(hashtag):hashtag;
        
        //When
        final String url = String.format(HASHTAG_URL_FORMAT,hashtag);
        log.severe(url);
                
        
        //Then
        assertTrue("https://api.twitter.com/1.1/search/tweets.json?q=%23OscarPistorius".equalsIgnoreCase(url));
    }
    
    private Map<String, String> fiveTweetsBeforeJudgementDayOptions() {
        //https://twitter.com/search?q=%23OscarPistorius%20since%3A2014-02-14%20until%3A2014-08-11
        final Map<String, String> options = new HashMap<>();
        options.put(COUNT, FIVE);
        options.put(SINCE, INCIDENT_DAY);
        options.put(UNTIL, JUDGEMENT_DAY);
        return options;
    }
    
    private Map<String, String> fiveTweetsBeforeNewYorkOptions() {
        //https://twitter.com/search?q=%23MakeAmericaGreatAgain%20since%3A2016-03-19%20until%3A2016-04-19
        final Map<String, String> options = new HashMap<>();
        options.put(COUNT, FIVE);
        options.put(SINCE, NY_PRIMARY_THIRTY_DAYS_PRIOR);
        options.put(UNTIL, NY_PRIMARY_2016);
        return options;
    }

    private static final String EXPECTED_TWEET_URL = "https://twitter.com/leilanitexas/status/729674502339055617";
    
    private static final String MATSO_ACCOUNT_TIMELINE = "https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=MatsobaneCarl&count=2";
    private static final String HASHTAG_URL_FORMAT = "https://api.twitter.com/1.1/search/tweets.json?q=%s";
    private static final String NY_PRIMARY_THIRTY_DAYS_PRIOR = "2016-03-19";
    private static final String NY_PRIMARY_2016 = "2016-04-19";
    private static final String JUDGEMENT_DAY = "2014-09-11";
    private static final String INCIDENT_DAY = "2014-02-14";
    private static final String COUNT = "count";
    private static final String SINCE = "since";
    private static final String UNTIL = "until";
    private static final String FIVE = "5";
}

package za.ac.wits.cpd.elen7046.group2;

import za.ac.wits.cpd.service.bigdatavisual.Tweet;
import za.ac.wits.cpd.service.bigdatavisual.TweetsDataExtractor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Matsobane Khwinana (Matsobane.Khwinana@momentum.co.za)
 */
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
        List<Tweet> tweets = this.extractor.fetchTimelineTweet(url);

        //Then
        assertTrue(tweets!= null&&!tweets.isEmpty());
        assertTrue(tweets.get(0).getText().contains("Twitter API"));
        
    }
    
    @Test
    public void testExtractData() {
        //Given
        final String oscarHashTagUrl = OSCAR_HASHTAG_URL;

        //When
        List<Tweet> tweetData = this.extractor.extract(oscarHashTagUrl);

        //Then
        assertTrue(tweetData.size() == 15);
    }

    /* Negative test */
    @Test(expected = NullPointerException.class)
    public void testExtractNullUrlData() {
        //Given
        final String oscarHashTagUrl = null;

        //When
        List<Tweet> tweetData = this.extractor.extract(oscarHashTagUrl);

        //Then
        assertTrue(tweetData.size() == 15);
    }

    @Test
    public void testExtractDataWithCount() {
        //Given
        final int count = 5;
        final String oscarHashTagUrl = OSCAR_HASHTAG_URL;
        //When
        List<Tweet> tweetData = this.extractor.extract(oscarHashTagUrl, count);

        //Then
        assertTrue(tweetData.size() == count);
    }
    
    @Test
    public void testExtractDataWithOptions() {
        //Given
        Map<String, String> options = fiveTweetsBeforeJudgementDayOptions();
        final String oscarHashTagUrl = OSCAR_HASHTAG_URL;
        
        //When
        List<Tweet> tweetData = this.extractor.extract(oscarHashTagUrl, options);

        //Then
        if (tweetData!=null&&!tweetData.isEmpty()) {
            for (Tweet tweet : tweetData) {
                System.out.println("+++++++ tweet: "+ tweet.toString());
            }
            assertTrue(tweetData.size() == Integer.valueOf(options.get(COUNT)));
        }else{
            fail("Tweet data returned was null or empty");
        }
            
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testStreamData() {
        //Given
        final int count = 5; //&count=5;
        final String oscarHashTagUrl = OSCAR_HASHTAG_URL;

        //When
        List<String> hashtagData = this.extractor.streamData(oscarHashTagUrl, count);

        //Then
        assertTrue(hashtagData.size() == 5);
    }

    private Map<String, String> fiveTweetsBeforeJudgementDayOptions() {
        //https://twitter.com/search?q=%23OscarPistorius%20since%3A2014-02-14%20until%3A2014-08-11
        final Map<String, String> options = new HashMap<>();
        options.put(COUNT, "5");
        options.put(SINCE, INCIDENT_DAY);
        options.put(UNTIL, JUDGEMENT_DAY);
        return options;
    }
    private static final String INCIDENT_DAY = "2014-02-14";
    
    
    private static final String MATSO_ACCOUNT_TIMELINE = "https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=MatsobaneCarl&count=2";
    private static final String OSCAR_HASHTAG_URL = "https://api.twitter.com/1.1/search/tweets.json?q=%23OscarPistorius";
    private static final String JUDGEMENT_DAY = "2014-09-11";
    private static final String COUNT = "count";
    private static final String SINCE = "since";
    private static final String UNTIL = "until";
    private static final String FIVE = "5";
}

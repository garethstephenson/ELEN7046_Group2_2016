package za.ac.wits.cpd.service.twitconpro.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import lombok.extern.java.Log;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;
import za.ac.wits.cpd.service.twitconpro.PersistenceManager;
import za.ac.wits.cpd.service.twitconpro.Tweet;
import za.ac.wits.cpd.service.twitconpro.TweetsDataExtractor;

/**
 *
 * @author Matsobane Khwinana (Matsobane.Khwinana@momentum.co.za)
 */
@Log
public class ExtractAndStoreTweetsTest {

    private PersistenceManager persistenceManager;
    private TweetsDataExtractor dataExtractor;

    @Before
    public void setUp() {
        this.persistenceManager = new PersistenceManager();
        this.dataExtractor = new TweetsDataExtractor();
    }

    @Ignore
    @Test
    public void testExtractAndStoreById() {
        //Given
        Long twitterId = 729674502339055617L;

        //When
        Tweet tweet = this.dataExtractor.extractTweetById(twitterId);
        this.persistenceManager.persist(tweet);
        Tweet dbTweet = persistenceManager.findByTwitterId(tweet.getTwitterId());

        //Then
        assertTrue(Objects.equals(dbTweet.getTwitterId(), twitterId));
    }

    @Test
    public void testExtractTrumpAndHilary10Times() {
        for (int i = 0; i < 10; i++) {
            log.log(Level.SEVERE, "Hilary attempt # {0}", i);
            testExtractAndStoreHilaryTweetsDuringPrimaries();
            log.log(Level.SEVERE, "Trump attempt # {0}", i);
            testExtractAndStoreTrumpTweetsDuringPrimaries();
        }
    }

    @Test
    public void testExtractAndStoreTrumpTweetsDuringPrimaries() {
        //Given
        final List<String> hashtags = new ArrayList<>();
        hashtags.add("donaldtrump");
        hashtags.add("MakeAmericaGreatAgain");
        hashtags.add("#nevertrump");

        final Map<String, String> options = hundredTweetsDuringPrimariesOptions();

        //When
        List<Tweet> tweetData = this.dataExtractor.extractHashtagsTweets(hashtags, options);
        for (Tweet tweet : tweetData) {
            this.persistenceManager.persist(tweet);
            Tweet dbTweet = persistenceManager.findByTwitterId(tweet.getTwitterId());

            //Then 
            //assertNotNull(dbTweet);
        }
    }

    @Test
    public void testExtractAndStoreHilaryTweetsDuringPrimaries() {
        //https://twitter.com/search?q=%23hillaryclinton%20OR%20%23iamwither%20OR%20%23crookedhillary%20since%3A2016-02-01%20until%3A2016-06-07
        //Given
        final List<String> hashtags = new ArrayList<>();
        hashtags.add("#iamwither");
        hashtags.add("#hillaryclinton");
        hashtags.add("#crookedhillary");

        final Map<String, String> options = hundredTweetsDuringPrimariesOptions();

        //When
        List<Tweet> tweetData = this.dataExtractor.extractHashtagsTweets(hashtags, options);
        for (Tweet tweet : tweetData) {
            this.persistenceManager.persist(tweet);
            Tweet dbTweet = persistenceManager.findByTwitterId(tweet.getTwitterId());

            //Then 
            //assertNotNull(dbTweet);
        }
    }

    private Map<String, String> hundredTweetsDuringPrimariesOptions() {
        final Map<String, String> options = new HashMap<>();
        options.put(COUNT, HUNDRED);
        options.put(SINCE, PRIMARIES_START_DATE);
        options.put(UNTIL, PRIMARIES_END_DATE);
        return options;
    }

    private static final String PRIMARIES_START_DATE = "2016-03-15";
    private static final String PRIMARIES_END_DATE = "2016-03-30";
    private static final String COUNT = "count";
    private static final String SINCE = "since";
    private static final String UNTIL = "until";
    private static final String HUNDRED = "100";
    private static final String FIVE = "5";
}

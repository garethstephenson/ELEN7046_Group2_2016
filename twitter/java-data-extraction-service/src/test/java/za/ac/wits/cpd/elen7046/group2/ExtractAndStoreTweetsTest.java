package za.ac.wits.cpd.elen7046.group2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import za.ac.wits.cpd.service.bigdatavisual.PersistenceManager;
import za.ac.wits.cpd.service.bigdatavisual.Tweet;
import za.ac.wits.cpd.service.bigdatavisual.TweetsDataExtractor;

/**
 *
 * @author Matsobane Khwinana (Matsobane.Khwinana@momentum.co.za)
 */
public class ExtractAndStoreTweetsTest {

    private PersistenceManager persistenceManager;
    private TweetsDataExtractor dataExtractor;

    @Before
    public void setUp() {
        this.persistenceManager = new PersistenceManager();
        this.dataExtractor = new TweetsDataExtractor();
    }

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
    public void testExtractAndStoreTrumpPriorToNYPrimary() {
        //Given
        final String hashtag = "MakeAmericaGreatAgain";
        final Map<String, String> options = fiveTweetsBeforeNewYorkOptions();

        //When
        List<Tweet> tweetData = this.dataExtractor.extractHashtagTweets(hashtag, options);
        for (Tweet tweet : tweetData) {
            this.persistenceManager.persist(tweet);
            Tweet dbTweet = persistenceManager.findByTwitterId(tweet.getTwitterId());
            
            //Then 
            assertTrue(Objects.equals(dbTweet.getTwitterId(), tweet.getTwitterId()));
        }
    }

    private Map<String, String> fiveTweetsBeforeNewYorkOptions() {
        //https://twitter.com/search?q=%23MakeAmericaGreatAgain%20since%3A2016-03-19%20until%3A2016-04-19
        final Map<String, String> options = new HashMap<>();
        options.put(COUNT, FIVE);
        options.put(SINCE, NY_PRIMARY_THIRTY_DAYS_PRIOR);
        options.put(UNTIL, NY_PRIMARY_2016);
        return options;
    }
    
    private static final String NY_PRIMARY_THIRTY_DAYS_PRIOR = "2016-03-19";
    private static final String NY_PRIMARY_2016 = "2016-04-19";
    private static final String COUNT = "count";
    private static final String SINCE = "since";
    private static final String UNTIL = "until";
    private static final String FIVE = "5";
}

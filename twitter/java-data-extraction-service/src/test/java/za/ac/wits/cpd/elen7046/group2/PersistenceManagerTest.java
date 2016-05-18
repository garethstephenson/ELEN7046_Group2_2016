package za.ac.wits.cpd.elen7046.group2;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Objects;
import java.util.TimeZone;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import za.ac.wits.cpd.service.bigdatavisual.Coordinate;
import za.ac.wits.cpd.service.bigdatavisual.GeoLocation;
import za.ac.wits.cpd.service.bigdatavisual.PersistenceManager;
import za.ac.wits.cpd.service.bigdatavisual.Tweet;

/**
 *
 * @author Matsobane Khwinana (Matsobane.Khwinana@momentum.co.za)
 */
public class PersistenceManagerTest {

    private PersistenceManager persistManager;
    
    @Before
    public void setUp() {
        this.persistManager = new PersistenceManager();
    }
    
    @Test
    public void testPersistTweet(){
        //Given
        Tweet tweet = createTweetWithoutLocation();
        tweet.setGeoLocation(createGeoLocation());

        //When
        this.persistManager.persist(tweet);
        
        //Then
        assertNotNull(this.persistManager.findAll());
        
    }

    @Test
    public void testFindByTwitterId(){
        //Given
        Long tweetId = 731559551053496320L;
        
        //When
        Tweet tweet = this.persistManager.findByTwitterId(tweetId);
        
        //Then
        assertNotNull(tweet);
        assertTrue(Objects.equals(tweet.getTwitterId(), tweetId));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPersistTweetWithoutLocation(){
        Tweet tweet = createTweetWithoutLocation();

        //When
        this.persistManager.persist(tweet);
        
        //Then - IllegalArgumentException must be thrown by the above statement 
        
    }
    
    private GregorianCalendar createTweetCalender(long l) {
        TimeZone timeZone = TimeZone.getTimeZone("UTC");
        GregorianCalendar date = (GregorianCalendar) GregorianCalendar.getInstance(timeZone);
        date.setTime(new Date(l));
        return date;
    }
    
    private Tweet createTweetWithoutLocation() {
        //Given
        Tweet tweet = new Tweet();
        tweet.setTwitterId(731559551053496320L);
        tweet.setCreatedBy("Cilla Webster");
        tweet.setCreatedAt(createTweetCalender(1463252363000L));
        tweet.setHashtags(createHashTags());
        tweet.setText("RT @Dee29659780: The SA justice may applaud Nel but yet another country US trial lawyers criticises his practice");
        tweet.setRetweet(true);
        tweet.setRetweetedCount(15L);
        tweet.setSensitive(false);
        return tweet;
    }

    private String[] createHashTags() {
       return new String[]{"OscarPistorius"};
    }

    private GeoLocation createGeoLocation() {
        GeoLocation geoLocation = new GeoLocation();
        Coordinate coordinate = new Coordinate(125.6, 10.6);
        geoLocation.setName("Pretoria");
        geoLocation.setType("point");
        geoLocation.setCoordinates(new Coordinate[]{coordinate});
        return geoLocation;
    }
}

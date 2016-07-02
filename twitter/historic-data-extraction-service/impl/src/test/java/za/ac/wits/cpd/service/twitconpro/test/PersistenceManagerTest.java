//package za.ac.wits.cpd.service.twitconpro.test;
//
//import java.util.Date;
//import java.util.GregorianCalendar;
//import java.util.List;
//import java.util.Objects;
//import java.util.TimeZone;
//import java.util.logging.Level;
//import javax.ejb.EJB;
//import lombok.extern.java.Log;
//import org.bson.conversions.Bson;
//import org.jboss.arquillian.container.test.api.Deployment;
//import org.jboss.arquillian.junit.Arquillian;
//import org.jboss.shrinkwrap.api.ShrinkWrap;
//import org.jboss.shrinkwrap.api.spec.WebArchive;
//import com.mongodb.Block;
//import com.mongodb.Mongo;
//import com.mongodb.MongoClient;
//import com.mongodb.client.MongoDatabase;
//import com.mongodb.client.FindIterable;
//import org.junit.Test;
//import static org.junit.Assert.*;
//import org.junit.Ignore;
//import org.junit.runner.RunWith;
//import za.ac.wits.cpd.service.twitconpro.Coordinate;
//import za.ac.wits.cpd.service.twitconpro.GeoLocation;
//import za.ac.wits.cpd.service.twitconpro.PersistenceManager;
//import za.ac.wits.cpd.service.twitconpro.Tweet;
//
//
///**
// *
// * @author Matsobane Khwinana (Matsobane.Khwinana@momentum.co.za)
// */
//@Log
//@RunWith(Arquillian.class)
//public class PersistenceManagerTest {
//
//    @Deployment
//    public static WebArchive createDeployment(){
//        return ShrinkWrap.create(WebArchive.class, "java-data-extraction-service-test.war")
//            .addClasses(PersistenceManager.class,Tweet.class,GeoLocation.class,
//                    Bson.class,Block.class,MongoDatabase.class,MongoClient.class,
//                    Mongo.class)
//            .addAsWebInfResource("web.xml", "web.xml")    
//            .addAsWebInfResource("test-beans.xml", "beans.xml");
//    }
//    
//    @EJB
//    private PersistenceManager persistManager;
//
//    @Ignore
//    @Test
//    public void testPersistTweet() {
//        //Given
//        Tweet tweet = createTweetWithoutLocation();
//        tweet.setGeoLocation(createGeoLocation());
//
//        //When
//        this.persistManager.persist(tweet);
//
//        //Then
//        assertNotNull(this.persistManager.findAll());
//
//    }
//
//    @Test
//    public void testFindByTwitterId() {
//        //Given
//        Long tweetId = 740271039398215681L;
//
//        //When
//        Tweet tweet = this.persistManager.findByTwitterId(tweetId);
//
//        //Then
//        assertNotNull(tweet);
//        assertTrue(Objects.equals(tweet.getTwitterId(), tweetId));
//    }
//
//    @Ignore
//    @Test
//    public void testFindAll() {
//        //Given
//        
//
//        //When
//        List<Tweet> tweets = this.persistManager.findAll();
//
//        //Then
//        
//        assertNotNull(tweets);
//        assertTrue(tweets.size() > 0);
//        log.log(Level.SEVERE, "#### Number of tweets: {0}", tweets.size());
//    }
//    
//    @Ignore @Test
//    public void testRemoveAll() {
//        //Given
//        
//
//        //When
//       this.persistManager.removeAll();
//       List<Tweet> tweets = this.persistManager.findAll();
//
//        //Then
//        assertTrue(tweets.isEmpty());
//    }
//    
//    @Ignore
//    @Test
//    public void testRemoveByTwitterId() {
//        //Given
//        Long tweetId = 729674502339055617L;
//
//        //When
//        this.persistManager.removeByTwitterId(tweetId);
//        Tweet tweet = this.persistManager.findByTwitterId(tweetId);
//
//        //Then
//        assertNull(tweet);
//    }
//    
//    
//    @Ignore
//    @Test(expected = IllegalArgumentException.class)
//    public void testPersistTweetWithoutLocation() {
//        Tweet tweet = createTweetWithoutLocation();
//
//        //When
//        this.persistManager.persist(tweet);
//
//        //Then - IllegalArgumentException must be thrown by the above statement 
//    }
//
//    private GregorianCalendar createTweetCalender(long l) {
//        TimeZone timeZone = TimeZone.getTimeZone("UTC");
//        GregorianCalendar date = (GregorianCalendar) GregorianCalendar.getInstance(timeZone);
//        date.setTime(new Date(l));
//        return date;
//    }
//
//    private Tweet createTweetWithoutLocation() {
//        //Given
//        Tweet tweet = new Tweet();
//        tweet.setTwitterId(729674501613621248L);
//        tweet.setCreatedBy("Cilla Webster");
//        tweet.setCreatedAt(createTweetCalender(1463252363000L));
//        tweet.setHashtags(createHashTags());
//        tweet.setText("RT @Dee29659780: The SA justice may applaud Nel but yet another country US trial lawyers criticises his practice");
//        tweet.setRetweet(true);
//        tweet.setRetweetedCount(15L);
//        tweet.setSensitive(false);
//        return tweet;
//    }
//
//    private String[] createHashTags() {
//        return new String[]{"OscarPistorius"};
//    }
//
//    private GeoLocation createGeoLocation() {
//        GeoLocation geoLocation = new GeoLocation();
//        Coordinate coordinate = new Coordinate(125.6, 10.6);
//        geoLocation.setName("Pretoria");
//        geoLocation.setType("point");
//        geoLocation.setCoordinates(coordinate);
//        return geoLocation;
//    }
//}

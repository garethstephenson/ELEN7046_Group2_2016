/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package za.ac.wits.cpd.elen7046.group2;

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
    public void testExtractAndStore() {
        //Given
        Long twitterId = 729674502339055617L;
        
        //When
        Tweet tweet = this.dataExtractor.extractTweetById(twitterId);
        this.persistenceManager.persist(tweet);
        Tweet dbTweet = persistenceManager.findByTwitterId(tweet.getTwitterId());
        
        //Then
        assertTrue(Objects.equals(dbTweet.getTwitterId(), twitterId));
    }

}

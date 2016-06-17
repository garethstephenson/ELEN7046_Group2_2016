
package za.ac.wits.cpd.service.twitconpro.api;

import java.net.URI;
import java.util.List;

/**
 *
 * @author Matsobane Khwinana (Matsobane.Khwinana@momentum.co.za)
 */
public interface DataDistributionService {
    /**
     * Distribute all tweets to the given URI.
     * 
     * @param uri 
     */
    public void distributeAllTweets(URI uri);
    /**
     * Distribute tweets of given hashtag to the given URI.
     * @param uri
     * @param hashtag 
     */
    public void distributeTweetsByHashtag(URI uri, String hashtag);
    
    /**
     * Distribute tweets of given hashtags to the given URI.
     * 
     * @param uri
     * @param hashtags 
     */
    public void distributeTweetsByHashtags(URI uri, List<String> hashtags);
}

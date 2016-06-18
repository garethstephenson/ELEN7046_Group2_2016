
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
     * Distribute all tweets to the given URI, with the given fileName.
     * 
     * @param uri
     * @param fileName 
     */
    public void distributeAllTweets(URI uri,String fileName);
    /**
     * Distribute tweets of given hashtag to the given URI.
     * @param uri
     * @param hashtag 
     */
    public void distributeTweetsByHashtag(URI uri, String hashtag);
    
    /**
     * Distribute tweets of given hashtags to the given URI, with the given fileName.
     * 
     * @param uri
     * @param fileName
     * @param hashtag 
     */
    public void distributeTweetsByHashtag(URI uri, String fileName, String hashtag);
    
    /**
     * Distribute tweets of given hashtags to the given URI.
     * 
     * @param uri
     * @param hashtags 
     */
    public void distributeTweetsByHashtags(URI uri, List<String> hashtags);
    
    /**
     * Distribute tweets of given hashtags to the given URI, with the given fileName.
     * 
     * @param uri
     * @param fileName
     * @param hashtags 
     */
    public void distributeTweetsByHashtags(URI uri, String fileName, List<String> hashtags);
}

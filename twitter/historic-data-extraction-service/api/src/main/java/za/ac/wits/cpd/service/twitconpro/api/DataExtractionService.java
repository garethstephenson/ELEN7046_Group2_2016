
package za.ac.wits.cpd.service.twitconpro.api;

import java.util.List;
import java.util.Map;

/**
 * Defines the data extraction service API.
 * 
 * @author Matsobane Khwinana (Matsobane.Khwinana@momentum.co.za)
 */
public interface DataExtractionService {
    
    public Tweet exactTweetById(Long id);
    public Tweet exactTweetByIdAndPersist(Long id);
    
    public List<Tweet> extractRandomTweetsByHashtag(String hashtag);
    public List<Tweet> extractAndPersistRandomTweetsByHashtag(String hashtag);
    
    public List<Tweet> extractHistoryTweetsByHashtag(String hashtag,int count);
    public List<Tweet> extractHistoryTweetsByHashtagAndPersist(String hashtag,int count);
    
    public List<Tweet> extractHistoryTweetsByHashtags(List<String> hashtags, Map<String, String> options);
    public List<Tweet> extractHistoryTweetsByHashtagsAndPersist(List<String> hashtags, Map<String, String> options);

}

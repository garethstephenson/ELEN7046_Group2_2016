
package za.ac.wits.cpd.service.twitconpro.api;

import java.util.List;
import java.util.Map;

/**
 * Defines the data extraction service API
 * @author Matsobane Khwinana (Matsobane.Khwinana@momentum.co.za)
 */
public interface DataExtractionService {
    public Tweet exactTweetById(Long id);
    public Tweet exactTweetByIdAndPersist(Long id);
    
    public List<Tweet> extractTweetsByHashtag(String hashtag);
    public List<Tweet> extractTweetsByHashtagAndPersist(String hashtag);
    
    public List<Tweet> extractTweetsByHashtag(String hashtag,int count);
    public List<Tweet> extractTweetsByHashtagAndPersist(String hashtag,int count);
    
    public List<Tweet> extractTweetsByHashtags(List<String> hashtags, Map<String, String> options);
    public List<Tweet> extractTweetsByHashtagsAndPersist(List<String> hashtags, Map<String, String> options);

}

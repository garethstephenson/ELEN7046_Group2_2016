package za.ac.wits.cpd.service.twitconpro.impl;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import lombok.NonNull;
import lombok.extern.java.Log;
import za.ac.wits.cpd.service.twitconpro.api.DataExtractionService;
import za.ac.wits.cpd.service.twitconpro.api.RemoteDataExtractionService;
import za.ac.wits.cpd.service.twitconpro.api.Tweet;

/**
 * The implementation class the Local and Remote DataExtractionService bean.
 * 
 * @author Matsobane Khwinana (Matsobane.Khwinana@momentum.co.za)
 */
@Log
@Stateless
@Local(value = DataExtractionService.class)
@Remote(value = RemoteDataExtractionService.class)
public class DataExtractionServiceImpl implements DataExtractionService,RemoteDataExtractionService{

    @EJB
    private TweetsDataExtractor extractor;
    
    @EJB
    private PersistenceManager persistManager;
    
    
    @Override
    public Tweet exactTweetById(@NonNull Long id) {
        return this.extractor.extractTweetById(id);
    }

    @Override
    public Tweet exactTweetByIdAndPersist(Long id) {
        Tweet tweet = exactTweetById(id);
        if(tweet!=null){
            this.persistManager.persist(tweet);
            return tweet;
        }
        
        return null;
    }

    @Override
    public List<Tweet> extractRandomTweetsByHashtag(String hashtag) {
        return this.extractor.extractHashtagTweets(hashtag);
    }

    @Override
    public List<Tweet> extractAndPersistRandomTweetsByHashtag(String hashtag) {
        List<Tweet> tweets = extractRandomTweetsByHashtag(hashtag);
        return persistTweetsOrThrowIllegalStateException(tweets);
    }

    @Override
    public List<Tweet> extractHistoryTweetsByHashtag(String hashtag, int count) {
        return this.extractor.extractHashtagTweets(hashtag, count);
    }

    @Override
    public List<Tweet> extractHistoryTweetsByHashtagAndPersist(String hashtag, int count) {
        List<Tweet> tweets = extractHistoryTweetsByHashtag(hashtag, count);
        return persistTweetsOrThrowIllegalStateException(tweets);
    }

    @Override
    public List<Tweet> extractHistoryTweetsByHashtags(List<String> hashtags, Map<String, String> options) {
        return this.extractor.extractHashtagsTweets(hashtags, options);
    }

    @Override
    public List<Tweet> extractHistoryTweetsByHashtagsAndPersist(List<String> hashtags, Map<String, String> options) {
        List<Tweet> tweets = extractHistoryTweetsByHashtags(hashtags, options);
        return persistTweetsOrThrowIllegalStateException(tweets);
    }

    @Override
    public List<Tweet> distributeAllTweets() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    private List<Tweet> persistTweetsOrThrowIllegalStateException(List<Tweet> tweets) throws IllegalStateException {
        if(tweets!=null&&!tweets.isEmpty()){
            tweets.stream().forEach((tweet) -> {
                try {
                    this.persistManager.persist(tweet);
                } catch(IllegalArgumentException e){
                    log.log(Level.SEVERE, e.getMessage(), tweet.toString());
                }
            });
            return tweets;
        }
        
        throw new IllegalStateException("No tweets returned for persistence!");
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package za.ac.wits.cpd.service.twitconpro.impl;

import java.util.List;
import java.util.Map;
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
    public List<Tweet> extractTweetsByHashtag(String hashtag) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Tweet> extractTweetsByHashtagAndPersist(String hashtag) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Tweet> extractTweetsByHashtag(String hashtag, int count) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Tweet> extractTweetsByHashtagAndPersist(String hashtag, int count) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Tweet> extractTweetsByHashtags(List<String> hashtags, Map<String, String> options) {
        return this.extractor.extractHashtagsTweets(hashtags, options);
    }

    @Override
    public List<Tweet> extractTweetsByHashtagsAndPersist(List<String> hashtags, Map<String, String> options) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}

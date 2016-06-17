/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package za.ac.wits.cpd.service.twitconpro.impl;

import java.net.URI;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import lombok.extern.java.Log;
import za.ac.wits.cpd.service.twitconpro.api.DataDistributionService;
import za.ac.wits.cpd.service.twitconpro.api.RemoteDataDistributionService;

/**
 * An implementation of the DataDistributionService bean.
 * 
 * @author Matsobane Khwinana (Matsobane.Khwinana@momentum.co.za)
 */
@Log
@Stateless
@Local(value = DataDistributionService.class)
@Remote(value = RemoteDataDistributionService.class)
public class DataDistributionServiceImpl implements RemoteDataDistributionService,DataDistributionService{

    @EJB
    private PersistenceManager persistManager;
    
    @EJB
    private FileHelper fileHelper;
    
    @Override
    public void distributeAllTweets(URI uri) {
        List<String> jsonTweets = this.persistManager.findAllJsonTweets();
        if(jsonTweets==null||jsonTweets.isEmpty()){throw new IllegalStateException("No Tweets found for distribution");}

        writeToFile(buildData(jsonTweets));
    }

    @Override
    public void distributeTweetsByHashtag(URI uri, String hashtag) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void distributeTweetsByHashtags(URI uri, List<String> hashtags) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private StringBuilder buildData(List<String> jsonTweets) {
        final StringBuilder builder = new StringBuilder();
        jsonTweets.stream().forEach((json) -> {
            builder.append(json).append(System.lineSeparator());
        });
        return builder;
    }

    private void writeToFile(final StringBuilder builder) {
        final String data = builder.toString();
        if(data!=null&&!data.isEmpty()){
            this.fileHelper.write(data);
        }
    }
    
}

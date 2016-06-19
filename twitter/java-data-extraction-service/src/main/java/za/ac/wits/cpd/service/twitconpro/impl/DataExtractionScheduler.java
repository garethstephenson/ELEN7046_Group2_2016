package za.ac.wits.cpd.service.twitconpro.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import lombok.extern.java.Log;
import za.ac.wits.cpd.service.twitconpro.api.DataExtractionService;

/**
 * 
 * @author Matsobane Khwinana (Matsobane.Khwinana@momentum.co.za)
 */
@Log
@Singleton
public class DataExtractionScheduler {
    @EJB
    private DataExtractionService extractionService;
    
    @Schedule(second="0", minute="0,5,10,15,20,25,30,35,40,45,50,55",hour="16,17", persistent=false)
    public void extractAndPersistEFFData(){
        log.info("#### Starting the EFF job ####");
        Map<String, String> options = hundredEFFTweetsAfterManifestoLaunch();
        this.extractionService.extractHistoryTweetsByHashtagsAndPersist(toEffHashtags(), options);
        log.info("#### Ending the EFF job ####");
    }
    
    @Schedule(second="0", minute="3,6,9,12,18,21,24,27,33,36,39,41,44,47,53,56,59",hour="16,17", persistent=false)
    public void extractAndPersistANCData(){
        log.info("#### Starting the ANC job ####");
        Map<String, String> options = hundredANCTweetsAfterManifestoLaunch();
        this.extractionService.extractHistoryTweetsByHashtagsAndPersist(toANCHashtags(), options);
        log.info("#### Ending the ANC job ####");
    }
    
    @Schedule(second="0", minute="2,4,8,14,16,22,26,28,32,34,38,42,46,48,52,54,58",hour="16,17", persistent=false)
    public void extractAndPersistDAData(){
        log.info("#### Starting the DA job ####");
        Map<String, String> options = hundredDATweetsAfterManifestoLaunch();
        this.extractionService.extractHistoryTweetsByHashtagsAndPersist(toDAHashtags(), options);
        log.info("#### Ending the DA job ####");
    }
    
    private List<String> toEffHashtags(){
        List<String> hashtags = new ArrayList<>();
        hashtags.add("EFFmanifesto");
        hashtags.add("EFF");
        hashtags.add("Malema");
        
        return hashtags;
    }
    
    private List<String> toANCHashtags(){
        List<String> hashtags = new ArrayList<>();
        hashtags.add("ANC");
        hashtags.add("ANCmanifesto");
        hashtags.add("ANCFriday");
        
        return hashtags;
    }
    
    private List<String> toDAHashtags(){
        List<String> hashtags = new ArrayList<>();
        hashtags.add("DA");
        hashtags.add("DAmanifesto");
        hashtags.add("VoteForChange");
        hashtags.add("#DAforJobs");
        
        return hashtags;
    }
    
    private Map<String, String> hundredEFFTweetsAfterManifestoLaunch() {
        final Map<String, String> options = new HashMap<>();
        options.put(COUNT, "124");
        options.put(SINCE, EFF_MANIFESTO_LAUNCH_DATE);
        options.put(UNTIL, THIRDY_DAYS_AFTER_EFF_MANIFESTO_LAUNCH);
        return options;
    }
    
    private Map<String, String> hundredANCTweetsAfterManifestoLaunch() {
        final Map<String, String> options = new HashMap<>();
        options.put(COUNT, "100");
        options.put(SINCE, ANC_MANIFESTO_LAUNCH_DATE);
        options.put(UNTIL, THIRDY_DAYS_AFTER_ANC_MANIFESTO_LAUNCH);
        return options;
    }
    
    private Map<String, String> hundredDATweetsAfterManifestoLaunch() {
        final Map<String, String> options = new HashMap<>();
        options.put(COUNT, "100");
        options.put(SINCE, DA_MANIFESTO_LAUNCH_DATE);
        options.put(UNTIL, THIRDY_DAYS_AFTER_DA_MANIFESTO_LAUNCH);
        return options;
    }
    
    private static final String EFF_MANIFESTO_LAUNCH_DATE = "2016-05-16";
    private static final String THIRDY_DAYS_AFTER_EFF_MANIFESTO_LAUNCH = "2016-05-20";
    
    private static final String ANC_MANIFESTO_LAUNCH_DATE = "2016-05-17";
    private static final String THIRDY_DAYS_AFTER_ANC_MANIFESTO_LAUNCH = "2016-05-20";
    
    private static final String DA_MANIFESTO_LAUNCH_DATE = "2016-04-30";
    private static final String THIRDY_DAYS_AFTER_DA_MANIFESTO_LAUNCH = "2016-05-04";
 
    private static final String PRIMARIES_START_DATE = "2016-03-15";
    private static final String PRIMARIES_END_DATE = "2016-03-30";
    private static final String HASHTAGS = "hashtags";
    private static final String HASHTAG = "hashtag";
    private static final String COUNT = "count";
    private static final String SINCE = "since";
    private static final String UNTIL = "until";
}

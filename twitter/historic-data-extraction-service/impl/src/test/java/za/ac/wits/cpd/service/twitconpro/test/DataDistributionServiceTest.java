package za.ac.wits.cpd.service.twitconpro.test;


import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import lombok.extern.java.Log;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import static org.hamcrest.CoreMatchers.is;

/**
 *
 * @author Matsobane Khwinana (Matsobane.Khwinana@momentum.co.za)
 */
@Log
public class DataDistributionServiceTest {
    private Client client;

    @Before
    public void initClient(){
        this.client = ClientBuilder.newClient();
    }
    
    @Test
    public void testPing(){
        //Given
        WebTarget target = this.client.target(restPath);
        
        //When
        Response response = target.request(MediaType.APPLICATION_JSON).accept(MediaType.TEXT_PLAIN).get();
        
        //Then
        assertThat(response.getStatus(), is(200));
        String payload = response.readEntity(String.class);
        log.severe(payload);
    }

    @Test
    public void testDistributeTweetById(){
        //Given
         WebTarget target = this.client.target(String.format(distributeTweetByIdFormat, 729674502339055617L));
         
         //When
        Response response = target.request(MediaType.APPLICATION_JSON)
                                    .accept(MediaType.APPLICATION_JSON)
                                    .get();
        
        //Then
        assertThat(response.getStatus(), is(204));
        
    }

    
    @Test
    public void testDistributeTweetsByHashtag(){
        //Given 
        String hashtag = "EFFmanifesto";
        String fileName = toTimestampFileName();
        
        WebTarget target = this.client.target(distributeTweetsByHashtagUrl)
                            .queryParam(URI, DEFAULT_DATA_DUMP_PATH)
                            .queryParam("fileName", fileName)
                            .queryParam(HASHTAG, hashtag);
        
        log.log(Level.SEVERE, "#### URL: {0}", target.getUri().toString());
        
        //When
        Response response = target.request(MediaType.APPLICATION_JSON)
                                    .accept(MediaType.APPLICATION_JSON)
                                    .get();
        
        //Then
        assertThat(response.getStatus(), is(204));
        assertTrue(isFileWritten(DEFAULT_DATA_DUMP_PATH+fileName));
    } 
    
    @Test
    public void testDistributeTweetsByHashtags(){
        //Given 
        String fileName = toTimestampFileName();
        WebTarget target = this.client.target(distributeTweetsByHashtagsUrl)
                            .queryParam(URI, DEFAULT_DATA_DUMP_PATH)
                            .queryParam(FILE_NAME, fileName)
                            .queryParam(HASHTAGS,"EFF")
                            .queryParam(HASHTAGS,"EFFmanifesto")
                            .queryParam(HASHTAGS,"Malema")
                            .queryParam(HASHTAGS,"ANC")
                            .queryParam(HASHTAGS,"ANCmanifesto")
                            .queryParam(HASHTAGS,"ANCFriday")
                            .queryParam(HASHTAGS,"DA")
                            .queryParam(HASHTAGS,"DAmanifesto")
                            .queryParam(HASHTAGS,"VoteForChange");
        
        log.log(Level.SEVERE, "#### URL: {0}", target.getUri().toString());
        
        //When
        Response response = target.request(MediaType.APPLICATION_JSON)
                                    .accept(MediaType.APPLICATION_JSON)
                                    .get();
        
        //Then
        assertThat(response.getStatus(), is(204));
        assertTrue(isFileWritten(DEFAULT_DATA_DUMP_PATH+fileName));
    } 
    private static final String FILE_NAME = "fileName";

    private boolean isFileWritten(String string) {
        log.severe(string);
        File file = new File(string);
        return file.exists();
    }

    private String toTimestampFileName() {
        DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd_hh_mm_ss");
        String timestamp = fmt.format(new Date());
        return timestamp+".json";
    }
    
    private final String restPath = "http://localhost:8080/historic-data-extraction-service/rest/distribute-data";
    private final String distributeTweetByIdFormat = "http://localhost:8080/historic-data-extraction-service/rest/distribute-data/byId/%d";
    private final String distributeTweetsByHashtagUrl = "http://localhost:8080/historic-data-extraction-service/rest/distribute-data/byHashtag";
    private final String distributeTweetsByHashtagsUrl = "http://localhost:8080/historic-data-extraction-service/rest/distribute-data/byHashtags";
    private static final String DEFAULT_DATA_DUMP_PATH = "/home/mkhwinana/Dev/eduworkspace/data/";
    private static final String HASHTAGS = "hashtags";
    private static final String HASHTAG = "hashtag";
    private static final String URI = "uri";

}

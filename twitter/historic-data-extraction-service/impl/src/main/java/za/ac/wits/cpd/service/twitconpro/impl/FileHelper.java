
package za.ac.wits.cpd.service.twitconpro.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import javax.ejb.Stateless;
import lombok.extern.java.Log;
import org.json.simple.JSONObject;

/**
 * Helper class for write Tweets on the file system.
 * 
 * @author Matsobane Khwinana (Matsobane.Khwinana@momentum.co.za)
 */
@Log
@Stateless
public class FileHelper {
    
    public static void write(JSONObject tweet){
        try {
            File file = new File(FILE_NAME_PREFIX + System.currentTimeMillis()+DOT_JSON);
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(tweet.toJSONString());
                log.log(Level.OFF, "{0} written....", file.getAbsolutePath());
            }
        } catch (IOException ex) {
            log.log(Level.SEVERE, "Failed to write a tweet to file", ex);
        }finally{
            
        }
    }
    
    public void write(String tweet){
        try {
            File file = new File("/home/mkhwinana/Dev/eduworkspace/data/" + System.currentTimeMillis()+DOT_JSON);
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(tweet);
                log.log(Level.OFF, "{0} written....", file.getAbsolutePath());
            }
        } catch (IOException ex) {
            log.log(Level.SEVERE, "Failed to write a tweet to file", ex);
        }finally{
            
        }
    }
    
    public void write(URI uri, String tweet){
        try {
            File file = new File(uri.toString() + toDefaultFileName());
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(tweet);
                log.log(Level.OFF, "{0} written....", file.getAbsolutePath());
            }
        } catch (IOException ex) {
            log.log(Level.SEVERE, "Failed to write a tweet to file", ex);
        }finally{
            
        }
    }
    
    public void write(URI uri,String fileName, String tweet){
        try {
            File file = new File(uri.toString() + fileName);
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(tweet);
                log.log(Level.OFF, "{0} written....", file.getAbsolutePath());
            }
        } catch (IOException ex) {
            log.log(Level.SEVERE, "Failed to write a tweet to file", ex);
        }finally{
            
        }
    }
    
    private String generateTimestamp() {
        DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd_hh_mm_ss");
        return fmt.format(new Date());
    }
    
    private String toDefaultFileName(){
        return generateTimestamp()+DOT_JSON;
    }
    
    private static final String DOT_JSON = ".json";
    private static final String FILE_NAME_PREFIX = "tweets/tweet-";

}

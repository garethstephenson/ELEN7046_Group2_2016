package za.ac.wits.cpd.service.twitconpro.impl;

import za.ac.wits.cpd.service.twitconpro.api.GeoLocation;
import za.ac.wits.cpd.service.twitconpro.api.Tweet;
import za.ac.wits.cpd.service.twitconpro.api.Coordinate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.logging.Level;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import lombok.extern.java.Log;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Maps the Twitter JSONObject payload to our custom Java Tweet object.
 * 
 * @author Matsobane Khwinana (Matsobane.Khwinana@momentum.co.za)
 */
@Log
@Stateless
public class TweetMapper {
    
    @EJB
    private LocationResolver locationResolver;

    public Tweet toTweet(JSONObject tweetResp) {
        final Tweet tweet = new Tweet();
        tweet.setTwitterId(toTwitterId(tweetResp));
        tweet.setCreatedBy(toCreatedBy(tweetResp));
        tweet.setCreatedAt(toGregorianCalendar((String) tweetResp.get(CREATED_AT)));
        tweet.setGeoLocation(toGeoLocation(tweetResp));
        tweet.setFavouriteCount(toFavouriteCount(tweetResp));
        tweet.setHashtags(toHashTags(tweetResp));
        tweet.setInReplyToName(toInReplyToName(tweetResp));
        tweet.setInReplyToStatusId(toInReplyToStatusId(tweetResp));
        tweet.setInReplyToUserId(toInReplyToUserId(tweetResp));
        tweet.setQuotedStatusId(toQuotedStatusId(tweetResp));
        tweet.setRetweeted(toRetweeted(tweetResp));
        tweet.setRetweet(toIsRetweet(tweetResp));
        tweet.setRetweetedCount(toRetweetCount(tweetResp));
        tweet.setLanguage(toLanguage(tweetResp));
        tweet.setSensitive(toSensitive(tweetResp));
        tweet.setUrl(toTweetUrl(tweetResp));
        tweet.setText(tweetResp.get(TEXT).toString());
        
        return tweet;
    }

    private String toCreatedBy(JSONObject tweetResp) {
        JSONObject userJson = (JSONObject)tweetResp.get(USER);
        String createdBy = userJson!=null?(String)userJson.get("name"):null;
        return createdBy;
    }

    private GeoLocation toGeoLocation(JSONObject tweetJson) {
        GeoLocation geo = toGeoLocationByCoordinates(tweetJson);
        if (geo != null)return geo;

        geo = toGeoLocationByPlace(tweetJson);
        if (geo != null)return geo;

        geo = toGeoLocationByUserProfile(tweetJson);
        if (geo != null) return geo;
                
        return null;
    }

    private GeoLocation toGeoLocationByUserProfile(JSONObject tweetJson){
        //TODO: try user profile location
        JSONObject userJson = (JSONObject)tweetJson.get(USER);
        if(userJson!=null){
           String locationJson = (String)userJson.get("location");
           if(isValidString(locationJson)){
               Coordinate geoCodes = locationResolver.getGeoCodes(locationJson);
               GeoLocation geoLoc = new GeoLocation();
               geoLoc.setName(locationJson);
               geoLoc.setCoordinates(geoCodes);
               geoLoc.setType(POINT);
               return geoLoc;
            }
           return null;
        }
        
        return null;
    }
    
    private GeoLocation toGeoLocationByPlace(JSONObject tweetResp) {
        JSONObject placeJson = (JSONObject) tweetResp.get(PLACE);
        if (placeJson!=null) {
            String name = (String)placeJson.get("full_name");
            if(isValidString(name)){
               Coordinate geoCodes = locationResolver.getGeoCodes(name, TEXAS_USA);
               GeoLocation geoLoc = new GeoLocation();
               geoLoc.setName(name);
               geoLoc.setCoordinates(geoCodes);
               geoLoc.setType(POINT);
               return geoLoc;
            }
        }
        return null;
    }

    private Coordinate[] toPolygonCoordinatesArray(JSONArray jsonCoordinates) {
        Coordinate[] coordinates = new Coordinate[4];
        final JSONArray parentArray = (JSONArray)jsonCoordinates.get(0);
        
        Double longitude0 = (Double) ((JSONArray)parentArray.get(0)).get(0);
        Double latitude0 = (Double) ((JSONArray)parentArray.get(0)).get(1);
        coordinates[0] = new Coordinate(latitude0, longitude0);
        
        Double longitude1 = (Double) ((JSONArray)parentArray.get(1)).get(0);
        Double latitude1 = (Double) ((JSONArray)parentArray.get(1)).get(1);
        coordinates[1] = new Coordinate(latitude1, longitude1);
        
        Double longitude2 = (Double) ((JSONArray)parentArray.get(2)).get(0);
        Double latitude2 = (Double) ((JSONArray)parentArray.get(2)).get(1);
        coordinates[2] = new Coordinate(latitude2, longitude2);
        
        Double longitude3 = (Double) ((JSONArray)parentArray.get(3)).get(0);
        Double latitude3 = (Double) ((JSONArray)parentArray.get(3)).get(1);
        coordinates[3] = new Coordinate(latitude3, longitude3);
        
        return coordinates;
    }

    private GeoLocation toGeoLocationByCoordinates(JSONObject tweetResp) {
        JSONObject coordinates = (JSONObject) tweetResp.get(COORDINATES);
        if (coordinates != null) {
            JSONArray geo = (JSONArray) coordinates.get(COORDINATES);
            if (geo != null) {
                final Double longitude = (Double) geo.get(0);
                final Double latitude = (Double) geo.get(1);
                final Coordinate coordinate = new Coordinate(latitude, longitude);

                //TODO: lookup name by coordinates...
                return new GeoLocation(coordinate, POINT,null);
            }
        }
        return null;
    }

    private GregorianCalendar toGregorianCalendar(String dateUtc) {
        try {
            //Input date: "Wed Apr 27 13:29:08 +0000 2016"
            TimeZone timeZone = TimeZone.getTimeZone(UTC);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(JSON_DATETIME_FORMAT);
            simpleDateFormat.setTimeZone(timeZone);
            Date date = simpleDateFormat.parse(dateUtc);
            GregorianCalendar calendar = (GregorianCalendar) GregorianCalendar.getInstance(timeZone);
            calendar.setTime(date);
            return calendar;
        } catch (ParseException ex) {
            log.log(Level.SEVERE, "Failed to convert JSON provided date time to Java GregorianCalender", ex);
        }
        return null;
    }
    
    private Long toFavouriteCount(JSONObject tweetResp) {
        Long count = (Long)tweetResp.get(FAVORITE_COUNT);
        return count!=null?count:0L;
    }

    private Long toTwitterId(JSONObject tweetResp) {
        return (Long)tweetResp.get(ID)!=null?(Long)tweetResp.get(ID):null;
    }

    private String toInReplyToName(JSONObject tweetResp) {
        String inReplyToNameJson = (String)tweetResp.get(IN_REPLY_TO_SCREEN_NAME);
        return inReplyToNameJson!=null?inReplyToNameJson:"";
    }

    private Long toInReplyToStatusId(JSONObject tweetResp) {
        Long inReplyToStatusIdJson = (Long)tweetResp.get(IN_REPLY_TO_STATUS_ID);
        return inReplyToStatusIdJson!=null?inReplyToStatusIdJson:-1L;
    }

    private Long toInReplyToUserId(JSONObject tweetResp) {
        Long inReplyToUserIdJson = (Long)tweetResp.get(IN_REPLY_TO_USER_ID);
        return inReplyToUserIdJson!=null?inReplyToUserIdJson:-1L;
    }

    private boolean toRetweeted(JSONObject tweetResp) {
        Boolean isRetweeted = (Boolean)tweetResp.get(RETWEETED);
        return isRetweeted!=null?isRetweeted:false;
    }

    private boolean toIsRetweet(JSONObject tweetResp) {
        JSONObject isRetweeted = (JSONObject)tweetResp.get(RETWEETED_STATUS);
        return isRetweeted!=null;
    }

    private Long toRetweetCount(JSONObject tweetResp) {
        Long retweetCountJson = (Long)tweetResp.get(RETWEET_COUNT);
        return retweetCountJson!=null?retweetCountJson:0L;
    }

    private Long toQuotedStatusId(JSONObject tweetResp) {
        Long quotedStatusIdJson = (Long)tweetResp.get(QUOTED_STATUS_ID);
        return quotedStatusIdJson!=null?quotedStatusIdJson:-1L;
    }
    
    private String[] toHashTags(JSONObject tweetResp) {
        JSONObject entitiesJson = (JSONObject)tweetResp.get(ENTITIES);
        if(entitiesJson!=null){
            JSONArray hashtagsJsonArray = (JSONArray)entitiesJson.get(HASHTAGS);
            if(hashtagsJsonArray!=null&&hashtagsJsonArray.size()>0){               
                String hashtags[] = new String[hashtagsJsonArray.size()];
                for (int i=0;i<hashtagsJsonArray.size();i++) 
                    hashtags[i] = toHashtagText(hashtagsJsonArray, i);;

                if(hashtags[0]!=null)
                    return hashtags;
            }
        }
        
        return null;
    }

    private String toHashtagText(JSONArray hashtagsJsonArray, int i) {
        JSONObject hashtagJson = (JSONObject) hashtagsJsonArray.get(i);
        final String text = (String)hashtagJson.get(TEXT);
        return text;
    }

    private String toLanguage(JSONObject tweetResp) {
        String lang = (String)tweetResp.get(LANGUAGE);
        //TODO: translate the BCP 47??
        return lang==null||lang.equalsIgnoreCase(UND)?EMPTY:lang;
    }

    private boolean toSensitive(JSONObject tweetResp) {
        /*This field only surfaces when a tweet contains a link. 
            The meaning of the field doesn’t pertain to the tweet content itself, 
            but instead it is an indicator that the URL contained in the tweet 
            may contain content or media identified as sensitive content.
        */
        Boolean isSensitive = (Boolean)tweetResp.get("possibly_sensitive");
        return isSensitive!=null?isSensitive:false;
    }

    private String toTweetUrl(JSONObject tweetResp) {        
        Long id = toTwitterId(tweetResp);
        String screenName = toUserScreenName(tweetResp);
        return (id!=null && isValidString(screenName))?String.format(TWEET_URL_FORMAT, screenName, id):EMPTY;    
    }

    private String toUserScreenName(JSONObject tweetResp) {
        JSONObject userJson = (JSONObject)tweetResp.get(USER);
        return userJson!=null?(String)userJson.get("screen_name"):EMPTY;
    }

    private boolean isValidString(String string) {
        return string!=null&&!string.isEmpty();
    }

    private static final String TWEET_URL_FORMAT = "https://twitter.com/%s/status/%d";
    private static final String JSON_DATETIME_FORMAT = "EE MMM dd HH:mm:ss zzz yyyy";
    private static final String IN_REPLY_TO_SCREEN_NAME = "in_reply_to_screen_name";
    private static final String IN_REPLY_TO_STATUS_ID = "in_reply_to_status_id";
    private static final String IN_REPLY_TO_USER_ID = "in_reply_to_user_id";
    private static final String QUOTED_STATUS_ID = "quoted_status_id";
    private static final String RETWEETED_STATUS = "retweeted_status";
    private static final String FAVORITE_COUNT = "favorite_count";
    private static final String RETWEET_COUNT = "retweet_count";
    private static final String COORDINATES = "coordinates";
    private static final String CREATED_AT = "created_at";
    private static final String TEXAS_USA = "Texas, USA";
    private static final String RETWEETED = "retweeted";
    private static final String LANGUAGE = "language";
    private static final String HASHTAGS = "hashtags";
    private static final String ENTITIES = "entities";
    private static final String PLACE = "place";
    private static final String POINT = "Point";
    private static final String TEXT = "text";
    private static final String USER = "user";
    private static final String UTC = "UTC";
    private static final String UND = "und";
    private static final String EMPTY = "";
    private static final String ID = "id";

}

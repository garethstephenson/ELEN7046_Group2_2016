package za.ac.wits.cpd.service.bigdatavisual;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.logging.Level;
import lombok.extern.java.Log;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Maps the Twitter JSONObject payload to our custom Java Tweet object.
 * 
 * @author Matsobane Khwinana (Matsobane.Khwinana@momentum.co.za)
 */
@Log
public class TweetMapper {

    public static Tweet toTweet(JSONObject tweetResp) {
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

    private static String toCreatedBy(JSONObject tweetResp) {
        JSONObject userJson = (JSONObject)tweetResp.get(USER);
        String createdBy = userJson!=null?(String)userJson.get("name"):null;
        return createdBy;
    }

    private static GeoLocation toGeoLocation(JSONObject tweetJson) {
        GeoLocation geo = toGeoLocationByCoordinates(tweetJson);
        if (geo != null)return geo;

        geo = toGeoLocationByPlace(tweetJson);
        if (geo != null)return geo;

        geo = toGeoLocationByUserProfile(tweetJson);
        if (geo != null) return geo;
                
        return null;
    }

    private static GeoLocation toGeoLocationByUserProfile(JSONObject tweetJson){
        //TODO: try user profile location
        JSONObject userJson = (JSONObject)tweetJson.get(USER);
        if(userJson!=null){
           String locationJson = (String)userJson.get("location");
           if(isValidString(locationJson)){
               Coordinate geoCodes = LocationResolver.getGeoCodes(locationJson);
               GeoLocation geoLoc = new GeoLocation();
               geoLoc.setName(locationJson);
               geoLoc.setCoordinates(new Coordinate[]{geoCodes});
               geoLoc.setType(POINT);
               return geoLoc;
            }
           return null;
        }
        
        return null;
    }
    
    /*
        "place": {
        "id": "cc95b56a28712044",
        "bounding_box": {
        "type": "Polygon",
        "coordinatesJsonArray": 
        [
            [
                [28.0010585, -25.9448996],
                [28.2712217, -25.9448996],
                [28.2712217, -25.7768617],
                [28.0010585, -25.7768617]
            ]
        ]
        },
        "place_type": "city",
        "contained_within": [],
        "name": "Centurion",
        "attributes": {},
        "country_code": "ZA",
        "url": "https:\/\/api.twitter.com\/1.1\/geo\/id\/cc95b56a28712044.json",
        "country": "South Africa",
        "full_name": "Centurion, South Africa"
        }
        
     */
    private static GeoLocation toGeoLocationByPlace(JSONObject tweetResp) {
        JSONObject placeJson = (JSONObject) tweetResp.get(PLACE);
        if (placeJson!=null) {
            JSONObject boundingBoxJSON = (JSONObject) placeJson.get(BOUNDING_BOX);
            String type = (String) boundingBoxJSON.get(TYPE);
            JSONArray coordinatesJsonArray = (JSONArray) boundingBoxJSON.get(COORDINATES);
            if (type.equalsIgnoreCase(POLYGON)) {
                Coordinate[] coordinates = toPolygonCoordinatesArray(coordinatesJsonArray);
                String name = (String) placeJson.get(NAME);
                return new GeoLocation(coordinates, type,name);
            }
        }
        return null;
    }

    private static Coordinate[] toPolygonCoordinatesArray(JSONArray jsonCoordinates) {
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

    private static GeoLocation toGeoLocationByCoordinates(JSONObject tweetResp) {
        JSONObject coordinates = (JSONObject) tweetResp.get(COORDINATES);
        if (coordinates != null) {
            JSONArray geo = (JSONArray) coordinates.get(COORDINATES);
            if (geo != null) {
                final Double longitude = (Double) geo.get(0);
                final Double latitude = (Double) geo.get(1);
                final Coordinate coordinate = new Coordinate(latitude, longitude);
                Coordinate coords[] = new Coordinate[]{coordinate};
                return new GeoLocation(coords, POINT,null);
            }
        }
        return null;
    }

    private static GregorianCalendar toGregorianCalendar(String dateUtc) {
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
    
    private static Long toFavouriteCount(JSONObject tweetResp) {
        Long count = (Long)tweetResp.get(FAVORITE_COUNT);
        return count!=null?count:0L;
    }

    private static Long toTwitterId(JSONObject tweetResp) {
        return (Long)tweetResp.get(ID)!=null?(Long)tweetResp.get(ID):null;
    }

    private static String toInReplyToName(JSONObject tweetResp) {
        String inReplyToNameJson = (String)tweetResp.get(IN_REPLY_TO_SCREEN_NAME);
        return inReplyToNameJson!=null?inReplyToNameJson:"";
    }

    private static Long toInReplyToStatusId(JSONObject tweetResp) {
        Long inReplyToStatusIdJson = (Long)tweetResp.get(IN_REPLY_TO_STATUS_ID);
        return inReplyToStatusIdJson!=null?inReplyToStatusIdJson:-1L;
    }

    private static Long toInReplyToUserId(JSONObject tweetResp) {
        Long inReplyToUserIdJson = (Long)tweetResp.get(IN_REPLY_TO_USER_ID);
        return inReplyToUserIdJson!=null?inReplyToUserIdJson:-1L;
    }

    private static boolean toRetweeted(JSONObject tweetResp) {
        Boolean isRetweeted = (Boolean)tweetResp.get(RETWEETED);
        return isRetweeted!=null?isRetweeted:false;
    }

    private static boolean toIsRetweet(JSONObject tweetResp) {
        JSONObject isRetweeted = (JSONObject)tweetResp.get(RETWEETED_STATUS);
        return isRetweeted!=null;
    }

    private static Long toRetweetCount(JSONObject tweetResp) {
        Long retweetCountJson = (Long)tweetResp.get(RETWEET_COUNT);
        return retweetCountJson!=null?retweetCountJson:0L;
    }

    private static Long toQuotedStatusId(JSONObject tweetResp) {
        Long quotedStatusIdJson = (Long)tweetResp.get(QUOTED_STATUS_ID);
        return quotedStatusIdJson!=null?quotedStatusIdJson:-1L;
    }
    
    private static String[] toHashTags(JSONObject tweetResp) {
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

    private static String toHashtagText(JSONArray hashtagsJsonArray, int i) {
        JSONObject hashtagJson = (JSONObject) hashtagsJsonArray.get(i);
        final String text = (String)hashtagJson.get(TEXT);
        return text;
    }

    private static String toLanguage(JSONObject tweetResp) {
        String lang = (String)tweetResp.get(LANGUAGE);
        //TODO: translate the BCP 47??
        return lang==null||lang.equalsIgnoreCase(UND)?EMPTY:lang;
    }

    private static boolean toSensitive(JSONObject tweetResp) {
        /*This field only surfaces when a tweet contains a link. 
            The meaning of the field doesn’t pertain to the tweet content itself, 
            but instead it is an indicator that the URL contained in the tweet 
            may contain content or media identified as sensitive content.
        */
        Boolean isSensitive = (Boolean)tweetResp.get("possibly_sensitive");
        return isSensitive!=null?isSensitive:false;
    }

    private static String toTweetUrl(JSONObject tweetResp) {
        String lang = (String) tweetResp.get("source");
        return lang!=null?lang:"";
    }

    private static boolean isValidString(String string) {
        return string!=null&&!string.isEmpty();
    }

    private static final String JSON_DATETIME_FORMAT = "EE MMM dd HH:mm:ss zzz yyyy";
    private static final String IN_REPLY_TO_SCREEN_NAME = "in_reply_to_screen_name";
    private static final String IN_REPLY_TO_STATUS_ID = "in_reply_to_status_id";
    private static final String IN_REPLY_TO_USER_ID = "in_reply_to_user_id";
    private static final String QUOTED_STATUS_ID = "quoted_status_id";
    private static final String RETWEETED_STATUS = "retweeted_status";
    private static final String FAVORITE_COUNT = "favorite_count";
    private static final String RETWEET_COUNT = "retweet_count";
    private static final String BOUNDING_BOX = "bounding_box";
    private static final String COORDINATES = "coordinates";
    private static final String CREATED_AT = "created_at";
    private static final String RETWEETED = "retweeted";
    private static final String LANGUAGE = "language";
    private static final String HASHTAGS = "hashtags";
    private static final String ENTITIES = "entities";
    private static final String POLYGON = "Polygon";
    private static final String PLACE = "place";
    private static final String POINT = "Point";
    private static final String NAME = "name";
    private static final String TYPE = "type";
    private static final String TEXT = "text";
    private static final String USER = "user";
    private static final String UTC = "UTC";
    private static final String UND = "und";
    private static final String EMPTY = "";
    private static final String ID = "id";

}

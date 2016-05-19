package za.ac.wits.cpd.service.bigdatavisual;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.FindIterable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import static java.util.Arrays.asList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.java.Log;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.Document;
import org.bson.json.JsonMode;
import org.bson.json.JsonWriterSettings;

/**
 *
 * @author Matsobane Khwinana (Matsobane.Khwinana@momentum.co.za)
 */
@Log
public class PersistenceManager {

    private MongoDatabase db;
    @Getter
    @Setter
    private String databaseName = "test"; //default test
    private MongoClient mongoClient;

    public PersistenceManager() {
        this.mongoClient = new MongoClient();
        this.db = this.mongoClient.getDatabase(getDatabaseName());
    }

    public PersistenceManager(String databaseName) {
        this.databaseName = databaseName;
        this.mongoClient = new MongoClient();
        this.db = mongoClient.getDatabase(databaseName);
    }

    public void persist(@NonNull Tweet tweet) {
        log.log(Level.SEVERE, "##### persisting {0}", tweet.toString());
        persistTweet(tweet);
    }

    public Tweet findByTwitterId(Long tweetId) {
        final List<Tweet> tweets = new ArrayList<>();
        FindIterable<Document> iterable = db.getCollection("Tweets").
                find(new Document("twitterID", tweetId));
        
        if (iterable != null) {
            iterable.forEach(new Block<Document>() {
                @Override
                public void apply(Document doc) {
                    BsonDocument bsonDoc = BsonDocument.parse(doc.toJson());
                    JsonWriterSettings strictSettings = new JsonWriterSettings(JsonMode.STRICT);
                    JsonWriterSettings shellSettings = new JsonWriterSettings(JsonMode.SHELL);
                    System.out.println("##### JsonMode.STRICT : " + bsonDoc.toJson(strictSettings));
                    System.out.println("#####  JsonMode.SHELL : " + bsonDoc.toJson(shellSettings));
                    
                    Tweet tweet = new Tweet();
                    tweet.setTwitterId(doc.getLong("twitterID"));
                    tweet.setText(doc.getString("tweetText"));
                    tweets.add(tweet);
                }
            });
        }

        if(!tweets.isEmpty())
            return tweets.get(0);
        
        return null;
    }

    public List<Tweet> findAll() {
        final List<Tweet> tweets = new ArrayList<>();
        FindIterable<Document> iterable = db.getCollection("Tweets").find();
        iterable.forEach(new Block<Document>() {
            @Override
            public void apply(Document doc) {
                tweets.add(toTweet(doc));
            }

            private Tweet toTweet(Document doc) {
                System.out.println("#### BSON: " + doc.toJson());

                Tweet tweet = new Tweet();
                tweet.setTwitterId(doc.getLong("twitterID"));
                tweet.setText(doc.getString("tweetText"));
                return tweet;
            }
        });

        return tweets;
    }

    private void persistTweet(Tweet tweet) {
        if (this.db != null) {
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
            //new ISODate(format.format(tweet.getCreatedAt().getTime()));
            db.getCollection("Tweets").insertOne(new Document()
                    .append("createdBy", tweet.getCreatedBy())
                    .append("createdAt", format.format(tweet.getCreatedAt().getTime()))
                    .append("coords", toCoordinatesArray(tweet))
                    .append("place", toPlace(tweet))
                    .append("favouriteCount", tweet.getFavouriteCount())
                    .append("hashtags", toHashtags(tweet))
                    .append("twitterID", tweet.getTwitterId())
                    .append("inReplyToName", tweet.getInReplyToName() != null ? tweet.getInReplyToName() : EMPTY)
                    .append("inReplyToStatusID", tweet.getInReplyToStatusId() != null ? tweet.getInReplyToStatusId() : MINUS_ONE)
                    .append("inReplyToUserID", tweet.getInReplyToUserId() != null ? tweet.getInReplyToUserId() : MINUS_ONE)
                    .append("quotedStatusID", tweet.getQuotedStatusId() != null ? tweet.getQuotedStatusId() : MINUS_ONE)
                    .append("isRetweet", tweet.isRetweeted())
                    .append("retweeted", tweet.isRetweet())
                    .append("retweetedCount", tweet.getRetweetedCount())
                    .append("language", tweet.getLanguage() != null ? tweet.getLanguage() : EMPTY)
                    .append("sensitive", tweet.isSensitive())
                    .append("tweetText", toTweetText(tweet))
                    .append("tweetURL", tweet.getUrl() != null ? tweet.getUrl() : EMPTY)
            );
        } else {
            throw new IllegalStateException(String.format("Mongo [%s] database is not available", getDatabaseName()));
        }
    }

    private static List<String> toHashtags(Tweet tweet) {
        final String[] hashtags = tweet.getHashtags();
        if (hashtags != null) {
            return asList(hashtags);
        }

        return Collections.EMPTY_LIST;
    }

    private static String toTweetText(Tweet tweet) {
        final String text = tweet.getText();
        if (text != null && !text.isEmpty()) {
            return text;
        }
        throw new IllegalArgumentException("Cannot persist a tweet without tweet text ");
    }

    private static String toPlace(Tweet tweet) {
        final GeoLocation geoLocation = tweet.getGeoLocation();

        if (geoLocation != null) {
            return geoLocation.getName();
        }
        throw new IllegalArgumentException("Cannot persist a tweet without a location");
    }

    private static List<Double> toCoordinatesArray(Tweet tweet) {
        final GeoLocation geoLocation = tweet.getGeoLocation();
        if (geoLocation != null) {
            return asList(geoLocation.getLongitude(), geoLocation.getLatitude());
        }

        throw new IllegalArgumentException("Cannot persist a tweet without a location");
    }

    private static final String MINUS_ONE = "-1";
    private static final String EMPTY = "";

}

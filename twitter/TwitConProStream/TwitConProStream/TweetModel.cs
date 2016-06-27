using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

using MongoDB.Bson;

namespace TwitConProStream
{
    public class TweetModel
    {
        //Tweet ID from Twitter
        private long tweetID = 0;

        //Username of the Account that Created the Tweet
        private string createdBy = "Not Sourced";
        //Datetime when the Tweet got created. UTC Time
        private DateTime createdAt = DateTime.Now;

        //Internal Class to maintain Geo Location Co-ordinates
        private Coordinates coordinates = Coordinates.DefaultCoordinates();

        //Number of times this tweet has been Favourited.
        private int favouriteCount = 0;
        //Whether or not this Tweet has been flagged as sensitive
        private bool sensitive = false;

        //Name of Account to which this Tweet is a reply
        private string inReplyToName;
        //ID of Tweet or Status to which this Tweet is a reply
        private long? inReplyToStatusID;
        //ID of Account to which this Tweet is a reply
        private long? inReplyToUserID;
        //ID of a quoted Status
        private long? quotedStatusID;
        //If the tweet is a retweet of another Tweet.
        private bool isRetweet = false;
        //If this tweet has been retweeted.
        private bool retweeted = false;
        //How many times this tweet has been retweeted.
        private int retweetCount = 0;

        //Language of the tweet. Sourced from an enumeration
        private string language;
        //String reflecting name of the place whence this tweet originates
        private string place;

        //Actual text of the tweet
        private string tweetText;
        //URL to locate the tweet in a browser
        private string tweetURL;
        //List of Hashtag elements
        private List<string> hashTags = new List<string>();

        public void AddHashTag(string s)
        {
            hashTags.Add(s);
        }

        //Method to return the tweet in BSON format
        public BsonDocument GetBSON()
        {
            List<BsonDocument> hashTagEntries = new List<BsonDocument>();

            foreach (string hashTag in hashTags)
            {
                hashTagEntries.Add(new BsonDocument { { "hashTag", hashTag } });
            }

            BsonArray hashTagArray = new BsonArray();

            foreach (BsonDocument bd in hashTagEntries)
            {
                hashTagArray.Add(bd);
            }


            var newTweet = new BsonDocument
                {
                    { "createdBy", CreatedBy },
                    { "createdAt", CreatedAt},
                    { "coords", new BsonArray
                        {
                            "latitude", Coords.Latitude,
                            "longitude", Coords.Longitude
                        }
                    },
                    { "favouriteCount", FavouriteCount },
                    { "hashtags", hashTagArray },   //TODO
                    { "twitterID", TweetID},
                    { "inReplyToName", InReplyToName},        //
                    { "inReplyToStatusID", InReplyToStatusID},  //
                    { "inReplyToUserID", InReplyToUserID},  //
                    { "isRetweet", IsRetweet },
                    { "language", Language},
                    { "place", Place },
                    { "sensitive", Sensitive },
                    { "quotedStatusID", QuotedStatusID }, //
                    { "retweeted", Retweeted },
                    { "retweetedCount", RetweetCount },
                    { "tweetText", TweetText },
                    { "tweetURL", TweetURL }
                };

            return newTweet;
        }

        #region Prpoerties

        public long TweetID
        {
            get { return tweetID; }
            set { tweetID = value; }
        }

        public string CreatedBy
        {
            get { return createdBy; }
            set { createdBy = value; }
        }

        public DateTime CreatedAt
        {
            get { return createdAt; }
            set { createdAt = value; }
        }

        public Coordinates Coords
        {
            get { return coordinates; }
            set { coordinates = value; }
        }

        public int FavouriteCount
        {
            get { return favouriteCount; }
            set { favouriteCount = value; }
        }

        public bool Sensitive
        {
            get { return sensitive; }
            set { sensitive = value; }
        }

        public string InReplyToName
        {
            get { return inReplyToName; }
            set { inReplyToName = value; }
        }

        public long? InReplyToStatusID
        {
            get { return inReplyToStatusID; }
            set { inReplyToStatusID = value; }
        }

        public long? InReplyToUserID
        {
            get { return inReplyToUserID; }
            set { inReplyToUserID = value; }
        }

        public long? QuotedStatusID
        {
            get { return quotedStatusID; }
            set { quotedStatusID = value; }
        }

        public bool IsRetweet
        {
            get { return isRetweet; }
            set { isRetweet = value; }
        }

        public bool Retweeted
        {
            get { return retweeted; }
            set { retweeted = value; }
        }

        public int RetweetCount
        {
            get { return retweetCount; }
            set { retweetCount = value; }
        }

        public string Language
        {
            get { return language; }
            set { language = value; }
        }

        public string Place
        {
            get { return place; }
            set { place = value; }
        }

        public string TweetText
        {
            get { return tweetText; }
            set { tweetText = value; }
        }

        public string TweetURL
        {
            get { return tweetURL; }
            set { tweetURL = value; }
        }

        #endregion

    }
}

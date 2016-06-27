using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

using System.IO;

using MongoDB.Driver;
using MongoDB.Bson;

namespace TwitConProStream
{
    public class MongoDBManager
    {
        protected IMongoClient _client;
        protected IMongoDatabase _twitterDataBase;
        protected TwitConProLogger _logger;

        public MongoDBManager(TwitConProLogger logger)
        {
            _logger = logger;
            //Localised Mongo Client
            _client = new MongoClient();
            //Source the Twitter Database from the Mongo Service
            _twitterDataBase = _client.GetDatabase("tweetDB2");
        }

        public void AddTweet(TweetModel tm)
        {
            //Get a Collection of Tweets from the Database
            var mongoCollection = _twitterDataBase.GetCollection<BsonDocument>("tweets");
            //Asynchronously add the received tweet
            mongoCollection.InsertOneAsync(tm.GetBSON());
        }

        /// <summary>
        /// This will dump the contents of the entire Database to textfiles using a root directory
        /// </summary>
        /// <param name="rootDir"></param>
        public void DumpDB(string rootDir)
        {
            DateTime dateTimeWindowStart = new DateTime(2016, 06, 10, 0, 0, 0);
            dateTimeWindowStart = dateTimeWindowStart.ToUniversalTime();

            DateTime endPeriod = new DateTime(2016, 07, 01, 0, 0, 0);
            endPeriod = endPeriod.ToUniversalTime();
            
            DumpDB(rootDir, dateTimeWindowStart, endPeriod);
        }

        /// <summary>
        /// This will dump the contents of the Database ranging between two provided dates)
        /// </summary>
        /// <param name="rootDir"></param>
        /// <param name="dateTimeWindowStart"></param>
        /// <param name="dateTimeWindowEnd"></param>
        /// <param name="endPeriod"></param>
        public async void DumpDB(string rootDir, DateTime dateTimeWindowStart, DateTime endPeriod)
        {
            try
            {
                _logger.LogDBMessage("Beginning Dump. Directory at " + rootDir + ". Start Time: " + dateTimeWindowStart.ToShortTimeString() + ". End time: " + endPeriod.ToShortTimeString());
                DateTime dateTimeWindowEnd = dateTimeWindowStart.AddHours(1.0);

                //Source a collection of the tweets from the database
                var mongoCollection = _twitterDataBase.GetCollection<BsonDocument>("tweets");

                //While the date is prior to the end period
                while (dateTimeWindowStart < endPeriod)
                {
                    var bsonWork = new BsonDocument();

                    //Get a batch of all the tweets in an hour
                    bsonWork["start"] = dateTimeWindowStart.ToUniversalTime();
                    bsonWork["end"] = dateTimeWindowEnd.ToUniversalTime();

                    var builder = Builders<BsonDocument>.Filter;

                    String newDateTimeOst = dateTimeWindowStart.ToUniversalTime().ToString();
                    //Add the limits to a filter
                    var filter = builder.Gte("createdAt", bsonWork["start"]) & builder.Lt("createdAt", bsonWork["end"]); //Not

                    //Create a text file from this time stamp (with hour reference)
                    StreamWriter outFile = new StreamWriter(@"C:\TEMP\" + dateTimeWindowStart.Year + "-" + dateTimeWindowStart.Month + "-" + dateTimeWindowStart.Day + "-" + dateTimeWindowStart.Hour + ".txt");

                    _logger.LogDBMessage("File for hour: " + dateTimeWindowStart.Hour + ".");

                    //Retrieve a collection of all the tweets that pass the filter
                    using (var cursor = await mongoCollection.FindAsync(filter))
                        while (await cursor.MoveNextAsync())
                        {
                            var batch = cursor.Current;

                            if (batch.Count() > 0)
                            {
                                //lstbxConsole.Items.Add(batch.Count());
                            }

                            //Iterate through each of the BSON Documents, and write out its elements.

                            foreach (var mongoDoc in batch)
                            {
                                outFile.WriteLine(mongoDoc.ToBson());
                            }

                        }

                    outFile.Close();

                    dateTimeWindowStart = dateTimeWindowStart.AddHours(1.0);
                    dateTimeWindowEnd = dateTimeWindowEnd.AddHours(1.0);
                }
            }
            catch (Exception ex)
            {
                _logger.LogEvent("DBManager Dump failed. Exception Message: " + ex.Message);
            }
        }
    }
}

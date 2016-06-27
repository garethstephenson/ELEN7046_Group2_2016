using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Text;
using System.Threading.Tasks;
using System.Xml.Linq;

using Tweetinvi;
using Tweetinvi.Core.Interfaces;
using Tweetinvi.Core.Enum;
using Tweetinvi.Core.Interfaces.Models.Entities;
using Tweetinvi.Core.Interfaces.Streaminvi;
using Tweetinvi.Core.Events.EventArguments;
using Tweetinvi.Streams;
using Tweetinvi.Streams.Helpers;
using Tweetinvi.Core.Exceptions;

using System.Threading;

namespace TwitConProStream
{
    class TwitterMessageHandler
    {
        MongoDBManager dbManager;
        TwitConProLogger logger;

        public TwitterMessageHandler(MongoDBManager _dbManager, TwitConProLogger _logger)
        {
            logger = _logger;
            dbManager = _dbManager;
        }

        public void TweetReceiveHandler(object sender, MatchedTweetReceivedEventArgs args)
        {

            try
            {
                TweetModel newTweet = new TweetModel();

                foreach (IHashtagEntity hashTag in args.Tweet.Hashtags)
                {
                    newTweet.AddHashTag(hashTag.Text);
                }

                newTweet.CreatedBy = args.Tweet.CreatedBy.ToString();
                newTweet.CreatedAt = args.Tweet.CreatedAt;
                if (args.Tweet.Coordinates != null)
                {

                    newTweet.Coords = new Coordinates(args.Tweet.Coordinates.Latitude, args.Tweet.Coordinates.Longitude);

                }
                else
                {
                    if (args.Tweet.CreatedBy.Location == null)
                    {
                        newTweet.Coords = new Coordinates(0.0, 0.0);
                    }
                    else
                    {
                        var address = args.Tweet.CreatedBy.Location.ToString();
                        var requestUri = string.Format("http://maps.googleapis.com/maps/api/geocode/xml?address={0}&sensor=false", Uri.EscapeDataString(address));

                        var request = WebRequest.Create(requestUri);
                        var response = request.GetResponse();
                        var xdoc = XDocument.Load(response.GetResponseStream());

                        var result = xdoc.Element("GeocodeResponse").Element("result");
                        if (result == null)
                        {
                            newTweet.Coords = new Coordinates(0.0, 0.0);
                        }
                        else
                        {
                            var locationElement = result.Element("geometry").Element("location");
                            var lat = double.Parse(locationElement.Element("lat").Value);
                            var lng = double.Parse(locationElement.Element("lng").Value);
                            newTweet.Coords = new Coordinates(lat, lng);
                        }
                    }
                }


                newTweet.FavouriteCount = args.Tweet.FavoriteCount;
                newTweet.TweetID = args.Tweet.Id;
                newTweet.InReplyToName = args.Tweet.InReplyToScreenName != null ? args.Tweet.InReplyToScreenName : "";
                newTweet.InReplyToStatusID = args.Tweet.InReplyToStatusId != null ? args.Tweet.InReplyToStatusId : -1;
                newTweet.InReplyToUserID = args.Tweet.InReplyToUserId != null ? args.Tweet.InReplyToUserId : -1;
                newTweet.IsRetweet = args.Tweet.IsRetweet;
                newTweet.Language = args.Tweet.Language.ToString();
                newTweet.Place = args.Tweet.Place != null ? args.Tweet.Place.FullName : "";
                newTweet.Sensitive = args.Tweet.PossiblySensitive;
                newTweet.QuotedStatusID = args.Tweet.QuotedStatusId != null ? args.Tweet.QuotedStatusId : -1;
                newTweet.Retweeted = args.Tweet.Retweeted;
                newTweet.RetweetCount = args.Tweet.RetweetCount;
                newTweet.TweetText = args.Tweet.Text;
                newTweet.TweetURL = args.Tweet.Url;

                dbManager.AddTweet(newTweet);
                logger.LogStream(newTweet.CreatedBy + ": " + newTweet.TweetText);

            }
            catch (Exception e)
            {
                logger.LogEvent("Problem handling a received tweet. Message is: " + e.Message);
            }


        }

        public void DisconnectHandler(object sender, DisconnectedEventArgs e)
        {
            logger.LogStream("*** ENDING STREAM ***");
        }

        private void MessageLimitHandler(object sender, LimitReachedEventArgs e)
        {
            logger.LogStream("*** MESSAGE LIMIT REACHED ***");
        }
    }
}

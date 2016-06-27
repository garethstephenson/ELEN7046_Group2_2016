using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

using Tweetinvi;
using Tweetinvi.Core.Interfaces;
using Tweetinvi.Core.Enum;
using Tweetinvi.Core.Interfaces.Models.Entities;
using Tweetinvi.Core.Interfaces.Streaminvi;
using Tweetinvi.Core.Events.EventArguments;
using Tweetinvi.Streams;
using Tweetinvi.Streams.Helpers;

using System.Threading;

namespace TwitConProStream
{
    public class TwitterStreamConnectionManager
    {
        string CONSUMER_KEY = "LzvtKyNtmgl2soCluqqZ04g7y";
        string CONSUMER_SECRET = "4XHRp6x2BwoENrCdtLObb0zDghAMccLQtZuZV71WeYwo7zRlwD";

        string ACCESS_TOKEN = "725260219782168576-Hp9AXSbGKRPEPwDOIFqOK6bRPAPq4gV";
        string ACCESS_TOKEN_SECRET = "aqlLQbeyIyxBbKiKo9bUr9Zq4P0tXTKNHtBemtzH7p6jH";

        IFilteredStream stream;

        TwitterMessageHandler twitterMessageHandler;

        MongoDBManager mbDataBase;

        TwitConProLogger logger;

        string[] searchTerms;

        public TwitterStreamConnectionManager(MongoDBManager _mbDataBase, TwitConProLogger _logger, string[] terms)
        {
            Auth.SetUserCredentials(CONSUMER_KEY, CONSUMER_SECRET, ACCESS_TOKEN, ACCESS_TOKEN_SECRET);

            Auth.ApplicationCredentials = Auth.Credentials;

            logger = _logger;
            searchTerms = terms;
            mbDataBase = _mbDataBase;
            twitterMessageHandler = new TwitterMessageHandler(mbDataBase, logger);
        }

        public void StartStream()
        {
            Thread workThread = new Thread(RunStream);
            workThread.Start();
            logger.LogEvent("Stream Started at " + DateTime.Now.ToShortTimeString() + ".");
        }

        private void RunStream()
        {
            stream = Tweetinvi.Stream.CreateFilteredStream();

            foreach (string s in searchTerms)
            {
                stream.AddTrack(s);
            }

            RateLimit.RateLimitTrackerMode = Tweetinvi.Core.RateLimitTrackerMode.TrackAndAwait;
            Tweetinvi.ExceptionHandler.SwallowWebExceptions = false;

            stream.MatchingTweetReceived += twitterMessageHandler.TweetReceiveHandler;
            stream.DisconnectMessageReceived += twitterMessageHandler.DisconnectHandler;

            stream.StartStreamMatchingAllConditions();

        }

        public void EndStream()
        {
            stream.StopStream();
            logger.LogEvent("Stream Ended at " + DateTime.Now.ToShortTimeString() + ".");
        }

    }
}

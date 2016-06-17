package za.ac.wits.cpd.service.twitconpro.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import za.ac.wits.cpd.service.twitconpro.api.Tweet;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import lombok.extern.java.Log;
import za.ac.wits.cpd.service.twitconpro.api.DataExtractionService;

/**
 *
 * @author Matsobane Khwinana (Matsobane.Khwinana@momentum.co.za)
 */
@Log
@Stateless
@Path("/extract-data")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class DataExtractionResource {

    @EJB
    private DataExtractionService extractionService;

    @GET
    public Response ping() {
        return Response.ok(PONG, MediaType.TEXT_PLAIN).build();
    }

    @GET
    @Path("/byId/{id}")
    public Tweet getTweetById(@PathParam(value = "id") Long id) {
        if (id == null) {
            throw new WebApplicationException(Response.Status.PRECONDITION_FAILED);
        }

        Tweet tweet = this.extractionService.exactTweetById(id);
        if (tweet != null) {
            return tweet;
        } else {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    }

    @GET
    @Path("/byIdAndPersist/{id}")
    public Tweet getTweetByAndPersistId(@PathParam(value = "id") Long id) {
        if (id == null) {
            throw new WebApplicationException(Response.Status.PRECONDITION_FAILED);
        }

        Tweet tweet = this.extractionService.exactTweetByIdAndPersist(id);
        if (tweet != null) {
            return tweet;
        } else {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    }

    @GET
    @Path("/historyByHashtags")
    public List<Tweet> extractHistoryTweetsByHashtags(@QueryParam(value = HASHTAGS) List<String> hashtags,
                                            @QueryParam(value = COUNT) int count,
                                            @QueryParam(value = SINCE) String since,
                                            @QueryParam(value = UNTIL) String until) {

        validateHistoryParams(hashtags, since, until);

        List<Tweet> tweets = this.extractionService.extractTweetsByHashtags(hashtags, toHistoryOptions(count, since, until));
        if (tweets != null && !tweets.isEmpty()) {
            return tweets;
        } else {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    }

    private Map<String, String> toHistoryOptions(int count, String since, String until) {
        Map<String, String> options = new HashMap<>();
        options.put(COUNT, count>0?String.valueOf(count):String.valueOf(100));
        options.put(SINCE, since);
        options.put(UNTIL, until);
        return options;
    }

    private void validateHistoryParams(List<String> hashtags, String since, String until) throws WebApplicationException {
        if (hashtags == null || hashtags.isEmpty()) {
            throw new WebApplicationException("Please specify the hashtags to search with", Response.Status.PRECONDITION_FAILED);
        }
        
        if (since == null || since.isEmpty()) {
            throw new WebApplicationException("Please specify the 'since' [yyyy-MM-dd] parameter to search with", Response.Status.PRECONDITION_FAILED);
        }
        
        if (until == null || until.isEmpty()) {
            throw new WebApplicationException("Please specify the 'until' [yyyy-MM-dd] parameter to search with", Response.Status.PRECONDITION_FAILED);
        }
    }

    private static final String PONG = "pong";
    private static final String SINCE = "since";
    private static final String UNTIL = "until";
    private static final String COUNT = "count";
    private static final String HASHTAGS = "hashtags";
}

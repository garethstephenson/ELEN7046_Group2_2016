package za.ac.wits.cpd.service.twitconpro.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import za.ac.wits.cpd.service.twitconpro.api.DataDistributionService;
import za.ac.wits.cpd.service.twitconpro.api.DataExtractionService;

/**
 *
 * @author Matsobane Khwinana (Matsobane.Khwinana@momentum.co.za)
 */
@Log
@Stateless
@Path("/distribute-data")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class DataDistributionResource {

    @EJB
    private DataDistributionService distributionService;

    @GET
    public Response ping() {
        return Response.ok(PONG, MediaType.TEXT_PLAIN).build();
    }

    @GET
    @Path("/byId/{id}")
    public void extractTweetById(@PathParam(value = "id") Long id) {
        if (id == null) {
            throw new WebApplicationException(Response.Status.PRECONDITION_FAILED);
        }

        
    }

    @GET
    @Path("/byHashtag/{hashtag}")
    public void extractAndPersistTweetById(@PathParam(value = "hashtag") String hashtag) {
        try {
            if (hashtag == null) {
                throw new WebApplicationException(Response.Status.PRECONDITION_FAILED);
            }
            
            URI uri = new URI("/home/mkhwinana/Dev/eduworkspace/data");
            this.distributionService.distributeTweetsByHashtag(uri, hashtag);
        } catch (URISyntaxException ex) {
            log.log(Level.SEVERE, "Failed to build the URI", ex);
            throw new WebApplicationException(ex.getMessage(),Response.Status.INTERNAL_SERVER_ERROR);
        }
        
    }

    private static final String PONG = "pong";
    private static final String SINCE = "since";
    private static final String UNTIL = "until";
    private static final String COUNT = "count";
    private static final String HASHTAGS = "hashtags";
}

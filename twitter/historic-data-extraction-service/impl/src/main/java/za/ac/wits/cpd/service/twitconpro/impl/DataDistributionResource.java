package za.ac.wits.cpd.service.twitconpro.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.logging.Level;
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
        
        this.distributionService.distributeTweetsById(id);

    }

    @GET
    @Path("/byHashtag")
    public void distributeTweetsbyHashtag(@QueryParam(value = "uri") String uri,
            @QueryParam(value = "fileName") String fileName,
            @QueryParam(value = "hashtag") String hashtag) {
        try {
            if (hashtag == null) {
                throw new WebApplicationException(Response.Status.PRECONDITION_FAILED);
            }

            if (!isValidString(fileName)) {
                this.distributionService.distributeTweetsByHashtag(toUrl(uri),hashtag);
            } else {
                this.distributionService.distributeTweetsByHashtag(toUrl(uri),fileName,hashtag);
            }
            
        } catch (URISyntaxException ex) {
            log.log(Level.SEVERE, "Failed to build the URI", ex);
            throw new WebApplicationException(ex.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        }

    }
    
    @GET
    @Path("/byHashtags")
    public void distributeTweetsbyHashtags(@QueryParam(value = "uri") String uri,
            @QueryParam(value = "fileName") String fileName,
            @QueryParam(value = "hashtags") List<String> hashtags) {
        try {
            if (hashtags == null) {
                throw new WebApplicationException(Response.Status.PRECONDITION_FAILED);
            }

            if (!isValidString(fileName)) {
                this.distributionService.distributeTweetsByHashtags(toUrl(uri),hashtags);
            } else {
                this.distributionService.distributeTweetsByHashtags(toUrl(uri),fileName,hashtags);
            }
            
        } catch (URISyntaxException ex) {
            log.log(Level.SEVERE, "Failed to build the URI", ex);
            throw new WebApplicationException(ex.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        }

    }

    private URI toUrl(String uri) throws URISyntaxException {
        return isValidString(uri)?new URI(uri):new URI(DEFAULT_DATA_DUMP_PATH);
    }

    private static boolean isValidString(String uri) {
        return (uri!=null&&!uri.isEmpty());
    }
    
    private static final String DEFAULT_DATA_DUMP_PATH = "/home/mkhwinana/Dev/eduworkspace/data";

    private static final String PONG = "pong";
    private static final String HASHTAGS = "hashtags";
}

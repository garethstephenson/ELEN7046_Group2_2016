
package za.ac.wits.cpd.service.twitconpro;

import java.util.logging.Level;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import lombok.extern.java.Log;

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
    private TweetsDataExtractor extractor;
    
    @GET
    public Response ping(){
        return Response.ok("Twit con pro", MediaType.TEXT_PLAIN).build();
    }
    
    @GET
    @Path("/id/{id}")
    public Tweet getTweetById(@PathParam(value = "id")Long id){
        if(id==null)throw new WebApplicationException(Response.Status.PRECONDITION_FAILED);
        
        Tweet tweet = this.extractor.extractTweetById(id);
        if(tweet!=null)
            return tweet;  
        else
            throw new WebApplicationException(Response.Status.NOT_FOUND);
    }
}

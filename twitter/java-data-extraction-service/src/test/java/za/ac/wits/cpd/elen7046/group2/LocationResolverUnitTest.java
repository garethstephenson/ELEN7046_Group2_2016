
package za.ac.wits.cpd.elen7046.group2;

import java.util.logging.Level;
import lombok.extern.java.Log;
import org.junit.Test;
import static org.junit.Assert.*;
import za.ac.wits.cpd.service.bigdatavisual.Coordinate;
import za.ac.wits.cpd.service.bigdatavisual.LocationResolver;

/**
 *
 * @author Matsobane Khwinana (Matsobane.Khwinana@momentum.co.za)
 */
@Log
public class LocationResolverUnitTest {
    
    @Test
    public void testLocationByFullName(){
        //Given 
        String locationName =  "Centurion, South Africa";
        
        //When
        Coordinate coordinates = LocationResolver.getGeoCodes(locationName);
        
        //Then
        assertEquals(CENTURION_LATITUDE, coordinates.getLatitude());
    }
    
    @Test
    public void testLocationByName(){
        //Given 
        String locationName =  "Centurion";
        
        //When
        Coordinate coordinates = LocationResolver.getGeoCodes(locationName);
        
        //Then
        assertEquals(CENTURION_LATITUDE, coordinates.getLatitude());
    }
    
    @Test
    public void testDefaultLocation(){
        //Given 
        String locationName =  "Somewhere in the world";
        
        //When
        Coordinate coordinates = LocationResolver.getGeoCodes(locationName, TEXAS_USA);
        
        //Then
        log.log(Level.SEVERE, "Texas coordinates: [{0},{1}]", 
                new Object[]{coordinates.getLatitude(),coordinates.getLongitude()});
        assertEquals(TEXAS_LONGITUDE, coordinates.getLongitude());
    }
    
    private static final String TEXAS_USA = "Texas, USA";
    private static final Double CENTURION_LATITUDE = -25.7752948;
    private static final Double TEXAS_LONGITUDE = -99.9018131;
}

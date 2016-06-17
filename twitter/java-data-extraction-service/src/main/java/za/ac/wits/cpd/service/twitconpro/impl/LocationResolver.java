package za.ac.wits.cpd.service.twitconpro.impl;

import za.ac.wits.cpd.service.twitconpro.api.Coordinate;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.*;

import java.util.logging.Level;
import javax.ejb.Stateless;
import lombok.extern.java.Log;

/**
 * A helper class used to determine location Geo-coordinates.
 * It uses Google's Geocoding API to resolve the coordinates.
 * 
 * @author Matsobane Khwinana (Matsobane.Khwinana@momentum.co.za)
 */
@Log
@Stateless
public class LocationResolver {

    /**
     * Converts a given location name to Geo-location coordinates.
     * For information, see the URL below for more documentation:
     * https://developers.google.com/maps/web-services/client-library
     * 
     * TODO: cache location coordinates for repeating location names 
     *      to avoid the roundtrip to Google.
     * 
     * @param locationName
     * @return Coordinate - latitude and longitude representation.
     */
    public Coordinate getGeoCodes(String locationName) {
        try {
            return lookupCoordinates(locationName);
        } catch (Exception ex) {
            log.log(Level.SEVERE, "Failed to lookup geo-location coordinates for " + locationName, ex);
        }

        return null;
    }

    
    public Coordinate getGeoCodes(String locationName, String defaultLocationName) {
        try {
            final Coordinate coordinates = lookupCoordinates(locationName); 
            if (coordinates!=null) 
                return coordinates;
            else
                return lookupCoordinates(defaultLocationName);
        } catch (Exception ex) {
            log.log(Level.SEVERE, "Failed to lookup geo-location coordinates for " + locationName, ex);
        }

        return null;
    }
    
    private Coordinate lookupCoordinates(String locationName) throws Exception {
        GeoApiContext context = new GeoApiContext().setApiKey(GOOGLE_API_KEY);
        GeocodingResult[] results = GeocodingApi.geocode(context, locationName).await();
        if (results != null && results.length > 0) {
            GeocodingResult result = results[0];
            log.log(Level.INFO, "#### GeocodingResult: {0}", result.toString());
            Geometry geometry = result.geometry;
            LatLng coordinates = geometry.location;
            return new Coordinate(coordinates.lat, coordinates.lng);
        }
        
        return null;
    }

    private static final String GOOGLE_API_KEY = "AIzaSyA3kPdcv9LNfthpEUHFkvh0vGvl8XpSdww";

}

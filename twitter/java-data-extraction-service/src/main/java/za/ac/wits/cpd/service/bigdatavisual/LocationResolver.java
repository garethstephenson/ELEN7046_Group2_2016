package za.ac.wits.cpd.service.bigdatavisual;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.*;

import java.util.logging.Level;
import lombok.extern.java.Log;

/**
 *
 * @author Matsobane Khwinana (Matsobane.Khwinana@momentum.co.za)
 */
@Log
public class LocationResolver {

    /**
     * Converts a given location name to Geo-location coordinates.
     * See the URL below for more documentation...
     * 
     * https://developers.google.com/maps/web-services/client-library
     * 
     * @param locationName
     * @return Coordinate - latitude and longitude
     */
    public static Coordinate getGeoCodes(String locationName) {
        try {
            GeoApiContext context = new GeoApiContext().setApiKey(GOOGLE_API_KEY);
            GeocodingResult[] results = GeocodingApi.geocode(context, locationName).await();

            if (results != null && results.length > 0) {
                GeocodingResult result = results[0];
                Geometry geometry = result.geometry;
                LatLng latlng = geometry.location;
                return new Coordinate(latlng.lat, latlng.lng);
            }
        } catch (Exception ex) {
            log.log(Level.SEVERE, "Failed to lookup geo-location coordinates for " + locationName, ex);
        }

        return null;
    }

    private static final String GOOGLE_API_KEY = "AIzaSyA3kPdcv9LNfthpEUHFkvh0vGvl8XpSdww";
    
    /*
    {
   "results" : [
      {
         "address_components" : [
            {
               "long_name" : "Texas",
               "short_name" : "TX",
               "types" : [ "administrative_area_level_1", "political" ]
            },
            {
               "long_name" : "United States",
               "short_name" : "US",
               "types" : [ "country", "political" ]
            }
         ],
         "formatted_address" : "Texas, USA",
         "geometry" : {
            "bounds" : {
               "northeast" : {
                  "lat" : 36.5007041,
                  "lng" : -93.5080389
               },
               "southwest" : {
                  "lat" : 25.8371638,
                  "lng" : -106.6456461
               }
            },
            "location" : {
               "lat" : 31.9685988,
               "lng" : -99.9018131
            },
            "location_type" : "APPROXIMATE",
            "viewport" : {
               "northeast" : {
                  "lat" : 36.5015087,
                  "lng" : -93.5080389
               },
               "southwest" : {
                  "lat" : 25.8371638,
                  "lng" : -106.6456461
               }
            }
         },
         "partial_match" : true,
         "place_id" : "ChIJSTKCCzZwQIYRPN4IGI8c6xY",
         "types" : [ "administrative_area_level_1", "political" ]
      }
   ],
   "status" : "OK"
}
    
    
    
    
    
    
    */
}

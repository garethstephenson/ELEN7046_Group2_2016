//
//package za.ac.wits.cpd.service.twitconpro.test;
//
//import java.util.logging.Level;
//import javax.ejb.EJB;
//import lombok.extern.java.Log;
//import org.jboss.arquillian.container.test.api.Deployment;
//import org.jboss.arquillian.junit.Arquillian;
//import org.jboss.shrinkwrap.api.ShrinkWrap;
//import org.jboss.shrinkwrap.api.asset.EmptyAsset;
//import org.jboss.shrinkwrap.api.spec.WebArchive;
//import org.junit.Test;
//import static org.junit.Assert.*;
//import org.junit.Ignore;
//import org.junit.runner.RunWith;
//import za.ac.wits.cpd.service.twitconpro.Coordinate;
//import za.ac.wits.cpd.service.twitconpro.LocationResolver;
//
///**
// * A unit test case for resolving missing Tweet locations.
// * 
// * @author Matsobane Khwinana (Matsobane.Khwinana@momentum.co.za)
// */
//@Log
//@RunWith(Arquillian.class)
//public class LocationResolverUnitTest {
//    
//    @Deployment
//    public static WebArchive createDeployment() {
//        return ShrinkWrap.create(WebArchive.class,"java-data-extraction-service-test.war")
//            .addClasses(LocationResolver.class)
//            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
//    }
//    
//    @EJB
//    private LocationResolver locationResolver;
//    @Ignore
//    @Test
//    public void testLocationByFullName(){
//        //Given 
//        String locationName =  "Centurion, South Africa";
//        
//        //When
//        Coordinate coordinates = locationResolver.getGeoCodes(locationName);
//        
//        //Then
//        assertEquals(CENTURION_LATITUDE, coordinates.getLatitude());
//    }
//    @Ignore
//    @Test
//    public void testLocationByName(){
//        //Given 
//        String locationName =  "Centurion";
//        
//        //When
//        Coordinate coordinates = locationResolver.getGeoCodes(locationName);
//        
//        //Then
//        assertEquals(CENTURION_LATITUDE, coordinates.getLatitude());
//    }
//    @Ignore
//    @Test
//    public void testDefaultLocation(){
//        //Given 
//        String locationName =  "Somewhere in the world";
//        
//        //When
//        Coordinate coordinates = locationResolver.getGeoCodes(locationName, TEXAS_USA);
//        
//        //Then
//        log.log(Level.SEVERE, "Texas coordinates: [{0},{1}]", 
//                new Object[]{coordinates.getLatitude(),coordinates.getLongitude()});
//        assertEquals(TEXAS_LONGITUDE, coordinates.getLongitude());
//    }
//    
//    private static final String TEXAS_USA = "Texas, USA";
//    private static final Double CENTURION_LATITUDE = -25.7752948;
//    private static final Double TEXAS_LONGITUDE = -99.9018131;
//}

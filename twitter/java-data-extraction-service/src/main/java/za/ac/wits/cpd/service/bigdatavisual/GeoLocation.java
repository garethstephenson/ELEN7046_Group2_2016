package za.ac.wits.cpd.service.bigdatavisual;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a Geographic location.
 * 
 * @author Matsobane Khwinana (Matsobane.Khwinana@momentum.co.za)
 */
@Data
@XmlRootElement
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class GeoLocation {
    @XmlAttribute
    private Coordinate[] coordinates;
    @XmlAttribute
    private String type;
    @XmlAttribute
    private String name;
    
    public Double getLongitude(){
        if(coordinates!=null&&coordinates.length>0)
              if(type.equalsIgnoreCase("Point"))
                  return coordinates[0].getLongitude();
        return null;        
    }
    
     public Double getLatitude(){
          if(coordinates!=null&&coordinates.length>0)
              if(type.equalsIgnoreCase("Point"))
                  return coordinates[0].getLatitude();
        return null;
     }
}

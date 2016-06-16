package za.ac.wits.cpd.service.twitconpro;

import java.io.Serializable;
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
public class GeoLocation implements Serializable{
    private static final long serialVersionUID = 8511377733993533387L;

    @XmlAttribute
    private Coordinate coordinates;
    @XmlAttribute
    private String type;
    @XmlAttribute
    private String name;

    public Double getLongitude() {
        if (coordinates != null )return coordinates.getLongitude();
        return null;
    }

    public Double getLatitude() {
        if (coordinates != null)return coordinates.getLatitude();
        return null;
    }
}

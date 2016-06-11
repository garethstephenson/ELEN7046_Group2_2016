
package za.ac.wits.cpd.service.twitconpro;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Matsobane Khwinana (Matsobane.Khwinana@momentum.co.za)
 */
@Data
@XmlRootElement
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class Coordinate {
    private Double latitude;
    private Double longitude;
}

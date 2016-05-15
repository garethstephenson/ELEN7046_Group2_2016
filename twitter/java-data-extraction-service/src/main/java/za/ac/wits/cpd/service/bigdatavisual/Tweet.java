package za.ac.wits.cpd.service.bigdatavisual;

import java.io.Serializable;
import java.util.GregorianCalendar;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A simplified data structure for representing a Tweet.
 *
 * @author Matsobane Khwinana (Matsobane.Khwinana@momentum.co.za)
 */
@Data
@XmlRootElement
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class Tweet implements Serializable{
    private static final long serialVersionUID = 7042265067804136769L;
    
    @XmlAttribute
    private Long twitterId;
    @XmlAttribute
    private String text;
    @XmlElementRef
    private GeoLocation geoLocation;
    @XmlAttribute
    private String createdBy;
    @XmlAttribute
    private GregorianCalendar createdAt;
    @XmlAttribute
    private Long favouriteCount;
    @XmlAttribute
    private String hashtags[];
    @XmlAttribute
    private String inReplyToName;
    @XmlAttribute
    private Long inReplyToStatusId;
    @XmlAttribute
    private Long inReplyToUserId;
    @XmlAttribute
    private Long quotedStatusId;
    @XmlAttribute
    private boolean isRetweet;
    @XmlAttribute
    private boolean retweeted;
    @XmlAttribute
    private Long retweetedCount;
    @XmlAttribute
    private String language;
    @XmlAttribute
    private boolean sensitive;
    @XmlAttribute
    private String url;
}

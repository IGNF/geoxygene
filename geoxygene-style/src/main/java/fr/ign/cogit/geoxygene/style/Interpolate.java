package fr.ign.cogit.geoxygene.style;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class Interpolate {
    @XmlElement(name = "LookupValue")
    String lookupvalue;

    public String getLookupvalue() {
        return this.lookupvalue;
    }

    public void setLookupvalue(String lookupvalue) {
        this.lookupvalue = lookupvalue;
    }

    @XmlElement(name = "InterpolationPoint")
    private List<InterpolationPoint> interpolationPoint = new ArrayList<InterpolationPoint>(
            0);

    public List<InterpolationPoint> getInterpolationPoint() {
        return this.interpolationPoint;
    }

    public void setInterpolationPoint(
            List<InterpolationPoint> interpolationPoint) {
        this.interpolationPoint = interpolationPoint;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime
                * result
                + ((this.interpolationPoint == null) ? 0
                        : this.interpolationPoint.hashCode());
        result = prime
                * result
                + ((this.lookupvalue == null) ? 0 : this.lookupvalue.hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        Interpolate other = (Interpolate) obj;
        if (this.interpolationPoint == null) {
            if (other.interpolationPoint != null) {
                return false;
            }
        } else if (!this.interpolationPoint.equals(other.interpolationPoint)) {
            return false;
        }
        if (this.lookupvalue == null) {
            if (other.lookupvalue != null) {
                return false;
            }
        } else if (!this.lookupvalue.equals(other.lookupvalue)) {
            return false;
        }
        return true;
    }

}

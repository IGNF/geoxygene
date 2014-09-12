package fr.ign.cogit.geoxygene.style;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import fr.ign.cogit.geoxygene.filter.expression.PropertyName;

@XmlAccessorType(XmlAccessType.FIELD)
public class Label {

    @XmlElement(name = "PropertyName")
    private PropertyName propertyName;

    public void setPropertyName(PropertyName propertyName) {
        this.propertyName = propertyName;
    }

    public PropertyName getPropertyName() {
        return this.propertyName;
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
                + ((this.propertyName == null) ? 0 : this.propertyName
                        .hashCode());
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
        Label other = (Label) obj;
        if (this.propertyName == null) {
            if (other.propertyName != null) {
                return false;
            }
        } else if (!this.propertyName.equals(other.propertyName)) {
            return false;
        }
        return true;
    }

}

/**
 * 
 */
package fr.ign.cogit.geoxygene.style.interpolation;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import fr.ign.cogit.geoxygene.style.PolygonSymbolizer;

/**
 * @author Nicolas Mellado
 *
 *         Symboliser that describes one style interpolated from two other ones
 *         (stored in the instance).
 * 
 *         Note that this class represent only the logic, and not the processing
 *         functions.
 * 
 * @see Validation mechanisms in package geoxygene.appli.validation
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class PolygonInterpolationSymbolizer 
extends PolygonSymbolizer 
implements InterpolationSymbolizerInterface {

    @XmlElement(name = "FirstSymbolizer")
    private PolygonSymbolizer firstSymbolizer = null;

    @XmlElement(name = "SecondSymbolizer")
    private PolygonSymbolizer secondSymbolizer = null;

    @XmlElement(name = "alpha")
    private float alpha = 0.5f;

    public PolygonInterpolationSymbolizer() {
        super();
        this.reset();
    }

    @Override
    public void reset() {

        this.firstSymbolizer = new PolygonSymbolizer();
        this.secondSymbolizer = new PolygonSymbolizer();
    }

    /**
     * @return the firstSymbolizer
     */
    public PolygonSymbolizer getFirstSymbolizer() {
        return this.firstSymbolizer;
    }

    /**
     * @param firstSymbolizer
     *            the firstSymbolizer to set
     */
    public void setFirstSymbolizer(PolygonSymbolizer firstSymbolizer) {
        this.firstSymbolizer = firstSymbolizer;
    }

    /**
     * @return the secondSymbolizer
     */
    public PolygonSymbolizer getSecondSymbolizer() {
        return this.secondSymbolizer;
    }

    /**
     * @param secondSymbolizer
     *            the secondSymbolizer to set
     */
    public void setSecondSymbolizer(PolygonSymbolizer secondSymbolizer) {
        this.secondSymbolizer = secondSymbolizer;
    }

    /**
     * @return the alpha
     * @Override
     */
    public float getAlpha() {
        return this.alpha;
    }

    /**
     * @param alpha
     *            the alpha to set
     * @Override
     */
    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        long temp;
        temp = Double.doubleToLongBits(this.alpha);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime
                * result
                + ((this.firstSymbolizer == null) ? 0 : this.firstSymbolizer
                        .hashCode());
        result = prime
                * result
                + ((this.secondSymbolizer == null) ? 0 : this.secondSymbolizer
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
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (this.getClass() != obj.getClass())
            return false;
        PolygonInterpolationSymbolizer other = (PolygonInterpolationSymbolizer) obj;
        if (Double.doubleToLongBits(this.alpha) != Double
                .doubleToLongBits(other.alpha))
            return false;
        if (this.firstSymbolizer == null) {
            if (other.firstSymbolizer != null)
                return false;
        } else if (!this.firstSymbolizer.equals(other.firstSymbolizer))
            return false;
        if (this.secondSymbolizer == null) {
            if (other.secondSymbolizer != null)
                return false;
        } else if (!this.secondSymbolizer.equals(other.secondSymbolizer))
            return false;
        return true;
    }
}

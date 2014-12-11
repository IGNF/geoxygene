/**
 * 
 */
package fr.ign.cogit.geoxygene.style;

import java.awt.Color;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * @author nmellado
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class PolygonInterpolationSymbolizer extends PolygonSymbolizer {

    @XmlElement(name = "FirstSymbolizer")
    private PolygonSymbolizer firstSymbolizer = null;

    @XmlElement(name = "SecondSymbolizer")
    private PolygonSymbolizer secondSymbolizer = null;

    @XmlElement(name = "alpha")
    private float alpha = 0.5f;

    public PolygonInterpolationSymbolizer() {
        super();
        this.firstSymbolizer = new PolygonSymbolizer();
        this.secondSymbolizer = new PolygonSymbolizer();

        // preset, generated automatically to avoid to generate
        // an empty structure in the SLD
        // Fill
        // Stroke
        Fill fill = new Fill(), subFill1 = new Fill(), subFill2 = new Fill();

        subFill1.setFill(Color.blue);
        subFill2.setFill(Color.red);

        subFill1.setFillOpacity(1.f);
        subFill2.setFillOpacity(1.f);

        this.setFill(fill);
        this.firstSymbolizer.setFill(subFill1);
        this.secondSymbolizer.setFill(subFill2);

        // Stroke
        Stroke stroke = new Stroke(), subStroke1 = new Stroke(), subStroke2 = new Stroke();
        subStroke1.setStroke(Color.black);
        subStroke2.setStroke(Color.green);
        subStroke1.setStrokeOpacity(1.f);
        subStroke2.setStrokeOpacity(1.f);
        subStroke1.setStrokeWidth(5.f);
        subStroke2.setStrokeWidth(10.f);

        this.setStroke(stroke);
        this.firstSymbolizer.setStroke(subStroke1);
        this.secondSymbolizer.setStroke(subStroke2);

        this.updateInternal();
    }

    /**
     * @return the alpha
     */
    public float getAlpha() {
        return this.alpha;
    }

    /**
     * @param alpha
     *            the alpha to set
     */
    public void setAlpha(float alpha) {
        this.alpha = alpha;
        this.updateInternal();
    }

    // Would be better to hide this function to allow to change easily between
    // different interpolation functions.
    private static float linearInterpolation(float v1, float v2, float alpha) {
        return v1 * alpha + v2 * (1.f - alpha);
    }

    private static Color interpolateRGBColors(Color x, Color y, float alpha) {
        float red = linearInterpolation(x.getRed(), y.getRed(), alpha);
        float green = linearInterpolation(x.getGreen(), y.getGreen(), alpha);
        float blue = linearInterpolation(x.getBlue(), y.getBlue(), alpha);

        System.out.println(x + " -- " + y);

        // note that if i pass float values they have to be in the range of
        // 0.0-1.0
        // and not in 0-255 like the ones i get returned by the getters.
        return new Color(red / 255.f, green / 255.f, blue / 255.f);
    }

    private static float interpolateScalars(float x, float y, float alpha) {
        return linearInterpolation(x, y, alpha);
    }

    @Override
    public void updateInternal() {
        // TODO Auto-generated method stub
        super.updateInternal();

        // update fill
        Fill subFill1 = this.firstSymbolizer.getFill();
        Fill subFill2 = this.secondSymbolizer.getFill();
        this.getFill().setColor(
                interpolateRGBColors(subFill1.getColor(), subFill2.getColor(),
                        this.alpha));
        this.getFill().setFillOpacity(
                interpolateScalars(subFill1.getFillOpacity(),
                        subFill2.getFillOpacity(), this.alpha));

        // update stroke
        Stroke subStroke1 = this.firstSymbolizer.getStroke();
        Stroke subStroke2 = this.secondSymbolizer.getStroke();

        // HACK: need to call this function to update internal transcient fields
        // according to css properties
        subStroke1.getColor();
        subStroke2.getColor();

        this.getStroke().setStroke(
                interpolateRGBColors(subStroke1.getStroke(),
                        subStroke2.getStroke(), this.alpha));
        this.getStroke().setStrokeOpacity(
                interpolateScalars(subStroke1.getStrokeOpacity(),
                        subStroke2.getStrokeOpacity(), this.alpha));
        this.getStroke().setStrokeWidth(
                interpolateScalars(subStroke1.getStrokeWidth(),
                        subStroke2.getStrokeWidth(), this.alpha));
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

package fr.ign.cogit.geoxygene.style.texture;

import java.awt.Color;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import fr.ign.cogit.geoxygene.style.colorimetry.ColorJaxbAdaptor;

@XmlAccessorType(XmlAccessType.FIELD)
public class PerlinNoiseTexture extends Texture {

    public PerlinNoiseTexture() {
        super();
    }

    public PerlinNoiseTexture(float scale, float amount, float angle, float stretch, Color color1, Color color2) {
        super();
        this.scale = scale;
        this.amount = amount;
        this.angle = angle;
        this.stretch = stretch;
        this.color1 = color1;
        this.color2 = color2;
    }

    @XmlElement(name = "Scale")
    private float scale = 10f;

    public float getScale() {
        return this.scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    @XmlElement(name = "Amount")
    private float amount = 0.5f;

    public float getAmount() {
        return this.amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    @XmlElement(name = "Angle")
    private float angle = (float) Math.PI;

    public float getAngle() {
        return this.angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    @XmlElement(name = "Stretch")
    private float stretch = 1f;

    public float getStretch() {
        return this.stretch;
    }

    public void setStretch(float stretch) {
        this.stretch = stretch;
    }

    @XmlJavaTypeAdapter(ColorJaxbAdaptor.class)
    @XmlElement(name = "Color1")
    private Color color1 = Color.YELLOW;

    public Color getColor1() {
        return this.color1;
    }

    public void setColor1(Color color1) {
        this.color1 = color1;
    }

    @XmlJavaTypeAdapter(ColorJaxbAdaptor.class)
    @XmlElement(name = "Color2")
    private Color color2 = Color.RED;

    public Color getColor2() {
        return this.color2;
    }

    public void setColor2(Color color2) {
        this.color2 = color2;
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
        result = prime * result + Float.floatToIntBits(this.amount);
        result = prime * result + Float.floatToIntBits(this.angle);
        result = prime * result + ((this.color1 == null) ? 0 : this.color1.hashCode());
        result = prime * result + ((this.color2 == null) ? 0 : this.color2.hashCode());
        result = prime * result + Float.floatToIntBits(this.scale);
        result = prime * result + Float.floatToIntBits(this.stretch);
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
        PerlinNoiseTexture other = (PerlinNoiseTexture) obj;
        if (Float.floatToIntBits(this.amount) != Float.floatToIntBits(other.amount)) {
            return false;
        }
        if (Float.floatToIntBits(this.angle) != Float.floatToIntBits(other.angle)) {
            return false;
        }
        if (this.color1 == null) {
            if (other.color1 != null) {
                return false;
            }
        } else if (!this.color1.equals(other.color1)) {
            return false;
        }
        if (this.color2 == null) {
            if (other.color2 != null) {
                return false;
            }
        } else if (!this.color2.equals(other.color2)) {
            return false;
        }
        if (Float.floatToIntBits(this.scale) != Float.floatToIntBits(other.scale)) {
            return false;
        }
        if (Float.floatToIntBits(this.stretch) != Float.floatToIntBits(other.stretch)) {
            return false;
        }
        return true;
    }

}

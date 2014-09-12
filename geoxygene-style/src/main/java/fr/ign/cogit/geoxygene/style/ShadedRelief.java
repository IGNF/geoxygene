package fr.ign.cogit.geoxygene.style;

import javax.xml.bind.annotation.XmlElement;

public class ShadedRelief {
    private boolean brightnessOnly = false;

    @XmlElement(name = "BrightnessOnly")
    public boolean isBrightnessOnly() {
        return this.brightnessOnly;
    }

    public void setBrightnessOnly(boolean brightnessOnly) {
        this.brightnessOnly = brightnessOnly;
    }

    private double reliefFactor = 55;

    @XmlElement(name = "ReliefFactor")
    public double getReliefFactor() {
        return this.reliefFactor;
    }

    public void setReliefFactor(double reliefFactor) {
        this.reliefFactor = reliefFactor;
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
        result = prime * result + (this.brightnessOnly ? 1231 : 1237);
        long temp;
        temp = Double.doubleToLongBits(this.reliefFactor);
        result = prime * result + (int) (temp ^ (temp >>> 32));
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
        ShadedRelief other = (ShadedRelief) obj;
        if (this.brightnessOnly != other.brightnessOnly) {
            return false;
        }
        if (Double.doubleToLongBits(this.reliefFactor) != Double
                .doubleToLongBits(other.reliefFactor)) {
            return false;
        }
        return true;
    }

}

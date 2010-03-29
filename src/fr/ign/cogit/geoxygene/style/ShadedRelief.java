package fr.ign.cogit.geoxygene.style;

import javax.xml.bind.annotation.XmlElement;

public class ShadedRelief {
    @XmlElement(name = "BrightnessOnly")
    private boolean brightnessOnly = false;
    public boolean isBrightnessOnly() {
        return this.brightnessOnly;
    }
    public void setBrightnessOnly(boolean brightnessOnly) {
        this.brightnessOnly = brightnessOnly;
    }
    @XmlElement(name = "ReliefFactor")
    private double reliefFactor = 55;
    public double getReliefFactor() {
        return this.reliefFactor;
    }
    public void setReliefFactor(double reliefFactor) {
        this.reliefFactor = reliefFactor;
    }
}

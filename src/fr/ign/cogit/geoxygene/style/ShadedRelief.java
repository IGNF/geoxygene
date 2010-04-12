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
}

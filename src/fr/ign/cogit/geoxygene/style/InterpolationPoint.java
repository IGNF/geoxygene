package fr.ign.cogit.geoxygene.style;

import java.awt.Color;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

public class InterpolationPoint {
    public InterpolationPoint() {
        
    }
    public InterpolationPoint(double data, Color value) {
        this.data = data;
        this.value = value;
    }
    private double data;

    @XmlElement(name = "Data")
    public double getData() {
        return this.data;
    }

    public void setData(double data) {
        this.data = data;
    }
    //@XmlElement(name = "Value")
    @XmlTransient
    private Color value;
    @XmlTransient
    public Color getValue() {
        return this.value;
    }

    public void setValue(Color value) {
        this.value = value;
    }
}

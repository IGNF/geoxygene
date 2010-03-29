package fr.ign.cogit.geoxygene.style;

import java.awt.Color;

import javax.xml.bind.annotation.XmlElement;

public class InterpolationPoint {
    public InterpolationPoint() {
        
    }
    public InterpolationPoint(double data, Color value) {
        this.data = data;
        this.value = value;
    }
    @XmlElement(name = "Data")
    private double data;

    public double getData() {
        return this.data;
    }

    public void setData(double data) {
        this.data = data;
    }
    @XmlElement(name = "Value")
    private Color value;

    public Color getValue() {
        return this.value;
    }

    public void setValue(Color value) {
        this.value = value;
    }
}
